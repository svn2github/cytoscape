/**
 *
 */
package cytoscape.plugin;

import cytoscape.*;

import cytoscape.plugin.util.*;

import cytoscape.util.FileUtil;

import java.io.*;

import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import java.util.*;
import java.util.jar.JarFile;
import java.util.jar.Manifest;


/**
 * @author skillcoy
 *
 */
public class PluginManager {
	private static PluginManager pluginMgr = null;
	private PluginTracker pluginTracker;
	private static File tempDir;
	private ClassLoader classLoader;
	private String defaultUrl;
	private String cyVersion;
	private String DEFAULT_VALUE = "http://db.systemsbiology.net/cytoscape/skillcoyne/plugins.xml";

	static {
		new PluginManager();
	}

	/**
	* DOCUMENT ME!
	*
	* @return DOCUMENT ME!
	*/
	public static PluginManager getPluginManager() {
		if (pluginMgr == null) {
			pluginMgr = new PluginManager();
		}

		return pluginMgr;
	}

	private PluginManager() {
		defaultUrl = DEFAULT_VALUE;

		// defaultUrl =
		// CytoscapeInit.getProperties().getProperty("defaultPluginUrl",
		// DEFAULT_VALUE);
		try {
			pluginTracker = new PluginTracker();
			cyVersion = CytoscapeVersion.version;
			tempDir = new File(CytoscapeInit.getConfigDirectory(), "plugins");

			if (!tempDir.exists()) {
				tempDir.mkdir();
			}
		} catch (java.io.IOException E) {
			E.printStackTrace(); // TODO do something useful with error
		}
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public File getTempDownloadDirecotry() {
		return tempDir;
	}

	/**
	 * Get a list of plugins by status.
	 * CURRENT: currently installed
	 * INSTALL: plugins to be installed
	 * DELETE: plugins to be deleted
	 * @param Status
	 * @return
	 */
	public List<PluginInfo> getPlugins(PluginTracker.PluginStatus Status) {
		return pluginTracker.getListByStatus(Status);
	}

	/**
	* @return List of PluginInfo objects from the default url
	*/
	public List<PluginInfo> inquire() throws ManagerError {
		return inquire(defaultUrl);
	}

	/**
	* Calls the given url, expects document describing plugins available for
	* download
	*
	* @param Url
	* @return List of PluginInfo objects
	*/
	public List<PluginInfo> inquire(String Url) throws ManagerError {
		List<PluginInfo> Plugins = null;

		try {
			PluginFileReader Reader = new PluginFileReader(Url);
			Plugins = Reader.getPlugins();
		} catch (java.io.IOException E) {
			throw new ManagerError("Failed to read xml file at " + Url, E);
		} catch (org.jdom.JDOMException E) {
			throw new ManagerError(Url + " did not return correctly formatted xml", E);
		}

		return Plugins;
	}

	/**
	* Registers a currently installed plugin with tracking object. Only useful if
	* the plugin was not installed via the install process.
	*
	* @param Plugin
	* @param JarFileName
	*/
	public void register(CytoscapePlugin Plugin, String JarFileName) {
		PluginInfo InfoObj;

		if (Plugin.getPluginInfoObject() == null) {
			InfoObj = new PluginInfo();
			InfoObj.setName(Plugin.getClass().getName());
			InfoObj.setPluginClassName(Plugin.getClass().getName());
			// I think we can safely assume it's a jar file if it's registering with no info object
			InfoObj.setFiletype(PluginInfo.FileType.JAR);

			if (JarFileName != null)
				InfoObj.addFileName(JarFileName);
		} else {
			InfoObj = Plugin.getPluginInfoObject();
			InfoObj.setPluginClassName(Plugin.getClass().getName());

			if (JarFileName != null)
				InfoObj.addFileName(JarFileName);
		}

		pluginTracker.addPlugin(InfoObj, PluginTracker.PluginStatus.CURRENT);
	}

	/**
	* Takes all objects on the "to-install" list and installs them from them
	* temporary download directory.
	*/
	public void install() throws ManagerError {
		List<PluginInfo> Plugins = pluginTracker.getListByStatus(PluginTracker.PluginStatus.INSTALL);

		for (PluginInfo CurrentPlugin : Plugins) {
			List<String> FileList = CurrentPlugin.getFileList();

			// TESTING
			if (FileList.size() > 1) {
				throw new ManagerError("Unexpected files in file list for plugin "
				                       + CurrentPlugin.getName());
			}

			try {
				switch (CurrentPlugin.getFileType()) {
					case JAR:

						String InstallFileName = "plugins" + System.getProperty("file.separator")
						                         + createFileName(CurrentPlugin);
						FileInputStream fis = new FileInputStream(FileList.get(0));
						FileOutputStream fos = new FileOutputStream(InstallFileName);

						byte[] buffer = new byte[1];

						while (((fis.read(buffer)) != -1)) {
							fos.write(buffer);
						}

						fis.close();
						fos.close();

						break;

					case ZIP:

						InputStream is = HttpUtils.getInputStream(FileList.get(0));

						if (UnzipUtil.zipContains(is,
						                          "plugins" + System.getProperty("file.separator")
						                          + "\\w+\\.jar")) {
							UnzipUtil.unzip(is);
						} else {
							throw new ManagerError("Zip file " + CurrentPlugin.getUrl()
							                       + " did not contain a plugin directory with a jar file.\nThis plugin will need to be installed manually.");
						}

						is.close();

						break;
				}

				;
			} catch (IOException E) {
				throw new ManagerError("Failed to install file " + FileList.get(0), E);
			}

			pluginTracker.removePlugin(CurrentPlugin, PluginTracker.PluginStatus.INSTALL);
			pluginTracker.addPlugin(CurrentPlugin, PluginTracker.PluginStatus.CURRENT);
		}
	}

	/**
	 * Marks the given object for deletion the next time Cytoscape
	 * is restarted.
	 * @param Obj
	 */
	public void delete(PluginInfo Obj) {
		pluginTracker.addPlugin(Obj, PluginTracker.PluginStatus.DELETE);
	}

	/**
	* Takes all objects on the "to-delete" list and deletes them
	*/
	public void delete() throws ManagerError {
		String ErrorMsg = "Failed to delete all files for the following plugins:\n";
		List<String> DeleteFailed = new ArrayList<String>();

		List<PluginInfo> Plugins = pluginTracker.getListByStatus(PluginTracker.PluginStatus.DELETE);

		for (PluginInfo CurrentPlugin : Plugins) {
			boolean deleteOk = false;

			// shouldn't happen...
			if (CurrentPlugin.getFileList().size() <= 0) {
				throw new ManagerError(CurrentPlugin.getName()
				                       + " does not have a list of files.  Please delete this plugin manually.");
			}

			// needs the list of all files installed
			for (String FileName : CurrentPlugin.getFileList()) {
				File ToDelete = new java.io.File(FileName);
				deleteOk = ToDelete.delete();
			}

			pluginTracker.removePlugin(CurrentPlugin, PluginTracker.PluginStatus.DELETE);

			if (!deleteOk) {
				DeleteFailed.add(CurrentPlugin.getName());
			}
		}

		// any files that failed to delete should get noted
		if (DeleteFailed.size() > 0) {
			for (String Msg : DeleteFailed) {
				ErrorMsg += ("-" + Msg + "\n");
			}

			throw new ManagerError(ErrorMsg);
		}
	}

	/**
	* Finds the given version of the new object, sets the old object for deletion
	* and downloads new object to temporary directory
	*
	* @param Obj
	*/
	public void update(PluginInfo Obj, String Version) throws ManagerError {
		// find new plugin, download, add to install list
		if (Obj.getProjectUrl() == null) {
			throw new ManagerError(Obj.getName()
			                       + " does not have a project url.\nCannot auto-update this plugin.");
		}

		// go through all the matching plugins on the given url
		for (PluginInfo NewPlugin : inquire(Obj.getProjectUrl())) {
			// unique id is the same and the version is what was requested
			if (NewPlugin.getID().equals(Obj.getID())
			    && NewPlugin.getPluginVersion().equals(Version.trim())) {
				download(NewPlugin);
				pluginTracker.addPlugin(NewPlugin, PluginTracker.PluginStatus.INSTALL);
				pluginTracker.addPlugin(Obj, PluginTracker.PluginStatus.DELETE);
			}
		}
	}

	/**
	* Downloads given object to the temporary directory.
	*
	* @param Obj
	* @return File downloaded
	*/
	public File download(PluginInfo Obj) throws ManagerError {
		File Download = null;

		try {
			Download = HttpUtils.downloadFile(Obj.getUrl(), new File(tempDir, createFileName(Obj)));
		} catch (IOException E) {
			throw new ManagerError("Failed to download file from " + Obj.getUrl() + " to "
			                       + tempDir.getAbsolutePath(), E);
		}

		Obj.addFileName(Download.getAbsolutePath());
		pluginTracker.addPlugin(Obj, PluginTracker.PluginStatus.INSTALL);

		return Download;
	}

	private String createFileName(PluginInfo Obj) {
		return Obj.getName() + "-" + Obj.getPluginVersion() + "." + Obj.getFileType().toString();
	}

	/**
	* Checks to see new plugin matches the original plugin and has a newer
	* version
	*/
	private boolean isUpdatable(PluginInfo Current, PluginInfo New) {
		boolean hasUpdate = false;

		if ((Current.getID() != null) && (New.getID() != null)) {
			boolean newVersion = isVersionNew(Current, New);

			if ((Current.getID().trim().equals(New.getID().trim())
			    && Current.getProjectUrl().equals(New.getProjectUrl())) && newVersion) {
				hasUpdate = true;
			}
		}

		return hasUpdate;
	}

	/**
	* compares the version numbers. Be sure the plugin info objects are passed in
	* order
	*
	* @param Current
	*          The currently installed PluginInfo object
	* @param New
	*          The new PluginInfo object to compare to
	*/
	private boolean isVersionNew(PluginInfo Current, PluginInfo New) {
		boolean isNew = false;
		String[] CurrentVersion = Current.getPluginVersion().split("\\.");
		String[] NewVersion = New.getPluginVersion().split("\\.");

		System.out.println("Current size: " + CurrentVersion.length);
		System.out.println("New size: " + NewVersion.length);

		for (int i = 0; i < NewVersion.length; i++) {
			System.out.println("i=" + i);

			// if we're beyond the end of the current version array then it's a
			// new version
			if (CurrentVersion.length <= i) {
				isNew = true;

				break;
			}

			// if at any point the new version number is greater
			// then it's "new" ie. 1.2.1 > 1.1
			// whoops...what if they add a character in here?? TODO !!!!
			if (Integer.valueOf(NewVersion[i]) > Integer.valueOf(CurrentVersion[i]))
				isNew = true;
		}

		return isNew;
	}

	/*
	* Iterate through all class files, return the subclass of CytoscapePlugin.
	* Similar to CytoscapeInit, however only plugins with manifest files that
	* describe the class of the CytoscapePlugin are valid.
	*/
	private String getPluginClass(String FileName) {
		String PluginClassName = null;

		java.net.URL[] urls = new java.net.URL[] { jarURL(FileName) };
		this.classLoader = new URLClassLoader(urls, Cytoscape.class.getClassLoader());

		try {
			JarURLConnection jc = (JarURLConnection) urls[0].openConnection();
			JarFile Jar = jc.getJarFile();
			Manifest m = Jar.getManifest();

			if (m != null) {
				PluginClassName = m.getMainAttributes().getValue("Cytoscape-Plugin");
			}
		} catch (java.io.IOException E) {
			E.printStackTrace();
		}

		return PluginClassName;
	}

	private static URL jarURL(String urlString) {
		URL url = null;

		try {
			String uString;

			if (urlString.matches(FileUtil.urlPattern))
				uString = "jar:" + urlString + "!/";
			else
				uString = "jar:file:" + urlString + "!/";

			url = new URL(uString);
		} catch (MalformedURLException mue) {
			mue.printStackTrace();
			System.out.println("couldn't create jar url from '" + urlString + "'");
		}

		return url;
	}
}
