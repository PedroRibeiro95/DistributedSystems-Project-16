package pt.upa.broker.handler;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.crypto.Cipher;
import javax.xml.namespace.QName;
import javax.xml.registry.JAXRException;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.ProtocolException;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.MessageContext.Scope;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import javax.xml.ws.soap.SOAPFaultException;

import example.crypto.X509DigitalSignature;
import example.crypto.X509CertificateCheck;

import static javax.xml.bind.DatatypeConverter.printHexBinary;
import static javax.xml.bind.DatatypeConverter.printBase64Binary;
import static javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.upa.ca.ws.*;

import static javax.xml.bind.DatatypeConverter.parseBase64Binary;


/**
 *  This SOAPHandler shows how to set/get values from headers in
 *  inbound/outbound SOAP messages.
 *
 *  A header is created in an outbound message and is read on an
 *  inbound message.
 *
 *  The value that is read from the header
 *  is placed in a SOAP message context property
 *  that can be accessed by other handlers or by the application.
 */
public class HeaderHandler implements SOAPHandler<SOAPMessageContext> {

    private Vector<String> receivedNonces = new Vector<>();
    private Vector<String> generatedNonces = new Vector<>();
    
	//
    // Handler interface methods
    //
    public Set<QName> getHeaders() {
        return null;
    }

    public boolean handleMessage(SOAPMessageContext smc) {
        System.out.println("Broker SOAP Handler: Handling message.");

        Boolean outboundElement = (Boolean) smc
                .get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);

        try {
            if (outboundElement.booleanValue()) {
                System.out.println("Adding security to outgoing SOAP message..");
                
                SOAPMessage msg = smc.getMessage();
                SOAPPart sp = msg.getSOAPPart();
                SOAPEnvelope se = sp.getEnvelope();
                SOAPHeader sh = se.getHeader();
                SOAPBody sb = se.getBody();
                PrivateKey key = generatePrivateKey();
                
            // check headerfile
        		if(sh == null) {
        			System.out.println("Header not found.");
        			//return false;
        			sh = se.addHeader();
        		}
                
            // check body
        		if(sb == null) {
        			throw new ProtocolException("Body not found.");
        		}
        		
        		//Generating random number to be used as nonce
        		SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        		
            	final byte array[] = new byte[4];
            	random.nextBytes(array);
            	String converted = printBase64Binary(array);
        		while(generatedNonces.contains(converted)){
        			random.nextBytes(array);
        			converted = printBase64Binary(array);
        		}
        		generatedNonces.add(converted);
        		
        		//Creates header element containing "exposed" nonce
        		Name name1 = se.createName("Nonce", "b", "http://ws.broker.upa.pt/");
        		SOAPHeaderElement e1 = sh.addHeaderElement(name1);
        		
        		e1.addTextNode(converted);
        		
        		//Gets body content
        		Iterator it1 = sb.getChildElements();
        		
        		if(!it1.hasNext()){
        			throw new ProtocolException("Body element not found.");
        		}
        		
        		SOAPElement se1 = (SOAPElement) it1.next();
        		String toDigest = se1.getTextContent();
        		
        		//Concatenate body value and nonce
        		toDigest=toDigest.concat(converted);
        		
        		
        		//Creates signature with given digest
        		Signature sig = Signature.getInstance("SHA1WithRSA");
        		sig.initSign(key);
        		sig.update(toDigest.getBytes());
        		byte[] signature = sig.sign();
        		
        		String digestToAttach = printBase64Binary(signature);
        		
        		//Creates header element with signature
        		Name name = se.createName("Signature", "b", "http://ws.broker.upa.pt/");
        		SOAPHeaderElement element = sh.addHeaderElement(name);
        		
        		element.addTextNode(digestToAttach);
        		
        		msg.saveChanges();

            } else {
                System.out.println("Verifying incoming SOAP message...");

                // get SOAP envelope header
                SOAPMessage msg = smc.getMessage();
                SOAPPart sp = msg.getSOAPPart();
                SOAPEnvelope se = sp.getEnvelope();
                SOAPBody sb = se.getBody();
                SOAPHeader sh = se.getHeader();
                
                // check header
        		if(sh == null) {
        			System.out.println("Header not found.");
        			throw new ProtocolException("Header not found.");
        		}
                
                // check body
        		if(sb == null) {
        			System.out.println("Body not found.");
        			throw new ProtocolException("Body not found.");
        		}
                
                //Gets body content
                Iterator it1 = sb.getChildElements();
        		
        		if(!it1.hasNext()){
        			System.out.println("Body element not found.");
        			throw new ProtocolException("Body element not found.");
        		}
        		
        		SOAPElement se1 = (SOAPElement) it1.next();
        		String toDigest = se1.getTextContent();
        		
        		//Gets nonce value
        		Name name10 = se.createName("TransporterID", "t", "http://ws.transporter.upa.pt/");
        		
        		Iterator it10 = sh.getChildElements(name10);
        		
        		if(!it10.hasNext()){
        			System.out.println("Header element not found.");
        			throw new ProtocolException("Header element not found.");
        		}
		
        		SOAPElement se10 = (SOAPElement) it10.next();
        		String transporterID = se10.getTextContent();
        		PublicKey key = generatePublicKey(transporterID);
        		
        		//Gets nonce value
        		Name name1 = se.createName("Nonce", "t", "http://ws.transporter.upa.pt/");
        		
        		Iterator it2 = sh.getChildElements(name1);
        		
        		if(!it2.hasNext()){
        			System.out.println("Header element not found.");
        			throw new ProtocolException("Header element not found.");
        		}
		
        		SOAPElement se2 = (SOAPElement) it2.next();
        		String nonce = se2.getTextContent();
        		
        		if(receivedNonces.contains(nonce)) throw new ProtocolException("Nounce already used");
        		receivedNonces.add(nonce);
        		
        		//Concatenates body and nonce
        		toDigest=toDigest.concat(nonce);
    		
        		//Gets signature
        		Name name2 = se.createName("Signature", "t", "http://ws.transporter.upa.pt/");
        		
        		Iterator it3 = sh.getChildElements(name2);
        		
        		if(!it3.hasNext()){
        			System.out.println("Header element not found.");
        			throw new ProtocolException("Header element not found.");
        		}
        		
        		SOAPElement se3 = (SOAPElement) it3.next();
        		String signature2 = se3.getTextContent();
        		
        		//Decrypting signature
        		Signature sig = Signature.getInstance("SHA1WithRSA");
        		sig.initVerify(key);
        		sig.update(toDigest.getBytes());
        		//Comparing generated signature with received one
        		if(sig.verify(parseBase64Binary(signature2))){
        			return true;
        		}else{
        			throw new ProtocolException("Signatures don't match");
        		}
            }
        } catch (java.lang.Exception e) {
            System.out.print("Caught exception in handleMessage: ");
            System.out.println(e);
            System.out.println("Continue normal processing...");
            throw new ProtocolException("Message could not be processed");
        }

