//--------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//--------------------------------------------------------------------------
package cytoscape.visual.calculators;
//--------------------------------------------------------------------------
import java.util.Map;
import java.util.Properties;
import javax.swing.*;

import giny.model.Node;

import cytoscape.CyNetwork;
import cytoscape.visual.mappings.ObjectMapping;
import cytoscape.visual.parsers.DoubleParser;
//--------------------------------------------------------------------------
public class GenericNodeFontSizeCalculator extends NodeCalculator
    implements NodeFontSizeCalculator{
    
    public GenericNodeFontSizeCalculator(String name, ObjectMapping m) {
	super(name, m);
	//All we need is some kind of Number
	if (!(Number.class.isAssignableFrom(m.getRangeClass()))) {
	    throw new ClassCastException("Invalid Calculator: Expected class Number, got " + 
					 m.getRangeClass().toString());
	}
    }
    
    /**
     * Constructor for dynamic creation via properties.
     */
    public GenericNodeFontSizeCalculator(String name, Properties props, String baseKey) {
	super(name, props, baseKey, new DoubleParser(), new Double(12));
    }

    /** 
     *  calculateNodeFontSize returns -1 if there is no mapping;
     *  since a negative number has no meaning as a font size,
     *  this is a case that the caller of calculateNodeFontSize
     *  should expect to handle.  The usual caller is
     *  NodeAppearanceCalculator.
     */
    public float calculateNodeFontSize(Node node, CyNetwork network) {
	String canonicalName = network.getNodeAttributes().getCanonicalName(node);
	Map attrBundle = network.getNodeAttributes().getAttributes(canonicalName);
	Object rangeValue = super.getMapping().calculateRangeValue(attrBundle);
	if (rangeValue != null)
	    return ((Number) rangeValue).floatValue();
	else
	    return -1;
    }
}
