import java.util.*;
import java.io.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

import giny.model.*;

import cytoscape.plugin.CytoscapePlugin;
import cytoscape.util.CytoscapeAction;
import cytoscape.Cytoscape;
import cytoscape.CyNetwork;
import cytoscape.data.FlagFilter;
import cytoscape.data.FlagEvent;
import cytoscape.data.FlagEventListener;

/**
 * This plugin creates two menu options allowing the user to activate or
 * inactivate this plugin from the plugins menu of Cytoscape. The plugin
 * is activated on construction.<P>
 *
 * When active, this plugin links all of the networks in a Cytoscape instance.
 * Whenever a graph object is flagged or unflagged in one network, the object
 * is set to the same state in all the other networks that contain that object.<P>
 *
 * If an object is added to a network, it will be flagged iff the same object
 * exists and is flagged in another network. When a new network is created,
 * each graph object will be flagged in that network iff the same object is
 * flagged in an existing network.
 *
 * When deactivated, this plugin does not change the current flagged state of
 * any networks and no longer responds to any changes.
 *
 * When activated, this plugin synchronizes all of the existing networks by
 * flagging each graph object in every network that contains it if it is flagged
 * in any of them.
 */
public class MultiNetworkNodeSelection extends CytoscapePlugin
    implements PropertyChangeListener, GraphPerspectiveChangeListener, FlagEventListener {
    
    CytoscapeAction activateAction;   //turns on this plugin
    CytoscapeAction deactivateAction; //turns off this plugin
    boolean working = false; //prevents responding to our own flag requests
    
    public MultiNetworkNodeSelection() {
        //creates two menu options for turning this plugin on and off
        activateAction = new ActivateMultiNetworkNodeSelectionAction();
        activateAction.setPreferredMenu("Plugins");
        deactivateAction = new DeactivateMultiNetworkNodeSelectionAction();
        deactivateAction.setPreferredMenu("Plugins");
        Cytoscape.getDesktop().getCyMenus().addAction(activateAction);
        Cytoscape.getDesktop().getCyMenus().addAction(deactivateAction);
        activateNetworkLinker(); //start with plugin activated
    }
    
    /**
     * Activates this plugin so that all Cytoscape networks are synchronized
     * in the flagged state of nodes and edges. This method first synchronizes
     * all the networks by flagging in all networks each object that is flagged
     * in any of them. It then attaches listeners to handle future changes and
     * switches which menu item is enabled.
     */
    public void activateNetworkLinker() {
        Set flaggedNodes = getAllFlaggedNodes();  //all nodes flagged in any network
        Set flaggedEdges = getAllFlaggedEdges();  //all edges flagged in any network
        //iterate over all networks, flagging the sets built above that hold
        //all graph objects flagged in any network (objects that aren't in a
        //particular network just get skipped)
        //also add our listeners to these objects to catch future changes
        for (Iterator iter = Cytoscape.getNetworkSet().iterator(); iter.hasNext(); ) {
            String net_id = (String)iter.next();
            CyNetwork network = Cytoscape.getNetwork(net_id);
            network.setFlaggedNodes(flaggedNodes, true);
            network.setFlaggedEdges(flaggedEdges, true);
            //attach listeners
            network.addGraphPerspectiveChangeListener(this);
            network.addFlagEventListener(this);
        }
        //this catches network creation and destruction events
        Cytoscape.getSwingPropertyChangeSupport().addPropertyChangeListener(this);
        //swap which UI menu is enabled
        activateAction.setEnabled(false);
        deactivateAction.setEnabled(true);
    }
    
    /**
     * Deactivates this plugin so that future changes to one network will not be
     * propagated to all other networks. The current flagged state of graph objects
     * is not changed, and all listeners are detached. Also switches which menu item
     * is enabled.
     */
    public void deactivateNetworkLinker() {
        //detach all the listeners from the networks
        for (Iterator iter = Cytoscape.getNetworkSet().iterator(); iter.hasNext(); ) {
            String net_id = (String)iter.next();
            CyNetwork network = Cytoscape.getNetwork(net_id);
            network.removeGraphPerspectiveChangeListener(this);
            network.removeFlagEventListener(this);
        }
        //don't pay attention to network creation/destruction events anymore
        Cytoscape.getSwingPropertyChangeSupport().removePropertyChangeListener(this);
        //swap which UI menu is enabled
        activateAction.setEnabled(true);
        deactivateAction.setEnabled(false);
    }
    
    /**
     * Returns a Set containing all the nodes that are flagged in any network.
     * Works properly even if this plugin is currently disabled.
     */
    public Set getAllFlaggedNodes() {
        Set flaggedNodes = new HashSet();  //all nodes flagged in any network
        //iterate over all networks, saving the flagged objects in each network
        for (Iterator iter = Cytoscape.getNetworkSet().iterator(); iter.hasNext(); ) {
            String net_id = (String)iter.next();
            CyNetwork network = Cytoscape.getNetwork(net_id);
            Set nodes = network.getFlaggedNodes();
            flaggedNodes.addAll(nodes);
        }
        return flaggedNodes;
    }
        
    /**
     * Returns a Set containing all the edges that are flagged in any network.
     * Works properly even if this plugin is currently disabled.
     */
    public Set getAllFlaggedEdges() {
        Set flaggedEdges = new HashSet();  //all nodes flagged in any network
        //iterate over all networks, saving the flagged objects in each network
        for (Iterator iter = Cytoscape.getNetworkSet().iterator(); iter.hasNext(); ) {
            String net_id = (String)iter.next();
            CyNetwork network = Cytoscape.getNetwork(net_id);
            Set edges = network.getFlaggedEdges();
            flaggedEdges.addAll(edges);
        }
        return flaggedEdges;
    }
    
    /**
     * When a CyNetwork is created, this method catches the event and flags
     * any graph objects that are flagged in another network. Does nothing
     * when a CyNetwork is destroyed, since it'll automatically discard its
     * set of listeners.<P>
     *
     * This method will not be called if this plugin is disabled, since the
     * listeners get detached.
     */
    public void propertyChange(PropertyChangeEvent event) {
        if (event.getPropertyName() == Cytoscape.NETWORK_CREATED) {
            //get a reference to the network that was created
            String net_id = (String)event.getNewValue();
            CyNetwork network = Cytoscape.getNetwork(net_id);
            if (network != null) {
                //flag objects in this network if they are flagged elsewhere
                Set flaggedNodes = getAllFlaggedNodes(); //flagged in any network
                network.setFlaggedNodes(flaggedNodes, true);
                Set flaggedEdges = getAllFlaggedEdges(); //flagged in any network
                network.setFlaggedEdges(flaggedEdges, true);
                //add listeners to catch future changes
                network.addGraphPerspectiveChangeListener(this);
                network.addFlagEventListener(this);
            } else {
                String lineSep = System.getProperty("line.separator");
                String errString = "In MultiNetworkNodeSelection.propertyChange: " + lineSep
                    + "  unexpected null network processing NETWORK_CREATED event";
                System.err.println(errString);
            }
        }
    }
    
    /**
     * Responds to flag events from a network's flagger by setting the same
     * state for the graph objects in all other networks. Does nothing if
     * the event was triggered by another method in this object.<P>
     *
     * This method will not be called if this plugin is disabled, since the
     * listeners get detached.
     */
    public void onFlagEvent(FlagEvent event) {
        if (working) {return;} //don't respond to our own flag requests
        working = true; //set this variable to prevent responding to our flag requests
        FlagFilter source = event.getSource();
        //iterate over all linked networks to set the same flagged state
        for (Iterator iter = Cytoscape.getNetworkSet().iterator(); iter.hasNext(); ) {
            String net_id = (String)iter.next();
            CyNetwork network = Cytoscape.getNetwork(net_id);
            FlagFilter filter = network.getFlagger();
            //if this filter is the source of the event, skip it
            if (source == filter) {continue;}
            handleFlagEvent(event, network);
        }
        working = false; //listen to flag events again
    }
    
    /**
     * Given a FlagEvent representing flagging in one network, sets the
     * same flagged state in the supplied filter from another network.
     * Note that the FlagFilter makes sure the graph object actually exists
     * in the attached graph before flagging it.
     */
    private void handleFlagEvent(FlagEvent event, CyNetwork network) {
        boolean flagOn = event.getEventType(); //true=flag on, false = flag off
        if (event.getTargetType() == FlagEvent.SINGLE_NODE) {
            network.setFlagged( (Node)event.getTarget(), flagOn );
        } else if (event.getTargetType() == FlagEvent.SINGLE_EDGE) {
            network.setFlagged( (Edge)event.getTarget(), flagOn );
        } else if (event.getTargetType() == FlagEvent.NODE_SET) {
            network.setFlaggedNodes( (Set)event.getTarget(), flagOn );
        } else if (event.getTargetType() == FlagEvent.EDGE_SET) {
            network.setFlaggedEdges( (Set)event.getTarget(), flagOn );
        } else {//huh? unknown target type
            //ignore for now
        }
    }
    
    /**
     * When nodes or edges are added to a linked CyNetwork, this method flags
     * those objects in the network that was changed if any linked network
     * currently flags those objects.
     */
    public void graphPerspectiveChanged(GraphPerspectiveChangeEvent event) {
        working = true; //don't respond to our own flag requests
        CyNetwork source = (CyNetwork)event.getSource();
        if (event.isNodesRestoredType()) {//nodes added to a graph
            Node[] newNodes = event.getRestoredNodes();//all restored nodes
            for (int n = 0; n < newNodes.length; n++) {
                Node nodeToCheck = newNodes[n];
                //see if any linked network currently flags this node
                for (Iterator iter = Cytoscape.getNetworkSet().iterator(); iter.hasNext(); ) {
                    String net_id = (String)iter.next();
                    CyNetwork network = Cytoscape.getNetwork(net_id);
                    if (network == source) {continue;}//skip the source network
                    if ( network.isFlagged(nodeToCheck) ) {
                        //it's flagged in another network, so flag it in the source
                        source.setFlagged(nodeToCheck, true);
                        break; //no point in checking other networks
                    }
                }
            }
        }
        //the event can reference both restored nodes and edges
        if (event.isEdgesRestoredType()) {//edges added to a graph
            Edge[] newEdges = event.getRestoredEdges();//all restored edges
            for (int n = 0; n < newEdges.length; n++) {
                Edge edgeToCheck = newEdges[n];
                //see if any linked network currently flags this edge
                for (Iterator iter = Cytoscape.getNetworkSet().iterator(); iter.hasNext(); ) {
                    String net_id = (String)iter.next();
                    CyNetwork network = Cytoscape.getNetwork(net_id);
                    if (network == source) {continue;}//skip the source network
                    if ( network.isFlagged(edgeToCheck) ) {
                        //it's flagged in another network, so flag it in the source
                        source.setFlagged(edgeToCheck, true);
                        break; //no point in checking other networks
                    }
                }
            }
        }
        working = false;//listen to flag events again
    }
    
    /**
     * Menu item that activates this plugin.
     */
    private class ActivateMultiNetworkNodeSelectionAction extends CytoscapeAction {
        
        public ActivateMultiNetworkNodeSelectionAction() {super("Activate Multi-Network Selection");}
        public void actionPerformed(ActionEvent ae) {activateNetworkLinker();}
    }
    
    /**
     * Menu item that deactivates this plugin.
     */
    private class DeactivateMultiNetworkNodeSelectionAction extends CytoscapeAction {
        
        public DeactivateMultiNetworkNodeSelectionAction() {super("Deactivate Multi-Network Selection");}
        public void actionPerformed(ActionEvent ae) {deactivateNetworkLinker();}
    }
}

