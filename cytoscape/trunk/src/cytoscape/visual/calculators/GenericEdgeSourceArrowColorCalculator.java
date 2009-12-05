package cytoscape.visual.calculators;

import static cytoscape.visual.VisualPropertyType.EDGE_SRCARROW_COLOR;
import giny.model.Edge;

import java.awt.Color;
import java.util.Properties;

import cytoscape.CyNetwork;
import cytoscape.visual.Appearance;
import cytoscape.visual.mappings.ObjectMapping;
import cytoscape.visual.parsers.ColorParser;


/**
 * This class exists ONLY to support legacy file formats. A VERY BAD PERSON
 * decided to use the class name to identify calculators in property files,
 * thus forever forcing us to keep these classes around.  
 *
 * <b>DO NOT USE THIS CLASS!!!</b>
  */
class GenericEdgeSourceArrowColorCalculator extends BasicCalculator {
    /**
     * Creates a new GenericEdgeSourceArrowColorCalculator object.
     *
     * @param name DOCUMENT ME!
     * @param m DOCUMENT ME!
     */
    GenericEdgeSourceArrowColorCalculator(String name, ObjectMapping m) {
        super(name, m, EDGE_SRCARROW_COLOR);
    }

    /**
     * Creates a new GenericEdgeSourceArrowColorCalculator object.
     *
     * @param name DOCUMENT ME!
     * @param props DOCUMENT ME!
     * @param baseKey DOCUMENT ME!
     */
    GenericEdgeSourceArrowColorCalculator(String name, Properties props, String baseKey) {
		super(name, props, baseKey, EDGE_SRCARROW_COLOR);
    }

}
