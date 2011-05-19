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

/* 
*
* Revisions:
*
* Tue Apr 07 17:39:08 2009 (Michael L. Creech) creech@w235krbza760
*  Fixed bug where custom ConnectorNode labels were erased when
*  restoring saved HyperEdges. See reconstructFromNodesEdgesAndAttributes.
*  Added isStandardLabel().
* Wed Apr 02 19:41:20 2008 (Michael L. Creech) creech@w235krbza760
*  Fixed bad bug where reading session or saved networks was not
*  reconstructing all information needed to correctly represent
*  HyperEdges (e.g., _net_to_hes_map). This would lead to HyperEdges
*  not being correctly removed and then errors (e.g.,
*  HEStructuralIntegrityException) occurring when reloading the same
*  file.
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
    /**
     * Name of the Attribute that defines the type of a CyNode--whether a connector node or
     * regular node.
     */
    public static final String ENTITY_TYPE_ATTRIBUTE_NAME       = "HyperEdge.EntityType";
    /**
     * The value used for the attribute ENTITY_TYPE_ATTRIBUTE_NAME when we have
     * a ConnectorNode.
     */
    public static final String ENTITY_TYPE_CONNECTOR_NODE_VALUE = "ConnectorNode";

    /**
     * The value used for the attribute ENTITY_TYPE_ATTRIBUTE_NAME when we have
     * a regular node.
     */
    public static final String ENTITY_TYPE_REGULAR_NODE_VALUE = "RegularNode";
    /**
     * The name of the attribute used to specify if a HyperEdge is directed or not.
     */
    public static final String DIRECTED_ATTRIBUTE_NAME    = "HyperEdge.isDirected";
    /**
     * The prefix used for creating the unique identifiers for ConnectorNodes.
     */
    public static final String CONNECTOR_NODE_UUID_PREFIX = "connector-node";
    /**
     * Edge Attribute name used to specify if a given edge is a hyperedge edge or not.
     */
    public static final String HYPEREDGE_EDGE_TAG_NAME = "HyperEdge.isEdge";

    /**
     * Attribute name used to label Nodes. This is equivalent to
     * cytoscape.data.Semantics.CANONICAL_NAME, but we don't use it
     * since it is deprecated:
     */

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
    private static final String SINGLE_QUOTE = "'";
    private static transient HyperEdgeManagerImpl manager = (HyperEdgeManagerImpl) HyperEdgeFactory.INSTANCE.getHyperEdgeManager();

    //    private transient static RootGraph            _root_graph = Cytoscape.getRootGraph ();
    private static transient CyAttributes edgeAttrs = Cytoscape.getEdgeAttributes();
    private static transient CyAttributes nodeAttrs = Cytoscape.getNodeAttributes();

    //    // Use to reduce String creation when retrieving attributes:
    //    private transient String _edgeInteractionAttributeName;

    //    // the List of all CyNodes contained in this HyperEdge. This may include
    //    // duplicates (e.g., (A,B,A).
    //    // the order of the nodes is important--it must correspond to the order
    //    // of the roles:
    //    private List _nodes          = new ArrayList();
    //    private Set  _distinct_nodes = new HashSet(11);
    private CyNode myConnectorNode;

    // the List of all CyEdges contained in this HyperEdge. This is a set--
    // it never inludes duplicates. This is used for faster access to the
    // edges then using the map:
    private Set<CyEdge> myEdges = new HashSet<CyEdge>();

    // map nodes to a List of unique CyEdges. For homodimer-like structures,
    // such as nodes (A,B,A) with edges (e1,e2,e3), 'A' would map to 
    // (e1,e3):
    private transient Map <CyNode, List <CyEdge>> nodeToEdgesMap = 
	new HashMap <CyNode, List <CyEdge>>();

    //    // map GraphPerpective to a List of edges that are the members of this HyperEdge
    //    // that belong to that CyNetwork. The list can be different for
    //    // each HyperEdge. ASSUME: if no edges are in a CyNetwork, then the
    //    // HyperEdge is not in the CyNetwork.
    //    private transient Map<CyNetwork, List<CyEdge>> _net_to_edges_map = new HashMap<CyNetwork, List<CyEdge>>();

    // The set of CyNetworks to which this HyperEdge belongs:
    private transient Set<CyNetwork> nets = new HashSet<CyNetwork>();

    //    private transient ListenerList _change_listener_store;
    private transient LifeState state;

    // private transient boolean _dirty;

    // private transient ListenerList _dirty_listener_store;

    // Used for temporarily marking objects for doing things like very
    // efficient intersection algorithms:
    private transient boolean marked;

    // private String _name;
    // private boolean _directed;
    private String myUuid;

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
    HyperEdgeImpl(final CyNode connectorNode, final CyNetwork net) {
        // REMEMBER: Don't invoke overridable methods of this object
        //           in this constructor!
        subclassConstructorSupport(createHEUUIDFromConnectorNodeUUID(connectorNode));
        reconstructFromNodesEdgesAndAttributes(connectorNode, net);
        // MLC 08/27/06 BEGIN:
        // constructorSupport(net, false);
        manager.registerHyperEdge(this);
	// MLC 04/02/08 BEGIN:
        // startInCyNetwork(net);
	startInCyNetwork (net);
	// MLC 04/02/08 END.
        primSetState(LifeState.NORMAL);
        // MLC 08/27/06 END.
    }

    HyperEdgeImpl(final CyNode node1, final String edgeIType1, final CyNode node2,
                            final String edgeIType2, final CyNetwork net, final boolean fireEvents) {
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

    HyperEdgeImpl(final CyNode node1, final String edgeIType1, final CyNode node2,
                            final String edgeIType2, final CyNode node3, final String edgeIType3,
                            final CyNetwork net, final boolean fireEvents) {
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

    HyperEdgeImpl(final CyNode[] nodes, final String[] edgeITypes, final CyNetwork net,
                            final boolean fireEvents) {
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

    HyperEdgeImpl(final List<CyNode> nodes, final List<String> edgeITypes,
                            final CyNetwork net, final boolean fireEvents) {
        // REMEMBER: Don't invoke overridable methods of this object
        //           in this constructor!
        HEUtils.notNull(net, "net");

        CyNode[] nodeArray = null;

        if (nodes != null) {
            nodeArray = new CyNode[nodes.size()];
            nodeArray = (CyNode[]) nodes.toArray(nodeArray);
        }

        String[] edgeITypesArray = null;

        if (edgeITypes != null) {
            edgeITypesArray = new String[edgeITypes.size()];
            edgeITypesArray = (String[]) edgeITypes.toArray(edgeITypesArray);
        }

        ensureCorrectArguments(nodeArray, edgeITypesArray, "List");
        // everything is ok:
        subclassConstructorSupport(null);
        createConnectorNode();
        addEdges(nodeArray, edgeITypesArray);
        constructorSupport(net, fireEvents);
    }

    HyperEdgeImpl(final Map<CyNode, String> nodeEdgeTypeMap,
                            final CyNetwork net, final boolean fireEvents) {
        // REMEMBER: Don't invoke overridable methods of this object
        //           in this constructor!
        HEUtils.notNull(net, "net");

        String[] edgeITypesArray = null;
        CyNode[] nodeArray = null;

        if (nodeEdgeTypeMap != null) {
            edgeITypesArray = new String[nodeEdgeTypeMap.size()];
            nodeArray       = new CyNode[nodeEdgeTypeMap.size()];
            nodeArray       = (CyNode[]) (nodeEdgeTypeMap.keySet()).toArray(nodeArray);
            edgeITypesArray = (String[]) (nodeEdgeTypeMap.values()).toArray(edgeITypesArray);
        }

        ensureCorrectArguments(nodeArray, edgeITypesArray, "Map");
        // everything is ok:
        subclassConstructorSupport(null);
        createConnectorNode();
        addEdges(nodeArray, edgeITypesArray);
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
    static HyperEdge reconstructIfConnectorNode(final CyNode connectorNode,
                                                          final CyNetwork net) {
        // First see if a HyperEdge already exists with this connector node:
        HyperEdge he = manager.getHyperEdgeForConnectorNode(connectorNode);

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

                // TODO FIX: possibly remove all shared Edges from this net.
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
    void subclassConstructorSupport(final String uuid) {
        // REMEMBER: Don't invoke overridable methods of this object
        //           in this constructor!
        primSetState(LifeState.CREATION_IN_PROGRESS);

        if (uuid == null) {
            myUuid = HEUtils.generateUUID(null);

            // primSetDirty(true, false);
        } else {
            myUuid = uuid;
        }

        //        _edgeInteractionAttributeName = cytoscape.data.Semantics.INTERACTION +
        //                                        '-' + _uuid;
    }

    final void constructorSupport(final CyNetwork net, final boolean fireEvents) {
        // REMEMBER: Don't invoke overridable methods of this object
        //           in this constructor!
        manager.registerHyperEdge(this);
        startInCyNetwork(net);
        primSetState(LifeState.NORMAL);

        if (fireEvents) {
            manager.fireNewHObjEvent(this);
        }
    }

    private String primSetName(final String newName) {
        final String lastVal = getConnectorNodeName();
        setConnectorNodeName(newName);

        return lastVal;
    }

    // implements Matchable interface:

    /**
     * {@inheritDoc}
     * Uses default similarity checking.
     * This default implementation simply calls:
     * <PRE>
     *    simMatcher.similarTo (this, he, optArgs);
     * </PRE>
     */
    public boolean isSimilar(final SimMatcher simMatcher, final HyperEdge he, final Object optArgs) {
        if (simMatcher == null) {
            return false;
        }

        return simMatcher.similarTo(this, he, optArgs);
    }

    private void ensureCorrectArguments(final CyNode[] nodes, final String[] edgeITypes,
                                        final String paramType) {
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
                HEUtils.throwIllegalArgumentException("The node " + paramType +
                                                      " given contains a null entry");
            }

            ensureNotNodeConnector(nodes[i]);

            if (edgeITypes[i] == null) {
                HEUtils.throwIllegalArgumentException("The edgeITypes " +
                                                      paramType +
                                                      " given contains a null entry");
            }
        }
    }

    private void ensureNotNodeConnector(final CyNode node) {
        if (manager.isConnectorNode(node, null)) {
            final String msg = "HyperEdgeImpl.ensureNotNodeConnector(): Illegal use of ConnectorNode '" +
                         node.getIdentifier() +
                         "'. Only non-ConnectorNodes may be used here.";
            HEUtils.throwIllegalArgumentException(msg);
        }
    }

    private void addEdges(final CyNode[] nodes, final String[] edgeITypes) {
        for (int i = 0; i < edgeITypes.length; i++) {
            primAddEdge(nodes[i], edgeITypes[i], false);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean inNetwork(final CyNetwork net) {
        HEUtils.checkAbbyNormal(this);
        HEUtils.notNull(net, "net");

        return primInCyNetwork(net);
    }

    // assumes net is not null
    // (also used by HyperEdgeManager)
    boolean primInCyNetwork(final CyNetwork net) {
        // return _net_to_edges_map.keySet().contains(net);
        return nets.contains(net);
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean addToNetwork(final CyNetwork net) {
        HEUtils.checkAbbyNormal(this);
        HEUtils.notNull(net, "net");

        if (primInCyNetwork(net) || primHasSharedEdges()) {
            return false;
        }

        nets.add(net);
        //        // No need to check for duplicates since
        //        // getEdges() will never contain duplicates (false as last param):
        //        MapUtils.addListValuesToMap(_net_to_edges_map,
        //                                    net,
        //                                    primGetEdges(null, sourceNet).iterator(),
        //                                    false);
        manager.addToCyNetwork(net, this);
        manager.fireChangeEvent(this, EventNote.Type.HYPEREDGE,
                                 EventNote.SubType.ADDED, net);

        return true;
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasSharedEdges() {
        HEUtils.checkAbbyNormal(this);

        return primHasSharedEdges();
    }

    boolean primHasSharedEdges() {
        for (CyEdge edge : myEdges) {
            if (primIsSharedEdge(edge)) {
                return true;
            }
        }

        return false;
    }

    /**
     * {@inheritDoc}
     */
    public Iterator<CyEdge> getSharedEdges() {
        HEUtils.checkAbbyNormal(this);

        return HEUtils.buildUnmodifiableCollectionIterator(primGetSharedEdges());
    }

    List<CyEdge> primGetSharedEdges() {
        final List<CyEdge> shared = new ArrayList<CyEdge>(0);

        for (CyEdge edge : myEdges) {
            if (primIsSharedEdge(edge)) {
                shared.add(edge);
            }
        }

        return shared;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isSharedEdge(final CyEdge edge) {
        HEUtils.checkAbbyNormal(this);

        return primIsSharedEdge(edge);
    }

    private boolean primIsSharedEdge(final CyEdge edge) {
        return (manager.isConnectorNode(primGetNode(edge),
                                         null));
    }

    private void startInCyNetwork(final CyNetwork net) {
        // No need to check for duplicates since _edges will never
        // contain duplicates (false as last param):
        //        MapUtils.addListValuesToMap(_net_to_edges_map, net, _edges.iterator(),
        //                                    false);
        nets.add(net);
        manager.addToCyNetwork(net, this);
    }

    /**
     * {@inheritDoc}
     */
    public boolean removeFromNetwork(final CyNetwork net) {
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
    void removeFromNetworkBookkeeping(final CyNetwork net,
                                                final Set<GraphObject> toIgnoreForDeletion) {
        toIgnoreForDeletion.add(myConnectorNode);
        final BookkeepingItem bkItem = new BookkeepingItem(toIgnoreForDeletion, net);
        primRemoveFromNetwork(net, bkItem);
    }

    private void primRemoveFromNetwork(final CyNetwork net, final BookkeepingItem bkItem) {
        if ((net == null) || (nets.size() < 2)) {
            primDestroy(true, bkItem);

            return;
        }

        // we have a value to remove, fire events:
        manager.fireChangeEvent(this, EventNote.Type.HYPEREDGE,
                                 EventNote.SubType.REMOVED, net);
        // Must be very careful where this call is located.
        // The edge-node information must still be available at time of call:
        manager.removeFromNetwork(net, this, bkItem);
        nets.remove(net);
    }

    /**
     * {@inheritDoc}
     */
    public Iterator<String> getAllEdgeTypes() {
        HEUtils.checkAbbyNormal(this);

        // in future size to be max number of edge types:
        final List<String> eTypes = new ArrayList<String>();
        String       eType;

        for (CyEdge edge : myEdges) {
            eType = HEUtils.getEdgeInteractionType(edge);

            if (!eTypes.contains(eType)) {
                eTypes.add(eType);
            }
        }

        return HEUtils.buildUnmodifiableCollectionIterator(eTypes);
        // return Collections.unmodifiableList(eTypes).iterator();
    }

    //    public int getNumNodes ()
    //    {
    //        HEUtils.checkAbbyNormal (this);
    //        return _node_to_edges_map.size ();
    //    }

    /**
     * {@inheritDoc}
     */
    public int getNumNodes() {
        HEUtils.checkAbbyNormal(this);

        return nodeToEdgesMap.size();
    }

    /**
     * {@inheritDoc}
     */
    public int getNumEdges() {
        HEUtils.checkAbbyNormal(this);

        return myEdges.size();
    }

    /**
     * {@inheritDoc}
     */
    public Iterator<CyNode> getNodes(final String edgeIType) {
        HEUtils.checkAbbyNormal(this);

        if (edgeIType == null) {
            // return all nodes:
            return HEUtils.buildUnmodifiableCollectionIterator(nodeToEdgesMap.keySet());
            //            return Collections.unmodifiableSet(_node_to_edges_map.keySet())
            //                              .iterator();
        }

        final List<CyNode> matchNodes = new ArrayList<CyNode>(myEdges.size());
        CyNode       cyNode;

        for (CyEdge edge : myEdges) {
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

    /**
     * {@inheritDoc}
     */
    public Iterator<CyNode> getNodesByEdgeTypes(final Collection<String> edgeITypes) {
        HEUtils.checkAbbyNormal(this);

        return HEUtils.buildUnmodifiableCollectionIterator(primGetNodesByEdgeTypes(edgeITypes));
    }

    Collection<CyNode> primGetNodesByEdgeTypes(final Collection<String> edgeITypes) {
        if (edgeITypes == null) {
            // return all nodes:
            return nodeToEdgesMap.keySet();
        }

        final List<CyNode> matchNodes = new ArrayList<CyNode>(myEdges.size());
        CyNode       cyNode;

        for (CyEdge edge : myEdges) {
            if (edgeITypes.contains(HEUtils.getEdgeInteractionType(edge))) {
                cyNode = primGetNode(edge);

                if (!matchNodes.contains(cyNode)) {
                    matchNodes.add(cyNode);
                }
            }
        }

        return matchNodes;
    }

    /**
     * {@inheritDoc}
     */
    public Iterator<CyEdge> getEdges(final CyNode node) {
        HEUtils.checkAbbyNormal(this);

        if (node == null) {
            // return Collections.unmodifiableSet(_edges).iterator();
            return HEUtils.buildUnmodifiableCollectionIterator(myEdges);
        }

        // return Collections.unmodifiableList(primGetEdges(node)).iterator();
        return HEUtils.buildUnmodifiableCollectionIterator(primGetEdges(node));
    }

    // ASSUME: node is non-null:
    List<CyEdge> primGetEdges(final CyNode node) {
        final List<CyEdge> edges = nodeToEdgesMap.get(node);

        if (edges == null) {
            // no match found
            return (new ArrayList<CyEdge>(0));
        }

        return edges;
    }

    /**
     * {@inheritDoc}
     */
    public CyEdge getAnEdge(final CyNode node) {
        HEUtils.checkAbbyNormal(this);

        return primGetAnEdge(node);
    }

    private CyEdge primGetAnEdge(final CyNode node) {
        final List<CyEdge> edges = nodeToEdgesMap.get(node);

        if (edges != null) {
            return edges.get(0);
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasMultipleEdges(final CyNode node) {
        HEUtils.checkAbbyNormal(this);

        final List<CyEdge> edges = nodeToEdgesMap.get(node);

        if (edges == null) {
            return false;
        }

        return (edges.size() > 1);
    }

    /**
     * {@inheritDoc}
     */
    public CyNode getConnectorNode() {
        HEUtils.checkAbbyNormal(this);

        return myConnectorNode;
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasConnectorNode(final CyNode node) {
        HEUtils.checkAbbyNormal(this);

        return (myConnectorNode == node);
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasNode(final CyNode node) {
        HEUtils.checkAbbyNormal(this);

        return (primGetAnEdge(node) != null);
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasEdge(final CyEdge edge) {
        HEUtils.checkAbbyNormal(this);

        return primHasEdge(edge);
    }

    private boolean primHasEdge(final CyEdge edge) {
        return myEdges.contains(edge);
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasEdgeOfType(final String edgeIType) {
        HEUtils.checkAbbyNormal(this);

        if (edgeIType == null) {
            return false;
        }

        for (CyEdge edge : myEdges) {
            if (edgeIType.equals(HEUtils.getEdgeInteractionType(edge))) {
                return true;
            }
        }

        return false;
    }

    /**
     * {@inheritDoc}
     */
    public boolean removeNode(final CyNode node) {
        HEUtils.checkAbbyNormal(this);

        final List<CyEdge> edges = nodeToEdgesMap.get(node);

        if (edges == null) {
            // node is null or not in this HyperEdge:
            return false;
        }

        // first determine if this whole HyperEdge will really be
        // destroyed vs just removing some edges. This occurs is
        // when there aren't enough edges left in this HyperEdge
        // after the removal of the edge.  If so, avoid
        // removing some CyEdges first--just have one destroy event:
        if ((myEdges.size() - edges.size()) < getMinimumNumberEdges()) {
            primDestroy(true, null);
        } else {
            primRemoveNode(node, true, null);
        }

        return true;
    }

    /**
     * {@inheritDoc}
     */
    public boolean removeEdge(final CyEdge edge) {
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

    void removeEdgeBookkeeping(final CyEdge edge, final CyNetwork net,
                                         final Set<GraphObject> toIgnoreForDeletion) {
        removeOrDeleteEdge(edge,
                           true,
                           new BookkeepingItem(toIgnoreForDeletion, net));
    }

    void removeOrDeleteEdge(final CyEdge edge, final boolean fireEvents,
                                      final BookkeepingItem bkItem) {
        // first determine if this whole HyperEdge will really be
        // destroyed vs just removing an edge. This occurs is
        // when there aren't enough edges left in this HyperEdge
        // after the removal of the edge.  If so, avoid
        // removing some CyEdges first--just have one destroy event:
        if (myEdges.size() <= getMinimumNumberEdges()) {
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

    /**
     * {@inheritDoc}
     */
    public CyEdge addEdge(final CyNode node, final String edgeIType) {
        HEUtils.checkAbbyNormal(this);
        Validate.notNull(node, "node");
        Validate.notNull(edgeIType, "edgeIType");

        if (manager.isConnectorNode(node, null)) {
            HEUtils.throwIllegalArgumentException("HyperEdge.addEdge(): node is ConnectorNode.");
        }

        final List<CyEdge> matches = primGetEdges(node);

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

    /**
     * {@inheritDoc}
     */
    //    public CyEdge connectHyperEdges(HyperEdge targetHE, String fromEdgeIType,
    //                                    String toEdgeIType) {
    public CyEdge connectHyperEdges(final HyperEdge targetHE, final String edgeIType) {
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
        final Iterator<CyNetwork> targetNets = targetHE.getNetworks();

        if (targetNets.hasNext()) {
            net = targetNets.next();

            if (targetNets.hasNext()) {
                // then targetNet has >= 2 nets, error:
                net = null;
            }
        }

        if ((nets.size() != 1) || (net == null) || (!nets.contains(net))) {
            HEUtils.throwIllegalArgumentException("HyperEdge.connectHyperEdges(): can't connect" +
                                                  this + " to " + targetHE +
                                                  "because they don't belong to one, and the same, CyNetwork.");
        }

        // Check if edge already exists:
        final List<CyEdge> existingEdges = primGetEdges(targetHE.getConnectorNode());

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
        final CyEdge edge = primAddEdge(targetHE.getConnectorNode(),
                                  // fromEdgeIType,
        edgeIType,
                                  true);
        ((HyperEdgeImpl) targetHE).primAddEdge(myConnectorNode, edge,
                                               // toEdgeIType,
        edgeIType, true);

        return edge;
    }

    /**
     * {@inheritDoc}
     */
    public Map<HyperEdge, HyperEdge> copy(final CyNetwork net, final EdgeFilter filter) {
        HEUtils.checkAbbyNormal(this);
        HEUtils.notNull(net, "net");
        HEUtils.notNull(filter, "filter");

        final String checkMsg = copyOK(filter,
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
        final Map<HyperEdge, HyperEdge> copyMap = new HashMap<HyperEdge, HyperEdge>();
        // will side-effect copyMap:
        primCopy(net,
                 filter,
                 copyMap,
                 new HashMap<CyEdge, CyEdge>());

        // Now go thru the copyMap and get the values and fire events for
        // creation of each new HyperEdge. We need to wait til all the
        // copies of all shared HyperEdges are created so we don't have
        // half completed objects during event callbacks.
        final Iterator<HyperEdge> newHEs = copyMap.values().iterator();

        while (newHEs.hasNext()) {
            final HyperEdge newHE = newHEs.next();
            manager.fireNewHObjEvent(newHE);
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
    private void primCopy(final CyNetwork net, final EdgeFilter filter,
                          // boolean copySharedHyperEdges,
    final Map<HyperEdge, HyperEdge> copyMap, final Map<CyEdge, CyEdge> sharedEdgesMap) {
        final List<CyEdge>  sharedEdges = primGetSharedEdges();
        final HyperEdgeImpl newHE = new HyperEdgeImpl();
        copyMap.put(this, newHE);

        for (CyEdge edge : myEdges) {
            if (!filter.includeEdge(this, edge)) {
                continue;
            }

            if (sharedEdges.contains(edge)) {
                final HyperEdgeImpl otherHE = (HyperEdgeImpl) manager.getHyperEdgeForConnectorNode(primGetNode(edge));

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
                final CyEdge edgeToShare = sharedEdgesMap.get(edge);

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
                    final CyEdge edgeCopy = copyEdge(newHE,
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
        copyExtraAttributesAndEdges(myConnectorNode,
                                    newHE.getConnectorNode(),
                                    net);
        // Don't fire events here because ajoining HyperEdges may not
        // be complete yet:
        newHE.constructorSupport(net, false);
    }

    private String copyOK(final EdgeFilter filter, final Set<HyperEdge> visitedHEs) {
        if (visitedHEs.contains(this)) {
            // we'd done already, skip:
            return null;
        }

        visitedHEs.add(this);

        int          edgeCount   = 0;
        final List<CyEdge> sharedEdges = primGetSharedEdges();

        for (CyEdge edge : myEdges) {
            if (filter.includeEdge(this, edge)) {
                edgeCount++;

                if (sharedEdges.contains(edge)) {
                    final HyperEdgeImpl otherHE    = (HyperEdgeImpl) manager.getHyperEdgeForConnectorNode(primGetNode(edge));
                    final String        otherHEMsg = otherHE.copyOK(filter, visitedHEs);

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
    private CyEdge copyEdge(final HyperEdgeImpl newHE, final CyNode toCopyNode,
                            final CyEdge toCopyEdge) {
        // the other endpoint from this connectorNode:
        // CyNode toCopyNode = primGetNode(toCopyEdge);
        final CyEdge copiedEdge = newHE.primAddEdge(toCopyNode,
                                              HEUtils.getEdgeInteractionType(toCopyEdge),
                                              false);

        // Now copy any extra attributes on toCopyEdge to copiedEdge:
        // we don't need to 'purge' = true because copyNode is new.
        CyAttributesUtils.copyAttributes(toCopyEdge.getIdentifier(),
                                         copiedEdge.getIdentifier(),
                                         edgeAttrs,
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

    private void copyExtraAttributesAndEdges(final CyNode originalNode,
                                             final CyNode copyNode, final CyNetwork net) {
        // copy connectorNode attributes:
        // we don't need purge=true because copyNode is new:
        CyAttributesUtils.copyAttributes(originalNode.getIdentifier(),
                                         copyNode.getIdentifier(),
                                         nodeAttrs,
                                         false);

        final int[]  adjacentEdges = net.getAdjacentEdgeIndicesArray(net.getIndex(originalNode),
                                                               true,
                                                               true,
                                                               true);
        CyEdge edge;

        for (int edgeIdx : adjacentEdges) {
            edge = (CyEdge) net.getEdge(edgeIdx);

            if (!manager.isHyperEdgeEdge(edge, null)) {
                // add an equivalent edge to the copyNode:
                final String itype = edgeAttrs.getStringAttribute(edge.getIdentifier(),
                                                              Semantics.INTERACTION);

                if (itype != null) {
                    final CyEdge newEdge = Cytoscape.getCyEdge(edge.getSource(),
                                                         edge.getTarget(),
                                                         Semantics.INTERACTION,
                                                         itype,
                                                         true,
                                                         edge.isDirected());
                    // we don't need purge=true because copyNode is new:
                    CyAttributesUtils.copyAttributes(edge.getIdentifier(),
                                                     newEdge.getIdentifier(),
                                                     edgeAttrs,
                                                     false);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean isConnected(final CyNode node1, final CyNode node2) {
        HEUtils.checkAbbyNormal(this);

        return ((primGetAnEdge(node1) != null) &&
               (primGetAnEdge(node2) != null));
    }

    /**
     * {@inheritDoc}
     */
    public void destroy() {
        HEUtils.checkAbbyNormal(this);
        primDestroy(true, null);
    }

    /**
     * {@inheritDoc}
     */
    public LifeState getState() {
        return state;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isState(final LifeState ls) {
        return (getState() == ls);
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        final StringBuilder result = new StringBuilder();
        result.append("[" + HEUtils.getAbrevClassName(this) + '.' + hashCode());
        //        result.append (" edge: '" + _edge + "'");
        //        result.append (" type: '" + _type + "'");
        result.append(" nodes: (" + primGetNumNodes() + ')');
        result.append(" edges: (" + primGetNumEdges() + ')');
        result.append(" name: '" + getName() + SINGLE_QUOTE);
        result.append(" directed: " + isDirected());
        result.append(" UUID: '" + getIdentifier() + SINGLE_QUOTE);
        result.append(" state: " + getState());
        result.append(']');

        return result.toString();
    }

    /**
     * {@inheritDoc}
     */
    public HyperEdgeManager getHyperEdgeManager() {
        return manager;
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        HEUtils.checkAbbyNormal(this);

        return getConnectorNodeName();
    }

    /**
     * {@inheritDoc}
     */
    public String setName(final String newName) {
        HEUtils.checkAbbyNormal(this);

        final String oldName = primSetName(newName);

        if (HEUtils.stringEqual(newName, oldName, true)) {
            return oldName;
        }

        // // new value is different than old:
        // primSetDirty(true, true);

        // CAUTION: Don't synchronize this whole method or place the
        //          following line in a synchronized block!
        //          Otherwise the listeners could possibly call back
        //          into this class causing a deadlock!
        manager.fireChangeEvent(this, EventNote.Type.NAME,
                                 EventNote.SubType.CHANGED, oldName);

        return oldName;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isDirected() {
        HEUtils.checkAbbyNormal(this);

        final Boolean retVal = nodeAttrs.getBooleanAttribute(myConnectorNode.getIdentifier(),
                                                          DIRECTED_ATTRIBUTE_NAME);

        return (retVal != null) && retVal.booleanValue();
    }

    /**
     * {@inheritDoc}
     */
    public boolean setDirected(final boolean netState) {
        HEUtils.checkAbbyNormal(this);

        final boolean lastVal = isDirected();

        if (netState == lastVal) {
            // no change:
            return lastVal;
        }

        // new value is different than old:
        if (!netState) {
            removeConnectorNodeDirected();
        } else {
            nodeAttrs.setAttribute(myConnectorNode.getIdentifier(),
                                     DIRECTED_ATTRIBUTE_NAME,
                                     Boolean.TRUE);
        }

        // primSetDirty(true, true);

        // CAUTION: Don't synchronize this whole method or place the
        //          following line in a synchronized block!
        //          Otherwise the listeners could possibly call back
        //          into this class causing a deadlock!
        manager.fireChangeEvent(this, EventNote.Type.DIRECTED,
                                 EventNote.SubType.CHANGED, null);

        return lastVal;
    }

    /**
     * {@inheritDoc}
     */
    public final int getMinimumNumberEdges() {
        return 2;
    }

    /**
     * {@inheritDoc}
     */
    public Iterator<CyNetwork> getNetworks() {
        HEUtils.checkAbbyNormal(this);

        // return Collections.unmodifiableSet(primGetNetworks()).iterator();
        return HEUtils.buildUnmodifiableCollectionIterator(primGetNetworks());
    }

    // used by the HyperEdgeManager:
     Set<CyNetwork> primGetNetworks() {
        return nets;
    }

    /**
     * Used for HyperEdge persistence in restoring a HyperEdge from
     * underlying Cytoscape CyNodes, CyEdges, and attributes.
     */
    private void reconstructFromNodesEdgesAndAttributes(final CyNode connectorNode,
                                                        final CyNetwork net) {
        myConnectorNode = connectorNode;
        // &&& HACK Cytoscape.getCyNode(), probably called by the reader
        //     set the CANONICAL_NAME (LABEL_ATTRIBUTE_NAME) to the node id,
        //     which we don't  want for connector nodes:
	// MLC 06/21/07 BEGIN:
        // _node_attrs.deleteAttribute(connectorNode.getIdentifier(),
        //                             HyperEdgeImpl.LABEL_ATTRIBUTE_NAME);
	// MLC 04/07/09 BEGIN:
	if (isStandardLabel (connectorNode)) {
	    // Only delete the label if it is the standard generated label (canonicalName)
	    // otherwise, if it has a custom value, *don't* delete the value:
	    HEUtils.deleteAttribute(nodeAttrs, connectorNode.getIdentifier(),
				    HyperEdgeImpl.LABEL_ATTRIBUTE_NAME);
	}
        // HEUtils.deleteAttribute(nodeAttrs, connectorNode.getIdentifier(),
        //                        HyperEdgeImpl.LABEL_ATTRIBUTE_NAME);
	// MLC 04/07/09 END.
	// MLC 06/21/07 END.
        // compute edges:
        final int[]  adjacentEdges = net.getAdjacentEdgeIndicesArray(net.getIndex(connectorNode),
                                                               true,
                                                               true,
                                                               true);
        CyEdge edge;

        for (int j = 0; j < adjacentEdges.length; j++) {
            edge = (CyEdge) net.getEdge(adjacentEdges[j]);
            // HEUtils.log("considering edge " + edge.getIdentifier());

            if (manager.isHyperEdgeEdge(edge, null)) {
                nets.add(net);
                // HEUtils.log("bookkeeping edge " + edge.getIdentifier());
                performEdgeBookkeeping(primGetNode(edge),
                                       edge,
                                       net,
                                       false);
            }
        }
    }

    // MLC 04/07/09 BEGIN:
    // Cytoscape generates a canonicalName label for each Node, which is the Node's ID.
    // Return true iff the label is this standard label.
    private boolean isStandardLabel (final CyNode connectorNode) {
	String existingLabel = nodeAttrs.getStringAttribute (connectorNode.getIdentifier(),
							     HyperEdgeImpl.LABEL_ATTRIBUTE_NAME);
	return ((existingLabel != null) &&
		existingLabel.equals (connectorNode.getIdentifier()));
    }
    // MLC 04/07/09 END.

    // This HyperEdge's connector node may not belong to net, but if it does
    // add all the appropriate info about the edges and nodes for this net:
    private void addCyNetworkToReconstruction(final CyNetwork net) {
        final int[] adjacentEdges = net.getAdjacentEdgeIndicesArray(net.getIndex(myConnectorNode),
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

            if (manager.isHyperEdgeEdge(edge, null)) {
                nets.add(net);
                performEdgeBookkeeping(primGetNode(edge),
                                       edge,
                                       net,
                                       false);
            }
        }
    }

    private static boolean isConnectorNodeForReconstruction(final CyNode node,
                                                            final CyNetwork net) {
        return ("true".equals(nodeAttrs.getStringAttribute(
                                                             node.getIdentifier(),
                                                             HyperEdgeImpl.IS_CONNECTOR_NODE_ATTRIBUTE_NAME)));
    }

    // used during reconstruction of HyperEdges from read in networks:
    private String createHEUUIDFromConnectorNodeUUID(final CyNode connectorNode) {
        final String cnUUID = connectorNode.getIdentifier();
        final String heUUID = cnUUID.substring(CONNECTOR_NODE_UUID_PREFIX.length() +
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
        myConnectorNode = HEUtils.createConnectorNode(createConnectorNodeUUIDFromHEUUID());
    }

    private String createConnectorNodeUUIDFromHEUUID() {
        HEUtils.notNull(myUuid, "_uuid");

        return CONNECTOR_NODE_UUID_PREFIX + '-' + myUuid;
    }

    // Used for copying ConnectorNodes when reloading shared
    // hyperedges:
     static String createConnectorNodeUUIDWithoutHE() {
        return HEUtils.generateUUID(CONNECTOR_NODE_UUID_PREFIX);
    }

    // ASSUME: All error checking of params has been done
     CyEdge primAddEdge(final CyNode node, final String edgeIType,
                                 final boolean fireEvents) {
        CyEdge edge;

        if (treatAsTarget(edgeIType)) {
            edge = HEUtils.createHEEdge(myConnectorNode, node, edgeIType);
        } else {
            edge = HEUtils.createHEEdge(node, myConnectorNode, edgeIType);
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
     boolean treatAsTarget(final String edgeIType) {
        final EdgeRole er = HyperEdgeFactory.INSTANCE.getEdgeTypeMap().get(edgeIType);

        return ((er != null) && (er == EdgeRole.TARGET));
    }

    private void primAddEdge(final CyNode node, final CyEdge edge, final String edgeIType,
                             final boolean fireEvents) {
        // ASSUME: edge may already exist from a previous call.
        //        setEdgeInteractionType(edge, edgeIType);
        setEdgeCanonicalName(edge, edgeIType);
        addNodeEntityTypeWhenNeeded(node);
        performEdgeBookkeeping(node, edge, null, fireEvents);
    }

    private void performEdgeBookkeeping(final CyNode node, final CyEdge edge,
                                        final CyNetwork net, final boolean fireEvents) {
        // ASSUME: node and edge may already exist in this HE within another
        //         net.
        // ASSUME: No info exists for net yet (net may be new):
        MapUtils.addListValueToMap(nodeToEdgesMap, node, edge, true);

        // edges doesn't allow duplicates via add:
        myEdges.add(edge);

        manager.registerEdge(edge, this, net);

        // primSetDirty(true, fireEvents);
        if (fireEvents) {
            manager.fireChangeEvent(this, EventNote.Type.EDGE,
                                     EventNote.SubType.ADDED, edge);
        }
    }

    private int primGetNumEdges() {
        return myEdges.size();
    }

    private int primGetNumNodes() {
        return nodeToEdgesMap.size();
    }

    // ASSUME: node belongs to this HyperEdge
    // ASSUME: HyperEdge deletion is determined and performed
    //         elsewhere.
    private void primRemoveNode(final CyNode node, final boolean fireEvents,
                                final BookkeepingItem bkItem) {
        final List<CyEdge> edges = nodeToEdgesMap.get(node);

        // We will really be removing some number of CyEdges:
        // Use a copy of the edges, since the edge data structures
        // will change:
        final List<CyEdge> edgesCopy = new ArrayList<CyEdge>(edges.size());
        edgesCopy.addAll(edges);

        for (CyEdge edge : edgesCopy) {
            // we know this hyperedge will not be destroyed:
            primRemoveEdge(node, edge, fireEvents, bkItem);
        }
    }

    private void primRemoveEdge(final CyNode node, final CyEdge edge, final boolean fireEvents,
                                final BookkeepingItem bkItem) {
        if (fireEvents) {
            manager.fireChangeEvent(this, EventNote.Type.EDGE,
                                     EventNote.SubType.REMOVED, edge);
        }

        // Must be very careful where this call is located.
        // The edge-node information must still be available at time of call:
        manager.unregisterEdge(edge, this);
        myEdges.remove(edge);

        // Remove needed attributes:
        removeGeneratedEdgeAttributes(edge);
        // update the _node_to_edges_map:
        MapUtils.removeCollectionValueFromMap(nodeToEdgesMap, node, edge);
        // Now delete the CyEdge from Cytoscape when we aren't already doing it:
        removeUnderlyingEdge(edge, bkItem);

        // MapUtils.removeCollectionValueFromMap(_net_to_edges_map, net, edge);
        final HyperEdgeImpl otherHE = (HyperEdgeImpl) manager.getHyperEdgeForConnectorNode(node);

        if ((otherHE != null) && (otherHE.primHasEdge(edge))) {
            BookkeepingItem realBkItem = bkItem;
            // The node we removed was a connectorNode of another
            // HyperEdge that contains edge.
            // remove this edge from the other HyperEdge, if there:
            // Since we are removing edge in this HyperEdge,
            // it will be definitely be gone for otherHE.
            // Avoid attempting to redelete edge in otherHE
            // by ensuring we always have a BookkeepingItem:
            if (realBkItem == null) {
                // Because this HyperEdge has a shared edge, it can
                // only be in one Network. The network will 
                // be the first element of the iterator:
                final Iterator<CyNetwork> netsIt   = nets.iterator();
                final Set<GraphObject>    toIgnore = new HashSet<GraphObject>();

                realBkItem = new BookkeepingItem(toIgnore,
                                             netsIt.next());
            }

            // ensure edge is on the list--will not duplicate since it is a Set:
            realBkItem.getItems().add(edge);

            otherHE.removeOrDeleteEdge(edge, fireEvents, realBkItem);
            //            otherHE.primRemoveEdge(otherHE.primGetNode(edge),
            //                                   edge,
            //                                   fireEvents);
        }
    }

    // Remove the given edge from its underlying representation (e.g.,
    // Cytoscape) in all CyNetworks for which it belongs:
    private void removeUnderlyingEdge(final CyEdge edge, final BookkeepingItem bkItem) {
        //        int edgeIdx = Cytoscape.getRootGraph().getIndex(edge);
        for (CyNetwork net : nets) {
            // only disregard deleting the specific item we are doing bookkeeping on.
            // All other items delete:
            if ((bkItem == null) || (bkItem.getNetwork() != net) ||
                (!bkItem.getItems().contains(edge))) {
                manager.removeUnderlyingEdge(edge, net);
            }

            //            // TODO change set_remove to true if removeEdge() is made
            //            // clear that it works on one network or across networks:
            //            net.removeEdge(edgeIdx, false);
            //            // net.hideEdge(edge);
        }
    }

    private void removeUnderlyingNode(final CyNode node, final BookkeepingItem bkItem) {
        // int nodeIdx = Cytoscape.getRootGraph().getIndex(node);
        for (CyNetwork net : nets) {
            // For each network, if nothing is left connected to node, then remove it:
	    manager.removeNodeFromNet (node, net , bkItem);
        }
    }

     void primDestroy(final boolean fireEvents, final BookkeepingItem bkItem) {
        // Allow multiple calls to delete via blanket obj deleter:
        if (isState(LifeState.DELETED)) {
            return;
        }

        primSetState(LifeState.DELETION_IN_PROGRESS);

        if (fireEvents) {
            manager.fireDeleteEvent(this);
        }

        removeNodeAndEdgeContent(bkItem);

        // Make sure this follows removal of CyEdges and CyNodes, since CyEdges may use
        // HyperGraphManager maps about this HyperEdge:
        manager.unregisterHyperEdge(this);

        removeConnectorNodeAndAttributes(bkItem);

        // We leave the connector node alone, like any other node:
        // HEUtils.removeNode(_connector_node);
        myEdges             = null; // help GC
        nodeToEdgesMap = null; // help GC
                                   // _net_to_edges_map   = null; // help GC

        nets              = null; // help GC
                                   //        _change_listener_store = null; // help GC

        myConnectorNode    = null; // help GC
        primDeleteSubclass();
    }

    //    protected void removeGeneratedAttributes() {
    //        removeConnectorNodeAttributes();
    //
    //        for (CyEdge edge : _edges) {
    //            removeGeneratedEdgeAttributes(edge);
    //        }
    //    }
    private void removeGeneratedEdgeAttributes(final CyEdge edge) {
        removeEdgeInteractionType(edge);
        removeEdgeHyperEdgeTag(edge);
        removeEdgeCanonicalName(edge);
    }

    private void removeConnectorNodeAndAttributes(final BookkeepingItem bkItem) {
        removeConnectorNodeName();
        removeConnectorNodeDirected();
        removeConnectorNodeEntityType();
        removeUnderlyingNode(myConnectorNode, bkItem);
    }

    /**
     * Remove all nodes and edges within a HyperEdge.
     * ASSUME: Only called when whole HyperEdge is being deleted.
     */
    private void removeNodeAndEdgeContent(final BookkeepingItem bkItem) {
        // cleanup all the datastructures for the stuff we
        // contain--nodes and edges:
        // Since the _node_to_edges_map will change via various
        // methods called, copy nodes:
        for (CyNode node : new ArrayList<CyNode>(nodeToEdgesMap.keySet())) {
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
    private void removeEdgeInteractionType(final CyEdge edge) {
        //        _edge_attrs.deleteAttribute(edge.getIdentifier(),
        //                                    _edgeInteractionAttributeName);
	// MLC 06/21/07 BEGIN:
        // _edge_attrs.deleteAttribute(edge.getIdentifier(),
        //                            Semantics.INTERACTION);
        HEUtils.deleteAttribute (edgeAttrs, edge.getIdentifier(),
				 Semantics.INTERACTION);
	// MLC 06/21/07 END.
    }

    private void removeEdgeHyperEdgeTag(final CyEdge edge) {
	// MLC 06/21/07 BEGIN:
        // _edge_attrs.deleteAttribute(edge.getIdentifier(),
        //                             HYPEREDGE_EDGE_TAG_NAME);
        HEUtils.deleteAttribute(edgeAttrs, edge.getIdentifier(),
				HYPEREDGE_EDGE_TAG_NAME);
	// MLC 06/21/07 END.
    }

    // based on Cytoscape.getCyEdge() internal setting of LABEL:
    private void setEdgeCanonicalName(final CyEdge edge, final String edgeIType) {
        edgeAttrs.setAttribute(edge.getIdentifier(),
                                 AttributeConstants.MONIKER,
                                 edgeIType);
        edgeAttrs.setAttribute(edge.getIdentifier(),
                                 LABEL_ATTRIBUTE_NAME,
                                 edgeIType);
    }

    // MAY BE USED IN THE FUTURE:
    // Only add entity type information if not already there.
    // Since Node may be used in many HyperEdges, node may already
    // have the attribute. Note that nodes that are shared will work
    // since the node will be connector node first, then be shared.
    private void addNodeEntityTypeWhenNeeded(final CyNode node) {
        if (nodeAttrs.getStringAttribute(node.getIdentifier(),
                                           ENTITY_TYPE_ATTRIBUTE_NAME) == null) {
            // no attribute value exists:
            nodeAttrs.setAttribute(node.getIdentifier(),
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
        return nodeAttrs.getStringAttribute(myConnectorNode.getIdentifier(),
                                              AttributeConstants.MONIKER);
    }

    private void setConnectorNodeName(final String newName) {
        if (newName == null) {
            removeConnectorNodeName();
        } else {
            nodeAttrs.setAttribute(myConnectorNode.getIdentifier(),
                                     AttributeConstants.MONIKER,
                                     newName);
            // will be replaced when there is an alternative:
            nodeAttrs.setAttribute(myConnectorNode.getIdentifier(),
                                     LABEL_ATTRIBUTE_NAME,
                                     newName);
        }
    }

    private void removeConnectorNodeEntityType() {
        if (myConnectorNode != null) {
	    // MLC 06/21/07 BEGIN:
            // _node_attrs.deleteAttribute(_connector_node.getIdentifier(),
            //                            ENTITY_TYPE_ATTRIBUTE_NAME);
            HEUtils.deleteAttribute(nodeAttrs,
				    myConnectorNode.getIdentifier(),
				    ENTITY_TYPE_ATTRIBUTE_NAME);
	    // MLC 06/21/07 END.
        }
    }

    private void removeConnectorNodeName() {
        if (myConnectorNode != null) {
            final String name = getName();

            if (name != null) {
		// MLC 06/21/07 BEGIN:
		//                _node_attrs.deleteAttribute(_connector_node.getIdentifier(),
		//                                            AttributeConstants.MONIKER);
		//                // will be replaced when there is an alternative:
		//                _node_attrs.deleteAttribute(_connector_node.getIdentifier(),
		//                                            LABEL_ATTRIBUTE_NAME);
                HEUtils.deleteAttribute(nodeAttrs,
					myConnectorNode.getIdentifier(),
					AttributeConstants.MONIKER);
                // will be replaced when there is an alternative:
                HEUtils.deleteAttribute(nodeAttrs,
					myConnectorNode.getIdentifier(),
					LABEL_ATTRIBUTE_NAME);
	    // MLC 06/21/07 END.
            }
        }
    }

    private void removeConnectorNodeDirected() {
        if ((myConnectorNode != null) && (isDirected())) {
	    // MLC 06/21/07 BEGIN:
            // _node_attrs.deleteAttribute(_connector_node.getIdentifier(),
            //                             DIRECTED_ATTRIBUTE_NAME);
            HEUtils.deleteAttribute(nodeAttrs,
				    myConnectorNode.getIdentifier(),
				    DIRECTED_ATTRIBUTE_NAME);
	    // MLC 06/21/07 END.
        }
    }

    private void removeEdgeCanonicalName(final CyEdge edge) {
	// MLC 06/21/07 BEGIN:
	//        _edge_attrs.deleteAttribute(edge.getIdentifier(),
	//                                    AttributeConstants.MONIKER);
	//        // will be replaced when there is an alternative:
	//        _edge_attrs.deleteAttribute(edge.getIdentifier(),
	//                                    LABEL_ATTRIBUTE_NAME);
        HEUtils.deleteAttribute(edgeAttrs, edge.getIdentifier(),
                                AttributeConstants.MONIKER);
        // will be replaced when there is an alternative:
        HEUtils.deleteAttribute(edgeAttrs, edge.getIdentifier(),
				LABEL_ATTRIBUTE_NAME);
	// MLC 06/21/07 END.
    }

    // Used for temporarily marking objects for doing things like very
    // efficient intersection algorithms:
    boolean isMarked() {
        return marked;
    }

    // Used for temporarily marking objects for doing things like very
    // efficient intersection algorithms:
    void setMarked(final boolean markState) {
        marked = markState;
    }

    HyperEdgeManagerImpl getManager() {
        return manager;
    }

    private void primSetState(final LifeState newState) {
        state = newState;
    }

    boolean primDeleteSubclass() {
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

    /**
     * {@inheritDoc}
     */
    public CyNode getNode(final CyEdge edge) {
        HEUtils.checkAbbyNormal(this);

        if (!primHasEdge(edge)) {
            HEUtils.throwIllegalArgumentException("HyperEdge.getNode(): edge doesn't belong to this HyperEdge.");
        }

        return primGetNode(edge);
    }

    // Assumes: edge is a member of this HyperEdge.
    CyNode primGetNode(final CyEdge edge) {
        final CyNode source = (CyNode) edge.getSource();

        if (source == myConnectorNode) {
            return (CyNode) edge.getTarget();
        }

        return source;
    }

    /**
     * {@inheritDoc}
     */
    public String getIdentifier() {
        return myUuid;
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
    //    public boolean setDirty(boolean netState) {
    //        HEUtils.checkAbbyNormal(this);
    //
    //        return primSetDirty(netState, true);
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
