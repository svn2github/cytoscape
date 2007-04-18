package cytoscape.visual.calculators;

import static cytoscape.visual.VisualPropertyType.EDGE_SRCARROW_COLOR;

import cytoscape.visual.mappings.ObjectMapping;

import java.util.Properties;


/**
 * DOCUMENT ME!
 *
 * @author $author$
  */
public class GenericEdgeSourceArrowColorCalculator
    extends AbstractEdgeArrowColorCalculator {
    /**
     * Creates a new GenericEdgeSourceArrowColorCalculator object.
     *
     * @param name DOCUMENT ME!
     * @param m DOCUMENT ME!
     */
    public GenericEdgeSourceArrowColorCalculator(String name, ObjectMapping m) {
        super(name, m, EDGE_SRCARROW_COLOR);
    }

    /**
     * Creates a new GenericEdgeSourceArrowColorCalculator object.
     *
     * @param name DOCUMENT ME!
     * @param props DOCUMENT ME!
     * @param baseKey DOCUMENT ME!
     */
    public GenericEdgeSourceArrowColorCalculator(String name, Properties props,
        String baseKey) {
        super(name, props, baseKey, EDGE_SRCARROW_COLOR);
    }
}
