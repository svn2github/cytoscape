
/*
 Copyright (c) 2006, 2007, The Cytoscape Consortium (www.cytoscape.org)

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

package cytoscape.editor.impl;

import cytoscape.editor.CytoscapeEditorManager;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;


/**
 *
 */
public class TextAnnotation extends JComponent {
	// MLC 05/09/07:
	private static final long serialVersionUID = -2388758275776657147L;
	private BufferedImage img; // image for figure we are drawing
	private int x1;
	private int y1;
	private int h1;
	private int w1;
	private BufferedImage image; // enclosing image for rendering on the
	                             // canvas
	private String text;

	/**
	 * Creates a new TextAnnotation object.
	 *
	 * @param text  DOCUMENT ME!
	 * @param x1  DOCUMENT ME!
	 * @param y1  DOCUMENT ME!
	 * @param w1  DOCUMENT ME!
	 * @param h1  DOCUMENT ME!
	 */
	public TextAnnotation(String text, int x1, int y1, int w1, int h1) {
		//		super();
		//		this.img = new BufferedImage(44, 33, BufferedImage.TYPE_INT_ARGB);
		//		Graphics2D grafx = img.createGraphics();
		this.text = text;
		// MLC 05/09/07:
		// this.img = img;
		this.x1 = x1;
		this.y1 = y1;
		this.w1 = w1;
		this.h1 = y1;

		JLabel label = new JLabel(text);
		this.add(label);
		label.setVisible(true);
		label.setOpaque(true);

		setBounds(x1, y1, w1, h1);
		label.repaint();
		this.repaint();
	}

	//	public void setBounds(int x, int y, int width, int height) {
	/**
	 *  DOCUMENT ME!
	 *
	 * @param x DOCUMENT ME!
	 * @param y DOCUMENT ME!
	 * @param width DOCUMENT ME!
	 * @param height DOCUMENT ME!
	 */
	public void setBounds(int x, int y, int width, int height) {
		super.setBounds(x, y, width, height);

		// set member vars
		this.x1 = x;
		this.y1 = y;
		this.w1 = width;
		this.h1 = height;

		// our bounds have changed, create a new image with new size
		if ((width > 0) && (height > 0)) {
			image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		}
	}

	//	}
	/**
	 *  DOCUMENT ME!
	 *
	 * @param g DOCUMENT ME!
	 */
	public void paint(Graphics g) {
		Graphics2D image2D = image.createGraphics();

		// draw into the image
		Composite origComposite = image2D.getComposite();
		image2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC));
		////		image2D.drawString(this.text, this.x1, this.y1);
		CytoscapeEditorManager.log("drawing " + text + " to " + image);

		image2D.setColor(Color.red);
		//		image2D.fillRect(0, 0, image.getWidth(null), image.getHeight(null));
		image2D.setColor(Color.black);
		image2D.drawString(text, 10, 10);
		image2D.setComposite(origComposite);

		((Graphics2D) g).drawImage(image, null, 0, 0);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public int getH1() {
		return h1;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param h1 DOCUMENT ME!
	 */
	public void setH1(int h1) {
		this.h1 = h1;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public int getW1() {
		return w1;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param w1 DOCUMENT ME!
	 */
	public void setW1(int w1) {
		this.w1 = w1;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public int getX1() {
		return x1;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param x1 DOCUMENT ME!
	 */
	public void setX1(int x1) {
		this.x1 = x1;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public int getY1() {
		return y1;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param y1 DOCUMENT ME!
	 */
	public void setY1(int y1) {
		this.y1 = y1;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public BufferedImage getImg() {
		return img;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param img DOCUMENT ME!
	 */
	public void setImg(BufferedImage img) {
		this.img = img;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String getText() {
		return text;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param text DOCUMENT ME!
	 */
	public void setText(String text) {
		this.text = text;
	}
}
