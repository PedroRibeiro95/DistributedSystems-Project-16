package pt.upa.broker.ws;

import static javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.jws.Oneway;
import javax.jws.HandlerChain;
import javax.jws.WebService;
import javax.xml.registry.JAXRException;
import javax.xml.ws.BindingProvider;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.upa.transporter.ws.*;
import pt.upa.transporter.ws.cli.TransporterClient;

@WebService(
	    endpointInterface="pt.upa.broker.ws.BrokerPortType",
	    wsdlLocation="broker.2_0.wsdl",
	    name="BrokerWebService",
	    portName="BrokerPort",
	    targetNamespace="http://ws.broker.upa.pt/",
	    serviceName="BrokerService"
	)
public class BrokerPort implements BrokerPortType{
	private ArrayList <TransporterClient> transporters = new ArrayList <TransporterClient> ();
	private ArrayList <TransportView> jobs = new ArrayList <TransportView> ();
	private boolean replicationBroker=false;
	private BrokerPortType port2 = null;
	private boolean firstTime=true;
	private String udURL;
	
	public BrokerPort(String uddiURL, boolean b) throws BrokerWsException{
		udURL=uddiURL;
		replicationBroker=b;
		if(uddiURL.equals("")){
			String msg = String.format("Client failed can t look on UDDI with %s!", uddiURL);
			throw new BrokerWsException(msg);
		}
		
		UDDINaming uddiNaming;
		try {
			uddiNaming = new UDDINaming(uddiURL);
			Collection <String> endpointAddress = new ArrayList<String>();
			endpointAddress.addAll(uddiNaming.list("UpaTransporter%"));
			for(String s : endpointAddress){
				TransporterClient temp = new TransporterClient(s);
				transporters.add(temp);
			}
			
		}
		catch (JAXRException e) {
			String msg = String.format("Client failed lookup on UDDI at %s!", uddiURL);
			throw new BrokerWsException(msg, e);
		}
		
	}
	
	@Override
	public String ping(String arg) {
		String s="Hello ";
		s=s.concat(arg);
		return s;
	}

	@Override
	public String requestTransport(String origin, String destination, int price)
			throws InvalidPriceFault_Exception, UnavailableTransportFault_Exception,
			UnavailableTransportPriceFault_Exception, UnknownLocationFault_Exception {
		
		ArrayList<String> locations = new ArrayList<String> (Arrays.asList("Porto","Braga","Viana do Castelo", 
				"Vila Real", "Bragança","Lisboa","Leiria","Santarem","Castelo Branco","Coimbra","Aveiro","Viseu",
				"Guarda","Setubal", "Evora","Portalegre","Beja","Faro"));
		
		JobView temp= null;
		TransportView tv = new TransportView();
		tv.setOrigin(origin);
		tv.setDestination(destination);
		tv.setState(TransportStateView.REQUESTED);
		int bestPriceFound=0;
		int tempPrice=0;
		TransporterClient theTransporter=null;
		
		if(price<0){
			InvalidPriceFault ipf = new InvalidPriceFault();
			ipf.setPrice(price);
			throw new InvalidPriceFault_Exception("Negative price", ipf);
		}
		
		if(!(locations.contains(origin))){
			UnknownLocationFault elf = new UnknownLocationFault();
			elf.setLocation(origin);
			throw new UnknownLocationFault_Exception("Unknown origin", elf);
		}
		
		if(!(locations.contains(destination))){
			UnknownLocationFault elf = new UnknownLocationFault();
			elf.setLocation(destination);
			throw new UnknownLocationFault_Exception("Unknown destination", elf);
		}
		
		//This for search every transporter registered in this broker for the cheapest transport requested
		for(TransporterClient t:transporters){
			try{
				temp=t.requestJob(origin, destination, price);
				if(temp==null){
					continue;
				}
				tempPrice=temp.getJobPrice();
				if(tv.getState().equals(TransportStateView.REQUESTED)){//First case and budget
					tv.setState(TransportStateView.BUDGETED);
					bestPriceFound=tempPrice;
				}
				
				if(tempPrice<=bestPriceFound){
					bestPriceFound=tempPrice;
					if(bestPriceFound<=price){//Transport with a lesser price than the limit
						if(tv.getState().equals(TransportStateView.BOOKED)){//decline the previous best offer
							theTransporter.decideJob(tv.getId(), false);
						}
						tv.setId(temp.getJobIdentifier());
						tv.setState(TransportStateView.BOOKED);
						theTransporter=t;
						tv.setPrice(bestPriceFound);
						tv.setTransporterCompany(temp.getCompanyName());
					}
				}
				if (!(tempPrice<=price && tempPrice<=bestPriceFound)){//decline a offer not within the limits given
					t.decideJob(temp.getJobIdentifier(), false);
				}
				
			}
			catch (BadLocationFault_Exception e){
				continue;
			}
			catch (BadPriceFault_Exception e){
				continue;
			}
			catch (BadJobFault_Exception e) {
				System.out.println("ERROR TRYING TO DECIDE JOB (FALSE) WITH ID" + tv.getId());
			}
		}
		if(tv.getState().equals(TransportStateView.BOOKED)){//accept the best offer
			jobs.add(tv);
			try {
				theTransporter.decideJob(tv.getId(), true);
			} catch (BadJobFault_Exception e) {
				System.out.println("ERROR TRYING TO DECIDE JOB (TRUE) WITH ID" + tv.getId());
			}
			if(!replicationBroker){
				updateTransports(tv);
			}
			return tv.getId();
		}
		
		if (bestPriceFound>price && tv.getState().equals(TransportStateView.BUDGETED)){
			tv.setState(TransportStateView.FAILED);
			UnavailableTransportPriceFault utpf = new UnavailableTransportPriceFault();
			utpf.setBestPriceFound(bestPriceFound);
			throw new UnavailableTransportPriceFault_Exception("No transport for limit price given", utpf);
		}
		
		if(tv.getState().equals(TransportStateView.REQUESTED)){
			tv.setState(TransportStateView.FAILED);
			UnavailableTransportFault utf = new UnavailableTransportFault();
			utf.setOrigin(origin);
			utf.setDestination(destination);
			throw new UnavailableTransportFault_Exception("There are no transport available for the given locations", utf);
		}
		
		return null;
	}

