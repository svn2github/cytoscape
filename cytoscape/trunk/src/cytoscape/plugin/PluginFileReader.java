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
		
//		InputStream is = URLUtil.getInputStream( new URL(downloadUrl) );
//		BufferedReader xsdReader = new BufferedReader( new InputStreamReader(PluginFileReader.class.getResourceAsStream("plugins.xsd")) );
//		String line = null;
//		String Xsd = "";
//		while ( (line = xsdReader.readLine()) != null)
//			Xsd += line;
//		
//		// validate
//		SAXBuilder Builder = new SAXBuilder("org.apache.xerces.parsers.SAXParser", true);
//		Builder.setFeature("http://apache.org/xml/features/validation/schema", true);
//    Builder.setProperty(
//        "http://apache.org/xml/properties/schema"
//        + "/external-noNamespaceSchemaLocation",
//        Xsd );
//		document = Builder.build(is);
		
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
		Info.setDownloadUrl(downloadUrl);
		Info.setProjectUrl(CurrentPlugin.getChildTextTrim(projUrlTag));
		Info.setInstallLocation(CurrentPlugin.getChildTextTrim(installLocTag));
		
		// category
		if (CurrentPlugin.getChild(categoryTag) != null) {
			Info.setCategory(CurrentPlugin.getChildTextTrim(categoryTag));
		} else {
			Info.setCategory(Category.NONE);
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

		Iterator<Element> versionI = CurrentPlugin.getChild(PluginXml.CYTOSCAPE_VERSIONS.getTag()).getChildren(PluginXml.CY_VERSRION.getTag()).iterator();
		while (versionI.hasNext()) {
			Element Version = versionI.next();
			Info.setCytoscapeVersion(Version.getTextTrim());

			if (Info != null && Info.isCytoscapeVersionCurrent()) {
				return Info;
			}
		}
		
		return null;
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
	private static String nameTag = PluginXml.NAME.getTag();

	private static String descTag = PluginXml.DESCRIPTION.getTag();

	private static String classTag = PluginXml.CLASS_NAME.getTag();

	private static String pluginVersTag = PluginXml.PLUGIN_VERSION.getTag();

	private static String cytoVersTag = "cytoscapeVersion";

	private static String urlTag = PluginXml.URL.getTag();

	private static String projUrlTag = PluginXml.PROJECT_URL.getTag();

	private static String downloadUrlTag = PluginXml.DOWNLOAD_URL.getTag();
	
	private static String categoryTag = PluginXml.CATEGORY.getTag();

	private static String fileListTag = PluginXml.FILE_LIST.getTag();

	private static String fileTag = PluginXml.FILE.getTag();

	private static String pluginListTag = PluginXml.PLUGIN_LIST.getTag();

	private static String pluginTag = PluginXml.PLUGIN.getTag();

	private static String authorListTag = PluginXml.AUTHOR_LIST.getTag();

	private static String authorTag = PluginXml.AUTHOR.getTag();

	private static String instTag = PluginXml.INSTITUTION.getTag();

	private static String fileType = PluginXml.FILE_TYPE.getTag();

	private static String uniqueID = PluginXml.UNIQUE_ID.getTag();

	private static String licenseTag = PluginXml.LICENSE.getTag();

	private static String licenseText = PluginXml.LICENSE_TEXT.getTag();
	
	private static String installLocTag = PluginXml.INSTALL_LOCATION.getTag();
}
