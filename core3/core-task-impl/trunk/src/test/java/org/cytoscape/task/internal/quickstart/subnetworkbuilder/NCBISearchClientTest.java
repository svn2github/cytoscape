package org.cytoscape.task.internal.quickstart.subnetworkbuilder;

import static org.junit.Assert.*;

import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class NCBISearchClientTest {

    private NCBISearchClient client;

    @Before
    public void setUp() throws Exception {
	client = new NCBISearchClient();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testSearchClient() throws Exception {

	final String disease = "Rheumatoid Arthritis";
	final String go1 = "neurogenesis";
	final String go2 = "Rho GTPase activity";
	
	// Enable this for client test.
//	final Set<String> result1 = client.search(disease, go1);
//
//	assertNotNull(result1);
//	assertEquals(322, result1.size());
//
//	final Set<String> result2 = client.search(disease, go1 + ", " + go2);
//
//	assertNotNull(result2);
//	assertEquals(391, result2.size());
    }

}
