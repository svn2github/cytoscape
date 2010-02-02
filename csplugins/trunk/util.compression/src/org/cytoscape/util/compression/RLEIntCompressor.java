package org.cytoscape.util.compression;


class DynamicIntArray {
	private static final int DEFAULT_INITIAL_CAPACITY = 1000;
	private int[] array;
	private int nextIndex = 0;

	DynamicIntArray(final int initialCapacity) {
		if (initialCapacity > 0)
			array = new int[initialCapacity];
		else
			array = new int[DEFAULT_INITIAL_CAPACITY];
	}

	void append(final int newValue) {
		if (nextIndex >= array.length)
			resize();

		array[nextIndex++] = newValue;
	}

	private void resize() {
		final int[] newArray = new int[array.length << 1];
		System.arraycopy(array, 0, newArray, 0, array.length);
		array = newArray;
	}
}


public class RLEIntCompressor {
	public int[] compress(final int[] uncompressedData) {
	}

	public int[] expand(final int[] compressedData) {
	}
}
