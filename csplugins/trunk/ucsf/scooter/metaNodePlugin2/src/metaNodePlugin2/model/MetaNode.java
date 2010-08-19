/* vim: set ts=2: */
/**
 * Copyright (c) 2007 The Regents of the University of California.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *   1. Redistributions of source code must retain the above copyright
 *      notice, this list of conditions, and the following disclaimer.
 *   2. Redistributions in binary form must reproduce the above
 *      copyright notice, this list of conditions, and the following
 *      disclaimer in the documentation and/or other materials provided
 *      with the distribution.
 *   3. Redistributions must acknowledge that this software was
 *      originally developed by the UCSF Computer Graphics Laboratory
 *      under support by the NIH National Center for Research Resources,
 *      grant P41-RR01081.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDER "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE REGENTS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT
 * OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 * BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */
package metaNodePlugin2.model;

// System imports
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.awt.Dimension;

// giny imports
import giny.view.NodeView;

// Cytoscape imports
import cytoscape.Cytoscape;
import cytoscape.CyEdge;
import cytoscape.CyNode;
import cytoscape.CyNetwork;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.view.CytoscapeDesktop;
import cytoscape.view.CyNetworkView;
import cytoscape.view.CyNodeView;
import cytoscape.data.CyAttributes;
import cytoscape.data.Semantics;
import cytoscape.logger.CyLogger;
import cytoscape.visual.VisualMappingManager;
import cytoscape.visual.VisualPropertyType;

import cytoscape.groups.CyGroup;
import cytoscape.groups.CyGroupManager;
import cytoscape.groups.CyGroupViewer;

// our imports
import metaNodePlugin2.MetaNodePlugin2;
import metaNodePlugin2.model.AttributeHandler.AttributeHandlingType;

// import csplugins.layout.Profile;

/**
 * The MetaNode class provides a wrapper for a CyGroup that
 * maintains the additional state information we want to keep
 * to allow the MetaNode abstraction (expand/contract) to work
 * more efficiently
 */
public class MetaNode {
	public static final String X_HINT_ATTR = "__metanodeHintX";
	public static final String Y_HINT_ATTR = "__metanodeHintY";
	public static final String CHILDREN_ATTR = "NumChildren";
	public static final String DESCENDENTS_ATTR = "NumDescendents";

	// Instance variables
	private CyGroup metaGroup = null;		// Keep handy copies of the CyGroup
	private CyNode groupNode = null;		// and group node information

	// newEdgeMap is a map with all of the meta-edges we've created
	// and the list of edges they have replaced
	protected Map<CyEdge,List<CyEdge>> newEdgeMap = null;

	// metaEdgeMap maps the nodes to the edges we've created
	private	Map<CyNode,CyEdge> metaEdgeMap = null;
	private List<MetaNode> childMetaNodes = null;
	private boolean multipleEdges = false;
	private boolean recursive = false;

	private CyAttributes edgeAttributes = null;
	private CyAttributes nodeAttributes = null;
	private CyNetworkView networkView = null;
	private CyNetwork network = null;
	private CyNetwork myGraph = null;
	private int nChildren = 0;
	private int nDescendents = 0;
	private Dimension metanodeSize = null;

	private boolean aggregateAttributes = false;
	private boolean hideMetanode = true;
	private boolean sizeToBoundingBox = false;
	private double metanodeOpacity = 0.;

	private CyLogger logger = null;

	/**
	 * Main constructor
	 *
	 * @param group the group to wrap the MetaNode around
	 */
	protected MetaNode(CyGroup group) {
		metaGroup = group;
		groupNode = group.getGroupNode();
		metaEdgeMap = new HashMap();
		logger = CyLogger.getLogger(MetaNode.class);
		this.hideMetanode = MetaNodeManager.hideMetanodeDefault;
		this.sizeToBoundingBox = MetaNodeManager.sizeToBoundingBoxDefault;
		this.metanodeOpacity = MetaNodeManager.metanodeOpacityDefault;
		this.aggregateAttributes = AttributeHandler.getEnable();

		// logger.debug("Creating meta-group "+group.getGroupName());

		newEdgeMap = new HashMap<CyEdge,List<CyEdge>>();

		// TODO: handle special case where all of the nodes in this group are
		// members of an existing metanode.  Implicitly, we treat this as a
		// hierarchy, which means that we first remove all of the nodes from the
		// parent metanode, then add the new metanode to the parent metanode

		update(null);

		// See if we need to "fix up" the CyGroup.  We might need to
		// add external edges to the CyGroup if we have nodes that used
		// to be connected to nodes which are now part of a collapsed
		// group.  If this is the case, some of *our* external edges
		// will be meta-edges
		List<CyEdge>newOuterEdges = new ArrayList<CyEdge>();
		List<CyEdge>externalEdges = group.getOuterEdges();

		for (CyEdge edge: externalEdges) {
			// logger.debug("... outer edge "+edge.getIdentifier());
			if (!isMetaEdge(edge))
				continue;

			// OK, so we have a meta-edge in our list.  That means
			// that the other side of the edge points to a group, and each
			// meta-edge that points to us may represent multiple edges.  We
			// will need to add those edges to our outer edge list.
			List<CyEdge> edges = getPartnerEdgeList(edge);
			if (edges != null)
				newOuterEdges.addAll(edges);
		}

		// OK, now add all of the new outer edges
		for (CyEdge edge: newOuterEdges) {
			if (!externalEdges.contains(edge)) {
					// logger.debug("... adding edge "+edge.getIdentifier());
			    group.addOuterEdge(edge);
			}
		}
		updateAttributes();
		// logger.debug("... done\n\n");
	}

