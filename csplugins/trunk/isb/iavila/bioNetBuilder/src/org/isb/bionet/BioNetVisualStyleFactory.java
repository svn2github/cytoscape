package org.isb.bionet;


import cytoscape.Cytoscape;
import cytoscape.data.Semantics;
import cytoscape.view.CytoscapeDesktop;
import cytoscape.visual.CalculatorCatalog;
import cytoscape.visual.EdgeAppearanceCalculator;
import cytoscape.visual.GlobalAppearanceCalculator;
import cytoscape.visual.LineType;
import cytoscape.visual.NodeAppearanceCalculator;
import cytoscape.visual.ShapeNodeRealizer;
import cytoscape.visual.VisualMappingManager;
import cytoscape.visual.VisualStyle;
import cytoscape.visual.calculators.*;
import cytoscape.visual.mappings.DiscreteMapping;
import cytoscape.visual.mappings.ObjectMapping;
import cytoscape.visual.mappings.PassThroughMapping;
import java.awt.Color;
import org.isb.bionet.datasource.interactions.*;
import org.isb.bionet.gui.wizard.*;

/**
 * Creates visual styles for the BioNetBuilder plugin.
 * 
 * @author iavila
 *
 */
public class BioNetVisualStyleFactory {
    
    public final static String BIONETBUILDER_VS = "BioNetBuilder default";
    
