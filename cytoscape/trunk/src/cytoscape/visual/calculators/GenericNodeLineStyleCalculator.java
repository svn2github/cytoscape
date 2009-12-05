package cytoscape.visual.calculators;

import static cytoscape.visual.VisualPropertyType.NODE_LINE_STYLE;

import java.util.Properties;

import cytoscape.visual.LineStyle;
import cytoscape.visual.mappings.ObjectMapping;
import cytoscape.visual.parsers.LineStyleParser;

/**
 * This class exists ONLY to support legacy file formats. A VERY BAD PERSON
 * decided to use the class name to identify calculators in property files,
 * thus forever forcing us to keep these classes around.  
 *
 * <b>DO NOT USE THIS CLASS!!!</b>
 */
class GenericNodeLineStyleCalculator extends BasicCalculator {
	
    /**
     * Creates a new GenericNodeLineWidthCalculator object.
     *
     * @param name DOCUMENT ME!
     * @param m DOCUMENT ME!
     */
    GenericNodeLineStyleCalculator(String name, ObjectMapping m) {
        super(name, m, NODE_LINE_STYLE);
    }

    /**
     * Creates a new GenericNodeLineWidthCalculator object.
     *
     * @param name DOCUMENT ME!
     * @param props DOCUMENT ME!
     * @param baseKey DOCUMENT ME!
     */
    GenericNodeLineStyleCalculator(String name, Properties props, String baseKey) {
        super(name, props, baseKey, NODE_LINE_STYLE);
    }
	
}
