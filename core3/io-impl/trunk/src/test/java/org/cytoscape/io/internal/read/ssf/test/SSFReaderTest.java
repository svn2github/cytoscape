package org.cytoscape.io.internal.read.ssf.test;


import static org.easymock.EasyMock.createMock;
import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.util.Map;

import org.cytoscape.event.CyEventHelper;
import org.cytoscape.io.internal.read.ssf.SSFReader;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.internal.CyNetworkFactoryImpl;
import org.cytoscape.model.internal.CyRootNetworkFactoryImpl;
import org.cytoscape.model.subnetwork.CyRootNetwork;
import org.cytoscape.model.subnetwork.CyRootNetworkFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SSFReaderTest {
	
	private static final String FILE_LOCATION = "src/test/resources/testData/SSFTestData/";
	
	private SSFReader reader;
	
	private CyEventHelper helperMock;
	private CyNetworkFactory factory;
	private CyRootNetworkFactory rnFactory;
	
	final File testFile1 = new File(FILE_LOCATION + "ssf_test1.ssf");
	final File testFile2 = new File(FILE_LOCATION + "ssf_test2.ssf");
	final File testFile3 = new File(FILE_LOCATION + "ssf_test3.ssf");
	final File testFile4 = new File(FILE_LOCATION + "ssf_test4.ssf");

	@Before
	public void setUp() throws Exception {
		rnFactory = new CyRootNetworkFactoryImpl();
		reader = new SSFReader(rnFactory);
		helperMock = createMock(CyEventHelper.class);
		factory = new CyNetworkFactoryImpl(helperMock);
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testRead1() throws Exception {
		FileInputStream is = new FileInputStream(testFile1);
		reader.setCyNetworkFactory(factory);
		reader.setInputStream(is);
		
		
		Map<Class<?>, Object> res = reader.read();
		CyRootNetwork rootNetwork = (CyRootNetwork) res.get(CyRootNetwork.class);
		assertNotNull(rootNetwork);
		assertEquals(5, rootNetwork.getNodeCount());
		assertEquals(2, rootNetwork.getEdgeCount());
		assertEquals(2, rootNetwork.getMetaNodeList().size());
		assertEquals(1, rootNetwork.getMetaNodeList().get(0).getSubNetwork().getNodeCount());
		assertEquals(2, rootNetwork.getMetaNodeList().get(1).getSubNetwork().getNodeCount());
		
		System.out.println("Node Count = " + rootNetwork.getNodeCount());
		System.out.println("Edge Count = " + rootNetwork.getEdgeCount());
		
		System.out.println("\n\n==========================\n\n");
		
		is.close();
	}
	
	@Test
	public void testRead2() throws Exception {
		FileInputStream is = new FileInputStream(testFile2);
		reader.setCyNetworkFactory(factory);
		reader.setInputStream(is);
		
		
		Map<Class<?>, Object> res = reader.read();
		CyRootNetwork rootNetwork = (CyRootNetwork) res.get(CyRootNetwork.class);
		assertNotNull(rootNetwork);
		assertEquals(8, rootNetwork.getNodeCount());
		assertEquals(3, rootNetwork.getEdgeCount());
		assertEquals(4, rootNetwork.getMetaNodeList().size());
		
		System.out.println("Node Count = " + rootNetwork.getNodeCount());
		System.out.println("Edge Count = " + rootNetwork.getEdgeCount());

		System.out.println("\n\n==========================\n\n");
	
		
		is.close();
		
	}

}
