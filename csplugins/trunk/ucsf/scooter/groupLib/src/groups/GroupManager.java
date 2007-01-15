/* :vim set ts=2: */
/**
 * This package implements the notion of "grouping" a series
 * of CyNodes in a CyNetwork.  The API handles the maintenance
 * of groups, but not the related presentation of groups.  A
 * separate interface (GroupModel) is used to actually
 * realize the presentation of the grouping model to the user.
 *
 * A "group" for our purposes is represented by a CyNode and an
 * associated CyNetwork.  The CyNode might be permanently hidden,
 * or displayed when in a "grouped" state, depending on the 
 * GroupModel being used.  Hidden CyAttributes associated
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

import edu.ucsf.groups.view.GroupViewer;
import edu.ucsf.groups.data.GroupAttributesHandler;
import edu.ucsf.groups.model.GroupModel;

/**
 * This is just a container class to provide convenient access to
 * the group methods.
 */
public class GroupManager {
	/**
	 * This is the default model we will use if the caller does not
	 * provide one to us.  This should be set to the expand/contract
	 * model (when that's written).
	 */
	private static final GroupModel defaultModel = null;

	/**
	 * This is the default group attributes handler we will use if the 
	 * caller does not provide one to us.  We need to discuss exactly
	 * what this should be set to.  My suggestion is to use a (slight)
	 * modification of the attributes handler implemented in the metanodes
	 * code.
	 */
	private static final GroupAttributesHandler defaultAttHandler = null;

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
																	 GroupModel model) 
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
	 * This method copies a group from one network to another.  Note that
	 * this is a /shallow/ copy, so only the group-related data structures
	 * are copied.  Enclosed nodes should already exist in the destination
	 * network.
	 *
	 * @param group the CyNode that represents the group to be copied
	 * @param newNetwork the CyNetwork that will contain the new group
	 * @return the CyNode that represents the new group
	 */
	public static CyNode copyGroup(CyNode group, CyNetwork newNetwork)
	{ return (CyNode)null; }

	/**
	 * This method will "ungroup" the group, that is, it will present the
	 * nodes in an "ungrouped" state.
	 *
	 * @param group_node the CyNode that 'contains' this group
	 * @param recursive if this is 'true', all groups contained within this group will be ungrouped
	 */
	public static void ungroupGroup(CyNode group_node, boolean recursive) {}

	/**
	 * This method will "regroup" the group, that is, it will present the
	 * nodes in an "grouped" state.
	 *
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
	public static void regroupGroup(CyNode group_node, 
										boolean create_multiple_edges, boolean create_relationship_edges) {}

	/**
	 * This method will remove a group -- essentially a permanent "ungroup".
	 *
	 * @param group_node the CyNode representing the group.
	 */
	public static void removeGroup(CyNode group_node) {}

	/**
	 * This method allows the caller to explicitly set the abstraction model used to
	 * represent the group.  This provides a mechanism for plug-in authors to provide
	 * alternate abstractions.
	 *
	 * @param group_node The CyNode that represents the group
	 * @param model The GroupModel to use with this group
	 */
	public static void setGroupModel(CyNode group_node, GroupModel model) {}

  /**
	 * This method returns the current model for this group.
	 *
	 * @param group_node The CyNode that represents the group
	 * @return The GroupModel in use with this group
	 */
	public static GroupModel getGroupModel(CyNode group_node) {return (GroupModel) null;}

	/**
	 * This method allows the caller to explicitly set the attributes handler used with
	 * this group.  This provides a mechanism for plug-in authors to provide
	 * alternate attributes handlers.
	 *
	 * @param group_node The CyNode that represents the group
	 * @param handler The GroupAttributesHandler to use with this group
	 */
	public static void setGroupAttributesHandler(CyNode group_node, 
	                                             GroupAttributesHandler handler) {}

  /**
	 * This method returns the current attributes handler for this group.
	 *
	 * @param group_node The CyNode that represents the group
	 * @return The GroupAttributesHandler in use with this group
	 */
	public static GroupAttributesHandler getGroupAttributesHandler(CyNode group_node) 
	{return (GroupAttributesHandler) null;}

	/**
	 * This method returns the list of groups (as represented by CyNodes) that this
	 * CyNode is a member of.
	 *
	 * @param network limit the search to this CyNetwork. 
	 * @param member the CyNode whose membership we are looking for
	 * @return a List of group CyNodes
	 */
	public static List getGroupNodes(CyNetwork network, CyNode member) 
	{
		return (List) null;
	}

	/**
	 * This method returns the list of groups (as represented by CyNodes) that this
	 * CyNode is a member of (reguardless of which network).
	 *
	 * @param member the CyNode whose membership we are looking for
	 * @return a List of group CyNodes
	 */
	public static List getAllGroupNodes(CyNode member) 
	{
		return (List) null;
	}

	/**
	 * This method returns a List of the groups contained within this group
	 *
	 * @param group_node the CyNode representing the group
	 * @return a List of group CyNodes
	 */
	public static List getSubGroups(CyNode group_node) { return (List)null; }


	/**
	 * This method returns the list of members that are contained within this group.
	 *
	 * @param group_node the group CyNode whose members we want
	 * @return the CyNetwork that is the subnetwork representing this group
	 */
	public static CyNetwork getGroupMembers(CyNode group_node)
	{
		return (CyNetwork) null;
	}

	/**
	 * This method returns the CyNetwork this group is part of.
	 *
	 * @param group_node the group CyNode whose CyNetwork we want
	 * @return the CyNetwork this group is part of
	 *
	 */
	public static CyNetwork getCyNetwork(CyNode group_node)
	{ return (CyNetwork) null; }

	/**
	 * Test to see if a group node is in the "group" or "ungrouped" state.
	 *
	 * @param group_node the group CyNode whose state we want to check.
	 * @return a boolean <code>true</code> if the group is in the 'grouped' state.
	 */
	public static boolean isGrouped(CyNode group_node) 
	{ return false; }

	/**
	 * This method returns the default model we will use if the caller does not
	 * provide one to us.
	 *
	 * @return the default GroupModel
	 */
	private static GroupModel getDefaultModel()
	{ return (GroupModel) null; }

	/**
	 * This method returnsthe default group attributes handler we will use if the 
	 * caller does not provide one to us.
	 *
	 * @return the default GroupAttributesHandler
	 */
	private static GroupAttributesHandler getDefaultAttributesHandler()
	{ return (GroupAttributesHandler) null; }
}
