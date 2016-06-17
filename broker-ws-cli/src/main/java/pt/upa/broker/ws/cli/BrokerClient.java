package pt.upa.broker.ws.cli;

import java.util.List;

import pt.upa.broker.ws.InvalidPriceFault_Exception;
import pt.upa.broker.ws.TransportView;
import pt.upa.broker.ws.UnavailableTransportFault_Exception;
import pt.upa.broker.ws.UnavailableTransportPriceFault_Exception;
import pt.upa.broker.ws.UnknownLocationFault_Exception;
import pt.upa.broker.ws.UnknownTransportFault_Exception;

public class BrokerClient {
	private BrokerClientFrontEnd fe;
	
	public BrokerClient(String uddiURL, String n) throws Exception{
		fe= new BrokerClientFrontEnd(uddiURL,n);
		
	}
	
	public String ping (String arg){
		return fe.ping(arg);
	}

	public String requestTransport(String origin, String destination, int price)
			throws InvalidPriceFault_Exception, UnavailableTransportFault_Exception,
			UnavailableTransportPriceFault_Exception, UnknownLocationFault_Exception{
		return fe.requestTransport(origin, destination, price);
	}
	
	public TransportView viewTransport(String id) throws UnknownTransportFault_Exception{
		return fe.viewTransport(id);
	}
	
	public List<TransportView> listTransports(){
		return fe.listTransports();
	}
	
	public void clearTransports(){
		fe.clearTransports();
	}
	
}
