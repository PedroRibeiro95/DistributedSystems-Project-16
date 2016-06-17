package pt.upa.transporter.ws.it;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Test;

import pt.upa.transporter.ws.BadJobFault_Exception;
import pt.upa.transporter.ws.BadLocationFault_Exception;
import pt.upa.transporter.ws.BadPriceFault_Exception;
import pt.upa.transporter.ws.JobView;

public class ClearAllJobsIT extends ExampleIT {

	@Test
    public void clearAllJobs()
    		throws BadLocationFault_Exception, BadPriceFault_Exception, BadJobFault_Exception{
    		
    		ArrayList<JobView> jobs = new ArrayList<JobView>();
    		
    		JobView jv = client.requestJob("Lisboa", "Faro", 25);
    		client.decideJob(jv.getJobIdentifier(), true);
    		JobView jv2 = client.requestJob("Lisboa", "Faro", 25);
    		client.decideJob(jv2.getJobIdentifier(), true);
    		client.clearJobs();
    		
    		assertEquals(jobs, client.listJobs());
    }
}
