package cytoscape.visual.mappings.continuous.unitTests;


import cytoscape.visual.mappings.continuous.ContinuousMappingReader;
import cytoscape.visual.mappings.continuous.ContinuousMappingWriter;
import cytoscape.visual.parsers.ColorParser;
import cytoscape.visual.parsers.ValueParser;
import junit.framework.TestCase;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;

/**
 * Tests the ContinuousMappingWriter Class.
 */
public class TestContinuousMappingWriter extends TestCase {

    /**
     * Tests the ContinuousMappingWriter Class.
     * @throws Exception All Exceptions.
     */
    public void testWriter() throws Exception {
        //  Read in a Properties File
        String baseKey = "nodeColorCalculator.RedGreen2.mapping";
        ValueParser parser = new ColorParser();
        InputStream in = TestContinuousMappingReader.getDataFile();
        Properties properties = new Properties();
        properties.load(in);
        ContinuousMappingReader reader = new ContinuousMappingReader
                (properties, baseKey, parser);
        ArrayList points = reader.getPoints();

        //  Now write out a set of Propeties.
        ContinuousMappingWriter writer = new ContinuousMappingWriter(points,
                baseKey, reader.getControllingAttributeName(),
                reader.getInterpolator());

        //  Test a samplint of properties.
        Properties newProps = writer.getProperties();

        //  Test Boundary Range Value
        String bv0Lesser = newProps.getProperty(baseKey + ".bv0.lesser");
        assertEquals("255,0,0", bv0Lesser);

        //  Test Interpolator
        String interp = newProps.getProperty(baseKey + ".interpolator");
        assertEquals("LinearNumberToColorInterpolator", interp);

        //  Test Controlling Attribute
        String type = newProps.getProperty(baseKey + ".controller");
        assertEquals("expression", type);
    }
}