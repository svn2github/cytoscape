/**
 * 
 */
package cytoscape.plugin;

import cytoscape.*;
import java.io.File;

import java.util.*;
import org.jdom.*;
import org.jdom.output.*;
import org.jdom.input.SAXBuilder;

/*
 * I think this will read and write an xml file pretty similar to the one expected from a website describing
 * plugins.  A list of the files/locations installed will be added.  A tag for plugin type (core/custom) will also
 * be added for the user?
 */


/**
 * @author skillcoy
 * Tracks all installed plugins and the files required for each.
 * Writes out a file when cytoscape is closed describing installed plugins, reads in
 * at load time (check that plugins are still there?)
 */
public class PluginTracker
	{
	//private int UniqueId;
	private Document TrackerDoc;
	private File InstallFile;
	private List<PluginInfo> Installed;
	
	protected PluginTracker() throws java.io.IOException
		{
		Installed = new ArrayList<PluginInfo>();
		InstallFile = new File( CytoscapeInit.getConfigDirectory(), "plugins_installed.xml" );
		
		if (InstallFile.exists())
			{
			SAXBuilder Builder = new SAXBuilder(false);
			try
				{
				TrackerDoc = Builder.build( new File(CytoscapeInit.getConfigDirectory(), "plugins_installed.xml") );
				//setId();
				}
			catch (JDOMException E)
				{ // TODO do something with this error
				E.printStackTrace();
				}
			}
		else
			{
			System.err.println("------- No install file, creating new document --------");
			TrackerDoc = new Document();
			TrackerDoc.setRootElement( new Element("CytoscapePlugin") );
			TrackerDoc.getRootElement().addContent( new Element("pluginlist") );
			}
		}

	/*
	 * Creates or gets the next unique id for a plugin entry
	 */
//	private void setId()
//		{
//		List<Element> PluginList = TrackerDoc.getRootElement().getChild("pluginlist").getChildren("plugin");
//		if (PluginList.size() <= 0) 
//			{
//			UniqueId = 1;
//			return;
//			}
//		
//		int LastId = 0;
//		Iterator<Element> pI = PluginList.iterator();
//		while(pI.hasNext())
//			{
//			int CurrentId = Integer.getInteger(pI.next().getAttributeValue("id")); 
//			if ( LastId <= CurrentId) LastId = CurrentId;
//			}
//		UniqueId = LastId + 1;
//		}
	
	
	public void addInstalledPlugin(PluginInfo obj, boolean Overwrite)
		{ // shoudl this be a hashmap keyed by the plugin class name since 2 classes of the same name
		if (Overwrite && Installed.contains(obj))
			{ // TODO remove from xml file as well
			Installed.remove(obj);
			}
		else if (!Overwrite && Installed.contains(obj))
		
			
			Installed.add(obj);
		
		// TODO each plugin entry should have a unique id
		Element Plugin = new Element("plugin");
		//Plugin.setAttribute("id", String.valueOf(UniqueId));

		Plugin.addContent( new Element("name").setText(obj.getName()) );
		Plugin.addContent( new Element("classname").setText(obj.getPluginClassName()) );
		Plugin.addContent( new Element("description").setText(obj.getDescription()) );
		Plugin.addContent( new Element("pluginVersion").setText(obj.getPluginVersion()) );
		Plugin.addContent( new Element("cytoscapeVersion").setText(obj.getCytoscapeVersion()) );
		Plugin.addContent( new Element("url").setText(obj.getUrl()) );
		Plugin.addContent( new Element("projectUrl").setText(obj.getProjectUrl()) );
		Plugin.addContent( new Element("category").setText(obj.getCategory()) );
		
		Element FileList = new Element("filelist");
		Iterator<String> fileI = obj.getFileList().iterator();
		while(fileI.hasNext())
			FileList.addContent( new Element("file").setText(fileI.next()) );
		Plugin.addContent(FileList);
		
		TrackerDoc.getRootElement().getChild("pluginlist").addContent(Plugin);
		}

	
	public void removePlugin(PluginInfo obj)
		{
		Installed.remove(obj);
		
		Iterator<Element> pI = TrackerDoc.getRootElement().getChild("pluginlist").getChildren("plugin").iterator();
		while(pI.hasNext())
			{
			Element Plugin = pI.next();
//			if ( Plugin.getChild("name").getTextTrim().equals(obj.getName()) &&
//			
//				)
				
			}
		}
	
	/**
	 * 
	 * @return List of PluginInfo objects for installed plugins
	 */
	public List<PluginInfo> getInstalledPlugins()
		{
		return Installed;
		}
	
	
	
	
	}
