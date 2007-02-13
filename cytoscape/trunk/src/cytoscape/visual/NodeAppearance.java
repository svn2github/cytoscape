/*
  File: NodeAppearance.java

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

//----------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//----------------------------------------------------------------------------
package cytoscape.visual;

import cytoscape.Cytoscape;

import cytoscape.data.CyAttributes;

import cytoscape.visual.LineType;

import cytoscape.visual.parsers.ArrowParser;
import cytoscape.visual.parsers.ColorParser;
import cytoscape.visual.parsers.DoubleParser;
import cytoscape.visual.parsers.FontParser;
import cytoscape.visual.parsers.LabelPositionParser;
import cytoscape.visual.parsers.LineTypeParser;
import cytoscape.visual.parsers.NodeShapeParser;
import cytoscape.visual.parsers.ObjectToString;
import cytoscape.visual.parsers.ParserFactory;
import cytoscape.visual.parsers.ValueParser;

import cytoscape.visual.ui.VizMapUI;

import giny.model.Node;

import giny.view.Label;
import giny.view.NodeView;

//----------------------------------------------------------------------------
import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.awt.Stroke;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


//----------------------------------------------------------------------------
/**
 * Objects of this class hold data describing the appearance of a Node.
 */
public class NodeAppearance implements Appearance, Cloneable {
	static Font defaultFont = new Font(null, Font.PLAIN, 12);

	// defaults 
	Color fillColor = Color.WHITE;
	Color borderColor = Color.BLACK;
	LineType borderLineType = LineType.LINE_1;
	byte shape = ShapeNodeRealizer.RECT;
	double width = 70.0;
	double height = 30.0;
	double size = 35.0;
	String label = "";
	String toolTip = "";
	Font font = defaultFont;
	Color labelColor = Color.black;
	boolean nodeSizeLocked = true;
	LabelPosition labelPosition = new LabelPosition();

