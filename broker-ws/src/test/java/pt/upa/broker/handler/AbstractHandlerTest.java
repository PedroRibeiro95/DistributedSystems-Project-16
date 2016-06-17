package pt.upa.broker.handler;

import java.io.ByteArrayInputStream;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.stream.StreamSource;

import org.junit.AfterClass;
import org.junit.BeforeClass;


/**
 *  Abstract handler test suite
 */
public abstract class AbstractHandlerTest {

    // static members

    /** hello-ws SOAP request message captured with LoggingHandler */
    protected static final String BROKER_SOAP_RESPONSE = "<S:Envelope "+
    "xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\" "+
    "xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">"+
    "<SOAP-ENV:Header><b:Nonce xmlns:b=\"http://ws.broker.upa.pt/\">G41kCw==</b:Nonce>"+
    "<b:Signature xmlns:b=\"http://ws.broker.upa.pt/\">"+
    "TVHiJhmNNnElVjL6r6tMNVlK4LQqEFG42h+fHeaJMDFbh7WLlHLkqVzOfi2m/PFR5LBDMMFUA5X6IpP/mc1"+
    "+kKCp3Q+FCbqmNUy4Kft9X+q/dF+9FX9CNUff0wjUHJgyiyagWnkR8KE3dCyygr78FXfzD+OVCHITK9nWgHq2"+
    "rcTDFR6F+fAO6AtF1oZWv63SLBB2vmlRLMWAhW5k8TBasAWTEl/LxzMTAJ/+O3RgYXtRgbJBMRUSK22PWIirEYo"+
    "rabVyeehEXzG/m1SCk9vw+pmkpLuMpXNa0NKIbySy9vEnrl5q3kcKYzxTr2MOMzmmQoRuo28etOTz/4TPd27Xng==</b:Signature>"+
    "</SOAP-ENV:Header><S:Body><ns2:requestJob xmlns:ns2=\"http://ws.transporter.upa.pt/\"><origin>Lisboa</origin>"+
    "<destination>Leiria</destination><price>55</price></ns2:requestJob></S:Body></S:Envelope>";

    /** hello-ws SOAP response message captured with LoggingHandler */
    protected static final String BROKER_SOAP_REQUEST = "<S:Envelope "+
    "xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\" "+
    "xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\"><SOAP-ENV:Header>"+
    "<t:TransporterID xmlns:t=\"http://ws.transporter.upa.pt/\">Transporter2</t:TransporterID>"+
    "<t:Nonce xmlns:t=\"http://ws.transporter.upa.pt/\">kBEcSA==</t:Nonce>"+
    "<t:Signature xmlns:t=\"http://ws.transporter.upa.pt/\">"+
    "avbfNt42Ol7G69N/KbgJfnsRNZkqxkgUpgzpsMdZtMQw9M9b0RhL5l9f+UJqDD0ViiiJL5R0tpp11poet8M0Bl"+
    "q8QwSDDzbMkoWOxXvPAJSgGKnGQysVQEc3VUaw687FjYveI5thBbQs873OnZmCs5aT+eo1x7CHRiQedpdA4gMzTg"+
    "96yUYmU6L+LSKN5UUvgoZkdHjaQmUtRZDYKQQZvEtfWFPFHCpwRw0Lo+oKgu9Fir/lEybDLioF7gm7vrmL5HVZt7DG"+
    "kg+NarMQkOKRXoEgzKj7KbTfMZptT6d1Ir4AFKU2vhcnfpd8zEOR7sSYcndW9rVPZixlC4me+w7HLg==</t:Signature>"+
    "</SOAP-ENV:Header><S:Body><ns2:requestJobResponse xmlns:ns2=\"http://ws.transporter.upa.pt/\">"+
    "<return><companyName>UpaTransporter2</companyName><jobIdentifier>2</jobIdentifier><jobOrigin>Lisboa</jobOrigin>"+
    "<jobDestination>Leiria</jobDestination><jobPrice>110</jobPrice><jobState>PROPOSED</jobState></return>"+
    "</ns2:requestJobResponse></S:Body></S:Envelope>";
    
