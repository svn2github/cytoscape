/* vim: set ts=2: */
/**
 * Copyright (c) 2010 The Regents of the University of California.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *   1. Redistributions of source code must retain the above copyright
 *      notice, this list of conditions, and the following disclaimer.
 *   2. Redistributions in binary form must reproduce the above
 *      copyright notice, this list of conditions, and the following
 *      disclaimer in the documentation and/or other materials provided
 *      with the distribution.
 *   3. Redistributions must acknowledge that this software was
 *      originally developed by the UCSF Computer Graphics Laboratory
 *      under support by the NIH National Center for Research Resources,
 *      grant P41-RR01081.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDER "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE REGENTS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT
 * OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 * BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */
package nodeCharts.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

// System imports
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

// Cytoscape imports
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.command.CyCommandResult;
import cytoscape.view.CyNetworkView;
import cytoscape.render.stateful.CustomGraphic;

import ding.view.DGraphView;
import ding.view.DNodeView;
import ding.view.InnerCanvas;
import giny.view.NodeView;


/**
 * The NodeChartViewer creates the actual custom graphics
 */
public class ViewUtils {

	public enum Position {
		CENTER ("center"),
		EAST ("east"),
		NORTH ("north"),
		NORTHEAST ("northeast"),
		NORTHWEST ("northwest"),
		SOUTH ("south"),
		SOUTHEAST ("southeast"),
		SOUTHWEST ("southwest"),
		WEST ("west");
	
		private String label;
		private static Map<String, Position>pMap;
	
		Position(String label) { 
			this.label = label; 
			addPosition(this);
		}
	
		public String getLabel() {
			return label;
		}

		public String toString() {
			return label;
		}
	
		private void addPosition(Position pos) {
			if (pMap == null) pMap = new HashMap<String,Position>();
			pMap.put(pos.getLabel(), pos);
		}
	
		static Position getPosition(String label) {
			if (pMap.containsKey(label))
				return pMap.get(label);
			return null;
		}
	}


	public static void addCustomGraphics(List<CustomGraphic> cgList, CyNode node, CyNetworkView view) {
		// Get the DNodeView
		NodeView nView = view.getNodeView(node);
		for (CustomGraphic cg: cgList) {
			((DNodeView)nView).addCustomGraphic(cg);
		}
	}

	public static void clearCustomGraphics(CyNode node, CyNetworkView view) {
		// Get the DNodeView
		NodeView nView = view.getNodeView(node);
		DNodeView dView = (DNodeView)nView;
		List<CustomGraphic> list = new ArrayList<CustomGraphic>();

		// First iterate over all of the custom graphics and collect them
		Iterator<CustomGraphic> cgIterator = dView.customGraphicIterator();
		while (cgIterator.hasNext()) {
			list.add(cgIterator.next());
		}

		// Now iterate over all of the custom graphics and remove them
		for (CustomGraphic cg: list) {
			dView.removeCustomGraphic(cg);
		}
	}

	public static Rectangle2D getNodeBoundingBox(CyNode node, CyNetworkView view, Object position) {
		DNodeView nView = (DNodeView)view.getNodeView(node);

		// Get the affine transform 
		double height = (nView.getHeight()-nView.getBorderWidth())*0.90;
		double width = (nView.getWidth()-nView.getBorderWidth())*0.90;

		// Create the bounding box.
		Rectangle2D.Double bbox = new Rectangle2D.Double(-width/2, -height/2, width, height);
		return positionAdjust(bbox, position);
	}

	/**
 	 * getPosition will return either a Point2D or a Position, depending on whether
 	 * the user provided us with a position keyword or a specific value.
 	 *
 	 * @param position the position argument
 	 * @return a Point2D representing the X,Y offset specified by the user or a Position
 	 * enum that corresponds to the provided keyword.  <b>null</b> is returned if the input
 	 * is illegal.
 	 */
	public static Object getPosition(String position) {
		Position pos = Position.getPosition(position);
		if (pos != null) 
			return pos;

		String [] xy = position.split(",");
		if (xy.length != 2) {
			return null;
		}

		try {
			Double x = Double.valueOf(xy[0]);
			Double y = Double.valueOf(xy[1]);
			return new Point2D.Double(x.doubleValue(), y.doubleValue());
		} catch (NumberFormatException e) {
			return null;
		}
	}


	private static final String DEFAULT_FONT=Font.SANS_SERIF;
	private static final int DEFAULT_STYLE=Font.PLAIN;
	private static final int DEFAULT_SIZE=8;

	public static enum TextAlignment {ALIGN_LEFT, ALIGN_CENTER_TOP, ALIGN_RIGHT, ALIGN_CENTER_BOTTOM, ALIGN_MIDDLE};

