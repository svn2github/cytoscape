package cytoscape.visual.calculators;

import cytoscape.CyNetwork;

import cytoscape.visual.Appearance;
import static cytoscape.visual.VisualPropertyType.EDGE_LINE_WIDTH;

import cytoscape.visual.mappings.ObjectMapping;

import cytoscape.visual.parsers.FloatParser;

import giny.model.Edge;

import java.util.Properties;


/**
 * @deprecated Use BasicCalculator(VisualPropertyType,...) instead. 
 * Will be hidden, although probably not removed, in 5/2008.
  */
@Deprecated
public class GenericEdgeLineWidthCalculator extends EdgeCalculator {
    /**
     * Creates a new GenericEdgeLineWidthCalculator object.
     *
     * @param name DOCUMENT ME!
     * @param m DOCUMENT ME!
     */
    public GenericEdgeLineWidthCalculator(String name, ObjectMapping m) {
        super(name, m, Float.class, EDGE_LINE_WIDTH);
    }

    /**
    * Creates a new GenericEdgeLineWidthCalculator object.
    *
    * @param name DOCUMENT ME!
    * @param m DOCUMENT ME!
    * @param c DOCUMENT ME!
    */
    public GenericEdgeLineWidthCalculator(String name, ObjectMapping m, Class c) {
        super(name, m, c, EDGE_LINE_WIDTH);
    }

    /**
     * Creates a new GenericEdgeLineWidthCalculator object.
     *
     * @param name DOCUMENT ME!
     * @param props DOCUMENT ME!
     * @param baseKey DOCUMENT ME!
     */
    public GenericEdgeLineWidthCalculator(String name, Properties props,
        String baseKey) {
        super(name, props, baseKey, new FloatParser(), new Float(0), EDGE_LINE_WIDTH);
    }

}
