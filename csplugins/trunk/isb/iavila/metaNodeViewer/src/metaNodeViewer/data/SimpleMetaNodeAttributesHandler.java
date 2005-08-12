package metaNodeViewer.data;
//import cytoscape.data.GraphObjAttributes;
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

// TODO: method setNodeAttributes(CyNetwork cy_network,int metanode_root_index,
// int[] children_nodes_root_indices) has commented
// code that is crashing due to GraphObjAttributes. Since we think we are going
// to refactor/rewrite GraphObjAttrbutes, leave it
// as it is and fix later. iliana.
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
	public String assignName(CyNetwork cy_net, int metanode_root_index) {

		RootGraph rootGraph = cy_net.getRootGraph();

		Node node = rootGraph.getNode(metanode_root_index);
		if (node == null) {
			return null;
		}
		String unique_name = getCanonicalMetaName(metanode_root_index);
		String common_name = getCommonMetaName(metanode_root_index, cy_net);
		Cytoscape.getNodeNetworkData().addNameMapping(unique_name, node);
		cy_net.setNodeAttributeValue(node, Semantics.COMMON_NAME, common_name);
		return unique_name;
	} // end assignName

	/**
	 * Simply calls separate methods for nodes then edges
	 */
	public boolean setAttributes(CyNetwork cy_network, int metanode_root_index,
			int[] children_nodes_root_indices,
			AbstractIntIntMap meta_edge_to_child_edge) {
		setNodeAttributes(cy_network, metanode_root_index,
				children_nodes_root_indices);
		setEdgeAttributes(cy_network, metanode_root_index,
				meta_edge_to_child_edge);
		return true;
	} // end setAttributes

	/**
	 * takes union of all children attributes, treating canonical and common
	 * name as special cases
	 */
	public boolean setNodeAttributes(CyNetwork cy_network,
			int metanode_root_index, int[] children_nodes_root_indices) {
		RootGraph rootGraph = cy_network.getRootGraph();
		Node metaNode = rootGraph.getNode(metanode_root_index);
		if (metaNode == null)
			return false;
		String metaName = (String) cy_network.getNodeAttributeValue(metaNode,
				Semantics.CANONICAL_NAME);
		if (metaName == null) {
			metaName = assignName(cy_network, metanode_root_index);
		}
		HashSet metaAttrs = new HashSet(); // the set of attributes for meta
										   // node

		// get children Nodes instead of indices
		Node[] childrenNodes = new Node[children_nodes_root_indices.length];
		for (int i = 0; i < children_nodes_root_indices.length; i++) {
			int childIndex = children_nodes_root_indices[i];
			childrenNodes[i] = rootGraph.getNode(childIndex);
		}// for i

		// iterate over attributes of children nodes
		String[] childrenAtts = Cytoscape.getNodeAttributesList(childrenNodes);
		for (int i = 0; i < childrenAtts.length; i++) {
			String attrName = childrenAtts[i];
			if (attrName.equals(Semantics.CANONICAL_NAME)
					|| attrName.equals(Semantics.COMMON_NAME)) {
				continue; // reserved
			}

			// iterate over children, constructing set of values for this attr
			HashSet uniqueValues = new HashSet();
			for (int j = 0; j < childrenNodes.length; j++) {
				Object value = cy_network.getNodeAttributeValue(
						childrenNodes[j], attrName);
				if (value instanceof java.lang.reflect.Array) {
					Object[] valueArray = (Object[]) value;
					for (int k = 0; k < valueArray.length; k++) {
						uniqueValues.add(valueArray[k]);
					}
				} else {
					uniqueValues.add(value);
				}
			}// for j

			// add to node attributes for meta node
			if (attrName == null) {
				System.out.println("attrName null");
			}

			if (metaName == null) {
				System.out.println("metaName null");
			}

			// Object[] array = uniqueValues.toArray();
			// for ( int i = 0; i < array.length; ++i ) {
			// System.out.println( "uv :"+i+ " "+array[i] );
			// }
			// nodeAttr.set(attrName, metaName, uniqueValues.toArray());
			
			// THIS IS THROWING AN EXCEPTION - GraphObjAttributes expects a
			// String but it gets an Object
			// need to solve this in GraphObjAttributes, or add a method in
			// CyNetwork to 'append' values
			// to an attribute:
			
			// cy_network.setNodeAttributeValue(metaNode, attrName,
			// uniqueValues.toArray());
			// System.err.println("DBG " +attrName+ " " +metaName+ " "
			// +uniqueValues);
			
			// For now, simply add the "_mn" ending to the attribute name so
			// that it is a new attribute
			Cytoscape.setNodeAttributeValue(metaNode, (attrName + "_mn"), uniqueValues.toArray());
		}
		return true;
	} // end setNodeAttributes

	/**
	 * Copies all edge attributes from child to meta edges
	 */
	public boolean setEdgeAttributes(CyNetwork cy_network,
			int metanode_root_index, AbstractIntIntMap meta_edge_to_child_edge) {

		// get graph and attributes
		RootGraph rootGraph = cy_network.getRootGraph();

		// copy over edge attributes, including edge names
		// -- also merges edges with same name
		usedEdgeNames = new HashSet();
		meta_edge_to_child_edge.forEachPair(new CopyEdgeAttr());

		return true;
	} // end setEdgeAttributes

	public boolean removeFromAttributes(CyNetwork cy_network,
			int metanode_root_index, int[] meta_edge_root_indices) {

		return true;
	} // end removeFromAttributes

	public boolean removeMetaEdgesFromAttributes(CyNetwork cy_network,
			int metanode_root_index, int[] meta_edge_root_indices) {

		return true;
	} // end removeMetaEdgesFromAttributes

	/**
	 * Method to encapsulate the canonical naming of meta nodes and edges
	 */
	protected String getCanonicalMetaName(int metanode_root_index) {
		if(metanode_root_index < 0){
			return "MetaNode_" + Integer.toString((metanode_root_index * -1));	
		}
        // Cytoscape 2.1 does not have negative indeces (I think)
		return "MetaNode_" + Integer.toString(metanode_root_index);
		
	}

	/**
	 * @return a String with the concatenated canonical names of the children of
	 *         the given meta-node
	 */
	protected String getCommonMetaName(int metanode_root_index,
			CyNetwork cy_network) {

		RootGraph rootGraph = cy_network.getRootGraph();
		String commonName = new String();
		List children = rootGraph.nodeMetaChildrenList(metanode_root_index);
		if (children == null)
			return null;
		int count = 0;
		for (Iterator it = children.iterator(); it.hasNext();) {
			count++;
			Node child = (Node) it.next();
			commonName += cy_network.getNodeAttributeValue(child,
					Semantics.CANONICAL_NAME);
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

		public boolean apply(int metaEdgeIndex, int childEdgeIndex){
			
			RootGraph rootGraph = Cytoscape.getRootGraph();
			// get edge info
			Edge metaEdge = rootGraph.getEdge(metaEdgeIndex);
			Edge childEdge = rootGraph.getEdge(childEdgeIndex);
			if (metaEdge == null || childEdge == null) {
				throw new NullPointerException("metaEdge or childEdge is null");
			}
			//String childEdgeName = (String) Cytoscape.getEdgeAttributeValue(
			//		childEdge, Semantics.CANONICAL_NAME);
			String metaEdgeName = "unknown";

			// infer the metaNode and use it to name the metaEdge
			String interaction = (String) Cytoscape.getEdgeAttributeValue(
					childEdge, Semantics.INTERACTION);
			Node sourceNode = metaEdge.getSource(); // null 6/24/05, passes ok
													// now 8/10/2005
			Node targetNode = metaEdge.getTarget();
			// TODO: What if there are multiple edges between two nodes, then
			// the meta-edges will have
			// the same name! (is this a problem?)
			if (rootGraph.nodeMetaChildrenList(sourceNode.getRootGraphIndex()) != null // crash
																						// 6/24/05,
																						// ok
																						// now
																						// 8/10/2005
					&& rootGraph.nodeMetaChildrenList(sourceNode.getRootGraphIndex()).size() > 0) {
				metaEdgeName = Cytoscape.getNodeAttributeValue(sourceNode,
						Semantics.CANONICAL_NAME)
						+ " ("
						+ interaction
						+ ") "
						+ Cytoscape.getNodeAttributeValue(targetNode,
								Semantics.CANONICAL_NAME);
			} else if (rootGraph.nodeMetaChildrenList(targetNode.getRootGraphIndex()) != null
					&& rootGraph.nodeMetaChildrenList(targetNode.getRootGraphIndex()).size() > 0) {
				metaEdgeName = Cytoscape.getNodeAttributeValue(metaEdge
						.getSource(), Semantics.CANONICAL_NAME)
						+ " ("
						+ interaction
						+ ") "
						+ Cytoscape.getNodeAttributeValue(targetNode,
								Semantics.CANONICAL_NAME);
			}
			
			Cytoscape.getEdgeNetworkData().addNameMapping(metaEdgeName, metaEdge);
			
			// Transfer attributes b/w edges--
			// if edge name redundant, merge attrs with existing name and remove

			// if (usedEdgeNames.contains(metaEdgeName)) {

			String[] allAttrNames = Cytoscape.getEdgeAttributesList();
			for (int i = 0; i < allAttrNames.length; i++) {
				String attrName = allAttrNames[i];
				
				if (attrName.equals(Semantics.INTERACTION))
					continue; // reserved
				
				if (attrName.equals(Semantics.CANONICAL_NAME))
					continue; // reserved
				 
				Object childValue = Cytoscape.getEdgeAttributeValue(
							childEdge, attrName);
				Object metaValue = Cytoscape.getEdgeAttributeValue(
						metaEdge, attrName);

				// take union of previous and new attr values
				HashSet uniqueValues = new HashSet();
				if (childValue instanceof java.lang.reflect.Array) {
					Object[] valueArray = (Object[]) childValue;
					for (int j = 0; j < valueArray.length; j++) {
						uniqueValues.add(valueArray[j]);
					}
				} else {
					uniqueValues.add(childValue);
				}
				if (metaValue instanceof java.lang.reflect.Array) {
					Object[] valueArray = (Object[]) metaValue;
					for (int j = 0; j < valueArray.length; j++) {
						uniqueValues.add(valueArray[j]);
					}
				} else {
					uniqueValues.add(metaValue);
				}
				// Again, do the trick to avoid an exception here
				// by adding the "_mn" sufix to the attribute name
				Cytoscape.setEdgeAttributeValue(metaEdge, (attrName + "_mn"),
						uniqueValues.toArray());
				}
		return true;
		}
		
	} //	 end CopyEdgeAttr

} // end class
