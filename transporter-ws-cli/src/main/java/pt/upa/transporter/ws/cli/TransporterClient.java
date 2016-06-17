package pt.upa.transporter.ws.cli;

import static javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY;

import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.ws.BindingProvider;
import pt.upa.transporter.ws.BadJobFault_Exception;
import pt.upa.transporter.ws.BadLocationFault_Exception;
import pt.upa.transporter.ws.BadPriceFault_Exception;
import pt.upa.transporter.ws.JobView;

//classes generated from WSDL
import pt.upa.transporter.ws.TransporterPortType;
import pt.upa.transporter.ws.TransporterService;

public class TransporterClient {
	private TransporterPortType port;
	
	public TransporterClient(String endpointAddress) {
	
		if (endpointAddress == null) {
			System.out.println("Not found!");
			return;
		} else {
			System.out.printf("Found %s%n", endpointAddress);
		}
	
		System.out.println("Creating stub ...");
		TransporterService service = new TransporterService();
		port = service.getTransporterPort();
	
		System.out.println("Setting endpoint address ...");
		BindingProvider bindingProvider = (BindingProvider) port;
		Map<String, Object> requestContext = bindingProvider.getRequestContext();
		requestContext.put(ENDPOINT_ADDRESS_PROPERTY, endpointAddress);
	
	}
	
	
	
	public String ping (String arg){
		return port.ping(arg);
	}
	
	public JobView requestJob(String origin, String destination, int price)
			throws BadLocationFault_Exception, BadPriceFault_Exception {
		return port.requestJob(origin, destination, price);
	}
	
	public JobView decideJob(String id, boolean accept) throws BadJobFault_Exception {
		return port.decideJob(id,accept);
	}
	
	public JobView jobStatus(String id) {
		return port.jobStatus(id);
	}
	
	public List<JobView> listJobs(){
		return port.listJobs();
	}
	
	public void clearJobs(){
		port.clearJobs();
	}
}
