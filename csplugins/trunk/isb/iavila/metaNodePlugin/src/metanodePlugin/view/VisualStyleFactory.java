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
package metanodePlugin.view;
import cytoscape.*;
import cytoscape.visual.*;
import cytoscape.visual.calculators.*;
import cytoscape.visual.mappings.*;
import org.isb.metanodes.model.*;

/**
 * A class with class methods that create and return custom visual styles for networks
 * that contain meta-nodes.
 *
 * @author Iliana Avila-Campillo iavila@systemsbiology.org, iliana.avila@gmail.com
 * @version %I%, %G%
 * @since 2.0
 */
public class VisualStyleFactory {
  
  /**
   * The name of the visual style for visualizing meta-nodes as modeled
   * by <code>metaNodeViewer.model.AbstractMetaNodeModeler</code>.
   */
  public static final String ABSTRACT_METANODE_VS = "MetaNodeStyle";
  /**
   * The name of the attribute that describes node type.
   * TODO: Maybe add to Semantics?
   */
  public static final String NODE_TYPE_ATT = "nodeType";
  
  /**
   * Creates and returns a visual style for meta-nodes as modeled by 
   * <code>metaNodeViewer.model.AbstractMetaNodeModeler</code>
   * 
   * @param network the CyNetwork with meta-nodes
   */
  public static VisualStyle createAbstractMetaNodeVisualStyle (CyNetwork network){
    
    VisualMappingManager vmManager = Cytoscape.getVisualMappingManager();
    NodeAppearanceCalculator nodeAppCalc = new NodeAppearanceCalculator();
    EdgeAppearanceCalculator edgeAppCalc = new EdgeAppearanceCalculator();
    CalculatorCatalog calculatorCatalog = vmManager.getCalculatorCatalog();
    // ------------------------------ Set the label ------------------------------//
    String cName = MetaNodeModelerFactory.getCytoscapeAbstractMetaNodeModeler().getNetworkAttributesHandler(network).getNodeLabelAttribute();
    NodeLabelCalculator nlc = calculatorCatalog.getNodeLabelCalculator(cName);
    if (nlc == null) {
      PassThroughMapping m =
        new PassThroughMapping(new String(), cName);
      nlc = new GenericNodeLabelCalculator(cName, m);
    }
    nodeAppCalc.setNodeLabelCalculator(nlc);
    
    // ------------------------------ Set node shapes ---------------------------//
    DiscreteMapping disMapping = new DiscreteMapping(new Byte(ShapeNodeRealizer.RECT),
                                                     ObjectMapping.NODE_MAPPING);
    disMapping.setControllingAttributeName(NODE_TYPE_ATT,
                                           network,
                                           false);
    disMapping.putMapValue("metaNode", new Byte(ShapeNodeRealizer.ELLIPSE));
    GenericNodeShapeCalculator shapeCalculator = 
      new GenericNodeShapeCalculator("Abstract MetaNode", disMapping);
    nodeAppCalc.setNodeShapeCalculator(shapeCalculator);
    
    //---------------------- Set the thickness of the border -------------------//
    DiscreteMapping borderMapping = new DiscreteMapping(LineType.LINE_1,
                                                        ObjectMapping.NODE_MAPPING);
    borderMapping.setControllingAttributeName(NODE_TYPE_ATT,
                                              network,
                                              false);
    borderMapping.putMapValue("metaNode", LineType.LINE_5);
    GenericNodeLineTypeCalculator lineCalculator = 
      new GenericNodeLineTypeCalculator("Abstract MetaNode",
                                        borderMapping);
    nodeAppCalc.setNodeLineTypeCalculator(lineCalculator);
    
    //--------------------- Set the size of the nodes --------------------------//
    Double defaultWidth = new Double(70);
    DiscreteMapping wMapping = new DiscreteMapping(defaultWidth,
                                                   ObjectMapping.NODE_MAPPING);
    wMapping.setControllingAttributeName(NODE_TYPE_ATT,
                                         network,
                                         true);
    SpecificNodeSizeCalculator nodeSizeCalculator = 
      new SpecificNodeSizeCalculator("Abstract MetaNode Width",
                                     wMapping);
    
    nodeSizeCalculator.setSpecialAttrName(NodeAppearanceCalculator.nodeWidthBypass);
    nodeAppCalc.setNodeWidthCalculator(nodeSizeCalculator);
    
    Double defaultHeight = new Double(50);
    DiscreteMapping hMapping = new DiscreteMapping(defaultHeight,
                                                   ObjectMapping.NODE_MAPPING);
    hMapping.setControllingAttributeName(NODE_TYPE_ATT,
                                         network,
                                         true);
    SpecificNodeSizeCalculator nodeSizeCalculator2 = 
      new SpecificNodeSizeCalculator ("Abstract MetaNode Height",
                                      hMapping);
    
    nodeSizeCalculator2.setSpecialAttrName(NodeAppearanceCalculator.nodeHeightBypass);
    nodeAppCalc.setNodeHeightCalculator(nodeSizeCalculator2);
    
    // ------------------------------ Font sizes of labels -----------------------------//
    DiscreteMapping fontSizeMapping = new DiscreteMapping (new Integer(12),
                                                           ObjectMapping.NODE_MAPPING);
    fontSizeMapping.setControllingAttributeName(NODE_TYPE_ATT,
                                                network,
                                                false);
    fontSizeMapping.putMapValue("metaNode",new Integer(48));
    
    GenericNodeFontSizeCalculator fontSizeCalculator = 
      new GenericNodeFontSizeCalculator("Abstract MetaNode",
                                        fontSizeMapping);
    nodeAppCalc.setNodeFontSizeCalculator(fontSizeCalculator);
    
    //------------------------- Create a visual style -------------------------------//
    GlobalAppearanceCalculator gac = 
      vmManager.getVisualStyle().getGlobalAppearanceCalculator();
    VisualStyle vs = calculatorCatalog.getVisualStyle(ABSTRACT_METANODE_VS);
    if(vs == null){
    		vs = new VisualStyle(ABSTRACT_METANODE_VS,
                                              nodeAppCalc,
                                              edgeAppCalc,gac);
    		calculatorCatalog.addVisualStyle(vs);
    }else{
    		vs.setNodeAppearanceCalculator(nodeAppCalc);
    		vs.setEdgeAppearanceCalculator(edgeAppCalc);
    		vs.setGlobalAppearanceCalculator(gac);
    }
    return vs;
  }//createAbstractMetaNodeVisualStyle

}//VisualStyleFactory
