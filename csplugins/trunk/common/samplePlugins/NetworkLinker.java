//---------------------------------------------------------------------------
//  $Revision:  
//  $Date$
//  $Author$
//---------------------------------------------------------------------------
package csplugins.common.samplePlugins;

import java.util.*;
import java.io.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

import giny.model.*;

import cytoscape.Cytoscape;
import cytoscape.CyNetwork;
import cytoscape.data.FlagFilter;
import cytoscape.data.FlagEvent;
import cytoscape.data.FlagEventListener;

/**
 * This class links the flagged state of a set of networks. Whenever a
 * graph object is flagged or unflagged in one network, the object
 * is set to the same state in all linked networks that contain that
 * object.
 *
 * If an object is added to a linked network, its flagged state will be
 * set equal to the current state in the other networks that already
 * contain that object.
 */
public class NetworkLinker
    implements PropertyChangeListener, GraphPerspectiveChangeListener, FlagEventListener {
    
    Set networks = new HashSet();
    boolean working = false; //prevents responding to our own flag requests
    
    public NetworkLinker() {
        //this catches network destroyed events
        Cytoscape.getSwingPropertyChangeSupport().addPropertyChangeListener(this);
    }
    
    /**
     * Sets the group of linked CyNetwork objects to those contained in the
     * argument. Any previous linked group is discarded. If the argument is
     * null or contains less than two elements, this method only clears the
     * current group of linked networks.
     *
     * @throws ClassCastException if the argument doesn't contain CyNetwork objects
     */
    public void setNetworks(Collection newNetworks) {
        clearNetworks(); //dispose of the current set of linked networks
        if (newNetworks == null || newNetworks.size() < 2) {return;}
        for (Iterator iter = newNetworks.iterator(); iter.hasNext(); ) {
            CyNetwork network = (CyNetwork)iter.next();
            this.networks.add(network);
            network.addGraphPerspectiveChangeListener(this);
            network.getFlagger().addFlagEventListener(this);
        }
    }
    
    /**
     * Detaches this listener object from all linked networks and FlagFilters
     * and discards the references to the currently linked networks.
     */
    public void clearNetworks() {
        //remove the listener from all networks first
        for (Iterator iter = networks.iterator(); iter.hasNext(); ) {
            CyNetwork network = (CyNetwork)iter.next();
            network.removeGraphPerspectiveChangeListener(this);
            network.getFlagger().removeFlagEventListener(this);
        }
        networks.clear();//now dispose of the network references
    }
    
    /**
     * When a CyNetwork is destroyed, this method catches the event and removes
     * that network from the set of linked networks if it is a member.
     */
    public void propertyChange(PropertyChangeEvent event) {
        if (networks.size() == 0) {return;}
        if (event.getPropertyName() == Cytoscape.NETWORK_DESTROYED) {
            Set allNetworks = Cytoscape.getNetworkSet();
            //iterate over all linked networks, removing any that no longer exist
            for (Iterator iter = networks.iterator(); iter.hasNext(); ) {
                CyNetwork network = (CyNetwork)iter.next();
                if (!allNetworks.contains(network)) {//this network was destroyed
                    network.removeGraphPerspectiveChangeListener(this);
                    network.getFlagger().removeFlagEventListener(this);
                    iter.remove();
                }
            }
        }
    }
    
    public void onFlagEvent(FlagEvent event) {
        if (working) {return;} //don't respond to our own flag requests
        working = true; //set this variable to prevent responding to our flag requests
        FlagFilter source = event.getSource();
        //iterate over all linked networks to set the same flagged state
        for (Iterator iter = networks.iterator(); iter.hasNext(); ) {
            CyNetwork network = (CyNetwork)iter.next();
            FlagFilter filter = network.getFlagger();
            //if this filter is the source of the event, skip it
            if (source == filter) {continue;}
            handleFlagEvent(event, filter);
        }
        working = false; //listen to flag events again
    }
    
    /**
     * Given a FlagEvent representing flagging in one network, sets the
     * same flagged state in the supplied filter from another network.
     * Note that the FlagFilter makes sure the graph object actually exists
     * in the attached graph before flagging it.
     */
    private void handleFlagEvent(FlagEvent event, FlagFilter filter) {
        boolean flagOn = event.getEventType(); //true=flag on, false = flag off
        if (event.getTargetType() == FlagEvent.SINGLE_NODE) {
            filter.setFlagged( (Node)event.getTarget(), flagOn );
        } else if (event.getTargetType() == FlagEvent.SINGLE_EDGE) {
            filter.setFlagged( (Edge)event.getTarget(), flagOn );
        } else if (event.getTargetType() == FlagEvent.NODE_SET) {
            filter.setFlaggedNodes( (Set)event.getTarget(), flagOn );
        } else if (event.getTargetType() == FlagEvent.EDGE_SET) {
            filter.setFlaggedEdges( (Set)event.getTarget(), flagOn );
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
            for (int n = 0; n< newNodes.length; n++) {
                Node nodeToCheck = newNodes[n];
                //see if any linked network currently flags this node
                for (Iterator iter = networks.iterator(); iter.hasNext(); ) {
                    CyNetwork network = (CyNetwork)iter.next();
                    if (network == source) {continue;}//skip the source network
                    if ( network.getFlagger().isFlagged(nodeToCheck) ) {
                        //it's flagged in another network, so flag it in the source
                        source.getFlagger().setFlagged(nodeToCheck, true);
                        break; //no point in checking other networks
                    }
                }
            }
        }
        //the event can reference both restored nodes and edges
        if (event.isEdgesRestoredType()) {//edges added to a graph
            Edge[] newEdges = event.getRestoredEdges();//all restored edges
            for (int n = 0; n< newEdges.length; n++) {
                Edge edgeToCheck = newEdges[n];
                //see if any linked network currently flags this edge
                for (Iterator iter = networks.iterator(); iter.hasNext(); ) {
                    CyNetwork network = (CyNetwork)iter.next();
                    if (network == source) {continue;}//skip the source network
                    if ( network.getFlagger().isFlagged(edgeToCheck) ) {
                        //it's flagged in another network, so flag it in the source
                        source.getFlagger().setFlagged(edgeToCheck, true);
                        break; //no point in checking other networks
                    }
                }
            }
        }
        working = false;//listen to flag events again
    }                
}

