package cytoscape.data.readers;

import java.io.File;
import junit.framework.TestCase;
import java.io.IOException;


public class NNFReaderTest extends TestCase {
	private static final String FILE_LOCATION = "testData/NNFData/";

	
	public void setUp() throws Exception {
		/* Intentionally empty! */
	}


	public void tearDown() throws Exception {
		/* Intentionally empty! */
	}


	public void testGood1() throws Exception {
		final NNFReader reader = new NNFReader(FILE_LOCATION + "good1.nnf");
		try {
			reader.read();
		}
		catch (final IOException e) {
			assertTrue(false); // We should never get here!
		}
	}
	
	
	public void testGood2() {
		final NNFReader reader = new NNFReader(FILE_LOCATION + "good2.nnf");
		try {
			reader.read();
		}
		catch (final IOException e) {
			assertTrue(false); // We should never get here!
		}
	}
	
	
	public void testGood3() {
		final NNFReader reader = new NNFReader(FILE_LOCATION + "good3.nnf");
		try {
			reader.read();
		}
		catch (final IOException e) {
			assertTrue(false); // We should never get here!
		}
	}
	
	
	public void testGood4() {
		final NNFReader reader = new NNFReader(FILE_LOCATION + "good4.nnf");
		try {
			reader.read();
		}
		catch (final IOException e) {
			assertTrue(false); // We should never get here!
		}
	}
	
	
	public void testGood5() {
		final NNFReader reader = new NNFReader(FILE_LOCATION + "good5.nnf");
		try {
			reader.read();
		}
		catch (final IOException e) {
			assertTrue(false); // We should never get here!
		}
	}
	
	
	public void testBad1() {
		final NNFReader reader = new NNFReader(FILE_LOCATION + "bad1.nnf");
		try {
			reader.read();
		}
		catch (final IOException e) {
			return;
		}

		assertTrue(false); // We should never get here!
	}
	
	
	public void testBad2() {
		final NNFReader reader = new NNFReader(FILE_LOCATION + "bad2.nnf");
		try {
			reader.read();
		}
		catch (final IOException e) {
			return;
		}

		assertTrue(false); // We should never get here!
	}
}