	/**
	 * Return the CyGroup this MetaNode represents
	 */
	public CyGroup getCyGroup() {
		return metaGroup;
	}

	/**
	 * Update our "environment" variables.  These are values that might
	 * change during our execution due to actions of other plugins or
	 * the user.
	 */
	public void update(CyNetworkView view) {
		// Initialize
		edgeAttributes = Cytoscape.getEdgeAttributes();
		nodeAttributes = Cytoscape.getNodeAttributes();
		this.network = metaGroup.getNetwork();
		this.networkView = Cytoscape.getNetworkView(network.getIdentifier());

		// Initialize our network-related information.  This might
		// be overwritten by calls to setGroupViewer
		// if (view != null) {
		// 	// Override the defaults for networkView and network
		// 	this.networkView = view;
		// 	this.network = view.getNetwork();
		// } else {
		// 	this.networkView = Cytoscape.getCurrentNetworkView();
		// 	this.network = Cytoscape.getCurrentNetwork();
		// }
	}

	/**
	 * Add a node to this metaNode.  It will already have been added to the 
	 * group, but we need to update our internal data structures and (possibly)
	 * hide/restore some nodes and/or edges.
	 *
	 * @param node the CyNode that was added
	 */
	public void nodeAdded(CyNode node) {
		// logger.debug(groupNode.getIdentifier()+": adding node "+node.getIdentifier());
		// Adding a node could result in a couple of changes to our
		// internal data structures.  First, we might have to add new
		// metaedges to reflect the connection between this node and other
		// nodes not a member of the group.  Second, we might have to
		// remove metaedges if this node is the destination of any of our
		// existing metaedges.
		update(null);

		// Check to see if we can remove any metaEdges
		if (newEdgeMap != null) {
			if (metaEdgeMap.containsKey(node)) {
				// Get the metaEdge
				CyEdge metaEdge = metaEdgeMap.get(node);
				// logger.debug("... removing edge "+metaEdge.getIdentifier());
				// Remove it from the network
				network.removeEdge(metaEdge.getRootGraphIndex(), true);
				// Remove it from our data structures
				metaEdgeMap.remove(node);
				newEdgeMap.remove(metaEdge);
			}
			// Now check to see if we need to add any metaEdges
			// Get the list of external edges
			List<CyEdge> edges = metaGroup.getOuterEdges();
	
			// Attach them to the group node
			for (CyEdge edge: edges) {
				// logger.debug("... checking edge "+edge.getIdentifier());
				// Did we "cause" this edge?
				if (edge.getTarget() == node || edge.getSource() == node) {
					// Yes, we need to create a new metaEdge, then
					CyEdge newEdge = createMetaEdge(edge, null, false);
				}
			}
		}

		// Finally, if we're collapsed, hide this node (possibly recursively) and
		// update the display
		if (!isCollapsed())
			return;

		// Get the X and Y coordinates of the metaNode
		NodeView nv = (NodeView)networkView.getNodeView(groupNode);
		double metaX = nv.getXPosition();
		double metaY = nv.getYPosition();
		// Get our X and Y coordinates
		nv = (NodeView)networkView.getNodeView(node);
		double X = nv.getXPosition();
		double Y = nv.getYPosition();
		// logger.debug(node.getIdentifier()+" = ("+X+", "+Y+")");
		// Update our attributes
		String nodeName = node.getIdentifier();
		setXHintAttr(nodeAttributes, nodeName, metaX-X);
		setYHintAttr(nodeAttributes, nodeName, metaY-Y);

		// Collapse ourselves (if we are a group and we aren't already collapsed)
		MetaNode child = MetaNodeManager.getMetaNode(node);
		if (child != null) {
			// If we're already collapsed, this will just return
			child.collapse(recursive, multipleEdges, false, networkView);
		}
		// Hide our edges
		// Hide the node
		hideNode(node);

		updateAttributes();
		updateDisplay();
	}

	/**
	 * Remove a node from this metaNode.  It will already have been removed from the 
	 * group, but we need to update our internal data structures and (possibly)
	 * hide/restore some nodes and/or edges.
	 *
	 * @param node the CyNode that was removed
	 */
	public void nodeRemoved(CyNode node) {
		update(null);
		// If we're collapsed, unhide the node
		if (isCollapsed()) {
			restoreNode(node, null);
		}

		List <CyEdge>removeEdges = new ArrayList();

		// For each metaEdge, see if we're the cause for the metaNode.  If so, remove it.
		for (CyEdge metaEdge: newEdgeMap.keySet()) {
			// Get the list of edges represented by this metaEdge
			List<CyEdge> edgeList = newEdgeMap.get(metaEdge);
			// For each edge, see if this node is on one side
			ListIterator <CyEdge> edgeIter = edgeList.listIterator();
			while (edgeIter.hasNext()) {
				CyEdge edge = edgeIter.next();
				if (edge.getTarget() == node || edge.getSource() == node) {
					// Remove it from this metaEdge
					edgeIter.remove();
					if (isCollapsed()) {
						restoreEdge(edge);
					}
				}
			}
			// Did we delete the entire list?
			if (edgeList.size() == 0) {
				removeEdges.add(metaEdge);
			}
		}
		// OK, now remove all of the metaEdges
		for (CyEdge edge: removeEdges) {
			newEdgeMap.remove(edge);
		}

		// Now, we need to see if we need to add any metaEdges since we've moved this
		// node out.  We'll find that out by walking the outerEdgeMap and see if we
		// are now a partner
		for (CyEdge edge: metaGroup.getOuterEdges()) {
			if (edge.getTarget() == node || edge.getSource() == node) {
				// Rats, looks like we need to create metaEdges for this node
				CyEdge metaEdge = createMetaEdge(edge, null, false);
				// If we're expanded, hide it
				if (!isCollapsed()) {
					hideEdge(metaEdge);
				}
			}
		}

		updateAttributes();
		updateDisplay();
	}

