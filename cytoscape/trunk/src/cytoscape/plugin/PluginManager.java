/*
 Copyright (c) 2006, 2007, The Cytoscape Consortium (www.cytoscape.org)

 The Cytoscape Consortium is:
 - Institute for Systems Biology
 - University of California San Diego
 - Memorial Sloan-Kettering Cancer Center
 - Institut Pasteur
 - Agilent Technologies

 This library is free software; you can redistribute it and/or modify it
 under the terms of the GNU Lesser General Public License as published
 by the Free Software Foundation; either version 2.1 of the License, or
 any later version.

 This library is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 documentation provided hereunder is on an "as is" basis, and the
 Institute for Systems Biology and the Whitehead Institute
 have no obligations to provide maintenance, support,
 updates, enhancements or modifications.  In no event shall the
 Institute for Systems Biology and the Whitehead Institute
 be liable to any party for direct, indirect, special,
 incidental or consequential damages, including lost profits, arising
 out of the use of this software and its documentation, even if the
 Institute for Systems Biology and the Whitehead Institute
 have been advised of the possibility of such damage.  See
 the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation,
 Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
*/
package cytoscape.plugin;

import cytoscape.Cytoscape;
import cytoscape.CytoscapeInit;
import cytoscape.CytoscapeVersion;

import cytoscape.plugin.util.*;

import cytoscape.util.FileUtil;

import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import javax.swing.JOptionPane;


/**
 * @author skillcoy This class deals with finding, downloading, installing and
 *         tracking plugins.
 */
public class PluginManager {
	private static PluginManager pluginMgr = null;
	private ClassLoader classLoader;
	private PluginTracker pluginTracker;
	private String defaultUrl;
	private String cyVersion;
	private String DEFAULT_VALUE = "http://cytoscape.org/whatever-the-plugin-url-is";

