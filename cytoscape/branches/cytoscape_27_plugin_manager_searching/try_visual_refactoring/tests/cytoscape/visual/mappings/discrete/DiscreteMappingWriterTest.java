
/*
  File: DiscreteMappingWriterTest.java 
  
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

//----------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//----------------------------------------------------------------------------
package cytoscape.visual.mappings.discrete;

import cytoscape.visual.mappings.discrete.DiscreteMappingReader;
import cytoscape.visual.mappings.discrete.DiscreteMappingWriter;
import cytoscape.visual.parsers.ColorParser;
import cytoscape.visual.parsers.ValueParser;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import junit.framework.TestCase;

import java.io.InputStream;
import java.util.Properties;
import java.util.TreeMap;

/**
 * Tests the DiscreteMappingWriter Class.
 */
public class DiscreteMappingWriterTest extends TestCase {

    /**
     * Tests the DiscreteMappingWriter Class.
     * @throws Exception All Exceptions.
     */
    public void testWriter() throws Exception {

        //  Read in a Properties File
        String baseKey = "nodeColorCalculator.JUnitDiscreteColor.mapping";
        ValueParser parser = new ColorParser();
        InputStream in = DiscreteMappingReaderTest.getDataFile();
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

    public void testControllerTypeWriting() throws Exception {

        //  Read in a Properties File
        String baseKey = "nodeColorCalculator.JUnitDiscreteColor.mapping";
        ValueParser parser = new ColorParser();
        InputStream in = DiscreteMappingReaderTest.getControllerTypeDataFile();
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
        String mapA = newProps.getProperty(baseKey + ".map.1");
        assertEquals("204,255,255", mapA);

        String mapY = newProps.getProperty(baseKey + ".map.6");
        assertEquals("255,51,51", mapY);

        //  Test Controlling Attribute
        String type = newProps.getProperty(baseKey + ".controller");
        assertEquals("controller","homer", type);

	// while you would think that this should be 3, the "homer"
	// attribute has not been set, therefore the attr won't be
	// found so the type is unknown
        String ctype = newProps.getProperty(baseKey + ".controllerType");
        assertEquals("controllerType","-1", ctype);
    }

    public void testControllerTypeWritingWithAttr() throws Exception {

        //  Read in a Properties File
        String baseKey = "nodeColorCalculator.JUnitDiscreteColor.mapping";
        ValueParser parser = new ColorParser();
        InputStream in = DiscreteMappingReaderTest.getControllerTypeDataFile();
        Properties properties = new Properties();
        properties.load(in);

	// Set the attribute so that it's type will be found
	// 15 == random int value
	Cytoscape.getNodeAttributes().setAttribute("id","homer",15); 

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
        String mapA = newProps.getProperty(baseKey + ".map.1");
        assertEquals("204,255,255", mapA);

        String mapY = newProps.getProperty(baseKey + ".map.6");
        assertEquals("255,51,51", mapY);

        //  Test Controlling Attribute
        String type = newProps.getProperty(baseKey + ".controller");
        assertEquals("controller","homer", type);

        String ctype = newProps.getProperty(baseKey + ".controllerType");
        assertEquals("controllerType","3", ctype);
    }
}
