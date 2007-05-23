package cytoscape.visual.calculators;

import cytoscape.CyNetwork;

import cytoscape.visual.ArrowShape;
import cytoscape.visual.Appearance;
import cytoscape.visual.VisualPropertyType;
import static cytoscape.visual.VisualPropertyType.EDGE_SRCARROW_SHAPE;

import cytoscape.visual.mappings.ObjectMapping;

import cytoscape.visual.parsers.ArrowParser;

import giny.model.Edge;

import java.util.Properties;


/**
 * DOCUMENT ME!
 *
 * @author $author$
  */
public class GenericEdgeSourceArrowShapeCalculator extends EdgeCalculator {
    /**
     * Creates a new GenericEdgeSourceArrowShapeCalculator object.
     *
     * @param name DOCUMENT ME!
     * @param m DOCUMENT ME!
     */
    public GenericEdgeSourceArrowShapeCalculator(String name, ObjectMapping m) {
        super(name, m, ArrowShape.class, EDGE_SRCARROW_SHAPE);
    }

    /**
     * Creates a new GenericEdgeSourceArrowShapeCalculator object.
     *
     * @param name DOCUMENT ME!
     * @param props DOCUMENT ME!
     * @param baseKey DOCUMENT ME!
     */
    public GenericEdgeSourceArrowShapeCalculator(String name, Properties props, String baseKey) {
		super(name, props, baseKey, new ArrowParser(), ArrowShape.NONE, EDGE_SRCARROW_SHAPE);
    }
}
