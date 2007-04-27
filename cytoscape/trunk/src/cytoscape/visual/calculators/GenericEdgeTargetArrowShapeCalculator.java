package cytoscape.visual.calculators;

import static cytoscape.visual.VisualPropertyType.EDGE_TGTARROW_SHAPE;

import cytoscape.visual.mappings.ObjectMapping;

import java.util.Properties;


/**
 * DOCUMENT ME!
 *
 * @author $author$
  */
public class GenericEdgeTargetArrowShapeCalculator
    extends AbstractEdgeArrowShapeCalculator {
    /**
     * Creates a new GenericEdgeTargetArrowShapeCalculator object.
     *
     * @param name DOCUMENT ME!
     * @param m DOCUMENT ME!
     * @param type DOCUMENT ME!
     */
    public GenericEdgeTargetArrowShapeCalculator(String name, ObjectMapping m) {
        super(name, m, EDGE_TGTARROW_SHAPE);
    }

    /**
     * Creates a new GenericEdgeTargetArrowShapeCalculator object.
     *
     * @param name DOCUMENT ME!
     * @param props DOCUMENT ME!
     * @param baseKey DOCUMENT ME!
     * @param type DOCUMENT ME!
     */
    public GenericEdgeTargetArrowShapeCalculator(String name, Properties props, String baseKey ) {
        super(name, props, baseKey, EDGE_TGTARROW_SHAPE);
    }
}
