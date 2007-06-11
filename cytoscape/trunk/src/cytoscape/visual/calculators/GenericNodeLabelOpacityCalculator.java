package cytoscape.visual.calculators;

import static cytoscape.visual.VisualPropertyType.NODE_LABEL_OPACITY;

import java.util.Properties;

import cytoscape.visual.mappings.ObjectMapping;
import cytoscape.visual.parsers.FloatParser;

public class GenericNodeLabelOpacityCalculator extends NodeCalculator {
    /**
     * Creates a new GenericNodeFontSizeCalculator object.
     *
     * @param name DOCUMENT ME!
     * @param m DOCUMENT ME!
     */
    public GenericNodeLabelOpacityCalculator(String name, ObjectMapping m) {
        super(name, m, Number.class, NODE_LABEL_OPACITY);
    }

    /**
     * Creates a new GenericNodeFontSizeCalculator object.
     *
     * @param name DOCUMENT ME!
     * @param props DOCUMENT ME!
     * @param baseKey DOCUMENT ME!
     */
    public GenericNodeLabelOpacityCalculator(String name, Properties props,
        String baseKey) {
        super(name, props, baseKey, new FloatParser(), new Integer(255),
            NODE_LABEL_OPACITY);
    }
}
