
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

import cytoscape.graph.fixed.FixedGraph;

import cytoscape.render.immed.EdgeAnchors;

import cytoscape.util.intr.IntEnumerator;
import cytoscape.util.intr.IntIterator;
import cytoscape.util.intr.IntObjHash;
import cytoscape.util.intr.MinIntHeap;

import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;

import java.util.HashMap;


class DEdgeDetails extends IntermediateEdgeDetails {
	final DGraphView m_view;
	final IntObjHash m_colorsLowDetail = new IntObjHash();
	final Object m_deletedEntry = new Object();
	final HashMap<Integer,Object> m_segmentThicknesses = new HashMap<Integer,Object>();
	final HashMap<Integer,Object> m_sourceArrows = new HashMap<Integer,Object>();
	final HashMap<Integer,Object> m_sourceArrowPaints = new HashMap<Integer,Object>();
	final HashMap<Integer,Object> m_targetArrows = new HashMap<Integer,Object>();
	final HashMap<Integer,Object> m_targetArrowPaints = new HashMap<Integer,Object>();
	final HashMap<Integer,Object> m_segmentPaints = new HashMap<Integer,Object>();
	final HashMap<Integer,Object> m_segmentDashLengths = new HashMap<Integer,Object>();
	final HashMap<Integer,Object> m_labelCounts = new HashMap<Integer,Object>();
	final HashMap<Long,String> m_labelTexts = new HashMap<Long,String>();
	final HashMap<Long,Font> m_labelFonts = new HashMap<Long,Font>();
	final HashMap<Long,Paint> m_labelPaints = new HashMap<Long,Paint>();

	DEdgeDetails(DGraphView view) {
		m_view = view;
	}

