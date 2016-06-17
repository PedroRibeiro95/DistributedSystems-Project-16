package pt.upa.transporter.ws;

import org.junit.*;

import static org.junit.Assert.*;

import java.util.ArrayList;

/**
 *  Unit Test example
 *  
 *  Invoked by Maven in the "test" life-cycle phase
 *  If necessary, should invoke "mock" remote servers 
 */
public class ExampleTest {

    // static members

		private TransporterPort _transporterPortOdd;
		private TransporterPort _transporterPortEven;
    // one-time initialization and clean-up

    @BeforeClass
    public static void oneTimeSetUp() {

    }

    @AfterClass
    public static void oneTimeTearDown() {

    }


    // members


    // initialization and clean-up for each test

    @Before
    public void setUp() {
    	try {
    		_transporterPortOdd = new TransporterPort(1, "UpaTransporter1");
    		_transporterPortEven = new TransporterPort(2, "UpaTransporter2");
			} catch (Exception e) {
				e.printStackTrace();
			}
    }

    @After
    public void tearDown() {
    	_transporterPortOdd = null;
    	_transporterPortEven = null;
    }


// ---------------------------ping-----------------------------------------------------------------

    @Test
    public void successPing() {
    	
      assertEquals("Pong !", _transporterPortOdd.ping(""));
    }
    
// ---------------------------requestJob-----------------------------------------------------------
    
    @Test(expected = BadLocationFault_Exception.class)
    public void requestJobBadOrigin() 
    		throws BadLocationFault_Exception, BadPriceFault_Exception { 
    	
				_transporterPortOdd.requestJob("Porto", "Lisboa", 10);
    }
    
    @Test(expected = BadLocationFault_Exception.class)
    public void requestJobBadDestination() 
    		throws BadLocationFault_Exception, BadPriceFault_Exception {  	

				_transporterPortOdd.requestJob("Lisboa", "Porto", 10);
    }
    
    @Test(expected = BadPriceFault_Exception.class)
    public void requestJobLowPrice() 
    		throws BadLocationFault_Exception, BadPriceFault_Exception {  	

				_transporterPortOdd.requestJob("Lisboa", "Faro", -1);
    }

    @Test
    public void requestJobPriceOver100() 
    		throws BadLocationFault_Exception, BadPriceFault_Exception {  	

    	assertEquals(null, _transporterPortOdd.requestJob("Lisboa", "Faro", 150));
    }
    
    @Test
    public void requestJobPriceLesserThan10() 
    		throws BadLocationFault_Exception, BadPriceFault_Exception {  	

    	assertTrue(_transporterPortOdd.requestJob("Lisboa", "Faro", 10).getJobPrice() < 10);
    }
    
    @Test
    public void requestJobPrice0() 
    		throws BadLocationFault_Exception, BadPriceFault_Exception {  	

    	assertTrue(_transporterPortOdd.requestJob("Lisboa", "Faro", 0).getJobPrice() == 0);
    }
    
    @Test
    public void requestJobOddPriceOddTransporter() 
    		throws BadLocationFault_Exception, BadPriceFault_Exception {  	

    	assertTrue(_transporterPortOdd.requestJob("Lisboa", "Faro", 51).getJobPrice() < 51);
    }
    
    @Test
    public void requestJobEvenPriceOddTransporter() 
    		throws BadLocationFault_Exception, BadPriceFault_Exception {  	

    	assertTrue(_transporterPortOdd.requestJob("Lisboa", "Faro", 50).getJobPrice() > 50);
    }
    
    @Test
    public void requestJobOddPriceEvenTransporter() 
    		throws BadLocationFault_Exception, BadPriceFault_Exception {  	

    	assertTrue(_transporterPortEven.requestJob("Lisboa", "Porto", 51).getJobPrice() > 51);
    }
    
    @Test
    public void requestJobEvenPriceEventTransporter() 
    		throws BadLocationFault_Exception, BadPriceFault_Exception {  	

    	assertTrue(_transporterPortEven.requestJob("Lisboa", "Porto", 50).getJobPrice() < 50);
    }
    
// ---------------------------decideJob------------------------------------------------------------
    
