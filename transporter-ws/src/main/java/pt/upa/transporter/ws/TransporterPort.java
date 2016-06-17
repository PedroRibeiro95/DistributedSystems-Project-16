package pt.upa.transporter.ws;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.annotation.Resource;
import javax.jws.HandlerChain;
import javax.jws.WebService;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

import pt.upa.transporter.handler.HeaderHandler;

@WebService(
	    endpointInterface="pt.upa.transporter.ws.TransporterPortType",
	    wsdlLocation="transporter.1_0.wsdl",
	    name="TransporterWebService",
	    portName="TransporterPort",
	    targetNamespace="http://ws.transporter.upa.pt/",
	    serviceName="TransporterService"
	)
@HandlerChain(file="/handler-chain.xml")
public class TransporterPort implements TransporterPortType{

	static Integer id=0;
	private Timer timer;
	private int transporterNumber=0;
	private ArrayList <JobView> jobs = new ArrayList <JobView> ();
	private String identifier;
	private String name;
	private ArrayList<String> cities = new ArrayList<String> ();
	private ArrayList<String> option1 = new ArrayList<String> (Arrays.asList("Porto","Braga","Viana do Castelo", 
			"Vila Real", "Bragança","Lisboa","Leiria","Santarem","Castelo Branco","Coimbra","Aveiro","Viseu","Guarda"));
	private ArrayList<String> option2 = new ArrayList<String> (Arrays.asList("Lisboa","Leiria","Santarem","Castelo Branco","Coimbra",
			"Aveiro","Viseu","Guarda", "Setubal", "Evora","Portalegre","Beja","Faro"));
	
	public static final String CLASS_NAME = TransporterPort.class.getSimpleName();
	public static final String TOKEN = "Transporter";

	@Resource
	private WebServiceContext webServiceContext;
	
	
	public TransporterPort(int i, String n){
		transporterNumber=i;
		name=n;
		if (i%2==0){
			cities=option1;
		}
		else{
			cities=option2;
		}
	}
	
	public void messageContext(){
		if(webServiceContext==null){
			return;
		}
		MessageContext messageContext = webServiceContext.getMessageContext();

		
		// *** #7 ***
		// put token in message context
		String newValue = TOKEN + transporterNumber;
		System.out.printf("%s put token '%s' on request context%n", CLASS_NAME, TOKEN);
		messageContext.put(HeaderHandler.RESPONSE_PROPERTY, newValue);
	}
	
	
	@Override
	public String ping(String name) {
		messageContext();
		return "Pong " + name + "!";
	}

	@Override
	public JobView requestJob(String origin, String destination, int price)
			throws BadLocationFault_Exception, BadPriceFault_Exception {
		messageContext();
		identifier=id.toString();
		
		if(origin == null || !(cities.contains(origin))){
			BadLocationFault blf = new BadLocationFault();
			blf.setLocation(origin);
			throw new BadLocationFault_Exception("Origin unknown", blf);
		}
		
		if(destination == null || !(cities.contains(destination))){
			BadLocationFault blf = new BadLocationFault();
			blf.setLocation(destination);
			throw new BadLocationFault_Exception("Destination unknown", blf);
		}
		
		if(price<0){
			BadPriceFault bpf = new BadPriceFault();
			bpf.setPrice(price);
			throw new BadPriceFault_Exception("Price not positive",bpf);
		}
		
		JobView jV= new JobView();
		jV.setCompanyName(name);
		jV.setJobOrigin(origin);
		jV.setJobDestination(destination);
		jV.setJobIdentifier(identifier);
		jV.setJobState(JobStateView.PROPOSED);
		
		if (price>100){
			return null;
		}
		if(price<=10){
			id++;
			jV.setJobPrice(price-1);
			if(price==0){
				jV.setJobPrice(0);
			}
			jobs.add(jV);
			return jV;
		}
		
		if(price>10 && price<=100){
			id++;
			if(transporterNumber%2==0 && price%2==0){
				jV.setJobPrice(price-1);
			}
			
			if(transporterNumber%2==0 && price%2!=0){
				jV.setJobPrice(price*2);
			}
			
			if(transporterNumber%2!=0 && price%2!=0){
				jV.setJobPrice(price-1);
			}
			
			if(transporterNumber%2!=0 && price%2==0){
				jV.setJobPrice(price*2);
			}
			jobs.add(jV);
			return jV;	
		}
		
		return null;
	}

	@Override
	public JobView decideJob(String id, boolean accept) throws BadJobFault_Exception {
		messageContext();
		for(JobView jV:jobs){
			if(jV.getJobIdentifier().equals(id)){
				if(accept==true){
					jV.setJobState(JobStateView.ACCEPTED);
				}
				else{
					jV.setJobState(JobStateView.REJECTED);
				}
				return jV;
			}
		}
		BadJobFault bjf = new BadJobFault ();
		bjf.setId(id);
		throw new BadJobFault_Exception("This id isn t a job budgeted by this trasporter",bjf);
		
		//return null;
	}

	@Override
	public JobView jobStatus(String id) {
		messageContext();
		for(JobView jv:jobs){
			if(jv.getJobIdentifier().equals(id)){
				return jv;
			}
		}
		return null;
	}

	@Override
	public List<JobView> listJobs() {
		messageContext();
		return jobs;
	}

	@Override
	public void clearJobs() {
		messageContext();
		jobs.clear();
	}

	// TODO
	
	public void timerFunc(Timer t){
		timer=t;
		TimerTask tasknewAH = new TimerTask(){
			public void run() {
				for(JobView jv : jobs){
					if(jv.getJobState().equals(JobStateView.ACCEPTED)){
						jv.setJobState(JobStateView.HEADING);
					}
				}
			}
		};
		TimerTask tasknewHO = new TimerTask(){
			public void run() {
				for(JobView jv : jobs){				
					if(jv.getJobState().equals(JobStateView.HEADING)){
						jv.setJobState(JobStateView.ONGOING);
					}
				}
			}
		};
		TimerTask tasknewOC = new TimerTask(){
			public void run() {
				for(JobView jv : jobs){
					if(jv.getJobState().equals(JobStateView.ONGOING)){
						jv.setJobState(JobStateView.COMPLETED);
					}
				}
			}
		};
		Random r = new Random();
		Double td=1000+((r.nextDouble())*4000);
		int ti=td.intValue();
		timer.schedule(tasknewAH,ti, ti);
		td=1000 + ((r.nextDouble())*4000);
		ti=td.intValue();
		timer.schedule(tasknewHO,ti, ti);
		td=1000+((r.nextDouble())*4000);
		ti=td.intValue();
		timer.schedule(tasknewOC,ti, ti);
		
	}

}
