//----------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//----------------------------------------------------------------------------
package cytoscape.visual.mappings.discrete;

import cytoscape.visual.parsers.ObjectToString;

import java.util.Iterator;
import java.util.Properties;
import java.util.TreeMap;

/**
 * Writes DiscreteMapping Properties.
 *
 * Unit Test for this class exists in:
 * cytoscape.visual.mappings.discrete.unitTests.TestDiscreteMappingWriter.
 */
public class DiscreteMappingWriter {
    private String attrName;
    private String baseKey;
    private TreeMap map;

    /**
     * Constructor.
     * @param attrName Controlling Attribute Name.
     * @param map Discrete Map.
     */
    public DiscreteMappingWriter(String attrName, String baseKey,
            TreeMap map) {
        this.attrName = attrName;
        this.baseKey = baseKey;
        this.map = map;
    }

    /**
     * Return a Properties object with entries suitable for customizing this
     * object via the applyProperties method.
     */
    public Properties getProperties() {
        Properties newProps = new Properties();
        String contKey = baseKey + ".controller";
        newProps.setProperty(contKey, attrName);

        String mapKey = baseKey + ".map.";
        Iterator iterator = map.keySet().iterator();
        while (iterator.hasNext()) {
            String key = (String) iterator.next();
            Object value = map.get(key);
            String stringValue = ObjectToString.getStringValue(value);
            newProps.setProperty(mapKey + key, stringValue);
        }
        return newProps;
    }
}
