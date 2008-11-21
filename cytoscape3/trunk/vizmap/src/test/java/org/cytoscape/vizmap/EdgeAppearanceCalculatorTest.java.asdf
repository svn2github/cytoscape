/*
 File: EdgeAppearanceCalculatorTest.java

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

// EdgeAppearanceCalculatorTest.java
//----------------------------------------------------------------------------
// $Revision: 7760 $
// $Date: 2006-06-26 09:28:49 -0700 (Mon, 26 Jun 2006) $
// $Author: mes $
//----------------------------------------------------------------------------
package org.cytoscape.vizmap;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.cytoscape.model.network.CyEdge;
import org.cytoscape.model.network.CyNetwork;
import org.cytoscape.model.network.CyNode;
import org.cytoscape.model.network.RootGraph;
import org.cytoscape.model.network.RootGraphFactory;
import org.cytoscape.attributes.CyAttributes;
import org.cytoscape.attributes.CyAttributesFactory;

import java.awt.*;
import java.io.FileInputStream;
import java.util.Properties;

/**
 * 
 */
public class EdgeAppearanceCalculatorTest extends TestCase {
	CyNetwork cyNet;
	CyNode a;
	CyNode b;
	CyNode c;
	CyNode d;
	CyEdge ab;
	CyEdge bc;
	CyEdge cd;
	CyEdge bd;
	CalculatorCatalog catalog;
	Properties props;
	CyAttributes edgeAttrs;

	/**
	 * Creates a new EdgeAppearanceCalculatorTest object.
	 * 
	 * @param name
	 *            DOCUMENT ME!
	 */
	public EdgeAppearanceCalculatorTest(String name) {
		super(name);
	}

	/**
	 * DOCUMENT ME!
	 */
	public void setUp() {

		RootGraph rg = RootGraphFactory.getRootGraph();

		a = rg.getNode(rg.createNode());
		a.setIdentifier("a");
		b = rg.getNode(rg.createNode());
		b.setIdentifier("b");
		c = rg.getNode(rg.createNode());
		c.setIdentifier("c");
		d = rg.getNode(rg.createNode());
		d.setIdentifier("d");
		CyNode[] nodes = new CyNode[] {a,b,c,d};

		ab = rg.getEdge(rg.createEdge(a, b));
		ab.setIdentifier("a (pp) b");
		bc = rg.getEdge(rg.createEdge(b, c));
		bc.setIdentifier("b (pp) c");
		cd = rg.getEdge(rg.createEdge(c, d));
		cd.setIdentifier("c (pp) d");
		bd = rg.getEdge(rg.createEdge(b, d));
		bd.setIdentifier("b (pp) d");
		CyEdge[] edges = new CyEdge[] {ab,bc,cd,bd};

		cyNet = rg.createGraphPerspective(nodes,edges);

		edgeAttrs = CyAttributesFactory.getCyAttributes("edge");
		edgeAttrs.setAttribute(ab.getIdentifier(),"sample",0.4);
		edgeAttrs.setAttribute(bc.getIdentifier(),"sample",1.5);
		edgeAttrs.setAttribute(cd.getIdentifier(),"sample",1.6);
		edgeAttrs.setAttribute(bd.getIdentifier(),"sample",2.7);

		props = new Properties();

		try {
			props.load(new FileInputStream("src/test/resources/testData/small.vizmap.props"));
		} catch (Exception e) {
			e.printStackTrace();
		}

		catalog = new CalculatorCatalog();
		CalculatorIO.loadCalculators(props, catalog, true);
	}

