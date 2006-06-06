/**  Copyright (c) 2003 Institute for Systems Biology
 **  This program is free software; you can redistribute it and/or modify
 **  it under the terms of the GNU General Public License as published by
 **  the Free Software Foundation; either version 2 of the License, or
 **  any later version.
 **
 **  This program is distributed in the hope that it will be useful,
 **  but WITHOUT ANY WARRANTY; without even the implied warranty of
 **  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  The software and
 **  documentation provided hereunder is on an "as is" basis, and the
 **  Institute for Systems Biology has no obligations to provide maintenance, 
 **  support, updates, enhancements or modifications.  In no event shall the
 **  Institute for Systems Biology be liable to any party for direct, 
 **  indirect, special,incidental or consequential damages, including 
 **  lost profits, arising out of the use of this software and its 
 **  documentation, even if the Institute for Systems Biology 
 **  has been advised of the possibility of such damage. See the
 **  GNU General Public License for more details.
 **   
 **  You should have received a copy of the GNU General Public License
 **  along with this program; if not, write to the Free Software
 **  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 **/

package org.isb.metanodes.model;

import java.util.*;

import giny.model.*;
import cytoscape.*;
import cytoscape.data.Semantics;
import cytoscape.data.CyAttributes;
import org.isb.metanodes.data.MetaNodeAttributesHandler;
import cern.colt.list.IntArrayList;
import cern.colt.map.AbstractIntIntMap;
import cern.colt.map.OpenIntIntHashMap;

/**
 * This class models meta-nodes by abstracting the graphs that compose
 * meta-nodes into single nodes that can be 'collapsed' or 'expanded'. Use class
 * metaNodeViewer.model.MetaNodeModelerFactory to get an instance of this class.
 * <p>
 * Edges between metaNodes can be of 3 types:<br>
 * <UL>
 * <LI>"Transferred edges" from children nodes: Edges between children nodes and other
 * non-children nodes are transfered to the parent metaNode
 * <LI>"Shared child" edges: if two metaNodes share a child node, there will be an edge between them
 * <LI>"Child of" edges: if a child node has two metaNode parents, and one of them is collapsed, and
 * the other one expanded, the child node will be visible and have an edge to the collapsed parent metaNode
 * </UL>
 * A metaNode can have a mix of the above edge types connected to it.
 * 
 * @author Iliana Avila-Campillo iavila@systemsbiology.org,
 *         iliana.avila@gmail.com
 */
public class AbstractMetaNodeModeler {

    protected static final boolean DEBUG = false;

    protected static final boolean REPORT_TIME = false;

    // Types of meta-edges
    protected static final String TRANSFERRED_EDGE_INTERACTION = "tr";
    
    protected static final String CHILD_OF_EDGE_INTERACTION = "childOf";
    
    protected static final String SHARED_CHILD_INTERACTION = "sharedChild";

    /**
     * A Map from CyNetworks to MetaNodeAttributesHandlers that are used to
     * assign names and attribute values to meta-nodes. If a network has not
     * been assigned a MetaNodeAttributesHandler, then defaultAttributesHandler
     * will be used.
     */
    protected Map networkToAttsHandler;

    /**
     * The default MetaNodeAttributesHandler.
     */
    protected MetaNodeAttributesHandler defaultAttributesHandler;

    /**
     * The RootGraph of all CyNetworks for which this modeler will be applied.
     */
    protected RootGraph rootGraph;

    // A map from metanode CyNodes to Lists of CyEdges
    // The edges have been percolated through the
    // RootGraph to model an "abstract" model
    private Map metaNodeToProcessedEdges;

    // A list of CyEdges that we have created that are
    // connected to at least one meta-node
    private List metaEdges;

    // A Map from CyNetworks to Lists of CyNodes
    // that are contained in the corresponding network
    private Map networkToNodes;

    // Whether or not multiple edges between a meta-node and another node (meta
    // or not meta)
    // should be created when abstracting the meta-node, true by default
    private boolean multipleEdges = true;
    
    // If true, "childOf" and "sharedChild" edges are created
    private boolean createMetaEdges = true;

    /**
     * Since there should only be one AbstractMetaNodeModeler per/Cytoscape, the
     * constructor is protected; use
     * <code>MetaNodeModelerFactory.getCytoscapeAbstractMetaNodeModeler();</code>
     * to get an instance.
     * 
     * @param root_graph
     *            the <code>RootGraph</code> of the <code>CyNetworks</code>s
     *            that will be modeled
     */
    protected AbstractMetaNodeModeler(RootGraph root_graph) {
        this.rootGraph = root_graph;
        this.metaNodeToProcessedEdges = new HashMap();
        this.metaEdges = new ArrayList();
        this.networkToNodes = new HashMap();
        this.networkToAttsHandler = new HashMap();
        this.defaultAttributesHandler = MetaNodeModelerFactory.DEFAULT_MN_ATTRIBUTES_HANDLER;
    }// constructor

    /**
     * Sets the default MetaNodeAttributesHandler, by default, this handler is
     * MetaNodeModelerFactory.DEFAULT_MN_ATTRIBUTES_HANDLER.
     * 
     * @param handler
     *            the handler to be used by default
     */
    public void setDefaultAttributesHandler(MetaNodeAttributesHandler handler) {
        this.defaultAttributesHandler = handler;
    }// setDefaultAttributesHandler

    /**
     * Sets the MetaNodeAttributesHandler that should be used from now on to
     * transfer node and edge attributes from children nodes and edges to
     * meta-nodes in the given CyNetwork
     * 
     * @param cy_network
     *            the CyNetwork for which the handler should be used
     * @param MetaNodeAttributesHandler
     *            the handler
     */
    public void setNetworkAttributesHandler(CyNetwork cy_network,
            MetaNodeAttributesHandler handler) {
        this.networkToAttsHandler.put(cy_network, handler);
    }// setNetworkAttributesHandler

    /**
     * Sets whether or not multiple edges between a meta-node and another node
     * (meta or not meta) should be created when abstracting the meta-node, true
     * by default
     */
    public void setMultipleEdges(boolean multiple_edges) {
        this.multipleEdges = multiple_edges;
    }// setMultipleEdges
    
    /**
     * Sets wether or not edges that represent meta-relationships should be created.
     * <p>
     * Edges representing meta-relationships are:<br>
     *<UL>
     * <LI>"Shared child" edges: if two metaNodes share a child node, there will be an edge between them
     * <LI>"Child of" edges: if a child node has two metaNode parents, and one of them is collapsed, and
     * the other one expanded, the child node will be visible and have an edge to the collapsed parent metaNode
     * </UL>
     * 
     * @param create whether or not to create meta-relationship edges
     */
    public void setCreateMetaRelationshipEdges (boolean create){
    		this.createMetaEdges = create;
    }

