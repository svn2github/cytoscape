/* -*-Java-*-
********************************************************************************
*
* File:         HyperEdgeImpl.java
* RCS:          $Header: /cvs/cvsroot/lstl-lsi/HyperEdge/src/cytoscape/hyperedge/impl/HyperEdgeImpl.java,v 1.1 2007/07/04 01:11:35 creech Exp $
* Description:
* Author:       Michael L. Creech
* Created:      Wed Sep 14 13:05:17 2005
* Modified:     Thu Jun 21 05:24:07 2007 (Michael L. Creech) creech@w235krbza760
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
* Thu Jun 21 05:23:32 2007 (Michael L. Creech) creech@w235krbza760
*  Changed use of CyAttributes.deleteAttribute()->HEUtils.deleteAttribute()
*  due to Cytoscape bug.
* Tue Jan 16 09:03:57 2007 (Michael L. Creech) creech@w235krbza760
*  Commented out some debugging statements.
* Thu Dec 28 06:51:26 2006 (Michael L. Creech) creech@w235krbza760
*  Commented out some debugging statements.
* Fri Dec 15 10:14:01 2006 (Michael L. Creech) creech@w235krbza760
*  Added use of BookkeepingItem in attempted fix when GraphObjects
*  are hidden. Will be removed in future.
* Wed Nov 29 05:39:35 2006 (Michael L. Creech) creech@w235krbza760
*  Changed addToCyNetwork()-->addToNetwork(),
*  getCyNetwork()-->getNetwork(), inCyNetwork()-->inNetwork(), and
*  removeFromCyNetwork()-->removeFromNetwork().
* Tue Nov 28 16:06:51 2006 (Michael L. Creech) creech@w235krbza760
*  Fixed bug with removal of shared Edges.
* Wed Nov 15 16:12:14 2006 (Michael L. Creech) creech@w235krbza760
*  Added getNode() and primGetNode().
* Tue Nov 14 07:53:21 2006 (Michael L. Creech) creech@w235krbza760
*  Added isSharedEdge() and primIsSharedEdge().
* Sat Nov 11 07:29:30 2006 (Michael L. Creech) creech@w235krbza760
*  Fixed bug in generating edge interaction attribute names.
* Fri Nov 10 16:18:51 2006 (Michael L. Creech) creech@w235krbza760
*  Added primHasSharedEdges(), hasSharedEdges(), primGetSharedEdges()
*  and getSharedEdges().
* Tue Nov 07 07:27:30 2006 (Michael L. Creech) creech@w235krbza760
*  Changed use of Edge-->CyEdge.
* Mon Nov 06 09:04:37 2006 (Michael L. Creech) creech@w235krbza760
*  Changed GraphPerspective-->CyNetwork, Node-->CyNode
* Thu Nov 02 05:18:40 2006 (Michael L. Creech) creech@w235krbza760
* Changed overall API and implementation so that HyperEdges are shared
* across GraphPerspectives--change made to HyperEdge affects all
* GraphPerspectives.
* Wed Nov 01 15:40:17 2006 (Michael L. Creech) creech@w235krbza760
*  Changed all unmodifiable iterators created to use
*  HEUtils.buildUnmodifiableCollectionIterator().
* Mon Sep 11 08:20:08 2006 (Michael L. Creech) creech@w235krbza760
*  Fixed bug in getNodesByEdgeTypes() where an empty iterator would
*  be returned when the GrapPerspective and edgeITypes is non-null.
*  Changed HyperEdgeImpl(Collection...) to HyperEdgeImpl(List...).
* Sat Sep 09 10:48:12 2006 (Michael L. Creech) creech@w235krbza760
*  Changed createConnectorNode() to remove CANONICAL_NAME attribute on
*  connector nodes.
* Sun Aug 20 19:32:30 2006 (Michael L. Creech) creech@w235krbza760
*  Converted to Java 5 with strongly types collections.
* Sat Aug 12 07:19:56 2006 (Michael L. Creech) creech@w235krbza760
*  Changed setDirected(), isDirected(), setName(), getName() to use connector node attribute
*  versus instance variable.
* Sat May 27 11:28:03 2006 (Michael L. Creech) creech@Dill
*  Changed use of Semantics.LABEL-->AttributeConstants.MONIKER.
* Wed May 24 15:21:43 2006 (Michael L. Creech) creech@Dill
*  Changed references to Semantics.CANONICAL_NAME-->Semantics.LABEL, commented out
*  references to Semantics.COMMON_NAME.
* Tue Oct 25 19:22:52 2005 (Michael L. Creech) creech@Dill
*  Updated to use new Cytoscape CyAttributes.
* Mon Oct 03 18:03:38 2005 (Michael L. Creech) creech@Dill
*  Added setting of IS_CONNECTOR_NODE_ATTRIBUTE_NAME attribute in createConnectorNode ().
********************************************************************************
*/
package cytoscape.hyperedge.impl;

import com.agilent.labs.lsiutils.AttributeConstants;
import com.agilent.labs.lsiutils.Validate;

import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;

import cytoscape.data.CyAttributes;
import cytoscape.data.CyAttributesUtils;
import cytoscape.data.Semantics;
import cytoscape.hyperedge.EdgeFilter;
import cytoscape.hyperedge.HyperEdge;
import cytoscape.hyperedge.HyperEdgeFactory;
import cytoscape.hyperedge.HyperEdgeManager;
import cytoscape.hyperedge.LifeState;
import cytoscape.hyperedge.SimMatcher;
import cytoscape.hyperedge.EdgeTypeMap.EdgeRole;
import cytoscape.hyperedge.event.EventNote;
import cytoscape.hyperedge.impl.utils.HEUtils;
import cytoscape.hyperedge.impl.utils.MapUtils;

import giny.model.GraphObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


// FUTURE NEED: Refactor so that all delete operations just delete the
//              underlying Cytoscape nodes and edges and let the event
//              callbacks in the HyperEdgeManager handle all
//              bookkeeping for removing from the HyperEdge. This
//              cleans things up since this bookkeeping is needed
//              anyway when Nodes and Edges are deleted in Cytoscape.

/**
 * Implementation of the HyperEdge interface.
 *
 * <H4>Unmodifiable Iterator Return Values</H4>
 *
 * Several HyperEdge operations return unmodifiable iterators as
 * return values (e.g., HyperEdge.getEdges()).  Creation of all these
 * iterators is done using {@link
 * HEUtils#buildUnmodifiableCollectionIterator
 * HEUtils.buildUnmodifiableCollectionIterator()}. These iterators
 * have certain properties that should be understood. Please see this
 * method for details.
 *
 * @author Michael L. Creech
 * @version 1.1
 */
public class HyperEdgeImpl implements HyperEdge {
    /**
     * Attribute name used to tell if a CyNode is a ConnectorNode.
     */
    public static final String IS_CONNECTOR_NODE_ATTRIBUTE_NAME = "HyperEdge.isConnectorNode";
    public static final String ENTITY_TYPE_ATTRIBUTE_NAME       = "HyperEdge.EntityType";
    public static final String ENTITY_TYPE_CONNECTOR_NODE_VALUE = "ConnectorNode";

    // May be used in future:
    public static final String ENTITY_TYPE_REGULAR_NODE_VALUE = "RegularNode";
    public static final String DIRECTED_ATTRIBUTE_NAME    = "HyperEdge.isDirected";
    public static final String CONNECTOR_NODE_UUID_PREFIX = "connector-node";

    // public static final String HYPEREDGE_EDGE_UUID_PREFIX = "hyper-edge";
    public static final String HYPEREDGE_EDGE_TAG_NAME = "HyperEdge.isEdge";

    // This is equivalent to cytoscape.data.Semantics.CANONICAL_NAME, but we don't use it 
    // since it is deprecated:
    public static final String LABEL_ATTRIBUTE_NAME = "canonicalName";

    //    // A copy() is performed after basic construction of a HyperEdge which
    //    // adds HyperEdge-based attributes to the CyEdges. This is a
    //    // list of the attributes to not copy, so we don't overwrite them:
    //    public static final AttributeFilter EDGE_COPY_IGNORE_ATTRIBUTES_FILTER;
    //
    //    static {
    //        List<String> edge_copy_ignore_attributes = new ArrayList<String>(3);
    //        // edge_copy_ignore_attributes.add(Semantics.INTERACTION);
    //        edge_copy_ignore_attributes.add(AttributeConstants.MONIKER);
    //        edge_copy_ignore_attributes.add(LABEL_ATTRIBUTE_NAME);
    //        EDGE_COPY_IGNORE_ATTRIBUTES_FILTER = new HEUtils.AttributeIgnoreFilter(edge_copy_ignore_attributes);
    //    }
    private transient static HyperEdgeManagerImpl _manager = (HyperEdgeManagerImpl) HyperEdgeFactory.INSTANCE.getHyperEdgeManager();

    //    private transient static RootGraph            _root_graph = Cytoscape.getRootGraph ();
    private transient static CyAttributes _edge_attrs = Cytoscape.getEdgeAttributes();
    private transient static CyAttributes _node_attrs = Cytoscape.getNodeAttributes();

    //    // Use to reduce String creation when retrieving attributes:
    //    private transient String _edgeInteractionAttributeName;

    //    // the List of all CyNodes contained in this HyperEdge. This may include
    //    // duplicates (e.g., (A,B,A).
    //    // the order of the nodes is important--it must correspond to the order
    //    // of the roles:
    //    private List _nodes          = new ArrayList();
    //    private Set  _distinct_nodes = new HashSet(11);
    private CyNode _connector_node;

    // the List of all CyEdges contained in this HyperEdge. This is a set--
    // it never inludes duplicates. This is used for faster access to the
    // edges then using the map:
    private Set<CyEdge> _edges = new HashSet<CyEdge>();

    // map nodes to a List of unique CyEdges. For homodimer-like structures,
    // such as nodes (A,B,A) with edges (e1,e2,e3), 'A' would map to 
    // (e1,e3):
    private transient Map<CyNode, List<CyEdge>> _node_to_edges_map = new HashMap<CyNode, List<CyEdge>>();

    //    // map GraphPerpective to a List of edges that are the members of this HyperEdge
    //    // that belong to that CyNetwork. The list can be different for
    //    // each HyperEdge. ASSUME: if no edges are in a CyNetwork, then the
    //    // HyperEdge is not in the CyNetwork.
    //    private transient Map<CyNetwork, List<CyEdge>> _net_to_edges_map = new HashMap<CyNetwork, List<CyEdge>>();

    // The set of CyNetworks to which this HyperEdge belongs:
    private transient Set<CyNetwork> _nets = new HashSet<CyNetwork>();

    //    private transient ListenerList _change_listener_store;
    private transient LifeState _state;

    // private transient boolean _dirty;

