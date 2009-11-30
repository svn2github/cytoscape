
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

package ding.view;

import cytoscape.render.stateful.CustomGraphic;
import cytoscape.render.stateful.NodeDetails;

import cytoscape.util.intr.IntObjHash;

import giny.model.GraphPerspective;
import giny.view.Label;
import giny.view.NodeView;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.TexturePaint;

import java.util.HashMap;
import java.util.Iterator;


/*
 * Access to the methods of this class should be synchronized externally if
 * there is a threat of multiple threads.
 */
class DNodeDetails extends IntermediateNodeDetails {
	final DGraphView m_view;
	final IntObjHash m_colorsLowDetail = new IntObjHash();
	final Object m_deletedEntry = new Object();

	// The values are Byte objects; the bytes are shapes defined in
	// cytoscape.render.immed.GraphGraphics.
	final HashMap m_shapes = new HashMap();
	final HashMap m_fillPaints = new HashMap();
	final HashMap m_borderWidths = new HashMap();
	final HashMap m_borderPaints = new HashMap();
	final HashMap m_labelCounts = new HashMap();
	final HashMap m_labelTexts = new HashMap();
	final HashMap m_labelFonts = new HashMap();
	final HashMap m_labelPaints = new HashMap();
	final HashMap m_labelTextAnchors = new HashMap();
	final HashMap m_labelNodeAnchors = new HashMap();
	final HashMap m_labelJustifys = new HashMap();
	final HashMap m_labelOffsetXs = new HashMap();
	final HashMap m_labelOffsetYs = new HashMap();
	final HashMap m_labelWidths = new HashMap();

	DNodeDetails(DGraphView view) {
		m_view = view;
	}

