package cytoscape.visual.calculators;

import cytoscape.CyNetwork;

import cytoscape.visual.Arrow;
import cytoscape.visual.EdgeAppearance;
import cytoscape.visual.VisualPropertyType;
import static cytoscape.visual.VisualPropertyType.EDGE_TGTARROW_SHAPE;

import cytoscape.visual.mappings.ObjectMapping;

import cytoscape.visual.parsers.ArrowParser;

import giny.model.Edge;

import java.util.Properties;

/**
 * DOCUMENT ME!
 *
 * @author $author$
  */
public class GenericEdgeTargetArrowShapeCalculator extends EdgeCalculator {
    /**
     * Creates a new GenericEdgeTargetArrowShapeCalculator object.
     *
     * @param name DOCUMENT ME!
     * @param m DOCUMENT ME!
     * @param type DOCUMENT ME!
     */
    public GenericEdgeTargetArrowShapeCalculator(String name, ObjectMapping m) {
        super(name, m, Arrow.class, EDGE_TGTARROW_SHAPE);
    }

    /**
     * Creates a new GenericEdgeTargetArrowShapeCalculator object.
     *
     * @param name DOCUMENT ME!
     * @param props DOCUMENT ME!
     * @param baseKey DOCUMENT ME!
     * @param type DOCUMENT ME!
     */
    public GenericEdgeTargetArrowShapeCalculator(String name, Properties props, String baseKey ) {
		super(name, props, baseKey, new ArrowParser(), Arrow.NONE, EDGE_TGTARROW_SHAPE);
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

		appr.setTargetArrow(a);
    }
}
