package cytoscape.visual.calculators;

import cytoscape.CyNetwork;

import cytoscape.visual.Appearance;
import static cytoscape.visual.VisualPropertyType.EDGE_LINE_WIDTH;

import cytoscape.visual.mappings.ObjectMapping;

import cytoscape.visual.parsers.FloatParser;

import giny.model.Edge;

import java.util.Properties;


/**
 * This class exists ONLY to support legacy file formats. A VERY BAD PERSON
 * decided to use the class name to identify calculators in property files,
 * thus forever forcing us to keep these classes around.  
 *
 * <b>DO NOT USE THIS CLASS!!!</b>
  */
class GenericEdgeLineWidthCalculator extends BasicCalculator {
    /**
     * Creates a new GenericEdgeLineWidthCalculator object.
     *
     * @param name DOCUMENT ME!
     * @param m DOCUMENT ME!
     */
    GenericEdgeLineWidthCalculator(String name, ObjectMapping m) {
        super(name, m, EDGE_LINE_WIDTH);
    }

    /**
    * Creates a new GenericEdgeLineWidthCalculator object.
    *
    * @param name DOCUMENT ME!
    * @param m DOCUMENT ME!
    * @param c DOCUMENT ME!
    */
    GenericEdgeLineWidthCalculator(String name, ObjectMapping m, Class c) {
        super(name, m, EDGE_LINE_WIDTH);
    }

    /**
     * Creates a new GenericEdgeLineWidthCalculator object.
     *
     * @param name DOCUMENT ME!
     * @param props DOCUMENT ME!
     * @param baseKey DOCUMENT ME!
     */
    GenericEdgeLineWidthCalculator(String name, Properties props, String baseKey) {
        super(name, props, baseKey, EDGE_LINE_WIDTH);
    }

}
