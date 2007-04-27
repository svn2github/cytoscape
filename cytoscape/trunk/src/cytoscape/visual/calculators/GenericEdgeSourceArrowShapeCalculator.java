package cytoscape.visual.calculators;

import static cytoscape.visual.VisualPropertyType.EDGE_SRCARROW_SHAPE;

import cytoscape.visual.mappings.ObjectMapping;

import java.util.Properties;


/**
 * DOCUMENT ME!
 *
 * @author $author$
  */
public class GenericEdgeSourceArrowShapeCalculator
    extends AbstractEdgeArrowShapeCalculator {
    /**
     * Creates a new GenericEdgeSourceArrowShapeCalculator object.
     *
     * @param name DOCUMENT ME!
     * @param m DOCUMENT ME!
     */
    public GenericEdgeSourceArrowShapeCalculator(String name, ObjectMapping m) {
        super(name, m, EDGE_SRCARROW_SHAPE);
    }

    /**
     * Creates a new GenericEdgeSourceArrowShapeCalculator object.
     *
     * @param name DOCUMENT ME!
     * @param props DOCUMENT ME!
     * @param baseKey DOCUMENT ME!
     */
    public GenericEdgeSourceArrowShapeCalculator(String name, Properties props, String baseKey) {
        super(name, props, baseKey, EDGE_SRCARROW_SHAPE);
    }
}
