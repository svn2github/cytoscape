package org.cytoscape.model;

import java.lang.String;
import java.util.List;

/**
 * CyProject is the top level of the Cytoscape model hierarchy.  A CyProject
 * maintains a list of all of the networks associated with this project,
 * the information about how each project was created, and a list of logs
 * and other information related to data provenance.  Roughly, a CyProject
 * is equivilent to a Cytoscape session.
 */
public interface CyProject {

	/**
	 * Return the name of this project.
	 *
	 * @return project name
	 */
	public String getProjectName();

	/**
	 * Return the description for this project.
	 *
	 * @return project description
	 */
	public String getProjectDescription();

	/**
	 * Set the description for this project.
	 *
	 * @param description the description for this project
	 */
	public void setProjectDescription(String description);

	/**
	 * Set the path for this project
	 *
	 * @param path the path to load this project
	 */
	public void setProjectPath(String path);

	/**
	 * Returns true if the project is active
	 *
	 * @return true if active, false otherwise
	 */
	public boolean isActive();

	/**
	 * The following routines all return null if the project
	 * is not active
	 */

	/**
	 * Return the list of networks associated with this project.
	 *
	 * @return the list of CyNetworks associated with this project
	 */
	public List<CyNetwork> getNetworks();

	/**
	 * Get the network named "networkName".
	 *
	 * @param networkName the name of the network we're looking for
	 * @return the network named <b>networkName</b> or <b>null</b> if such
	 * a network doesn't exist.
	 */
	public CyNetwork getNetwork(String networkName);

	/**
	 * Add the network named "networkName".
	 *
	 * @param network the name of the network we're looking for
	 */
	public void addNetwork(CyNetwork network);

	/**
	 * Remove (delete) a network from the project.
	 *
	 * @param network the network to remove from the project
	 */
	public void removeNetwork(CyNetwork network);

	/**
	 * Add a new file to the project.  Generally, files are associated
	 * with an imported network, some kind of annotation file, or some other
	 * imported data set. 
	 *
	 * @param filePath the path to the filename
	 * @param description a description of the file contents
	 */
	public void addFile(String filePath, String description);

	/**
	 * Add a comment of some kind to the project.  Comments are either
	 * user descriptions of actions taken, or user entered text to describe
	 * actions or results.
	 *
	 * @param network the network the comment is associated with
	 * @param comment the entered text
	 */
	public void addComment(CyNetwork network, String comment);

	/**
	 * Get the list of comments associated with this network.
	 *
	 * @param network the network to get the comments for
	 * @return the list of comments for this network
	 */
	public List<String> getComments(CyNetwork network);

	/**
	 * Add a description of a manipulation to the network.  These are
	 * meant to be automatically generated descriptions of user-initiated
	 * manipulations.
	 *
	 * @param network the network the comment is associated with
	 * @param whatChanged the text that describes what changed
	 */
	public void addManipulation(CyNetwork network, String whatChanged);

	/**
	 * Get the list of manipulations associated with this network.
	 *
	 * @param network the network to get the comments for
	 * @return the list of manipulations for this network
	 */
	public List<String> getManipulations(CyNetwork network);

}
