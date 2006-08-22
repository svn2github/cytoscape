
/*
  File: AttributeLayout.java 
  
  Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)
  
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

// AttributeLayout: 


//----------------------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//----------------------------------------------------------------------------------------
package cytoscape.layout;
//----------------------------------------------------------------------------------------
import java.util.*;

import giny.model.*;
import giny.view.*;
import giny.util.SpringEmbeddedLayouter;

import cytoscape.Cytoscape;
import cytoscape.CyEdge;
import cytoscape.data.CyAttributes;
import cytoscape.data.CyAttributesUtils;
import cytoscape.data.Semantics;
import cytoscape.view.CyNetworkView;
import cytoscape.ding.DingNetworkView;
//----------------------------------------------------------------------------------------
/**
 * This class provides methods for performing operations on a graph related to the
 * values of a particular data attribute attached to the nodes of the graph. It
 * allows edges to be added between nodes that share an attribute value, to lay out
 * the graph by grouping nodes with shared attribute values, and to clean up after
 * these operations by removing the nodes and edges that were created.
 */
public class AttributeLayout {
    
    public static final int DO_LAYOUT = 0;
    public static final int CREATE_EDGES = 1;
    public static final int CLEAR_OBJECTS = 2;
    
    CyNetworkView cyNetView; //the window to which we're attached
    
    //list of category nodes created during layout
    protected int[] categoryNodes;
    //list of edges created during create edges operation
    protected int[] createdEdges;
    
    public AttributeLayout(CyNetworkView cyNetView) {
        this.cyNetView = cyNetView;
    }
    
    public void doCallback(String attributeName, int functionToPerform) {
        if (functionToPerform == DO_LAYOUT) {
            doLayout(attributeName);
        } else if (functionToPerform == CREATE_EDGES) {
            createEdges(attributeName);
        } else if (functionToPerform == CLEAR_OBJECTS) {
            clearPreviousGraphObjects();
        }
    }
    
    /**
     * Removes any nodes and edges that were created by a previously called method
     * of this class. It does not undo a previously applied layout.
     */
    public void clearPreviousGraphObjects() {
        RootGraph rootGraph = cyNetView.getNetwork().getRootGraph();
        if (categoryNodes != null) {
            for (int i=0; i<categoryNodes.length; i++) {
                rootGraph.removeNode(categoryNodes[i]);
            }
            categoryNodes = null;//explicitly discard the array
        }
        if (createdEdges != null) {
            for (int i=0; i<createdEdges.length; i++) {
                rootGraph.removeEdge(createdEdges[i]);
            }
            createdEdges = null;//explicitly discard the array
        }
        cyNetView.redrawGraph(false, false); //forces update of the UI
    }
    
