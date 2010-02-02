package org.cytoscape.util.compression;


/**
 * Class to maintain a dynamically growing int array.
 */
class DynamicIntArray {
	private static final int DEFAULT_INITIAL_CAPACITY = 1000;
	private int[] array;
	private int nextIndex = 0;

	DynamicIntArray() {
		this(0);
	}

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

	/**
	 * @return An array of the exact size of the data.
	 */
	int[] toArray() {
		final int[] retval = new int[nextIndex];
		System.arraycopy(array, 0, retval, 0, nextIndex);

		return retval;
	}

	/**
	 * Used to grow our internal array.  This is done by capacity doubling which implies that the
	 * amortised asymptotic complexity of append() is O(1) which is the best we can hope for!
	 */
	private void resize() {
		final int[] newArray = new int[array.length << 1];
		System.arraycopy(array, 0, newArray, 0, array.length);
		array = newArray;
	}
}


/**
 * Class that implements run-length encoding of int arrays.
 */
public class RLEIntCompressor implements IntCompressor {
	public int[] compress(final int[] uncompressedData) {
		int runLength = 0;
		int currentValue = uncompressedData[0];

		final DynamicIntArray dynArray = new DynamicIntArray(uncompressedData.length);

		for (final int value : uncompressedData) {
			if (value == currentValue) {
				++runLength;
				if (runLength == Integer.MAX_VALUE) { // Yes, I am *that* paranoid!  ;)
					dynArray.append(runLength);
					dynArray.append(currentValue);
					runLength = 0;
				}
			}
			else {
				dynArray.append(runLength);
				dynArray.append(currentValue);
				runLength = 1;
				currentValue = value;
			}
		}

		// Unfinished business?
		if (runLength > 0) {
			dynArray.append(runLength);
			dynArray.append(currentValue);
		}

		return dynArray.toArray();
	}

	public int[] expand(final int[] compressedData) throws IllegalStateException {
		final DynamicIntArray dynArray = new DynamicIntArray(compressedData.length << 2);

		try {
			int i = 0;
			while (i < compressedData.length) {
				final int runLength = compressedData[i++];
				if (runLength <= 0)
					throw new IllegalStateException("invalid run length <= 0!");

				final int value =  compressedData[i++];

				for (int k = 0; k < runLength; ++k)
					dynArray.append(value);
			}
		} catch (final ArrayIndexOutOfBoundsException e) {
			throw new IllegalStateException("garbled compressed data!");
		}

		return dynArray.toArray();
	}
}
