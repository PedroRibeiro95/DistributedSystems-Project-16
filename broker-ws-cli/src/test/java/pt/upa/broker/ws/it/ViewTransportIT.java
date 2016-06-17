package pt.upa.broker.ws.it;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.lang.Thread;

import pt.upa.broker.ws.InvalidPriceFault_Exception;
import pt.upa.broker.ws.TransportStateView;
import pt.upa.broker.ws.TransportView;
import pt.upa.broker.ws.UnavailableTransportFault_Exception;
import pt.upa.broker.ws.UnavailableTransportPriceFault_Exception;
import pt.upa.broker.ws.UnknownLocationFault_Exception;
import pt.upa.broker.ws.UnknownTransportFault_Exception;

public class ViewTransportIT extends ExampleIT {

	@Test
  public void viewTransportAfterCreation() 
  		throws InvalidPriceFault_Exception, UnavailableTransportFault_Exception, 
  		UnavailableTransportPriceFault_Exception, UnknownLocationFault_Exception, UnknownTransportFault_Exception {
		
		String id = client.requestTransport("Lisboa", "Porto", 9);
		TransportView tv = client.viewTransport(id);
		assertTrue(tv.getOrigin().equals("Lisboa") && tv.getDestination().equals("Porto") 
				&& tv.getPrice() < 9);
  }
	
	@Test
  public void checkStateAfter5s() 
  		throws InvalidPriceFault_Exception, UnavailableTransportFault_Exception, 
  		UnavailableTransportPriceFault_Exception, UnknownLocationFault_Exception, UnknownTransportFault_Exception, InterruptedException {
		
		String id = client.requestTransport("Lisboa", "Porto", 9);
		Thread.sleep(5000);

		TransportView tv = client.viewTransport(id);
		assertTrue(tv.getState().equals(TransportStateView.HEADING) || tv.getState().equals(TransportStateView.ONGOING) 
				|| tv.getState().equals(TransportStateView.COMPLETED));
  }
	
	@Test
  public void checkStateAfter10s() 
  		throws InvalidPriceFault_Exception, UnavailableTransportFault_Exception, 
  		UnavailableTransportPriceFault_Exception, UnknownLocationFault_Exception, UnknownTransportFault_Exception, InterruptedException {
		
		String id = client.requestTransport("Lisboa", "Porto", 9);
		Thread.sleep(10000);

		TransportView tv = client.viewTransport(id);
		assertTrue(tv.getState().equals(TransportStateView.ONGOING) 
				|| tv.getState().equals(TransportStateView.COMPLETED));
  }
	
	@Test
  public void checkStateAfter15s() 
  		throws InvalidPriceFault_Exception, UnavailableTransportFault_Exception, 
  		UnavailableTransportPriceFault_Exception, UnknownLocationFault_Exception, UnknownTransportFault_Exception, InterruptedException {
		
		String id = client.requestTransport("Lisboa", "Porto", 9);
		Thread.sleep(10000);

		TransportView tv = client.viewTransport(id);
		assertTrue(tv.getState().equals(TransportStateView.COMPLETED));
  }
}