    /**
     * Performs a layout operation based on values of the given node attribute.
     * This method creates a new node for each unique value of the given attribute
     * and computes a layout assuming that each of these new nodes is connected to
     * every node that has that attribute value and all other edges are ignored.<P>
     *
     * This method first removes any nodes or edges created by previous calls to
     * methods in this class by calling {@link #clearPreviousGraphObjects()}.<P>
     *
     * This method works by cloning the existing GraphPerspective and computing the
     * layout on modifications of that clone. The computed layout is copied to the
     * current GraphPerspective and the newly created category nodes are added.<P>
     */
    public void doLayout(String attributeName) {
        //should handle error cases better
        clearPreviousGraphObjects();
        
        //this creates a map from each unique attribute value to the set of nodes
        //that have that value defined
        Map valueMap = buildValueMap(attributeName);
        if (valueMap.size() == 0) {return;}
        
        //we do the layout on a clone of the existing graph perspective
        GraphPerspective realGP = cyNetView.getNetwork().getGraphPerspective();
        GraphPerspective layoutGP = (GraphPerspective)realGP.clone();
        RootGraph rootGraph = realGP.getRootGraph();
        CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
        
        int[] currentEdges = layoutGP.getEdgeIndicesArray();
        layoutGP.hideEdges(currentEdges);//hide all the current edges
        
        //figure out how many nodes and edges to create
        int newNodeCount = valueMap.size();
        int newEdgeCount = 0;
        //the values in the map are Sets of nodes. We'll create one edge for
        //every element in every Set. So iterate over values and count Set size.
        for (Iterator es = valueMap.keySet().iterator(); es.hasNext(); ) {
            Object key = es.next();
            Set nodeSet = (Set)valueMap.get(key);
            newEdgeCount += nodeSet.size();
        }
        //we save the list of created nodes as a class variable so that we can
        //delete them later. The created edges will be deleted later in this method.
        this.categoryNodes = new int[newNodeCount];
        int[] newEdges = new int[newEdgeCount];
        int nodeIndex = 0;
        int edgeIndex = 0;
        //The map contains String keys and a Set of nodes for each key. Create
        //a node for the key and an edge between that new node and every node
        //in the set
        for (Iterator mi = valueMap.keySet().iterator(); mi.hasNext(); ) {
            Object category = mi.next();
            int categoryInt = rootGraph.createNode();
            this.categoryNodes[nodeIndex] = categoryInt;
            nodeIndex++;
            //get a name for this node
            String categoryName = category.toString();
            if (categoryName.length() > 50) {//sanity check
                categoryName = categoryName.substring(50) + "...";
            }
            //and add node and name to the node attributes
            Node node = rootGraph.getNode(categoryInt);
            //now process the set of nodes that have this attribute value
            Set nodeSet = (Set)valueMap.get(category);
            for (Iterator si = nodeSet.iterator(); si.hasNext(); ) {
                Node targetNode = (Node)si.next();
                int index = rootGraph.getIndex(targetNode);
                int edge = rootGraph.createEdge(categoryInt, index);
                newEdges[edgeIndex] = edge;  //save these edges locally
                edgeIndex++;
            }
        }
        //we have to unhide the new nodes and edges in the layout perspective
        layoutGP.restoreNodes(this.categoryNodes);
        layoutGP.restoreEdges(newEdges);
        
        //for now, we do the highly non-optimal thing of creating a new view
        //and computing the layout on that view. We'll change this to only
        //computing on the layout perspective when that's supported.
        GraphView layoutView = new ding.view.DGraphView(layoutGP);
        SpringEmbeddedLayouter layouter = new SpringEmbeddedLayouter(layoutView);
        layouter.doLayout();
        
        rootGraph.removeEdges(newEdges); //get rid of the newly created edges
        
        //now to copy the layout to the real graph perspective. First we need to
        //unhide the newly created category nodes, then move every node to it's
        //calculated position
        realGP.restoreNodes(this.categoryNodes);
        
        GraphView realView = ( GraphView )cyNetView;
        for (Iterator vi = realView.getNodeViewsIterator(); vi.hasNext(); ) {
            NodeView nv = (NodeView)vi.next();
            NodeView layoutV = layoutView.getNodeView( nv.getNode() );
            nv.setXPosition(layoutV.getXPosition(), false);
            nv.setYPosition(layoutV.getYPosition(), false);
        }
        for (Iterator vi = realView.getNodeViewsIterator(); vi.hasNext(); ) {
            NodeView nv = (NodeView)vi.next();
            nv.setNodePosition( true );
        }
        
        cyNetView.redrawGraph(false, true);
        realView.fitContent();
    }
    
