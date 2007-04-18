/*
 File: EdgeAppearance.java

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

//----------------------------------------------------------------------------
import static cytoscape.visual.VisualPropertyType.EDGE_COLOR;
import static cytoscape.visual.VisualPropertyType.EDGE_FONT_FACE;
import static cytoscape.visual.VisualPropertyType.EDGE_LABEL;
import static cytoscape.visual.VisualPropertyType.EDGE_LABEL_COLOR;
import static cytoscape.visual.VisualPropertyType.EDGE_LINETYPE;
import static cytoscape.visual.VisualPropertyType.EDGE_LINE_WIDTH;
import static cytoscape.visual.VisualPropertyType.EDGE_SRCARROW_COLOR;
import static cytoscape.visual.VisualPropertyType.EDGE_SRCARROW_SHAPE;
import static cytoscape.visual.VisualPropertyType.EDGE_TGTARROW_COLOR;
import static cytoscape.visual.VisualPropertyType.EDGE_TGTARROW_SHAPE;
import static cytoscape.visual.VisualPropertyType.EDGE_TOOLTIP;

import cytoscape.visual.parsers.ArrowParser;
import cytoscape.visual.parsers.ColorParser;
import cytoscape.visual.parsers.FloatParser;
import cytoscape.visual.parsers.FontParser;
import cytoscape.visual.parsers.LineParser;
import cytoscape.visual.parsers.ObjectToString;

import giny.model.Edge;

import giny.view.EdgeView;
import giny.view.Label;

import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.awt.Stroke;

import java.util.Properties;


//----------------------------------------------------------------------------
/**
 * Objects of this class hold data describing the appearance of an Edge.
 */
