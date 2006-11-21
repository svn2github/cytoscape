
/*
  File: PropertiesTester.java 
  
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
//----------------------------------------------------------------------------

import java.util.*;
import java.io.*;
import junit.framework.*;

import cytoscape.visual.calculators.*;
import cytoscape.visual.mappings.*;
//----------------------------------------------------------------------------
/**
 * Test program to check that calculators are loaded properly from the
 * properties file. Works by writing a text description of the read calculators
 * to stdout.
 */
public class VizMapPropertiesTest extends TestCase {
    
    public void testProperties() {
        CalculatorCatalog catalog = new CalculatorCatalog();
        Properties props = new Properties();
        try {
            String propsFile = "resources/props/vizmap.props";
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
        checkCalculator( catalog.getNodeColorCalculator("RedGreen") );
        System.out.println();
        
        Collection nodeLineTypeCalcs = catalog.getNodeLineTypeCalculators();
        System.out.println("nodeLineTypeCalcs.size() = " + nodeLineTypeCalcs.size());
        checkCalculator( catalog.getNodeLineTypeCalculator("BasicDiscrete") );
        checkCalculator( catalog.getNodeLineTypeCalculator("BasicContinuous") );
        System.out.println();
        
        Collection nodeShapeCalcs = catalog.getNodeShapeCalculators();
        System.out.println("nodeShapeCalcs.size() = " + nodeShapeCalcs.size());
        checkCalculator( catalog.getNodeShapeCalculator("BasicDiscrete") );
        checkCalculator( catalog.getNodeShapeCalculator("BasicContinuous") );
        System.out.println();
        
        Collection nodeSizeCalcs = catalog.getNodeSizeCalculators();
        System.out.println("nodeSizeCalcs.size() = " + nodeSizeCalcs.size());
        checkCalculator( catalog.getNodeSizeCalculator("BasicDiscrete") );
        checkCalculator( catalog.getNodeSizeCalculator("BasicContinuous") );
        System.out.println();
        
        Collection nodeLabelCalcs = catalog.getNodeLabelCalculators();
        System.out.println("nodeLabelCalcs.size() = " + nodeLabelCalcs.size());
        checkCalculator( catalog.getNodeLabelCalculator("BasicDiscrete") );
        checkCalculator( catalog.getNodeLabelCalculator("BasicContinuous") );
        checkCalculator( catalog.getNodeLabelCalculator("testPassThrough") );
        System.out.println();
        
        Collection nodeToolTipCalcs = catalog.getNodeToolTipCalculators();
        System.out.println("nodeToolTipCalcs.size() = " + nodeToolTipCalcs.size());
        checkCalculator( catalog.getNodeToolTipCalculator("BasicDiscrete") );
        checkCalculator( catalog.getNodeToolTipCalculator("BasicContinuous") );
        System.out.println();
        
        Collection nodeFontFaceCalcs = catalog.getNodeFontFaceCalculators();
        System.out.println("nodeFontFaceCalcs.size() = " + nodeFontFaceCalcs.size());
        checkCalculator( catalog.getNodeFontFaceCalculator("BasicDiscrete") );
        checkCalculator( catalog.getNodeFontFaceCalculator("BasicContinuous") );
        System.out.println();
        
        Collection nodeFontSizeCalcs = catalog.getNodeFontSizeCalculators();
        System.out.println("nodeFontSizeCalcs.size() = " + nodeFontSizeCalcs.size());
        checkCalculator( catalog.getNodeFontSizeCalculator("BasicDiscrete") );
        checkCalculator( catalog.getNodeFontSizeCalculator("BasicContinuous") );
        System.out.println();
        
        Collection edgeColorCalcs = catalog.getEdgeColorCalculators();
        System.out.println("edgeColorCalcs.size() = " + edgeColorCalcs.size());
        checkCalculator( catalog.getEdgeColorCalculator("BasicDiscrete") );
        checkCalculator( catalog.getEdgeColorCalculator("BasicContinuous") );
        System.out.println();
        
        Collection edgeLineTypeCalcs = catalog.getEdgeLineTypeCalculators();
        System.out.println("edgeLineTypeCalcs.size() = " + edgeLineTypeCalcs.size());
        checkCalculator( catalog.getEdgeLineTypeCalculator("BasicDiscrete") );
        checkCalculator( catalog.getEdgeLineTypeCalculator("BasicContinuous") );
        System.out.println();        
        
        Collection edgeArrowCalcs = catalog.getEdgeArrowCalculators();
        System.out.println("edgeArrowCalcs.size() = " + edgeArrowCalcs.size());
        checkCalculator( catalog.getEdgeArrowCalculator("BasicDiscrete") );
        checkCalculator( catalog.getEdgeArrowCalculator("BasicContinuous") );
        System.out.println();
        
        Collection edgeLabelCalcs = catalog.getEdgeLabelCalculators();
        System.out.println("edgeLabelCalcs.size() = " + edgeLabelCalcs.size());
        checkCalculator( catalog.getEdgeLabelCalculator("BasicDiscrete") );
        checkCalculator( catalog.getEdgeLabelCalculator("BasicContinuous") );
        checkCalculator( catalog.getEdgeLabelCalculator("testPassThrough") );
        System.out.println();
        
        Collection edgeToolTipCalcs = catalog.getEdgeToolTipCalculators();
        System.out.println("edgeToolTipCalcs.size() = " + edgeToolTipCalcs.size());
        checkCalculator( catalog.getEdgeToolTipCalculator("BasicDiscrete") );
        checkCalculator( catalog.getEdgeToolTipCalculator("BasicContinuous") );
        System.out.println();
        
        Collection edgeFontFaceCalcs = catalog.getEdgeFontFaceCalculators();
        System.out.println("edgeFontFaceCalcs.size() = " + edgeFontFaceCalcs.size());
        checkCalculator( catalog.getEdgeFontFaceCalculator("BasicDiscrete") );
        checkCalculator( catalog.getEdgeFontFaceCalculator("BasicContinuous") );
        System.out.println();
        
        Collection edgeFontSizeCalcs = catalog.getEdgeFontSizeCalculators();
        System.out.println("edgeFontSizeCalcs.size() = " + edgeFontSizeCalcs.size());
        checkCalculator( catalog.getEdgeFontSizeCalculator("BasicDiscrete") );
        checkCalculator( catalog.getEdgeFontSizeCalculator("BasicContinuous") );
	System.out.println();

	Iterator vizStyles = catalog.getVisualStyles().iterator();
	while(vizStyles.hasNext()) {
	    VisualStyle style = (VisualStyle) vizStyles.next();
	    System.out.println(style.getName());
	    System.out.println();
	}
    }
    
    private void checkCalculator(Calculator c) {
        if (c == null) {
		fail();
        }
        AbstractCalculator gc = (AbstractCalculator)c;
        ObjectMapping m = gc.getMapping(0);
        System.out.println("controller = " + m.getControllingAttributeName());
        System.out.println("Map = " + m);
    }
}