	/**
	 * DOCUMENT ME!
	 */
	public void testDefaultAppearance() {
		EdgeAppearanceCalculator eac = new EdgeAppearanceCalculator();

		EdgeAppearance ea = eac.calculateEdgeAppearance(ab, cyNet);

		// this tests that the default edge appearance is correct
		assertTrue("color", Get.color(ea.get(VisualPropertyType.EDGE_COLOR)).equals(Color.BLACK));
		
		
		assertTrue("src arrow", Get.arrowShape(ea.get(VisualPropertyType.EDGE_SRCARROW_SHAPE)) == ArrowShape.NONE);
		assertTrue("trg arrow", Get.arrowShape(ea.get(VisualPropertyType.EDGE_TGTARROW_SHAPE)) == ArrowShape.NONE);
		assertTrue("label", Get.string(ea.get(VisualPropertyType.EDGE_LABEL)).equals(""));
		assertTrue("tooltip", Get.string(ea.get(VisualPropertyType.EDGE_TOOLTIP)).equals(""));
		assertTrue("font size", Get.font(ea.get(VisualPropertyType.EDGE_FONT_FACE)).getSize() == 10);
		assertTrue("font style", Get.font(ea.get(VisualPropertyType.EDGE_FONT_FACE)).getStyle() == Font.PLAIN);
	}

	/**
	 * DOCUMENT ME!
	 */
	public void testApplyProperties() {
		EdgeAppearanceCalculator eac = new EdgeAppearanceCalculator();
		eac.applyProperties("homer", props, "edgeAppearanceCalculator.homer", catalog);

		EdgeAppearance ea = eac.calculateEdgeAppearance(ab, cyNet);
		System.out.println(eac.getDescription());
		System.out.println("color " + Get.color(ea.get(VisualPropertyType.EDGE_COLOR)));
		System.out.println("src arrow " + Get.arrowShape(ea.get(VisualPropertyType.EDGE_SRCARROW_SHAPE)));
		System.out.println("trg arrow " + Get.arrowShape(ea.get(VisualPropertyType.EDGE_TGTARROW_SHAPE)));
		System.out.println("label " + Get.string(ea.get(VisualPropertyType.EDGE_LABEL)));
		System.out.println("tooltip " + Get.string(ea.get(VisualPropertyType.EDGE_TOOLTIP)));
		System.out.println("font size " + Get.font(ea.get(VisualPropertyType.EDGE_FONT_FACE)).getSize());
		System.out.println("font style " + Get.font(ea.get(VisualPropertyType.EDGE_FONT_FACE)).getStyle());

		assertTrue("color " + Get.color(ea.get(VisualPropertyType.EDGE_COLOR)), 
		                      Get.color(ea.get(VisualPropertyType.EDGE_COLOR)).equals(
		                                    new Color(132, 116, 144)));
		assertTrue("trg arrow " + Get.arrowShape(ea.get(VisualPropertyType.EDGE_TGTARROW_SHAPE)),
		                          Get.arrowShape(ea.get(VisualPropertyType.EDGE_TGTARROW_SHAPE)) == 
		                               ArrowShape.NONE);
		assertTrue("label " + Get.string(ea.get(VisualPropertyType.EDGE_LABEL)), 
		                      Get.string(ea.get(VisualPropertyType.EDGE_LABEL)).equals("0.4"));
		assertTrue("tooltip " + Get.string(ea.get(VisualPropertyType.EDGE_TOOLTIP)), 
		                        Get.string(ea.get(VisualPropertyType.EDGE_TOOLTIP)).equals(""));
		assertTrue("font size " + Get.font(ea.get(VisualPropertyType.EDGE_FONT_FACE)).getSize(), 
		                          Get.font(ea.get(VisualPropertyType.EDGE_FONT_FACE)).getSize() == 5);
		assertTrue("font style " + Get.font(ea.get(VisualPropertyType.EDGE_FONT_FACE)).getStyle(), 
		                           Get.font(ea.get(VisualPropertyType.EDGE_FONT_FACE)).getStyle() == 
		                               Font.PLAIN);
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param args
	 *            DOCUMENT ME!
	 */
	public static void main(String[] args) {
		junit.textui.TestRunner.run(new TestSuite(
				EdgeAppearanceCalculatorTest.class));
	}
}
