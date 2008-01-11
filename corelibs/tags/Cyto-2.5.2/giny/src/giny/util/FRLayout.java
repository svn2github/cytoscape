/*
 * Modified using information from BioLayout and JUNG
 */

package giny.util;

import giny.model.*;
import giny.view.*;
import java.util.Iterator;
import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.impl.DenseDoubleMatrix1D;
import cern.colt.map.OpenIntObjectHashMap;
import cern.colt.map.PrimeFinder;

/** @deprecated Yell loudly (cytoscape-discuss@googlegroups.com) if you want to keep this.
    If we do keep it, it will move somewhere else.  If no one yells, it'll be removed 10/2007 */
public class FRLayout extends AbstractLayout {


	private double forceConstant;
	private double temperature;
	private int currentIteration;
	private String status = null;
  private int mMaxIterations = 50;
	private double EPSILON = 0.000001D;

  // this can be set or calculated
  
  protected OpenIntObjectHashMap nodeIndexToFRDataMap;

  public FRLayout ( GraphView view ) {
    super( view );
    nodeIndexToFRDataMap = new OpenIntObjectHashMap( PrimeFinder.nextPrime( graphView.getNodeViewCount() ) );
  }
  
  public void doLayout () {
    initialize( null );
    while ( !incrementsAreDone() ) {
      advancePositions();
      //System.out.println( getStatus() );
    }
    Iterator nodes = graphView.getNodeViewsIterator();
    while ( nodes.hasNext() ) {
      ( ( NodeView )nodes.next() ).setNodePosition( true );
    }

  }


  /**
	 * Returns the current temperature and number of iterations elapsed, as a string.
	 */
  public String getStatus () {
		return status;
	}
  
  /**
   * Allow a node to be forced into position
   */
	public void forceMove ( NodeView picked, int x, int y ) {
		//super.forceMove(picked, x, y);
	}

	protected void initialize_local() {
		currentIteration = 0;
		temperature = getCurrentSize().getWidth() / 10;
		forceConstant =
			0.75
      * Math.sqrt( getCurrentSize().getHeight()
                   * getCurrentSize().getWidth()
                   / graphView.getNodeViewCount());
    System.out.println( "Forceconstant set to: "+forceConstant);
	
  }

	protected void initialize_local_node_view( NodeView v ) {
     nodeIndexToFRDataMap.put( v.getGraphPerspectiveIndex(), new FRVertexData() );
	}


	/**
	 * Moves the iteration forward one notch, calculation attraction and
	 * repulsion between nodes and edges and cooling the temperature.
	 */
	public void advancePositions () {
		currentIteration++;
		status =
			"Num Nodes: "
        + graphView.getNodeViewCount()
				+ " Iteration: "
				+ currentIteration
				+ " temp: "
				+ temperature;
		
    // Calculate repulsion
    //    repulsion calculations are done on all non-fixed nodes
    for ( Iterator node_views_iterator = graphView.getNodeViewsIterator();
          node_views_iterator.hasNext(); ) {
			NodeView nv1 = ( NodeView )node_views_iterator.next();
			if ( dontMove( nv1 ) ) {
				// if this node is on the fixed list, then don't calculate a repulsion
        continue;
      }
			calcRepulsion( nv1 );
		}

    //Calculate attraction
    //  attraction calculations are done on all edges
    for  (Iterator edge_views_iterator = graphView.getEdgeViewsIterator(); 
          edge_views_iterator.hasNext(); ) {
			EdgeView e = (EdgeView )edge_views_iterator.next();
			calcAttraction(e);
		}

		double cumulativeChange = 0;

		for ( Iterator node_views_iterator = graphView.getNodeViewsIterator();
          node_views_iterator.hasNext(); ) {
			NodeView nv1 = ( NodeView )node_views_iterator.next();
      if ( dontMove( nv1 ) ) {
				continue;
      }
      // if this node is on the fixed list, then don't move it
			calcPositions(nv1);
		}

    // this is not just a rad call, but also operates on the temperature
		cool();
	}

