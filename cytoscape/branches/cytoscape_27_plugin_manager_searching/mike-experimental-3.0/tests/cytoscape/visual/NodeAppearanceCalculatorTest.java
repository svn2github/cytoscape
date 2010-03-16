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
// $Revision: 11154 $
// $Date: 2007-07-25 12:41:45 -0700 (Wed, 25 Jul 2007) $
// $Author: kono $
//----------------------------------------------------------------------------
package cytoscape.visual;

import org.cytoscape.Node;
import org.cytoscape.RootGraph;
import giny.view.NodeView;

import java.awt.Color;
import java.awt.Font;
import java.io.FileReader;
import java.util.Properties;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.cytoscape.Edge;
import org.cytoscape.GraphPerspective;
import org.cytoscape.Node;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.data.Semantics;
import cytoscape.data.readers.CyAttributesReader;
import cytoscape.util.FileUtil;

/**
 * 
 */
public class NodeAppearanceCalculatorTest extends TestCase {
	GraphPerspective cyNet;
	Node a;
	Node b;
	Node c;
	Edge ab;
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
		assertTrue("a color  " + Get.color(na.get(VisualPropertyType.NODE_FILL_COLOR)) + " expect "
				+ Color.WHITE.toString(), Get.color(na.get(VisualPropertyType.NODE_FILL_COLOR)).equals(Color.WHITE));
		assertTrue("a border color  " + Get.color(na.get(VisualPropertyType.NODE_BORDER_COLOR)) + " expect "
				+ Color.BLACK.toString(), Get.color(na.get(VisualPropertyType.NODE_BORDER_COLOR)).equals(
				Color.BLACK));
		//assertEquals("a lineType  ", LineType.LINE_1, na.get(VisualPropertyType.NODE_LINE_TYPE));
		assertEquals("a shape  ", NodeShape.RECT, na.get(VisualPropertyType.NODE_SHAPE));

		// node size is locked so all should be the same
		assertEquals("a width  ", 35.0, Get.ddouble(na.get(VisualPropertyType.NODE_WIDTH)));
		assertEquals("a height  ", 35.0, Get.ddouble(na.get(VisualPropertyType.NODE_HEIGHT)));
		assertEquals("a size  ", 35.0, Get.ddouble(na.get(VisualPropertyType.NODE_SIZE)));

		assertEquals("a label  ", "", Get.string(na.get(VisualPropertyType.NODE_LABEL)));
		assertEquals("a tooltip  ", "", Get.string(na.get(VisualPropertyType.NODE_TOOLTIP)));
		assertEquals("a font size  ", 12, Get.font(na.get(VisualPropertyType.NODE_FONT_FACE)).getSize());
		assertEquals("a font style  ", Font.PLAIN, Get.font(na.get(VisualPropertyType.NODE_FONT_FACE)).getStyle());
		assertTrue("a label color  " + Get.color(na.get(VisualPropertyType.NODE_LABEL_COLOR)) + " expect "
				+ Color.BLACK, Get.color(na.get(VisualPropertyType.NODE_LABEL_COLOR)).equals(Color.BLACK));

		// should still be default for node b
		NodeAppearance nb = nac.calculateNodeAppearance(b, cyNet);

		assertTrue("b color  " + Get.color(nb.get(VisualPropertyType.NODE_FILL_COLOR)) + " expect "
				+ Color.WHITE.toString(), Get.color(nb.get(VisualPropertyType.NODE_FILL_COLOR)).equals(Color.WHITE));
		assertTrue("b border color  " + Get.color(nb.get(VisualPropertyType.NODE_BORDER_COLOR)) + " expect "
				+ Color.BLACK.toString(), Get.color(nb.get(VisualPropertyType.NODE_BORDER_COLOR)).equals(
				Color.BLACK));
//		assertEquals("b lineType  ", LineType.LINE_1, nb.get(VisualPropertyType.NODE_LINE_TYPE));
		assertEquals("b shape  ", NodeShape.RECT, nb.get(VisualPropertyType.NODE_SHAPE));
		// still locked
		assertEquals("b width  ", 35.0, Get.ddouble(nb.get(VisualPropertyType.NODE_WIDTH)));
		assertEquals("b height  ", 35.0, Get.ddouble(nb.get(VisualPropertyType.NODE_HEIGHT)));
		assertEquals("b size  ", 35.0, Get.ddouble(nb.get(VisualPropertyType.NODE_SIZE)));

