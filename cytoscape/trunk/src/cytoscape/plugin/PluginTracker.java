/**
 * 
 */
package cytoscape.plugin;

import cytoscape.*;
import cytoscape.plugin.PluginInfo.AuthorInfo;

import java.io.File;
import java.io.FileWriter;

import java.util.Iterator;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Collection;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.output.XMLOutputter;
import org.jdom.input.SAXBuilder;



/**
 * @author skillcoy
 * Tracks all installed plugins and the files required for each.
 * Writes out a file when cytoscape is closed describing installed plugins, reads in
 * at load time (check that plugins are still there?)
 * 
 * Tracker is meant to only be used by the PluginManager
 */

public class PluginTracker
	{
	private Document TrackerDoc;
	private File InstallFile;
	private String InstallFileName = "plugins_installed.xml";
	
	private HashMap<String, PluginInfo> Installed;
	private HashMap<String, Element> Deleted;

	// lists plugins that have been registered, if any have been deleted they will not be in this list
	// and will be removed from the hash
	private List<String> CurrentPlugins;
	
	protected PluginTracker() throws java.io.IOException
		{
		CurrentPlugins = new ArrayList<String>();
		Installed = new HashMap<String, PluginInfo>();
		InstallFile = new File( CytoscapeInit.getConfigDirectory(), this.InstallFileName );
		
		if (InstallFile.exists())
			{
			SAXBuilder Builder = new SAXBuilder(false);
			try
				{
				TrackerDoc = Builder.build( new File(CytoscapeInit.getConfigDirectory(), this.InstallFileName) );
				readDoc();
				}
			catch (JDOMException E)
				{ // TODO do something with this error
				E.printStackTrace();
				}
			}
		else
			{
			TrackerDoc = new Document();
			TrackerDoc.setRootElement( new Element("CytoscapePlugin") );
			TrackerDoc.getRootElement().addContent( new Element(this.PluginList) );
			}
		}

	/*
	 * Read xml document in, create info objects from entries, add to hash
	 * Should only get read it once.
	 */
	private void readDoc()
		{
		Iterator<Element> pI = TrackerDoc.getRootElement().getChild(this.PluginList).getChildren(this.Plugin).iterator();
		while(pI.hasNext())
			{
			Element Plugin = pI.next();
			// skip deleted plugins
			if (Plugin.getChild("deleted") != null) 
				{
				/* Could be useful to track the xml element of deleted plugins in case the exact same plugin is 
				 * reinstalled.  Currently not doing anything with it though. */
				//Deleted.put(Plugin.getChildTextTrim(this.Class), Plugin);
				continue;
				}
			
			PluginInfo Info = new PluginInfo();
			Info.setName( Plugin.getChildTextTrim(this.Name) );
			Info.setDescription( Plugin.getChildTextTrim(this.Desc) );
			Info.setPluginClassName( Plugin.getChildTextTrim(this.Class) );
			Info.setPluginVersion( Plugin.getChildTextTrim(this.PluginVers) );
			Info.setCytoscapeVersion( Plugin.getChildTextTrim(this.CytoVers) );
			Info.setCategory( Plugin.getChildTextTrim(this.Category) );
			Info.setUrl( Plugin.getChildTextTrim(this.Url) );
			Info.setProjectUrl( Plugin.getChildTextTrim(this.ProjUrl) );
			
			Iterator<Element> fI = Plugin.getChild(this.FileList).getChildren(this.File).iterator();
			while(fI.hasNext())
				Info.addFileName( fI.next().getTextTrim() );

			Iterator<Element> aI = Plugin.getChild(this.AuthorList).getChildren(this.Author).iterator();
			while(aI.hasNext())
				{
				Element Author = aI.next();
				Info.addAuthor(Author.getChildTextTrim(this.Name), Author.getChildTextTrim(this.Inst));
				}
			
			Installed.put(Info.getPluginClassName(), Info);
			}
		}
	
	
	/*
	 * 1. Object has already been added to the set then it will also be in the xml file
	 * 2. Object is not in the set, add to set and the xml file
	 * 3. Object was previously added but needs to be changed, remove from set and xml file, reinsert (as in 
	 *    an update has occurred)
	 */
	protected void addInstalledPlugin(PluginInfo obj, boolean Overwrite)
		{ 
		if (!Overwrite && Installed.containsKey(obj.getPluginClassName())) return;
		
		Element Plugin = new Element(this.Plugin);
		if (Overwrite && Installed.containsKey(obj.getPluginClassName()))
			{ // replace
			Installed.remove(obj.getPluginClassName());
			CurrentPlugins.remove(obj.getPluginClassName()); // shouldn't be in here but could be
			Plugin = this.getMatchingPlugin(obj);
			//Plugin.removeContent(); // jdom 1.0
			this.removeChildren(Plugin);
			}

		CurrentPlugins.add(obj.getPluginClassName());
		Installed.put(obj.getPluginClassName(), obj);
		
		Plugin.addContent( new Element(this.Name).setText(obj.getName()) );
		Plugin.addContent( new Element(this.Class).setText(obj.getPluginClassName()) );
		Plugin.addContent( new Element(this.Desc).setText(obj.getDescription()) );
		Plugin.addContent( new Element(this.PluginVers).setText(obj.getPluginVersion()) );
		Plugin.addContent( new Element(this.CytoVers).setText(obj.getCytoscapeVersion()) );
		Plugin.addContent( new Element(this.Url).setText(obj.getUrl()) );
		Plugin.addContent( new Element(this.ProjUrl).setText(obj.getProjectUrl()) );
		Plugin.addContent( new Element(this.Category).setText(obj.getCategory()) );
		
		Element AuthorList = new Element(this.AuthorList);
		Iterator<cytoscape.plugin.PluginInfo.AuthorInfo> aI = obj.getAuthors().iterator();
		while(aI.hasNext())
			{
			AuthorInfo authorInfo = aI.next();
			Element Author = new Element(this.Author);
			Author.addContent( new Element(this.Name).setText(authorInfo.getAuthor()) );
			Author.addContent( new Element(this.Inst).setText(authorInfo.getInstitution()) );
			AuthorList.addContent(Author);
			}
		Plugin.addContent(AuthorList);
		
		Element FileList = new Element(this.FileList);
		Iterator<String> fileI = obj.getFileList().iterator();
		while(fileI.hasNext())
			FileList.addContent( new Element(this.File).setText(fileI.next()) );
		Plugin.addContent(FileList);
		
		TrackerDoc.getRootElement().getChild(this.PluginList).addContent(Plugin);
		write();
		}

	/**
	 * Removes a plugin from the list/xml of installed plugins
	 * @param obj
	 */
	protected void removePlugin(PluginInfo obj)
		{
		System.out.println("------- Deleting " + obj.getName());
		Installed.remove(obj.getPluginClassName());
		
		Iterator<Element> pI = TrackerDoc.getRootElement().getChild(this.PluginList).getChildren(this.Plugin).iterator();
		while(pI.hasNext())
			{
			Element Plugin = pI.next();
			if (Plugin.getChild(this.Class).getTextTrim().equals(obj.getPluginClassName()))
				{
				Plugin.addContent( new Element("deleted") );
				}
			}
		
		write();
		}
	
	
	/**
	 * 
	 * @return List of PluginInfo objects for installed plugins
	 */
	protected Collection<PluginInfo> getInstalledPlugins()
		{
		return Installed.values();
		}
	
	/**
	 * Writes doc to file
	 */
	protected void write()
		{
		XMLOutputter out = new XMLOutputter("  ", true);
		out.setTrimAllWhite(true);
		//System.err.println( out.outputString(TrackerDoc) );
		try
			{ out.output(TrackerDoc, new FileWriter(this.InstallFile) ); }
		catch (java.io.IOException E) { E.printStackTrace(); }
		}
	
	
	private Element getMatchingPlugin(PluginInfo obj)
		{
		Iterator<Element> pI = TrackerDoc.getRootElement().getChild(this.PluginList).getChildren(this.Plugin).iterator();
		while(pI.hasNext())
			{
			Element Plugin = pI.next();
			if ( Plugin.getChildTextTrim(this.Class).equals(obj.getPluginClassName()) ) 
				return Plugin;
			}
		return null;
		}
	
	private void removeChildren(Element xmlElement)
		{
		Iterator<Element> iter = xmlElement.getChildren().iterator();
		while(iter.hasNext())
			{ 
			iter.next().detach();
			}
		}
	
	// XML Tags
	private String Name = "name";
	private String Desc = "description";
	private String Class = "classname";
	private String PluginVers = "pluginVersion";
	private String CytoVers = "cytoscapeVersion";
	private String Url = "url";
	private String ProjUrl = "projectUrl";
	private String Category = "category";
	private String FileList = "filelist";
	private String File = "file";
	private String PluginList = "pluginlist";
	private String Plugin = "plugin";
	private String AuthorList = "authorlist";
	private String Author = "author";
	private String Inst = "institution";
	}