    /**
     * If a visual style named BIONETBUILDER_VS does not exist yet, it creates
     * it and adds it to the collection of Cytoscape visual styles
     */
    public static void addBioNetVisualStyleToCytoscape (){
        CytoscapeDesktop cyDesktop = Cytoscape.getDesktop();
        VisualMappingManager vmManager = cyDesktop.getVizMapManager();
      
        CalculatorCatalog calculatorCatalog = vmManager.getCalculatorCatalog();
        VisualStyle vs = calculatorCatalog.getVisualStyle(BIONETBUILDER_VS);
        if(vs != null) {
            System.out.println("Visual style " + BIONETBUILDER_VS + " already exists.");
            return; // it already exists
        }
        
        NodeAppearanceCalculator nodeAppCalc = new NodeAppearanceCalculator();
        EdgeAppearanceCalculator edgeAppCalc = new EdgeAppearanceCalculator();
         
        // ------------------------------ Set the label ------------------------------//
        // Display the value for Semantics.COMMON_NAME as a label
        String cName = "Common Name";
        NodeLabelCalculator nlc = calculatorCatalog.getNodeLabelCalculator(cName);
        if (nlc == null) {
          PassThroughMapping m =
            new PassThroughMapping(new String(), Semantics.COMMON_NAME);
          nlc = new GenericNodeLabelCalculator(cName, m);
        }
        nodeAppCalc.setNodeLabelCalculator(nlc);
        // ---------------------------- Set the node color --------------------------//
        // According to dataSource attribute
        cName = "Data Source";
        NodeColorCalculator nodeColorCalculator = calculatorCatalog.getNodeColorCalculator(cName);
        if(nodeColorCalculator == null){
            //          Create a discrete color calculator for dataSource
            DiscreteMapping dataSourceMappingNodes =
                new DiscreteMapping(Color.WHITE,ObjectMapping.NODE_MAPPING);
            dataSourceMappingNodes.setControllingAttributeName("dataSource",Cytoscape.getCurrentNetwork(),false);
            dataSourceMappingNodes.putMapValue(BindInteractionsSource.NAME, Color.BLUE);
            dataSourceMappingNodes.putMapValue(DipInteractionsSource.NAME, Color.GREEN);
            dataSourceMappingNodes.putMapValue(KeggInteractionsSource.NAME,Color.RED);
            dataSourceMappingNodes.putMapValue(ProlinksInteractionsSource.NAME,Color.ORANGE);
            dataSourceMappingNodes.putMapValue(NodeSourcesPanel.USER_LIST,Color.CYAN);
            dataSourceMappingNodes.putMapValue(NodeSourcesPanel.ANNOTS,Color.PINK);
            dataSourceMappingNodes.putMapValue(NodeSourcesPanel.TAXONOMY,Color.GRAY);
            dataSourceMappingNodes.putMapValue(NodeSourcesPanel.NETS,Color.MAGENTA);
            nodeColorCalculator = 
                new GenericNodeColorCalculator("Data Source", dataSourceMappingNodes);
        }
        nodeAppCalc.setNodeFillColorCalculator(nodeColorCalculator);
        // --------------------------- Set the edge color --------------------------//
        
        
        EdgeColorCalculator edgeColorCalculator = calculatorCatalog.getEdgeColorCalculator(cName);
        if(edgeColorCalculator == null){
            //  Create a discrete color calculator for dataSource
            DiscreteMapping dataSourceMappingEdges =
                new DiscreteMapping(Color.BLACK,ObjectMapping.EDGE_MAPPING);
            dataSourceMappingEdges.setControllingAttributeName("src",Cytoscape.getCurrentNetwork(),false);
            dataSourceMappingEdges.putMapValue(BindInteractionsSource.NAME, Color.BLUE );
            dataSourceMappingEdges.putMapValue(DipInteractionsSource.NAME, Color.GREEN);
            dataSourceMappingEdges.putMapValue(KeggInteractionsSource.NAME, Color.RED);
            dataSourceMappingEdges.putMapValue(ProlinksInteractionsSource.NAME,Color.ORANGE);
            edgeColorCalculator =
                new GenericEdgeColorCalculator("src", dataSourceMappingEdges);
            calculatorCatalog.addCalculator(edgeColorCalculator);
        }
        
        cName = "Interaction Type";
        edgeColorCalculator = calculatorCatalog.getEdgeColorCalculator(cName);
        if(edgeColorCalculator == null){
            DiscreteMapping interactionTypeMapping = 
                new DiscreteMapping(Color.BLACK, ObjectMapping.EDGE_MAPPING);
            interactionTypeMapping.setControllingAttributeName(Semantics.INTERACTION,
                    Cytoscape.getCurrentNetwork(),false);
            interactionTypeMapping.putMapValue("pp", Color.BLUE);
            interactionTypeMapping.putMapValue("pd", Color.RED);
            interactionTypeMapping.putMapValue("pr", Color.ORANGE);
            interactionTypeMapping.putMapValue(ProlinksInteractionsSource.PP, Color.CYAN);
            interactionTypeMapping.putMapValue(ProlinksInteractionsSource.RS, Color.PINK);
            interactionTypeMapping.putMapValue(ProlinksInteractionsSource.GC, Color.MAGENTA);
            interactionTypeMapping.putMapValue(ProlinksInteractionsSource.GN, Color.DARK_GRAY);
            edgeColorCalculator =
                new GenericEdgeColorCalculator(Semantics.INTERACTION,interactionTypeMapping);
            //Todo: KEGG interactions
            
        }
        edgeAppCalc.setEdgeColorCalculator(edgeColorCalculator);
        
        // ------------------------------ Set node shapes ---------------------------//
//        DiscreteMapping disMapping = new DiscreteMapping(new Byte(ShapeNodeRealizer.RECT),
//                                                        ObjectMapping.NODE_MAPPING);
//        disMapping.setControllingAttributeName(NODE_TYPE_ATT,
//                                               network,
//                                               false);
//        disMapping.putMapValue("metaNode", new Byte(ShapeNodeRealizer.ELLIPSE));
//        GenericNodeShapeCalculator shapeCalculator = 
//            new GenericNodeShapeCalculator("BioNet", disMapping);
//        nodeAppCalc.setNodeShapeCalculator(shapeCalculator);
        
        //---------------------- Set the thickness of the border -------------------//
//        DiscreteMapping borderMapping = new DiscreteMapping(LineType.LINE_1,
//                                                            ObjectMapping.NODE_MAPPING);
//        borderMapping.setControllingAttributeName(NODE_TYPE_ATT,
//                                                  network,
//                                                  false);
//        borderMapping.putMapValue("metaNode", LineType.LINE_5);
//        GenericNodeLineTypeCalculator lineCalculator = 
//          new GenericNodeLineTypeCalculator("Abstract MetaNode",
//                                            borderMapping);
//        nodeAppCalc.setNodeLineTypeCalculator(lineCalculator);
//        
        //--------------------- Set the size of the nodes --------------------------//
//        Double defaultWidth = new Double(70);
//        DiscreteMapping wMapping = new DiscreteMapping(defaultWidth,
//                                                       ObjectMapping.NODE_MAPPING);
//        wMapping.setControllingAttributeName(NODE_TYPE_ATT,
//                                             network,
//                                             true);
//        SpecificNodeSizeCalculator nodeSizeCalculator = 
//          new SpecificNodeSizeCalculator("Abstract MetaNode Width",
//                                         wMapping);
//        
//        nodeSizeCalculator.setSpecialAttrName(NodeAppearanceCalculator.nodeWidthBypass);
//        nodeAppCalc.setNodeWidthCalculator(nodeSizeCalculator);
//        
//        Double defaultHeight = new Double(50);
//        DiscreteMapping hMapping = new DiscreteMapping(defaultHeight,
//                                                       ObjectMapping.NODE_MAPPING);
//        hMapping.setControllingAttributeName(NODE_TYPE_ATT,
//                                             network,
//                                             true);
//        SpecificNodeSizeCalculator nodeSizeCalculator2 = 
//          new SpecificNodeSizeCalculator ("Abstract MetaNode Height",
//                                          hMapping);
//        
//        nodeSizeCalculator2.setSpecialAttrName(NodeAppearanceCalculator.nodeHeightBypass);
//        nodeAppCalc.setNodeHeightCalculator(nodeSizeCalculator2);
        
        // ------------------------------ Font sizes of labels -----------------------------//
//        DiscreteMapping fontSizeMapping = new DiscreteMapping (new Integer(12),
//                                                               ObjectMapping.NODE_MAPPING);
//        fontSizeMapping.setControllingAttributeName(NODE_TYPE_ATT,
//                                                    network,
//                                                    false);
//        fontSizeMapping.putMapValue("metaNode",new Integer(48));
//        
//        GenericNodeFontSizeCalculator fontSizeCalculator = 
//          new GenericNodeFontSizeCalculator("Abstract MetaNode",
//                                            fontSizeMapping);
//        nodeAppCalc.setNodeFontSizeCalculator(fontSizeCalculator);
        
        //------------------------- Create a visual style -------------------------------//
        GlobalAppearanceCalculator gac = 
          vmManager.getVisualStyle().getGlobalAppearanceCalculator();
        VisualStyle visualStyle = new VisualStyle(BIONETBUILDER_VS,
                                                  nodeAppCalc,
                                                  edgeAppCalc,gac);
        
        vmManager.setVisualStyle(visualStyle);
        }
    
}