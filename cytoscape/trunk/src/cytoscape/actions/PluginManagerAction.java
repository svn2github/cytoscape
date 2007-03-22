/**
 *
 */
package cytoscape.actions;

import cytoscape.Cytoscape;
import cytoscape.CytoscapeInit;

import cytoscape.dialogs.*;

import cytoscape.dialogs.PluginManageDialog.PluginStatus;

import cytoscape.plugin.PluginInfo;
import cytoscape.plugin.PluginManager;

import cytoscape.util.CytoscapeAction;

import java.awt.event.ActionEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;


/**
 * @author skillcoy
 *
 */
public class PluginManagerAction extends CytoscapeAction {
	/**
	 *
	 */
	public PluginManagerAction() {
		super("Manage Plugins");
		setPreferredMenu("Plugins");
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param e DOCUMENT ME!
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		/*
		 * This will actually pop up the "currently installed" dialog box which will
		 * have a button to "install plugins" poping up the PluginInstallDialog
		 */
		PluginManager Mgr = PluginManager.getPluginManager();

		Map<String, List<PluginInfo>> InstalledInfo = Mgr.getPluginsByCategory(Mgr
		                                                .getInstalledPlugins());
		Map<String, List<PluginInfo>> DownloadInfo = Mgr.getPluginsByCategory(Mgr.inquire());

		PluginManageDialog dialog = new PluginManageDialog(Cytoscape.getDesktop());
		dialog.setSiteName("Cytoscape");

		for (String Category : InstalledInfo.keySet()) {
			dialog.addCategory(Category, InstalledInfo.get(Category), PluginStatus.INSTALLED);
		}

		for (String Category : DownloadInfo.keySet()) {
			dialog.addCategory(Category, DownloadInfo.get(Category), PluginStatus.AVAILABLE);
		}

		dialog.setVisible(true);
	}
}
