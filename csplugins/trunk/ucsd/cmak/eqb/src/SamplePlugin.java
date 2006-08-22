package csplugins.common.samplePlugins;

import java.util.*;
import java.awt.event.ActionEvent;
import javax.swing.JOptionPane;

import giny.model.Node;
import giny.view.NodeView;

import cytoscape.plugin.CytoscapePlugin;
import cytoscape.util.CytoscapeAction;
import cytoscape.Cytoscape;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.CyEdge;
import cytoscape.view.CyNetworkView;
import cytoscape.data.Semantics;

/**
 * This is a sample Cytoscape plugin using core graph and data structures. For each
 * currently selected node in the graph view, the action method of this plugin
 * additionally selects the neighbors of that node if their canonical name ends
 * with the same letter. (For yeast genes, whose names are of the form 'YOR167C',
 * this selects genes that are on the same DNA strand). This operation was
 * chosen to be illustrative, not necessarily useful.<P>
 *
 * Note that selection is a property of the view of the graph, while neighbors
 * are a property of the graph itself. Thus this plugin must access both the
 * graph and its view.
 */
public class SamplePlugin extends CytoscapePlugin {
    
    /**
     * This constructor creates an action and adds it to the Plugins menu.
     */
    public SamplePlugin() {
        //create a new action to respond to menu activation
        SamplePluginAction action = new SamplePluginAction();
        //set the preferred menu
        action.setPreferredMenu("Plugins");
        //and add it to the menus
        Cytoscape.getDesktop().getCyMenus().addAction(action);
    }
    
    /**
     * Gives a description of this plugin.
     */
    public String describe() {
        StringBuffer sb = new StringBuffer();
        sb.append("For every currently selected node in the graph view, this ");
        sb.append("plugin additionally selects each neighbor of that node if ");
        sb.append("the canonical names of the two nodes have the same last letter.");
        return sb.toString();
    }
        
    /**
     * This class gets attached to the menu item.
     */
    public class SamplePluginAction extends CytoscapeAction {
        
        /**
         * The constructor sets the text that should appear on the menu item.
         */
        public SamplePluginAction() {super("SamplePlugin");}
        
        /**
         * This method is called when the user selects the menu item.
         */
        public void actionPerformed(ActionEvent ae) {
            //get the network object; this contains the graph
            CyNetwork network = Cytoscape.getCurrentNetwork();
            //get the network view object
            CyNetworkView view = Cytoscape.getCurrentNetworkView();
            //can't continue if either of these is null
            if (network == null || view == null) {return;}
            //put up a dialog if there are no selected nodes
            if (view.getSelectedNodes().size() == 0) {
                JOptionPane.showMessageDialog(view.getComponent(),
                        "Please select one or more nodes.");
            }
            
            //a container to hold the objects we're going to select
            Set nodeViewsToSelect = new HashSet();
            //iterate over every node view
            for (Iterator i = view.getSelectedNodes().iterator(); i.hasNext(); ) {
                NodeView nView = (NodeView)i.next();
                //first get the corresponding node in the network
                CyNode node = (CyNode)nView.getNode();
                //get the last letter of its name
                String lastLetter = getLastLetter(network, node);
                //skip if we can't get a valid name
                if (lastLetter == null) {continue;}
                
                //now get the neighbors of that node
                List neighbors = network.neighborsList(node);
                //and iterate over the neighbors
                for (Iterator ni = neighbors.iterator(); ni.hasNext(); ) {
                    CyNode neighbor = (CyNode)ni.next();
                    //get the last letter of the neighbor's name
                    String testLetter = getLastLetter(network, neighbor);
                    //skip if we can't get a valid name
                    if (testLetter == null) {continue;}
                    //test for equality of the last letter of the names
                    if (lastLetter.equalsIgnoreCase(testLetter)) {
                        //success; get the view on this neighbor
                        NodeView neighborView = view.getNodeView(neighbor);
                        //and add that view to our container of objects to select
                        nodeViewsToSelect.add(neighborView);
                    }
                }
            }
            //now go through our container and select each view
            for (Iterator i = nodeViewsToSelect.iterator(); i.hasNext(); ) {
                NodeView nView = (NodeView)i.next();
                nView.setSelected(true);
            }
            //tell the view to redraw since we've changed the selection
            view.redrawGraph(false, true);
        }
        
        /**
         * Gets the canonical name of the given node from the network object
         * and returns a String holding just the last letter of that name.
         *
         * Returns null if a valid name cannot be obtained.
         */
        private String getLastLetter(CyNetwork network, CyNode node) {
            String canonicalName = (String)network.getNodeAttributeValue(node, Semantics.CANONICAL_NAME);
            //return nothing if we can't get a valid name
            if (canonicalName == null || canonicalName.length() == 0) {return null;}
            //extract the last letter
            int length = canonicalName.length();
            String lastLetter = canonicalName.substring(length-1);
            return lastLetter;
        }
    }
}