  /**
   * This gets called for every NodeView. 
   */
	public void calcPositions ( NodeView node_view ) {

		FRVertexData fvd = getFRData( node_view );
    double node_view_x = graphView.getNodeDoubleProperty( node_view.getGraphPerspectiveIndex(), GraphView.NODE_X_POSITION );
    double node_view_y = graphView.getNodeDoubleProperty( node_view.getGraphPerspectiveIndex(), GraphView.NODE_Y_POSITION );

		double deltaLength =
			Math.max(EPSILON, Math.sqrt( fvd.disp.zDotProduct(fvd.disp) ) );

		double newXDisp =	fvd.getXDisp() / deltaLength * Math.min( deltaLength, temperature );

		if (Double.isNaN( newXDisp ) ) {
		//	throw new Exception("Unexpected mathematical result");
      newXDisp = 1;
      System.out.println( "Unexpected mathematical result -- newXDisp");
      System.out.println( "fvd.getXDisp(): "+fvd.getXDisp()+" deltaLength: "+deltaLength+" temperature: "+temperature );
    }

		double newYDisp =
			fvd.getYDisp() / deltaLength * Math.min( deltaLength, temperature );

    graphView.setNodeDoubleProperty( node_view.getGraphPerspectiveIndex(), GraphView.NODE_X_POSITION, node_view_x + newXDisp );
    graphView.setNodeDoubleProperty( node_view.getGraphPerspectiveIndex(), GraphView.NODE_Y_POSITION, node_view_y + newYDisp );

    if (Double.isNaN( newYDisp ) ) {
		//	throw new Exception("Unexpected mathematical result");
      newXDisp = 1;
      System.out.println( "Unexpected mathematical result -- newYDisp");
      System.out.println( "fvd.getYDisp(): "+fvd.getYDisp()+" deltaLength: "+deltaLength+" temperature: "+temperature );
    }
    

    // make sure that the node location is inside the set borders.
    // double borderWidth = getCurrentSize().getWidth() / 50.0;
// 		double newXPos = graphView.getNodeDoubleProperty( node_view.getGraphPerspectiveIndex(), GraphView.NODE_X_POSITION );
// 		if ( newXPos < borderWidth ) {
// 			newXPos = borderWidth 
//                  + Math.random() 
//                  * borderWidth * 2.0;
// 		} else if ( newXPos > ( getCurrentSize().getWidth() - borderWidth ) ) {
// 			newXPos = getCurrentSize().getWidth()
//                  - borderWidth
//                  - Math.random() 
//                  * borderWidth * 2.0;
// 		}

// 		double newYPos = graphView.getNodeDoubleProperty( node_view.getGraphPerspectiveIndex(), GraphView.NODE_Y_POSITION );
// 		if ( newYPos < borderWidth ) {
// 			newYPos = borderWidth 
//                  + Math.random() 
//                  * borderWidth * 2.0;
// 		} else if ( newYPos > ( getCurrentSize().getHeight() - borderWidth ) ) {
// 			newYPos =	getCurrentSize().getHeight()
// 					       - borderWidth
// 					       - Math.random() 
//                  * borderWidth * 2.0;
// 		}
	
//     graphView.setNodeDoubleProperty( node_view.getGraphPerspectiveIndex(), GraphView.NODE_X_POSITION, newXPos );
//     graphView.setNodeDoubleProperty( node_view.getGraphPerspectiveIndex(), GraphView.NODE_Y_POSITION, newYPos );

	}


  /**
   * Calculates the attractions for each edge
   * this should have options to be weighted
   */
	public void calcAttraction ( EdgeView e ) {
	
    int source_index = graphView.getGraphPerspective().getEdgeSourceIndex( e.getGraphPerspectiveIndex() );
    int target_index = graphView.getGraphPerspective().getEdgeTargetIndex( e.getGraphPerspectiveIndex() );

    double source_x = graphView.getNodeDoubleProperty( source_index, GraphView.NODE_X_POSITION );
    double source_y = graphView.getNodeDoubleProperty( source_index, GraphView.NODE_Y_POSITION );
    double target_x = graphView.getNodeDoubleProperty( target_index, GraphView.NODE_X_POSITION );
    double target_y = graphView.getNodeDoubleProperty( target_index, GraphView.NODE_Y_POSITION );
	
    double xDelta = source_x - target_x;
    double yDelta = source_y - target_y;

    // should be easy to extrapilate to 3-D, just need to remeber some geom...
		double deltaLength =
			Math.max(EPSILON, Math.sqrt( (xDelta * xDelta) + (yDelta * yDelta) ) );

    // this cooresponds to enright p175, fr(a) = d^2/k
    double force = ( deltaLength * deltaLength ) / forceConstant;

    //BioLayout mod:
    //double bio_force = ( ( deltaLength / weight ) * ( deltaLength / weight ) ) / forceConstant;


		if (Double.isNaN(force)) {
      //			throw new Exception("Unexpected mathematical result");
       force = 1;
       System.out.println( "Unexpected mathematical result -- force in Attraction");
       System.out.println( "deltaLength: "+deltaLength+" forceConstant: "+forceConstant);
		}

		FRVertexData fvd1 = getFRData( source_index );
		FRVertexData fvd2 = getFRData( target_index );

		fvd1.decrementDisp(
			( xDelta / deltaLength ) * force,
			( yDelta / deltaLength ) * force );
		fvd2.incrementDisp(
			( xDelta / deltaLength ) * force,
			( yDelta / deltaLength ) * force);
	}