    /**
     * @return whether or not multiple edges between a meta-node and another
     *         node (meta or not meta) should be created when abstracting the
     *         meta-node, true by default
     */
    public boolean getMultipleEdges() {
        return this.multipleEdges;
    }// getMultipleEdges
    
    /**
     * Gets wether or not edges that represent meta-relationships should be created.
     * <p>
     * Edges representing meta-relationships are:<br>
     *<UL>
     * <LI>"Shared child" edges: if two metaNodes share a child node, there will be an edge between them
     * <LI>"Child of" edges: if a child node has two metaNode parents, and one of them is collapsed, and
     * the other one expanded, the child node will be visible and have an edge to the collapsed parent metaNode
     * </UL>
     * 
     * @return whether or not to create meta-relationship edges
     */
    public boolean getCreateMetaRelationshipEdges (){
    		return this.createMetaEdges;
    }

    /**
     * Gets the MetaNodeAttributesHandler that is being used to transfer node
     * and edge attributes from children nodes and edges to meta-nodes for the
     * given CyNetwork
     * 
     * @param cy_network
     *            the network for which to return the MetaNodeAttributesHandler
     *            in use
     */
    public MetaNodeAttributesHandler getNetworkAttributesHandler(
            CyNetwork cy_network) {
        MetaNodeAttributesHandler handler = (MetaNodeAttributesHandler) this.networkToAttsHandler
                .get(cy_network);
        if (handler == null) {
            return this.defaultAttributesHandler;
        }
        return handler;
    }// getNetworkAttributesHandler

    /**
     * Sets the RootGraph whose CyNetworks will be changed so that their
     * meta-nodes can be collapsed and expanded. Clears internal data-structures
     * for the current RootGraph.
     */
    // Should in theory not be needed, since Cytoscape only has ONE RootGraph
    // throughout execution.
    public void setRootGraph(RootGraph new_root_graph) {
        this.rootGraph = new_root_graph;
        this.metaNodeToProcessedEdges.clear();
        this.metaEdges.clear();
        this.networkToNodes.clear();
        this.networkToAttsHandler.clear();
    }// setRootGraph

    /**
     * @return the RootGraph that this AbstractMetaNodeModeler models
     */
    public RootGraph getRootGraph() {
        return this.rootGraph;
    }// getRootGraph

    /**
     * Applies the model to the CyNetwork TODO: Why does this not take a
     * CyNetwork????
     * 
     * @return false if the model could not be applied, maybe because the model
     *         had already been applied previously, true otherwise TODO:
     *         IMPLEMENT!, also, why does this method not take a CN for an
     *         argument???
     */
    public boolean applyModel() {
        return false;
    }// applyModel

    /**
     * Undos the changes made to <code>CyNetwork</code> in order to apply the
     * model.
     * 
     * @param temporary_undo
     *            whether or not the "undo" is temporary, if not temporary,
     *            then, the <code>CyNetwork</code>'s <code>RootGraph</code>
     *            will also be modified so that it is in the same state as it
     *            was before any calls to <code>applyModel()</code>
     * 
     * @return false if the undo was not successful, maybe because the model was
     *         already undone, true otherwise TODO: IMPLEMENT! also, why does
     *         this method not take a CyNetwork for an argument?
     */
    public boolean undoModel(boolean temporary_undo) {
        return false;
    }// undoModel

    /**
     * Applies the model to the given <code>CyNode</code>. Calls
     * <code>applyModel (cy_network,node,getDescendants(node_index))</code>
     * 
     * @param cy_network
     *            the <code>CyNetwork</code> that will be modified
     * @param node
     *            the <code>CyNode</code> that represents a metanode
     * 
     * @return true if the node was successfuly abstracted, false otherwise
     *         (maybe the node is not a meta-node, or the node has already been
     *         abstracted)
     */
    public boolean applyModel(CyNetwork cy_network, CyNode node) {
        ArrayList descendants = new ArrayList();
        getDescendants(node, descendants);
        return applyModel(cy_network, node, descendants);
    }// applyModel

