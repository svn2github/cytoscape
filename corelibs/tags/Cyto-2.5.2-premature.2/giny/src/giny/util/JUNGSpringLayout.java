/*
 * This code is based on the SpringLayout of JUNG
 */
package giny.util;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Iterator;

import cern.colt.map.OpenIntObjectHashMap;
import cern.colt.map.PrimeFinder;

import giny.model.*;
import giny.view.*;

/**
 * The SpringLayout package represents a visualization of a set of nodes.
 * The SpringLayout, which is initialized with a Graph, assigns X/Y
 * locations to each node. When called <code>relax()</code>, the SpringLayout moves
 * the visualization forward one step.
 */
/** @deprecated Yell loudly (cytoscape-discuss@googlegroups.com) if you want to keep this.
    If we do keep it, it will move somewhere else.  If no one yells, it'll be removed 10/2007 */
public class JUNGSpringLayout extends AbstractLayout {

  private LengthFunction lengthFunction;
	public static int RANGE = 100;
	private double FORCE_CONSTANT = 1.0 / 3.0;
  private OpenIntObjectHashMap nodeIndexToDataMap;
  private OpenIntObjectHashMap edgeIndexToDataMap;
  
  double increment;
  double NUM_INCRMENTS = 100;


  long relaxTime = 0;
	public static int STRETCH = 70;

  /**
	 * Constructor for a SpringLayout for a raw graph with associated
	 * dimension--the input knows how big the graph is. Defaults to the
	 * unit length function.
	 */
	public JUNGSpringLayout ( GraphView g ) {
		this(g, UNITLENGTHFUNCTION);
	}

  /**
	 * Constructor for a SpringLayout for a raw graph with
	 * associated component.
	 *
	 * @param g	the input Graph
	 * @param f	the length function
	 */
	public JUNGSpringLayout ( GraphView g, LengthFunction f ) {
		super(g);
		this.lengthFunction = f;
    nodeIndexToDataMap = new OpenIntObjectHashMap( PrimeFinder.nextPrime( graphView.getNodeViewCount() ) );
    edgeIndexToDataMap = new OpenIntObjectHashMap( PrimeFinder.nextPrime( graphView.getEdgeViewCount() ) );
  }

  public void doLayout () {
    initialize( null );
    while ( !incrementsAreDone() ) {
      advancePositions();
      System.out.println( increment +" "+getStatus() );
    }
    Iterator nodes = graphView.getNodeViewsIterator();
    while ( nodes.hasNext() ) {
      ( ( NodeView )nodes.next() ).setNodePosition( true );
    }

  }

  protected void initialize_local() {
    increment = 0;
		for (Iterator iter = graphView.getEdgeViewsIterator();
			iter.hasNext();
			) {
			EdgeView e = ( EdgeView ) iter.next();
			SpringEdgeData sed = getSpringData(e);
			if (sed == null) {
				sed = new SpringEdgeData(e);
				edgeIndexToDataMap.put( e.getGraphPerspectiveIndex(), sed );
			}
			calcEdgeLength(sed, lengthFunction);
		}
	}

  protected void initialize_local_node_view( NodeView  v ) {
		SpringVertexData vud = getSpringData(v);
		if (vud == null) {
			vud = new SpringVertexData();
			nodeIndexToDataMap.put( v.getGraphPerspectiveIndex(), vud );
		}
	}

  protected void calcEdgeLength ( SpringEdgeData sed, LengthFunction f ) {
		sed.length = f.getLength( sed.e );
	}

  /**
	 * Relaxation step. Moves all nodes a smidge.
	 */
  public void advancePositions() {
    increment++;
    for (Iterator iter = graphView.getNodeViewsIterator();
			iter.hasNext();
			) {
			NodeView v = ( NodeView ) iter.next();
			SpringVertexData svd = getSpringData( v );
			if (svd == null) {
				System.out.println("How confusing!");
				continue;
			}
			svd.dx /= 4;
			svd.dy /= 4;
			svd.edgedx = svd.edgedy = 0;
			svd.repulsiondx = svd.repulsiondy = 0;
		}
		relaxEdges();
		calculateRepulsion();
		moveNodes();
	}

