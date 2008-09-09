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
import static cytoscape.visual.VisualPropertyType.EDGE_FONT_SIZE;
import static cytoscape.visual.VisualPropertyType.EDGE_LABEL;
import static cytoscape.visual.VisualPropertyType.EDGE_LABEL_COLOR;
import static cytoscape.visual.VisualPropertyType.EDGE_LINETYPE;
import static cytoscape.visual.VisualPropertyType.EDGE_LINE_WIDTH;
import static cytoscape.visual.VisualPropertyType.EDGE_SRCARROW_COLOR;
import static cytoscape.visual.VisualPropertyType.EDGE_SRCARROW_SHAPE;
import static cytoscape.visual.VisualPropertyType.EDGE_TGTARROW_COLOR;
import static cytoscape.visual.VisualPropertyType.EDGE_TGTARROW_SHAPE;
import static cytoscape.visual.VisualPropertyType.EDGE_TOOLTIP;
import static cytoscape.visual.VisualPropertyType.EDGE_SRCARROW;
import static cytoscape.visual.VisualPropertyType.EDGE_TGTARROW;

import cytoscape.visual.parsers.ArrowParser;
import cytoscape.visual.parsers.ColorParser;
import cytoscape.visual.parsers.FloatParser;
import cytoscape.visual.parsers.FontParser;
import cytoscape.visual.parsers.ObjectToString;

import giny.model.Edge;

import giny.view.EdgeView;
import giny.view.Label;

import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.awt.Stroke;

import java.util.Properties;


/**
 * Objects of this class hold data describing the appearance of an Edge.
 */
public class EdgeAppearance extends Appearance {

	/**
	 * Creates a new EdgeAppearance object.
	 */
	public EdgeAppearance() {
		super();
	}

	/**
	 * Clone.
	 */
    public Object clone() {
        EdgeAppearance ga = new EdgeAppearance();
        ga.copy(this);
        return ga;
	}

	/**
	 * @deprecated Use Appearance.get(VisualPropertyType) instead. Will be removed 5/2008.
	 */
	public Color getColor() {
		return (Color)(get(EDGE_COLOR));
	}

	/**
	 * @deprecated Use Appearance.set(VisualPropertyType,Object) instead. Will be removed 5/2008.
	 */
	public void setColor(Color c) {
		if (c != null)
			set(EDGE_COLOR, c);
	}

	/**
	 * @deprecated Use Appearance.get(VisualPropertyType) instead. Will be removed 5/2008.
	 */
	public LineType getLineType() {
		return (LineType)(get(EDGE_LINETYPE));
	}

	/**
	 * @deprecated Use Appearance.get(VisualPropertyType) instead. Will be removed 5/2008.
	 */
	public Float getLineWidth() {
		return (Float)(get(EDGE_LINE_WIDTH));
	}

	/**
	 * @deprecated Use Appearance.set(VisualPropertyType,Object) instead. Will be removed 5/2008.
	 */
	public void setLineType(LineType lt) {
		if (lt != null)
			set(EDGE_LINETYPE,lt);
	}

	/**
	 * @deprecated Use Appearance.set(VisualPropertyType,Object) instead. Will be removed 5/2008.
	 */
	public void setLineWidth(Float w) {
		if (w != null)
			set(EDGE_LINE_WIDTH,w);
	}

	/**
	 * @deprecated Use Appearance.get(VisualPropertyType) instead. Will be removed 5/2008.
	 */
	public Arrow getSourceArrow() {
		return (Arrow)(get(EDGE_SRCARROW));
	}

	/**
	 * @deprecated Use Appearance.set(VisualPropertyType,Object) instead. Will be removed 5/2008.
	 */
	public void setSourceArrow(Arrow a) {
		if (a != null) {
			set(EDGE_SRCARROW,a);
			set(EDGE_SRCARROW_SHAPE,a.getShape());
			set(EDGE_SRCARROW_COLOR,a.getColor());
		}
	}

	/**
	 * @deprecated Use Appearance.get(VisualPropertyType) instead. Will be removed 5/2008.
	 */
	public Arrow getTargetArrow() {
		return (Arrow)(get(EDGE_TGTARROW));
	}

	/**
	 * @deprecated Use Appearance.set(VisualPropertyType,Object) instead. Will be removed 5/2008.
	 */
	public void setTargetArrow(Arrow a) {
		if (a != null) {
			set(EDGE_TGTARROW,a);
			set(EDGE_TGTARROW_SHAPE,a.getShape());
			set(EDGE_TGTARROW_COLOR,a.getColor());
		}
	}

	/**
	 * @deprecated Use Appearance.get(VisualPropertyType) instead. Will be removed 5/2008.
	 */
	public String getLabel() {
		return (String)(get(EDGE_LABEL));
	}

	/**
	 * @deprecated Use Appearance.set(VisualPropertyType,Object) instead. Will be removed 5/2008.
	 */
	public void setLabel(String s) {
		if (s != null)
			set(EDGE_LABEL,s);
	}

	/**
	 * @deprecated Use Appearance.get(VisualPropertyType) instead. Will be removed 5/2008.
	 */
	public String getToolTip() {
		return (String)(get(EDGE_TOOLTIP));
	}

	/**
	 * @deprecated Use Appearance.set(VisualPropertyType,Object) instead. Will be removed 5/2008.
	 */
	public void setToolTip(String s) {
		if (s != null)
			set(EDGE_TOOLTIP,s);
	}

	/**
	 * @deprecated Use Appearance.get(VisualPropertyType) instead. Will be removed 5/2008.
	 */
	public Font getFont() {
		return (Font)(get(EDGE_FONT_FACE));
	}

	/**
	 * @deprecated Use Appearance.set(VisualPropertyType,Object) instead. Will be removed 5/2008.
	 */
	public void setFont(Font f) {
		if (f != null)
			set(EDGE_FONT_FACE,f);
	}

	/**
	 * @deprecated Use Appearance.get(VisualPropertyType) instead. Will be removed 5/2008.
	 */
	public float getFontSize() {
		return ((Number)(get(EDGE_FONT_SIZE))).floatValue();
	}

	/**
	 * @deprecated Use Appearance.set(VisualPropertyType,Object) instead. Will be removed 5/2008.
	 */
	public void setFontSize(float f) {
		set(EDGE_FONT_SIZE,new Float(f));
	}

	/**
	 * @deprecated Use Appearance.get(VisualPropertyType) instead. Will be removed 5/2008.
	 */
	public Color getLabelColor() {
		return (Color)(get(EDGE_LABEL_COLOR));
	}

	/**
	 * @deprecated Use Appearance.set(VisualPropertyType,Object) instead. Will be removed 5/2008.
	 */
	public void setLabelColor(Color c) {
		if (c != null)
			set(EDGE_LABEL_COLOR,c);
	}

	/**
	 * @deprecated Use applyAppearance(edgeView) instead - now we always
	 *             optimize. Will be removed 10/2007
	 */
	public void applyAppearance(EdgeView edgeView, boolean optimizer) {
		super.applyAppearance(edgeView);
	}

	/**
	 * @deprecated Use Appearance.get(VisualPropertyType) instead. Will be removed 5/2008.
	 */
	public Object get(byte b) {
		return get(VisualPropertyType.getVisualPorpertyType(b));
	}
	
	/**
	 * @deprecated Use Appearance.set(VisualPropertyType,Object) instead. Will be removed 5/2008.
	 */
	public void set(byte b, Object o) {
		set(VisualPropertyType.getVisualPorpertyType(b),o);
	}
}
