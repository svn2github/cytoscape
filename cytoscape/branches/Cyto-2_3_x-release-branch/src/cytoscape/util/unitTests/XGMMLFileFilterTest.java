package cytoscape.util.unitTests;

import java.io.File;

import junit.framework.TestCase;
import cytoscape.data.readers.GraphReader;
import cytoscape.data.readers.XGMMLReader;
import cytoscape.util.XGMMLFileFilter;

/**
 * Tests Implementation of CyFileFilter.
 * 
 * TODO: Add to DataSuite
 */
public class XGMMLFileFilterTest extends TestCase {
	XGMMLFileFilter test;
	File DUMMY_XGMML_FILE;
	GraphReader reader;

	public void setUp() throws Exception {
		test = new XGMMLFileFilter();
		DUMMY_XGMML_FILE = File.createTempFile("inputXGMMLTest", ".xgmml");
	}

	public void tearDown() throws Exception {
		test = null;
		DUMMY_XGMML_FILE.delete();
	}

	public void testGetReader() throws Exception {
		reader = test.getReader(DUMMY_XGMML_FILE.toString());
		assertEquals(reader.getClass(), XGMMLReader.class);
	}
}