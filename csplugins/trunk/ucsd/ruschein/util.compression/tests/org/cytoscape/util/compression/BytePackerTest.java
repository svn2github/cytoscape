/**
 * @author ruschein
 */
/*
  Copyright (c) 2010, The Cytoscape Consortium (www.cytoscape.org)

  This library is free software; you can redistribute it and/or modify it
  under the terms of the GNU Lesser General Public License as published
  by the Free Software Foundation; either version 2.1 of the License, or
  any later version.

  This library is distributed in the hope that it will be useful, but
  WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
  MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
  documentation provided hereunder is on an "as is" basis, and the
  Institute for Systems Biology and the Whitehead Institute
  have no obligations to provide maintenance, support,
  updates, enhancements or modifications.  In no event shall the
  Institute for Systems Biology and the Whitehead Institute
  be liable to any party for direct, indirect, special,
  incidental or consequential damages, including lost profits, arising
  out of the use of this software and its documentation, even if the
  Institute for Systems Biology and the Whitehead Institute
  have been advised of the possibility of such damage.  See
  the GNU Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License
  along with this library; if not, write to the Free Software Foundation,
  Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
*/
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
