


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


 */


public class GpuLayout extends CytoscapePlugin {
    
    /**
     * This constructor creates an action and adds it to the Plugins menu.
     */
    public GpuLayout() {
	// Show message on screen
	String message = "Hello World!";
        //System.out.println(message);
	// use the CytoscapeDesktop as parent for a Swing dialog
	JOptionPane.showMessageDialog( Cytoscape.getDesktop(), message);

        //create a new action to respond to menu activation
        GpuLayoutAction action = new GpuLayoutAction();
        //set the preferred menu
        action.setPreferredMenu("Layouts");
        //and add it to the menus
        Cytoscape.getDesktop().getCyMenus().addAction(action);
    }
    
    /**
     * Gives a description of this plugin.
     */
    public String describe() {
        StringBuffer sb = new StringBuffer();
        sb.append("Bla Bla... ");
        sb.append(" more Bla.. ");
        return sb.toString();
    }
        
    /**
     * This class gets attached to the menu item.
     */
    public class GpuLayoutAction extends CytoscapeAction {
        
        /**
         * The constructor sets the text that should appear on the menu item.
         */
        public GpuLayoutAction() {super("GPU Layout");}
        
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
                // get the neighbors of that node
                List neighbors = network.neighborsList(node);
                // and iterate over the neighbors
                for (Iterator ni = neighbors.iterator(); ni.hasNext(); ) {
                    CyNode neighbor = (CyNode)ni.next();
		    // get the view on this neighbor
		    NodeView neighborView = view.getNodeView(neighbor);
		    //and add that view to our container of objects to select
		    nodeViewsToSelect.add(neighborView);
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

