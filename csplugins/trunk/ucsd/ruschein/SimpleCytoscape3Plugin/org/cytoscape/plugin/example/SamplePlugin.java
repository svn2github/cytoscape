package org.cytoscape.plugin.example;


import org.cytoscape.plugin.CyPluginAdapter;
import org.cytoscape.plugin.CyPlugin;


public class SamplePlugin extends CyPlugin {
	public SamplePlugin(CyPluginAdapter a){
		super(a);
		a.getCySwingApplication().addAction(new MenuAction(a));
	}
}
