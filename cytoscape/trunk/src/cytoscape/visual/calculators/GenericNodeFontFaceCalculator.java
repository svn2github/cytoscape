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

import y.base.Node;

import cytoscape.data.CyNetwork;
import cytoscape.visual.mappings.ObjectMapping;
import cytoscape.visual.parsers.FontParser;
//--------------------------------------------------------------------------
public class GenericNodeFontFaceCalculator extends NodeCalculator
    implements NodeFontFaceCalculator{
    
    public GenericNodeFontFaceCalculator(String name, ObjectMapping m) {
	super(name, m);
	if (!(Font.class.isAssignableFrom(m.getRangeClass()))) {
	    throw new ClassCastException("Invalid Calculator: Expected class Font, got " + 
					 m.getRangeClass().toString());
	}
    }
    /**
     * Constructor for dynamic creation via properties.
     */
    public GenericNodeFontFaceCalculator(String name, Properties props, String baseKey) {
        super(name, props, baseKey, new FontParser(), new Font(null, Font.PLAIN, 12));
    }
    
    public Font calculateNodeFontFace(Node node, CyNetwork network) {
	String canonicalName = network.getNodeAttributes().getCanonicalName(node);
	Map attrBundle = network.getNodeAttributes().getAttributes(canonicalName);
	return (Font) super.getMapping().calculateRangeValue(attrBundle);
    }
}
