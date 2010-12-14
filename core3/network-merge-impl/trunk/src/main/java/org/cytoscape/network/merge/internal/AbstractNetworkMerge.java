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

package org.cytoscape.network.merge.internal;

import java.util.List;
import java.util.Vector;
import java.util.Iterator;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.CyTableEntry;


import org.cytoscape.work.TaskMonitor;

/**
 * NetworkMerge implement
 */
public abstract class AbstractNetworkMerge implements NetworkMerge {
     protected final NetworkMergeParameter parameter;
     protected TaskMonitor taskMonitor;
     protected boolean interrupted; // to enable cancel of the network merge operation

     public AbstractNetworkMerge(final NetworkMergeParameter parameter) {
             if (parameter==null) {
                     throw new NullPointerException();
             }

             this.parameter = parameter;
             taskMonitor = null;
             interrupted = false;
     }
     
     public void interrupt() {
            interrupted = true;
     }
     
     public void setTaskMonitor(final TaskMonitor taskMonitor) {
            this.taskMonitor = taskMonitor;
     }

     /**
     * Check whether two nodes match
     *
     * @param net1,net2 two networks
     * @param n1,n2 two nodes belongs to net1 and net2 respectively
     * 
     * @return true if n1 and n2 matches
     */
    public abstract boolean matchNode(CyNetwork net1, CyNode n1, CyNetwork net2, CyNode n2);
    
    /**
     * Merge (matched) nodes into one
     * 
     * @param mapNetNode 
     *              map of network to node, node in the network to be merged
     * 
     * @return merged Node
     */
    public abstract CyNode mergeNode(Map<CyNetwork,Set<CyNode>> mapNetNode);
    
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
    public abstract CyEdge mergeEdge(Map<CyNetwork,Set<CyNode>> mapNetEdge, CyNode source, 
                                CyNode target, String interaction, boolean directed);
    
