/**
 * 
 */
package cytoscape.plugin;

import java.util.List;
import java.util.ArrayList;


/**
 * @author skillcoy Object describes a plugin
 */
public class PluginInfo
	{
	public static final int JAR = 1;
	public static final int ZIP = 2;
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

	public PluginInfo()
		{
		PluginFiles = new ArrayList<String>();
		Authors = new ArrayList<AuthorInfo>();
		}

	// TODO These maybe should check to be sure nothing is being set to null
	/* SET */
	public void setName(String arg)
		{
		this.Name = arg;
		}

	public void setDescription(String arg)
		{
		this.Description = arg;
		}

	public void setPluginVersion(String arg)
		{
		this.PluginVersion = arg;
		}

	public void setCytoscapeVersion(String arg)
		{
		this.CytoscapeVersion = arg;
		}

	public void setUrl(String arg)
		{
		this.Url = arg;
		}

	public void setProjectUrl(String arg)
		{
		this.ProjectUrl = arg;
		}

	public void setFiletype(int type)
		{
		this.FileType = type;
		}

	public void setFileList(List<String> list)
		{
		this.PluginFiles = list;
		}

	public void setCategory(String arg)
		{
		this.Category = arg;
		}
	
	public void addFileName(String arg)
		{
		this.PluginFiles.add(arg);
		}

	public void addAuthor(String Name, String Institution)
		{
		Authors.add(new AuthorInfo(Name, Institution));
		}

	/* GET */
	public int getFileType()
		{
		return this.FileType;
		}

	public String getName()
		{
		return this.Name;
		}

	public List<AuthorInfo> getAuthors()
		{ 
		return this.Authors;
		}
	
	public String getDescription()
		{
		return this.Description;
		}

	public String getPluginVersion()
		{
		return this.PluginVersion;
		}

	public String getCytoscapeVersion()
		{
		return this.CytoscapeVersion;
		}

	/**
	 * @return Url to download plugin from
	 */
	public String getUrl()
		{
		return this.Url;
		}

	/**
	 * @return Url that returns the document of available plugins this plugin came
	 *         from
	 */
	public String getProjectUrl()
		{
		return this.ProjectUrl;
		}
	
	public String getCategory()
		{
		return this.Category;
		}

	public List<String> getFileList()
		{
		return this.PluginFiles;
		}

	public String toString()
		{ return getName(); }
	
	public class AuthorInfo
		{
		private String AuthName;
		private String InstName;

		public AuthorInfo(String Name, String Institution)
			{
			this.AuthName = Name;
			this.InstName = Institution;
			}

		public String getAuthor()
			{ return this.AuthName; }

		public String getInstitution()
			{ return this.InstName; }
		}
	}
