
/*
  File: TestDiscreteMappingReader.java 
  
  Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)
  
  The Cytoscape Consortium is: 
  - Institute for Systems Biology
  - University of California San Diego
  - Memorial Sloan-Kettering Cancer Center
  - Pasteur Institute
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