  private void relaxEdges() {
		for (Iterator i = graphView.getEdgeViewsIterator();
         i.hasNext(); 
         ) {
			EdgeView e = ( EdgeView ) i.next();

      int source_index = graphView.getGraphPerspective().getEdgeSourceIndex( e.getGraphPerspectiveIndex() );
      int target_index = graphView.getGraphPerspective().getEdgeTargetIndex( e.getGraphPerspectiveIndex() );
      
      double source_x = graphView.getNodeDoubleProperty( source_index, GraphView.NODE_X_POSITION );
      double source_y = graphView.getNodeDoubleProperty( source_index, GraphView.NODE_Y_POSITION );
      double target_x = graphView.getNodeDoubleProperty( target_index, GraphView.NODE_X_POSITION );
      double target_y = graphView.getNodeDoubleProperty( target_index, GraphView.NODE_Y_POSITION );

			double vx = source_x - target_x;
			double vy = source_y - target_y;
			double len = Math.sqrt(vx * vx + vy * vy);

			double desiredLen = getLength( e );
			len = (len == 0) ? .0001 : len;

			// force factor: optimal length minus actual length,
			// is made smaller as the current actual length gets larger.
			// why?

			//			System.out.println("Desired : " + getLength( e ));
			double f = FORCE_CONSTANT * (desiredLen - len) / len;

			f = f * Math.pow(STRETCH / 100.0, ( graphView.getGraphPerspective().getDegree( source_index ) + graphView.getGraphPerspective().getDegree( target_index ) - 2));

      // the actual movement distance 'dx' is the force multiplied by the distance to go.
			double dx = f * vx;
			double dy = f * vy;
			SpringVertexData v1D, v2D;
			v1D = getSpringData( source_index );
			v2D = getSpringData( target_index );

			SpringEdgeData sed = getSpringData(e);
			sed.f = f;

			v1D.edgedx += dx;
			v1D.edgedy += dy;
			v2D.edgedx += -dx;
			v2D.edgedy += -dy;
		}
	}

  private void calculateRepulsion() {
		for (Iterator iter = graphView.getNodeViewsIterator();
			iter.hasNext();
			) {
			NodeView v = ( NodeView ) iter.next();
			if ( dontMove(v) )
				continue;

			SpringVertexData svd = getSpringData(v);
			double dx = 0, dy = 0;

			for (Iterator iter2 = graphView.getNodeViewsIterator();
				iter2.hasNext();
				) {
				NodeView v2 = ( NodeView ) iter2.next();
				if (v == v2)
					continue;

         double v_x = graphView.getNodeDoubleProperty( v.getGraphPerspectiveIndex(), GraphView.NODE_X_POSITION );
         double v_y = graphView.getNodeDoubleProperty( v.getGraphPerspectiveIndex(), GraphView.NODE_Y_POSITION );
         double v2_x = graphView.getNodeDoubleProperty( v2.getGraphPerspectiveIndex(), GraphView.NODE_X_POSITION );
         double v2_y = graphView.getNodeDoubleProperty( v2.getGraphPerspectiveIndex(), GraphView.NODE_Y_POSITION );
         
				double vx = v_x - v2_x;
				double vy = v_y - v2_y;
				double distance = vx * vx + vy * vy;
				if (distance == 0) {
					dx += Math.random();
					dy += Math.random();
				} else if (distance < RANGE * RANGE) {
          double factor = 1;
					dx += factor * vx / Math.pow(distance, 2);
					dy += factor * vy / Math.pow(distance, 2);
				}
			}
			double dlen = dx * dx + dy * dy;
			if (dlen > 0) {
				dlen = Math.sqrt(dlen) / 2;
				svd.repulsiondx += dx / dlen;
				svd.repulsiondy += dy / dlen;
			}
		}
	}

