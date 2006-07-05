package cytoscape.data.unitTests;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import java.io.File;
import java.util.Hashtable;
import java.util.Enumeration;
import java.util.Set;
import java.io.FilenameFilter;
import javax.swing.*;
import javax.swing.filechooser.*;
import cytoscape.data.readers.GraphReader;
import cytoscape.util.CyFileFilter;
import cytoscape.data.readers.GMLReader;
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