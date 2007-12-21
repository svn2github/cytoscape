/* -*-Java-*-
********************************************************************************
*
* File:         HyperEdgeManagerImpl.java
* RCS:          $Header: /cvs/cvsroot/lstl-lsi/HyperEdge/src/cytoscape/hyperedge/impl/HyperEdgeManagerImpl.java,v 1.1 2007/07/04 01:11:35 creech Exp $
* Description:
* Author:       Michael L. Creech
* Created:      Fri Sep 16 17:13:31 2005
* Modified:     Thu Dec 20 10:22:06 2007 (Michael L. Creech) creech@w235krbza760
* Language:     Java
* Package:
* Status:       Experimental (Do Not Distribute)
*
* (c) Copyright 2005, Agilent Technologies, all rights reserved.
*
********************************************************************************
*
* Revisions:
*
* Wed Dec 19 14:23:47 2007 (Michael L. Creech) creech@w235krbza760
*  Added actually deleting connectorNodes on the delayed deletion
*  list whenever NETWORK_MODIFIED events are fired.
* Thu Oct 25 16:02:47 2007 (Michael L. Creech) creech@w235krbza760
*  Changed to version 2.57
* Fri Oct 05 03:49:24 2007 (Michael L. Creech) creech@w235krbza760
*  Changed to version 2.56.
* Mon Jul 30 14:53:04 2007 (Michael L. Creech) creech@w235krbza760
*  Changed to version 2.55.
* Wed Jul 25 16:09:15 2007 (Michael L. Creech) creech@w235krbza760
*  Changed to version 2.54.
* Tue Jul 03 16:36:16 2007 (Michael L. Creech) creech@w235krbza760
*  Changed to version 2.53.
* Thu Jun 28 16:44:28 2007 (Michael L. Creech) creech@w235krbza760
*  Changed to version 2.52.
* Thu Jun 21 05:25:19 2007 (Michael L. Creech) creech@w235krbza760
*  Changed use of CyAttributes.deleteAttribute()->HEUtils.deleteAttribute()
*  due to Cytoscape bug.
* Fri May 11 15:35:20 2007 (Michael L. Creech) creech@w235krbza760
*  Added no arg version of hideConnectorNodes() that removes nodes
*  from all Networks.
* Tue May 08 17:56:56 2007 (Michael L. Creech) creech@w235krbza760
*  Updated version to 2.50.
* Tue Jan 16 08:54:59 2007 (Michael L. Creech) creech@w235krbza760
*  Commented out some debugging statements. Updated version to 2.4 alfa 6.
* Wed Dec 27 16:16:57 2006 (Michael L. Creech) creech@w235krbza760
*  Added the delayed node deletion solution to the recursive
*  hiding problem (see HyperEdge documents, deletion-issues.txt).
*  This involved removing use of DeletedCatcher and
*  adding hideConnectorNodes() and addToDelayedHidingMap().
* Fri Dec 15 10:13:08 2006 (Michael L. Creech) creech@w235krbza760
*  Added use of BookkeepingItem in attempted fix when GraphObjects
*  are hidden. Will be removed in future.
* Wed Dec 13 14:25:34 2006 (Michael L. Creech) creech@w235krbza760
*  Fixed bug in removeNodeInfoWhenNeeded() where nodes that are
*  both regular nodes and ConnectorNodes (via a shared edge) were
*  having their attributes removed when a shared edge connecting them
*  was removed.
* Tue Dec 05 11:43:28 2006 (Michael L. Creech) creech@w235krbza760
*  Changed to version 2.4 alfa 3.
* Tue Nov 28 06:20:24 2006 (Michael L. Creech) creech@w235krbza760
*  Added event handling for removal of nodes and edges and destruction
*  of networks.
* Tue Nov 07 07:26:16 2006 (Michael L. Creech) creech@w235krbza760
*  Changed use of Edge-->CyEdge. Changed to version 2.4 alfa 1.
* Mon Nov 06 09:19:37 2006 (Michael L. Creech) creech@w235krbza760
*  Changed GraphPerspective-->CyNetwork, Node-->CyNode, Edge-->CyEdge.
* Mon Nov 06 05:28:55 2006 (Michael L. Creech) creech@w235krbza760
*  Changed to version 2.1.1.
* Thu Nov 02 17:03:01 2006 (Michael L. Creech) creech@w235krbza760
*  Removed use of _gp_to_nodes_map and changed semantics to
*  getNodesByEdgeTypes() to be an ORing vs ANDing.
* Thu Nov 02 05:19:45 2006 (Michael L. Creech) creech@w235krbza760
* Changed overall API and implementation so that HyperEdges are shared
* across GraphPerspectives--change made to HyperEdge affects all
* GraphPerspectives.
* Changed to version 2.1.
* Wed Nov 01 15:46:47 2006 (Michael L. Creech) creech@w235krbza760
*  Changed all unmodifiable iterators created to use
*  HEUtils.buildUnmodifiableCollectionIterator().
* Wed Sep 20 08:09:52 2006 (Michael L. Creech) creech@w235krbza760
*  Changed to version 2.0 alfa 4.
* Wed Sep 13 14:43:10 2006 (Michael L. Creech) creech@w235krbza760
*  Changed to version 2.0 alfa 3.
* Sun Aug 27 14:45:53 2006 (Michael L. Creech) creech@w235krbza760
*  Beefed up checkIsEmptry() error messages.
* Sun Aug 20 19:33:27 2006 (Michael L. Creech) creech@w235krbza760
*  Converted to Java 5 with strongly types collections.
* Wed Aug 16 14:19:53 2006 (Michael L. Creech) creech@w235krbza760
*  Removed _he_to_gps_map and used HyperEdgeImpl.primGetGraphPerspectives()
*  instead.
* Wed May 24 15:51:18 2006 (Michael L. Creech) creech@Dill
*  Changed to version 1.0 alfa 6.
* Mon May 08 19:23:38 2005 (Michael L. Creech) creech@Dill
*  Changed to version 1.0 alfa 5.
********************************************************************************
*/
package cytoscape.hyperedge.impl;


import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;

import cytoscape.data.CyAttributes;
import cytoscape.data.CyAttributesUtils;
import cytoscape.data.Semantics;
import cytoscape.hyperedge.HEStructuralIntegrityException;
import cytoscape.hyperedge.HyperEdge;
import cytoscape.hyperedge.HyperEdgeManager;
import cytoscape.hyperedge.LifeState;
import cytoscape.hyperedge.event.ChangeListener;
import cytoscape.hyperedge.event.DeleteListener;
import cytoscape.hyperedge.event.EventNote;
import cytoscape.hyperedge.event.NewObjectListener;
import cytoscape.hyperedge.impl.utils.HEUtils;
import cytoscape.hyperedge.impl.utils.ListenerList;
import cytoscape.hyperedge.impl.utils.MapUtils;

import giny.model.GraphObject;
import giny.model.GraphPerspectiveChangeEvent;
import giny.model.GraphPerspectiveChangeListener;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Singleton object implementation of the HyperEdgeManager interface.
 *
 * <H4>Unmodifiable Iterator Return Values</H4>
 *
 * Several HyperEdgeManager operations return unmodifiable iterators
 * as return values (e.g., HyperEdgeManager.getHyperEdgesByNode()).
 * Creation of all these iterators is done using {@link
 * HEUtils#buildUnmodifiableCollectionIterator
 * HEUtils.buildUnmodifiableCollectionIterator()}. These iterators
 * have certain properties that should be understood. Please see this
 * method for details.
 *
 * @author Michael L. Creech
 * @version 2.0
 */
public class HyperEdgeManagerImpl implements HyperEdgeManager {
    // used for fine-grained synchronization:
    private final static Boolean          INTERSECTION_LOCK   = new Boolean(true);
    private static final HyperEdgeManager INSTANCE            = new HyperEdgeManagerImpl();
    private static final Double           VERSION_NUMBER      = 2.58;
    private static final String           VERSION             = "HyperEdge Version " +
                                                                VERSION_NUMBER +
                                                                ", 20-Dec-07";
    private static transient ListenerList _new_listener_store = new ListenerList();

    // Used for setting and reading _internalRemoval:
    private final static Boolean LOCAL_REMOVAL_LOCK = new Boolean(true);

    // handles edges removed from CyNetworks:
    private transient GraphObjsHiddenUpdater gosUpdater = new GraphObjsHiddenUpdater();

    // private transient HyperEdgeFactory _factory = HyperEdgeFactory.INSTANCE;

    // maps a String UUID to a HyperEdge:
    private Map<String, HyperEdge> _uuid_to_he_map = new HashMap<String, HyperEdge>();

    // This is updated when a HyperEdge is created/destroyed:
    private Set<HyperEdge> _all_hyper_edges = new HashSet<HyperEdge>();

    // This is updated when a CyEdge is added/removed from a HyperEdge:
    private Set<CyEdge> _all_edges = new HashSet<CyEdge>();

    // This is updated when a CyNode is added/removed from a HyperEdge:
    // Does not contain ConnectorNodes.
    private Set<CyNode> _all_nodes = new HashSet<CyNode>();

    //    // maps a HyperEdge to a List of all CyNetworks it belongs to:
    //    // should always be in sync with _net_to_hes_map:
    //    private Map _he_to_nets_map = new HashMap();

    // maps a CyNetwork to a List of all HyperEdges within it:
    // should always be in sync with _he_to_net_map:
    private Map<CyNetwork, List<HyperEdge>> _net_to_hes_map = new HashMap<CyNetwork, List<HyperEdge>>();

    // maps a CyNode to a List of HyperEdges:
    // Does not contain ConnectorNodes.
    private Map<CyNode, List<HyperEdge>> _node_to_hes_map = new HashMap<CyNode, List<HyperEdge>>();

