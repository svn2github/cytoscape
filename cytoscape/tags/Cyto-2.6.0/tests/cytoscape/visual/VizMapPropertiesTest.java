/*
  File: VizMapPropertiesTest.java

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

//----------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//----------------------------------------------------------------------------
package cytoscape.visual;

import cytoscape.visual.calculators.AbstractCalculator;
import cytoscape.visual.calculators.Calculator;

import cytoscape.visual.mappings.ObjectMapping;

import junit.framework.TestCase;

import java.awt.Color;

import java.io.FileInputStream;
import java.io.InputStream;

import java.util.Collection;
import java.util.Iterator;
import java.util.Properties;


//----------------------------------------------------------------------------
/**
 * Test program to check that calculators are loaded properly from the
 * properties file. Works by writing a text description of the read calculators
 * to stdout.
 */
public class VizMapPropertiesTest extends TestCase {
	private CalculatorCatalog catalog;
	private Properties props;

	/**
	 *  DOCUMENT ME!
	 */
	public void setUp() {
		catalog = new CalculatorCatalog();
		props = new Properties();

		try {
			String propsFile = "testData/old_vizmap.props";
			InputStream is = new FileInputStream(propsFile);
			props.load(is);
			is.close();
		} catch (Exception e) {
			e.printStackTrace();

			return;
		}

		CalculatorIO.loadCalculators(props, catalog);
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void tearDown() {
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void testProperties() {
		Collection nodeColorCalcs = catalog.getCalculators(VisualPropertyType.NODE_FILL_COLOR);
		System.out.println("nodeColorCalcs.size() = " + nodeColorCalcs.size());
		checkCalculator(catalog.getCalculator(VisualPropertyType.NODE_FILL_COLOR, "RedGreen"));
		System.out.println();

		Collection nodeLineTypeCalcs = catalog.getCalculators(VisualPropertyType.NODE_LINETYPE);
		System.out.println("nodeLineTypeCalcs.size() = " + nodeLineTypeCalcs.size());
		//checkCalculator(catalog.getNodeLineTypeCalculator("BasicDiscrete"));
		assertNull(catalog.getCalculator(VisualPropertyType.NODE_LINETYPE, "BasicDiscrete"));

		//checkCalculator(catalog.getNodeLineTypeCalculator("BasicContinuous"));
		assertNull(catalog.getCalculator(VisualPropertyType.NODE_LINETYPE, "BasicContinuous"));
		System.out.println();

		Collection nodeShapeCalcs = catalog.getCalculators(VisualPropertyType.NODE_SHAPE);
		System.out.println("nodeShapeCalcs.size() = " + nodeShapeCalcs.size());
		checkCalculator(catalog.getCalculator(VisualPropertyType.NODE_SHAPE, "BasicDiscrete"));
		checkCalculator(catalog.getCalculator(VisualPropertyType.NODE_SHAPE, "BasicContinuous"));
		System.out.println();

		Collection nodeSizeCalcs = catalog.getCalculators(VisualPropertyType.NODE_SIZE);
		System.out.println("nodeSizeCalcs.size() = " + nodeSizeCalcs.size());
		checkCalculator(catalog.getCalculator(VisualPropertyType.NODE_SIZE, "BasicDiscrete"));
		checkCalculator(catalog.getCalculator(VisualPropertyType.NODE_SIZE, "BasicContinuous"));
		System.out.println();

		Collection nodeLabelCalcs = catalog.getCalculators(VisualPropertyType.NODE_LABEL);
		System.out.println("nodeLabelCalcs.size() = " + nodeLabelCalcs.size());
		checkCalculator(catalog.getCalculator(VisualPropertyType.NODE_LABEL, "BasicDiscrete"));
		checkCalculator(catalog.getCalculator(VisualPropertyType.NODE_LABEL, "BasicContinuous"));
		System.out.println();

		Collection nodeToolTipCalcs = catalog.getCalculators(VisualPropertyType.NODE_TOOLTIP);
		System.out.println("nodeToolTipCalcs.size() = " + nodeToolTipCalcs.size());
		checkCalculator(catalog.getCalculator(VisualPropertyType.NODE_TOOLTIP, "BasicDiscrete"));
		checkCalculator(catalog.getCalculator(VisualPropertyType.NODE_TOOLTIP, "BasicContinuous"));
		System.out.println();

		Collection nodeFontFaceCalcs = catalog.getCalculators(VisualPropertyType.NODE_FONT_FACE);
		System.out.println("nodeFontFaceCalcs.size() = " + nodeFontFaceCalcs.size());
		checkCalculator(catalog.getCalculator(VisualPropertyType.NODE_FONT_FACE, "BasicDiscrete"));
		checkCalculator(catalog.getCalculator(VisualPropertyType.NODE_FONT_FACE, "BasicContinuous"));
		System.out.println();

		Collection nodeFontSizeCalcs = catalog.getCalculators(VisualPropertyType.NODE_FONT_SIZE);
		System.out.println("nodeFontSizeCalcs.size() = " + nodeFontSizeCalcs.size());
		checkCalculator(catalog.getCalculator(VisualPropertyType.NODE_FONT_SIZE, "BasicDiscrete"));
		checkCalculator(catalog.getCalculator(VisualPropertyType.NODE_FONT_SIZE, "BasicContinuous"));
		System.out.println();

		Collection edgeColorCalcs = catalog.getCalculators(VisualPropertyType.EDGE_COLOR);
		System.out.println("edgeColorCalcs.size() = " + edgeColorCalcs.size());
		checkCalculator(catalog.getCalculator(VisualPropertyType.EDGE_COLOR, "BasicDiscrete"));
		System.out.println();

		Collection edgeLineTypeCalcs = catalog.getCalculators(VisualPropertyType.EDGE_LINETYPE);
		System.out.println("edgeLineTypeCalcs.size() = " + edgeLineTypeCalcs.size());
		assertEquals(0, edgeLineTypeCalcs.size());
		//		checkCalculator(catalog.getEdgeLineTypeCalculator("BasicDiscrete"));
		//		checkCalculator(catalog.getEdgeLineTypeCalculator("BasicContinuous"));
		System.out.println();

		Collection edgeArrowCalcs = catalog.getCalculators(VisualPropertyType.EDGE_SRCARROW);
		System.out.println("edgeArrowCalcs.size() = " + edgeArrowCalcs.size());
		checkCalculator(catalog.getCalculator(VisualPropertyType.EDGE_SRCARROW, "BasicDiscrete"));
		checkCalculator(catalog.getCalculator(VisualPropertyType.EDGE_TGTARROW, "BasicContinuous"));
		System.out.println();

		Collection edgeLabelCalcs = catalog.getCalculators(VisualPropertyType.EDGE_LABEL);
		System.out.println("edgeLabelCalcs.size() = " + edgeLabelCalcs.size());
		checkCalculator(catalog.getCalculator(VisualPropertyType.EDGE_LABEL, "BasicDiscrete"));
		checkCalculator(catalog.getCalculator(VisualPropertyType.EDGE_LABEL, "BasicContinuous"));
		checkCalculator(catalog.getCalculator(VisualPropertyType.EDGE_LABEL, "testPassThrough"));
		System.out.println();

		Collection edgeToolTipCalcs = catalog.getCalculators(VisualPropertyType.EDGE_TOOLTIP);
		System.out.println("edgeToolTipCalcs.size() = " + edgeToolTipCalcs.size());
		checkCalculator(catalog.getCalculator(VisualPropertyType.EDGE_TOOLTIP, "BasicDiscrete"));
		checkCalculator(catalog.getCalculator(VisualPropertyType.EDGE_TOOLTIP, "BasicContinuous"));
		System.out.println();

		Collection edgeFontFaceCalcs = catalog.getCalculators(VisualPropertyType.EDGE_FONT_FACE);
		System.out.println("edgeFontFaceCalcs.size() = " + edgeFontFaceCalcs.size());
		checkCalculator(catalog.getCalculator(VisualPropertyType.EDGE_FONT_FACE, "BasicDiscrete"));
		checkCalculator(catalog.getCalculator(VisualPropertyType.EDGE_FONT_FACE, "BasicContinuous"));
		System.out.println();

		Collection edgeFontSizeCalcs = catalog.getCalculators(VisualPropertyType.EDGE_FONT_SIZE);
		System.out.println("edgeFontSizeCalcs.size() = " + edgeFontSizeCalcs.size());
		checkCalculator(catalog.getCalculator(VisualPropertyType.EDGE_FONT_SIZE, "BasicDiscrete"));
		checkCalculator(catalog.getCalculator(VisualPropertyType.EDGE_FONT_SIZE, "BasicContinuous"));
		System.out.println();
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void testVisualStyle() {
		final String DEF = "default";
		final String CLONE = "default clone1";

		System.out.println("====================== Start Visual Style Test ====================");

		Collection<VisualStyle> vizStyles = catalog.getVisualStyles();

		// Show current style names
		for (VisualStyle style : vizStyles)
			System.out.println("Style Names: " + style.getName());

		// Create clone
		final VisualStyle originalStyle = catalog.getVisualStyle(DEF);
		assertNotNull(originalStyle);

		// Set original style node & background color
		originalStyle.getGlobalAppearanceCalculator().setDefaultBackgroundColor(Color.white);
		originalStyle.getNodeAppearanceCalculator().getDefaultAppearance()
		             .set(VisualPropertyType.NODE_FILL_COLOR, Color.red);
		System.out.println("Original Style BG Color = "
                + originalStyle.getGlobalAppearanceCalculator().getDefaultBackgroundColor());
		System.out.println("Original Style Def Node Color = "
		                   + originalStyle.getNodeAppearanceCalculator().getDefaultAppearance()
		                                  .get(VisualPropertyType.NODE_FILL_COLOR));

		final VisualStyle cloneStyle = new VisualStyle(originalStyle, CLONE);
		assertNotNull(originalStyle);
		assertEquals(cloneStyle.getName(), CLONE);

		// Register clone to the catalog
		catalog.addVisualStyle(cloneStyle);

		cloneStyle.getGlobalAppearanceCalculator().setDefaultBackgroundColor(Color.black);
		cloneStyle.getNodeAppearanceCalculator().getDefaultAppearance()
		          .set(VisualPropertyType.NODE_FILL_COLOR, Color.blue);
		System.out.println("Clone Style BG Color = "
                + cloneStyle.getGlobalAppearanceCalculator().getDefaultBackgroundColor());
		System.out.println("Clone Style Def Node Color = "
		                   + cloneStyle.getNodeAppearanceCalculator().getDefaultAppearance()
		                               .get(VisualPropertyType.NODE_FILL_COLOR));

		for (VisualStyle style : vizStyles)
			System.out.println("New Style Names: " + style.getName());

		assertEquals(catalog.getVisualStyle(DEF).getGlobalAppearanceCalculator()
		                    .getDefaultBackgroundColor(), Color.white);
		assertEquals(catalog.getVisualStyle(CLONE).getGlobalAppearanceCalculator()
		                    .getDefaultBackgroundColor(), Color.black);

		assertEquals(catalog.getVisualStyle(DEF).getNodeAppearanceCalculator().getDefaultAppearance()
		                    .get(VisualPropertyType.NODE_FILL_COLOR), Color.red);
		assertEquals(catalog.getVisualStyle(CLONE).getNodeAppearanceCalculator()
		                    .getDefaultAppearance().get(VisualPropertyType.NODE_FILL_COLOR),
		             Color.blue);

		System.out.println("====================== End Visual Style Test ====================");
	}

	private void checkCalculator(Calculator c) {
		if (c == null) {
			fail();
		}

		AbstractCalculator gc = (AbstractCalculator) c;
		ObjectMapping m = gc.getMapping(0);
		System.out.println("controller = " + m.getControllingAttributeName());
		System.out.println("Map = " + m);
	}
}
