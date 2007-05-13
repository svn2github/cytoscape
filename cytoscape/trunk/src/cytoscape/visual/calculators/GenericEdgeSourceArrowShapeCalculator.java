package cytoscape.visual.calculators;

import cytoscape.CyNetwork;

import cytoscape.visual.Arrow;
import cytoscape.visual.EdgeAppearance;
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
        super(name, m, Arrow.class, EDGE_SRCARROW_SHAPE);
    }

    /**
     * Creates a new GenericEdgeSourceArrowShapeCalculator object.
     *
     * @param name DOCUMENT ME!
     * @param props DOCUMENT ME!
     * @param baseKey DOCUMENT ME!
     */
    public GenericEdgeSourceArrowShapeCalculator(String name, Properties props, String baseKey) {
		super(name, props, baseKey, new ArrowParser(), Arrow.NONE, EDGE_SRCARROW_SHAPE);
    }

    /**
     * DOCUMENT ME!
     *
     * @param appr DOCUMENT ME!
     * @param edge DOCUMENT ME!
     * @param network DOCUMENT ME!
     */
    public void apply(EdgeAppearance appr, Edge edge, CyNetwork network) {
        Arrow a = (Arrow) getRangeValue(edge);

        // default has already been set - no need to do anything
        if (a == null)
            return;

    	appr.setSourceArrow(a);
    }
}