    // maps a CyNode to a List of CyEdges (within the HyperEdge world, not Cytstoscape):
    // Does not contain ConnectorNodes.
    private Map<CyNode, List<CyEdge>> _node_to_edges_map = new HashMap<CyNode, List<CyEdge>>();

    //    // maps an edge interaction type to a List of HyperEdges:
    // MLC 08/11/06:
    //    private Map _eit_to_hes_map = new HashMap();

    // maps ConnectorNodes to the HyperEdge that contains them:
    private Map<CyNode, HyperEdge> _cn_to_he_map = new HashMap<CyNode, HyperEdge>();

    // maps a CyNetwork to a Set of CyEdges:
    private Map<CyNetwork, Set<CyEdge>> _net_to_edges_map = new HashMap<CyNetwork, Set<CyEdge>>();
    private Map<CyNetwork, Set<CyNode>> _delayedHidingMap = new HashMap<CyNetwork, Set<CyNode>>();

    //    // Maps a CyNetwork to the set of CyNodes that it contains
    //    // via HyperEdges.  Specifically, it maps a CyNetwork to a
    //    // RefCountMultiValue that contains a List of (CyNodes) along with
    //    // reference counts to these CyNodes.  Note that a CyNetwork
    //    // may contain more than one reference to the same CyNode thru other
    //    // HyperEdges or thru multiple references in the same HyperEdge.
    //    // Does not map to ConnectorNodes.
    //    private Map<CyNetwork, RefCountMultiValue<CyNode>> _net_to_nodes_map = new HashMap<CyNetwork, RefCountMultiValue<CyNode>>();

    // Have any persistent objects been changed?:
    // private transient boolean _any_dirty;
    // Used for if any persistent objs are dirty--useful for
    // operations like save:
    // private transient ListenerList _any_dirty_listener_store = new ListenerList();
    private transient ListenerList _change_listener_store;
    private transient ListenerList _delete_listener_store;

    // Used to specify if we are in the middle of an internal removal of edges or nodes
    // so that we can ignore event handling. For example, if we perform a HyperEdge.removeEdge()
    // which leads to actually removing the edge in Cytoscape, we want to ignore the
    // GraphPerpsectiveChangeEvent for the removal of this edge in GraphObjsHiddenUpdater.
    // In this case, _internalRemoval would be true. On the other hand, if something external
    // caused an edge to be removed (e.g,. user of Cytoscape deleting selected edges), then
    // we want GraphObjsHiddenUpdater to run normally.
    private transient boolean _internalRemoval = false;

    // private CytoscapeData _hedge_attr_data = new CytoscapeDataImpl(CytoscapeDataImpl.OTHER);
    protected HyperEdgeManagerImpl() {
        super();
        Cytoscape.getSwingPropertyChangeSupport().addPropertyChangeListener(Cytoscape.SESSION_LOADED,
                                                                            new HESessionLoadedUpdater());
        Cytoscape.getSwingPropertyChangeSupport().addPropertyChangeListener(Cytoscape.NETWORK_LOADED,
                                                                            new HENetworkLoadedUpdater());
        Cytoscape.getSwingPropertyChangeSupport().addPropertyChangeListener(Cytoscape.NETWORK_CREATED,
                                                                            new HENetworkCreatedUpdater());
        Cytoscape.getSwingPropertyChangeSupport().addPropertyChangeListener(Cytoscape.NETWORK_DESTROYED,
                                                                            new HENetworkDestroyedUpdater());
	// MLC 12/18/07 BEGIN:
        Cytoscape.getSwingPropertyChangeSupport().addPropertyChangeListener(Cytoscape.NETWORK_MODIFIED,
                                                                            new HENetworkModifiedUpdater());	
	// MLC 12/18/07 END.
        //        Cytoscape.getPropertyChangeSupport().addPropertyChangeListener(this);
    }

    // implements HyperEdgeManager interface:
    public void reset(boolean fireEvents) {
        //        if (fireEvents) {
        // delete all HyperEdges, this should clear all other
        // data structures:
        // Don't use iterator because _all_hyper_edges will be
        // modified as we destroy hyper edges:
        HyperEdge[] hes = new HyperEdge[_all_hyper_edges.size()];
        // Object[]  hes = _all_hyper_edges.toArray();
        _all_hyper_edges.toArray(hes);

        for (HyperEdge he : hes) {
            ((HyperEdgeImpl) he).primDestroy(fireEvents, null);
        }

        //        } else {
        //            // cleanup all attributes we generated:
        //            removeGeneratedHyperEdgeAttributes();
        //
        //            // remove all connector Nodes (and thereby all CyEdges) before
        //            // resetting data structures. ASSUME: _all_hyper_edges is not
        //            // affected by removal at the Cytoscape level:
        //            Iterator<HyperEdge> it = _all_hyper_edges.iterator();
        //
        //            // RootGraph rg = Cytoscape.getRootGraph ();
        //            HyperEdgeImpl he;
        //
        //            while (it.hasNext()) {
        //                he = (HyperEdgeImpl) (it.next());
        //                // he.removeUnderlyingConnectorNode();
        //                // HEUtils.removeNode((it.next()).getConnectorNode());
        //            }
        //
        //            //            Object[] edges = _all_edges.toArray ();
        //            //            CyEdge     edge;
        //            //            for (int i = 0; i < edges.length; i++)
        //            //            {
        //            //                edge = (CyEdge) edges[i];
        //            //                Cytoscape.getRootGraph ().removeEdge (edge);
        //            //            }
        //            _uuid_to_he_map.clear();
        //            _all_hyper_edges.clear();
        //            _all_edges.clear();
        //            _all_nodes.clear();
        //            // _he_to_nets_map.clear();
        //            _net_to_hes_map.clear();
        //            _node_to_hes_map.clear();
        //            _node_to_edges_map.clear();
        //            // MLC 08/11/06:
        //            // _eit_to_hes_map.clear();
        //            _cn_to_he_map.clear();
        //            _net_to_edges_map.clear();
        //            _net_to_nodes_map.clear();
        //
        //            // TODO: Clean out event handlers?
        //            //	    _delete_listener_store = null;
        //            //	    _change_listener_store = null;
        //            //	    _new_listener_store.clear();
        //        }
        checkAllEmpty();
    }

    //    private void removeGeneratedHyperEdgeAttributes() {
    //        Iterator it = _all_hyper_edges.iterator();
    //
    //        while (it.hasNext()) {
    //            ((HyperEdgeImpl) it.next()).removeGeneratedAttributes();
    //        }
    //    }

    // implements HyperEdgeManager interface:
    public Iterator<HyperEdge> getHyperEdgesByNode(CyNode node, CyNetwork net) {
        if (node == null) {
            if (net == null) {
                return HEUtils.buildUnmodifiableCollectionIterator(_all_hyper_edges);
            } else {
                List<HyperEdge> hes_in_net = _net_to_hes_map.get(net);

                if (hes_in_net == null) {
                    // return Collections.EMPTY_LIST.iterator();
                    return (HEUtils.buildUnmodifiableCollectionIterator(new ArrayList<HyperEdge>(0)));
                } else {
                    return HEUtils.buildUnmodifiableCollectionIterator(hes_in_net);
                }
            }
        }

        // node is not null:
        // all HyperEdges that contain a given CyNode:
        List<HyperEdge> hes_for_node = _node_to_hes_map.get(node);

        if (hes_for_node == null) {
            // no HyperEdges for the given node:
            // return Collections.EMPTY_LIST.iterator();
            return (HEUtils.buildUnmodifiableCollectionIterator(new ArrayList<HyperEdge>(0)));
        }

        if (net == null) {
            // return all HyperEdges:
            return HEUtils.buildUnmodifiableCollectionIterator(hes_for_node);
        }

        // find intersection of CyNetwork HEs and CyNode HEs:
        // HyperEdges contained within a CyNetwork:
        List<HyperEdge> hes_in_net = _net_to_hes_map.get(net);

        if (hes_in_net == null) {
            // no HEs in this CyNetwork:
            // return Collections.EMPTY_LIST.iterator();
            return (HEUtils.buildUnmodifiableCollectionIterator(new ArrayList<HyperEdge>(0)));
        }

        // Now check the intersection of HEs in CyNetwork and ones
        // with the given CyNode:
        // first mark all HEs with a given CyNode, then check if marks are found
        // in each HE in the CyNetwork:
        return HEUtils.buildUnmodifiableCollectionIterator(intersection(hes_for_node,
                                                                        hes_in_net));
    }

    // implements HyperEdgeManager interface:
    public Iterator<HyperEdge> getHyperEdgesByNodes(Collection<CyNode> nodes,
                                                    CyNetwork net) {
        Collection<HyperEdge> start_col_of_hes;

        if (net == null) {
            start_col_of_hes = _all_hyper_edges;
        } else {
            start_col_of_hes = _net_to_hes_map.get(net);
        }

        if (start_col_of_hes == null) {
            // no matches, return empty Iterator:
            // return Collections.EMPTY_LIST.iterator();
            return (HEUtils.buildUnmodifiableCollectionIterator(new ArrayList<HyperEdge>(0)));
        }

        if (nodes == null) {
            return HEUtils.buildUnmodifiableCollectionIterator(start_col_of_hes);
        }

        if (nodes.isEmpty()) {
            // no matches, return empty Iterator:
            // return Collections.EMPTY_LIST.iterator();
            return (HEUtils.buildUnmodifiableCollectionIterator(new ArrayList<HyperEdge>(0)));
        }

        // Now iterate over all the HEs looking for ones that match nodes:
        Iterator<HyperEdge>       all_hes_it = start_col_of_hes.iterator();
        Iterator<?extends CyNode> node_it;
        HyperEdge                 he;
        boolean                   does_match;
        List<HyperEdge>           matches = new ArrayList<HyperEdge>();

        while (all_hes_it.hasNext()) {
            he         = all_hes_it.next();
            node_it    = nodes.iterator();
            does_match = true;

            while (node_it.hasNext()) {
                if (!he.hasNode((CyNode) node_it.next())) {
                    does_match = false;

                    break;
                }
            }

            if (does_match) {
                matches.add(he);
            }
        }

        return HEUtils.buildUnmodifiableCollectionIterator(matches);
    }

