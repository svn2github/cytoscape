/* -*-Java-*-
********************************************************************************
*
* File:         BookkeepingItem.java
* RCS:          $Header: /cvs/cvsroot/lstl-lsi/HyperEdge/src/cytoscape/hyperedge/impl/BookkeepingItem.java,v 1.1 2007/07/04 01:11:35 creech Exp $
* Description:
* Author:       Michael L. Creech
* Created:      Wed Dec 13 05:56:10 2006
* Modified:     Thu Dec 14 11:26:00 2006 (Michael L. Creech) creech@w235krbza760
* Language:     Java
* Package:
* Status:       Experimental (Do Not Distribute)
*
* (c) Copyright 2006, Agilent Technologies, all rights reserved.
*
********************************************************************************
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
    private Set<GraphObject> _toIgnore;
    private CyNetwork        _net;

    public BookkeepingItem(Set<GraphObject> toIgnore, CyNetwork net) {
        _toIgnore = toIgnore;
        _net      = net;
    }

    public CyNetwork getNetwork() {
        return _net;
    }

    /**
     * What items should we ignore (not delete) because we are just
     * doing bookkeeping on them?
     */
    public Set<GraphObject> getItems() {
        return _toIgnore;
    }
}
