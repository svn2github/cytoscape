/**
 * 
 */
package cytoscape.plugin;

public abstract class DownloadableInfo
	{
	protected String versionMatch = "^\\d+\\.\\d+";

	protected String versionSplit = "\\.";

	private String releaseDate;

	private String uniqueID;

	private String name;

	private String description;

	private String objVersion;

	private String cytoscapeVersion;

	private String downloadURL;

	protected String installLocation;

	public DownloadableInfo()
		{
		}
	
	public DownloadableInfo(String ID)
		{
		this.uniqueID = ID;
		}

	/* --- SET --- */
	/**
   * @param Category
   *          Sets the category of the downloadable object.
   */
	public abstract void setCategory(String Category);

	/**
   * @param name
   *          Sets the name of the downloadable object.
   */
	public void setName(String Name)
		{
		this.name = Name;
		}

	/**
   * @param description
   *          Sets the descriptoin of the downloadable object.
   */
	public void setDescription(String Description)
		{
		this.description = Description;
		}

	/**
   * @param url
   *          Sets the URL for the xml file describing all downloadable objects
   *          from any given project. (ex.
   *          http://cytoscape.org/plugins/plugin.xml)
   */
	protected void setDownloadableURL(String url)
		{
		this.downloadURL = url;
		}

	/**
   * @param cyVersion
   *          Sets the Cytoscape version this object is compatible with.
   */
	public void setCytoscapeVersion(String cyVersion) throws NumberFormatException
		{
		if (versionOk(cyVersion, false))
			{
			this.cytoscapeVersion = cyVersion;
			}
		else
			{
			throw new NumberFormatException(
					"Cytoscape version numbers must be in the format: \\d+.\\d+  optional to add: .\\d+-[a-z]");
			}
		}

	/**
   * @param version
   *          Sets the version of this object.
   */
	public void setObjectVersion(double objVersion) throws NumberFormatException
		{
		String Version = Double.toString(objVersion);
		if (versionOk(Version, true))
			{
			this.objVersion = Version;
			}
		else
			{
			throw new NumberFormatException("Bad version '" + Version + "'." + this
					+ " version numbers must be in the format: \\d+.\\d+");
			}
		}

	/**
   * TODO - would probably be better to use a date object
   * 
   * @param date
   *          Sets the release date of this object.
   */
	public void setReleaseDate(String date)
		{
		this.releaseDate = date;
		}

	/* --- GET --- */
	public abstract String getCategory();

	/**
   * @return Url that returns the document of available downloadable objects
   *         this object came from. Example
   *         http://cytoscape.org/plugins/plugins.xml
   */
	public String getDownloadableURL()
		{
		return this.downloadURL;
		}

	/**
   * @return Version of the downloadable object.
   */
	public String getObjectVersion()
		{
		return this.objVersion;
		}

	/**
   * @return Name of the downloadable object.
   */
	public String getName()
		{
		return this.name;
		}

	/**
   * @return Description of the downloadable object.
   */
	public String getDescription()
		{
		return this.description;
		}

	/**
   * @return Compatible Cytocape version of this object.
   */
	public String getCytoscapeVersion()
		{
		return this.cytoscapeVersion;
		}

	/**
   * @return Release date for this object.
   */
	public String getReleaseDate()
		{
		return this.releaseDate;
		}

	/**
   * @return Unique identifier for the downloadable object.
   */
	public String getID()
		{
		return this.uniqueID;
		}

	/**
   * Compare the version of the object to the given object.
   * 
   * @param New
   *          Potentially newer DownloadableInfo object
   * @return true if given version is newer
   */
	public boolean isNewerObjectVersion(DownloadableInfo New)
		{
		String[] CurrentVersion = this.getObjectVersion().split(versionSplit);
		String[] NewVersion = New.getObjectVersion().split(versionSplit);

		int CurrentMajor = Integer.valueOf(CurrentVersion[0]).intValue();
		int NewMajor = Integer.valueOf(NewVersion[0]).intValue();

		int CurrentMinor = Integer.valueOf(CurrentVersion[1]).intValue();
		int NewMinor = Integer.valueOf(NewVersion[1]).intValue();

		if ((CurrentMajor > NewMajor || (CurrentMajor == NewMajor && CurrentMinor >= NewMinor))) { return false; }

		return true;
		}

	/**
   * @return true if the plugin is compatible with the current Cytoscape version
   *         major.minor (bugfix is only checked if the plugin specifies a
   *         bugfix version)
   */
	public boolean isCytoscapeVersionCurrent()
		{
		String[] CyVersion = cytoscape.CytoscapeVersion.version.split(versionSplit);
		String[] PlVersion = getCytoscapeVersion().split(versionSplit);

		for (int i = 0; i < PlVersion.length; i++)
			{
			if (Integer.valueOf(CyVersion[i]).intValue() != Integer.valueOf(
					PlVersion[i]).intValue()) return false;
			}
		return true;
		}

	// this just checks the downloadable object version and the cytoscape version
	protected boolean versionOk(String version, boolean downloadObj)
		{
		// \d+.\+d ok
		String Match = versionMatch;
		String Split = versionSplit;

		if (downloadObj)
			{
			Match = Match + "$";
			}
		else
			{ // cytoscape version
			Match = Match + "(\\.\\d+)?$";
			Split = "\\.|-";
			}

		if (!version.matches(Match)) { return false; }

		String[] SplitVersion = version.split(Split);

		int max = 2;
		if (!downloadObj)
			{
			max = 3; // cytoscape version numbers
			// if there's a fourth is must be alpha
			if (SplitVersion.length == 4)
				{
				if (!SplitVersion[3].matches("[a-z]+")) { return false; }
				}
			}

		// can't be longer than the accepted version types
		if (SplitVersion.length > max) { return false; }

		// must be digets
		for (int i = 0; i < max && i < SplitVersion.length; i++)
			{
			if (!SplitVersion[i].matches("\\d+")) { return false; }
			}

		return true;
		}

	}