	static {
		new PluginManager();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public static PluginManager getPluginManager() {
		if (pluginMgr == null) {
			pluginMgr = new PluginManager();
		}

		return pluginMgr;
	}

	private PluginManager() {
		defaultUrl = CytoscapeInit.getProperties().getProperty("defaultPluginUrl", DEFAULT_VALUE);

		try {
			pluginTracker = new PluginTracker();
			cyVersion = CytoscapeVersion.version;
		} catch (java.io.IOException E) {
			E.printStackTrace(); // TODO do something useful with error
		}
	}

	/*
	 * JOptionPane with an error msg
	 */
	private static void showError(String Msg) {
		JOptionPane.showMessageDialog(Cytoscape.getDesktop(), Msg, "Plugin Installation Error",
		                              JOptionPane.ERROR_MESSAGE);
	}

	/**
	 * @return Url to the url set up in the cytoscape.props file for plugins
	 */
	public String getDefaultUrl() {
		return defaultUrl;
	}

	/**
	 * @return List of PluginInfo objects from the default url
	 */
	public List<PluginInfo> inquire() {
		return inquire(defaultUrl);
	}

	/**
	 * Calls the given url, expects document describing plugins available for
	 * download
	 *
	 * @param Url
	 * @return List of PluginInfo objects
	 */
	public List<PluginInfo> inquire(String Url) {
		List<PluginInfo> Plugins = null;

		try {
			PluginFileReader Reader = new PluginFileReader(Url);
			Plugins = Reader.getPlugins();
		} catch (java.io.IOException E) {
			showError("Failed to get plugin information from the url '" + Url + "'");
			E.printStackTrace();
		}

		return Plugins;
	}

	/**
	 * @return A hashmap of plugins for the current version of Cytoscape listed
	 *         by their category from the default url
	 */

	//	public Map<String, List<PluginInfo>> getPluginsByCategory(PluginInfo[] Plugins) {
	//		ArrayList<PluginInfo> piList = new ArrayList<PluginInfo>();
	//		piList.
	//	return getPluginsByCategory(defaultUrl);
	//	}

	/**
	 *
	 * @param Url
	 * @return A hashmap of plugins for the current version of Cytoscape listed
	 *         by their category
	 */
	public Map<String, List<PluginInfo>> getPluginsByCategory(List<PluginInfo> Plugins) {
		HashMap<String, List<PluginInfo>> Categories = new HashMap<String, List<PluginInfo>>();

		for (PluginInfo Info : Plugins) {
			// don't list anything not implemented to the current version
			if (!Info.getCytoscapeVersion().equals(this.cyVersion))
				continue;

			String CategoryName = Info.getCategory();

			if ((CategoryName == null) || (CategoryName.length() <= 0))
				CategoryName = "Uncategorized";

			if (Categories.containsKey(CategoryName)) // add to existing list
				Categories.get(CategoryName).add(Info);
			else { // create new list

				List<PluginInfo> pList = new ArrayList<PluginInfo>();
				pList.add(Info);
				Categories.put(CategoryName, pList);
			}
		}

		return Categories;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public List<PluginInfo> getInstalledPlugins() {
		List<PluginInfo> Installed = new ArrayList<PluginInfo>();
		Installed.addAll(pluginTracker.getInstalledPlugins());

		return Installed;
	}

	/**
	 * Aborts install of jar/zip files
	 */
	public void abortInstall() {
		HttpUtils.STOP = true;
		UnzipUtil.STOP = true;
	}

	/**
	 * Gets plugin from the url within the PluginInfo object. Installs zip/jar
	 * files
	 *
	 * @param PluginInfo
	 * @return True if plugin installed
	 */
	public boolean install(PluginInfo obj) {
		/*
		 * currently installs jar and zip files only If jar file just drop it in
		 * the plugin dir If zip file check to see that it's set up with
		 * directories: plugins/jarfile.jar plugins/jarfile2.jar
		 * someDir/propsfile.props etc...
		 *
		 * TODO This method has gotten somewhat more complicated than originally
		 * planned. Maybe good to think about breaking it up.
		 */
		boolean installOk = false;

		switch (obj.getFileType()) {
			// write jar directly to the plugins dir
			case JAR:

				try {
					java.io.File Installed = HttpUtils.downloadFile(obj.getUrl(),
					                                                obj.getName() + ".jar",
					                                                "plugins"
					                                                + System.getProperty("file.separator"));
					obj.addFileName(Installed.getAbsolutePath());

					String ClassName = getPluginClass(Installed.getAbsolutePath());
					obj.setPluginClassName(ClassName);

					if (!HttpUtils.STOP)
						installOk = true;
				} catch (java.io.IOException E) {
					showError("Error installing plugin '" + obj.getName() + "' from "
					          + obj.getUrl());
					E.printStackTrace();
				}

				break;

			/*
			 * unzip, this will put things in the directories set up within the zip
			 * file
			 */
			case ZIP:

				try {
					if (UnzipUtil.zipContains(HttpUtils.getInputStream(obj.getUrl()),
					                          "plugins" + System.getProperty("file.separator")
					                          + "\\w+\\.jar")) {
						List<String> InstalledFiles = UnzipUtil.unzip(HttpUtils.getInputStream(obj
						                                                                                                                                                                                                                                               .getUrl()));
						obj.setFileList(InstalledFiles);

						String ClassName = getPluginClass(InstalledFiles);
						obj.setPluginClassName(ClassName);

						if (!UnzipUtil.STOP)
							installOk = true;
						else
							this.delete(obj);
					} else
						// at least one jar file is required to be in the plugin
						// directory in
						// order to unzip correctly
						showError("Zip file " + obj.getName()
						          + " did not contain a plugin directory with a jar file.");
				} catch (java.io.IOException E) {
					showError("Error unzipping " + obj.getUrl());
					E.printStackTrace();
				}

				break;
		}

		if (installOk)
			pluginTracker.addInstalledPlugin(obj, true);

		return installOk;
	}

	/**
	 * @return List contains the PluginInfo objects for all plugins with
	 *         available updates.
	 */
	public List<PluginInfo> getUpdatablePlugins() {
		ArrayList<PluginInfo> Updatable = new ArrayList<PluginInfo>();

		for (PluginInfo CurrentPlugin : getInstalledPlugins()) {
			if (CurrentPlugin.getProjectUrl() == null)
				continue;

			List<PluginInfo> ProjectPlugins = inquire(CurrentPlugin.getProjectUrl());

			for (PluginInfo NewPlugin : ProjectPlugins) {
				if (isUpdatable(CurrentPlugin, NewPlugin))
					Updatable.add(CurrentPlugin);
			}
		}

		return Updatable;
	}

	/**
	 * Updates a given plugin if it defines a project url to use in updating
	 * TODO!!!!
	 */
	public boolean update(PluginInfo obj) {
		boolean updated = false;

		if (obj.getProjectUrl() == null) {
			// TODO error
			showError("Cytoscape doesn't have a project url for the plugin " + obj.getName()
			          + ".  You may need to manually updated.");
		}

		List<PluginInfo> CurrentPlugins = inquire(obj.getProjectUrl());

		for (PluginInfo Plugin : CurrentPlugins) {
			// unique id + projecturl is the same and the version is new
			if (isUpdatable(obj, Plugin)) {
				// then update!!
				delete(obj);
				updated = install(obj);
			} else {
				// no message since we don't necessarily expect a plugin to have
				// an update
				updated = false;
			}
		}

		return updated;
	}

	/*
	 * Checks to see new plugin matches the original plugin and has a newer
	 * version
	 */
	private boolean isUpdatable(PluginInfo Current, PluginInfo New) {
		if ((Current.getID().equals(New.getID())
		    && Current.getProjectUrl().equals(New.getProjectUrl())) && isVersionNew(Current, New)) {
			return true;
		} else

			return false;
	}

	/**
	 * compares the version numbers. Be sure the plugin info objects are passed
	 * in order
	 *
	 * @param Current
	 *            The currently installed PluginInfo object
	 * @param New
	 *            The new PluginInfo object to compare to
	 */
	private boolean isVersionNew(PluginInfo Current, PluginInfo New) {
		boolean isNew = false;
		String[] CurrentVersion = Current.getPluginVersion().split("\\.");
		String[] NewVersion = Current.getPluginVersion().split("\\.");

		for (int i = 0; i < NewVersion.length; i++) {
			// if we're beyond the end of the current version array then it's a
			// new version
			if (CurrentVersion.length < i) {
				isNew = true;

				break;
			}

			// this means that if at any point the new version number is greater
			// then it's "new"
			// ie. 1.2.1 > 1.1
			if (Integer.valueOf(NewVersion[i]) > Integer.valueOf(CurrentVersion[i]))
				isNew = true;
		}

		return isNew;
	}

	/**
	 * Deletes installed plugin and all known associated files
	 *
	 * @param obj
	 * @return If any file from the plugin failed to delete method returns
	 *         false.
	 */
	public boolean delete(PluginInfo obj) {
		boolean deleteOk = false;

		if (obj.getFileList().size() <= 0) {
			// for now I'll just output an error to stderr
			System.err.println(obj.getName()
			                   + " does not have a list of files.  Deletion needs to be manual");
		}

		// needs the list of all files installed
		// Iterator<String> fileI = obj.getFileList().iterator();
		for (String FileName : obj.getFileList()) // while (fileI.hasNext())
		 {
			// String FileName = fileI.next();
			if (!(new java.io.File(FileName)).delete())
				deleteOk = false;
			else
				deleteOk = true;
		}

		if (deleteOk)
			this.pluginTracker.removePlugin(obj);

		return deleteOk;
	}

	/**
	 * Registers the plugin with tracking object.
	 *
	 * @param Plugin
	 * @param JarFileName
	 */
	public void register(CytoscapePlugin Plugin, String JarFileName) {
		PluginInfo InfoObj;

		if (Plugin.getPluginInfoObject() == null) {
			System.out.println(Plugin.getClass().getName() + " no plugin info object");
			InfoObj = new PluginInfo();
			InfoObj.setName(Plugin.getClass().getName());
			InfoObj.setPluginClassName(Plugin.getClass().getName());

			if (JarFileName != null)
				InfoObj.addFileName(JarFileName);
		} else {
			InfoObj = Plugin.getPluginInfoObject();
			InfoObj.setPluginClassName(Plugin.getClass().getName());

			if (JarFileName != null)
				InfoObj.addFileName(JarFileName);
		}

		pluginTracker.addInstalledPlugin(InfoObj, false);
	}

	/*
	 * The following methods are for the purposes of getting the class name of
	 * the class that extends CytoscapePlugin If CytoscapePlugin should take
	 * over loading classes these methods maybe be accessed from elsewhere.
	 * Currently they are mostly replicated from CytoscapeInit (ick). SK
	 * 2007/03/19
	 */

	/*
	 * Iterate through all the jar files in a zip, return the one that
	 * subclasses CytoscapePlugin
	 */
	private String getPluginClass(List<String> Files) {
		// Iterator<String> fileI = Files.iterator();
		for (String FileName : Files) // while (fileI.hasNext())
		 {
			// String FileName = fileI.next();
			if (!FileName.endsWith(".jar"))
				continue;

			String ClassName = getPluginClass(FileName);

			if (ClassName != null)
				return ClassName;
		}

		return null;
	}

	/*
	 * Iterate through all class files, return the subclass of CytoscapePlugin.
	 * Similar to CytoscapeInit
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
				String className = m.getMainAttributes().getValue("Cytoscape-Plugin");

				if (className != null) {
					Class pc = getPluginClassFromJar(className);

					if (pc != null)
						PluginClassName = className;
				}
			}

			// no manifest file, go through all the classes
			Enumeration entries = Jar.entries();

			if (entries == null)
				return null;

			while (entries.hasMoreElements()) {
				// get the entry
				String entry = entries.nextElement().toString();

				if (entry.endsWith("class")) {
					// convert the entry to an assignable class name
					entry = entry.replaceAll("\\.class$", "");
					// A regex to match the two known types of file
					entry = entry.replaceAll("/|\\\\", ".");

					Class pc = getPluginClassFromJar(entry);

					if (pc != null)
						PluginClassName = entry;
				}
			}
		} catch (java.io.IOException E) {
			E.printStackTrace();
		}

		return PluginClassName;
	}

	/*
	 * This loads a class, returns the class if it's a subclass of
	 * CytoscapePlugin copied from CytoscapeInit mostly, not sure if it's really
	 * ok to load a class while cytoscape is running?
	 */
	private Class getPluginClassFromJar(String name) {
		Class c = null;

		try {
			this.classLoader.toString();
			c = this.classLoader.loadClass(name);
		} catch (ClassNotFoundException e) {
			// e.printStackTrace();
			System.err.println("Class not found " + e.getMessage());

			return null;
		} catch (NoClassDefFoundError e) {
			e.printStackTrace();

			return null;
		}

		if (CytoscapePlugin.class.isAssignableFrom(c))
			return c;

		else

			return null;
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
