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
 * DOCUMENT ME!
 *
 * @author $author$
  */
public class GenericEdgeSourceArrowColorCalculator extends EdgeCalculator {
    /**
     * Creates a new GenericEdgeSourceArrowColorCalculator object.
     *
     * @param name DOCUMENT ME!
     * @param m DOCUMENT ME!
     */
    public GenericEdgeSourceArrowColorCalculator(String name, ObjectMapping m) {
        super(name, m, Color.class, EDGE_SRCARROW_COLOR);
    }

    /**
     * Creates a new GenericEdgeSourceArrowColorCalculator object.
     *
     * @param name DOCUMENT ME!
     * @param props DOCUMENT ME!
     * @param baseKey DOCUMENT ME!
     */
    public GenericEdgeSourceArrowColorCalculator(String name, Properties props,
        String baseKey) {
		super(name, props, baseKey, new ColorParser(), Color.black, EDGE_SRCARROW_COLOR);
    }

}
