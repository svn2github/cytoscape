/*
 File: PluginTracker.java 
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

import cytoscape.CytoscapeInit;

import cytoscape.plugin.PluginInfo.AuthorInfo;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;

import org.jdom.input.SAXBuilder;

import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import java.io.File;
import java.io.FileWriter;

import java.util.*;


/**
 * @author skillcoy
 * 
 */
public class PluginTracker {
	private Document trackerDoc;
	private File installFile;

	/**
	 * Used for testing
	 * 
	 * @param FileName
	 *            Xml file name
	 * @param Dir
	 *            directory to to write xml file
	 * @throws java.io.IOException
	 */
	protected PluginTracker(File Dir, String FileName) throws java.io.IOException {
		installFile = new File(Dir, FileName);
		init();
	}
	
	protected PluginTracker(File file) throws java.io.IOException {
		installFile = file;
		init();
	}
	
	/*
	 * Used for tests.
	 */
	protected File getTrackerFile() {
		return installFile;
	}
	
	/*
	 * Sets up the xml doc for tracking.
	 */
	private void init() throws java.io.IOException {
		if (PluginManager.usingWebstartManager()) { // we don't want the old
													// webstart file
			installFile.delete();
		}
	
		if (installFile.exists() && installFile.length() > 0) {
			SAXBuilder Builder = new SAXBuilder(false);
			try {
				trackerDoc = Builder.build(installFile);
				write();
			} catch (JDOMException E) { // TODO do something with this error
				E.printStackTrace();
			}
		} else {
			trackerDoc = new Document();
			trackerDoc.setRootElement(new Element("CytoscapePlugin"));
			trackerDoc.getRootElement().addContent(new Element(PluginStatus.CURRENT.getTagName()));
			trackerDoc.getRootElement().addContent(new Element(PluginStatus.INSTALL.getTagName()));
			trackerDoc.getRootElement().addContent(new Element(PluginStatus.DELETE.getTagName()));
			write();
		}
	}

	/**
	 * @deprecated Use {@link PluginTracker#getPluginListByStatus(PluginStatus)}
	 * @param Status
	 * @return
	 */
	protected List<PluginInfo> getListByStatus(PluginStatus Status) {
		return getPluginContent(trackerDoc.getRootElement().getChild(Status.getTagName()));
	}
	
	/**
	 * Gets a list of plugins by their status. CURRENT: currently installed
	 * DELETED: to be deleted INSTALL: to be installed
	 * 
	 * @param Status
	 * @return List of PluginInfo objects
	 */
	protected List<PluginInfo> getPluginListByStatus(PluginStatus Status) {
		return getPluginContent(trackerDoc.getRootElement().getChild(Status.getTagName()));
	}
	
	/**
	 * Get the list of all downloadable object by their status.
	 * CURRENT: currently installed
	 * DELETED: to be deleted
	 * INSTALLED: to be installed
	 * 
	 * @param Status
	 * @return
	 */
	protected List<DownloadableInfo> getDownloadableListByStatus(PluginStatus Status) {
		return this.getDownloadableContent(trackerDoc.getRootElement().getChild(Status.getTagName()));
	}
	
	/**
	 * 
	 * Gets a list of themes by their status. CURRENT: currently installed
	 * DELETED: to be deleted INSTALL: to be installed
	 * 
	 * @param Status
	 * @return List of ThemeInfo objects
	 */
	protected List<ThemeInfo> getThemeListByStatus(PluginStatus Status) {
		return getThemeContent(trackerDoc.getRootElement().getChild(Status.getTagName()));
	}
	

	
	protected void addDownloadable(DownloadableInfo obj, PluginStatus Status) {
		Element Parent = trackerDoc.getRootElement().getChild(Status.getTagName());
		
		switch (obj.getType()) {
		case PLUGIN:
			addPlugin((PluginInfo) obj, Status);
			break;
		case THEME:
			addTheme((ThemeInfo) obj, Status);
			break;
		}
	}
	
