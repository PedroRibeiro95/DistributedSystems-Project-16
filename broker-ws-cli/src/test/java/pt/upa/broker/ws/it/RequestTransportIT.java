package pt.upa.broker.ws.it;

import org.junit.Test;

import pt.upa.broker.ws.InvalidPriceFault_Exception;
import pt.upa.broker.ws.UnavailableTransportFault_Exception;
import pt.upa.broker.ws.UnavailableTransportPriceFault_Exception;
import pt.upa.broker.ws.UnknownLocationFault_Exception;

public class RequestTransportIT extends ExampleIT {
  
  @Test(expected = InvalidPriceFault_Exception.class)
  public void requestTransportNegativePrice() 
  		throws InvalidPriceFault_Exception, UnavailableTransportFault_Exception,
  		UnavailableTransportPriceFault_Exception, UnknownLocationFault_Exception  {  	

			client.requestTransport("Lisboa", "Faro", -1);
  }
  
  @Test(expected = UnavailableTransportFault_Exception.class)
  public void requestTransportPriceHigherThan100() 
  		throws InvalidPriceFault_Exception, UnavailableTransportFault_Exception,
  		UnavailableTransportPriceFault_Exception, UnknownLocationFault_Exception  {  	

			client.requestTransport("Lisboa", "Faro", 101);
  }
  
  @Test(expected = UnknownLocationFault_Exception.class)
  public void requestTransportUnknownDestination() 
  		throws InvalidPriceFault_Exception, UnavailableTransportFault_Exception,
  		UnavailableTransportPriceFault_Exception, UnknownLocationFault_Exception  {  	

			client.requestTransport("Lisboa", "Madrid", 50);
  }
  
  @Test(expected = UnknownLocationFault_Exception.class)
  public void requestTransportUnknownOrigin() 
  		throws InvalidPriceFault_Exception, UnavailableTransportFault_Exception,
  		UnavailableTransportPriceFault_Exception, UnknownLocationFault_Exception  {  	

			client.requestTransport("Madrid", "Lisboa", 50);
  }
  
  @Test(expected = UnavailableTransportPriceFault_Exception.class)
  public void requestOddPriceToEvenTransporter() 
  		throws InvalidPriceFault_Exception, UnavailableTransportFault_Exception,
  		UnavailableTransportPriceFault_Exception, UnknownLocationFault_Exception  {  	

			client.requestTransport("Porto", "Lisboa", 51);
  }
  
  @Test(expected = UnavailableTransportPriceFault_Exception.class)
  public void requestEvenPriceToOddTransporter() 
  		throws InvalidPriceFault_Exception, UnavailableTransportFault_Exception,
  		UnavailableTransportPriceFault_Exception, UnknownLocationFault_Exception  {  	

			client.requestTransport("Faro", "Lisboa", 50);
  }
	
}