    // private transient ListenerList _dirty_listener_store;

    // Used for temporarily marking objects for doing things like very
    // efficient intersection algorithms:
    private transient boolean _marked;

    // private String _name;
    // private boolean _directed;
    private String _uuid;

    /**
     * Copy constructor. Returns a raw, incomplete HyperEdge.
     */
    private HyperEdgeImpl() {
        subclassConstructorSupport(null);
        createConnectorNode();
    }

    /**
     * This constructor is used for reconstructing a HyperEdge
     * from it's underlying CyNode, CyEdge, and attribute representation.
     * This is used when a session or network is read into Cytoscape.
     */
    protected HyperEdgeImpl(CyNode connectorNode, CyNetwork net) {
        // REMEMBER: Don't invoke overridable methods of this object
        //           in this constructor!
        subclassConstructorSupport(createHEUUIDFromConnectorNodeUUID(connectorNode));
        reconstructFromNodesEdgesAndAttributes(connectorNode, net);
        // MLC 08/27/06 BEGIN:
        // constructorSupport(net, false);
        _manager.registerHyperEdge(this);
        // startInCyNetwork(net);
        primSetState(LifeState.NORMAL);
        // MLC 08/27/06 END.
    }

    protected HyperEdgeImpl(CyNode node1, String edgeIType1, CyNode node2,
                            String edgeIType2, CyNetwork net, boolean fireEvents) {
        // REMEMBER: Don't invoke overridable methods of this object
        //           in this constructor!
        HEUtils.notNull(node1, "node1");
        HEUtils.notNull(node2, "node2");
        ensureNotNodeConnector(node1);
        ensureNotNodeConnector(node2);
        HEUtils.notNull(edgeIType1, "edgeIType1");
        HEUtils.notNull(edgeIType2, "edgeIType2");
        HEUtils.notNull(net, "net");

        // everything is ok:
        subclassConstructorSupport(null);
        createConnectorNode();
        primAddEdge(node1, edgeIType1, false);
        primAddEdge(node2, edgeIType2, false);
        constructorSupport(net, fireEvents);
    }

    protected HyperEdgeImpl(CyNode node1, String edgeIType1, CyNode node2,
                            String edgeIType2, CyNode node3, String edgeIType3,
                            CyNetwork net, boolean fireEvents) {
        // REMEMBER: Don't invoke overridable methods of this object
        //           in this constructor!
        HEUtils.notNull(node1, "node1");
        HEUtils.notNull(node2, "node2");
        HEUtils.notNull(node3, "node3");
        ensureNotNodeConnector(node1);
        ensureNotNodeConnector(node2);
        ensureNotNodeConnector(node3);
        HEUtils.notNull(edgeIType1, "edgeIType1");
        HEUtils.notNull(edgeIType2, "edgeIType2");
        HEUtils.notNull(edgeIType3, "edgeIType3");
        HEUtils.notNull(net, "net");

        // everything is ok:
        subclassConstructorSupport(null);
        createConnectorNode();
        primAddEdge(node1, edgeIType1, false);
        primAddEdge(node2, edgeIType2, false);
        primAddEdge(node3, edgeIType3, false);
        constructorSupport(net, fireEvents);
    }

    protected HyperEdgeImpl(CyNode[] nodes, String[] edgeITypes, CyNetwork net,
                            boolean fireEvents) {
        // REMEMBER: Don't invoke overridable methods of this object
        //           in this constructor!
        HEUtils.notNull(net, "net");
        ensureCorrectArguments(nodes, edgeITypes, "Array");
        // everything is ok:
        subclassConstructorSupport(null);
        createConnectorNode();
        addEdges(nodes, edgeITypes);
        constructorSupport(net, fireEvents);
    }

    protected HyperEdgeImpl(List<CyNode> nodes, List<String> edgeITypes,
                            CyNetwork net, boolean fireEvents) {
        // REMEMBER: Don't invoke overridable methods of this object
        //           in this constructor!
        HEUtils.notNull(net, "net");

        CyNode[] node_array = null;

        if (nodes != null) {
            node_array = new CyNode[nodes.size()];
            node_array = (CyNode[]) nodes.toArray(node_array);
        }

        String[] edgeITypes_array = null;

        if (edgeITypes != null) {
            edgeITypes_array = new String[edgeITypes.size()];
            edgeITypes_array = (String[]) edgeITypes.toArray(edgeITypes_array);
        }

        ensureCorrectArguments(node_array, edgeITypes_array, "List");
        // everything is ok:
        subclassConstructorSupport(null);
        createConnectorNode();
        addEdges(node_array, edgeITypes_array);
        constructorSupport(net, fireEvents);
    }

    protected HyperEdgeImpl(Map<CyNode, String> node_edge_type_map,
                            CyNetwork net, boolean fireEvents) {
        // REMEMBER: Don't invoke overridable methods of this object
        //           in this constructor!
        HEUtils.notNull(net, "net");

        String[] edgeITypes_array = null;
        CyNode[] node_array = null;

        if (node_edge_type_map != null) {
            edgeITypes_array = new String[node_edge_type_map.size()];
            node_array       = new CyNode[node_edge_type_map.size()];
            node_array       = (CyNode[]) (node_edge_type_map.keySet()).toArray(node_array);
            edgeITypes_array = (String[]) (node_edge_type_map.values()).toArray(edgeITypes_array);
        }

        ensureCorrectArguments(node_array, edgeITypes_array, "Map");
        // everything is ok:
        subclassConstructorSupport(null);
        createConnectorNode();
        addEdges(node_array, edgeITypes_array);
        constructorSupport(net, fireEvents);
    }

    /*
     * ASSUME: We are never given a ConnectorNode for an
     *         existing HyperEdge that has shared edges (would violate
     *         rule that HyperEdges with shared edges can only exist
     *         in one Cynetwork).
     * Given a CyNetwork and possible connector node, construct
     * a new HyperEdge if no existing HyperEdge exists with this
     * connector node, otherwise add to an existing HyperEdge.
    * @return null if connectorNode is not a connector node.
    */
    static protected HyperEdge reconstructIfConnectorNode(CyNode connectorNode,
                                                          CyNetwork net) {
        // First see if a HyperEdge already exists with this connector node:
        HyperEdge he = _manager.getHyperEdgeForConnectorNode(connectorNode);

        if (he == null) {
            // This HyperEdge doesn't already exist in any form. See if
            // connectorNode is really a connector node at the atomic level
            // and build a new HyperEdge if it is:
            if (!isConnectorNodeForReconstruction(connectorNode, net)) {
                return null;
            }

            // build the new HyperEdge:
            he = new HyperEdgeImpl(connectorNode, net);
        } else {
            // This HyperEdge already exists in another CyNetwork.
            if (he.hasSharedEdges()) {
                // We are reloading a CyNetwork that had a shared Edge in it.
                // If a CyNetwork is reloaded, a new CyNetwork is created with all the
                // same CyNodes and CyEdges. Since a HyperEdge with shared Edges can't exist
                // in multiple CyNetworks, we replace this ConnectorNode with a copy that
                // has a different uuid and remove the original ConnectorNode from
                // this CyNetwork.

                // TODO: FIX: possibly remove all shared Edges from this net.
                HEUtils.throwIllegalStateException("HyperEdgeImpl.reconstructIfConnectorNode(): Attempting to add a HyperEdge with shared edges to more than one CyNetwork!");
            }

            // This HyperEdge already exists in another CyNetwork. Add
            // he to another network (net).
            ((HyperEdgeImpl) he).addCyNetworkToReconstruction(net);
        }

        return he;
    }

    // support ops that need to be done early and may be used by
    // subclasses during construction.
    protected void subclassConstructorSupport(String uuid) {
        // REMEMBER: Don't invoke overridable methods of this object
        //           in this constructor!
        primSetState(LifeState.CREATION_IN_PROGRESS);

        if (uuid == null) {
            _uuid = HEUtils.generateUUID(null);

            // primSetDirty(true, false);
        } else {
            _uuid = uuid;
        }

        //        _edgeInteractionAttributeName = cytoscape.data.Semantics.INTERACTION +
        //                                        '-' + _uuid;
    }

    final protected void constructorSupport(CyNetwork net, boolean fireEvents) {
        // REMEMBER: Don't invoke overridable methods of this object
        //           in this constructor!
        _manager.registerHyperEdge(this);
        startInCyNetwork(net);
        primSetState(LifeState.NORMAL);

        if (fireEvents) {
            _manager.fireNewHObjEvent(this);
        }
    }

    private String primSetName(String new_name) {
        String last_val = getConnectorNodeName();
        setConnectorNodeName(new_name);

        return last_val;
    }

    // implements Matchable interface:

    /**
     * This default implementation simply calls:
     * <PRE>
     *    simMatcher.similarTo (this, he, optArgs);
     * </PRE>
     */
    public boolean isSimilar(SimMatcher simMatcher, HyperEdge he, Object optArgs) {
        if (simMatcher == null) {
            return false;
        }

        return simMatcher.similarTo(this, he, optArgs);
    }

    private void ensureCorrectArguments(CyNode[] nodes, String[] edgeITypes,
                                        String param_type) {
        if (nodes == null) {
            HEUtils.throwIllegalArgumentException("The 'nodes' parameter must be non-null");
        }

        if (edgeITypes == null) {
            HEUtils.throwIllegalArgumentException("The 'edgeITypes' parameter must be non-null");
        }

        if (nodes.length != edgeITypes.length) {
            HEUtils.throwIllegalArgumentException("Number of nodes != number of edgeITypes");
        }

        if (nodes.length < 2) {
            HEUtils.throwIllegalArgumentException("Number of nodes and edgeITypes must be >= 2");
        }

        // check nodes and edgeITypes:
        for (int i = 0; i < nodes.length; i++) {
            if (nodes[i] == null) {
                HEUtils.throwIllegalArgumentException("The node " + param_type +
                                                      " given contains a null entry");
            }

            ensureNotNodeConnector(nodes[i]);

            if (edgeITypes[i] == null) {
                HEUtils.throwIllegalArgumentException("The edgeITypes " +
                                                      param_type +
                                                      " given contains a null entry");
            }
        }
    }

    private void ensureNotNodeConnector(CyNode node) {
        if (_manager.isConnectorNode(node, null)) {
            String msg = "HyperEdgeImpl.ensureNotNodeConnector(): Illegal use of ConnectorNode '" +
                         node.getIdentifier() +
                         "'. Only non-ConnectorNodes may be used here.";
            HEUtils.throwIllegalArgumentException(msg);
        }
    }

    private void addEdges(CyNode[] nodes, String[] edgeITypes) {
        for (int i = 0; i < edgeITypes.length; i++) {
            primAddEdge(nodes[i], edgeITypes[i], false);
        }
    }

