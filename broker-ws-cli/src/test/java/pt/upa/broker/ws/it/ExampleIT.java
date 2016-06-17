package pt.upa.broker.ws.it;

import org.junit.*;

import pt.upa.broker.ws.cli.BrokerClient;

/**
 *  Integration Test example
 *  
 *  Invoked by Maven in the "verify" life-cycle phase
 *  Should invoke "live" remote servers 
 */
public class ExampleIT {

  // static members

	protected static BrokerClient client;

  // one-time initialization and clean-up

  @BeforeClass
  public static void oneTimeSetUp() throws Exception {
  	
  	client = new BrokerClient("http://localhost:9090", "UpaBroker");
  }

  @AfterClass
  public static void oneTimeTearDown() {
  	
  	client = null;
  }

}