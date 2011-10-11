package cytoscape.cytable;

import cytoscape.Cytoscape;
import cytoscape.plugin.CytoscapePlugin;


public class CyTableReaderPlugin extends CytoscapePlugin {

	public CyTableReaderPlugin() {
		Cytoscape.getDesktop().getCyMenus().addAction( new CyTableReaderAction("Node"));
		Cytoscape.getDesktop().getCyMenus().addAction( new CyTableReaderAction("Edge"));
	}
}	