		assertEquals("b label  ", "", Get.string(nb.get(VisualPropertyType.NODE_LABEL)));
		assertEquals("b tooltip  ", "", Get.string(nb.get(VisualPropertyType.NODE_TOOLTIP)));
		assertEquals("b font size  ", 12, Get.font(nb.get(VisualPropertyType.NODE_FONT_FACE)).getSize());
		assertEquals("b font style  ", Font.PLAIN, Get.font(nb.get(VisualPropertyType.NODE_FONT_FACE)).getStyle());
		assertTrue("b label color  " + Get.color(nb.get(VisualPropertyType.NODE_LABEL_COLOR)) + " expect "
				+ Color.BLACK, Get.color(nb.get(VisualPropertyType.NODE_LABEL_COLOR)).equals(Color.BLACK));

		nac.setNodeSizeLocked(false);

		NodeAppearance nc = nac.calculateNodeAppearance(c, cyNet);

		assertTrue("c color  " + Get.color(nc.get(VisualPropertyType.NODE_FILL_COLOR)) + " expect "
				+ Color.WHITE.toString(), Get.color(nc.get(VisualPropertyType.NODE_FILL_COLOR)).equals(Color.WHITE));
		assertTrue("c border color  " + Get.color(nc.get(VisualPropertyType.NODE_BORDER_COLOR)) + " expect "
				+ Color.BLACK.toString(), Get.color(nc.get(VisualPropertyType.NODE_BORDER_COLOR)).equals(
				Color.BLACK));
//		assertEquals("c lineType  ", LineType.LINE_1, nc.get(VisualPropertyType.NODE_LINE_TYPE));
		assertEquals("c shape  ", NodeShape.RECT, nc.get(VisualPropertyType.NODE_SHAPE));
		// now we see the default width and height
		assertEquals("c width  ", 70.0, Get.ddouble(nc.get(VisualPropertyType.NODE_WIDTH)));
		assertEquals("c height  ", 30.0, Get.ddouble(nc.get(VisualPropertyType.NODE_HEIGHT)));
		assertEquals("c size  ", 35.0, Get.ddouble(nc.get(VisualPropertyType.NODE_SIZE)));
		assertEquals("c label  ", "", Get.string(nc.get(VisualPropertyType.NODE_LABEL)));
		assertEquals("c tooltip  ", "", Get.string(nc.get(VisualPropertyType.NODE_TOOLTIP)));
		assertEquals("c font size  ", 12, Get.font(nc.get(VisualPropertyType.NODE_FONT_FACE)).getSize());
		assertEquals("c font style  ", Font.PLAIN, Get.font(nc.get(VisualPropertyType.NODE_FONT_FACE)).getStyle());
		assertTrue("c label color  " + Get.color(nc.get(VisualPropertyType.NODE_LABEL_COLOR)) + " expect "
				+ Color.BLACK, Get.color(nc.get(VisualPropertyType.NODE_LABEL_COLOR)).equals(Color.BLACK));

		NodeAppearance def = nac.getDefaultAppearance();
		def.set(VisualPropertyType.NODE_FILL_COLOR,Color.GREEN);
		def.set(VisualPropertyType.NODE_BORDER_COLOR,Color.BLUE);
		def.set(VisualPropertyType.NODE_HEIGHT,23.0);
		def.set(VisualPropertyType.NODE_WIDTH,47.0);

		na = nac.calculateNodeAppearance(a, cyNet);
		assertTrue("color  " + Get.color(na.get(VisualPropertyType.NODE_FILL_COLOR)) + " expect "
				+ Color.GREEN.toString(), Get.color(na.get(VisualPropertyType.NODE_FILL_COLOR)).equals(Color.GREEN));
		assertTrue("border color  " + Get.color(na.get(VisualPropertyType.NODE_BORDER_COLOR)) + " expect "
				+ Color.BLUE.toString(), Get.color(na.get(VisualPropertyType.NODE_BORDER_COLOR)).equals(Color.BLUE));
		assertEquals("width  ", 47.0, Get.ddouble(na.get(VisualPropertyType.NODE_WIDTH)));
		assertEquals("height  ", 23.0, na.get(VisualPropertyType.NODE_HEIGHT));

		nb = nac.calculateNodeAppearance(b, cyNet);
		assertTrue("color  " + Get.color(nb.get(VisualPropertyType.NODE_FILL_COLOR)) + " expect "
				+ Color.GREEN.toString(), Get.color(nb.get(VisualPropertyType.NODE_FILL_COLOR)).equals(Color.GREEN));
		assertTrue("border color  " + Get.color(nb.get(VisualPropertyType.NODE_BORDER_COLOR)) + " expect "
				+ Color.BLUE.toString(), Get.color(nb.get(VisualPropertyType.NODE_BORDER_COLOR)).equals(Color.BLUE));
		assertEquals("width  ", 47.0, Get.ddouble(nb.get(VisualPropertyType.NODE_WIDTH)));
		assertEquals("height  ", 23.0, nb.get(VisualPropertyType.NODE_HEIGHT));

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
		nac.applyProperties("homer", props, "nodeAppearanceCalculator.homer", catalog);
		System.out.println(nac.getDescription());