	/**
	 * Recollapse a MetaNode.  This is only used when we're restoring a metaNode
	 * from XGMML.  The problem is that we need to remember the hints of the
	 * nodes before we collapse, then update those hints so that we expand
	 * properly.
	 *
	 * @param recursive if 'true', this operation is recursive
	 * @param multipleEdges if 'true', use multiple edges to represent the meta-edges
	 * @param view the view to use
	 */
	public void recollapse(boolean recursive, boolean multipleEdges, CyNetworkView view) {
		// Override the defaults for networkView and network
		update(view);

		if (newEdgeMap == null)
			createMetaEdges();

		expand(recursive, view, true);
		collapse(recursive, multipleEdges, false, view);
	}
	

	/**
	 * Collapse this MetaNode
	 *
	 * @param recursive if 'true', this operation is recursive
	 * @param multipleEdges if 'true', use multiple edges to represent the meta-edges
	 * @param updateNetwork if 'true', actually update the network
	 * @param view the CyNetworkView
	 */
	public void collapse(boolean recursive, boolean multipleEdges, boolean updateNetwork,
	                     CyNetworkView view) {
		// Initialize
		update(view);

		if (isCollapsed()) 
			return;

		// logger.debug("collapsing "+groupNode);
		// Profile prf = new Profile();
		// prf.start();

		// prf.done("collapse: Update=");

		this.multipleEdges = multipleEdges;
		this.recursive = recursive;

		// Add the group node in the center of where the member nodes were
		restoreNode(groupNode, hideNodes(recursive, multipleEdges));
		// prf.done("collapse: RestoreNode=");

		// Create our meta edges
		createMetaEdges();
		// prf.done("collapse: createMetaEdges=");

		// Set our state
		metaGroup.setState(MetaNodePlugin2.COLLAPSED);

		// If we're supposed to, update the display
		if (updateNetwork) {
			updateAttributes();
			updateDisplay();
		}
		// prf.done("collapse: updateDisplay=");
	}

	/**
	 * Expand this MetaNode.
	 *
	 * @param recursive if 'true', this operation is recursive
	 * @param view the CyNetworkView
	 * @param update update the display?
	 */
	public void expand(boolean recursive, CyNetworkView view, boolean update) {
		// Initialize
		update(view);

		if (!isCollapsed()) 
			return;

		// logger.debug("Expanding "+groupNode);

		// First, find all of our edges.  This will include any metaEdges as well
		// as any edges that were created by external applications
		int [] edgeArray = network.getAdjacentEdgeIndicesArray(groupNode.getRootGraphIndex(),true,true,true);

		if (edgeArray != null) {
			// logger.debug(groupNode.getIdentifier()+" has "+edgeArray.length+" edges");

			// Now, go through a (hopefully) quick loop to add any edges we don't already have into
			// our edge map
			for (int edgeIndex = 0; edgeIndex < edgeArray.length; edgeIndex++) {
				CyEdge edge = (CyEdge)network.getEdge(edgeArray[edgeIndex]);
				if (!isMetaEdge(edge)) {
					// Not a meta edge.  This may be an additional edge that got created
					// to our group node.  We want to add this to our outerEdge list
					// logger.debug("Adding outer edge "+edge.getIdentifier()+" to "+groupNode.getIdentifier());
					metaGroup.addOuterEdge(edge);
					if (!this.hideMetanode)
						continue;
				}
				hideEdge(edge);
			}
		}

		// Add the nodes back in
		restoreNodes();

		// See if we need to create membership edges
		if (!this.hideMetanode) {
			// See if we're resizing to the bounding box...
			if (this.sizeToBoundingBox && metanodeSize != null) {
				// Get the nodeView
				NodeView nv = networkView.getNodeView(groupNode);
				String id = groupNode.getIdentifier();
				nodeAttributes.setAttribute(id, VisualPropertyType.NODE_SHAPE.getBypassAttrName(), "rect");
				nodeAttributes.setAttribute(id, VisualPropertyType.NODE_HEIGHT.getBypassAttrName(), ""+metanodeSize.getHeight()+5);
				nodeAttributes.setAttribute(id, VisualPropertyType.NODE_WIDTH.getBypassAttrName(), ""+metanodeSize.getWidth()+5);
				nodeAttributes.setAttribute(id, VisualPropertyType.NODE_OPACITY.getBypassAttrName(), ""+this.metanodeOpacity);
				Cytoscape.getVisualMappingManager().vizmapNode(nv, networkView);
				nv.setShape(NodeView.RECTANGLE);
				nv.setHeight(metanodeSize.getHeight()+5);
				nv.setWidth(metanodeSize.getWidth()+5);

				// Now, if we're selected, select our children
				if (network.isSelected(nv.getNode()))
					network.setSelectedNodeState(metaGroup.getNodes(), true);
			} else {
				List<CyEdge> innerEdges = metaGroup.getInnerEdges();
				for (CyNode node: metaGroup.getNodes()) {
					// System.out.println("MetaNode: expanding "+groupNode+" node "+node);
					CyEdge edge = Cytoscape.getCyEdge(groupNode, node, Semantics.INTERACTION, "member", true);
					if (!innerEdges.contains(edge)) {
						metaGroup.addInnerEdge(edge);
					}
				}
			}
		}

		// Add the edges back in
		restoreEdges();
		// Remove the metaNode
		if (this.hideMetanode)
			network.hideNode(groupNode);

		if (update) {
			// update
			updateDisplay();
		}

		metaGroup.setState(MetaNodePlugin2.EXPANDED);

		// Now, for any of our nodes that are metaNodes and were expanded when we
		// collapsed, expand them to get them back into their original state
		restoreMetaNodes(true);
	}

