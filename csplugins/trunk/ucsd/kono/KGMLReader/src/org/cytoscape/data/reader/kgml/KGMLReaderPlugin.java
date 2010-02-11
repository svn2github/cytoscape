package org.cytoscape.data.reader.kgml;

import cytoscape.data.ImportHandler;
import cytoscape.plugin.CytoscapePlugin;

public class KGMLReaderPlugin extends CytoscapePlugin {
	
	public KGMLReaderPlugin() {
		ImportHandler importHandler = new ImportHandler();
		importHandler.addFilter(new KGMLFilter());
	}

}
