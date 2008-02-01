/**
 * This code is based on the ForceDirectedLayout of openJGraph
 */

package giny.util;

import java.util.*;
import giny.model.*;
import giny.view.*;
import java.awt.geom.Point2D;

/**
 * An implementation of a directed-force layout using logarithmic springs
 * and electrical forces, as discussed in Chapter 10 of the book
 * "Graph Drawing".
 * <p>
 * However, note that the implementation is a bit different to the equation
 * 10.2 in the book such that:
 * <ul>
 * <li>Electrical repulsion is subtracted from the force of the spring, since
 * they tend to be opposite forces. The book adds them instead of subtracting.</li>
 * <li>The distance between the vertices in the calculation of the electrical
 * repulsion is not squared or multiplied by itself.</li>
 * <li>This used logarithimic springs</li>
 * </ul>
 * I am not a mathematician, but the above adjustments to the equation greatly
 * improved the force-directed layout.
 *
 * @author      Jesus M. Salvo Jr.
 * @author Rowan H Christmas
 */

/** @deprecated Yell loudly (cytoscape-discuss@googlegroups.com) if you want to keep this.
    If we do keep it, it will move somewhere else.  If no one yells, it'll be removed 10/2007 */
public class ForceDirectedLayout extends AbstractLayout {

  private double      springLength = 30;
  private double      stiffness = 30;
  private double      electricalRepulsion = 200;
  private double      increments;
  private boolean     initialized = false;
  double NUM_INCRMENTS = 100;
  double increment  = .5;

  public ForceDirectedLayout ( GraphView g ) {
    super( g );
  }

  protected void initialize_local() {
    // no local initialization needs to be done
  }

  protected void initialize_local_node_view( NodeView  v ) {
    // no NodeView initialization needs to be done.
  }

  public void doLayout () {
    initialize( null );
    while ( !incrementsAreDone() ) {
      advancePositions();
      System.out.println( increments +" "+getStatus() );
    }
    Iterator nodes = graphView.getNodeViewsIterator();
    while ( nodes.hasNext() ) {
      ( ( NodeView )nodes.next() ).setNodePosition( true );
    }

  }

  
  /**
	 * This one is an incremental visualization
	 */
	public boolean isIncremental() {
		return true;
	}

	/**
	 * For now, we pretend it never finishes.
	 */
	public boolean incrementsAreDone() {
    if ( increments < NUM_INCRMENTS ) {
      return false;
    } 
    return true;
	}

  /**
	 * Relaxation step. Moves all nodes a smidge.
	 */
  public void advancePositions() {
    increments++;
    for (Iterator iter = graphView.getNodeViewsIterator();
         iter.hasNext();
         ) {
			NodeView v = ( NodeView ) iter.next();
			
      if ( dontMove(v) )
				continue;

      double xForce = 0;
      double yForce = 0;

      double distance;
      double spring;
      double repulsion;
      double xSpring = 0;
      double ySpring = 0;
      double xRepulsion = 0;
      double yRepulsion = 0;

      double adjacentDistance = 0;

      
      double thisX = graphView.getNodeDoubleProperty( v.getGraphPerspectiveIndex(), GraphView.NODE_X_POSITION );
      double thisY = graphView.getNodeDoubleProperty( v.getGraphPerspectiveIndex(), GraphView.NODE_Y_POSITION );
      double adjX = thisX;
      double adjY = thisY;

      int[] adjacent_nodes;
      NodeView adjacent_node_view;
      
      // Get the spring force between all of its adjacent vertices.
      adjacent_nodes = graphView.getGraphPerspective().neighborsArray( v.getGraphPerspectiveIndex() );
      for( int i = 0; i < adjacent_nodes.length; ++i ) {
        adjacent_node_view = graphView.getNodeView( adjacent_nodes[i] );
                
        adjX = graphView.getNodeDoubleProperty( adjacent_node_view.getGraphPerspectiveIndex(), GraphView.NODE_X_POSITION );
        adjY = graphView.getNodeDoubleProperty( adjacent_node_view.getGraphPerspectiveIndex(), GraphView.NODE_Y_POSITION );
            
        distance = Point2D.distance( adjX, adjY, thisX, thisY );
        if( distance == 0 )
          distance = .0001;
        
        //spring = this.stiffness * ( distance - this.springLength ) *
        //    (( thisX - adjX ) / ( distance ));
        spring = this.stiffness * Math.log( distance / this.springLength ) *
          (( thisX - adjX ) / ( distance ));
        
        xSpring += spring;
        
        //spring = this.stiffness * ( distance - this.springLength ) *
        //    (( thisY - adjY ) / ( distance ));
        spring = this.stiffness * Math.log( distance / this.springLength ) *
          (( thisY - adjY ) / ( distance ));
        
        ySpring += spring;
        
      }

     
      // Get the electrical repulsion between all vertices,
      // including those that are not adjacent.
      for ( Iterator ite = graphView.getNodeViewsIterator();
            ite.hasNext(); ) {
        NodeView other_v = ( NodeView )ite.next();
        
        if( v == other_v )
          continue;
        
        adjX = graphView.getNodeDoubleProperty( other_v.getGraphPerspectiveIndex(), GraphView.NODE_X_POSITION );
        adjY = graphView.getNodeDoubleProperty( other_v.getGraphPerspectiveIndex(), GraphView.NODE_Y_POSITION );
        
        distance = Point2D.distance( adjX, adjY, thisX, thisY );
        if( distance == 0 )
          distance = .0001;
        
        repulsion = ( this.electricalRepulsion / distance ) *
          (( thisX - adjX ) / ( distance ));
        
        xRepulsion += repulsion;
        
        repulsion = ( this.electricalRepulsion / distance ) *
          (( thisY - adjY ) / ( distance ));
        
        yRepulsion += repulsion;
      }

      // Combine the two to produce the total force exerted on the vertex.
      xForce = xSpring - xRepulsion;
      yForce = ySpring - yRepulsion;

      // Move the vertex in the direction of "the force" --- thinking of star wars :-)
      // by a small proportion
      double xadj = 0 - ( xForce * this.increment );
      double yadj = 0 - ( yForce * this.increment );

      double newX = thisX + adjX;
      double newY = thisY + adjY;
      
      // Ensure the vertex's position is never negative.
      if ( newX >= 0 && newY >= 0 ) {
         graphView.setNodeDoubleProperty( v.getGraphPerspectiveIndex(), GraphView.NODE_X_POSITION, xadj );
         graphView.setNodeDoubleProperty( v.getGraphPerspectiveIndex(), GraphView.NODE_Y_POSITION, yadj );
      
      } else if( newX < 0 && newY >= 0 ) {
        if( thisX > 0 ) {
          xadj = 0 - thisX;
        }  else { 
          xadj = 0;
        graphView.setNodeDoubleProperty( v.getGraphPerspectiveIndex(), GraphView.NODE_X_POSITION, xadj );
        graphView.setNodeDoubleProperty( v.getGraphPerspectiveIndex(), GraphView.NODE_Y_POSITION, yadj );
        }
      } else if( newY < 0 && newX >= 0 ) {
        if ( thisY > 0 ) {
          yadj = 0 - thisY;
        } else {
          yadj = 0;
        }
        graphView.setNodeDoubleProperty( v.getGraphPerspectiveIndex(), GraphView.NODE_X_POSITION, xadj );
        graphView.setNodeDoubleProperty( v.getGraphPerspectiveIndex(), GraphView.NODE_Y_POSITION, yadj );
      }
    }
  }
}


    