	public static Shape getLabelShape(String label, String fontName, int fontStyle, int fontSize,
	                                  Point2D position, TextAlignment tAlign, double rotation, CyNetworkView view) {


		if (fontName == null) fontName = DEFAULT_FONT;
		if (fontStyle == 0) fontStyle = DEFAULT_STYLE;
		if (fontSize == 0) fontSize = DEFAULT_SIZE;

		Font font = new Font(fontName, fontStyle, fontSize);
		// Get the canvas so that we can find the graphics context
		InnerCanvas canvas = ((DGraphView)view).getCanvas();
		Graphics2D g2d = (Graphics2D)canvas.getGraphics();
		FontRenderContext frc = g2d.getFontRenderContext();
		TextLayout tl = new TextLayout(label, font, frc);
		Shape lShape = tl.getOutline(null);

		// System.out.println("  Label = "+label);

		// Figure out how to move the text to center it on the bbox
		double textWidth = lShape.getBounds2D().getMaxX() - lShape.getBounds2D().getMinX(); 
		double textHeight = lShape.getBounds2D().getMaxY() - lShape.getBounds2D().getMinY();

		// System.out.println("  Text size = ("+textWidth+","+textHeight+")");

		double pointX = position.getX();
		double pointY = position.getY();

		double textStartX = pointX;
		double textStartY = pointY;

		switch (tAlign) {
		case ALIGN_CENTER_TOP:
			// System.out.println("  Align = CENTER_TOP");
			textStartX = pointX + textWidth/2;
			textStartY = pointY + textHeight;
			break;
		case ALIGN_CENTER_BOTTOM:
			// System.out.println("  Align = CENTER_BOTTOM");
			textStartX = pointX + textWidth/2;
			textStartY = pointY - textHeight;
			break;
		case ALIGN_RIGHT:
			// System.out.println("  Align = RIGHT");
			textStartX = pointX - textWidth;
			textStartY = pointY + textHeight/2;
			break;
		case ALIGN_LEFT:
			// System.out.println("  Align = LEFT");
			textStartX = pointX;
			textStartY = pointY + textHeight/2;
			break;
		case ALIGN_MIDDLE:
			textStartX = pointX - textWidth/2;;
			textStartY = pointY + textHeight/2;
			break;
		default:
			// System.out.println("  Align = "+tAlign);
		}

		// System.out.println("  Text bounds = "+lShape.getBounds2D());
		// System.out.println("  Position = "+position);

		// System.out.println("  Offset = ("+textStartX+","+textStartY+")");

		// Use the bounding box to create an Affine transform.  We may need to scale the font
		// shape if things are too cramped, but not beneath some arbitrary minimum
		AffineTransform trans = new AffineTransform();
		if (rotation != 0.0)
			trans.rotate(Math.toRadians(rotation), pointX, pointY);
		trans.translate(textStartX, textStartY);

		// System.out.println("  Transform: "+trans);
		return trans.createTransformedShape(lShape);
	}

	/**
 	 * This is used to draw a line from a text box to an object -- for example from a pie label to
 	 * the pie slice itself.
 	 */
	public static Shape getLabelLine(Rectangle2D textBounds, Point2D labelPosition, TextAlignment tAlign) {
		double lineStartX = 0;
		double lineStartY = 0;
		switch (tAlign) {
			case ALIGN_CENTER_TOP:
				lineStartY = textBounds.getMaxY();
				lineStartX = textBounds.getCenterX();
			break;
			case ALIGN_CENTER_BOTTOM:
				lineStartY = textBounds.getMinY();
				lineStartX = textBounds.getCenterX();
			break;
			case ALIGN_RIGHT:
				lineStartY = textBounds.getCenterY();
				lineStartX = textBounds.getMaxX();
			break;
			case ALIGN_LEFT:
				lineStartY = textBounds.getCenterY();
				lineStartX = textBounds.getMinX();
			break;
		}

		BasicStroke stroke = new BasicStroke(0.5f);
		return stroke.createStrokedShape(new Line2D.Double(lineStartX, lineStartY, labelPosition.getX(), labelPosition.getY()));
	}

	private static Rectangle2D positionAdjust(Rectangle2D.Double bbox, Object pos) {
		if (pos == null)
			return bbox;

		double height = bbox.getHeight();
		double width = bbox.getWidth();
		double x = bbox.getX();
		double y = bbox.getY();

		if (pos instanceof Position) {
			Position p = (Position) pos;

			switch (p) {
			case EAST:
				x = width/2;
				break;
			case WEST:
				x = -width*1.5;
				break;
			case NORTH:
				y = -height*1.5;
				break;
			case SOUTH:
				y = height/2;
				break;
			case NORTHEAST:
				x = width/2;
				y = -height*1.5;
				break;
			case NORTHWEST:
				x = -width*1.5;
				y = -height*1.5;
				break;
			case SOUTHEAST:
				x = width/2;
				y = height/2;
				break;
			case SOUTHWEST:
				x = -width*1.5;
				y = height/2;
				break;
			case CENTER:
			default:
			}
		} else if (pos instanceof Point2D.Double) {
			x += ((Point2D.Double)pos).getX();
			y += ((Point2D.Double)pos).getY();
		}

		return new Rectangle2D.Double(x,y,width,height);
	}

}
