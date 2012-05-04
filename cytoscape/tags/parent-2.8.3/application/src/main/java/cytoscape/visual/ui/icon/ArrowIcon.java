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

import cytoscape.Cytoscape;

import cytoscape.visual.ArrowShape;
import cytoscape.visual.VisualPropertyType;
import static cytoscape.visual.VisualPropertyType.EDGE_SRCARROW_SHAPE;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;


/**
 * Icon for arrow shape.
 *
 * @version 0.5
 * @since Cytoscape 2.5
 * @author kono
 */
public class ArrowIcon extends VisualPropertyIcon {
	private static final Stroke EDGE_STROKE = new BasicStroke(6.0f, BasicStroke.CAP_SQUARE,
	                                                          BasicStroke.JOIN_MITER);
	private static final Stroke EDGE_STROKE_SMALL = new BasicStroke(4.0f, BasicStroke.CAP_SQUARE,
	                                                                BasicStroke.JOIN_MITER);
	private static final int DEF_L_PAD = 15;

	private final ArrowShape arrow;

	/**
	 * Creates a new ArrowIcon object.
	 * @param arrow The ArrowShape to create the icon for.
	 * @param width The width of the icon.
	 */
	public ArrowIcon(ArrowShape arrow, int width) {
		super(arrow.getShape(), width, DEFAULT_ICON_SIZE, 
		      arrow.getName(), DEFAULT_ICON_COLOR);
		this.arrow = arrow;
	}

	/**
	 * Creates a new ArrowIcon object.
	 * @param arrow The ArrowShape to create the icon for.
	 */
	public ArrowIcon(ArrowShape arrow) {
		this(arrow, DEFAULT_ICON_SIZE * 3);
	}


	/**
	 * Draw icon using Java2D.
	 *
	 * @param c The component that the icon is being rendered in.  
	 * Used to calculate width and height of the icon.
	 * @param g The Graphics used to render the icon. 
	 * @param x Not used in this implementation. 
	 * @param y Not used in this implementation.
	 */
	public void paintIcon(Component c, Graphics g, int x, int y) {
		Graphics2D g2d = (Graphics2D) g;

		// Turn AA on
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setColor(color);

		g2d.translate(leftPad, bottomPad);

		// If shape is not defined, treat as no-head.
		if (shape == null) {
			if ((width < 20) || (height < 20)) {
				g2d.translate(-leftPad, -bottomPad);
				g2d.setStroke(EDGE_STROKE_SMALL);
				g2d.drawLine(3, c.getHeight()/2, width/2 +10, c.getHeight()/2);
			} else {
				g2d.setStroke(EDGE_STROKE);
				g2d.drawLine(DEF_L_PAD, c.getHeight()/2, width/2 +10, c.getHeight()/2);
			}
			return;
		}

		final AffineTransform af = new AffineTransform();
		final Rectangle2D bound = shape.getBounds2D();
		final double minx = bound.getMinX();
		final double miny = bound.getMinY();

		Shape newShape = shape;

		// Adjust position if it is NOT in first quadrant.
		if (minx < 0) {
			af.setToTranslation(Math.abs(minx), 0);
			newShape = af.createTransformedShape(newShape);
		}

		if (miny < 0) {
			af.setToTranslation(0, Math.abs(miny));
			newShape = af.createTransformedShape(newShape);
		}

		final double shapeWidth = newShape.getBounds2D().getWidth();
		final double shapeHeight = newShape.getBounds2D().getHeight();

		final double originalXYRatio = shapeWidth / shapeHeight;

		final double xRatio = (width / 3) / shapeWidth;
		final double yRatio = height / shapeHeight;
		af.setToScale(xRatio * originalXYRatio, yRatio);
		newShape = af.createTransformedShape(newShape);

		af.setToTranslation((width * 0.8) - newShape.getBounds2D().getCenterX(),
		                    ((height + 20) / 2) - newShape.getBounds2D().getCenterY());
		newShape = af.createTransformedShape(newShape);

		g2d.fill(newShape);

		// Finally, draw an edge (line) to the arrow head if desired.
		if ( arrow.renderEdgeWithArrow() ) {
			if ((width < 20) || (height < 20)) {
				g2d.translate(-leftPad, -bottomPad);
				g2d.setStroke(EDGE_STROKE_SMALL);
				g2d.drawLine(3, c.getHeight()/2, width/2 +10, c.getHeight()/2);
			} else {
				g2d.setStroke(EDGE_STROKE);
				g2d.drawLine(DEF_L_PAD, (height + 20) / 2,
			             (int) (newShape.getBounds2D().getCenterX()) - 2, (height + 20) / 2);
			}
		}
	}
}