    // implements HyperEdgeManager interface:
    public Iterator<CyEdge> getEdgesByNode(CyNode node, CyNetwork net) {
        if (node == null) {
            if (net == null) {
                return HEUtils.buildUnmodifiableCollectionIterator(_all_edges);
            } else {
                Set<CyEdge> edges_in_net = _net_to_edges_map.get(net);

                if (edges_in_net == null) {
                    // return Collections.EMPTY_LIST.iterator();
                    return (HEUtils.buildUnmodifiableCollectionIterator(new ArrayList<CyEdge>(0)));
                } else {
                    return HEUtils.buildUnmodifiableCollectionIterator(edges_in_net);
                }
            }
        }

        // node is not null:
        // all CyEdges that contain a given CyNode:
        List<CyEdge> edges_for_node = _node_to_edges_map.get(node);

        if (edges_for_node == null) {
            // no CyEdges for the given node:
            // return Collections.EMPTY_LIST.iterator();
            return (HEUtils.buildUnmodifiableCollectionIterator(new ArrayList<CyEdge>(0)));
        }

        if (net == null) {
            // return all CyEdges:
            return HEUtils.buildUnmodifiableCollectionIterator(edges_for_node);
        }

        // find intersection of CyNetwork CyEdges and CyNode's CyEdges:
        // HyperEdges contained within a CyNetwork:
        Set edges_in_net = (Set) _net_to_edges_map.get(net);

        if (edges_in_net == null) {
            // no CyEdges in this CyNetwork:
            // return Collections.EMPTY_LIST.iterator();
            return (HEUtils.buildUnmodifiableCollectionIterator(new ArrayList<CyEdge>(0)));
        }

        List<CyEdge>     matches           = new ArrayList<CyEdge>();
        Iterator<CyEdge> edges_for_node_it = edges_for_node.iterator();
        CyEdge           to_match;

        while (edges_for_node_it.hasNext()) {
            to_match = edges_for_node_it.next();

            if (edges_in_net.contains(to_match)) {
                matches.add(to_match);
            }
        }

        return HEUtils.buildUnmodifiableCollectionIterator(matches);
    }

    // implements HyperEdgeManager interface:
    public Iterator<HyperEdge> getHyperEdgesByEdgeTypes(Collection<String> edgeITypes,
                                                        CyNetwork          net) {
        Collection<HyperEdge> start_col_of_hes;

        if (net == null) {
            start_col_of_hes = _all_hyper_edges;
        } else {
            start_col_of_hes = _net_to_hes_map.get(net);
        }

        if (start_col_of_hes == null) {
            // no matches, return empty Iterator:
            // return Collections.EMPTY_LIST.iterator();
            return (HEUtils.buildUnmodifiableCollectionIterator(new ArrayList<HyperEdge>(0)));
        }

        if (edgeITypes == null) {
            return HEUtils.buildUnmodifiableCollectionIterator(start_col_of_hes);
        }

        if (edgeITypes.isEmpty()) {
            // no matches, return empty Iterator:
            // return Collections.EMPTY_LIST.iterator();
            return (HEUtils.buildUnmodifiableCollectionIterator(new ArrayList<HyperEdge>(0)));
        }

        // Now iterate over all the HEs looking for ones that match edge types:
        Iterator        hes_it        = start_col_of_hes.iterator();
        Iterator        edge_types_it;
        HyperEdge       he;
        boolean         does_match;
        List<HyperEdge> matches = new ArrayList<HyperEdge>();

        while (hes_it.hasNext()) {
            he            = (HyperEdge) hes_it.next();
            edge_types_it = edgeITypes.iterator();
            does_match    = true;

            while (edge_types_it.hasNext()) {
                if (!he.hasEdgeOfType((String) edge_types_it.next())) {
                    does_match = false;

                    break;
                }
            }

            if (does_match) {
                matches.add(he);
            }
        }

        return HEUtils.buildUnmodifiableCollectionIterator(matches);
    }

    // implements HyperEdgeManager interface:
    public Iterator<CyNode> getNodesByEdgeTypes(Collection<String> edgeITypes,
                                                CyNetwork net) {
        return HEUtils.buildUnmodifiableCollectionIterator(primGetNodesByEdgeTypes(edgeITypes,
                                                                                   net));
    }

    private Collection<CyNode> primGetNodesByEdgeTypes(Collection<String> edgeITypes,
                                                       CyNetwork          net) {
        Collection<HyperEdge> startCollectionOfHEs = null;

        if (net == null) {
            // deal with all HyperEdges:
            if (edgeITypes == null) {
                // no filtering:
                return _all_nodes;
            }

            startCollectionOfHEs = _all_hyper_edges;
        } else {
            startCollectionOfHEs = _net_to_hes_map.get(net);
        }

        if (startCollectionOfHEs == null) {
            // no matches, return empty Iterator:
            return (new ArrayList<CyNode>(0));
        }

        // Now go thru each HyperEdge and gather CyNodes that match criteria:
        Set<CyNode> matchNodes = new HashSet<CyNode>();

        for (HyperEdge he : startCollectionOfHEs) {
            for (CyNode node : ((HyperEdgeImpl) he).primGetNodesByEdgeTypes(edgeITypes)) {
                matchNodes.add(node); // will not add duplicates
            }
        }

        return matchNodes;
    }

    //    // implements HyperEdgeManager interface:
    //    public Iterator<CyNode> getNodesByEdgeTypes(Collection<String> edgeITypes,
    //                                              CyNetwork net) {
    //        Collection<CyNode> start_col_of_nodes = null;
    //
    //        if (net == null) {
    //            start_col_of_nodes = _all_nodes;
    //        } else {
    //            RefCountMultiValue<CyNode> refs = _net_to_nodes_map.get(net);
    //
    //            if (refs != null) {
    //                start_col_of_nodes = refs.getValues();
    //            }
    //        }
    //
    //        if (start_col_of_nodes == null) {
    //            // no matches, return empty Iterator:
    //            // return Collections.EMPTY_LIST.iterator();
    //            return (HEUtils.buildUnmodifiableCollectionIterator(
    //                new ArrayList<CyNode>(0)));
    //        }
    //
    //        if (edgeITypes == null) {
    //            return HEUtils.buildUnmodifiableCollectionIterator(start_col_of_nodes);
    //        }
    //
    //        if (edgeITypes.isEmpty()) {
    //            // no matches, return empty Iterator:
    //            // return Collections.EMPTY_LIST.iterator();
    //            return (HEUtils.buildUnmodifiableCollectionIterator(
    //                new ArrayList<CyNode>(0)));
    //        }
    //
    //        // Now get the intersection of all nodes that have edges with the
    //        // given edgeITypes:
    //        // Iterator<CyNode> nodes_it = start_col_of_nodes.iterator();
    //        // CyNode           node;
    //        List<CyNode> matches       = new ArrayList<CyNode>();
    //        List<CyEdge> edges_of_node;
    //
    //        // Iterator<CyEdge> edges_of_node_it;
    //        Set<CyEdge>    net_edges;
    //        List<String> required_e_types;
    //
    //        // CyEdge           pos_match;
    //        int match_idx;
    //
    //        if (net == null) {
    //            net_edges = _all_edges;
    //        } else {
    //            net_edges = _net_to_edges_map.get(net);
    //        }
    //
    //        for (CyNode node : start_col_of_nodes) {
    //            // while (nodes_it.hasNext()) {
    //            // node             = (CyNode) nodes_it.next();
    //            // check that all the edges of this node. Ensure there is an edge
    //            // for each edgeIType and these edges must be connected to node and
    //            // be a member of NET:
    //            required_e_types = new ArrayList<String>(edgeITypes);
    //            edges_of_node    = _node_to_edges_map.get(node);
    //
    //            // edges_of_node_it = edges_of_node.iterator();
    //            for (CyEdge pos_match : edges_of_node) {
    //                // while (edges_of_node_it.hasNext()) {
    //                // pos_match = edges_of_node_it.next();
    //                match_idx = required_e_types.indexOf(
    //                    HyperEdgeImpl.getEdgeInteractionType(pos_match));
    //
    //                if (match_idx < 0) {
    //                    continue;
    //                }
    //
    //                if (net_edges.contains(pos_match)) {
    //                    // we have a match, remove from required_e_types, if still there:
    //                    required_e_types.remove(match_idx);
    //
    //                    if (required_e_types.isEmpty()) {
    //                        // we're done, we found a match:
    //                        matches.add(node);
    //
    //                        break;
    //                    }
    //                }
    //            }
    //        }
    //
    //        return HEUtils.buildUnmodifiableCollectionIterator(matches);
    //    }

    // implements HyperEdgeManager interface:
    public Iterator<HyperEdge> getHyperEdgesByNetwork(CyNetwork net) {
        if (net == null) {
            return HEUtils.buildUnmodifiableCollectionIterator(_all_hyper_edges);
        } else {
            List<HyperEdge> hes = _net_to_hes_map.get(net);

            if (hes == null) {
                // no matches:
                // return Collections.EMPTY_LIST.iterator();
                return (HEUtils.buildUnmodifiableCollectionIterator(new ArrayList<HyperEdge>(0)));
            } else {
                return HEUtils.buildUnmodifiableCollectionIterator(hes);
            }
        }
    }

