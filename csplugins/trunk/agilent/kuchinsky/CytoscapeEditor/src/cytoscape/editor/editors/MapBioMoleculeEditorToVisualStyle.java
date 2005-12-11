/*
 * Created on Sep 19, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package cytoscape.editor.editors;

import java.awt.Color;

import cytoscape.Cytoscape;
import cytoscape.visual.Arrow;
import cytoscape.visual.CalculatorCatalog;
import cytoscape.visual.EdgeAppearanceCalculator;
import cytoscape.visual.GlobalAppearanceCalculator;
import cytoscape.visual.NodeAppearanceCalculator;
import cytoscape.visual.ShapeNodeRealizer;
import cytoscape.visual.VisualMappingManager;
import cytoscape.visual.VisualStyle;
import cytoscape.visual.calculators.GenericEdgeLabelCalculator;
import cytoscape.visual.calculators.GenericNodeColorCalculator;
import cytoscape.visual.calculators.GenericNodeLabelCalculator;
import cytoscape.visual.calculators.GenericNodeShapeCalculator;
import cytoscape.visual.calculators.NodeColorCalculator;
import cytoscape.visual.calculators.NodeShapeCalculator;
import cytoscape.visual.mappings.DiscreteMapping;
import cytoscape.visual.mappings.ObjectMapping;
import cytoscape.visual.mappings.PassThroughMapping;


/**
 * NOTE: THE CYTOSCAPE EDITOR FUNCTIONALITY IS STILL BEING EVOLVED AND IN A STATE OF TRANSITION TO A 
 * FULLY EXTENSIBLE EDITING FRAMEWORK FOR CYTOSCAPE VERSION 2.3.  
 * 
 * THE JAVADOC COMMENTS ARE OUT OF DATE IN MANY PLACES AND ARE BEING UPDATED.  
 * THE APIs WILL CHANGE AND THIS MAY IMPACT YOUR CODE IF YOU 
 * MAKE EXTENSIONS AT THIS POINT.  PLEASE CONTACT ME (mailto: allan_kuchinsky@agilent.com) 
 * IF YOU ARE INTENDING TO EXTEND THIS CODE AND I WILL WORK WITH YOU TO HELP MINIMIZE THE IMPACT TO YOUR CODE OF 
 * FUTURE CHANGES TO THE FRAMEWORK
 *
 * PLEASE SEE http://www.cytoscape.org/cgi-bin/moin.cgi/CytoscapeEditorFramework FOR 
 * DETAILS ON THE EDITOR FRAMEWORK AND PLANNED EVOLUTION FOR CYTOSCAPE VERSION 2.3.
 *
 */

