/*
 File: PluginInfo.java 
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

import cytoscape.util.URLUtil;

import java.util.ArrayList;
import java.util.List;

import java.net.URL;

/**
 * Object describes a plugin
 */
public class PluginInfo extends DownloadableInfo {
	/**
	 * Jar and Zip files currently supported
	 * 
	 * @author skillcoy
	 * 
	 */
	public enum FileType {
		JAR("jar"), ZIP("zip");

		private String typeText;

		private FileType(String type) {
			typeText = type;
		}

		public String toString() {
			return typeText;
		}
	}

	private FileType fileType;

	private String pluginClassName;

	private List<AuthorInfo> authors;

	private String projectUrl;

	private List<String> pluginFiles;

	protected String enclosingJar;
	
	protected String installLocation;
	
	
	/**
	 * See {@link DownloadableInfo#DownloadableInfo()}
	 * 
	 * Initializes a PluginInfo object with the following defaults:
	 * setName("Unknown"); setDescription("No description");
	 * setObjectVersion("0.1"); setCytoscapeVersion(
	 * cytoscape.cytoscapeVersion.version ); setCategory("Uncategorized");
	 * 
	 */
	public PluginInfo() {
		init();
	}

	/**
	 * See {@link DownloadableInfo#DownloadableInfo(String)}
	 * 
	 * @param UniqueID
	 *            Additionally this sets the unique identifier that will be used
	 *            to find a new version of the plugin at the given download url.
	 */
	public PluginInfo(String UniqueID) {
		super(UniqueID);
		init();
	}

	/**
	 * See {@link DownloadableInfo#DownloadableInfo(String)}
	 * 
	 * @param UniqueID
	 * @param ParentObj
	 *            Additionally this sets the unique identifier that will be used
	 *            to find a new version of the plugin at the given download url and
	 *            sets the parent downloadable object.
	 */
	public PluginInfo(String UniqueID, DownloadableInfo ParentObj) {
		super(UniqueID, ParentObj);
		init();
	}
		
	/*
	 * Sets all the fields that are required to a default value in case it is
	 * not called
	 */
	private void init() {
		pluginFiles = new ArrayList<String>();
		authors = new ArrayList<AuthorInfo>();
		setName("Unknown");
		setDescription("No description");
		setObjectVersion(0.1);
		setCategory(Category.NONE);
		setPluginClassName("");
	}

	/**
	 * Sets the plugin class name. Used for tracking plugins.
	 * 
	 * @param className
	 */
	public void setPluginClassName(String className) {
		pluginClassName = className;
	}

	/**
	 * @deprecated See {@link DownloadableInfo#setObjectVersion(double)}} will be removed June 2008
	 * 
	 * Sets the version of the plugin. Defaults to 0.1
	 * 
	 * @param version
	 * 				String version with format \d+.\d+	
	 * @throws NumberFormatException
	 * 				If the string version is of a format other than \d+.\d+
	 */
	public void setPluginVersion(double Version) throws NumberFormatException {
		this.setObjectVersion(Version);
	}

	/**
	 * @deprecated See {@link DownloadableInfo#setDownloadableURL(String)}} will be removed June 2008
	 * pluginUrl this plugin was downloaded from. It is presumed this can be
	 * used for update later.
	 * @param url
	 */
	protected void setUrl(String url) {
		this.setObjectUrl(url);
	}

	/**
	 * Sets the url of a site describing this plugin project
	 * @param url
	 */
	public void setProjectUrl(String url) {
		projectUrl = url;
	}

	/**
	 * @deprecated See {@link DownloadableInfo#setObjectUrl(String)}} will be removed June 2008
	 * URL for the xml file describing all plugins from any given project
	 * (ex. http://cytoscape.org/plugins/plugin.xml)
	 * 
	 * @param url
	 */
	protected void setDownloadUrl(String url) {
		this.setDownloadableURL(url);
	}
	
	/**
	 * Jar or Zip are currently supported. Use PluginInfo.JAR or PluginInfo.ZIP.
	 * This will only be set by the PluginManager generally and can only be set once 
	 * as an object's file type will not change.
	 * @param type
	 */
	protected void setFiletype(FileType type) {
		if (fileType == null)
			fileType = type;
	}

	/**
	 * Sets a list of files (prefer full paths) installed with this plugin.
	 * Includes the jar file.
	 * @param list
	 */
	protected void setFileList(List<String> list) {
		pluginFiles = list;
	}

	/**
	 * Adds a file to the list of installed files.
	 * 
	 * @param fileName
	 */
	protected void addFileName(String fileName) {
		if (!pluginFiles.contains(fileName))
			pluginFiles.add(fileName);
	}

	/**
	 * Adds an author to the list of authors.
	 * 
	 * @param authorName
	 * @param institution
	 */
	public void addAuthor(String authorName, String institution) {
		authors.add(new AuthorInfo(authorName, institution));
	}

	/**
	 * Clears author list.
	 */
	public void clearAuthorList() {
		authors.clear();
	}
	
	/**
	 * @deprecated See {@link DownloadableInfo#setLicense(String)} will be removed June 2008
	 * Sets the license information for the plugin. Not required.
	 * 
	 * @param java.net.URL
	 *            object where license can be downloaded from.
	 */
	public void setLicense(URL url) {
		super.setLicense(url);
	}