    public boolean inNetwork(CyNetwork net) {
        HEUtils.checkAbbyNormal(this);
        HEUtils.notNull(net, "net");

        return primInCyNetwork(net);
    }

    // assumes net is not null
    // (also used by HyperEdgeManager)
    protected boolean primInCyNetwork(CyNetwork net) {
        // return _net_to_edges_map.keySet().contains(net);
        return _nets.contains(net);
    }

    public boolean addToNetwork(CyNetwork net) {
        HEUtils.checkAbbyNormal(this);
        HEUtils.notNull(net, "net");

        if (primInCyNetwork(net) || primHasSharedEdges()) {
            return false;
        }

        _nets.add(net);
        //        // No need to check for duplicates since
        //        // getEdges() will never contain duplicates (false as last param):
        //        MapUtils.addListValuesToMap(_net_to_edges_map,
        //                                    net,
        //                                    primGetEdges(null, sourceNet).iterator(),
        //                                    false);
        _manager.addToCyNetwork(net, this);
        _manager.fireChangeEvent(this, EventNote.Type.HYPEREDGE,
                                 EventNote.SubType.ADDED, net);

        return true;
    }

    public boolean hasSharedEdges() {
        HEUtils.checkAbbyNormal(this);

        return primHasSharedEdges();
    }

    protected boolean primHasSharedEdges() {
        for (CyEdge edge : _edges) {
            if (primIsSharedEdge(edge)) {
                return true;
            }
        }

        return false;
    }

    public Iterator<CyEdge> getSharedEdges() {
        HEUtils.checkAbbyNormal(this);

        return HEUtils.buildUnmodifiableCollectionIterator(primGetSharedEdges());
    }

    protected List<CyEdge> primGetSharedEdges() {
        List<CyEdge> shared = new ArrayList<CyEdge>(0);

        for (CyEdge edge : _edges) {
            if (primIsSharedEdge(edge)) {
                shared.add(edge);
            }
        }

        return shared;
    }

    public boolean isSharedEdge(CyEdge edge) {
        HEUtils.checkAbbyNormal(this);

        return primIsSharedEdge(edge);
    }

    private boolean primIsSharedEdge(CyEdge edge) {
        return (_manager.isConnectorNode(primGetNode(edge),
                                         null));
    }

    private void startInCyNetwork(CyNetwork net) {
        // No need to check for duplicates since _edges will never
        // contain duplicates (false as last param):
        //        MapUtils.addListValuesToMap(_net_to_edges_map, net, _edges.iterator(),
        //                                    false);
        _nets.add(net);
        _manager.addToCyNetwork(net, this);
    }

    public boolean removeFromNetwork(CyNetwork net) {
        HEUtils.checkAbbyNormal(this);

        if ((net != null) && (!primInCyNetwork(net))) {
            return false;
        }

        primRemoveFromNetwork(net, null);

        return true;
    }

    /**
     * This method is similar to removeFromNetwork() but will
     * not actually hide nodes and edges found within a given Set.
     * This is used for Cytoscape node and edge removals that trigger
     * needed updates to HyperEdges.
     * This is because these nodes and edges are in the process of
     * being deleted.
     * NOTE: This method may be removed if (when) HyperEdge is placed
     * in the Cytoscape core. Calls to this method would be replaced
     * with removeFromNetwork().
     */
    protected void removeFromNetworkBookkeeping(CyNetwork net,
                                                Set<GraphObject> toIgnoreForDeletion) {
        toIgnoreForDeletion.add(_connector_node);
        BookkeepingItem bkItem = new BookkeepingItem(toIgnoreForDeletion, net);
        primRemoveFromNetwork(net, bkItem);
    }

    private void primRemoveFromNetwork(CyNetwork net, BookkeepingItem bkItem) {
        if ((net == null) || (_nets.size() < 2)) {
            primDestroy(true, bkItem);

            return;
        }

        // we have a value to remove, fire events:
        _manager.fireChangeEvent(this, EventNote.Type.HYPEREDGE,
                                 EventNote.SubType.REMOVED, net);
        // Must be very careful where this call is located.
        // The edge-node information must still be available at time of call:
        _manager.removeFromNetwork(net, this, bkItem);
        _nets.remove(net);
    }

    // implements HyperEdge interface:
    public Iterator<String> getAllEdgeTypes() {
        HEUtils.checkAbbyNormal(this);

        // in future size to be max number of edge types:
        List<String> e_types = new ArrayList<String>();
        String       e_type;

        for (CyEdge edge : _edges) {
            e_type = HEUtils.getEdgeInteractionType(edge);

            if (!e_types.contains(e_type)) {
                e_types.add(e_type);
            }
        }

        return HEUtils.buildUnmodifiableCollectionIterator(e_types);
        // return Collections.unmodifiableList(e_types).iterator();
    }

    //    public int getNumNodes ()
    //    {
    //        HEUtils.checkAbbyNormal (this);
    //        return _node_to_edges_map.size ();
    //    }

    // implements HyperEdge interface:
    public int getNumNodes() {
        HEUtils.checkAbbyNormal(this);

        return _node_to_edges_map.size();
    }

    // implements HyperEdge interface:
    public int getNumEdges() {
        HEUtils.checkAbbyNormal(this);

        return _edges.size();
    }

    // implements HyperEdge interface:
    public Iterator<CyNode> getNodes(String edgeIType) {
        HEUtils.checkAbbyNormal(this);

        if (edgeIType == null) {
            // return all nodes:
            return HEUtils.buildUnmodifiableCollectionIterator(_node_to_edges_map.keySet());
            //            return Collections.unmodifiableSet(_node_to_edges_map.keySet())
            //                              .iterator();
        }

        List<CyNode> matchNodes = new ArrayList<CyNode>(_edges.size());
        CyNode       cyNode;

        for (CyEdge edge : _edges) {
            if (edgeIType.equals(HEUtils.getEdgeInteractionType(edge))) {
                cyNode = primGetNode(edge);

                if (!matchNodes.contains(cyNode)) {
                    matchNodes.add(cyNode);
                }
            }
        }

        return HEUtils.buildUnmodifiableCollectionIterator(matchNodes);
        // return Collections.unmodifiableList(matchNodes).iterator();
    }

    // implements HyperEdge interface:
    public Iterator<CyNode> getNodesByEdgeTypes(Collection<String> edgeITypes) {
        HEUtils.checkAbbyNormal(this);

        return HEUtils.buildUnmodifiableCollectionIterator(primGetNodesByEdgeTypes(edgeITypes));
    }

    protected Collection<CyNode> primGetNodesByEdgeTypes(Collection<String> edgeITypes) {
        if (edgeITypes == null) {
            // return all nodes:
            return _node_to_edges_map.keySet();
        }

        List<CyNode> matchNodes = new ArrayList<CyNode>(_edges.size());
        CyNode       cyNode;

        for (CyEdge edge : _edges) {
            if (edgeITypes.contains(HEUtils.getEdgeInteractionType(edge))) {
                cyNode = primGetNode(edge);

                if (!matchNodes.contains(cyNode)) {
                    matchNodes.add(cyNode);
                }
            }
        }

        return matchNodes;
    }

    // implements HyperEdge interface:
    public Iterator<CyEdge> getEdges(CyNode node) {
        HEUtils.checkAbbyNormal(this);

        if (node == null) {
            // return Collections.unmodifiableSet(_edges).iterator();
            return HEUtils.buildUnmodifiableCollectionIterator(_edges);
        }

        // return Collections.unmodifiableList(primGetEdges(node)).iterator();
        return HEUtils.buildUnmodifiableCollectionIterator(primGetEdges(node));
    }

    // ASSUME: node is non-null:
    protected List<CyEdge> primGetEdges(CyNode node) {
        List<CyEdge> edges = _node_to_edges_map.get(node);

        if (edges == null) {
            // no match found
            return (new ArrayList<CyEdge>(0));
        }

        return edges;
    }

    // implements HyperEdge interface:
    public CyEdge getAnEdge(CyNode node) {
        HEUtils.checkAbbyNormal(this);

        return primGetAnEdge(node);
    }

    private CyEdge primGetAnEdge(CyNode node) {
        List<CyEdge> edges = _node_to_edges_map.get(node);

        if (edges != null) {
            return edges.get(0);
        }

        return null;
    }

    // implements HyperEdge interface:
    public boolean hasMultipleEdges(CyNode node) {
        HEUtils.checkAbbyNormal(this);

        List<CyEdge> edges = _node_to_edges_map.get(node);

        if (edges == null) {
            return false;
        }

        return (edges.size() > 1);
    }

    // implements HyperEdge interface:
    public CyNode getConnectorNode() {
        HEUtils.checkAbbyNormal(this);

        return _connector_node;
    }

    // implements HyperEdge interface:
    public boolean hasConnectorNode(CyNode node) {
        HEUtils.checkAbbyNormal(this);

        return (_connector_node == node);
    }

    // implements HyperEdge interface:
    public boolean hasNode(CyNode node) {
        HEUtils.checkAbbyNormal(this);

        return (primGetAnEdge(node) != null);
    }

    // implements HyperEdge interface:
    public boolean hasEdge(CyEdge edge) {
        HEUtils.checkAbbyNormal(this);

        return primHasEdge(edge);
    }

    private boolean primHasEdge(CyEdge edge) {
        return _edges.contains(edge);
    }

    // implements HyperEdge interface:
    public boolean hasEdgeOfType(String edgeIType) {
        HEUtils.checkAbbyNormal(this);

        if (edgeIType == null) {
            return false;
        }

        for (CyEdge edge : _edges) {
            if (edgeIType.equals(HEUtils.getEdgeInteractionType(edge))) {
                return true;
            }
        }

        return false;
    }

    // implements HyperEdge interface:
    public boolean removeNode(CyNode node) {
        HEUtils.checkAbbyNormal(this);

        List<CyEdge> edges = _node_to_edges_map.get(node);

        if (edges == null) {
            // node is null or not in this HyperEdge:
            return false;
        }

        // first determine if this whole HyperEdge will really be
        // destroyed vs just removing some edges. This occurs is
        // when there aren't enough edges left in this HyperEdge
        // after the removal of the edge.  If so, avoid
        // removing some CyEdges first--just have one destroy event:
        if ((_edges.size() - edges.size()) < getMinimumNumberEdges()) {
            primDestroy(true, null);
        } else {
            primRemoveNode(node, true, null);
        }

        return true;
    }

    // implements HyperEdge interface:
    public boolean removeEdge(CyEdge edge) {
        HEUtils.checkAbbyNormal(this);

        // Find the one CyNode that is associated with this edge and
        // remove from the map:
        if (!primHasEdge(edge)) {
            return false;
        }

        removeOrDeleteEdge(edge, true, null);

        return true;
    }

