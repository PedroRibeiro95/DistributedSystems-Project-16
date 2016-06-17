package pt.upa.broker.ws.it;


import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Test;

import pt.upa.broker.ws.InvalidPriceFault_Exception;
import pt.upa.broker.ws.TransportView;
import pt.upa.broker.ws.UnavailableTransportFault_Exception;
import pt.upa.broker.ws.UnavailableTransportPriceFault_Exception;
import pt.upa.broker.ws.UnknownLocationFault_Exception;
import pt.upa.broker.ws.UnknownTransportFault_Exception;

public class ListTransportsIT extends ExampleIT {
	
	@Test
  public void listAllTransports() throws UnknownTransportFault_Exception, InvalidPriceFault_Exception,
		UnavailableTransportFault_Exception, UnavailableTransportPriceFault_Exception,
		UnknownLocationFault_Exception {
		
		ArrayList<TransportView> views = new ArrayList<TransportView>();
		int s=client.listTransports().size();
		String id1 = client.requestTransport("Lisboa", "Porto", 9);
		TransportView tv1 = client.viewTransport(id1);
		String id2 = client.requestTransport("Lisboa", "Faro", 9);
		TransportView tv2 = client.viewTransport(id2);
		views.add(tv1);
		views.add(tv2);
		s +=views.size();
		
		assertTrue("Jobs incorrectly listed", client.listTransports().size() == s);

  }
}
