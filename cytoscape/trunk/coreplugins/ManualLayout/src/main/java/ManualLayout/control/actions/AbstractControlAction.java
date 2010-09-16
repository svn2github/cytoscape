
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

package ManualLayout.control.actions;

import cytoscape.*;
import cytoscape.CyNetwork;
import cytoscape.Cytoscape;

import cytoscape.data.*;

import cytoscape.util.*;

import cytoscape.view.*;
import cytoscape.view.CyNetworkView;

import giny.model.*;
import giny.view.*;

import ding.view.DGraphView;
import ding.view.ViewChangeEdit;

import java.awt.event.*;

import java.util.*;

import javax.swing.*;


/**
 *
 */
public abstract class AbstractControlAction extends CytoscapeAction {
	protected double X_min;
	protected double X_max;
	protected double Y_min;
	protected double Y_max;
	protected NodeView node_view;
	protected Iterator sel_nodes;
	protected String title;

	/**
	 * Creates a new AbstractControlAction object.
	 *
	 * @param icon  DOCUMENT ME!
	 */
	public AbstractControlAction(String title, ImageIcon icon) {
		super("", icon);
		this.title = title;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param e DOCUMENT ME!
	 */
	public void actionPerformed(ActionEvent e) {
		GraphView view = Cytoscape.getCurrentNetworkView();
		ViewChangeEdit vce = new ViewChangeEdit((DGraphView)view, title);
		computeDimensions(view);
		control(view.getSelectedNodes());
		view.updateView();
		vce.post();
	}

	protected abstract void control(List l);

	/**
	 * This may look silly, but it is meant to be overridden
	 * with special cases.
	 */
	protected double getX(NodeView n) {
		return n.getXPosition();
	}

	/**
	 * This may look silly, but it is meant to be overridden
	 * with special cases.
	 */
	protected double getY(NodeView n) {
		return n.getYPosition();
	}

	protected void computeDimensions(GraphView view) {
		X_min = Double.POSITIVE_INFINITY;
		X_max = Double.NEGATIVE_INFINITY;
		Y_min = Double.POSITIVE_INFINITY;
		Y_max = Double.NEGATIVE_INFINITY;
		sel_nodes = view.getSelectedNodes().iterator();

		while (sel_nodes.hasNext()) {
			node_view = (NodeView) sel_nodes.next();

			double X = getX(node_view);

			if (X > X_max)
				X_max = X;

			if (X < X_min)
				X_min = X;

			double Y = getY(node_view);

			if (Y > Y_max)
				Y_max = Y;

			if (Y < Y_min)
				Y_min = Y;
		}
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public boolean isInToolBar() {
		return false;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public boolean isInMenuBar() {
		return false;
	}

	public class XComparator implements Comparator<NodeView> {
		public int compare(NodeView n1, NodeView n2) {
			if (getX(n1) == getX(n2))
				return 0;
			else if (getX(n1) < getX(n2))
				return -1;
			else

				return 1;
		}

		public boolean equals(NodeView n1, NodeView n2) {
			if (getX(n1) == getX(n2))
				return true;
			else

				return false;
		}
	}

	public class YComparator implements Comparator<NodeView> {
		public int compare(NodeView n1, NodeView n2) {
			if (getY(n1) == getY(n2))
				return 0;
			else if (getY(n1) < getY(n2))
				return -1;
			else

				return 1;
		}

		public boolean equals(NodeView n1, NodeView n2) {
			if (getY(n1) == getY(n2))
				return true;
			else

				return false;
		}
	}
}
