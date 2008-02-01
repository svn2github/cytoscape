
/*
 Copyright (c) 2007, The Cytoscape Consortium (www.cytoscape.org)

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

import giny.model.Node;
import giny.model.Edge;
import giny.view.NodeView;
import giny.view.EdgeView;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;

import java.util.*;


/**
 * Records the state of a view.  Used for undo by ViewChangeEdit. If it would help
 * to make this public, then please do so.
 */
class ViewState {

	protected double scaleFactor;
	protected Point2D center;
	protected Map<Node, Point2D.Double> points;
	protected Map<Edge, List> anchors;
	protected Map<Edge, Integer> linetype;
	protected DGraphView view;

	/**
	 * @param v The view whose state we're recording.
	 */
	public ViewState(DGraphView v) {
		view = v;
		points = new HashMap<Node, Point2D.Double>();
		anchors = new HashMap<Edge, List>();
		linetype = new HashMap<Edge, Integer>();

		// record the state of the view
		center = view.getCenter();
		scaleFactor = view.getZoom();
		points.clear();

		// Use nodes as keys because they are less volatile than
		// node views, which can disappear between when this edit
		// is created and when it is used.
		Iterator ni = view.getGraphPerspective().nodesIterator(); 
		while (ni.hasNext()) {
			Node n = (Node) ni.next();
			NodeView nv = view.getNodeView(n);
			points.put(n, new Point2D.Double(nv.getXPosition(), nv.getYPosition()));
		}

		Iterator ei = view.getGraphPerspective().edgesIterator(); 
		while (ei.hasNext()) {
			Edge e = (Edge) ei.next();
			EdgeView ev = view.getEdgeView(e);
			anchors.put(e, ev.getBend().getHandles());
			linetype.put(e, ev.getLineType());
		}
	}

	/**
	 * Checks if the ViewState is the same. If scale and center are
	 * equal it then begins comparing node positions.
	 * @param o The object to test for equality.
	 */
	public boolean equals(Object o) {
		if ( !(o instanceof ViewState) ) {
			return false;
		}

		ViewState vs = (ViewState)o; 

		if ( view != vs.view ) {
			return false;
		}

		if ( !center.equals(vs.center) ) {
			return false;
		}

		if ( java.lang.Double.compare(scaleFactor, vs.scaleFactor) != 0 ) {
			return false;
		}

		// Use nodes as keys because they are less volatile than views...
		Iterator ni = view.getGraphPerspective().nodesIterator(); 
		while (ni.hasNext()) {
			Node n = (Node) ni.next();
			NodeView nv = view.getNodeView(n);
			if ( !points.get(n).equals( vs.points.get(n) ) ) {
				return false;
			}
		}

		return true;
	}


	/**
	 * Applies the recorded state to the view used to create
	 * this object.
	 */
	public void apply() {

		// Use nodes as keys because they are less volatile than views...
		Iterator ni = view.getGraphPerspective().nodesIterator(); 
		while (ni.hasNext()) {
			Node n = (Node) ni.next();
			NodeView nv = view.getNodeView(n);
			Point2D.Double p = points.get(n);
			nv.setXPosition(p.getX());
			nv.setYPosition(p.getY());
		}

		view.setZoom(scaleFactor);
		view.setCenter(center.getX(), center.getY());
		view.updateView();

		Iterator ei = view.getGraphPerspective().edgesIterator(); 
		while (ei.hasNext()) {
			Edge e = (Edge) ei.next();
			EdgeView ev = view.getEdgeView(e);
			ev.getBend().setHandles( anchors.get(e) );
			ev.setLineType( linetype.get(e).intValue() );
		}
	}
}
