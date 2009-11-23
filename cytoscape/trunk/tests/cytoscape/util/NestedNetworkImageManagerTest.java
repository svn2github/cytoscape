package cytoscape.util;

import cytoscape.data.readers.NNFReader;
import junit.framework.TestCase;

public class NestedNetworkImageManagerTest extends TestCase {
	
	private static final String FILE_LOCATION = "testData/NNFData/";

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testNestedNetworkImageManager() throws Exception {
		// Load nested network
		TestUtil.destroyNetworksEdgesAndNodes();

		final NNFReader reader = new NNFReader(FILE_LOCATION + "good3.nnf");
		reader.read();
		
		assertEquals(3, NestedNetworkImageManager.getNetworkImageGenerator().getImageCount());
		
	}

}
