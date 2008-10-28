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
package org.cytoscape.vizmap.icon;

import org.cytoscape.vizmap.VMMFactory;
import org.cytoscape.view.VisualPropertyCatalog;
import org.cytoscape.view.VisualPropertyIcon;

import cytoscape.render.immed.GraphGraphics;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.Map;


/**
 * Icon for node shapes.
 *
 * @version 0.5
 * @since Cytoscape 2.5
 * @author kono
 *
 */
public class NodeIcon extends VisualPropertyIcon {
	private final static long serialVersionUID = 1202339876280466L;
	protected Shape newShape;
	protected Graphics2D g2d;

	/**
	 * Creates a new NodeIcon object.
	 */
	public NodeIcon() {
		this(Integer.valueOf(0), //FIXME: use first shape as default value note:this was pre-visual style default previously, but we want to use it for NodeRenderer's icons
		     DEFAULT_ICON_SIZE, DEFAULT_ICON_SIZE,
			 "some default name" /*FIXME*/,
		     DEFAULT_ICON_COLOR);
	}

	public NodeIcon(String name) {
		this(Integer.valueOf(0), //FIXME: use first shape as default value note:this was pre-visual style default previously, but we want to use it for NodeRenderer's icons
		     DEFAULT_ICON_SIZE, DEFAULT_ICON_SIZE,
			 name,
		     DEFAULT_ICON_COLOR);
	}
	
	public NodeIcon(Shape s) {
		this(s, DEFAULT_ICON_SIZE, DEFAULT_ICON_SIZE, "some shape"/*FIXME: names!! */, DEFAULT_ICON_COLOR);
	}

	/**
	 * Creates a new NodeShapeIcon object.
	 *
	 * @param shape
	 * @param width
	 * @param height
	 * @param name
	 */
	public NodeIcon(Shape shape, int width, int height, String name) {
		this(shape, width, height, name, DEFAULT_ICON_COLOR);
	}

	/**
	 * Creates a new NodeShapeIcon object.
	 *
	 * @param shape DOCUMENT ME!
	 * @param width DOCUMENT ME!
	 * @param height DOCUMENT ME!
	 * @param name DOCUMENT ME!
	 * @param color DOCUMENT ME!
	 */
	public NodeIcon(Shape shape, int width, int height, String name, Color color) {
		super(shape, width, height, name, color);

		adjustShape();
	}

	public NodeIcon(Integer shape_code, int width, int height, String name, Color color) {
		super(GraphGraphics.getNodeShapes().get(new Byte(shape_code.byteValue())), width, height, name, color);

		adjustShape();
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param width DOCUMENT ME!
	 */
	public void setIconWidth(int width) {
		super.setIconWidth(width);
		adjustShape();
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param height DOCUMENT ME!
	 */
	public void setIconHeight(int height) {
		super.setIconHeight(height);
		adjustShape();
	}

	private void adjustShape() {
		final double shapeWidth = shape.getBounds2D().getWidth();
		final double shapeHeight = shape.getBounds2D().getHeight();

		final double xRatio = width / shapeWidth;
		final double yRatio = height / shapeHeight;

		final AffineTransform af = new AffineTransform();

		final Rectangle2D bound = shape.getBounds2D();
		final double minx = bound.getMinX();
		final double miny = bound.getMinY();

		if (minx < 0) {
			af.setToTranslation(Math.abs(minx), 0);
			shape = af.createTransformedShape(shape);
		}

		if (miny < 0) {
			af.setToTranslation(0, Math.abs(miny));
			shape = af.createTransformedShape(shape);
		}

		af.setToScale(xRatio, yRatio);
		shape = af.createTransformedShape(shape);
	}

	/**
	 * Draw icon using Java2D.
	 *
	 * @param c DOCUMENT ME!
	 * @param g DOCUMENT ME!
	 * @param x DOCUMENT ME!
	 * @param y DOCUMENT ME!
	 */
	public void paintIcon(Component c, Graphics g, int x, int y) {
		g2d = (Graphics2D) g;

		final AffineTransform af = new AffineTransform();

		// AA on
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		g2d.translate(0, bottomPad);

		newShape = shape;

		af.setToTranslation(leftPad, (c.getHeight() - newShape.getBounds2D().getHeight()) / 2);
		newShape = af.createTransformedShape(newShape);

		g2d.setColor(color);
		g2d.setStroke(new BasicStroke(2.0f));
		g2d.draw(newShape);

		g2d.translate(0, -bottomPad);
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public NodeIcon clone() {
		final NodeIcon cloned = new NodeIcon(shape, width, height, name, color);

		return cloned;
	}
}
