package cytoscape.data.webservice.util;

import cytoscape.Cytoscape;
import cytoscape.data.webservice.ui.UnifiedNetworkImportDialog;
import cytoscape.plugin.PluginManager;
import cytoscape.plugin.DownloadableInfo;
import cytoscape.plugin.ManagerUtil;
import cytoscape.plugin.Category;

import cytoscape.task.TaskMonitor;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.util.TaskManager;

import javax.swing.*;
import java.util.*;

public class WebServiceThemeInstall {

	public final static String WEBSERVICE_THEME = "WebServiceClientPack";
	
	private PluginManager mgr;
    private UnifiedNetworkImportDialog unifiedNetworkImportDialog;

    public WebServiceThemeInstall(UnifiedNetworkImportDialog dialog) {
		super();
        unifiedNetworkImportDialog = dialog;
        mgr = PluginManager.getPluginManager();
	}
	
	// for test purposes so a manager pointing at the tmp directory can be used
	protected WebServiceThemeInstall(PluginManager manager) {
		super();
		mgr = manager;
	}
	
	public DownloadableInfo installTheme() throws org.jdom.JDOMException, java.io.IOException {
		 Map<String, List<DownloadableInfo>> DownloadableByCategory = 
			 ManagerUtil.sortByCategory( mgr.inquire(cytoscape.CytoscapeInit.getProperties().getProperty("defaultPluginDownloadUrl")) );
		
		 DownloadableInfo WSTheme = null;
		 for (DownloadableInfo Theme:  DownloadableByCategory.get( Category.THEME.toString() )) {
			 if (Theme.getName().equals(WEBSERVICE_THEME) && Theme.isCytoscapeVersionCurrent() ) {
				 if (WSTheme == null)
					 WSTheme = Theme;
				 
				 if (Theme.isNewerObjectVersion(WSTheme))
					 WSTheme = Theme;
			 }
		 }
		 return this.runInstallTask(WSTheme);
	}
	
	
	private DownloadableInfo runInstallTask(DownloadableInfo obj) {
		// Create Task
		InstallTask task = new InstallTask(obj);

		// Configure JTask Dialog Pop-Up Box
		JTaskConfig jTaskConfig = new JTaskConfig();
		jTaskConfig.setOwner(Cytoscape.getDesktop());
		jTaskConfig.displayCloseButton(false);
		jTaskConfig.displayStatus(true);
		jTaskConfig.setAutoDispose(true);
		jTaskConfig.displayCancelButton(true);
		// Execute Task in New Thread; pop open JTask Dialog Box.
		TaskManager.executeTask(task, jTaskConfig);
		DownloadableInfo info = task.getDownloadedPlugin();
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                unifiedNetworkImportDialog.resetGUI();
                JOptionPane.showMessageDialog(Cytoscape.getDesktop(),
                                "Web Services Pack Successfully Installed.",
                                "Installation Successfull", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        return info;
	}

	private class InstallTask implements cytoscape.task.Task {
		private cytoscape.task.TaskMonitor taskMonitor;
		private DownloadableInfo infoObj;
		private String errorMsg;
		
		public InstallTask(DownloadableInfo Info)
				throws java.lang.IllegalArgumentException {
			String ErrorMsg = null;
			if (Info == null) {
				ErrorMsg = "DownloadableInfo object cannot be null\n";
				throw new java.lang.IllegalArgumentException(ErrorMsg);
			}
			infoObj = Info;
		}

		public void run() {
			if (taskMonitor == null) {
				throw new IllegalStateException("Task Monitor is not set.");
			}
			taskMonitor.setStatus("Installing " + infoObj.getName() + " v"
					+ infoObj.getObjectVersion());
			taskMonitor.setPercentCompleted(-1);

			PluginManager Mgr = PluginManager.getPluginManager();
			try {
				infoObj = Mgr.download(infoObj, taskMonitor);
				taskMonitor.setStatus(infoObj.getName() + " v"
						+ infoObj.getObjectVersion() + " complete.");

				taskMonitor.setStatus(infoObj.getName() + " v"
						+ infoObj.getObjectVersion() + " loading...");

				Mgr.install(infoObj);
				Mgr.loadPlugin(infoObj);
			} catch (java.io.IOException ioe) {
				taskMonitor
						.setException(ioe, "Failed to download "
								+ infoObj.getName() + " from "
								+ infoObj.getObjectUrl());
				infoObj = null;
				ioe.printStackTrace();
			} catch (cytoscape.plugin.ManagerException me) {
				this.setErrorMessage("Failed to install " + infoObj.toString());
				taskMonitor.setException(me, me.getMessage());
				infoObj = null;
				me.printStackTrace();
			} catch (cytoscape.plugin.PluginException pe) {
				this.setErrorMessage("Failed to install " + infoObj.toString());
				infoObj = null;
				taskMonitor.setException(pe, pe.getMessage());
				pe.printStackTrace();
			} catch (ClassNotFoundException cne) {
				taskMonitor.setException(cne, cne.getMessage());
				this.setErrorMessage("Failed to install " + infoObj.toString());
				infoObj = null;
				cne.printStackTrace();
			} finally {
				taskMonitor.setPercentCompleted(100);
			}
		}

		private void setErrorMessage(String em) {
			errorMsg = em;
		}
		
		public String getErrorMessage() {
			return errorMsg;
		}
		
		public DownloadableInfo getDownloadedPlugin() {
			return infoObj;
		}

		public void halt() {
			// not haltable
		}

		public void setTaskMonitor(TaskMonitor monitor)
				throws IllegalThreadStateException {
			this.taskMonitor = monitor;
		}

		public String getTitle() {
			return "Installing Cytoscape Theme: '" + infoObj.getName() + "'";		}

	}

	
	
	
}
