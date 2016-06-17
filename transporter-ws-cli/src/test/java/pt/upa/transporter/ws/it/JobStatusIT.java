package pt.upa.transporter.ws.it;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import pt.upa.transporter.ws.BadJobFault_Exception;
import pt.upa.transporter.ws.BadLocationFault_Exception;
import pt.upa.transporter.ws.BadPriceFault_Exception;
import pt.upa.transporter.ws.JobView;

public class JobStatusIT extends ExampleIT {
	
	@Test
    public void unknownJobStatus(){
    	
    		assertEquals(null, client.jobStatus("4000"));
    }
    
    @Test
    public void sucessJobStatus()
    		throws BadLocationFault_Exception, BadPriceFault_Exception, BadJobFault_Exception{
    		
    		JobView jv = client.requestJob("Lisboa", "Faro", 25);
    		client.decideJob(jv.getJobIdentifier(), true);
    		
    		JobView jv2 = client.jobStatus(jv.getJobIdentifier());
    		
    		assertEquals(jv.getJobIdentifier(), jv2.getJobIdentifier());
    }
    
}
