/* -*-Java-*-
********************************************************************************
*
* File:         BioChemicalReactionVisualStyle.java
* RCS:          $Header: /cvs/cvsroot/lstl-lsi/HyperEdgeEditor/src/cytoscape/hyperedge/editor/BioChemicalReactionVisualStyle.java,v 1.1 2007/07/04 01:19:09 creech Exp $
* Description:
* Author:       Michael L. Creech
* Created:      Fri Jul 21 11:37:47 2006
* Modified:     Fri Jul 13 08:55:42 2007 (Michael L. Creech) creech@w235krbza760
* Language:     Java
* Package:
* Status:       Experimental (Do Not Distribute)
*
* (c) Copyright 2006, Agilent Technologies, all rights reserved.
*
********************************************************************************
*
* Revisions:
*
* Fri Jul 13 08:55:20 2007 (Michael L. Creech) creech@w235krbza760
*  Added setup of edge tooltips in createEdgeTooltips().
* Thu May 10 14:37:57 2007 (Michael L. Creech) creech@w235krbza760
*  Updated to Cytoscape 2.5 removing use of ShapeNodeRealizer and
*  changing Arrow references.
* Tue Jan 16 09:16:47 2007 (Michael L. Creech) creech@w235krbza760
*  Commented out some debugging statements.
* Fri Dec 01 05:32:25 2006 (Michael L. Creech) creech@w235krbza760
*  Changed to use HyperEdgeImpl.ENTITY_TYPE_ATTRIBUTE_NAME for Node calculators.
* Sat Nov 25 11:23:46 2006 (Michael L. Creech) creech@w235krbza760
*  Updated this Visual Style to use new form for Calculators (Cytoscape 2.4
*  changes).
* Sun Sep 10 13:05:01 2006 (Michael L. Creech) creech@w235krbza760
*  Changed createNodeSize() to use
*  HyperEdgeImpl.IS_CONNECTOR_NODE_ATTRIBUTE_NAME versus
*  CytoscapeEditorManager.NODE_TYPE.
*
********************************************************************************
*/
package cytoscape.hyperedge.editor;

import cytoscape.Cytoscape;

import cytoscape.data.Semantics;

import cytoscape.hyperedge.EdgeTypeMap;

import cytoscape.hyperedge.impl.HyperEdgeImpl;

import cytoscape.visual.Arrow;
import cytoscape.visual.ArrowShape;
import cytoscape.visual.CalculatorCatalog;
import cytoscape.visual.EdgeAppearanceCalculator;
import cytoscape.visual.GlobalAppearanceCalculator;
import cytoscape.visual.NodeAppearanceCalculator;
import cytoscape.visual.NodeShape;
import cytoscape.visual.VisualMappingManager;
import cytoscape.visual.VisualPropertyType;
import cytoscape.visual.VisualStyle;

import cytoscape.visual.calculators.BasicCalculator;
import cytoscape.visual.calculators.Calculator;

import cytoscape.visual.mappings.DiscreteMapping;
import cytoscape.visual.mappings.ObjectMapping;
import cytoscape.visual.mappings.PassThroughMapping;

import java.awt.Color;


/**
 *
 * Creates visual style that is to be used for presenting the HyperEdge sample
 * CyNetworks and for use by the HyperEdgeEditor.
 * @author Michael L. Creech
 *
 */
public class BioChemicalReactionVisualStyle extends VisualStyle {
    public static final String BIOCHEMICAL_REACTION_VISUAL_STYLE = "BioChemicalReaction";

    // public static final String BIOCHEMICAL_REACTION_VISUAL_STYLE = "HyperEdgeEditor";

    //    public static final String PRODUCT = EdgeTypeMap.PRODUCT;
    //    public static final String ACTIVATING_MEDIATOR = EdgeTypeMap.ACTIVATING_MEDIATOR;
    //    public static final String INHIBITING_MEDIATOR = EdgeTypeMap.INHIBITING_MEDIATOR;
    //    public static final String SUBSTRATE = EdgeTypeMap.SUBSTRATE;
    public static final String                    HE_CONNECTOR_NODE   = "hyperEdgeConnectorNode";
    public static final double                    CONNECTOR_NODE_SIZE = 10.0;
    private static BioChemicalReactionVisualStyle _bcrVisualStyle     = new BioChemicalReactionVisualStyle();

