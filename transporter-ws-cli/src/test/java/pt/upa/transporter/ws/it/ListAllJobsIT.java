package pt.upa.transporter.ws.it;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Test;

import pt.upa.transporter.ws.BadJobFault_Exception;
import pt.upa.transporter.ws.BadLocationFault_Exception;
import pt.upa.transporter.ws.BadPriceFault_Exception;
import pt.upa.transporter.ws.JobView;

public class ListAllJobsIT extends ExampleIT {
	
	@Test
    public void listAllJobs()
    		throws BadLocationFault_Exception, BadPriceFault_Exception, BadJobFault_Exception{
    		
    		ArrayList<JobView> jobs = new ArrayList<JobView>();
    		
    		JobView jv = client.requestJob("Lisboa", "Faro", 25);
    		client.decideJob(jv.getJobIdentifier(), true);
    		JobView jv2 = client.requestJob("Lisboa", "Faro", 25);
    		client.decideJob(jv2.getJobIdentifier(), true);
    		
    		jobs.add(jv);
    		jobs.add(jv2);
    		
    		int test = 0;
    		for(JobView j: jobs){
    			for(JobView j2: client.listJobs()){
    				if(j.getJobIdentifier().equals(j2.getJobIdentifier())){
    					test++;
    				}
    			}
    		}
    		
    		assertTrue("Jobs incorrectly listed", test == jobs.size());
    }
}
