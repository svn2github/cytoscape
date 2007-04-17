/**
 * 
 */
package cytoscape.actions;

import java.awt.event.ActionEvent;

import cytoscape.Cytoscape;

import cytoscape.util.CytoscapeAction;

import cytoscape.dialogs.plugins.PluginUpdateDialog;

import cytoscape.plugin.PluginInfo;
import cytoscape.plugin.PluginManager;
import cytoscape.plugin.ManagerError;
// import cytoscape.plugin.ManagerUtil;
import cytoscape.plugin.PluginTracker.PluginStatus;

// import java.util.Map;
import java.util.List;

import javax.swing.JOptionPane;

public class PluginUpdateAction extends CytoscapeAction {
	public PluginUpdateAction() {
		super("Update Plugins");
		setPreferredMenu("Plugins");
	}

	public void actionPerformed(ActionEvent e) {
		PluginUpdateDialog Dialog = new PluginUpdateDialog(Cytoscape
				.getDesktop());

		try {
			boolean updateFound = false;
			PluginManager Mgr = PluginManager.getPluginManager();
			// Find updates
			for (PluginInfo Current : Mgr.getPlugins(PluginStatus.CURRENT)) {
				List<PluginInfo> Updates = Mgr.findUpdates(Current);
				if (Updates.size() > 0) {
					Dialog.addCategory(Current.getCategory(), Current, Updates);
					updateFound = true;
				}
			}
			if (updateFound) {
				Dialog.setVisible(true);
			} else {
				JOptionPane.showMessageDialog(Cytoscape.getDesktop(),
						"No updates avaialbe for currently installed plugins.",
						"Plugin Updates", JOptionPane.INFORMATION_MESSAGE);
			}
		} catch (ManagerError E) {
			JOptionPane.showMessageDialog(Cytoscape.getDesktop(), E
					.getMessage(), "Plugin Update Error",
					JOptionPane.ERROR_MESSAGE);
			E.printStackTrace();
		}
	}

}
