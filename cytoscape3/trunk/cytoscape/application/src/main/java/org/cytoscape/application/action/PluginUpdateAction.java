/**
 * 
 */
package org.cytoscape.application.action;

import java.awt.event.ActionEvent;


import cytoscape.task.ui.JTaskConfig;



import java.util.List;

import javax.swing.JOptionPane;

import org.cytoscape.application.dialog.plugin.PluginUpdateDialog;
import org.cytoscape.application.plugin.DownloadableInfo;
import org.cytoscape.application.plugin.ManagerException;
import org.cytoscape.application.plugin.PluginInfo;
import org.cytoscape.application.plugin.PluginManager;
import org.cytoscape.application.plugin.PluginStatus;
import org.cytoscape.application.util.Cytoscape;


public class PluginUpdateAction extends CytoscapeAction {
	public PluginUpdateAction() {
		super("Update Plugins");
		setPreferredMenu("Plugins");

		if (PluginManager.usingWebstartManager()) {
			setEnabled(false);
		}
	}

	public void actionPerformed(ActionEvent e) {
		PluginUpdateDialog Dialog = new PluginUpdateDialog(Cytoscape
				.getDesktop());

		if (!PluginManager.usingWebstartManager()) {
			boolean updateFound = false;
			PluginManager Mgr = PluginManager.getPluginManager();
			// Find updates
			for (DownloadableInfo Current : Mgr.getDownloadables(PluginStatus.CURRENT)) {

			// Configure JTask Dialog Pop-Up Box
//			JTaskConfig jTaskConfig = new JTaskConfig();
//			jTaskConfig.setOwner(Cytoscape.getDesktop());
//			jTaskConfig.displayCloseButton(false);
//			jTaskConfig.displayStatus(true);
//			jTaskConfig.setAutoDispose(true);
//			jTaskConfig.displayCancelButton(false);

				
				try {
					List<DownloadableInfo> Updates = Mgr.findUpdates(Current);
					if (Updates.size() > 0) {
						Dialog.addCategory(Current.getCategory(), Current,
								Updates);
						updateFound = true;
					}
				} catch (org.jdom.JDOMException jde) {
					System.err.println("Failed to retrieve updates for "
							+ Current.getName() + ", XML incorrect at "
							+ Current.getDownloadableURL());
					System.err.println(jde.getMessage());
					// jde.printStackTrace();
				} catch (java.io.IOException ioe) {
					System.err.println("Failed to read XML file for "
							+ Current.getName() + " at "
							+ Current.getDownloadableURL());
					ioe.printStackTrace();
				}

			}
			if (updateFound) {
				Dialog.setVisible(true);
			} else {
				JOptionPane.showMessageDialog(Cytoscape.getDesktop(),
						"No updates available for currently installed plugins.",
						"Plugin Updates", JOptionPane.INFORMATION_MESSAGE);
			}
		} else {
			JOptionPane
					.showMessageDialog(
							Cytoscape.getDesktop(),
							"Plugin updates are not available when using Cytoscape through webstart",
							"Plugin Update", JOptionPane.INFORMATION_MESSAGE);
		}
	}

}
