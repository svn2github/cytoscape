/**
 * 
 */
package cytoscape.plugin;

import java.util.*;

import cytoscape.plugin.PluginInfo.AuthorInfo;

/**
 * @author skillcoy
 * 
 */
public class ThemeInfo extends DownloadableInfo {
	private Set<PluginInfo> themePlugins;

	/**
	 * See {@link DownloadableInfo#DownloadableInfo()}
	 * 
	 * Initializes a ThemeInfo object with the following defaults:
	 * setName("Unknown"); setDescription("No description");
	 * setObjectVersion("0.1"); setCytoscapeVersion(
	 * cytoscape.cytoscapeVersion.version ); setCategory("Theme");
	 */
	public ThemeInfo() {
		init();
	}

	/**
	 * See {@link DownloadableInfo#DownloadableInfo(String)}
	 * 
	 * @param UniqueID
	 *            Additionally this sets the unique identifier that will be used
	 *            to find a new version of the theme at the given download url.
	 */
	public ThemeInfo(String ID) {
		super(ID);
		this.setCategory("Theme");
		init();
	}

	private void init() {
		setName("Unknown");
		setDescription("No description");
		setObjectVersion(0.1);
		setCytoscapeVersion(cytoscape.CytoscapeVersion.version);
		setCategory(Category.THEME);
		themePlugins = new HashSet<PluginInfo>();
	}

	/**
	 * See {@link DownloadableInfo#getType()}
	 */
	public DownloadableType getType() {
		return DownloadableType.THEME;
	}

	public void replacePlugin(PluginInfo oldPlugin, PluginInfo newPlugin) {
		themePlugins.remove(oldPlugin);
		themePlugins.add(newPlugin);
	}
	
	/**
	 * @param plugin
	 *            Add a plugin object to this theme.
	 */
	public void addPlugin(PluginInfo plugin) {
		themePlugins.add(plugin);
	}

	/**
	 * @return All plugins that make up this theme.
	 */
	public List<PluginInfo> getPlugins() {
		return new ArrayList<PluginInfo>(themePlugins);
	}

	public void clearPluginList() {
		this.themePlugins.clear();
	}

}
