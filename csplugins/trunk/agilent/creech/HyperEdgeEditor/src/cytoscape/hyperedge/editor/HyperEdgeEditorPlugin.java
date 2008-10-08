/* -*-Java-*-
********************************************************************************
*
* File:         HyperEdgeEditorPlugin.java
* RCS:          $Header: /cvs/cvsroot/lstl-lsi/HyperEdgeEditor/src/cytoscape/hyperedge/editor/HyperEdgeEditorPlugin.java,v 1.1 2007/07/04 01:19:09 creech Exp $
* Description:
* Author:       Michael L. Creech
* Created:      Mon Jul 24 06:36:19 2006
* Modified:     Thu Sep 25 09:52:25 2008 (Michael L. Creech) creech@w235krbza760
* Language:     Java
* Package:
* Status:       Experimental (Do Not Distribute)
*
* (c) Copyright 2006, Agilent Technologies, all rights reserved.
*
********************************************************************************
*
* Revisions:
*
* Thu Sep 25 09:52:17 2008 (Michael L. Creech) creech@w235krbza760
*  Changed to version 2.64.
* Wed Jul 09 10:41:20 2008 (Michael L. Creech) creech@w235krbza760
*  Changed to version 2.63.
* Thu Apr 03 06:21:12 2008 (Michael L. Creech) creech@w235krbza760
*  Changed to version 2.62.
* Fri Mar 28 07:24:04 2008 (Michael L. Creech) creech@w235krbza760
*  Changed to version 2.61.
* Wed Dec 19 14:17:20 2007 (Michael L. Creech) creech@w235krbza760
*  Removed use of fixUpCytoscapeDeleteMenu().
* Thu Nov 29 08:59:46 2007 (Michael L. Creech) creech@w235krbza760
*  Changed to version 2.59. Changed to use new 4 arg Cytoscape.createNetworkView().
* Sun Nov 04 13:35:10 2007 (Michael L. Creech) creech@w235krbza760
*  Changed addNodeContextMenuItems() to only enable HyperEdge
*  operations when you right-click on a connector node in a CyNetworkView
*  and that connector node belongs to a HyperEdge that is a member
*  of the CyNetwork of this CyNetworkView.
* Wed Oct 31 10:09:22 2007 (Michael L. Creech) creech@w235krbza760
*  Changed to version 2.58.
* Thu Oct 25 16:05:12 2007 (Michael L. Creech) creech@w235krbza760
*  Changed to version 2.57. Added HESessionLoadedMonitor that
*  ensures that the BioChemicalReaction Visual Style is set up after loading
*  a session that might wipe out this visual style.
* Thu Oct 04 18:37:13 2007 (Michael L. Creech) creech@w235krbza760
*  Changed to fix rendering and initial visual style used to display
*  sample networks. Changed to version 2.56.
* Wed Jul 25 16:09:57 2007 (Michael L. Creech) creech@w235krbza760
*  Changed to version 2.55.
* Mon Jul 16 15:06:17 2007 (Michael L. Creech) creech@w235krbza760
*  Updated use of CyLayouts.getLayout() due to API changes.
* Fri Jul 13 08:53:07 2007 (Michael L. Creech) creech@w235krbza760
*  Changed to version 2.54.
* Tue Jul 03 16:38:33 2007 (Michael L. Creech) creech@w235krbza760
*  Removed use of getPluginInfoObject() & updated version number to 2.53.
* Thu May 17 07:33:39 2007 (Michael L. Creech) creech@w235krbza760
*  Changed call to CytoscapeEditorManager.register() in
*  initializeHyperEdgeEditor() to reflect new location of
*  HyperEdgeEditor and changed version to 2.51.
* Tue May 08 18:02:22 2007 (Michael L. Creech) creech@w235krbza760
*  Updated VERSION to 2.50 and added getPluginInfoObject() for Cytoscape 2.5.
* Wed Jan 31 08:54:13 2007 (Michael L. Creech) creech@w235krbza760
*  Updated VERSION to 2.4 beta 1.
* Tue Jan 16 09:25:56 2007 (Michael L. Creech) creech@w235krbza760
*  Updated VERSION to 2.4 alfa 1.
* Sat Jan 06 10:23:19 2007 (Michael L. Creech) creech@w235krbza760
*  Changed to version 0.09.
* Fri Dec 29 07:44:36 2006 (Michael L. Creech) creech@w235krbza760
*  Changed initialization of the plugin to replace the DeleteAction
*  from CytsocapeEditor with our wrapper version to handle deletion of
*  HyperEdges.
*  Added getVersion() and fixUpCytoscapeDeleteMenu().
********************************************************************************
*/
package cytoscape.hyperedge.editor;

import giny.view.NodeView;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import com.agilent.labs.lsiutils.gui.MiscGUI;

