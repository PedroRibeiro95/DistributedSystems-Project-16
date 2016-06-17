package pt.upa.broker;

import pt.upa.broker.ws.TransportStateView;
import pt.upa.broker.ws.TransportView;
import pt.upa.broker.ws.cli.BrokerClient;

public class BrokerClientApplication {
	
	public static void main(String[] args) throws Exception {
		if (args.length < 2) {
			System.err.println("Argument(s) missing!");
			System.err.printf("Usage: java %s uddiURL name%n", BrokerClientApplication.class.getName());
			return;
		}

		String uddiURL = args[0];
		String name = args[1];

		BrokerClient c = new BrokerClient(uddiURL,name);
		String result = c.ping("friend");
		System.out.println(result);
		System.out.println("LISTA DE TRANSPORTS 1 " + c.listTransports());
		System.out.println("Starting request");
		String id = c.requestTransport("Lisboa", "Leiria", 55);
		System.out.println("Finished request with id: " + id + 
				" next the there will be a sleep of 15 sec where you can interrupt the main broker aka UpaBroker");
		Thread.sleep(15000);
		System.out.println("LISTA DE TRANSPORTS 2" + c.listTransports());
		TransportView jc = c.viewTransport(id);
		System.out.println("DEPOIS VIEW: " + jc.getTransporterCompany());
		while(jc.getState()!=TransportStateView.COMPLETED){
			jc = c.viewTransport(id);
			System.out.println("ESTADO " + jc.getState());
		}
		//c.clearTransports();
		System.out.println("LISTA DE TRANSPORTS 4" + c.listTransports());
		
	}

}
