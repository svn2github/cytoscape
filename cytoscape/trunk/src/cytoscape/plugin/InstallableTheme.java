/**
 * 
 */
package cytoscape.plugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jdom.JDOMException;

import cytoscape.task.TaskMonitor;
import cytoscape.task.util.TaskManager;

/**
 * @author skillcoy
 *
 */
public class InstallableTheme implements Installable {

	private ThemeInfo infoObj;
	
	public InstallableTheme(ThemeInfo obj) {
		this.infoObj = obj;
	}
	
	/* (non-Javadoc)
	 * @see cytoscape.plugin.Installable#getInfoObj()
	 */
	public DownloadableInfo getInfoObj() {
		return this.infoObj;
	}

	/* (non-Javadoc)
	 * @see cytoscape.plugin.Installable#install()
	 */
	public boolean install() throws IOException, ManagerException {
		return installToDir(null, null);
	}

	public boolean installToDir(File dir) throws IOException, ManagerException {
		return installToDir(dir, null);
	}
	
	public boolean installToDir(File dir, TaskMonitor taskMonitor) throws IOException, ManagerException {
		File InstallDir = dir;
		if (InstallDir == null)
			InstallDir = getInstallDirectory();
		
		if (!InstallDir.exists())
			InstallDir.mkdirs();

		for (PluginInfo plugin: this.infoObj.getPlugins()) {
			InstallablePlugin pi = new InstallablePlugin(plugin);
			try {
				pi.installToDir(InstallDir, taskMonitor);
				this.infoObj.replacePlugin(plugin, pi.getInfoObj());
			} catch (Exception me) {
				// failed to install a plugin stop now and remove all of them
				// throw exception that theme failed to install due to bad plugin
				for (PluginInfo pInfo: infoObj.getPlugins()) {
					if (pInfo.equals(plugin))
						continue;
					InstallablePlugin ipDelete = new InstallablePlugin(pInfo);
					ipDelete.uninstall();
				}
				throw new ManagerException("Failed to install the theme '" + this.infoObj.toString() + "'", me);
			}
			
		}
		return true;
	}
	
	/* (non-Javadoc)
	 * @see cytoscape.plugin.Installable#install(cytoscape.task.TaskMonitor)
	 */
	public boolean install(TaskMonitor taskMonitor) throws IOException,
			ManagerException {
		return installToDir(null, taskMonitor);
// TODO if one of the plugins in a theme fails to install correctly all of the plugins should be removed

//		// eventually a separate directory containing all of the theme plugins/files would be nice but with
//		// the current method of loading them at start up it would be a pain to do
	
//		File InstallDir = getInstallDirectory();
//		if (!InstallDir.exists())
//			InstallDir.mkdirs();

//		for (PluginInfo plugin: this.infoObj.getPlugins()) {
//			InstallablePlugin pi = new InstallablePlugin(plugin);
//			try {
//				pi.install(taskMonitor);
//				this.infoObj.replacePlugin(plugin, pi.getInfoObj());
//			} catch (Exception me) {
//				// failed to install a plugin stop now and remove all of them
//				// throw exception that theme failed to install due to bad plugin
//				for (PluginInfo pInfo: infoObj.getPlugins()) {
//					if (pInfo.equals(plugin))
//						continue;
//					InstallablePlugin ipDelete = new InstallablePlugin(pInfo);
//					ipDelete.uninstall();
//				}
//				throw new ManagerException("Failed to install the theme '" + this.infoObj.toString() + "'", me);
//			}
//			
//		}
//		return true;
	}

	/* (non-Javadoc)
	 * @see cytoscape.plugin.Installable#uninstall()
	 */
	public boolean uninstall() throws cytoscape.plugin.ManagerException { 
		boolean deleteOk = true;
		for (PluginInfo plugin: this.infoObj.getPlugins()) {
			InstallablePlugin ins = new InstallablePlugin(plugin);
			if (!ins.uninstall())
				deleteOk = false;
		}
		return deleteOk;
	}

