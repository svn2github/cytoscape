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
import java.awt.geom.Point2D;

import giny.model.*;
import giny.view.*;
import cytoscape.*;
import cytoscape.util.GinyFactory;

import cytoscape.data.GraphObjAttributes;
import cern.colt.list.IntArrayList;
import cern.colt.map.OpenIntIntHashMap;


//-------------------------------------------------------------------------------------
/**
 * Reader for graph in GML format. Given the filename provides the graph and
 * attributes objects constructed from the file.
 */
public class GMLReader implements GraphReader {
  private String filename;
 
  GMLTree gmlTree;

  IntArrayList nodes;
  IntArrayList edges;
  OpenIntIntHashMap edgeMap;
  /**
   * @param filename The GML file to be read in
   */
  public GMLReader ( String filename ) {
    this.filename = filename;
     // create and read the GML file
    gmlTree = new GMLTree(filename);
  }


 //  public GMLReader ( String zip_entry, boolean is_zip ) {
//     gmlTree = new GMLTree( zip_entry, is_zip );
//   }

  /**
   * Equivalent to read(), as names in a GML file are not currently canonicalized.
   */
  public void read ( boolean canonicalize ) {
    read();
  }

  
  /**
   * This will read AND create a Graph from the file specified in the constructor
   */
  public void read () {
   
    Vector nodeIds = gmlTree.getVector(new String[] {"graph","node","id"},GMLTree.INTEGER);
    Vector nodeLabels = gmlTree.getVector(new String[] {"graph","node","label"},GMLTree.STRING);

    // in case gml node ids are not ordered consecutively (0..n)
    Hashtable nodeNameMap  = new Hashtable(nodeIds.size());
    for(int i=0; i<nodeIds.size(); i++) {
      nodeNameMap.put(nodeIds.get(i), nodeLabels.get(i));
    }

    Vector edgeSources = gmlTree.getVector(new String[] {"graph","edge","source"},GMLTree.INTEGER);
    Vector edgeTargets = gmlTree.getVector(new String[] {"graph","edge","target"},GMLTree.INTEGER);
    Vector edgeLabels  = gmlTree.getVector(new String[] {"graph","edge","label"},GMLTree.STRING);
       
    //Use the number of ids to get the number of nodes in hte graph
    //Use the number of source ids to get the number of edges in hte graph
    //(since every edge must have some source node)
    Cytoscape.ensureCapacity( nodeIds.size(), edgeSources.size() );
    nodes = new IntArrayList( nodeIds.size() );
    edgeMap = new OpenIntIntHashMap( edgeSources.size() );

    //---------------------------------------------------------------------------
    // set a default edge type if it's not defined in the GML file
    // need a better system for defining the default...
    String et = "pp";
    if(edgeLabels.isEmpty()) {
      for(int i=0; i < edgeSources.size(); i++) {
        edgeLabels.add(et);
      }
    }
       
    //---------------------------------------------------------------------------
    // loop through all of the nodes (using a hash to avoid duplicates)
    // adding nodes to the Cytoscape.getRootGraph(). 
    // Create the nodeName mapping in Cytoscape.getNodeNetworkData()
    //---------------------------------------------------------------------------
    Hashtable gmlId2GINYId = new Hashtable();
    String nodeName, interactionType;

    // removed to do name check, yes I know its not as fast :( RHV
    //    int[] new_nodes = Cytoscape.getRootGraph().createNodes(nodeIds.size());
  
    Iterator idIt = nodeIds.iterator();
    for(int i=0; i<nodeIds.size(); i++) {
      Integer gmlId = (Integer)idIt.next();
      nodeName = (String)nodeNameMap.get(gmlId);
      Node node = ( Node )Cytoscape.getCyNode( nodeName, true );
      nodes.add( node.getRootGraphIndex() );
      gmlId2GINYId.put(gmlId,new Integer(node.getRootGraphIndex()));
    }

    Iterator sourceIt = edgeSources.iterator();
    Iterator labelIt = edgeLabels.iterator();
    Iterator targetIt = edgeTargets.iterator();
    
    edges = new IntArrayList(edgeSources.size());
    for ( int i = 0; i < edgeSources.size(); ++i ) {
      interactionType = (String)labelIt.next();
      String sourceName = ( String )nodeNameMap.get(sourceIt.next());
      String targetName = ( String )nodeNameMap.get(targetIt.next());
      String edgeName = ""+sourceName+" ("+interactionType+") "+targetName;

      Edge edge = ( Edge )Cytoscape.getCyEdge( sourceName, 
                                               edgeName, 
                                               targetName,
                                               interactionType );
      edges.add(edge.getRootGraphIndex());
      edgeMap.put( edge.getRootGraphIndex(), 0 );
    } // end of for ()
    
  } // read

  /**
   * @return null, there is no GML reader available outside of Y-Files right now
   */
  public RootGraph getRootGraph () {
    return Cytoscape.getRootGraph();
  }

  /**
   * @return the node attributes that were read in from the GML file.
   */
  public GraphObjAttributes getNodeAttributes () {
    return Cytoscape.getNodeNetworkData();
  }