    /**
     * This method is similar to removeEdge() but will
     * not actually hide the given edge or any other Nodes
     * and Edges found within a given Set.
     * This is used for Cytoscape node and edge removals that trigger
     * needed updates to HyperEdges.
     * This is because these nodes and edges are in the process of
     * being deleted.
     * NOTE: This method may be removed if (when) HyperEdge is placed
     * in the Cytoscape core. Calls to this method would be replaced
     * with removeEdge().
     */

    protected void removeEdgeBookkeeping(CyEdge edge, CyNetwork net,
                                         Set<GraphObject> toIgnoreForDeletion) {
        removeOrDeleteEdge(edge,
                           true,
                           new BookkeepingItem(toIgnoreForDeletion, net));
    }

    protected void removeOrDeleteEdge(CyEdge edge, boolean fireEvents,
                                      BookkeepingItem bkItem) {
        // first determine if this whole HyperEdge will really be
        // destroyed vs just removing an edge. This occurs is
        // when there aren't enough edges left in this HyperEdge
        // after the removal of the edge.  If so, avoid
        // removing some CyEdges first--just have one destroy event:
        if (_edges.size() <= getMinimumNumberEdges()) {
            primDestroy(true, bkItem);
        } else {
            //        List<CyEdge> edgeList = new ArrayList<CyEdge>(1);
            //        edgeList.add(edge);
            primRemoveEdge(primGetNode(edge),
                           edge,
                           fireEvents,
                           bkItem);
        }
    }

    // implements HyperEdge interface:
    public CyEdge addEdge(CyNode node, String edgeIType) {
        HEUtils.checkAbbyNormal(this);
        Validate.notNull(node, "node");
        Validate.notNull(edgeIType, "edgeIType");

        if (_manager.isConnectorNode(node, null)) {
            HEUtils.throwIllegalArgumentException("HyperEdge.addEdge(): node is ConnectorNode.");
        }

        List<CyEdge> matches = primGetEdges(node);

        for (CyEdge match : matches) {
            if (edgeIType.equals(HEUtils.getEdgeInteractionType(match))) {
                HEUtils.throwIllegalArgumentException("HyperEdge.addEdge(): a CyEdge already exists to node '" +
                                                      node.getIdentifier() +
                                                      "' with edgeIType '" +
                                                      edgeIType + "'.");
            }
        }

        return primAddEdge(node, edgeIType, true);
    }

    // implements HyperEdge interface:
    //    public CyEdge connectHyperEdges(HyperEdge targetHE, String fromEdgeIType,
    //                                    String toEdgeIType) {
    public CyEdge connectHyperEdges(HyperEdge targetHE, String edgeIType) {
        HEUtils.checkAbbyNormal(this);

        if (targetHE == this) {
            HEUtils.throwIllegalArgumentException("HyperEdge.connectHyperEdges(): targetHE can't be this HyperEdge.");
        }

        Validate.notNull(targetHE, "targetHE");
        //        Validate.notNull(fromEdgeIType, "fromEdgeIType");
        //        Validate.notNull(toEdgeIType, "toEdgeIType");
        Validate.notNull(edgeIType, "edgeIType");

        // Make sure HyperEdges exist in the one same CyNetwork:
        CyNetwork           net        = null;
        Iterator<CyNetwork> targetNets = targetHE.getNetworks();

        if (targetNets.hasNext()) {
            net = targetNets.next();

            if (targetNets.hasNext()) {
                // then targetNet has >= 2 nets, error:
                net = null;
            }
        }

        if ((_nets.size() != 1) || (net == null) || (!_nets.contains(net))) {
            HEUtils.throwIllegalArgumentException("HyperEdge.connectHyperEdges(): can't connect" +
                                                  this + " to " + targetHE +
                                                  "because they don't belong to one, and the same, CyNetwork.");
        }

        // Check if edge already exists:
        List<CyEdge> existingEdges = primGetEdges(targetHE.getConnectorNode());

        for (CyEdge cyEdge : existingEdges) {
            //            if (fromEdgeIType.equals(getEdgeInteractionType(cyEdge)) &&
            //                toEdgeIType.equals(
            //                ((HyperEdgeImpl) targetHE).getEdgeInteractionType(cyEdge))) {
            if (edgeIType.equals(HEUtils.getEdgeInteractionType(cyEdge))) {
                //                if (edgeIType.equals(HEUtils.getEdgeInteractionType(cyEdge))) {
                // return the match:
                return cyEdge;
                //                } else {
                //                    HEUtils.throwIllegalStateException("HyperEdgeImpl.connectHyperEdges(): Corrupt shared edge state found, edge " +
                //                                                       HEUtils.toString(cyEdge) +
                //                                                       " was found in HyperEdge " +
                //                                                       this +
                //                                                       " but not in HyperEdge " +
                //                                                       targetHE + "!");
                //                }
            }
        }

        // Everything seems OK, create the CyEdge:
        CyEdge edge = primAddEdge(targetHE.getConnectorNode(),
                                  // fromEdgeIType,
        edgeIType,
                                  true);
        ((HyperEdgeImpl) targetHE).primAddEdge(_connector_node, edge,
                                               // toEdgeIType,
        edgeIType, true);

        return edge;
    }

    // implements HyperEdge interface:
    public Map<HyperEdge, HyperEdge> copy(CyNetwork net, EdgeFilter filter) {
        HEUtils.checkAbbyNormal(this);
        HEUtils.notNull(net, "net");
        HEUtils.notNull(filter, "filter");

        String checkMsg = copyOK(filter,
                                 new HashSet<HyperEdge>());

        if (checkMsg != null) {
            HEUtils.throwIllegalArgumentException(checkMsg);
        }

        // List<CyEdge> sharedEdges = primGetSharedEdges();

        //        // check if copySharedHyperEdges is false and there aren't
        //        // enough remaining edges to make a new HyperEdge
        //        if (!copySharedHyperEdges &&
        //            ((_edges.size() - sharedEdges.size()) < getMinimumNumberEdges())) {
        //            HEUtils.throwIllegalArgumentException(
        //                "HyperEdge.copy(): We can't copy to a new HyperEdge because we will not have enough edges since we are ignoring shared edges (total edges: " +
        //                _edges.size() + " shared edges: " + sharedEdges.size() + ".");
        //        }

        // everything is ok for copying:
        Map<HyperEdge, HyperEdge> copyMap = new HashMap<HyperEdge, HyperEdge>();
        // will side-effect copyMap:
        primCopy(net,
                 filter,
                 copyMap,
                 new HashMap<CyEdge, CyEdge>());

        // Now go thru the copyMap and get the values and fire events for
        // creation of each new HyperEdge. We need to wait til all the
        // copies of all shared HyperEdges are created so we don't have
        // half completed objects during event callbacks.
        Iterator<HyperEdge> newHEs = copyMap.values().iterator();

        while (newHEs.hasNext()) {
            HyperEdge newHE = newHEs.next();
            _manager.fireNewHObjEvent(newHE);
        }

        return copyMap;
    }

    // @param copyMap is a call-by-reference structure, so it will be changed
    //                by the time this method completes.
    // @param sharedEdgesMap a Map with a key being an original shared edge and value
    //                       being the copied shared edge. This is a
    //                       call-by-reference structure, so it may be
    //                       changed by the time this method
    //                       completes.
    // @return a Map where each key is a shared edge of this HyperEdge
    //         and the value is the copy of this shared edge.
    //         Will return null if no shared edges or if copySharedHyperEdges
    //         is false.
    private void primCopy(CyNetwork net, EdgeFilter filter,
                          // boolean copySharedHyperEdges,
    Map<HyperEdge, HyperEdge> copyMap, Map<CyEdge, CyEdge> sharedEdgesMap) {
        List<CyEdge>  sharedEdges = primGetSharedEdges();
        HyperEdgeImpl newHE = new HyperEdgeImpl();
        copyMap.put(this, newHE);

        for (CyEdge edge : _edges) {
            if (!filter.includeEdge(this, edge)) {
                continue;
            }

            if (sharedEdges.contains(edge)) {
                HyperEdgeImpl otherHE = (HyperEdgeImpl) _manager.getHyperEdgeForConnectorNode(primGetNode(edge));

                // This edge connects to another HyperEdge:
                //                if (copySharedHyperEdges) {
                // since the hyperedges to copy are always bidirectional
                // and may also form cycles, we must determine
                // if we've already copied otherHE:
                HyperEdge otherHECopy = copyMap.get(otherHE);

                if (otherHECopy == null) {
                    // we haven't dealt with copying this HyperEdge yet.
                    otherHE.primCopy(net, filter, copyMap, sharedEdgesMap);
                    // Now get copy of otherHE:
                    otherHECopy = copyMap.get(otherHE);
                }

                // otherHECopy should contain shared edge
                // that points at newHE. Now get this edge and
                // share in newHE:
                CyEdge edgeToShare = sharedEdgesMap.get(edge);

                // We want to take this same edge and
                // connect from newHE to otherHECopy.
                // The edgeIType will be the same for newHE as
                // from this HyperEdge:

                // We've copied (or are copying) the HyperEdge
                // specified by otherHE. We have two cases:
                // 1) This is the first link to otherHE in which
                //    case we must create the shared edge 
                // like a regular edge with one endpoint 
                // newHE's ConnectorNode, the other endpoint
                // is otherHECopy.
                // 2) This isn't the first link to otherHE (we
                //    have multiple shared edges to the same
                //    ConnectorNode). In this case, the copied
                //    shared edge already exists--we just want to
                //    add it to newHE:
                if (edgeToShare == null) {
                    CyEdge edgeCopy = copyEdge(newHE,
                                               otherHECopy.getConnectorNode(),
                                               edge);
                    sharedEdgesMap.put(edge, edgeCopy);
                } else {
                    newHE.primAddEdge(otherHECopy.getConnectorNode(),
                                      edgeToShare,
                                      HEUtils.getEdgeInteractionType(edge),
                                      false);
                }

                //                } else {
                //                    // don't copy the edge:
                //                    continue;
                //                }
            } else {
                // we have a regular edge to copy:
                copyEdge(newHE,
                         primGetNode(edge),
                         edge);
            }
        }

        // Now copy any extra "regular" edges and attributes from the original ConnectorNode
        // to the new one:
        copyExtraAttributesAndEdges(_connector_node,
                                    newHE.getConnectorNode(),
                                    net);
        // Don't fire events here because ajoining HyperEdges may not
        // be complete yet:
        newHE.constructorSupport(net, false);
    }