    //    /**
    //     * Cytoscape Attribute:  Edge Type.
    //     */
    //    public static final String EDGE_TYPE =
    //            "EDGE_TYPE";
    //    
    //    public static final String ACTIVATION = "Activation";
    //    public static final String INHIBITION = "Inhibition";
    //    public static final String CATALYSIS = "Catalysis";
    private BioChemicalReactionVisualStyle() {
        super(BIOCHEMICAL_REACTION_VISUAL_STYLE);
    }

    private BioChemicalReactionVisualStyle(VisualStyle style) {
        super(style);
    }

    /**
     * Return current instance.
     */
    public static BioChemicalReactionVisualStyle getVisualStyle() {
        return _bcrVisualStyle;
    }

    /**
     * Creates a New BioChemicalReaction visual style.  If an existing
     * BioChemicalReaction visual style already exists, we use it.
     * Otherwise, we create a new one.
     */

    //    public VisualStyle createVizMapper() {
    public BioChemicalReactionVisualStyle setupVisualStyle(boolean forceRedefine,
                                                           boolean setAsCurrentVisualStyle) {
        VisualMappingManager manager = Cytoscape.getVisualMappingManager();
        CalculatorCatalog    catalog = manager.getCalculatorCatalog();

        VisualStyle foundVisualStyle = catalog.getVisualStyle(BIOCHEMICAL_REACTION_VISUAL_STYLE);

        if (forceRedefine) {
            if (foundVisualStyle != null) {
                // remove the old one:
                catalog.removeVisualStyle(foundVisualStyle.getName());
                foundVisualStyle = null;
            }
        }

        if (foundVisualStyle == null) {
            // What would be better is to reset the existing
            // _foundVisualStyle, but there isn't any reset:
            _bcrVisualStyle = new BioChemicalReactionVisualStyle();
            _bcrVisualStyle.primSetupStyle(manager, catalog, true);
            // try setting the visual style:
        } else {
            // 'this' should be foundVisualStyle:
            if (foundVisualStyle != this) {
                // We have the case of reading in a saved visual style, which will not be
                // a BioChemicalReactionVisualStyle. So take its characteristics and
                // copy to new BioChemicalReactionVisualStyle:
                // remove the old one:
                catalog.removeVisualStyle(foundVisualStyle.getName());
                _bcrVisualStyle = new BioChemicalReactionVisualStyle(foundVisualStyle);
                _bcrVisualStyle.primSetupStyle(manager, catalog, false);
            }

            if ((manager.getVisualStyle() != foundVisualStyle) &&
                (setAsCurrentVisualStyle)) {
                // Change the current visual style 
                Cytoscape.getDesktop().setVisualStyle(foundVisualStyle);
            }
        }

        return _bcrVisualStyle;
    }

    private void primSetupStyle(VisualMappingManager manager,
                                CalculatorCatalog catalog, boolean defineDefault) {
        // MLC 01/15/07:
        // HEUtils.log("defining visual style: " + this);
        if (defineDefault) {
            defineVisualStyle(manager, catalog);
        }

        manager.setVisualStyle(this);

        //  The visual style must be added to the Global Catalog
        //  in order for it to be written out to vizmap.props upon user exit
        // MLC 01/15/07:
        // HEUtils.log("Adding visual style " + this + " to catalog " + catalog);
        catalog.addVisualStyle(this);

        // Cytoscape.getDesktop().setVisualStyle(this);
    }