	private java.io.File getInstallDirectory() {
		 java.io.File Dir = new java.io.File(
				 PluginManager.getPluginManager().getPluginManageDirectory(),
				 this.infoObj.getName()+"-"+this.infoObj.getObjectVersion());
		return Dir;
	}

	/* (non-Javadoc)
	 * @see cytoscape.plugin.Installable#findUpdates()
	 */
	public List<DownloadableInfo> findUpdates() throws IOException, JDOMException {
		final List<DownloadableInfo> UpdatableThemes = new ArrayList<DownloadableInfo>();
		final Set<DownloadableInfo> Seen = new HashSet<DownloadableInfo>();

		Seen.add(this.infoObj);
		
		if (this.infoObj.getDownloadableURL() == null
				|| this.infoObj.getDownloadableURL().length() <= 0) {
			return UpdatableThemes;
		}
		
		final ThemeInfo InfoToUpdate = this.infoObj;
		final List<Exception> Exceptions = new ArrayList<Exception>();

		
		PluginManagerInquireTask task = new PluginManagerInquireTask(this.infoObj.getDownloadableURL(),
				new PluginInquireAction() {

					public String getProgressBarMessage() {
						return "Connecting to "
								+ InfoToUpdate.getDownloadableURL()
								+ " to search for updates...";
					}

					public void inquireAction(List<DownloadableInfo> Results) {

						if (isExceptionThrown()) {
							Exceptions.add(0, getIOException());
							Exceptions.add(1, getJDOMException());
						}
						
						for (DownloadableInfo New : Results) {
							DownloadableInfo temp = InfoToUpdate;
							if (!InfoToUpdate.getType().equals(New.getType()))
								continue;
							// ID or classname are unique
							boolean newer = InfoToUpdate.isNewerObjectVersion(New);
							if ( New.getID().equals(InfoToUpdate.getID()) && newer ) {
								if (!Seen.contains(New) && newer) {
									UpdatableThemes.add(New);
								} else {
									Seen.add(New);
								}
							}
						}
					}
				});

		// Execute Task in New Thread; pop open JTask Dialog Box.
		TaskManager.executeTask(task, null);

		if (Exceptions.size() > 0) {
			if (Exceptions.get(0) != null) {
				throw (java.io.IOException) Exceptions.get(0);
			}
			if (Exceptions.size() > 1 && Exceptions.get(1) != null) {
				throw (org.jdom.JDOMException) Exceptions.get(1);
			}
		}

		return UpdatableThemes;
	}

	/* (non-Javadoc)
	 * @see cytoscape.plugin.Installable#update(cytoscape.plugin.DownloadableInfo, cytoscape.task.TaskMonitor)
	 */
	public boolean update(DownloadableInfo newInfoObj, TaskMonitor taskMonitor) throws IOException, ManagerException {
		
		ThemeInfo newObj = (ThemeInfo) newInfoObj;
		
		if (infoObj.getDownloadableURL() == null) {
			throw new ManagerException(
					infoObj.getName()
							+ " does not have a project url.\nCannot auto-update this plugin.");
		}
		// ID or classname
		if ( infoObj.getID().equals(newObj.getID()) 
				&& infoObj.getDownloadableURL().equals(newObj.getDownloadableURL())
				&& infoObj.isNewerObjectVersion(newObj)) {

			this.infoObj = newObj;
			this.install(taskMonitor);
			
		} else {
			throw new ManagerException(
					"Failed to update '"
							+ infoObj.getName()
							+ "', the new plugin did not match what is currently installed\n"
							+ "or the version was not newer than what is currently installed.");
		}

		return true;
	}

	/* (non-Javadoc)
	 * @see cytoscape.plugin.Installable#update(cytoscape.plugin.DownloadableInfo)
	 */
	public boolean update(DownloadableInfo newInfoObj) throws IOException, ManagerException {
		return update(newInfoObj, null);
	}


}
