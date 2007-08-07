package cytoscape.util;

import java.io.File;

import junit.framework.TestCase;
import cytoscape.data.readers.GMLReader;
import cytoscape.data.readers.GraphReader;
import cytoscape.util.GMLFileFilter;

/**
 * Tests Implementation of CyFileFilter.
 *
 * TODO:  Add to DataSuite
 */
public class GMLFileFilterTest extends TestCase {
	GMLFileFilter test;
	File DUMMY_GML_FILE;
	GraphReader reader;
	
	public void setUp() throws Exception {
		test = new GMLFileFilter();
		DUMMY_GML_FILE = File.createTempFile("inputGmlTest", ".gml");
	}
	
	public void tearDown() throws Exception {
		test = null;
		DUMMY_GML_FILE.delete();
	}

	public void testGetReader() throws Exception {
		reader = test.getReader(DUMMY_GML_FILE.toString());
		assertEquals(reader.getClass(), GMLReader.class);
	}
}
