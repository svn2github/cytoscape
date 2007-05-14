/**
 *
 */
package cytoscape.plugin;

import cytoscape.util.ProxyHandler;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;

import org.jdom.input.SAXBuilder;

import cytoscape.util.URLUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import java.net.URL;

import java.io.InputStream;

/**
 * @author skillcoy
 * 
 */
public class PluginFileReader {
	private Document document;

	private String downloadUrl;

	/**
	 * Creates a new PluginFileReader object.
	 * 
	 * @param Url
	 *            DOCUMENT ME!
	 * 
	 */
	protected PluginFileReader(String Url) throws java.io.IOException,
			JDOMException {
		downloadUrl = Url;
		
		InputStream is = URLUtil.getInputStream( new URL(downloadUrl) );
		
		// would be nice to validate later
		SAXBuilder Builder = new SAXBuilder(false);
		document = Builder.build(is);
	}

	/**
	 * @return The global project name given by the xml document. NOT CURRENTLY
	 *         USED
	 */
	protected String getProjectName() {
		return document.getRootElement().getChild(nameTag).getTextTrim();
	}

	/**
	 * @return The global project description given by the xml document. NOT
	 *         CURRENTLY USED
	 */
	protected String getProjectDescription() {
		return document.getRootElement().getChild(descTag).getTextTrim();
	}

	/**
	 * @return The global project url given by the xml document. NOT CURRENTLY
	 *         USED
	 */
	protected String getProjectUrl() {
		return document.getRootElement().getChild(urlTag).getTextTrim();
	}

	/**
	 * This gets the PluginInfo objects as set up in the xml document.
	 * 
	 * @return The list of PluginInfo objects specified by the xml document.
	 */
	protected List<PluginInfo> getPlugins() {
		List<PluginInfo> Plugins = new ArrayList<PluginInfo>();

		Iterator<Element> pluginI = document.getRootElement().getChild(
				pluginListTag).getChildren(pluginTag).iterator();

		while (pluginI.hasNext()) {
			Element CurrentPlugin = pluginI.next();
			PluginInfo Info = createInfoObject(CurrentPlugin);
			if (Info == null) continue;
			Plugins.add(Info);
		}
		return Plugins;
	}

	/**
	 * Creates the PluginInfo object from the xml <plugin> element.
	 * This could be useful to the PluginTracker.
	 * 
	 * @param CurrentPlugin Element
	 * @return PluginInfo object
	 */
	protected PluginInfo createInfoObject(Element CurrentPlugin) {
		PluginInfo Info = new PluginInfo(CurrentPlugin
				.getChildTextTrim(uniqueID));
		Info.setName(CurrentPlugin.getChildTextTrim(nameTag));
		Info.setDescription(CurrentPlugin.getChildTextTrim(descTag));
		Info.setUrl(CurrentPlugin.getChildTextTrim(urlTag));
		Info.setProjectUrl(downloadUrl);
		Info.setCytoscapeVersion(CurrentPlugin.getChildTextTrim(cytoVersTag));
		Info.setInstallLocation(CurrentPlugin.getChildTextTrim(installLocTag));
		
		// category
		if (CurrentPlugin.getChild(categoryTag) != null) {
			Info.setCategory(CurrentPlugin.getChildTextTrim(categoryTag));
		} else {
			Info.setCategory(PluginInfo.Category.NONE);
		}

		// file type
		PluginInfo.FileType Type = getType(CurrentPlugin);
		if (Type == null) { // unknown type error and move on
			System.err.println("Unknown plugin file type '" + Type
					+ " skipping plugin " + Info.getName());
			return null;
		} else {
			Info.setFiletype(Type);
		}
		// authors
		Info = addAuthors(Info, CurrentPlugin);
		// license
		Info = addLicense(Info, CurrentPlugin);

		// plugin version
		Info = addVersion(Info, CurrentPlugin);

		// Cytoscape version
		if (Info != null && !Info.isCytoscapeVersionCurrent()) {
			Info = null;
		}
		
		
		return Info;
	}
	
	protected PluginInfo addVersion(PluginInfo obj, Element Plugin) {
		String Version = Plugin.getChildTextTrim(pluginVersTag);
		try 
			{
			obj.setPluginVersion(Double.valueOf(Version));
			return obj;
			}
		catch (NumberFormatException ie)
			{ // is there a better way to let people know it's a bad version?  This will just skip past bad version numbers
			ie.printStackTrace();
			return null;
			}
	}
	
	// get license text, add to info object
	protected static PluginInfo addLicense(PluginInfo obj, Element Plugin) {
		Element License = Plugin.getChild(licenseTag);
		
		if (License != null) {
			boolean RequireAlways = false;
			if (License.getChild("license_required") != null) {
				RequireAlways = true;
			}
			if (License.getChild(licenseText) != null) {
				obj.setLicense( License.getChildTextTrim(licenseText), RequireAlways );
			} else if (License.getChild(urlTag) != null) {
				try {
					String LicenseText = URLUtil.download( new URL(License.getChildTextTrim(urlTag)) );
					obj.setLicense(LicenseText, RequireAlways);
				} catch (Exception E) {
					E.printStackTrace();
				}
			}
		}
		return obj;
	}
	
	// get the authors, add to info object
	private PluginInfo addAuthors(PluginInfo obj, Element Plugin) {
		Iterator<Element> authI = Plugin.getChild(authorListTag).getChildren(authorTag).iterator();
		while (authI.hasNext()) {
			Element CurrentAuthor = authI.next();
			obj.addAuthor(CurrentAuthor.getChildTextTrim(nameTag),
					CurrentAuthor.getChildTextTrim(instTag));
		}
		return obj;
	}

	// get the type from the plugin element
	private PluginInfo.FileType getType(Element Plugin) {
		PluginInfo.FileType Type = null;

		String GivenType = Plugin.getChild(fileType).getTextTrim();

		if (GivenType.equalsIgnoreCase(PluginInfo.FileType.JAR.toString())) {
			Type = PluginInfo.FileType.JAR;
		} else if (GivenType.equalsIgnoreCase(PluginInfo.FileType.ZIP
				.toString())) {
			Type = PluginInfo.FileType.ZIP;
		}
		return Type;
	}

	// XML Tags PluginTracker uses the same tags
	protected static final String nameTag = "name";

	protected static final String descTag = "description";

	protected static final String classTag = "classname";

	protected static final String pluginVersTag = "pluginVersion";

	protected static final String cytoVersTag = "cytoscapeVersion";

	protected static final String urlTag = "url";

	protected static final String projUrlTag = "projectUrl";

	protected static final String categoryTag = "category";

	protected static final String fileListTag = "filelist";

	protected static final String fileTag = "file";

	protected static final String pluginListTag = "pluginlist";

	protected static final String pluginTag = "plugin";

	protected static final String authorListTag = "authorlist";

	protected static final String authorTag = "author";

	protected static final String instTag = "institution";

	protected static final String fileType = "filetype";

	protected static final String uniqueID = "uniqueID";

	protected static final String licenseTag = "license";

	protected static final String licenseText = "text";
	
	protected static final String installLocTag = "installLocation";
}
