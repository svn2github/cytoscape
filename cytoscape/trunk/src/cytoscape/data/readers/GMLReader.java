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
import y.base.*;
import y.view.*;

import y.io.YGFIOHandler;
import y.io.GMLIOHandler;

import giny.model.RootGraph;

import cytoscape.GraphObjAttributes;
//-------------------------------------------------------------------------------------
public class GMLReader implements GraphReader {
  private String filename;
  GraphObjAttributes edgeAttributes = new GraphObjAttributes ();
  Graph2D graph;    


  /**
   * @param filename The GML file to be read in
   */
  public GMLReader ( String filename ) {
    this.filename = filename;
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
    return null;
  } // getRootGraph

  /**
   * @return the Edge Attributes that were read in from the GML file.
   */
  public GraphObjAttributes getEdgeAttributes () {
    return edgeAttributes;

  } // getEdgeAttributes
 
} // class GMLReader