/**
 * @author ajk
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class MapBioMoleculeEditorToVisualStyle {

	public static final String BIOMOLECULE_VISUAL_STYLE = "BioMoleculeEditor";

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

    /**
	     * Creates a New BioMolecule VizMapper.
	     * If an existing BioMolecule Viz Mapper already exists, we use it.
	     * Otherwise, we create a new one.
	     */
	    public void createVizMapper() {

	        VisualMappingManager manager =
	                Cytoscape.getDesktop().getVizMapManager();
	        CalculatorCatalog catalog = manager.getCalculatorCatalog();

	        VisualStyle existingStyle = catalog.getVisualStyle
	                (BIOMOLECULE_VISUAL_STYLE);

	        //  If the BioPAX Visual Style already exists, use this one instead.
	        //  The user may have tweaked the out-of-the box mapping, and we don't
	        //  want to over-ride these tweaks.
	        if (existingStyle != null) {
	            manager.setVisualStyle(existingStyle);
	        } else {
	            VisualStyle bpVisualStyle = new VisualStyle(BIOMOLECULE_VISUAL_STYLE);
	            NodeAppearanceCalculator nac = new NodeAppearanceCalculator();
	            nac.setDefaultNodeLabelColor(Color.BLACK);
	            EdgeAppearanceCalculator eac = new EdgeAppearanceCalculator();
	            GlobalAppearanceCalculator gac = new GlobalAppearanceCalculator();
	            gac.setDefaultBackgroundColor(new Color (204,204,255));

	            createNodeShape(nac);
	            createNodeLabel(nac);
	            createNodeColor(nac);
//	            createEdgeLabel(eac);
	            createTargetArrows(eac);

	            bpVisualStyle.setNodeAppearanceCalculator(nac);
	            bpVisualStyle.setEdgeAppearanceCalculator(eac);
	            bpVisualStyle.setGlobalAppearanceCalculator(gac);
	            manager.setVisualStyle(bpVisualStyle);

	            //  The visual style must be added to the Global Catalog
	            //  in order for it to be written out to vizmap.props upon user exit
	            catalog.addVisualStyle(bpVisualStyle);
	        }
	    }

	    private void createTargetArrows(EdgeAppearanceCalculator eac) {
	    	
	    	eac.setDefaultEdgeTargetArrow(Arrow.COLOR_DELTA);
	    	
	    }

	    private void createEdgeLabel(EdgeAppearanceCalculator eac) {
	        PassThroughMapping passThroughMapping = new PassThroughMapping("",
	                ObjectMapping.EDGE_MAPPING);
	        passThroughMapping.setControllingAttributeName
	                (EDGE_TYPE, null, false);

	        GenericEdgeLabelCalculator edgeLabelCalculator =
	                new GenericEdgeLabelCalculator("BioMoleculeEditor Edge Label Passthrough", passThroughMapping);
	        eac.setEdgeLabelCalculator(edgeLabelCalculator);
	    }

	    private void createNodeLabel(NodeAppearanceCalculator nac) {
	        PassThroughMapping passThroughMapping = new PassThroughMapping("",
	                ObjectMapping.NODE_MAPPING);
	        passThroughMapping.setControllingAttributeName
	                ("canonicalName", null, false);
 
	        GenericNodeLabelCalculator nodeLabelCalculator =
	                new GenericNodeLabelCalculator("BioMoleculeEditor ID Label"
	                , passThroughMapping);
	        nac.setNodeLabelCalculator(nodeLabelCalculator);
	    }

	    private void createNodeShape(NodeAppearanceCalculator nac) {
	        //  Create a New Discrete Mapper, for mapping BioPAX Type to Shape
	        DiscreteMapping discreteMapping = new DiscreteMapping
	                (new Byte(ShapeNodeRealizer.RECT),
	                        NODE_TYPE,
	                        ObjectMapping.NODE_MAPPING);
	        
	            discreteMapping.putMapValue("biochemicalReaction",
	                    new Byte(ShapeNodeRealizer.ELLIPSE));
	            discreteMapping.putMapValue("catalysis",
	                    new Byte(ShapeNodeRealizer.ROUND_RECT));
	            discreteMapping.putMapValue("protein",
	                    new Byte(ShapeNodeRealizer.RECT));
	            discreteMapping.putMapValue("smallMolecule",
	                    new Byte(ShapeNodeRealizer.DIAMOND));

	            NodeShapeCalculator nodeShapeCalculator =
	                new GenericNodeShapeCalculator("BioMoleculeEditor Node Type Shape Calculator"
	                , discreteMapping);
	        nac.setNodeShapeCalculator(nodeShapeCalculator);
	    }

	    private void createNodeColor(NodeAppearanceCalculator nac) {
	        //  Create a New Discrete Mapper, for mapping BioPAX Type to Shape
	        DiscreteMapping discreteMapping = new DiscreteMapping
	                (Color.WHITE,
	                        NODE_TYPE,
	                        ObjectMapping.NODE_MAPPING);
	        
	            discreteMapping.putMapValue("biochemicalReaction",
	                    new Color (204, 0, 61));
	            discreteMapping.putMapValue("catalysis",
	                    new Color(0, 163, 0));
	            discreteMapping.putMapValue("protein",
	                    new Color (0, 102, 255));
	            discreteMapping.putMapValue("smallMolecule",
	                    new Color (193, 249, 36));

	            NodeColorCalculator nodeColorCalculator =
	                new GenericNodeColorCalculator("BioMoleculeEditor Node Color Calculator"
	                , discreteMapping);
	        nac.setNodeFillColorCalculator(nodeColorCalculator);
	    }

}
