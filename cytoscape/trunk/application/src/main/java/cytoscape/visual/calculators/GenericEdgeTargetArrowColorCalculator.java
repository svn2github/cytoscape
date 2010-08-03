package cytoscape.visual.calculators;


import static cytoscape.visual.VisualPropertyType.EDGE_TGTARROW_COLOR;

import cytoscape.visual.mappings.ObjectMapping;
import cytoscape.visual.parsers.ColorParser;
import cytoscape.visual.Appearance;
import cytoscape.CyNetwork;

import giny.model.Edge;

import java.awt.Color;

import java.util.Properties;


/**
 * This class exists ONLY to support legacy file formats. A VERY BAD PERSON
 * decided to use the class name to identify calculators in property files,
 * thus forever forcing us to keep these classes around.  
 *
 * <b>DO NOT USE THIS CLASS!!!</b>
  */
class GenericEdgeTargetArrowColorCalculator extends BasicCalculator {
    /**
     * Creates a new GenericEdgeTargetArrowColorCalculator object.
     *
     * @param name DOCUMENT ME!
     * @param m DOCUMENT ME!
     */
    GenericEdgeTargetArrowColorCalculator(String name, ObjectMapping m) {
        super(name, m, EDGE_TGTARROW_COLOR);
    }

    /**
     * Creates a new GenericEdgeTargetArrowColorCalculator object.
     *
     * @param name DOCUMENT ME!
     * @param props DOCUMENT ME!
     * @param baseKey DOCUMENT ME!
     */
    GenericEdgeTargetArrowColorCalculator(String name, Properties props, String baseKey) {
		super(name, props, baseKey, EDGE_TGTARROW_COLOR);
    }

}
