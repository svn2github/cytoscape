package cytoscape.visual.calculators;

import cytoscape.CyNetwork;
import cytoscape.visual.parsers.FloatParser;
import static cytoscape.visual.VisualPropertyType.NODE_LINE_WIDTH;

import cytoscape.visual.mappings.ObjectMapping;

import giny.model.Node;

import java.util.Properties;


/**
 * DOCUMENT ME!
 *
 * @author $author$
  */
public class GenericNodeLineWidthCalculator extends NodeCalculator {
    /**
     * Creates a new GenericNodeLineWidthCalculator object.
     *
     * @param name DOCUMENT ME!
     * @param m DOCUMENT ME!
     */
    public GenericNodeLineWidthCalculator(String name, ObjectMapping m) {
        super(name, m, Float.class, NODE_LINE_WIDTH);
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
        super(name, props, baseKey, new FloatParser(), Float.class, NODE_LINE_WIDTH);
    }
    
}