import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.Semantics;
import cytoscape.editor.CytoscapeEditorManager;
import cytoscape.hyperedge.HyperEdge;
import cytoscape.hyperedge.HyperEdgeFactory;
import cytoscape.hyperedge.HyperEdgeManager;
import cytoscape.hyperedge.editor.actions.DeleteHyperEdgeAction;
import cytoscape.hyperedge.editor.actions.SelectHyperEdgeAction;
import cytoscape.hyperedge.impl.HyperEdgeImpl;
import cytoscape.layout.CyLayoutAlgorithm;
import cytoscape.layout.CyLayouts;
import cytoscape.logger.CyLogger;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.util.CytoscapeAction;
import cytoscape.view.CyMenus;
import cytoscape.view.CyNetworkView;
import cytoscape.view.CytoscapeDesktop;
import ding.view.NodeContextMenuListener;


/**
 *
 * @author Michael L. Creech
 * @version 1.0
 *
 */
public class HyperEdgeEditorPlugin extends CytoscapePlugin {
    private static final Double VERSION = 2.64;

    public HyperEdgeEditorPlugin() {
        initializeHyperEdgeEditor();
    }

    // MLC 07/03/07 BEGIN:
    //    // overrides CytoscapePlugin.getPluginInfoObject():
    //    public PluginInfo getPluginInfoObject() {
    //        PluginInfo info = new PluginInfo();
    //        info.setName("HyperEdgeEditor");
    //        info.setDescription("Add, remove, and modify HyperEdges in a Cytoscape Network.");
    //        info.setCategory(PluginInfo.Category.FUNCTIONAL_ENRICHMENT);
    //        info.setPluginVersion(VERSION);
    //        info.setCytoscapeVersion("2.5");
    //        // info.setProjectUrl("http://www.cytoscape.org/download_agilent_literature_search_v2.5.php?file=litsearch_v2.4");
    //        info.addAuthor("Allan Kuchinsky", "Agilent Labs");
    //        info.addAuthor("Michael Creech", "Blue Oak Software");
    //        info.addAuthor("Aditya Vailaya", "Agilent Labs");
    //        return info;
    //    }
    // MLC 07/03/07 END.
    public Double getVersion() {
        return VERSION;
    }

    private void initializeHyperEdgeEditor() {
        // ASSUME: CytoscapeEditor is always loaded before HyperEdgeEditor.
        CyLogger.getLogger().info("BEGIN initializeHyperEdgeEditor");
        CytoscapeEditorManager.register("cytoscape.hyperedge.editor.HyperEdgeEditor",
                                        "cytoscape.hyperedge.editor.event.HyperEdgeEditEventHandler",
                                        HyperEdgeImpl.ENTITY_TYPE_ATTRIBUTE_NAME,
                                        Semantics.INTERACTION,
                                        BioChemicalReactionVisualStyle.BIOCHEMICAL_REACTION_VISUAL_STYLE);
	// MLC 12/18/07:
        // fixUpCytoscapeDeleteMenu();

        CyMenus cms = Cytoscape.getDesktop().getCyMenus();
        cms.addAction(new ShowSampleNetworksAction());

        PopupMenuMonitor pmm = new PopupMenuMonitor();
        Cytoscape.getSwingPropertyChangeSupport()
                 .addPropertyChangeListener(CytoscapeDesktop.NETWORK_VIEW_CREATED,
                                            pmm);

        Cytoscape.getSwingPropertyChangeSupport()
                 .addPropertyChangeListener(CytoscapeDesktop.NETWORK_VIEW_DESTROYED,
                                            pmm);
        Cytoscape.getSwingPropertyChangeSupport().addPropertyChangeListener(Cytoscape.SESSION_LOADED,
                                                                            new HEESessionLoadedMonitor());
    }

    // MLC 12/18/07 BEGIN:
    //    // replace CytoscapeEditor DeleteAction with HyperEdgeEditorDeleteAction:
    //    private void fixUpCytoscapeDeleteMenu() {
    //        DeleteAction action = CytoscapeEditorManager.getDeleteAction();
    //        // remove delete action item and replace with our version of it:
    //        Cytoscape.getDesktop().getCyMenus().getMenuBar().removeAction(action);
    //        Cytoscape.getDesktop().getCyMenus()
    //                 .addAction(new HyperEdgeDeleteAction());
    //    }
    // MLC 12/18/07 END.

    public class ShowSampleNetworksAction extends CytoscapeAction {
        /**
                 *
                 */
        private static final long serialVersionUID = 2279855440773628815L;

        /**
        * The constructor sets the text that should appear on the menu item.
        */
        public ShowSampleNetworksAction() {
            super("HyperEdge Sample Networks");
            setPreferredMenu("Import");
        }

