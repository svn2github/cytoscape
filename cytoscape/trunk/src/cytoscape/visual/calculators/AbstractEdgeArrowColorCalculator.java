package cytoscape.visual.calculators;

import cytoscape.CyNetwork;

import cytoscape.visual.EdgeAppearance;
import cytoscape.visual.VisualPropertyType;

import cytoscape.visual.mappings.ObjectMapping;

import cytoscape.visual.parsers.ColorParser;

import giny.model.Edge;

import java.awt.Color;

import java.util.Properties;


/**
 * DOCUMENT ME!
 *
 * @author $author$
  */
abstract class AbstractEdgeArrowColorCalculator extends EdgeCalculator {
    /**
     * Creates a new AbstractEdgeArrowColorCalculator object.
     *
     * @param name DOCUMENT ME!
     * @param m DOCUMENT ME!
     * @param type DOCUMENT ME!
     */
    public AbstractEdgeArrowColorCalculator(String name, ObjectMapping m,
        VisualPropertyType type) {
        super(name, m, Color.class, type);
    }

    /**
     * Creates a new AbstractEdgeArrowColorCalculator object.
     *
     * @param name DOCUMENT ME!
     * @param props DOCUMENT ME!
     * @param baseKey DOCUMENT ME!
     * @param type DOCUMENT ME!
     */
    public AbstractEdgeArrowColorCalculator(String name, Properties props,
        String baseKey, VisualPropertyType type) {
        super(name, props, baseKey, new ColorParser(), Color.black, type);
    }

    /**
     * DOCUMENT ME!
     *
     * @param appr DOCUMENT ME!
     * @param edge DOCUMENT ME!
     * @param network DOCUMENT ME!
     */
    public void apply(EdgeAppearance appr, Edge edge, CyNetwork network) {
        Color c = (Color) getRangeValue(edge);

        // default has already been set - no need to do anything
        if (c == null)
            return;

        if (type == VisualPropertyType.EDGE_SRCARROW_COLOR)
            appr.setSourceArrowColor(c);
        else if (type == VisualPropertyType.EDGE_TGTARROW_COLOR)
            appr.setTargetArrowColor(c);
    }
}