    // implements HyperEdgeManager interface:
    public Iterator<CyEdge> getEdgesByNetwork(CyNetwork net) {
        if (net == null) {
            return HEUtils.buildUnmodifiableCollectionIterator(_all_edges);
        } else {
            Set<CyEdge> edges = _net_to_edges_map.get(net);

            if (edges == null) {
                // no matches:
                // return Collections.EMPTY_LIST.iterator();
                return (HEUtils.buildUnmodifiableCollectionIterator(new ArrayList<CyEdge>(0)));
            } else {
                return HEUtils.buildUnmodifiableCollectionIterator(edges);
            }
        }
    }

    // implements HyperEdgeManager interface:
    public int getNumHyperEdges(CyNetwork net) {
        if (net == null) {
            return _all_hyper_edges.size();
        } else {
            List<HyperEdge> hes = _net_to_hes_map.get(net);

            if (hes != null) {
                return hes.size();
            }

            return 0;
        }
    }

    // implements HyperEdgeManager interface:
    public int getNumNodes(CyNetwork net) {
        if (net == null) {
            return _all_nodes.size();
        } else {
            // get the HyperEdges and the CyNodes in each HyperEdge and count:
            return primGetNodesByEdgeTypes(null, net).size();
        }
    }

    // implements HyperEdgeManager interface:
    public int getNumEdges(CyNetwork net) {
        if (net == null) {
            return _all_edges.size();
        } else {
            Set<CyEdge> edges = _net_to_edges_map.get(net);

            if (edges != null) {
                return edges.size();
            }

            return 0;
        }
    }

    // implements HyperEdgeManager interface:
    public boolean inHyperEdge(CyNode node, CyNetwork net) {
        if (node == null) {
            return false;
        }

        // All HyperEdges containing a given node:
        List<HyperEdge> hes_for_node = _node_to_hes_map.get(node);

        if (hes_for_node == null) {
            return false;
        }

        if (net == null) {
            // at least one match was found:
            return true;
        }

        for (HyperEdge he : hes_for_node) {
            if (he.inNetwork(net)) {
                return true;
            }
        }

        return false;
    }

    // implements HyperEdgeManager interface:
    public boolean isConnectorNode(CyNode node, CyNetwork net) {
        if (node == null) {
            return false;
        }

        // All HyperEdges containing a given node:
        HyperEdge he_with_cn = _cn_to_he_map.get(node);

        if (he_with_cn == null) {
            return false;
        }

        if (net == null) {
            return true;
        } else {
            return (he_with_cn.inNetwork(net));
        }
    }

    // implements HyperEdgeManager interface:
    public boolean isHyperEdgeEdge(CyEdge edge, CyNetwork net) {
        if (edge == null) {
            return false;
        }

        String id = edge.getIdentifier();

        if (id == null) {
            return false;
        }

        String edgeAttVal = Cytoscape.getEdgeAttributes()
                                     .getStringAttribute(id,
                                                         HyperEdgeImpl.HYPEREDGE_EDGE_TAG_NAME);

        if (!("true".equals(edgeAttVal))) {
            return false;
        }

        if (net == null) {
            return true;
        }

        // check hyperedge is in net:
        return ((isConnectorNode((CyNode) edge.getSource(), net)) ||
               (isConnectorNode((CyNode) edge.getTarget(), net)));
    }

    // implements HyperEdgeManager interface:
    public HyperEdge getHyperEdgeForConnectorNode(CyNode connectorNode) {
        return _cn_to_he_map.get(connectorNode);
    }

    //    // implements HyperEdgeManager interface:
    //    public Iterator getCyNetworks(HyperEdge hedge) {
    //        if (hedge == null) {
    //            // no matches:
    //            return Collections.EMPTY_LIST.iterator();
    //        }
    //
    //        List nets = (List) _he_to_nets_map.get(hedge);
    //
    //        if (nets == null) {
    //            // no matches:
    //            return Collections.EMPTY_LIST.iterator();
    //        } else {
    //            return Collections.unmodifiableList(nets).iterator();
    //        }
    //    }

    // implements HyperEdgeManager interface:
    public String getHyperEdgeVersion() {
        return VERSION;
    }

    // implements HyperEdgeManager interface:
    public Double getHyperEdgeVersionNumber() {
        return VERSION_NUMBER;
    }

    // implements HyperEdgeManager interface:
    public boolean addChangeListener(ChangeListener l) {
        _change_listener_store = ListenerList.setupListenerListWhenNecessary(_change_listener_store);

        return _change_listener_store.addListener(l);
    }

    // implements HyperEdgeManager interface:
    public boolean removeChangeListener(ChangeListener l) {
        if (_change_listener_store != null) {
            return _change_listener_store.removeListener(l);
        }

        return false;
    }

    // implements HyperEdgeManager interface:
    public boolean addNewObjectListener(NewObjectListener l) {
        return _new_listener_store.addListener(l);
    }

    // implements HyperEdgeManager interface:
    public boolean removeNewObjectListener(NewObjectListener l) {
        return _new_listener_store.removeListener(l);
    }

    // implements HyperEdgeManager interface:
    public boolean addDeleteListener(DeleteListener l) {
        _delete_listener_store = ListenerList.setupListenerListWhenNecessary(_delete_listener_store);

        return _delete_listener_store.addListener(l);
    }

    // implements HyperEdgeManager interface:
    public boolean removeDeleteListener(DeleteListener l) {
        if (_delete_listener_store != null) {
            return _delete_listener_store.removeListener(l);
        }

        return false;
    }

    protected void fireDeleteEvent(HyperEdge he) {
        if ((_delete_listener_store != null) &&
            (_delete_listener_store.hasListeners())) {
            // Now call all the listeners:
            Iterator it = _delete_listener_store.iterator();

            synchronized (_delete_listener_store) {
                while (it.hasNext()) {
                    ((DeleteListener) it.next()).objectDestroyed(he);
                }
            }
        }
    }

    /**
     * A hack needed to handle deletion of ConnectorNodes.  Will hide
     * all ConnectorNodes on the internal hidden ConnectorNode list
     * for a given Cynetwork.
     * If (when) HyperEdges are added to the Cytoscape core, this can
     * be removed.
     * See details on this in How Do We Keep HyperEdge Structures Up-To_Date?
     * in http://cytoscape.org/cgi-bin/moin.cgi/HyperEdgeUpdating.
     */
    public void hideConnectorNodes(CyNetwork net) {
        Set<CyNode> nodesForNet = _delayedHidingMap.get(net);

        if (nodesForNet != null) {
            for (CyNode node : nodesForNet) {
                // HEUtils.log("DELAYED REMOVED: " + HEUtils.toString(node));
                removeUnderlyingNode(node, net);
            }
        }
    }

    // MLC 05/11/07 BEGIN:
    /**
     * A hack needed to handle deletion of ConnectorNodes.  Will hide
     * all ConnectorNodes on the internal hidden ConnectorNode list for
     * all CyNetworks.
     * If (when) HyperEdges are added to the Cytoscape core, this can
     * be removed.
     * See details on this in How Do We Keep HyperEdge Structures Up-To_Date?
     * in http://cytoscape.org/cgi-bin/moin.cgi/HyperEdgeUpdating.
     */
    public void hideConnectorNodes() {
	Set<CyNetwork> nets = _delayedHidingMap.keySet();
	for (CyNetwork net : nets) {
	    hideConnectorNodes (net);
	}
    }
    // MLC 05/11/07 END.

    private void addToDelayedHidingMap(CyNetwork net, CyNode node) {
        Set<CyNode> nodesForNet = _delayedHidingMap.get(net);

        if (nodesForNet == null) {
            nodesForNet = new HashSet<CyNode>();
            _delayedHidingMap.put(net, nodesForNet);
        }

        nodesForNet.add(node);
    }

    //    // implements HyperEdgeManager interface:
    //    public int load(String uri, CyNetwork net, Format format) {
    //        if (format == Format.XML) {
    //            Document doc = HEXMLUtils.readDocument(uri, false);
    //
    //            if (doc == null) {
    //                return 0;
    //            }
    //
    //            //            Element hyperedges_ele = HEXMLUtils.getOneDocSubElementByTagName (doc,
    //            //                                                                              "HyperEdges",
    //            //                                                                              "HyperEdgeManagerImpl.load()");
    //            // return XMLPersist.parseHyperEdges (hyperedges_ele, net);
    //            return XMLPersist.parseHyperEdges(doc, net);
    //        } else {
    //            String msg = "We currently can only load using Format.XML format.";
    //            HEUtils.errorLog(msg);
    //
    //            return -1;
    //        }
    //    }
    //
    //    // returns the number of HyperEdges saved.
    //    // implements HyperEdgeManager interface:
    //    public int save(String uri_str, CyNetwork net, Format format) {
    //        if (format == Format.XML) {
    //            return XMLPersist.saveHyperEdgesAsXML(uri_str, net);
    //        } else {
    //            String msg = "We currently can only save using Format.XML format.";
    //            HEUtils.errorLog(msg);
    //
    //            return -1;
    //        }
    //    }

    // ASSUME: net and hedge are non-null
    // ASSUME: hedge doesn't belong to net already.
    protected void addToCyNetwork(CyNetwork net, HyperEdge hedge) {
        //        List nets = MapUtils.ensureListValueForMap(_he_to_nets_map, hedge);
        //        if (nets.contains(net)) {
        //            return false;
        //        }
        //
        //        nets.add(net);
        List<HyperEdge> hes = MapUtils.ensureListValueForMap(_net_to_hes_map,
                                                             net);
        hes.add(hedge);
        // Now add the CyNodes and CyEdges:
        // edges returned are not shared:
        MapUtils.addSetValuesToMap(_net_to_edges_map,
                                   net,
                                   hedge.getEdges(null));
        //        // nodes returned may be shared, but may duplicate previous
        //        // nodes in the map:
        //        MapUtils.addValuesToRefCountMap(_net_to_nodes_map,
        //                                        net,
        //                                        hedge.getNodes(null));
        addNodesAndEdgesToNet(net, hedge);
    }

