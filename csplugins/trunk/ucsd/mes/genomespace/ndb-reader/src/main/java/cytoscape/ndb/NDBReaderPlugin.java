package cytoscape.ndb;

import cytoscape.Cytoscape;
import cytoscape.data.ImportHandler;
import cytoscape.plugin.CytoscapePlugin;


/**
 * This class is used to instantiate your plugin. Put whatever initialization code
 * you need into the no argument constructor (the only one that will be called).
 * The actual functionality of your plugin can be in this class, but should 
 * probably be separated into separted classes that get instantiated here.
 */
public class NDBReaderPlugin extends CytoscapePlugin {

	public NDBReaderPlugin() {
		super();
		ImportHandler ih = Cytoscape.getImportHandler();
		ih.addFilter(new NDBFilter());
	}
}	


