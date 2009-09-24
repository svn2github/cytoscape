package org.cytoscape.webservice.psicquic.test;


import static org.junit.Assert.*;

import javax.xml.transform.TransformerConfigurationException;

import org.cytoscape.webservice.psicquic.RegistoryManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class RegistoryManagerTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void accessRegistory() throws Exception {
		RegistoryManager rm = new RegistoryManager();
		
		assertNotNull(rm.getRegistry());
	}

}