    /**
     * Check whether two edges match
     *
     * @param net1,net2 two networks
     * @param e1,e2 two edges belongs to net1 and net2 respectively
     * 
     * @return true if n1 and n2 matches
     */
    public boolean matchEdge(final CyNetwork net1, CyEdge e1, final CyNetwork net2, CyEdge e2) {
        if (net1==null || e1==null || e2==null) {
            throw new NullPointerException();
        }
        
        //TODO should interaction be considered or not?
        final CyTable net1EdgeAttrs = net1.getDefaultEdgeTable();
        final CyTable net2EdgeAttrs = net2.getDefaultEdgeTable();

        String i1 = net1EdgeAttrs.get("interaction",String.class);
        String i2 = net2EdgeAttrs.get("interaction",String.class);


        if ((i1==null&&i2!=null) || (i1!=null&&i2==null)) {
                return false;
        }

        if (i1!=null && !i1.equals(i2))
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
     * {@inheritDoc}
     */
    @Override
    public CyNetwork mergeNetwork(final List<CyNetwork> networks, final Operation op, final String title) {
        if (networks==null || op==null || title==null) {
            throw new java.lang.NullPointerException();
        }
        
        if (networks.isEmpty()) {
            throw new IllegalArgumentException("No merging network");
        }
        
        if (title.length()==0) {
            throw new IllegalArgumentException("Empty title");
        }
        
        // get node matching list
        List<Map<CyNetwork,Set<CyTableEntry>>> matchedNodeList = getMatchedList(networks,true);
        
        matchedNodeList = selectMatchedGOList(matchedNodeList, op, networks);

        final Map<CyNode,CyNode> mapNN = new HashMap<CyNode,CyNode>(); // save information on mapping from original nodes to merged nodes
                                                         // to use when merge edges
        
        // merge nodes in the list
        final int nNode = matchedNodeList.size();
        List<CyNode> nodes = new Vector<CyNode>(nNode);
        for (int i=0; i<nNode; i++) {
            if (interrupted) return null;
            updateTaskMonitor("Merging nodes...\n"+i+"/"+nNode,(i+1)*1.0/nNode);
            
            final Map<CyNetwork,Set<CyTableEntry>> mapNetNode = matchedNodeList.get(i);
            final CyNode node = mergeNode(mapNetNode);
            nodes.add(node);
            
            final Iterator<Set<CyTableEntry>> itNodes = mapNetNode.values().iterator();
            while (itNodes.hasNext()) {
                final Set<CyTableEntry> nodes_ori = itNodes.next();
                final Iterator<CyTableEntry> itNode = nodes_ori.iterator();
                while (itNode.hasNext()) {
                    final CyNode node_ori = (CyNode) itNode.next();
                    mapNN.put(node_ori, node);                    
                }
            }            
        }
        updateTaskMonitor("Merging nodes completed",1.0);
        
        // match edges
        List<Map<CyNetwork,Set<CyTableEntry>>> matchedEdgeList = getMatchedList(networks,false);

        matchedEdgeList = selectMatchedGOList(matchedEdgeList, op, networks);
        
        // merge edges
        final int nEdge = matchedEdgeList.size();
        final List<CyEdge> edges = new Vector<CyEdge>(nEdge);
        for (int i=0; i<nEdge; i++) {
            if (interrupted) return null;
            updateTaskMonitor("Merging edges...\n"+i+"/"+nEdge,(i+1)/nEdge);
            
            final Map<CyNetwork,Set<CyTableEntry>> mapNetEdge = matchedEdgeList.get(i);
            
            // get the source and target nodes in merged network
            final Iterator<Set<CyTableEntry>> itEdges = mapNetEdge.values().iterator();
                       
            final Set<CyTableEntry> edgeSet = itEdges.next();
            if (edgeSet==null||edgeSet.isEmpty()) {
                throw new java.lang.IllegalStateException("Null or empty edge set");
            }
            
            final CyEdge edge_ori = (CyEdge) edgeSet.iterator().next();
            final CyNode source = mapNN.get(edge_ori.getSource());
            final CyNode target = mapNN.get(edge_ori.getTarget());
            if (source==null||target==null) { // some of the node may be exluded when intersection or difference
                continue;
            }
            
            final boolean directed = edge_ori.isDirected();
            final CyAttributes attributes = Cytoscape.getEdgeAttributes();
            final String interaction = (String)attributes.getAttribute(edge_ori.getIdentifier(),Semantics.INTERACTION);
            
            final CyEdge edge = mergeEdge(mapNetEdge,source,target,interaction,directed);
            edges.add(edge);
        }
        updateTaskMonitor("Merging edges completed",1.0);
        
        // create new network
        final CyNetwork network = Cytoscape.createNetwork(nodes, edges, title);
                
        updateTaskMonitor("Successfully merged the selected "+networks.size()+" networks into network "+title+" with "+nNode+" nodes and "+nEdge+" edges",1.0);
        
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
    protected List<Map<CyNetwork,Set<CyTableEntry>>> getMatchedList(final List<CyNetwork> networks, final boolean isNode) {
        if (networks==null) {
            throw new java.lang.NullPointerException();
        }
        
        if (networks.isEmpty()) {
            throw new java.lang.IllegalArgumentException("No merging network");
        }
                
        final List<Map<CyNetwork,Set<CyTableEntry>>> matchedList = new Vector<Map<CyNetwork,Set<CyTableEntry>>>();
        
        final int nNet = networks.size();
        
        // Get the total number nodes/edge to calculate the status
        int totalGO=0, processedGO=0;
        for (int i=0; i<nNet; i++) {
            final CyNetwork net = networks.get(i);
            totalGO += isNode?net.getNodeCount():net.getEdgeCount();
        }
        
        for (int i=0; i<nNet; i++) {
            
            
            final CyNetwork net1 = networks.get(i);
            final Iterator<CyTableEntry> it;
            if (isNode) {
            	it = (Iterator<CyTableEntry>)net1.getNodeList().iterator(); 
            } else { //edge
            	it = net1.getEdgeList().iterator();
            }
            
            while (it.hasNext()) {
                if (interrupted) return null;
                updateTaskMonitor("Matching "+(isNode?"nodes":"edges")+"...\n"+processedGO+"/"+totalGO,processedGO/totalGO);
                processedGO++;
                
                final CyTableEntry go1 = it.next();
                
                // chech whether any nodes in the matchedNodeList match with this node
                // if yes, add to the list, else add a new map to the list
                boolean matched = false;
                final int n = matchedList.size();
                int j=0;
                for (; j<n; j++) {
                    final Map<CyNetwork,Set<CyTableEntry>> matchedGO = matchedList.get(j);
                    final Iterator<CyNetwork> itNet = matchedGO.keySet().iterator();
                    while (itNet.hasNext()) {
                        final CyNetwork net2 = itNet.next();
                        //if (net1==net2) continue; // assume the same network don't have nodes match to each other
                        if (!parameter.inNetworkMergeEnabled() && net1==net2) continue;

                        final Set<CyTableEntry> gos2 = matchedGO.get(net2);
                        if (gos2!=null) {
                        	CyTableEntry go2 = gos2.iterator().next();
                            if (isNode) { //NODE
                                matched = matchNode(net1,(CyNode)go1,net2,(CyNode)go2);
                            } else {// EDGE
                                matched = matchEdge(net1,(CyEdge)go1,net2,(CyEdge)go2);
                            }
                            if (matched) {
                                Set<CyTableEntry> gos1 = matchedGO.get(net1);
                                if (gos1==null) {
                                    gos1 = new HashSet<CyTableEntry>();
                                    matchedGO.put(net1, gos1);
                                }
                                gos1.add(go1);
                                break;
                            }
                        }
                    }
                    if (matched) {
                        break;
                    }
                }                
                if (!matched) { //no matched node found, add new map to the list
                    final Map<CyNetwork,Set<CyTableEntry>> matchedGO = new HashMap<CyNetwork,Set<CyTableEntry>>();
                    Set<CyTableEntry> gos1 = new HashSet<CyTableEntry>();
                    gos1.add(go1);
                    matchedGO.put(net1, gos1);
                    matchedList.add(matchedGO);
                }
                
            }
            
        }
        
        updateTaskMonitor("Matching "+(isNode?"nodes":"edges")+" completed",1.0);
        
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
    protected List<Map<CyNetwork,Set<CyTableEntry>>> selectMatchedGOList(final List<Map<CyNetwork,Set<CyTableEntry>>> matchedGOList,
                                                                          final Operation op, 
                                                                          final List<CyNetwork> networks) {
        if (matchedGOList==null || op==null) {
            throw new java.lang.NullPointerException();
        }        
        
        int nnet = networks.size();
        
        if (op==Operation.UNION) {
            return matchedGOList;
        } else if (op==Operation.INTERSECTION) {
            List<Map<CyNetwork,Set<CyTableEntry>>> list = new Vector<Map<CyNetwork,Set<CyTableEntry>>>();
            for (Map<CyNetwork,Set<CyTableEntry>> map:matchedGOList) {
                if (map.size()==nnet) {// if contained in all the networks
                    list.add(map);
                }
            }

            return list;
        } else { //if (op==Operation.DIFFERENCE)
            List<Map<CyNetwork,Set<CyTableEntry>>> list = new Vector<Map<CyNetwork,Set<CyTableEntry>>>();
            if (nnet<2) return list;
            
            CyNetwork net1 = networks.get(0);
            CyNetwork net2 = networks.get(1);
            
            for (Map<CyNetwork,Set<CyTableEntry>> map:matchedGOList) {
                if (map.containsKey(net1)&&!map.containsKey(net2)) {
                    list.add(map);
                }
            }

            return list;
        }        
    }
    
    private void updateTaskMonitor(String status, double percentage) {
        if (this.taskMonitor!=null) {
            taskMonitor.setStatusMessage(status);
            taskMonitor.setProgress(percentage);
        }
    }
}
