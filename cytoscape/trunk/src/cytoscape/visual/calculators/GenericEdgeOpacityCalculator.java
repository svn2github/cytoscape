package cytoscape.visual.calculators;

import static cytoscape.visual.VisualPropertyType.EDGE_OPACITY;

import java.util.Properties;

import cytoscape.visual.mappings.ObjectMapping;
import cytoscape.visual.parsers.DoubleParser;

public class GenericEdgeOpacityCalculator extends EdgeCalculator {
	 /**
     * Creates a new GenericNodeFontSizeCalculator object.
     *
     * @param name DOCUMENT ME!
     * @param m DOCUMENT ME!
     */
    public GenericEdgeOpacityCalculator(String name, ObjectMapping m) {
        super(name, m, Number.class, EDGE_OPACITY);
    }

    /**
     * Creates a new GenericNodeFontSizeCalculator object.
     *
     * @param name DOCUMENT ME!
     * @param props DOCUMENT ME!
     * @param baseKey DOCUMENT ME!
     */
    public GenericEdgeOpacityCalculator(String name, Properties props,
        String baseKey) {
        super(name, props, baseKey, new DoubleParser(), new Integer(255),
            EDGE_OPACITY);
    }
}
