/**Copyright (c) 2004 Trey Ideker
 * Modified version of Iliana's (ISB) MetaNodeViewer package
 *
 * This is a specific implementation of MetaNodesAttributesHandler that:
 *
 *  1) Handles canonical and common naming of the new meta_node and its
 *     associated meta_edges.
 *  2) Transfers the union of all attributes on the child nodes to the
 *     parent meta node.
 *  3) Transfers edge attributes from each child edge to its corresponding
 *     meta edge
 *  4) Collapses meta edges with the same name, taking the union of their
 *     attributes
 *  
 * Step 4 above is invoked whenever multiple meta_edges connect from the
 * same source or target.  For instance, consider the graph:
 *    A pd B
 *    A pd C
 * If the user collapses B and C into a meta_node, this will create
 * corresponding meta_edges:
 *    A (pd) MetaNode1
 *    A (pd) MetaNode1
 * which will have their attributes transferred and then get themselves
 * collapsed to a single edge.  Of course, this was a specific design
 * decision on my part and I could have handled things differently.  But
 * in this case where you are collapsing many (N) nodes with the same
 * neighbor, it seems undesirable to end up with N edges connecting this
 * neighbor to the meta_node.
 *
 * @author Trey Ideker trey@bioeng.ucsd.edu
 * @version %I%, %G%
 * @since 2.0
**/

package metaNodeViewer.data;

import cytoscape.*;
import cytoscape.data.GraphObjAttributes;
import cytoscape.data.*;
import giny.model.*;
import cern.colt.map.AbstractIntIntMap;
import cern.colt.list.IntArrayList;
import cern.colt.function.IntIntProcedure;
import java.util.*;

public class SimpleMetaNodeAttributesHandler implements MetaNodeAttributesHandler {

    protected GraphObjAttributes nodeAttr;
    protected GraphObjAttributes edgeAttr;
    protected RootGraph rootGraph;
    HashSet usedEdgeNames; // tracks the edge names that have been examined so far
    
    // transfers all children names to meta node name
    public String assignName (CyNetwork cy_net, int metanode_root_index) {
	if (nodeAttr == null) nodeAttr = cy_net.getNodeAttributes();
	if (rootGraph == null) 
	    rootGraph = cy_net.getGraphPerspective().getRootGraph();
	Node node = rootGraph.getNode(metanode_root_index);
	if(node == null) { return null; }
	String unique_name = getCanonicalMetaName (metanode_root_index);
	String common_name = getCommonMetaName (metanode_root_index);
	//System.err.println("Common Name = " + common_name);
	nodeAttr.addNameMapping(unique_name, node);
	nodeAttr.set("canonicalName", unique_name, unique_name);
	nodeAttr.set("commonName", unique_name, common_name);
	return unique_name;
    } // end assignName

    // simply calls separate methods (below) for nodes then edges
    public boolean setAttributes (CyNetwork cy_network, 
				  int metanode_root_index,
				  int [] children_nodes_root_indices,
				  AbstractIntIntMap meta_edge_to_child_edge) {
	setNodeAttributes(cy_network, metanode_root_index, 
			  children_nodes_root_indices);
	setEdgeAttributes(cy_network, metanode_root_index, 
			  meta_edge_to_child_edge);
	return true;
    } // end setAttributes