  /**
   * @return the edge attributes that were read in from the GML file.
   */
  public GraphObjAttributes getEdgeAttributes () {
    return Cytoscape.getEdgeNetworkData();
  }
  public void layout(GraphView myView){
    
    if ( myView == null || myView.nodeCount() == 0 ) {
      return;
    }

    if(gmlTree == null){
      throw new RuntimeException("Failed to read gml file on initialization");
    }
    else {
      Vector nodeLabels  = gmlTree.getVector(new String[] {"graph","node","label"},GMLTree.STRING);
      Vector nodeX = gmlTree.getVector(new String[] {"graph","node","graphics","x"},GMLTree.DOUBLE);
      Vector nodeY = gmlTree.getVector(new String[] {"graph","node","graphics","y"},GMLTree.DOUBLE);

      Vector nodeHeight = gmlTree.getVector(new String[] {"graph","node","graphics","h"},GMLTree.DOUBLE);
      Vector nodeWidth  = gmlTree.getVector(new String[] {"graph","node","graphics","w"},GMLTree.DOUBLE);
      Vector nodeType   = gmlTree.getVector(new String[] {"graph","node","graphics","type"},GMLTree.STRING);

      String ELLIPSE = "ellipse";
      String RECTANGLE = "rectangle";

      //for (Iterator nodeIt = Cytoscape.getRootGraph().nodesIterator();nodeIt.hasNext();i++) {
      //Let's play the game, "Who can develop the kludgiest fix for this bug?"
      for (int i=0;i<nodes.size();i++) {
	Node current = Cytoscape.getRootGraph().getNode(nodes.get(i));
	//Ladies and gentlemen, I present your current and reigning champion
	String nodeName = (String)nodeLabels.get(i);
	//why are we doing this? The identifier potentially gets canonicalized which screws with this
	//comparison, I'm not actually sure how it was working before [ed: poorly ]
	//if ( !current.getIdentifier().equals(nodeName)) {
	//  throw new RuntimeException("Unexpected node ordering when laying out:"+current.getIdentifier()+" "+nodeName);
	//} // end of if ()
	Number X = (Number) nodeX.get(i);
	Number Y = (Number) nodeY.get(i);
	
	NodeView currentView = myView.getNodeView(current);
	currentView.setXPosition(X.doubleValue());
	currentView.setYPosition(Y.doubleValue());
	if( nodeHeight.size() == nodeLabels.size() )
	  currentView.setHeight( ((Double) nodeHeight.get(i)).doubleValue());
	if( nodeWidth.size() == nodeLabels.size() )
	  currentView.setWidth( ((Double) nodeWidth.get(i)).doubleValue());
	if( nodeType.size() == nodeLabels.size() ) {
	  String nType = (String) nodeType.get(i);
	  if( nType.equals(ELLIPSE) ) {
	    currentView.setShape(NodeView.ELLIPSE);
	  }
	  else if( nType.equals(RECTANGLE) ) currentView.setShape(NodeView.RECTANGLE);		      
	}
      }
    }
    // vector of GMLTrees rooted at "edge" 
    Vector edgeGML  = gmlTree.getVector(new String[]{"graph","edge"},GMLTree.GMLTREE);
    String [] xKeys = new String[] {"point","x"};
    String [] yKeys = new String[] {"point","y"};
    String [] lineKeys = new String[] {"graphics","Line"};
    // ASSUMING THE ORDER IN THE GML IS THE SAME AS THAT RETURNED BY THE ITERATOR!!!!
    // exclamation points are fun!!!!
    for ( int i = 0 ; i < edges.size() ; i++) {
      Edge current = (Edge)Cytoscape.getRootGraph().getEdge(edges.get(i));
      GMLTree tmpGML = (GMLTree)edgeGML.get(i);
      Vector tmpLine = tmpGML.getVector(lineKeys, GMLTree.GMLTREE);
      if(tmpLine.size()==1) {
	GMLTree Line = (GMLTree)tmpLine.get(0); 
	EdgeView currentView = myView.getEdgeView(current);
	currentView.getBend().removeAllHandles();
	//currentView.getBend().setHandles(); // we want to do this
	Vector tmpXs = Line.getVector(xKeys,GMLTree.DOUBLE);
	Vector tmpYs = Line.getVector(yKeys,GMLTree.DOUBLE);
	
	// Edge Handles are reversed for some reason??
	for(int j=tmpXs.size()-2; j>0; j--) {
	  Point2D.Double pt = new Point2D.Double(((Double)tmpXs.get(j)).doubleValue(), ((Double)tmpYs.get(j)).doubleValue());
	  currentView.getBend().addHandle( pt );
	}
      } // END if(tmpLine.size()==1)
    } // END for(Iterator...
  }
  
  
  public int[] getNodeIndicesArray() {
    nodes.trimToSize();
    //System.out.println( "GML: nodes array size: "+nodes.size() );
    return nodes.elements();
  }

  public int[] getEdgeIndicesArray() {
    //System.out.println( "GML: edges array size: "+edge_indices_array.length );
    // return edge_indices_array;
    IntArrayList edge_indices = new IntArrayList( edgeMap.size() );
    edgeMap.keys( edge_indices );
    edge_indices.trimToSize();
    return edge_indices.elements();
  }

}




