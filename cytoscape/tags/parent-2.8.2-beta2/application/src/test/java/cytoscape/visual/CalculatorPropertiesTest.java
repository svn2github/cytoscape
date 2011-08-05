/*
  File: CalculatorPropertiesTest.java

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
// $Revision: 8216 $
// $Date: 2006-09-15 15:48:20 -0700 (Fri, 15 Sep 2006) $
// $Author: mes $
//----------------------------------------------------------------------------
package cytoscape.visual;

import cytoscape.visual.calculators.*;

import cytoscape.visual.mappings.*;

import junit.framework.*;

import java.io.*;

//----------------------------------------------------------------------------
import java.util.*;


//----------------------------------------------------------------------------
/**
 * Test program to check that calculators are loaded properly from the
 * properties file. Works by writing a text description of the read calculators
 * to stdout.
 */
public class CalculatorPropertiesTest extends TestCase {
	/**
	 *  DOCUMENT ME!
	 */
	public void testFailure() {
		// TODOOOO get this working!
		//fail();
	}

	/*
	 public static void main(String[] args) {
	     new CalculatorPropertiesTest();
	 }

	 public CalculatorPropertiesTest() {
	     CalculatorCatalog catalog = new CalculatorCatalog();
	     Properties props = new Properties();
	     try {
	         String propsFile = "vizmap.props";
	         InputStream is = new FileInputStream(propsFile);
	         props.load(is);
	         is.close();
	     } catch (Exception e) {
	         e.printStackTrace();
	         return;
	     }

	     CalculatorIO.loadCalculators(props, catalog);

	     Collection nodeColorCalcs = catalog.getNodeColorCalculators();
	     System.out.println("nodeColorCalcs.size() = " + nodeColorCalcs.size());
	     checkCalculator( catalog.getNodeColorCalculator("testDiscrete") );
	     checkCalculator( catalog.getNodeColorCalculator("testContinuous") );
	     System.out.println();

	     Collection nodeLineTypeCalcs = catalog.getNodeLineTypeCalculators();
	     System.out.println("nodeLineTypeCalcs.size() = " + nodeLineTypeCalcs.size());
	     checkCalculator( catalog.getNodeLineTypeCalculator("testDiscrete") );
	     checkCalculator( catalog.getNodeLineTypeCalculator("testContinuous") );
	     System.out.println();

	     Collection nodeShapeCalcs = catalog.getNodeShapeCalculators();
	     System.out.println("nodeShapeCalcs.size() = " + nodeShapeCalcs.size());
	     checkCalculator( catalog.getNodeShapeCalculator("testDiscrete") );
	     checkCalculator( catalog.getNodeShapeCalculator("testContinuous") );
	     System.out.println();

	     Collection nodeSizeCalcs = catalog.getNodeSizeCalculators();
	     System.out.println("nodeSizeCalcs.size() = " + nodeSizeCalcs.size());
	     checkCalculator( catalog.getNodeSizeCalculator("testDiscrete") );
	     checkCalculator( catalog.getNodeSizeCalculator("testContinuous") );
	     System.out.println();

	     Collection nodeLabelCalcs = catalog.getNodeLabelCalculators();
	     System.out.println("nodeLabelCalcs.size() = " + nodeLabelCalcs.size());
	     checkCalculator( catalog.getNodeLabelCalculator("testDiscrete") );
	     checkCalculator( catalog.getNodeLabelCalculator("testContinuous") );
	     checkCalculator( catalog.getNodeLabelCalculator("testPassThrough") );
	     System.out.println();

	     Collection nodeToolTipCalcs = catalog.getNodeToolTipCalculators();
	     System.out.println("nodeToolTipCalcs.size() = " + nodeToolTipCalcs.size());
	     checkCalculator( catalog.getNodeToolTipCalculator("testDiscrete") );
	     checkCalculator( catalog.getNodeToolTipCalculator("testContinuous") );
	     System.out.println();

	     Collection nodeFontFaceCalcs = catalog.getNodeFontFaceCalculators();
	     System.out.println("nodeFontFaceCalcs.size() = " + nodeFontFaceCalcs.size());
	     checkCalculator( catalog.getNodeFontFaceCalculator("testDiscrete") );
	     checkCalculator( catalog.getNodeFontFaceCalculator("testContinuous") );
	     System.out.println();

	     Collection nodeFontSizeCalcs = catalog.getNodeFontSizeCalculators();
	     System.out.println("nodeFontSizeCalcs.size() = " + nodeFontSizeCalcs.size());
	     checkCalculator( catalog.getNodeFontSizeCalculator("testDiscrete") );
	     checkCalculator( catalog.getNodeFontSizeCalculator("testContinuous") );
	     System.out.println();

	     Collection edgeColorCalcs = catalog.getEdgeColorCalculators();
	     System.out.println("edgeColorCalcs.size() = " + edgeColorCalcs.size());
	     checkCalculator( catalog.getEdgeColorCalculator("testDiscrete") );
	     checkCalculator( catalog.getEdgeColorCalculator("testContinuous") );
	     System.out.println();

	     Collection edgeLineTypeCalcs = catalog.getEdgeLineTypeCalculators();
	     System.out.println("edgeLineTypeCalcs.size() = " + edgeLineTypeCalcs.size());
	     checkCalculator( catalog.getEdgeLineTypeCalculator("testDiscrete") );
	     checkCalculator( catalog.getEdgeLineTypeCalculator("testContinuous") );
	     System.out.println();

	     Collection edgeArrowCalcs = catalog.getEdgeArrowCalculators();
	     System.out.println("edgeArrowCalcs.size() = " + edgeArrowCalcs.size());
	     checkCalculator( catalog.getEdgeArrowCalculator("testDiscrete") );
	     checkCalculator( catalog.getEdgeArrowCalculator("testContinuous") );
	     System.out.println();

	     Collection edgeLabelCalcs = catalog.getEdgeLabelCalculators();
	     System.out.println("edgeLabelCalcs.size() = " + edgeLabelCalcs.size());
	     checkCalculator( catalog.getEdgeLabelCalculator("testDiscrete") );
	     checkCalculator( catalog.getEdgeLabelCalculator("testContinuous") );
	     checkCalculator( catalog.getEdgeLabelCalculator("testPassThrough") );
	     System.out.println();

	     Collection edgeToolTipCalcs = catalog.getEdgeToolTipCalculators();
	     System.out.println("edgeToolTipCalcs.size() = " + edgeToolTipCalcs.size());
	     checkCalculator( catalog.getEdgeToolTipCalculator("testDiscrete") );
	     checkCalculator( catalog.getEdgeToolTipCalculator("testContinuous") );
	     System.out.println();

	     Collection edgeFontFaceCalcs = catalog.getEdgeFontFaceCalculators();
	     System.out.println("edgeFontFaceCalcs.size() = " + edgeFontFaceCalcs.size());
	     checkCalculator( catalog.getEdgeFontFaceCalculator("testDiscrete") );
	     checkCalculator( catalog.getEdgeFontFaceCalculator("testContinuous") );
	     System.out.println();

	     Collection edgeFontSizeCalcs = catalog.getEdgeFontSizeCalculators();
	     System.out.println("edgeFontSizeCalcs.size() = " + edgeFontSizeCalcs.size());
	     checkCalculator( catalog.getEdgeFontSizeCalculator("testDiscrete") );
	     checkCalculator( catalog.getEdgeFontSizeCalculator("testContinuous") );
	 System.out.println();

	 Iterator vizStyles = catalog.getVisualStyles().iterator();
	 while(vizStyles.hasNext()) {
	     VisualStyle style = (VisualStyle) vizStyles.next();
	     System.out.println(style.getName());
	     System.out.println();
	 }
	 }

	 public void checkCalculator(Calculator c) {
	     if (c == null) {
	         System.out.println("Oops, got a null calculator");
	         return;
	     }
	     AbstractCalculator gc = (AbstractCalculator)c;
	     ObjectMapping m = gc.getMapping(0);
	     System.out.println("controller = " + m.getControllingAttributeName());
	     System.out.println("Map = " + m);
	 }
	 */
}