	/**
	 * @deprecated See {@link DownloadableInfo#setLicense(String, boolean)} will be removed June 2008
	 * Sets the license information for the plugin. Not required.
	 * 
	 * @param Text
	 *            string of license.
	 * @param alwaysRequired
	 *            If the user expects the license to be required for both
	 *            install and update at all times (true) or only at install
	 *            (false)
	 */
	public void setLicense(String licenseText, boolean alwaysRequired) {
		super.setLicense(licenseText, alwaysRequired);
	}


	/**
	 * This is meant to only get set by the PluginManager.  It can only
	 * be set once as the install location can't move.
	 * 
	 * @param Loc
	 */
	protected void setInstallLocation(String Loc) {
		if (installLocation == null)
			installLocation = Loc;
	}
	
	/* GET */
	/**
	 * Gets the full install path for this plugin.  Should look like
	 * <HOME_DIR>/.cytoscape/<cytoscape_version>/plugins/<pluginName-pluginVersion>
	 */
	public java.io.File getPluginDirectory() {
		 java.io.File PluginDir = new java.io.File(
				 PluginManager.getPluginManager().getPluginManageDirectory(),
				 this.getName()+"-"+this.getObjectVersion());
		return PluginDir;
	}

	/**
	 * @return String of the installation location for the plugin and all of it's files.
	 * 		Generally this is .cytoscape/[cytoscape version]/plugins/PluginName-version
	 */
	public String getInstallLocation() {
		return installLocation;
	}
	
	/**
	 * @deprecated See {@link cytoscape.plugin.DownloadableInfo#getLicenseText()} will be removed June 2008
	 * @return The text of the license for this plugin if available.
	 */
	public String getLicenseText() {
		return super.getLicenseText();
	}

	/**
	 * @deprecated See {@link cytoscape.plugin.DownloadableInfo#isLicenseRequired()} will be removed June 2008
	 * @return If the license is always required to be accepted for installs and
	 *         updates this returns true. If it only is required at install time
	 *         (never at update) returns false.
	 */
	public boolean isLicenseRequired() {
		return super.isLicenseRequired();
	}

	/**
	 * @return FileType of file type for plugin. PluginInfo.JAR or
	 *         PluginInfo.ZIP
	 */
	public FileType getFileType() {
		return fileType;
	}

	/**
	 * @return Java class name
	 */
	public String getPluginClassName() {
		return pluginClassName;
	}

	/**
	 * @return List of authors.
	 */
	public List<AuthorInfo> getAuthors() {
		return authors;
	}

	/**
	 * @deprecated See {@link DownloadableInfo#getObjectVersion()} will be removed June 2008
	 * @return Plugin version.
	 */
	public String getPluginVersion() {
		return super.getObjectVersion();
	}

	/**
	 * @deprecated See {@link DownloadableInfo#getObjectUrl()}} will be removed June 2008
	 * @return Url to download plugin from
	 */
	public String getUrl() {
		return super.getObjectUrl();
	}

	/**
	 * 
	 * @return Url that points to a site describing this plugin project
	 */
	public String getProjectUrl() {
		return projectUrl;
	}

	/**
	 * @deprecated See {@link DownloadableInfo#getDownloadableURL()}} will be removed June 2008
	 * @return Url that returns the document of available plugins this plugin
	 *         came from.  Example http://cytoscape.org/plugins/all_plugins.xml
	 */
	public String getDownloadUrl()  {
		return super.getDownloadableURL();
	}
	
	/**
	 * {@link DownloadableInfo#getType()}
	 */
	public DownloadableType getType()
		{
		return DownloadableType.PLUGIN;
		}
	
	/**
	 * @return List of files installed with this plugin (includes plugin jar
	 *         file).
	 */
	public List<String> getFileList() {
		return pluginFiles;
	}


	// wonder if this will overwrite the Object.equals method...
	public boolean equals(Object Obj) {
		PluginInfo obj = (PluginInfo) Obj;
		if (this.getID().equals(obj.getID()) &&
			this.getDownloadableURL().equals(obj.getDownloadableURL()) &&
			this.getObjectVersion().equals(obj.getObjectVersion()))
			return true;
		
		return false;
	}
	
	/**
	 * @deprecated See {@link DownloadableInfo#isNewerObjectVersion(DownloadableInfo)}} will be removed June 2008
	 * Compare the version of the object to the given object.
	 * 
	 * @param New
	 *            Potentially newer PluginInfo object
	 * @return true if given version is newer
	 */
	public boolean isNewerPluginVersion(PluginInfo New) {
		return super.isNewerObjectVersion(New);
	}

	public String htmlOutput() {
		
		String Html = this.basicHtmlOutput();
		Html += "<b>Released By:</b><br><ul>";
		for (AuthorInfo ai : getAuthors()) {
			Html += "<li>" + ai.getAuthor() + ", " + ai.getInstitution()
					+ "<br>";
		}
		Html += "</ul>";

		Html += "</font></body></html>";
		return Html;
	}

	/**
	 * Describes an author for a given plugin.
	 * 
	 */
	public class AuthorInfo {
		private String authorName;

		private String institutionName;

		public AuthorInfo(String Name, String Institution) {
			authorName = Name;
			institutionName = Institution;
		}

		public String getAuthor() {
			return authorName;
		}

		public String getInstitution() {
			return institutionName;
		}
	}


}
