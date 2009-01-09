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

import y.base.Node;

import cytoscape.data.CyNetwork;
import cytoscape.visual.mappings.ObjectMapping;
import cytoscape.visual.parsers.DoubleParser;
//----------------------------------------------------------------------------
public class GenericNodeSizeCalculator extends NodeCalculator implements NodeSizeCalculator {
    
    public GenericNodeSizeCalculator(String name, ObjectMapping m) {
	super(name, m);

        Double d = new Double(0.0);
        Class c = d.getClass();
        if (!c.isAssignableFrom(m.getRangeClass()) ) {
            String s = "Invalid Calculator: Expected class " + c.toString()
                    + ", got " + m.getRangeClass().toString();
            throw new ClassCastException(s);
        }
    }
    /**
     * Constructor for dynamic creation via properties.
     */
    public GenericNodeSizeCalculator(String name, Properties props, String baseKey) {
        super(name, props, baseKey, new DoubleParser(), new Double(0));
    }
    
    /** 
     *  calculateNodeSize returns -1 if there is no mapping;
     *  since a negative number has no meaning as a node size,
     *  this is a case that the caller of calculateNodeSize
     *  should expect to handle.  The usual caller is
     *  NodeAppearanceCalculator.
     */
    public double calculateNodeSize(Node node, CyNetwork network) {
        String canonicalName = network.getNodeAttributes().getCanonicalName(node);
        Map attrBundle = network.getNodeAttributes().getAttributes(canonicalName);
	Object rangeValue = super.getMapping().calculateRangeValue(attrBundle);
	if(rangeValue!=null)
	    return ((Number)rangeValue).doubleValue();
	else
	    return -1;
    }
}

