//----------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//----------------------------------------------------------------------------
package cytoscape.visual.calculators;
//----------------------------------------------------------------------------
import java.util.Map;
import java.util.Properties;
import javax.swing.JPanel;

import giny.model.Edge;
import cytoscape.visual.LineType;

import cytoscape.CyNetwork;
import cytoscape.visual.mappings.ObjectMapping;
import cytoscape.visual.parsers.LineTypeParser;
//----------------------------------------------------------------------------
public class GenericEdgeLineTypeCalculator extends EdgeCalculator implements EdgeLineTypeCalculator {
    
    public GenericEdgeLineTypeCalculator(String name, ObjectMapping m) {
	super(name, m);

        Class c = null;
	//c = LineType.class;  // this line won't obfuscate; the one below does.
	c = LineType.LINE_1.getClass();
        if (!c.isAssignableFrom(m.getRangeClass()) ) {
            String s = "Invalid Calculator: Expected class " + c.toString()
		+ ", got " + m.getRangeClass().toString();
            throw new ClassCastException(s);
        }
    }
    /**
     * Constructor for dynamic creation via properties.
     */
    public GenericEdgeLineTypeCalculator(String name, Properties props, String baseKey) {
        super(name, props, baseKey, new LineTypeParser(), LineType.LINE_1);
    }
    
    public LineType calculateEdgeLineType(Edge edge, CyNetwork network) {
        String canonicalName = edge.getIdentifier();
        Map attrBundle = getAttrBundle(canonicalName);
        return (LineType)super.getMapping().calculateRangeValue(attrBundle);
    }
}

