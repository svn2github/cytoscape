/* vim: set ts=2: */
/**
 * @author Scooter Morris
 * @version %I%, %G%
 * @since 2.0
 */

package edu.ucsf.groups.model;

import java.util.List;

import cytoscape.*;
import cytoscape.data.*;

import edu.ucsf.groups.view.GroupAbstractionViewer;

/**
 * An interface for classes that handle the model associated with presentation
 * of grouped nodes.
 */

public interface GroupAbstractionModel {
	/**
	 * Return the "name" of this abstraction.  This will be stored
	 * as part of the group.
	 *
	 * @return a String with the name of the abstraction
	 */
	public String getGroupAbstractionName();

	/**
	 * Set the GroupViewer for this group.  This is linked to the
	 * abstraction since not all viewers will work with all abstractions.
	 * Note that each abstraction should provide (or utilize an existing)
	 * default viewer.
	 *
	 * @param viewer the GroupViewer to use
	 */
	public void setGroupViewer(GroupAbstractionViewer viewer);

	/**
	 * Get the GroupViewer for this group.
	 *
	 * @return the GroupViewer currently set
	 */
	public GroupAbstractionViewer getGroupViewer();

	/**
	 * This method is called from the GroupUtils API upon creation of
	 * a new group to allow the group abstraction implementation to perform
	 * any initialization required.  This is called after the GroupUtils
	 * API has completed all of its initialization.
	 *
	 * @param network the CyNetwork that will contain the resulting group node
	 * @param group_node the CyNode that represents the group
	 */
	public void createGroup(CyNetwork network, CyNode group_node);

	/**
	 * This method is called from the GroupUtils API upon deletion of
	 * a new group to allow the group abstraction implementation to perform
	 * any updates in internal data structures.  This is called before the GroupUtils
	 * API performs any updates, including the actual deletion of the node and its
	 * associated attributes.
	 *
	 * @param network the CyNetwork that will contain the resulting group node
	 * @param group_node the CyNode that represents the group
	 */
	public void removeGroup(CyNetwork network, CyNode group_node);

	/**
	 * This method is called from the GroupUtils API to actually present
	 * the group in a 'grouped' state.
	 *
	 * @param network the CyNetwork that will contain the resulting group node
	 * @param group_node the CyNode that represents the group
	 */
	public void regroupGroup(CyNetwork network, CyNode group_node);

	/**
	 * This method is called from the GroupUtils API to actually present
	 * the group in an 'ungrouped' state.
	 *
	 * @param network the CyNetwork that will contain the resulting group node
	 * @param group_node the CyNode that represents the group
	 */
	public void ungroupGroup(CyNetwork network, CyNode group_node);

	/**
	 * Inform the group model abstraction that an edge has been added.
	 * This can be an empty function if this abstraction has no need
	 * to track specific edge information.
	 *
	 * @param network the CyNetwork containing the groups.
	 * @param edge the CyEdge that has been added.
	 */
	public void addEdgeNotify(CyNetwork network, CyEdge edge);

	/**
	 * Inform the group model abstraction that an edge has been deleted.
	 * This can be an empty function if this abstraction has no need
	 * to track specific edge information.
	 *
	 * @param network the CyNetwork containing the groups.
	 * @param edge the CyEdge that has been deleted.
	 */
	public void deleteEdgeNotify(CyNetwork network, CyEdge edge);

	/**
	 * Inform the group model abstraction that a node has been deleted.
	 * This can be an empty function if this abstraction has no need
	 * to track specific node information.
	 *
	 * @param network the CyNetwork containing the groups.
	 * @param node the CyNode that has been deleted.
	 */
	public void addNodeNotify(CyNetwork network, CyNode node);
}//GroupAbstractionModel
