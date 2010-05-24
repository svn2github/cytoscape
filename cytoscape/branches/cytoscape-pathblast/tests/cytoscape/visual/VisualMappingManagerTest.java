/*
 File: VisualMappingManagerTest.java

 Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)

 The Cytoscape Consortium is:
 - Institute for Systems Biology
 - University of California San Diego
 - Memorial Sloan-Kettering Cancer Center
 - Institut Pasteur
 - Agilent Technologies

 This library is free software; you can redistribute it and/or modify it
 under the terms of the GNU Lesser General Public License as published
 by the Free Software Foundation; either version 2.1 of the License, or
 any later version.

 This library is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 documentation provided hereunder is on an "as is" basis, and the
 Institute for Systems Biology and the Whitehead Institute
 have no obligations to provide maintenance, support,
 updates, enhancements or modifications.  In no event shall the
 Institute for Systems Biology and the Whitehead Institute
 be liable to any party for direct, indirect, special,
 incidental or consequential damages, including lost profits, arising
 out of the use of this software and its documentation, even if the
 Institute for Systems Biology and the Whitehead Institute
 have been advised of the possibility of such damage.  See
 the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation,
 Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 */

// VisualMappingManagerTest.java
//----------------------------------------------------------------------------
// $Revision: 18686 $
// $Date: 2009-12-07 13:56:52 -0800 (Mon, 07 Dec 2009) $
// $Author: mes $
//----------------------------------------------------------------------------
package cytoscape.visual;

import giny.model.Node;
import giny.model.RootGraph;
import giny.view.NodeView;

import java.awt.Color;
import java.awt.Font;
import java.io.FileReader;
import java.util.Properties;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.view.CyNetworkView;
import cytoscape.data.CyAttributes;
import cytoscape.data.Semantics;
import cytoscape.data.readers.CyAttributesReader;
import cytoscape.util.FileUtil;
import static cytoscape.visual.VisualPropertyType.*;

/**
 * 
 */
public class VisualMappingManagerTest extends TestCase {
	CyNetwork cyNet;
	CyNode a;
	CyNode b;
	CyNode c;
	CyEdge ab;
	CalculatorCatalog catalog;
	Properties props;

	/**
	 * Creates a new VisualMappingManagerTest object.
	 * 
	 * @param name
	 *            DOCUMENT ME!
	 */
	public VisualMappingManagerTest(String name) {
		super(name);
	}

	/**
	 * DOCUMENT ME!
	 */
	public void setUp() {
		System.out.println("setup begin");
		cyNet = Cytoscape.createNetworkFromFile("testData/small.sif");
		a = Cytoscape.getCyNode("a");
		b = Cytoscape.getCyNode("b");
		c = Cytoscape.getCyNode("c");
		ab = Cytoscape.getCyEdge(a, b, Semantics.INTERACTION, "pp", false);
		props = new Properties();

		try {
			CyAttributesReader.loadAttributes(Cytoscape.getNodeAttributes(),
					new FileReader("testData/small.nodeAttr"));
			props.load(FileUtil.getInputStream("testData/small.vizmap.props"));
		} catch (Exception e) {
			e.printStackTrace();
		}

		catalog = new CalculatorCatalog();
		CalculatorIO.loadCalculators(props, catalog, true);
		System.out.println("setup end");
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @throws Exception
	 *             DOCUMENT ME!
	 */
	public void testBypass() throws Exception {
		Color fillColor = new Color(63, 128, 255);
		Color borderColor = new Color(100, 100, 50);
		NodeShape shape = NodeShape.DIAMOND;
		double width = 49.0;
		double height = 79.0;
		String label = "testLabel";
		String toolTip = "testToolTip";
		Font font = new Font("SansSerif", Font.ITALIC, 10);
		String fontString = "SansSerif,italic,10";

		RootGraph graph = Cytoscape.getRootGraph();
		int index1 = graph.createNode();
		Node first = graph.getNode(index1);
		int index2 = graph.createNode();
		Node second = graph.getNode(index2);

		CyNetwork network2 = Cytoscape.createNetwork(Cytoscape.getRootGraph()
				.getNodeIndicesArray(), Cytoscape.getRootGraph()
				.getEdgeIndicesArray(), null);

		CyAttributes secondNodeAttr = Cytoscape.getNodeAttributes();
		String secondName = second.getIdentifier();
		secondNodeAttr.setAttribute(secondName, "node.fillColor", "63,128,255");
		secondNodeAttr.setAttribute(secondName, "node.borderColor", "100,100,50");
		secondNodeAttr.setAttribute(secondName, "node.size", "32.0");

		CyNetworkView view = Cytoscape.getNetworkView( network2.getIdentifier() );
		NodeView nv = view.getNodeView(index2);

		VisualMappingManager vmm = Cytoscape.getVisualMappingManager();
		vmm.setNetworkView(view);
		vmm.setVisualStyle(view.getVisualStyle()); 

		vmm.vizmapNode(nv,view);

		assertEquals(fillColor, nv.getUnselectedPaint());
		assertEquals(borderColor, nv.getBorderPaint());
		// node size locked
		assertEquals(32.0, nv.getWidth());
		assertEquals(32.0, nv.getHeight());
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param args
	 *            DOCUMENT ME!
	 */
	public static void main(String[] args) {
		junit.textui.TestRunner.run(new TestSuite(
				VisualMappingManagerTest.class));
	}
}
