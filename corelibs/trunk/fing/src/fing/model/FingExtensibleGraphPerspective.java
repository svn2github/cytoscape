
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

package fing.model;

import cytoscape.util.intr.IntIterator;


/**
 * Please try to restrain from using this class.  This class was created so
 * that certain legacy applications would have an easier time using this
 * giny.model implementation.  Please use FingRootGraphFactory instead of this
 * class.
 * @see FingRootGraphFactory
 **/
public class FingExtensibleGraphPerspective extends FGraphPerspective {
	/**
	 * rootGraphNodeInx need not contain all endpoint nodes corresponding to
	 * edges in rootGraphEdgeInx - this is calculated automatically by this
	 * constructor.  If any index does not correspond to an existing node or
	 * edge, an IllegalArgumentException is thrown.  The indices lists need not
	 * be non-repeating - the logic in this constructor handles duplicate
	 * filtering.
	 **/
	public FingExtensibleGraphPerspective(FingExtensibleRootGraph root,
	                                      IntIterator rootGraphNodeInx, IntIterator rootGraphEdgeInx) {
		super(root, rootGraphNodeInx, rootGraphEdgeInx);
	}
}
