package cytoscape.data.readers;

import java.io.IOException;

import junit.framework.TestCase;

/**
 * Test code for Nested Network Format file reader.
 * 
 * @author kono, ruschein
 * @since Cytoscape 2.7.0
 */
public class NNFReaderTest extends TestCase {
	
	// All test data files are in this directory.
	private static final String FILE_LOCATION = "testData/NNFData/";

	
	public void setUp() throws Exception {
		/* Intentionally empty! */
	}


	public void tearDown() throws Exception {
		/* Intentionally empty! */
	}


	public void testGood1() throws Exception {
		final NNFReader reader = new NNFReader(FILE_LOCATION + "good1.nnf");
		reader.read();
		
		assertNotNull(reader.getFirstNetwork());
		assertEquals("root", reader.getFirstNetwork().getTitle());
	}
	
	
	public void testGood2() throws Exception {
		final NNFReader reader = new NNFReader(FILE_LOCATION + "good2.nnf");
		reader.read();
		
		assertNotNull(reader.getFirstNetwork());
		assertEquals("root", reader.getFirstNetwork().getTitle());
	}
	
	
	public void testGood3() throws Exception {
		final NNFReader reader = new NNFReader(FILE_LOCATION + "good3.nnf");
		reader.read();
		
		assertNotNull(reader.getFirstNetwork());
		assertEquals("root", reader.getFirstNetwork().getTitle());
	}
	
	
	public void testGood4() throws Exception {
		final NNFReader reader = new NNFReader(FILE_LOCATION + "good4.nnf");
		reader.read();
		
		assertNotNull(reader.getFirstNetwork());
		assertEquals("root", reader.getFirstNetwork().getTitle());
	}
	
	
	public void testGood5() throws Exception {
		final NNFReader reader = new NNFReader(FILE_LOCATION + "good5.nnf");
		reader.read();
		
		assertNotNull(reader.getFirstNetwork());
		assertEquals("TopLevelNetwork", reader.getFirstNetwork().getTitle());
	}
	
	
	public void testBad1() throws Exception {
		final NNFReader reader = new NNFReader(FILE_LOCATION + "bad1.nnf");
		try {
			reader.read();
		} catch (IOException e) {
			e.printStackTrace();
			assertNotNull(reader.getFirstNetwork());
			return;
		}
		
		//If not caught by the above, something is wrong!
		fail();
	}
	
	
	public void testBad2() throws Exception {
		final NNFReader reader = new NNFReader(FILE_LOCATION + "bad2.nnf");
		try {
			reader.read();
		} catch (IOException e) {
			e.printStackTrace();
			assertNotNull(reader.getFirstNetwork());
			return;
		}
		
		//If not caught by the above, something is wrong!
		fail();
	}
}
