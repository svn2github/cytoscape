package cytoscape.visual.calculators;

import cytoscape.visual.Line;
import static cytoscape.visual.VisualPropertyType.NODE_LINETYPE;

import cytoscape.visual.mappings.ObjectMapping;

import java.util.Properties;


/**
 * DOCUMENT ME!
 *
 * @author $author$
  */
public class GenericNodeLineWidthCalculator extends AbstractNodeLineCalculator {
    /**
     * Creates a new GenericNodeLineWidthCalculator object.
     *
     * @param name DOCUMENT ME!
     * @param m DOCUMENT ME!
     */
    public GenericNodeLineWidthCalculator(String name, ObjectMapping m) {
        super(name, m, Line.class, NODE_LINETYPE);
    }

    /**
     * Creates a new GenericNodeLineWidthCalculator object.
     *
     * @param name DOCUMENT ME!
     * @param props DOCUMENT ME!
     * @param baseKey DOCUMENT ME!
     */
    public GenericNodeLineWidthCalculator(String name, Properties props,
        String baseKey) {
        super(name, props, baseKey, NODE_LINETYPE);
    }
}