    // ASSUME: net and hedge are non-null
    // ASSUME: hedge belongs to net.
    protected void removeFromNetwork(CyNetwork net, HyperEdge hedge,
                                     BookkeepingItem bkItem) {
        //        // check that we are really gonna remove stuff before firing the
        //        // event:
        //        List value_list = (List) _he_to_nets_map.get(hedge);
        //
        //        if ((value_list == null) || (!value_list.contains(net))) {
        //            return false;
        //        }
        //
        //        // we have a value to remove, fire events:
        //        if (_change_listener_store != null) {
        //            List event_args = new ArrayList(2);
        //            event_args.add(net);
        //            event_args.add(hedge);
        //            fireChangeEvent(EventNote.Type.HYPEREDGE,
        //                EventNote.SubType.REMOVED, event_args);
        //        }
        //
        //        value_list.remove(net);
        //
        //        if (value_list.isEmpty()) {
        //            _he_to_nets_map.remove(hedge);
        //        }
        MapUtils.removeCollectionValueFromMap(_net_to_hes_map, net, hedge);

        MapUtils.removeCollectionValuesFromMap(_net_to_edges_map,
                                               net,
                                               hedge.getEdges(null));
        //        MapUtils.removeValuesFromRefCountMap(_net_to_nodes_map,
        //                                             net,
        //                                             hedge.getNodes(null));

        //        // We've removed *one* reference to each CyNode in the hedge in
        //        // this net, but there may be more then one. Find CyNodes with
        //        // multiple references and update the reference counts by
        //        // building a new list that has an element that corresponds to
        //        // the number of edges each CyNode has, minus 1. Then feed an
        //        // iterator of this list into removeValuesFromRefCountMap:
        //        Iterator<CyNode> uniqueNodes   = hedge.getNodes(null);
        //        CyNode           node;
        //        List<CyEdge>     edgeList;
        //        HyperEdgeImpl  hedgeImpl     = (HyperEdgeImpl) hedge;
        //        List<CyNode>     multipleNodes = new ArrayList<CyNode>();
        //
        //        while (uniqueNodes.hasNext()) {
        //            node     = uniqueNodes.next();
        //            edgeList = hedgeImpl.primGetEdges(node);
        //
        //            if (edgeList.size() > 1) {
        //                for (int x = 0; x < (edgeList.size() - 1); x++) {
        //                    multipleNodes.add(node);
        //                }
        //            }
        //        }
        //
        //        MapUtils.removeValuesFromRefCountMap(_net_to_nodes_map,
        //                                             net,
        //                                             multipleNodes.iterator());
        removeEdgesFromNet(net, hedge);
        removeConnectorNodeFromNet(net, hedge, bkItem);
    }

    private void addNodesAndEdgesToNet(CyNetwork targetNet, HyperEdge he) {
        targetNet.restoreNode(he.getConnectorNode());

        Iterator<CyNode> nodeIt = he.getNodes(null);

        // ASSUME: If node is already in net, it will not be added again:
        while (nodeIt.hasNext()) {
            addNodeToCyNetwork(nodeIt.next(),
                               targetNet);
            // targetNet.restoreNode(nodeIt.next());
        }

        Iterator<CyEdge> edgeIt = he.getEdges(null);

        // ASSUME: If subedge is already in net, it will not be added again:
        while (edgeIt.hasNext()) {
            addEdgeToCyNetwork(edgeIt.next(),
                               targetNet);
        }
    }

    private void addEdgeToCyNetwork(CyEdge edge, CyNetwork net) {
        // ASSUME: If edge is already in net, it will not be added again:
        //        HEUtils.log("really adding edge " + edge.getIdentifier() + " to net " +
        //            ((CyNetwork) net).getTitle());
        net.addEdge(edge);
        // net.restoreEdge(edge);
    }

    private void addNodeToCyNetwork(CyNode node, CyNetwork net) {
        // ASSUME: If node is already in net, it will not be added again:
        net.addNode(node);
        // net.restoreNode(node);
    }

    private void removeEdgesFromNet(CyNetwork net, HyperEdge he) {
        // net.hideNode(he.getConnectorNode());
        Iterator<CyEdge> edgeIt = he.getEdges(null);

        //        // Create a collection of all the HyperEdges since we may be
        //        // deleting the HyperEdges, the Iterator would otherwise get a
        //        // ConcurrentModificationException (not sure why in this case, but does):
        // Collection<CyEdge> edges = HEUtils.createCollection(edgeIt);
        //        for (CyEdge edge : edges) {
        while (edgeIt.hasNext()) {
            removeUnderlyingEdge(edgeIt.next(),
                                 net);
            // net.removeEdge(Cytoscape.getRootGraph().getIndex(edgeIt.next()),
            //               false);
        }
    }

    private void removeRegularNodeAttributes(CyNode node) {
	// MLC 06/21/07 BEGIN:
        // Cytoscape.getNodeAttributes().deleteAttribute(node.getIdentifier(),
	//                                               HyperEdgeImpl.ENTITY_TYPE_ATTRIBUTE_NAME);
        HEUtils.deleteAttribute(Cytoscape.getNodeAttributes(),
				node.getIdentifier(),
				HyperEdgeImpl.ENTITY_TYPE_ATTRIBUTE_NAME);
	// MLC 06/21/07 END.
    }

    protected void removeUnderlyingNode(CyNode node, CyNetwork net) {
        int nodeIdx = Cytoscape.getRootGraph().getIndex(node);

        synchronized (LOCAL_REMOVAL_LOCK) {
            _internalRemoval = true;
        }

        // TODO: change set_remove=true if removeNode() is made
        // clear that it works on one network or across networks:
        net.removeNode(nodeIdx, false);

        synchronized (LOCAL_REMOVAL_LOCK) {
            _internalRemoval = false;
        }
    }

    protected void removeUnderlyingEdge(CyEdge edge, CyNetwork net) {
        int edgeIdx = Cytoscape.getRootGraph().getIndex(edge);

        synchronized (LOCAL_REMOVAL_LOCK) {
            _internalRemoval = true;
        }

        // TODO: change set_remove=true if removeEdge() is made
        // clear that it works on one network or across networks:
        // HEUtils.log("REMOVED " + HEUtils.toString(edge));
        net.removeEdge(edgeIdx, false);

        synchronized (LOCAL_REMOVAL_LOCK) {
            _internalRemoval = false;
        }
    }

    // ASSUME: Edges will be removed before this is called.
    private void removeConnectorNodeFromNet(CyNetwork net, HyperEdge he,
                                            BookkeepingItem bkItem) {
        removeNodeFromNet(he.getConnectorNode(),
                          net,
                          bkItem);
        //        CyNode cn = he.getConnectorNode();
        //        // If nothing is left connected to node, then remove it:
        //        // if (net.getDegree(cn) == 0) {
        //
        //        if ((net.getDegree(cn) == 0) &&
        //            ((bkItem == null) || (bkItem.getNetwork() != net) ||
        //            (!bkItem.getItems().contains (cn)))) {
        //            removeUnderlyingNode(cn, net);
        //            // net.removeNode(Cytoscape.getRootGraph().getIndex(cn),
        //            //               false);
        //        }
    }

    protected void removeNodeFromNet(CyNode node, CyNetwork net,
                                     BookkeepingItem bkItem) {
        int edgesLeft = net.getDegree(node);

        if ((edgesLeft > 0) && (bkItem != null)) {
            // see if edges are a member of bkItem and weren't deleted yet:
            int[] adjacentEdges = net.getAdjacentEdgeIndicesArray(net.getIndex(node),
                                                                  true,
                                                                  true,
                                                                  true);

            for (int edgeIdx : adjacentEdges) {
                if (bkItem.getItems().contains((CyEdge) net.getEdge(edgeIdx))) {
                    // this edge will be removed later (or is being removed):
                    edgesLeft--;
                }
            }
        }

        if (edgesLeft == 0) {
            // MLC 12/27/06 BEGIN:
            // we possibly delete this node:
            if (bkItem == null) {
                // directly remove--we aren't responding to
                // deletion events:
                // HEUtils.log("REMOVED: " + HEUtils.toString(node));
                removeUnderlyingNode(node, net);
            } else// we are bookkeeping--responding to Cytoscape
            // deletion events:
            // ignore all nodes on the ignore list:
            if ((bkItem.getNetwork() != net) ||
                (!bkItem.getItems().contains(node))) {
                addToDelayedHidingMap(net, node);
            }

            //	if ((bkItem == null) || (bkItem.getNetwork() != net) ||
            //            (!bkItem.getItems().contains(node)))) {
            //            removeUnderlyingNode(node, net);
            //        }
            // MLC 12/27/06 END.
        }
    }

    protected static HyperEdgeManager getHyperEdgeManager() {
        return INSTANCE;
    }

    protected void registerHyperEdge(HyperEdge he) {
        addHyperEdgeUUIDAssoc(he);
        _all_hyper_edges.add(he);
        _cn_to_he_map.put(he.getConnectorNode(),
                          he);
    }

    protected void fireNewHObjEvent(HyperEdge he) {
        if (_new_listener_store.hasListeners()) {
            List list = _new_listener_store.getListeners();

            // Now call all the listeners:
            Iterator it = list.iterator();

            while (it.hasNext()) {
                ((NewObjectListener) it.next()).objectCreated(he);
            }
        }
    }

