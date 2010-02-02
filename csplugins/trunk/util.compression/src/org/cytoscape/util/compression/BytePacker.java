package org.cytoscape.util.compression;


/**
 * Class to convert between int and byte arrays.
 */
public class BytePacker {
	/**
	 * Converts a byte array to an int array.  This requires that the byte array has a size that
	 * is a multiple of 4!  The byte order in the ints will be the first original byte being the
	 * most-significant byte in the first int and so on...
	 */
	static public int[] pack(final byte[] bytes) throws IllegalStateException {
		if ((bytes.length % 4) != 0)
			throw new IllegalStateException("input array size must be a multiple of 4!");

		final int[] ints = new int[bytes.length >> 2];

		for (int i = 0; i < ints.length; ++i) {
			ints[i] = ((int)(bytes[i << 2]) << 24) | ((int)(bytes[(i << 2) + 1]) << 16)
			          | ((int)(bytes[(i << 2) + 2]) << 8) | (int)bytes[(i << 2) + 3];
		}

		return ints;
	}

	/**
	 * Converts an int array to a byte array.  The bytes will be in most-significant to least significant order.
	 */
	static public byte[] unpack(final int[] ints) {
		final byte[] bytes = new byte[ints.length << 2];

		for (int i = 0; i < ints.length; ++i) {
			int value = ints[i];
			bytes[i << 2] = (byte)(value >> 24);
			bytes[(i << 2) + 1] = (byte)((value >> 16) & 0xFF);
			bytes[(i << 2) + 2] = (byte)((value >> 8) & 0xFF);
			bytes[(i << 2) + 3] = (byte)(value & 0xFF);
		}

		return bytes;
	}
}
