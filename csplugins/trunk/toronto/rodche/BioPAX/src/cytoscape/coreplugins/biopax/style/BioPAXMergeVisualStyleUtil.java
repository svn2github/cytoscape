package cytoscape.coreplugins.biopax.style;

import cytoscape.coreplugins.biopax.BioPaxGraphReader;
import cytoscape.coreplugins.biopax.style.BioPaxVisualStyleUtil;
import cytoscape.visual.*;
import cytoscape.visual.calculators.Calculator;
import cytoscape.visual.calculators.BasicCalculator;
import cytoscape.visual.mappings.DiscreteMapping;
import cytoscape.visual.mappings.ObjectMapping;
import cytoscape.Cytoscape;

import java.awt.*;

public class BioPAXMergeVisualStyleUtil extends BioPaxVisualStyleUtil {
    public static final String BIOPAX_MERGE_SRC_FIRST = "1";
    public static final String BIOPAX_MERGE_SRC_SECOND = "2";
    public static final String BIOPAX_MERGE_SRC_MERGE = "M";
    public static final String BIOPAX_MODEL_STRING = "biopax.model.xml";
    public static final String DEFAULT_CHARSET = "UTF-8";
    public static final String BIOPAX_MERGE_SRC = "biopax.merge.src";

    public static final String BIOPAX_MERGE_VISUAL_STYLE =
                "Merge Specific " + BioPaxVisualStyleUtil.BIO_PAX_VISUAL_STYLE;

    private static final Color DEFAULT_NODE_BORDER_COLOR = new Color(0, 102, 102);

    public static VisualStyle getBioPAXMergeVisualStyle(Color src1_color, Color src2_color, Color merge_color) {
        VisualMappingManager manager = Cytoscape.getVisualMappingManager();
        CalculatorCatalog catalog = manager.getCalculatorCatalog();

        VisualStyle visualStyle = catalog.getVisualStyle(BIOPAX_MERGE_VISUAL_STYLE);

        if(visualStyle == null) {
            try {
                visualStyle = (VisualStyle) getBioPaxVisualStyle().clone();
            } catch (CloneNotSupportedException e) {
                visualStyle = getBioPaxVisualStyle();
            }

            NodeAppearanceCalculator nac = visualStyle.getNodeAppearanceCalculator();
            createNodeBorderColor(nac, src1_color, src2_color, merge_color);

            visualStyle.setName(BIOPAX_MERGE_VISUAL_STYLE);

            catalog.addVisualStyle(visualStyle);
        }
        
        return visualStyle;
    }

    private static void createNodeBorderColor(NodeAppearanceCalculator nac,
                                              Color src1_color, Color src2_color, Color merge_color) {
        DiscreteMapping discreteMapping = new DiscreteMapping(DEFAULT_NODE_BORDER_COLOR,
                                                              BIOPAX_MERGE_SRC,
                                                              ObjectMapping.NODE_MAPPING);

        discreteMapping.putMapValue(BIOPAX_MERGE_SRC_FIRST,  src1_color);
        discreteMapping.putMapValue(BIOPAX_MERGE_SRC_SECOND, src2_color);
        discreteMapping.putMapValue(BIOPAX_MERGE_SRC_MERGE, merge_color);

        // create and set node label calculator in node appearance calculator
        Calculator nodeColorCalculator= new BasicCalculator("Merge Specific BioPAX Node Border Color", discreteMapping,
                                                            VisualPropertyType.NODE_BORDER_COLOR);
        nac.setCalculator(nodeColorCalculator);

        // set default color
        nac.getDefaultAppearance().set(VisualPropertyType.NODE_BORDER_COLOR, DEFAULT_NODE_BORDER_COLOR);

    }
}