    /** hello-ws SOAP request message captured with LoggingHandler */
    protected static final String BROKER_SOAP_RESPONSE_ERROR = "<S:Envelope "+
    "xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\" "+
    "xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">"+
    "<SOAP-ENV:Header><b:Nonce xmlns:b=\"http://ws.broker.upa.pt/\">WxQmHw==</b:Nonce>"+
    "<b:Signature xmlns:b=\"http://ws.broker.upa.pt/\">"+
    "fKSMi3uFbFLd24likuA5KOSp1ElSDjNG96Z8FLVC04cO72fpgSHoutAjDNn9u2YBKisSgwSxQESnM154OXgw"+
    "ateSjnC+uaqX2o/+RCk1QGkRXVyalEibgvAxFoECbZHz9bgAjVqiZavmiDMTTWucEmiC/YLvqGzH6VJzPCBR8s"+
    "9Hg45Toj39opf0mf6HfTGKQPEmzdhEQM11FhRv134t199oVm9rXGeyQZP7f4tRmIZAr5nVk9666MsYKEYX8Mxf0r"+
    "pB/xIr/IsGlr4QTfdxeCfTY0+FH8DgTNCE3OibHcdUliXaV7eQBDx4WDuJzHZr3I3eQ5cc2+YBErANGxNPww==</b:Signature>"+
    "</SOAP-ENV:Header><S:Body></S:Body></S:Envelope>";
    
    /** hello-ws SOAP response message captured with LoggingHandler */
    protected static final String BROKER_SOAP_REQUEST_ERROR = "<S:Envelope "+
    "xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\" "+
    "xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\"><SOAP-ENV:Header>"+
    "<t:TransporterID xmlns:t=\"http://ws.transporter.upa.pt/\">Transporter2</t:TransporterID>"+
    "<t:Nonce xmlns:t=\"http://ws.transporter.upa.pt/\">kBEcSA==</t:Nonce>"+
    "<t:Signature xmlns:t=\"http://ws.transporter.upa.pt/\">"+
    "avbfNt42Ol7G69N/KbgJfnsRNZkqxkgUpgzpsMdZtMQw9M9b0RhL5l9f+UJqDD0ViiiJL5R0tpp11poet8M0Bl"+
    "q8QwSDDzbMkoWOxXvPAJSgGKnGQysVQEc3VUaw687FjYveI5thBbQs873OnZmCs5aT+eo1x7CHRiQedpdA4gMzTg"+
    "96yUYmU6L+LSKN5UUvgoZkdHjaQmUtRZDYKQQZvEtfWFPFHCpwRw0Lo+oKgu9Fir/lEybDLioF7gm7vrmL5HVZt7DG"+
    "kg+NarMQkOKRXoEgzKj7KbTfMZptT6d1Ir4AFKU2vhcnfpd8zEOR7sSYcndW9rVPZixlC4me+w7HLg==</t:Signature>"+
    "</SOAP-ENV:Header><S:Body><ns2:requestJobResponse xmlns:ns2=\"http://ws.transporter.upa.pt/\">"+
    "<return><companyName>UpaTransporter2</companyName><jobIdentifier>2</jobIdentifier><jobOrigin>Lisboa</jobOrigin>"+
    "<jobDestination>Leiria</jobDestination><jobPrice>105</jobPrice><jobState>PROPOSED</jobState></return>"+
    "</ns2:requestJobResponse></S:Body></S:Envelope>";

    /** SOAP message factory */
    protected static final MessageFactory MESSAGE_FACTORY;

    static {
        try {
            MESSAGE_FACTORY = MessageFactory.newInstance();
        } catch(SOAPException e) {
            throw new RuntimeException(e);
        }
    }


    // helper functions

    protected static SOAPMessage byteArrayToSOAPMessage(byte[] msg) throws Exception {
        ByteArrayInputStream byteInStream = new ByteArrayInputStream(msg);
        StreamSource source = new StreamSource(byteInStream);
        SOAPMessage newMsg = newMsg = MESSAGE_FACTORY.createMessage();
        SOAPPart soapPart = newMsg.getSOAPPart();
        soapPart.setContent(source);
        return newMsg;
    }


    // one-time initialization and clean-up

    @BeforeClass
    public static void oneTimeSetUp() {

    }

    @AfterClass
    public static void oneTimeTearDown() {

    }

}
