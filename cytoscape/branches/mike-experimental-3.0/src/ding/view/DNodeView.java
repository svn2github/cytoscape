
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

import cytoscape.render.immed.GraphGraphics;

import cytoscape.GraphPerspective;
import cytoscape.Node;

import giny.view.GraphView;
import giny.view.GraphViewChangeListener;
import giny.view.Label;
import giny.view.NodeView;
import giny.view.EdgeView;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.TexturePaint;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import java.util.ArrayList;
import java.util.List;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 */
public class DNodeView implements NodeView, Label {
	static final float DEFAULT_WIDTH = 20.0f;
	static final float DEFAULT_HEIGHT = 20.0f;
	static final byte DEFAULT_SHAPE = GraphGraphics.SHAPE_ELLIPSE;
	static final Paint DEFAULT_BORDER_PAINT = Color.black;
	static final String DEFAULT_LABEL_TEXT = "";
	static final Font DEFAULT_LABEL_FONT = new Font(null, Font.PLAIN, 1);
	static final Paint DEFAULT_LABEL_PAINT = Color.black;
	DGraphView m_view;
	final int m_inx; // The FixedGraph index (non-negative).
	boolean m_selected;
	Paint m_unselectedPaint;
	Paint m_selectedPaint;
	Paint m_borderPaint;

	/**
	 * Stores the position of a nodeView when it's hidden so that when the 
	 * nodeView is retored we can restore the view into the same position.
	 */
	float m_hiddenXMin;
	float m_hiddenYMin;
	float m_hiddenXMax;
	float m_hiddenYMax;

	ArrayList<Shape> m_graphicShapes;
	ArrayList<Paint> m_graphicPaints;

	// AJK: 04/26/06 for tooltip
	String m_toolTipText = null;

