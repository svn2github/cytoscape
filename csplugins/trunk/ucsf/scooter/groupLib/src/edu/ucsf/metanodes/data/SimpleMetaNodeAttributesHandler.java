package org.isb.metanodes.data;

import cytoscape.data.*;
import giny.model.*;
import cern.colt.map.AbstractIntIntMap;
import cern.colt.function.IntIntProcedure;
import java.util.*;

import org.mskcc.dataservices.bio.vocab.CommonVocab;

import cytoscape.*;

/**
 * Implementation of MetaNodesAttributesHandler that transfers the union of all
 * attributes on the child nodes to the parent meta-nodes.
 * 
 * <p>
 * This is a specific implementation of MetaNodesAttributesHandler that:
 * </p>
 * <p>
 * 1) Handles canonical and common naming of the new meta_node and its
 * associated meta_edges. 2) Transfers the union of all attributes on the child
 * nodes to the parent meta node. 3) Transfers edge attributes from each child
 * edge to its corresponding meta edge 4) Collapses meta edges with the same
 * name, taking the union of their attributes
 * </p>
 * <p>
 * Step 4 above is invoked whenever multiple meta_edges connect from the same
 * source or target. For instance, consider the graph: A pd B A pd C If the user
 * collapses B and C into a meta_node, this will create corresponding
 * meta_edges: A (pd) MetaNode1 A (pd) MetaNode1 which will have their
 * attributes transferred and then get themselves collapsed to a single edge. Of
 * course, this was a specific design decision on my part and I could have
 * handled things differently. But in this case where you are collapsing many
 * (N) nodes with the same neighbor, it seems undesirable to end up with N edges
 * connecting this neighbor to the meta_node.
 * </p>
 * 
 * @author Trey Ideker trey@bioeng.ucsd.edu
 * @author Iliana Avila iavila@systemsbiology.org
 * @version 1.0
 */
