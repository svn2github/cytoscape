
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

import giny.model.Node;
import giny.model.Edge;
import giny.view.NodeView;
import giny.view.EdgeView;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;

import java.util.*;

import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.UndoableEdit;
import javax.swing.undo.UndoableEditSupport;

import undo.Undo;


/**
 * A Ding specific undoable edit.
 */
public class ViewChangeEdit extends AbstractUndoableEdit {

	private double m_orig_scaleFactor;
	private Point2D m_orig_center;
	private Map<Node, Point2D.Double> m_orig_points;
	private Map<Edge, List> m_orig_anchors;
	private Map<Edge, Integer> m_orig_linetype;

	private double m_new_scaleFactor;
	private Point2D m_new_center;
	private Map<Node, Point2D.Double> m_new_points;
	private Map<Edge, List> m_new_anchors;
	private Map<Edge, Integer> m_new_linetype;

	private DGraphView m_view;

	private String m_label;

	public ViewChangeEdit(DGraphView view,String label) {
		super();
		m_view = view;
		m_label = label;

		m_orig_points = new HashMap<Node, Point2D.Double>();
		m_new_points = new HashMap<Node, Point2D.Double>();

		m_orig_anchors = new HashMap<Edge, List>();
		m_new_anchors = new HashMap<Edge, List>();

		m_orig_linetype = new HashMap<Edge, Integer>();
		m_new_linetype = new HashMap<Edge, Integer>();

		saveOldPositions();
	}

	protected void saveOldPositions() {
		m_orig_center = m_view.getCenter();
		m_orig_scaleFactor = m_view.getZoom();
		m_orig_points.clear();

		// Use nodes as keys because they are less volatile than
		// node views, which can disappear between when this edit
		// is created and when it is used.
		Iterator ni = m_view.getGraphPerspective().nodesIterator(); 
		while (ni.hasNext()) {
			Node n = (Node) ni.next();
			NodeView nv = m_view.getNodeView(n);
			m_orig_points.put(n, new Point2D.Double(nv.getXPosition(), nv.getYPosition()));
		}

		Iterator ei = m_view.getGraphPerspective().edgesIterator(); 
		while (ei.hasNext()) {
			Edge e = (Edge) ei.next();
			EdgeView ev = m_view.getEdgeView(e);
			m_orig_anchors.put(e, ev.getBend().getHandles());
			m_orig_linetype.put(e, ev.getLineType());
		}
	}

	protected void saveNewPositions() {
		m_new_center = m_view.getCenter(); 
		m_new_scaleFactor = m_view.getZoom(); 
		m_new_points.clear();

		// Use nodes as keys because they are less volatile than
		// node views, which can disappear between when this edit
		// is created and when it is used.
		Iterator ni = m_view.getGraphPerspective().nodesIterator(); 
		while (ni.hasNext()) {
			Node n = (Node) ni.next();
			NodeView nv = m_view.getNodeView(n);
			m_new_points.put(n, new Point2D.Double(nv.getXPosition(), nv.getYPosition()));
		}

		Iterator ei = m_view.getGraphPerspective().edgesIterator(); 
		while (ei.hasNext()) {
			Edge e = (Edge) ei.next();
			EdgeView ev = m_view.getEdgeView(e);
			m_new_anchors.put(e, ev.getBend().getHandles());
			m_new_linetype.put(e, ev.getLineType());
		}
	}

	public void post() {
		saveNewPositions();
		if ( changedSinceInit() )
			Undo.getUndoableEditSupport().postEdit( this );
	}

	private boolean changedSinceInit() {
		if ( !m_new_center.equals(m_orig_center) ) {
			return true;
		}

		if ( java.lang.Double.compare(m_new_scaleFactor, m_orig_scaleFactor) != 0 ) {
			return true;
		}

		// Use nodes as keys because they are less volatile than views...
		Iterator ni = m_view.getGraphPerspective().nodesIterator(); 
		while (ni.hasNext()) {
			Node n = (Node) ni.next();
			NodeView nv = m_view.getNodeView(n);
			if ( !m_new_points.get(n).equals( m_orig_points.get(n) ) ) {
				return true;
			}
		}

		return false;
	}

	/**
	 * @return Not sure where this is used.
	 */
	public String getPresentationName() {
		return m_label;
	}

	/**
	 * @return Used in the edit menu.
	 */
	public String getRedoPresentationName() {
		return "Redo: " + m_label;
	}

	/**
	 * @return Used in the edit menu.
	 */
	public String getUndoPresentationName() {
		return "Undo: " + m_label;
	}

	/**
	 * Applies the new state to the view after it has been undone.
	 */
	public void redo() {
		super.redo();

		// Use nodes as keys because they are less volatile than views...
		Iterator ni = m_view.getGraphPerspective().nodesIterator(); 
		while (ni.hasNext()) {
			Node n = (Node) ni.next();
			NodeView nv = m_view.getNodeView(n);
			Point2D.Double p = m_new_points.get(n);
			nv.setXPosition(p.getX());
			nv.setYPosition(p.getY());
		}

		m_view.setZoom(m_new_scaleFactor);
		m_view.setCenter(m_new_center.getX(), m_new_center.getY());
		m_view.updateView();

		Iterator ei = m_view.getGraphPerspective().edgesIterator(); 
		while (ei.hasNext()) {
			Edge e = (Edge) ei.next();
			EdgeView ev = m_view.getEdgeView(e);
			ev.getBend().setHandles( m_new_anchors.get(e) );
			ev.setLineType( m_new_linetype.get(e).intValue() );
		}
	}

	/**
	 * Applies the original state to the view.
	 */
	public void undo() {
		super.undo();

		// Use nodes as keys because they are less volatile than views...
		Iterator ni = m_view.getGraphPerspective().nodesIterator(); 
		while (ni.hasNext()) {
			Node n = (Node) ni.next();
			NodeView nv = m_view.getNodeView(n);
			Point2D.Double p = m_orig_points.get(n);
			nv.setXPosition(p.getX());
			nv.setYPosition(p.getY());
		}

		m_view.setZoom(m_orig_scaleFactor);
		m_view.setCenter(m_orig_center.getX(), m_orig_center.getY());
		m_view.updateView();

		Iterator ei = m_view.getGraphPerspective().edgesIterator(); 
		while (ei.hasNext()) {
			Edge e = (Edge) ei.next();
			EdgeView ev = m_view.getEdgeView(e);
			ev.getBend().setHandles( m_orig_anchors.get(e) );
			ev.setLineType( m_orig_linetype.get(e).intValue() );
		}
	}
}
