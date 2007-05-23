package cytoscape.visual.calculators;

import cytoscape.CyNetwork;

import cytoscape.visual.ArrowShape;
import cytoscape.visual.Appearance;
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
        super(name, m, ArrowShape.class, EDGE_TGTARROW_SHAPE);
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
		super(name, props, baseKey, new ArrowParser(), ArrowShape.NONE, EDGE_TGTARROW_SHAPE);
    }

}
