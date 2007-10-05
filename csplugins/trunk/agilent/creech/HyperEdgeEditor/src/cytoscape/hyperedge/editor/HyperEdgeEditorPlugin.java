/* -*-Java-*-
********************************************************************************
*
* File:         HyperEdgeEditorPlugin.java
* RCS:          $Header: /cvs/cvsroot/lstl-lsi/HyperEdgeEditor/src/cytoscape/hyperedge/editor/HyperEdgeEditorPlugin.java,v 1.1 2007/07/04 01:19:09 creech Exp $
* Description:
* Author:       Michael L. Creech
* Created:      Mon Jul 24 06:36:19 2006
* Modified:     Thu Oct 04 18:41:07 2007 (Michael L. Creech) creech@w235krbza760
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

import com.agilent.labs.lsiutils.gui.MiscGUI;

import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;

import cytoscape.data.Semantics;

import cytoscape.editor.CytoscapeEditorManager;

import cytoscape.editor.actions.DeleteAction;

import cytoscape.hyperedge.HyperEdge;
import cytoscape.hyperedge.HyperEdgeFactory;

import cytoscape.hyperedge.editor.actions.DeleteHyperEdgeAction;
import cytoscape.hyperedge.editor.actions.HyperEdgeDeleteAction;
import cytoscape.hyperedge.editor.actions.SelectHyperEdgeAction;

import cytoscape.hyperedge.impl.HyperEdgeImpl;

import cytoscape.layout.CyLayoutAlgorithm;
import cytoscape.layout.CyLayouts;

import cytoscape.plugin.CytoscapePlugin;

import cytoscape.util.CyNetworkViewUtil;
import cytoscape.util.CytoscapeAction;

import cytoscape.view.CyMenus;
import cytoscape.view.CyNetworkView;
import cytoscape.view.CytoscapeDesktop;

import ding.view.NodeContextMenuListener;

import giny.view.NodeView;

import java.awt.event.ActionEvent;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.util.List;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;


/**
 *
 * @author Michael L. Creech
 * @version 1.0
 *
 */
public class HyperEdgeEditorPlugin extends CytoscapePlugin {
    private static final Double VERSION = 2.56;

    public HyperEdgeEditorPlugin() {
        initializeHyperEdgeEditor();
    }

    // MLC 07/23/07 BEGIN:
    //    /**
    //     * Overrides CytoscapePlugin.describe():
    //     */
    //    public String describe() {
    //        return "Add, remove, and modify HyperEdges in a Cytoscape Network.";
    //    }
    // MLC 07/23/07 END>

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
        // MLC 07/20/07 BEGIN:
        // ASSUME: CytoscapeEditor is always loaded before HyperEdgeEditor.
        System.out.println("BEGIN initializeHyperEdgeEditor");
        //        // in case we are loaded before the CytoscapeEditor:
        //        CytoscapeEditorPlugin.initializeCytoscapeEditor();
        // MLC 07/20/07 END.
        // MLC 05/17/07:
        // CytoscapeEditorManager.register("HyperEdgeEditor",
        // MLC 05/17/07:
        CytoscapeEditorManager.register("cytoscape.hyperedge.editor.HyperEdgeEditor",
                                        "cytoscape.hyperedge.editor.event.HyperEdgeEditEventHandler",
                                        HyperEdgeImpl.ENTITY_TYPE_ATTRIBUTE_NAME,
                                        Semantics.INTERACTION,
                                        BioChemicalReactionVisualStyle.BIOCHEMICAL_REACTION_VISUAL_STYLE);
        fixUpCytoscapeDeleteMenu();

        CyMenus cms = Cytoscape.getDesktop().getCyMenus();
        cms.addAction(new ShowSampleNetworksAction());
        Cytoscape.getSwingPropertyChangeSupport()
                 .addPropertyChangeListener(new PopupMenuMonitor());
    }

    // replace CytoscapeEditor DeleteAction with HyperEdgeEditorDeleteAction:
    private void fixUpCytoscapeDeleteMenu() {
        // MLC 07/20/07:
        // DeleteAction action = CytoscapeEditorManager.manager.getDeleteAction();
        // MLC 07/20/07:
        DeleteAction action = CytoscapeEditorManager.getDeleteAction();
        // remove delete action item and replace with our version of it:
        Cytoscape.getDesktop().getCyMenus().getMenuBar().removeAction(action);
        Cytoscape.getDesktop().getCyMenus()
                 .addAction(new HyperEdgeDeleteAction());
    }

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

            // MLC 09/26/06 BEGIN:
            // sn.createSampleNetworks();
            // (re)define the BioChemicalReactionVisualStyle:
            BioChemicalReactionVisualStyle.getVisualStyle()
                                          .setupVisualStyle(false, true);

            List<CyNetwork> networks = sn.createSampleNetworks();

            // BEGIN
            // MLC 07/16/07:
            CyLayoutAlgorithm myAlgor = CyLayouts.getLayout("force-directed");

            if (myAlgor == null) {
                myAlgor = CyLayouts.getDefaultLayout();
            }

            // END
            for (CyNetwork net : networks) {
                // MLC 10/04/07 BEGIN:
                CyNetworkViewUtil.createNetworkView(net,
                                                    net.getTitle(),
                                                    myAlgor,
                                                    BioChemicalReactionVisualStyle.getVisualStyle());
                //                Cytoscape.createNetworkView(net,
                //                                            net.getTitle(),
                //                                            myAlgor);
                //                // getVisualStyle() returns null:
                //                // Change to BioChemicalReactionVisualStyle:
                //                Cytoscape.getNetworkView(net.getIdentifier())
                //                         .setVisualStyle(BioChemicalReactionVisualStyle.getVisualStyle()
                //                                                                       .getName());
                // MLC 10/04/07 END.
            }
        }
    }

    private class PopupMenuMonitor implements PropertyChangeListener,
                                              // EdgeContextMenuListener,
    NodeContextMenuListener {
        static final public String TOOLTIP_DISABLED = "<HTML>enabled when a HyperEdge ConnectorNode is selected</HTML>";

        public void propertyChange(PropertyChangeEvent e) {
            if (e.getPropertyName() == CytoscapeDesktop.NETWORK_VIEW_CREATED) {
                addPopupMenusToNetworkView(Cytoscape.getCurrentNetworkView());
            } else if (e.getPropertyName() == CytoscapeDesktop.NETWORK_VIEW_DESTROYED) {
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

            HyperEdge he = HyperEdgeFactory.INSTANCE.getHyperEdgeManager()
                                                    .getHyperEdgeForConnectorNode(node);

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