  protected void moveNodes() {
		
		synchronized (getCurrentSize()) {
			
      for (Iterator i = graphView.getNodeViewsIterator();
             i.hasNext();) {
				NodeView v = ( NodeView ) i.next();
				if (dontMove(v))
					continue;
				SpringVertexData vd = getSpringData(v);
				 
        double v_x = graphView.getNodeDoubleProperty( v.getGraphPerspectiveIndex(), GraphView.NODE_X_POSITION );
        double v_y = graphView.getNodeDoubleProperty( v.getGraphPerspectiveIndex(), GraphView.NODE_Y_POSITION );
        
				vd.dx += vd.repulsiondx + vd.edgedx;
				vd.dy += vd.repulsiondy + vd.edgedy;

				// keeps nodes from moving any faster than 5 per time unit
				graphView.setNodeDoubleProperty( v.getGraphPerspectiveIndex(), GraphView.NODE_X_POSITION,  v_x + (Math.max(-5, Math.min(5, vd.dx))) );
        graphView.setNodeDoubleProperty( v.getGraphPerspectiveIndex(), GraphView.NODE_Y_POSITION,  v_y + (Math.max(-5, Math.min(5, vd.dy))) );

				int width = getCurrentSize().width;
				int height = getCurrentSize().height;

				if ( v_x < 0) {
					graphView.setNodeDoubleProperty( v.getGraphPerspectiveIndex(), GraphView.NODE_X_POSITION, 0 );
        } else if ( v_x > width) {
					graphView.setNodeDoubleProperty( v.getGraphPerspectiveIndex(), GraphView.NODE_X_POSITION, width );
				}
				if ( v_y < 0) {
					graphView.setNodeDoubleProperty( v.getGraphPerspectiveIndex(), GraphView.NODE_Y_POSITION, 0);
				} else if ( v_y > height) {
					graphView.setNodeDoubleProperty( v.getGraphPerspectiveIndex(), GraphView.NODE_Y_POSITION, height );
				}

			}
		}

	}

  public SpringVertexData getSpringData ( NodeView v ) {
    return (SpringVertexData)nodeIndexToDataMap.get( v.getGraphPerspectiveIndex() );
  }
  
  public SpringVertexData getSpringData ( int v ) {
    return (SpringVertexData)nodeIndexToDataMap.get( v );
  }
  
  public SpringEdgeData getSpringData ( EdgeView e ) {
    return (SpringEdgeData)edgeIndexToDataMap.get( e.getGraphPerspectiveIndex() );
  }

  public double getLength ( EdgeView e ) {
		return ( (SpringEdgeData)edgeIndexToDataMap.get( e.getGraphPerspectiveIndex() ) ).length;
	}


  /* ---------------Length Function------------------ */

	/**
	 * If the edge is weighted, then override this method to
	 * show what the visualized length is.
	 * 
	 * @author Danyel Fisher
	 */
	public interface LengthFunction {
		public double getLength( EdgeView e);
	}

	private static final class UnitLengthFunction implements LengthFunction {
		int length;
		public UnitLengthFunction ( int length ) {
			this.length = length;
		}
		public double getLength( EdgeView e) {
			return length;
		}
	}

	public static final LengthFunction UNITLENGTHFUNCTION =
		new UnitLengthFunction(30);

  	/* ---------------User Data------------------ */

	static class SpringVertexData {
		public double edgedx;
		public double edgedy;
		public double repulsiondx;
		public double repulsiondy;

		public SpringVertexData() {
		}
		/** movement speed, x */
		public double dx;
		/** movement speed, y */
		public double dy;
	}

	static class SpringEdgeData {
		public double f;
    EdgeView e;
		double length;
		public SpringEdgeData( EdgeView e) {
			this.e = e;
		}
	}

  /* ---------------Resize handler------------------ */

	public class SpringDimensionChecker extends ComponentAdapter {
		public void componentResized(ComponentEvent e) {
			resize(e.getComponent().getSize());
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
    if ( increment < NUM_INCRMENTS ) {
      return false;
    } 
    return true;
	}

}

