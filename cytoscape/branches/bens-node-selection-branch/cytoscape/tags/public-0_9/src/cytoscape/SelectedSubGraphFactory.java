// SelectedSubGraphFactory: 
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
  recreateNodeAttributes ();
  createEdgesWithAttributes ();

} // ctor
//------------------------------------------------------------------------------
protected void createSubgraph ()
{
  NodeCursor nc = parentGraph.selectedNodes (); 
  subgraph = new Graph2D (parentGraph, nc);  // creates a copy
}
//------------------------------------------------------------------------------
/**
 *  clone the full nodeAttributes of the parent graph and replace the 
 *  nameMap (which maps from Node to canonicalName). <p>
 *  the main problem faced here is our need, within the nodeAttributes
 *  of the new subgraph, to map from a Node to the node's canonical
 *  name, since  all attributes for nodes are stored by the node's
 *  canonical name.  The Node's in the new subgraph are distinct from
 *  the corresponding (selected) nodes in the parent graph, but they
 *  do have the same node labels. so the strategy is:
 *  <ol>
 *     <li> from the parent graph, create a temporary map from
 *          <currentNodeLabel> to <nodeCanonicalName>. 
 *     <li> traverse the nodes in the new subgraph, extracting each
 *          label, looking up the canonical name in the temporary map,
 *          and adding the new pair (<canonicalName>, <subgraphNode)>
 *          to the subgraph's node attributes name map.
 *  </ol>
 */
protected void recreateNodeAttributes ()
{
  NodeCursor nc = parentGraph.selectedNodes (); 
  HashMap parentGraphNameMap = new HashMap ();

    // there must be a canonicalName -> node entry for every node in the graph
  assert (parentNodeAttributes.getNameMap().size() == parentGraph.nodeCount ());

  for (nc.toFirst (); nc.ok (); nc.next ()) { 
    Node node = nc.node ();
    String canonicalName = parentNodeAttributes.getCanonicalName (nc.node ());
    String nodeLabel = parentGraph.getLabelText (node);
    parentGraphNameMap.put (nodeLabel, canonicalName);
    } // for nc

  Node [] newNodes = subgraph.getNodeArray ();
  newNodeAttributes = (GraphObjAttributes) parentNodeAttributes.clone ();
  newNodeAttributes.clearNameMap ();

  for (int i=0; i < newNodes.length; i++) {
    Node newNode = newNodes [i];
    String nodeLabel = subgraph.getLabelText (newNode);
    String canonicalName = (String) parentGraphNameMap.get (nodeLabel);
    newNodeAttributes.addNameMapping (canonicalName, newNode);
    subgraphNodeMap.put (canonicalName, newNode);
    } // for i

} // recreateNodeAttributes
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
  assert (parentEdgeAttributes.getNameMap().size() == parentGraph.edgeCount ());

  newEdgeAttributes = new GraphObjAttributes ();

   // get and then remove all edges in the subgraph.  recreate them
   // below.  this allows 

  Edge [] newEdges = subgraph.getEdgeArray ();
  for (int i=0; i < newEdges.length; i++)
    subgraph.removeEdge (newEdges [i]);  

  EdgeCursor ec = parentGraph.edges ();

  for (ec.toFirst (); ec.ok (); ec.next ()) { 
    Edge parentEdge = ec.edge ();
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
      newEdgeAttributes.add (canonicalName, attributeBundle);
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
