package cytoscape.visual.calculators;

import cytoscape.CyNetwork;

import cytoscape.visual.Arrow;
import cytoscape.visual.EdgeAppearance;
import cytoscape.visual.VisualPropertyType;

import cytoscape.visual.mappings.ObjectMapping;

import cytoscape.visual.parsers.ArrowParser;

import giny.model.Edge;

import java.util.Properties;


/**
 * DOCUMENT ME!
 *
 * @author $author$
  */
abstract class AbstractEdgeArrowShapeCalculator extends EdgeCalculator {
    /**
     * Creates a new AbstractEdgeArrowShapeCalculator object.
     *
     * @param name DOCUMENT ME!
     * @param m DOCUMENT ME!
     * @param type DOCUMENT ME!
     */
    public AbstractEdgeArrowShapeCalculator(String name, ObjectMapping m,
        VisualPropertyType type) {
        super(name, m, Arrow.class, type);
    }

    /**
     * Creates a new AbstractEdgeArrowShapeCalculator object.
     *
     * @param name DOCUMENT ME!
     * @param props DOCUMENT ME!
     * @param baseKey DOCUMENT ME!
     * @param type DOCUMENT ME!
     */
    public AbstractEdgeArrowShapeCalculator(String name, Properties props,
        String baseKey, VisualPropertyType type) {
        super(name, props, baseKey, new ArrowParser(), Arrow.NONE, type);
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

        if (type == VisualPropertyType.EDGE_SRCARROW_SHAPE)
            appr.setSourceArrow(a);
        else if (type == VisualPropertyType.EDGE_TGTARROW_SHAPE)
            appr.setTargetArrow(a);
    }
}
