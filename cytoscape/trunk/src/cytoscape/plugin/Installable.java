/**
 * 
 */
package cytoscape.plugin;



/**
 * @author skillcoy
 * 
 */
public interface Installable {

	public DownloadableInfo getInfoObj();
	
	public boolean install() throws java.io.IOException, cytoscape.plugin.ManagerException;
	
	public boolean install(cytoscape.task.TaskMonitor taskMonitor) 
		throws java.io.IOException, cytoscape.plugin.ManagerException;
	
	public boolean uninstall() throws cytoscape.plugin.ManagerException;
	
	public boolean update(cytoscape.plugin.DownloadableInfo newObj) 
		throws java.io.IOException, cytoscape.plugin.ManagerException;
	
	public boolean update(cytoscape.plugin.DownloadableInfo newObj, cytoscape.task.TaskMonitor taskMonitor) 
		throws java.io.IOException, cytoscape.plugin.ManagerException;

	public java.util.List<cytoscape.plugin.DownloadableInfo> findUpdates()
		throws java.io.IOException, org.jdom.JDOMException;


}
