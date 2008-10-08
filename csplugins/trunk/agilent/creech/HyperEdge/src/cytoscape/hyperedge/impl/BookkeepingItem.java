
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

package cytoscape.hyperedge.impl;

import cytoscape.CyNetwork;

import giny.model.GraphObject;

import java.util.Set;


/**
 * Define a Set of Nodes and Edges that we must only remove HyperEdge management
 * bookkeeping information--we must ignore actually deleting
 * underlying Cytoscape Nodes and Edges because these items are in the
 * process of being deleted.
 */
class BookkeepingItem {
    private Set<GraphObject> toIgnore;
    private CyNetwork        net;

    /**
     * Constructor.
     * @param toIgnore the Set of Nodes and Edges to ignore deleting.
     * @param net the CyNetwork where these Nodes and Edges reside.
     */
    public BookkeepingItem(final Set<GraphObject> toIgnore, final CyNetwork net) {
        this.toIgnore = toIgnore;
        this.net      = net;
    }

    public CyNetwork getNetwork() {
        return net;
    }

    /**
     * What items should we ignore (not delete) because we are just
     * doing bookkeeping on them?
     */
    public Set<GraphObject> getItems() {
        return toIgnore;
    }
}
