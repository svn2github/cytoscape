package cytoscape.visual.calculators;

import static cytoscape.visual.VisualPropertyType.EDGE_LABEL_OPACITY;

import java.util.Properties;

import cytoscape.visual.mappings.ObjectMapping;
import cytoscape.visual.parsers.FloatParser;

public class GenericEdgeLabelOpacityCalculator extends EdgeCalculator {
    /**
     * Creates a new GenericNodeFontSizeCalculator object.
     *
     * @param name DOCUMENT ME!
     * @param m DOCUMENT ME!
     */
    public GenericEdgeLabelOpacityCalculator(String name, ObjectMapping m) {
        super(name, m, Number.class, EDGE_LABEL_OPACITY);
    }

    /**
     * Creates a new GenericNodeFontSizeCalculator object.
     *
     * @param name DOCUMENT ME!
     * @param props DOCUMENT ME!
     * @param baseKey DOCUMENT ME!
     */
    public GenericEdgeLabelOpacityCalculator(String name, Properties props,
        String baseKey) {
        super(name, props, baseKey, new FloatParser(), new Integer(255),
            EDGE_LABEL_OPACITY);
    }
}
