
/*
  File: TestContinuousColorRangeCalculator.java 
  
  Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)
  
  The Cytoscape Consortium is: 
  - Institute for Systems Biology
  - University of California San Diego
  - Memorial Sloan-Kettering Cancer Center
  - Institut Pasteur
  - Agilent Technologies
  
  This library is free software; you can redistribute it and/or modify it
  under the terms of the GNU Lesser General Public License as published
  by the Free Software Foundation; either version 2.1 of the License, or
  any later version.
  
  This library is distributed in the hope that it will be useful, but
  WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
  MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
  documentation provided hereunder is on an "as is" basis, and the
  Institute for Systems Biology and the Whitehead Institute 
  have no obligations to provide maintenance, support,
  updates, enhancements or modifications.  In no event shall the
  Institute for Systems Biology and the Whitehead Institute 
  be liable to any party for direct, indirect, special,
  incidental or consequential damages, including lost profits, arising
  out of the use of this software and its documentation, even if the
  Institute for Systems Biology and the Whitehead Institute 
  have been advised of the possibility of such damage.  See
  the GNU Lesser General Public License for more details.
  
  You should have received a copy of the GNU Lesser General Public License
  along with this library; if not, write to the Free Software Foundation,
  Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
*/

package cytoscape.visual.mappings.continuous.unitTests;

import cytoscape.visual.mappings.continuous.ContinuousMappingReader;
import cytoscape.visual.mappings.continuous.ContinuousRangeCalculator;
import cytoscape.visual.parsers.ColorParser;
import cytoscape.visual.parsers.ValueParser;
import junit.framework.TestCase;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

/**
 * Tests the ContinuousRangeCalculator.
 */
public class TestContinuousColorRangeCalculator extends TestCase {

    /**
     * Test with First Sample Data Set.
     * @throws Exception All Exceptions.
     */
    public void testCalculator1() throws Exception {
        InputStream in = getMapper1Props();
        Properties props = getProperties(in);

        ValueParser parser = new ColorParser();
        ContinuousMappingReader reader = new ContinuousMappingReader(props,
                "nodeColorCalculator.Sample.mapping", parser);
        ArrayList points = reader.getPoints();

        //  Create some sample values...
        HashMap bundle = new HashMap();
        double values[] = {-1.0, 0.07, 2.0, 4.0, 5.0, 10.0};
        for (int i = 0; i < values.length; i++) {
            String key = "key" + i;
            bundle.put(key, new Double(values[i]));
        }

        ContinuousRangeCalculator calc = new ContinuousRangeCalculator(points,
                reader.getInterpolator(), bundle);

        //  These are the colors we expect back...
        Color colors[] = {Color.BLUE, Color.BLUE, Color.BLUE, Color.BLUE,
                          Color.WHITE, Color.RED};

        //  Test that calculator returns the right colors.
        for (int i = 0; i < values.length; i++) {
            Object color = calc.calculateRangeValue("key" + i);
            assertEquals(colors[i], color);
        }
    }

    /**
     * Test with Second Sample Data Set.
     * @throws Exception All Exceptions.
     */
    public void testCalculator2() throws Exception {
        InputStream in = getMapper2Props();
        Properties props = getProperties(in);

        ValueParser parser = new ColorParser();
        ContinuousMappingReader reader = new ContinuousMappingReader(props,
                "nodeColorCalculator.Sample.mapping", parser);
        ArrayList points = reader.getPoints();

        //  Create some sample values...
        HashMap bundle = new HashMap();
        double values[] = {-1.0, 0.07, 2.0, 4.0, 5.0, 10.0};
        for (int i = 0; i < values.length; i++) {
            String key = "key" + i;
            bundle.put(key, new Double(values[i]));
        }

        ContinuousRangeCalculator calc = new ContinuousRangeCalculator(points,
                reader.getInterpolator(), bundle);

        //  These are the colors we expect back...
        Color colors[] = {
            new Color(0, 0, 255), //  Blue
            new Color(255, 251, 251), //  White (w/ some red)
            new Color(255, 153, 153), //  Pink
            new Color(255, 51, 51), //  Pinker
            new Color(255, 0, 0), //  Red
            new Color(0, 0, 0)           //  Black
        };

        //  Test that calculator returns the right colors.
        for (int i = 0; i < values.length; i++) {
            Object color = calc.calculateRangeValue("key" + i);
            assertEquals(colors[i], color);
        }
    }

    private Properties getProperties(InputStream in) throws IOException {
        Properties props = new Properties();
        props.load(in);
        return props;
    }

    private InputStream getMapper1Props() {
        String props = new String
                ("nodeColorCalculator.Sample.class=cytoscape.visual."
                + "calculators.GenericNodeColorCalculator\n"
                + "nodeColorCalculator.Sample.mapping.boundaryvalues=1\n"
                + "nodeColorCalculator.Sample.mapping.bv0.domainvalue=5.0\n"
                + "nodeColorCalculator.Sample.mapping.bv0.equal=255,255,255\n"
                + "nodeColorCalculator.Sample.mapping.bv0.greater=255,0,0\n"
                + "nodeColorCalculator.Sample.mapping.bv0.lesser=0,0,255\n"
                + "nodeColorCalculator.Sample.mapping.controller=Expression\n"
                + "nodeColorCalculator.Sample.mapping.interpolator="
                + "LinearNumberToColorInterpolator\n"
                + "nodeColorCalculator.Sample.mapping."
                + "type=ContinuousMapping\n");
        ByteArrayInputStream in = new ByteArrayInputStream(props.getBytes());
        return in;
    }

    private InputStream getMapper2Props() {
        String props = new String
                ("nodeColorCalculator.Sample.class=cytoscape.visual.calculators."
                + "GenericNodeColorCalculator\n"
                + "nodeColorCalculator.Sample.mapping.boundaryvalues=2\n"
                + "nodeColorCalculator.Sample.mapping.bv0.domainvalue=0.0\n"
                + "nodeColorCalculator.Sample.mapping.bv0.equal=255,255,255\n"
                + "nodeColorCalculator.Sample.mapping.bv0.greater=255,255,255\n"
                + "nodeColorCalculator.Sample.mapping.bv0.lesser=0,0,255\n"
                + "nodeColorCalculator.Sample.mapping.bv1.domainvalue=5.0\n"
                + "nodeColorCalculator.Sample.mapping.bv1.equal=255,0,0\n"
                + "nodeColorCalculator.Sample.mapping.bv1.greater=0,0,0\n"
                + "nodeColorCalculator.Sample.mapping.bv1.lesser=255,0,0\n"
                + "nodeColorCalculator.Sample.mapping.controller=Expression\n"
                + "nodeColorCalculator.Sample.mapping.interpolator="
                + "LinearNumberToColorInterpolator\n"
                + "nodeColorCalculator.Sample.mapping.type=ContinuousMapping\n");
        ByteArrayInputStream in = new ByteArrayInputStream(props.getBytes());
        return in;
    }
}
