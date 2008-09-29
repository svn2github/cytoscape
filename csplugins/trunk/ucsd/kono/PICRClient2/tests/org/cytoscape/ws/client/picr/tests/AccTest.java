package org.cytoscape.ws.client.picr.tests;


import javax.xml.rpc.ServiceException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ebi.picr.AccessionMapperInterface;
import ebi.picr.AccessionMapperService;
import ebi.picr.AccessionMapperService_Impl;

public class AccTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	
	@Test
	public void cleintTest() {
		final AccessionMapperService mapper = new AccessionMapperService_Impl();
		AccessionMapperInterface clientStub2 = null;
		try {
			clientStub2 = mapper.getAccessionMapperPort();
		} catch (ServiceException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
}
