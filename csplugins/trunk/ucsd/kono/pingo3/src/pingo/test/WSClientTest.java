package pingo.test;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import pingo.webservice.QuickGOClient;

public class WSClientTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testClient() throws Exception {
		QuickGOClient client = new QuickGOClient();
		client.search("test");
	}

}