    /**
     * Create the mappings for Node shape, color, and Edge target arrow, line type
     * for this visual style
     * @param manager
     * @param catalog
     */
    private void defineVisualStyle(VisualMappingManager manager,
                                   CalculatorCatalog catalog) {
        NodeAppearanceCalculator nac = new NodeAppearanceCalculator();
        // nac.setDefaultNodeLabelColor(Color.BLACK);
        // MLC 06/30/07:
        // nac.getDefaultAppearance().setLabelColor(Color.BLACK);
        // MLC 06/30/07:
        nac.getDefaultAppearance()
           .set(VisualPropertyType.NODE_LABEL_COLOR, Color.BLACK);

        EdgeAppearanceCalculator   eac = new EdgeAppearanceCalculator();
        GlobalAppearanceCalculator gac = new GlobalAppearanceCalculator();
        gac.setDefaultBackgroundColor(new Color(204, 204, 255));

        createNodeShape(nac);
        createNodeSize(nac);
        createNodeLabel(nac);
        createNodeColor(nac);
        createTargetArrows(eac);
        // MLC 07/13/07 BEGIN:
        createEdgeTooltips (eac);
        // MLC 07/13/07 END.
        _bcrVisualStyle.setNodeAppearanceCalculator(nac);
        _bcrVisualStyle.setEdgeAppearanceCalculator(eac);
        _bcrVisualStyle.setGlobalAppearanceCalculator(gac);
    }

    /**
     * create mappings for TargetArrows for edges
     * @param eac
     */
    private void createTargetArrows(EdgeAppearanceCalculator eac) {
        // the first argument to the DiscreteMapping constructor seems
        // to no longer define the default. The default appearance
        // from the EdgeAppearanceCalculator must be used instead:
        DiscreteMapping discreteMapping = new DiscreteMapping(Arrow.NONE,
                                                              Semantics.INTERACTION,
                                                              ObjectMapping.EDGE_MAPPING);
        discreteMapping.putMapValue(EdgeTypeMap.SUBSTRATE, Arrow.NONE);
        // MLC 05/10/07 BEGIN:
        //        discreteMapping.putMapValue(EdgeTypeMap.PRODUCT, Arrow.BLACK_DELTA);
        //        discreteMapping.putMapValue(EdgeTypeMap.ACTIVATING_MEDIATOR,
        //                                    Arrow.BLACK_DELTA);
        //        discreteMapping.putMapValue(EdgeTypeMap.INHIBITING_MEDIATOR,
        //                                    Arrow.BLACK_T);
        discreteMapping.putMapValue(EdgeTypeMap.PRODUCT,
                                    new Arrow(ArrowShape.DELTA, Color.BLACK));
        discreteMapping.putMapValue(EdgeTypeMap.ACTIVATING_MEDIATOR,
                                    new Arrow(ArrowShape.DELTA, Color.BLACK));
        discreteMapping.putMapValue(EdgeTypeMap.INHIBITING_MEDIATOR,
                                    new Arrow(ArrowShape.T, Color.BLACK));

        // MLC 05/10/07 END.
        // MLC 06/30/07 BEGIN:
        //        GenericEdgeTargetArrowCalculator edgeTargetArrowCalculator = new GenericEdgeTargetArrowCalculator("HyperEdge target arrows",
        //                                                                                                          discreteMapping);
        // eac.getDefaultAppearance().setTargetArrow(Arrow.NONE);
        Calculator edgeTargetArrowCalculator = new BasicCalculator("HyperEdge target arrows",
                                                                   discreteMapping,
                                                                   VisualPropertyType.EDGE_TGTARROW);
        eac.getDefaultAppearance()
           .set(VisualPropertyType.EDGE_TGTARROW, Arrow.NONE);
        // MLC 06/30/07 END.
        eac.setCalculator(edgeTargetArrowCalculator);
        // MLC 01/15/07:
        // HEUtils.log("Set edge target arrow calculator to " +
        //            edgeTargetArrowCalculator);
    }

    // MLC 07/13/07 BEGIN:
    /**
     * creates a passthrough mapping for Edge tooltip.
     */

