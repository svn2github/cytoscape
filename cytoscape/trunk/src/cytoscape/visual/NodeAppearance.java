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
import static cytoscape.visual.VisualPropertyType.NODE_BORDER_COLOR;
import static cytoscape.visual.VisualPropertyType.NODE_FILL_COLOR;
import static cytoscape.visual.VisualPropertyType.NODE_FONT_FACE;
import static cytoscape.visual.VisualPropertyType.NODE_FONT_SIZE;
import static cytoscape.visual.VisualPropertyType.NODE_HEIGHT;
import static cytoscape.visual.VisualPropertyType.NODE_LABEL;
import static cytoscape.visual.VisualPropertyType.NODE_LABEL_COLOR;
import static cytoscape.visual.VisualPropertyType.NODE_LABEL_POSITION;
import static cytoscape.visual.VisualPropertyType.NODE_LINETYPE;
import static cytoscape.visual.VisualPropertyType.NODE_SHAPE;
import static cytoscape.visual.VisualPropertyType.NODE_SIZE;
import static cytoscape.visual.VisualPropertyType.NODE_TOOLTIP;
import static cytoscape.visual.VisualPropertyType.NODE_WIDTH;

import cytoscape.visual.parsers.ColorParser;
import cytoscape.visual.parsers.DoubleParser;
import cytoscape.visual.parsers.FontParser;
import cytoscape.visual.parsers.LabelPositionParser;
import cytoscape.visual.parsers.LineParser;
import cytoscape.visual.parsers.NodeShapeParser;
import cytoscape.visual.parsers.ObjectToString;

import giny.model.Node;

import giny.view.Label;
import giny.view.NodeView;

import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.awt.Stroke;

import java.util.Properties;


//----------------------------------------------------------------------------
/**
 * Objects of this class hold data describing the appearance of a Node.
 */
