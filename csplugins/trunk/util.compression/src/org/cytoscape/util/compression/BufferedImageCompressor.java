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
import java.io.ByteArrayOutputStream;


public class BufferedImageCompressor {
	static public int[] compress(final BufferedImage image) throws IllegalStateException {
		final int[] imageAsIntArray;
		if (image.getRaster().getDataBuffer() instanceof DataBufferInt) {
			final DataBufferInt dataBuffer = (DataBufferInt)image.getRaster().getDataBuffer();
			imageAsIntArray = dataBuffer.getData();
		}
		else
			throw new IllegalStateException("image has an unknown internal representation!");

		return (new RLEIntCompressor()).compress(imageAsIntArray);
	}

	static public BufferedImage expand(final int[] compressedImage, final int width, final int height) throws IllegalStateException {
		final int[] imageAsIntArray = (new RLEIntCompressor()).expand(compressedImage);

		final BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB_PRE);
		if (image.getRaster().getDataBuffer() instanceof DataBufferInt) {
			final DataBufferInt dataBuffer = (DataBufferInt)image.getRaster().getDataBuffer();
			for (int i = 0; i < imageAsIntArray.length; ++i)
				dataBuffer.setElem(i, imageAsIntArray[i]);
		}
		else
			throw new IllegalStateException("image has an unknown internal representation!");

		return image;
	}
}
