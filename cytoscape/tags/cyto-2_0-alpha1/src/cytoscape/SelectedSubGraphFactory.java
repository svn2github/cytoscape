// SelectedSubGraphFactory: 

/** Copyright (c) 2002 Institute for Systems Biology and the Whitehead Institute
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** any later version.
 ** 
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and the
 ** Institute for Systems Biology and the Whitehead Institute 
 ** have no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall the
 ** Institute for Systems Biology and the Whitehead Institute 
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if the
 ** Institute for Systems Biology and the Whitehead Institute 
 ** have been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 ** 
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/

//------------------------------------------------------------------------------
// $Revision$   
// $Date$ 
// $Author$
//-----------------------------------------------------------------------------------
package cytoscape;
//------------------------------------------------------------------------------
import y.base.*;
import y.view.*;
import java.util.*;
//------------------------------------------------------------------------------
/**
 * creates a new graph, using canonical names when available, with 
 * appropriate node and edge attributes, based upon nodes selected in the
 * parentGraph;
 */
public class SelectedSubGraphFactory {

  Graph2D parentGraph;
  Graph2D subgraph;
  GraphObjAttributes parentNodeAttributes, parentEdgeAttributes;
  GraphObjAttributes newNodeAttributes, newEdgeAttributes;
  HashMap subgraphNodeMap = new HashMap ();

//------------------------------------------------------------------------------
public SelectedSubGraphFactory (Graph2D parentGraph, GraphObjAttributes nodeAttributes,
                                GraphObjAttributes edgeAttributes) 
{
  this.parentGraph = parentGraph;
  this.parentNodeAttributes = nodeAttributes;
  this.parentEdgeAttributes = edgeAttributes;
  
  createSubgraph ();
} // ctor
//------------------------------------------------------------------------------
// Method added by iliana 3.17.2003
/**
 * This method creates the subgraph by copying one by one the selected nodes
 * in the parentGraph. The sungraph's node attributes are cloned from the 
 * parentGraph's node attributes, making sure that the map of Node->canonicalName
 * entries has Node objects that are in the sungraph, not the parentGraph.
 * This method calls createEdgesWithAttributes().
 */
protected void createSubgraph ()
{
  NodeCursor nc = parentGraph.selectedNodes (); 
  
  // There must be a canonicalName -> node entry for every node in the graph
  assert( parentNodeAttributes.getNameMap().size() == parentGraph.nodeCount () );
  
  subgraph = new Graph2D();
  
  // Clone the parent's node attributes
  newNodeAttributes = (GraphObjAttributes)parentNodeAttributes.clone();
  newNodeAttributes.clearNameMap();
  
  // Copy one by one each selected node
  Node parentNode;
  Node newNode;
  String canonicalName;
  for( nc.toFirst(); nc.ok(); nc.next() ){
    parentNode = nc.node();
    newNode = parentNode.createCopy(subgraph);
    canonicalName = parentNodeAttributes.getCanonicalName(parentNode);
    // Set node attributes Node->canonicalName mapping for the subgraph
    newNodeAttributes.addNameMapping(canonicalName, newNode);
    // Remember the mapping of canonicalName to newNode for creating edges
    subgraphNodeMap.put (canonicalName, newNode);
  }//for nc
  
  createEdgesWithAttributes ();
    
}//createSungraph
//------------------------------------------------------------------------------
/**
 *  create (de novo, ignoring edges which may have been created with the
 *  subgraph operation) edges in the subgraph which correspond to edges
 *  in the parent graph.  do this by traversing the edges in the parent graph,
 *  and recognizing those with source & target nodes present in the subgraph.
 *  using this approach, the canonical name of the original edge is available,
 *  along with its HashMap attribute bundle, and both may be transferred to the
 *  new edgeAttributes.
 */
protected void createEdgesWithAttributes ()
{   
    // there must be a canonicalName -> edge entry for every edge in the graph
  assert( parentEdgeAttributes.getNameMap().size() == parentGraph.edgeCount () );

  newEdgeAttributes = new GraphObjAttributes ();

   // get and then remove all edges in the subgraph.  recreate them
   // below.  this allows 

  Edge [] newEdges = subgraph.getEdgeArray ();
  for (int i=0; i < newEdges.length; i++)
    subgraph.removeEdge (newEdges [i]);  

  EdgeCursor ec = parentGraph.edges ();

  for (ec.toFirst (); ec.ok (); ec.next ()) { 
    
    Edge parentEdge = ec.edge ();
    String parentEdgeName = parentEdgeAttributes.getCanonicalName(parentEdge);
    Node source = parentEdge.source ();
    Node target = parentEdge.target ();
    String sourceName = parentNodeAttributes.getCanonicalName (source);
    String targetName = parentNodeAttributes.getCanonicalName (target);
    boolean isSubgraphEdge =
       subgraphNodeMap.containsKey (sourceName) && subgraphNodeMap.containsKey (targetName);
    if (isSubgraphEdge) {
      Node subgraphSourceNode = (Node) subgraphNodeMap.get (sourceName);
      Node subgraphTargetNode = (Node) subgraphNodeMap.get (targetName);
      Edge newEdge = subgraph.createEdge (subgraphSourceNode, subgraphTargetNode);
      String canonicalName = parentEdgeAttributes.getCanonicalName (parentEdge);
      HashMap attributeBundle = parentEdgeAttributes.getAttributes (canonicalName);
      newEdgeAttributes.set (canonicalName, attributeBundle);
      newEdgeAttributes.addNameMapping (canonicalName, newEdge);
      } // if in subgraph
    } // for ec

} // createEdgesWithAttributes
//------------------------------------------------------------------------------
public Graph2D getSubGraph () { return subgraph; }
public GraphObjAttributes getNodeAttributes () { return newNodeAttributes; }
public GraphObjAttributes getEdgeAttributes () { return newEdgeAttributes; }
//------------------------------------------------------------------------------
} // class SelectedSubGraphFactory