	/**
	 * Create a meta edge.  A meta-edge is an edge that replaces one
	 * or more edges between two nodes with an edge between one of the
	 * nodes and a collapsed group that contains the other node.  More
	 * than one edge might be replaced if the node that is not part
	 * of our group (the partner node) has edges to more than one node
	 * that is part of our group. 
	 *
	 *     There are a couple of different cases when we go to create a
	 * meta-edge:
	 * 1) Neither node is hidden: Just create the edge.
	 * 2) Our partner node is hidden: Create a meta-edge to the parent
	 *	  of the partner and store it in both our list and our partners
	 *
	 * @param edge the CyEdge to create a metaEdge to replace
	 * @param ignoreCollapsed if 'true' create the metaEdge whether the
	 * partner node is collapsed or not.  This allows other MetaNodes to
	 * have us create missing metaEdges to them during expansion
	 * @return the created metaEdge
	 */
	public CyEdge createMetaEdge(CyEdge edge, CyNode partner, boolean ignoreCollapsed) {

		// logger.debug(metaGroup.getGroupName()+": Examining edge "+edge.getIdentifier());
		CyNode source = (CyNode)edge.getSource();
		CyNode target = (CyNode)edge.getTarget();

		CyEdge newEdge = null;

		if (partner == null)
			partner = getPartner(edge);

		// Is our partner collapsed?
		if (!ignoreCollapsed && isNodeHidden(partner)) {
			// The other edge is hidden.  It must have been collapsed
			// since we were created.  We need to create a meta-edge
			// between us and the parent of the collapsed node.  We
			// also need to have the parent of the collapsed node create
			// a meta-node back to our node, but since we're collapsing,
			// we need to add it in without creating it.
			MetaNode parent = getParent(partner);
			if (parent == null) {
				return null;
			}
			// logger.debug("... parent of "+partner.getIdentifier()+" is "+parent.metaGroup.getGroupName());
			if (MetaNodeManager.getMetaNode(parent.groupNode) == null || isNodeHidden(parent.groupNode)) {
				// logger.debug("... returning edge "+edge.getIdentifier());
				return null;
			}

			if (!multipleEdges && metaEdgeMap.containsKey(parent.groupNode) && parent.isCollapsed()) {
				// Already seen this?
				// logger.debug("... returning cached edge "+metaEdgeMap.get(parent.groupNode).getIdentifier());
				return metaEdgeMap.get(parent.groupNode);
			}

			newEdge = getMetaEdge(groupNode, parent.groupNode, edge);

			// Add it to our maps
			addMetaEdge(newEdge, parent.groupNode, edge);

			// Add it to the other group's maps
			parent.addMetaEdge(newEdge, groupNode, edge);
		
		} else if (!multipleEdges && metaEdgeMap.containsKey(partner)) {
			// We've already seen this partner and we only want
			// one edge per partner
			
			// First make sure our partner is not an expanded metanode
			if (MetaNodeManager.getMetaNode(partner) != null && !MetaNodeManager.getMetaNode(partner).isCollapsed()) {
				// logger.debug("... returning null ");
				// skip it
				return null;
			}
			newEdge = metaEdgeMap.get(partner);
			addMetaEdge(newEdge, partner, edge);
		} else {
			// Do we already have this edge?
			if (metaEdgeMap.containsKey(partner)) {
				// Yes, return it
				return null;
				// newEdge = metaEdgeMap.get(partner);
			} else {
				if (source == partner)
					target = groupNode;
				else
					source = groupNode;

				newEdge = getMetaEdge(source, target, edge);
				addMetaEdge(newEdge, partner, edge);
			}
		}
		// logger.debug("... returning edge "+newEdge.getIdentifier());
		return newEdge;
	}

	public void addMetaEdge(CyEdge newEdge, CyNode partner, CyEdge edge) {
		if (!newEdgeMap.containsKey(newEdge)) {
			newEdgeMap.put(newEdge, new ArrayList());
		}
		if (!newEdgeMap.get(newEdge).contains(edge))
			newEdgeMap.get(newEdge).add(edge);
		metaEdgeMap.put(partner,newEdge);
	}

	public int getDescendentCount() {
		return nDescendents;
	}

	/**
 	 * Create a new network from the currently collapsed group
 	 */
	public void createNetworkFromGroup() {
		// Get the list of nodes in the group
		List<CyNode> nodes = metaGroup.getNodes();
		List<CyEdge> edges = metaGroup.getInnerEdges();
		CyNetwork new_net = Cytoscape.createNetwork(nodes, edges, 
		                                            metaGroup.getGroupName(), 
		                                            Cytoscape.getCurrentNetwork());
		CyNetworkView new_view = Cytoscape.createNetworkView(new_net, "group");
		// Now that we have a style, apply our position hints
		for (CyNode node: nodes) {
			double xValue = getXHintAttr(nodeAttributes, node.getIdentifier(), 0.0);
			double yValue = getYHintAttr(nodeAttributes, node.getIdentifier(), 0.0);
			NodeView v = new_view.getNodeView(node);
			v.setXPosition(xValue);
			v.setYPosition(yValue);
		}
		// Set the visual style
		new_view.setVisualStyle(Cytoscape.getCurrentNetworkView().getVisualStyle().getName());
		new_view.fitContent();
	}

