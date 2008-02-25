/**
 * 
 */
package org.cytoscape.application.plugin;



/**
 * @author skillcoy
 * 
 */
public interface Installable {

	public DownloadableInfo getInfoObj();
	
	public boolean install() throws java.io.IOException, org.cytoscape.application.plugin.ManagerException;
	
	public boolean install(cytoscape.task.TaskMonitor taskMonitor) 
		throws java.io.IOException, org.cytoscape.application.plugin.ManagerException;

	public boolean installToDir(java.io.File dir) throws java.io.IOException, org.cytoscape.application.plugin.ManagerException;

	public boolean installToDir(java.io.File dir, cytoscape.task.TaskMonitor taskMonitor) 
		throws java.io.IOException, org.cytoscape.application.plugin.ManagerException;
	
	public boolean uninstall() throws org.cytoscape.application.plugin.ManagerException;
	
	public boolean update(org.cytoscape.application.plugin.DownloadableInfo newObj) 
		throws java.io.IOException, org.cytoscape.application.plugin.ManagerException;
	
	public boolean update(org.cytoscape.application.plugin.DownloadableInfo newObj, cytoscape.task.TaskMonitor taskMonitor) 
		throws java.io.IOException, org.cytoscape.application.plugin.ManagerException;

	public java.util.List<org.cytoscape.application.plugin.DownloadableInfo> findUpdates()
		throws java.io.IOException, org.jdom.JDOMException;


}
