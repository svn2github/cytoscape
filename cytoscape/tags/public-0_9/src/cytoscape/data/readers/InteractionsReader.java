// InteractionsReader:  from semi-structured text file, into an array of Interactions
//-----------------------------------------------------------------------------------------
// $Revision$   
// $Date$ 
// $Author$
//-----------------------------------------------------------------------------------
package cytoscape.data.readers;
//-----------------------------------------------------------------------------------------
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.Hashtable;

import y.base.Node;
import y.base.Edge;
import y.view.Graph2D;

import cytoscape.GraphObjAttributes;
import cytoscape.data.Interaction;
//-----------------------------------------------------------------------------------------
public class InteractionsReader implements GraphReader {
  String filename;
  Vector allInteractions = new Vector ();
  GraphObjAttributes edgeAttributes = new GraphObjAttributes ();
  Graph2D graph;

//-----------------------------------------------------------------------------------------
public InteractionsReader (String filename)
{
  this.filename = filename;
}
//-----------------------------------------------------------------------------------------
public void read ()
{
  TextFileReader reader = new TextFileReader (filename);
  reader.read ();
  String rawText = reader.getText ();
  StringTokenizer strtok = new StringTokenizer (rawText, "\n");

  Vector interactions = new Vector ();
  while (strtok.hasMoreElements ()) {
    String newLine = (String) strtok.nextElement ();
    allInteractions.addElement (new Interaction (newLine));
    }

  for (int i=0; i < interactions.size (); i++) {
    Interaction inter = (Interaction) allInteractions.elementAt (i);
    System.out.println (inter);
    }

  createGraphFromInteractionData ();

}  // readFromFile
//-------------------------------------------------------------------------------------------
public int getCount ()
{
  return allInteractions.size ();
}
//-------------------------------------------------------------------------------------------
public Interaction [] getAllInteractions ()
{
  Interaction [] result = new Interaction [allInteractions.size ()];

  for (int i=0; i < allInteractions.size (); i++) {
    Interaction inter = (Interaction) allInteractions.elementAt (i);
    result [i] = inter;
    }

  return result;

}
//-------------------------------------------------------------------------------------------
protected void createGraphFromInteractionData ()
{
  graph = new Graph2D ();
  Interaction [] interactions = getAllInteractions ();

  Hashtable nodes = new Hashtable ();

    //---------------------------------------------------------------------------
    // loop through all of the interactions -- which are triples of the form:
    //           source; target0, target1...; interaction type
    // using a hash to avoid duplicate node creation, add a node to the graph
    // for each source and target
    // in addition,
    //---------------------------------------------------------------------------

  for (int i=0; i < interactions.length; i++) {
    Interaction interaction = interactions [i];
    String nodeName = interaction.getSource ();
    if (!nodes.containsKey (nodeName)) {
      Node node = graph.createNode (0.0, 0.0, 70.0, 30.0, nodeName);
      nodes.put (nodeName, node);
      }
    String [] targets = interaction.getTargets ();
    for (int t=0; t < targets.length; t++) {
      String targetNodeName = targets [t];
      if (!nodes.containsKey (targetNodeName)) {
        Node targetNode = graph.createNode (0.0, 0.0, 70.0, 30.0, targetNodeName);
        nodes.put (targetNodeName, targetNode);
        } // if target node is previously unknown
      } // for t
    } // i


    //---------------------------------------------------------------------------
    // now loop over the interactions again, this time creating edges between
    // all sources and each of their respective targets.
    // for each edge, save the source-target pair, and their interaction type,
    // in edgeAttributes -- a hash of a hash of name-value pairs, like this:
    //   edgeAttributes ["interaction"] = interactionHash
    //   interactionHash [sourceNode::targetNode] = "pd"
    //---------------------------------------------------------------------------

  for (int i=0; i < interactions.length; i++) {
    Interaction interaction = interactions [i];
    String nodeName = interaction.getSource ();
    String interactionType = interaction.getType ();
    Node sourceNode = (Node) nodes.get (nodeName);
    String [] targets = interaction.getTargets ();
    for (int t=0; t < targets.length; t++) {
      Node targetNode = (Node) nodes.get (targets [t]);
      Edge edge = graph.createEdge (sourceNode, targetNode);
      //String edgeName = nodeName + " -> " + targets [t];
      String edgeName = nodeName + " (" + interactionType + ") " + targets [t];
      int previousMatchingEntries = edgeAttributes.countIdentical(edgeName);
      if (previousMatchingEntries > 0)
        edgeName = edgeName + "_" + previousMatchingEntries;
      edgeAttributes.add ("interaction", edgeName, interactionType);
      edgeAttributes.addNameMapping (edgeName, edge);
      } // for t
   } // for i
  
} // createGraphFromInteractionData
//-------------------------------------------------------------------------------------------
public Graph2D getGraph ()
{
  return graph;

} // createGraph
//------------------------------------------------------------------------------------
public GraphObjAttributes getEdgeAttributes ()
{
  return edgeAttributes;

} // getEdgeAttributes
//------------------------------------------------------------------------------------
} // InteractionsReader

