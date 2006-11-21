/** Copyright (c) 2004 Memorial Sloan-Kettering Cancer Center.
 **
 ** Code written by: Ethan Cerami
 ** Authors: Ethan Cerami, Gary Bader, Chris Sander
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** any later version.
 ** 
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and
 ** Memorial Sloan-Kettering Cancer Center 
 ** has no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall
 ** Memorial Sloan-Kettering Cancer Center
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if
 ** Memorial Sloan-Kettering Cancer Center 
 ** has been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 ** 
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/
package org.cytoscape.coreplugin.cpath.action;

import org.cytoscape.coreplugin.cpath.model.UserSelection;
import cytoscape.CyEdge;
import cytoscape.CyNode;
import cytoscape.data.SelectEvent;
import cytoscape.data.SelectEventListener;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Displays Details Regarding a Selected Interactor or Interaction.
 *
 * @author Ethan Cerami
 */
public class DisplayDetails implements SelectEventListener {
    private HashMap cyMap;
    private UserSelection userSelection;
    private int totalNumNodesSelected = 0;
    private int totalNumEdgesSelected = 0;

    /**
     * Constructor.
     *
     * @param cyMap         HashMap all all Interactors/Interactions, index by
     *                      node/edge ID.
     * @param userSelection Current User Selection.
     */
    public DisplayDetails(HashMap cyMap, UserSelection userSelection) {
        this.cyMap = cyMap;
        this.userSelection = userSelection;
    }

    /**
     * User Has Selected Node(s)/Edge(s) in the Main Cytoscape Window.
     *
     * @param event SelectEvent Object.
     */
    public void onSelectEvent(SelectEvent event) {
        int targetType = event.getTargetType();

        //  Only show details when exactly one node/edge is selected.
        //  This is done by keeping a running total of number of nodes/edges
        //  currently selected.  Fixes bug #0000511.
        //  A simpler option would be to obtain a SelectFilter object from
        //  the current network, and simply query it for a list of selected
        //  nodes/edges.  However, we want the listener to work on multiple
        //  networks.  For example, we want to display node/edge details
        //  for a parent network and any of its subnetworks.
        if (targetType == SelectEvent.NODE_SET) {
            HashSet set = (HashSet) event.getTarget();
            trackTotalNumberNodesSelected(event, set);
            if (event.getEventType() && totalNumNodesSelected < 3) {
                Iterator iterator = set.iterator();
                CyNode node = (CyNode) iterator.next();
                String id = node.getIdentifier();
//                Interactor interactor = (Interactor) cyMap.get(id);
//                userSelection.setSelectedInteractor(id, interactor);
            }
        } else if (targetType == SelectEvent.EDGE_SET) {
            HashSet set = (HashSet) event.getTarget();
            trackTotalNumEdgesSelected(event, set);
            if (event.getEventType() && totalNumEdgesSelected < 3) {
                Iterator iterator = set.iterator();
                CyEdge edge = (CyEdge) iterator.next();
                String id = edge.getIdentifier();
//                Interaction interaction = (Interaction) cyMap.get(id);
//                userSelection.setSelectedInteraction(interaction);
            }
        }
    }

    /**
     * Keeps track of total number of Edges currently selected by the user.
     */
    private void trackTotalNumEdgesSelected(SelectEvent event, HashSet set) {
        if (event.getEventType()) {
            totalNumEdgesSelected += set.size();
        } else {
            totalNumEdgesSelected -= set.size();
        }
    }

    /**
     * Keeps track of total number of Nodes currently selected by the user.
     */
    private void trackTotalNumberNodesSelected(SelectEvent event, HashSet set) {
        if (event.getEventType()) {
            totalNumNodesSelected += set.size();
        } else {
            totalNumNodesSelected -= set.size();
        }
    }
}
