package sbmlreader;

import cytoscape.*;
import cytoscape.view.CytoscapeDesktop;
import cytoscape.visual.*;
import cytoscape.visual.ui.*;
import cytoscape.visual.calculators.*;
import cytoscape.visual.mappings.*;
import cytoscape.data.Semantics;
import giny.view.EdgeView;
import java.awt.Color;


/**
 * VisualStyleFactory.java
 * This class defines the visualstyle in Cytoscape for the SBMLReader plugin.
 * 
 * @author W.P.A. Ligtenberg, Eindhoven University of Technology
 */
public class SBMLVisualStyleFactory {
    
    public static final String SBMLReader_VS = "SBMLReader Style";
    public static final String NODE_TYPE_ATT = "sbml type";
    public static final String EDGE_TYPE_ATT = "interaction";
    
    public static VisualStyle createVisualStyle(CyNetwork network){
        VisualMappingManager vmManager = Cytoscape.getVisualMappingManager();
        NodeAppearanceCalculator nodeAppCalc = new NodeAppearanceCalculator();
        EdgeAppearanceCalculator edgeAppCalc = new EdgeAppearanceCalculator();
        CalculatorCatalog calculatorCatalog = vmManager.getCalculatorCatalog();
        
        // ------------------------------ Set node shapes ---------------------------//
        DiscreteMapping disMapping = new DiscreteMapping(new Byte(
                ShapeNodeRealizer.RECT),ObjectMapping.NODE_MAPPING);
        disMapping.setControllingAttributeName(NODE_TYPE_ATT, network, false);
        disMapping.putMapValue("species", new Byte(ShapeNodeRealizer.DIAMOND));
        disMapping.putMapValue("reaction", new Byte(ShapeNodeRealizer.ELLIPSE));
        Calculator shapeCalculator = new GenericNodeShapeCalculator("SBMLReader Shape Calculator", 
	                                                             disMapping);
        nodeAppCalc.setCalculator(shapeCalculator); 
        
        // ------------------------------ Set the label ------------------------------//
        // Display the value for geneName as a label
        String cName = "sbml name";
        Calculator nlc = calculatorCatalog.getCalculator(VizMapUI.NODE_LABEL, cName);
        if (nlc == null) {
          PassThroughMapping m = new PassThroughMapping(new String(), cName);
          nlc = new GenericNodeLabelCalculator(cName, m);
        }
        nodeAppCalc.setCalculator(nlc);
        
        //--------------------- Set the size of the nodes --------------------------//
        //Discrete mapping on nodeType
        Double speciesNodeSize = new Double(30);
        Double reactionNodeSize = new Double(30);
        DiscreteMapping sizeMapping = new DiscreteMapping(reactionNodeSize ,ObjectMapping.NODE_MAPPING);
        sizeMapping.setControllingAttributeName(NODE_TYPE_ATT, network, false);
        sizeMapping.putMapValue("species", speciesNodeSize);
        sizeMapping.putMapValue("reaction", reactionNodeSize);
        Calculator sizeCalculator = new GenericNodeUniformSizeCalculator("SBMLReader Size Calculator", 
	                                                                  sizeMapping);
        nodeAppCalc.setCalculator(sizeCalculator);
	nodeAppCalc.setNodeSizeLocked(true);
        
        // ------------------------------ Set edge arrow shape ---------------------------//
        DiscreteMapping arrowMapping = new DiscreteMapping(Arrow.BLACK_DELTA ,ObjectMapping.NODE_MAPPING);
        arrowMapping.setControllingAttributeName(EDGE_TYPE_ATT, network, false);
        arrowMapping.putMapValue("reaction-product", Arrow.COLOR_ARROW);
        arrowMapping.putMapValue("reaction-reactant", Arrow.NONE);
        arrowMapping.putMapValue("reaction-modifier", Arrow.COLOR_CIRCLE);
        Calculator edgeArrowCalculator = new GenericEdgeTargetArrowCalculator(
	                                      "SBMLReader Edge Arrow Calculator", arrowMapping);
        edgeAppCalc.setCalculator(edgeArrowCalculator); 
        
        // ------------------------------ Set edge colour ---------------------------//
        DiscreteMapping edgeColorMapping = new DiscreteMapping(Color.BLACK ,ObjectMapping.NODE_MAPPING);
        edgeColorMapping.setControllingAttributeName(EDGE_TYPE_ATT, network, false);
        edgeColorMapping.putMapValue("reaction-product", Color.GREEN);
        edgeColorMapping.putMapValue("reaction-reactant", Color.RED);
        edgeColorMapping.putMapValue("reaction-modifier", Color.BLACK);
        Calculator edgeColorCalculator = new GenericEdgeColorCalculator("SBMLReader Edge Color Calculator", 
	                                                                edgeColorMapping);
        edgeAppCalc.setCalculator(edgeColorCalculator);
                
        //------------------------- Create a visual style -------------------------------//
        GlobalAppearanceCalculator gac = vmManager.getVisualStyle().getGlobalAppearanceCalculator();
        VisualStyle visualStyle = new VisualStyle(SBMLReader_VS, nodeAppCalc, edgeAppCalc,gac);
        return visualStyle;
    }
}