	/*
	 * @param inx the RootGraph index of node (a negative number).
	 */
	DNodeView(DGraphView view, int inx) {
		m_view = view;
		m_inx = ~inx;
		m_selected = false;
		m_unselectedPaint = m_view.m_nodeDetails.fillPaint(m_inx);
		m_selectedPaint = Color.yellow;
		m_borderPaint = m_view.m_nodeDetails.borderPaint(m_inx);
		m_graphicShapes = null;
		m_graphicPaints = null;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public GraphView getGraphView() {
		return m_view;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public Node getNode() {
		synchronized (m_view.m_lock) {
			return m_view.m_structPersp.getNode(~m_inx);
		}
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public int getGraphPerspectiveIndex() {
		return ~m_inx;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public int getRootGraphIndex() {
		return ~m_inx;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param otherNodeView DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public List<EdgeView> getEdgeViewsList(NodeView otherNodeView) {
		synchronized (m_view.m_lock) {
			return m_view.getEdgeViewsList(getNode(), otherNodeView.getNode());
		}
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public int getShape() {
		synchronized (m_view.m_lock) {
			final byte nativeShape = m_view.m_nodeDetails.shape(m_inx);

			return GinyUtil.getGinyNodeType(nativeShape);
		}
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param paint DOCUMENT ME!
	 */
	public void setSelectedPaint(Paint paint) {
		synchronized (m_view.m_lock) {
			if (paint == null)
				throw new NullPointerException("paint is null");

			m_selectedPaint = paint;

			if (isSelected()) {
				m_view.m_nodeDetails.overrideFillPaint(m_inx, m_selectedPaint);

				if (m_selectedPaint instanceof Color)
					m_view.m_nodeDetails.overrideColorLowDetail(m_inx, (Color) m_selectedPaint);

				m_view.m_contentChanged = true;
			}
		}
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public Paint getSelectedPaint() {
		return m_selectedPaint;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param paint DOCUMENT ME!
	 */
	public void setUnselectedPaint(Paint paint) {
		synchronized (m_view.m_lock) {
			if (paint == null)
				throw new NullPointerException("paint is null");

			m_unselectedPaint = paint;

			if (!isSelected()) {
				m_view.m_nodeDetails.overrideFillPaint(m_inx, m_unselectedPaint);

				if (m_unselectedPaint instanceof Color)
					m_view.m_nodeDetails.overrideColorLowDetail(m_inx, (Color) m_unselectedPaint);

				m_view.m_contentChanged = true;
			}
		}
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public Paint getUnselectedPaint() {
		return m_unselectedPaint;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param paint DOCUMENT ME!
	 */
	public void setBorderPaint(Paint paint) {
		synchronized (m_view.m_lock) {
			m_borderPaint = paint;
			fixBorder();
			m_view.m_contentChanged = true;
		}
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public Paint getBorderPaint() {
		return m_borderPaint;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param width DOCUMENT ME!
	 */
	public void setBorderWidth(float width) {
		synchronized (m_view.m_lock) {
			m_view.m_nodeDetails.overrideBorderWidth(m_inx, width);
			m_view.m_contentChanged = true;
		}
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public float getBorderWidth() {
		synchronized (m_view.m_lock) {
			return m_view.m_nodeDetails.borderWidth(m_inx);
		}
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param stroke DOCUMENT ME!
	 */
	public void setBorder(Stroke stroke) {
		if (stroke instanceof BasicStroke) {
			synchronized (m_view.m_lock) {
				setBorderWidth(((BasicStroke) stroke).getLineWidth());

				final float[] dashArray = ((BasicStroke) stroke).getDashArray();

				if ((dashArray != null) && (dashArray.length > 1)) {
					m_borderDash = dashArray[0];
					m_borderDash2 = dashArray[1];
				} else {
					m_borderDash = 0.0f;
					m_borderDash2 = 0.0f;
				}

				fixBorder();
			}
		}
	}

	private float m_borderDash = 0.0f;
	private float m_borderDash2 = 0.0f;
	private final static Color s_transparent = new Color(0, 0, 0, 0);

	// Callers of this method must be holding m_view.m_lock.
	private void fixBorder() {
		if ((m_borderDash == 0.0f) && (m_borderDash2 == 0.0f))
			m_view.m_nodeDetails.overrideBorderPaint(m_inx, m_borderPaint);
		else {
			final int size = (int) Math.max(1.0f, (int) (m_borderDash + m_borderDash2)); // Average times two.

			if ((size == m_view.m_lastSize) && (m_borderPaint == m_view.m_lastPaint)) {
				/* Use the cached texture paint. */ } else {
				final BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
				final Graphics2D g2 = (Graphics2D) img.getGraphics();
				g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC));
				g2.setPaint(s_transparent);
				g2.fillRect(0, 0, size, size);
				g2.setPaint(m_borderPaint);
				g2.fillRect(0, 0, size / 2, size / 2);
				g2.fillRect(size / 2, size / 2, size / 2, size / 2);
				m_view.m_lastTexturePaint = new TexturePaint(img,
				                                             new Rectangle2D.Double(0, 0, size, size));
				m_view.m_lastSize = size;
				m_view.m_lastPaint = m_borderPaint;
			}

			m_view.m_nodeDetails.overrideBorderPaint(m_inx, m_view.m_lastTexturePaint);
		}
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public Stroke getBorder() {
		synchronized (m_view.m_lock) {
			if ((m_borderDash == 0.0f) && (m_borderDash2 == 0.0f))
				return new BasicStroke(getBorderWidth());
			else

				return new BasicStroke(getBorderWidth(), BasicStroke.CAP_SQUARE,
				                       BasicStroke.JOIN_MITER, 10.0f,
				                       new float[] { m_borderDash, m_borderDash2 }, 0.0f);
		}
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param trans DOCUMENT ME!
	 */
	public void setTransparency(float trans) {
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public float getTransparency() {
		return 1.0f;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param width DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public boolean setWidth(double width) {
		synchronized (m_view.m_lock) {
			if (!m_view.m_spacial.exists(m_inx, m_view.m_extentsBuff, 0))
				return false;

			final double xCenter = (((double) m_view.m_extentsBuff[0]) + m_view.m_extentsBuff[2]) / 2.0d;
			final double wDiv2 = width / 2.0d;
			final float xMin = (float) (xCenter - wDiv2);
			final float xMax = (float) (xCenter + wDiv2);

			if (!(xMax > xMin))
				throw new IllegalArgumentException("width is too small");

			m_view.m_spacial.delete(m_inx);
			m_view.m_spacial.insert(m_inx, xMin, m_view.m_extentsBuff[1], xMax,
			                        m_view.m_extentsBuff[3]);

			final double w = ((double) xMax) - xMin;
			final double h = ((double) m_view.m_extentsBuff[3]) - m_view.m_extentsBuff[1];

			if (!(Math.max(w, h) < (1.99d * Math.min(w, h)))
			    && (getShape() == NodeView.ROUNDED_RECTANGLE))
				setShape(NodeView.RECTANGLE);

			m_view.m_contentChanged = true;

			return true;
		}
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public double getWidth() {
		synchronized (m_view.m_lock) {
			if (!m_view.m_spacial.exists(m_inx, m_view.m_extentsBuff, 0))
				return -1.0d;

			return ((double) m_view.m_extentsBuff[2]) - m_view.m_extentsBuff[0];
		}
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param height DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public boolean setHeight(double height) {
		synchronized (m_view.m_lock) {
			if (!m_view.m_spacial.exists(m_inx, m_view.m_extentsBuff, 0))
				return false;

			final double yCenter = (((double) m_view.m_extentsBuff[1]) + m_view.m_extentsBuff[3]) / 2.0d;
			final double hDiv2 = height / 2.0d;
			final float yMin = (float) (yCenter - hDiv2);
			final float yMax = (float) (yCenter + hDiv2);

			if (!(yMax > yMin))
				throw new IllegalArgumentException("height is too small max:" + yMax + " min:"
				                                   + yMin + " center:" + yCenter + " height:"
				                                   + height);

			m_view.m_spacial.delete(m_inx);
			m_view.m_spacial.insert(m_inx, m_view.m_extentsBuff[0], yMin, m_view.m_extentsBuff[2],
			                        yMax);

			final double w = ((double) m_view.m_extentsBuff[2]) - m_view.m_extentsBuff[0];
			final double h = ((double) yMax) - yMin;

			if (!(Math.max(w, h) < (1.99d * Math.min(w, h)))
			    && (getShape() == NodeView.ROUNDED_RECTANGLE))
				setShape(NodeView.RECTANGLE);

			m_view.m_contentChanged = true;

			return true;
		}
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public double getHeight() {
		synchronized (m_view.m_lock) {
			if (!m_view.m_spacial.exists(m_inx, m_view.m_extentsBuff, 0))
				return -1.0d;

			return ((double) m_view.m_extentsBuff[3]) - m_view.m_extentsBuff[1];
		}
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public Label getLabel() {
		return this;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public int getDegree() {
		// This method is totally ridiculous.
		return m_view.getGraphPerspective().getDegree(~m_inx);
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param x DOCUMENT ME!
	 * @param y DOCUMENT ME!
	 */
	public void setOffset(double x, double y) {
		synchronized (m_view.m_lock) {
			if (!m_view.m_spacial.exists(m_inx, m_view.m_extentsBuff, 0))
				return;

			final double wDiv2 = (((double) m_view.m_extentsBuff[2]) - m_view.m_extentsBuff[0]) / 2.0d;
			final double hDiv2 = (((double) m_view.m_extentsBuff[3]) - m_view.m_extentsBuff[1]) / 2.0d;
			final float xMin = (float) (x - wDiv2);
			final float xMax = (float) (x + wDiv2);
			final float yMin = (float) (y - hDiv2);
			final float yMax = (float) (y + hDiv2);

			if (!(xMax > xMin))
				throw new IllegalStateException("width of node has degenerated to zero after "
				                                + "rounding");

			if (!(yMax > yMin))
				throw new IllegalStateException("height of node has degenerated to zero after "
				                                + "rounding");

			m_view.m_spacial.delete(m_inx);
			m_view.m_spacial.insert(m_inx, xMin, yMin, xMax, yMax);
			m_view.m_contentChanged = true;
		}
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public Point2D getOffset() {
		synchronized (m_view.m_lock) {
			if (!m_view.m_spacial.exists(m_inx, m_view.m_extentsBuff, 0))
				return null;

			final double xCenter = (((double) m_view.m_extentsBuff[0]) + m_view.m_extentsBuff[2]) / 2.0d;
			final double yCenter = (((double) m_view.m_extentsBuff[1]) + m_view.m_extentsBuff[3]) / 2.0d;

			return new Point2D.Double(xCenter, yCenter);
		}
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param xPos DOCUMENT ME!
	 */
	public void setXPosition(double xPos) {
		synchronized (m_view.m_lock) {
			if (!m_view.m_spacial.exists(m_inx, m_view.m_extentsBuff, 0))
				return;

			final double wDiv2 = (((double) m_view.m_extentsBuff[2]) - m_view.m_extentsBuff[0]) / 2.0d;
			final float xMin = (float) (xPos - wDiv2);
			final float xMax = (float) (xPos + wDiv2);

			if (!(xMax > xMin))
				throw new IllegalStateException("width of node has degenerated to zero after "
				                                + "rounding");

			m_view.m_spacial.delete(m_inx);
			m_view.m_spacial.insert(m_inx, xMin, m_view.m_extentsBuff[1], xMax,
			                        m_view.m_extentsBuff[3]);
			m_view.m_contentChanged = true;
		}
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param xPos DOCUMENT ME!
	 * @param update DOCUMENT ME!
	 */
	public void setXPosition(double xPos, boolean update) {
		setXPosition(xPos);
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public double getXPosition() {
		synchronized (m_view.m_lock) {
			if (!m_view.m_spacial.exists(m_inx, m_view.m_extentsBuff, 0))
				return Double.NaN;

			return (((double) m_view.m_extentsBuff[0]) + m_view.m_extentsBuff[2]) / 2.0d;
		}
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param yPos DOCUMENT ME!
	 */
	public void setYPosition(double yPos) {
		synchronized (m_view.m_lock) {
			if (!m_view.m_spacial.exists(m_inx, m_view.m_extentsBuff, 0))
				return;

			final double hDiv2 = (((double) m_view.m_extentsBuff[3]) - m_view.m_extentsBuff[1]) / 2.0d;
			final float yMin = (float) (yPos - hDiv2);
			final float yMax = (float) (yPos + hDiv2);

			if (!(yMax > yMin))
				throw new IllegalStateException("height of node has degenerated to zero after "
				                                + "rounding");

			m_view.m_spacial.delete(m_inx);
			m_view.m_spacial.insert(m_inx, m_view.m_extentsBuff[0], yMin, m_view.m_extentsBuff[2],
			                        yMax);
			m_view.m_contentChanged = true;
		}
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param yPos DOCUMENT ME!
	 * @param update DOCUMENT ME!
	 */
	public void setYPosition(double yPos, boolean update) {
		setYPosition(yPos);
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public double getYPosition() {
		synchronized (m_view.m_lock) {
			if (!m_view.m_spacial.exists(m_inx, m_view.m_extentsBuff, 0))
				return Double.NaN;

			return (((double) m_view.m_extentsBuff[1]) + m_view.m_extentsBuff[3]) / 2.0d;
		}
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param animate DOCUMENT ME!
	 */
	public void setNodePosition(boolean animate) {
	}

	/**
	 * DOCUMENT ME!
	 */
	public void select() {
		final boolean somethingChanged;

		synchronized (m_view.m_lock) {
			somethingChanged = selectInternal();

			if (somethingChanged)
				m_view.m_contentChanged = true;
		}

		if (somethingChanged) {
			final GraphViewChangeListener listener = m_view.m_lis[0];

			if (listener != null)
				listener.graphViewChanged(new GraphViewNodesSelectedEvent(m_view,
				                                                          new int[] { ~m_inx }));
		}
	}

	// Should synchronize around m_view.m_lock.
	boolean selectInternal() {
		if (m_selected)
			return false;

		m_selected = true;
		m_view.m_nodeDetails.overrideFillPaint(m_inx, m_selectedPaint);

		if (m_selectedPaint instanceof Color)
			m_view.m_nodeDetails.overrideColorLowDetail(m_inx, (Color) m_selectedPaint);

		m_view.m_selectedNodes.insert(m_inx);

		return true;
	}

	/**
	 * DOCUMENT ME!
	 */
	public void unselect() {
		final boolean somethingChanged;

		synchronized (m_view.m_lock) {
			somethingChanged = unselectInternal();

			if (somethingChanged)
				m_view.m_contentChanged = true;
		}

		if (somethingChanged) {
			final GraphViewChangeListener listener = m_view.m_lis[0];

			if (listener != null)
				listener.graphViewChanged(new GraphViewNodesUnselectedEvent(m_view,
				                                                            new int[] { ~m_inx }));
		}
	}

	// Should synchronize around m_view.m_lock.
	boolean unselectInternal() {
		if (!m_selected)
			return false;

		m_selected = false;
		m_view.m_nodeDetails.overrideFillPaint(m_inx, m_unselectedPaint);

		if (m_unselectedPaint instanceof Color)
			m_view.m_nodeDetails.overrideColorLowDetail(m_inx, (Color) m_unselectedPaint);

		m_view.m_selectedNodes.delete(m_inx);

		return true;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public boolean isSelected() {
		return m_selected;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param selected DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public boolean setSelected(boolean selected) {
		if (selected)
			select();
		else
			unselect();

		return true;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param shape DOCUMENT ME!
	 */
	public void setShape(final int shape) {
		synchronized (m_view.m_lock) {
			byte nativeShape = GinyUtil.getNativeNodeType(shape);

			// special case
			if ( shape == NodeView.ROUNDED_RECTANGLE ) {
					final double width = getWidth();
					final double height = getHeight();

					if (!(Math.max(width, height) < (1.99d * Math.min(width, height))))
						nativeShape = GraphGraphics.SHAPE_RECTANGLE;
					else
						nativeShape = GraphGraphics.SHAPE_ROUNDED_RECTANGLE;
			}

			m_view.m_nodeDetails.overrideShape(m_inx, nativeShape);
			m_view.m_contentChanged = true;
		}
	}

	// AJK: 04/26/06 BEGIN
	/**
	 *  DOCUMENT ME!
	 *
	 * @param tip DOCUMENT ME!
	 */
	public void setToolTip(String tip) {
		m_toolTipText = tip;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public String getToolTip() {
		return m_toolTipText;
	}

	// AJK: 04/26/06 END
	/**
	 *  DOCUMENT ME!
	 *
	 * @param position DOCUMENT ME!
	 */
	public void setPositionHint(int position) {
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public Paint getTextPaint() {
		synchronized (m_view.m_lock) {
			return m_view.m_nodeDetails.labelPaint(m_inx, 0);
		}
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param textPaint DOCUMENT ME!
	 */
	public void setTextPaint(Paint textPaint) {
		synchronized (m_view.m_lock) {
			m_view.m_nodeDetails.overrideLabelPaint(m_inx, 0, textPaint);
			m_view.m_contentChanged = true;
		}
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public double getGreekThreshold() {
		return 0.0d;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param threshold DOCUMENT ME!
	 */
	public void setGreekThreshold(double threshold) {
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public String getText() {
		synchronized (m_view.m_lock) {
			return m_view.m_nodeDetails.labelText(m_inx, 0);
		}
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param text DOCUMENT ME!
	 */
	public void setText(String text) {
		synchronized (m_view.m_lock) {
			m_view.m_nodeDetails.overrideLabelText(m_inx, 0, text);

			if (DEFAULT_LABEL_TEXT.equals(m_view.m_nodeDetails.labelText(m_inx, 0)))
				m_view.m_nodeDetails.overrideLabelCount(m_inx, 0);
			else
				m_view.m_nodeDetails.overrideLabelCount(m_inx, 1);

			m_view.m_contentChanged = true;
		}
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public Font getFont() {
		synchronized (m_view.m_lock) {
			return m_view.m_nodeDetails.labelFont(m_inx, 0);
		}
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param font DOCUMENT ME!
	 */
	public void setFont(Font font) {
		synchronized (m_view.m_lock) {
			m_view.m_nodeDetails.overrideLabelFont(m_inx, 0, font);
			m_view.m_contentChanged = true;
		}
	}

	// Custom graphic stuff.

	/**
	 * Returns the number of custom graphic objects currently set on this
	 * node view.
	 */
	public int getCustomGraphicCount() {
		synchronized (m_view.m_lock) {
			if (m_graphicShapes == null)
				return 0;

			return m_graphicShapes.size();
		}
	}

	/**
	 * Returns the shape of the custom graphic object at specified index on
	 * this node view.  The index parameter must be in the range
	 * [0, getCustomGraphicCount()-1].
	 */
	public Shape getCustomGraphicShape(int index) {
		synchronized (m_view.m_lock) {
			return m_graphicShapes.get(index);
		}
	}

	/**
	 * Returns the paint on the custom graphic object at specified index on
	 * this node view.  The index parameter must be in the range
	 * [0, getCustomGraphicCount()-1].
	 */
	public Paint getCustomGraphicPaint(int index) {
		synchronized (m_view.m_lock) {
			return (Paint) m_graphicPaints.get(index);
		}
	}

	/**
	 * Removes the custom graphic object at specified index.  The index parameter
	 * must be in the range [0, getCustomGraphicCount()-1].  Once the object
	 * at specified index is removed, all object remaining and at a higher index
	 * will be shifted such that their index is decreased by one.
	 */
	public void removeCustomGraphic(int index) {
		synchronized (m_view.m_lock) {
			m_graphicShapes.remove(index);
			m_graphicPaints.remove(index);

			if (m_graphicShapes.size() == 0) {
				m_graphicShapes = null;
				m_graphicPaints = null;
			}

			m_view.m_contentChanged = true;
		}
	}

	/**
	 * Adds a custom graphic object at specified index.  The index of an object
	 * is only important in that objects with lower index are rendered before
	 * objects with higher index; if objects overlap, this order may be important
	 * to consider.  A custom graphic object consists of the specified shape
	 * that is filled with the specified paint; the shape is placed relative to
	 * this node's location.
	 */
	public void addCustomGraphic(Shape s, Paint p, int index) {
		if ((s == null) || (p == null))
			throw new NullPointerException("shape and paint must be non-null");

		synchronized (m_view.m_lock) {
			if (index < 0)
				index = 0;
			else if (index > getCustomGraphicCount())
				index = getCustomGraphicCount();

			if (m_graphicShapes == null) {
				m_graphicShapes = new ArrayList<Shape>();
				m_graphicPaints = new ArrayList<Paint>();
			}

			m_graphicShapes.add(index, s);
			m_graphicPaints.add(index, p);
			m_view.m_contentChanged = true;
		}
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param position DOCUMENT ME!
	 */
	public void setTextAnchor(int position) {
		synchronized (m_view.m_lock) {
			m_view.m_nodeDetails.overrideLabelTextAnchor(m_inx, 0, position);
			m_view.m_contentChanged = true;
		}
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public int getTextAnchor() {
		synchronized (m_view.m_lock) {
			return DNodeDetails.convertND2G(m_view.m_nodeDetails.labelTextAnchor(m_inx, 0));
		}
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param justify DOCUMENT ME!
	 */
	public void setJustify(int justify) {
		synchronized (m_view.m_lock) {
			m_view.m_nodeDetails.overrideLabelJustify(m_inx, 0, justify);
			m_view.m_contentChanged = true;
		}
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public int getJustify() {
		synchronized (m_view.m_lock) {
			return DNodeDetails.convertND2G(m_view.m_nodeDetails.labelJustify(m_inx, 0));
		}
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param x DOCUMENT ME!
	 */
	public void setLabelOffsetX(double x) {
		synchronized (m_view.m_lock) {
			m_view.m_nodeDetails.overrideLabelOffsetVectorX(m_inx, 0, x);
			m_view.m_contentChanged = true;
		}
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public double getLabelOffsetX() {
		synchronized (m_view.m_lock) {
			return m_view.m_nodeDetails.labelOffsetVectorX(m_inx, 0);
		}
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param y DOCUMENT ME!
	 */
	public void setLabelOffsetY(double y) {
		synchronized (m_view.m_lock) {
			m_view.m_nodeDetails.overrideLabelOffsetVectorY(m_inx, 0, y);
			m_view.m_contentChanged = true;
		}
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public double getLabelOffsetY() {
		synchronized (m_view.m_lock) {
			return m_view.m_nodeDetails.labelOffsetVectorY(m_inx, 0);
		}
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param position DOCUMENT ME!
	 */
	public void setNodeLabelAnchor(int position) {
		synchronized (m_view.m_lock) {
			m_view.m_nodeDetails.overrideLabelNodeAnchor(m_inx, 0, position);
			m_view.m_contentChanged = true;
		}
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public int getNodeLabelAnchor() {
		synchronized (m_view.m_lock) {
			return DNodeDetails.convertND2G(m_view.m_nodeDetails.labelNodeAnchor(m_inx, 0));
		}
	}
}
