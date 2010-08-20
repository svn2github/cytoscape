package org.cytoscape.io.internal.read.sif;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;

import java.util.List;

import static org.mockito.Mockito.*;


import org.cytoscape.test.support.NetworkTestSupport;
import org.cytoscape.test.support.NetworkViewTestSupport;

import org.cytoscape.io.internal.util.ReadUtils;
import org.cytoscape.io.internal.util.StreamUtilImpl;

import org.cytoscape.work.TaskMonitor;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyEdge;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.layout.CyLayouts;
import org.cytoscape.view.layout.CyLayoutAlgorithm;

public class SIFNetworkViewProducerTest {

	TaskMonitor taskMonitor;
	CyNetworkFactory netFactory; 
	CyNetworkViewFactory viewFactory;
	ReadUtils readUtil;
	CyLayouts layouts;
	
	@Before
	public void setUp() throws Exception {
		taskMonitor = mock(TaskMonitor.class);	

		CyLayoutAlgorithm def = mock(CyLayoutAlgorithm.class);

		layouts = mock(CyLayouts.class);
		when(layouts.getDefaultLayout()).thenReturn(def);

		NetworkTestSupport nts = new NetworkTestSupport();
		netFactory = nts.getNetworkFactory();

		NetworkViewTestSupport nvts = new NetworkViewTestSupport();
		viewFactory = nvts.getNetworkViewFactory();

		readUtil = new ReadUtils( new StreamUtilImpl() );
	}

	/**
	 * 'typical' means that all lines have the form "node1 pd node2 [node3 node4 ...]
	 */
	@Test
	public void testReadFromTypicalFile() throws Exception {

		CyNetworkView[] views = getViews("sample.sif");

		CyNetwork net = checkNetwork(views, 31, 27);

		findInteraction(net, "YNL312W", "YPL111W", "pd", 1);
	} 

	/** all lines have the degenerate form "node1"
	 *  that is, with no interaction type and no target
	 */ 
	@Test
	public void testReadFileWithNoInteractions() throws Exception {
		CyNetworkView[] views = getViews("degenerate.sif");

		CyNetwork net = checkNetwork(views, 9, 0);

		for ( CyNode n : net.getNodeList() )
			assertTrue( n.attrs().get("name",String.class).startsWith("Y") );
	} 

	@Test
	public void testReadMultiWordProteinsFile() throws Exception {

		CyNetworkView[] views = getViews("multiWordProteins.sif");

		CyNetwork net = checkNetwork(views, 28, 31);

		findInteraction(net,"26S ubiquitin dependent proteasome", 
		                                  "I-kappa-B-alpha", "interactsWith", 1);
		findInteraction(net,"TRAF6", "RIP2",  "interactsWith", 13);
		findInteraction(net,"TRAF6", "ABCDE oopah",  "interactsWith", 13);
		findInteraction(net,"TRAF6", "HJKOL coltrane",  "interactsWith", 13);

	} 

	@Test
	public void testReadMultiWordProteinsFileWithErrantSpaces() throws Exception {

		CyNetworkView[] views = getViews("multiWordProteinsFileTrailingSpaces.sif");

		CyNetwork net = checkNetwork(views, 28, 31);

		findInteraction(net,"26S ubiquitin dependent proteasome", 
		                                  "I-kappa-B-alpha", "interactsWith", 1);
		findInteraction(net,"TRAF6", "RIP2",  "interactsWith", 13);
		findInteraction(net,"TRAF6", "ABCDE oopah",  "interactsWith", 13);
		findInteraction(net,"TRAF6", "HJKOL coltrane",  "interactsWith", 13);
	} 


	// will fail if it doesn't find the specified interaction
	private void findInteraction(CyNetwork net, String source, String target, 
	                                 String interaction, int count) {
		for ( CyNode n : net.getNodeList() ) {
			if ( n.attrs().get("name",String.class).equals(source) ) {
				List<CyNode> neigh = net.getNeighborList(n,CyEdge.Type.ANY);
				assertEquals(count,neigh.size());
				for ( CyNode nn : neigh ) {
					if ( nn.attrs().get("name",String.class).equals(target) ) {
						List<CyEdge> con = net.getConnectingEdgeList(n,nn, CyEdge.Type.ANY);
						for ( CyEdge e : con ) {
							if ( e.attrs().get("interaction",String.class).equals(interaction) ) {
								return;
							}
						}
					}
				}
			} 
		} 
		fail("couldn't find interaction: " + source + " " + interaction + " " + target );
	}

	// in the SIF world, we only ever create one view for one network 
	private CyNetwork checkNetwork(CyNetworkView[] views, int numNodes, int numEdges) {
		assertNotNull(views);
		assertEquals(1,views.length);

		CyNetwork net = views[0].getModel();

		assertNotNull( net );

		assertEquals( numNodes, net.getNodeCount() );
		assertEquals( numEdges, net.getEdgeCount() );

		return net;
	}

	private CyNetworkView[] getViews(String file) throws Exception {
		File f = new File("./src/test/resources/testData/sif/" + file);
		SIFNetworkViewProducer snvp = new SIFNetworkViewProducer(new FileInputStream(f), 
		                                              readUtil, layouts, viewFactory, netFactory);
		snvp.run(taskMonitor);

		return snvp.getNetworkViews();
	}
}
