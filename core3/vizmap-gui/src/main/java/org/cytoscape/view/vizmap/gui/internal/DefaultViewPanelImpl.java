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
package org.cytoscape.view.vizmap.gui.internal;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseListener;

import javax.swing.JPanel;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.vizmap.gui.DefaultViewPanel;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyle;


/**
 * Panel to show the default properties visually (as graphics).
 *
 * @version 0.6
 * @since Cytoscape 2.5
 * @author kono
 */
public class DefaultViewPanelImpl extends JPanel implements DefaultViewPanel {
	private final static long serialVersionUID = 1202339876691085L;
	
	// Padding around canvas
	private static final int PADDING = 20;
	
	// Dummy network view
	private CyNetworkView view;
	
	// Dummy network displayed in the canvas
	private static CyNetwork dummyNet;
	
	// Background color of canvas
	private Color background = Color.white;

	/*
	 * Dummy graph components
	 */
	private CyNode source;
	private CyNode target;
	private CyEdge edge;
	
	// Rendering engine's canvas.
	private Component canvas = null;

	// Target visual style to be edited
	private VisualStyle vs;

	// Used to create dummy network
	private CyNetworkFactory cyNetworkFactory;
	private CyNetworkViewFactory cyNetworkViewFactory;

	/**
	 * Creates a new DefaultViewPanel object.
	 *
	 * @param cyNetworkFactory  DOCUMENT ME!
	 * @param cyNetworkViewFactory  DOCUMENT ME!
	 */
	public DefaultViewPanelImpl(CyNetworkFactory cyNetworkFactory, CyNetworkViewFactory cyNetworkViewFactory, VisualStyle vs) {
		this.cyNetworkViewFactory = cyNetworkViewFactory;
		this.cyNetworkFactory = cyNetworkFactory;
		this.vs = vs;
		createDummyNet();

		//TODO: get current BG color from VS
		//background = this.vmm.getVisualStyle().getGlobalAppearanceCalculator().getDefaultBackgroundColor();
		
		this.setBackground(background);
	}
	
	private void createDummyNet() {
		dummyNet = cyNetworkFactory.getInstance();

		source = dummyNet.addNode();
		source.attrs().set("name", "Source");

		target = dummyNet.addNode();
		target.attrs().set("name", "Target");

		edge = dummyNet.addEdge(source, target, true);
		edge.attrs().set("name", "Source (interaction) Target");

		dummyNet.attrs().set("name", "Default Appearance");
		view = cyNetworkViewFactory.getNetworkViewFor(dummyNet);
		
		view.getNodeView(source).getVisualProperty().setOffset(0, 0);
		view.getNodeView(target).setOffset(150, 10);
	}

	protected void updateBackgroungColor(final Color newColor) {
		background = newColor;
		this.setBackground(background);
		repaint();
	}


	/**
	 * DOCUMENT ME!
	 */
	protected void updateView() {
		if (view != null) {
			//vmm.setNetworkView(view);
			vmm.setVisualStyleForView(view, vmm.getVisualStyle());

			final Dimension panelSize = this.getSize();
			view.setSize(new Dimension((int) panelSize.getWidth() - PADDING,
			                           (int) panelSize.getHeight() - PADDING));
			view.fitContent();
			canvas = view.getComponent();

			for (MouseListener listener : canvas.getMouseListeners())
				canvas.removeMouseListener(listener);

			this.removeAll();
			this.add(canvas);

			canvas.setLocation(PADDING / 2, PADDING / 2);
			vmm.applyAppearances();

			if ((background != null) && (canvas != null)) {
				canvas.setBackground(background);
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.cytoscape.vizmap.gui.internal.DefaultViewPanel#getView()
	 */
	public CyNetworkView getView() {
		return view;
	}
	
	/* (non-Javadoc)
	 * @see org.cytoscape.vizmap.gui.internal.DefaultViewPanel#getRendererComponent()
	 */
	public Component getRendererComponent() {
		return canvas;
	}
	
}
