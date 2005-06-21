/**  Copyright (c) 2003 Institute for Systems Biology
 **  This program is free software; you can redistribute it and/or modify
 **  it under the terms of the GNU General Public License as published by
 **  the Free Software Foundation; either version 2 of the License, or
 **  any later version.
 **
 **  This program is distributed in the hope that it will be useful,
 **  but WITHOUT ANY WARRANTY; without even the implied warranty of
 **  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  The software and
 **  documentation provided hereunder is on an "as is" basis, and the
 **  Institute for Systems Biology has no obligations to provide maintenance, 
 **  support, updates, enhancements or modifications.  In no event shall the
 **  Institute for Systems Biology be liable to any party for direct, 
 **  indirect, special,incidental or consequential damages, including 
 **  lost profits, arising out of the use of this software and its 
 **  documentation, even if the Institute for Systems Biology 
 **  has been advised of the possibility of such damage. See the
 **  GNU General Public License for more details.
 **   
 **  You should have received a copy of the GNU General Public License
 **  along with this program; if not, write to the Free Software
 **  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 **/
package phenotypeGenetics.view;
import java.awt.Color;
import java.util.*;
import phenotypeGenetics.*;
import cytoscape.*;
import cytoscape.view.*;
import cytoscape.visual.*;
import cytoscape.visual.calculators.*;
import cytoscape.visual.mappings.*;
import cytoscape.visual.ui.*;
import cytoscape.data.Semantics;
import giny.view.EdgeView;
/**
 * A class with class methods that create and modify a PhenotypeGenetics visual style
 *
 * @author Iliana Avila-Campillo iavila@systemsbiology.org, iliana.avila@gmail.com
 * @version %I%, %G%
 * @since 2.0
 */
public class PGVisualStyle {
  
  /**
   * The name of the visual style
   */
  public static final String PHENOTYPE_GENETICS_VS = "PhenotypeGenetics";

  protected static final NodeAppearanceCalculator NODE_APP_CALCULATOR =
    new NodeAppearanceCalculator();

  protected static final EdgeAppearanceCalculator EDGE_APP_CALCULATOR =
    new EdgeAppearanceCalculator();

  protected static DiscreteMapping edgeColorMapping;
  protected static DiscreteMapping edgeTypeMapping;
  protected static DiscreteMapping arrowMapping;
     