        return true;
    }

    public boolean handleFault(SOAPMessageContext smc) {
        System.out.println("Ignoring fault message...");
        return true;
    }

    public void close(MessageContext messageContext) {
    }
    
    public PublicKey generatePublicKey(String transporter) throws java.lang.Exception {
    	
    	//Reading public key from certificate
    	CAPortType ca = contactAuthServer("http://localhost:9090", "UpaCA");
    	String certificate = ca.generateCertificate(transporter);
    	byte[] byteCertificate = parseBase64Binary(certificate);
    	CertificateFactory cf = CertificateFactory.getInstance("X.509");
    	Certificate c = cf.generateCertificate(new ByteArrayInputStream(byteCertificate));
    	KeyStore keystore = X509DigitalSignature.readKeystoreFile("keys/Broker.jks", "ins3cur3".toCharArray());
    	Certificate cstore = keystore.getCertificate("ca");
    	if(!X509CertificateCheck.verifySignedCertificate(c, cstore.getPublicKey())){
    		throw new ProtocolException("Certificate not signed by CA");
    	}
      PublicKey pub   = X509DigitalSignature.getPublicKeyFromCertificate(c);
      
      return pub;
    }
    
    public PrivateKey generatePrivateKey() throws java.lang.Exception{
    	PrivateKey priv = X509DigitalSignature.getPrivateKeyFromKeystore
  				("keys/Broker.jks", "ins3cur3".toCharArray(),
  						"Broker", "1nsecure".toCharArray());
    	return priv;
    }
    
    public CAPortType contactAuthServer(String url, String name){
  		System.out.printf("Contacting UDDI at %s%n", url);
  		UDDINaming uddiNaming;
    	String endpointAddress = "";
			try {
				uddiNaming = new UDDINaming(url);
	  		System.out.printf("Looking for '%s'%n", name);
	  		endpointAddress = uddiNaming.lookup(name);
	  		if (endpointAddress == null) {
	  			System.out.println("Not found!");
	  			return null;
	  		} else {
	  			System.out.printf("Found %s%n", endpointAddress);
	  		}
			} catch (JAXRException e) {
				e.printStackTrace();
			}

  		System.out.println("Creating stub ...");
  		CAPortService service = new CAPortService();
  		CAPortType port = service.getCAPortPort();

  		System.out.println("Setting endpoint address ...");
  		BindingProvider bindingProvider = (BindingProvider) port;
  		Map<String, Object> requestContext = bindingProvider.getRequestContext();
  		requestContext.put(ENDPOINT_ADDRESS_PROPERTY, endpointAddress);
  		
  		return port;
    }
}