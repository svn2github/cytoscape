package cytoscape.visual.calculators;

import cytoscape.CyNetwork;

import cytoscape.visual.Line;
import cytoscape.visual.NodeAppearance;
import cytoscape.visual.VisualPropertyType;

import cytoscape.visual.mappings.ObjectMapping;

import cytoscape.visual.parsers.LineParser;

import giny.model.Node;

import java.util.Properties;


/**
 * DOCUMENT ME!
 *
 * @author $author$
  */
public abstract class AbstractNodeLineCalculator extends NodeCalculator {
    /**
     * Creates a new AbstractNodeLineCalculator object.
     *
     * @param name DOCUMENT ME!
     * @param m DOCUMENT ME!
     * @param c DOCUMENT ME!
     * @param type DOCUMENT ME!
     */
    public AbstractNodeLineCalculator(String name, ObjectMapping m, Class c,
        VisualPropertyType type) {
        super(name, m, c, type);
    }

    /**
     * Creates a new AbstractNodeLineCalculator object.
     *
     * @param name DOCUMENT ME!
     * @param props DOCUMENT ME!
     * @param baseKey DOCUMENT ME!
     * @param type DOCUMENT ME!
     */
    public AbstractNodeLineCalculator(String name, Properties props,
        String baseKey, VisualPropertyType type) {
        super(name, props, baseKey, new LineParser(), Line.DEFAULT_LINE, type);
    }

    /**
     * DOCUMENT ME!
     *
     * @param appr DOCUMENT ME!
     * @param node DOCUMENT ME!
     * @param network DOCUMENT ME!
     */
    public void apply(NodeAppearance appr, Node node, CyNetwork network) {
        final Line line = (Line) getRangeValue(node);

        // default has already been set - no need to do anything
        if (line == null)
            return;

        appr.setBorderLine(line);
    }
}