    // ASSUME: edge must be fully filled in before calling this method.
    // ASSUME: he may not have been registered yet (using registerHyperEdge).
    protected void registerEdge(CyEdge edge, HyperEdge he, CyNetwork net) {
        _all_edges.add(edge);

        // CyNode node = edge.getSource ();
        CyNode node = ((HyperEdgeImpl) he).primGetNode(edge);
        _all_nodes.add(node); // add node, when needed.

        MapUtils.addListValueToMap(_node_to_hes_map, node, he, true);
        MapUtils.addListValueToMap(_node_to_edges_map, node, edge, true);

        // MLC 08/11/06:
        if (net == null) {
            // Get the CyNetworks node is in via the ones HE is in
            // and add net to node info:
            for (CyNetwork aNet : ((HyperEdgeImpl) he).primGetNetworks()) {
                //                MapUtils.addValueToRefCountMap(_net_to_nodes_map, aNet, node);
                MapUtils.addSetValueToMap(_net_to_edges_map, aNet, edge);
                addEdgeToCyNetwork(edge, aNet);
                addNodeToCyNetwork(node, aNet);

                // }
            }
        } else {
            //            MapUtils.addValueToRefCountMap(_net_to_nodes_map, net, node);
            MapUtils.addSetValueToMap(_net_to_edges_map, net, edge);
            addEdgeToCyNetwork(edge, net);
            addNodeToCyNetwork(node, net);
        }
    }

    protected void unregisterHyperEdge(HyperEdge he) {
        for (CyNetwork net : ((HyperEdgeImpl) he).primGetNetworks()) {
            MapUtils.removeCollectionValueFromMap(_net_to_hes_map, net, he);

            //}
        }

        if (_cn_to_he_map.remove(he.getConnectorNode()) == null) {
            HEUtils.log("unregisterHyperEdge: ERROR: Connector wasn't found!");
        }

        // _he_to_nets_map.remove(he);
        _all_hyper_edges.remove(he);
        removeHyperEdgeUUIDAssoc(he);
    }

    /**
     * ASSUME: 'he' has not yet been unregistered.
     * ASSUME: The HyperEdge containing this CyEdge has not yet been modified
     *         to remove this CyEdge.
     * only removed info for this edge.
     */
    protected void unregisterEdge(CyEdge edge, HyperEdge he) {
        removeNodeInfoWhenNeeded(edge, he);
        // remove from all CyNetworks:
        _all_edges.remove(edge);

        // find the CyNetworks that this edge belongs to and
        // remove from them:
        for (CyNetwork net : ((HyperEdgeImpl) he).primGetNetworks()) {
            MapUtils.removeCollectionValueFromMap(_net_to_edges_map, net, edge);
        }
    }

    // update all node-related maps and sets as is appropriate.
    private void removeNodeInfoWhenNeeded(CyEdge edge, HyperEdge he) {
        // get CyNode to check for removal:
        CyNode node = ((HyperEdgeImpl) he).primGetNode(edge);

        if (!he.hasMultipleEdges(node)) {
            // The node only has one edge connected to it.
            // This hyperedge (he) will have node removed from it.
            // remove this node from this HyperEdge:
            MapUtils.removeCollectionValueFromMap(_node_to_hes_map, node, he);

            // There's a chance the node is no longer a member of any HyperEdges:
            List hes_containing_node = (List) _node_to_hes_map.get(node);

            if (hes_containing_node == null) {
                // there are no other HyperEdge references to node.
                // remove completely:
                _all_nodes.remove(node);

                // node is no longer a regular node of any HyperEdge. However,
                // it may be a ConnectorNode of a HyperEdge (edge was a
                // shared edge). In this case, don't delete the attributes:
                if (!isConnectorNode(node, null)) {
                    removeRegularNodeAttributes(node);
                }
            } else {
                // There are other HyperEdges containing node.
            }

            //            // removing from all CyNetworks:
            //            // More than one CyNetwork may contain the same
            //            // HyperEdge.  Track them down and remove:
            //            for (CyNetwork net : ((HyperEdgeImpl) he).primGetCyNetworks()) {
            //                MapUtils.removeValueFromRefCountMap(_net_to_nodes_map, net, node);
            //            }
        } else {
            // 'he' has another reference to node:
            // Nothing to do but _node_to_edges_map (below).
        }

        //        // removing count from all CyNetworks:
        //        // More than one CyNetwork may contain the same
        //        // HyperEdge.
        //        for (CyNetwork net : ((HyperEdgeImpl) he).primGetCyNetworks()) {
        //            MapUtils.removeValueFromRefCountMap(_net_to_nodes_map, net, node);
        //        }
        MapUtils.removeCollectionValueFromMap(_node_to_edges_map, node, edge);
    }

    /**
     * Remove a UUID-HyperEdge association. Returns true if
     * an association was found and removed.
     */
    private boolean removeHyperEdgeUUIDAssoc(HyperEdge he) {
        return (_uuid_to_he_map.remove(he.getIdentifier()) != null);
    }

    /**
    * Associates a UUID with a HyperEdge.
    * @param he the HyperEdge to retrieve with the given key--must be
    * non-null.  The UUID of the HyperEdge is used as a key--must be
    * non-null.
    */
    private void addHyperEdgeUUIDAssoc(HyperEdge he) {
        String uuid = he.getIdentifier();

        if (uuid == null) {
            throw new HEStructuralIntegrityException("Found a NULL UUID!");
        }

        Object obj = _uuid_to_he_map.put(uuid, he);

        if (obj != null) {
            String                         msg = "attempting to add a HyperEdge with the same UUID key!";
            HEStructuralIntegrityException e = new HEStructuralIntegrityException(msg);
            e.printStackTrace();
            throw e;
        }
    }

    private void checkAllEmpty() {
        checkIsEmpty(_uuid_to_he_map, "_uuid_to_he_map");
        checkIsEmpty(_all_hyper_edges, "_all_hyper_edges");
        checkIsEmpty(_all_edges, "_all_edges");
        checkIsEmpty(_all_nodes, "_all_nodes");
        // checkIsEmpty(_he_to_nets_map, "_he_to_nets_map");
        checkIsEmpty(_net_to_hes_map, "_net_to_hes_map");
        checkIsEmpty(_node_to_hes_map, "_node_to_hes_map");
        checkIsEmpty(_node_to_edges_map, "_node_to_hes_map");
        // MLC 08/11/06:
        //        checkIsEmpty(_eit_to_hes_map, "_eit_to_hes_map");
        checkIsEmpty(_cn_to_he_map, "_cn_to_he_map");
        checkIsEmpty(_net_to_edges_map, "_net_to_edges_map");
        //        checkIsEmpty(_net_to_nodes_map, "_net_to_nodes_map");
    }

    private void checkIsEmpty(Map map, String msg) {
        if (!map.isEmpty()) {
            StringBuilder sb    = new StringBuilder();
            Map.Entry     entry;
            Iterator      mapIt = map.entrySet().iterator();

            while (mapIt.hasNext()) {
                entry = (Map.Entry) mapIt.next();
                sb.append("\nkey: ");
                sb.append(HEUtils.toString(entry.getKey()));
                sb.append(" value: ");
                sb.append(HEUtils.toString(entry.getValue()));
            }

            throw new HEStructuralIntegrityException(msg +
                                                     " was found to have " +
                                                     map.size() +
                                                     " elements when it should be empty! Key-values are:" +
                                                     sb.toString());
        }
    }

    private void checkIsEmpty(Set set, String msg) {
        if (!set.isEmpty()) {
            StringBuilder sb = new StringBuilder();

            for (Object ele : set) {
                sb.append("\nelement: ");

                sb.append(HEUtils.toString(ele));
            }

            throw new HEStructuralIntegrityException(msg +
                                                     " was found to have " +
                                                     set.size() +
                                                     " elements when it should be empty!" +
                                                     sb.toString());
        }
    }

    /**
     * Return a non-null List of the HyperEdges that intersect two given lists.
     * This method is thread-safe.
     * @param a a List of HyperEdgeImpls.
     * @param b a List of HyperEdgeImpls.
     * @return the empty list if a or b is null, or if no intersections are found.
     *         Otherwise, return the list of intersecting HyperEdges.
     */
    static public List<HyperEdge> intersection(List<HyperEdge> a,
                                               List<HyperEdge> b) {
        if ((a == null) || (b == null)) {
            return new ArrayList<HyperEdge>(0);
        }

        List<HyperEdge> results = new ArrayList<HyperEdge>();

        synchronized (INTERSECTION_LOCK) {
            Iterator<HyperEdge> a_it = a.iterator();

            while (a_it.hasNext()) {
                ((HyperEdgeImpl) a_it.next()).setMarked(true);
            }

            HyperEdgeImpl       ho;
            Iterator<HyperEdge> b_it = b.iterator();

            while (b_it.hasNext()) {
                ho = ((HyperEdgeImpl) b_it.next());

                if (ho.isMarked()) {
                    results.add(ho);
                }
            }

            // reset marks:
            a_it = a.iterator();

            while (a_it.hasNext()) {
                ((HyperEdgeImpl) a_it.next()).setMarked(false);
            }
        }

        return results;
    }

