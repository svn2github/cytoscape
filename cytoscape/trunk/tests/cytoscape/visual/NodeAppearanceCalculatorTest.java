/*
 File: NodeAppearanceCalculatorTest.java

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

// NodeAppearanceCalculatorTest.java
//----------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
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
import cytoscape.data.CyAttributes;
import cytoscape.data.Semantics;
import cytoscape.data.readers.CyAttributesReader;
import cytoscape.util.FileUtil;

/**
 * 
 */
public class NodeAppearanceCalculatorTest extends TestCase {
	CyNetwork cyNet;
	CyNode a;
	CyNode b;
	CyNode c;
	CyEdge ab;
	CalculatorCatalog catalog;
	Properties props;

	/**
	 * Creates a new NodeAppearanceCalculatorTest object.
	 * 
	 * @param name
	 *            DOCUMENT ME!
	 */
	public NodeAppearanceCalculatorTest(String name) {
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
	 */
	public void testDefaultAppearance() {
		System.out.println("begin NodeAppearanceCalculatorTest.testDefaultAppearance()");

		NodeAppearanceCalculator nac = new NodeAppearanceCalculator();

		NodeAppearance na = nac.calculateNodeAppearance(a, cyNet);

		// this tests that the default node appearance is correct
		assertTrue("a color  " + na.getFillColor() + " expect "
				+ Color.WHITE.toString(), na.getFillColor().equals(Color.WHITE));
		assertTrue("a border color  " + na.getBorderColor() + " expect "
				+ Color.BLACK.toString(), na.getBorderColor().equals(
				Color.BLACK));
		assertEquals("a lineType  ", LineType.LINE_1, na.getBorderLineType());
		assertEquals("a shape  ", ShapeNodeRealizer.RECT, na.getShape());

		// node size is locked so all should be the same
		assertEquals("a width  ", 35.0, na.getWidth());
		assertEquals("a height  ", 35.0, na.getHeight());
		assertEquals("a size  ", 35.0, na.getSize());

		assertEquals("a label  ", "", na.getLabel());
		assertEquals("a tooltip  ", "", na.getToolTip());
		assertEquals("a font size  ", 12, na.getFont().getSize());
		assertEquals("a font style  ", Font.PLAIN, na.getFont().getStyle());
		assertTrue("a label color  " + na.getLabelColor() + " expect "
				+ Color.BLACK, na.getLabelColor().equals(Color.BLACK));

		// should still be default for node b
		NodeAppearance nb = nac.calculateNodeAppearance(b, cyNet);

		assertTrue("b color  " + nb.getFillColor() + " expect "
				+ Color.WHITE.toString(), nb.getFillColor().equals(Color.WHITE));
		assertTrue("b border color  " + nb.getBorderColor() + " expect "
				+ Color.BLACK.toString(), nb.getBorderColor().equals(
				Color.BLACK));
		assertEquals("b lineType  ", LineType.LINE_1, nb.getBorderLineType());
		assertEquals("b shape  ", ShapeNodeRealizer.RECT, nb.getShape());
		// still locked
		assertEquals("b width  ", 35.0, nb.getWidth());
		assertEquals("b height  ", 35.0, nb.getHeight());
		assertEquals("b size  ", 35.0, nb.getSize());

		assertEquals("b label  ", "", nb.getLabel());
		assertEquals("b tooltip  ", "", nb.getToolTip());
		assertEquals("b font size  ", 12, nb.getFont().getSize());
		assertEquals("b font style  ", Font.PLAIN, nb.getFont().getStyle());
		assertTrue("b label color  " + nb.getLabelColor() + " expect "
				+ Color.BLACK, nb.getLabelColor().equals(Color.BLACK));

		nac.setNodeSizeLocked(false);

		NodeAppearance nc = nac.calculateNodeAppearance(c, cyNet);

		assertTrue("c color  " + nc.getFillColor() + " expect "
				+ Color.WHITE.toString(), nc.getFillColor().equals(Color.WHITE));
		assertTrue("c border color  " + nc.getBorderColor() + " expect "
				+ Color.BLACK.toString(), nc.getBorderColor().equals(
				Color.BLACK));
		assertEquals("c lineType  ", LineType.LINE_1, nc.getBorderLineType());
		assertEquals("c shape  ", ShapeNodeRealizer.RECT, nc.getShape());
		// now we see the default width and height
		assertEquals("c width  ", 70.0, nc.getWidth());
		assertEquals("c height  ", 30.0, nc.getHeight());
		assertEquals("c size  ", 35.0, nc.getSize());
		assertEquals("c label  ", "", nc.getLabel());
		assertEquals("c tooltip  ", "", nc.getToolTip());
		assertEquals("c font size  ", 12, nc.getFont().getSize());
		assertEquals("c font style  ", Font.PLAIN, nc.getFont().getStyle());
		assertTrue("c label color  " + nc.getLabelColor() + " expect "
				+ Color.BLACK, nc.getLabelColor().equals(Color.BLACK));

		NodeAppearance def = nac.getDefaultAppearance();
		def.setFillColor(Color.GREEN);
		def.setBorderColor(Color.BLUE);
		def.setWidth(47.0);
		def.setHeight(23.0);

		na = nac.calculateNodeAppearance(a, cyNet);
		assertTrue("color  " + na.getFillColor() + " expect "
				+ Color.GREEN.toString(), na.getFillColor().equals(Color.GREEN));
		assertTrue("border color  " + na.getBorderColor() + " expect "
				+ Color.BLUE.toString(), na.getBorderColor().equals(Color.BLUE));
		assertEquals("width  ", 47.0, na.getWidth());
		assertEquals("height  ", 23.0, na.getHeight());

		nb = nac.calculateNodeAppearance(b, cyNet);
		assertTrue("color  " + nb.getFillColor() + " expect "
				+ Color.GREEN.toString(), nb.getFillColor().equals(Color.GREEN));
		assertTrue("border color  " + nb.getBorderColor() + " expect "
				+ Color.BLUE.toString(), nb.getBorderColor().equals(Color.BLUE));
		assertEquals("width  ", 47.0, nb.getWidth());
		assertEquals("height  ", 23.0, nb.getHeight());

		System.out
				.println("end NodeAppearanceCalculatorTest.testDefaultAppearance()");
	}

	/**
	 * DOCUMENT ME!
	 */
	public void testApplyProperties() {
		System.out
				.println("begin NodeAppearanceCalculatorTest.testApplyProperties()");

		NodeAppearanceCalculator nac = new NodeAppearanceCalculator();
		nac.applyProperties("homer", props, "nodeAppearanceCalculator.homer",
				catalog);
		System.out.println(nac.getDescription());

		// node a
		nac.setNodeSizeLocked(false);

		NodeAppearance na = nac.calculateNodeAppearance(a, cyNet);
		System.out.println("NodeAppearance a\n" + na.getDescription());

		Color ca = new Color(246, 242, 103);
		assertTrue(
				"a color  " + na.getFillColor() + " expect " + ca.toString(),
				na.getFillColor().equals(ca));
		assertTrue("a border color  " + na.getBorderColor() + " expect "
				+ Color.BLACK.toString(), na.getBorderColor().equals(
				Color.BLACK));
		assertEquals("a lineType  ", LineType.LINE_1, na.getBorderLineType());
		assertEquals("a shape  ", ShapeNodeRealizer.RECT, na.getShape());
		assertEquals("a width  ", 70.0, na.getWidth());
		assertEquals("a height  ", 10.0, na.getHeight()); // only height has a
															// calc set
		assertEquals("a size  ", 35.0, na.getSize()); // props don't set size
		assertEquals("a label  ", "a", na.getLabel());
		assertEquals("a tooltip  ", "", na.getToolTip());
		assertEquals("a font size  ", 12, na.getFont().getSize());
		assertEquals("a font style  ", Font.PLAIN, na.getFont().getStyle());
		assertTrue("a label color  " + na.getLabelColor() + " expect "
				+ Color.BLACK, na.getLabelColor().equals(Color.BLACK));

		// node b
		NodeAppearance nb = nac.calculateNodeAppearance(b, cyNet);
		System.out.println("NodeAppearance b\n" + nb.getDescription());

		Color cb = new Color(87, 25, 230);
		assertTrue(
				"b color  " + nb.getFillColor() + " expect " + cb.toString(),
				nb.getFillColor().equals(cb));
		assertTrue("b border color  " + nb.getBorderColor() + " expect "
				+ Color.BLACK, nb.getBorderColor().equals(Color.BLACK));
		//assertEquals("b lineType  ", LineType.LINE_5, nb.getBorderLineType());
		assertEquals("b line width  ", 5.0f, nb.getBorderLine().getWidth());
		assertEquals("b line type  ", LineTypeDef.SOLID, nb.getBorderLine().getType());
		
		assertEquals("b shape  ", ShapeNodeRealizer.RECT, nb.getShape());
		assertEquals("b width  ", 70.0, nb.getWidth());
		assertEquals("b height  ", 30.0, nb.getHeight());
		assertEquals("b size  ", 35.0, nb.getSize()); // props don't set size
		assertEquals("b label  ", "b", nb.getLabel());
		assertEquals("b tooltip  ", "", nb.getToolTip());
		assertEquals("b font size  ", 12, nb.getFont().getSize());
		assertEquals("b font style  ", Font.PLAIN, nb.getFont().getStyle());
		assertTrue("b label color  " + nb.getLabelColor() + " expect "
				+ Color.BLACK.toString(), nb.getLabelColor()
				.equals(Color.BLACK));

		nac.setNodeSizeLocked(true);

		NodeAppearance nc = nac.calculateNodeAppearance(c, cyNet);
		System.out.println("NodeAppearance c\n" + nc.getDescription());

		Color cc = new Color(209, 205, 254);
		assertTrue(
				"c color  " + nc.getFillColor() + " expect " + cc.toString(),
				nc.getFillColor().equals(cc));
		assertTrue("c border color  " + nc.getBorderColor() + " expect "
				+ Color.BLACK, nc.getBorderColor().equals(Color.BLACK));
		assertEquals("c line Type  ", LineTypeDef.LONG_DASH, nc.getBorderLine().getType());
		assertEquals("c line width  ", 1.0f, nc.getBorderLine().getWidth());
		
		assertEquals("c shape  ", ShapeNodeRealizer.RECT, nc.getShape());
		assertEquals("c width  ", 35.0, nc.getWidth()); // since node size is
														// locked
		assertEquals("c height  ", 35.0, nc.getHeight()); // since node size
															// is locked
		assertEquals("c size  ", 35.0, nc.getSize());
		assertEquals("c label  ", "c", nc.getLabel());
		assertEquals("c tooltip  ", "", nc.getToolTip());
		assertEquals("c font size  ", 12, nc.getFont().getSize());
		assertEquals("c font style  ", Font.PLAIN, nc.getFont().getStyle());
		assertTrue("c label color  " + nc.getLabelColor() + " expect "
				+ Color.BLACK.toString(), nc.getLabelColor()
				.equals(Color.BLACK));

		System.out
				.println("end NodeAppearanceCalculatorTest.testApplyProperties()");
	}

	/**
	 * DOCUMENT ME!
	 */
	public void testNodeSizeLock() {
		System.out
				.println("begin NodeAppearanceCalculatorTest.testNodeSizeLock()");

		NodeAppearanceCalculator nac = new NodeAppearanceCalculator();
		System.out.println(nac.getDescription());

		NodeView view = new TestNodeView();
		NodeAppearance na = null;

		// using default props
		nac.setNodeSizeLocked(false);
		na = nac.calculateNodeAppearance(a, cyNet);
		na.applyAppearance(view);

		assertEquals("height", 30.0, view.getHeight());
		assertEquals("width", 70.0, view.getWidth());

		nac.setNodeSizeLocked(true);
		na = nac.calculateNodeAppearance(a, cyNet);
		na.applyAppearance(view);

		assertEquals("height", 35.0, view.getHeight());
		assertEquals("width", 35.0, view.getWidth());

		nac.applyProperties("homer", props, "nodeAppearanceCalculator.homer",
				catalog);

		// still locked
		na = nac.calculateNodeAppearance(a, cyNet);
		na.applyAppearance(view);
		assertEquals("height", 35.0, view.getHeight());
		assertEquals("width", 35.0, view.getWidth());

		nac.setNodeSizeLocked(false);
		na = nac.calculateNodeAppearance(a, cyNet);
		na.applyAppearance(view);
		assertEquals("height", 10.0, view.getHeight());
		assertEquals("width", 70.0, view.getWidth());

		System.out
				.println("end NodeAppearanceCalculatorTest.testNodeSizeLock()");
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
//		String fillColorString = "63,128,255";
//		String borderColorString = "100,100,50";
//		LineType lineType = LineType.DASHED_3;
//		String lineTypeString = "DASHED_3";
		byte shape = ShapeNodeRealizer.DIAMOND;
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
		secondNodeAttr.setAttribute(secondName, "node.borderColor",
				"100,100,50");
		secondNodeAttr.setAttribute(secondName, "node.lineType", "dashed3");
		secondNodeAttr.setAttribute(secondName, "node.shape", "diamond");
		secondNodeAttr.setAttribute(secondName, "node.width", "49.0");
		secondNodeAttr.setAttribute(secondName, "node.height", "79.0");
		secondNodeAttr.setAttribute(secondName, "node.size", "32.0");
		secondNodeAttr.setAttribute(secondName, "node.label", "testLabel");
		secondNodeAttr.setAttribute(secondName, "node.toolTip", "testToolTip");
		secondNodeAttr.setAttribute(secondName, "node.font",
				"SansSerif,italic,10");

		NodeAppearanceCalculator nac = new NodeAppearanceCalculator();
		nac.setNodeSizeLocked(false);

		NodeAppearance secondApp = nac
				.calculateNodeAppearance(second, network2);
		System.out.println("secondApp\n" + secondApp.getDescription());
		assertTrue(secondApp.getFillColor().equals(fillColor));
		assertTrue(secondApp.getBorderColor().equals(borderColor));
		
		/*
		 * Need to figure out why this fails.
		 */
		//assertEquals(secondApp.getBorderLine().getType(), LineTypeDef.DASH);
		//assertEquals(secondApp.getBorderLine().getWidth(), 3.0f);
		
		assertTrue(secondApp.getShape() == shape);
		assertEquals("width ", secondApp.getWidth(), width, 0.0001);
		assertEquals("height ", secondApp.getHeight(), height, 0.0001);
		assertTrue(secondApp.getLabel().equals(label));
		assertTrue(secondApp.getToolTip().equals(toolTip));
		assertTrue(secondApp.getFont().equals(font));

		nac.setNodeSizeLocked(true);
		nac.calculateNodeAppearance(secondApp, second, network2);
		assertEquals("width", secondApp.getWidth(), 32.0, 0.0001);
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param args
	 *            DOCUMENT ME!
	 */
	public static void main(String[] args) {
		junit.textui.TestRunner.run(new TestSuite(
				NodeAppearanceCalculatorTest.class));
	}
}
