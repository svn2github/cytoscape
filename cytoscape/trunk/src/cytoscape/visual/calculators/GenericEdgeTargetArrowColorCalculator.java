package cytoscape.visual.calculators;


//import static cytoscape.visual.VisualPropertyType.EDGE_TGTARROW_COLOR;
import static cytoscape.visual.VisualPropertyType.EDGE_TGTARROW_COLOR;

import cytoscape.visual.mappings.ObjectMapping;
import cytoscape.visual.parsers.ColorParser;
import cytoscape.visual.Appearance;
import cytoscape.CyNetwork;

import giny.model.Edge;

import java.awt.Color;

import java.util.Properties;


/**
 * DOCUMENT ME!
 *
 * @author $author$
  */
public class GenericEdgeTargetArrowColorCalculator extends EdgeCalculator {
    /**
     * Creates a new GenericEdgeTargetArrowColorCalculator object.
     *
     * @param name DOCUMENT ME!
     * @param m DOCUMENT ME!
     */
    public GenericEdgeTargetArrowColorCalculator(String name, ObjectMapping m) {
        super(name, m, Color.class, EDGE_TGTARROW_COLOR);
    }

    /**
     * Creates a new GenericEdgeTargetArrowColorCalculator object.
     *
     * @param name DOCUMENT ME!
     * @param props DOCUMENT ME!
     * @param baseKey DOCUMENT ME!
     */
    public GenericEdgeTargetArrowColorCalculator(String name, Properties props,
        String baseKey) {
		super(name, props, baseKey, new ColorParser(), Color.black, EDGE_TGTARROW_COLOR);
    }

}
