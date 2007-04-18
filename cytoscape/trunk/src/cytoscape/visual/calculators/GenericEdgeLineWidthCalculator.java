package cytoscape.visual.calculators;

import cytoscape.CyNetwork;

import cytoscape.visual.EdgeAppearance;
import cytoscape.visual.Line;
import static cytoscape.visual.VisualPropertyType.EDGE_LINE_WIDTH;

import cytoscape.visual.mappings.ObjectMapping;

import cytoscape.visual.parsers.FloatParser;

import giny.model.Edge;

import java.util.Properties;


/**
 * DOCUMENT ME!
 *
 * @author $author$
  */
public class GenericEdgeLineWidthCalculator extends EdgeCalculator {
    /**
     * Creates a new GenericEdgeLineWidthCalculator object.
     *
     * @param name DOCUMENT ME!
     * @param m DOCUMENT ME!
     */
    public GenericEdgeLineWidthCalculator(String name, ObjectMapping m) {
        super(name, m, Float.class, EDGE_LINE_WIDTH);
    }

    /**
    * Creates a new GenericEdgeLineWidthCalculator object.
    *
    * @param name DOCUMENT ME!
    * @param m DOCUMENT ME!
    * @param c DOCUMENT ME!
    */
    public GenericEdgeLineWidthCalculator(String name, ObjectMapping m, Class c) {
        super(name, m, c, EDGE_LINE_WIDTH);
    }

    /**
     * Creates a new GenericEdgeLineWidthCalculator object.
     *
     * @param name DOCUMENT ME!
     * @param props DOCUMENT ME!
     * @param baseKey DOCUMENT ME!
     */
    public GenericEdgeLineWidthCalculator(String name, Properties props,
        String baseKey) {
        super(name, props, baseKey, new FloatParser(), new Float(0),
            EDGE_LINE_WIDTH);
    }

    /**
     * DOCUMENT ME!
     *
     * @param appr DOCUMENT ME!
     * @param edge DOCUMENT ME!
     * @param network DOCUMENT ME!
     */
    public void apply(EdgeAppearance appr, Edge edge, CyNetwork network) {
        final Object lineWidth = getRangeValue(edge);

        // default has already been set - no need to do anything
        if (lineWidth == null)
            return;

        if (lineWidth instanceof Double)
            appr.setLineWidth(((Double) lineWidth).floatValue());
        else
            appr.setLineWidth((Float) lineWidth);
    }

    /**
     * DOCUMENT ME!
     *
     * @param e DOCUMENT ME!
     * @param n DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Line calculateEdgeLineWidth(Edge e, CyNetwork n) {
        final EdgeAppearance ea = new EdgeAppearance();
        apply(ea, e, n);

        return ea.getLine();
    }
}
