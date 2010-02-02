package org.cytoscape.util.compression;


interface IntCompressor {
	int[] compress(final int[] uncompressedData);
	int[] expand(final int[] compressedData);
}
