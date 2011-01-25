
package com.mycompany.sample.internal;

import org.cytoscape.plugin.CyPluginAdapter;
import com.mycompany.sample.internal.action.Sample02Action;
import org.cytoscape.plugin.CyPlugin;

/**
 * An implementation of CyPluginAdapter
 */
public class Sample02Plugin extends CyPlugin {
	
	
	public Sample02Plugin(CyPluginAdapter a){
		super(a);
		
		new Sample02Action(a);
		
	}
}