		// node a
		nac.setNodeSizeLocked(false);

		NodeAppearance na = nac.calculateNodeAppearance(a, cyNet);
		System.out.println("NodeAppearance a\n" + na.getDescription());

		Color ca = new Color(246, 242, 103);
		assertTrue( "a color  " + Get.color(na.get(VisualPropertyType.NODE_FILL_COLOR)) + " expect " + ca.toString(),
				Get.color(na.get(VisualPropertyType.NODE_FILL_COLOR)).equals(ca));
		assertTrue("a border color  " + Get.color(na.get(VisualPropertyType.NODE_BORDER_COLOR)) + " expect "
				+ Color.BLACK.toString(), Get.color(na.get(VisualPropertyType.NODE_BORDER_COLOR)).equals(
				Color.BLACK));
//		assertEquals("a lineType  ", LineType.LINE_1, na.get(VisualPropertyType.NODE_LINE_TYPE));
		assertEquals("a shape  ", NodeShape.RECT, na.get(VisualPropertyType.NODE_SHAPE));
		assertEquals("a width  ", 70.0, Get.ddouble(na.get(VisualPropertyType.NODE_WIDTH)));
		assertEquals("a height  ", 10.0, Get.ddouble(na.get(VisualPropertyType.NODE_HEIGHT))); // only height has a
															// calc set
		assertEquals("a size  ", 35.0, Get.ddouble(na.get(VisualPropertyType.NODE_SIZE))); // props don't set size
		assertEquals("a label  ", "a", Get.string(na.get(VisualPropertyType.NODE_LABEL)));
		assertEquals("a tooltip  ", "", Get.string(na.get(VisualPropertyType.NODE_TOOLTIP)));
		assertEquals("a font size  ", 12, Get.font(na.get(VisualPropertyType.NODE_FONT_FACE)).getSize());
		assertEquals("a font style  ", Font.PLAIN, Get.font(na.get(VisualPropertyType.NODE_FONT_FACE)).getStyle());
		assertTrue("a label color  " + Get.color(na.get(VisualPropertyType.NODE_LABEL_COLOR)) + " expect "
				+ Color.BLACK, Get.color(na.get(VisualPropertyType.NODE_LABEL_COLOR)).equals(Color.BLACK));

		// node b
		NodeAppearance nb = nac.calculateNodeAppearance(b, cyNet);
		System.out.println("NodeAppearance b\n" + nb.getDescription());

		Color cb = new Color(87, 25, 230);
		assertTrue(
				"b color  " + Get.color(nb.get(VisualPropertyType.NODE_FILL_COLOR)) + " expect " + cb.toString(),
				Get.color(nb.get(VisualPropertyType.NODE_FILL_COLOR)).equals(cb));
		assertTrue("b border color  " + Get.color(nb.get(VisualPropertyType.NODE_BORDER_COLOR)) + " expect "
				+ Color.BLACK, Get.color(nb.get(VisualPropertyType.NODE_BORDER_COLOR)).equals(Color.BLACK));
		//assertEquals("b lineType  ", LineType.LINE_5, nb.get(VisualPropertyType.NODE_LINE_TYPE));
		//assertEquals("b line width  ", 5.0f, nb.get(VisualPropertyType.NODE_LINE_TYPE).getWidth());
		assertEquals("b line width  ", 1.0f, Get.ffloat(nb.get(VisualPropertyType.NODE_LINE_WIDTH)));
		
		assertEquals("b line style  ", LineStyle.SOLID, nb.get(VisualPropertyType.NODE_LINE_STYLE));
		
		assertEquals("b shape  ", NodeShape.RECT, nb.get(VisualPropertyType.NODE_SHAPE));
		assertEquals("b width  ", 70.0, Get.ddouble(nb.get(VisualPropertyType.NODE_WIDTH)));
		assertEquals("b height  ", 30.0, Get.ddouble(nb.get(VisualPropertyType.NODE_HEIGHT)));
		assertEquals("b size  ", 35.0, Get.ddouble(nb.get(VisualPropertyType.NODE_SIZE))); // props don't set size
		assertEquals("b label  ", "b", Get.string(nb.get(VisualPropertyType.NODE_LABEL)));
		assertEquals("b tooltip  ", "", Get.string(nb.get(VisualPropertyType.NODE_TOOLTIP)));
		assertEquals("b font size  ", 12, Get.font(nb.get(VisualPropertyType.NODE_FONT_FACE)).getSize());
		assertEquals("b font style  ", Font.PLAIN, Get.font(nb.get(VisualPropertyType.NODE_FONT_FACE)).getStyle());
		assertTrue("b label color  " + Get.color(nb.get(VisualPropertyType.NODE_LABEL_COLOR)) + " expect "
				+ Color.BLACK.toString(), Get.color(nb.get(VisualPropertyType.NODE_LABEL_COLOR))
				.equals(Color.BLACK));