    /**
     * Applies the model to the given <code>CyNode</code>. Use this method
     * instead of <code>applyModel(cy_network,node)</code> if the descendants
     * of the node are known.
     * 
     * @param cy_network
     *            the <code>CyNetwork</code> that will be modified
     * @param node
     *            the <code>CyNode</code> that represents a metanode
     * @param descendants
     *            a List of <code>CyNode</code>s that are descendants of the
     *            given node
     * 
     * @return true if the node was successfuly abstracted, false otherwise
     *         (maybe the node is not a meta-node, or the node has already been
     *         abstracted)
     * 
     * NOTE: Descendant nodes are all the nodes that are contained in the tree
     * rooted at node with index node_index (not only the leaves).
     */
    public boolean applyModel(CyNetwork cy_network, CyNode node,
            List descendants) {

        long startTime;
        if (REPORT_TIME)
            startTime = System.currentTimeMillis();
        if (DEBUG)
            System.err.println("----- applyModel (cy_network,"
                    + node.getIdentifier() + ") -----");

        if (cy_network.getRootGraph() != this.rootGraph) {
            // This should not happen
            if (DEBUG) {
                System.err
                        .println("----- applyModel (CyNetwork,"
                                + node.getIdentifier()
                                + ") leaving, cy_networks's RootGraph != this.rootGraph -----");
            }
            return false;
        }

        List cnNodes = (ArrayList) this.networkToNodes.get(cy_network);
        if (cnNodes == null) {
            // I know that this is the first time that applyModel is called for
            // the given network
            cnNodes = new ArrayList(getNodesInCyNet(cy_network));
            this.networkToNodes.put(cy_network, cnNodes);
        }

        long prepareTime;
        if (REPORT_TIME) {
            prepareTime = System.currentTimeMillis();
        }
        if (!prepareNodeForModel(cy_network, node)) {
            if (DEBUG) {
                System.err
                        .println("----- applyModel (CyNetwork,"
                                + node.getIdentifier()
                                + ") returning, "
                                + "prepareNodeForModel returned false, returning false -----");
            }
            return false;
        }

        if (REPORT_TIME) {
            long timeToPrepare = (System.currentTimeMillis() - prepareTime) / 1000;
            System.out.println("time in prepareNodeForModel ( " + node + ") = "
                    + timeToPrepare);
        }
        // Restore the meta-node, in case that it is not showing

        CyNode restoredNode = (CyNode) cy_network.restoreNode(node);
        if (restoredNode == null) {
            // For now, do this...
            System.out.println("Did not restore node [" + node
                    + "] successfully.");
            restoredNode = node;
            // throw new IllegalStateException("Did not restore node [" + node +
            // "] successfully.");
        }

        // The restored meta-node is now contained in the network, so remember
        // this:
        cnNodes.add(restoredNode);

        if (DEBUG) {
            System.err.println("Restored node [" + node.getIdentifier()
                    + "] and got back node [" + restoredNode.getIdentifier()
                    + "]");
        }

        // Hide the meta-node's descendants and connected edges
        Iterator it = descendants.iterator();
        int numHiddenNodes = 0;
        while (it.hasNext()) {
            CyNode n = (CyNode) it.next();
            CyNode hiddenNode = (CyNode) cy_network.hideNode(n);
            if (hiddenNode != null) {
                numHiddenNodes++;
            }
        }
        if (DEBUG) {
            System.err.println("Hid " + numHiddenNodes
                    + " descendant nodes of node " + node.getIdentifier()
                    + ", from a total of " + descendants.size()
                    + " descendants");
        }

        // Restore the edges connecting the meta-node and nodes that are in
        // cyNetwork
        // NOTE: These edges are created in prepareNodeForModel
        long restoreEdgesStart = System.currentTimeMillis();
        int numRestoredEdges = 0;

        // create a list of all edges that should be restored
        ArrayList restoredEdges = new ArrayList();
        it = cy_network.nodesIterator();
        while (it.hasNext()) {

            CyNode otherNode = (CyNode) it.next();
            // Get edges in BOTH directions and restore them
            int[] connectingEdgesRindices = this.rootGraph
                    .getConnectingEdgeIndicesArray(new int[] {
                            node.getRootGraphIndex(),
                            otherNode.getRootGraphIndex() });

            if (connectingEdgesRindices == null
                    || connectingEdgesRindices.length == 0) {
                if (DEBUG) {
                    System.err
                            .println("There are no connecting edges between nodes "
                                    + node.getIdentifier()
                                    + " and "
                                    + otherNode.getIdentifier());
                }
                continue;
            }
            if (DEBUG) {
                System.err.println("There are "
                        + connectingEdgesRindices.length
                        + " edges between nodes " + node.getIdentifier()
                        + " and " + otherNode.getIdentifier());
            }

            for (int ci = 0; ci < connectingEdgesRindices.length; ++ci) {
                CyEdge cEdge = (CyEdge) this.rootGraph
                        .getEdge(connectingEdgesRindices[ci]);
                CyEdge rEdge = (CyEdge) cy_network.restoreEdge(cEdge);
                if (rEdge != null)
                    restoredEdges.add(rEdge);
            }

        }// for node_i

        if (restoredNode != null || numHiddenNodes > 0
                || restoredEdges.size() > 0) {
            if (DEBUG) {
                System.err.println("----- applyModel (CyNetwork,"
                        + node.getIdentifier() + ") returning true -----");
            }
            return true;
        }
        if (DEBUG) {
            System.err.println("----- applyModel (CyNetwork,"
                    + node.getIdentifier() + ") returning false -----");
        }
        return false;

    }// applyModel

