/* -*-Java-*-
********************************************************************************
*
* File:         CytoShapeIcon.java
* RCS:          $Header: $
* Description:
* Author:       Allan Kuchinksy
* Created:      Mon Sept 06 11:43:57 2005
* Modified:     Thu May 10 15:05:50 2007 (Michael L. Creech) creech@w235krbza760
* Language:     Java
* Package:
* Status:       Experimental (Do Not Distribute)
*
* (c) Copyright 2006, Agilent Technologies, all rights reserved.
*
********************************************************************************
*
* Revisions:
*
* Wed May 09 17:14:25 2007 (Michael L. Creech) creech@w235krbza760
*  Updated to Cytoscape 2.5-the byte _shape --> NodeShape enum,
*  and changes to use of Arrows.
* Mon Dec 04 11:44:33 2006 (Michael L. Creech) creech@w235krbza760
*  Added constructor for handling size information. Changed
*  getIconWidth/Height() to return real width and height of
*  icon. Added DEFAULT_WIDTH and DEFAULT_HEIGHT and deprecated
*  WIDTH & HEIGHT.
********************************************************************************
*/
package cytoscape.editor.impl;

import org.cytoscape.vizmap.ArrowShape;
import org.cytoscape.vizmap.NodeShape;

import javax.swing.*;
import java.awt.*;


/**
 *
 * Specialized Icon for Cytoscape editor palette entry.  Renders icon
 * based upon input shape, size, color.
 *
 * @author Allan Kuchinsky
 * @version 2.0
 */
public class CytoShapeIcon implements Icon {
	// MLC 12/04/06 BEGIN:
	/**
	 * 
	 */
	public static final int DEFAULT_WIDTH = 32;

	/**
	 * 
	 */
	public static final int DEFAULT_HEIGHT = 32;

	/**
	 * @deprecated Use DEFAULT_WIDTH. Will be removed 12/2007.
	 */
	public static final int WIDTH = 32;

