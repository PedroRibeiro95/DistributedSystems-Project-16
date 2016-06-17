package pt.upa.broker;

import static javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.registry.JAXRException;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Endpoint;
import javax.xml.ws.WebServiceException;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.upa.broker.ws.BrokerPort;
import pt.upa.broker.ws.BrokerPortType;
import pt.upa.broker.ws.BrokerService;

public class BrokerApplication {

	private static boolean replace=false;
	
	public static void main(String[] args) {
		System.out.println(BrokerApplication.class.getSimpleName() + " starting...");
		if (args.length < 3) {
			System.err.println("Argument(s) missing!");
			System.err.printf("Usage: java %s uddiURL wsName wsURL%n", BrokerApplication.class.getName());
			return;
		}
		String uddiURL = args[0];
		String name = args[1];
		String url = args[2];
		boolean b = false;
		if(name.equals("0")){
			name="UpaBroker";
		}
		else{
			b=true;
			name="UpaBrokerSlave";
		}
		Endpoint endpoint = null;
		UDDINaming uddiNaming = null;
		try {
			BrokerPort port = new BrokerPort(uddiURL,b);
			endpoint = Endpoint.create(port);

			// publish endpoint
			System.out.printf("Starting %s%n", url);
			endpoint.publish(url);

			// publish to UDDI
			System.out.printf("Publishing '%s' to UDDI at %s%n", name, uddiURL);
			uddiNaming = new UDDINaming(uddiURL);
			uddiNaming.rebind(name, url);
			Timer timer = new Timer();
			if(b){
				System.out.printf("Looking for '%s'%n", "UpaBroker");
				String endpointAddress = uddiNaming.lookup("UpaBroker");
				if (endpointAddress == null) {
					System.out.println("Not found!");
					return;
				} else {
					System.out.printf("Found %s%n", endpointAddress);
				}

				System.out.println("Creating stub ...");
				BrokerService service = new BrokerService();
				BrokerPortType port2 = service.getBrokerPort();

				System.out.println("Setting endpoint address ...");
				BindingProvider bindingProvider = (BindingProvider) port2;
				Map<String, Object> requestContext = bindingProvider.getRequestContext();
				requestContext.put(ENDPOINT_ADDRESS_PROPERTY, endpointAddress);
				
				
				//---------------Receive timeout----------------------------------//
				 int receiveTimeout = 3000;
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
				
				TimerTask AreYouAlive = new TimerTask(){
					public void run() {
						try {
							if(!replace){
								String result = port2.ping("friend");
								System.out.println("Sending are you alive msg");
								if(result.equals("Hello friend")){
									System.out.println("He is alive!");
								}
							}

			            } catch(WebServiceException wse) {
			                //System.out.println("Caught: " + wse);
			                //Throwable cause = wse.getCause();
			            	System.out.println("Replace Broker server");
			            	try {
								UDDINaming uddiNaming2 =new UDDINaming(uddiURL);
								uddiNaming2.rebind("UpaBroker", url);
								System.out.println("Changing broker port to: " + url);
							} catch (JAXRException e) {
								System.out.printf("Caught exception: %s%n", e);
								e.printStackTrace();
							}
			                replace=true;
			            }
					}
				};
				
				timer.schedule(AreYouAlive,4000, 4000);
				
			}
			
			// wait
			System.out.println("Awaiting connections");
			System.out.println("Press enter to shutdown");
			System.in.read();
			timer.cancel();
			timer.purge();

		} catch (Exception e) {
			System.out.printf("Caught exception: %s%n", e);
			e.printStackTrace();

		} finally {
			try {
				if (endpoint != null) {
					// stop endpoint
					endpoint.stop();
					System.out.printf("Stopped %s%n", url);
				}
			} catch (Exception e) {
				System.out.printf("Caught exception when stopping: %s%n", e);
			}
			try {
				if (uddiNaming != null) {
					// delete from UDDI
					uddiNaming.unbind(name);
					System.out.printf("Deleted '%s' from UDDI%n", name);
				}
			} catch (Exception e) {
				System.out.printf("Caught exception when deleting: %s%n", e);
			}
		}

	}

}
