package pt.upa.broker.ws.cli;

import static javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.registry.JAXRException;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.WebServiceException;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.upa.broker.BrokerClientApplication;
//classes generated from WSDL
import pt.upa.broker.ws.BrokerPortType;
import pt.upa.broker.ws.BrokerService;
//
import pt.upa.broker.ws.InvalidPriceFault_Exception;
import pt.upa.broker.ws.TransportView;
import pt.upa.broker.ws.UnavailableTransportFault_Exception;
import pt.upa.broker.ws.UnavailableTransportPriceFault_Exception;
import pt.upa.broker.ws.UnknownLocationFault_Exception;
import pt.upa.broker.ws.UnknownTransportFault_Exception;

public class BrokerClientFrontEnd {

	private String url;
	private String name;
	private BrokerPortType port;
	
	public BrokerClientFrontEnd(String uddiURL, String n) throws Exception{
		url=uddiURL;
		name=n;
		System.out.println(BrokerClientApplication.class.getSimpleName() + " starting...");
		System.out.printf("Contacting UDDI at %s%n", url);
		UDDINaming uddiNaming = new UDDINaming(url);

		System.out.printf("Looking for '%s'%n", name);
		String endpointAddress = uddiNaming.lookup(name);
		if (endpointAddress == null) {
			System.out.println("Not found!");
			return;
		} else {
			System.out.printf("Found %s%n", endpointAddress);
		}

		System.out.println("Creating stub ...");
		BrokerService service = new BrokerService();
		port = service.getBrokerPort();

		System.out.println("Setting endpoint address ...");
		BindingProvider bindingProvider = (BindingProvider) port;
		Map<String, Object> requestContext = bindingProvider.getRequestContext();
		requestContext.put(ENDPOINT_ADDRESS_PROPERTY, endpointAddress);
		
		//---------------Receive timeout----------------------------------//
		 int receiveTimeout = 8000;
           // The receive timeout property has alternative names
           // Again, set them all to avoid compability issues
           final List<String> RECV_TIME_PROPS = new ArrayList<String>();
           RECV_TIME_PROPS.add("com.sun.xml.ws.request.timeout");
           RECV_TIME_PROPS.add("com.sun.xml.internal.ws.request.timeout");
           RECV_TIME_PROPS.add("javax.xml.ws.client.receiveTimeout");
           // Set timeout until the response is received (unit is milliseconds; 0 means infinite)
           for (String propName : RECV_TIME_PROPS)
               requestContext.put(propName, receiveTimeout);
           //System.out.printf("Set receive timeout to %d milliseconds%n", receiveTimeout);
        //---------------Receive timeout----------------------------------//
		
	}
	
	
	public String ping (String arg){
		try {
            return port.ping(arg);

        } catch(WebServiceException wse) {
			reconnect();
			return port.ping(arg);
        }
	}

	public String requestTransport(String origin, String destination, int price)
			throws InvalidPriceFault_Exception, UnavailableTransportFault_Exception,
			UnavailableTransportPriceFault_Exception, UnknownLocationFault_Exception{
		try{
			return port.requestTransport(origin, destination, price);
		}
		catch(WebServiceException wse) {
			reconnect();
			return port.requestTransport(origin, destination, price);
        }
	}
	
	public TransportView viewTransport(String id) throws UnknownTransportFault_Exception{
		TransportView tv =null;
		try{
			return port.viewTransport(id);
		}
		catch(WebServiceException wse) {
        	reconnect();
        	return port.viewTransport(id);
        }
	}
	
	public List<TransportView> listTransports(){
		List l= new ArrayList <TransportView> ();
		try{
			return  port.listTransports(); 
		}
		catch(WebServiceException wse) {
			reconnect();
			return port.listTransports();
        }
	}
	
	public void clearTransports(){
		try{
			port.clearTransports(); 
		}
		catch(WebServiceException wse) {
			reconnect();
			clearTransports(); 
        }
	}
	
	
	public void reconnect(){
		System.out.printf("Contacting UDDI at %s%n", url);
		try{
			UDDINaming uddiNaming = new UDDINaming(url);
	
			System.out.printf("Looking for '%s'%n", name);
			String endpointAddress = uddiNaming.lookup(name);
			if (endpointAddress == null) {
				System.out.println("Not found!");
				return;
			} else {
				System.out.printf("Found %s%n", endpointAddress);
			}
	
			System.out.println("Creating stub ...");
			BrokerService service = new BrokerService();
			port = service.getBrokerPort();
	
			System.out.println("Setting endpoint address ...");
			BindingProvider bindingProvider = (BindingProvider) port;
			Map<String, Object> requestContext = bindingProvider.getRequestContext();
			requestContext.put(ENDPOINT_ADDRESS_PROPERTY, endpointAddress);
		}
		catch (JAXRException e){
			System.out.println("JAXRException OCCURRED");
			reconnect();
		}
	}
}
