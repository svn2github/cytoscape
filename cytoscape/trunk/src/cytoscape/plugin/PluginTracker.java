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
import java.util.Iterator;
import java.util.List;


/*
 * TODO The xml reading that occurs in the tracker is nearly identical to what
 * PluginFileReader is doing. Would be nice to merge them.
 */

/**
 * @author skillcoy Tracks all installed plugins and the files required for
 *         each. Writes out a file when cytoscape is closed describing installed
 *         plugins, reads in at load time (check that plugins are still there?)
 *
 * Tracker is meant to only be used by the PluginManager
 */
public class PluginTracker {
	private Document trackerDoc;
	private File installFile;
	private HashMap<String, PluginInfo> installedPlugins;
	private HashMap<String, Element> deletedPlugins;
	private final String INSTALL_FILE_NAME = "plugins_installed.xml";

	// XML Tags to prevent misspelling issues, PluginFileReader uses the same
	// tags the xml needs to stay consistent
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
	 * Read xml document in, create info objects from entries, add to hash
	 * Should only get read it once.
	 */
	private void readDoc() {
		// Iterator<Element> pI = trackerDoc.getRootElement()
		// .getChild(this.pluginListTag).getChildren(this.pluginTag).iterator();
		List<Element> AllPlugins = trackerDoc.getRootElement().getChild(this.pluginListTag)
		                                     .getChildren(this.pluginTag);

		for (Element Plugin : AllPlugins)// while (pI.hasNext())
		 {
			// Element Plugin = pI.next();

			// skip deleted plugins
			if (Plugin.getChild("deleted") != null) {
				/*
				 * Could be useful to track the xml element of deleted plugins
				 * in case the exact same plugin is reinstalled. Currently not
				 * doing anything with it though.
				 */

				// deletedPlugins.put(Plugin.getChildTextTrim(this.classTag),
				// Plugin);
				continue;
			}

			PluginInfo Info = new PluginInfo(Plugin.getChildText(this.uniqueIdTag));
			Info.setName(Plugin.getChildTextTrim(this.nameTag));
			Info.setDescription(Plugin.getChildTextTrim(this.descTag));
			Info.setPluginClassName(Plugin.getChildTextTrim(this.classTag));
			Info.setPluginVersion(Plugin.getChildTextTrim(this.pluginVersTag));
			Info.setCytoscapeVersion(Plugin.getChildTextTrim(this.cytoVersTag));
			Info.setCategory(Plugin.getChildTextTrim(this.categoryTag));
			Info.setUrl(Plugin.getChildTextTrim(this.urlTag));
			Info.setProjectUrl(Plugin.getChildTextTrim(this.projUrlTag));

			// Iterator<Element> fI =
			// Plugin.getChild(this.fileListTag).getChildren(
			// this.fileTag).iterator();
			// while (fI.hasNext())
			// Info.addFileName(fI.next().getTextTrim());
			List<Element> Files = Plugin.getChild(this.fileListTag).getChildren(this.fileTag);

			for (Element File : Files) {
				Info.addFileName(File.getTextTrim());
			}

			// Iterator<Element> aI =
			// Plugin.getChild(this.authorListTag).getChildren(
			// this.authorTag).iterator();
			List<Element> Authors = Plugin.getChild(this.authorListTag).getChildren(this.authorTag);

			for (Element Author : Authors)// while (aI.hasNext())
			 {
				// Element Author = aI.next();
				Info.addAuthor(Author.getChildTextTrim(this.nameTag),
				               Author.getChildTextTrim(this.instTag));
			}

			installedPlugins.put(Info.getPluginClassName(), Info);
		}
	}