    /**
     * It undos the model for the given <code>CyNode</code>.
     * 
     * @param cy_network
     *            the <code>CyNetwork</code> whose <code>RootGraph</code>
     *            should be the one set in the constructor (or through
     *            <code>setRootGraph</code>)and in which the
     *            <code>CyNode</code> should reside
     * @param node
     *            the <code>CyNode</code> that may be a metanode
     * @param recursive_undo
     *            whether or not any existing meta-nodes inside the meta-node
     *            being undone should also be undone
     * @param temporary_undo
     *            whether or not the "undo" is temporary, if not temporary,
     *            then, the <code>cy_network</code>'s <code>RootGraph</code>
     *            will also be modified so that it is in the same state as it
     *            was before any calls to <code>applyModel(node_index)</code>
     */
    public boolean undoModel(CyNetwork cy_network, CyNode node,
            boolean recursive_undo, boolean temporary_undo) {

        long startOfUndo = System.currentTimeMillis();

        if (DEBUG) {
            System.err.println("----- undoModel (CyNetwork," + node + ","
                    + recursive_undo + "," + temporary_undo + ") -----");
        }

        if (!cy_network.containsNode(node)) {
            System.err.println("cy_network does not contain the node" + node
                    + " returning false.");
            return false; // ???
        }

        // Get the indices of the nodes that the meta-node is connected to, we
        // will use these later
        int[] metaEdges = cy_network.getAdjacentEdgeIndicesArray(node
                .getRootGraphIndex(), true, true, true);
        IntArrayList metaNeighbors = new IntArrayList();
        // Null pointer exception here: (8.10.2005) (before 12.11.05
        // refactoring)
        if (metaEdges != null) {
            for (int i = 0; i < metaEdges.length; i++) {

                CyEdge edge = (CyEdge) this.rootGraph.getEdge(metaEdges[i]);
                CyNode source = (CyNode) edge.getSource();
                CyNode target = (CyNode) edge.getTarget();

                CyNode neighborNode = null;
                if (source == node) {
                    neighborNode = target;
                } else if (target == node) {
                    neighborNode = source;
                }

                if (neighborNode == null) {

                    if (DEBUG) {
                        System.err.println("Edge " + edge
                                + " is an adjacent edge of meta-node " + node
                                + " but we could not identify the neighbor of "
                                + " the meta-node!!!");
                    }

                } else {
                    metaNeighbors.add(neighborNode.getRootGraphIndex());
                }

            }// for i
        }

        int[] childrenRindices = null;

        if (recursive_undo) {

            if (!temporary_undo) {
                // Remove edges that *we* created in the RootGraph for this
                // meta-node and its descendant meta-nodes, it also removes the
                // meta-nodes
                // from this networks client data available through
                // MetaNodeFactory.METANODES_IN_NETWORK
                removeMetaNode(cy_network, node, true);
            }

            // Get the descendants with no children of this meta-node, since
            // they will be displayed after this call to undo
            // TODO: Check performance of this method:
            // COULD IMPROVE BY USING this.networkToNodes ??
            childrenRindices = this.rootGraph.getChildlessMetaDescendants(node
                    .getRootGraphIndex());

            if (DEBUG) {
                if (childrenRindices != null) {
                    System.err.println("childless-descendants of node " + node
                            + " are: ");
                    for (int cd = 0; cd < childrenRindices.length; cd++) {
                        System.err.println(this.rootGraph
                                .getNode(childrenRindices[cd]));
                    }// for cd
                } else {
                    System.err.println("node " + node
                            + " has no childless descendants");
                }
            }// if DEBUG

        } else {
            // not recursive
            if (!temporary_undo) {
                // Remove edges that *we* created in the RootGraph for this
                // meta-node (only)
                removeMetaNode(cy_network, node, false);

            }
            // Not recursive, so just get the immediate children
            childrenRindices = this.rootGraph.getNodeMetaChildIndicesArray(node
                    .getRootGraphIndex());
        }

        if (childrenRindices == null || childrenRindices.length == 0) {
            if (DEBUG) {
                System.err.println("----- undoModel (CyNetwork," + node + ","
                        + recursive_undo
                        + ") returning false since the given node is not a "
                        + " meta-node -----");
            }
            return false;
        }

        // Restore the children nodes and their adjacent edges that connect to
        // other nodes currently contained in CyNetwork
        int[] restoredNodeRindices = cy_network.restoreNodes(childrenRindices);
        int[] edgesToRestore = this.rootGraph
                .getConnectingEdgeIndicesArray(childrenRindices);
        cy_network.restoreEdges(edgesToRestore);

        IntArrayList childrenAndNeighbors = new IntArrayList();
        metaNeighbors.trimToSize();
        childrenAndNeighbors.addAllOf(metaNeighbors);
        for (int i = 0; i < childrenRindices.length; i++) {
            childrenAndNeighbors.add(childrenRindices[i]);
        }// for i
        childrenAndNeighbors.trimToSize();

        // Need to call this method to get the edges that need to be restored
        // Get the connecting edges between the children *and* the nodes in
        // cy_network that their parent node is connected to
        int[] connectingEdges = this.rootGraph
                .getConnectingEdgeIndicesArray(childrenAndNeighbors.elements());
        int[] restoredEdges = cy_network.restoreEdges(connectingEdges);

        if (DEBUG) {
            int notRestored = 0;
            for (int i = 0; i < restoredEdges.length; i++) {
                if (restoredEdges[i] == 0) {
                    notRestored++;
                }
            }// for i

            if (notRestored > 0) {
                System.err.println(notRestored + " edges were not restored!!!");
            }

        }// DEBUG

        if (DEBUG) {
            System.err.println("Restored " + restoredNodeRindices.length
                    + " children nodes of meta-node" + node + " in cy_network");
        }
        // Hide the meta-node and adjacent edges
        CyNode hiddenNode = (CyNode) cy_network.hideNode(node);
        // The meta-node is no longer in the network, so remember this:
        ArrayList cnNodes = (ArrayList) this.networkToNodes.get(cy_network);
        if (cnNodes != null) {
            cnNodes.remove(node);
        } 

        if (REPORT_TIME) {
            long timeToUndo = (System.currentTimeMillis() - startOfUndo) / 1000;
            System.out.println("undoModel time = " + timeToUndo);
        }

        if (DEBUG) {
            System.err.println("Hid node " + node
                    + " in graphPerspective and got back node index "
                    + hiddenNode);
        }

        if (restoredNodeRindices.length > 0 || hiddenNode != null) {
            if (DEBUG) {
                System.err.println("----- undoModel (CyNetwork," + node + ","
                        + recursive_undo + ") returning true -----");
            }
            return true;
        }

        if (DEBUG) {
            System.err.println("----- undoModel (CyNetwork," + node + ","
                    + recursive_undo + ") returning false -----");
        }
        return false;
    }// undoModel

