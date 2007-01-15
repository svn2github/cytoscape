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

import edu.ucsf.groups.view.GroupViewer;

/**
 * An interface for classes that handle the model associated with presentation
 * of grouped nodes.
 */

public interface GroupModel {
	/**
	 * Return the "name" of this abstraction.  This will be stored
	 * as part of the group.
	 *
	 * @return a String with the name of the abstraction
	 */
	public String getGroupName();

	/**
	 * Set the GroupViewer for this group.  This is linked to the
	 * abstraction since not all viewers will work with all abstractions.
	 * Note that each abstraction should provide (or utilize an existing)
	 * default viewer.
	 *
	 * @param viewer the GroupViewer to use
	 */
	public void setGroupViewer(GroupViewer viewer);

	/**
	 * Get the GroupViewer for this group.
	 *
	 * @return the GroupViewer currently set
	 */
	public GroupViewer getGroupViewer();

	/**
	 * This method is called from the GroupManager API upon creation of
	 * a new group to allow the group abstraction implementation to perform
	 * any initialization required.  This is called after the GroupManager
	 * API has completed all of its initialization.
	 *
	 * @param network the CyNetwork that will contain the resulting group node
	 * @param group_node the CyNode that represents the group
	 */
	public void createGroup(CyNetwork network, CyNode group_node);

	/**
	 * This method is called from the GroupManager API when it copies a group 
	 * from one network to another.
	 *
	 * @param oldGroup the CyNode that represents the group to be copied
	 * @param newGroup the CyNode that represents the new group 
	 */
	public void copyGroup(CyNode newGroup, CyNode oldGroup);

	/**
	 * This method is called from the GroupManager API upon deletion of
	 * a new group to allow the group abstraction implementation to perform
	 * any updates in internal data structures.  This is called before the GroupManager
	 * API performs any updates, including the actual deletion of the node and its
	 * associated attributes.
	 *
	 * @param group_node the CyNode that represents the group
	 */
	public void removeGroup(CyNode group_node);

	/**
	 * This method is called from the GroupManager API to actually present
	 * the group in a 'grouped' state.
	 *
	 * @param group_node the CyNode that represents the group
	 */
	public void regroupGroup(CyNode group_node);

	/**
	 * This method is called from the GroupManager API to actually present
	 * the group in an 'ungrouped' state.
	 *
	 * @param group_node the CyNode that represents the group
	 */
	public void ungroupGroup(CyNode group_node);

}//GroupModel
