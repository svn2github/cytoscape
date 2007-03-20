/**
 *
 */
package cytoscape.plugin;

import cytoscape.CytoscapeInit;

import java.util.ArrayList;
import java.util.List;


/**
 * @author skillcoy Object describes a plugin
 */
public class PluginInfo {
	/**
	 * 
	 */
	public static final int JAR = 1;

	/**
	 * 
	 */
	public static final int ZIP = 2;
	private String PluginClassName;
	private String Name;
	private List<AuthorInfo> Authors;
	private String Description;
	private String PluginVersion;
	private String CytoscapeVersion;
	private String Url;
	private String ProjectUrl;
	private String Category;
	private List<String> PluginFiles;
	private int FileType;
	protected String EnclosingJar;

	/**
	 * Initializes a PluginInfo object with the following defaults:
	 *    setName("Unknown");
	 *    setDescription("No description");
	 *    setPluginVersion("0.1");
	 *    setCytoscapeVersion( cytoscape.CytoscapeVersion.version );
	 *    setCategory("Uncategorized");
	 *    setProjectUrl(CytoscapeInit.getProperties().getProperty("defaultPluginUrl"));
	 */
	public PluginInfo() {
		PluginFiles = new ArrayList<String>();
		Authors = new ArrayList<AuthorInfo>();
		init();
	}

	/*
	 * Sets all the fields that are required to a default value in case it is not called
	 */
	private void init() {
		setName("Unknown");
		setDescription("No description");
		setPluginVersion("0.1");
		setCytoscapeVersion(cytoscape.CytoscapeVersion.version);
		setCategory("Uncategorized");
		setProjectUrl(CytoscapeInit.getProperties().getProperty("defaultPluginUrl"));
	}

	// TODO These maybe should check to be sure nothing is being set to null
	/* SET */

	/**
	 * Sets name of plugin.  This will be displayed to users.
	 * @param arg
	 */
	public void setName(String arg) {
		this.Name = arg;
	}

	/**
	 * Sets the plugin class name.  Used for tracking plugins.
	 * @param arg
	 */
	public void setPluginClassName(String arg) {
		this.PluginClassName = arg;
	}

	/**
	 * Sets a description of the plugin.  This will be displayed to users.
	 * @param arg
	 */
	public void setDescription(String arg) {
		this.Description = arg;
	}

	/**
	 * Sets the version of the plugin.  Defaults to 0.1
	 * @param arg
	 */
	public void setPluginVersion(String arg) {
		this.PluginVersion = arg;
	}

	/**
	 * Sets the Cytoscape version this plugin is compatible with.
	 * @param arg
	 */
	public void setCytoscapeVersion(String arg) {
		this.CytoscapeVersion = arg;
	}

	/**
	 * Url this plugin was downloaded from.  It is presumed this can be used for update later.
	 * @param arg
	 */
	public void setUrl(String arg) {
		this.Url = arg;
	}

	/**
	 * Url for the xml file describing all plugins from any given project (ex. http://cytoscape.org/plugins/plugin.xml)
	 * @param arg
	 */
	public void setProjectUrl(String arg) {
		this.ProjectUrl = arg;
	}

	/**
	 * Jar or Zip are currently supported.  Use PluginInfo.JAR or PluginInfo.ZIP.
	 * @param type
	 */
	public void setFiletype(int type) {
		this.FileType = type;
	}

	/**
	 * Sets a list of files (prefer full paths) installed with this plugin.  Includes the jar file.
	 * @param list
	 */
	public void setFileList(List<String> list) {
		this.PluginFiles = list;
	}

	/**
	 * Category for the plugin.  Defaults to "Uncategorized"
	 * @param arg
	 */
	public void setCategory(String arg) {
		this.Category = arg;
	}

	/**
	 * Adds a file to the list of installed files.
	 * @param arg
	 */
	public void addFileName(String arg) {
		this.PluginFiles.add(arg);
	}

	/**
	 * Adds an author to the list of authors.
	 * @param Name
	 * @param Institution
	 */
	public void addAuthor(String Name, String Institution) {
		Authors.add(new AuthorInfo(Name, Institution));
	}

	/* GET */
	/**
	 * @return int of file type for plugin.  PluginInfo.JAR or PluginInfo.ZIP
	 */
	public int getFileType() {
		return this.FileType;
	}

	/**
	 * @return Name of plugin
	 */
	public String getName() {
		return this.Name;
	}

	/**
	 * @return Java class name
	 */
	public String getPluginClassName() {
		return this.PluginClassName;
	}

	/**
	 * @return List of authors.
	 */
	public List<AuthorInfo> getAuthors() {
		return this.Authors;
	}

	/**
	 * @return Plugin description.
	 */
	public String getDescription() {
		return this.Description;
	}

	/**
	 * @return Plugin version.
	 */
	public String getPluginVersion() {
		return this.PluginVersion;
	}

	/**
	 * @return Compatible Cytoscape version
	 */
	public String getCytoscapeVersion() {
		return this.CytoscapeVersion;
	}

	/**
	 * @return Url to download plugin from
	 */
	public String getUrl() {
		return this.Url;
	}

	/**
	 * @return Url that returns the document of available plugins this plugin came
	 *         from
	 */
	public String getProjectUrl() {
		return this.ProjectUrl;
	}

	/**
	 * @return Plugin category.
	 */
	public String getCategory() {
		return this.Category;
	}

	/**
	 * @return List of files installed with this plugin (includes plugin jar file).
	 */
	public List<String> getFileList() {
		return this.PluginFiles;
	}

	/**
	 * @return Returns plugin name ( getName() )
	 */
	public String toString() {
		return getName();
	}

	/**
	 * @return String of nice output for the information contained by the PluginInfo object.
	 */
	public String prettyOutput() {
		String Text = getName() + "\n\n";
		Text += ("Version: " + getPluginVersion() + "\n\n");
		Text += ("Category: " + getCategory() + "\n\n");

		// don't current have a release date, might be nice
		Text += (getDescription() + "\n\n");
		Text += "Released By: ";

		java.util.Iterator<AuthorInfo> aI = getAuthors().iterator();

		while (aI.hasNext()) {
			AuthorInfo Info = aI.next();

			if (Info.getAuthor() != null)
				Text += (Info.getAuthor() + ", ");

			Text += (Info.getInstitution() + "\n");
		}

		return Text;
	}

	/**
	 * Describes an author for a given plugin.
	 * @author skillcoy
	 */
	public class AuthorInfo {
		private String AuthName;
		private String InstName;

		public AuthorInfo(String Name, String Institution) {
			this.AuthName = Name;
			this.InstName = Institution;
		}

		public String getAuthor() {
			return this.AuthName;
		}

		public String getInstitution() {
			return this.InstName;
		}
	}
}
