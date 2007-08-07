//----------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//----------------------------------------------------------------------------
package cytoscape.visual.mappings.discrete;

import cytoscape.visual.parsers.ValueParser;

import java.util.Enumeration;
import java.util.Properties;
import java.util.TreeMap;

/**
 * Reads in DiscreteMapping Properties.
 *
 * Unit Test for this class exists in:
 * cytoscape.visual.mappings.discrete.unitTests.TestDiscreteMappingReader.
 */
public class DiscreteMappingReader {
    private String controllingAttribute;
    private TreeMap map = new TreeMap();

    /**
     * Constructor.
     * @param props Properties Object.
     * @param baseKey Base Property Key.
     * @param parser ValueParser Object.
     */
    public DiscreteMappingReader(Properties props, String baseKey,
            ValueParser parser) {
        readProperties(props, baseKey, parser);
    }

    /**
     * Gets Controlling Attribute Name.
     * @return Controlling Attribute Name.
     */
    public String getControllingAttributeName() {
        return controllingAttribute;
    }

    /**
     * Gets the Discrete Map.
     * @return TreeMap Object.
     */
    public TreeMap getMap() {
        return map;
    }

    /**
     * Read in Settings from the Properties Object.
     */
    private void readProperties(Properties props, String baseKey,
            ValueParser parser) {
        String contKey = baseKey + ".controller";
        controllingAttribute = props.getProperty(contKey);
        String mapKey = baseKey + ".map.";
        Enumeration eProps = props.propertyNames();
        while (eProps.hasMoreElements()) {
            String key = (String) eProps.nextElement();
            if (key.startsWith(mapKey)) {
                String value = props.getProperty(key);
                String domainVal = key.substring(mapKey.length());
                Object parsedVal = parser.parseStringValue(value);
                map.put(domainVal, parsedVal);
            }
        }
    }
}