	/**
	 * @deprecated
	 * Adds the given ThemeInof object to the list of themes sharing the given
	 * status.
	 * 
	 * @param obj
	 * @param Status
	 */
	private void addTheme(ThemeInfo obj, PluginStatus Status) {
		Element ThemeParent = trackerDoc.getRootElement().getChild(Status.getTagName());
		
		Element Theme = getMatchingInfoObj(obj, Status.getTagName());
		if (Theme != null) {
			// TODO update the theme element
		} else {
			ThemeParent.addContent(createThemeContent(obj));
			System.out.println("Adding theme " + obj.getName() + " status " + Status.name());
		}
		write();
	}
	
	
	/**
	 * @deprecated
	 * Adds the given PluginInfo object to the list of plugins sharing the given
	 * status.
	 * 
	 * @param obj
	 * @param Status
	 */
	protected void addPlugin(PluginInfo obj, PluginStatus Status) {
		Element PluginParent = trackerDoc.getRootElement().getChild(Status.getTagName());
		
		// Element Plugin = getMatchingPlugin(obj, Status.getTagName());
		Element Plugin = getMatchingInfoObj(obj, Status.getTagName());
		if (Plugin != null) {
			// update the element

			// add parent info, should be able to find it using the unique id
			// and version
//			if (obj.getParent() != null) {
//				if (Plugin.getChild(PluginXml.PARENT_OBJ.getTag()) == null) {
//					Plugin.addContent(createParent(obj));
//				} else {
//					Plugin.getChild(PluginXml.PARENT_OBJ.getTag()).getChild(PluginXml.PARENT_TYPE.getTag()).setText(obj.getParent().getType().value());
//					Plugin.getChild(PluginXml.PARENT_OBJ.getTag()).getChild(PluginXml.UNIQUE_ID.getTag()).setText(obj.getParent().getID());
//					Plugin.getChild(PluginXml.PARENT_OBJ.getTag()).getChild(PluginXml.VERSION.getTag()).setText(obj.getParent().getObjectVersion());
//				}
//			}
		
			if (!obj.getName().equals(obj.getPluginClassName())) {
				Plugin.getChild(nameTag).setText(obj.getName());
			}
			if (!obj.getCategory().equals(Category.NONE.getCategoryText())) {
				Plugin.getChild(categoryTag).setText(obj.getCategory());
			}
			
			Plugin.getChild(installLocTag).setText(obj.getInstallLocation());
			Plugin.getChild(descTag).setText(obj.getDescription());
			Plugin.getChild(pluginVersTag).setText(obj.getObjectVersion());
			Plugin.getChild(cytoVersTag).setText(obj.getCytoscapeVersion());
			
			if (obj.getPluginClassName() != null) {
				Plugin.getChild(classTag).setText(obj.getPluginClassName());
			}

			if (Plugin.getChild(PluginXml.RELEASE_DATE.getTag()) != null) {
				Plugin.getChild(PluginXml.RELEASE_DATE.getTag()).setText(obj.getReleaseDate());
			} else {
				Element ReleaseDate = new Element(PluginXml.RELEASE_DATE.getTag());
				Plugin.addContent( ReleaseDate.setText(obj.getReleaseDate()) );
			}
			
			
			Plugin.removeChild(authorListTag);
			Element Authors = new Element(authorListTag);
			for(AuthorInfo ai: obj.getAuthors()) {
				Element Author = new Element(authorTag);
				Author.addContent( new Element(nameTag).setText(ai.getAuthor()) );
				Author.addContent( new Element(instTag).setText(ai.getInstitution()) );
				Authors.addContent(Author);
			}
			Plugin.addContent(Authors);
		} else {
			PluginParent.addContent(createPluginContent(obj));
			System.out.println("Adding plugin " + obj.getName() + " status " + Status.name());
		}
		write();
	}

	/**
	 * @deprecated
	 * @param obj
	 * @param Status
	 */
	protected void removePlugin(PluginInfo obj, PluginStatus Status) {
		this.removeDownloadable(obj, Status);
	}
	
