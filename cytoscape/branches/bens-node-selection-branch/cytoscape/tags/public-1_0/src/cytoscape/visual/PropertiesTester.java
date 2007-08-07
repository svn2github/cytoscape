//----------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//----------------------------------------------------------------------------
package cytoscape.visual;
//----------------------------------------------------------------------------

import java.util.*;
import java.io.*;

import cytoscape.visual.calculators.*;
import cytoscape.visual.mappings.*;
//----------------------------------------------------------------------------
/**
 * Test program to check that calculators are loaded properly from the
 * properties file. Works by writing a text description of the read calculators
 * to stdout.
 */
public class PropertiesTester {
    
    public static void main(String[] args) {
        new PropertiesTester();
    }
    
    public PropertiesTester() {
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
        
        NodeAppearanceCalculator nac1 = catalog.getNodeAppearanceCalculator("testDiscrete");
        System.out.println(nac1.getDescription());
        System.out.println();
        NodeAppearanceCalculator nac2 = catalog.getNodeAppearanceCalculator("testContinuous");
        System.out.println(nac2.getDescription());
        System.out.println();
        EdgeAppearanceCalculator eac1 = catalog.getEdgeAppearanceCalculator("testDiscrete");
        System.out.println(eac1.getDescription());
        System.out.println();
        EdgeAppearanceCalculator eac2 = catalog.getEdgeAppearanceCalculator("testContinuous");
        System.out.println(eac2.getDescription());
        System.out.println();
    }
    
    public void checkCalculator(Calculator c) {
        if (c == null) {
            System.out.println("Oops, got a null calculator");
            return;
        }
        AbstractCalculator gc = (AbstractCalculator)c;
        ObjectMapping m = gc.getMapping();
        System.out.println("controller = " + m.getControllingAttributeName());
        System.out.println("Map = " + m);
    }
}

