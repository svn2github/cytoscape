package cytoscape.visual.calculators;

import giny.model.Node;

import java.util.Properties;


import cytoscape.CyNetwork;
import cytoscape.visual.Appearance;
import cytoscape.visual.LineStyle;
import static cytoscape.visual.VisualPropertyType.NODE_LINE_STYLE;
import cytoscape.visual.mappings.ObjectMapping;
import cytoscape.visual.parsers.LineStyleParser;

public class GenericNodeLineStyleCalculator extends NodeCalculator {

	
    /**
     * Creates a new GenericNodeLineWidthCalculator object.
     *
     * @param name DOCUMENT ME!
     * @param m DOCUMENT ME!
     */
    public GenericNodeLineStyleCalculator(String name, ObjectMapping m) {
        super(name, m, LineStyle.class, NODE_LINE_STYLE);
    }

    /**
     * Creates a new GenericNodeLineWidthCalculator object.
     *
     * @param name DOCUMENT ME!
     * @param props DOCUMENT ME!
     * @param baseKey DOCUMENT ME!
     */
    public GenericNodeLineStyleCalculator(String name, Properties props,
        String baseKey) {
        super(name, props, baseKey, new LineStyleParser(), LineStyle.SOLID, NODE_LINE_STYLE);
    }
	
}
