package org.cytoscape.util.compression;


import junit.framework.*;


public class RLEIntCompressorTest extends TestCase {
	private final IntCompressor compressor = new RLEIntCompressor();

	public void testCompress() {
		final int[] rawData = { 1, 2, 3, 3, 3, 3, 4, 5, 5, 5, 6 };
		final int[] compressedData = compressor.compress(rawData);
		final int[] expectedCompressedData = { 1, 1, 1, 2, 4, 3, 1, 4, 3, 5, 1, 6 };

		assertEquals(expectedCompressedData.length, compressedData.length);
		for (int i = 0; i < expectedCompressedData.length; ++i)
			assertEquals(expectedCompressedData[i], compressedData[i]);
	}

	public void testExpand() {
		final int[] compressedData = { 1, 1, 1, 2, 4, 3, 1, 4, 3, 5, 1, 6 };
		final int[] expandedData = compressor.expand(compressedData);
		final int[] expectedExpandedData = { 1, 2, 3, 3, 3, 3, 4, 5, 5, 5, 6 };

		assertEquals(expectedExpandedData.length, expandedData.length);
		for (int i = 0; i < expectedExpandedData.length; ++i)
			assertEquals(expectedExpandedData[i], expandedData[i]);
	}
}
