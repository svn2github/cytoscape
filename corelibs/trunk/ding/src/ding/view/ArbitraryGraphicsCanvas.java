
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
import java.awt.Color;
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

	/**
	 * Constructor.
	 *
	 * @param backgroundColor Color
	 * @param isVisible boolean
	 * @param isOpaque boolean
	 */
	public ArbitraryGraphicsCanvas(Color backgroundColor, boolean isVisible, boolean isOpaque) {

		// init members
		m_backgroundColor = backgroundColor;
		m_isVisible = isVisible;
		m_isOpaque = isOpaque;
	}

	/**
	 * Our implementation of JComponent setBounds.
	 */
	public void setBounds(int x, int y, int width, int height) {
		super.setBounds(x, y, width, height);

		// our bounds have changed, create a new image with new size
		if ((width > 0) && (height > 0)) {
			// create the buffered image
			m_img = new BufferedImage(width,
									  height,
									  BufferedImage.TYPE_INT_ARGB);
			// probably need to do some scaling of the children here
		}
	}

	/**
	 * Our implementation of paint.
	 * Invoked by Swing to draw components.
	 *
	 * @param graphics Graphics
	 */
    public void paint(Graphics graphics) {

		// only paint if we have an image to paint on
		if (m_img != null) {

			// get image graphics
			Graphics2D image2D = ((BufferedImage)m_img).createGraphics();

			// first clear the image
			clearImage(image2D);

			// now paint children
			if (m_isVisible) paintChildren(image2D);

			// render image
			((Graphics2D)graphics).drawImage(((BufferedImage)m_img), null, 0, 0);
		}
    }

    /**
     * Invoke this method to print the component.
     *
     * @param graphics Graphics
     */
    public void print(Graphics graphics) {

		//if we have an image to print, lets print it.
		if (m_img != null) {
			((Graphics2D)graphics).drawImage(((BufferedImage)m_img), null, 0, 0);
		}
	}

	/**
	 * Utility function to clean the background of the image,
	 * using m_backgroundColor
	 *
	 * image2D Graphics2D
	 */
	private void clearImage(Graphics2D image2D) {

		// set color alpha based on opacity setting
		int alpha = (m_isOpaque) ? 255 : 0;
		Color backgroundColor = new Color(m_backgroundColor.getRed(),
										  m_backgroundColor.getGreen(),
										  m_backgroundColor.getBlue(),
										  alpha);

		// set the alpha composite on the image, and clear its area
		Composite origComposite = image2D.getComposite();
		image2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC));
		image2D.setPaint(backgroundColor);
		image2D.fillRect(0, 0, m_img.getWidth(null), m_img.getHeight(null));
		image2D.setComposite(origComposite);
	}
}
