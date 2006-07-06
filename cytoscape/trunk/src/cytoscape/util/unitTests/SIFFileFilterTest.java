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
import cytoscape.data.readers.InteractionsReader;
import cytoscape.util.SIFFileFilter;

/**
 * Tests Implementation of CyFileFilter.
 *
 * TODO:  Add to DataSuite
 */
public class SIFFileFilterTest extends TestCase {
	SIFFileFilter test;
	File DUMMY_SIF_FILE;
	GraphReader reader;
	
	public void setUp() throws Exception {
		test = new SIFFileFilter();
		DUMMY_SIF_FILE = File.createTempFile("inputSifTest", ".sif");
	}
	
	public void tearDown() throws Exception {
		test = null;
		DUMMY_SIF_FILE.delete();
	}

	public void testGetReader() throws Exception {
		reader = test.getReader(DUMMY_SIF_FILE.toString());
		assertEquals(reader.getClass(), InteractionsReader.class);
	}
}