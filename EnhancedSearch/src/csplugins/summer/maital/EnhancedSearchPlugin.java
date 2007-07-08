package csplugins.summer.maital;

import cytoscape.plugin.CytoscapePlugin;
import cytoscape.plugin.PluginInfo;


/**
 * Enhanced Search plugin
 *
 * @author Maital Ashkenazi
 * @version 1.0
 *
 */
public class EnhancedSearchPlugin extends CytoscapePlugin {

    private static final double VERSION = 1.00;

	// Creates a new EnhancedSearchPlugin object.
    public EnhancedSearchPlugin() {
        EnhancedSearch plugin = new EnhancedSearch();
    }
    
    //  Information needed for Cytoscape version 2.5 plugin management
    public PluginInfo getPluginInfoObject() {

        PluginInfo info = new PluginInfo();

        info.setName("EnhancedSearch");
        info.setDescription("Perform search on multiple attribute fields.");
        info.setCategory(PluginInfo.Category.NETWORK_ATTRIBUTE_IO);
        info.setPluginVersion(VERSION);
        info.setCytoscapeVersion("2.5");
        info.setProjectUrl("http://conklinwolf.ucsf.edu/genmappwiki/Google_Summer_of_Code_2007/Maital");
        info.addAuthor("Maital Ashkenazi", "HUJI");
        
        return info;
    }

}