	/**
 	 * Determine if this metanode is collapsed in this view
 	 *
 	 * @param view the network view to check
 	 * @return True if it is collapsed, False otherwise
 	 */
	public boolean isCollapsed() {
		if (metaGroup.getState() == MetaNodePlugin2.COLLAPSED)
			return true;
		return false;
	}

	/**
	 * Sets whether or not we hide the metnode when we expand the
	 * network.
	 *
	 * @param hide if 'true' we hide the metanode upon expansion
	 */
	public void setHideMetaNode(boolean hide) {
		this.hideMetanode = hide;
	}

	/**
	 * Sets the opacity of a metanode if we don't hide on expansion.
	 *
	 * @param opacity the opacity (between 0 and 100)
	 */
	public void setExpandedOpacity(double opacity) {
		this.metanodeOpacity = 255*opacity/100;
	}

	/**
	 * Controls whether this metanode is aggregating attributes
	 *
	 * @param aggregate if 'true' aggregate
	 */
	public void setAggregateAttributes(boolean aggregate) {
		if (!this.aggregateAttributes && aggregate) {
			this.aggregateAttributes = aggregate;
			updateAttributes();
		} else {
			this.aggregateAttributes = aggregate;
		}
	}


	/**
	 * Returns 'true' if we hide the metnode when we expand the
	 * network.
	 *
	 * @return 'true' if we hide the metanode upon expansion
	 */
	public boolean getHideMetaNode() {
		return this.hideMetanode;
	}

	/**
	 * Sets whether or not we size the metnode to the bounding box
	 * of all of the children when we expand the network.  NOTE:
	 * this only makes sense if hideMetanode is false.
	 *
	 * @param hide if 'true' we resize on expansion
	 */
	public void setSizeToBoundingBox(boolean resize) {
		this.sizeToBoundingBox = resize;
	}

	/**
	 * Returns 'true' if we hide the metnode when we expand the
	 * network.
	 *
	 * @return 'true' if we hide the metanode upon expansion
	 */
	public boolean getSizeToBoundingBox() {
		return this.sizeToBoundingBox;
	}

	/*****************************************************************
	 *                   Private methods                             *
	 ****************************************************************/

	private CyEdge getMetaEdge(CyNode source, CyNode target, CyEdge edge) {
		// Create the edge
		String identifier = "meta-"+edge.getIdentifier();
		String interaction = edgeAttributes.getStringAttribute(edge.getIdentifier(), Semantics.INTERACTION);

		CyEdge newEdge = Cytoscape.getCyEdge(source.getIdentifier(),identifier,
		                                     target.getIdentifier(),"meta-"+interaction);
		// logger.debug("Created meta-edge "+newEdge.getIdentifier());
		return newEdge;
	}

	/**
	 * Create all of the necessary metaEdges
	 *
	 */
	private void createMetaEdges() {
		List<CyNode> nodes = metaGroup.getNodes();
		// Get the list of external edges
		List<CyEdge> edges = metaGroup.getOuterEdges();
	
		// Attach them to the group node
		for (CyEdge edge: edges) {
			// logger.debug("Outer edge: "+edge.getIdentifier());
			CyEdge newEdge = createMetaEdge(edge, null, false);
			if (newEdge != null) {
				network.addEdge(newEdge);
				if (networkView != null)
					networkView.applyVizMap(newEdge);
			}
		}
		return;
	}

	/**
	 * Restore the edges we hid when we collapsed.  This
	 * is called as part of the expansion process.
	 */
	private void restoreEdges() {
		//
		// Restore outer edges
		//
		// logger.debug(metaGroup.getGroupName()+": restoreEdges");
		for (CyEdge edge: metaGroup.getOuterEdges()) {
			// First, see if the partner of this edge is gone
			// logger.debug("... restoring outer edge "+edge.getIdentifier());
			String identifier = edge.getIdentifier();
			// Get the edge partner
			CyNode partner = getPartner(edge);
			// logger.debug("... partner is "+partner.getIdentifier());
			// Is our partner an expanded metaNode?
			if (MetaNodeManager.getMetaNode(partner) != null && !MetaNodeManager.getMetaNode(partner).isCollapsed()) {
				// Yes, just continue
				continue;
			} else if (isNodeHidden(partner)) {
				// Partner is collapsed -- need to make a meta edge
				// Get the parent
				MetaNode parent = getParent(partner);
				if (parent != null) {
					// Create the meta-edge
					// logger.debug("... parent of "+partner.getIdentifier()+" is "+parent.metaGroup.getGroupName());
					restoreEdge(parent.createMetaEdge(edge, getLocalNode(edge), false));
				}
			} else {
				restoreEdge(edge);
			}
		}

		//
		// Restore inner edges
		//
		for (CyEdge edge: metaGroup.getInnerEdges()) {
			// logger.debug("restoreEdges: restoring inner edge "+edge.getIdentifier());
			restoreEdge(edge);
		}
	}

	/**
	 * Internal routine for restoring edges.  This routine makes sure that
	 * the target of the edge is visible before we restore it.  At this point,
	 * since there is no way to determine if the node is hidden or not, 
	 *
	 * @param edge the edge we might want to restore
	 */
	private void restoreEdge(CyEdge edge) {
		if (edge == null) return;
		CyNode partner = getPartner(edge);
		if (!isNodeHidden(partner)) {
			// logger.debug("Restoring edge "+edge.getIdentifier()+" partner = "+partner.getIdentifier());
			network.restoreEdge(edge);
			if (networkView != null && networkView != Cytoscape.getNullNetworkView());
				networkView.applyVizMap(edge);
		} else {
			// logger.debug("Not restoring edge "+edge.getIdentifier()+". "+
			//               partner.getIdentifier()+" is hidden");
		}
	}

