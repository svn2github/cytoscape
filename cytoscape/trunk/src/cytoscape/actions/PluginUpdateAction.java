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
import cytoscape.plugin.ManagerException;
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

		if (!PluginManager.usingWebstartManager()) {
//			try {
				boolean updateFound = false;
				PluginManager Mgr = PluginManager.getPluginManager();
				// Find updates
				for (PluginInfo Current : Mgr.getPlugins(PluginStatus.CURRENT)) {
					
					try {
						List<PluginInfo> Updates = Mgr.findUpdates(Current);
						if (Updates.size() > 0) {
							Dialog.addCategory(Current.getCategory(), Current,
									Updates);
							updateFound = true;
						}
					} catch (org.jdom.JDOMException jde) {
						System.err.println("Failed to retrieve updates for " + Current.getName() + ", XML incorrect at " + Current.getProjectUrl());
						System.err.println(jde.getMessage());
						//jde.printStackTrace();
					} catch (java.io.IOException ioe) {
						System.err.println("Failed to read XML file for " + Current.getName() + " at " + Current.getProjectUrl());
						ioe.printStackTrace();
					}
					
				}
				if (updateFound) {
					Dialog.setVisible(true);
				} else {
					JOptionPane
							.showMessageDialog(
									Cytoscape.getDesktop(),
									"No updates avaialbe for currently installed plugins.",
									"Plugin Updates",
									JOptionPane.INFORMATION_MESSAGE);
				}
//			} catch (org.jdom.JDOMException jde) {
//				JOptionPane.showMessageDialog(Cytoscape.getDesktop(), "XML file incorrectly formatted", "Error", JOptionPane.ERROR_MESSAGE);
//				jde.printStackTrace();
//			} catch (java.io.IOException ioe) {
//				JOptionPane.showMessageDialog(Cytoscape.getDesktop(), "Error reading XML file", "Error", JOptionPane.ERROR_MESSAGE);
//				ioe.printStackTrace();
//			}
		} else {
			JOptionPane
					.showMessageDialog(
							Cytoscape.getDesktop(),
							"Plugin updates are not available when using Cytoscape through webstart",
							"Plugin Update", JOptionPane.INFORMATION_MESSAGE);
		}
	}

}