    // takes union of all children attributes, treating canonical and
    // common name as special cases
    public boolean setNodeAttributes (CyNetwork cy_network, 
				      int metanode_root_index,
				      int [] children_nodes_root_indices) {
	
	// make sure of data structures
	if (nodeAttr == null) nodeAttr = cy_network.getNodeAttributes();
	if (rootGraph == null)
	    rootGraph = cy_network.getGraphPerspective().getRootGraph();
	Node metaNode = rootGraph.getNode(metanode_root_index);
	if (metaNode == null) return false;
	if (nodeAttr.getCanonicalName(metaNode) == null) 
	    assignName(cy_network, metanode_root_index);
	String metaName = nodeAttr.getCanonicalName(metaNode);
	HashSet metaAttrs = new HashSet (); // the set of attributes for meta node

	// iterate over attributes
	String [] allAttrNames = nodeAttr.getAttributeNames();
	for (int j=0; j<allAttrNames.length; j++) {
	    String attrName = allAttrNames[j];
	    if (attrName.equals("canonicalName")) continue; // reserved
	    if (attrName.equals("commonName"))    continue; // reserved

	    // iterate over children, constructing set of values for this attr
	    HashSet uniqueValues = new HashSet ();
	    for (int i=0; i<children_nodes_root_indices.length; i++) {
		int childIndex = children_nodes_root_indices[i];
		Node childNode = rootGraph.getNode(childIndex);
		String childName = nodeAttr.getCanonicalName(childNode);
		Vector childValues = nodeAttr.getList(attrName, childName); 
		uniqueValues.addAll(childValues); 
	    }
	    
	    // add to node attributes for meta node
	    if ( attrName == null )
        System.out.println( "attrName null" );

      if ( metaName == null ) 
        System.out.println( "metaName null" );

      //Object[] array = uniqueValues.toArray();
      //for ( int i = 0; i < array.length; ++i ) {
      //System.out.println( "uv :"+i+ " "+array[i] );
      //}
      nodeAttr.set(attrName, metaName, uniqueValues.toArray());
	    //System.err.println("DBG " +attrName+ " " +metaName+ " " +uniqueValues);
	}
	return true;
    } // end setNodeAttributes
    
    // copies all edge attributes from child to meta edges
    public boolean setEdgeAttributes (CyNetwork cy_network, 
				      int metanode_root_index,
				      AbstractIntIntMap meta_edge_to_child_edge) {

	// get graph and attributes
	if (edgeAttr == null) edgeAttr = cy_network.getEdgeAttributes();
	if (rootGraph == null)
	    rootGraph = cy_network.getGraphPerspective().getRootGraph();

	// copy over edge attributes, including edge names
	// -- also merges edges with same name
	usedEdgeNames = new HashSet ();
	meta_edge_to_child_edge.forEachPair( new CopyEdgeAttr() );

	return true;
    } // end setEdgeAttributes
    	

    public boolean removeFromAttributes (CyNetwork cy_network,
					 int metanode_root_index,
					 int [] meta_edge_root_indices) {

	return true;
    } // end removeFromAttributes
    

    public boolean removeMetaEdgesFromAttributes (CyNetwork cy_network,
						  int metanode_root_index,
						  int [] meta_edge_root_indices) {
	
	return true;
    } // end removeMetaEdgesFromAttributes

    // method to encapsulate the canonical naming of meta nodes and edges
    protected String getCanonicalMetaName (int metanode_root_index) {
	return "MetaNode_" + Integer.toString( (metanode_root_index*-1) );
    }

    protected String getCommonMetaName (int metanode_root_index) {
	String commonName = new String ();
	List children = rootGraph.nodeMetaChildrenList(metanode_root_index);
	if (children == null) return null;
	int count=0;
	for (Iterator it = children.iterator(); it.hasNext(); ) {
	    count++;
	    Node child = (Node) it.next();
	    commonName += nodeAttr.getCanonicalName(child);
	    //if (it.hasNext() && (count % 3) == 0) commonName += '\n';
	    if (it.hasNext()) commonName += ",";
	}
	return commonName;
    }
    
