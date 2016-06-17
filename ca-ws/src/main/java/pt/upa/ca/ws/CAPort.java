package pt.upa.ca.ws;

import static javax.xml.bind.DatatypeConverter.printBase64Binary;

import javax.jws.WebService;

import example.crypto.X509DigitalSignature;
//import java.security.PrivateKey;
//import java.security.PublicKey;
import java.security.cert.Certificate;

@WebService(endpointInterface = "pt.upa.ca.ws.CAPortType")
public class CAPort implements CAPortType {

	@Override
	public String ping(String name) {
		
		return "Hola " + name + "!";
	}
	
	@Override
	public String generateCertificate(String server) throws Exception{		
		Certificate certificate = X509DigitalSignature.readCertificateFile("keys/" + server + ".cer");
		
//		PublicKey pubKey = X509DigitalSignature.getPublicKeyFromCertificate(certificate);
//		PrivateKey privKey = X509DigitalSignature.getPrivateKeyFromKeystore("keys/ca.jks", "passwd");
//		
//		byte[] c = X509DigitalSignature.makeDigitalSignature(pubKey.getEncoded(), privKey);
		
		return printBase64Binary(certificate.getEncoded());
	}

}
