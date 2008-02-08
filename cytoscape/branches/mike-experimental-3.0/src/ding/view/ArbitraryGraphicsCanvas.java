/*
  File: ArbitraryGraphicsCanvas.java

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

// package
package ding.view;

import cytoscape.GraphPerspective;

// import
import cytoscape.Node;

import giny.view.NodeView;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import java.util.HashMap;
import java.util.Map;


/**
 * This class extends cytoscape.view.CytoscapeCanvas.  Its meant
 * to live within a ding.view.DGraphView class.  It is the canvas
 * used for arbitrary graphics drawing (background & foreground panes).
 */
public class ArbitraryGraphicsCanvas extends DingCanvas implements ViewportChangeListener {
	private final static long serialVersionUID = 1202416510975364L;
	/**
	 * Testing boolean to quickly turn on/off anchor nodes.
	 */
	private static final boolean USE_REPOSITION_CODE = false;

	/**
	 * Our reference to the GraphPerspective our view belongs to
	 */
	private GraphPerspective m_graphPerspective;

	/**
	 * Our reference to the DGraphView we live within
	 */
	private DGraphView m_dGraphView;

	/**
	 * Our reference to the inner canvas
	 */
	private InnerCanvas m_innerCanvas;

	/*
	 * Map of component(s) to hidden node(s)
	 */
	private Map<Component, Node> m_componentToNodeMap;

	/**
	 * Constructor.
	 *
	 * @param graphPerspective GraphPerspective
	 * @param dGraphView DGraphView
	 * @param innerCanvas InnerCanvas
	 * @param backgroundColor Color
	 * @param isVisible boolean
	 * @param isOpaque boolean
	 */
	public ArbitraryGraphicsCanvas(GraphPerspective graphPerspective, DGraphView dGraphView,
	                               InnerCanvas innerCanvas, Color backgroundColor,
	                               boolean isVisible, boolean isOpaque) {
		// init members
		m_graphPerspective = graphPerspective;
		m_dGraphView = dGraphView;
		m_innerCanvas = innerCanvas;
		m_backgroundColor = backgroundColor;
		m_isVisible = isVisible;
		m_isOpaque = isOpaque;
		m_componentToNodeMap = new HashMap<Component, Node>();
	}

	/**
	 * Our implementation of add
	 */
	public Component add(Component component) {
		if (USE_REPOSITION_CODE) {
			// create an "anchor node"
			int nodeIndex = m_graphPerspective.getRootGraph().createNode();
			Node node = m_graphPerspective.getRootGraph().getNode(nodeIndex);
			node.setIdentifier(component.toString());
			m_graphPerspective.restoreNode(node);

			// set its node view coordinates
			NodeView nodeView = m_dGraphView.getNodeView(node);
			double[] nodeCanvasCoordinates = new double[2];
			nodeCanvasCoordinates[0] = component.getX();
			nodeCanvasCoordinates[1] = component.getY();
			m_dGraphView.xformComponentToNodeCoords(nodeCanvasCoordinates);
			nodeView.setXPosition(nodeCanvasCoordinates[0]);
			nodeView.setYPosition(nodeCanvasCoordinates[1]);

			// add to map
			m_componentToNodeMap.put(component, node);

			// hide the node - make it very small -
			// hiding it via hideGraphObject takes it out of the ding repositioning loop
			//m_dGraphView.hideGraphObject(nodeView, true, true);
			nodeView.setWidth(1.0);
			nodeView.setHeight(1.0);
		}

		// do our stuff
		return super.add(component);
	}

	/**
	 * Our implementation of ViewportChangeListener.
	 */
	public void viewportChanged(int viewportWidth, int viewportHeight, double newXCenter,
	                            double newYCenter, double newScaleFactor) {
		if (USE_REPOSITION_CODE) {
			if (setBoundsChildren())
				repaint();
		}
	}

	/**
	 * Our implementation of JComponent setBounds.
	 */
	public void setBounds(int x, int y, int width, int height) {
		super.setBounds(x, y, width, height);

		// our bounds have changed, create a new image with new size
		if ((width > 0) && (height > 0)) {
			// create the buffered image
			m_img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

			// update childrens bounds
			if (USE_REPOSITION_CODE) {
				setBoundsChildren();
			}
		}
	}

	/**
	 * Our implementation of paint.
	 * Invoked by Swing to draw components.
	 *
	 * @param graphics Graphics
	 */
	public void paint(Graphics graphics) {
		// only paint if we have an image to paint on
		if (m_img != null) {
			// get image graphics
			Graphics2D image2D = ((BufferedImage) m_img).createGraphics();

			// first clear the image
			clearImage(image2D);

			// now paint children
			if (m_isVisible)
				paintChildren(image2D);

			// render image
			graphics.drawImage(m_img, 0, 0, null);
		}
	}

	/**
	 * Invoke this method to print the component.
	 *
	 * @param graphics Graphics
	 */
	public void print(Graphics graphics) {
		//if we have an image to print, lets print it.
		if (m_img != null) {
			graphics.drawImage(m_img, 0, 0, null);
		}
	}

	/**
	 * Called to update the bounds of our child components.
	 *
	 * @return boolean
	 */
	private boolean setBoundsChildren() {
		// get list of child components
		Component[] components = getComponents();

		// no components, outta here
		if (components.length == 0)
			return false;

		// interate through the components
		for (Component c : components) {
			// get node
			Node node = m_componentToNodeMap.get(c);

			// get node view
			NodeView nodeView = m_dGraphView.getNodeView(node);

			// new image coordinates
			double[] currentNodeCoordinates = new double[2];
			currentNodeCoordinates[0] = nodeView.getXPosition();
			currentNodeCoordinates[1] = nodeView.getYPosition();

			AffineTransform transform = m_innerCanvas.getAffineTransform();
			transform.transform(currentNodeCoordinates, 0, currentNodeCoordinates, 0, 1);

			// set bounds
			c.setBounds((int) currentNodeCoordinates[0], (int) currentNodeCoordinates[1],
			            c.getWidth(), c.getHeight());
		}

		// outta here
		return true;
	}

	/**
	 * Utility function to clean the background of the image,
	 * using m_backgroundColor
	 *
	 * image2D Graphics2D
	 */
	private void clearImage(Graphics2D image2D) {
		// set color alpha based on opacity setting
		int alpha = (m_isOpaque) ? 255 : 0;
		Color backgroundColor = new Color(m_backgroundColor.getRed(), m_backgroundColor.getGreen(),
		                                  m_backgroundColor.getBlue(), alpha);

		// set the alpha composite on the image, and clear its area
		Composite origComposite = image2D.getComposite();
		image2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC));
		image2D.setPaint(backgroundColor);
		image2D.fillRect(0, 0, m_img.getWidth(null), m_img.getHeight(null));
		image2D.setComposite(origComposite);
	}
}