    private String copyOK(EdgeFilter filter, Set<HyperEdge> visitedHEs) {
        if (visitedHEs.contains(this)) {
            // we'd done already, skip:
            return null;
        }

        visitedHEs.add(this);

        int          edgeCount   = 0;
        List<CyEdge> sharedEdges = primGetSharedEdges();

        for (CyEdge edge : _edges) {
            if (filter.includeEdge(this, edge)) {
                edgeCount++;

                if (sharedEdges.contains(edge)) {
                    HyperEdgeImpl otherHE    = (HyperEdgeImpl) _manager.getHyperEdgeForConnectorNode(primGetNode(edge));
                    String        otherHEMsg = otherHE.copyOK(filter, visitedHEs);

                    if (otherHEMsg != null) {
                        return otherHEMsg;
                    }
                }
            }
        }

        if (edgeCount < getMinimumNumberEdges()) {
            return "HyperEdge.copy(): We can't copy to a new HyperEdge because we will not have enough edges for '" +
                   this + "' given filter '" + filter + "'.";
        } else {
            return null;
        }
    }

    //    private CyEdge copySharedEdge(HyperEdgeImpl newHE, CyNode toCopyNode,
    //                                  CyEdge toCopyEdge) {
    //        // CyNode toCopyNode = otherHE.getConnectorNode();
    //        CyEdge copiedEdge = newHE.primAddEdge(toCopyNode,
    //                                              getEdgeInteractionType(toCopyEdge),
    //                                              false);
    //        // Now copy any extra attributes on toCopyEdge to copiedEdge:
    //        HEUtils.copyAttributes(toCopyEdge,
    //			       copiedEdge,
    //                               _edge_attrs,
    //                               EDGE_COPY_IGNORE_ATTRIBUTES_FILTER,
    //                               false); // we don't need to purge because copyNode is new.
    //	// We have one attribute that was copied that we need to
    //	// remove.  The edge Interaction Type of the original edge,
    //	// which is specific to the connector node of the edge and is
    //	// different in the copy, since the copy attaches to a
    //	// different ConnectorNode (copy of original ConnectorNode).
    //	_edge_attrs.deleteAttribute(copiedEdge.getIdentifier(),
    //                                    _edgeInteractionAttributeName);
    //        return copiedEdge;
    //    }
    private CyEdge copyEdge(HyperEdgeImpl newHE, CyNode toCopyNode,
                            CyEdge toCopyEdge) {
        // the other endpoint from this connectorNode:
        // CyNode toCopyNode = primGetNode(toCopyEdge);
        CyEdge copiedEdge = newHE.primAddEdge(toCopyNode,
                                              HEUtils.getEdgeInteractionType(toCopyEdge),
                                              false);

        // Now copy any extra attributes on toCopyEdge to copiedEdge:
        // we don't need to 'purge' = true because copyNode is new.
        CyAttributesUtils.copyAttributes(toCopyEdge.getIdentifier(),
                                         copiedEdge.getIdentifier(),
                                         _edge_attrs,
                                         // EDGE_COPY_IGNORE_ATTRIBUTES_FILTER,
        false);

        //        // We have one attribute that was copied that we need to
        //        // remove.  The edge Interaction Type of the original edge,
        //        // which is specific to the connector node of the edge and is
        //        // different in the copy, since the copy attaches to a
        //        // different ConnectorNode (copy of original ConnectorNode).
        //        _edge_attrs.deleteAttribute(copiedEdge.getIdentifier(),
        //                                    _edgeInteractionAttributeName);
        return copiedEdge;
    }

    private void copyExtraAttributesAndEdges(CyNode originalNode,
                                             CyNode copyNode, CyNetwork net) {
        // copy connectorNode attributes:
        // we don't need purge=true because copyNode is new:
        CyAttributesUtils.copyAttributes(originalNode.getIdentifier(),
                                         copyNode.getIdentifier(),
                                         _node_attrs,
                                         false);

        int[]  adjacentEdges = net.getAdjacentEdgeIndicesArray(net.getIndex(originalNode),
                                                               true,
                                                               true,
                                                               true);
        CyEdge edge;

        for (int edgeIdx : adjacentEdges) {
            edge = (CyEdge) net.getEdge(edgeIdx);

            if (!_manager.isHyperEdgeEdge(edge, null)) {
                // add an equivalent edge to the copyNode:
                String itype = _edge_attrs.getStringAttribute(edge.getIdentifier(),
                                                              Semantics.INTERACTION);

                if (itype != null) {
                    CyEdge newEdge = Cytoscape.getCyEdge(edge.getSource(),
                                                         edge.getTarget(),
                                                         Semantics.INTERACTION,
                                                         itype,
                                                         true,
                                                         edge.isDirected());
                    // we don't need purge=true because copyNode is new:
                    CyAttributesUtils.copyAttributes(edge.getIdentifier(),
                                                     newEdge.getIdentifier(),
                                                     _edge_attrs,
                                                     false);
                }
            }
        }
    }

    // implements HyperEdge interface:
    public boolean isConnected(CyNode node1, CyNode node2) {
        HEUtils.checkAbbyNormal(this);

        return ((primGetAnEdge(node1) != null) &&
               (primGetAnEdge(node2) != null));
    }

    // implements HyperEdge interface:
    public void destroy() {
        HEUtils.checkAbbyNormal(this);
        primDestroy(true, null);
    }

    // implements HyperEdge interface:
    public LifeState getState() {
        return _state;
    }

    // implements HyperEdge interface:
    public boolean isState(LifeState ls) {
        return (getState() == ls);
    }

    // implements HyperEdge interface:
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("[" + HEUtils.getAbrevClassName(this) + '.' + hashCode());
        //        result.append (" edge: '" + _edge + "'");
        //        result.append (" type: '" + _type + "'");
        result.append(" nodes: (" + primGetNumNodes() + ")");
        result.append(" edges: (" + primGetNumEdges() + ")");
        result.append(" name: '" + getName() + "'");
        result.append(" directed: " + isDirected());
        result.append(" UUID: '" + getIdentifier() + "'");
        result.append(" state: " + getState());
        result.append(']');

