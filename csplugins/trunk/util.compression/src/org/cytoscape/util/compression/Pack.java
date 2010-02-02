package org.cytoscape.util.compression;


/**
 * Class to convert between int and byte arrays.
 */
public class Pack {
	/**
	 * Converts a byte array to an int array.  This requires that the byte array has a size that
	 * is a multiple of 4!  The byte order in the ints will be the first original byte being the
	 * most-significant byte in the first int and so on...
	 */
	static public int[] pack(final byte[] data) throws IllegalStateException {
		if ((data.length % 4) != 0)
			throw new IllegalStateException("data size must be a multiple of 4!");

		final int[] retval = new int[data.length >> 2];

		for (int i = 0; i < retval.length; ++i) {
			retval[i] = (int)data[i << 4] | (int) data[(i << 4) + 1]
			            | (int) data[(i << 4) + 2] |(int) data[(i << 4) + 3];
		}

		return retval;
	}

	/**
	 * Converts an int array to a byte array.  The bytes will be in most-significant to least significant order.
	 */
	static public byte[] unpack(final int[] data) {
		final byte[] retval = new byte[data.length << 2];

		for (int i = 0; i < data.length; ++i) {
			int value = data[i];
			retval[i << 2] = (byte)(value >> 24);
			retval[(i << 2) + 1] = (byte)((value >> 16) & 0xFF);
			retval[(i << 2) + 2] = (byte)((value >> 8) & 0xFF);
			retval[(i << 2) + 3] = (byte)(value & 0xFF);
		}

		return retval;
	}
}
