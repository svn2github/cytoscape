package cytoscape.visual.calculators;

import cytoscape.CyNetwork;

import cytoscape.visual.EdgeAppearance;
import cytoscape.visual.Line;
import cytoscape.visual.VisualPropertyType;

import cytoscape.visual.mappings.ObjectMapping;

import cytoscape.visual.parsers.LineParser;

import giny.model.Edge;

import java.util.Properties;


/**
 * DOCUMENT ME!
 *
 * @author $author$
  */
abstract class AbstractEdgeLineCalculator extends EdgeCalculator {
    /**
     * Creates a new AbstractEdgeLineCalculator object.
     *
     * @param name DOCUMENT ME!
     * @param m DOCUMENT ME!
     * @param c DOCUMENT ME!
     * @param type DOCUMENT ME!
     */
    public AbstractEdgeLineCalculator(String name, ObjectMapping m, Class c,
        VisualPropertyType type) {
        super(name, m, c, type);
    }

    /**
     * Creates a new AbstractEdgeLineCalculator object.
     *
     * @param name DOCUMENT ME!
     * @param props DOCUMENT ME!
     * @param baseKey DOCUMENT ME!
     * @param type DOCUMENT ME!
     */
    public AbstractEdgeLineCalculator(String name, Properties props,
        String baseKey, VisualPropertyType type) {
        super(name, props, baseKey, new LineParser(), Line.DEFAULT_LINE, type);
    }

    /**
     * DOCUMENT ME!
     *
     * @param appr DOCUMENT ME!
     * @param edge DOCUMENT ME!
     * @param network DOCUMENT ME!
     */
    public void apply(EdgeAppearance appr, Edge edge, CyNetwork network) {
        final Line line = (Line) getRangeValue(edge);

        // default has already been set - no need to do anything
        if (line == null)
            return;

        appr.setLine(line);
    }
}