// TODO: internal class CopyEdgeAttr needs to be looked at. See comments.
// iliana.
// TODO: Implement removeFromAttributes() -iliana
// TODO: Imeplemnt removeMetaEdgesFromAttributes() -iliana
public class SimpleMetaNodeAttributesHandler implements
        MetaNodeAttributesHandler {

    /**
     * Tracks the edge names that have been examined so far
     */
    protected HashSet usedEdgeNames;

    /**
     * The name of the node attribute to which meta-node names should be assigned to
     */
    protected String nodeLabelAttribute = DEFAULT_NODE_LABEL_ATTRIBUTE;
    
    /**
	 * Sets the name of the node attribute to which meta-node names should be assigned to,
	 * if not set, it is DEFAULT_NODE_LABEL_ATTRIBUTE
	 * 
	 * @param attribute_name the name of a node attribute of type String, if it is not of type
	 * String, it is not set
	 */
	public void setNodeLabelAttribute (String attribute_name){
		if(Cytoscape.getNodeAttributes().getType(attribute_name) != CyAttributes.TYPE_STRING){
			return;
		}
		this.nodeLabelAttribute = attribute_name;
	}
	
	/**
	 * Gets the name of the node attribute to which meta-node names should be assigned to,
	 * if not set, it is DEFAULT_NODE_LABEL_ATTRIBUTE
	 * 
	 * @return a String representing the name of the node attribute
	 */
	public String getNodeLabelAttribute (){
		return this.nodeLabelAttribute;
	}
    
    
    /**
     * Transfers all children names to meta node name
     */
    public String assignName(CyNetwork cy_net, CyNode node) {

        if (node == null) {
            return null;
        }
        String unique_name = getCanonicalMetaName(node);
        String label_name = getCommonMetaName(node, cy_net);
        CyAttributes nodeAtts = Cytoscape.getNodeAttributes();
        node.setIdentifier(unique_name);
        Cytoscape.getNodeAttributes().setAttribute(node.getIdentifier(), getNodeLabelAttribute(), label_name);
        return unique_name;
    } // end assignName

    /**
     * Simply calls separate methods for nodes then edges
     */
    public boolean setAttributes(CyNetwork cy_network, CyNode node,
            ArrayList children, AbstractIntIntMap meta_edge_to_child_edge) {
        setNodeAttributes(cy_network, node, children);
        setEdgeAttributes(cy_network, node, meta_edge_to_child_edge);
        return true;
    } // end setAttributes

    /**
     * takes union of all children attributes, treating canonical and common
     * name as special cases
     */
    public boolean setNodeAttributes(CyNetwork cy_network, CyNode node, ArrayList children) {
        if (node == null)
            return false;
        CyAttributes nodeAtts = Cytoscape.getNodeAttributes();
        String metaName = node.getIdentifier();
        if (metaName == null) {
            metaName = assignName(cy_network, node);
        }
      
        // metanode
        // iterate over attributes of children nodes
        String[] childrenAtts = nodeAtts.getAttributeNames();
        for (int i = 0; i < childrenAtts.length; i++) {
            String attrName = childrenAtts[i];
            if (attrName.equals("identifier") || attrName.equals(getNodeLabelAttribute()))
                continue; // reserved
            // iterate over children, constructing set of values for this attr
            HashSet uniqueValues = new HashSet();
            Map simpleMap = null;
            boolean typeSupported = true;
            for (int j = 0; j < children.size(); j++) {
                String nodeID = ((CyNode)children.get(j)).getIdentifier();
                byte valueType = nodeAtts.getType(attrName);
                
                if (valueType == CyAttributes.TYPE_STRING) {
                    uniqueValues.add(nodeAtts.getStringAttribute(nodeID,
                            attrName));
                } else if (valueType == CyAttributes.TYPE_FLOATING) {
                    uniqueValues.add(nodeAtts.getDoubleAttribute(nodeID,
                            attrName));
                } else if (valueType == CyAttributes.TYPE_INTEGER) {
                    uniqueValues.add(nodeAtts.getIntegerAttribute(nodeID,
                            attrName));
                } else if (valueType == CyAttributes.TYPE_SIMPLE_LIST) {
                    uniqueValues.addAll(nodeAtts.getAttributeList(nodeID,
                            attrName));
                } else if (valueType == CyAttributes.TYPE_SIMPLE_MAP) {
                    if (simpleMap == null)
                        simpleMap = new HashMap();
                    simpleMap
                            .putAll(nodeAtts.getAttributeMap(nodeID, attrName));
                } else if (valueType == CyAttributes.TYPE_BOOLEAN) {
                    uniqueValues.add(nodeAtts.getBooleanAttribute(nodeID,
                            attrName));
                } else {
                    typeSupported = false;
                }

                if (!typeSupported)
                    continue; // go to the next attribute

            }// for j

            // For now, simply add the "_mn" ending to the attribute name so
            // that it is a new attribute
            String metaAttrName = attrName + "_mn";
            if(simpleMap != null){
                nodeAtts.setAttributeMap(node.getIdentifier(),metaAttrName, simpleMap);
            }else if(uniqueValues.size() > 0){
               nodeAtts.setAttributeList(node.getIdentifier(),metaAttrName, new ArrayList(uniqueValues));
            }
        }
        return true;
    } // end setNodeAttributes

    /**
     * Copies all edge attributes from child to meta edges
     */
    public boolean setEdgeAttributes(CyNetwork cy_network,
           CyNode node, AbstractIntIntMap meta_edge_to_child_edge) {

        // copy over edge attributes, including edge names
        // -- also merges edges with same name
        usedEdgeNames = new HashSet();
        meta_edge_to_child_edge.forEachPair(new CopyEdgeAttr());

        return true;
    } // end setEdgeAttributes

    public boolean removeFromAttributes(CyNetwork cy_network,CyNode node, ArrayList children) {
        return true;
    } // end removeFromAttributes

    public boolean removeMetaEdgesFromAttributes(CyNetwork cy_network,CyNode metanode_root_index, ArrayList children) {
        return true;
    } // end removeMetaEdgesFromAttributes

    /**
     * Method to encapsulate the canonical naming of meta nodes and edges
     */
    protected String getCanonicalMetaName(CyNode node) {
        return "MetaNode_" + Integer.toString(node.getRootGraphIndex());
    }

    /**
     * @return a String with the concatenated canonical names of the children of
     *         the given meta-node
     */
    protected String getCommonMetaName(CyNode node,CyNetwork cy_network) {

        RootGraph rootGraph = cy_network.getRootGraph();
        String commonName = new String();
        List children = rootGraph.nodeMetaChildrenList(node.getRootGraphIndex());
        if (children == null)
            return null;
        int count = 0;
        for (Iterator it = children.iterator(); it.hasNext();) {
            count++;
            CyNode child = (CyNode) it.next();
            commonName += child.getIdentifier();
            // if (it.hasNext() && (count % 3) == 0) commonName += '\n';
            if (it.hasNext())
                commonName += ",";
        }
        return commonName;
    }

    /**
     * Procedure class to copy over edge attributes
     */
    protected class CopyEdgeAttr implements IntIntProcedure {

        public boolean apply(int metaEdgeIndex, int childEdgeIndex) {

            RootGraph rootGraph = Cytoscape.getRootGraph();
            // get edge info
            CyEdge metaEdge = (CyEdge)rootGraph.getEdge(metaEdgeIndex);
            CyEdge childEdge = (CyEdge)rootGraph.getEdge(childEdgeIndex);
            if (metaEdge == null || childEdge == null) {
                throw new NullPointerException("metaEdge or childEdge is null");
            }

            // The code below is problematic, once an edge is given a unique ID, it should not be changed!
            // AbstractMetaNodeModeler gived names to edges!
//            String metaEdgeName = "unknown";
//
//            // infer the metaNode and use it to name the metaEdge
//            String interaction = (String) Cytoscape.getEdgeAttributes().getStringAttribute(childEdge.getIdentifier(),Semantics.INTERACTION);
//            CyNode sourceNode = (CyNode)metaEdge.getSource();
//            CyNode targetNode = (CyNode)metaEdge.getTarget();
//            // TODO: What if there are multiple edges between two nodes, then
//            // the meta-edges will have
//            // the same name! (is this a problem?)
//            if (rootGraph.nodeMetaChildrenList(sourceNode.getRootGraphIndex()) != null 
//                    && rootGraph.nodeMetaChildrenList(
//                            sourceNode.getRootGraphIndex()).size() > 0) {
//                
//                metaEdgeName = sourceNode.getIdentifier()+" ("+interaction+") "+targetNode.getIdentifier();
//            
//            } else if (rootGraph.nodeMetaChildrenList(targetNode
//                    .getRootGraphIndex()) != null
//                    && rootGraph.nodeMetaChildrenList(
//                            targetNode.getRootGraphIndex()).size() > 0) {
//                
//                metaEdgeName = sourceNode.getIdentifier()+" ("+interaction+") "+targetNode.getIdentifier();
//            
//            
//            }
//            metaEdge.setIdentifier(metaEdgeName);

           
            // Transfer attributes b/w edges--
            // if edge name redundant, merge attrs with existing name and remove
            // if (usedEdgeNames.contains(metaEdgeName)) {

           CyAttributes edgeAtts = Cytoscape.getEdgeAttributes();
            String[] allAttrNames = edgeAtts.getAttributeNames();
            for (int i = 0; i < allAttrNames.length; i++) {
                String attrName = allAttrNames[i];
                if (attrName.equals(Semantics.INTERACTION) || attrName.equals("identifier") || 
                		attrName.equals(getNodeLabelAttribute()))
                	continue; // reserved

                HashSet uniqueValues = new HashSet();
                Map simpleMap = null;
                String childEdgeID = childEdge.getIdentifier();
                boolean typeSupported = true;
                
                byte valueType = edgeAtts.getType(attrName);
                if (valueType == CyAttributes.TYPE_STRING) {
                    String value = edgeAtts.getStringAttribute(childEdgeID,
                            attrName);
                    if(value != null) uniqueValues.add(value);
                } else if (valueType == CyAttributes.TYPE_FLOATING) {
                    Double value = edgeAtts.getDoubleAttribute(childEdgeID,
                            attrName);
                    if(value != null)uniqueValues.add(value);
                } else if (valueType == CyAttributes.TYPE_INTEGER) {
                    Integer value = edgeAtts.getIntegerAttribute(childEdgeID,
                            attrName);
                    if(value != null)uniqueValues.add(value);
                } else if (valueType == CyAttributes.TYPE_SIMPLE_LIST) {
                    List value = edgeAtts.getAttributeList(childEdgeID,
                            attrName);
                    if(value != null)uniqueValues.addAll(value);
                } else if (valueType == CyAttributes.TYPE_SIMPLE_MAP) {
                    Map value = edgeAtts.getAttributeMap(childEdgeID, attrName);
                    if (simpleMap == null)
                        simpleMap = new HashMap();
                    if(value != null)simpleMap.putAll(value);
                } else if (valueType == CyAttributes.TYPE_BOOLEAN) {
                    Boolean value = edgeAtts.getBooleanAttribute(childEdgeID,
                            attrName);
                    if(value != null)uniqueValues.add(value);
                } else {
                    typeSupported = false;
                }

                if (!typeSupported)
                   continue;
            
                // For now, simply add the "_mn" ending to the attribute name so
                // that it is a new attribute
                String metaAttrName = attrName + "_mn";
                if(simpleMap != null){
                    edgeAtts.setAttributeMap(metaEdge.getIdentifier(),metaAttrName, simpleMap);
                }else if(uniqueValues.size() > 0){
                    edgeAtts.setAttributeList(metaEdge.getIdentifier(),metaAttrName, new ArrayList(uniqueValues));
                }
                
            }//for each attribute
            
            return true;
        }

    } // end CopyEdgeAttr

} // end class
