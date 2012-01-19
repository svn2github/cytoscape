package cytoscape.nntk;

import cytoscape.Cytoscape;
import cytoscape.plugin.CytoscapePlugin;


public class NestedNetworkToolkitPlugin extends CytoscapePlugin {

	public NestedNetworkToolkitPlugin() {
		super();
		Cytoscape.getPropertyChangeSupport().addPropertyChangeListener( new NNTKNetworkListener() );
	}
}	
