package org.cytoscape.plugin.example;


import org.cytoscape.plugin.CyPluginAdapter;
import org.cytoscape.plugin.CyPlugin;


public class SamplePlugin extends CyPlugin {
	public SamplePlugin(CyPluginAdapter a){
		super(a);
		System.err.println("Hello plugin world!");
//		a.getCySwingApplication().addAction(new Sample02aAction(a));
	}
}