	/*
	 * 1. Object has already been added to the set then it will also be in the
	 * xml file 2. Object is not in the set, add to set and the xml file 3.
	 * Object was previously added but needs to be changed, remove from set and
	 * xml file, reinsert (as in an update has occurred)
	 */
	protected void addInstalledPlugin(PluginInfo obj, boolean Overwrite) {
		if (!Overwrite && installedPlugins.containsKey(obj.getPluginClassName()))
			return;

		Element Plugin = new Element(this.pluginTag);

		if (Overwrite && installedPlugins.containsKey(obj.getPluginClassName())) { // replace
			installedPlugins.remove(obj.getPluginClassName());
			CurrentPlugins.remove(obj.getPluginClassName()); // shouldn't be
			                                                 // in here
			                                                 // but could be

			Plugin = this.getMatchingPlugin(obj);
			// Plugin.removeContent(); // jdom 1.0
			this.removeChildren(Plugin);
		}

		CurrentPlugins.add(obj.getPluginClassName());
		installedPlugins.put(obj.getPluginClassName(), obj);

		Plugin.addContent(new Element(this.uniqueIdTag).setText(obj.getID()));
		Plugin.addContent(new Element(this.nameTag).setText(obj.getName()));
		Plugin.addContent(new Element(this.classTag).setText(obj.getPluginClassName()));
		Plugin.addContent(new Element(this.descTag).setText(obj.getDescription()));
		Plugin.addContent(new Element(this.pluginVersTag).setText(obj.getPluginVersion()));
		Plugin.addContent(new Element(this.cytoVersTag).setText(obj.getCytoscapeVersion()));
		Plugin.addContent(new Element(this.urlTag).setText(obj.getUrl()));
		Plugin.addContent(new Element(this.projUrlTag).setText(obj.getProjectUrl()));
		Plugin.addContent(new Element(this.categoryTag).setText(obj.getCategory()));

		Element AuthorList = new Element(this.authorListTag);

		// Iterator<cytoscape.plugin.PluginInfo.AuthorInfo> aI =
		// obj.getAuthors().iterator();
		List<AuthorInfo> AllAuthors = obj.getAuthors();

		for (AuthorInfo CurrentAuthor : AllAuthors)// while (aI.hasNext())
		 {
			// AuthorInfo CurrentAuthor = aI.next();
			Element Author = new Element(this.authorTag);
			Author.addContent(new Element(this.nameTag).setText(CurrentAuthor.getAuthor()));
			Author.addContent(new Element(this.instTag).setText(CurrentAuthor.getInstitution()));
			AuthorList.addContent(Author);
		}

		Plugin.addContent(AuthorList);

		Element fileListTag = new Element(this.fileListTag);

		// Iterator<String> fileI = obj.getFileList().iterator();
		// while (fileI.hasNext())
		// fileListTag.addContent(new
		// Element(this.fileTag).setText(fileI.next()));
		for (String FileName : obj.getFileList()) {
			fileListTag.addContent(new Element(this.fileTag).setText(FileName));
		}

		Plugin.addContent(fileListTag);

		trackerDoc.getRootElement().getChild(this.pluginListTag).addContent(Plugin);
		write();
	}

	/**
	 * Removes a plugin from the list/xml of installed plugins
	 *
	 * @param obj
	 */
	protected void removePlugin(PluginInfo obj) {
		System.out.println("------- Deleting " + obj.getName());
		installedPlugins.remove(obj.getPluginClassName());

		// Iterator<Element> pI = trackerDoc.getRootElement()
		// .getChild(this.pluginListTag).getChildren(this.pluginTag).iterator();
		List<Element> Plugins = trackerDoc.getRootElement().getChild(this.pluginListTag)
		                                  .getChildren(this.pluginTag);

		for (Element Plugin : Plugins)// while (pI.hasNext())
		 {
			// Element Plugin = pI.next();
			if (Plugin.getChild(this.classTag).getTextTrim().equals(obj.getPluginClassName())) {
				Plugin.addContent(new Element("deleted"));
			}
		}

		write();
	}

	/**
	 *
	 * @return List of PluginInfo objects for installed plugins
	 */
	protected Collection<PluginInfo> getInstalledPlugins() {
		return installedPlugins.values();
	}

	/**
	 * Writes doc to file
	 */
	protected void write() {
		XMLOutputter out = new XMLOutputter("  ", true);
		out.setTrimAllWhite(true);

		// System.err.println( out.outputString(trackerDoc) );
		try {
			out.output(trackerDoc, new FileWriter(this.installFile));
		} catch (java.io.IOException E) {
			E.printStackTrace();
		}
	}

	/*
	 * Exactly what it shoulds like
	 */
	private Element getMatchingPlugin(PluginInfo obj) {
		// Iterator<Element> pI = trackerDoc.getRootElement()
		// .getChild(this.pluginListTag).getChildren(this.pluginTag).iterator();
		List<Element> Plugins = trackerDoc.getRootElement().getChild(this.pluginListTag)
		                                  .getChildren(this.pluginTag);

		for (Element Plugin : Plugins)// while (pI.hasNext())
		 {
			// Element Plugin = pI.next();
			if (Plugin.getChildTextTrim(this.classTag).equals(obj.getPluginClassName()))
				return Plugin;
		}

		return null;
	}

	private void removeChildren(Element xmlElement) {
		List<Element> Children = xmlElement.getChildren();

		for (Element Child : Children) {
			Child.detach();
		}
	}
}
