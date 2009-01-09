package cytoscape.visual.mappings.continuous.unitTests;

import cytoscape.visual.mappings.BoundaryRangeValues;
import cytoscape.visual.mappings.Interpolator;
import cytoscape.visual.mappings.LinearNumberToColorInterpolator;
import cytoscape.visual.mappings.continuous.ContinuousMappingPoint;
import cytoscape.visual.mappings.continuous.ContinuousMappingReader;
import cytoscape.visual.parsers.ColorParser;
import cytoscape.visual.parsers.ValueParser;
import junit.framework.TestCase;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;

/**
 * Tests the ContinuousMappingReader Class.
 */
public class TestContinuousMappingReader extends TestCase {

    /**
     * Tests the ContinuousMappingReader Class.
     * @throws Exception All Exceptions.
     */
    public void testReader() throws Exception {
        InputStream in = getDataFile();
        Properties properties = new Properties();
        properties.load(in);

        int size = properties.size();
        assertEquals(17, size);

        ValueParser parser = new ColorParser();
        ContinuousMappingReader reader = new ContinuousMappingReader
                (properties, "nodeColorCalculator.RedGreen2.mapping",
                        parser);
        String attrName = reader.getControllingAttributeName();
        assertEquals("expression", attrName);

        Interpolator interp = reader.getInterpolator();
        assertTrue(interp instanceof LinearNumberToColorInterpolator);

        ArrayList points = reader.getPoints();
        assertEquals(3, points.size());

        ContinuousMappingPoint point0 = (ContinuousMappingPoint)
                points.get(0);
        double value0 = point0.getValue();
        assertEquals(-1.0, value0, 0.0001);

        BoundaryRangeValues range0 = point0.getRange();
        assertEquals(Color.RED, range0.lesserValue);
        assertEquals(Color.RED, range0.equalValue);
        assertEquals(Color.RED, range0.greaterValue);

        ContinuousMappingPoint point1 = (ContinuousMappingPoint)
                points.get(1);
        double value1 = point1.getValue();
        assertEquals(0.0, value1, 0.0001);
        BoundaryRangeValues range1 = point1.getRange();
        assertEquals(Color.WHITE, range1.lesserValue);
        assertEquals(Color.WHITE, range1.equalValue);
        assertEquals(Color.WHITE, range1.greaterValue);
    }

    static InputStream getDataFile() {
        String file =
                "nodeColorCalculator.RedGreen2.mapping.bv0.lesser:  255,0,0\n"
                + "nodeColorCalculator.RedGreen2.mapping.interpolator:  "
                + "LinearNumberToColorInterpolator\n"
                + "nodeColorCalculator.RedGreen2.mapping.bv0.greater:  255,0,0\n"
                + "nodeColorCalculator.RedGreen2.mapping.bv1.domainvalue:  0.0\n"
                + "nodeColorCalculator.RedGreen2.class:  "
                + "cytoscape.visual.calculators.GenericNodeColorCalculator\n"
                + "nodeColorCalculator.RedGreen2.mapping.bv1.equal:  255,255,255\n"
                + "nodeColorCalculator.RedGreen2.mapping.bv1.greater:  255,255,255\n"
                + "nodeColorCalculator.RedGreen2.mapping.controller:  expression\n"
                + "nodeColorCalculator.RedGreen2.mapping.boundaryvalues:  3\n"
                + "nodeColorCalculator.RedGreen2.mapping.bv2.domainvalue:  0.0\n"
                + "nodeColorCalculator.RedGreen2.mapping.bv0.domainvalue:  -1.0\n"
                + "nodeColorCalculator.RedGreen2.mapping.bv2.lesser:  255,255,255\n"
                + "nodeColorCalculator.RedGreen2.mapping.type:  ContinuousMapping\n"
                + "nodeColorCalculator.RedGreen2.mapping.bv0.equal:  255,0,0\n"
                + "nodeColorCalculator.RedGreen2.mapping.bv2.greater:  102,255,102\n"
                + "nodeColorCalculator.RedGreen2.mapping.bv1.lesser:  255,255,255\n"
                + "nodeColorCalculator.RedGreen2.mapping.bv2.equal:  255,255,255\n";
        ByteArrayInputStream in = new ByteArrayInputStream(file.getBytes());
        return in;
    }

}