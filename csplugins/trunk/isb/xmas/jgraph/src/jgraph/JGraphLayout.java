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

public class JGraphLayout {

  int AnnealingLayoutAlgorithm = 0;
  int MoenLayoutAlgorithm = 1;
  int CircleGraphLayout = 2;
  int RadialTreeLayoutAlgorithm = 3;
  int GEMLayoutAlgorithm = 4;
  int SpringEmbeddedLayoutAlgorithm = 5;
  int SugiyamaLayoutAlgorithm = 6;
  int TreeLayoutAlgorithm = 7;

  int layout_type = 0;

  protected GraphView graphView;

  public JGraphLayout ( CyNetworkView view, int layout_type, double extra_dat ) {
    this.graphView = ( GraphView )view;
    this.layout_type = layout_type;
  }

  public void doLayout ( ) {

    

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

    // now do the layout
    JGraphLayoutAlgorithm layout = null;

   //  if ( layout_type == 0 )
//       layout = new AnnealingLayoutAlgorithm();
//     else if ( layout_type == 1 )
//       layout = new MoenLayoutAlgorithm();
//     else
    if ( layout_type == 2 )
      layout = new CircleGraphLayout();
    else if ( layout_type == 3 )
      layout = new RadialTreeLayoutAlgorithm();
    //    else if ( layout_type == 4 )
    //       layout = new GEMLayoutAlgorithm( new AnnealingLayoutAlgorithm() );
    else if ( layout_type == 5 )
      layout = new SpringEmbeddedLayoutAlgorithm();
    else if ( layout_type == 6 )
      layout = new SugiyamaLayoutAlgorithm();
    // else if ( layout_type == 7 )
    //       layout = new TreeLayoutAlgorithm();
    
    layout.run( graph, cells.toArray() );
    
    GraphLayoutCache cache = graph.getGraphLayoutCache();
    Iterator i = cells.iterator();
    while ( i.hasNext() ) {
      Object cell = i.next();
      CellView cell_view = cache.getMapping( cell, false );
      if ( cell_view instanceof VertexView ) {
        // ok, we found a node
        Rectangle2D rect = cell_view.getBounds();
        giny.model.Node giny = ( giny.model.Node )j_giny_node_map.get( cell );
        NodeView node_view = graphView.getNodeView( giny );
        node_view.setXPosition( rect.getX(), false );
        node_view.setYPosition( rect.getY(), false );
        node_view.setNodePosition( true );
        
       
      }
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
