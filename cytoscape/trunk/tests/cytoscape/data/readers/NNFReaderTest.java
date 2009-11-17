package cytoscape.data.readers;

import java.io.File;

import junit.framework.TestCase;

public class NNFReaderTest extends TestCase {

	private static final String FILE_LOCATION = "testData/NNFData/";
	
	public void setUp() throws Exception {
		
	}

	public void tearDown() throws Exception {

	}

	public void testGood1() throws Exception {
		final NNFReader reader = new NNFReader(FILE_LOCATION + "good1.nnf");
		reader.read();
	}
	
	
	public void testGood2() {
		assertTrue(true);
	}
	
	
	public void testGood3() {
		assertTrue(true);
	}
	
	
	public void testGood4() {
		assertTrue(true);
	}
	
	
	public void testGood5() {
		assertTrue(true);
	}
	
}
