package pt.upa.broker.ws.it;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ClearTransportsIT extends ExampleIT {
	
	@Test
  public void clearAllTransports() {
		
		client.clearTransports();
		
		assertTrue(client.listTransports().isEmpty());
  }

}
