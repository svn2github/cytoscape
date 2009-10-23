package org.cytoscape.io.internal.read.ssf.test;

import static org.easymock.EasyMock.createMock;
import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.util.Map;

import org.cytoscape.event.CyEventHelper;
import org.cytoscape.io.internal.read.ssf.SSFReader;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.internal.CyNetworkFactoryImpl;
import org.cytoscape.model.internal.CyRootNetworkFactoryImpl;
import org.cytoscape.model.subnetwork.CyMetaNode;
import org.cytoscape.model.subnetwork.CyRootNetwork;
import org.cytoscape.model.subnetwork.CyRootNetworkFactory;
import org.cytoscape.model.subnetwork.CySubNetwork;
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
		System.out.println("\n\n=========  Test 1 =================\n\n");
		FileInputStream is = new FileInputStream(testFile1);
		reader.setCyNetworkFactory(factory);
		reader.setInputStream(is);

		Map<Class<?>, Object> res = reader.read();
		CySubNetwork baseNetwork = (CySubNetwork) res.get(CyNetwork.class);

		CyRootNetwork rootNet = baseNetwork.getRootNetwork();
		assertNotNull(rootNet);
		assertEquals(5, rootNet.getNodeCount());
		assertEquals(2, rootNet.getEdgeCount());
		assertTrue(rootNet instanceof CyRootNetwork);
		assertEquals(2, rootNet.getMetaNodeList().size());
		assertEquals(1, rootNet.getMetaNodeList().get(0).getSubNetwork()
				.getNodeCount());
		assertEquals(2, rootNet.getMetaNodeList().get(1).getSubNetwork()
				.getNodeCount());

		System.out.println("Node Count = " + baseNetwork.getNodeCount());
		System.out.println("Edge Count = " + baseNetwork.getEdgeCount());

		is.close();
		System.out.println("\n\n=========  Test 1 End =================\n\n");
	}

	@Test
	public void testRead2() throws Exception {
		System.out.println("\n\n=========  Test 2 =================\n\n");
		FileInputStream is = new FileInputStream(testFile2);
		reader.setCyNetworkFactory(factory);
		reader.setInputStream(is);

		Map<Class<?>, Object> res = reader.read();
		CySubNetwork baseNetwork = (CySubNetwork) res.get(CyNetwork.class);

		CyRootNetwork rootNetwork = baseNetwork.getRootNetwork();

		assertNotNull(rootNetwork);
		assertEquals(8, rootNetwork.getNodeCount());
		assertEquals(3, rootNetwork.getEdgeCount());
		assertEquals(4, rootNetwork.getMetaNodeList().size());

		String metanodeName;
		for (CyMetaNode metanode : rootNetwork.getMetaNodeList()) {
			metanodeName = metanode.attrs().get("name", String.class);
			System.out.println("MN Name = " + metanodeName);
			if (metanodeName.equals("M2"))
				assertEquals(1, metanode.getSubNetwork().getEdgeCount());
			else if (metanodeName.equals("M4"))
				assertEquals(7, metanode.getSubNetwork().getNodeCount());

		}

		System.out.println("Node Count = " + rootNetwork.getNodeCount());
		System.out.println("Edge Count = " + rootNetwork.getEdgeCount());

		is.close();
		System.out.println("\n\n=========  Test 2 End =================\n\n");
	}

	@Test
	public void testRead3() throws Exception {
		System.out.println("\n\n=========  Test 3 =================\n\n");
		FileInputStream is = new FileInputStream(testFile3);
		reader.setCyNetworkFactory(factory);
		reader.setInputStream(is);

		Map<Class<?>, Object> res = reader.read();
		CySubNetwork baseNetwork = (CySubNetwork) res.get(CyNetwork.class);

		CyRootNetwork rootNetwork = baseNetwork.getRootNetwork();
		assertNotNull(rootNetwork);
		assertEquals(5, rootNetwork.getNodeCount());
		assertEquals(2, rootNetwork.getEdgeCount());
		assertEquals(2, rootNetwork.getMetaNodeList().size());

		String metanodeName;
		for (CyMetaNode metanode : rootNetwork.getMetaNodeList()) {
			metanodeName = metanode.attrs().get("name", String.class);
			System.out.println("MN Name = " + metanodeName);
			if (metanodeName.equals("M1"))
				assertEquals(1, metanode.getSubNetwork().getNodeCount());
			else if (metanodeName.equals("M2")) {
				for (CyNode n : metanode.getSubNetwork().getNodeList()) {
					System.out.println(n.attrs().get("name", String.class));
				}

				assertEquals(2, metanode.getSubNetwork().getNodeCount());
			}
		}

		System.out.println("Node Count = " + rootNetwork.getNodeCount());
		System.out.println("Edge Count = " + rootNetwork.getEdgeCount());

		is.close();
		System.out.println("\n\n=========  Test 3 End =================\n\n");
	}

	@Test
	public void testRead4() throws Exception {
		System.out.println("\n\n=========  Test 4 =================\n\n");
		FileInputStream is = new FileInputStream(testFile4);
		reader.setCyNetworkFactory(factory);
		reader.setInputStream(is);

		Map<Class<?>, Object> res = reader.read();
		CySubNetwork baseNetwork = (CySubNetwork) res.get(CyNetwork.class);

		CyRootNetwork rootNetwork = baseNetwork.getRootNetwork();
		assertEquals(9, rootNetwork.getNodeCount());
		assertEquals(8, rootNetwork.getEdgeCount());
		assertEquals(3, rootNetwork.getMetaNodeList().size());

		String metanodeName;
		for (CyMetaNode metanode : rootNetwork.getMetaNodeList()) {
			metanodeName = metanode.attrs().get("name", String.class);
			System.out.println("MN Name = " + metanodeName);
			if (metanodeName.equals("M1"))
				assertEquals(2, metanode.getSubNetwork().getNodeCount());
			else if (metanodeName.equals("M3"))
				assertEquals(4, metanode.getSubNetwork().getNodeCount());

		}

		System.out.println("Node Count = " + rootNetwork.getNodeCount());
		System.out.println("Edge Count = " + rootNetwork.getEdgeCount());

		is.close();
		System.out.println("\n\n=========  Test 4 End =================\n\n");
	}

}
