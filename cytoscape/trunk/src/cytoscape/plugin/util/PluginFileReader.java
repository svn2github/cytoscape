/**
 * 
 */
package cytoscape.plugin.util;

import cytoscape.plugin.PluginInfo;

import org.jdom.*;
import org.jdom.input.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author skillcoy
 *
 */
public class PluginFileReader
	{
	private Document Doc;
	
	public PluginFileReader(String Url) throws java.io.IOException
		{
		try
			{
			SAXBuilder Builder = new SAXBuilder(false); // would be nice to validate later
			Doc = Builder.build( new java.net.URL(Url) );
			}
		catch (JDOMException E)
			{ E.printStackTrace(); }
		}
	
	public String getProjectName()
		{
		return Doc.getRootElement().getChild("name").getTextTrim();
		}
	
	public String getProjectDescriptoin()
		{ 
		return Doc.getRootElement().getChild("description").getTextTrim();
		}
	
	public String getProjectUrl()
		{
		return Doc.getRootElement().getChild("url").getTextTrim();
		}

	public List<PluginInfo> getPlugins()
		{
		List<PluginInfo> Plugins = new ArrayList<PluginInfo>();
		
		Iterator<Element> pluginI = Doc.getRootElement().getChildren("plugin").iterator();
		while(pluginI.hasNext())
			{
			Element CurrentPlugin = pluginI.next();

			PluginInfo Info = new PluginInfo();
			Info.setName( CurrentPlugin.getChild("name").getTextTrim()	);
			Info.setDescription( CurrentPlugin.getChild("description").getTextTrim() );
			Info.setPluginVersion( CurrentPlugin.getChild("pluginVersion").getTextTrim() );
			Info.setCytoscapeVersion( CurrentPlugin.getChild("cytoscapeVersion").getTextTrim() );
			Info.setUrl( CurrentPlugin.getChild("url").getTextTrim() );
			Info.setProjectUrl( getProjectUrl() );
			Info.setAuthor( CurrentPlugin.getChild("author").getTextTrim() );
			Info.setInstitution( CurrentPlugin.getChild("institution").getTextTrim() );
			
			String Type = CurrentPlugin.getChild("filetype").getTextTrim();
			if (Type.equalsIgnoreCase("jar"))
				{
				Info.setFiletype( PluginInfo.JAR );
				}
			else if (Type.equalsIgnoreCase("zip"))
				{
				Info.setFiletype( PluginInfo.ZIP );
				}
			else
				{
				// unknown type error and move on
				System.err.println("Unknown plugin file type '" + Type + " skipping");
				continue;
				}
			Plugins.add(Info);
			}
		
		return Plugins;
		}
	
	
	}