	/**
	 * Removes the given DownloadableInfo object from the list of plugins/themes sharing the
	 * given status.
	 * 
	 * @param obj
	 * @param Status
	 */
	protected void removeDownloadable(DownloadableInfo obj, PluginStatus Status) {
		Element Parent = trackerDoc.getRootElement().getChild(Status.getTagName());
		
		Element InfoObj = this.getMatchingInfoObj(obj, Status.getTagName());
		if (InfoObj != null) {
			Parent.removeContent(InfoObj);
			System.out.println("Removing plugin/theme " + obj.getName() + " status " + Status.name());
			write();
		}
	}
	

	/**
	 * Matches one of the following rule: 1. Plugin class name 2. uniqueID &&
	 * projUrl 3. Plugin specific Url (on the assumption that no two plugins can
	 * be downloaded from the same url)
	 * 
	 * @param Obj
	 * @param Tag
	 * @return
	 */
	protected Element getMatchingInfoObj(DownloadableInfo Obj, String Tag) {
		List<Element> InfoObjs = trackerDoc.getRootElement().getChild(Tag).getChildren();
		
		for (Element Current: InfoObjs) {
		if ( Current.getChildTextTrim(uniqueIdTag).equals(Obj.getID()) && 
				 Current.getChildTextTrim(downloadUrlTag).equals(Obj.getDownloadableURL()) )
			return Current;
		}
		return null;
	}
	
	/**
	 * Writes doc to file
	 */
	protected void write() {
		try {
			XMLOutputter out = new XMLOutputter(Format.getPrettyFormat());
			FileWriter Writer = new FileWriter(installFile);
			out.output(trackerDoc, Writer);
			out.outputString(trackerDoc);
			Writer.close();
		} catch (java.io.IOException E) {
			E.printStackTrace();
		}
	}

	
	public String toString() {
		XMLOutputter out = new XMLOutputter(Format.getPrettyFormat());
		return out.outputString(trackerDoc);
	}
	
	
	/**
	 * Deletes the tracker file. This is currently never used outside of tests.
	 */
	protected void delete() {
		installFile.delete();
	}
	
	/*
	 * Set up the object with all the basic fields filled in
	 */
	private DownloadableInfo createBasicObject(Element e, DownloadableType Type) {
		DownloadableInfo Info = null;
		switch (Type) {
		case PLUGIN:
			Info = new PluginInfo(e.getChildTextTrim(uniqueIdTag));
			break;
		case THEME:
			Info = new ThemeInfo(e.getChildTextTrim(uniqueIdTag));
			break;
		}
		
		Info.setName(e.getChildTextTrim(nameTag));
		Info.setDescription(e.getChildTextTrim(descTag));
		Info.setCytoscapeVersion(e.getChildTextTrim(cytoVersTag));
		Info.setCategory(e.getChildTextTrim(categoryTag));
		Info.setObjectUrl(e.getChildTextTrim(urlTag));
		Info.setDownloadableURL(e.getChildTextTrim(downloadUrlTag));
		Info.setReleaseDate(e.getChildTextTrim(PluginXml.RELEASE_DATE.getTag()));

		return Info;
	}
	
	
	// Not sure this is the right way to do this but for now...
	private List<ThemeInfo> getThemeContent(Element ThemeParentTag) {
		List<ThemeInfo> Content = new ArrayList<ThemeInfo>();
		
		List<Element> Themes = ThemeParentTag.getChildren(PluginXml.THEME.getTag());
		for (Element CurrentTheme: Themes) {
			ThemeInfo themeInfo = (ThemeInfo) createBasicObject(CurrentTheme, DownloadableType.THEME);
			themeInfo.setObjectVersion( Double.valueOf(CurrentTheme.getChildTextTrim(PluginXml.THEME_VERSION.getTag())) );
			// add plugins
			Iterator<Element> pluginI = CurrentTheme.getChild(PluginXml.PLUGIN_LIST.getTag()).getChildren(PluginXml.PLUGIN.getTag()).iterator();
			while (pluginI.hasNext()) {
				PluginInfo pluginInfo = createPluginObject(pluginI.next());
				pluginInfo.setParent(themeInfo);
			}
			Content.add(themeInfo);
		}
		return Content;
	}
	
