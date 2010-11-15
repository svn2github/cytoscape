
/*
 Copyright (c) 2008, The Cytoscape Consortium (www.cytoscape.org)

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
package org.cytoscape.application.swing;

import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.model.View;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.events.RowSetMicroListener;

/**
 * A utility class that provides an implementation of
 * {@link RowSetMicroListener} for a particular {@link View}
 * and {@link VisualProperty}.
 */
public class ViewUpdater<T,S> implements RowSetMicroListener {

	protected final CyRow row; 
	protected final View<T> view;
	protected final VisualProperty<S> vp;
	protected final String columnName;

	/**
	 * Constructor.
	 * @param view The view that the visual property should be set whne the row is changed. 
	 * @param vp The visual property that should be set on the view when the row is changed.
	 * @param row The row that is being listened to.
	 * @param columnName The name of the column within the row that is being listened to.
	 */
	public ViewUpdater(View<T> view, VisualProperty<S> vp, CyRow row, String columnName) {
		this.view = view;
		this.vp = vp;
		this.row = row;
		this.columnName = columnName;
	}

	/**
	 * @inheritdoc
	 */
	public Object getEventSource() {
		return row;
	}

	/**
	 * Called whenever the {@link CyRow} is changed. Will attempt to set
	 * the visual property on the view with the new value that has been
	 * set in the row.
	 * @param columnName The name of the column with the row that was changed.
	 * @param value The new value that the row has been set to. 
	 */
	@SuppressWarnings("unchecked")
	public void handleRowSet(final String columnName, final Object value) {
		if ( columnName == null || !columnName.equals(this.columnName) )
			return;

		// Assume caller checks validity of value parameter.
		view.setVisualProperty(vp, (S)value);
	}
}
