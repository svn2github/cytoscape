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
