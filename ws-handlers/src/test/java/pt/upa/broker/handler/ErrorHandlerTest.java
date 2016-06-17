package pt.upa.broker.handler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;

import javax.xml.soap.Name;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.ws.ProtocolException;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.MessageContext.Scope;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.junit.Test;

import mockit.Mocked;
import mockit.StrictExpectations;


/**
 *  Handler test suite
 */
public class ErrorHandlerTest extends AbstractHandlerTest {

    // tests

    @Test(expected = ProtocolException.class)
    public void errorHeaderHandlerOutbound(
        @Mocked final SOAPMessageContext soapMessageContext)
        throws Exception {

        // Preparation code not specific to JMockit, if any.
        final String soapText = BROKER_SOAP_RESPONSE_ERROR;
        // System.out.println(soapText);

        final SOAPMessage soapMessage = byteArrayToSOAPMessage(soapText.getBytes());
        final Boolean soapOutbound = true;

        // an "expectation block"
        // One or more invocations to mocked types, causing expectations to be recorded.
        new StrictExpectations() {{
            soapMessageContext.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
            result = soapOutbound;

            soapMessageContext.getMessage();
            result = soapMessage;
        }};

        // Unit under test is exercised.
        HeaderHandler handler = new HeaderHandler();
        handler.handleMessage(soapMessageContext);

        // Additional verification code, if any, either here or before the verification block.

        // assert that message would proceed normally
        //assertTrue("Handler result is wrong!", handleResult);

        /*// assert header
        SOAPPart soapPart = soapMessage.getSOAPPart();
        SOAPEnvelope soapEnvelope = soapPart.getEnvelope();
        SOAPHeader soapHeader = soapEnvelope.getHeader();
        //assertNotNull(soapHeader);

        // assert header element
        Name name = soapEnvelope.createName("Nonce", "b", "http://ws.broker.upa.pt/");
        Iterator it = soapHeader.getChildElements(name);
        //assertTrue("Header contains this element!", it.hasNext());

        // assert header element value
        SOAPElement element = (SOAPElement) it.next();
        String valueString = element.getValue();
        assertTrue("Header contains this nonce!", "QVhK/A==".equals(valueString));*/ 

        //soapMessage.writeTo(System.out);
    }

    @Test(expected = ProtocolException.class)
    public void errorHeaderHandlerInbound(
        @Mocked final SOAPMessageContext soapMessageContext)
        throws Exception {

        // Preparation code not specific to JMockit, if any.
        final String soapText1 = BROKER_SOAP_REQUEST;
        final String soapText2 = BROKER_SOAP_REQUEST_ERROR;
        //System.out.println(soapText);

        final SOAPMessage soapMessage1 = byteArrayToSOAPMessage(soapText1.getBytes());
        final SOAPMessage soapMessage2 = byteArrayToSOAPMessage(soapText2.getBytes());
        final Boolean soapOutbound = false;

        // an "expectation block"
        // One or more invocations to mocked types, causing expectations to be recorded.
        new StrictExpectations() {{
            soapMessageContext.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
            result = soapOutbound;

            soapMessageContext.getMessage();
            result = soapMessage1;

            /*FIXME soapMessageContext.put(HeaderHandler.CONTEXT_PROPERTY, 22);
            soapMessageContext.setScope(HeaderHandler.CONTEXT_PROPERTY, Scope.APPLICATION);*/
        }};

        // Unit under test is exercised.
        HeaderHandler handler = new HeaderHandler();
        handler.handleMessage(soapMessageContext);

        new StrictExpectations() {{
            soapMessageContext.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
            result = soapOutbound;

            soapMessageContext.getMessage();
            result = soapMessage2;

            /*FIXME soapMessageContext.put(HeaderHandler.CONTEXT_PROPERTY, 22);
            soapMessageContext.setScope(HeaderHandler.CONTEXT_PROPERTY, Scope.APPLICATION);*/
        }};
        
        handler.handleMessage(soapMessageContext);
        
        // Additional verification code, if any, either here or before the verification block.

        // assert that message would proceed normally
        //assertTrue("Handler result is correct!", !handleResult);

        //soapMessage.writeTo(System.out);
    }

}