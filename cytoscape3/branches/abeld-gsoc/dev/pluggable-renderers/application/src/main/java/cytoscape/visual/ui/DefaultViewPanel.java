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
package cytoscape.visual.ui;

import org.cytoscape.view.GraphView;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import org.cytoscape.Edge;
import org.cytoscape.GraphPerspective;
import org.cytoscape.Node;
import org.cytoscape.view.GraphViewFactory;
import cytoscape.Cytoscape;
import org.cytoscape.vizmap.VisualStyle;


/**
 * Panel to show the default properties visually (as graphics).
 *
 * @version 0.6
 * @since Cytoscape 2.5
 * @author kono
  */
public class DefaultViewPanel extends JPanel {
	private final static long serialVersionUID = 1202339876691085L;
	private static final int PADDING = 20;
	private GraphView view;
	private static GraphPerspective dummyNet;
	private Color background;

	/*
	 * Dummy graph component
	 */
	private final Node source;
	private final Node target;
	private final Edge edge;
	private VisualStyle visualStyle;

	/**
	 * 
	 */
	public DefaultViewPanel(VisualStyle visualStyle) {
		this.visualStyle = visualStyle;
		source = Cytoscape.getCyNode("Source",true);
		target = Cytoscape.getCyNode("Target",true);
		edge = Cytoscape.getCyEdge(source.getIdentifier(), "Edge", target.getIdentifier(), "interaction");

		List<Node> nodes = new ArrayList<Node>();
		List<Edge> edges = new ArrayList<Edge>();
		nodes.add(source);
		nodes.add(target);
		edges.add(edge);

		dummyNet = Cytoscape.getRootGraph().createGraphPerspective(nodes, edges);
		dummyNet.setTitle("Default Appearance");

		view = GraphViewFactory.createGraphView(dummyNet);
		view.setIdentifier(dummyNet.getIdentifier());
		view.getNodeView(source).setOffset(0, 0);
		view.getNodeView(target).setOffset(150, 10);
		visualStyle.apply(view);
		background = (Color) visualStyle.getGlobalProperty("backgroundColor");
		this.setBackground(background);
	}

	/**
	 * DOCUMENT ME!
	 */
	public void updateView(VisualStyle vs) {
		visualStyle = vs;
		if (view != null) {
			visualStyle.apply(view);
			final Dimension panelSize = this.getSize();
			view.setSize(new Dimension((int) panelSize.getWidth() - PADDING,
			                        (int) panelSize.getHeight() - PADDING));
			view.fitContent();

			Component canvas = view.getComponent(); 
			
			this.removeAll(); //FIXME: should this 'remove then add' be done?
			this.add(canvas);

			canvas.setLocation(PADDING / 2, PADDING / 2);

			if ((background != null) && (canvas != null)) {
				canvas.setBackground(background);
			}
			this.repaint();
		}
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public GraphView getView() {
		return view;
	}
}
