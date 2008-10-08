
/*
 Copyright (c) 2008, The Cytoscape Consortium (www.cytoscape.org)

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

/*
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


/**
 * Main interface to Cytoscape environment.
 *
 * @author Michael L. Creech
 * @version 1.0
 */
public class HyperEdgePlugin extends CytoscapePlugin {
    /**
     * The name of this plugin.
     */
    public static final String MY_NAME = "HyperEdgePlugin";
    /**
     * Constructor for this plugin.
     */
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
