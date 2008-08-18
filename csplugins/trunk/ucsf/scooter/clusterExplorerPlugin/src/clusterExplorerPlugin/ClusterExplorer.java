package clusterExplorerPlugin;

import cytoscape.Cytoscape;
import cytoscape.plugin.CytoscapePlugin;


public class ClusterExplorer extends CytoscapePlugin {
	
	public ClusterExplorer() {
		
		// System.out.println("COMA plugin started.");
		
		Cytoscape.getDesktop().getCyMenus().getOperationsMenu().add(new ClusterExplorerMenu());
		
	}
	
}
