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
  GraphObjAttributes nodeAttributes = new GraphObjAttributes ();
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

	  Vector gmlCreator = gml.getVector("Creator");
	  Vector gmlGraphLabel = gml.getVector("graph", "label");

	  GMLObject gmlNodes = gml.getGML("graph", "node");
	  GMLObject gmlEdges = gml.getGML("graph", "edge");

	  Vector nodeIds     = gmlNodes.getVector("node", "id");
	  Vector nodeLabels  = gmlNodes.getVector("node", "label");

	  // in case gml node ids are not ordered consecutively (0..n)
          Hashtable nodeNameMap  = new Hashtable(nodeIds.size());
	  for(int i=0; i<nodeIds.size(); i++)
	      nodeNameMap.put(nodeIds.get(i), nodeLabels.get(i));

	  Vector edgeSources = gmlEdges.getVector("edge", "source");
	  Vector edgeLabels  = gmlEdges.getVector("edge", "label");
	  Vector edgeTargets = gmlEdges.getVector("edge", "target");

	  //GMLObject gmlNodeGraphics = gmlNodes.getGML("node", "graphics");
	  //GMLObject gmlEdgeGraphics = gmlEdges.getGML("edge", "graphics");

	  //---------------------------------------------------------------------------
	  // set a default edge type if it's not defined in the GML file
	  // need a better system for defining the default...
	  String et = "pp";
	  if(edgeLabels.isEmpty())
	      for(int i=0; i < edgeSources.size(); i++)
		  edgeLabels.add(et);

	  //---------------------------------------------------------------------------
	  // loop through all of the nodes (using a hash to avoid duplicates)
	  // adding nodes to the rootGraph. 
	  // Create the nodeName mapping in nodeAttributes
	  //---------------------------------------------------------------------------
	  Hashtable nodeHash = new Hashtable ();
	  String nodeName, interactionType;
	  for(int i=0; i<nodeIds.size(); i++) {
	      nodeName = (String) nodeNameMap.get(nodeIds.get(i));
	      //if(canonicalize) nodeName = canonicalizeName(nodeName);	      
	      if (!nodeHash.containsKey(nodeName)) {
		  giny.model.Node node = rootGraph.getNode(rootGraph.createNode());
		  node.setIdentifier(nodeName);
		  nodeHash.put(nodeName, node);
		  nodeAttributes.addNameMapping(nodeName, node);
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
	  String sourceName, targetName, edgeName;
	  for (int i=0; i < edgeSources.size(); i++) {
	      sourceName = (String) nodeNameMap.get(edgeSources.get(i));
	      targetName = (String) nodeNameMap.get(edgeTargets.get(i));
	      interactionType = (String) edgeLabels.get(i);
	      //if(canonicalize) nodeName = canonicalizeName(nodeName);
	      //if(canonicalize) targetName = canonicalizeName(targetName);

	      giny.model.Node sourceNode = (giny.model.Node) nodeHash.get(sourceName);
	      giny.model.Node targetNode = (giny.model.Node) nodeHash.get(targetName);
	      giny.model.Edge edge = rootGraph.getEdge(rootGraph.createEdge(sourceNode, targetNode));
	      edgeName = sourceName + " (" + interactionType + ") " + targetName;
	      int previousMatchingEntries = edgeAttributes.countIdentical(edgeName);
	      if (previousMatchingEntries > 0)
		  edgeName = edgeName + "_" + previousMatchingEntries;
	      edgeAttributes.add("interaction", edgeName, interactionType);
	      edgeAttributes.addNameMapping(edgeName, edge);
	  }

	  //--------------------------------------------------
	  // load vectors with concatonated keys and their values
	  //
	  Vector nodeAttribs = new Vector();
 	  Vector edgeAttribs = new Vector();
	  Vector nodeValues = new Vector();
 	  Vector edgeValues = new Vector();
	  gmlNodes.flatPairs(nodeAttribs, nodeValues);
 	  gmlEdges.flatPairs(edgeAttribs, edgeValues);

	  //--------------------------------------------------
	  // load node attributes from here
	  //
	  nodeName = "";
	  for(int i=0; i<nodeAttribs.size(); i++) {
	      String attrib = (String) nodeAttribs.get(i);
	      Object  value = (Object) nodeValues.get(i);
	      if(attrib.equals("gml.node.label"))
		  nodeName = (String) value;
	      if(attrib.startsWith("gml.node.graphics")) {
		  //System.out.println(attrib + "\t" + nodeName + "\t" + value);
		  nodeAttributes.add(attrib, nodeName, value);
	      }
	  }
	  //--------------------------------------------------
	  // load edge attributes from here
	  //
	  // to prevent initialization error
	  interactionType = (String) edgeLabels.get(0);
	  sourceName = (String) nodeNameMap.get(edgeSources.get(0));
	  targetName = (String) nodeNameMap.get(edgeTargets.get(0));
	  edgeName = "";
	  int edgeCount = 0;
 	  for(int i=0; i<edgeAttribs.size(); i++) {
 	      String attrib = (String) edgeAttribs.get(i);
 	      Object value = (Object) edgeValues.get(i);
 	      if(attrib.equals("gml.edge.source")) {
 		  sourceName = (String) nodeNameMap.get(value);
		  interactionType = (String) edgeLabels.get(edgeCount);
		  edgeCount++;
	      }
 	      if(attrib.equals("gml.edge.target")) {
		  targetName = (String) nodeNameMap.get(value);
		  edgeName = sourceName + " (" + interactionType + ") " + targetName;
	      }
 	      if(attrib.startsWith("gml.edge.graphics")) {
 		  //System.out.println(attrib + "\t" + edgeLabel + "\t" + value);
 		  edgeAttributes.add(attrib, edgeName, value);
 	      }
 	  }
      }

  } // read

  /**
   * @return the Graph2D that was created
   */
  public Graph2D getGraph () {
    return graph;
  }

  /**
   * @return null, there is no GML reader available outside of Y-Files right now
   */
  public RootGraph getRootGraph () {
    return rootGraph;
  }

  /**
   * @return the node attributes that were read in from the GML file.
   */
  public GraphObjAttributes getNodeAttributes () {
    return nodeAttributes;
  }

  /**
   * @return the edge attributes that were read in from the GML file.
   */
  public GraphObjAttributes getEdgeAttributes () {
    return edgeAttributes;
  }
 
} // class GMLReader


