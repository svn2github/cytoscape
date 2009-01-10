package org.cytoscape.coreplugin.cpath2.cytoscape;

import cytoscape.visual.*;
import cytoscape.visual.calculators.Calculator;
import cytoscape.visual.calculators.BasicCalculator;
import cytoscape.visual.mappings.DiscreteMapping;
import cytoscape.visual.mappings.ObjectMapping;
import cytoscape.visual.mappings.PassThroughMapping;
import cytoscape.Cytoscape;
import cytoscape.data.Semantics;
import java.awt.*;

import org.mskcc.biopax_plugin.style.BioPaxVisualStyleUtil;
import org.mskcc.biopax_plugin.mapping.MapNodeAttributes;
import org.mskcc.biopax_plugin.util.biopax.BioPaxConstants;

/**
 * Binary SIF Visual Style.
 *
 * @author Ethan Cerami.
 */
public class BinarySifVisualStyleUtil {
    public final static String BINARY_SIF_VISUAL_STYLE = "Binary_SIF_Version_1";
    public final static String BINARY_NETWORK = "BINARY_NETWORK";
    public final static String COMPONENT_OF = "COMPONENT_OF";
    public final static String COMPONENT_IN_SAME = "IN_SAME_COMPONENT";
    public final static String SEQUENTIAL_CATALYSIS = "SEQUENTIAL_CATALYSIS";
    public final static String CONTROLS_STATE_CHANGE = "STATE_CHANGE";
    public final static String CONTROLS_METABOLIC_CHANGE = "METABOLIC_CATALYSIS";
    public final static String PARTICIPATES_CONVERSION = "REACTS_WITH";
    public final static String PARTICIPATES_INTERACTION = "INTERACTS_WITH";
    public final static String CO_CONTROL_INDEPENDENT_SIMILAR = "CO_CONTROL_INDEPENDENT_SIMILAR";
    public final static String CO_CONTROL_INDEPENDENT_ANTI = "CO_CONTROL_INDEPENDENT_ANTI";
    public final static String CO_CONTROL_DEPENDENT_SIMILAR = "CO_CONTROL_DEPENDENT_SIMILAR";
    public final static String CO_CONTROL_DEPENDENT_ANTI = "CO_CONTROL_DEPENDENT_ANTI";
    private final static String COMPLEX = "Complex"; 

    /**
	 * Constructor.
	 * If an existing BioPAX Viz Mapper already exists, we use it.
	 * Otherwise, we create a new one.
	 *
	 * @return VisualStyle Object.
	 */
	public static VisualStyle getVisualStyle() {
		VisualMappingManager manager = Cytoscape.getVisualMappingManager();
		CalculatorCatalog catalog = manager.getCalculatorCatalog();

        VisualStyle visualStyle = catalog.getVisualStyle(BINARY_SIF_VISUAL_STYLE);
        if (visualStyle == null) {
            visualStyle = new VisualStyle(BINARY_SIF_VISUAL_STYLE);

            NodeAppearanceCalculator nac = new NodeAppearanceCalculator();

            //  set node opacity
            nac.getDefaultAppearance().set(cytoscape.visual.VisualPropertyType.NODE_OPACITY, 125);
            //  unlock node size
            nac.setNodeSizeLocked(false);

            createNodeShapes(nac);
            createNodeColors(nac);
            createNodeLabel(nac);

            EdgeAppearanceCalculator eac = new EdgeAppearanceCalculator();
            eac.getDefaultAppearance().set(cytoscape.visual.VisualPropertyType.EDGE_LINE_WIDTH,
                    4.0);
            createEdgeColor (eac);
            createDirectedEdges (eac);

            visualStyle.setNodeAppearanceCalculator(nac);
            visualStyle.setEdgeAppearanceCalculator(eac);
            GlobalAppearanceCalculator gac = new GlobalAppearanceCalculator();
            gac.setDefaultBackgroundColor(Color.WHITE);
            visualStyle.setGlobalAppearanceCalculator(gac);

            //  The visual style must be added to the Global Catalog
            //  in order for it to be written out to vizmap.props upon user exit
            catalog.addVisualStyle(visualStyle);
        }
        return visualStyle;
    }

    private static void createNodeShapes(NodeAppearanceCalculator nac) {
        //  Default shape is an ellipse.
        nac.getDefaultAppearance().set(VisualPropertyType.NODE_SHAPE, NodeShape.ELLIPSE);

        //  Complexes are Hexagons.
        DiscreteMapping discreteMapping = new DiscreteMapping(NodeShape.ELLIPSE,
            MapNodeAttributes.BIOPAX_ENTITY_TYPE, ObjectMapping.NODE_MAPPING);
        discreteMapping.putMapValue(COMPLEX, NodeShape.HEXAGON);
        Calculator nodeShapeCalculator = new BasicCalculator("Node Shape",
            discreteMapping, VisualPropertyType.NODE_SHAPE);
		nac.setCalculator(nodeShapeCalculator);        
    }