    /**
     * Creates edges between the given CyNode and other nodes so that when the
     * CyNode is collapsed (applyModel) or expanded (undoModel) the needed edges
     * (between meta-nodes and non-meta-nodes, or meta-nodes and meta-nodes),
     * will be there for hiding or unhiding as necessary. Uses
     * getMultipleEdges() to decide whether or not to crealte multiple edges
     * between a meta-node and other nodes.
     * 
     * @param cy_network
     *            edges will be created in the RootGraph of the cy_network
     * @param node
     *            the node to be prepared
     * @return false if the node does not have any descendants in
     *         graph_perspective
     */
    protected boolean prepareNodeForModel(CyNetwork cy_network, CyNode node) {
        if (DEBUG) {
            System.err.println("----- prepareNodeForModel (CyNetwork,"
                    + node.getIdentifier() + ") -----");
        }

        ArrayList cnNodes = (ArrayList) this.networkToNodes.get(cy_network);
        if (cnNodes == null) {
            // Should not get here
            throw new IllegalStateException(
                    "this.networkToNodes.get(cy_network) returned null");
        }

        if (!isMetaNode(node)) {
            boolean returnBool;
            if (cnNodes.contains(node)) {
                returnBool = true;
            } else {
                returnBool = false;
            }
            if (DEBUG) {
                System.err
                        .println("----- prepareNodeForModel (CyNetwork,"
                                + node.getIdentifier()
                                + ") leaving, node is not a meta-node, returning is in graphPerspective = "
                                + returnBool + "-----");
            }
            return returnBool;
        }// !isMetaNode

        int[] childrenRindices = this.rootGraph
                .getNodeMetaChildIndicesArray(node.getRootGraphIndex());
        if (DEBUG) {
            if (childrenRindices != null) {
                System.err
                        .println("Meta-node "
                                + node.getIdentifier()
                                + " has "
                                + childrenRindices.length
                                + " children, recursively calling prepareNodeForModel for each one of them");
            } else {
                System.err.println("Meta-node " + node.getIdentifier()
                        + " has no children.");
            }
        }

        boolean hasDescendantInCN = false;
        if (childrenRindices != null) {
            // Recursively prepare each child node
            for (int i = 0; i < childrenRindices.length; i++) {
                boolean temp = prepareNodeForModel(cy_network,
                        (CyNode) this.rootGraph.getNode(childrenRindices[i]));
                hasDescendantInCN = hasDescendantInCN || temp;
            }// for i
        }

        // If the meta-node does not have a single descendant in
        // CyNetwork then skip it
        if (!hasDescendantInCN) {
            if (DEBUG) {
                System.err
                        .println("----- prepareNodeForModel (CyNetwork,"
                                + node.getIdentifier()
                                + ") leaving, meta-node does not have a descendant contained "
                                + "in graphPerspective, returning false -----");
            }
            return false;
        }

        // Add edges to the meta-node in this.rootGraph, respect directionality
        // Also, see if multiple edges are to be created

        // A map from neighbor nodes of the metanode to the metaedge that connects
        // the metanode to the neighbor
        // Used for the multiple edge condition
        Map neighborNodeToMetaEdge = new HashMap();

        // For the attributes handler:
        AbstractIntIntMap metaEdgeToChildEdge = new OpenIntIntHashMap();
        int numNewEdges = 0;
        Map metaedgeToNumChildEdges = new HashMap();
        CyNode childNode = null;
        for (int child_i = 0; child_i < childrenRindices.length; child_i++) {

            int childNodeRindex = childrenRindices[child_i];
            childNode = (CyNode) Cytoscape.getRootGraph().getNode(
                    childNodeRindex);

            if (DEBUG) {
                System.err.println("Child index of "
                        + childNode.getIdentifier() + " is " + childNodeRindex);
            }
          
            int[] adjacentEdgeRindices = this.rootGraph
                    .getAdjacentEdgeIndicesArray(childNodeRindex, true, true,
                            true);

            if (DEBUG) {
                System.err.println("Child node " + childNode.getIdentifier()
                        + " has " + adjacentEdgeRindices.length
                        + " total adjacent edges in this.rootGraph");
            }
            // Process each edge by creating edges in RootGraph that reflect
            // connections of meta-node children
            ArrayList processedEdges = (ArrayList) this.metaNodeToProcessedEdges
                    .get(node);
            if (processedEdges == null) {
                // This List will be used later, so create it
                // Also, note that if the ArrayList for a meta-node is
                // not null, we know that
                // prepareNodeForModel(GraphPerspective, meta-node)
                // has been called before
                // for that node (which is useful information for later)
                processedEdges = new ArrayList();
            }
            
            
            // TEST CODE //
            // whether there is an edge between this metanode and a the current childNode
           CyEdge metaNodeToChildEdge = null;
           if(getCreateMetaRelationshipEdges()){
	           for (int edge_i = 0; edge_i < adjacentEdgeRindices.length; edge_i++) {
	
	                int childEdgeRindex = adjacentEdgeRindices[edge_i];
	                CyEdge childEdge = (CyEdge) this.rootGraph
	                        .getEdge(childEdgeRindex);
	
	                if (DEBUG && childEdge == null) {
	                    throw new IllegalStateException("CyEdge for index ["
	                            + childEdgeRindex + "] is null!");
	                }
	                
	                // Identify the node on the other end of the edge
	                CyNode otherNode = null;
	                CyNode sourceNode = (CyNode) childEdge.getSource();
	                CyNode targetNode = (CyNode) childEdge.getTarget();
	                boolean metaNodeIsSource = false;
	                if (targetNode.getRootGraphIndex() == childNodeRindex) {
	                    otherNode = sourceNode;
	                } else if (sourceNode.getRootGraphIndex() == childNodeRindex) {
	                    metaNodeIsSource = true;
	                    otherNode = targetNode;
	                }
	                
	                if(otherNode == node){metaNodeToChildEdge = childEdge;}
	            }
           }
           // TEST CODE END //
            
           for (int edge_i = 0; edge_i < adjacentEdgeRindices.length; edge_i++) {

                int childEdgeRindex = adjacentEdgeRindices[edge_i];
                CyEdge childEdge = (CyEdge) this.rootGraph
                        .getEdge(childEdgeRindex);

                if (DEBUG && childEdge == null) {
                    throw new IllegalStateException("CyEdge for index ["
                            + childEdgeRindex + "] is null!");
                }
                // See if we already processed this edge before, and if so, skip
                // it
                // ArrayList processedEdges = (ArrayList)
                // this.metaNodeToProcessedEdges.get(node);
                // if (processedEdges != null &&
                // processedEdges.contains(childEdge) ) {
                if (processedEdges.contains(childEdge)) {
                    if (DEBUG) {
                        System.out.println("Edge " + childEdge.getIdentifier()
                                + " has already been processed before for "
                                + " meta-node " + node.getIdentifier()
                                + ", skipping it.");
                    }
                    continue;
                }
                // if (processedEdges == null){

                // If the edge connects two descendants of the meta-node, then
                // ignore it, and remember this processed edge
                if (edgeConnectsDescendants(node.getRootGraphIndex(),
                        childEdgeRindex)) {
                    if (DEBUG) {
                        System.out
                                .println("Edge "
                                        + childEdge.getIdentifier()
                                        + " connects descendants of node "
                                        + node.getIdentifier()
                                        + ", skipping it, and adding it to processedEdges.");
                    }
                    processedEdges.add(childEdge);
                    this.metaNodeToProcessedEdges.put(node, processedEdges);
                    continue;
                }

                // Identify the node on the other end of the edge, and determine
                // whether the meta-node is the source or not
                CyNode otherNode = null;
                CyNode sourceNode = (CyNode) childEdge.getSource();
                CyNode targetNode = (CyNode) childEdge.getTarget();
                boolean metaNodeIsSource = false;
                if (targetNode.getRootGraphIndex() == childNodeRindex) {
                    otherNode = sourceNode;
                } else if (sourceNode.getRootGraphIndex() == childNodeRindex) {
                    metaNodeIsSource = true;
                    otherNode = targetNode;
                }
                
                // TEST CODE
                if(otherNode == node){
                	// Ignore this edge
                	System.err.println("This is a childOf edge, continue.");
                	// Remember that this edge has been processed
                	processedEdges.add(childEdge);
                	this.metaNodeToProcessedEdges.put(node, processedEdges);  
                	continue; // go to the next child-edge
                }
                
                String interactionType = TRANSFERRED_EDGE_INTERACTION + ":" + 
                		Cytoscape.getEdgeAttributes().getStringAttribute(childEdge.getIdentifier(),Semantics.INTERACTION);
                
                if(getCreateMetaRelationshipEdges() && isMetaNode(otherNode)){
                		// This is a childNode that two metanodes are sharing
                		// If the other metaNode has a sharedMember edge to this metanode, continue
                	  ArrayList otherProcessedEdges = (ArrayList) this.metaNodeToProcessedEdges
                      .get(otherNode);
                	  if (otherProcessedEdges != null
                		  & metaNodeToChildEdge != null && otherProcessedEdges.contains(metaNodeToChildEdge) ) {
                		  // Remember that this edge has been processed
                		  processedEdges.add(childEdge);
                		  this.metaNodeToProcessedEdges.put(node, processedEdges);
                		  continue;
                	  }
                	  //System.err.println("otherNode [" + otherNode + "] is a metaNode, this should be a sharedMember edge");
                	  interactionType = SHARED_CHILD_INTERACTION;
                }// isMetaNode(otherNode)
                // TEST CODE END
                
                if(!getMultipleEdges() && neighborNodeToMetaEdge.containsKey(otherNode)){
                    
                    CyEdge metaedge = (CyEdge)neighborNodeToMetaEdge.get(otherNode);
                    Integer numChildren = (Integer)metaedgeToNumChildEdges.get(metaedge);
                    if(numChildren != null){
                        int num = numChildren.intValue() + 1;
                        metaedgeToNumChildEdges.put(metaedge, new Integer(num));
                    }
                
                }else if (getMultipleEdges()
                        || (!getMultipleEdges() && !neighborNodeToMetaEdge.containsKey(otherNode))) {

                    // Create an edge in rootGraph respecting directionality

                    CyEdge newEdge = null;

                    if (metaNodeIsSource) {
                        newEdge = createMetaEdge(node, otherNode, interactionType);
                    } else {
                        newEdge = createMetaEdge(otherNode, node, interactionType);
                    }
                    if (!getMultipleEdges()) {
                        metaedgeToNumChildEdges.put(newEdge, new Integer(1));
                    }
                    neighborNodeToMetaEdge.put(otherNode,newEdge);
                    metaEdgeToChildEdge.put(newEdge.getRootGraphIndex(),
                            childEdgeRindex);
                    numNewEdges++;
                    // Remember that *we* created this edge, so that later, we
                    // can reset RootGraph to its original state if needed by
                    // removing edges we
                    // created
                    this.metaEdges.add(newEdge);
                    
                    // If the edge is of type shared child, set a sharedChild edge attribute with the id of the shared child as a value
                    if(interactionType.equals(SHARED_CHILD_INTERACTION)){
                 	   Cytoscape.getEdgeAttributes().setAttribute(newEdge.getIdentifier(),SHARED_CHILD_INTERACTION,childNode.getIdentifier());
                 	   
                    }

                    // If otherNodeRindex has parents, and the parents have been
                    // processed before,
                    // then mark newEdgeRindex as a processed edge for the
                    // parents
                    // This is so that if prepareNodeForModel
                    // is called after this call, a duplicate edge from
                    // otherNodeRindex to node_rindex won't be created
                    int[] otherNodeParentsRindices = this.rootGraph
                            .getNodeMetaParentIndicesArray(otherNode
                                    .getRootGraphIndex());
                    if (otherNodeParentsRindices != null) {
                        for (int otherParent_i = 0; otherParent_i < otherNodeParentsRindices.length; otherParent_i++) {
                            CyNode otherParent = (CyNode) this.rootGraph
                                    .getNode(otherNodeParentsRindices[otherParent_i]);
                            List otherProcessedEdges = (ArrayList) this.metaNodeToProcessedEdges
                                    .get(otherParent);
                            if (otherProcessedEdges != null) {
                                // We know that this parent has been processed
                                // before
                                if (DEBUG)
                                    System.out
                                            .println("Edge "
                                                    + newEdge.getIdentifier()
                                                    + " added to processed edges for other parent node "
                                                    + otherParent
                                                            .getIdentifier());
                                otherProcessedEdges.add(newEdge);
                            }
                        }// for each otherNodeRindex parent
                    }// if otherNodeRindex has parents

                    if (DEBUG) {
                        System.err.println("New edge "
                                + newEdge.getIdentifier()
                                + " created in this.rootGraph:");
                        // System.err.println("Source is "
                        // + newEdge.getSource().getIdentifier()+ " Target is "
                        // + newEdge.getTarget().getIdentifier());
                    }
                }// if an edge should be created

                // Remember that we processed this edge
                if (DEBUG)
                    System.out.println("Adding edge "
                            + childEdge.getIdentifier()
                            + " to processedEdges for node "
                            + node.getIdentifier());
                
                processedEdges.add(childEdge);
                this.metaNodeToProcessedEdges.put(node, processedEdges);
            
           }// for each adjacent edge to child node
           
           
           
            if(getCreateMetaRelationshipEdges() && metaNodeToChildEdge == null){
            		// Create a child edge
            		CyEdge childOfEdge = createMetaEdge(childNode,node, CHILD_OF_EDGE_INTERACTION);
            		if(childOfEdge == null){
            			throw new IllegalStateException("Child edge between meta-node [" + node + "] and child [" + childNode + "] could not be created.");
            		}//if childOfEdge == null
            		//System.err.println("Created childOf edge because of child [" + childNode + "]");
            }// if getCreateMetaRelationshipEdges

        }// for each child of node_rindex

        // Transfer node and edge attributes to the meta-node as needed
        metaEdgeToChildEdge.trimToSize();
        MetaNodeAttributesHandler attributesHandler = getNetworkAttributesHandler(cy_network);
        ArrayList children = new ArrayList();
        for (int i = 0; i < childrenRindices.length; i++) {
            children.add(rootGraph.getNode(childrenRindices[i]));
        }
        boolean attributesSet = attributesHandler.setAttributes(cy_network,
                node, children, metaEdgeToChildEdge);

        // Set a "numChildrenEdges" attribute if the edge is collapsed
        // do this here so that if the user uses her own
        // MetaNodeAttributesHandler, the attribute exists as well
        if (!getMultipleEdges() && metaedgeToNumChildEdges.size() > 0) {
            CyAttributes edgeAtts = Cytoscape.getEdgeAttributes();
            Iterator it = metaedgeToNumChildEdges.keySet().iterator();
            while(it.hasNext()){
                CyEdge metaEdge = (CyEdge)it.next();
                edgeAtts.setAttribute(metaEdge.getIdentifier(), "numChildrenEdges",
                    (Integer)metaedgeToNumChildEdges.get(metaEdge));
            }
        }
        if (!attributesSet) {
            if (DEBUG) {
                System.out
                        .println("----- prepareNodeForModel (CyNetwork,"
                                + node.getIdentifier()
                                + "): error,  attributesHandler.setAttributes returned false!!! -----");
            }
        }
        if (DEBUG) {
            System.out.println("----- prepareNodeForModel (CyNetwork,"
                    + node.getIdentifier() + ") returning true -----");
        }
        return true;

    }// prepareNodeForModel

