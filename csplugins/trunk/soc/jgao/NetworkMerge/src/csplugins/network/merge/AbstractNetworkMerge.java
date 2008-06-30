/* File: AbstractNetworkMerge.java

 Copyright (c) 2006, 2007, The Cytoscape Consortium (www.cytoscape.org)

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

package csplugins.network.merge;

import java.util.List;
import java.util.Vector;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;

import cytoscape.Cytoscape;
import cytoscape.CyNetwork;
import cytoscape.data.CyAttributes;
import cytoscape.data.Semantics;

import giny.model.GraphObject;
import giny.model.Node;
import giny.model.Edge;
/**
 * NetworkMerge implement
 * 
 * 
 */
public abstract class AbstractNetworkMerge implements NetworkMerge {
     /**
     * Check whether two nodes match
     *
     * @param net1,net2 two networks
     * @param n1,n2 two nodes belongs to net1 and net2 respectively
     * 
     * @return true if n1 and n2 matches
     */
    public abstract boolean matchNode(CyNetwork net1, Node n1, CyNetwork net2, Node n2);
    
    /**
     * Merge (matched) nodes into one
     * 
     * @param mapNetNode 
     *              map of network to node, node in the network to be merged
     * 
     * @return merged Node
     */
    public abstract Node mergeNode(Map<CyNetwork,GraphObject> mapNetNode);
    
    /**
     * Merge (matched) nodes into one. This method will be refactored in Cytoscape3
     * 
     * @param mapNetEdge 
     *              map from network to Edge, Edge in the network to be merged
     * @param source, target
     *              source and target nodes in the merge network
     * 
     * @return merged Node
     */
    public abstract Edge mergeEdge(Map<CyNetwork,GraphObject> mapNetEdge, Node source, 
                                Node target, String interaction, boolean directed);
    
    /**
     * Check whether two edges match
     *
     * @param net1,net2 two networks
     * @param e1,e2 two edges belongs to net1 and net2 respectively
     * 
     * @return true if n1 and n2 matches
     */
    public boolean matchEdge(CyNetwork net1, Edge e1, CyNetwork net2, Edge e2) {
        //TODO should interaction be considered or not?
        CyAttributes attributes = Cytoscape.getEdgeAttributes();
        if (!attributes.getAttribute(e1.getIdentifier(),Semantics.INTERACTION)
                .equals(attributes.getAttribute(e2.getIdentifier(), Semantics.INTERACTION)))
            return false;
        if (e1.isDirected()) { // directed
            if (!e2.isDirected()) return false;
            return matchNode(net1, e1.getSource(), net2, e2.getSource())
                    && matchNode(net1, e1.getTarget(), net2, e2.getTarget());
        } else { //non directed
            if (e2.isDirected()) return false;
            if (matchNode(net1, e1.getSource(), net2, e2.getSource())
                    && matchNode(net1, e1.getTarget(), net2, e2.getTarget()))
                return true;
            if (matchNode(net1, e1.getSource(), net2, e2.getTarget())
                    && matchNode(net1, e1.getTarget(), net2, e2.getSource()))
                return true;
            return false;
        }
    }
    
    /**
     * Merge networks into one
     *
     * @param networks 
     *            Networks to be merged
     * @param title
     *            The title of the new network
     * @return merged Node
     */
    public CyNetwork mergeNetwork(List<CyNetwork> networks, Operation op, String title) {
        List<Map<CyNetwork,GraphObject>> matchedNodeList = getMatchedList(networks,true);
        matchedNodeList = selectMatchedNodeList(matchedNodeList, op, networks.size());
        List<Map<CyNetwork,GraphObject>> matchedEdgeList = getMatchedList(networks,false);
        
        Map<Node,Node> mapNN = new HashMap<Node,Node>(); // save information on mapping from original nodes to merged nodes
                                                         // to use when merge edges
        
        // merge nodes in the list
        int nNode = matchedNodeList.size();
        List<Node> nodes = new Vector<Node>(nNode);
        for (int i=0; i<nNode; i++) {
            Map<CyNetwork,GraphObject> mapNetNode = matchedNodeList.get(i);
            Node node = mergeNode(mapNetNode);
            nodes.add(node);
            
            Iterator<GraphObject> itNode = mapNetNode.values().iterator();
            while (itNode.hasNext()) {
                Node node_ori = (Node) itNode.next();
                mapNN.put(node_ori, node);
            }
        }
        
        // merge edges
        int nEdge = matchedEdgeList.size();
        List<Edge> edges = new Vector<Edge>(nEdge);
        for (int i=0; i<nEdge; i++) {
            Map<CyNetwork,GraphObject> mapNetEdge = matchedEdgeList.get(i);
            
            // get the source and target nodes in merged network
            Iterator<GraphObject> itEdge = mapNetEdge.values().iterator();
            Edge edge_ori = (Edge) itEdge.next();
            Node source = mapNN.get(edge_ori.getSource());
            Node target = mapNN.get(edge_ori.getTarget());
            if (source==null||target==null) { // some of the node may be exluded when intersection or difference
                continue;
            }
            
            boolean directed = edge_ori.isDirected();
            CyAttributes attributes = Cytoscape.getEdgeAttributes();
            String interaction = (String)attributes.getAttribute(edge_ori.getIdentifier(),Semantics.INTERACTION);
            
            // for DEBUG, to check whether the edge in the list have the same source and target node in the merged network
            // Could this be implemented as an exception?
            while (itEdge.hasNext()) {
                Edge edge_ori1 = (Edge) itEdge.next();
                Node source1 = mapNN.get(edge_ori.getSource());
                Node target1 = mapNN.get(edge_ori.getTarget());
                
                if (edge_ori1.isDirected()) {
                    if (!directed||source!=source1 || target!=target1) {
                        System.err.println("Edge node not match");
                    }
                } else {
                    if (directed||(!(source==source1&&target==target1)&&!(source==target1&&target==source1))) {
                        System.err.println("Edge node not match");
                    }
                }
            } // for DEBUG
            
            Edge edge = mergeEdge(mapNetEdge,source,target,interaction,directed);
            edges.add(edge);
        }
        
        // create new network
        CyNetwork network = Cytoscape.createNetwork(nodes, edges, title);
        
        return network;
    }
        
