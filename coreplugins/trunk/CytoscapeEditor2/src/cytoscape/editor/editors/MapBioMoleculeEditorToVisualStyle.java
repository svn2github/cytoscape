/*
 * Created on Sep 19, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package cytoscape.editor.editors;

import java.awt.Color;

import cytoscape.Cytoscape;
import cytoscape.data.Semantics;
import cytoscape.visual.Arrow;
import cytoscape.visual.CalculatorCatalog;
import cytoscape.visual.EdgeAppearanceCalculator;
import cytoscape.visual.GlobalAppearanceCalculator;
import cytoscape.visual.NodeAppearanceCalculator;
import cytoscape.visual.ShapeNodeRealizer;
import cytoscape.visual.VisualMappingManager;
import cytoscape.visual.VisualStyle;
import cytoscape.visual.calculators.GenericEdgeArrowCalculator;
import cytoscape.visual.calculators.GenericNodeColorCalculator;
import cytoscape.visual.calculators.GenericNodeLabelCalculator;
import cytoscape.visual.calculators.GenericNodeShapeCalculator;
import cytoscape.visual.calculators.NodeColorCalculator;
import cytoscape.visual.calculators.NodeShapeCalculator;
import cytoscape.visual.mappings.DiscreteMapping;
import cytoscape.visual.mappings.ObjectMapping;
import cytoscape.visual.mappings.PassThroughMapping;

/**
 * 
 * creates visual style that is to be used by the SimpleBioMoleculeEditor
 * @author ajk
 *
 */
public class MapBioMoleculeEditorToVisualStyle {

	public static final String BIOMOLECULE_VISUAL_STYLE = "SimpleBioMoleculeEditor";

	/**
     * Cytoscape Attribute:  Node Type.
     */
    public static final String NODE_TYPE =
            "NODE_TYPE";

    /**
     * Cytoscape Attribute:  Edge Type.
     */
    public static final String EDGE_TYPE =
            "EDGE_TYPE";
    
    public static final String ACTIVATION = "Activation";
    public static final String INHIBITION = "Inhibition";
    public static final String CATALYSIS = "Catalysis";

    /**
	     * Creates a New BioMolecule VizMapper.
	     * If an existing BioMolecule Viz Mapper already exists, we use it.
	     * Otherwise, we create a new one.
	     */
	    public VisualStyle createVizMapper() {

	        VisualMappingManager manager =
	                Cytoscape.getVisualMappingManager();
	        CalculatorCatalog catalog = manager.getCalculatorCatalog();

	        VisualStyle existingStyle = catalog.getVisualStyle
	                (BIOMOLECULE_VISUAL_STYLE);
	        
	        System.out.println(
	        		"Got visual style for " + BIOMOLECULE_VISUAL_STYLE + " = " + 
	        		existingStyle);

	        if (existingStyle != null) {
//                System.out.println("Got existing visual style: " + existingStyle);
                return null;
	        } else {
	            VisualStyle bpVisualStyle = new VisualStyle(BIOMOLECULE_VISUAL_STYLE);
	            // AJK: 03/29/06 define fields of visual style 
	            System.out.println("defining visual style: " + bpVisualStyle);
	            defineVisualStyle (bpVisualStyle, manager, catalog);
	            manager.setVisualStyle(bpVisualStyle);

	            //  The visual style must be added to the Global Catalog
	            //  in order for it to be written out to vizmap.props upon user exit
	            System.out.println("Adding visual style " + bpVisualStyle 
	            		+ " to catalog " + catalog);
	            catalog.addVisualStyle(bpVisualStyle);
	            
	            // for debugging
//	    		VisualStyle vizStyle = catalog.getVisualStyle(BIOMOLECULE_VISUAL_STYLE);
//	    		System.out.println ("Got visual Style from catalog: " + catalog 
//	    				+ " = " + vizStyle);
	    		
	            // try setting the visual style to BioMolecule
	            Cytoscape.getDesktop().setVisualStyle(bpVisualStyle);
	            return bpVisualStyle;
	        }
	    }
	    
