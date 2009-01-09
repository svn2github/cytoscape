
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

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 */
public class BirdsEyeView extends Component {
	private final static long serialVersionUID = 1202416511863994L;
	private final double[] m_extents = new double[4];
	private DGraphView m_view;
	private final ContentChangeListener m_cLis;
	private final ViewportChangeListener m_vLis;
	private Image m_img = null;
	private boolean m_contentChanged = false;
	private double m_myXCenter;
	private double m_myYCenter;
	private double m_myScaleFactor;
	private int m_viewWidth;
	private int m_viewHeight;
	private double m_viewXCenter;
	private double m_viewYCenter;
	private double m_viewScaleFactor;
	private Component m_desktopView;

	/**
	 * Creates a new BirdsEyeView object.
	 *
	 * @param view DOCUMENT ME!
	 */
	public BirdsEyeView(DGraphView view) {
		this(view, null);
	}

	/**
	 * Creates a new BirdsEyeView object.
	 *
	 * @param view The view to monitor
	 * @param desktopView The desktop area holding the view. This should be NetworkViewManager.getDesktopPane().
	 */
	public BirdsEyeView(DGraphView view, Component desktopView) {
		super();
		m_cLis = new InnerContentChangeListener();
		m_vLis = new InnerViewportChangeListener();
		m_desktopView = desktopView;
		addMouseListener(new InnerMouseListener());
		addMouseMotionListener(new InnerMouseMotionListener());
		changeView(view);
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param view DOCUMENT ME!
	 */
	public void changeView(DGraphView view) {
		destroy();
		m_view = view;

		if (m_view != null) {
			m_view.addContentChangeListener(m_cLis);
			m_view.addViewportChangeListener(m_vLis);
			updateBounds();
			final Point2D pt = m_view.getCenter();
			m_viewXCenter = pt.getX();
			m_viewYCenter = pt.getY();
			m_viewScaleFactor = m_view.getZoom();
			m_contentChanged = true;
		}

		repaint();
	}

	private void updateBounds()
	{
		final Rectangle2D viewable = getViewableRect();
		m_viewWidth = (int) viewable.getWidth();
		m_viewHeight = (int) viewable.getHeight();
		final Rectangle2D viewableInView = getViewableRectInView(viewable);
		m_viewXCenter = viewableInView.getX() + viewableInView.getWidth() / 2.0;
		m_viewYCenter = viewableInView.getY() + viewableInView.getHeight() / 2.0;
	}

	private Rectangle2D getViewableRectInView(final Rectangle2D viewable)
	{
		if (m_view == null || m_view.getCanvas() == null || m_view.getCanvas().m_grafx == null)
			return new Rectangle2D.Double(0.0, 0.0, 0.0, 0.0);

		final double[] origin = new double[2];
		origin[0] = viewable.getX();
		origin[1] = viewable.getY();
		m_view.xformComponentToNodeCoords(origin);


		final double[] destination = new double[2];
		destination[0] = viewable.getX() + viewable.getWidth();
		destination[1] = viewable.getY() + viewable.getHeight();
		m_view.xformComponentToNodeCoords(destination);

		Rectangle2D result = new Rectangle2D.Double(origin[0], origin[1], destination[0] - origin[0], destination[1] - origin[1]);
		return result;
	}

	private Rectangle2D getViewableRect()
	{
		if (m_view == null)
			return new Rectangle2D.Double(0.0, 0.0, 0.0, 0.0);

		if (m_desktopView == null)
		{
			final Rectangle r = m_view.getComponent().getBounds();
			return new Rectangle2D.Double(r.x, r.y, r.width, r.height);
		}

		final Rectangle desktopRect = m_desktopView.getBounds();
		if (m_desktopView.isShowing())
		{
			Point s = m_desktopView.getLocationOnScreen();
			desktopRect.x = s.x;
			desktopRect.y = s.y;
		}

		final Rectangle viewRect = m_view.getComponent().getBounds();
		if (m_view.getComponent().isShowing())
		{
			Point s = m_view.getComponent().getLocationOnScreen();
			viewRect.x = s.x;
			viewRect.y = s.y;
		}

		desktopRect.x -= viewRect.x;
		desktopRect.y -= viewRect.y;
		viewRect.x = 0;
		viewRect.y = 0;

		final Rectangle viewable = desktopRect.intersection(viewRect);
		return viewable;
	}

	/**
	 * DOCUMENT ME!
	 */
	public void destroy() {
		if (m_view == null)
			return;

		m_view.removeContentChangeListener(m_cLis);
		m_view.removeViewportChangeListener(m_vLis);
	}

	/**
	 * This used to be called reshape, which is deprecated, so I've changed
	 * it to setBounds.  Not sure if this will break anything!
	 *
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	public void setBounds(int x, int y, int width, int height) {
		super.setBounds(x, y, width, height);

		if ((width > 0) && (height > 0))
			m_img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

		m_contentChanged = true;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param g DOCUMENT ME!
	 */
	public void update(Graphics g) {
		if (m_img == null)
			return;

		if (m_view == null) {
			g.setColor(Color.white);
			g.fillRect(0, 0, getWidth(), getHeight());

			return;
		}

		updateBounds();

		if (m_contentChanged) {
			if (m_view.getExtents(m_extents)) {
				m_myXCenter = (m_extents[0] + m_extents[2]) / 2.0d;
				m_myYCenter = (m_extents[1] + m_extents[3]) / 2.0d;
				m_myScaleFactor = 0.8d * Math.min(((double) getWidth()) / (m_extents[2]
				                                                          - m_extents[0]),
				                                  ((double) getHeight()) / (m_extents[3]
				                                                           - m_extents[1]));
			} else {
				m_myXCenter = 0.0d;
				m_myYCenter = 0.0d;
				m_myScaleFactor = 1.0d;
			}

			m_view.drawSnapshot(m_img, m_view.getGraphLOD(),
			                    m_view.m_backgroundCanvas.getBackground(), m_myXCenter,
			                    m_myYCenter, m_myScaleFactor);
			m_contentChanged = false;
		}

		g.drawImage(m_img, 0, 0, null);

		final double rectWidth = m_myScaleFactor * (((double) m_viewWidth) / m_viewScaleFactor);
		final double rectHeight = m_myScaleFactor * (((double) m_viewHeight) / m_viewScaleFactor);
		final double rectXCenter = (((double) getWidth()) / 2.0d)
		                           + (m_myScaleFactor * (m_viewXCenter - m_myXCenter));
		final double rectYCenter = (((double) getHeight()) / 2.0d)
		                           + (m_myScaleFactor * (m_viewYCenter - m_myYCenter));
		final Rectangle2D rect = new Rectangle2D.Double(rectXCenter - (rectWidth / 2),
		                                                rectYCenter - (rectHeight / 2), rectWidth,
		                                                rectHeight);
		final Graphics2D g2 = (Graphics2D) g;
		g2.setColor(new Color(63, 63, 255, 63));
		g2.fill(rect);
		g2.setColor(Color.blue);
		g2.draw(rect);
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param g DOCUMENT ME!
	 */
	public void paint(Graphics g) {
		update(g);
	}

	private final class InnerContentChangeListener implements ContentChangeListener {
		public void contentChanged() {
			m_contentChanged = true;
			repaint();
		}
	}

	private final class InnerViewportChangeListener implements ViewportChangeListener {
		public void viewportChanged(int w, int h, double newXCenter, double newYCenter,
		                            double newScaleFactor) {
			m_viewWidth = w;
			m_viewHeight = h;
			m_viewXCenter = newXCenter;
			m_viewYCenter = newYCenter;
			m_viewScaleFactor = newScaleFactor;
			repaint();
		}
	}

	private int m_currMouseButton = 0;
	private int m_lastXMousePos = 0;
	private int m_lastYMousePos = 0;

	private final class InnerMouseListener implements MouseListener {
		public void mouseClicked(MouseEvent e) {
		}

		public void mouseEntered(MouseEvent e) {
		}

		public void mouseExited(MouseEvent e) {
		}

		public void mousePressed(MouseEvent e) {
			if (e.getButton() == MouseEvent.BUTTON1) {
				m_currMouseButton = 1;
				m_lastXMousePos = e.getX();
				m_lastYMousePos = e.getY();
			}
		}

		public void mouseReleased(MouseEvent e) {
			if (e.getButton() == MouseEvent.BUTTON1) {
				if (m_currMouseButton == 1)
					m_currMouseButton = 0;
			}
		}
	}

	private final class InnerMouseMotionListener implements MouseMotionListener {
		public void mouseDragged(MouseEvent e) {
			if (m_currMouseButton == 1) {
				final int currX = e.getX();
				final int currY = e.getY();
				final double deltaX = (currX - m_lastXMousePos) / m_myScaleFactor;
				final double deltaY = (currY - m_lastYMousePos) / m_myScaleFactor;
				m_lastXMousePos = currX;
				m_lastYMousePos = currY;

				if (m_view != null) {
					final Point2D pt = m_view.getCenter();
					m_view.setCenter(pt.getX() + deltaX, pt.getY() + deltaY);
				}
			}
		}

		public void mouseMoved(MouseEvent e) {
		}
	}
}
