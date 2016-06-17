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
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.MessageContext.Scope;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.junit.Test;

import mockit.Mocked;
import mockit.StrictExpectations;


/**
 *  Handler test suite
 */
public class SucessHandlerTest extends AbstractHandlerTest {

    // tests

    @Test
    public void successHeaderHandlerOutbound(
        @Mocked final SOAPMessageContext soapMessageContext)
        throws Exception {

        // Preparation code not specific to JMockit, if any.
        final String soapText = BROKER_SOAP_RESPONSE;
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
        boolean handleResult = handler.handleMessage(soapMessageContext);

        // Additional verification code, if any, either here or before the verification block.

        // assert that message would proceed normally
        assertTrue("Handler result is wrong!", handleResult);

        // assert header
        SOAPPart soapPart = soapMessage.getSOAPPart();
        SOAPEnvelope soapEnvelope = soapPart.getEnvelope();
        SOAPHeader soapHeader = soapEnvelope.getHeader();
        assertNotNull(soapHeader);

        // assert header element
        Name name = soapEnvelope.createName("Nonce", "b", "http://ws.broker.upa.pt/");
        Iterator it = soapHeader.getChildElements(name);
        assertTrue(it.hasNext());

        // assert header element value
        SOAPElement element = (SOAPElement) it.next();
        String valueString = element.getValue();
        assertEquals("Handler does not contain this nonce!", "G41kCw==", valueString); 

        //soapMessage.writeTo(System.out);
    }

    @Test
    public void successHeaderHandlerInbound(
        @Mocked final SOAPMessageContext soapMessageContext)
        throws Exception {

        // Preparation code not specific to JMockit, if any.
        final String soapText = BROKER_SOAP_REQUEST;
        //System.out.println(soapText);

        final SOAPMessage soapMessage = byteArrayToSOAPMessage(soapText.getBytes());
        final Boolean soapOutbound = false;

        // an "expectation block"
        // One or more invocations to mocked types, causing expectations to be recorded.
        new StrictExpectations() {{
            soapMessageContext.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
            result = soapOutbound;

            soapMessageContext.getMessage();
            result = soapMessage;

            /*FIXME soapMessageContext.put(HeaderHandler.CONTEXT_PROPERTY, 22);
            soapMessageContext.setScope(HeaderHandler.CONTEXT_PROPERTY, Scope.APPLICATION);*/
        }};

        // Unit under test is exercised.
        HeaderHandler handler = new HeaderHandler();
        boolean handleResult = handler.handleMessage(soapMessageContext);

        // Additional verification code, if any, either here or before the verification block.

        // assert that message would proceed normally
        assertTrue("Handler result is wrong!", handleResult);

        //soapMessage.writeTo(System.out);
    }

}
