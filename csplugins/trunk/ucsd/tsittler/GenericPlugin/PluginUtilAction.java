package GenericPlugin;

import java.lang.reflect.Array;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

import java.io.*;
import java.util.*;//HashMap,Vector,Iterator
import cytoscape.CyNode;
import cytoscape.CyEdge;
import cytoscape.Cytoscape;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.data.GraphObjAttributes;
import cytoscape.CyNetwork;
import cytoscape.data.CyNetworkFactory;
import cytoscape.data.readers.GMLReader;
import cytoscape.view.CyNetworkView;
import cytoscape.util.GinyFactory;
import cytoscape.actions.FitContentAction;
import cytoscape.data.Semantics;

/**
 * This class gets attached to the menu item.
 */
class PluginUtilAction extends AbstractAction {
    
    /**
     * The constructor sets the text that should appear on the menu item.
     */
    public ArgVector args;
    public String name;
    PluginUtil plugin;
    
    public PluginUtilAction(String initname,ArgVector initargs,PluginUtil initplugin){
	super(initname);
	name=initname;args=initargs;plugin=initplugin;
    }
    
    public void actionPerformed(ActionEvent ae) {
	//inform listeners that we're doing an operation on the network??
	if (plugin.getUIArgs(name,args)){
	    Thread t = new PluginUtilThread(name,args,plugin); 
	    t.start();
	}
    }
}