    /**
     * Removes the edges that were created by <code>prepareNodeForModel()</code>
     * that are connected to the given <code>CyNode</code> and their
     * attributes in the given CyNetwork. It also remembers that the meta-node
     * is no longer a meta-node for cy_net, and if recursive, it also remembers
     * this for other meta-nodes in the path.
     * 
     * @param cy_net
     *            the CyNetwork that contains the meta-node
     * @param recursive
     *            if true, edges for meta-node descendants of the given node are
     *            also removed (except for those connected to the descendatns
     *            with no children)
     */
    protected void removeMetaNode(CyNetwork cy_net, CyNode meta_node,
            boolean recursive) {

        if (DEBUG) {
            System.err.println("----- removeMetaNode (" + meta_node + ","
                    + recursive + ")-----");
        }

        // If not a meta-node, then return
        if (!isMetaNode(meta_node)) {
            if (DEBUG) {
                System.err.println("----- removeMetaEdges returning, node "
                        + meta_node + " is not a meta node -----");
            }
            return;
        }

        // It is a meta-node, remember that it is no longer a meta-node of
        // cy_net
        CyAttributes netAttributes = Cytoscape.getNetworkAttributes();
        ArrayList metaNodesForNetwork = (ArrayList) netAttributes
                .getAttributeList(cy_net.getIdentifier(), MetaNodeFactory.METANODES_IN_NETWORK);
        if (metaNodesForNetwork != null) {
            Iterator it = metaNodesForNetwork.iterator();
            Integer match = null;
            while (it.hasNext()) {
                Integer meta = (Integer)it.next();
                if ( meta.intValue() == meta_node.getRootGraphIndex() ) {
                    match = meta;
                    break;
                }
            }
            if (match != null) metaNodesForNetwork.remove(match);
        }

        // If recursive, get all the descendants of this meta-node, and remove
        // their meta-edges
        if (recursive) {

            // This method is too slow:
            // int [] descendants =
            // this.rootGraph.getNodeMetaChildIndicesArray(meta_node_index,true);

            ArrayList descendants = new ArrayList();
            getDescendants(meta_node, descendants);
            for (int i = 0; i < descendants.size(); i++) {
                removeMetaNode(cy_net, (CyNode) descendants.get(i), false);
            }// for i
        }// if recursive

        IntArrayList adjacentEdgeRindices = new IntArrayList(this.rootGraph
                .getAdjacentEdgeIndicesArray(meta_node.getRootGraphIndex(),
                        true, true, true));
        adjacentEdgeRindices.trimToSize();

        // Remember that this meta-node has no processed edges anymore so that
        // if applyModel
        // is called, new edges are created once more
        if (DEBUG) {
            System.err.println("metaNodeToProcessedEdges.containsKey("
                    + meta_node + ") = "
                    + this.metaNodeToProcessedEdges.containsKey(meta_node));
        }
        this.metaNodeToProcessedEdges.remove(meta_node);
        if (DEBUG) {
            System.err
                    .println("after removal, metaNodeToProcessedEdges.containsKey("
                            + meta_node
                            + ") = "
                            + this.metaNodeToProcessedEdges
                                    .containsKey(meta_node));
        }

        // Make sure we are not going to remove edges that were there originally
        IntArrayList metaEdgesRindices = new IntArrayList();
        for (int i = 0; i < this.metaEdges.size(); i++)
            metaEdgesRindices.add(((CyEdge) this.metaEdges.get(i))
                    .getRootGraphIndex());
        adjacentEdgeRindices.retainAll(metaEdgesRindices);
        adjacentEdgeRindices.trimToSize();

        if (adjacentEdgeRindices.size() == 0) {
            if (DEBUG) {
                System.err.println("----- removeMetaEdges returning, node "
                        + meta_node + " has no adjacent edges -----");
            }
            return;
        }

        // Remove the edges that are connected to this meta-node and that *we*
        // created
        if (DEBUG) {
            System.err.println("before removing edges, num e = "
                    + this.rootGraph.getEdgeCount());
        }

        // ------------------ DISASTER AREA !!!
        // -------------------------------// (before refactoring on 12.11.05)

        // 5/5/05 this method crashes !!! :
        // this.rootGraph.removeEdges(adjacentEdgeRindices.elements());

        int notRemoved = 0;
        for (int i = 0; i < adjacentEdgeRindices.size(); i++) {

            // This method also crashes! 5/5/05
            // this.rootGraph.removeEdge(adjacentEdgeRindices.get(i));

            // This also crashes! 5/6/05
            // Edge edge = this.rootGraph.getEdge(adjacentEdgeRindices.get(i));
            // if(this.rootGraph.removeEdge(edge) == null){
            // notRemoved++;
            // }

            // 2nd argument removes the edge from the RootGraph as well (in
            // theory)
            // if( cy_net.removeEdge(adjacentEdgeRindices.get(i),true) ){
            // the method returns true if the edge is still in rootGraph
            // force removal from rootGraph:
            // if(this.rootGraph.removeEdge(adjacentEdgeRindices.get(i)) == 0){
            // notRemoved++; //it didn't get removed!
            // }
            // }

            // Try first removing it from CyNetwork and then from the RootGraph
            boolean stillInRootGraph = cy_net.removeEdge(adjacentEdgeRindices
                    .get(i), false);
            if (!stillInRootGraph) {
                // this is not supposed to happen!
                throw new IllegalStateException("Removed edge with index "
                        + adjacentEdgeRindices.get(i)
                        + " in cy_net, but it also got removed in RootGraph!!");
            }

            if (this.rootGraph.removeEdge(adjacentEdgeRindices.get(i)) == 0) {
                // if it returned 0, that means there is trouble!
                throw new IllegalStateException("Removed edge with index "
                        + adjacentEdgeRindices.get(i)
                        + " in this.rootGraph, but method returned 0!!!");
            }
            this.metaEdges.remove(this.rootGraph.getEdge(adjacentEdgeRindices
                    .get(i)));
        }// for i

        if (DEBUG) {
            if (notRemoved > 0) {
                System.err.println(notRemoved + " edges where not removed!");
            }
            System.err.println("after removing edges, num e = "
                    + this.rootGraph.getEdgeCount());
        }
        // ----------------------END DISASTER AREA
        // ----------------------------------//

        // Update metaEdgesRindices
        if (DEBUG) {
            System.err.println("metaEdgesRindices.size = "
                    + this.metaEdges.size());
        }
        // this.metaEdges.removeAll(adjacentEdgeRindices);
        if (DEBUG) {
            System.err.println("after removing, metaEdgesRindices.size = "
                    + this.metaEdges.size());
        }
        ArrayList adjacentEdges = new ArrayList();
        for (int i = 0; i < adjacentEdgeRindices.size(); i++)
            adjacentEdges.add(i, this.rootGraph.getEdge(adjacentEdgeRindices
                    .get(i)));
        // Remove the edges from the lists of processed edges
        Iterator it = this.metaNodeToProcessedEdges.values().iterator();
        while (it.hasNext()) {
            ArrayList processedEdges = (ArrayList) it.next();
            if (processedEdges == null) {
                if (DEBUG) {
                    System.err.println("processedEdges is null");
                }
                continue;
            }// processedEdges == null
            processedEdges.removeAll(adjacentEdges);
        }// for i

        // And finally, remove the attributes for these edges

        MetaNodeAttributesHandler attributesHandler = getNetworkAttributesHandler(cy_net);
        boolean r = attributesHandler.removeMetaEdgesFromAttributes(cy_net,
                meta_node, adjacentEdges);
        if (!r) {
            if (DEBUG) {
                System.err
                        .println("----- AbstractMetaNodeModeler.removeMetaEdges: error, could not remove"
                                + " attributes for meta-edges");
            }
        }
    }// removeMetaEdges