        return result.toString();
    }

    // implements HyperEdge interface:
    public HyperEdgeManager getHyperEdgeManager() {
        return _manager;
    }

    // implements HyperEdge interface:
    public String getName() {
        HEUtils.checkAbbyNormal(this);

        return getConnectorNodeName();
    }

    // implements HyperEdge interface:
    public String setName(String new_name) {
        HEUtils.checkAbbyNormal(this);

        String old_name = primSetName(new_name);

        if (HEUtils.stringEqual(new_name, old_name, true)) {
            return old_name;
        }

        // // new value is different than old:
        // primSetDirty(true, true);

        // CAUTION: Don't synchronize this whole method or place the
        //          following line in a synchronized block!
        //          Otherwise the listeners could possibly call back
        //          into this class causing a deadlock!
        _manager.fireChangeEvent(this, EventNote.Type.NAME,
                                 EventNote.SubType.CHANGED, old_name);

        return old_name;
    }

    // implements HyperEdge interface:
    public boolean isDirected() {
        HEUtils.checkAbbyNormal(this);

        Boolean ret_val = _node_attrs.getBooleanAttribute(_connector_node.getIdentifier(),
                                                          DIRECTED_ATTRIBUTE_NAME);

        return (ret_val != null) && ret_val.booleanValue();
    }

    // implements HyperEdge interface:
    public boolean setDirected(boolean new_state) {
        HEUtils.checkAbbyNormal(this);

        boolean last_val = isDirected();

        if (new_state == last_val) {
            // no change:
            return last_val;
        }

        // new value is different than old:
        if (new_state == false) {
            removeConnectorNodeDirected();
        } else {
            _node_attrs.setAttribute(_connector_node.getIdentifier(),
                                     DIRECTED_ATTRIBUTE_NAME,
                                     Boolean.TRUE);
        }

        // primSetDirty(true, true);

        // CAUTION: Don't synchronize this whole method or place the
        //          following line in a synchronized block!
        //          Otherwise the listeners could possibly call back
        //          into this class causing a deadlock!
        _manager.fireChangeEvent(this, EventNote.Type.DIRECTED,
                                 EventNote.SubType.CHANGED, null);

        return last_val;
    }

    // implements HyperEdge interface:    
    final public int getMinimumNumberEdges() {
        return 2;
    }

    // implements HyperEdge interface:
    public Iterator<CyNetwork> getNetworks() {
        HEUtils.checkAbbyNormal(this);

        // return Collections.unmodifiableSet(primGetNetworks()).iterator();
        return HEUtils.buildUnmodifiableCollectionIterator(primGetNetworks());
    }

    // used by the HyperEdgeManager:
    protected Set<CyNetwork> primGetNetworks() {
        return _nets;
    }

    /**
     * Used for HyperEdge persistence in restoring a HyperEdge from
     * underlying Cytoscape CyNodes, CyEdges, and attributes.
     */
    private void reconstructFromNodesEdgesAndAttributes(CyNode connectorNode,
                                                        CyNetwork net) {
        _connector_node = connectorNode;
        // &&& HACK Cytoscape.getCyNode(), probably called by the reader
        //     set the CANONICAL_NAME (LABEL_ATTRIBUTE_NAME) to the node id,
        //     which we don't  want for connector nodes:
	// MLC 06/21/07 BEGIN:
        // _node_attrs.deleteAttribute(connectorNode.getIdentifier(),
        //                             HyperEdgeImpl.LABEL_ATTRIBUTE_NAME);
        HEUtils.deleteAttribute(_node_attrs, connectorNode.getIdentifier(),
                                HyperEdgeImpl.LABEL_ATTRIBUTE_NAME);
	// MLC 06/21/07 END.
        // compute edges:
        int[]  adjacentEdges = net.getAdjacentEdgeIndicesArray(net.getIndex(connectorNode),
                                                               true,
                                                               true,
                                                               true);
        CyEdge edge;

        for (int j = 0; j < adjacentEdges.length; j++) {
            edge = (CyEdge) net.getEdge(adjacentEdges[j]);
            // HEUtils.log("considering edge " + edge.getIdentifier());

            if (_manager.isHyperEdgeEdge(edge, null)) {
                _nets.add(net);
                // HEUtils.log("bookkeeping edge " + edge.getIdentifier());
                performEdgeBookkeeping(primGetNode(edge),
                                       edge,
                                       net,
                                       false);
            }
        }
    }

    // This HyperEdge's connector node may not belong to net, but if it does
    // add all the appropriate info about the edges and nodes for this net:
    private void addCyNetworkToReconstruction(CyNetwork net) {
        int[] adjacentEdges = net.getAdjacentEdgeIndicesArray(net.getIndex(_connector_node),
                                                              true,
                                                              true,
                                                              true);

        if (adjacentEdges == null) {
            // this HyperEdge (connector node) doesn't exist in net:
            return;
        }

        CyEdge edge;

        for (int j = 0; j < adjacentEdges.length; j++) {
            edge = (CyEdge) net.getEdge(adjacentEdges[j]);

            if (_manager.isHyperEdgeEdge(edge, null)) {
                _nets.add(net);
                performEdgeBookkeeping(primGetNode(edge),
                                       edge,
                                       net,
                                       false);
            }
        }
    }

    static private boolean isConnectorNodeForReconstruction(CyNode node,
                                                            CyNetwork net) {
        return ("true".equals(_node_attrs.getStringAttribute(
                                                             node.getIdentifier(),
                                                             HyperEdgeImpl.IS_CONNECTOR_NODE_ATTRIBUTE_NAME)));
    }

    // used during reconstruction of HyperEdges from read in networks:
    private String createHEUUIDFromConnectorNodeUUID(CyNode connectorNode) {
        String cnUUID = connectorNode.getIdentifier();
        String heUUID = cnUUID.substring(CONNECTOR_NODE_UUID_PREFIX.length() +
                                         1);
        // MLC 01/15/07:
        // HEUtils.log("heUUID = '" + heUUID + "'.");

        return heUUID;
    }

    // ASSUME: HypeEdge has already been created, with a UUID.
    private void createConnectorNode() {
        // Use the uuid of this HyperEdge as the basis of the ConnectorNode
        // uuid:
        // String uuid = HEUtils.generateUUID(CONNECTOR_NODE_UUID_PREFIX);
        _connector_node = HEUtils.createConnectorNode(createConnectorNodeUUIDFromHEUUID());
    }

    private String createConnectorNodeUUIDFromHEUUID() {
        HEUtils.notNull(_uuid, "_uuid");

        return CONNECTOR_NODE_UUID_PREFIX + '-' + _uuid;
    }

    // Used for copying ConnectorNodes when reloading shared
    // hyperedges:
    protected static String createConnectorNodeUUIDWithoutHE() {
        return HEUtils.generateUUID(CONNECTOR_NODE_UUID_PREFIX);
    }

    // ASSUME: All error checking of params has been done
    protected CyEdge primAddEdge(CyNode node, String edgeIType,
                                 boolean fireEvents) {
        CyEdge edge;

        if (treatAsTarget(edgeIType)) {
            edge = HEUtils.createHEEdge(_connector_node, node, edgeIType);
        } else {
            edge = HEUtils.createHEEdge(node, _connector_node, edgeIType);
        }

        //        int  edge_idx = _root_graph.createEdge (node, _connector_node);
        //        CyEdge edge = _root_graph.getEdge (edge_idx);
        //	String uuid = HEUtils.generateUUID ();
        //        edge.setIdentifier (uuid);
        primAddEdge(node, edge, edgeIType, fireEvents);

        return edge;
    }

    /**
     * specify whether a given edge interaction type should
     * cause the new additional CyNode to be treated as the target
     * of an edge. Use the EdgeTypeMap to determine this.
     * For edgeITypes not in the EdgeTypeMap, the CyNode will
     * be treated as the source of the edge.
     */
    protected boolean treatAsTarget(String edgeIType) {
        EdgeRole er = HyperEdgeFactory.INSTANCE.getEdgeTypeMap().get(edgeIType);

        return ((er != null) && (er == EdgeRole.TARGET));
    }

    private void primAddEdge(CyNode node, CyEdge edge, String edgeIType,
                             boolean fireEvents) {
        // ASSUME: edge may already exist from a previous call.
        //        setEdgeInteractionType(edge, edgeIType);
        setEdgeCanonicalName(edge, edgeIType);
        addNodeEntityTypeWhenNeeded(node);
        performEdgeBookkeeping(node, edge, null, fireEvents);
    }

    private void performEdgeBookkeeping(CyNode node, CyEdge edge,
                                        CyNetwork net, boolean fireEvents) {
        // ASSUME: node and edge may already exist in this HE within another
        //         net.
        // ASSUME: No info exists for net yet (net may be new):
        MapUtils.addListValueToMap(_node_to_edges_map, node, edge, true);

        // edges doesn't allow duplicates via add:
        _edges.add(edge);

        _manager.registerEdge(edge, this, net);

        // primSetDirty(true, fireEvents);
        if (fireEvents) {
            _manager.fireChangeEvent(this, EventNote.Type.EDGE,
                                     EventNote.SubType.ADDED, edge);
        }
    }

    private int primGetNumEdges() {
        return _edges.size();
    }

    private int primGetNumNodes() {
        return _node_to_edges_map.size();
    }

    // ASSUME: node belongs to this HyperEdge
    // ASSUME: HyperEdge deletion is determined and performed
    //         elsewhere.
    private void primRemoveNode(CyNode node, boolean fireEvents,
                                BookkeepingItem bkItem) {
        List<CyEdge> edges = _node_to_edges_map.get(node);

        // We will really be removing some number of CyEdges:
        // Use a copy of the edges, since the edge data structures
        // will change:
        List<CyEdge> edges_copy = new ArrayList<CyEdge>(edges.size());
        edges_copy.addAll(edges);

        for (CyEdge edge : edges_copy) {
            // we know this hyperedge will not be destroyed:
            primRemoveEdge(node, edge, fireEvents, bkItem);
        }
    }

    private void primRemoveEdge(CyNode node, CyEdge edge, boolean fireEvents,
                                BookkeepingItem bkItem) {
        if (fireEvents) {
            _manager.fireChangeEvent(this, EventNote.Type.EDGE,
                                     EventNote.SubType.REMOVED, edge);
        }

        // Must be very careful where this call is located.
        // The edge-node information must still be available at time of call:
        _manager.unregisterEdge(edge, this);
        _edges.remove(edge);

        // Remove needed attributes:
        removeGeneratedEdgeAttributes(edge);
        // update the _node_to_edges_map:
        MapUtils.removeCollectionValueFromMap(_node_to_edges_map, node, edge);
        // Now delete the CyEdge from Cytoscape when we aren't already doing it:
        removeUnderlyingEdge(edge, bkItem);

        // MapUtils.removeCollectionValueFromMap(_net_to_edges_map, net, edge);
        HyperEdgeImpl otherHE = (HyperEdgeImpl) _manager.getHyperEdgeForConnectorNode(node);

        if ((otherHE != null) && (otherHE.primHasEdge(edge))) {
            // The node we removed was a connectorNode of another
            // HyperEdge that contains edge.
            // remove this edge from the other HyperEdge, if there:
            // Since we are removing edge in this HyperEdge,
            // it will be definitely be gone for otherHE.
            // Avoid attempting to redelete edge in otherHE
            // by ensuring we always have a BookkeepingItem:
            if (bkItem == null) {
                // Because this HyperEdge has a shared edge, it can
                // only be in one Network. The network will 
                // be the first element of the iterator:
                Iterator<CyNetwork> netsIt   = _nets.iterator();
                Set<GraphObject>    toIgnore = new HashSet<GraphObject>();

                bkItem = new BookkeepingItem(toIgnore,
                                             netsIt.next());
            }

            // ensure edge is on the list--will not duplicate since it is a Set:
            bkItem.getItems().add(edge);

            otherHE.removeOrDeleteEdge(edge, fireEvents, bkItem);
            //            otherHE.primRemoveEdge(otherHE.primGetNode(edge),
            //                                   edge,
            //                                   fireEvents);
        }
    }

    // Remove the given edge from its underlying representation (e.g.,
    // Cytoscape) in all CyNetworks for which it belongs:
    private void removeUnderlyingEdge(CyEdge edge, BookkeepingItem bkItem) {
        //        int edgeIdx = Cytoscape.getRootGraph().getIndex(edge);
        for (CyNetwork net : _nets) {
            // only disregard deleting the specific item we are doing bookkeeping on.
            // All other items delete:
            if ((bkItem == null) || (bkItem.getNetwork() != net) ||
                (!bkItem.getItems().contains(edge))) {
                _manager.removeUnderlyingEdge(edge, net);
            }

            //            // TODO: change set_remove to true if removeEdge() is made
            //            // clear that it works on one network or across networks:
            //            net.removeEdge(edgeIdx, false);
            //            // net.hideEdge(edge);
        }
    }

    private void removeUnderlyingNode(CyNode node, BookkeepingItem bkItem) {
        // int nodeIdx = Cytoscape.getRootGraph().getIndex(node);
        for (CyNetwork net : _nets) {
            // For each network, if nothing is left connected to node, then remove it:
	    _manager.removeNodeFromNet (node, net , bkItem);
        }
    }

    protected void primDestroy(boolean fireEvents, BookkeepingItem bkItem) {
        // Allow multiple calls to delete via blanket obj deleter:
        if (isState(LifeState.DELETED)) {
            return;
        }

        primSetState(LifeState.DELETION_IN_PROGRESS);

        if (fireEvents) {
            _manager.fireDeleteEvent(this);
        }

        removeNodeAndEdgeContent(bkItem);

        // Make sure this follows removal of CyEdges and CyNodes, since CyEdges may use
        // HyperGraphManager maps about this HyperEdge:
        _manager.unregisterHyperEdge(this);

        removeConnectorNodeAndAttributes(bkItem);

        // We leave the connector node alone, like any other node:
        // HEUtils.removeNode(_connector_node);
        _edges             = null; // help GC
        _node_to_edges_map = null; // help GC
                                   // _net_to_edges_map   = null; // help GC

        _nets              = null; // help GC
                                   //        _change_listener_store = null; // help GC

        _connector_node    = null; // help GC
        primDeleteSubclass();
    }

    //    protected void removeGeneratedAttributes() {
    //        removeConnectorNodeAttributes();
    //
    //        for (CyEdge edge : _edges) {
    //            removeGeneratedEdgeAttributes(edge);
    //        }
    //    }
    private void removeGeneratedEdgeAttributes(CyEdge edge) {
        removeEdgeInteractionType(edge);
        removeEdgeHyperEdgeTag(edge);
        removeEdgeCanonicalName(edge);
    }

    private void removeConnectorNodeAndAttributes(BookkeepingItem bkItem) {
        removeConnectorNodeName();
        removeConnectorNodeDirected();
        removeConnectorNodeEntityType();
        removeUnderlyingNode(_connector_node, bkItem);
    }

    /**
     * Remove all nodes and edges within a HyperEdge.
     * ASSUME: Only called when whole HyperEdge is being deleted.
     */
    private void removeNodeAndEdgeContent(BookkeepingItem bkItem) {
        // cleanup all the datastructures for the stuff we
        // contain--nodes and edges:
        // Since the _node_to_edges_map will change via various
        // methods called, copy nodes:
        for (CyNode node : new ArrayList<CyNode>(_node_to_edges_map.keySet())) {
            primRemoveNode(node, false, bkItem);
        }
    }

    // implements Mutable interface:
    //    public boolean addChangeListener(ChangeListener l) {
    //        _change_listener_store = ListenerList.setupListenerListWhenNecessary(_change_listener_store);
    //
    //        return _change_listener_store.addListener(l);
    //    }
    //
    //    // implements Mutable interface:
    //    public boolean removeChangeListener(ChangeListener l) {
    //        if (_change_listener_store != null) {
    //            return _change_listener_store.removeListener(l);
    //        }
    //
    //        return false;
    //    }

    //    // return the type of a given edge.
    //    private String getEdgeInteractionType(CyEdge edge) {
    //        // return _edge_attrs.getStringAttribute(edge.getIdentifier(),
    //        //                                      _edgeInteractionAttributeName);
    //        return _edge_attrs.getStringAttribute(edge.getIdentifier(),
    //                                              Semantics.INTERACTION);
    //    }

    //    private void setEdgeInteractionType(CyEdge edge, String type_val) {
    //	//        _edge_attrs.setAttribute(edge.getIdentifier(),
    //	//                                 _edgeInteractionAttributeName,
    //	//                                 type_val);
    //	_edge_attrs.setAttribute(edge.getIdentifier(),
    //				 Semantics.INTERACTION,
    //				 type_val);
    //    }
    private void removeEdgeInteractionType(CyEdge edge) {
        //        _edge_attrs.deleteAttribute(edge.getIdentifier(),
        //                                    _edgeInteractionAttributeName);
	// MLC 06/21/07 BEGIN:
        // _edge_attrs.deleteAttribute(edge.getIdentifier(),
        //                            Semantics.INTERACTION);
        HEUtils.deleteAttribute (_edge_attrs, edge.getIdentifier(),
				 Semantics.INTERACTION);
	// MLC 06/21/07 END.
    }

    private void removeEdgeHyperEdgeTag(CyEdge edge) {
	// MLC 06/21/07 BEGIN:
        // _edge_attrs.deleteAttribute(edge.getIdentifier(),
        //                             HYPEREDGE_EDGE_TAG_NAME);
        HEUtils.deleteAttribute(_edge_attrs, edge.getIdentifier(),
				HYPEREDGE_EDGE_TAG_NAME);
	// MLC 06/21/07 END.
    }

    // based on Cytoscape.getCyEdge() internal setting of LABEL:
    private void setEdgeCanonicalName(CyEdge edge, String edgeIType) {
        _edge_attrs.setAttribute(edge.getIdentifier(),
                                 AttributeConstants.MONIKER,
                                 edgeIType);
        _edge_attrs.setAttribute(edge.getIdentifier(),
                                 LABEL_ATTRIBUTE_NAME,
                                 edgeIType);
    }

    // MAY BE USED IN THE FUTURE:
    // Only add entity type information if not already there.
    // Since Node may be used in many HyperEdges, node may already
    // have the attribute. Note that nodes that are shared will work
    // since the node will be connector node first, then be shared.
    private void addNodeEntityTypeWhenNeeded(CyNode node) {
        if (_node_attrs.getStringAttribute(node.getIdentifier(),
                                           ENTITY_TYPE_ATTRIBUTE_NAME) == null) {
            // no attribute value exists:
            _node_attrs.setAttribute(node.getIdentifier(),
                                     ENTITY_TYPE_ATTRIBUTE_NAME,
                                     ENTITY_TYPE_REGULAR_NODE_VALUE);
        }
    }

    //    private static String getEdgeCanonicalName (CyEdge edge)
    //    {
    //        return (String) _edge_attrs.getAttributeValue (edge.getIdentifier (),
    //                                                      cytoscape.data.Semantics.LABEL);
    //    }
    private String getConnectorNodeName() {
        return _node_attrs.getStringAttribute(_connector_node.getIdentifier(),
                                              AttributeConstants.MONIKER);
    }

    private void setConnectorNodeName(String newName) {
        if (newName == null) {
            removeConnectorNodeName();
        } else {
            _node_attrs.setAttribute(_connector_node.getIdentifier(),
                                     AttributeConstants.MONIKER,
                                     newName);
            // will be replaced when there is an alternative:
            _node_attrs.setAttribute(_connector_node.getIdentifier(),
                                     LABEL_ATTRIBUTE_NAME,
                                     newName);
        }
    }

    private void removeConnectorNodeEntityType() {
        if (_connector_node != null) {
	    // MLC 06/21/07 BEGIN:
            // _node_attrs.deleteAttribute(_connector_node.getIdentifier(),
            //                            ENTITY_TYPE_ATTRIBUTE_NAME);
            HEUtils.deleteAttribute(_node_attrs,
				    _connector_node.getIdentifier(),
				    ENTITY_TYPE_ATTRIBUTE_NAME);
	    // MLC 06/21/07 END.
        }
    }

    private void removeConnectorNodeName() {
        if (_connector_node != null) {
            String name = getName();

            if (name != null) {
		// MLC 06/21/07 BEGIN:
		//                _node_attrs.deleteAttribute(_connector_node.getIdentifier(),
		//                                            AttributeConstants.MONIKER);
		//                // will be replaced when there is an alternative:
		//                _node_attrs.deleteAttribute(_connector_node.getIdentifier(),
		//                                            LABEL_ATTRIBUTE_NAME);
                HEUtils.deleteAttribute(_node_attrs,
					_connector_node.getIdentifier(),
					AttributeConstants.MONIKER);
                // will be replaced when there is an alternative:
                HEUtils.deleteAttribute(_node_attrs,
					_connector_node.getIdentifier(),
					LABEL_ATTRIBUTE_NAME);
	    // MLC 06/21/07 END.
            }
        }
    }

    private void removeConnectorNodeDirected() {
        if ((_connector_node != null) && (isDirected())) {
	    // MLC 06/21/07 BEGIN:
            // _node_attrs.deleteAttribute(_connector_node.getIdentifier(),
            //                             DIRECTED_ATTRIBUTE_NAME);
            HEUtils.deleteAttribute(_node_attrs,
				    _connector_node.getIdentifier(),
				    DIRECTED_ATTRIBUTE_NAME);
	    // MLC 06/21/07 END.
        }
    }

    private void removeEdgeCanonicalName(CyEdge edge) {
	// MLC 06/21/07 BEGIN:
	//        _edge_attrs.deleteAttribute(edge.getIdentifier(),
	//                                    AttributeConstants.MONIKER);
	//        // will be replaced when there is an alternative:
	//        _edge_attrs.deleteAttribute(edge.getIdentifier(),
	//                                    LABEL_ATTRIBUTE_NAME);
        HEUtils.deleteAttribute(_edge_attrs, edge.getIdentifier(),
                                AttributeConstants.MONIKER);
        // will be replaced when there is an alternative:
        HEUtils.deleteAttribute(_edge_attrs, edge.getIdentifier(),
				LABEL_ATTRIBUTE_NAME);
	// MLC 06/21/07 END.
    }

    // Used for temporarily marking objects for doing things like very
    // efficient intersection algorithms:
    protected boolean isMarked() {
        return _marked;
    }

    // Used for temporarily marking objects for doing things like very
    // efficient intersection algorithms:
    protected void setMarked(boolean mark_state) {
        _marked = mark_state;
    }

    protected HyperEdgeManagerImpl getManager() {
        return _manager;
    }

    private void primSetState(LifeState new_state) {
        _state = new_state;
    }

    protected boolean primDeleteSubclass() {
        // Note: will probably be in state DELETION_IN_PROGRESS from subclass.
        // Take care not to add the check for DELETION_IN_PROGRESS that is
        // found in many subclasses, otherwise we wouldn't finish
        // our deletion process here:
        if (isState(LifeState.DELETED)) {
            return false;
        }

        primSetState(LifeState.DELETED);

        //        // don't trigger event since we are in the process of deletion:
        //        primSetDirty(true, false);
        return true;
    }

    public CyNode getNode(CyEdge edge) {
        HEUtils.checkAbbyNormal(this);

        if (!primHasEdge(edge)) {
            HEUtils.throwIllegalArgumentException("HyperEdge.getNode(): edge doesn't belong to this HyperEdge.");
        }

        return primGetNode(edge);
    }

    // Assumes: edge is a member of this HyperEdge.
    protected CyNode primGetNode(CyEdge edge) {
        CyNode source = (CyNode) edge.getSource();

        if (source == _connector_node) {
            return (CyNode) edge.getTarget();
        }

        return source;
    }

    // implements Identifiable interface:
    public String getIdentifier() {
        return _uuid;
    }

    //    // ASSUME: net != null
    //    // ASSUME: edge exists in this HyperEdge
    //    private void removeNodeOneGraphPerpsective(CyNode node, List<CyEdge> edges,
    //                                               CyNetwork net) {
    //        // Determine if we remove the HyperEdge from a
    //        // CyNetwork.  If so, avoid removing some CyEdges first:
    //        if (wouldDestroyInCyNetwork(net, edges)) {
    //            // primDestroy(true);
    //            removeFromNetwork(net);
    //        } else {
    //            primRemoveNode(node, net, true);
    //        }
    //    }
    //
    //    // ASSUME: net != null
    //    // ASSUME: edge exists in this HyperEdge
    //    // edgeList is a single element list containing the edge to remove.
    //    private void removeEdgeOneGraphPerpsective(List<CyEdge> edgeList,
    //                                               CyNetwork net) {
    //        // Determine if we remove the HyperEdge from a
    //        // CyNetwork.  If so, avoid removing some CyEdges first:
    //        if (wouldDestroyInCyNetwork(net, edgeList)) {
    //            removeFromNetwork(net);
    //        } else {
    //            primRemoveEdge(primGetNode(edge),
    //                           edgeList.get(0),
    //                           net,
    //                           true);
    //        }
    //    }
    //
    //    // Return true if we would destroy this HyperEdge by removing the
    //    // given edges.  Return false iff removal of the given edges would
    //    // not lead to removal of the HyperEdge.
    //    private boolean wouldDestroy(List<?extends CyEdge> removeEdges) {
    //
    //	int numEdges = _edges.size();
    //	for (CyEdge edge : removeEdges) {
    //            if (_edges.contains(edge)) {
    //                numEdges--;
    //                if (numEdges < getMinimumNumberEdges()) {
    //                    return true;
    //                }
    //	    }
    //	}
    //	return false;
    //    }
    //
    //    // Return true if we would destroy this HyperEdge within a given
    //    // CyNetwork by removing the given edges from this
    //    // CyNetwork.
    //    private boolean wouldDestroyInCyNetwork(CyNetwork net,
    //                                                   List<?extends CyEdge> removeEdges) {
    //        List<CyEdge> netEdges = _net_to_edges_map.get(net);
    //
    //        if (netEdges == null) {
    //            // the net doesn't belong to this HyperEdge
    //            return false;
    //        }
    //
    //        int netSize = netEdges.size();
    //
    //        Iterator rEdges = removeEdges.iterator();
    //
    //        while (rEdges.hasNext()) {
    //            if (netEdges.contains(rEdges.next())) {
    //                netSize--;
    //
    //                if (netSize < getMinimumNumberEdges()) {
    //                    return true;
    //                }
    //            }
    //        }
    //
    //        return false;
    //    }
    //

    //    // Return an iterator over all the CyNetworks for which a given
    //    // edge belongs:
    //    private Iterator<CyNetwork> getNetworks(CyEdge edge) {
    //        List<CyNetwork> netList = new ArrayList<CyNetwork>(3);
    //        Set<CyNetwork>  netSet = _net_to_edges_map.keySet();
    //
    //        for (CyNetwork net : netSet) {
    //            if (hasEdgeInNET(edge, net)) {
    //                netList.add(net);
    //            }
    //        }
    //
    //        return netList.iterator();
    //    }

    //    // How many graph perspectives is edge in within this HyperEdge?
    //    private int numCyNetworks(CyEdge edge) {
    //        Iterator<CyNetwork> netIt  = _net_to_edges_map.keySet().iterator();
    //        int                        count = 0;
    //
    //        while (netIt.hasNext()) {
    //            if (hasEdgeInNET(edge, netIt.next())) {
    //                count++;
    //            }
    //        }
    //
    //        return count;
    //    }

    //    // Return true if we would destroy this HyperEdge by removing the
    //    // given edges from the given CyNetwork.  Return false iff
    //    // there exists a CyNetwork where removal does not take
    //    // place or where removal of the given edges would not lead to
    //    // removal of the HyperEdge.
    //    private boolean wouldDestroy(CyNetwork net,
    //                                 List<?extends CyEdge> removeEdges) {
    //        Set<CyNetwork> allNets = _net_to_edges_map.keySet();
    //
    //        if (net == null) {
    //            Iterator<CyNetwork> netIt = allNets.iterator();
    //
    //            while (netIt.hasNext()) {
    //                if (!wouldDestroyInCyNetwork(netIt.next(), removeEdges)) {
    //                    return false;
    //                }
    //            }
    //
    //            return true;
    //        } else {
    //            // allNets size is a course check--if there's another,
    //            // uneffected, CyNetwork in the HE, then we don't destroy:
    //            return ((allNets.size() == 1) &&
    //                   wouldDestroyInCyNetwork(net, removeEdges));
    //        }
    //    }

    // ASSUME: net is not null.
    //    private boolean hasEdgeInNET(CyEdge edge, CyNetwork net) {
    //        List<CyEdge> edges = _net_to_edges_map.get(net);
    //
    //        if (edges == null) {
    //            // wrong net for this HE:
    //            return false;
    //        }
    //
    //        for (CyEdge matchEdge : edges) {
    //            if (edge == matchEdge) {
    //                return true;
    //            }
    //        }
    //        return false;
    //    }

    //    // ASSUME: edges may be null
    //    private Collection<CyNode> getDistinctNodes(Collection<?extends CyEdge> edges) {
    //        List<CyNode> nodeSet = new ArrayList<CyNode>();
    //
    //        if (edges != null) {
    //            CyNode                    node;
    //            Iterator<?extends CyEdge> edgeIt = edges.iterator();
    //
    //            while (edgeIt.hasNext()) {
    //                node = primGetNode((CyEdge) edgeIt.next());
    //
    //                if (!nodeSet.contains(node)) {
    //                    nodeSet.add(node);
    //                }
    //            }
    //        }
    //
    //        return nodeSet;
    //    }

    //    // implements Identifiable interface:
    //    public boolean addDirtyListener(DirtyListener l) {
    //        _dirty_listener_store = ListenerList.setupListenerListWhenNecessary(_dirty_listener_store);
    //
    //        return _dirty_listener_store.addListener(l);
    //    }
    //
    //    // implements Identifiable interface:
    //    public boolean removeDirtyListener(DirtyListener l) {
    //        if (_dirty_listener_store != null) {
    //            return _dirty_listener_store.removeListener(l);
    //        }
    //
    //        return false;
    //    }
    //
    //    // implements Identifiable interface:
    //    public boolean isDirty() {
    //        return _dirty;
    //    }
    //
    //    // implements Identifiable interface:
    //    public boolean save(Writer w, Object args, Format format) {
    //        HEUtils.checkAbbyNormal(this);
    //
    //        if (format == Format.XML) {
    //            return saveAsXML(w, args);
    //        }
    //
    //        return false;
    //    }
    //
    //    // implements Identifiable interface:
    //    public boolean load(Map values) {
    //        HEUtils.checkAbbyNormal(this);
    //
    //        if (values == null) {
    //            HEUtils.throwIllegalArgumentException(
    //                "HyperEdgeImpl.load() called with a null values map!");
    //        }
    //
    //        if (!isState(LifeState.CREATION_IN_PROGRESS)) {
    //            HEUtils.throwIllegalStateException(
    //                "HyperEdgeImpl.load() called when not in state CREATION_IN_PROGRESS!");
    //        }
    //
    //        _connector_node = (CyNode) values.get(PERSISTENT_CONNECTOR_NODE_ATTRIBUTE_NAME);
    //
    //        if (_connector_node == null) {
    //            HEUtils.errorLog(
    //                "HyperEdgeImpl.load(): Warning, missing 'connector_node' property. Will try to compute.");
    //        }
    //
    //        String name = (String) values.get(PERSISTENT_NAME_ATTRIBUTE_NAME);
    //
    //        if (name != null) {
    //            primSetName(name);
    //        }
    //
    //        if (_connector_node != null) {
    //            _node_attrs.setAttribute(_connector_node.getIdentifier(),
    //                IS_CONNECTOR_NODE_ATTRIBUTE_NAME, "true");
    //        }
    //
    //        Boolean directed = (Boolean) values.get(PERSISTENT_DIRECTED_ATTRIBUTE_NAME);
    //
    //        if (directed != null) {
    //            _directed = directed.booleanValue();
    //        }
    //
    //        // List   nodes      = (List) values.get ("nodes");
    //        List edges = (List) values.get(PERSISTENT_EDGES_ATTRIBUTE_NAME);
    //
    //        CyEdge edge;
    //
    //        for (int i = 0; i < edges.size(); i++) {
    //            edge = (CyEdge) edges.get(i);
    //            primAddEdge((edge.getSource() != _connector_node)
    //                ? edge.getSource() : edge.getTarget(), edge,
    //                getEdgeInteractionType(edge), null, false);
    //        }
    //
    //        Boolean existing_obj = (Boolean) values.get(PERSISTENT_EXISTING_OBJ_ATTRIBUTE_NAME);
    //
    //        if (existing_obj == null) {
    //            existing_obj = Boolean.FALSE;
    //        }
    //
    //        if (!existing_obj.booleanValue()) {
    //            constructorSupportfalse);
    //        }
    //
    //        return true;
    //    }
    //
    //    private boolean saveAsXML(Writer w, Object args) {
    //        String interface_name = HEXMLUtils.mapClassToInterfaceName(this);
    //        String name = HEXMLUtils.addAmpStrings(getName());
    //        boolean directed = Boolean.valueOf(_directed).booleanValue();
    //        String indent = HEXMLUtils.getSaveIndent(args);
    //
    //        if (name != null) {
    //            HEXMLUtils.println(w,
    //                indent + "<" + interface_name + " uuid=\"" + getIdentifier() +
    //                "\" name=\"" + name + "\" directed=\"" + directed + "\">");
    //        } else {
    //            HEXMLUtils.println(w,
    //                indent + "<" + interface_name + " uuid=\"" + getIdentifier() +
    //                "\" directed=\"" + directed + "\">");
    //        }
    //
    //        String bigger_indent = indent + HEXMLUtils.INDENT_INCREMENT;
    //        String biggest_indent = bigger_indent + HEXMLUtils.INDENT_INCREMENT;
    //        HEXMLUtils.saveElement(w, _connector_node.getIdentifier(),
    //            PERSISTENT_CONNECTOR_NODE_ATTRIBUTE_NAME, "uuid", bigger_indent);
    //        //        HEXMLUtils.println (w, bigger_indent + "<Nodes>");
    //        //        Iterator it = _node_to_edges_map.keySet ().iterator ();
    //        //        while (it.hasNext ())
    //        //        {
    //        //            HEXMLUtils.saveElement (w, ((CyNode) it.next ()).getIdentifier (),
    //        //                                    "Node", "uuid", biggest_indent);
    //        //        }
    //        //        HEXMLUtils.println (w, bigger_indent + "</Nodes>");
    //        HEXMLUtils.println(w, bigger_indent + "<CyEdges>");
    //
    //        Iterator edge_it = _edges.iterator();
    //        CyEdge edge;
    //        Map se_map = new HashMap(5);
    //
    //        while (edge_it.hasNext()) {
    //            se_map.clear();
    //            edge = (CyEdge) edge_it.next();
    //            se_map.put("uuid", edge.getIdentifier());
    //            se_map.put("i_type",
    //                HEXMLUtils.addAmpStrings(getEdgeInteractionType(edge)));
    //            // se_map.put ("directed", new Boolean(edge.isDirected ()));
    //            se_map.put("source",
    //                HEXMLUtils.addAmpStrings(edge.getSource().getIdentifier()));
    //            se_map.put("target",
    //                HEXMLUtils.addAmpStrings(edge.getTarget().getIdentifier()));
    //            HEXMLUtils.saveElement(w, "CyEdge", se_map, biggest_indent, true);
    //        }
    //
    //        HEXMLUtils.println(w, bigger_indent + "</CyEdges>");
    //        HEXMLUtils.println(w, indent + "</" + interface_name + ">");
    //
    //        return true;
    //    }
    //
    //    // implements Mutable interface:
    //    public boolean setDirty(boolean new_state) {
    //        HEUtils.checkAbbyNormal(this);
    //
    //        return primSetDirty(new_state, true);
    //    }
    //    /**
    //     * Keep package private to avoid allowing overriding:
    //     */
    //    boolean primSetDirty(boolean new_dirty, boolean trigger_event) {
    //        if (_dirty == new_dirty) {
    //            return false;
    //        }
    //
    //        _dirty = new_dirty;
    //
    //        //        if (new_dirty)
    //        //        {
    //        //            _manager.setAnyDirty (true);
    //        //        }
    //        if (trigger_event) {
    //            fireDirtyEvent();
    //        }
    //
    //        return true;
    //    }
    //
    //    public void fireDirtyEvent() {
    //        if ((_dirty_listener_store != null) &&
    //                (_dirty_listener_store.hasListeners())) {
    //            // Now call all the listeners:
    //            Iterator it = _dirty_listener_store.iterator();
    //
    //            synchronized (_dirty_listener_store) {
    //                while (it.hasNext()) {
    //                    ((DirtyListener) it.next()).dirtyStateChanged(this);
    //                }
    //            }
    //        }
    //    }
}