    //    public boolean isAnyDirty ()
    //    {
    //        return _any_dirty;
    //    }
    //    public boolean setAnyDirtyClean ()
    //    {
    //        boolean ret_val = (_any_dirty != false);
    //        setAnyDirty (false);
    //        return ret_val;
    //    }
    //    protected void setAnyDirty (boolean dirty)
    //    {
    //        if (_any_dirty != dirty)
    //        {
    //            _any_dirty = dirty;
    //            processAnyDirtyObjsEvent (dirty);
    //        }
    //    }
    //
    //    protected void processAnyDirtyObjsEvent (boolean dirty)
    //    {
    //        if ((_any_dirty_listener_store != null) &&
    //            (_any_dirty_listener_store.hasListeners ()))
    //        {
    //            List list = _any_dirty_listener_store.getListeners ();
    //
    //            // Now call all the listeners:
    //            Iterator it = list.iterator ();
    //            while (it.hasNext ())
    //            {
    //                ((AnyDirtyListener) it.next ()).dirtyHyperGraphStateChanged (dirty);
    //            }
    //        }
    //    }
    //
    //    public boolean addAnyDirtyListener (AnyDirtyListener l)
    //    {
    //        return _any_dirty_listener_store.addListener (l);
    //    }
    //
    //    public boolean removeAnyDirtyListener (AnyDirtyListener l)
    //    {
    //        return _any_dirty_listener_store.removeListener (l);
    //    }
    protected void fireChangeEvent(HyperEdge he, EventNote.Type type,
                                   EventNote.SubType sub_type,
                                   Object supporting_info) {
        if ((_change_listener_store == null) ||
            (he.getState() != LifeState.NORMAL)) {
            return;
        }

        // fire event
        EventNote en = new EventNote(he, type, sub_type, supporting_info);

        if (_change_listener_store.hasListeners()) {
            List list = _change_listener_store.getListeners();

            // Now call all the listeners:
            Iterator it = list.iterator();

            while (it.hasNext()) {
                ((ChangeListener) it.next()).objectChanged(en);
            }
        }
    }

    //    // Lookup HyperEdge in case it has been seen before
    //    // even though right now, they should never be forward referenced.
    //    public HyperEdge getHyperEdge(String uuid) {
    //        HyperEdge he = (HyperEdge) _uuid_to_he_map.get(uuid);
    //
    //        if (he != null) {
    //            return he;
    //        }
    //
    //        return ((HyperEdgeFactoryImpl) _factory).createRestoredHyperEdge(uuid);
    //    }

    // Lookup HyperEdge in case it has been seen before
    public HyperEdge findHyperEdge(String uuid) {
        return (HyperEdge) _uuid_to_he_map.get(uuid);
    }

    // handle persistence across sessions
    private class HESessionLoadedUpdater extends HENetworkLoadedUpdater {
        // override propertyChange:
        public void propertyChange(PropertyChangeEvent e) {
            handleLoadedSession((List) e.getNewValue());
        }

        private void handleLoadedSession(List networks) {
            if (networks == null) {
                return;
            }

            Iterator  netIt = networks.iterator();
            CyNetwork net;

            while (netIt.hasNext()) {
                String netID = (String) netIt.next();
                net = (CyNetwork) Cytoscape.getNetwork(netID);
                handleLoadedNetwork(net);
            }
        }
    }

