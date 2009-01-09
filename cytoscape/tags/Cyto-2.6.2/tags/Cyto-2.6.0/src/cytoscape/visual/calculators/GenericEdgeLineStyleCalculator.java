package cytoscape.visual.calculators;

import static cytoscape.visual.VisualPropertyType.EDGE_LINE_STYLE;

import java.util.Properties;

import cytoscape.visual.LineStyle;
import cytoscape.visual.mappings.ObjectMapping;
import cytoscape.visual.parsers.LineStyleParser;

/**
 * 
 * @deprecated Will be removed 5/2008
 *
 */
@Deprecated
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
