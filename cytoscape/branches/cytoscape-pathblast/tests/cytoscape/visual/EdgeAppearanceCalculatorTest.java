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
package cytoscape.visual;

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
import cytoscape.data.Semantics;
import cytoscape.data.readers.CyAttributesReader;
import cytoscape.util.FileUtil;
import java.io.InputStream;
import static cytoscape.visual.VisualPropertyType.*; 

/**
 * 
 */
public class EdgeAppearanceCalculatorTest extends TestCase {
	CyNetwork cyNet;
	CyNode a;
	CyNode b;
	CyEdge ab;
	CalculatorCatalog catalog;
	Properties props;

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
		cyNet = Cytoscape.createNetworkFromFile("testData/small.sif");
		a = Cytoscape.getCyNode("a");
		b = Cytoscape.getCyNode("b");
		ab = Cytoscape.getCyEdge(a, b, Semantics.INTERACTION, "pp", false);
		props = new Properties();

		try {
			CyAttributesReader.loadAttributes(Cytoscape.getEdgeAttributes(),
					new FileReader("testData/small.edgeAttr"));

            InputStream is = null;
            try {
				is = FileUtil.getInputStream("testData/small.vizmap.props");
                props.load(is);
            }
            finally {
                if (is != null) {
                    is.close();
                }
            }
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
		EdgeAppearanceCalculator eac = new EdgeAppearanceCalculator(new VisualPropertyDependencyImpl());

		EdgeAppearance ea = eac.calculateEdgeAppearance(ab, cyNet);

		// this tests that the default edge appearance is correct
		assertTrue("color", ((Color)ea.get(EDGE_COLOR)).equals(Color.BLACK));
		assertTrue("lineType", ((LineStyle)ea.get(EDGE_LINETYPE)) == LineStyle.SOLID);
		assertTrue("src arrow", ((ArrowShape)ea.get(EDGE_SRCARROW_SHAPE)) == ArrowShape.NONE);
		assertTrue("trg arrow", ((ArrowShape)ea.get(EDGE_TGTARROW_SHAPE)) == ArrowShape.NONE);
		assertTrue("label", ((String)ea.get(EDGE_LABEL)).equals(""));
		assertTrue("tooltip", ((String)ea.get(EDGE_TOOLTIP)).equals(""));
		assertTrue("font size", ((Number)ea.get(EDGE_FONT_SIZE)).intValue() == 10);
		assertTrue("font style", ((Font)ea.get(EDGE_FONT_FACE)).getStyle() == Font.PLAIN);
	}

	/**
	 * DOCUMENT ME!
	 */
	public void testApplyProperties() {
		EdgeAppearanceCalculator eac = new EdgeAppearanceCalculator(new VisualPropertyDependencyImpl());
		eac.applyProperties("homer", props, "edgeAppearanceCalculator.homer", catalog);

		EdgeAppearance ea = eac.calculateEdgeAppearance(ab, cyNet);
		System.out.println(eac.getDescription());

		System.out.println("color " + ea.get(EDGE_COLOR));
		System.out.println("linetype " + ((LineStyle)ea.get(EDGE_LINETYPE)).toString());
		System.out.println("src arrow " + ea.get(EDGE_SRCARROW_SHAPE));
		System.out.println("trg arrow " + ea.get(EDGE_TGTARROW_SHAPE));
		System.out.println("label " + ((String)ea.get(EDGE_LABEL)));
		System.out.println("tooltip " + ((String)ea.get(EDGE_TOOLTIP)));
		System.out.println("font size " + ((Number)ea.get(EDGE_FONT_SIZE)).intValue());
		System.out.println("font style " + ((Font)ea.get(EDGE_FONT_FACE)).getStyle());

		assertTrue("color " + ea.get(EDGE_COLOR), ((Color)ea.get(EDGE_COLOR)).equals(
				new Color(132, 116, 144)));
		assertTrue("src arrow " + ea.get(EDGE_SRCARROW_SHAPE),
				((ArrowShape)ea.get(EDGE_SRCARROW_SHAPE)) == ArrowShape.DIAMOND);
		assertTrue("trg arrow " + ea.get(EDGE_TGTARROW_SHAPE),
				((ArrowShape)ea.get(EDGE_TGTARROW_SHAPE)) == ArrowShape.NONE);
		assertTrue("label " + ea.get(EDGE_LABEL), ((String)ea.get(EDGE_LABEL)).equals("0.4"));
		assertTrue("tooltip " + ea.get(EDGE_TOOLTIP), ((String)ea.get(EDGE_TOOLTIP)).equals(""));
		// TODO  this should be right:
		//assertTrue("font size " + ((Number)ea.get(EDGE_FONT_SIZE)).intValue(), ((Number)ea.get(EDGE_FONT_SIZE)).intValue() == 5);
		assertTrue("font style " + ((Font)ea.get(EDGE_FONT_FACE)).getStyle(), ((Font)ea.get(EDGE_FONT_FACE))
				.getStyle() == Font.PLAIN);
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