        /**
         * This method is called when the user selects the menu item.
         */
        public void actionPerformed(ActionEvent ae) {
            SampleNetworks sn = new SampleNetworks();

            // (re)define the BioChemicalReactionVisualStyle:
            BioChemicalReactionVisualStyle.getVisualStyle()
                                          .setupVisualStyle(false, true);

            List<CyNetwork>   networks = sn.createSampleNetworks();
            CyLayoutAlgorithm myAlgor = CyLayouts.getLayout("force-directed");

            if (myAlgor == null) {
                myAlgor = CyLayouts.getDefaultLayout();
            }

            for (CyNetwork net : networks) {
                // MLC 11/29/07 BEGIN:
                //                CyNetworkViewUtil.createNetworkView(net,
                //                                                    net.getTitle(),
                //                                                    myAlgor,
                //                                                    BioChemicalReactionVisualStyle.getVisualStyle());
                Cytoscape.createNetworkView(net,
                                            net.getTitle(),
                                            myAlgor,
                                            BioChemicalReactionVisualStyle.getVisualStyle());
                // MLC 11/29/07 END.
            }
        }
    }

    private class HEESessionLoadedMonitor implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent e) {
            // ensure we always have this style--even if the session loaded doesn't have it:
            BioChemicalReactionVisualStyle.getVisualStyle()
                                          .setupVisualStyle(false, false);
        }
    }

    private class PopupMenuMonitor implements PropertyChangeListener,
                                              // EdgeContextMenuListener,
    NodeContextMenuListener {
        static final public String TOOLTIP_DISABLED = "<HTML>enabled when a HyperEdge ConnectorNode is selected that exists in this view</HTML>";

        public void propertyChange(PropertyChangeEvent e) {
            if (e.getPropertyName() == CytoscapeDesktop.NETWORK_VIEW_CREATED) {
                addPopupMenusToNetworkView(Cytoscape.getCurrentNetworkView());
            } else {
                // must be CytoscapeDesktop.NETWORK_VIEW_DESTROYED) {
                removePopupMenusFromNetworkView(Cytoscape.getCurrentNetworkView());
            }
        }

        private void addPopupMenusToNetworkView(CyNetworkView view) {
            view.addNodeContextMenuListener(this);
            // view.addEdgeContextMenuListener(this);
        }

        private void removePopupMenusFromNetworkView(CyNetworkView view) {
            view.removeNodeContextMenuListener(this);
            // view.removeEdgeContextMenuListener(this);
        }

        // implements NodeContextMenuListener interface
        public void addNodeContextMenuItems(NodeView view, JPopupMenu menu) {
            CyNode node = (CyNode) view.getNode();

            // HEUtils.log("HEE: Node popup, node = " + HEUtils.toString(node));
            CyNetwork net = (CyNetwork) (view.getGraphView()
                                             .getGraphPerspective());

            // MLC 11/04/07 BEGIN:
            HyperEdgeManager heMan = HyperEdgeFactory.INSTANCE.getHyperEdgeManager();

            // HyperEdge he = HyperEdgeFactory.INSTANCE.getHyperEdgeManager()
            //                                        .getHyperEdgeForConnectorNode(node);
            HyperEdge he = heMan.getHyperEdgeForConnectorNode(node);

            if (!heMan.isConnectorNode(node, net)) {
                // the HyperEdge must belong to the network we are
                // popping up the menu in:
                he = null;
            }

            // MLC 11/04/07 END.
            menu.add(addNodeMenuItems(he, net));
        }

        private JMenuItem addNodeMenuItems(HyperEdge he, CyNetwork net) {
            JMenu   subMenu = new JMenu("HyperEdgeEditor");
            boolean enabled = (he != null);
            MiscGUI.setComponentEnabled(subMenu, enabled, TOOLTIP_DISABLED, null);

            if (enabled) {
                // we have a ConnectorNode, add popup menu items:
                subMenu.add(addDeleteHyperEdgeMenuItems(he, net));
                subMenu.add(new JMenuItem(new SelectHyperEdgeAction(he, net)));
            }

            return subMenu;
        }

        private JMenuItem addDeleteHyperEdgeMenuItems(HyperEdge he,
                                                      CyNetwork net) {
            JMenu deleteSubMenu = new JMenu("Delete");
            deleteSubMenu.add(new JMenuItem(new DeleteHyperEdgeAction(he, net,
                                                                      "Delete HyperEdge From This Network",
                                                                      false)));
            deleteSubMenu.add(new JMenuItem(new DeleteHyperEdgeAction(he, net,
                                                                      "Delete HyperEdge From All Networks",
                                                                      true)));

            return deleteSubMenu;
        }

        //        // implements EdgeContextMenuListener interface
        //        public void addEdgeContextMenuItems(EdgeView view, JPopupMenu menu) {
        //            CyEdge edge = (CyEdge) view.getEdge();
        //            // HEUtils.log("HEE: Edge popup, edge = " + HEUtils.toString(edge));
        //        }
    }
}