	    /**
	     * Create the mappings for Node shape, color, and Edge target arrow, line type
	     * for this visual style
	     * @param bpVisualStyle
	     * @param manager
	     * @param catalog
	     */
	    public void defineVisualStyle (VisualStyle bpVisualStyle, VisualMappingManager manager,
	    	CalculatorCatalog catalog)
	    {
            NodeAppearanceCalculator nac = new NodeAppearanceCalculator();
            nac.setDefaultNodeLabelColor(Color.BLACK);
            EdgeAppearanceCalculator eac = new EdgeAppearanceCalculator();
            GlobalAppearanceCalculator gac = new GlobalAppearanceCalculator();
            gac.setDefaultBackgroundColor(new Color (204,204,255));

            createNodeShape(nac);
            createNodeLabel(nac);
            createNodeColor(nac);
            createTargetArrows(eac);

            bpVisualStyle.setNodeAppearanceCalculator(nac);
            bpVisualStyle.setEdgeAppearanceCalculator(eac);
            bpVisualStyle.setGlobalAppearanceCalculator(gac);
     	
	    }

	    /**
	     * create mappings for TargetArrows for edges
	     * @param eac
	     */
	    private void createTargetArrows(EdgeAppearanceCalculator eac) {
	    	
	        DiscreteMapping discreteMapping = new DiscreteMapping
            (Arrow.NONE,
                    EDGE_TYPE,
                    ObjectMapping.EDGE_MAPPING);

	    	discreteMapping.putMapValue(ACTIVATION, Arrow.BLACK_DELTA);
	    	discreteMapping.putMapValue(CATALYSIS, Arrow.BLACK_CIRCLE);
	        discreteMapping.putMapValue(INHIBITION, Arrow.BLACK_T);

	        GenericEdgeArrowCalculator edgeTargetArrowCalculator =
                new GenericEdgeArrowCalculator("SimpleBioMoleculeEditor target arrows",
                discreteMapping);
        eac.setEdgeTargetArrowCalculator(edgeTargetArrowCalculator);
        System.out.println ("Set edge target arrow calculator to " + edgeTargetArrowCalculator);

	    }

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
	        passThroughMapping.setControllingAttributeName
//            (Semantics.LABEL, null, false);
	        (Semantics.CANONICAL_NAME, null, false);
	
	        GenericNodeLabelCalculator nodeLabelCalculator =
	                new GenericNodeLabelCalculator("SimpleBioMoleculeEditor ID Label"
	                , passThroughMapping);
	        nac.setNodeLabelCalculator(nodeLabelCalculator);
	    }

	    
	    /**
	     * create mappings for Node shape
	     * @param nac
	     */
	    private void createNodeShape(NodeAppearanceCalculator nac) {
	        DiscreteMapping discreteMapping = new DiscreteMapping
	                (new Byte(ShapeNodeRealizer.RECT),
	                        NODE_TYPE,
	                        ObjectMapping.NODE_MAPPING);
	        
	            discreteMapping.putMapValue("biochemicalReaction",
	                    new Byte(ShapeNodeRealizer.ELLIPSE));
	            discreteMapping.putMapValue("catalyst",
	                    new Byte(ShapeNodeRealizer.ROUND_RECT));
	            discreteMapping.putMapValue("protein",
	                    new Byte(ShapeNodeRealizer.RECT));
	            discreteMapping.putMapValue("smallMolecule",
	                    new Byte(ShapeNodeRealizer.DIAMOND));

	            NodeShapeCalculator nodeShapeCalculator =
	                new GenericNodeShapeCalculator("SimpleBioMoleculeEditor Node Type Shape Calculator"
	                , discreteMapping);
	        nac.setNodeShapeCalculator(nodeShapeCalculator);
	    }

	    /**
	     * create mappings for Node color
	     * @param nac
	     */
	    private void createNodeColor(NodeAppearanceCalculator nac) {
	        DiscreteMapping discreteMapping = new DiscreteMapping
	                (Color.WHITE,
	                        NODE_TYPE,
	                        ObjectMapping.NODE_MAPPING);
	        
	            discreteMapping.putMapValue("biochemicalReaction",
	                    new Color (204, 0, 61));
	            discreteMapping.putMapValue("catalyst",
	                    new Color(0, 163, 0));
	            discreteMapping.putMapValue("protein",
	                    new Color (0, 102, 255));
	            discreteMapping.putMapValue("smallMolecule",
	                    new Color (193, 249, 36));

	            NodeColorCalculator nodeColorCalculator =
	                new GenericNodeColorCalculator("SimpleBioMoleculeEditor Node Color Calculator"
	                , discreteMapping);
	        nac.setNodeFillColorCalculator(nodeColorCalculator);
	    }

}