	// TODO PluginFileReader does much the same stuff, need to merge the two (??
	// maybe)
	/*
	 * Takes a list of elemnts, creates the PluginInfo object for each and
	 * returns list of objects
	 */
	private List<PluginInfo> getPluginContent(Element PluginParentTag) {
		List<PluginInfo> Content = new ArrayList<PluginInfo>();

		List<Element> Plugins = PluginParentTag.getChildren(pluginTag);

		for (Element CurrentPlugin : Plugins) {
			PluginInfo Info = createPluginObject(CurrentPlugin);
			Content.add(Info);
		}
		return Content;
	}
	
	/**
	 * Gets all downloadable objects currently available.
	 * @param Parent
	 * @return
	 */
	private List<DownloadableInfo> getDownloadableContent(Element Parent) {
		List<DownloadableInfo> Content = new ArrayList<DownloadableInfo>();
		
		Content.addAll( getPluginContent(Parent) );
		Content.addAll( getThemeContent(Parent) );
		
		return Content;
	}
	
	

	/*
	 * Create the PluginInfo object from a <plugin>...</plugin> tree 
	 */
	private PluginInfo createPluginObject(Element PluginElement) {
    	PluginInfo Info = (PluginInfo) createBasicObject(PluginElement, DownloadableType.PLUGIN);
    	
    	Info.setPluginClassName(PluginElement.getChildTextTrim(classTag));
    	Info.setProjectUrl(PluginElement.getChildTextTrim(projUrlTag));
    	Info.setInstallLocation(PluginElement.getChildTextTrim(installLocTag));
		Info.setObjectVersion(Double.valueOf(PluginElement.getChildTextTrim(pluginVersTag)));

		// set file type
		String FileType = PluginElement.getChildTextTrim(fileTypeTag);
		if (FileType.equalsIgnoreCase(PluginInfo.FileType.JAR.toString())) {
			Info.setFiletype(PluginInfo.FileType.JAR);
		} else if (FileType.equalsIgnoreCase(PluginInfo.FileType.ZIP.toString())) {
			Info.setFiletype(PluginInfo.FileType.ZIP);
		}
		
		// add plugin files
		List<Element> Files = PluginElement.getChild(fileListTag).getChildren(fileTag);
		for (Element File : Files) {
			Info.addFileName(File.getTextTrim());
		}

		// add plugin authors
		List<Element> Authors = PluginElement.getChild(authorListTag)
		                                     .getChildren(authorTag);
		for (Element Author : Authors) {
			Info.addAuthor(Author.getChildTextTrim(nameTag),
			               Author.getChildTextTrim(instTag));
		}

		Info = PluginFileReader.addLicense(Info, PluginElement);
    	
		return Info;
	}

	
//	private Element createParent(DownloadableInfo obj) {
//		Element Parent = new Element(PluginXml.PARENT_OBJ.getTag());
//		Parent.addContent( new Element(PluginXml.PARENT_TYPE.getTag()).setText(obj.getParent().getType().value()) );
//		Parent.addContent( new Element(PluginXml.UNIQUE_ID.getTag()).setText(obj.getParent().getID()) );
//		Parent.addContent( new Element(PluginXml.VERSION.getTag()).setText(obj.getParent().getObjectVersion()));
//		return Parent;
//	}
	
	
	private Element createBasicContent(DownloadableInfo obj, Element e) {
		e.addContent(new Element(uniqueIdTag).setText(obj.getID()));
		e.addContent(new Element(nameTag).setText(obj.getName()));
		e.addContent(new Element(descTag).setText(obj.getDescription()));
		e.addContent(new Element(cytoVersTag).setText(obj.getCytoscapeVersion()));
		e.addContent(new Element(urlTag).setText(obj.getObjectUrl()));
		e.addContent(new Element(downloadUrlTag).setText(obj.getDownloadableURL()));
		e.addContent(new Element(categoryTag).setText(obj.getCategory()));
		e.addContent(new Element(PluginXml.RELEASE_DATE.getTag()).setText(obj.getReleaseDate()));
		
		return e;
	}
	
