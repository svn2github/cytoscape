// GMLReader.java

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

//-------------------------------------------------------------------------------------
// $Revision$   
// $Date$ 
// $Author$
//-----------------------------------------------------------------------------------
package cytoscape.data.readers;
//-----------------------------------------------------------------------------------------
import java.util.*;

import y.base.*;
import y.view.*;

import y.io.YGFIOHandler;
import y.io.GMLIOHandler;

import giny.model.RootGraph;
import luna.*;

import cytoscape.GraphObjAttributes;
//-------------------------------------------------------------------------------------
public class GMLReader implements GraphReader {
  private String filename;
  private boolean isYFiles;
  GraphObjAttributes edgeAttributes = new GraphObjAttributes ();
  Graph2D graph;
  RootGraph rootGraph;

  /**
   * @param filename The GML file to be read in
   * @param isYFiles  
   */
  public GMLReader ( String filename, boolean isYFiles) {
    this.filename = filename;
    this.isYFiles = isYFiles;
  }
  /**
   * @param filename The GML file to be read in
   */
  public GMLReader ( String filename ) {
    this.filename = filename;
    isYFiles = true;
  }

  /**
   * Calls read()
   * @param canonicalize <B>Note</B> this seems to have no effect
   */
  public void read ( boolean canonicalize ) {
    read();
  }

  /**
   * This will read AND create a Graph from the file specified in the constructor
   */
  public void read ()
  {
      if( isYFiles ) {
	  GMLIOHandler ioh  = new GMLIOHandler ();
	  graph = new Graph2D ();
	  try {
	      ioh.read (graph, filename);
	  }
	  catch (java.io.IOException e) {
	      System.err.println ("error reading '" + filename + "' -- " + e.getMessage ());
	      e.printStackTrace ();
	      graph = null; //signals callers that something went wrong
	      return;
	  }

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
      }  // read GML file into GINY graph
      else {
	  rootGraph = new LunaRootGraph ();

	  // create and read the GML file
	  GMLObject gml = new GMLObject();
	  gml.read(filename);

	  //GMLObject g = gml.getItems("graph"); System.out.println(g.toString());
	  Vector graphCreator = gml.getVector("Creator");
	  Vector graphLabel   = gml.getVector("graph", "label");
	  //System.out.println("G_CREATOR:" + graphCreator.toString());
	  //System.out.println("G___LABEL:" + graphLabel.toString());

	  GMLObject gmlNodes = gml.getItems("graph", "node");
	  GMLObject gmlEdges = gml.getItems("graph", "edge");
	  Vector nodeIds     = gmlNodes.getVector("node", "id");
	  Vector nodeLabels  = gmlNodes.getVector("node", "label");
	  //System.out.println("   nodeIds:" + nodeIds.toString());
	  //System.out.println("nodeLables:" + nodeLabels.toString());

	  // in case gml node ids are not ordered consecutively (0..n)
          Hashtable nodeMap  = new Hashtable(nodeIds.size());
	  for(int i=0; i<nodeIds.size(); i++)
	      nodeMap.put(nodeIds.get(i), nodeLabels.get(i));

	  Vector edgeSources = gmlEdges.getVector("edge", "source");
	  Vector edgeTypes   = gmlEdges.getVector("edge", "label");
	  Vector edgeTargets = gmlEdges.getVector("edge", "target");

	  //---------------------------------------------------------------------------
	  // Need to pass the graphical parameters
	  GMLObject nodeGraphics = gmlNodes.getItems("node", "graphics");
	  GMLObject edgeGraphics = gmlEdges.getItems("edge", "graphics");
	  Vector nodeXPos, nodeYPos, nodeWidth, nodeHeight;
	  if(nodeGraphics.size() == nodeIds.size()) {
	      //System.out.println(nodeGraphics.toString());
	      nodeXPos = nodeGraphics.getVector("graphics", "x");
	      nodeYPos = nodeGraphics.getVector("graphics", "y");
	      nodeWidth  = nodeGraphics.getVector("graphics", "w");
	      nodeHeight = nodeGraphics.getVector("graphics", "h");
	  }
	  // set a default edge type if it's not defined in the GML file
	  // need a better system for defining the default...
	  String et = "pp";
	  if(edgeTypes.isEmpty())
	      for(int i=0; i < edgeSources.size(); i++)
		  edgeTypes.add(et);

	  //---------------------------------------------------------------------------
	  // loop through all of the nodes (using a hash to avoid duplicates)
	  // adding nodes to the rootGraph
	  //---------------------------------------------------------------------------
	  Hashtable nodes = new Hashtable ();
	  String nodeName, interactionType;
	  for(int i=0; i<nodeIds.size(); i++) {
	      nodeName = (String) nodeMap.get(nodeIds.get(i));
	      //if(canonicalize) nodeName = canonicalizeName(nodeName);	      
	      if (!nodes.containsKey(nodeName)) {
		  giny.model.Node node = rootGraph.getNode(rootGraph.createNode());
		  node.setIdentifier(nodeName);
		  //node.setXPosition(nodeXPos.get(i));
		  //node.setYPosition(nodeYPos.get(i));
		  nodes.put(nodeName, node);
	      }
	  }
	  //---------------------------------------------------------------------------
	  // loop over the interactions creating edges between all sources and their 
	  // respective targets.
	  // for each edge, save the source-target pair, and their interaction type,
	  // in edgeAttributes -- a hash of a hash of name-value pairs, like this:
	  // ???  edgeAttributes ["interaction"] = interactionHash
	  // ???  interactionHash [sourceNode::targetNode] = "pd"
	  //---------------------------------------------------------------------------
	  String sourceName, targetName;
	  for (int i=0; i < edgeSources.size(); i++) {
	      sourceName = (String) nodeMap.get(edgeSources.get(i));
	      targetName = (String) nodeMap.get(edgeTargets.get(i));
	      interactionType = (String) edgeTypes.get(i);
	      //if(canonicalize) nodeName = canonicalizeName(nodeName);
	      //if(canonicalize) targetName = canonicalizeName(targetName);

	      giny.model.Node sourceNode = (giny.model.Node) nodes.get(sourceName);
	      giny.model.Node targetNode = (giny.model.Node) nodes.get(targetName);
	      giny.model.Edge edge = rootGraph.getEdge(rootGraph.createEdge(sourceNode, targetNode));
	      String edgeName = sourceName + " (" + interactionType + ") " + targetName;
	      int previousMatchingEntries = edgeAttributes.countIdentical(edgeName);
	      if (previousMatchingEntries > 0)
		  edgeName = edgeName + "_" + previousMatchingEntries;
	      edgeAttributes.add ("interaction", edgeName, interactionType);
	      edgeAttributes.addNameMapping (edgeName, edge);
	  }

      }

  } // read

  /**
   * @return the Graph2D that was created
   */
  public Graph2D getGraph () {
    return graph;

  } // createGraph

  /**
   * @return null, there is no GML reader available outside of Y-Files right now
   */
  public RootGraph getRootGraph () {
    return rootGraph;

  } // getRootGraph

  /**
   * @return the Edge Attributes that were read in from the GML file.
   */
  public GraphObjAttributes getEdgeAttributes () {
    return edgeAttributes;

  } // getEdgeAttributes
 
} // class GMLReader