  /**
   * Creates and returns a visual style for the PhenotypeGenetics plug-in
   */
  public static VisualStyle createVisualStyle (){
    
    CytoscapeDesktop cyDesktop = Cytoscape.getDesktop();
    VisualMappingManager vmManager = cyDesktop.getVizMapManager();
    //NodeAppearanceCalculator nodeAppCalc = new NodeAppearanceCalculator();
    //EdgeAppearanceCalculator edgeAppCalc = new EdgeAppearanceCalculator();
    CalculatorCatalog calculatorCatalog = vmManager.getCalculatorCatalog();
    
    // ------------------------------ Set the label ------------------------------//
    // Display the value for Semantics.COMMON_NAME as a label
    String cName = Semantics.COMMON_NAME;
    NodeLabelCalculator nlc = calculatorCatalog.getNodeLabelCalculator(cName);
    if(nlc == null){
      PassThroughMapping m =
        new PassThroughMapping(new String(), Semantics.COMMON_NAME);
      nlc = new GenericNodeLabelCalculator(cName, m);
    }
    NODE_APP_CALCULATOR.setNodeLabelCalculator(nlc);
    
    // ------------------------------ Set node shapes ---------------------------//
    // DiscreteMapping disMapping = new DiscreteMapping(new Byte(ShapeNodeRealizer.RECT),
    //                                                      ObjectMapping.NODE_MAPPING);
    //     disMapping.setControllingAttributeName(NODE_TYPE_ATT,
    //                                            network,
    //                                            false);
    //     disMapping.putMapValue("metaNode", new Byte(ShapeNodeRealizer.ELLIPSE));
    //     GenericNodeShapeCalculator shapeCalculator = 
    //       new GenericNodeShapeCalculator("Abstract MetaNode", disMapping);
    //     NODE_APP_CALCULATOR.setNodeShapeCalculator(shapeCalculator);
    
    //---------------------- Set the thickness of the border -------------------//
    // DiscreteMapping borderMapping = new DiscreteMapping(LineType.LINE_1,
    //                                                         ObjectMapping.NODE_MAPPING);
    //     borderMapping.setControllingAttributeName(NODE_TYPE_ATT,
    //                                               network,
    //                                               false);
    //     borderMapping.putMapValue("metaNode", LineType.LINE_5);
    //     GenericNodeLineTypeCalculator lineCalculator = 
    //       new GenericNodeLineTypeCalculator("Abstract MetaNode",
    //                                         borderMapping);
    //     NODE_APP_CALCULATOR.setNodeLineTypeCalculator(lineCalculator);
    
    //--------------------- Set the size of the nodes --------------------------//
    // Double defaultWidth = new Double(70);
    //     DiscreteMapping wMapping = new DiscreteMapping(defaultWidth,
    //                                                    ObjectMapping.NODE_MAPPING);
    //     wMapping.setControllingAttributeName(NODE_TYPE_ATT,
    //                                          network,
    //                                          true);
    //     SpecificNodeSizeCalculator nodeSizeCalculator = 
    //       new SpecificNodeSizeCalculator("Abstract MetaNode Width",
    //                                      wMapping);
    
    //     nodeSizeCalculator.setSpecialAttrName(NodeAppearanceCalculator.nodeWidthBypass);
    //     NODE_APP_CALCULATOR.setNodeWidthCalculator(nodeSizeCalculator);
    
    //     Double defaultHeight = new Double(50);
    //     DiscreteMapping hMapping = new DiscreteMapping(defaultHeight,
    //                                                    ObjectMapping.NODE_MAPPING);
    //     hMapping.setControllingAttributeName(NODE_TYPE_ATT,
    //                                          network,
    //                                          true);
    //     SpecificNodeSizeCalculator nodeSizeCalculator2 = 
    //       new SpecificNodeSizeCalculator ("Abstract MetaNode Height",
    //                                       hMapping);
    
    //     nodeSizeCalculator2.setSpecialAttrName(NodeAppearanceCalculator.nodeHeightBypass);
    //     NODE_APP_CALCULATOR.setNodeHeightCalculator(nodeSizeCalculator2);
    
    // ------------------------------ Font sizes of labels -----------------------------//
    // DiscreteMapping fontSizeMapping = new DiscreteMapping (new Integer(12),
    //                                                            ObjectMapping.NODE_MAPPING);
    //     fontSizeMapping.setControllingAttributeName(NODE_TYPE_ATT,
    //                                                 network,
    //                                                 false);
    //     fontSizeMapping.putMapValue("metaNode",new Integer(48));
    
    //     GenericNodeFontSizeCalculator fontSizeCalculator = 
    //       new GenericNodeFontSizeCalculator("Abstract MetaNode",
    //                                         fontSizeMapping);
    //     NODE_APP_CALCULATOR.setNodeFontSizeCalculator(fontSizeCalculator);
    
    //------------------------- Create a visual style -------------------------------//
    GlobalAppearanceCalculator gac = 
      vmManager.getVisualStyle().getGlobalAppearanceCalculator();
    VisualStyle visualStyle = new VisualStyle(PHENOTYPE_GENETICS_VS,
                                              NODE_APP_CALCULATOR,
                                              EDGE_APP_CALCULATOR,gac);
    // TODO: Not sure if I want to do this:
    //catalog.addVisualStyle(visualStyle);
    return visualStyle;
  }//createAbstractMetaNodeVisualStyle

