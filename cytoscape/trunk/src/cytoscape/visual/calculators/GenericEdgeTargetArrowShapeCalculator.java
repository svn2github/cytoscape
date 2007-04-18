package cytoscape.visual.calculators;

import cytoscape.visual.VisualPropertyType;

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
    public GenericEdgeTargetArrowShapeCalculator(String name, ObjectMapping m,
        VisualPropertyType type) {
        super(name, m, type);
    }

    /**
     * Creates a new GenericEdgeTargetArrowShapeCalculator object.
     *
     * @param name DOCUMENT ME!
     * @param props DOCUMENT ME!
     * @param baseKey DOCUMENT ME!
     * @param type DOCUMENT ME!
     */
    public GenericEdgeTargetArrowShapeCalculator(String name, Properties props,
        String baseKey, VisualPropertyType type) {
        super(name, props, baseKey, type);
    }
}