	/**
	 * Internal routine to hide an edge.  Actually, in the case of edges,
	 * we just go ahead and do the hide
	 *
	 * @param edge the edge we want to hide
	 */
	private void hideEdge(CyEdge edge) {
		// logger.debug("Hiding edge "+edge.getIdentifier());
		network.hideEdge(edge);
	}

	/**
	 * Restore the nodes we hid when we collapsed.  This
	 * is called as part of the expansion process.
	 */
	private void restoreNodes () {
		NodeView metaNodeView = (NodeView)networkView.getNodeView(groupNode);
		double xCenter = 0;
		double yCenter = 0;
		if (metaNodeView != null) {
			xCenter = metaNodeView.getXPosition();
			yCenter = metaNodeView.getYPosition();
		}
		double xOffset = 0;
		double yOffset = 0;

		for (CyNode node: metaGroup.getNodes()) {
			double xValue = getXHintAttr(nodeAttributes, node.getIdentifier(), xCenter);
			double yValue = getYHintAttr(nodeAttributes, node.getIdentifier(), yCenter);
			Dimension d = new Dimension();
			d.setSize(xValue, yValue);
			// Is this already visible?
			if (network.containsNode(node))
				continue;
			restoreNode(node, d);
		}
	}

	/**
	 * Restore any metaNodes that we have that were expanded when we collapsed
	 * @param update update the display?
	 */
	private void restoreMetaNodes (boolean update) {
		if (childMetaNodes == null) return;

		for (int i = childMetaNodes.size()-1; i >= 0; i--) {
			MetaNode child = childMetaNodes.get(i);
			child.expand(true, networkView, update);
		}
		childMetaNodes = null;
	}

	/**
	 * Hide the nodes during collapse.
	 *
	 * @param recursive if 'true' this is recursive
	 * @param multipleEdges if 'true' make one metaEdge for each edge
	 * @return the X,Y location of the center of the hidden nodes
	 */
	private Dimension hideNodes(boolean recursive, boolean multipleEdges) {
		List<CyNode> nodes = metaGroup.getNodes();
		// Remove each of the member nodes (but remember where they were)
		Iterator <CyNode> nodeIter = nodes.iterator();
		double xCenter = 0;
		double yCenter = 0;
		double xMax = -100000;
		double xMin = 100000;
		double yMax = -100000;
		double yMin = 100000;
		Double xLocations[] = new Double[nodes.size()];
		Double yLocations[] = new Double[nodes.size()];
		int averageCount = 0;
		for (int i = 0; i < nodes.size(); i++) {
			CyNode node = nodes.get(i);
			// Check and see if this is a group
			MetaNode child = MetaNodeManager.getMetaNode(node);
			if (child != null) {
				// Yes, recurse down
				if (!child.isCollapsed()) {
					if (childMetaNodes == null) {
						childMetaNodes = new ArrayList<MetaNode>();
					}
					childMetaNodes.add(child);
					child.collapse(recursive, multipleEdges, false, networkView);
				}
			}
			NodeView nodeView = (NodeView)networkView.getNodeView(node);
			if (nodeView != null && !isNodeHidden(node)) {
				averageCount++;
				double xLocation = nodeView.getXPosition();
				double yLocation = nodeView.getYPosition();
				double width = nodeView.getWidth();
				double height = nodeView.getHeight();

				xLocations[i] = new Double(xLocation);
				yLocations[i] = new Double(yLocation);
				
				xCenter += xLocation;
				yCenter += yLocation;

				// Calculate our bounding box
				if (xLocation+width/2 > xMax) xMax = xLocation+width/2;
				if (yLocation+height/2 > yMax) yMax = yLocation+height/2;
				if (xLocation-width/2 < xMin) xMin = xLocation-width/2;
				if (yLocation-height/2 < yMin) yMin = yLocation-height/2;
			} else {
				xLocations[i] = null;
				yLocations[i] = null;
			}
		}
		// xCenter = xCenter / averageCount;
		// yCenter = yCenter / averageCount;
		xCenter = xMin+(xMax-xMin)/2;
		yCenter = yMin+(yMax-yMin)/2;

		for (int i = 0; i < nodes.size(); i++) {
			CyNode node = nodes.get(i);
			String nodeName = node.getIdentifier();
			if (xLocations[i] == null)
				setXHintAttr(nodeAttributes,nodeName,0.0);
			else
				setXHintAttr(nodeAttributes,nodeName,xCenter-xLocations[i].doubleValue());

			if (yLocations[i] == null)
				setYHintAttr(nodeAttributes,nodeName,0.0);
			else
				setYHintAttr(nodeAttributes,nodeName,yCenter-yLocations[i].doubleValue());
			// Hide the node
			hideNode(node);
		}
		if (metanodeSize == null) {
			metanodeSize = new Dimension();
		}
		metanodeSize.setSize(xMax-xMin, yMax-yMin);
		Dimension dim = new Dimension();
		dim.setSize(xCenter, yCenter);
		return dim;
	}

	/**
	 * Internal routine to hide a node.  This routine basically
	 * just hides the code, but is also keeps track of which nodes
	 * are hidden.
	 *
	 * @param node the node to hide
	 */
	private void hideNode(CyNode node) {
		// logger.debug("Hiding node "+node.getIdentifier());
		network.hideNode(node);
	}

