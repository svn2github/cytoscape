//--------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//--------------------------------------------------------------------------
package cytoscape.visual.calculators;
//--------------------------------------------------------------------------
import java.util.Map;
import java.util.Properties;
import java.awt.Font;
import javax.swing.*;

import giny.model.Edge;

import cytoscape.CyNetwork;
import cytoscape.visual.mappings.ObjectMapping;
import cytoscape.visual.parsers.FontParser;
//--------------------------------------------------------------------------
public class GenericEdgeFontFaceCalculator extends EdgeCalculator
    implements EdgeFontFaceCalculator{
    
    public GenericEdgeFontFaceCalculator(String name, ObjectMapping m) {
	super(name, m);
	if (!(Font.class.isAssignableFrom(m.getRangeClass()))) {
	    throw new ClassCastException("Invalid Calculator: Expected class Font, got " + 
					 m.getRangeClass().toString());
	}
    }
    /**
     * Constructor for dynamic creation via properties.
     */
    public GenericEdgeFontFaceCalculator(String name, Properties props, String baseKey) {
        super(name, props, baseKey, new FontParser(), new Font(null, Font.PLAIN, 12));
    }

    public Font calculateEdgeFontFace(Edge edge, CyNetwork network) {
	String canonicalName = network.getEdgeAttributes().getCanonicalName(edge);
	Map attrBundle = network.getEdgeAttributes().getAttributes(canonicalName);
	return (Font) super.getMapping().calculateRangeValue(attrBundle);
    }
}