		nac.setNodeSizeLocked(true);

		NodeAppearance nc = nac.calculateNodeAppearance(c, cyNet);
		System.out.println("NodeAppearance c\n" + nc.getDescription());

		Color cc = new Color(209, 205, 254);
		assertTrue(
				"c color  " + Get.color(nc.get(VisualPropertyType.NODE_FILL_COLOR)) + " expect " + cc.toString(),
				Get.color(nc.get(VisualPropertyType.NODE_FILL_COLOR)).equals(cc));
		assertTrue("c border color  " + Get.color(nc.get(VisualPropertyType.NODE_BORDER_COLOR)) + " expect "
				+ Color.BLACK, Get.color(nc.get(VisualPropertyType.NODE_BORDER_COLOR)).equals(Color.BLACK));
		assertEquals("c line Type  ", LineStyle.SOLID, nb.get(VisualPropertyType.NODE_LINE_STYLE));
//		assertEquals("c line width  ", 1.0f, nc.get(VisualPropertyType.NODE_LINE_TYPE).getWidth());
		
		assertEquals("c shape  ", NodeShape.RECT, Get.nodeShape(nc.get(VisualPropertyType.NODE_SHAPE)));
		assertEquals("c size  ", 35.0, Get.ddouble(nc.get(VisualPropertyType.NODE_SIZE)));
		// since node size is locked
		assertEquals("c width  ", 35.0, Get.ddouble(nc.get(VisualPropertyType.NODE_WIDTH))); 
		// since node size is locked
		assertEquals("c height  ", 35.0, Get.ddouble(nc.get(VisualPropertyType.NODE_HEIGHT))); 
		assertEquals("c label  ", "c", Get.string(nc.get(VisualPropertyType.NODE_LABEL)));
		assertEquals("c tooltip  ", "", Get.string(nc.get(VisualPropertyType.NODE_TOOLTIP)));
		assertEquals("c font size  ", 12, Get.font(nc.get(VisualPropertyType.NODE_FONT_FACE)).getSize());
		assertEquals("c font style  ", Font.PLAIN, Get.font(nc.get(VisualPropertyType.NODE_FONT_FACE)).getStyle());
		assertTrue("c label color  " + Get.color(nc.get(VisualPropertyType.NODE_LABEL_COLOR)) + " expect "
				+ Color.BLACK.toString(), Get.color(nc.get(VisualPropertyType.NODE_LABEL_COLOR))
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

		nac.applyProperties("homer", props, "nodeAppearanceCalculator.homer", catalog);

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

		GraphPerspective network2 = Cytoscape.createNetwork(Cytoscape.getRootGraph()
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
		assertTrue(Get.color(secondApp.get(VisualPropertyType.NODE_FILL_COLOR)).equals(fillColor));
		assertTrue(Get.color(secondApp.get(VisualPropertyType.NODE_BORDER_COLOR)).equals(borderColor));
		
		/*
		 * Need to figure out why this fails.
		 */
		//assertEquals(secondApp.get(VisualPropertyType.NODE_LINE_TYPE).getType(), LineStyle.DASH);
		//assertEquals(secondApp.get(VisualPropertyType.NODE_LINE_TYPE).getWidth(), 3.0f);
		
		assertTrue(secondApp.get(VisualPropertyType.NODE_SHAPE) == shape);
		assertEquals("width ", Get.ddouble(secondApp.get(VisualPropertyType.NODE_WIDTH)), width, 0.0001);
		assertEquals("height ", Get.ddouble(secondApp.get(VisualPropertyType.NODE_HEIGHT)), height, 0.0001);
		assertTrue(Get.string(secondApp.get(VisualPropertyType.NODE_LABEL)).equals(label));
		assertTrue(Get.string(secondApp.get(VisualPropertyType.NODE_TOOLTIP)).equals(toolTip));
		assertTrue(Get.font(secondApp.get(VisualPropertyType.NODE_FONT_FACE)).equals(font));

		nac.setNodeSizeLocked(true);
		nac.calculateNodeAppearance(secondApp, second, network2);
		assertEquals("width", Get.ddouble(secondApp.get(VisualPropertyType.NODE_WIDTH)), 32.0, 0.0001);
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
