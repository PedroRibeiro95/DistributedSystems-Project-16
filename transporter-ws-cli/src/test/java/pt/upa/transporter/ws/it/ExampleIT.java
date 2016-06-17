package pt.upa.transporter.ws.it;

import org.junit.*;

import java.io.IOException;
import java.util.Properties;

import javax.xml.registry.JAXRException;

import org.junit.AfterClass;
import org.junit.BeforeClass;


import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.upa.transporter.ws.cli.*;

/**
 *  Integration Test example
 *  
 *  Invoked by Maven in the "verify" life-cycle phase
 *  Should invoke "live" remote servers 
 */
public abstract class ExampleIT {

  // static members

	protected static TransporterClient client;

  // one-time initialization and clean-up

  @BeforeClass
  public static void oneTimeSetUp() throws JAXRException, IOException {
	UDDINaming uddiNaming = new UDDINaming("http://localhost:9090");
	String endpointAddress = uddiNaming.lookup("UpaTransporter1");
	client = new TransporterClient(endpointAddress);

  }

  @AfterClass
  public static void oneTimeTearDown() {
  	client = null;
  }

}