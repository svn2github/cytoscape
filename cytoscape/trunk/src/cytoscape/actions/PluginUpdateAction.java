/**
 * 
 */
package cytoscape.actions;

import java.awt.event.ActionEvent;

import cytoscape.Cytoscape;
import cytoscape.logger.CyLogger;

import cytoscape.task.ui.JTaskConfig;
import cytoscape.util.CytoscapeAction;

import cytoscape.dialogs.plugins.PluginUpdateDialog;

import cytoscape.plugin.DownloadableInfo;
import cytoscape.plugin.PluginInfo;
import cytoscape.plugin.PluginManager;
import cytoscape.plugin.ManagerException;
import cytoscape.plugin.PluginStatus;

import java.util.List;
import java.util.ArrayList;

import javax.swing.JOptionPane;

public class PluginUpdateAction extends CytoscapeAction {
	protected static CyLogger logger = CyLogger.getLogger(PluginUpdateAction.class);

	public PluginUpdateAction() {
		super("Update Plugins");
		setPreferredMenu("Plugins");

		if (PluginManager.usingWebstartManager()) {
			setEnabled(false);
		}
	}

	public void actionPerformed(ActionEvent e) {
		PluginUpdateDialog dialog = new PluginUpdateDialog(Cytoscape.getDesktop());
		dialog.setVisible(false);
		ArrayList<String> xmlIncorrect = new ArrayList<String>();
		
		if (!PluginManager.usingWebstartManager()) {
			boolean updateFound = false;
			PluginManager mgr = PluginManager.getPluginManager();
			// Find updates
			for (DownloadableInfo current : mgr.getDownloadables(PluginStatus.CURRENT)) {

				try {
					List<DownloadableInfo> updates = mgr.findUpdates(current);
					if (updates.size() > 0) {
						dialog.addCategory(current.getCategory(), current, updates);
						updateFound = true;
					}
				} catch (org.jdom.JDOMException jde) {
					logger.warn(jde.getMessage());
					xmlIncorrect.add(current.toString());
				} catch (java.io.IOException ioe) {
					logger.warn("Failed to read XML file for "
							+ current.getName() + " at "
							+ current.getDownloadableURL(), ioe);
					xmlIncorrect.add(current.toString());
				}

			}
			if (xmlIncorrect.size() > 0) {
				// show option pane warning message?
			}
			
			if (updateFound) {
				dialog.setVisible(true);
			} else {
				JOptionPane.showMessageDialog(Cytoscape.getDesktop(),
						"No updates available for currently installed plugins.",
						"Plugin Updates", JOptionPane.INFORMATION_MESSAGE);
			}
		} else {
			JOptionPane.showMessageDialog( Cytoscape.getDesktop(),
					"Plugin updates are not available when using Cytoscape through webstart",
					"Plugin Update", JOptionPane.INFORMATION_MESSAGE);
		}
	}

}
