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

import y.base.Edge;

import cytoscape.visual.Network;
import cytoscape.visual.mappings.ObjectMapping;
import cytoscape.visual.parsers.DoubleParser;
//--------------------------------------------------------------------------
public class GenericEdgeFontSizeCalculator extends EdgeCalculator
    implements EdgeFontSizeCalculator{
    
    public GenericEdgeFontSizeCalculator(String name, ObjectMapping m) {
	super(name, m);
	//All we need is some kind of Number
	if (!(Number.class.isAssignableFrom(m.getRangeClass()))) {
	    throw new ClassCastException("Invalid Calculator: Expected class Font, got " + 
					 m.getRangeClass().toString());
	}
    }
    
    /**
     * Constructor for dynamic creation via properties.
     */
    public GenericEdgeFontSizeCalculator(String name, Properties props, String baseKey) {
	super(name, props, baseKey, new DoubleParser(), new Double(12));
    }

    /** 
     *  calculateEdgeFontSize returns -1 if there is no mapping;
     *  since a negative number has no meaning as a font size,
     *  this is a case that the caller of calculateEdgeFontSize
     *  should expect to handle.  The usual caller is
     *  NodeAppearanceCalculator.
     */
    public float calculateEdgeFontSize(Edge edge, Network network) {
	String canonicalName = network.getEdgeAttributes().getCanonicalName(edge);
	Map attrBundle = network.getEdgeAttributes().getAttributes(canonicalName);
	Object rangeValue = super.getMapping().calculateRangeValue(attrBundle);
	if (rangeValue != null)
	    return ((Number) rangeValue).floatValue();
	else
	    return -1;
    }
}