public class NodeAppearance
    implements Appearance, Cloneable {
    private static final Font DEFAULT_FONT = new Font(null, Font.PLAIN, 12);
    private static final Color DEFAULT_FILL_COLOR = Color.white;
    private static final Color DEFAULT_BORDER_COLOR = Color.black;
    private static final Color DEFAULT_LABEL_COLOR = Color.black;
    private Color fillColor = DEFAULT_FILL_COLOR;
    private Color borderColor = DEFAULT_BORDER_COLOR;
    @Deprecated
    private LineType borderLineType = LineType.LINE_1;
    private Line borderLine = Line.DEFAULT_LINE;
    private NodeShape nodeShape = NodeShape.RECT;

    /*
     * Will be removed April, 2008
     */
    @Deprecated
    private byte shape = ShapeNodeRealizer.RECT;
    private double width = 70.0;
    private double height = 30.0;
    private double size = 35.0;
    private String label = "";
    private String toolTip = "";
    private Font font = DEFAULT_FONT;
    private Color labelColor = DEFAULT_LABEL_COLOR;
    private boolean nodeSizeLocked = true;
    private LabelPosition labelPosition = new LabelPosition();

    /**
     * Creates a new NodeAppearance object.
     */
    public NodeAppearance() {
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Color getFillColor() {
        return fillColor;
    }

    /**
     * DOCUMENT ME!
     *
     * @param c DOCUMENT ME!
     */
    public void setFillColor(Color c) {
        if (c != null)
            fillColor = c;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Color getBorderColor() {
        return borderColor;
    }

    /**
     * DOCUMENT ME!
     *
     * @param c DOCUMENT ME!
     */
    public void setBorderColor(Color c) {
        if (c != null)
            borderColor = c;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    @Deprecated
    public LineType getBorderLineType() {
        return borderLineType;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Line getBorderLine() {
        return borderLine;
    }

    /**
     * DOCUMENT ME!
     *
     * @param lt DOCUMENT ME!
     */
    @Deprecated
    public void setBorderLineType(LineType lt) {
        if (lt != null)
            borderLineType = lt;
    }

    /**
     * DOCUMENT ME!
     *
     * @param newLine DOCUMENT ME!
     */
    public void setBorderLine(Line newLine) {
        if (newLine != null)
            borderLine = newLine;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    @Deprecated
    public byte getShape() {
        return shape;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public NodeShape getNodeShape() {
        return nodeShape;
    }

    /**
     * DOCUMENT ME!
     *
     * @param s DOCUMENT ME!
     */
    @Deprecated
    public void setShape(byte s) {
        nodeShape = ShapeNodeRealizer.getNodeShape(s);
        shape = s;
    }

    /**
     * DOCUMENT ME!
     *
     * @param s DOCUMENT ME!
     */
    public void setNodeShape(NodeShape s) {
        nodeShape = s;

        // Will be removed April, 2008
        shape = (byte) s.ordinal();
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
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
     * Sets the width variable, but also the size variable if the node size is
     * locked. This is to support deprecated code that used setting width/height
     * for setting uniform size as well.
     */
    public void setWidth(double d) {
        width = d;

        if (nodeSizeLocked)
            size = d;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
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
     * Sets the height variable, but also the size variable if the node size is
     * locked. This is to support deprecated code that used setting width/height
     * for setting uniform size as well.
     */
    public void setHeight(double d) {
        height = d;

        if (nodeSizeLocked)
            size = d;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public double getSize() {
        return size;
    }

    /**
     * DOCUMENT ME!
     *
     * @param s DOCUMENT ME!
     */
    public void setSize(double s) {
        size = s;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getLabel() {
        return label;
    }

    /**
     * DOCUMENT ME!
     *
     * @param s DOCUMENT ME!
     */
    public void setLabel(String s) {
        if (s != null)
            label = s;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getToolTip() {
        return toolTip;
    }

    /**
     * DOCUMENT ME!
     *
     * @param s DOCUMENT ME!
     */
    public void setToolTip(String s) {
        if (s != null)
            toolTip = s;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Font getFont() {
        return font;
    }

    /**
     * DOCUMENT ME!
     *
     * @param f DOCUMENT ME!
     */
    public void setFont(Font f) {
        if (f != null)
            font = f;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public float getFontSize() {
        return (float) font.getSize2D();
    }

    /**
     * DOCUMENT ME!
     *
     * @param f DOCUMENT ME!
     */
    public void setFontSize(float f) {
        font = font.deriveFont(f);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Color getLabelColor() {
        return labelColor;
    }

    /**
     * DOCUMENT ME!
     *
     * @param c DOCUMENT ME!
     */
    public void setLabelColor(Color c) {
        if (c != null)
            labelColor = c;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public LabelPosition getLabelPosition() {
        return labelPosition;
    }

    /**
     * DOCUMENT ME!
     *
     * @param c DOCUMENT ME!
     */
    public void setLabelPosition(LabelPosition c) {
        if (c != null)
            labelPosition = c;
    }

    /**
     * @deprecated use applyAppearance(nodeView) instead - now we always
     *             optimize. will be removed 10/2007
     */
    public void applyAppearance(NodeView nodeView, boolean optimizer) {
        applyAppearance(nodeView);
    }

    /**
     * DOCUMENT ME!
     *
     * @param nodeView DOCUMENT ME!
     */
    public void applyAppearance(final NodeView nodeView) {
        boolean change_made = false;

        if (!fillColor.equals(nodeView.getUnselectedPaint())) {
            change_made = true;
            nodeView.setUnselectedPaint(fillColor);
        }

        if (!borderColor.equals(nodeView.getBorderPaint())) {
            change_made = true;
            nodeView.setBorderPaint(borderColor);
        }

        final Stroke newBorderLine = borderLine.getStroke();

        if (!newBorderLine.equals(nodeView.getBorder())) {
            change_made = true;
            nodeView.setBorder(newBorderLine);
        }

        double difference;

        if (nodeSizeLocked) {
            difference = size - nodeView.getHeight();

            if (Math.abs(difference) > .1) {
                change_made = true;
                nodeView.setHeight(size);
            }

            difference = size - nodeView.getWidth();

            if (Math.abs(difference) > .1) {
                change_made = true;
                nodeView.setWidth(size);
            }
        } else {
            difference = height - nodeView.getHeight();

            if (Math.abs(difference) > .1) {
                change_made = true;
                nodeView.setHeight(height);
            }

            difference = width - nodeView.getWidth();

            if (Math.abs(difference) > .1) {
                change_made = true;
                nodeView.setWidth(width);
            }
        }

        final int newShape = nodeShape.getGinyShape();

        if (nodeView.getShape() != newShape) {
            change_made = true;
            nodeView.setShape(newShape);
        }

        final Label nodelabel = nodeView.getLabel();

        final String newLabel = label;

        if (!newLabel.equals(nodelabel.getText())) {
            change_made = true;
            nodelabel.setText(newLabel);
        }

        final Font newFont = getFont();

        if (!newFont.equals(nodelabel.getFont())) {
            change_made = true;
            nodelabel.setFont(newFont);
        }

        final Paint newTextColor = labelColor;

        if (!newTextColor.equals(nodelabel.getTextPaint())) {
            change_made = true;
            nodelabel.setTextPaint(newTextColor);
        }

        final int newTextAnchor = labelPosition.getLabelAnchor();

        if (nodelabel.getTextAnchor() != newTextAnchor) {
            change_made = true;
            nodelabel.setTextAnchor(newTextAnchor);
        }

        final int newJustify = labelPosition.getJustify();

        if (nodelabel.getJustify() != newJustify) {
            change_made = true;
            nodelabel.setJustify(newJustify);
        }

        final int newNodeAnchor = labelPosition.getTargetAnchor();

        if (nodeView.getNodeLabelAnchor() != newNodeAnchor) {
            change_made = true;
            nodeView.setNodeLabelAnchor(newNodeAnchor);
        }

        final double newOffsetX = labelPosition.getOffsetX();

        if (nodeView.getLabelOffsetX() != newOffsetX) {
            change_made = true;
            nodeView.setLabelOffsetX(newOffsetX);
        }

        final double newOffsetY = labelPosition.getOffsetY();

        if (nodeView.getLabelOffsetY() != newOffsetY) {
            change_made = true;
            nodeView.setLabelOffsetY(newOffsetY);
        }

        if (change_made)
            nodeView.setNodePosition(false);
    }

    /**
     * DOCUMENT ME!
     *
     * @param nacProps DOCUMENT ME!
     * @param baseKey DOCUMENT ME!
     */
    public void applyDefaultProperties(final Properties nacProps,
        final String baseKey) {
        String value = null;

        Color curColor;
        value = nacProps.getProperty(
                NODE_FILL_COLOR.getDefaultPropertyKey(baseKey));

        if (value != null) {
            curColor = (new ColorParser()).parseColor(value);

            if (curColor != null)
                setFillColor(curColor);
        }

        value = nacProps.getProperty(
                NODE_BORDER_COLOR.getDefaultPropertyKey(baseKey));

        if (value != null) {
            curColor = (new ColorParser()).parseColor(value);

            if (curColor != null)
                setBorderColor(curColor);
        }

        value = nacProps.getProperty(
                NODE_LINETYPE.getDefaultPropertyKey(baseKey));

        if (value != null) {
            final Line lt = (new LineParser()).parseLine(value);

            if (lt != null)
                setBorderLine(lt);
        }

        value = nacProps.getProperty(NODE_SHAPE.getDefaultPropertyKey(baseKey));

        if (value != null) {
            final NodeShape shape = (new NodeShapeParser()).parseNodeShapeEnum(value);

            if (NodeShapeParser.isValidShape(shape))
                setNodeShape(shape);
        }

        Double curDoubleValue;
        value = nacProps.getProperty(NODE_WIDTH.getDefaultPropertyKey(baseKey));

        if (value != null) {
            curDoubleValue = (new DoubleParser()).parseDouble(value);

            if ((curDoubleValue != null) && (curDoubleValue > 0))
                width = curDoubleValue;
        }

        value = nacProps.getProperty(
                NODE_HEIGHT.getDefaultPropertyKey(baseKey));

        if (value != null) {
            curDoubleValue = (new DoubleParser()).parseDouble(value);

            if ((curDoubleValue != null) && (curDoubleValue > 0))
                height = curDoubleValue;
        }

        value = nacProps.getProperty(NODE_SIZE.getDefaultPropertyKey(baseKey));

        if (value != null) {
            curDoubleValue = (new DoubleParser()).parseDouble(value);

            if ((curDoubleValue != null) && (curDoubleValue > 0))
                size = curDoubleValue;
        }

        value = nacProps.getProperty(NODE_LABEL.getDefaultPropertyKey(baseKey));

        if (value != null)
            setLabel(value);

        value = nacProps.getProperty(
                NODE_TOOLTIP.getDefaultPropertyKey(baseKey));

        if (value != null)
            setToolTip(value);

        value = nacProps.getProperty(
                NODE_FONT_FACE.getDefaultPropertyKey(baseKey));

        if (value != null) {
            final Font f = (new FontParser()).parseFont(value);

            if (f != null)
                setFont(f);
        }

        value = nacProps.getProperty(
                NODE_LABEL_POSITION.getDefaultPropertyKey(baseKey));

        if (value != null) {
            final LabelPosition f = (new LabelPositionParser()).parseLabelPosition(value);

            if (f != null)
                setLabelPosition(f);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param baseKey DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Properties getDefaultProperties(String baseKey) {
        String key = null;
        String value = null;
        final Properties newProps = new Properties();

        key = NODE_FILL_COLOR.getDefaultPropertyKey(baseKey);
        value = ObjectToString.getStringValue(getFillColor());
        newProps.setProperty(key, value);

        key = NODE_BORDER_COLOR.getDefaultPropertyKey(baseKey);
        value = ObjectToString.getStringValue(getBorderColor());
        newProps.setProperty(key, value);

        key = NODE_LINETYPE.getDefaultPropertyKey(baseKey);
        value = ObjectToString.getStringValue(getBorderLine());
        newProps.setProperty(key, value);

        key = NODE_SHAPE.getDefaultPropertyKey(baseKey);

        final NodeShape nodeShape = getNodeShape();
        value = ObjectToString.getStringValue(nodeShape);
        newProps.setProperty(key, value);

        key = NODE_WIDTH.getDefaultPropertyKey(baseKey);

        final Double nodeWidthDouble = new Double(getWidth());
        value = ObjectToString.getStringValue(nodeWidthDouble);
        newProps.setProperty(key, value);

        key = NODE_HEIGHT.getDefaultPropertyKey(baseKey);

        final Double nodeHeightDouble = new Double(getHeight());
        value = ObjectToString.getStringValue(nodeHeightDouble);
        newProps.setProperty(key, value);

        key = NODE_SIZE.getDefaultPropertyKey(baseKey);

        final Double nodeSizeDouble = new Double(getSize());
        value = ObjectToString.getStringValue(nodeSizeDouble);
        newProps.setProperty(key, value);

        key = NODE_LABEL.getDefaultPropertyKey(baseKey);
        value = ObjectToString.getStringValue(getLabel());
        newProps.setProperty(key, value);

        key = NODE_TOOLTIP.getDefaultPropertyKey(baseKey);
        value = ObjectToString.getStringValue(getToolTip());
        newProps.setProperty(key, value);

        key = NODE_FONT_FACE.getDefaultPropertyKey(baseKey);
        value = ObjectToString.getStringValue(getFont());
        newProps.setProperty(key, value);

        key = NODE_LABEL_POSITION.getDefaultPropertyKey(baseKey);
        value = ObjectToString.getStringValue(getLabelPosition());
        newProps.setProperty(key, value);

        return newProps;
    }

    /**
     * DOCUMENT ME!
     *
     * @param prefix DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getDescription(String prefix) {
        if (prefix == null)
            prefix = "";

        final String lineSep = System.getProperty("line.separator");
        final StringBuffer sb = new StringBuffer();

        sb.append(prefix + "NodeFillColor = ")
          .append(fillColor)
          .append(lineSep);
        sb.append(prefix + "NodeBorderColor = ")
          .append(borderColor)
          .append(lineSep);

        String nodeLineText = ObjectToString.getStringValue(borderLine);
        sb.append(prefix + "NodeLineType = ")
          .append(nodeLineText)
          .append(lineSep);

        Byte nodeShapeByte = new Byte(shape);
        String nodeShapeText = ObjectToString.getStringValue(nodeShapeByte);
        sb.append(prefix + "NodeShape = ")
          .append(nodeShapeText)
          .append(lineSep);
        sb.append(prefix + "NodeWidth = ")
          .append(width)
          .append(lineSep);
        sb.append(prefix + "NodeHeight = ")
          .append(height)
          .append(lineSep);
        sb.append(prefix + "NodeSize = ")
          .append(size)
          .append(lineSep);
        sb.append(prefix + "NodeLabel = ")
          .append(label)
          .append(lineSep);
        sb.append(prefix + "NodeToolTip = ")
          .append(toolTip)
          .append(lineSep);
        sb.append(prefix + "NodeFont = ")
          .append(font)
          .append(lineSep);
        sb.append(prefix + "NodeFontColor = ")
          .append(labelColor.toString())
          .append(lineSep);
        sb.append(prefix + "NodeLabelPosition = ")
          .append(labelPosition.toString())
          .append(lineSep);
        sb.append(prefix + "nodeSizeLocked = ")
          .append(nodeSizeLocked)
          .append(lineSep);

        return sb.toString();
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getDescription() {
        return getDescription(null);
    }

    /**
     * DOCUMENT ME!
     *
     * @param type DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    @Deprecated
    public Object get(final byte type) {
        return get(VisualPropertyType.getVisualPorpertyType(type));
    }

    /**
     * DOCUMENT ME!
     *
     * @param type DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Object get(final VisualPropertyType type) {
        Object defaultObj = null;

        switch (type) {
        case NODE_FILL_COLOR:
            defaultObj = getFillColor();

            break;

        case NODE_BORDER_COLOR:
            defaultObj = getBorderColor();

            break;

        case NODE_LINETYPE:
            defaultObj = getBorderLine();

            break;

        case NODE_LINE_WIDTH:
            defaultObj = getBorderLine()
                             .getWidth();

            break;

        case NODE_SHAPE:
            defaultObj = getNodeShape();

            break;

        case NODE_HEIGHT:
            defaultObj = new Double(getHeight());

            break;

        case NODE_WIDTH:
            defaultObj = new Double(getWidth());

            break;

        case NODE_SIZE:
            defaultObj = new Double(getSize());

            break;

        case NODE_LABEL:
            defaultObj = getLabel();

            break;

        case NODE_LABEL_COLOR:
            defaultObj = getLabelColor();

            break;

        case NODE_TOOLTIP:
            defaultObj = getToolTip();

            break;

        case NODE_FONT_FACE:
            defaultObj = getFont();

            break;

        case NODE_FONT_SIZE:
            defaultObj = new Double(getFont().getSize());

            break;

        case NODE_LABEL_POSITION:
            defaultObj = getLabelPosition();

            break;
        }

        return defaultObj;
    }

    /**
     * DOCUMENT ME!
     *
     * @param type DOCUMENT ME!
     * @param c DOCUMENT ME!
     */
    @Deprecated
    public void set(final byte type, final Object c) {
        set(
            VisualPropertyType.getVisualPorpertyType(type),
            c);
    }

    /**
     * DOCUMENT ME!
     *
     * @param type DOCUMENT ME!
     * @param c DOCUMENT ME!
     */
    public void set(final VisualPropertyType type, final Object c) {
        switch (type) {
        case NODE_FILL_COLOR:
            setFillColor((Color) c);

            break;

        case NODE_BORDER_COLOR:
            setBorderColor((Color) c);

            break;

        case NODE_LINETYPE:
            setBorderLine((Line) c);

            break;

        case NODE_SHAPE:
            setNodeShape(((NodeShape) c));

            break;

        case NODE_HEIGHT:
            setHeight(((Double) c).doubleValue());

            break;

        case NODE_WIDTH:
            setWidth(((Double) c).doubleValue());

            break;

        case NODE_SIZE:
            setSize(((Double) c).doubleValue());

            break;

        case NODE_LABEL:
            setLabel((String) c);

            break;

        case NODE_LABEL_COLOR:
            setLabelColor((Color) c);

            break;

        case NODE_TOOLTIP:
            setToolTip((String) c);

            break;

        case NODE_FONT_FACE:
            setFont((Font) c);

            break;

        case NODE_FONT_SIZE:
            setFontSize(((Double) c).floatValue());

            break;

        case NODE_LABEL_POSITION:
            setLabelPosition((LabelPosition) c);

            break;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param na DOCUMENT ME!
     */
    public void copy(final NodeAppearance na) {
        // remember the new lock state
        final boolean actualLockState = na.getNodeSizeLocked();

        // set everything to false so that it copy
        // correctly
        setNodeSizeLocked(false);
        na.setNodeSizeLocked(false);

        setFillColor(na.getFillColor());
        setBorderColor(na.getBorderColor());
        setBorderLine(na.getBorderLine());
        setNodeShape(na.getNodeShape());
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
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Object clone() {
        final NodeAppearance na = new NodeAppearance();
        na.copy(this);

        return na;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean getNodeSizeLocked() {
        return nodeSizeLocked;
    }

    /**
     * DOCUMENT ME!
     *
     * @param b DOCUMENT ME!
     */
    public void setNodeSizeLocked(boolean b) {
        nodeSizeLocked = b;
    }

    /**
     * DOCUMENT ME!
     *
     * @param n DOCUMENT ME!
     */
    public void applyBypass(final Node n) {
        if (n == null)
            return;

        final String id = n.getIdentifier();
        final CyAttributes attrs = Cytoscape.getNodeAttributes();

        setFillColor(
            BypassHelper.getColorBypass(
                attrs,
                id,
                NODE_FILL_COLOR.getBypassAttrName()));

        setBorderColor(
            BypassHelper.getColorBypass(
                attrs,
                id,
                NODE_BORDER_COLOR.getBypassAttrName()));

        setBorderLine((Line) BypassHelper.getBypass(
                attrs,
                id,
                NODE_LINETYPE.getBypassAttrName(),
                Line.class));

        final NodeShape tempShape = (NodeShape) BypassHelper.getBypass(
                attrs,
                id,
                NODE_SHAPE.getBypassAttrName(),
                NodeShape.class);

        if (tempShape != null)
            setNodeShape(tempShape);

        final Double w = (Double) BypassHelper.getBypass(
                attrs,
                id,
                NODE_WIDTH.getBypassAttrName(),
                Double.class);

        if (w != null)
            setWidth(w.doubleValue());

        final Double h = (Double) BypassHelper.getBypass(
                attrs,
                id,
                NODE_HEIGHT.getBypassAttrName(),
                Double.class);

        if (h != null)
            setHeight(h.doubleValue());

        final Double s = (Double) BypassHelper.getBypass(
                attrs,
                id,
                NODE_SIZE.getBypassAttrName(),
                Double.class);

        if (s != null)
            setSize(s.doubleValue());

        setLabel((String) BypassHelper.getBypass(
                attrs,
                id,
                NODE_LABEL.getBypassAttrName(),
                String.class));

        setToolTip((String) BypassHelper.getBypass(
                attrs,
                id,
                NODE_TOOLTIP.getBypassAttrName(),
                String.class));

        setFont((Font) BypassHelper.getBypass(
                attrs,
                id,
                NODE_FONT_FACE.getBypassAttrName(),
                Font.class));

        final Double f = (Double) BypassHelper.getBypass(
                attrs,
                id,
                NODE_FONT_SIZE.getBypassAttrName(),
                Double.class);

        if (f != null)
            setFontSize(f.floatValue());

        setLabelColor((Color) BypassHelper.getBypass(
                attrs,
                id,
                NODE_LABEL_COLOR.getBypassAttrName(),
                Color.class));

        setLabelPosition((LabelPosition) BypassHelper.getBypass(
                attrs,
                id,
                NODE_LABEL_POSITION.getBypassAttrName(),
                LabelPosition.class));
    }
}
