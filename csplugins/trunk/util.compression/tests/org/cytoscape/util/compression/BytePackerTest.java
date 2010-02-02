package org.cytoscape.util.compression;


import junit.framework.*;


public class BytePackerTest extends TestCase {
	public void testPack() {
		final byte[] bytes = { 4, 3, 2, 1, 0xA, 0xB, 0xC, 0xD };
		final int[] ints = BytePacker.pack(bytes);
		assertEquals(ints.length, 2);
		assertEquals(ints[0], 0x04030201);
		assertEquals(ints[1], 0x0A0B0C0D);
	}


	public void testUnpack() {
		final int ints[] = { 0x04030201, 0x0A0B0C0D };
		final byte[] bytes = BytePacker.unpack(ints);
		assertEquals(bytes.length, 8);
		assertEquals(bytes[0], 4);
		assertEquals(bytes[1], 3);
		assertEquals(bytes[2], 2);
		assertEquals(bytes[3], 1);
		assertEquals(bytes[4], 0xA);
		assertEquals(bytes[5], 0xB);
		assertEquals(bytes[6], 0xC);
		assertEquals(bytes[7], 0xD);
	}
}
