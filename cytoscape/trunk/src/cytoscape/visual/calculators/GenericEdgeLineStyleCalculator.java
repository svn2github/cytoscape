package cytoscape.visual.calculators;

import giny.model.Edge;

import java.util.Properties;


import cytoscape.CyNetwork;
import cytoscape.visual.EdgeAppearance;
import cytoscape.visual.LineStyle;
import static cytoscape.visual.VisualPropertyType.EDGE_LINE_STYLE;
import cytoscape.visual.mappings.ObjectMapping;
import cytoscape.visual.parsers.LineStyleParser;

public class GenericEdgeLineStyleCalculator extends EdgeCalculator {

	
    /**
     * Creates a new GenericEdgeLineWidthCalculator object.
     *
     * @param name DOCUMENT ME!
     * @param m DOCUMENT ME!
     */
    public GenericEdgeLineStyleCalculator(String name, ObjectMapping m) {
        super(name, m, LineStyle.class, EDGE_LINE_STYLE);
    }

    /**
     * Creates a new GenericEdgeLineWidthCalculator object.
     *
     * @param name DOCUMENT ME!
     * @param props DOCUMENT ME!
     * @param baseKey DOCUMENT ME!
     */
    public GenericEdgeLineStyleCalculator(String name, Properties props,
        String baseKey) {
        super(name, props, baseKey, new LineStyleParser(), LineStyle.SOLID, EDGE_LINE_STYLE);
    }
	
}