	@Override
	public TransportView viewTransport(String id) throws UnknownTransportFault_Exception {
		TransportView view=null;
		JobView jv=null;
		for(TransportView tv: jobs){
			if(tv.getId().equals(id)){
				view=tv;
			}
		}
		if(view!=null){
			for(TransporterClient t:transporters){
				jv=t.jobStatus(id);
				if(jv!=null && !(jv.getJobState().equals(JobStateView.REJECTED))){
					if(jv.getJobState().equals(JobStateView.HEADING)){
						view.setState(TransportStateView.HEADING);
					}
					
					else if(jv.getJobState().equals(JobStateView.ONGOING)){
						view.setState(TransportStateView.ONGOING);
					}
					
					else if(jv.getJobState().equals(JobStateView.COMPLETED)){
						view.setState(TransportStateView.COMPLETED);
					}
					return view;
				}
			}
		}
		UnknownTransportFault utf = new UnknownTransportFault();
		utf.setId(id);
		throw new UnknownTransportFault_Exception("This id doesnt match any transport requested",utf);
		
	}

	@Override
	public List<TransportView> listTransports() {
		ArrayList <TransportView> j = new ArrayList <TransportView> ();
		j.addAll(jobs);
		return j;
	}

	@Override
	public void clearTransports() {
		for(TransporterClient tc: transporters){
			tc.clearJobs();
		}
		jobs.clear();
		if(!replicationBroker){
			updateTransports(null);
		}
		
		
	}

	public void addJobs(TransportView jv){
		System.out.println("Receiving update");
		jobs.add(jv);
	}
	
	@Override
	public void updateTransports(TransportView jv) {
		if(!replicationBroker && firstTime){
			String endpointAddress2="";
			UDDINaming uddiNaming2;
			try {
				uddiNaming2 = new UDDINaming(udURL);
				//System.out.printf("Looking for '%s'%n", "UpaBrokerSlave");
				endpointAddress2 = uddiNaming2.lookup("UpaBrokerSlave");
			} catch (JAXRException e) {
				e.printStackTrace();
			}
			if (endpointAddress2 == null) {
				//System.out.println("Not found!");
				return;
			} else {
				//System.out.printf("Found %s%n", endpointAddress2);
			}

			//System.out.println("Creating stub ...");
			BrokerService service = new BrokerService();
			port2 = service.getBrokerPort();

			//System.out.println("Setting endpoint address ...");
			BindingProvider bindingProvider = (BindingProvider) port2;
			Map<String, Object> requestContext = bindingProvider.getRequestContext();
			requestContext.put(ENDPOINT_ADDRESS_PROPERTY, endpointAddress2);
			firstTime=false;
		}
		if(jv==null){
			port2.clearTransports();
		}
		else{
			System.out.println("Sending update");
			port2.addJobs(jv);
		}
		
	}

}
