package jgraph;

import org.jgraph.JGraph;
import org.jgraph.graph.*;

import org.jgraph.layout.*;

import giny.model.*;
import giny.view.*;
import giny.util.*;

import javax.swing.*;

import java.util.*;

import java.awt.geom.Rectangle2D;
import java.awt.Color;

import cern.colt.map.*;

import cytoscape.view.*;

public class JGraphExport {
  
  protected GraphView graphView;
  protected int exportType = 0;

  public JGraphExport ( CyNetworkView view, int export_type ) {
    this.graphView = ( GraphView )view;
    this.exportType = export_type;
  }

  public void doExport ( ) {

    

    GraphPerspective perspective = graphView.getGraphPerspective();
    Map j_giny_node_map = new HashMap( PrimeFinder.nextPrime( perspective.getNodeCount() ) );
    Map giny_j_node_map = new HashMap( PrimeFinder.nextPrime( perspective.getNodeCount() ) );
    Map j_giny_edge_map = new HashMap( PrimeFinder.nextPrime( perspective.getEdgeCount() ) );

    Iterator node_iterator = perspective.nodesIterator();
    Iterator edge_iterator = perspective.edgesIterator();


    // Construct Model and Graph
    //
    GraphModel model = new DefaultGraphModel();
    JGraph graph = new JGraph(model);
   
    // Create Nested Map (from Cells to Attributes)
    //
    Map attributes = new Hashtable();

   
    Set cells = new HashSet();


    // create Vertices
    while ( node_iterator.hasNext() ) {

      // get the GINY node and node view
      giny.model.Node giny = ( giny.model.Node )node_iterator.next();
      NodeView node_view = graphView.getNodeView( giny );

      DefaultGraphCell jcell = new DefaultGraphCell( giny.getIdentifier() );
      
      // Create Vertex Attributes
      //
      AttributeMap attrib = new AttributeMap(); 
      attributes.put( jcell, attrib);
    
      // Set bounds
      Rectangle2D bounds = new Rectangle2D.Double( node_view.getXPosition(), 
                                                   node_view.getYPosition(),
                                                   node_view.getWidth(),
                                                   node_view.getHeight() );

      GraphConstants.setBounds( attrib, bounds);


      j_giny_node_map.put( jcell, giny );
      giny_j_node_map.put( giny, jcell );

      cells.add( jcell );

    }

    while ( edge_iterator.hasNext() ) {
      
      giny.model.Edge giny = ( giny.model.Edge )edge_iterator.next();
      
      DefaultGraphCell j_source = ( DefaultGraphCell )giny_j_node_map.get( giny.getSource() );
      DefaultGraphCell j_target = ( DefaultGraphCell )giny_j_node_map.get( giny.getTarget() );

      DefaultPort source_port = new DefaultPort();
      DefaultPort target_port = new DefaultPort();

      j_source.add( source_port );
      j_target.add( target_port );

      // create the edge
      DefaultEdge jedge = new DefaultEdge();
      
      j_giny_edge_map.put( jedge, giny );


      // Create Edge Attributes
      //
      
      AttributeMap edgeAttrib = new AttributeMap(); 
      attributes.put(jedge, edgeAttrib);
    
      // Connect Edge
      //
      ConnectionSet cs = new ConnectionSet( jedge, source_port, target_port);

      Object[] ecells = new Object[] { jedge, j_source, j_target };
      // Insert into Model
      //
      model.insert( ecells, attributes, cs, null, null);

      cells.add( jedge );

    }

    if ( exportType == 0 ) {
      
      String dot = org.jgraph.util.JGraphGraphvizEncoder.encode( graph, cells.toArray() ) ;

      System.out.println( dot );
    }



    // I don't think that any of the current layouts have edge components, 
    // so I won't bother for now.

    model = null;
    graph = null;
    attributes = null;
    cells = null;
    System.gc();
    
  }

}