    private void createEdgeTooltips(EdgeAppearanceCalculator eac) {
	PassThroughMapping passThroughMapping = new PassThroughMapping("",
								       ObjectMapping.EDGE_MAPPING);
	
	// Cytoscape produces canonicalName attributes that start as the value
	// of the edge IDs:
	passThroughMapping.setControllingAttributeName
            (Semantics.CANONICAL_NAME, null, false);
	
	Calculator edgeTooltipCalculator = new BasicCalculator("Edge Tooltip",
							       passThroughMapping,
							       VisualPropertyType.EDGE_TOOLTIP);
	eac.setCalculator(edgeTooltipCalculator);
    }
    // MLC 07/13/07 END.

    /**
     * creates a passthrough mapping for Node Label
     * TODO: what attribute should this be based upon: canonical name, label?
     * @param nac
     */
    private void createNodeLabel(NodeAppearanceCalculator nac) {
        PassThroughMapping passThroughMapping = new PassThroughMapping("",
                                                                       ObjectMapping.NODE_MAPPING);

        //      change canonicalName to Label
        //	        passThroughMapping.setControllingAttributeName
        //	                ("canonicalName", null, false);
        passThroughMapping.setControllingAttributeName //            (Semantics.LABEL, null, false);
        (HyperEdgeImpl.LABEL_ATTRIBUTE_NAME, null, false);

        // MLC 06/30/07 BEGIN:
        //        GenericNodeLabelCalculator nodeLabelCalculator = new GenericNodeLabelCalculator("HyperEdge ID Label",
        //                                                                                        passThroughMapping);
        Calculator nodeLabelCalculator = new BasicCalculator("HyperEdge ID Label",
                                                             passThroughMapping,
                                                             VisualPropertyType.NODE_LABEL);
        // MLC 06/30/07 END.
        // nac.setNodeLabelCalculator(nodeLabelCalculator);
        nac.setCalculator(nodeLabelCalculator);
    }

    /**
     * create mappings for Node shape
     * @param nac
     */
    private void createNodeShape(NodeAppearanceCalculator nac) {
        // The first argument to the DiscreteMapping constructor seems
        // to no longer define the default. The default appearance
        // from the NodeAppearanceCalculator must be used instead:
        // MLC 05/10/07 BEGIN:
        //        DiscreteMapping discreteMapping = new DiscreteMapping(new Byte(ShapeNodeRealizer.ELLIPSE),
        //                                                              HyperEdgeImpl.ENTITY_TYPE_ATTRIBUTE_NAME,
        //                                                              ObjectMapping.NODE_MAPPING);
        //        discreteMapping.putMapValue(HyperEdgeImpl.ENTITY_TYPE_REGULAR_NODE_VALUE,
        //                                    new Byte(ShapeNodeRealizer.ELLIPSE));
        //        discreteMapping.putMapValue(HyperEdgeImpl.ENTITY_TYPE_CONNECTOR_NODE_VALUE,
        //                                    new Byte(ShapeNodeRealizer.RECT));
        DiscreteMapping discreteMapping = new DiscreteMapping(NodeShape.ELLIPSE,
                                                              HyperEdgeImpl.ENTITY_TYPE_ATTRIBUTE_NAME,
                                                              ObjectMapping.NODE_MAPPING);
        discreteMapping.putMapValue(HyperEdgeImpl.ENTITY_TYPE_REGULAR_NODE_VALUE,
                                    NodeShape.ELLIPSE);
        discreteMapping.putMapValue(HyperEdgeImpl.ENTITY_TYPE_CONNECTOR_NODE_VALUE,
                                    NodeShape.RECT);

        // MLC 05/10/07 END.	
        // MLC 06/30/07 BEGIN:
        //        Calculator nodeShapeCalculator = new GenericNodeShapeCalculator("HyperEdge Node Type Shape Calculator",
        //                                                                        discreteMapping);
        //        nac.getDefaultAppearance().setNodeShape(NodeShape.ELLIPSE);
        Calculator nodeShapeCalculator = new BasicCalculator("HyperEdge Node Type Shape Calculator",
                                                             discreteMapping,
                                                             VisualPropertyType.NODE_SHAPE);
        nac.getDefaultAppearance()
           .set(VisualPropertyType.NODE_SHAPE, NodeShape.ELLIPSE);
        // MLC 06/30/07 END.
        nac.setCalculator(nodeShapeCalculator);
    }

