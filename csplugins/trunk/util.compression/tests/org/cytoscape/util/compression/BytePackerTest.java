package org.cytoscape.util.compression;


import junit.framework.*;


public class BytePackerTest extends TestCase {
	public void testPack() {
		final byte[] bytes = { 4, 3, 2, 1, 0xA, 0xB, 0xC, 0xD };
		final int[] ints = BytePacker.pack(bytes);
		assertEquals(ints.length, 2);
		assertEquals(0x04030201, ints[0]);
		assertEquals(0x0A0B0C0D, ints[1]);
	}


	public void testUnpack() {
		final int ints[] = { 0x04030201, 0x0A0B0C0D };
		final byte[] bytes = BytePacker.unpack(ints);
		assertEquals(8, bytes.length);
		assertEquals(4, bytes[0]);
		assertEquals(3, bytes[1]);
		assertEquals(2, bytes[2]);
		assertEquals(1, bytes[3]);
		assertEquals(0xA, bytes[4]);
		assertEquals(0xB, bytes[5]);
		assertEquals(0xC, bytes[6]);
		assertEquals(0xD, bytes[7]);
	}
}
