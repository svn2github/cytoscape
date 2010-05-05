package cytoscape.tutorial23;

import java.awt.event.ActionEvent;
import javax.swing.JOptionPane;

import cytoscape.plugin.CytoscapePlugin;
import cytoscape.Cytoscape;

import cytoscape.data.eqn_attribs.AttribFunction;
import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.util.TaskManager;
import cytoscape.util.CytoscapeAction;


public class Tutorial23 extends CytoscapePlugin {
	public Tutorial23() {
		// Create an Action, add the action to Cytoscape menu
		MyPluginAction action = new MyPluginAction(this);
		Cytoscape.getDesktop().getCyMenus().addCytoscapeAction((CytoscapeAction) action);
	}
	
	public class MyPluginAction extends CytoscapeAction {
		public MyPluginAction(final Tutorial23 myPlugin) {
			// Add the menu item under menu pulldown "Plugins"
			super("Tutorial23 - Attribute Function Example");
			setPreferredMenu("Plugins");
		}
		
		public void actionPerformed(ActionEvent e) {
			JOptionPane.showMessageDialog(null, "The integer-XOR attribute function IXOR(i1,i2) has been registered!",
			                              "Plugin Result", JOptionPane.INFORMATION_MESSAGE);
		}
	}	
}
