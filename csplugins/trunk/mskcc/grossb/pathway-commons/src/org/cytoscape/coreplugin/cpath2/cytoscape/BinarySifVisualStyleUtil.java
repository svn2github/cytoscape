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
    public final static String BINARY_SIF_VISUAL_STYLE = "Binary_SIF";

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
        //  If the BioPAX Visual Style already exists, use this one instead.
        //  The user may have tweaked the out-of-the box mapping, and we don't
        //  want to over-ride these tweaks.
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
            createEdgeColor (eac);

            visualStyle.setNodeAppearanceCalculator(nac);
            visualStyle.setEdgeAppearanceCalculator(eac);
            GlobalAppearanceCalculator gac = new GlobalAppearanceCalculator();
            gac.setDefaultBackgroundColor(new Color (204, 204, 255));
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

        discreteMapping.putMapValue("Component.InSame", Color.RED);
        discreteMapping.putMapValue("Component.Of", Color.BLUE);
        discreteMapping.putMapValue("Participates.Conversion", Color.GREEN);
        discreteMapping.putMapValue("interaction", Color.YELLOW);

        // create and set edge label calculator in edge appearance calculator
		Calculator edgeColorCalculator = new BasicCalculator("Edge Color",
            discreteMapping, VisualPropertyType.EDGE_COLOR);
		eac.setCalculator(edgeColorCalculator);

		// set default color
		eac.getDefaultAppearance().set(cytoscape.visual.VisualPropertyType.EDGE_COLOR,
               Color.BLACK);
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
