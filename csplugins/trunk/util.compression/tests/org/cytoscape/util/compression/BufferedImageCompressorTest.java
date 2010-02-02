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


import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;
import java.awt.image.Raster;
import junit.framework.*;


public class BufferedImageCompressorTest extends TestCase {
	public void testCompress() {
		final int IMG_WIDTH = 2;
		final int IMG_HEIGHT = 3;
		final BufferedImage image = new BufferedImage(IMG_WIDTH, IMG_HEIGHT, BufferedImage.TYPE_INT_ARGB_PRE);

		assertEquals(IMG_WIDTH, image.getWidth());
		assertEquals(IMG_HEIGHT, image.getHeight());

		final int[] compressedImage = BufferedImageCompressor.compress(image);
	}

	public void testExpand() {
		final int IMG_WIDTH = 2;
		final int IMG_HEIGHT = 3;
		final BufferedImage origImage = new BufferedImage(IMG_WIDTH, IMG_HEIGHT, BufferedImage.TYPE_INT_ARGB_PRE);
		final DataBufferInt origDataBuffer = (DataBufferInt)origImage.getRaster().getDataBuffer();
		origDataBuffer.setElem(1, 0xA);
		origDataBuffer.setElem(3, 0xD);
		final int[] origImageData = origDataBuffer.getData();

		assertEquals(IMG_WIDTH, origImage.getWidth());
		assertEquals(IMG_HEIGHT, origImage.getHeight());

		final int[] compressedImage = BufferedImageCompressor.compress(origImage);

		final BufferedImage expandedImage = BufferedImageCompressor.expand(compressedImage, IMG_WIDTH, IMG_HEIGHT);
		final DataBufferInt dataBuffer = (DataBufferInt)expandedImage.getRaster().getDataBuffer();
		final int[] imageData = dataBuffer.getData();

		assertEquals(origImageData.length, imageData.length);
		for (int i = 0; i < origImageData.length; ++i)
			assertEquals(origImageData[i], imageData[i]);
		for (int i = 0; i < origImageData.length; ++i)
			System.out.println(origImageData[i]);
	}
}