    /**
     * @return true iff the node has at least one child
     */
    protected boolean isMetaNode(CyNode node) {
        int[] childrenIndices = this.rootGraph
                .getNodeMetaChildIndicesArray(node.getRootGraphIndex());
        if (childrenIndices == null || childrenIndices.length == 0) {
            return false;
        }
        return true;
    }// isMetaNode

    /**
     * @return true if the edge with edge_rindex connects two descendants of
     *         node node_rindex
     */
    protected boolean edgeConnectsDescendants(int node_rindex, int edge_rindex) {
        int[] childrenRindices = this.rootGraph
                .getNodeMetaChildIndicesArray(node_rindex);
        if (childrenRindices == null || childrenRindices.length == 0) {
            return false;
        }
        int sourceRindex = this.rootGraph.getEdgeSourceIndex(edge_rindex);
        int targetRindex = this.rootGraph.getEdgeTargetIndex(edge_rindex);
        // If the source is the descendant of one of the children and so is the
        // target, then return true
        if (this.rootGraph.isNodeMetaChild(node_rindex, sourceRindex, true)
                && this.rootGraph.isNodeMetaChild(node_rindex, targetRindex,
                        true)) {
            return true;
        }
        return false;
    }// edgeConnectsDescendants

    /**
     * @return an array of CyNode that are contained in cy_net or that are
     *         contained in this.rootGraph and is a descendant of a CyNode
     *         contained in cy_net
     */
    // NOTE: This method is slow, avoid calling it as much as possible
    protected ArrayList getNodesInCyNet(CyNetwork cy_net) {
        if (DEBUG) {
            System.err.println("--------- getNodesInCynet (cy_net) ---------");
        }
        // DEPRECATED:
        // int[] nodeRindices = graph_perspective.getNodeIndicesArray();
        Iterator it = cy_net.nodesIterator();
        ArrayList cyNetNodes = new ArrayList();
        // If cy_net contains meta-nodes, then their descendants are
        // also in it
        while (it.hasNext()) {

            // NOTE: This method takes a very long time to run:
            // int [] children =
            // this.rootGraph.getNodeMetaChildIndicesArray(nodeRindices[i],true);
            CyNode node = (CyNode) it.next();
            cyNetNodes.add(node);
            ArrayList descendants = new ArrayList();
            getDescendants(node, descendants);
            descendants.trimToSize();
            Iterator it2 = descendants.iterator();
            // TODO: Optimize
            while (it2.hasNext()) {
                CyNode child = (CyNode) it2.next();
                if (DEBUG) {
                    System.err.println(" child = " + child);
                }
                cyNetNodes.add(child);
            }// for j
        }
        if (DEBUG) {
            System.err
                    .println("--------- leaving getNodeRindicesInGP (GraphPerspective) ---------");
        }
        return cyNetNodes;
    }// getNodeRindicesInGP