	/**
	 * Internal routine to restore a node.  This routine basically
	 * just restores the code, but is also keeps track of which nodes
	 * are hidden.
	 *
	 * @param node the node to restore
	 */
	private void restoreNode(CyNode node, Dimension center) {
		// logger.debug("Restoring node "+node.getIdentifier());

		network.restoreNode(node);

		if (center != null) {
			// Get the nodeView
			NodeView nv = (NodeView)networkView.getNodeView(node);
			if (nv == null) {
				nv = networkView.addNodeView(node.getRootGraphIndex());
			}

			if (nv != null) {
				networkView.applyVizMap(node);
				nv.setXPosition(center.getWidth());
				nv.setYPosition(center.getHeight());
			}
		}
	}

	/**
	 * Test to see if a node is hidden
	 *
	 * @param node the node to check
	 * @return true if the node is hidden, false otherwise
	 */
	private boolean isNodeHidden(CyNode node) {
		return (!network.containsNode(node));
	}

	/**
	 * Get the partner (i.e. the 'other' node) for an edge
	 *
	 * @param edge the edge to get the partner for
	 * @return the partner CyNode
	 */
	private CyNode getPartner(CyEdge edge) {
		CyNode source = (CyNode)edge.getSource();
		CyNode target = (CyNode)edge.getTarget();
		if (source == groupNode || metaGroup.getNodes().contains(source))
			return target;
		return source;
	}

	/**
	 * Get the localNode for an edge
	 *
	 * @param edge the edge to get the localnode for
	 * @return the local CyNode
	 */
	private CyNode getLocalNode(CyEdge edge) {
		CyNode source = (CyNode)edge.getSource();
		CyNode target = (CyNode)edge.getTarget();
		if (source == groupNode || metaGroup.getNodes().contains(source))
			return source;
		return target;
	}


	/**
	 * If our parter is a MetaNode, get our partner's
	 * newEdge (i.e. metaEdge) list.
	 *
	 * @param metaEdge the metaEdge to use to get the partner MetaNode
	 * @return the List of metaEdges created by the partner
	 */
	private List<CyEdge> getPartnerEdgeList(CyEdge metaEdge) {
		CyNode partner = getPartner(metaEdge);
		// We have the partner node -- get the group
		MetaNode metaPartner = MetaNodeManager.getMetaNode(partner);
		if (metaPartner == null) {
			// Shouldn't happen!
			return new ArrayList<CyEdge>(0);
		}
		return metaPartner.newEdgeMap.get(metaEdge);
	}

	/**
	 * Return the MetaNode for a node, if this node is part
	 * of a MetaNode
	 *
	 * @param node the CyNode to find the parent for
	 * @return the MetaNode that is the parent for the node
	 */
	private MetaNode getParent(CyNode node) {
		// First, get the list of groups
		List<CyGroup> groupList = node.getGroups();
		if (groupList == null) return null;

		for (CyGroup group: groupList) {
			MetaNode meta = MetaNodeManager.getMetaNode(group.getGroupNode());
			if (meta != null) {
				if (meta != this && meta.isCollapsed()) return meta;
			}
		}
		return null;
	}

	/**
	 * Return 'true' if the edge is a meta-edge
	 *
	 * @param edge the edge to test
	 * @return true if edge is a meta-edge
	 */
	private boolean isMetaEdge(CyEdge edge) {
		if (metaEdgeMap.containsValue(edge))
			return true;

		return false;
	}

	/**
	 * Update the display
	 */
	private void updateDisplay() {
		VisualMappingManager vizmapper = Cytoscape.getVisualMappingManager();
		vizmapper.applyAppearances();
		// networkView.updateView();
		networkView.redrawGraph(false,true);
	}

	/**
	 * Update our child counts and attributes
	 */
	protected void updateAttributes() {

		// Start by walking through all of the node
		// attributes and figure out the number of
		// children and dscendents that we have.  We'll
		// also update our attributes as we go
		nChildren = metaGroup.getNodes().size();
		nDescendents = nChildren;

		// We need to update all of our contained metanode attributes
		// first or we'll wind up adding extra attributes
		for (CyNode node: metaGroup.getNodes()) {
			MetaNode mn = MetaNodeManager.getMetaNode(node);
			if (mn != null) {
				if (mn.getCyGroup().getState() == MetaNodePlugin2.COLLAPSED) {
					mn.updateAttributes();
				}
			}
		}

		// OK, now handle
		for (CyNode node: metaGroup.getNodes()) {
			MetaNode mn = MetaNodeManager.getMetaNode(node);
			if (mn != null) {
				// This node is a metaNode
				if (mn.getCyGroup().getState() == MetaNodePlugin2.COLLAPSED) {
					nDescendents += mn.getDescendentCount()-1;
					aggregateAttributes(nodeAttributes, "node", node.getIdentifier(), mn);
				}
			} else {
				aggregateAttributes(nodeAttributes, "node", node.getIdentifier(), null);
			}
		}
		// logger.debug("Assigning attributes for node "+groupNode.getIdentifier());
		assignAttributes(nodeAttributes, "node", groupNode.getIdentifier());

		// Update our "special" attributes
		nodeAttributes.setAttribute(groupNode.getIdentifier(), CHILDREN_ATTR,
		                            new Integer(nChildren));
		nodeAttributes.setAttribute(groupNode.getIdentifier(), DESCENDENTS_ATTR,
		                            new Integer(nDescendents));

		// OK, now walk though our meta edges and update
		// those attributes.  Note that there isn't an easy
		// way to update meta edges recursively, so we just
		// do our best
		for (CyEdge metaEdge: newEdgeMap.keySet()) {
			for (CyEdge childEdge: newEdgeMap.get(metaEdge)) {
				aggregateAttributes(edgeAttributes, "edge", childEdge.getIdentifier(), null);
			}
			assignAttributes(edgeAttributes, "edge", metaEdge.getIdentifier());
		}
	}

