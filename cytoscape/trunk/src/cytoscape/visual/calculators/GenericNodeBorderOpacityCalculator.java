package cytoscape.visual.calculators;

import static cytoscape.visual.VisualPropertyType.NODE_BORDER_OPACITY;

import java.util.Properties;

import cytoscape.visual.mappings.ObjectMapping;
import cytoscape.visual.parsers.FloatParser;

public class GenericNodeBorderOpacityCalculator extends NodeCalculator {
    /**
     * Creates a new GenericNodeFontSizeCalculator object.
     *
     * @param name DOCUMENT ME!
     * @param m DOCUMENT ME!
     */
    public GenericNodeBorderOpacityCalculator(String name, ObjectMapping m) {
        super(name, m, Number.class, NODE_BORDER_OPACITY);
    }

    /**
     * Creates a new GenericNodeFontSizeCalculator object.
     *
     * @param name DOCUMENT ME!
     * @param props DOCUMENT ME!
     * @param baseKey DOCUMENT ME!
     */
    public GenericNodeBorderOpacityCalculator(String name, Properties props,
        String baseKey) {
        super(name, props, baseKey, new FloatParser(), new Integer(255),
            NODE_BORDER_OPACITY);
    }
}