    /**
     * Adds edges between all nodes that share a value for the given node attribute.
     * Multiple edges will be added if the attribute value is a list and two nodes
     * share more than one value. The created edges are saved for later removal by
     * the {@link clearPreviousGraphObjects} method.
     */
    public void createEdges(String attributeName) {
        clearPreviousGraphObjects();
        
        Map valueMap = buildValueMap(attributeName);
        if (valueMap.size() == 0) {return;}
        
        GraphPerspective gp = cyNetView.getNetwork().getGraphPerspective();
        RootGraph rootGraph = gp.getRootGraph();
        CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
        CyAttributes edgeAttributes = Cytoscape.getEdgeAttributes();
        
        //count up how many edges to create
        int edgeCount = 0;
        for (Iterator mi = valueMap.keySet().iterator(); mi.hasNext(); ) {
            Object key = mi.next();
            Set nodeSet = (Set)valueMap.get(key);
            int setSize = nodeSet.size(); //number of nodes in this group
            edgeCount += setSize*(setSize-1)/2; //number of edges, no self-edges
        }
        this.createdEdges = new int[edgeCount];
        
        int edgeIndex = 0;
        for (Iterator mi = valueMap.keySet().iterator(); mi.hasNext(); ) {
            Object category = mi.next();
            String categoryName = category.toString();
            if (categoryName.length() > 50) {//sanity check
                categoryName = categoryName.substring(50) + "...";
            }
            Set nodeSet = (Set)valueMap.get(category);
            //we have to dump this to an array so that we can do the double iteration
            //over the contents without duplicating pairs
            Node[] nodeArray = (Node[])nodeSet.toArray(new Node[0]);
            for (int i=0; i<nodeArray.length-1; i++) {
                Node firstNode = nodeArray[i];
                String firstName = firstNode.getIdentifier();
                for (int j=i+1; j<nodeArray.length; j++) {
                    Node secondNode = nodeArray[j];
                    String secondName = secondNode.getIdentifier();
                    int edgeInt = rootGraph.createEdge(firstNode, secondNode);
                    this.createdEdges[edgeIndex] = edgeInt;
                    edgeIndex++;
                    //create a name for this edge
                    String edgeName = CyEdge.createIdentifier(firstName,categoryName,secondName);
                    //and add it to the edge attributes
                    Edge edge = rootGraph.getEdge(edgeInt);
                    gp.restoreEdge(edge);
                }
            }
        }
        cyNetView.redrawGraph(false, true);
    }
    
    /**
     * This function build a Map structure based on the unique values of the
     * data attribute specified by the argument. The Map maps each unique
     * attribute value to the set of nodes that have that attribute value.
     * That is, the keys of the map are the unique data attribute values,
     * and the values for each key is a Set containing the nodes that have
     * that data attribute value. Note that, if the data attribute is actually
     * a List of values, then a given node may match more than one attribute
     * value and thus may appear more than once in the map.
     *
     * The data is taken from the node attributes structure of the current
     * network available from the CyNetworkView attached to this class. An empty
     * Map will be returned if no such attribute exists.
     */
    public Map buildValueMap(String attributeName) {
        Map returnMap = new HashMap();
        if (attributeName == null) {return returnMap;}
        CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
        GraphPerspective gp = cyNetView.getNetwork().getGraphPerspective();
        
        //get the attribute, which is a map of object names to data values
        //return an empty map if there is no such attribute
        Map attrMap = CyAttributesUtils.getAttribute(attributeName, nodeAttributes);
        if (attrMap == null || attrMap.size() == 0) {return returnMap;}
        
        //iterate over all the nodes and process their attribute value, creating
        //entries for each new data attribute value and storing the node in each
        //map entry that it matches
        for (Iterator ni = gp.nodesIterator(); ni.hasNext(); ) {
            Node node = (Node)ni.next();
            String canonicalName = node.getIdentifier();
            if (canonicalName == null || canonicalName.length() == 0) {continue;}
            Object attrValue = attrMap.get(canonicalName); //this is the attribute value
            if (attrValue == null) {continue;}
            if (attrValue instanceof List) {//list of values
                List theList = (List)attrValue;
                for (Iterator li = theList.iterator(); li.hasNext(); ) {
                    Object value = li.next(); //one attribute value
                    if (value == null) {continue;}
                    //get the set of nodes for this value
                    Set nodeSet = (Set)returnMap.get(value);
                    if (nodeSet == null) {//never seen this attribute value before
                        nodeSet = new HashSet(); //so create a new set and add it
                        returnMap.put(value, nodeSet);
                    }
                    nodeSet.add(node);
                }
            } else {//single object instead of a list
                Set nodeSet = (Set)returnMap.get(attrValue);
                if (nodeSet == null) {//never seen this attribute value before
                    nodeSet = new HashSet(); //so create a new set and add it
                    returnMap.put(attrValue, nodeSet);
                }
                nodeSet.add(node);
            }
        }
        return returnMap;
    }
}