	void unregisterEdge(int edge) {
		final Object o = m_colorsLowDetail.get(edge);

		if ((o != null) && (o != m_deletedEntry))
			m_colorsLowDetail.put(edge, m_deletedEntry);

		final Integer key = new Integer(edge);
		m_segmentThicknesses.remove(key);
		m_sourceArrows.remove(key);
		m_sourceArrowPaints.remove(key);
		m_targetArrows.remove(key);
		m_targetArrowPaints.remove(key);
		m_segmentPaints.remove(key);
		m_segmentDashLengths.remove(key);
		m_labelCounts.remove(key);
		m_labelTexts.remove(key);
		m_labelFonts.remove(key);
		m_labelPaints.remove(key);
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param edge DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public Color colorLowDetail(int edge) {
		final Object o = m_colorsLowDetail.get(edge);

		if ((o == null) || (o == m_deletedEntry))
			return super.colorLowDetail(edge);

		return (Color) o;
	}

	/*
	 * A null color has the special meaning to remove overridden color.
	 */
	void overrideColorLowDetail(int edge, Color color) {
		if ((color == null) || color.equals(super.colorLowDetail(edge))) {
			final Object val = m_colorsLowDetail.get(edge);

			if ((val != null) && (val != m_deletedEntry))
				m_colorsLowDetail.put(edge, m_deletedEntry);
		} else
			m_colorsLowDetail.put(edge, color);
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param edge DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public byte sourceArrow(int edge) {
		final Object o = m_sourceArrows.get(new Integer(edge));

		if (o == null)
			return super.sourceArrow(edge);

		return ((Byte) o).byteValue();
	}

	/*
	 * A non-negative arrowType has the special meaning to remove overridden
	 * arrow.
	 */
	void overrideSourceArrow(int edge, byte arrowType) {
		if ((arrowType >= 0) || (arrowType == super.sourceArrow(edge)))
			m_sourceArrows.remove(new Integer(edge));
		else
			m_sourceArrows.put(new Integer(edge), new Byte(arrowType));
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param edge DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public Paint sourceArrowPaint(int edge) {
		final Object o = m_sourceArrowPaints.get(new Integer(edge));

		if (o == null)
			return super.sourceArrowPaint(edge);

		return (Paint) o;
	}

	/*
	 * A null paint has the special meaning to remove overridden paint.
	 */
	void overrideSourceArrowPaint(int edge, Paint paint) {
		if ((paint == null) || paint.equals(super.sourceArrowPaint(edge)))
			m_sourceArrowPaints.remove(new Integer(edge));
		else
			m_sourceArrowPaints.put(new Integer(edge), paint);
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param edge DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public byte targetArrow(int edge) {
		final Object o = m_targetArrows.get(new Integer(edge));

		if (o == null)
			return super.targetArrow(edge);

		return ((Byte) o).byteValue();
	}

	/*
	 * A non-negative arrowType has the special meaning to remove overridden
	 * arrow.
	 */
	void overrideTargetArrow(int edge, byte arrowType) {
		if ((arrowType >= 0) || (arrowType == super.targetArrow(edge)))
			m_targetArrows.remove(new Integer(edge));
		else
			m_targetArrows.put(new Integer(edge), new Byte(arrowType));
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param edge DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public Paint targetArrowPaint(int edge) {
		final Object o = m_targetArrowPaints.get(new Integer(edge));

		if (o == null)
			return super.targetArrowPaint(edge);

		return (Paint) o;
	}

	/*
	 * A null paint has the special meaning to remove overridden paint.
	 */
	void overrideTargetArrowPaint(int edge, Paint paint) {
		if ((paint == null) || paint.equals(super.targetArrowPaint(edge)))
			m_targetArrowPaints.remove(new Integer(edge));
		else
			m_targetArrowPaints.put(new Integer(edge), paint);
	}

	private final MinIntHeap m_heap = new MinIntHeap();
	private final float[] m_extentsBuff = new float[4];

	/**
	 * DOCUMENT ME!
	 *
	 * @param edge DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public EdgeAnchors anchors(int edge) {
		final EdgeAnchors returnThis = (EdgeAnchors) (m_view.getEdgeView(~edge));

		if (returnThis.numAnchors() > 0)
			return returnThis;

		final FixedGraph graph = (FixedGraph) m_view.m_drawPersp;

		if (graph.edgeSource(edge) == graph.edgeTarget(edge)) { // Self-edge.

			final int node = graph.edgeSource(edge);
			m_view.m_spacial.exists(node, m_extentsBuff, 0);

			final double w = ((double) m_extentsBuff[2]) - m_extentsBuff[0];
			final double h = ((double) m_extentsBuff[3]) - m_extentsBuff[1];
			final double x = (((double) m_extentsBuff[0]) + m_extentsBuff[2]) / 2.0d;
			final double y = (((double) m_extentsBuff[1]) + m_extentsBuff[3]) / 2.0d;
			final double nodeSize = Math.max(w, h);
			int i = 0;
			final IntIterator selfEdges = graph.edgesConnecting(node, node, true, true, true);

			while (selfEdges.hasNext()) {
				final int e2 = selfEdges.nextInt();

				if (e2 == edge)
					break;

				if (((EdgeAnchors) m_view.getEdgeView(~e2)).numAnchors() == 0)
					i++;
			}

			final int inx = i;

			return new EdgeAnchors() {
					public int numAnchors() {
						return 2;
					}

					public void getAnchor(int anchorInx, float[] anchorArr, int offset) {
						if (anchorInx == 0) {
							anchorArr[offset] = (float) (x - (((inx + 3) * nodeSize) / 2.0d));
							anchorArr[offset + 1] = (float) y;
						} else if (anchorInx == 1) {
							anchorArr[offset] = (float) x;
							anchorArr[offset + 1] = (float) (y - (((inx + 3) * nodeSize) / 2.0d));
						}
					}
				};
		}

		while (true) {
			{
				final IntIterator otherEdges = graph.edgesConnecting(graph.edgeSource(edge),
				                                                     graph.edgeTarget(edge), true,
				                                                     true, true);
				m_heap.empty();

				while (otherEdges.hasNext())
					m_heap.toss(otherEdges.nextInt());
			}

			final IntEnumerator otherEdges = m_heap.orderedElements(false);
			int otherEdge = otherEdges.nextInt();

			if (otherEdge == edge)
				break;

			int i = (((EdgeAnchors) m_view.getEdgeView(~otherEdge)).numAnchors() == 0) ? 1 : 0;

			while (true) {
				if (edge == (otherEdge = otherEdges.nextInt()))
					break;

				if (((EdgeAnchors) m_view.getEdgeView(~otherEdge)).numAnchors() == 0)
					i++;
			}

			final int inx = i;
			m_view.m_spacial.exists(graph.edgeSource(edge), m_extentsBuff, 0);

			final double srcW = ((double) m_extentsBuff[2]) - m_extentsBuff[0];
			final double srcH = ((double) m_extentsBuff[3]) - m_extentsBuff[1];
			final double srcX = (((double) m_extentsBuff[0]) + m_extentsBuff[2]) / 2.0d;
			final double srcY = (((double) m_extentsBuff[1]) + m_extentsBuff[3]) / 2.0d;
			m_view.m_spacial.exists(graph.edgeTarget(edge), m_extentsBuff, 0);

			final double trgW = ((double) m_extentsBuff[2]) - m_extentsBuff[0];
			final double trgH = ((double) m_extentsBuff[3]) - m_extentsBuff[1];
			final double trgX = (((double) m_extentsBuff[0]) + m_extentsBuff[2]) / 2.0d;
			final double trgY = (((double) m_extentsBuff[1]) + m_extentsBuff[3]) / 2.0d;
			final double nodeSize = Math.max(Math.max(Math.max(srcW, srcH), trgW), trgH);
			final double midX = (srcX + trgX) / 2;
			final double midY = (srcY + trgY) / 2;
			final double dx = trgX - srcX;
			final double dy = trgY - srcY;
			final double len = Math.sqrt((dx * dx) + (dy * dy));

			if (((float) len) == 0.0f)
				break;

			final double normX = Math.abs(dx / len);
			final double normY = Math.abs(dy / len);
			final double factor = ((inx + 1) / 2) * (((inx % 2) == 0) ? 1 : (-1)) * nodeSize;
			final double anchorX = midX + (factor * normY);
			final double anchorY = midY - (factor * normX);

			return new EdgeAnchors() {
					public int numAnchors() {
						return 1;
					}

					public void getAnchor(int inx, float[] arr, int off) {
						arr[off] = (float) anchorX;
						arr[off + 1] = (float) anchorY;
					}
				};
		}

		return returnThis;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param edge DOCUMENT ME!
	 * @param anchorInx DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public float anchorSize(int edge, int anchorInx) {
		if (m_view.getEdgeView(~edge).isSelected()
		    && (((DEdgeView) m_view.getEdgeView(~edge)).numAnchors() > 0))
			return m_view.getAnchorSize();
		else

			return 0.0f;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param edge DOCUMENT ME!
	 * @param anchorInx DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public Paint anchorPaint(int edge, int anchorInx) {
		if (((DEdgeView) (m_view.getEdgeView(~edge))).m_lineType == DEdgeView.STRAIGHT_LINES)
			anchorInx = anchorInx / 2;

		if (m_view.m_selectedAnchors.count((edge << 6) | anchorInx) > 0)
			return m_view.getAnchorSelectedPaint();
		else

			return m_view.getAnchorUnselectedPaint();
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param edge DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public float segmentThickness(int edge) {
		final Object o = m_segmentThicknesses.get(new Integer(edge));

		if (o == null)
			return super.segmentThickness(edge);

		return ((Float) o).floatValue();
	}

	/*
	 * A negative thickness value has the special meaning to remove overridden
	 * thickness.
	 */
	void overrideSegmentThickness(int edge, float thickness) {
		if ((thickness < 0.0f) || (thickness == super.segmentThickness(edge)))
			m_segmentThicknesses.remove(new Integer(edge));
		else
			m_segmentThicknesses.put(new Integer(edge), new Float(thickness));
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param edge DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public Paint segmentPaint(int edge) {
		final Object o = m_segmentPaints.get(new Integer(edge));

		if (o == null)
			return super.segmentPaint(edge);

		return (Paint) o;
	}

	/*
	 * A null paint has the special meaning to remove overridden paint.
	 */
	void overrideSegmentPaint(int edge, Paint paint) {
		if ((paint == null) || paint.equals(super.segmentPaint(edge)))
			m_segmentPaints.remove(new Integer(edge));
		else
			m_segmentPaints.put(new Integer(edge), paint);
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param edge DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public float segmentDashLength(int edge) {
		final Object o = m_segmentDashLengths.get(new Integer(edge));

		if (o == null)
			return super.segmentDashLength(edge);

		return ((Float) o).floatValue();
	}

	/*
	 * A negative length value has the special meaning to remove overridden
	 * length.
	 */
	void overrideSegmentDashLength(int edge, float length) {
		if ((length < 0.0f) || (length == super.segmentDashLength(edge)))
			m_segmentDashLengths.remove(new Integer(edge));
		else
			m_segmentDashLengths.put(new Integer(edge), new Float(length));
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param edge DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public int labelCount(int edge) {
		final Object o = m_labelCounts.get(new Integer(edge));

		if (o == null)
			return super.labelCount(edge);

		return ((Integer) o).intValue();
	}

	/*
	 * A negative labelCount has the special meaning to remove overridden count.
	 */
	void overrideLabelCount(int edge, int labelCount) {
		if ((labelCount < 0) || (labelCount == super.labelCount(edge)))
			m_labelCounts.remove(new Integer(edge));
		else
			m_labelCounts.put(new Integer(edge), new Integer(labelCount));
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param edge DOCUMENT ME!
	 * @param labelInx DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public String labelText(int edge, int labelInx) {
		final long key = (((long) edge) << 32) | ((long) labelInx);
		final String o = m_labelTexts.get(new Long(key));

		if (o == null)
			return super.labelText(edge, labelInx);

		return o;
	}

	/*
	 * A null text has the special meaning to remove overridden text.
	 */
	void overrideLabelText(int edge, int labelInx, String text) {
		final long key = (((long) edge) << 32) | ((long) labelInx);

		if ((text == null) || text.equals(super.labelText(edge, labelInx)))
			m_labelTexts.remove(new Long(key));
		else
			m_labelTexts.put(new Long(key), text);
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param edge DOCUMENT ME!
	 * @param labelInx DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public Font labelFont(int edge, int labelInx) {
		final long key = (((long) edge) << 32) | ((long) labelInx);
		final Font o = m_labelFonts.get(new Long(key));

		if (o == null)
			return super.labelFont(edge, labelInx);

		return o;
	}

	/*
	 * A null font has the special meaning to remove overridden font.
	 */
	void overrideLabelFont(int edge, int labelInx, Font font) {
		final long key = (((long) edge) << 32) | ((long) labelInx);

		if ((font == null) || font.equals(super.labelFont(edge, labelInx)))
			m_labelFonts.remove(new Long(key));
		else
			m_labelFonts.put(new Long(key), font);
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param edge DOCUMENT ME!
	 * @param labelInx DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public Paint labelPaint(int edge, int labelInx) {
		final long key = (((long) edge) << 32) | ((long) labelInx);
		final Paint o = m_labelPaints.get(new Long(key));

		if (o == null)
			return super.labelPaint(edge, labelInx);

		return o;
	}

	/*
	 * A null paint has the special meaning to remove overridden paint.
	 */
	void overrideLabelPaint(int edge, int labelInx, Paint paint) {
		final long key = (((long) edge) << 32) | ((long) labelInx);

		if ((paint == null) || paint.equals(super.labelPaint(edge, labelInx)))
			m_labelPaints.remove(new Long(key));
		else
			m_labelPaints.put(new Long(key), paint);
	}

	/**
	 * The arrow size will scale with the edge width.
	 */
	public float sourceArrowSize(int edge) {
		return (segmentThickness(edge) + DEdgeView.DEFAULT_ARROW_SIZE);
	}

	/**
	 * The arrow size will scale with the edge width.
	 */
	public float targetArrowSize(int edge) {
		return (segmentThickness(edge) + DEdgeView.DEFAULT_ARROW_SIZE);
	}
}
