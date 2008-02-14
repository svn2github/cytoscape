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

/**
 * Binary SIF Visual Style.
 *
 * @author Ethan Cerami.
 */
public class BinarySifVisualStyleUtil {
    public final static String BINARY_SIF_VISUAL_STYLE = "Binary_SIF_Version_1";
    public final static String BINARY_NETWORK = "BINARY_NETWORK";
    public final static String COMPONENT_OF = "Component_Of";
    public final static String COMPONENT_IN_SAME = "Component_InSame";
    public final static String SEQUENTIAL_CATALYSIS = "SequentialCatalysis";
    public final static String CONTROLS_STATE_CHANGE = "Controls_StateChange";
    public final static String CONTROLS_METABOLIC_CHANGE = "Controls_MetabolicChange";
    public final static String PARTICIPATES_CONVERSION = "Participates_Conversion";
    public final static String PARTICIPATES_INTERACTION = "Participates_Interaction";
    public final static String CO_CONTROL_INDEPENDENT_SIMILAR = "Co-Control-Indepedent_Similar";
    public final static String CO_CONTROL_INDEPENDENT_ANTI = "Co-Control_Dependent_Anti";
    public final static String CO_CONTROL_DEPENDENT_SIMILAR = "Co-Control_Dependent_Similar";
    public final static String CONTROL_DEPENDENT_ANTI = "Control_Dependent_Anti";

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
            Color color = new Color (255, 153, 153);
            nac.getDefaultAppearance().set(cytoscape.visual.VisualPropertyType.NODE_FILL_COLOR,
                    color);
            nac.getDefaultAppearance().set(cytoscape.visual.VisualPropertyType.NODE_SHAPE,
                    NodeShape.ELLIPSE);
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

	private static void createEdgeColor(EdgeAppearanceCalculator eac) {
		// create a discrete mapper, for mapping biopax node type
		// to a particular node color
		DiscreteMapping discreteMapping = new DiscreteMapping(Color.BLACK,
            Semantics.INTERACTION, ObjectMapping.EDGE_MAPPING);

        discreteMapping.putMapValue(COMPONENT_OF, Color.decode("#0000FF"));
        discreteMapping.putMapValue(COMPONENT_IN_SAME, Color.decode("#00FFFF"));
        discreteMapping.putMapValue(SEQUENTIAL_CATALYSIS, Color.decode("#808080"));
        discreteMapping.putMapValue(CONTROLS_STATE_CHANGE, Color.decode("#8B008B"));
        discreteMapping.putMapValue(CONTROLS_METABOLIC_CHANGE, Color.decode("#008000"));
        discreteMapping.putMapValue(PARTICIPATES_CONVERSION, Color.decode("#90EE90"));
        discreteMapping.putMapValue(PARTICIPATES_INTERACTION, Color.decode("#FF00FF"));
        discreteMapping.putMapValue(CO_CONTROL_INDEPENDENT_SIMILAR, Color.decode("#FFA500"));
        discreteMapping.putMapValue(CO_CONTROL_INDEPENDENT_ANTI, Color.decode("#FFC0CB"));
        discreteMapping.putMapValue(CO_CONTROL_DEPENDENT_SIMILAR, Color.decode("#FF0000"));
        discreteMapping.putMapValue(CONTROL_DEPENDENT_ANTI, Color.decode("#FFFF00"));

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