public class EdgeAppearance
    implements Appearance, Cloneable {
    private static final Font DEFAULT_FONT = new Font("SansSerif", Font.PLAIN,
            10);
    private static final Color DEFAULT_COLOR = Color.black;
    private Color color = DEFAULT_COLOR;
    @Deprecated
    private LineType lineType = LineType.LINE_1;
    private Line line = Line.DEFAULT_LINE;
    private Arrow sourceArrow = Arrow.NONE;
    private Arrow targetArrow = Arrow.NONE;
    private String label = "";
    private String toolTip = "";
    private Font font = DEFAULT_FONT;
    private Color labelColor = DEFAULT_COLOR;

    /**
     * Creates a new EdgeAppearance object.
     */
    public EdgeAppearance() {
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Color getColor() {
        return color;
    }

    /**
     * DOCUMENT ME!
     *
     * @param c DOCUMENT ME!
     */
    public void setColor(Color c) {
        if (c != null)
            color = c;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    @Deprecated
    public LineType getLineType() {
        return lineType;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Line getLine() {
        return line;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Float getLineWidth() {
        return line.getWidth();
    }

    /**
     * DOCUMENT ME!
     *
     * @param lt DOCUMENT ME!
     */
    @Deprecated
    public void setLineType(LineType lt) {
        if (lt != null)
            lineType = lt;
    }

    /**
     * DOCUMENT ME!
     *
     * @param newLine DOCUMENT ME!
     */
    public void setLine(Line newLine) {
        if (newLine != null)
            line = newLine;
    }

    /**
     * DOCUMENT ME!
     *
     * @param w DOCUMENT ME!
     */
    public void setLineWidth(Float w) {
        if (w != null)
            line.setWidth(w);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Arrow getSourceArrow() {
        return sourceArrow;
    }

    /**
     * DOCUMENT ME!
     *
     * @param a DOCUMENT ME!
     */
    public void setSourceArrow(Arrow a) {
        if (a != null)
            sourceArrow = a;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Color getSourceArrowColor() {
        return sourceArrow.getColor();
    }

    /**
     * DOCUMENT ME!
     *
     * @param c DOCUMENT ME!
     */
    public void setSourceArrowColor(Color c) {
        if (c != null)
            sourceArrow.setColor(c);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Arrow getTargetArrow() {
        return targetArrow;
    }

    /**
     * DOCUMENT ME!
     *
     * @param a DOCUMENT ME!
     */
    public void setTargetArrow(Arrow a) {
        if (a != null)
            targetArrow = a;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Color getTargetArrowColor() {
        return targetArrow.getColor();
    }

    /**
     * DOCUMENT ME!
     *
     * @param c DOCUMENT ME!
     */
    public void setTargetArrowColor(Color c) {
        if (c != null)
            targetArrow.setColor(c);
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
     * @deprecated Use applyAppearance(edgeView) instead - now we always
     *             optimize. Will be removed 10/2007
     */
    public void applyAppearance(EdgeView edgeView, boolean optimizer) {
        applyAppearance(edgeView);
    }

    /**
     * DOCUMENT ME!
     *
     * @param edgeView DOCUMENT ME!
     */
    public void applyAppearance(final EdgeView edgeView) {
        final Paint newUnselectedPaint = getColor();

        if (!newUnselectedPaint.equals(edgeView.getUnselectedPaint()))
            edgeView.setUnselectedPaint(newUnselectedPaint);

        final Stroke newStroke = getLine()
                                     .getStroke();

        if (!newStroke.equals(edgeView.getStroke()))
            edgeView.setStroke(newStroke);

        final int newSourceEdge = getSourceArrow()
                                      .getShape()
                                      .getGinyArrow();

        if (newSourceEdge != edgeView.getSourceEdgeEnd())
            edgeView.setSourceEdgeEnd(newSourceEdge);

        /*
         * New for 2.5: arrow colors can be different from edge color
         */
        final Paint newSourceArrowColor = getSourceArrow()
                                              .getColor();

        if (newSourceArrowColor != edgeView.getSourceEdgeEndPaint())
            edgeView.setSourceEdgeEndPaint(newSourceArrowColor);

        final int newTargetEdge = getTargetArrow()
                                      .getShape()
                                      .getGinyArrow();

        if (newTargetEdge != edgeView.getTargetEdgeEnd())
            edgeView.setTargetEdgeEnd(newTargetEdge);

        final Paint newTargetArrowColor = getTargetArrow()
                                              .getColor();

        if (newTargetArrowColor != edgeView.getTargetEdgeEndPaint())
            edgeView.setTargetEdgeEndPaint(newTargetArrowColor);

        final Label label = edgeView.getLabel();

        final String newText = getLabel();

        if (!newText.equals(label.getText()))
            label.setText(newText);

        final Font newFont = getFont();

        if (!newFont.equals(label.getFont()))
            label.setFont(newFont);

        final Paint newLabelColor = getLabelColor();

        if (!newLabelColor.equals(label.getTextPaint()))
            label.setTextPaint(newLabelColor);
    }

    /**
     * DOCUMENT ME!
     *
     * @param eacProps DOCUMENT ME!
     * @param baseKey DOCUMENT ME!
     */
    public void applyDefaultProperties(final Properties eacProps,
        final String baseKey) {
        String value = null;

        value = eacProps.getProperty(EDGE_COLOR.getDefaultPropertyKey(baseKey));

        if (value != null) {
            final Color c = (new ColorParser()).parseColor(value);

            if (c != null)
                setColor(c);
        }

        value = eacProps.getProperty(
                EDGE_LINETYPE.getDefaultPropertyKey(baseKey));

        if (value != null) {
            final Line lt = (new LineParser()).parseLine(value);

            if (lt != null)
                setLine(lt);
        }

        // TODO: linewidthparser should be implemented.  
        value = eacProps.getProperty(
                EDGE_LINE_WIDTH.getDefaultPropertyKey(baseKey));

        if (value != null) {
            final Float w = (new FloatParser()).parseFloat(value);

            if (w != null)
                setLineWidth(w);
        }

        value = eacProps.getProperty(
                EDGE_SRCARROW_SHAPE.getDefaultPropertyKey(baseKey));

        if (value != null) {
            final Arrow a = (new ArrowParser()).parseArrow(value);

            if (a != null)
                setSourceArrow(a);
        }

        value = eacProps.getProperty(
                EDGE_SRCARROW_COLOR.getDefaultPropertyKey(baseKey));

        if (value != null) {
            final Color c = (new ColorParser()).parseColor(value);

            if (c != null)
                setSourceArrowColor(c);
        }

        value = eacProps.getProperty(
                EDGE_TGTARROW_SHAPE.getDefaultPropertyKey(baseKey));

        if (value != null) {
            final Arrow a = (new ArrowParser()).parseArrow(value);

            if (a != null)
                setTargetArrow(a);
        }

        value = eacProps.getProperty(
                EDGE_TGTARROW_COLOR.getDefaultPropertyKey(baseKey));

        if (value != null) {
            final Color c = (new ColorParser()).parseColor(value);

            if (c != null)
                setTargetArrowColor(c);
        }

        value = eacProps.getProperty(EDGE_LABEL.getDefaultPropertyKey(baseKey));

        if (value != null)
            setLabel(value);

        value = eacProps.getProperty(
                EDGE_TOOLTIP.getDefaultPropertyKey(baseKey));

        if (value != null)
            setToolTip(value);

        value = eacProps.getProperty(
                EDGE_FONT_FACE.getDefaultPropertyKey(baseKey));

        if (value != null) {
            final Font f = (new FontParser()).parseFont(value);

            if (f != null)
                setFont(f);
        }

        value = eacProps.getProperty(
                EDGE_LABEL_COLOR.getDefaultPropertyKey(baseKey));

        if (value != null) {
            final Color c = (new ColorParser()).parseColor(value);
            setLabelColor(c);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param baseKey DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Properties getDefaultProperties(final String baseKey) {
        String key = null;
        String value = null;
        final Properties newProps = new Properties();

        // Save default values
        key = EDGE_COLOR.getDefaultPropertyKey(baseKey);
        value = ObjectToString.getStringValue(getColor());
        newProps.setProperty(key, value);

        key = EDGE_LINETYPE.getDefaultPropertyKey(baseKey);
        value = ObjectToString.getStringValue(getLine());
        newProps.setProperty(key, value);

        key = EDGE_LINE_WIDTH.getDefaultPropertyKey(baseKey);
        value = ObjectToString.getStringValue(getLineWidth());
        newProps.setProperty(key, value);

        key = EDGE_SRCARROW_SHAPE.getDefaultPropertyKey(baseKey);
        value = ObjectToString.getStringValue(getSourceArrow());
        newProps.setProperty(key, value);

        key = EDGE_SRCARROW_COLOR.getDefaultPropertyKey(baseKey);
        value = ObjectToString.getStringValue(getSourceArrowColor());
        newProps.setProperty(key, value);

        key = EDGE_TGTARROW_SHAPE.getDefaultPropertyKey(baseKey);
        value = ObjectToString.getStringValue(getTargetArrow());
        newProps.setProperty(key, value);

        key = EDGE_TGTARROW_COLOR.getDefaultPropertyKey(baseKey);
        value = ObjectToString.getStringValue(getTargetArrowColor());
        newProps.setProperty(key, value);

        key = EDGE_LABEL.getDefaultPropertyKey(baseKey);
        value = ObjectToString.getStringValue(getLabel());
        newProps.setProperty(key, value);

        key = EDGE_TOOLTIP.getDefaultPropertyKey(baseKey);
        value = ObjectToString.getStringValue(getToolTip());
        newProps.setProperty(key, value);

        key = EDGE_FONT_FACE.getDefaultPropertyKey(baseKey);
        value = ObjectToString.getStringValue(getFont());
        newProps.setProperty(key, value);

        key = EDGE_LABEL_COLOR.getDefaultPropertyKey(baseKey);
        value = ObjectToString.getStringValue(getLabelColor());
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

        sb.append(prefix + "EdgeColor = ")
          .append(color)
          .append(lineSep);

        String edgeLineTypeText = ObjectToString.getStringValue(line);
        sb.append(prefix + "EdgeLineType = ")
          .append(edgeLineTypeText)
          .append(lineSep);

        String sourceArrowText = ObjectToString.getStringValue(sourceArrow);
        sb.append(prefix + "EdgeSourceArrow = ")
          .append(sourceArrowText)
          .append(lineSep);

        String targetArrowText = ObjectToString.getStringValue(targetArrow);
        sb.append(prefix + "EdgeTargetArrow = ")
          .append(targetArrowText)
          .append(lineSep);

        sb.append(prefix + "EdgeSourceArrowColor = ")
          .append(sourceArrow.getColor())
          .append(lineSep);

        sb.append(prefix + "EdgeTargetArrowColor = ")
          .append(targetArrow.getColor())
          .append(lineSep);

        sb.append(prefix + "EdgeLabel = ")
          .append(label)
          .append(lineSep);
        sb.append(prefix + "EdgeToolTip = ")
          .append(toolTip)
          .append(lineSep);
        sb.append(prefix + "EdgeFont = ")
          .append(font)
          .append(lineSep);
        sb.append(prefix + "EdgeLabelColor = ")
          .append(labelColor)
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
     * Use public Object get(final VisualPropertyType type) instead.
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
        case EDGE_COLOR:
            defaultObj = getColor();

            break;

        case EDGE_LINETYPE:
            defaultObj = getLine();

            break;

        case EDGE_LINE_WIDTH:
            defaultObj = getLineWidth();

            break;

        case EDGE_SRCARROW:
        case EDGE_SRCARROW_SHAPE:
            defaultObj = getSourceArrow();

            break;

        case EDGE_TGTARROW:
        case EDGE_TGTARROW_SHAPE:
            defaultObj = getTargetArrow();

            break;

        case EDGE_SRCARROW_COLOR:
            defaultObj = getSourceArrowColor();

            break;

        case EDGE_TGTARROW_COLOR:
            defaultObj = getTargetArrowColor();

            break;

        case EDGE_LABEL:
            defaultObj = getLabel();

            break;

        case EDGE_TOOLTIP:
            defaultObj = getToolTip();

            break;

        case EDGE_FONT_FACE:
            defaultObj = getFont();

            break;

        case EDGE_FONT_SIZE:
            defaultObj = new Double(getFont().getSize2D());

            break;

        case EDGE_LABEL_COLOR:
            defaultObj = getLabelColor();

            break;
        }

        return defaultObj;
    }

    /**
     * Use public void set(final VisualPropertyType type, final Object c)
     * instead.
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
     * @param newValue DOCUMENT ME!
     */
    public void set(final VisualPropertyType type, final Object newValue) {
        switch (type) {
        case EDGE_COLOR:
            setColor((Color) newValue);

            break;

        case EDGE_LINETYPE:
            setLine((Line) newValue);

            break;

        case EDGE_LINE_WIDTH:
            setLineWidth(((Double) newValue).floatValue());

            break;

        case EDGE_SRCARROW_SHAPE:
            setSourceArrow((Arrow) newValue);

            break;

        case EDGE_TGTARROW_SHAPE:
            setTargetArrow((Arrow) newValue);

            break;

        case EDGE_SRCARROW_COLOR:
            setSourceArrowColor((Color) newValue);

            break;

        case EDGE_TGTARROW_COLOR:
            setTargetArrowColor((Color) newValue);

            break;

        case EDGE_LABEL:
            setLabel((String) newValue);

            break;

        case EDGE_TOOLTIP:
            setToolTip((String) newValue);

            break;

        case EDGE_FONT_FACE:
            setFont((Font) newValue);

            break;

        case EDGE_FONT_SIZE:
            setFontSize(((Double) newValue).floatValue());

            break;

        case EDGE_LABEL_COLOR:
            setLabelColor((Color) newValue);

            break;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param ea DOCUMENT ME!
     */
    public void copy(final EdgeAppearance ea) {
        setColor(ea.getColor());
        setLine(ea.getLine());

        // New from 2.5
        setLineWidth(ea.getLineWidth());

        setSourceArrow(ea.getSourceArrow());
        setTargetArrow(ea.getTargetArrow());

        // New from 2.5
        setSourceArrowColor(ea.getSourceArrowColor());
        setTargetArrowColor(ea.getTargetArrowColor());

        setLabel(ea.getLabel());
        setToolTip(ea.getToolTip());
        setFont(ea.getFont());
        setLabelColor(ea.getLabelColor());
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Object clone() {
        final EdgeAppearance ea = new EdgeAppearance();
        ea.copy(this);

        return ea;
    }

    /**
     * DOCUMENT ME!
     *
     * @param e DOCUMENT ME!
     */
    public void applyBypass(final Edge e) {
        if (e == null)
            return;

        final String id = e.getIdentifier();
        final CyAttributes attrs = Cytoscape.getEdgeAttributes();

        setColor(
            BypassHelper.getColorBypass(
                attrs,
                id,
                EDGE_COLOR.getBypassAttrName()));
        setLine((Line) BypassHelper.getBypass(
                attrs,
                id,
                EDGE_LINETYPE.getBypassAttrName(),
                Line.class));
        setSourceArrow((Arrow) BypassHelper.getBypass(
                attrs,
                id,
                EDGE_SRCARROW_SHAPE.getBypassAttrName(),
                Arrow.class));
        setTargetArrow((Arrow) BypassHelper.getBypass(
                attrs,
                id,
                EDGE_TGTARROW_SHAPE.getBypassAttrName(),
                Arrow.class));

        // New from 2.5:
        setSourceArrowColor(
            BypassHelper.getColorBypass(
                attrs,
                id,
                EDGE_SRCARROW_COLOR.getBypassAttrName()));
        setTargetArrowColor(
            BypassHelper.getColorBypass(
                attrs,
                id,
                EDGE_TGTARROW_COLOR.getBypassAttrName()));

        setLabel((String) BypassHelper.getBypass(
                attrs,
                id,
                EDGE_LABEL.getBypassAttrName(),
                String.class));
        setToolTip((String) BypassHelper.getBypass(
                attrs,
                id,
                EDGE_TOOLTIP.getBypassAttrName(),
                String.class));
        setFont((Font) BypassHelper.getBypass(
                attrs,
                id,
                EDGE_FONT_FACE.getBypassAttrName(),
                Font.class));

        final Double d = (Double) BypassHelper.getBypass(
                attrs,
                id,
                EDGE_FONT_FACE.getBypassAttrName(),
                Double.class);

        if (d != null)
            setFontSize(d.floatValue());

        setLabelColor(
            BypassHelper.getColorBypass(
                attrs,
                id,
                EDGE_LABEL_COLOR.getBypassAttrName()));
    }
}
