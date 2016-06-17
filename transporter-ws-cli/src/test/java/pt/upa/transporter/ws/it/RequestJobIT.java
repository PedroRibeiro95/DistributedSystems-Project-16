package pt.upa.transporter.ws.it;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import pt.upa.transporter.ws.BadLocationFault_Exception;
import pt.upa.transporter.ws.BadPriceFault_Exception;

public class RequestJobIT extends ExampleIT {

	// tests
	// assertEquals(expected, actual);
	
  @Test(expected = BadLocationFault_Exception.class)
  public void requestJobBadOrigin() 
  		throws BadLocationFault_Exception, BadPriceFault_Exception { 
  	
			client.requestJob("Porto", "Lisboa", 10);
  }
  
  @Test(expected = BadLocationFault_Exception.class)
  public void requestJobBadDestination() 
  		throws BadLocationFault_Exception, BadPriceFault_Exception {  	

			client.requestJob("Lisboa", "Porto", 10);
  }
  
  @Test(expected = BadPriceFault_Exception.class)
  public void requestJobLowPrice() 
  		throws BadLocationFault_Exception, BadPriceFault_Exception {  	

			client.requestJob("Lisboa", "Faro", -1);
  }

  @Test
  public void requestJobPriceOver100() 
  		throws BadLocationFault_Exception, BadPriceFault_Exception {  	

  	assertEquals(null, client.requestJob("Lisboa", "Faro", 150));
  }
  
  @Test
  public void requestJobPriceLesserThan10() 
  		throws BadLocationFault_Exception, BadPriceFault_Exception {  	

  	assertTrue(client.requestJob("Lisboa", "Faro", 10).getJobPrice() < 10);
  }
  
  @Test
  public void requestJobOddPrice() 
  		throws BadLocationFault_Exception, BadPriceFault_Exception {  	

  	assertTrue(client.requestJob("Lisboa", "Faro", 51).getJobPrice() < 51);
  }
  
  @Test
  public void requestJobEvenPrice() 
  		throws BadLocationFault_Exception, BadPriceFault_Exception {  	

  	assertTrue(client.requestJob("Lisboa", "Faro", 50).getJobPrice() > 50);
  }
}
