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
import static cytoscape.visual.VisualPropertyType.*;

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
		assertTrue("a color  " + na.get(NODE_FILL_COLOR) + " expect "
				+ Color.WHITE.toString(), ((Color)na.get(NODE_FILL_COLOR)).equals(Color.WHITE));
		assertTrue("a border color  " + na.get(NODE_BORDER_COLOR) + " expect "
				+ Color.BLACK.toString(), ((Color)na.get(NODE_BORDER_COLOR)).equals(
				Color.BLACK));
		assertEquals("a lineType  ", LineStyle.SOLID, ((LineStyle)na.get(NODE_LINETYPE)));
		assertEquals("a shape  ", NodeShape.RECT, ((NodeShape)na.get(NODE_SHAPE)));

		// node size is locked so all should be the same
		assertEquals("a width  ", 35.0, getWidth(na,nac));
		assertEquals("a height  ", 35.0, getHeight(na,nac));
		assertEquals("a size  ", 35.0, ((Double)na.get(NODE_SIZE)).doubleValue());

		assertEquals("a label  ", "", ((String)na.get(NODE_LABEL)));
		assertEquals("a tooltip  ", "", ((String)na.get(NODE_TOOLTIP)));
		assertEquals("a font size  ", 12, ((Number)na.get(NODE_FONT_SIZE)).intValue());
		assertEquals("a font style  ", Font.PLAIN, ((Font)na.get(NODE_FONT_FACE)).getStyle());
		assertTrue("a label color  " + na.get(NODE_LABEL_COLOR) + " expect "
				+ Color.BLACK, ((Color)na.get(NODE_LABEL_COLOR)).equals(Color.BLACK));

		// should still be default for node b
		NodeAppearance nb = nac.calculateNodeAppearance(b, cyNet);

		assertTrue("b color  " + nb.get(NODE_FILL_COLOR) + " expect "
				+ Color.WHITE.toString(), ((Color)nb.get(NODE_FILL_COLOR)).equals(Color.WHITE));
		assertTrue("b border color  " + nb.get(NODE_BORDER_COLOR) + " expect "
				+ Color.BLACK.toString(), ((Color)nb.get(NODE_BORDER_COLOR)).equals(
				Color.BLACK));
		assertEquals("b lineType  ", LineStyle.SOLID, ((LineStyle)nb.get(NODE_LINETYPE)));
		assertEquals("b shape  ", NodeShape.RECT, ((NodeShape)nb.get(NODE_SHAPE)));
		// still locked
		assertEquals("b width  ", 35.0, getWidth(nb,nac));
		assertEquals("b height  ", 35.0, getHeight(nb,nac));
		assertEquals("b size  ", 35.0, ((Double)nb.get(NODE_SIZE)).doubleValue());

		assertEquals("b label  ", "", ((String)nb.get(NODE_LABEL)));
		assertEquals("b tooltip  ", "", ((String)nb.get(NODE_TOOLTIP)));
		assertEquals("b font size  ", 12, ((Number)nb.get(NODE_FONT_SIZE)).intValue());
		assertEquals("b font style  ", Font.PLAIN, ((Font)nb.get(NODE_FONT_FACE)).getStyle());
		assertTrue("b label color  " + nb.get(NODE_LABEL_COLOR) + " expect "
				+ Color.BLACK, ((Color)nb.get(NODE_LABEL_COLOR)).equals(Color.BLACK));

		nac.setNodeSizeLocked(false);

		NodeAppearance nc = nac.calculateNodeAppearance(c, cyNet);

		assertTrue("c color  " + nc.get(NODE_FILL_COLOR) + " expect "
				+ Color.WHITE.toString(), ((Color)nc.get(NODE_FILL_COLOR)).equals(Color.WHITE));
		assertTrue("c border color  " + nc.get(NODE_BORDER_COLOR) + " expect "
				+ Color.BLACK.toString(), ((Color)nc.get(NODE_BORDER_COLOR)).equals(
				Color.BLACK));
		assertEquals("c lineType  ", LineStyle.SOLID, ((LineStyle)nc.get(NODE_LINETYPE)));
		assertEquals("c shape  ", NodeShape.RECT, ((NodeShape)nc.get(NODE_SHAPE)));
		// now we see the default width and height
		assertEquals("c width  ", 70.0, getWidth(nc,nac));
		assertEquals("c height  ", 30.0, getHeight(nc,nac));
		assertEquals("c size  ", 35.0, ((Double)nc.get(NODE_SIZE)).doubleValue());
		assertEquals("c label  ", "", ((String)nc.get(NODE_LABEL)));
		assertEquals("c tooltip  ", "", ((String)nc.get(NODE_TOOLTIP)));
		assertEquals("c font size  ", 12, ((Number)nc.get(NODE_FONT_SIZE)).intValue());
		assertEquals("c font style  ", Font.PLAIN, ((Font)nc.get(NODE_FONT_FACE)).getStyle());
		assertTrue("c label color  " + nc.get(NODE_LABEL_COLOR) + " expect "
				+ Color.BLACK, ((Color)nc.get(NODE_LABEL_COLOR)).equals(Color.BLACK));

		NodeAppearance def = nac.getDefaultAppearance();
		def.set(NODE_FILL_COLOR,Color.GREEN);
		def.set(NODE_BORDER_COLOR,Color.BLUE);
		def.set(NODE_WIDTH,47.0);
		def.set(NODE_HEIGHT,23.0);

		na = nac.calculateNodeAppearance(a, cyNet);
		assertTrue("color  " + na.get(NODE_FILL_COLOR) + " expect "
				+ Color.GREEN.toString(), ((Color)na.get(NODE_FILL_COLOR)).equals(Color.GREEN));
		assertTrue("border color  " + na.get(NODE_BORDER_COLOR) + " expect "
				+ Color.BLUE.toString(), ((Color)na.get(NODE_BORDER_COLOR)).equals(Color.BLUE));
		assertEquals("width  ", 47.0, getWidth(na,nac));
		assertEquals("height  ", 23.0, getHeight(na,nac));

		nb = nac.calculateNodeAppearance(b, cyNet);
		assertTrue("color  " + nb.get(NODE_FILL_COLOR) + " expect "
				+ Color.GREEN.toString(), ((Color)nb.get(NODE_FILL_COLOR)).equals(Color.GREEN));
		assertTrue("border color  " + nb.get(NODE_BORDER_COLOR) + " expect "
				+ Color.BLUE.toString(), ((Color)nb.get(NODE_BORDER_COLOR)).equals(Color.BLUE));
		assertEquals("width  ", 47.0, getWidth(nb,nac));
		assertEquals("height  ", 23.0, getHeight(nb,nac));

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
		assertTrue( "a color  " + na.get(NODE_FILL_COLOR) + " expect " + ca.toString(),
				((Color)na.get(NODE_FILL_COLOR)).equals(ca));
		assertTrue("a border color  " + na.get(NODE_BORDER_COLOR) + " expect "
				+ Color.BLACK.toString(), ((Color)na.get(NODE_BORDER_COLOR)).equals(
				Color.BLACK));
		assertEquals("a lineType  ", LineStyle.SOLID, ((LineStyle)na.get(NODE_LINETYPE)));
		assertEquals("a shape  ", NodeShape.RECT, ((NodeShape)na.get(NODE_SHAPE)));
		assertEquals("a width  ", 70.0, getWidth(na,nac));
		assertEquals("a height  ", 10.0, getHeight(na,nac)); // only height has a
															// calc set
		assertEquals("a size  ", 35.0, ((Double)na.get(NODE_SIZE)).doubleValue()); // props don't set size
		assertEquals("a label  ", "a", ((String)na.get(NODE_LABEL)));
		assertEquals("a tooltip  ", "", ((String)na.get(NODE_TOOLTIP)));
		assertEquals("a font size  ", 12, ((Number)na.get(NODE_FONT_SIZE)).intValue());
		assertEquals("a font style  ", Font.PLAIN, ((Font)na.get(NODE_FONT_FACE)).getStyle());
		assertTrue("a label color  " + na.get(NODE_LABEL_COLOR) + " expect "
				+ Color.BLACK, ((Color)na.get(NODE_LABEL_COLOR)).equals(Color.BLACK));

		// node b
		NodeAppearance nb = nac.calculateNodeAppearance(b, cyNet);
		System.out.println("NodeAppearance b\n" + nb.getDescription());

		Color cb = new Color(87, 25, 230);
		assertTrue(
				"b color  " + nb.get(NODE_FILL_COLOR) + " expect " + cb.toString(),
				((Color)nb.get(NODE_FILL_COLOR)).equals(cb));
		assertTrue("b border color  " + nb.get(NODE_BORDER_COLOR) + " expect "
				+ Color.BLACK, ((Color)nb.get(NODE_BORDER_COLOR)).equals(Color.BLACK));
		assertEquals("b line width  ", 1.0f, nb.get(VisualPropertyType.NODE_LINE_WIDTH));
		
		assertEquals("b line style  ", LineStyle.SOLID, nb.get(VisualPropertyType.NODE_LINE_STYLE));
		
		assertEquals("b shape  ", NodeShape.RECT, ((NodeShape)nb.get(NODE_SHAPE)));
		assertEquals("b width  ", 70.0, getWidth(nb,nac));
		assertEquals("b height  ", 30.0, getHeight(nb,nac));
		assertEquals("b size  ", 35.0, ((Double)nb.get(NODE_SIZE)).doubleValue()); // props don't set size
		assertEquals("b label  ", "b", ((String)nb.get(NODE_LABEL)));
		assertEquals("b tooltip  ", "", ((String)nb.get(NODE_TOOLTIP)));
		assertEquals("b font size  ", 12, ((Number)nb.get(NODE_FONT_SIZE)).intValue());
		assertEquals("b font style  ", Font.PLAIN, ((Font)nb.get(NODE_FONT_FACE)).getStyle());
		assertTrue("b label color  " + nb.get(NODE_LABEL_COLOR) + " expect "
				+ Color.BLACK.toString(), ((Color)nb.get(NODE_LABEL_COLOR))
				.equals(Color.BLACK));

		nac.setNodeSizeLocked(true);

		NodeAppearance nc = nac.calculateNodeAppearance(c, cyNet);
		System.out.println("NodeAppearance c\n" + nc.getDescription());

		Color cc = new Color(209, 205, 254);
		assertTrue(
				"c color  " + nc.get(NODE_FILL_COLOR) + " expect " + cc.toString(),
				((Color)nc.get(NODE_FILL_COLOR)).equals(cc));
		assertTrue("c border color  " + nc.get(NODE_BORDER_COLOR) + " expect "
				+ Color.BLACK, ((Color)nc.get(NODE_BORDER_COLOR)).equals(Color.BLACK));
		assertEquals("c line Type  ", LineStyle.SOLID, nb.get(VisualPropertyType.NODE_LINE_STYLE));

		assertEquals("c line width  ", 1.0f, ((Number)nc.get(NODE_LINE_WIDTH)).floatValue());
		
		assertEquals("c shape  ", NodeShape.RECT, ((NodeShape)nc.get(NODE_SHAPE)));
		assertEquals("c width  ", 35.0, getWidth(nc,nac)); // since node size is
														// locked
		assertEquals("c height  ", 35.0, getHeight(nc,nac)); // since node size
															// is locked
		assertEquals("c size  ", 35.0, ((Double)nc.get(NODE_SIZE)).doubleValue());
		assertEquals("c label  ", "c", ((String)nc.get(NODE_LABEL)));
		assertEquals("c tooltip  ", "", ((String)nc.get(NODE_TOOLTIP)));
		assertEquals("c font size  ", 12, ((Number)nc.get(NODE_FONT_SIZE)).intValue());
		assertEquals("c font style  ", Font.PLAIN, ((Font)nc.get(NODE_FONT_FACE)).getStyle());
		assertTrue("c label color  " + nc.get(NODE_LABEL_COLOR) + " expect "
				+ Color.BLACK.toString(), ((Color)nc.get(NODE_LABEL_COLOR))
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
		na.applyAppearance(view,nac.getDependency());

		assertEquals("height", 30.0, view.getHeight());
		assertEquals("width", 70.0, view.getWidth());

		nac.setNodeSizeLocked(true);
		na = nac.calculateNodeAppearance(a, cyNet);
		na.applyAppearance(view,nac.getDependency());

		assertEquals("height", 35.0, view.getHeight());
		assertEquals("width", 35.0, view.getWidth());

		nac.applyProperties("homer", props, "nodeAppearanceCalculator.homer",
				catalog);

		// still locked
		na = nac.calculateNodeAppearance(a, cyNet);
		na.applyAppearance(view,nac.getDependency());
		assertEquals("height", 35.0, view.getHeight());
		assertEquals("width", 35.0, view.getWidth());

		nac.setNodeSizeLocked(false);
		na = nac.calculateNodeAppearance(a, cyNet);
		na.applyAppearance(view,nac.getDependency());
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
//		LineStyle lineType = LineStyle.DASHED_3;
//		String lineTypeString = "DASHED_3";
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
		assertTrue(((Color)secondApp.get(NODE_FILL_COLOR)).equals(fillColor));
		assertTrue(((Color)secondApp.get(NODE_BORDER_COLOR)).equals(borderColor));
		
		assertTrue(((NodeShape)secondApp.get(NODE_SHAPE)) == shape);
		assertEquals("width ", getWidth(secondApp,nac), width, 0.0001);
		assertEquals("height ", getHeight(secondApp,nac), height, 0.0001);
		assertTrue(((String)secondApp.get(NODE_LABEL)).equals(label));
		assertTrue(((String)secondApp.get(NODE_TOOLTIP)).equals(toolTip));
		assertTrue(((Font)secondApp.get(NODE_FONT_FACE)).equals(font));

		nac.setNodeSizeLocked(true);
		nac.calculateNodeAppearance(secondApp, second, network2);
		assertEquals("width", getWidth(secondApp,nac), 32.0, 0.0001);
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

	private double getWidth(NodeAppearance na, NodeAppearanceCalculator nac) {
		if (nac.getNodeSizeLocked())
			return ((Double)(na.get(NODE_SIZE))).doubleValue();
		else
			return ((Double)(na.get(NODE_WIDTH))).doubleValue();
	}
	private double getHeight(NodeAppearance na, NodeAppearanceCalculator nac) {
		if (nac.getNodeSizeLocked())
			return ((Double)(na.get(NODE_SIZE))).doubleValue();
		else
			return ((Double)(na.get(NODE_HEIGHT))).doubleValue();
	}
}