	/**
	 * @deprecated Use DEFAULT_HEIGHT. Will be removed 12/2007.
	 */
	public static final int HEIGHT = 32;
	private Dimension _size = new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT);

	// MLC 12/04/06 END.
	private Color _color;
	private Image _image = null;
    // MLC 05/09/07:
    // private byte _shape;
    // MLC 05/09/07:
	private NodeShape _shape;
	private ArrowShape _arrowType = null;
 
    public CytoShapeIcon(NodeShape shape, Color color, Dimension size) {
    	this(shape, color);
	_size = size;
    }

    public CytoShapeIcon(NodeShape shape, Color color) {
	_color = color;
	_shape = shape;
	_image = null;
	_arrowType = null;
    }
    // MLC 05/09/07 END.
	/**
	 * Creates a new CytoShapeIcon object.
	 *
	 * @param img  DOCUMENT ME!
	 */
	public CytoShapeIcon(Image img) {
		_image = img;
		_arrowType = null;
	}

	/**
	 * Creates a new CytoShapeIcon object.
	 *
	 * @param arrowType  DOCUMENT ME!
	 */
	public CytoShapeIcon(ArrowShape arrowType) {
		_arrowType = arrowType;
	}

	/* (non-Javadoc)
	 * @see javax.swing.Icon#getIconHeight()
	 */
	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public int getIconHeight() {
		// MLC 12/04/06:
		return _size.height;

		// MLC 12/04/06:
		// return 32;
	}

	/* (non-Javadoc)
	 * @see javax.swing.Icon#getIconWidth()
	 */
	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public int getIconWidth() {
		// MLC 12/04/06:
		return _size.width;

		// MLC 12/04/06:
		// return 32;
	}

	/**


	/**
	* Implements specialized coordinate line drawing for palette shcapes
	*
	* (non-Javadoc)
	* @see javax.swing.Icon#paintIcon(java.awt.Component, java.awt.Graphics, int, int)
	*/
	public void paintIcon(Component c, Graphics g, int x, int y) {
		int width = getIconWidth();
		int height = getIconHeight();

		if (_image != null) {
			g.drawImage(_image, x, y, c);

			return;
		}

		if (_arrowType != null) {
			//g.setColor(_arrowType.getColor());

			if (_arrowType == ArrowShape.DELTA) {
				g.fillPolygon(new int[] {
				                  x, x + ((3 * width) / 4), x + ((3 * width) / 4), x + width,
				                  x + ((3 * width) / 4), x + ((3 * width) / 4), x
				              },
				              new int[] {
				                  y + ((7 * height) / 16), y + ((7 * height) / 16),
				                  y + ((5 * height) / 16), y + (height / 2),
				                  y + ((11 * height) / 16), y + ((9 * height) / 16),
				                  y + ((9 * height) / 16)
				              }, 7);
			} else if (_arrowType == ArrowShape.CIRCLE) {
				g.fillRect(x, y + ((7 * height) / 16), (13 * (width / 16)), height / 8);
				g.fillOval(x + ((5 * width) / 8), y + ((5 * height) / 16), (6 * width) / 16,
				           (6 * height) / 16);
			} else if (_arrowType == ArrowShape.T) {
				g.fillRect(x, y + ((7 * height) / 16), (15 * (width / 16)), height / 8);
				g.fillRect(x + (15 * (width / 16)), y + ((5 * height) / 16), width / 16,
				           (height * 6) / 16);
			} else if (_arrowType == ArrowShape.NONE) {
				g.fillRect(x, y + ((7 * height) / 16), (15 * (width / 16)), height / 8);
			}

			return;
		}

		g.setColor(_color);

		if (_shape == NodeShape.TRIANGLE) {
			g.fillPolygon(new int[] { x, x + (width / 2), x + width },
			              new int[] { y + height, y, y + height }, 3);
			g.setColor(Color.BLACK);
			g.drawPolygon(new int[] { x, x + (width / 2), x + width },
			              new int[] { y + height, y, y + height }, 3);
		} else if (_shape == NodeShape.ROUND_RECT) {
			g.fillRoundRect(x, y, width, height, width / 2, height / 2);
			g.setColor(Color.BLACK);
			g.drawRoundRect(x, y, width, height, width / 2, height / 2);
		} else if (_shape == NodeShape.DIAMOND) {
			g.fillPolygon(new int[] { x, x + (width / 2), x + width, x + (width / 2) },
			              new int[] { y + (height / 2), y, y + (height / 2), y + height }, 4);
			g.setColor(Color.BLACK);
			g.drawPolygon(new int[] { x, x + (width / 2), x + width, x + (width / 2) },
			              new int[] { y + (height / 2), y, y + (height / 2), y + height }, 4);
		} else if (_shape == NodeShape.ELLIPSE) {
			g.fillOval(x, y, width, height);
			g.setColor(Color.BLACK);
			g.drawOval(x, y, width, height);
		} else if (_shape == NodeShape.HEXAGON) {
			g.fillPolygon(new int[] {
			                  x, x + (width / 4), x + ((3 * width) / 4), x + width,
			                  x + ((3 * width) / 4), x + (width / 4)
			              },
			              new int[] { y + (height / 2), y, y, y + (height / 2), y + height, y
				          + height }, 6);
			g.setColor(Color.BLACK);
			g.drawPolygon(new int[] {
			                  x, x + (width / 4), x + ((3 * width) / 4), x + width,
			                  x + ((3 * width) / 4), x + (width / 4)
			              },
			              new int[] { y + (height / 2), y, y, y + (height / 2), y + height, y
				          + height }, 6);
		} else if (_shape == NodeShape.OCTAGON) {
			g.fillPolygon(new int[] {
			                  x, x + (width / 4), x + ((3 * width) / 4), x + width, x + width,
			                  x + ((3 * width) / 4), x + (width / 4), x
			              },
			              new int[] {
			                  y + (height / 4), y, y, y + (height / 4), y + (3 * (height / 4)),
			                  y + height, y + height, y + (3 * (height / 4))
			              }, 8);
			g.setColor(Color.BLACK);
			g.drawPolygon(new int[] {
			                  x, x + (width / 4), x + ((3 * width) / 4), x + width, x + width,
			                  x + ((3 * width) / 4), x + (width / 4), x
			              },
			              new int[] {
			                  y + (height / 4), y, y, y + (height / 4), y + (3 * (height / 4)),
			                  y + height, y + height, y + (3 * (height / 4))
			              }, 8);
		// MLC 05/09/07:
		// } else if (_shape == ShapeNodeRealizer.PARALLELOGRAM) {
		// MLC 05/09/07:
		} else if (_shape == NodeShape.PARALLELOGRAM) {
			g.fillPolygon(new int[] { x, x + ((3 * width) / 4), x + width, x + (width / 4) },
			              new int[] { y, y, y + height, y + height }, 4);
			g.setColor(Color.BLACK);
			g.drawPolygon(new int[] { x, x + ((3 * width) / 4), x + width, x + (width / 4) },
			              new int[] { y, y, y + height, y + height }, 4);
		// MLC 05/09/07:
		// } else if (_shape == ShapeNodeRealizer.RECT) {
		// MLC 05/09/07:
		} else if (_shape == NodeShape.RECT) {
			g.fillRect(x, y, width, height);
			g.setColor(Color.BLACK);
			g.drawRect(x, y, width, height);
		}
	}
}
