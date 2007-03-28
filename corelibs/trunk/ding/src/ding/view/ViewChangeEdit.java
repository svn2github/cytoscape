
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

import giny.view.NodeView;

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
	private Map<NodeView, Point2D.Double> m_orig_points;

	private double m_new_scaleFactor;
	private Point2D m_new_center;
	private Map<NodeView, Point2D.Double> m_new_points;

	private DGraphView m_view;

	private String m_label;

	public ViewChangeEdit(DGraphView view,String label) {
		super();
		m_view = view;
		m_label = label;

		m_orig_points = new HashMap<NodeView, Point2D.Double>();
		m_new_points = new HashMap<NodeView, Point2D.Double>();

		saveOldPositions();
	}

	protected void saveOldPositions() {
		m_orig_center = m_view.getCenter();
		m_orig_scaleFactor = m_view.getZoom();
		//System.out.println("~~~~~~~~~~ set old center: " + m_orig_center);
		//System.out.println("~~~~~~~~~~ set old zoom: " + m_orig_scaleFactor);
		m_orig_points.clear();

		Iterator nvi = m_view.getNodeViewsIterator(); 
		while (nvi.hasNext()) {
			NodeView nv = (NodeView) nvi.next();
			m_orig_points.put(nv, new Point2D.Double(nv.getXPosition(), nv.getYPosition()));
		}
	}

	protected void saveNewPositions() {
		m_new_center = m_view.getCenter(); 
		m_new_scaleFactor = m_view.getZoom(); 
		//System.out.println("~~~~~~~~~~ set NEW center: " + m_new_center);
		//System.out.println("~~~~~~~~~~ set NEW zoom: " + m_new_scaleFactor);
		m_new_points.clear();

		Iterator nvi = m_view.getNodeViewsIterator(); 
		while (nvi.hasNext()) {
			NodeView nv = (NodeView) nvi.next();
			m_new_points.put(nv, new Point2D.Double(nv.getXPosition(), nv.getYPosition()));
		}
	}

	public void post() {
		saveNewPositions();
		if ( changedSinceInit() )
			Undo.getUndoableEditSupport().postEdit( this );
	}

	private boolean changedSinceInit() {
		if ( !m_new_center.equals(m_orig_center) ) {
			//System.out.println("xxxxxxx center differs " + m_new_center + "  " + m_orig_center);
			return true;
		}

		if ( java.lang.Double.compare(m_new_scaleFactor, m_orig_scaleFactor) != 0 ) {
			//System.out.println("xxxxxxx scale differs " + m_new_scaleFactor + "  " + m_orig_scaleFactor);
			return true;
		}

		Iterator nvi = m_view.getNodeViewsIterator(); 
		while (nvi.hasNext()) {
			NodeView nv = (NodeView) nvi.next();
			if ( !m_new_points.get(nv).equals( m_orig_points.get(nv) ) ) {
				//System.out.println("xxxxxxx pos differs " + m_new_points.get(nv) + " " + m_orig_points.get(nv));
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
		//System.out.println("~~~~~~~ redo NEW center: " + m_new_center);
		//System.out.println("~~~~~~~ redo NEW zoom: " + m_new_scaleFactor);

		Iterator it = m_view.getNodeViewsIterator();
		while (it.hasNext()) {
			NodeView nv = (NodeView) it.next();
			Point2D.Double p = m_new_points.get(nv);
			nv.setXPosition(p.getX());
			nv.setYPosition(p.getY());
		}

		m_view.setZoom(m_new_scaleFactor);
		m_view.setCenter(m_new_center.getX(), m_new_center.getY());
		m_view.updateView();
	}

	/**
	 * Applies the original state to the view.
	 */
	public void undo() {
		super.undo();
		//System.out.println("~~~~~~~ redo old center: " + m_orig_center);
		//System.out.println("~~~~~~~ redo old zoom: " + m_orig_scaleFactor);

		Iterator it = m_view.getNodeViewsIterator();
		while (it.hasNext()) {
			NodeView nv = (NodeView) it.next();
			Point2D.Double p = m_orig_points.get(nv);
			nv.setXPosition(p.getX());
			nv.setYPosition(p.getY());
		}

		m_view.setZoom(m_orig_scaleFactor);
		m_view.setCenter(m_orig_center.getX(), m_orig_center.getY());
		m_view.updateView();
	}
}
