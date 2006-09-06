/* :vim set ts=2: */
/**
 * This package implements the notion of "grouping" a series
 * of CyNodes in a CyNetwork.  The API handles the maintenance
 * of groups, but not the related presentation of groups.  A
 * separate interface (GroupAbstractionModel) is used to actually
 * realize the presentation of the grouping model to the user.
 *
 * A "group" for our purposes is represented by a CyNode and an
 * associated CyNetwork.  The CyNode might be permanently hidden,
 * or displayed when in a "grouped" state, depending on the 
 * GroupAbstractionModel being used.  Hidden CyAttributes associated
 * with the group CyNode are used to maintain group membership
 * and information about associated edges.
 *
 * This code draws heavily on the "metanode" implementation done
 * by Iliana Avila-Campillo during her tenure at the Institute for
 * Systems Biology, and we gratefully acknowledge her efforts:
 * without them, we never would have gotten started.
 */
package edu.ucsf.groups;

import java.util.*;
import cytoscape.*;
import cytoscape.data.*;

import edu.ucsf.groups.view.GroupAbstractionViewer;
import edu.ucsf.groups.data.GroupAttributesHandler;
import edu.ucsf.groups.model.GroupAbstractionModel;

/**
 * This is just a container class to provide convenient access to
 * the group methods.
 */
public class GroupUtils {
	/**
	 * This is the default model we will use if the caller does not
	 * provide one to us.  This should be set to the expand/contract
	 * model (when that's written).
	 */
	public static final GroupAbstractionModel defaultModel = null;

	/**
	 * This is the default group attributes handler we will use if the 
	 * caller does not provide one to us.  We need to discuss exactly
	 * what this should be set to.  My suggestion is to use a (slight)
	 * modification of the attributes handler implemented in the metanodes
	 * code.
	 */
	public static final GroupAttributesHandler defaultAttHandler = null;

	/**
	 * This method creates a group when given two networks: the current
	 * network and the new network that will be "contained" within the
	 * group.
	 *
	 * @param network the CyNetwork that will contain the resulting group node
	 * @param child_network the CyNetwork that contains the nodes to be grouped
	 * @param att_handler the group attributes handler
	 * @param model the grouping abstraction modeler
	 * @return the CyNode that represents the group
	 */
	public static CyNode createGroup(CyNetwork network, 
																	 CyNetwork child_network,
																	 GroupAttributesHandler att_handler,
																	 GroupAbstractionModel model) 
	{
		return (CyNode)null;
	}

	/**
	 * This method creates a group when given two networks: the current
	 * network and the new network that will be "contained" within the
	 * group.
	 *
	 * @param network the CyNetwork that will contain the resulting group node
	 * @param child_network the CyNetwork that contains the nodes to be grouped
	 * @return the CyNode that represents the group
	 */
	public static CyNode createGroup(CyNetwork network, 
																	 CyNetwork child_network) 
	{
		return createGroup(network, child_network, defaultAttHandler, defaultModel);
	}

	/**
	 * This method will "ungroup" the group, that is, it will present the
	 * nodes in an "ungrouped" state.
	 *
	 * @param network the CyNetwork that will contain the resulting group node
	 * @param group_node the CyNode that 'contains' this group
	 * @param recursive if this is 'true', all groups contained within this group will be ungrouped
	 */
	public static void ungroupGroup(CyNetwork network, CyNode group_node, boolean recursive) {}

	/**
	 * This method will "regroup" the group, that is, it will present the
	 * nodes in an "grouped" state.
	 *
	 * @param network the CyNetwork that will contain the resulting group node
	 * @param group_node the CyNode that 'contains' this group
	 * @param create_multiple_edges if the grouping action results in the "hiding" of the
	 * internal edges, then setting this to <code>true</code> will cause the external edges 
	 * will be created with one external edge for every internal edge that connects to
	 * an external node.  Otherwise, only one edge will be created between the group node 
	 * and any externally connected node.
	 * @param create_relationship_edges if the grouping action results in the "hiding" of the
	 * internal edges, then setting this to <code>true</code> will cause the creations of 
	 * edges that represent the relationship between this group and possible children.  
	 * This provides a mechanism to node that an ungrouped node from a different group is
	 * also a member of this group.
	 */
	public static void regroupGroup(CyNetwork network, CyNode group_node, 
										boolean create_multiple_edges, boolean create_relationship_edges) {}

	/**
	 * This method will remove a group -- essentially a permanent "ungroup".
	 *
	 * @param network the CyNetwork containing this group node.
	 * @param group_node the CyNode representing the group.
	 */
	public static void removeGroup(CyNetwork network, CyNode group_node) {}

	/**
	 * This method allows the caller to explicitly set the abstraction model used to
	 * represent the group.  This provides a mechanism for plug-in authors to provide
	 * alternate abstractions.
	 *
	 * @param group_node The CyNode that represents the group
	 * @param model The GroupAbstractionModel to use with this group
	 */
	public static void setGroupAbstraction(CyNode group_node, GroupAbstractionModel model) {}

	/**
	 * This method returns the list of groups (as represented by CyNodes) that this
	 * CyNode is a member of.
	 *
	 * @param network the CyNetwork containing the groups
	 * @param member the CyNode whose membership we are looking for
	 * @return a List of group CyNodes
	 */
	public static List getGroupNodes(CyNetwork network, CyNode member) 
	{
		return (List) null;
	}

	/**
	 * This method returns the list of members that are contained within this group.
	 *
	 * @param network the CyNetwork containing the groups
	 * @param group_node the group CyNode whose members we want
	 * @return a List of member CyNodes
	 */
	public static List getGroupMembers(CyNetwork network, CyNode group_node)
	{
		return (List) null;
	}

	/**
	 * Test to see if a group node is in the "group" or "ungrouped" state.
	 *
	 * @param network the CyNetwork containing the groups.
	 * @param group_node the group CyNode whose state we want to check.
	 * @return a boolean <code>true</code> if the group is in the 'grouped' state.
	 */
	public static boolean isGrouped(CyNetwork network, CyNode group_node) 
	{ return false; }

	/**
	 * Inform the group API that an edge has been added.
	 *
	 * @param network the CyNetwork containing the groups.
	 * @param edge the CyEdge that has been added.
	 */
	public static void addEdgeNotify(CyNetwork network, CyEdge edge) {}

	/**
	 * Inform the group API that an edge has been deleted.
	 *
	 * @param network the CyNetwork containing the groups.
	 * @param edge the CyEdge that has been deleted.
	 */
	public static void deleteEdgeNotify(CyNetwork network, CyEdge edge) {}

	/**
	 * Inform the group API that a node has been deleted.
	 *
	 * @param network the CyNetwork containing the groups.
	 * @param node the CyNode that has been deleted.
	 * 
	 */
	public static void deleteNodeNotify(CyNetwork network, CyNode node) {}
}
