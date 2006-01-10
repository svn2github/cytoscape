package metaNodeViewer.data;

import cytoscape.data.*;
import giny.model.*;
import cern.colt.map.AbstractIntIntMap;
import cern.colt.function.IntIntProcedure;
import java.util.*;
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
     * Transfers all children names to meta node name
     */
    public String assignName(CyNetwork cy_net, CyNode node) {

        if (node == null) {
            return null;
        }
        String unique_name = getCanonicalMetaName(node);
        String common_name = getCommonMetaName(node, cy_net);
        CyAttributes nodeAtts = Cytoscape.getNodeAttributes();
        node.setIdentifier(unique_name);
        nodeAtts.setAttribute(node.getIdentifier(), Semantics.CANONICAL_NAME,
                unique_name);
        nodeAtts.setAttribute(node.getIdentifier(), Semantics.COMMON_NAME,
                common_name);
        // OLD:
        // Cytoscape.getNodeNetworkData().addNameMapping(unique_name, node);
        // cy_net.setNodeAttributeValue(node, Semantics.COMMON_NAME,
        // common_name);
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
        String metaName = nodeAtts.getStringAttribute(node.getIdentifier(),
                Semantics.CANONICAL_NAME);
        if (metaName == null) {
            metaName = assignName(cy_network, node);
        }
      
        // metanode
        // iterate over attributes of children nodes
        String[] childrenAtts = nodeAtts.getAttributeNames();
        for (int i = 0; i < childrenAtts.length; i++) {
            String attrName = childrenAtts[i];
            if (attrName.equals(Semantics.CANONICAL_NAME)
                    || attrName.equals(Semantics.COMMON_NAME)) {
                continue; // reserved
            }

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
            commonName += Cytoscape.getNodeAttributes().getStringAttribute(child.getIdentifier(),Semantics.CANONICAL_NAME);
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
            // String childEdgeName = (String) Cytoscape.getEdgeAttributeValue(
            // childEdge, Semantics.CANONICAL_NAME);
            String metaEdgeName = "unknown";

            // infer the metaNode and use it to name the metaEdge
            String interaction = (String) Cytoscape.getEdgeAttributes().getStringAttribute(childEdge.getIdentifier(),Semantics.INTERACTION);
            CyNode sourceNode = (CyNode)metaEdge.getSource(); // null 6/24/05, passes ok now 8/10/2005
            CyNode targetNode = (CyNode)metaEdge.getTarget();
            // TODO: What if there are multiple edges between two nodes, then
            // the meta-edges will have
            // the same name! (is this a problem?)
            if (rootGraph.nodeMetaChildrenList(sourceNode.getRootGraphIndex()) != null // crash
                    // 6/24/05,
                    // ok
                    // now
                    // 8/10/2005
                    && rootGraph.nodeMetaChildrenList(
                            sourceNode.getRootGraphIndex()).size() > 0) {
                
                metaEdgeName = Cytoscape.getNodeAttributes().getStringAttribute(sourceNode.getIdentifier(),Semantics.CANONICAL_NAME)
                        + " ("
                        + interaction
                        + ") "
                        + Cytoscape.getNodeAttributes().getStringAttribute(targetNode.getIdentifier(),Semantics.CANONICAL_NAME);
            
            } else if (rootGraph.nodeMetaChildrenList(targetNode
                    .getRootGraphIndex()) != null
                    && rootGraph.nodeMetaChildrenList(
                            targetNode.getRootGraphIndex()).size() > 0) {
               
                metaEdgeName = Cytoscape.getNodeAttributes().getStringAttribute(sourceNode.getIdentifier(),Semantics.CANONICAL_NAME)
                + " ("
                + interaction
                + ") "
                + Cytoscape.getNodeAttributes().getStringAttribute(targetNode.getIdentifier(),Semantics.CANONICAL_NAME);
            
            
            }

           Cytoscape.getEdgeAttributes().setAttribute(metaEdge.getIdentifier(),Semantics.CANONICAL_NAME,metaEdgeName);
           
            // Transfer attributes b/w edges--
            // if edge name redundant, merge attrs with existing name and remove
            // if (usedEdgeNames.contains(metaEdgeName)) {

           CyAttributes edgeAtts = Cytoscape.getEdgeAttributes();
            String[] allAttrNames = edgeAtts.getAttributeNames();
            for (int i = 0; i < allAttrNames.length; i++) {
                String attrName = allAttrNames[i];
                if (attrName.equals(Semantics.INTERACTION))
                    continue; // reserved

                if (attrName.equals(Semantics.CANONICAL_NAME))
                    continue; // reserved

                HashSet uniqueValues = new HashSet();
                Map simpleMap = null;
                String childEdgeID = childEdge.getIdentifier();
                boolean typeSupported = true;
                
                byte valueType = edgeAtts.getType(attrName);
                if (valueType == CyAttributes.TYPE_STRING) {
                    uniqueValues.add(edgeAtts.getStringAttribute(childEdgeID,
                            attrName));
                } else if (valueType == CyAttributes.TYPE_FLOATING) {
                    uniqueValues.add(edgeAtts.getDoubleAttribute(childEdgeID,
                            attrName));
                } else if (valueType == CyAttributes.TYPE_INTEGER) {
                    uniqueValues.add(edgeAtts.getIntegerAttribute(childEdgeID,
                            attrName));
                } else if (valueType == CyAttributes.TYPE_SIMPLE_LIST) {
                    uniqueValues.addAll(edgeAtts.getAttributeList(childEdgeID,
                            attrName));
                } else if (valueType == CyAttributes.TYPE_SIMPLE_MAP) {
                    if (simpleMap == null)
                        simpleMap = new HashMap();
                    simpleMap
                            .putAll(edgeAtts.getAttributeMap(childEdgeID, attrName));
                } else if (valueType == CyAttributes.TYPE_BOOLEAN) {
                    uniqueValues.add(edgeAtts.getBooleanAttribute(childEdgeID,
                            attrName));
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
