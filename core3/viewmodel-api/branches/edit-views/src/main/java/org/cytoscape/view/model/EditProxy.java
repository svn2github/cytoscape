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
package org.cytoscape.view.model;

/**
 * A proxy that collects the edits made to the underlying object, and
 * doesn't apply them until .mergeEdits() is called.
 * 
 * Thus the changes collected by and EditProxy object can be
 * considered a 'transaction' or a 'revision'.
 * 
 * Note that this interface shouldn't be implemented on its
 * own. Instead, implementors must also implement the interface of the
 * underlying object, as well, and provide an updated view of the
 * underlying object. (See edit-views branch of viewmodel for an
 * example.)
 * 
 * This shouldn't actually be in the viewmodel-api bundle, since it
 * might be used in model-api, i.e. it is more basic.
 */
public interface EditProxy {
	/**
	 * Merge (commit) the changes collected by this proxy into the
	 * underlying object, doing this in as bulk way as
	 * possible. (Triggering as few events as possible.)
	 *
	 * @return  DOCUMENT ME!
	 */
	void mergeEdits();
}
