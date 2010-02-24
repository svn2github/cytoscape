package org.cytoscape.data.reader.kgml;

import org.cytoscape.kegg.browser.KEGGNetworkListener;

import cytoscape.Cytoscape;
import cytoscape.data.ImportHandler;
import cytoscape.plugin.CytoscapePlugin;


public class KGMLReaderPlugin extends CytoscapePlugin {
	
	public KGMLReaderPlugin() {
		ImportHandler importHandler = new ImportHandler();
		importHandler.addFilter(new KGMLFilter());
		
		Cytoscape.getSwingPropertyChangeSupport().addPropertyChangeListener(
				new KEGGNetworkListener());
	}

}
