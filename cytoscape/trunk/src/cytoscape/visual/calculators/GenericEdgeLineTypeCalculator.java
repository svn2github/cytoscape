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

import y.base.Edge;
import y.view.LineType;

import cytoscape.data.CyNetwork;
import cytoscape.visual.mappings.ObjectMapping;
import cytoscape.visual.parsers.LineTypeParser;
//----------------------------------------------------------------------------
public class GenericEdgeLineTypeCalculator extends EdgeCalculator implements EdgeLineTypeCalculator {
    
    public GenericEdgeLineTypeCalculator(String name, ObjectMapping m) {
	super(name, m);

        Class c = null;
	//c = LineType.class;  // this line won't obfuscate; the one below does.
	c = (LineType.getLineType(1,LineType.LINE_STYLE)).getClass();
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
        String canonicalName = network.getEdgeAttributes().getCanonicalName(edge);
        Map attrBundle = network.getEdgeAttributes().getAttributes(canonicalName);
        return (LineType)super.getMapping().calculateRangeValue(attrBundle);
    }
}