    private static void createNodeColors(NodeAppearanceCalculator nac) {
        Color color = new Color (255, 153, 153);
        nac.getDefaultAppearance().set(VisualPropertyType.NODE_FILL_COLOR, color);

        //  Complexes are a Different Color.
        Color lightBlue = new Color (153, 153, 255);
        DiscreteMapping discreteMapping = new DiscreteMapping(color,
            MapNodeAttributes.BIOPAX_ENTITY_TYPE, ObjectMapping.NODE_MAPPING);
        discreteMapping.putMapValue(COMPLEX, lightBlue);

        Calculator nodeShapeCalculator = new BasicCalculator("Node Color",
            discreteMapping, VisualPropertyType.NODE_FILL_COLOR);
		nac.setCalculator(nodeShapeCalculator);
    }

    private static void createEdgeColor(EdgeAppearanceCalculator eac) {
		// create a discrete mapper, for mapping biopax node type
		// to a particular node color
		DiscreteMapping discreteMapping = new DiscreteMapping(Color.BLACK,
            Semantics.INTERACTION, ObjectMapping.EDGE_MAPPING);

        discreteMapping.putMapValue(PARTICIPATES_CONVERSION, Color.decode("#ccc1da"));
        discreteMapping.putMapValue(PARTICIPATES_INTERACTION, Color.decode("#7030a0"));
        discreteMapping.putMapValue(CONTROLS_STATE_CHANGE, Color.decode("#0070c0"));
        discreteMapping.putMapValue(CONTROLS_METABOLIC_CHANGE, Color.decode("#00b0f0"));
        discreteMapping.putMapValue(SEQUENTIAL_CATALYSIS, Color.decode("#7f7f7f"));        
        discreteMapping.putMapValue(CO_CONTROL_DEPENDENT_ANTI, Color.decode("#ff0000"));
        discreteMapping.putMapValue(CO_CONTROL_INDEPENDENT_ANTI, Color.decode("#fd95a6"));
        discreteMapping.putMapValue(CO_CONTROL_DEPENDENT_SIMILAR, Color.decode("#00b050"));
        discreteMapping.putMapValue(CO_CONTROL_INDEPENDENT_SIMILAR, Color.decode("#92d050"));
        discreteMapping.putMapValue(COMPONENT_IN_SAME, Color.decode("#ffff00"));
        discreteMapping.putMapValue(COMPONENT_OF, Color.decode("#ffc000"));

        // create and set edge label calculator in edge appearance calculator
		Calculator edgeColorCalculator = new BasicCalculator("Edge Color",
            discreteMapping, VisualPropertyType.EDGE_COLOR);
		eac.setCalculator(edgeColorCalculator);

		// set default color
		eac.getDefaultAppearance().set(cytoscape.visual.VisualPropertyType.EDGE_COLOR,
               Color.BLACK);
	}

	private static void createDirectedEdges(EdgeAppearanceCalculator eac) {
		DiscreteMapping discreteMapping = new DiscreteMapping(ArrowShape.NONE,
            Semantics.INTERACTION, ObjectMapping.EDGE_MAPPING);

        discreteMapping.putMapValue(COMPONENT_OF, ArrowShape.ARROW);
        discreteMapping.putMapValue(CONTROLS_STATE_CHANGE, ArrowShape.ARROW);
        discreteMapping.putMapValue(CONTROLS_METABOLIC_CHANGE, ArrowShape.ARROW);
        discreteMapping.putMapValue(SEQUENTIAL_CATALYSIS, ArrowShape.ARROW);

        // create and set edge label calculator in edge appearance calculator
		Calculator edgeColorCalculator = new BasicCalculator("Edge Source Arrow Shape",
            discreteMapping, VisualPropertyType.EDGE_TGTARROW_SHAPE);
		eac.setCalculator(edgeColorCalculator);

		// set default color
		eac.getDefaultAppearance().set(cytoscape.visual.VisualPropertyType.EDGE_TGTARROW_SHAPE,
               ArrowShape.NONE);
	}

    private static void createNodeLabel(NodeAppearanceCalculator nac) {
		// create pass through mapper for node labels
		PassThroughMapping passThroughMapping = new PassThroughMapping("",
		                                                               ObjectMapping.NODE_MAPPING);
		passThroughMapping.setControllingAttributeName(BioPaxVisualStyleUtil.BIOPAX_NODE_LABEL,
                null, false);

		// create and set node label calculator in node appearance calculator
		Calculator nodeLabelCalculator = new BasicCalculator("BioPAX Node Label",
		                                                     passThroughMapping,
		                                                     VisualPropertyType.NODE_LABEL);
		nac.setCalculator(nodeLabelCalculator);
	}
}
