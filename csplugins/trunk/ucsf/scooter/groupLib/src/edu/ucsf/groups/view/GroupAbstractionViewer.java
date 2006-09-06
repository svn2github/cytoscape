/* :vim set ts=2: */
/**
 * @author Scooter Morris scooter@cgl.ucsf.edu
 */

package edu.ucsf.groups.view;

import java.util.List;

import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.CyEdge;
import cytoscape.view.CyNetworkView;

/**
 * An interface for classes that handle the actual presentation of groups.
 */

public interface GroupAbstractionViewer {
	/**
	 * Returns the name of this viewer.  This will be saved as part of the group.
	 *
	 * @return String containing the viewer name.
	 */
	public String getGroupAbstractionViewerName();

	/**
	 * Show this group in a grouped state.
	 *
	 * @param networkView the CyNetworkView for displaying this group
	 * @param groupedNetwork the CyNetwork containing the group members
	 * @param group_node the CyNode representing the group
	 * @param members a List of CyNodes containing the members of the group
	 * @param internalEdges a List of CyEdges containing the internal edges for this group
	 * @param externalEdges a List of CyEdges containing the external edges for this group
	 */
	public void viewGrouped(CyNetworkView networkView, CyNetwork groupedNetwork,
													CyNode group_node, List members,
													List internalEdges, List externalEdges);

	/**
	 * Show this group in an ungrouped state.
	 *
	 * @param networkView the CyNetworkView for displaying this group
	 * @param groupedNetwork the CyNetwork containing the group members
	 * @param group_node the CyNode representing the group
	 * @param members a List of CyNodes containing the members of the group
	 * @param internalEdges a List of CyEdges containing the internal edges for this group
	 * @param externalEdges a List of CyEdges containing the external edges for this group
	 */
	public void viewUngrouped(CyNetworkView networkView, CyNetwork groupedNetwork,
														CyNode group_node, List members,
														List internalEdges, List externalEdges);
}
