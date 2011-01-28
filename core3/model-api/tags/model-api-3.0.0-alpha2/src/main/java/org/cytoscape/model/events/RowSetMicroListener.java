/*
 Copyright (c) 2008, 2010, The Cytoscape Consortium (www.cytoscape.org)

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
package org.cytoscape.model.events;


import org.cytoscape.event.CyMicroListener;


/**
 * Listener for row set events. The event source for this listener
 * will the be CyRow that is being modified. 
 */
public interface RowSetMicroListener extends CyMicroListener {
	/**
	 * The method that should react to the changed row.
	 * @param columnName The name of the column changed.
	 * @param newValue The value the column was set to.
	 * @param newRawValue The internal representation of the new value which may or may not be
	 *        the same as newValue.
	 *
	 * If both, newValue and newRawValue are null this means that the table entry was unset. If
	 * only newValue is null and newRawValue is not null this means that newRawValue cannot be
	 * evaluated currently but that the row was still updated!
	 */
	void handleRowSet(final String columnName, final Object newValue, final Object newRawValue);
}
