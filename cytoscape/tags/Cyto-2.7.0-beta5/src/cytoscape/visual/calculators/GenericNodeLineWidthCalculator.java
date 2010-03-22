package cytoscape.visual.calculators;

import cytoscape.CyNetwork;
import cytoscape.visual.parsers.FloatParser;
import static cytoscape.visual.VisualPropertyType.NODE_LINE_WIDTH;

import cytoscape.visual.mappings.ObjectMapping;

import giny.model.Node;

import java.util.Properties;


/**
 * This class exists ONLY to support legacy file formats. A VERY BAD PERSON
 * decided to use the class name to identify calculators in property files,
 * thus forever forcing us to keep these classes around.  
 *
 * <b>DO NOT USE THIS CLASS!!!</b>
  */
class GenericNodeLineWidthCalculator extends BasicCalculator {
    /**
     * Creates a new GenericNodeLineWidthCalculator object.
     *
     * @param name DOCUMENT ME!
     * @param m DOCUMENT ME!
     */
    GenericNodeLineWidthCalculator(String name, ObjectMapping m) {
        super(name, m, NODE_LINE_WIDTH);
    }

    /**
     * Creates a new GenericNodeLineWidthCalculator object.
     *
     * @param name DOCUMENT ME!
     * @param props DOCUMENT ME!
     * @param baseKey DOCUMENT ME!
     */
    GenericNodeLineWidthCalculator(String name, Properties props, String baseKey) {
        super(name, props, baseKey, NODE_LINE_WIDTH);
    }
    
}
