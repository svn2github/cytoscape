//----------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//----------------------------------------------------------------------------
package cytoscape.visual.mappings.discrete.unitTests;

import cytoscape.visual.mappings.discrete.DiscreteMappingReader;
import cytoscape.visual.mappings.discrete.DiscreteMappingWriter;
import cytoscape.visual.parsers.ColorParser;
import cytoscape.visual.parsers.ValueParser;
import junit.framework.TestCase;

import java.io.InputStream;
import java.util.Properties;
import java.util.TreeMap;

/**
 * Tests the DiscreteMappingWriter Class.
 */
public class TestDiscreteMappingWriter extends TestCase {

    /**
     * Tests the DiscreteMappingWriter Class.
     * @throws Exception All Exceptions.
     */
    public void testWriter() throws Exception {

        //  Read in a Properties File
        String baseKey = "nodeColorCalculator.JUnitDiscreteColor.mapping";
        ValueParser parser = new ColorParser();
        InputStream in = TestDiscreteMappingReader.getDataFile();
        Properties properties = new Properties();
        properties.load(in);

        DiscreteMappingReader reader = new DiscreteMappingReader
                (properties, baseKey, parser);
        TreeMap map = reader.getMap();

        //  Now write out a set of Propeties.
        DiscreteMappingWriter writer = new DiscreteMappingWriter
                (reader.getControllingAttributeName(), baseKey,
                        reader.getMap());


        //  Test a sampling of properties.
        Properties newProps = writer.getProperties();

        //  Test a few elements...
        String mapA = newProps.getProperty(baseKey + ".map.A");
        assertEquals("204,255,255", mapA);

        String mapY = newProps.getProperty(baseKey + ".map.Y");
        assertEquals("255,51,51", mapY);

        //  Test Controlling Attribute
        String type = newProps.getProperty(baseKey + ".controller");
        assertEquals("canonicalName", type);
    }
}