	void unregisterNode(int node) {
		final Object o = m_colorsLowDetail.get(node);

		if ((o != null) && (o != m_deletedEntry))
			m_colorsLowDetail.put(node, m_deletedEntry);

		final Integer key = new Integer(node);
		m_shapes.remove(key);
		m_fillPaints.remove(key);
		m_borderWidths.remove(key);
		m_borderPaints.remove(key);
		m_labelTextAnchors.remove(key);
		m_labelNodeAnchors.remove(key);
		m_labelJustifys.remove(key);
		m_labelOffsetXs.remove(key);
		m_labelOffsetYs.remove(key);
		m_labelWidths.remove(key);

		final Object intr = m_labelCounts.remove(key);
		final int labelCount = ((intr == null) ? 0 : ((Integer) intr).intValue());

		for (int i = 0; i < labelCount; i++) {
			final Long lKey = new Long((((long) node) << 32) | ((long) i));
			m_labelTexts.remove(lKey);
			m_labelFonts.remove(lKey);
			m_labelPaints.remove(lKey);
		}
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param node DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public Color colorLowDetail(int node) {
		final Object o = m_colorsLowDetail.get(node);

		if ((o == null) || (o == m_deletedEntry))
			return super.colorLowDetail(node);

		return (Color) o;
	}

	/*
	 * A null color has the special meaning to remove overridden color.
	 */
	void overrideColorLowDetail(int node, Color color) {
		if ((color == null) || color.equals(super.colorLowDetail(node))) {
			final Object val = m_colorsLowDetail.get(node);

			if ((val != null) && (val != m_deletedEntry))
				m_colorsLowDetail.put(node, m_deletedEntry);
		} else
			m_colorsLowDetail.put(node, color);
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param node DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public byte shape(int node) {
		final Object o = m_shapes.get(new Integer(node));

		if (o == null)
			return super.shape(node);

		return ((Byte) o).byteValue();
	}

	/*
	 * The shape argument must be pre-checked for correctness.
	 * A negative shape value has the special meaning to remove overridden shape.
	 */
	void overrideShape(int node, byte shape) {
		if ((shape < 0) || (shape == super.shape(node)))
			m_shapes.remove(new Integer(node));
		else
			m_shapes.put(new Integer(node), new Byte(shape));
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param node DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public Paint fillPaint(int node) {
		final Object o = m_fillPaints.get(new Integer(node));

		if (o == null)
			return super.fillPaint(node);

		return (Paint) o;
	}

	/*
	 * A null paint has the special meaning to remove overridden paint.
	 */
	void overrideFillPaint(int node, Paint paint) {
		if ((paint == null) || paint.equals(super.fillPaint(node)))
			m_fillPaints.remove(new Integer(node));
		else
			m_fillPaints.put(new Integer(node), paint);
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param node DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public float borderWidth(int node) {
		final Object o = m_borderWidths.get(new Integer(node));

		if (o == null)
			return super.borderWidth(node);

		return ((Float) o).floatValue();
	}

	/*
	 * A negative width value has the special meaning to remove overridden width.
	 */
	void overrideBorderWidth(int node, float width) {
		if ((width < 0.0f) || (width == super.borderWidth(node)))
			m_borderWidths.remove(new Integer(node));
		else
			m_borderWidths.put(new Integer(node), new Float(width));
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param node DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public Paint borderPaint(int node) {
		final Object o = m_borderPaints.get(new Integer(node));

		if (o == null)
			return super.borderPaint(node);

		return (Paint) o;
	}

	/*
	 * A null paint has the special meaning to remove overridden paint.
	 */
	void overrideBorderPaint(int node, Paint paint) {
		if ((paint == null) || paint.equals(super.borderPaint(node)))
			m_borderPaints.remove(new Integer(node));
		else
			m_borderPaints.put(new Integer(node), paint);
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param node DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public int labelCount(int node) {
		final Object o = m_labelCounts.get(new Integer(node));

		if (o == null)
			return super.labelCount(node);

		return ((Integer) o).intValue();
	}

	/*
	 * A negative labelCount has the special meaning to remove overridden count.
	 */
	void overrideLabelCount(int node, int labelCount) {
		if ((labelCount < 0) || (labelCount == super.labelCount(node)))
			m_labelCounts.remove(new Integer(node));
		else
			m_labelCounts.put(new Integer(node), new Integer(labelCount));
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param node DOCUMENT ME!
	 * @param labelInx DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public String labelText(int node, int labelInx) {
		final long key = (((long) node) << 32) | ((long) labelInx);
		final Object o = m_labelTexts.get(new Long(key));

		if (o == null)
			return super.labelText(node, labelInx);

		return (String) o;
	}

	/*
	 * A null text has the special meaning to remove overridden text.
	 */
	void overrideLabelText(int node, int labelInx, String text) {
		final long key = (((long) node) << 32) | ((long) labelInx);

		if ((text == null) || text.equals(super.labelText(node, labelInx)))
			m_labelTexts.remove(new Long(key));
		else
			m_labelTexts.put(new Long(key), text);
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param node DOCUMENT ME!
	 * @param labelInx DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public Font labelFont(int node, int labelInx) {
		final long key = (((long) node) << 32) | ((long) labelInx);
		final Object o = m_labelFonts.get(new Long(key));

		if (o == null)
			return super.labelFont(node, labelInx);

		return (Font) o;
	}

	/*
	 * A null font has the special meaning to remove overridden font.
	 */
	void overrideLabelFont(int node, int labelInx, Font font) {
		final long key = (((long) node) << 32) | ((long) labelInx);

		if ((font == null) || font.equals(super.labelFont(node, labelInx)))
			m_labelFonts.remove(new Long(key));
		else
			m_labelFonts.put(new Long(key), font);
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param node DOCUMENT ME!
	 * @param labelInx DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public Paint labelPaint(int node, int labelInx) {
		final long key = (((long) node) << 32) | ((long) labelInx);
		final Object o = m_labelPaints.get(new Long(key));

		if (o == null)
			return super.labelPaint(node, labelInx);

		return (Paint) o;
	}

	/*
	 * A null paint has the special meaning to remove overridden paint.
	 */
	void overrideLabelPaint(int node, int labelInx, Paint paint) {
		final long key = (((long) node) << 32) | ((long) labelInx);

		if ((paint == null) || paint.equals(super.labelPaint(node, labelInx)))
			m_labelPaints.remove(new Long(key));
		else
			m_labelPaints.put(new Long(key), paint);
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param node DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 * @deprecated Switch to using the CustomGraphics way of manipulating custom graphics.
	 *             For details, see deprecation note under 
	 *  	       {@link ding.view.DNodeView#addCustomGraphic(Shape,Paint,int) DNodeView.addCustomGraphic(Shape,Paint,int)}.
	 *             Once switched, then
	 *             use {@link ding.view.DNodeView#getNumCustomGraphics() DNodeView.getNumCustomGraphics()}.
	 */
	@Deprecated public int graphicCount(int node) {
		final DNodeView nv = (DNodeView) m_view.getNodeView(~node);

		return nv.getCustomGraphicCount();
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param node DOCUMENT ME!
	 * @param inx DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 * @deprecated Switch to using the CustomGraphics way of manipulating custom graphics.
	 *             For details, see deprecation note under 
	 *  	       {@link ding.view.DNodeView#addCustomGraphic(Shape,Paint,int) addCustomGraphic(Shape,Paint,int)}.
	 *             Once switched, then
	 *             use {@link cytoscape.render.stateful.CustomGraphic#getShape() cytoscape.render.stateful.CustomGraphic.getShape()}.
	 */
	@Deprecated public Shape graphicShape(int node, int inx) {
		final DNodeView nv = (DNodeView) m_view.getNodeView(~node);

		return nv.getCustomGraphicShape(inx);
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param node DOCUMENT ME!
	 * @param inx DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 * @deprecated Switch to using the CustomGraphics way of manipulating custom graphics.
	 *             For details, see deprecation note under 
	 *  	       {@link ding.view.DNodeView#addCustomGraphic(Shape,Paint,int) addCustomGraphic(Shape,Paint,int)}.
	 *             Once switched, then
	 *             use {@link cytoscape.render.stateful.CustomGraphic#getPaint() cytoscape.render.stateful.CustomGraphic.getPaint()}.
	 */
	@Deprecated public Paint graphicPaint(int node, int inx) {
		final DNodeView nv = (DNodeView) m_view.getNodeView(~node);

		return nv.getCustomGraphicPaint(inx);
	}

    // overrides NodeDetails.customGraphicCount():
    public int customGraphicCount(final int node) {
	final DNodeView dnv = (DNodeView) m_view.getNodeView(~node);	
	return dnv.getNumCustomGraphics();
    }

    // overrides NodeDetails.customGraphics():
    public Iterator<CustomGraphic> customGraphics (final int node) {
	final DNodeView dnv = (DNodeView) m_view.getNodeView(~node);
	return dnv.customGraphicIterator();
    }
    // overrides NodeDetails.customGraphicLock():
    public Object customGraphicLock (final int node) {
	final DNodeView dnv = (DNodeView) m_view.getNodeView(~node);
	return dnv.customGraphicLock();	
    }

	// label positioning
	/**
	 *  DOCUMENT ME!
	 *
	 * @param node DOCUMENT ME!
	 * @param labelInx DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public byte labelTextAnchor(final int node, final int labelInx) {
		final Object o = m_labelTextAnchors.get(new Integer(node));

		if (o == null)
			return super.labelTextAnchor(node, labelInx);

		return convertG2ND(((Integer) o).intValue());
	}

	void overrideLabelTextAnchor(final int node, final int inx, final int anchor) {
		if (convertG2ND(anchor) == super.labelTextAnchor(node, inx))
			m_labelTextAnchors.remove(new Integer(node));
		else
			m_labelTextAnchors.put(new Integer(node), new Integer(anchor));
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param node DOCUMENT ME!
	 * @param labelInx DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public byte labelNodeAnchor(final int node, final int labelInx) {
		final Object o = m_labelNodeAnchors.get(new Integer(node));

		if (o == null)
			return super.labelNodeAnchor(node, labelInx);

		return convertG2ND(((Integer) o).intValue());
	}

	void overrideLabelNodeAnchor(final int node, final int inx, final int anchor) {
		if (convertG2ND(anchor) == super.labelNodeAnchor(node, inx))
			m_labelNodeAnchors.remove(new Integer(node));
		else
			m_labelNodeAnchors.put(new Integer(node), new Integer(anchor));
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param node DOCUMENT ME!
	 * @param labelInx DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public float labelOffsetVectorX(final int node, final int labelInx) {
		final Object o = m_labelOffsetXs.get(new Integer(node));

		if (o == null)
			return super.labelOffsetVectorX(node, labelInx);

		return ((Double) o).floatValue();
	}

	void overrideLabelOffsetVectorX(final int node, final int inx, final double x) {
		if (((float) x) == super.labelOffsetVectorX(node, inx))
			m_labelOffsetXs.remove(new Integer(node));
		else
			m_labelOffsetXs.put(new Integer(node), new Double(x));
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param node DOCUMENT ME!
	 * @param labelInx DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public float labelOffsetVectorY(final int node, final int labelInx) {
		final Object o = m_labelOffsetYs.get(new Integer(node));

		if (o == null)
			return super.labelOffsetVectorY(node, labelInx);

		return ((Double) o).floatValue();
	}

	void overrideLabelOffsetVectorY(final int node, final int inx, final double y) {
		if (((float) y) == super.labelOffsetVectorY(node, inx))
			m_labelOffsetYs.remove(new Integer(node));
		else
			m_labelOffsetYs.put(new Integer(node), new Double(y));
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param node DOCUMENT ME!
	 * @param labelInx DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public byte labelJustify(final int node, final int labelInx) {
		final Object o = m_labelJustifys.get(new Integer(node));

		if (o == null)
			return super.labelJustify(node, labelInx);

		return convertG2ND(((Integer) o).intValue());
	}

	void overrideLabelJustify(final int node, final int inx, final int justify) {
		if (convertG2ND(justify) == super.labelJustify(node, inx))
			m_labelJustifys.remove(new Integer(node));
		else
			m_labelJustifys.put(new Integer(node), new Integer(justify));
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param node DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public double labelWidth(int node) {
		final Object o = m_labelWidths.get(new Integer(node));

		if (o == null)
			return super.labelWidth(node);

		return ((Double) o).doubleValue();
	}
	
	
	@Override
	public TexturePaint getNestedNetworkTexturePaint(final int node) {
		final DNodeView dNodeView = (DNodeView) m_view.getNodeView(~node);
		final GraphPerspective nestedNetwork = dNodeView.getNode().getNestedNetwork();
		if (nestedNetwork == null) {
			return null;
		}
		
		return dNodeView.getNestedNetworkTexturePaint();
	}

	/*
	 * A negative width value has the special meaning to remove overridden width.
	 */
	void overrideLabelWidth(int node, double width) {
		if ((width < 0.0) || (width == super.labelWidth(node)))
			m_labelWidths.remove(new Integer(node));
		else
			m_labelWidths.put(new Integer(node), new Double(width));
	}

	static byte convertG2ND(int giny) {
		switch (giny) {
			case (Label.NORTH):
				return NodeDetails.ANCHOR_NORTH;

			case (Label.SOUTH):
				return NodeDetails.ANCHOR_SOUTH;

			case (Label.EAST):
				return NodeDetails.ANCHOR_EAST;

			case (Label.WEST):
				return NodeDetails.ANCHOR_WEST;

			case (Label.NORTHEAST):
				return NodeDetails.ANCHOR_NORTHEAST;

			case (Label.NORTHWEST):
				return NodeDetails.ANCHOR_NORTHWEST;

			case (Label.SOUTHEAST):
				return NodeDetails.ANCHOR_SOUTHEAST;

			case (Label.SOUTHWEST):
				return NodeDetails.ANCHOR_SOUTHWEST;

			case (Label.CENTER):
				return NodeDetails.ANCHOR_CENTER;

			case (Label.JUSTIFY_CENTER):
				return NodeDetails.LABEL_WRAP_JUSTIFY_CENTER;

			case (Label.JUSTIFY_RIGHT):
				return NodeDetails.LABEL_WRAP_JUSTIFY_RIGHT;

			case (Label.JUSTIFY_LEFT):
				return NodeDetails.LABEL_WRAP_JUSTIFY_LEFT;

			default:
				return -1;
		}
	}

	static int convertND2G(byte nd) {
		switch (nd) {
			case (NodeDetails.ANCHOR_NORTH):
				return Label.NORTH;

			case (NodeDetails.ANCHOR_SOUTH):
				return Label.SOUTH;

			case (NodeDetails.ANCHOR_EAST):
				return Label.EAST;

			case (NodeDetails.ANCHOR_WEST):
				return Label.WEST;

			case (NodeDetails.ANCHOR_NORTHEAST):
				return Label.NORTHEAST;

			case (NodeDetails.ANCHOR_NORTHWEST):
				return Label.NORTHWEST;

			case (NodeDetails.ANCHOR_SOUTHEAST):
				return Label.SOUTHEAST;

			case (NodeDetails.ANCHOR_SOUTHWEST):
				return Label.SOUTHWEST;

			case (NodeDetails.ANCHOR_CENTER):
				return Label.CENTER;

			case (NodeDetails.LABEL_WRAP_JUSTIFY_CENTER):
				return Label.JUSTIFY_CENTER;

			case (NodeDetails.LABEL_WRAP_JUSTIFY_RIGHT):
				return Label.JUSTIFY_RIGHT;

			case (NodeDetails.LABEL_WRAP_JUSTIFY_LEFT):
				return Label.JUSTIFY_LEFT;

			default:
				return -1;
		}
	}
}
