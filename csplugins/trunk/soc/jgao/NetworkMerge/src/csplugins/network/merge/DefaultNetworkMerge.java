/* File: DefaultNetworkMerge.java

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

import java.util.Vector;
import java.util.Iterator;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.Arrays;

import cytoscape.Cytoscape;
import cytoscape.CyNetwork;
import cytoscape.data.CyAttributes;
import cytoscape.data.Semantics;


import giny.model.Node;
import giny.model.Edge;
import giny.model.GraphObject;

/**
 * Attribute based Network merge
 * 
 * 
 */
public class DefaultNetworkMerge extends AbstractNetworkMerge{
    private MatchingAttribute matchingAttribute;
    private AttributeMapping nodeAttributeMapping;
    private AttributeMapping edgeAttributeMapping;   
    
    public DefaultNetworkMerge(MatchingAttribute matchingAttribute,
                                AttributeMapping nodeAttributeMapping,
                                AttributeMapping edgeAttributeMapping) {
        this.matchingAttribute = matchingAttribute;
        this.nodeAttributeMapping = nodeAttributeMapping;
        this.edgeAttributeMapping = edgeAttributeMapping;
    }
    
    
    /**
     * Check whether two nodes match
     *
     * @param net1,net2 two networks
     * @param n1,n2 two nodes belongs to net1 and net2 respectively
     * 
     * @return true if n1 and n2 matches
     */
    public boolean matchNode(CyNetwork net1, Node n1, CyNetwork net2, Node n2) {
        String attr1 = matchingAttribute.getAttributeForMatching(net1.getIdentifier());
        String attr2 = matchingAttribute.getAttributeForMatching(net2.getIdentifier());
                        
        //TODO: remove in cytoscape3
        if (attr1.compareTo("ID")==0) {
            attr1 = Semantics.CANONICAL_NAME;
        }
        if (attr1.compareTo("ID")==0) {
            attr1 = Semantics.CANONICAL_NAME;
        }//TODO: remove in cytoscape3
        
        CyAttributes attributes = Cytoscape.getNodeAttributes();
        Object value1 = attributes.getAttribute(n1.getIdentifier(), attr1);
        Object value2 = attributes.getAttribute(n2.getIdentifier(), attr2);
        return value1.equals(value2); //using ID mapping here
    }
    
    /**
     * Merge (matched) nodes into one. This method will be refactored in Cytoscape3
     * 
     * @param mapNetNode 
     *              map of network to node, node in the network to be merged
     * 
     * @return merged Node
     */
    public Node mergeNode(Map<CyNetwork,GraphObject> mapNetNode) {
        //TODO: refactor in Cytoscape3, 
        // in 2.x node with the same identifier be the same node
        // and different nodes must have different identifier.
        // Is this true in 3.0?
        if (mapNetNode==null||mapNetNode.isEmpty()) {
            return null;
        }
        
        // Assign ID and canonicalName in resulting network        
        Set<GraphObject> nodes = new HashSet<GraphObject>(mapNetNode.values());
        Iterator<GraphObject> itNode = nodes.iterator();
        String id = new String(itNode.next().getIdentifier());
        
        if (nodes.size()>1) { // if more than 1 nodes to be merged, assign the id 
                              // as the combination of all identifiers
            while (itNode.hasNext()) {
                Node node = (Node) itNode.next();
                id += "_"+node.getIdentifier();
            }

            // if node with this id exist, get new one
            String appendix = "";
            int app = 0;
            while (Cytoscape.getCyNode(id+appendix)!=null) {
                appendix = ""+ ++app; 
            }
            id += appendix;            
        }
        
        // Get the node with id or create a new node
        // for attribute confilict handling, introduce a conflict node here?
        Node node = Cytoscape.getCyNode(id, true);

        // merging attribute according to attrbute mapping
        CyAttributes cyAttributes = Cytoscape.getNodeAttributes();
        
        // set the canonicalName the same as id -- remove in Cytoscape3
        cyAttributes.setAttribute(id, Semantics.CANONICAL_NAME, id);
        
        // set other attributes as indicated in attributeMapping        
        setAttribute(id,mapNetNode,cyAttributes,nodeAttributeMapping);

        return node;
    }
    
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
    public Edge mergeEdge(Map<CyNetwork,GraphObject> mapNetEdge, Node source, 
                                Node target, String interaction, boolean directed) {
        //TODO: refactor in Cytoscape3
        if (mapNetEdge==null||mapNetEdge.isEmpty()||source==null||target==null) {
            return null;
        }
        
        // Get the edge or create a new one
        // attribute confilict handling?
        Edge edge = Cytoscape.getCyEdge(source, target, 
                Semantics.INTERACTION, interaction, true, directed); // ID and canonicalName set when created
        String id = edge.getIdentifier();

        // merging attribute according to attrbute mapping
        CyAttributes cyAttributes = Cytoscape.getEdgeAttributes();
        
        // set other attributes as indicated in attributeMapping
        setAttribute(id,mapNetEdge,cyAttributes,edgeAttributeMapping);

        return edge;
    }
    
    /*
     * set attribute for the merge node/edge according to attribute mapping
     * 
     */
    protected void setAttribute(String id, Map<CyNetwork,GraphObject> mapNetGO, CyAttributes cyAttributes, AttributeMapping attributeMapping) {
        final Set<Map.Entry<CyNetwork,GraphObject>> entrySet = mapNetGO.entrySet();
        
        int nattr = attributeMapping.getSizeMergedAttributes();
        for (int i=0; i<nattr; i++) {
            String attr_merged = attributeMapping.getMergedAttribute(i);
            Set values_ori = new HashSet(); // how to handle different attribute type?            
            
            byte type = -1;
            
            Iterator<Map.Entry<CyNetwork,GraphObject>> itEntry = entrySet.iterator();
            while (itEntry.hasNext()) {
                Map.Entry<CyNetwork,GraphObject> entry = itEntry.next();
                CyNetwork network = entry.getKey();
                String attr_ori = attributeMapping.getOriginalAttribute(network.getIdentifier(), i);
                if (attr_ori==null) continue;
                                
                type = cyAttributes.getType(attr_ori);
                
                GraphObject go = entry.getValue();
                String id_ori = go.getIdentifier();
                
                Object value_ori = cyAttributes.getAttribute(id_ori, attr_ori);
                if (value_ori==null) continue; //TODO null attribute 
                if (value_ori.equals("")) continue; //TODO empty attribute 
                values_ori.add(value_ori);
            }
            if (values_ori.isEmpty()) {
                continue;
            } else if (values_ori.size()==1) { // no attribute conflict
                //cyAttributes.setAttribute(id, attr_merged, values_ori.iterator().next().toString()); //TODO modify to support other types
                
                if (!Arrays.asList(cyAttributes.getAttributeNames()).contains(attr_merged)) {
                    cyAttributes.getMultiHashMapDefinition().defineAttribute(attr_merged, type, null);
                }
                
                Object obj = values_ori.iterator().next();
                cyAttributes.getMultiHashMap().setAttributeValue(id, attr_merged, obj, null);
            } else {
                //TODO: modify the code--use a conflict node and put to conflictHandler later
                if (!Arrays.asList(cyAttributes.getAttributeNames()).contains(attr_merged)) {
                    cyAttributes.getMultiHashMapDefinition().defineAttribute(attr_merged, type, null);
                }
                
                Object obj = values_ori.iterator().next();
                cyAttributes.getMultiHashMap().setAttributeValue(id, attr_merged, obj, null);
            }
        }
    }
        
    
}