  /**
   * @param attribute the controlling attribute for the mapping
   * @param att_value the value of the controlling attribute
   * @param direction the direction of the arrow
   */
  public static void setArrowMapping (String attribute, 
                                      String att_value,
                                      String direction){
    
    //CyNetworkView netView = Cytoscape.getCurrentNetworkView();
    VisualMappingManager vmManager = Cytoscape.getDesktop().getVizMapManager();
    //EdgeAppearanceCalculator edgeAppCalc = 
    //vmManager.getVisualStyle().getEdgeAppearanceCalculator();
    
    if(PGVisualStyle.arrowMapping == null){
      PGVisualStyle.arrowMapping = 
        new DiscreteMapping(Arrow.NONE, ObjectMapping.EDGE_MAPPING);
      PGVisualStyle.arrowMapping.setControllingAttributeName(attribute,
                                                             vmManager.getNetwork(),
                                                             false);
    }//arrowMapping == null
    
    if(direction.equals(DiscretePhenoValueInequality.NOT_DIRECTIONAL)){
      PGVisualStyle.arrowMapping.putMapValue(att_value, 
                                             Arrow.NONE);
    }else{
      PGVisualStyle.arrowMapping.putMapValue(att_value, 
                                             Arrow.COLOR_ARROW);
    }
    
    GenericEdgeArrowCalculator earrowc =  
      new GenericEdgeArrowCalculator(attribute, PGVisualStyle.arrowMapping);
    
    EDGE_APP_CALCULATOR.setEdgeTargetArrowCalculator(earrowc);
    
  }//setArrowMapping
  
  /**
   * @param attribute the controlling attribute for the mapping
   * @param att_value the value of the controlling attribute to put in the map
   * @param edge_type the visual attribute
   */
  public static void setEdgeTypeMapping (String attribute,
                                         String att_value,
                                         String edge_type){
    
    //CyNetworkView netView = Cytoscape.getCurrentNetworkView();
    VisualMappingManager vmManager = Cytoscape.getDesktop().getVizMapManager();
    //EdgeAppearanceCalculator edgeAppCalc = 
    //vmManager.getVisualStyle().getEdgeAppearanceCalculator();
    
    if(PGVisualStyle.edgeTypeMapping == null){
      PGVisualStyle.edgeTypeMapping = 
        new DiscreteMapping(LineType.LINE_1,ObjectMapping.EDGE_MAPPING);
      PGVisualStyle.edgeTypeMapping.setControllingAttributeName(attribute,
                                                                vmManager.getNetwork(),
                                                                false);
    }//edgeTypeMapping == null
    
    HashMap stringToLineType = MiscDialog.getStringToLineTypeHashMap();
    LineType lineType = (LineType)stringToLineType.get(edge_type);
    
    PGVisualStyle.edgeTypeMapping.putMapValue(att_value, lineType);    
    
    GenericEdgeLineTypeCalculator eltc =
	    new GenericEdgeLineTypeCalculator(attribute, 
                                        PGVisualStyle.edgeTypeMapping);
    
    EDGE_APP_CALCULATOR.setEdgeLineTypeCalculator(eltc);
    
  }//setEdgeTypeMapping
  
  /**
   * @param attribute the controlling attribute for the mapping
   * @param att_value the value of the controlling attribute to put in the map
   * @param color the visual attribute
   */
  public static void setColorMapping (String attribute, 
                                      String att_value,
                                      Color color){
    
    //CyNetworkView netView = Cytoscape.getCurrentNetworkView();
    VisualMappingManager vmManager = Cytoscape.getDesktop().getVizMapManager();
    //VisualStyle vs = vmManager.getVisualStyle();
       
    //EdgeAppearanceCalculator edgeAppCalc = 
    //vmManager.getVisualStyle().getEdgeAppearanceCalculator();
    
    if(PGVisualStyle.edgeColorMapping == null){
      
      PGVisualStyle.edgeColorMapping = 
        new DiscreteMapping(Color.BLACK, ObjectMapping.EDGE_MAPPING);
      PGVisualStyle.edgeColorMapping.setControllingAttributeName(attribute,
                                                                 vmManager.getNetwork(),
                                                                 false);
    }// if edgeColorMapping == null
    
    PGVisualStyle.edgeColorMapping.putMapValue(att_value, color);
    
    CalculatorCatalog calcCatalog = vmManager.getCalculatorCatalog();
    
    GenericEdgeColorCalculator colorCalculator = 
      (GenericEdgeColorCalculator)calcCatalog.getEdgeColorCalculator(attribute);
    
    if(colorCalculator == null){
      colorCalculator = 
        new GenericEdgeColorCalculator(attribute,
                                       PGVisualStyle.edgeColorMapping);
    }
    
    EDGE_APP_CALCULATOR.setEdgeColorCalculator(colorCalculator);
    
  }//setColorMapping

}//PGVisualStyle
