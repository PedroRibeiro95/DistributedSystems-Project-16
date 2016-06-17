package pt.upa.broker.ws;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;

import javax.xml.registry.JAXRException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import mockit.Expectations;
import mockit.Mocked;
import mockit.Verifications;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;

public class BrokerWSUDDIMockTest {
	
	// static members

    private static final String uddiURL = "http://localhost:9090";
    private static final String wsName = "UpaTransporter%";
    private static final String wsURL1 = "http://localhost:8081/transporter-ws/endpoint";
    private static final String wsURL2 = "http://localhost:8082/transporter-ws/endpoint";
    private Collection <String> endpointAddress = new ArrayList<String>();
    
 // one-time initialization and clean-up

    @BeforeClass
    public static void oneTimeSetUp() {
    }

    @AfterClass
    public static void oneTimeTearDown() {
    }


    // initialization and clean-up for each test

    @Before
    public void setUp() {
    	endpointAddress.add(wsURL1);
    	endpointAddress.add(wsURL2);
    }

    @After
    public void tearDown() {
    	endpointAddress.clear();
    }

    @Test
    public void testMockUddi(@Mocked final UDDINaming uddiNaming)
            throws Exception {


        // an "expectation block"
        // One or more invocations to mocked types, causing expectations to be
        // recorded.
        new Expectations() {
            {
                new UDDINaming(uddiURL);
                uddiNaming.list(wsName);
                result = endpointAddress;
            }
        };

        // Unit under test is exercised.
        new BrokerPort(uddiURL,false);

        // a "verification block"
        // One or more invocations to mocked types, causing expectations to be
        // verified.
        new Verifications() {
            {
                // Verifies that zero or one invocations occurred, with the
                // specified argument value:
                new UDDINaming(uddiURL);
                uddiNaming.list(wsName);
                maxTimes = 1;
                uddiNaming.unbind(null);
                maxTimes = 0;
                uddiNaming.bind(null, null);
                maxTimes = 0;
                uddiNaming.rebind(null, null);
                maxTimes = 0;
            }
        };

    }


    @Test
    public void testMockUddiServerNotFound(@Mocked final UDDINaming uddiNaming)
            throws Exception {


        // an "expectation block"
        // One or more invocations to mocked types, causing expectations to be
        // recorded.
        new Expectations() {
            {
                new UDDINaming(uddiURL);
                result = new JAXRException("created for testing");
            }
        };

        // Unit under test is exercised.
        try {
            new BrokerPort(uddiURL,false);
            fail();

        } catch (BrokerWsException e) {
            assertTrue(e.getCause() instanceof JAXRException);
            final String expectedMessage = String.format(
                    "Client failed lookup on UDDI at %s!", uddiURL);
            assertEquals(expectedMessage, e.getMessage());
        }

        // a "verification block"
        // One or more invocations to mocked types, causing expectations to be
        // verified.
        new Verifications() {
            {
                // Verifies that zero or one invocations occurred, with the
                // specified argument value:
                new UDDINaming(uddiURL);
            }
        };

    }
    
    @Test
    public void testMockUddiServerLookupError(@Mocked final UDDINaming uddiNaming)
            throws Exception {

        // an "expectation block"
        // One or more invocations to mocked types, causing expectations to be
        // recorded.
        new Expectations() {
            {
                new UDDINaming(uddiURL);
                uddiNaming.list(wsName);
                result = new JAXRException("created for testing");
            }
        };

        // Unit under test is exercised.
        try {
            new BrokerPort(uddiURL,false);
            fail();

        } catch (BrokerWsException e) {
        	
        	assertTrue(e.getCause() instanceof JAXRException);
            final String expectedMessage = String.format(
                    "Client failed lookup on UDDI at %s!", uddiURL);
            assertEquals(expectedMessage, e.getMessage());
        }

        // a "verification block"
        // One or more invocations to mocked types, causing expectations to be
        // verified.
        new Verifications() {
            {
                // Verifies that zero or one invocations occurred, with the
                // specified argument value:
                new UDDINaming(uddiURL);
                uddiNaming.list(wsName);
            }
        };
    }

    
}
