/* -*-Java-*-
********************************************************************************
*
* File:         HyperEdgePlugin.java
* RCS:          $Header: /cvs/cvsroot/lstl-lsi/HyperEdge/src/cytoscape/hyperedge/HyperEdgePlugin.java,v 1.1 2007/07/04 01:11:35 creech Exp $
* Description:
* Author:       Michael L. Creech
* Created:      Fri Jun 17 05:05:12 2005
* Modified:     Mon Jul 23 09:35:37 2007 (Michael L. Creech) creech@w235krbza760
* Language:     Java
* Package:
* Status:       Experimental (Do Not Distribute)
*
* (c) Copyright 2005, Agilent Technologies, all rights reserved.
*
********************************************************************************
*
* Revisions:
*
* Mon Jul 23 09:35:06 2007 (Michael L. Creech) creech@w235krbza760
*  Removed describe().
* Tue Jul 03 16:39:13 2007 (Michael L. Creech) creech@w235krbza760
*  Removed getPluginInfoObject().
* Tue May 08 17:54:57 2007 (Michael L. Creech) creech@w235krbza760
*  Added getPluginInfoObject() for Cytoscape 2.5.
* Tue Nov 28 06:19:50 2006 (Michael L. Creech) creech@w235krbza760
*  Moved all event handling to the HyperEdgeManager.
* Mon Oct 03 18:08:18 2005 (Michael L. Creech) creech@Dill
*  Removed use of LoadHyperEdges and SaveHyperEdges until .sif and .gml load and
*  save handle UUIDs.
* Mon Sep 26 19:18:00 2005 (Michael L. Creech) creech@Dill
*  Added issuing feedback during Load/Save to Cytoscape status bar.
*  Changed suffix from .xml to .hpe
********************************************************************************
*/
package cytoscape.hyperedge;


//import cytoscape.CytoscapeObj;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.plugin.PluginInfo;
import cytoscape.plugin.PluginManager;


/**
 * Main interface to Cytoscape environment.
 *
 * @author Michael L. Creech
 * @version 1.0
 */
public class HyperEdgePlugin extends CytoscapePlugin {
    public HyperEdgePlugin() {
        //        Cytoscape.getDesktop().getCyMenus().getOperationsMenu()
        //                 .add(new MainPluginAction());
    }

    // MLC 07/23/07 BEGIN:
    //    /**
    //     * Gives a description of this plugin.
    //     */
    //    public String describe() {
    //        StringBuffer sb = new StringBuffer();
    //        sb.append("Allows the creation of HyperEdges--mutable edges that allow more than two Nodes.");
    //
    //        return sb.toString();
    //    }
    // MLC 07/23/07 END.

    // MLC 07/03/07 BEGIN:
    //    // overrides CytoscapePlugin.getPluginInfoObject():
    //    public PluginInfo getPluginInfoObject() {
    //        PluginInfo info = new PluginInfo();
    //        info.setName("HyperEdgePlugin");
    //        info.setDescription("Allows the creation of HyperEdges--mutable edges that allow more than two Nodes.");
    //        info.setCategory(PluginInfo.Category.FUNCTIONAL_ENRICHMENT);
    //        info.setPluginVersion(HyperEdgeFactory.INSTANCE.getHyperEdgeManager().getHyperEdgeVersionNumber());
    //        info.setCytoscapeVersion("2.5");
    //        // info.setProjectUrl("http://www.cytoscape.org/download_agilent_literature_search_v2.5.php?file=litsearch_v2.4");
    //        info.addAuthor("Aditya Vailaya", "Agilent Labs");
    //        info.addAuthor("Michael Creech", "Blue Oak Software");
    //        info.addAuthor("Allan Kuchinsky", "Agilent Labs");
    //
    //        return info;
    //    }
    // MLC 07/03/07 END.
}
