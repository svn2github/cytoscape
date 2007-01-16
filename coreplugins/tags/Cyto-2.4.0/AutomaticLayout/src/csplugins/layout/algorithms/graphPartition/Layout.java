package csplugins.layout.algorithms.graphPartition;

import giny.model.*;
import giny.view.*;

import cern.colt.list.*;
import cern.colt.map.*;

import java.util.*;

import csplugins.layout.algorithms.GraphPartition;

/**
 * Class that represents the Layout of a given graph.
 */
public class Layout {

  OpenIntDoubleHashMap nodeXMap;
  OpenIntDoubleHashMap nodeYMap;

  GraphPerspective gp;

  public Layout ( GraphPerspective gp ) {
    this.gp = gp;
    nodeXMap = new OpenIntDoubleHashMap(  PrimeFinder.nextPrime( gp.getNodeCount() ) );
    nodeYMap = new OpenIntDoubleHashMap(  PrimeFinder.nextPrime( gp.getNodeCount() ) );
  }

  public Layout ( GraphView view, boolean load_current_values ) {
    this( view.getGraphPerspective() );
    
    // initialize current values
    if ( load_current_values ) {
      Iterator i = view.getNodeViewsIterator();
      while ( i.hasNext() ) {
        NodeView nv = ( NodeView )i.next();
        setX( nv, nv.getXPosition() );
        setY( nv, nv.getYPosition() );
      }
    }
  }

  /**
   * Apply the layout to a given GraphView
   */
  public void applyLayout ( GraphView view ) {
    
   
    List partitions = GraphPartition.partition( view.getGraphPerspective() );
    Iterator p = partitions.iterator();
   //  while ( p.hasNext() ) {
//       java.awt.Color c = new java.awt.Color( (float)Math.random(), (float)Math.random(), (float)Math.random() );
//       int[] nodes = ( int[] )p.next();
//       for ( int i = 0; i < nodes.length; ++i ) {
//         view.getNodeView( nodes[i] ).setUnselectedPaint( c );
//       }      
//     }


     Iterator i = view.getNodeViewsIterator();
     while ( i.hasNext() ) {
       NodeView nv = ( NodeView )i.next();
       nv.setXPosition( getX( nv ), false );
       nv.setYPosition( getY( nv ), false );
       nv.setNodePosition( true );
       
     }
  }

  // set

  public boolean setX ( int node, double x ) {
    return nodeXMap.put( node, x );
  }

  public boolean setY ( int node, double y ) {
    return nodeYMap.put( node, y );
  }

  public boolean setX ( Node node, double x ) {
    return nodeXMap.put( node.getRootGraphIndex(), x );
  }

  public boolean setY ( Node node, double y ) {
    return nodeYMap.put( node.getRootGraphIndex(), y );
  }

  public boolean setX ( NodeView node, double x ) {
    return nodeXMap.put( node.getRootGraphIndex(), x );
  }

  public boolean setY ( NodeView node, double y ) {
    return nodeYMap.put( node.getRootGraphIndex(), y );
  }
  
  // get

  public double getX ( int node ) {
    return nodeXMap.get( node );
  }

  public double getY ( int node ) {
    return nodeYMap.get( node );
  }

  public double getX ( Node node ) {
    return nodeXMap.get( node.getRootGraphIndex() );
  }

  public double getY ( Node node ) {
    return nodeYMap.get( node.getRootGraphIndex() );
  }

  public double getX ( NodeView node ) {
    return nodeXMap.get( node.getRootGraphIndex() );
  }

  public double getY ( NodeView node ) {
    return nodeYMap.get( node.getRootGraphIndex() );
  }


                                     
  


}
