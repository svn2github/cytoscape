/**
 *
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
	private ClassLoader classLoader;
	private PluginTracker Tracker;
	private String DefaultUrl;
	private String CyVersion;

	/**
	 * Creates a new PluginManager object.
	 */
	public PluginManager() {
		DefaultUrl = CytoscapeInit.getProperties().getProperty("defaultPluginUrl");
		init();
	}

	// is this a good idea?  Maybe not even be possible.  Manager is instantiated only once by CytoscapeInit (or should be!)
	/**
	 * Creates a new PluginManager object.
	 *
	 * @param Url  DOCUMENT ME!
	 */
	public PluginManager(String Url) {
		DefaultUrl = Url;
		init();
	}

	private void init() {
		try {
			Tracker = new PluginTracker();
			CyVersion = CytoscapeVersion.version;
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
		return DefaultUrl;
	}

	/**
	* @return List of PluginInfo objects from the default url
	*/
	public List<PluginInfo> inquire() {
		return inquire(DefaultUrl);
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
	* @return A hashmap of plugins for the current version of Cytoscape listed by
	*         their category from the default url
	*/
	public Map<String, List<PluginInfo>> getPluginsByCategory() {
		return getPluginsByCategory(DefaultUrl);
	}

	/**
	*
	* @param Url
	* @return A hashmap of plugins for the current version of Cytoscape listed by
	*         their category
	*/
	public Map<String, List<PluginInfo>> getPluginsByCategory(String Url) {
		HashMap<String, List<PluginInfo>> Categories = new HashMap<String, List<PluginInfo>>();

		Iterator<PluginInfo> pI = inquire(Url).iterator();

		while (pI.hasNext()) {
			PluginInfo Info = pI.next();

			System.out.println("CyVersion: " + CyVersion);
			System.out.println("PluginCyVersion: " + Info.getCytoscapeVersion());

			// don't list anything not implemented to the current version
			if (!Info.getCytoscapeVersion().equals(this.CyVersion))
				continue;

			String CategoryName = Info.getCategory();

			if ((CategoryName == null) || (CategoryName.length() <= 0))
				CategoryName = "Uncategorized";

			if (Categories.containsKey(CategoryName)) // add to existing list
				Categories.get(CategoryName).add(Info);
			else// create new list
			 {
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
	public PluginInfo[] getInstalledPlugins() {
		Collection<PluginInfo> Installed = Tracker.getInstalledPlugins();
		PluginInfo[] AllInstalled = new PluginInfo[Installed.size()];

		return Installed.toArray(AllInstalled);
	}

	/**
	* Aborts install of jar/zip files
	*/
	public void abortInstall() {
		HttpUtils.STOP = true;
		UnzipUtil.STOP = true;
	}

	/**
	* Gets plugin from the url within the PluginInfo object.  Installs zip/jar files
	*
	* @param PluginInfo
	* @return True if plugin installed
	*/
	public boolean install(PluginInfo obj) {
		/*
		* currently installs jar and zip files only If jar file just drop it in the
		* plugin dir If zip file check to see that it's set up with directories:
		* plugins/jarfile.jar plugins/jarfile2.jar someDir/propsfile.props etc...
		*
		* TODO This method has gotten somewhat more complicated than originally planned.  Maybe good to think about
		* breaking it up.
		*/
		boolean installOk = false;

		switch (obj.getFileType()) {
			case (PluginInfo.JAR):

				try { // write jar directly to the plugins dir

					java.io.File Installed = HttpUtils.downloadFile(obj.getUrl(),
					                                                obj.getName() + ".jar",
					                                                "plugins/");
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

			case (PluginInfo.ZIP):

				try { // unzip, this will put things in the directories set up within the zip file

					if (UnzipUtil.zipContains(HttpUtils.getInputStream(obj.getUrl()),
					                          "plugins/\\w+\\.jar")) {
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
						// at least one jar file is required to be in the plugin directory in
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
			Tracker.addInstalledPlugin(obj, true);

		return installOk;
	}

	/**
	* Deletes installed plugin and all known associated files
	*
	* @param obj
	* @return If any file from the plugin failed to delete method returns false.
	*/
	public boolean delete(PluginInfo obj) {
		boolean deleteOk = false;

		if (obj.getFileList().size() <= 0) {
			// for now I'll just output an error to stderr
			System.err.println(obj.getName()
			                   + " does not have a list of files.  Deletion needs to be manual");
		}

		// needs the list of all files installed
		Iterator<String> fileI = obj.getFileList().iterator();

		while (fileI.hasNext()) {
			String FileName = fileI.next();

			if (!(new java.io.File(FileName)).delete())
				deleteOk = false;
			else
				deleteOk = true;
		}

		if (deleteOk)
			this.Tracker.removePlugin(obj);

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

		Tracker.addInstalledPlugin(InfoObj, false);
	}

	/*
	 * The following methods are for the purposes of getting the class name of the class that extends CytoscapePlugin
	 * If CytoscapePlugin should take over loading classes these methods maybe be accessed from elsewhere.  Currently they
	 * are mostly replicated from CytoscapeInit (ick).
	 * SK 2007/03/19
	 */

	/*
	 * Iterate through all the jar files in a zip, return the one that subclasses CytoscapePlugin
	 */
	private String getPluginClass(List<String> Files) {
		Iterator<String> fileI = Files.iterator();

		while (fileI.hasNext()) {
			String FileName = fileI.next();

			if (!FileName.endsWith(".jar"))
				continue;

			String ClassName = getPluginClass(FileName);

			if (ClassName != null)
				return ClassName;
		}

		return null;
	}

	/*
	 * Iterate through all class files, return the subclass of CytoscapePlugin.  Similar to CytoscapeInit
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

	/* This loads a class, returns the class if it's a subclass of CytoscapePlugin
	 * copied from CytoscapeInit mostly, not sure if it's really ok to load a
	* class while cytoscape is running?
	*/
	private Class getPluginClassFromJar(String name) {
		Class c = null;

		try {
			this.classLoader.toString();
			c = this.classLoader.loadClass(name);
		} catch (ClassNotFoundException e) {
			//e.printStackTrace();
			System.err.println(e.getMessage());

			return null;
		} catch (NoClassDefFoundError e) {
			//e.printStackTrace();
			System.err.println(e.getMessage());

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