	/**
	 * Convenience method to set the X hint attribute.
	 *
	 * @param nodeAttributes the node attributes
	 * @param nodeName the name of the node
	 * @param value the value to set
	 */
	private void setXHintAttr(CyAttributes nodeAttributes, String nodeName, double value) {
		String attr = metaGroup.getGroupName()+":"+X_HINT_ATTR;
		nodeAttributes.setUserVisible(attr,false);
		nodeAttributes.setAttribute(nodeName,attr,value);
	}

	/**
	 * Convenience method to set the Y hint attribute.
	 *
	 * @param nodeAttributes the node attributes
	 * @param nodeName the name of the node
	 * @param value the value to set
	 */
	private void setYHintAttr(CyAttributes nodeAttributes, String nodeName, double value) {
		String attr = metaGroup.getGroupName()+":"+Y_HINT_ATTR;
		nodeAttributes.setUserVisible(attr,false);
		nodeAttributes.setAttribute(nodeName,attr,value);
	}

	/**
	 * Convenience method to get the X hint attribute.
	 *
	 * @param nodeAttributes the node attributes
	 * @param nodeName the name of the node
	 * @return the X Hint attribute value
	 */
	private double getXHintAttr(CyAttributes nodeAttributes, String nodeName, double center) {
		String attr = metaGroup.getGroupName()+":"+X_HINT_ATTR;
		if (nodeAttributes.hasAttribute(nodeName, attr))
			return center - nodeAttributes.getDoubleAttribute(nodeName,attr);
		else if (nodeAttributes.hasAttribute(nodeName, X_HINT_ATTR))
			return center - nodeAttributes.getDoubleAttribute(nodeName,X_HINT_ATTR);
		else 
			return center;
	}

	/**
	 * Convenience method to get the Y hint attribute.
	 *
	 * @param nodeAttributes the node attributes
	 * @param nodeName the name of the node
	 * @return the Y Hint attribute value
	 */
	private double getYHintAttr(CyAttributes nodeAttributes, String nodeName, double center) {
		String attr = metaGroup.getGroupName()+":"+Y_HINT_ATTR;
		if (nodeAttributes.hasAttribute(nodeName, attr))
			return center - nodeAttributes.getDoubleAttribute(nodeName,attr);
		else if (nodeAttributes.hasAttribute(nodeName, Y_HINT_ATTR))
			return center - nodeAttributes.getDoubleAttribute(nodeName,Y_HINT_ATTR);
		else 
			return center;
	}

	/**
	 * Aggregate the data into our map.
	 *
	 * @param attrMap the attributes over which we're aggregating
	 * @param attrType "edge" or "node"
	 * @param source the source (node or edge ID) for the attributes
	 * @param recurse a metanode if we are supposed to recurse (only done for MEDIAN)
	 */
	private void aggregateAttributes(CyAttributes attrMap, 
	                                 String attrType,
	                                 String source, MetaNode recurse) {
		if (!AttributeHandler.getEnable())
			return;

		String [] attributes = attrMap.getAttributeNames();
		for (int i = 0; i < attributes.length; i++) {
			String attr = attributes[i];
			byte type = attrMap.getType(attr);
			// Also need to add exclusions
			if (!attrMap.getUserVisible(attr) || 
			    type == CyAttributes.TYPE_COMPLEX ||
			    type == CyAttributes.TYPE_SIMPLE_MAP) {
				continue;
			}

			// Do we have a specific handler (override)?
			AttributeHandler handler = AttributeHandler.getHandler(attrType+"."+attr);
			if (handler == null) {
				// No, create a basic handler
				handler = AttributeHandler.getDefaultHandler(attrMap.getType(attr), attrType+"."+attr);
			}
			if (handler != null) {
				// Aggregate
				aggregateAttribute(attrMap, handler, source, recurse);
			}
		}
	}

	private void aggregateAttribute(CyAttributes attrMap,
	                                AttributeHandler handler,
	                                String source,
	                                MetaNode recurse) {

		if (recurse == null) {
			Object value = handler.aggregateAttribute(attrMap, source, 1);
			return;
		}
		if (handler.getHandlerType() != AttributeHandlingType.MEDIAN &&
		    handler.getHandlerType() != AttributeHandlingType.MCV) {
			handler.aggregateAttribute(attrMap, source, recurse.getDescendentCount());
			return;
		}

		for (CyNode node: recurse.getCyGroup().getNodes()) {
			MetaNode mn = MetaNodeManager.getMetaNode(node);
			if (mn != null) {
				aggregateAttribute(attrMap, handler, null, mn);
			} else {
				aggregateAttribute(attrMap, handler, node.getIdentifier(), null);
			}
		}
	}

	/**
 	 * Actually assign the aggregated attributes to our meta (edge,node)
 	 *
 	 * @param attrMap the attributes map we're assigning to
	 * @param attrType "edge" or "node"
 	 * @param target the name of the object
 	 */
	private void	assignAttributes(CyAttributes attrMap,
	                               String attrType,
	                               String target) {

		if (!AttributeHandler.getEnable())
			return;

		String [] attributes = attrMap.getAttributeNames();
		for (int i = 0; i < attributes.length; i++) {
			String attr = attributes[i];
			// Get our handler
			AttributeHandler handler = AttributeHandler.getHandler(attrType+"."+attr);
			if (handler != null)
				handler.assignAttribute(attrMap, target);
		}
	}
}
