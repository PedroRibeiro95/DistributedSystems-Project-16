package pt.upa.transporter;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.upa.transporter.ws.JobStateView;
import pt.upa.transporter.ws.JobView;
import pt.upa.transporter.ws.cli.TransporterClient;



public class TransporterClientApplication {

	public static void main(String[] args) throws Exception {
		if (args.length < 2) {
			System.err.println("Argument(s) missing!");
			System.err.printf("Usage: java %s uddiURL name%n", TransporterClientApplication.class.getName());
			return;
		}
		

		String uddiURL = args[0];
		String name = args[1];
		UDDINaming uddiNaming = new UDDINaming(uddiURL);
		String endpointAddress = uddiNaming.lookup(name);
		
		TransporterClient t = new TransporterClient(endpointAddress);
		
		String result = t.ping("friend");
		System.out.println(result);
		/*System.out.println("LISTA DE JOBS1" + t.listJobs());
		JobView jv = t.requestJob("Lisboa", "Leiria", 55);
		System.out.println("LISTA DE JOBS2" + t.listJobs());
		JobView je = t.decideJob(jv.getJobIdentifier(),true);
		System.out.println("LISTA DE JOBS3" + t.listJobs());
		JobView jc = t.jobStatus(jv.getJobIdentifier());
		while(jc.getJobState()!=JobStateView.COMPLETED){
			jc = t.jobStatus(jv.getJobIdentifier());
			System.out.println("ESTADO " + jc.getJobState());
		}
		t.clearJobs();
		System.out.println("LISTA DE JOBS" + t.listJobs());
		
		System.out.println("LISTA DE JOBS4" + t.listJobs());
		jv = t.requestJob("Lisboa", "Leiria", 55);
		System.out.println("LISTA DE JOBS5" + t.listJobs());
		je = t.decideJob(jv.getJobIdentifier(),true);
		System.out.println("LISTA DE JOBS6" + t.listJobs());
		jc = t.jobStatus(jv.getJobIdentifier());
		while(jc.getJobState()!=JobStateView.COMPLETED){
			jc = t.jobStatus(jv.getJobIdentifier());
			System.out.println("ESTADO " + jc.getJobState());
		}
		t.clearJobs();
		System.out.println("LISTA DE JOBS7" + t.listJobs());*/
	}
}