	private Element createThemeContent(ThemeInfo obj) {
		Element Theme = new Element(PluginXml.THEME.getTag());
		
		Theme = createBasicContent(obj, Theme);
		Theme.addContent(new Element(PluginXml.THEME_VERSION.getTag()).setText(obj.getObjectVersion()));
		
		Element PluginList = new Element(PluginXml.PLUGIN_LIST.getTag());
		for (PluginInfo plugin: obj.getPlugins()) {
			PluginList.addContent(createPluginContent(plugin));
		}
		Theme.addContent(PluginList);
		
		return Theme;
	}
	
	/*
	 * Create the plugin tag with all the appropriate tags for the PluginInfo
	 * object
	 */
	private Element createPluginContent(PluginInfo obj) {
		Element Plugin = new Element(pluginTag);

		Plugin = createBasicContent(obj, Plugin);
		
		Plugin.addContent(new Element(pluginVersTag).setText(obj.getObjectVersion()));
		Plugin.addContent(new Element(classTag).setText(obj.getPluginClassName()));
		Plugin.addContent(new Element(projUrlTag).setText(obj.getProjectUrl()));
		Plugin.addContent(new Element(fileTypeTag).setText(obj.getFileType().toString()));
		Plugin.addContent(new Element(installLocTag).setText(obj.getInstallLocation()));
		
		
		// license
		Element License = new Element(licenseTag);
		License.addContent( new Element("text").setText(obj.getLicenseText()) );
		Plugin.addContent(License);
		
		// authors
		Element AuthorList = new Element(authorListTag);
		for (AuthorInfo CurrentAuthor : obj.getAuthors()) {
			Element Author = new Element(authorTag);
			Author.addContent(new Element(nameTag).setText(CurrentAuthor.getAuthor()));
			Author.addContent(new Element(instTag).setText(CurrentAuthor.getInstitution()));
			AuthorList.addContent(Author);
		}
		Plugin.addContent(AuthorList);

		// files
		Element FileList = new Element(fileListTag);
		for (String FileName : obj.getFileList()) {
			FileList.addContent(new Element(fileTag).setText(FileName));
		}
		Plugin.addContent(FileList);

		return Plugin;
	}

	// XML Tags to prevent misspelling issues, PluginFileReader uses most of the
	// same tags, the xml needs to stay consistent
	private String cytoVersTag = "cytoscapeVersion";

	private String nameTag = PluginXml.NAME.getTag();

	private String descTag = PluginXml.DESCRIPTION.getTag();
	
	private String classTag = PluginXml.CLASS_NAME.getTag();
	
	private String pluginVersTag = PluginXml.PLUGIN_VERSION.getTag();
	
	private String urlTag = PluginXml.URL.getTag();
	
	private String projUrlTag = PluginXml.PROJECT_URL.getTag();
	
	private String downloadUrlTag = PluginXml.DOWNLOAD_URL.getTag();
	
	private String categoryTag = PluginXml.CATEGORY.getTag();
	
	private String fileListTag = PluginXml.FILE_LIST.getTag();
	
	private String fileTag = PluginXml.FILE.getTag();
	
	private String pluginListTag = PluginXml.PLUGIN_LIST.getTag();
	
	private String pluginTag = PluginXml.PLUGIN.getTag();
	
	private String authorListTag = PluginXml.AUTHOR_LIST.getTag();
	
	private String authorTag = PluginXml.AUTHOR.getTag();
	
	private String instTag = PluginXml.INSTITUTION.getTag();
	
	private String uniqueIdTag = PluginXml.UNIQUE_ID.getTag();
	
	private String fileTypeTag = PluginXml.FILE_TYPE.getTag();
	
	private String licenseTag = PluginXml.LICENSE.getTag();
	
	private String installLocTag = PluginXml.INSTALL_LOCATION.getTag();
}