    @Test(expected = BadJobFault_Exception.class)
    public void unknownJobDecision()
    		throws BadJobFault_Exception {
    		
    		_transporterPortOdd.decideJob("4000", true);
    }
    
    @Test(expected = BadJobFault_Exception.class)
    public void nullJobDecision()
    		throws BadJobFault_Exception {
    		
    		_transporterPortEven.decideJob(null, true);
    }
    
    @Test(expected = BadJobFault_Exception.class)
    public void emptyStringJobDecision()
    		throws BadJobFault_Exception {
    		
    		_transporterPortEven.decideJob("", true);
    }
    
    @Test
    public void acceptedJobDecision()
    		throws BadLocationFault_Exception, BadPriceFault_Exception, BadJobFault_Exception{
    		
    		JobView jv = _transporterPortOdd.requestJob("Lisboa", "Faro", 25);
    		_transporterPortOdd.decideJob(jv.getJobIdentifier(), true);
    		
    		assertTrue("Job does not exit", _transporterPortOdd.listJobs().contains(jv));
    }
    
    @Test
    public void rejectedJobDecision()
    		throws BadLocationFault_Exception, BadPriceFault_Exception, BadJobFault_Exception{
    		
    		JobView jv = _transporterPortOdd.requestJob("Lisboa", "Faro", 25);
    		_transporterPortOdd.decideJob(jv.getJobIdentifier(), false);
    		
    		assertTrue("Wrong job stat", 
    				_transporterPortOdd.jobStatus(jv.getJobIdentifier()).getJobState().equals(JobStateView.REJECTED));
    }
    
// ---------------------------jobStatus------------------------------------------------------------
    
    @Test
    public void unknownJobStatus(){
    	
    		assertEquals(null, _transporterPortOdd.jobStatus("4000"));
    }
    
    @Test
    public void sucessJobStatus()
    		throws BadLocationFault_Exception, BadPriceFault_Exception, BadJobFault_Exception{
    		
    		JobView jv = _transporterPortOdd.requestJob("Lisboa", "Faro", 25);
    		_transporterPortOdd.decideJob(jv.getJobIdentifier(), true);
    		
    		assertEquals(jv, _transporterPortOdd.jobStatus(jv.jobIdentifier));
    }
    
    @Test
    public void emptyStringIDJobStatus(){
    	
    		assertEquals(null, _transporterPortOdd.jobStatus(""));
    }
    
    @Test
    public void nullIDJobStatus(){
    	
    		assertEquals(null, _transporterPortOdd.jobStatus(null));
    }
    
// ---------------------------listAllJobs----------------------------------------------------------
    
    @Test
    public void listAllJobs()
    		throws BadLocationFault_Exception, BadPriceFault_Exception, BadJobFault_Exception{
    		
    		ArrayList<JobView> jobs = new ArrayList<JobView>();
    		
    		JobView jv = _transporterPortOdd.requestJob("Lisboa", "Faro", 25);
    		_transporterPortOdd.decideJob(jv.getJobIdentifier(), true);
    		JobView jv2 = _transporterPortOdd.requestJob("Lisboa", "Faro", 25);
    		_transporterPortOdd.decideJob(jv2.getJobIdentifier(), true);
    		
    		jobs.add(jv);
    		jobs.add(jv2);
    		
    		assertEquals(jobs, _transporterPortOdd.listJobs());
    }
    
// ---------------------------clearAllJobs---------------------------------------------------------
    
    @Test
    public void clearAllJobs()
    		throws BadLocationFault_Exception, BadPriceFault_Exception, BadJobFault_Exception{
    		
    		ArrayList<JobView> jobs = new ArrayList<JobView>();
    		
    		JobView jv = _transporterPortOdd.requestJob("Lisboa", "Faro", 25);
    		_transporterPortOdd.decideJob(jv.getJobIdentifier(), true);
    		JobView jv2 = _transporterPortOdd.requestJob("Lisboa", "Faro", 25);
    		_transporterPortOdd.decideJob(jv2.getJobIdentifier(), true);
    		_transporterPortOdd.clearJobs();
    		
    		assertEquals(jobs, _transporterPortOdd.listJobs());
    }
}