    // procedure class to copy over edge attributes
    protected class CopyEdgeAttr implements IntIntProcedure {
	public boolean apply (int metaEdgeIndex, int childEdgeIndex ) {

	    // get edge info
	    Edge metaEdge = rootGraph.getEdge(metaEdgeIndex);
	    Edge childEdge = rootGraph.getEdge(childEdgeIndex);
	    String childEdgeName = edgeAttr.getCanonicalName(childEdge);
	    String metaEdgeName = "";

	    // infer the metaNode and use it to name the metaEdge
	    String interaction = 
		edgeAttr.getStringValue("interaction", childEdgeName);
	    Node sourceNode = metaEdge.getSource();
	    Node targetNode = metaEdge.getTarget();
	    if (rootGraph.nodeMetaChildrenList(sourceNode) != null)
		metaEdgeName = nodeAttr.getCanonicalName(sourceNode) + " (" 
		    + interaction + ") " 
		    + nodeAttr.getCanonicalName(metaEdge.getTarget());
	    else if (rootGraph.nodeMetaChildrenList(targetNode) != null)
		metaEdgeName = nodeAttr.getCanonicalName(metaEdge.getSource()) 
		    + " (" + interaction + ") " 
		    + nodeAttr.getCanonicalName(targetNode);
	    
	    // Transfer attributes b/w edges--
	    // if edge name redundant, merge attrs with existing name and remove
	    if (usedEdgeNames.contains(metaEdgeName)) {
		
		String [] allAttrNames = edgeAttr.getAttributeNames();
		for (int j=0; j<allAttrNames.length; j++) {
		    String attrName = allAttrNames[j];
		    if (attrName.equals("interaction")) continue; // reserved
		    if (attrName.equals("canonicalName")) continue; // reserved
		    Vector childValues = edgeAttr.getList(attrName, childEdgeName); 
		    Vector metaValues = edgeAttr.getList(attrName, metaEdgeName);
		    
		    // take union of previous and new attr values
		    HashSet uniqueValues = new HashSet ();
		    uniqueValues.addAll(childValues); 
		    uniqueValues.addAll(metaValues);
		    try { 
			edgeAttr.deleteAttribute(attrName, metaEdgeName);
			for (Iterator it=uniqueValues.iterator(); it.hasNext(); ) {
			    Object thisValue = it.next();
			    //String thisValue = (String) it.next();
			    //System.err.println("Adding value: " + thisValue);
			    edgeAttr.append(attrName, metaEdgeName, thisValue);
			}
			//System.err.println("ADDED: ");
			//System.err.println("  Attr Name " + attrName);
			//System.err.println("  MetaEdgeName " + metaEdgeName);
			//System.err.println("  ChildEdgeName " + childEdgeName);
			//System.err.println("  Values " + uniqueValues);
		    } catch (IllegalArgumentException exc) {
			System.err.println("Caught IllegalArgumentException:");
			System.err.println("  Attr Name " + attrName);
			System.err.println("  MetaEdgeName " + metaEdgeName);
			System.err.println("  ChildEdgeName " + childEdgeName);
			System.err.println("  Values " + uniqueValues);
		    }

		    // remove the redundant metaEdge
		    rootGraph.removeEdge(metaEdgeIndex);		    
		}		
	    }
	    else {  // if meta edge not seen before, register name and lump transfer attrs 
		edgeAttr.addNameMapping(metaEdgeName, metaEdge);
		edgeAttr.set("canonicalName", metaEdgeName, metaEdgeName);
		edgeAttr.set(metaEdgeName, edgeAttr.getAttributes(childEdgeName));
		usedEdgeNames.add(metaEdgeName);
		//System.err.println("\nFIRST OBSERVATION-- transferring all attributes");
	    }

	    //System.err.println("FINISHED MetaEdge " + metaEdgeName 
	    //		       + " to ChildEdge " + childEdgeName + "\n");
	    return true;
	}
    } // end CopyEdgeAttr

    // if multiple edges map to the same name, merge edges and edge attrs
    // NOTE -- CURRENTLY NOT BEING USED, FUNCTIONALITY WAS SUBSUMED
    //         BY COPYEDGEATTR ABOVE
    protected boolean mergeEdgesOfSameName ( int [] metaEdgeArray ) {
	HashSet allEdgeNames = new HashSet ();
	for (int i=0; i<metaEdgeArray.length; i++) {
	    int metaEdgeIndex = metaEdgeArray[i];
	    Edge metaEdge = rootGraph.getEdge(metaEdgeIndex);	    
	    String metaEdgeName = edgeAttr.getCanonicalName(metaEdge);
	    // if edge seen before, merge attrs
	    if (allEdgeNames.contains(metaEdgeName)) {
		rootGraph.removeEdge(metaEdgeIndex);
	    }
	    else allEdgeNames.add(metaEdgeName);
	}
	return true;
    } // end mergeEdgesOfSameName

} // end class
