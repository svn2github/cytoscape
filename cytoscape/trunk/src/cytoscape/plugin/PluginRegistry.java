/** Copyright (c) 2004 Institute for Systems Biology and the Whitehead Institute
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** any later version.
 **
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and the
 ** Institute for Systems Biology and the Whitehead Institute
 ** have no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall the
 ** Institute for Systems Biology and the Whitehead Institute
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if the
 ** Institute for Systems Biology and the Whitehead Institute
 ** have been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 **
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/
//-------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//-------------------------------------------------------------------------
package cytoscape.plugin;
//-------------------------------------------------------------------------
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Iterator;
//-------------------------------------------------------------------------
/**
 * User: sheridan
 * Date: Apr 5, 2004
 * Time: 11:43:36 PM
 * Maintain a central timestamped list of Plugins. Each running instance
 * of cytoscape maintains a single PluginRegistry object (as a member of
 * the shared CytoscpaeObj object). The registry holds classes which are
 * to be used as plugins by CyWindows. Each CyWindow's CyMenu can ask for
 * the set of plugins which have been added to the registry since a
 * timestamp (Using System.currentTimeMillis ()). By so doing, it can
 * retrieve all the plugins which have been added since the previous
 * update of its CyMenu.
 */
public class PluginRegistry {
    protected class PluginRegistryNode {
        public Class plugin;
        public long loadTime;
    }
    protected ArrayList registry;
    protected long lastChangeTime;

    /**
     * Initialize registry as empty.
     */
    public PluginRegistry() {
        registry = new ArrayList();
        markPluginRegistryChangeTime();
    }

    /**
     * Update the last time of change to the registry.
     */
    public void markPluginRegistryChangeTime() {
        lastChangeTime = System.currentTimeMillis();
    }

    /**
     * Returns all plugins loaded since a time (in milliseconds).
     */
    public Class[] getPluginsLoadedSince(long time) {
        if (time >= lastChangeTime) return new Class[0];
        LinkedList newPlugins = new LinkedList();
        Iterator iter;
        for (iter = registry.iterator(); iter.hasNext();) {
            PluginRegistryNode node = (PluginRegistryNode)iter.next();
            if (node.loadTime >= time) {
                newPlugins.add(node.plugin);
            }
        }
        Class newPlugin[] = new Class[newPlugins.size()];
        newPlugins.toArray(newPlugin);
        return newPlugin;
    }

    /**
     * Adds a plugin class to the plugin registry.
     */
    public void addPluginToRegistry(Class plugin) throws
            NotAPluginException, PluginAlreadyRegisteredException {
        if (!AbstractPlugin.class.isAssignableFrom(plugin)) {
            throw new NotAPluginException("class: " + plugin.getName()
                    + "is not a plugin");
        }
        if (pluginRegistryContains(plugin.getName())) {
            throw new PluginAlreadyRegisteredException("plugin already loaded: "
                    + plugin.getName());
        }
        PluginRegistryNode newNode = new PluginRegistryNode();
        newNode.plugin = plugin;
        long now = System.currentTimeMillis();
        newNode.loadTime = now;
        registry.add(newNode);
        lastChangeTime = now;
    }

    /**
     * Tests if plugin is in registry.
     */
    public boolean pluginRegistryContains(String pluginName) {
        Iterator iter;
        for (iter = registry.iterator(); iter.hasNext();) {
            PluginRegistryNode node = (PluginRegistryNode)iter.next();
            if (node.plugin.getName().equals(pluginName)) return true;
        }
        return false;
    }
}

