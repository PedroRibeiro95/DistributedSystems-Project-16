package pt.upa.transporter.ws.it;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import pt.upa.transporter.ws.BadJobFault_Exception;
import pt.upa.transporter.ws.BadLocationFault_Exception;
import pt.upa.transporter.ws.BadPriceFault_Exception;
import pt.upa.transporter.ws.JobView;

public class DecideJobIT extends ExampleIT {
	
	@Test(expected = BadJobFault_Exception.class)
    public void unknownJobDecision()
    		throws BadJobFault_Exception {
    		
    		client.decideJob("4000", true);
    }
    
    @Test
    public void acceptedJobDecision()
    		throws BadLocationFault_Exception, BadPriceFault_Exception, BadJobFault_Exception{
    		
    		JobView jv = client.requestJob("Lisboa", "Faro", 25);
    		client.decideJob(jv.getJobIdentifier(), true);
    		
    		boolean test = false;
    		for(JobView j: client.listJobs()){
    			if(j.getJobIdentifier().equals(jv.getJobIdentifier())){
    				test = true;
    			}
    		}
    		
    		assertTrue("Job does not exist", test);
    }
    
    @Test
    public void rejectedJobDecision()
    		throws BadLocationFault_Exception, BadPriceFault_Exception, BadJobFault_Exception{
    		
    		JobView jv = client.requestJob("Lisboa", "Faro", 25);
    		client.decideJob(jv.getJobIdentifier(), false);
    		
    		boolean test = false;
    		for(JobView j: client.listJobs()){
    			if(j.getJobIdentifier().equals(jv.getJobIdentifier())){
    				test = true;
    			}
    		}
    		
    		assertTrue("Job exists", test);
    }
	
}
