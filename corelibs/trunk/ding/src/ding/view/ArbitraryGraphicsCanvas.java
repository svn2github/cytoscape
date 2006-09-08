
/*
  File: NetworkViewManager.java 
  
  Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)
  
  The Cytoscape Consortium is: 
  - Institute for Systems Biology
  - University of California San Diego
  - Memorial Sloan-Kettering Cancer Center
  - Institut Pasteur
  - Agilent Technologies
  
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

// package
package ding.view;

// import
import java.awt.Paint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Composite;
import java.awt.AlphaComposite;
import java.awt.image.BufferedImage;

/**
 * This class extends cytoscape.view.CytoscapeCanvas.  Its meant
 * to live within a ding.view.DGraphView class.  It is the canvas
 * used for arbitrary graphics drawing (background & foreground panes).
 */
public class ArbitraryGraphicsCanvas extends DingCanvas {

	public ArbitraryGraphicsCanvas(Paint backgroundPaint) {

		// init members
		this.m_bgPaint = backgroundPaint;
	}

	/**
	 * Our implementation of JComponent setBounds.
	 */
	public void setBounds(int x, int y, int width, int height) {
		super.setBounds(x, y, width, height);
		if ((width > 0) && (height > 0)) {
			// create the buffered image
			m_img = new BufferedImage(width,
									  height,
									  BufferedImage.TYPE_INT_ARGB);
		}
	}

	/**
	 * Our implementation of paint.
	 * Invoked by Swing to draw components.
	 *
	 * @param g Graphics
	 */
    public void paint(Graphics g) {

		// only paint if we have an image to paint on
		if (m_img != null) {

			// get image graphics
			Graphics2D image2D = ((BufferedImage)m_img).createGraphics();

			// first clear the image
			clearImage(image2D);

			// render image
			((Graphics2D)g).drawImage(((BufferedImage)m_img), null, 0, 0);
		}
    }

	/**
	 * Our implementation of update.
	 *
	 * @param g Graphics
	 */
    public void update(Graphics g) {
        paint(g);
	}

	/**
	 * Utility function to clean the background of the image,
	 * using m_bgPaint.
	 *
	 * image2D Graphics2D
	 */
	private void clearImage(Graphics2D image2D) {

		final Composite origComposite = image2D.getComposite();
		image2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC));
		image2D.setPaint(m_bgPaint);
		image2D.fillRect(0, 0, m_img.getWidth(null), m_img.getHeight(null));
		image2D.setComposite(origComposite);
	}
}
