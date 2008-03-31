package clusterMaker.algorithms.FORCE;

import cytoscape.Cytoscape;
import cytoscape.plugin.CytoscapePlugin;


public class CytoscapeFORCEplugin extends CytoscapePlugin {
	
	public CytoscapeFORCEplugin() {
		
		System.out.println("COMA plugin started.");
		
		Cytoscape.getDesktop().getCyMenus().getOperationsMenu().add(new CytoscapeFORCEmenu());
		
	}

}
