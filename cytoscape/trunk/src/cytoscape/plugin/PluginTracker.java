/**
 *
 */
package cytoscape.plugin;

import cytoscape.*;

import cytoscape.plugin.PluginInfo.AuthorInfo;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;

import org.jdom.input.SAXBuilder;

import org.jdom.output.XMLOutputter;

import java.io.File;
import java.io.FileWriter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;


/*
 * TODO The xml reading that occurs in the tracker is nearly identical to what
 * PluginFileReader is doing. Would be nice to merge them.
 */

/**
 * @author skillcoy Tracks all installed plugins and the files required for
 *         each. Writes out a file when cytoscape is closed describing installed
 *         plugins, reads in at load time (check that plugins are still there?)
 *         Tracker is meant to only be used by the PluginManager
 */
public class PluginTracker {
	private Document trackerDoc;
	private File installFile;
	private HashMap<String, PluginInfo> installedPlugins;
	private HashMap<String, PluginInfo> deletedPlugins;
	private final String INSTALL_FILE_NAME = "plugins_installed.xml";

	// XML Tags to prevent misspelling issues, PluginFileReader uses the same
	// tags, the xml needs to stay consistent
	private String nameTag = PluginFileReader.nameTag;
	private String descTag = PluginFileReader.descTag;
	private String classTag = PluginFileReader.classTag;
	private String pluginVersTag = PluginFileReader.pluginVersTag;
	private String cytoVersTag = PluginFileReader.cytoVersTag;
	private String urlTag = PluginFileReader.urlTag;
	private String projUrlTag = PluginFileReader.projUrlTag;
	private String categoryTag = PluginFileReader.categoryTag;
	private String fileListTag = PluginFileReader.fileListTag;
	private String fileTag = PluginFileReader.fileTag;
	private String pluginListTag = PluginFileReader.pluginListTag;
	private String pluginTag = PluginFileReader.pluginTag;
	private String authorListTag = PluginFileReader.authorListTag;
	private String authorTag = PluginFileReader.authorTag;
	private String instTag = PluginFileReader.instTag;
	private String uniqueIdTag = PluginFileReader.uniqueID;

	// lists plugins that have been registered, if any have been deleted they
	// will not be in this list and will be removed from the hash
	private List<String> CurrentPlugins;

	protected PluginTracker() throws java.io.IOException {
		CurrentPlugins = new ArrayList<String>();
		installedPlugins = new HashMap<String, PluginInfo>();
		deletedPlugins = new HashMap<String, PluginInfo>();

		installFile = new File(CytoscapeInit.getConfigDirectory(), this.INSTALL_FILE_NAME);

		if (installFile.exists()) {
			SAXBuilder Builder = new SAXBuilder(false);

			try {
				trackerDoc = Builder.build(new File(CytoscapeInit.getConfigDirectory(),
				                                    this.INSTALL_FILE_NAME));
				readDoc();
			} catch (JDOMException E) { // TODO do something with this error
				E.printStackTrace();
			}
		} else {
			trackerDoc = new Document();
			trackerDoc.setRootElement(new Element("CytoscapePlugin"));
			trackerDoc.getRootElement().addContent(new Element(this.pluginListTag));
		}
	}

	/*
	 * Read xml document in, create info objects from entries, add to hash Should
	 * only get read it once.
	 */
	private void readDoc() {
		List<Element> AllPlugins = trackerDoc.getRootElement().getChild(this.pluginListTag)
		                                     .getChildren(this.pluginTag);

		for (Element Plugin : AllPlugins) {
			PluginInfo Info = new PluginInfo(Plugin.getChildText(this.uniqueIdTag));
			Info.setName(Plugin.getChildTextTrim(this.nameTag));
			Info.setDescription(Plugin.getChildTextTrim(this.descTag));
			Info.setPluginClassName(Plugin.getChildTextTrim(this.classTag));
			Info.setPluginVersion(Plugin.getChildTextTrim(this.pluginVersTag));
			Info.setCytoscapeVersion(Plugin.getChildTextTrim(this.cytoVersTag));
			Info.setCategory(Plugin.getChildTextTrim(this.categoryTag));
			Info.setUrl(Plugin.getChildTextTrim(this.urlTag));
			Info.setProjectUrl(Plugin.getChildTextTrim(this.projUrlTag));

			List<Element> Files = Plugin.getChild(this.fileListTag).getChildren(this.fileTag);

			for (Element File : Files) {
				Info.addFileName(File.getTextTrim());
			}

			List<Element> Authors = Plugin.getChild(this.authorListTag).getChildren(this.authorTag);

			for (Element Author : Authors) {
				Info.addAuthor(Author.getChildTextTrim(this.nameTag),
				               Author.getChildTextTrim(this.instTag));
			}

			if (Plugin.getChild("deleted") != null)
				deletedPlugins.put(Info.getPluginClassName(), Info);
			else
				installedPlugins.put(Info.getPluginClassName(), Info);
		}
	}

