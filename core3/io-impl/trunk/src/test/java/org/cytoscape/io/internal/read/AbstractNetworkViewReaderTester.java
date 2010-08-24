package org.cytoscape.io.internal.read;

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

public class AbstractNetworkViewReaderTester {

	protected TaskMonitor taskMonitor;
	protected CyNetworkFactory netFactory; 
	protected CyNetworkViewFactory viewFactory;
	protected ReadUtils readUtil;
	protected CyLayouts layouts;
	
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
	 * Will fail if it doesn't find the specified interaction.
	 */
	protected void findInteraction(CyNetwork net, String source, String target, 
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

	/**
	 * Assuming we only create one network. 
	 */
	protected CyNetwork checkSingleNetwork(CyNetworkView[] views, int numNodes, int numEdges) {
		assertNotNull(views);
		assertEquals(1,views.length);

		CyNetwork net = views[0].getModel();

		assertNotNull( net );

		assertEquals( numNodes, net.getNodeCount() );
		assertEquals( numEdges, net.getEdgeCount() );

		return net;
	}
}
