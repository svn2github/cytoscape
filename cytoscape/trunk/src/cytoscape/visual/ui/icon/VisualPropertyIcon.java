
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

package cytoscape.visual.ui.icon;

import java.awt.Color;
import java.awt.Shape;

import javax.swing.ImageIcon;


/**
 *
 * Icon created from Shape object passed from rendering engine.<br>
 *
 * This icon is scalable (vector image).
 *
 * Actual paint method is defined in child classes.
 *
 * @version 0.5
 * @since Cytoscape 2.5
 * @author kono
 *
 */
public abstract class VisualPropertyIcon extends ImageIcon {
	/**
	 * Default icon color.
	 */
	public static final Color DEFAULT_ICON_COLOR = Color.DARK_GRAY;

	/**
	 * 
	 */
	public static final int DEFAULT_ICON_SIZE = 32;
	protected int height;
	protected int width;
	protected Color color;
	protected Shape shape;
	protected String name;
	protected int pad = 0;

	/**
	 * Creates a new VisualPropertyIcon object.
	 *
	 * @param shape  DOCUMENT ME!
	 * @param name  DOCUMENT ME!
	 */
	public VisualPropertyIcon(String name, Color color) {
		this(null, DEFAULT_ICON_SIZE, DEFAULT_ICON_SIZE, name, color);
	}

	/**
	 * Constructor without Color parameter.
	 *
	 * @param shape
	 * @param width
	 * @param height
	 * @param name
	 */
	public VisualPropertyIcon(Shape shape, int width, int height, String name) {
		this(shape, width, height, name, DEFAULT_ICON_COLOR);
	}

	/**
	 * Constructor with full parameter set.
	 *
	 * @param shape
	 * @param width
	 * @param height
	 * @param name
	 * @param color
	 */
	public VisualPropertyIcon(Shape shape, int width, int height, String name, Color color) {
		this.shape = shape;
		this.width = width;
		this.height = height;
		this.name = name;

		if (color != null)
			this.color = color;
		else
			this.color = DEFAULT_ICON_COLOR;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Shape getShape() {
		return shape;
	}

	/**
	 * Get height of icon. This implements Icon interface.
	 */
	public int getIconHeight() {
		return height;
	}

	/**
	 * Get width of icon. This implements Icon interface.
	 */
	public int getIconWidth() {
		return width;
	}

	/**
	 * Set width.
	 *
	 * @param width
	 *            Width of icon
	 */
	public void setIconWidth(int width) {
		this.width = width;
	}

	/**
	 * Set height.
	 *
	 * @param height
	 *            Height of icon
	 */
	public void setIconHeight(int height) {
		this.height = height;
	}

	/**
	 * Get human-readable name of this icon.
	 *
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set human-readable name of this icon.
	 *
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Get color of icon
	 *
	 * @return Icon color.
	 */
	public Color getColor() {
		return color;
	}

	/**
	 * Set icon color.
	 *
	 * @param color
	 *            Icon color.
	 */
	public void setColor(Color color) {
		this.color = color;
	}

	/**
	* Insert space on the left.
	*
	* @param pad DOCUMENT ME!
	*/
	public void setLeftPadding(int pad) {
		this.pad = pad;
	}
}
