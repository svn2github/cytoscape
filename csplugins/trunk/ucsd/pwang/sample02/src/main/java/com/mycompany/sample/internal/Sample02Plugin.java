
package com.mycompany.sample.internal;

import org.cytoscape.plugin.CyPluginAdapter;
import com.mycompany.sample.internal.action.Sample02Action;
import org.cytoscape.plugin.CyPlugin;
import org.cytoscape.application.swing.CySwingApplication;

/**
 * An implementation of CyPluginAdapter
 */
public class Sample02Plugin extends CyPlugin {
	
	
	public Sample02Plugin(CyPluginAdapter a){
		super(a);
		
		a.getCySwingApplication().addAction(new Sample02Action(a));
		
	}
}
