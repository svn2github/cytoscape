//----------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//----------------------------------------------------------------------------
package cytoscape.visual.mappings.discrete.unitTests;

import cytoscape.visual.mappings.discrete.DiscreteMappingReader;
import cytoscape.visual.parsers.ColorParser;
import cytoscape.visual.parsers.ValueParser;
import junit.framework.TestCase;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Properties;
import java.util.TreeMap;

/**
 * Tests the DiscreteMappingReader Class.
 */
public class TestDiscreteMappingReader extends TestCase {

    /**
     * Tests the DiscreteMappingReader Class.
     * @throws Exception All Exceptions.
     */
    public void testReader() throws Exception {
        InputStream in = getDataFile();
        Properties properties = new Properties();
        properties.load(in);

        ValueParser parser = new ColorParser();
        DiscreteMappingReader reader = new DiscreteMappingReader(properties,
                "nodeColorCalculator.JUnitDiscreteColor.mapping", parser);
        String attribute = reader.getControllingAttributeName();
        assertEquals("canonicalName", attribute);
        TreeMap map = reader.getMap();

        //  Test a few of the mapping elements.
        Color color = (Color) map.get("A");
        assertEquals(new Color(204, 255, 255), color);
        color = (Color) map.get("Y");
        assertEquals(new Color(255, 51, 51), color);
    }

    static InputStream getDataFile() {
        String file = new String
                ("nodeColorCalculator.JUnitDiscreteColor.class=cytoscape."
                + "visual.calculators.GenericNodeColorCalculator\n"
                + "nodeColorCalculator.JUnitDiscreteColor.mapping."
                + "controller=canonicalName\n"
                + "nodeColorCalculator.JUnitDiscreteColor.mapping."
                + "map.A=204,255,255\n"
                + "nodeColorCalculator.JUnitDiscreteColor.mapping."
                + "map.B=51,255,51\n"
                + "nodeColorCalculator.JUnitDiscreteColor.mapping."
                + "map.C=204,204,255\n"
                + "nodeColorCalculator.JUnitDiscreteColor.mapping.map."
                + "D=255,255,255\n"
                + "nodeColorCalculator.JUnitDiscreteColor.mapping.map."
                + "X=102,51,0\n"
                + "nodeColorCalculator.JUnitDiscreteColor.mapping.map."
                + "Y=255,51,51\n"
                + "nodeColorCalculator.JUnitDiscreteColor.mapping."
                + "type=DiscreteMapping\n");
        ByteArrayInputStream in = new ByteArrayInputStream(file.getBytes());
        return in;
    }
}