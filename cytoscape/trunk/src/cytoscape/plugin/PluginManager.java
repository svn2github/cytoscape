/**
 * 
 */
package cytoscape.plugin;

import cytoscape.Cytoscape;
import cytoscape.CytoscapeInit;
import cytoscape.CytoscapeVersion;

import cytoscape.plugin.util.*;
import java.util.*;
import java.util.regex.Pattern;
import java.util.zip.ZipInputStream;

import javax.swing.JOptionPane;

/**
 * @author skillcoy
 * This class deals with finding, downloading, installing and tracking plugins.
 */
public class PluginManager
	{
	private String DefaultUrl;
	private String CyVersion;
	
	public PluginManager() 
		{
		DefaultUrl = CytoscapeInit.getProperties().getProperty("defaultPluginUrl");
		CyVersion = CytoscapeVersion.version;
		}
	
	/**
	 * @return Url to the url set up in the cytoscape.props file for plugins
	 */
	public String getDefaultUrl()
		{
		return DefaultUrl;
		}
	
	/**
	 * @return List of PluginInfo objects from the default url
	 */
	public List<PluginInfo> inquire()
		{
		return inquire(DefaultUrl);
		}
	
	/**
	 * Calls the given url, expects document describing plugins available for download
	 * @param Url
	 * @return List of PluginInfo objects
	 */
	public List<PluginInfo> inquire(String Url)
		{
		List<PluginInfo> Plugins = null;

		try
			{
			PluginFileReader Reader = new PluginFileReader(Url);
			Plugins = Reader.getPlugins();
			}
		catch (java.io.IOException E)
			{
			showError("Failed to get plugin information from the url '" + Url + "'"); 
			E.printStackTrace();
			}
		return Plugins;
		}

	/**
	 * @return A hashmap of plugins for the current version of Cytoscape listed by their category from the default url
	 */
	public Map<String, List<PluginInfo>> getPluginsByCategory()
		{
		return getPluginsByCategory(DefaultUrl);
		}
	
	/**
	 * 
	 * @param Url
	 * @return A hashmap of plugins for the current version of Cytoscape listed by their category
	 */
	public Map<String, List<PluginInfo>> getPluginsByCategory(String Url)
		{
		HashMap<String, List<PluginInfo>> Categories = new HashMap<String, List<PluginInfo>>();
		
		Iterator<PluginInfo> pI = inquire(Url).iterator();
		while(pI.hasNext())
			{
			PluginInfo Info = pI.next();

			System.out.println("CyVersion: " + CyVersion);
			System.out.println("PluginCyVersion: "  + Info.getCytoscapeVersion());
			// don't list anything not implemented to the current version
			if (!Info.getCytoscapeVersion().equals(this.CyVersion)) continue;

			String CategoryName = Info.getCategory();
			if (CategoryName == null || CategoryName.length() <= 0)
				CategoryName = "Uncategorized";

			if (Categories.containsKey(CategoryName)) // add to existing list
				Categories.get(CategoryName).add(Info);
			else // create new list
				{
				List<PluginInfo> pList = new ArrayList<PluginInfo>();
				pList.add(Info);
				Categories.put(CategoryName, pList);
				}
			}
		return Categories;
		}

	/*
	 * Aborts install of jar/zip files
	 */
	public void abortInstall()
		{ 
		HttpUtils.STOP = true;
		UnzipUtil.STOP = true;
		}
	
	/**
	 * Gets plugin from the url within the PluginInfo object
	 * @param PluginInfo
	 * @return True if plugin installed 
	 */
	public boolean install(PluginInfo obj)
		{ 
		System.out.println("INSTALLING " + obj.getName());
		/* currently installs jar and zip files only 
		 * If jar file just drop it in the plugin dir
		 * If zip file check to see that it's set up with directories:
		 * 		plugins/jarfile.jar
		 * 		plugins/jarfile2.jar
		 * 		someDir/propsfile.props
		 * etc...
		 */
		
		boolean installOk = false;
		switch (obj.getFileType())
			{
			case (PluginInfo.JAR):
				try
					{ // write jar directly to the plugins dir
					java.io.File Installed = HttpUtils.downloadFile(obj.getUrl(), obj.getName()+".jar", "plugins/");
					obj.addFileName(Installed.getAbsolutePath());
					if (!HttpUtils.STOP) installOk = true;
					}
				catch (java.io.IOException E)
					{
					showError("Error installing plugin '" + obj.getName() + "' from " + obj.getUrl());
					E.printStackTrace();
					}
				break;
			case (PluginInfo.ZIP):
				try
					{	// unzip, this will put things in the directories set up within the zip file
					if (UnzipUtil.zipContains(HttpUtils.getInputStream(obj.getUrl()), "plugins/\\w+\\.jar"))
						{
						List<String> InstalledFiles = UnzipUtil.unzip( HttpUtils.getInputStream(obj.getUrl()));
						obj.setFileList(InstalledFiles);
						if(!UnzipUtil.STOP) installOk = true;
						else this.delete(obj);
						}
					else // at least one jar file is required to be in the plugin directory in order to unzip correctly
						showError( "Zip file " + obj.getName() + " did not contain a plugin directory with a jar file."); 
					}
				catch (java.io.IOException E)
					{
					showError( "Error unzipping " + obj.getUrl());
					E.printStackTrace();
					}
				break;
			};
		return installOk;
		}

	/**
	 * Deletes installed plugin and all known associated files
	 * @param obj
	 * @return 
	 * 		If any file from the plugin failed to delete method returns false.
	 */
	public boolean delete(PluginInfo obj)
		{
		boolean deleteOk = false;
		// needs the list of all files installed
		Iterator<String> fileI = obj.getFileList().iterator();
		while(fileI.hasNext())
			{
			String FileName = fileI.next();
			if (!(new java.io.File(FileName)).delete()) deleteOk = false;
			}
		return deleteOk;
		}
	
	/**
	 * 
	 * @return List containing PluginInfo objects for each installed
	 * plugin.
	 */
	public List<PluginInfo> listCurrentlyInstalled()
		{
		return PluginTracker.getInstalledPlugins();
		}
	
	private void showError(String Msg)
		{
		JOptionPane.showMessageDialog(Cytoscape.getDesktop(), Msg, "Plugin Installation Error", JOptionPane.ERROR_MESSAGE);
		}
	
	}