    /**
     * Stores in the given ArrayList the nodes that are descendants of the given
     * node, descendants are nodes that are direct children, or children of
     * children, etc. of the given node.
     * 
     * @param node
     *            the node for which descendants are being returned
     * @param descendatns
     *            an ArrayList to which the descendants will be added
     * 
     * This is faster than RootGraph.getNodeMetaChildIndicesArray(index,true).
     * TODO: Replace implementation in giny?
     */
    public void getDescendants(CyNode node, ArrayList descendants) {
        if (descendants == null) {
            descendants = new ArrayList();
        }
        Iterator it = node.getGraphPerspective().nodesIterator();
        while (it.hasNext()) {
            CyNode childNode = (CyNode) it.next();
            if (childNode.getGraphPerspective().getNodeCount() > 0) {
                getDescendants(childNode, descendants);
            }
            descendants.add(childNode);
        }// while

        // if (descendants == null) {
        // descendants = new ArrayList();
        // }
        // // Get all the immediate children
        // //DEPRECATED:
        // //int[] allNodes = this.rootGraph.getNodeIndicesArray();
        // Iterator it = this.rootGraph.nodesIterator();
        // while (it.hasNext()) {
        // CyNode rgNode = (CyNode)it.next();
        // if (this.rootGraph.isNodeMetaParent(rgNode.getRootGraphIndex(),
        // node.getRootGraphIndex())) {
        // descendants.add(rgNode);
        // getDescendants(rgNode, descendants);
        // }
        // }

    }// getDescendants

    /**
     * Creates a new CyEdge between the given nodes, the interaction type is
     * META_EDGE_INTERACTION.<br>
     * This method allows to have several edges between the same pair of nodes
     * with the same interaction type. The consequence is that several edges
     * have the same entry in CyAttributes. This may have bad consequences, but
     * I have not found them yet. Also, since the meta-edges are named the same,
     * if saved to a sif file, the sif readers we have will only create one edge
     * per group of multiedges between two nodes. This is because Cytoscape does
     * not allow multiple edges between the same pair of nodes with the same
     * interaction type.
     * 
     * @param node_1
     *            the source node
     * @param node_2
     *            the target node
     * @return a new CyEdge
     */
    protected static CyEdge createMetaEdge(CyNode node_1, CyNode node_2, String interactionType) {
        // create the edge
        CyEdge edge = (CyEdge) Cytoscape.getRootGraph().getEdge(
                Cytoscape.getRootGraph().createEdge(node_1, node_2));

        // create the edge id
        String edge_name = node_1.getIdentifier() + " ("
                + interactionType + ") " + node_2.getIdentifier();
        edge.setIdentifier(edge_name);
        System.err.println(" ----------- Created edge : " + edge_name);
        	
        CyAttributes edgeAttributes = Cytoscape.getEdgeAttributes();
        edgeAttributes.setAttribute(edge_name, Semantics.INTERACTION,
                (String) interactionType);
        return edge;
    }
    

}// class AbstractMetaNodeModeler