	/*
	 * 1. Object has already been added to the set then it will also be in the xml
	 * file
	 * 2. Object is not in the set, add to set and the xml file
	 * 3. Object was
	 * previously added but needs to be changed, remove from set and xml file,
	 * reinsert (as in an update has occurred)
	 *
	 * TODO the 3 steps above are not actually implemented
	 */
	protected void addInstalledPlugin(PluginInfo obj, boolean Overwrite) {
		if (!Overwrite && installedPlugins.containsKey(obj.getPluginClassName()))
			return;

		//		Element Plugin = new Element(this.pluginTag);

		//		if (Overwrite && installedPlugins.containsKey(obj.getPluginClassName()))
		//			{ // replace
		//			installedPlugins.remove(obj.getPluginClassName());
		//			// shouldn't be in here but could be
		//			CurrentPlugins.remove(obj.getPluginClassName());
		//			Plugin = this.getMatchingPlugin(obj);
		//			Plugin.removeContent(); // jdom 1.0
		//			}
		CurrentPlugins.add(obj.getPluginClassName());
		installedPlugins.put(obj.getPluginClassName(), obj);

		Element Plugin = createPluginContent(obj);
		trackerDoc.getRootElement().getChild(pluginListTag).addContent(Plugin);
		write();
	}

	/**
	 * Removes a plugin from the list/xml of installed plugins
	 * @param obj
	 */
	protected void removePlugin(PluginInfo obj) {
		System.out.println("------- Deleting " + obj.getName());
		installedPlugins.remove(obj.getPluginClassName());

		List<Element> Plugins = trackerDoc.getRootElement().getChild(this.pluginListTag)
		                                  .getChildren(this.pluginTag);

		for (Element Plugin : Plugins) {
			if (Plugin.getChild(this.classTag).getTextTrim().equals(obj.getPluginClassName())) {
				Plugin.addContent(new Element("deleted"));
			}
		}

		write();
	}

	/**
	 * @return Collection of PluginInfo objects for installed plugins
	 */
	protected Collection<PluginInfo> getInstalledPlugins() {
		return installedPlugins.values();
	}

	/**
	 * @return Collection of PluginInfo objects for deleted plugins
	 */
	protected Collection<PluginInfo> getDeletedPlugins() {
		return deletedPlugins.values();
	}

	/**
	 * Writes doc to file
	 */
	protected void write() {
		try {
			XMLOutputter out = new XMLOutputter(org.jdom.output.Format.getPrettyFormat());

			// XMLOutputter out = new XMLOutputter(" ", true);
			// out.setTrimAllWhite(true);
			FileWriter Writer = new FileWriter(this.installFile);
			out.output(trackerDoc, Writer);
			Writer.close();
		} catch (java.io.IOException E) {
			E.printStackTrace();
		}
	}

	/*
	 * Exactly what it shoulds like
	 */
	private Element getMatchingPlugin(PluginInfo obj) {
		List<Element> Plugins = trackerDoc.getRootElement().getChild(this.pluginListTag)
		                                  .getChildren(this.pluginTag);

		for (Element Plugin : Plugins) {
			if (Plugin.getChildTextTrim(this.classTag).equals(obj.getPluginClassName()))
				return Plugin;
		}

		return null;
	}

	/*
	 * Create the plugin tag with all the appropriate tags for the PluginInfo object
	 */
	private Element createPluginContent(PluginInfo obj) {
		Element Plugin = new Element(pluginTag);

		Plugin.addContent(new Element(uniqueIdTag).setText(obj.getID()));
		Plugin.addContent(new Element(nameTag).setText(obj.getName()));
		Plugin.addContent(new Element(classTag).setText(obj.getPluginClassName()));
		Plugin.addContent(new Element(descTag).setText(obj.getDescription()));
		Plugin.addContent(new Element(pluginVersTag).setText(obj.getPluginVersion()));
		Plugin.addContent(new Element(cytoVersTag).setText(obj.getCytoscapeVersion()));
		Plugin.addContent(new Element(urlTag).setText(obj.getUrl()));
		Plugin.addContent(new Element(projUrlTag).setText(obj.getProjectUrl()));
		Plugin.addContent(new Element(categoryTag).setText(obj.getCategory()));

		Element AuthorList = new Element(authorListTag);

		List<AuthorInfo> AllAuthors = obj.getAuthors();

		for (AuthorInfo CurrentAuthor : AllAuthors) {
			Element Author = new Element(authorTag);
			Author.addContent(new Element(nameTag).setText(CurrentAuthor.getAuthor()));
			Author.addContent(new Element(instTag).setText(CurrentAuthor.getInstitution()));
			AuthorList.addContent(Author);
		}

		Plugin.addContent(AuthorList);

		Element FileList = new Element(fileListTag);

		for (String FileName : obj.getFileList()) {
			FileList.addContent(new Element(fileTag).setText(FileName));
		}

		Plugin.addContent(FileList);

		return Plugin;
	}
}
