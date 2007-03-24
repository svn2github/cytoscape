/**
 *
 */
package cytoscape.actions;

import cytoscape.Cytoscape;

import cytoscape.dialogs.PluginManageDialog;
import cytoscape.dialogs.PluginManageDialog.PluginStatus;

import cytoscape.plugin.PluginInfo;
import cytoscape.plugin.PluginManager;
import cytoscape.plugin.ManagerError;

import cytoscape.util.CytoscapeAction;

import java.util.List;
import java.util.Map;
import java.awt.event.ActionEvent;

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
		PluginManageDialog dialog = new PluginManageDialog(Cytoscape.getDesktop());
		PluginManager Mgr = PluginManager.getPluginManager();

		Map<String, List<PluginInfo>> InstalledInfo = 
			Mgr.getPluginsByCategory(Mgr.getInstalledPlugins());
		try {
			Map<String, List<PluginInfo>> DownloadInfo = Mgr.getPluginsByCategory(Mgr.inquire());
	
			for (String Category : DownloadInfo.keySet()) {
				dialog.addCategory(Category, DownloadInfo.get(Category), PluginStatus.AVAILABLE);
			}
			dialog.setSiteName("Cytoscape");
		} catch (ManagerError E) {
				JOptionPane.showMessageDialog(Cytoscape.getDesktop(), 
						E.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				E.printStackTrace();
		} 
		
		for (String Category : InstalledInfo.keySet()) {
			dialog.addCategory(Category, InstalledInfo.get(Category), PluginStatus.INSTALLED);
		}
		dialog.setVisible(true);
	}
}
