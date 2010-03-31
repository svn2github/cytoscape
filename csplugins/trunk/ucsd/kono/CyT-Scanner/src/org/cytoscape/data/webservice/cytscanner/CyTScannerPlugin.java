package org.cytoscape.data.webservice.cytscanner;

import twitter4j.TwitterFactory;
import cytoscape.plugin.CytoscapePlugin;

public class CyTScannerPlugin extends CytoscapePlugin {
	
	private final TwitterFactory factory;
	
	public CyTScannerPlugin() {
		factory = new TwitterFactory();
	}
	
	public TwitterFactory getFactory() {
		return factory;
	}
}
