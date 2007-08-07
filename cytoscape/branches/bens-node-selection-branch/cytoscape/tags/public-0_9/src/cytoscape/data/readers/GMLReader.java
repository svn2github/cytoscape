// GMLReader.java
//-------------------------------------------------------------------------------------
// $Revision$   
// $Date$ 
// $Author$
//-----------------------------------------------------------------------------------
package cytoscape.data.readers;
//-----------------------------------------------------------------------------------------
import y.base.*;
import y.view.*;

import y.io.YGFIOHandler;
import y.io.GMLIOHandler;

import cytoscape.GraphObjAttributes;
//-------------------------------------------------------------------------------------
public class GMLReader implements GraphReader {
  private String filename;
  GraphObjAttributes edgeAttributes = new GraphObjAttributes ();
  Graph2D graph;    
//-------------------------------------------------------------------------------------
public GMLReader (String filename)
{
  this.filename = filename;
}
//-------------------------------------------------------------------------------------
public void read ()
{
  GMLIOHandler ioh  = new GMLIOHandler ();
  graph = new Graph2D ();
  ioh.read (graph, filename);

  // set the interaction types recorded in the labels
  // erase the labels serving this purpose
  // while creating the edge names (the hard way)
  Graph2DView gView = new Graph2DView(graph);

  for (EdgeCursor ec = graph.edges(); ec.ok(); ec.next()) {
      Edge edge = ec.edge();
      String interactionType = graph.getLabelText(edge);
      graph.setLabelText(edge, null); // erase the label
      String sourceName = gView.getGraph2D().getLabelText(edge.source());
      String targetName = gView.getGraph2D().getLabelText(edge.target());
      String edgeName =  sourceName + " (" + interactionType + ") " + targetName;
      int previousMatchingEntries = edgeAttributes.countIdentical(edgeName);
      if (previousMatchingEntries > 0)
	  edgeName = edgeName + "_" + previousMatchingEntries;
      edgeAttributes.add("interaction", edgeName, interactionType);
      edgeAttributes.addNameMapping(edgeName, edge);
      edgeAttributes.add("interaction", edgeName, interactionType);      
  }
  
} // read
//------------------------------------------------------------------------------------
public Graph2D getGraph ()
{
  return graph;

} // createGraph
//------------------------------------------------------------------------------------
public GraphObjAttributes getEdgeAttributes ()
{
  return edgeAttributes;

} // getEdgeAttributes
//------------------------------------------------------------------------------
} // class GMLReader