    /**
     * Get a list of matched nodes/edges
     *
     * @param networks
     *           Networks to be merged
     * 
     * @return list of map from network to node/edge
     */    
    protected List<Map<CyNetwork,GraphObject>> getMatchedList(List<CyNetwork> networks, boolean isNode) {
        List<Map<CyNetwork,GraphObject>> matchedList = new Vector<Map<CyNetwork,GraphObject>>();
        
        int nNet = networks.size();
        for (int i=0; i<nNet; i++) {
            CyNetwork net1 = networks.get(i);
            Iterator<GraphObject> it;
            if (isNode) {
                it = net1.nodesIterator();
            } else { //edge
                it = net1.edgesIterator();
            }
            while (it.hasNext()) {
                GraphObject go1 = it.next();
                
                // chech whether any nodes in the matchedNodeList match with this node
                // if yes, add to the list, else add a new map to the list
                boolean matched = false;
                int n = matchedList.size();
                int j=0;
                for (; j<n; j++) {
                    Map<CyNetwork,GraphObject> matchedGO = matchedList.get(j);
                    Iterator<CyNetwork> itNet = matchedGO.keySet().iterator();
                    while (itNet.hasNext()) {
                        CyNetwork net2 = itNet.next();
                        if (net1==net2) continue; // assume the same network don't have nodes match to each other
                        GraphObject go2 = matchedGO.get(net2);
                        if (isNode) { //NODE
                            matched = matchNode(net1,(Node)go1,net2,(Node)go2);
                        } else {// EDGE
                            matched = matchEdge(net1,(Edge)go1,net2,(Edge)go2);
                        }
                        if (matched) {
                            matchedGO.put(net1, go1);
                            break;
                        }
                    }
                    if (matched) {
                        break;
                    }
                }                
                if (!matched) { //no matched node found, add new map to the list
                    Map<CyNetwork,GraphObject> matchedGO = new HashMap<CyNetwork,GraphObject>();
                    matchedGO.put(net1, go1);
                    matchedList.add(matchedGO);
                }
            }
        }
        
        return matchedList;
    }
    /**
     * Select nodes for merge according to different op
     *
     * @param networks
     *           Networks to be merged
     * @param op
     *           Operation
     * @param size
     *           Number of networks
     * 
     * @return list of matched nodes
     */    
    protected List<Map<CyNetwork,GraphObject>> selectMatchedNodeList(List<Map<CyNetwork,GraphObject>> matchedNodeList, Operation op, int size) {
        List<Map<CyNetwork,GraphObject>> list = new Vector<Map<CyNetwork,GraphObject>>(matchedNodeList);
        
        if (op==Operation.UNION) {
            
        } else if (op==Operation.INTERSECTION) {
            for (int i=list.size()-1; i>=0; i--) {
                Map<CyNetwork,GraphObject> map = list.get(i);
                if(map.size()!=size) { // if not contained in all the networks, remove
                    list.remove(i);
                }
            }
        } else { //if (op==Operation.DIFFERENCE)
            for (int i=list.size()-1; i>=0; i--) {
                Map<CyNetwork,GraphObject> map = list.get(i);
                if(map.size()==size) { // if contained in all the networks, remove
                    list.remove(i);
                }
            }
        }
        
        return list;
    }
 
}