    // handle persistence across networks (e.g., HGMML).
    private class HENetworkLoadedUpdater implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent e) {
            Object[] info = (Object[]) e.getNewValue();

            // Note that if a network is not saved with attributes,
            // the handleLoadedNetwork will do nothing.
            if (info != null) {
                handleLoadedNetwork((CyNetwork) info[0]);
            }
        }

        protected void handleLoadedNetwork(CyNetwork net) {
            if (net == null) {
                return;
            }

            handleReloadedSharedHyperEdges(net);

            // find all connector nodes:
            Iterator nodesIt = net.nodesIterator();
            CyNode   node;

            while (nodesIt.hasNext()) {
                node = (CyNode) nodesIt.next();
                HyperEdgeImpl.reconstructIfConnectorNode(node, net);
            }

            // monitorNetworkForDeletions (net);
        }

        // HyperEdges with shared edges can only exist in one
        // CyNetwork.  Search this newly loaded CyNetwork and look for
        // HyperEdge ConnectorNodes that belong to an existing
        // HyperEdge that has shared edges.  If we find such a
        // ConnectorNode, it must be that we've reloaded a network
        // (either the exact same, or a different version of the
        // network). This is because a shared HyperEdge can only exist
        // in at most one Network and since it already exists, we have
        // a problem. In this case for this CyNetwork, we copy this
        // ConnectorNode and delete the original from this
        // CyNetwork. We can then proceed with normal reconstruction--
        // a brand new HyperEdge will be created based on finding this
        // new (copy) ConnectorNode.
        private void handleReloadedSharedHyperEdges(CyNetwork net) {
            // find all connector nodes:
            // since we may be adding new nodes, thereby invalidating the iterator,
            // copy nodes into a list:
            Collection<CyNode> nodes = HEUtils.createCollection((Iterator<CyNode>) net.nodesIterator());

            for (CyNode node : nodes) {
                replaceUnderlyingSharedConnectorNodes(node, net);
            }
        }

        // Take an existing connector node and clone it
        // within a given CyNetwork. This new connector node will
        // represent a copied HyperEdge.
        // This is used without references to HyperEdges, on the
        // underlying Cytoscape CyNodes and CyEdges during reconstruction
        // of HyperEdges when a Cytoscape Session or CyNetwork is read in.
        private void replaceUnderlyingSharedConnectorNodes(CyNode cn,
                                                           CyNetwork net) {
            HyperEdge he = getHyperEdgeForConnectorNode(cn);

            if ((he == null) || (!he.hasSharedEdges())) {
                return;
            }

            // cn represents a HyperEdge with shared edges that
            // already exists in another network. Copy cn in net:
	    // MLC 01/15/07:
	    //            HEUtils.log("Found Reloaded Shared HyperEdge with Connector Node " +
	    //                        HEUtils.toString(cn));

            CyNode cnCopy = HEUtils.createConnectorNode(HyperEdgeImpl.createConnectorNodeUUIDWithoutHE());
            // we don't need purge=true because copyNode is new:
            CyAttributesUtils.copyAttributes(cn.getIdentifier(),
                                             cnCopy.getIdentifier(),
                                             Cytoscape.getNodeAttributes(),
                                             false);
            addNodeToCyNetwork(cnCopy, net);
	    // MLC 01/15/07:
            // HEUtils.log("cloned " + HEUtils.toString(cn) + " ==> " +
            //            HEUtils.toString(cnCopy));

            // now copy all the edges and their attributes:
            int[]        adjacentEdges = net.getAdjacentEdgeIndicesArray(net.getIndex(cn),
                                                                         true,
                                                                         true,
                                                                         true);
            CyEdge       edge;
            CyNode       source;
            CyNode       target;
            CyAttributes edgeAttrs = Cytoscape.getEdgeAttributes();

            // will copy all Edges including user added edges:
            for (int edgeIdx : adjacentEdges) {
                edge = (CyEdge) net.getEdge(edgeIdx);

                // add an equivalent edge to the cnCopy:
                // ASSUME: All edges use Semantics.INTERACTION for their type:
                String itype = edgeAttrs.getStringAttribute(edge.getIdentifier(),
                                                            Semantics.INTERACTION);

                if (itype != null) {
                    source = (CyNode) edge.getSource();
                    target = (CyNode) edge.getTarget();

                    if (source == cn) {
                        source = cnCopy;
                    }

                    if (target == cn) {
                        target = cnCopy;
                    }

		    // MLC 01/15/07:
                    // HEUtils.log("Copying edge " + HEUtils.toString(edge));

                    CyEdge newEdge = HEUtils.createEdge(source, target, itype);
                    // we don't need purge=true because copyNode is new:
                    CyAttributesUtils.copyAttributes(edge.getIdentifier(),
                                                     newEdge.getIdentifier(),
                                                     edgeAttrs,
                                                     false);
                    addEdgeToCyNetwork(newEdge, net);
		    // MLC 01/15/07:
                    // HEUtils.log("cloned " + HEUtils.toString(edge) + " ==> " +
                    //            HEUtils.toString(newEdge));
                }
            }

            // This is never a bookkeeping operation--directly
            // remove the node:
            // now remove cn and its edges from net:
	    // MLC 01/15/07:
            // HEUtils.log("REMOVED: " + HEUtils.toString(cn));
            removeUnderlyingNode(cn, net);
        }
    }

    // Monitor all created Networks so we can update any HyperEdges
    // found in these networks when deletions occur.
    private class HENetworkCreatedUpdater implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent e) {
            String    network_name = (String) e.getNewValue();
            CyNetwork net = (CyNetwork) Cytoscape.getNetwork(network_name);
            // MLC 01/15/07:
            // HEUtils.log("NETWORK CREATED " + network_name);
            net.addGraphPerspectiveChangeListener(gosUpdater);
        }
    }

    // Monitor all destroyed Networks so we can
    // update any HyperEdges that were found in these networks.
    private class HENetworkDestroyedUpdater implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent e) {
            String    network_name = (String) e.getNewValue();
            CyNetwork net = (CyNetwork) Cytoscape.getNetwork(network_name);
//          MLC 01/15/07:
            // HEUtils.log("NETWORK DESTROYED " + network_name);

            // remove edges updater since our updates for
            // removing HyperEdges from net may trigger node and
            // edge removals from the HyperEdge. Also, the
            // network is being destroyed, so good cleanup practice:
            net.removeGraphPerspectiveChangeListener(gosUpdater);
            // check all the edges in net to see if they belong
            // to HyperEdges and update the HyperEdges if needed:
            removeHyperEdgesFromNetwork(net);
        }

        private void removeHyperEdgesFromNetwork(CyNetwork net) {
            Iterator<HyperEdge> heIt = getHyperEdgesByNetwork(net);

            // Create a collection of all the HyperEdges since we may be
            // deleting the HyperEdges, the Iterator would otherwise get a
            // ConcurrentModificationException:
            Collection<HyperEdge> hes = HEUtils.createCollection(heIt);

            for (HyperEdge he : hes) {
		// MLC 01/15/07:
                // HEUtils.log("removed " + HEUtils.toString(he));
                he.removeFromNetwork(net);
            }
        }
    }

    // MLC 12/18/07 BEGIN:
    // We use network modification as a convenient
    // time to clean off ConnectorNodes on the delayed hiding map.
    // We have no easy way to test if the modification was a deletion,
    // so we just do it for all modifications. This implies
    // hideConnectorNodes() must be an efficient operation.
    private class HENetworkModifiedUpdater implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent e) {
            CyNetwork net = (CyNetwork)e.getNewValue(); 
            // HEUtils.log("NETWORK MODIFIED " + net.getIdentifier());
	    hideConnectorNodes(net);
        }
    }

    // MLC 12/18/07 END.


    // Handle removal of CyEdges from CyNetworks:
    private class GraphObjsHiddenUpdater
        implements GraphPerspectiveChangeListener {
        // The source, target or both could be ConnectorNodes ( when
        // edge links two HyperEdges).
        private CyNode getConnectorNode(CyEdge edge, CyNetwork net) {
            if (isConnectorNode((CyNode) edge.getSource(), net)) {
                return (CyNode) edge.getSource();
            }

            if (isConnectorNode((CyNode) edge.getTarget(), net)) {
                return (CyNode) edge.getTarget();
            }

            return null;
        }

        // Update HyperEdge structures to keep in sync with changes to their
        // underlying Cytoscape structures. For example, if an Edge is removed
        // in Cytoscape, endure the edge is removed from the corresponding HyperEdges.
        // This is a complicated update mechanism. For details on its design, see
        // the Delayed Node Deletion solution in the deletion-issues.txt document.
        //
        // NOTE: This updating will not be necessary if (when) HyperEdge is placed
        //       in the Cytscape core.
        // ASSUME: Node callbacks occur before the Edges to the node are
        //         deleted.
        // ASSUME: It is ok to "rehide"--possibly call hide on already hidden nodes
        //         or edges. But, it ISN'T ok to recursively hide--delete a
        //         node or edge while we are in the middle of deleting it.
        public void graphPerspectiveChanged(GraphPerspectiveChangeEvent e) {
            // TODO: This doesn't handle restoring of hidden nodes!
            // DON'T respond to internal deletions that are part of
            // HyperEdge workings. 
            synchronized (LOCAL_REMOVAL_LOCK) {
                if (_internalRemoval) {
                    return;
                }
            }

            if ((!e.isNodesHiddenType()) && (!e.isEdgesHiddenType())) {
                // nothing to concern ourselves with:
                return;
            }

            // build a set of all nodes and edges NOT to delete because
            // they will be deleted by Cytoscape:
            Set<GraphObject> toIgnoreForDeletion = new HashSet<GraphObject>();

            if (e.isNodesHiddenType()) {
                for (int nodeIdx : e.getHiddenNodeIndices()) {
                    toIgnoreForDeletion.add(Cytoscape.getRootGraph()
                                                     .getNode(nodeIdx));
                }
            }

            if (e.isEdgesHiddenType()) {
                for (int edgeIdx : e.getHiddenEdgeIndices()) {
                    toIgnoreForDeletion.add(Cytoscape.getRootGraph()
                                                     .getEdge(edgeIdx));
                }
            }

            // DeletedCatcher deletedCatcher = new DeletedCatcher();
            // handleHiddenNodes(e, deletedCatcher, toIgnoreForDeletion);
            // handleHiddenEdges(e, deletedCatcher, toIgnoreForDeletion);
            handleHiddenNodes(e, toIgnoreForDeletion);
            handleHiddenEdges(e, toIgnoreForDeletion);
        }

        private void handleHiddenNodes(GraphPerspectiveChangeEvent e,
                                       // DeletedCatcher deletedCatcher,
        Set<GraphObject> toIgnoreForDeletion) {
            if (!e.isNodesHiddenType()) {
                return;
            }

            int[]     hiddenNodes = e.getHiddenNodeIndices();
            CyNetwork net = (CyNetwork) e.getSource();

            if (hiddenNodes == null) {
                return;
            }

            for (int nodeIdx : hiddenNodes) {
                {
                    CyNode node = (CyNode) Cytoscape.getRootGraph()
                                                    .getNode(nodeIdx);

                    // if (deletedCatcher.contains(node)) {
                    //    continue;
                    // }
                    HyperEdge he = getHyperEdgeForConnectorNode(node);

                    if (he == null) {
                        continue;
                    }

                    // HEUtils.log("CONNECTOR NODE HIDDEN = " +
                    //            HEUtils.toString(node));
                    // // if any other objects are deleted during bookkeeping
                    // // remember them to avoid them in the future:
                    // net.addGraphPerspectiveChangeListener(deletedCatcher);
                    ((HyperEdgeImpl) he).removeFromNetworkBookkeeping(net,
                                                                      toIgnoreForDeletion);
                    // net.removeGraphPerspectiveChangeListener(deletedCatcher);
                }
            }
        }

        private void handleHiddenEdges(GraphPerspectiveChangeEvent e,
                                       // DeletedCatcher deletedCatcher,
        Set<GraphObject> toIgnoreForDeletion) {
            if (!e.isEdgesHiddenType()) {
                return;
            }

            int[]     hiddenEdges = e.getHiddenEdgeIndices();
            CyNetwork net = (CyNetwork) e.getSource();

            if (hiddenEdges == null) {
                return;
            }

	    // MLC 01/15/07:
            // HEUtils.log("EDGES HIDDEN EVENT");

            for (int edgeIdx : hiddenEdges) {
                CyEdge edge = (CyEdge) Cytoscape.getRootGraph().getEdge(edgeIdx);

                // if (deletedCatcher.contains(edge)) {
                //    continue;
                // }
                if (!isHyperEdgeEdge(edge, null)) {
                    continue;
                }

                // Now get the HyperEdge containing this
                // CyEdge (if any) and update it:
                HyperEdge he = getHyperEdgeForConnectorNode(getConnectorNode(edge,
                                                                             net));

                if (he == null) {
                    continue;
                }

                // If edge is shared across two HyperEdges, the removal
                // from one, will remove it from the other:
		// MLC 01/15/07:
                // HEUtils.log("Removing CyEdge " + edge.getIdentifier());
                // // if any other objects are deleted during bookkeeping
                // // remember them to avoid them in the future:
                // net.addGraphPerspectiveChangeListener(deletedCatcher);
                // since we are in the middle of deleting this edge,
                // just remove bookkeeping info about it:
                ((HyperEdgeImpl) he).removeEdgeBookkeeping(edge, net,
                                                           toIgnoreForDeletion);
                // net.removeGraphPerspectiveChangeListener(deletedCatcher);
            }
        }

        //        private class DeletedCatcher implements GraphPerspectiveChangeListener {
        //            private Set<GraphObject> _deleted = new HashSet<GraphObject>();
        //
        //            public boolean contains(GraphObject go) {
        //                return _deleted.contains(go);
        //            }
        //
        //            public void graphPerspectiveChanged(GraphPerspectiveChangeEvent e) {
        //                if (e.isNodesHiddenType()) {
        //                    int[] hiddenNodes = e.getHiddenNodeIndices();
        //
        //                    if (hiddenNodes != null) {
        //                        for (int nodeIdx : hiddenNodes) {
        //                            CyNode node = (CyNode) Cytoscape.getRootGraph()
        //                                                            .getNode(nodeIdx);
        //                            HEUtils.log("DeletedCatcher: adding node " +
        //                                        HEUtils.toString(node));
        //                            _deleted.add(node);
        //                            // _deleted.add(Cytoscape.getRootGraph()
        //                            //                      .getNode(nodeIdx));
        //                        }
        //                    }
        //                }
        //
        //                if (e.isEdgesHiddenType()) {
        //                    int[] hiddenEdges = e.getHiddenEdgeIndices();
        //
        //                    if (hiddenEdges != null) {
        //                        for (int edgeIdx : hiddenEdges) {
        //                            CyEdge edge = (CyEdge) Cytoscape.getRootGraph()
        //                                                            .getEdge(edgeIdx);
        //                            _deleted.add(edge);
        //                            HEUtils.log("DeletedCatcher: adding edge " +
        //                                        HEUtils.toString(edge));
        //
        //                            // _deleted.add(Cytoscape.getRootGraph()
        //                            //                      .getNode(edgeIdx));
        //                        }
        //                    }
        //                }
        //            }
        //        }
    }

    //    public Object setAttributeValue (HyperObj obj,
    //                                     String   attribute,
    //                                     Object   value)
    //    {
    //	// NOTE: It would be nice to make this more OO, based on 'obj', but
    //	//       it is not the Cytoscape way:
    //	if (obj instanceof HyperEdge)
    //	    {
    //		return _hedge_attr_data.setAttributeValue (obj.getIdentifier(),
    //							    attribute, value);
    //	    }
    //	else
    //	    {
    //		// if obj is null or another type:
    //		return null;
    //	    }
    //    }
    //    public Object getAttributeValue (HyperObj obj,
    //                                     String   attribute)
    //    {
    //	// NOTE: It would be nice to make this more OO, based on 'obj', but
    //	//       it is not the Cytoscape way:
    //	if (obj instanceof HyperEdge)
    //	    {
    //		return _hedge_attr_data.getAttributeValue (obj.getIdentifier(),
    //							    attribute);
    //	    }
    //	else
    //	    {
    //		// if obj is null or another type:
    //		return null;
    //	    }
    //    }
    //    public CytoscapeData getHyperEdgeData ()
    //    {
    //        return _hedge_attr_data;
    //    }
}