  /**
   * Finds the replusive forces for a node
   */
	public void calcRepulsion ( NodeView nv1 ) {
		FRVertexData fvd1 = getFRData( nv1 );
		fvd1.setDisp(0, 0);

    for ( Iterator node_views_iterator = graphView.getNodeViewsIterator();
          node_views_iterator.hasNext(); ) {
      NodeView nv2 = ( NodeView )node_views_iterator.next();

			if ( dontMove( nv2 ) ) {
				// don't find the repulsize force is this node is fixed.
        continue;
      }
			if ( nv1 != nv2 ) {

        double nv1_x = graphView.getNodeDoubleProperty( nv1.getGraphPerspectiveIndex(), GraphView.NODE_X_POSITION );
        double nv1_y = graphView.getNodeDoubleProperty( nv1.getGraphPerspectiveIndex(), GraphView.NODE_Y_POSITION );
        double nv2_x = graphView.getNodeDoubleProperty( nv2.getGraphPerspectiveIndex(), GraphView.NODE_X_POSITION );
        double nv2_y = graphView.getNodeDoubleProperty( nv2.getGraphPerspectiveIndex(), GraphView.NODE_Y_POSITION );

        double xDelta = nv1_x - nv2_x;
        double yDelta = nv1_y = nv2_y;

				double deltaLength =
					Math.max(
						EPSILON,
						Math.sqrt( ( xDelta * xDelta ) + ( yDelta * yDelta ) ) );

        // this cooresponds to enright p175, fr(r) = k^2/d
        // why not negative? does it matter?
				double force = ( forceConstant * forceConstant ) / deltaLength;

        //BioLayout mod:
        //double bio_force = ( weight * ( ( forceConstant * forceConstant ) ) / deltaLength

				if (Double.isNaN(force)) {
          //		throw new Exception("Unexpected mathematical result");
          force = 1;
          System.out.println( "Unexpected mathematical result -- force in Repulsion");
          System.out.println( "deltaLength: "+deltaLength+" forceConstant: "+forceConstant);
				}

				fvd1.incrementDisp(
					( xDelta / deltaLength ) * force,
					( yDelta / deltaLength ) * force);
			}
		}
	}

	private void cool() {
		temperature *= (1.0 - currentIteration / (double) mMaxIterations);
	}

  public void setMaxIterations(int maxIterations) {
    mMaxIterations = maxIterations;
  }

   
  public FRVertexData getFRData ( NodeView v ) {
    return ( FRVertexData )nodeIndexToFRDataMap.get( v.getGraphPerspectiveIndex() );
	}

  public FRVertexData getFRData ( int v ) {
    return ( FRVertexData )nodeIndexToFRDataMap.get( v );
  }
  
 
  

	/**
	 * This one is an incremental visualization.
	 */
	public boolean isIncremental() {
		return true;
	}

	/**
	 * Returns true once the current iteration has passed the maximum count,
	 * <tt>MAX_ITERATIONS</tt>.
	 */
	public boolean incrementsAreDone() {
		if (currentIteration > mMaxIterations) {
			return true;
		}
		return false;
	}

	public static class FRVertexData {
		private DoubleMatrix1D disp;

		public FRVertexData() {
			initialize();
		}

		public void initialize() {
			disp = new DenseDoubleMatrix1D(2);
		}

		public double getXDisp() {
			return disp.get(0);
		}

		public double getYDisp() {
			return disp.get(1);
		}

		public void setDisp(double x, double y) {
			disp.set(0, x);
			disp.set(1, y);
		}

		public void incrementDisp(double x, double y) {
			disp.set(0, disp.get(0) + x);
			disp.set(1, disp.get(1) + y);
		}

		public void decrementDisp(double x, double y) {
			disp.set(0, disp.get(0) - x);
			disp.set(1, disp.get(1) - y);
		}
	}




} //FRLayout
