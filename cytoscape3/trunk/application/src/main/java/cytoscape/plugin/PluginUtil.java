
package cytoscape.plugin;

import java.util.ArrayList;
import java.util.List;

public class PluginUtil {
	
	private PluginUtil() {};

	public static void loadPlugins(List<String> initPlugins) {

			PluginManager mgr = PluginManager.getPluginManager();
			try {
				System.out.println("updating plugins...");
				mgr.delete();
			} catch (ManagerException me) {
				me.printStackTrace();
			}
			
			mgr.install();

			System.out.println("loading plugins....");

			// TODO smart plugin loading. If there are multiple of the same
			// plugin (this will only work in the .cytoscape directory) load the
			// newest version first. Should be able to examine the directories
			// for this information. All installed plugins are named like
			// 'MyPlugin-1.0' currently this isn't necessary as old version are
			// not kept around
			List<String> installedPlugins = new ArrayList<String>();

			// load from those listed on the command line
			installedPlugins.addAll(initPlugins);

			// Get all directories where plugins have been installed
			// going to have to be a little smart...themes contain their plugins in subdirectories
			List<DownloadableInfo> mgrInstalledPlugins = mgr.getDownloadables(PluginStatus.CURRENT);

			for (DownloadableInfo dInfo : mgrInstalledPlugins) {
				if (dInfo.getCategory().equals(Category.CORE.getCategoryText()))
					continue;
					
				switch (dInfo.getType()) { // TODO get rid of switches
					case PLUGIN:
						installedPlugins.add(((PluginInfo) dInfo).getInstallLocation());

						break;
					case THEME:
						ThemeInfo tInfo = (ThemeInfo) dInfo;

						for (PluginInfo plugin : tInfo.getPlugins()) {
							installedPlugins.add(plugin.getInstallLocation());
						}

						break;
					}
				}

			// TODO this exception wasn't getting caught
			try {
				mgr.loadPlugins(installedPlugins);
			} catch ( Throwable mue ) {
				mue.printStackTrace();
			}

			List<Throwable> pluginLoadingErrors = mgr.getLoadingErrors();

			for (Throwable t : pluginLoadingErrors) {
				System.out.println("Caught this exception while loading plugins:");
				t.printStackTrace();
			}

			mgr.clearErrorList();
	 }
}