    private void createNodeSize(NodeAppearanceCalculator nac) {
        // The first argument to the DiscreteMapping constructor seems
        // to no longer define the default. The default appearance
        // from the NodeAppearanceCalculator must be used instead:
        // a -1.0 means compute size in the standard way.
        DiscreteMapping discreteMapping = new DiscreteMapping(new Double(-1.0),
                                                              HyperEdgeImpl.ENTITY_TYPE_ATTRIBUTE_NAME,
                                                              ObjectMapping.NODE_MAPPING);

        // discreteMapping.putMapValue(HyperEdgeImpl.ENTITY_TYPE_REGULAR_NODE_VALUE,
        //                            new Double(-1.0)); // default
        discreteMapping.putMapValue(HyperEdgeImpl.ENTITY_TYPE_CONNECTOR_NODE_VALUE,
                                    new Double(CONNECTOR_NODE_SIZE));

        // MLC 06/30/07 BEGIN:
        //        Calculator nodeSizeCalculator = new GenericNodeUniformSizeCalculator("HyperEdge Node Uniform Size Calculator",
        //                                                                             discreteMapping);
        // &&&& IS NODE_SIZE CORRECT FOR uniform size?:
        Calculator nodeSizeCalculator = new BasicCalculator("HyperEdge Node Uniform Size Calculator",
                                                            discreteMapping,
                                                            VisualPropertyType.NODE_SIZE);
        // MLC 06/30/07 END.
        //        Calculator nodeWidthCalculator  = new GenericNodeWidthCalculator("HyperEdge Node Type Width Calculator",
        //                                                                         discreteMapping);
        //        Calculator nodeHeightCalculator = new GenericNodeHeightCalculator("HyperEdge Node Type Height Calculator",
        //                                                                          discreteMapping);
        // MLC 01/15/07:
        // HEUtils.log("Set nodeSizeCalculator to " + nodeSizeCalculator);
        nac.setCalculator(nodeSizeCalculator);
        //        nac.setCalculator(nodeWidthCalculator);
        //        nac.setCalculator(nodeHeightCalculator);
    }

    /**
     * create mappings for Node color
     * @param nac
     */
    private void createNodeColor(NodeAppearanceCalculator nac) {
        // The first argument to the DiscreteMapping constructor seems
        // to no longer define the default. The default appearance
        // from the NodeAppearanceCalculator must be used instead:
        DiscreteMapping discreteMapping = new DiscreteMapping(Color.WHITE,
                                                              HyperEdgeImpl.ENTITY_TYPE_ATTRIBUTE_NAME,
                                                              ObjectMapping.NODE_MAPPING);

        discreteMapping.putMapValue(HyperEdgeImpl.ENTITY_TYPE_REGULAR_NODE_VALUE,
                                    Color.WHITE);
        discreteMapping.putMapValue(HyperEdgeImpl.ENTITY_TYPE_CONNECTOR_NODE_VALUE,
                                    Color.WHITE);

        // MLC 06/30/07 BEGIN:
        //        Calculator nodeColorCalculator = new GenericNodeFillColorCalculator("HyperEdge Node Fill Color Calculator",
        //                                                                            discreteMapping);
        //        nac.getDefaultAppearance().setFillColor(Color.WHITE);
        Calculator nodeColorCalculator = new BasicCalculator("HyperEdge Node Fill Color Calculator",
                                                             discreteMapping,
                                                             VisualPropertyType.NODE_FILL_COLOR);
        nac.getDefaultAppearance()
           .set(VisualPropertyType.NODE_FILL_COLOR, Color.WHITE);
        // MLC 06/30/07 END.
        nac.setCalculator(nodeColorCalculator);
    }
}