	/**
	 * Creates a new NodeAppearance object.
	 */
	public NodeAppearance() {
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Color getFillColor() {
		return fillColor;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param c DOCUMENT ME!
	 */
	public void setFillColor(Color c) {
		if (c != null)
			fillColor = c;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Color getBorderColor() {
		return borderColor;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param c DOCUMENT ME!
	 */
	public void setBorderColor(Color c) {
		if (c != null)
			borderColor = c;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public LineType getBorderLineType() {
		return borderLineType;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param lt DOCUMENT ME!
	 */
	public void setBorderLineType(LineType lt) {
		if (lt != null)
			borderLineType = lt;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public byte getShape() {
		return shape;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param s DOCUMENT ME!
	 */
	public void setShape(byte s) {
		shape = s;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public double getWidth() {
		if (nodeSizeLocked)
			return size;
		else

			return width;
	}

	/**
	 * Sets only the height variable.
	 */
	public void setJustWidth(double d) {
		width = d;
	}

	/**
	 * Sets the width variable, but also the size variable if
	 * the node size is locked. This is to support deprecated
	 * code that used setting width/height for setting uniform
	 * size as well.
	 */
	public void setWidth(double d) {
		width = d;

		if (nodeSizeLocked)
			size = d;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public double getHeight() {
		if (nodeSizeLocked)
			return size;
		else

			return height;
	}

	/**
	 * Sets only the height variable.
	 */
	public void setJustHeight(double d) {
		height = d;
	}

	/**
	 * Sets the height variable, but also the size variable if
	 * the node size is locked. This is to support deprecated
	 * code that used setting width/height for setting uniform
	 * size as well.
	 */
	public void setHeight(double d) {
		height = d;

		if (nodeSizeLocked)
			size = d;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public double getSize() {
		return size;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param s DOCUMENT ME!
	 */
	public void setSize(double s) {
		size = s;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String getLabel() {
		return label;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param s DOCUMENT ME!
	 */
	public void setLabel(String s) {
		if (s != null)
			label = s;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String getToolTip() {
		return toolTip;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param s DOCUMENT ME!
	 */
	public void setToolTip(String s) {
		if (s != null)
			toolTip = s;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Font getFont() {
		return font;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param f DOCUMENT ME!
	 */
	public void setFont(Font f) {
		if (f != null)
			font = f;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public float getFontSize() {
		return (float) font.getSize2D();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param f DOCUMENT ME!
	 */
	public void setFontSize(float f) {
		font = font.deriveFont(f);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Color getLabelColor() {
		return labelColor;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param c DOCUMENT ME!
	 */
	public void setLabelColor(Color c) {
		if (c != null)
			labelColor = c;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public LabelPosition getLabelPosition() {
		return labelPosition;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param c DOCUMENT ME!
	 */
	public void setLabelPosition(LabelPosition c) {
		if (c != null)
			labelPosition = c;
	}

	/** @deprecated use applyAppearance(nodeView) instead - now we always optimize.
	    will be removed 10/2007 */
	public void applyAppearance(NodeView nodeView, boolean optimizer) {
		applyAppearance(nodeView);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param nodeView DOCUMENT ME!
	 */
	public void applyAppearance(NodeView nodeView) {
		boolean change_made = false;

		Paint existingUnselectedColor = nodeView.getUnselectedPaint();

		if (!fillColor.equals(existingUnselectedColor)) {
			change_made = true;
			nodeView.setUnselectedPaint(fillColor);
		}

		Paint existingBorderPaint = nodeView.getBorderPaint();

		if (!borderColor.equals(existingBorderPaint)) {
			change_made = true;
			nodeView.setBorderPaint(borderColor);
		}

		Stroke existingBorderType = nodeView.getBorder();
		Stroke newBorderType = borderLineType.getStroke();

		if (!newBorderType.equals(existingBorderType)) {
			change_made = true;
			nodeView.setBorder(newBorderType);
		}

		if (nodeSizeLocked) {
			double existingHeight = nodeView.getHeight();
			double difference = size - existingHeight;

			if (Math.abs(difference) > .1) {
				change_made = true;
				nodeView.setHeight(size);
			}

			double existingWidth = nodeView.getWidth();
			difference = size - existingWidth;

			if (Math.abs(difference) > .1) {
				change_made = true;
				nodeView.setWidth(size);
			}
		} else {
			double existingHeight = nodeView.getHeight();
			double difference = height - existingHeight;

			if (Math.abs(difference) > .1) {
				change_made = true;
				nodeView.setHeight(height);
			}

			double existingWidth = nodeView.getWidth();
			difference = width - existingWidth;

			if (Math.abs(difference) > .1) {
				change_made = true;
				nodeView.setWidth(width);
			}
		}

		int existingShape = nodeView.getShape();
		int newShape = ShapeNodeRealizer.getGinyShape(shape);

		if (existingShape != newShape) {
			change_made = true;
			nodeView.setShape(newShape);
		}

		Label nodelabel = nodeView.getLabel();
		String existingLabel = nodelabel.getText();
		String newLabel = label;

		if (!newLabel.equals(existingLabel)) {
			change_made = true;
			nodelabel.setText(newLabel);
		}

		Font existingFont = nodelabel.getFont();
		Font newFont = getFont();

		if (!newFont.equals(existingFont)) {
			change_made = true;
			nodelabel.setFont(newFont);
		}

		Paint existingTextColor = nodelabel.getTextPaint();
		Paint newTextColor = labelColor;

		if (!newTextColor.equals(existingTextColor)) {
			change_made = true;
			nodelabel.setTextPaint(newTextColor);
		}

		int existingTextAnchor = nodelabel.getTextAnchor();
		int newTextAnchor = labelPosition.getLabelAnchor();

		if (existingTextAnchor != newTextAnchor) {
			change_made = true;
			nodelabel.setTextAnchor(newTextAnchor);
		}

		int existingJustify = nodelabel.getJustify();
		int newJustify = labelPosition.getJustify();

		if (existingJustify != newJustify) {
			change_made = true;
			nodelabel.setJustify(newJustify);
		}

		int existingNodeAnchor = nodeView.getNodeLabelAnchor();
		int newNodeAnchor = labelPosition.getTargetAnchor();

		if (existingNodeAnchor != newNodeAnchor) {
			change_made = true;
			nodeView.setNodeLabelAnchor(newNodeAnchor);
		}

		double existingOffsetX = nodeView.getLabelOffsetX();
		double newOffsetX = labelPosition.getOffsetX();

		if (existingOffsetX != newOffsetX) {
			change_made = true;
			nodeView.setLabelOffsetX(newOffsetX);
		}

		double existingOffsetY = nodeView.getLabelOffsetY();
		double newOffsetY = labelPosition.getOffsetY();

		if (existingOffsetY != newOffsetY) {
			change_made = true;
			nodeView.setLabelOffsetY(newOffsetY);
		}

		if (change_made) {
			nodeView.setNodePosition(false);
		}
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param nacProps DOCUMENT ME!
	 * @param baseKey DOCUMENT ME!
	 */
	public void applyDefaultProperties(Properties nacProps, String baseKey) {
		String value = null;

		//look for default values
		value = nacProps.getProperty(baseKey + ".defaultNodeFillColor");

		if (value != null) {
			Color c = (new ColorParser()).parseColor(value);

			if (c != null) {
				setFillColor(c);
			}
		}

		value = nacProps.getProperty(baseKey + ".defaultNodeBorderColor");

		if (value != null) {
			Color c = (new ColorParser()).parseColor(value);

			if (c != null) {
				setBorderColor(c);
			}
		}

		value = nacProps.getProperty(baseKey + ".defaultNodeLineType");

		if (value != null) {
			LineType lt = (new LineTypeParser()).parseLineType(value);

			if (lt != null) {
				setBorderLineType(lt);
			}
		}

		value = nacProps.getProperty(baseKey + ".defaultNodeShape");

		if (value != null) {
			Byte bObj = (new NodeShapeParser()).parseNodeShape(value);

			if (bObj != null) {
				byte b = bObj.byteValue();

				if (NodeShapeParser.isValidShape(b)) {
					setShape(b);
				}
			}
		}

		value = nacProps.getProperty(baseKey + ".defaultNodeWidth");

		if (value != null) {
			Double dObj = (new DoubleParser()).parseDouble(value);

			if (dObj != null) {
				double d = dObj.doubleValue();

				if (d > 0) {
					width = d;
				}
			}
		}

		value = nacProps.getProperty(baseKey + ".defaultNodeHeight");

		if (value != null) {
			Double dObj = (new DoubleParser()).parseDouble(value);

			if (dObj != null) {
				double d = dObj.doubleValue();

				if (d > 0) {
					height = d;
				}
			}
		}

		value = nacProps.getProperty(baseKey + ".defaultNodeSize");

		if (value != null) {
			Double dObj = (new DoubleParser()).parseDouble(value);

			if (dObj != null) {
				double d = dObj.doubleValue();

				if (d > 0) {
					size = d;
				}
			}
		}

		value = nacProps.getProperty(baseKey + ".defaultNodeLabel");

		if (value != null) {
			setLabel(value);
		}

		value = nacProps.getProperty(baseKey + ".defaultNodeToolTip");

		if (value != null) {
			setToolTip(value);
		}

		value = nacProps.getProperty(baseKey + ".defaultNodeFont");

		if (value != null) {
			Font f = (new FontParser()).parseFont(value);

			if (f != null) {
				setFont(f);
			}
		}

		value = nacProps.getProperty(baseKey + ".defaultNodeLabelPosition");

		if (value != null) {
			LabelPosition f = (new LabelPositionParser()).parseLabelPosition(value);

			if (f != null) {
				setLabelPosition(f);
			}
		}
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param baseKey DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Properties getDefaultProperties(String baseKey) {
		String key = null;
		String value = null;
		Properties newProps = new Properties();

		//save default values
		key = baseKey + ".defaultNodeFillColor";
		value = ObjectToString.getStringValue(getFillColor());
		newProps.setProperty(key, value);
		key = baseKey + ".defaultNodeBorderColor";
		value = ObjectToString.getStringValue(getBorderColor());
		newProps.setProperty(key, value);
		key = baseKey + ".defaultNodeLineType";
		value = ObjectToString.getStringValue(getBorderLineType());
		newProps.setProperty(key, value);
		key = baseKey + ".defaultNodeShape";

		Byte nodeShapeByte = new Byte(getShape());
		value = ObjectToString.getStringValue(nodeShapeByte);
		newProps.setProperty(key, value);
		key = baseKey + ".defaultNodeWidth";

		Double nodeWidthDouble = new Double(getWidth());
		value = ObjectToString.getStringValue(nodeWidthDouble);
		newProps.setProperty(key, value);

		key = baseKey + ".defaultNodeHeight";

		Double nodeHeightDouble = new Double(getHeight());
		value = ObjectToString.getStringValue(nodeHeightDouble);
		newProps.setProperty(key, value);

		key = baseKey + ".defaultNodeSize";

		Double nodeSizeDouble = new Double(getSize());
		value = ObjectToString.getStringValue(nodeSizeDouble);
		newProps.setProperty(key, value);

		key = baseKey + ".defaultNodeLabel";
		value = ObjectToString.getStringValue(getLabel());
		newProps.setProperty(key, value);
		key = baseKey + ".defaultNodeToolTip";
		value = ObjectToString.getStringValue(getToolTip());
		newProps.setProperty(key, value);
		key = baseKey + ".defaultNodeFont";
		value = ObjectToString.getStringValue(getFont());
		newProps.setProperty(key, value);
		key = baseKey + ".defaultNodeLabelPosition";
		value = ObjectToString.getStringValue(getLabelPosition());
		newProps.setProperty(key, value);

		return newProps;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param prefix DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String getDescription(String prefix) {
		if (prefix == null)
			prefix = "";

		String lineSep = System.getProperty("line.separator");
		StringBuffer sb = new StringBuffer();

		sb.append(prefix + "NodeFillColor = ").append(fillColor).append(lineSep);
		sb.append(prefix + "NodeBorderColor = ").append(borderColor).append(lineSep);

		String nodeLineTypeText = ObjectToString.getStringValue(borderLineType);
		sb.append(prefix + "NodeLineType = ").append(nodeLineTypeText).append(lineSep);

		Byte nodeShapeByte = new Byte(shape);
		String nodeShapeText = ObjectToString.getStringValue(nodeShapeByte);
		sb.append(prefix + "NodeShape = ").append(nodeShapeText).append(lineSep);
		sb.append(prefix + "NodeWidth = ").append(width).append(lineSep);
		sb.append(prefix + "NodeHeight = ").append(height).append(lineSep);
		sb.append(prefix + "NodeSize = ").append(size).append(lineSep);
		sb.append(prefix + "NodeLabel = ").append(label).append(lineSep);
		sb.append(prefix + "NodeToolTip = ").append(toolTip).append(lineSep);
		sb.append(prefix + "NodeFont = ").append(font).append(lineSep);
		sb.append(prefix + "NodeFontColor = ").append(labelColor.toString()).append(lineSep);
		sb.append(prefix + "NodeLabelPosition = ").append(labelPosition.toString()).append(lineSep);
		sb.append(prefix + "nodeSizeLocked = ").append(nodeSizeLocked).append(lineSep);

		return sb.toString();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String getDescription() {
		return getDescription(null);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param type DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Object get(byte type) {
		Object defaultObj = null;

		switch (type) {
			case VizMapUI.NODE_COLOR:
				defaultObj = getFillColor();

				break;

			case VizMapUI.NODE_BORDER_COLOR:
				defaultObj = getBorderColor();

				break;

			case VizMapUI.NODE_LINETYPE:
				defaultObj = getBorderLineType();

				break;

			case VizMapUI.NODE_SHAPE:
				defaultObj = new Byte(getShape());

				break;

			case VizMapUI.NODE_HEIGHT:
				defaultObj = new Double(getHeight());

				break;

			case VizMapUI.NODE_WIDTH:
				defaultObj = new Double(getWidth());

				break;

			case VizMapUI.NODE_SIZE:
				defaultObj = new Double(getSize());

				break;

			case VizMapUI.NODE_LABEL:
				defaultObj = getLabel();

				break;

			case VizMapUI.NODE_LABEL_COLOR:
				defaultObj = getLabelColor();

				break;

			case VizMapUI.NODE_TOOLTIP:
				defaultObj = getToolTip();

				break;

			case VizMapUI.NODE_FONT_FACE:
				defaultObj = getFont();

				break;

			case VizMapUI.NODE_FONT_SIZE:
				defaultObj = new Double(getFont().getSize());

				break;

			case VizMapUI.NODE_LABEL_POSITION:
				defaultObj = getLabelPosition();

				break;
		}

		return defaultObj;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param type DOCUMENT ME!
	 * @param c DOCUMENT ME!
	 */
	public void set(byte type, Object c) {
		//System.out.println("NodeAppearance before: " + getDescription(null));
		switch (type) {
			case VizMapUI.NODE_COLOR:
				setFillColor((Color) c);

				break;

			case VizMapUI.NODE_BORDER_COLOR:
				setBorderColor((Color) c);

				break;

			case VizMapUI.NODE_LINETYPE:
				setBorderLineType((LineType) c);

				break;

			case VizMapUI.NODE_SHAPE:
				setShape(((Byte) c).byteValue());

				break;

			case VizMapUI.NODE_HEIGHT:
				setHeight(((Double) c).doubleValue());

				break;

			case VizMapUI.NODE_WIDTH:
				setWidth(((Double) c).doubleValue());

				break;

			case VizMapUI.NODE_SIZE:
				setSize(((Double) c).doubleValue());

				break;

			case VizMapUI.NODE_LABEL:
				setLabel((String) c);

				break;

			case VizMapUI.NODE_LABEL_COLOR:
				setLabelColor((Color) c);

				break;

			case VizMapUI.NODE_TOOLTIP:
				setToolTip((String) c);

				break;

			case VizMapUI.NODE_FONT_FACE:
				setFont((Font) c);

				break;

			case VizMapUI.NODE_FONT_SIZE:
				setFontSize(((Double) c).floatValue());

				break;

			case VizMapUI.NODE_LABEL_POSITION:
				setLabelPosition((LabelPosition) c);

				break;
		}

		//System.out.println("NodeAppearance after: " + getDescription(null));
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param na DOCUMENT ME!
	 */
	public void copy(NodeAppearance na) {
		// remember the new lock state 
		boolean actualLockState = na.getNodeSizeLocked();

		// set everything to false so that it copy
		// correctly
		setNodeSizeLocked(false);
		na.setNodeSizeLocked(false);

		setFillColor(na.getFillColor());
		setBorderColor(na.getBorderColor());
		setBorderLineType(na.getBorderLineType());
		setShape(na.getShape());
		setWidth(na.getWidth());
		setHeight(na.getHeight());
		setSize(na.getSize());
		setLabel(na.getLabel());
		setToolTip(na.getToolTip());
		setFont(na.getFont());
		setLabelColor(na.getLabelColor());
		setLabelPosition(na.getLabelPosition());

		// now set the lock state correctly
		setNodeSizeLocked(actualLockState);
		na.setNodeSizeLocked(actualLockState);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Object clone() {
		NodeAppearance na = new NodeAppearance();
		na.copy(this);

		return na;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public boolean getNodeSizeLocked() {
		return nodeSizeLocked;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param b DOCUMENT ME!
	 */
	public void setNodeSizeLocked(boolean b) {
		nodeSizeLocked = b;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param n DOCUMENT ME!
	 */
	public void applyBypass(Node n) {
		if (n == null)
			return;

		String id = n.getIdentifier();
		CyAttributes attrs = Cytoscape.getNodeAttributes();

		setFillColor(BypassHelper.getColorBypass(attrs, id, "node.fillColor"));
		setBorderColor(BypassHelper.getColorBypass(attrs, id, "node.borderColor"));
		setBorderLineType((LineType) BypassHelper.getBypass(attrs, id, "node.lineType",
		                                                    LineType.class));

		Byte b = (Byte) BypassHelper.getBypass(attrs, id, "node.shape", Byte.class);

		if (b != null)
			setShape(b.byteValue());

		Double w = (Double) BypassHelper.getBypass(attrs, id, "node.width", Double.class);

		if (w != null)
			setWidth(w.doubleValue());

		Double h = (Double) BypassHelper.getBypass(attrs, id, "node.height", Double.class);

		if (h != null)
			setHeight(h.doubleValue());

		Double s = (Double) BypassHelper.getBypass(attrs, id, "node.size", Double.class);

		if (s != null)
			setSize(s.doubleValue());

		setLabel((String) BypassHelper.getBypass(attrs, id, "node.label", String.class));
		setToolTip((String) BypassHelper.getBypass(attrs, id, "node.toolTip", String.class));
		setFont((Font) BypassHelper.getBypass(attrs, id, "node.font", Font.class));

		Double f = (Double) BypassHelper.getBypass(attrs, id, "node.fontSize", Double.class);

		if (f != null)
			setFontSize(f.floatValue());

		setLabelColor((Color) BypassHelper.getBypass(attrs, id, "node.labelColor", Color.class));
		setLabelPosition((LabelPosition) BypassHelper.getBypass(attrs, id, "node.labelPosition",
		                                                        LabelPosition.class));
	}
}
