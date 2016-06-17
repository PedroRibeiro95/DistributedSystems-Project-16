package pt.upa.broker.ws.it;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class PingIT extends ExampleIT {

  @Test
  public void successPing() {
    assertTrue(!(client.ping("").equals("")));
  }
	
}
