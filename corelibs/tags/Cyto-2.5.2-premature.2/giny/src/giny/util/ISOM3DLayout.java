/*
 * This is based on the ISOMLayout from the JUNG project.
 */

package giny.util;

import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.impl.DenseDoubleMatrix1D;
import cern.colt.map.OpenIntObjectHashMap;
import cern.colt.map.PrimeFinder;

import giny.model.*;
import giny.view.*;

/** @deprecated Yell loudly (cytoscape-discuss@googlegroups.com) if you want to keep this.
    If we do keep it, it will move somewhere else.  If no one yells, it'll be removed 10/2007 */
public class ISOM3DLayout extends Abstract3DLayout {

  
	private int maxEpoch;
	private int epoch;

	private int radiusConstantTime;
	private int radius;
	private int minRadius;

	private double adaption;
	private double initialAdaption;
	private double minAdaption;

	private double factor;
	private double coolingFactor;

  private boolean trace;
	private boolean done;

  private Vector queue;
	private String status = null;

  private OpenIntObjectHashMap nodeIndexToDataMap;

  private double globalX, globalY, globalZ;



  public ISOM3DLayout ( GraphView view ) {
    super( view );
    nodeIndexToDataMap = new OpenIntObjectHashMap( PrimeFinder.nextPrime( view.getNodeViewCount() ) );
    queue = new Vector();
		trace = false;
  }

  public void doLayout () {
    initialize( null );
    while ( epoch < maxEpoch ) {
      advancePositions();
      System.out.println( getStatus() );
    }
    Iterator nodes = graphView.getNodeViewsIterator();
    while ( nodes.hasNext() ) {
      ( ( NodeView )nodes.next() ).setNodePosition( true );
    }


  }

  	/**
	 * Returns the current number of epochs and execution status, as a string.
	 */
	public String getStatus() {
		return status;
	}

  protected void initialize_local() {
		done = false;

		maxEpoch = 2000;
		epoch = 1;

		radiusConstantTime = 100;
		radius = 5;
		minRadius = 1;

		initialAdaption = 90.0D / 100.0D;
		adaption = initialAdaption;
		minAdaption = 0;

    coolingFactor = 2;

  }

  protected void initialize_local_node_view( NodeView v) {
		ISOMVertexData vd = getISOMVertexData(v);
		if (vd == null) {
      vd = new ISOMVertexData();
      nodeIndexToDataMap.put( v.getGraphPerspectiveIndex(), vd );
		}
		vd.visited = false;
	}

  /**
	* Advances the current positions of the graph elements.
	*/
	public void advancePositions() {
		status = "epoch: " + epoch + "; ";
		if (epoch < maxEpoch) {
			adjust();
			updateParameters();
			status += " status: running";

		} else {
			status += "adaption: " + adaption + "; ";
			status += "status: done";
			done = true;
		}
	}

  private synchronized void adjust() {
		//Generate random position in graph space
		ISOMVertexData tempISOM = new ISOMVertexData();
		

		// creates a new XY data location
		globalX = 10 + Math.random() * getCurrentSize().getWidth();
    globalY = 10 + Math.random() * getCurrentSize().getHeight();
    
    // arbitrary depth
    globalZ = 10 + Math.random() * 50;

    //Get closest vertex to random position
    NodeView winner = getNodeView( globalX, globalY, globalZ );
    
		for (Iterator iter = graphView.getNodeViewsIterator();
			iter.hasNext();
			) {
			NodeView v = ( NodeView ) iter.next();
			ISOMVertexData ivd = getISOMVertexData(v);
			ivd.distance = 0;
			ivd.visited = false;
		}
		adjustVertex(winner);
	}
  
  private synchronized void updateParameters() {
		epoch++;
		double factor = Math.exp(-1 * coolingFactor * (1.0 * epoch / maxEpoch));
		adaption = Math.max(minAdaption, factor * initialAdaption);
    if ((radius > minRadius) && (epoch % radiusConstantTime == 0)) {
			radius--;
		}
	}

  private synchronized void adjustVertex( NodeView v ) {
		queue.removeAllElements();
		ISOMVertexData ivd = getISOMVertexData(v);
		ivd.distance = 0;
		ivd.visited = true;
		queue.add(v);
		NodeView current;

		while ( !queue.isEmpty() ) {
			current = ( NodeView ) queue.remove(0);
			ISOMVertexData currData = getISOMVertexData(current);
			
      int current_index = current.getGraphPerspectiveIndex();
      double current_x = graphView.getNodeDoubleProperty( current_index, GraphView.NODE_X_POSITION );
      double current_y = graphView.getNodeDoubleProperty( current_index, GraphView.NODE_Y_POSITION );
      double current_z = graphView.getNodeDoubleProperty( current_index, GraphView.NODE_Y_POSITION );


			double dx = globalX - current_x;
			double dy = globalY - current_y;
      double dz = globalZ - current_z;

			double factor = adaption / Math.pow(2, currData.distance);

      graphView.setNodeDoubleProperty( current_index, GraphView.NODE_X_POSITION, current_x + factor * dx );
      graphView.setNodeDoubleProperty( current_index, GraphView.NODE_Y_POSITION, current_y + factor * dy );
      graphView.setNodeDoubleProperty( current_index, GraphView.NODE_Z_POSITION, current_z + factor * dz );

			if (currData.distance < radius) {
				int[] neighbors = graphView.getGraphPerspective().neighborsArray( current_index );
        for ( int neighbor_index = 0; neighbor_index < neighbors.length; ++neighbor_index ) {
          NodeView child = graphView.getNodeView( neighbors[ neighbor_index ] );
          ISOMVertexData childData = getISOMVertexData(child);
					if (!childData.visited) {
						childData.visited = true;
						childData.distance = currData.distance + 1;
						queue.addElement(child);
					}
				}
			}
		}
	}

	public ISOMVertexData getISOMVertexData (  NodeView v ) {
    return ( ISOMVertexData )nodeIndexToDataMap.get( v.getGraphPerspectiveIndex() );
  }
  
  public ISOMVertexData getISOMVertexData (  int v ) {
    return ( ISOMVertexData )nodeIndexToDataMap.get( v );
  }                                                

  /**
	 * This one is an incremental visualization.
	 * @return <code>true</code> is the layout algorithm is incremental, <code>false</code> otherwise
	 */
	public boolean isIncremental() {
		return true;
	}

  /**
	 * For now, we pretend it never finishes.
	 * @return <code>true</code> is the increments are done, <code>false</code> otherwise
	 */
	public boolean incrementsAreDone() {
		return false;
	}

  public static class ISOMVertexData {
		public DoubleMatrix1D disp;

		int distance;
		boolean visited;

		public ISOMVertexData() {
			initialize();
		}

		public void initialize() {
			disp = new DenseDoubleMatrix1D(3);

			distance = 0;
			visited = false;
		}

		public double getXDisp() {
			return disp.get(0);
		}

		public double getYDisp() {
			return disp.get(1);
		}

    public double getZDisp() {
			return disp.get(2);
		}


		public void setDisp(double x, double y, double z) {
			disp.set(0, x);
			disp.set(1, y);
      disp.set(2, z);
    }

		public void incrementDisp(double x, double y, double z) {
			disp.set(0, disp.get(0) + x);
			disp.set(1, disp.get(1) + y);
      disp.set(2, disp.get(2) + z);
		}

		public void decrementDisp(double x, double y, double z) {
			disp.set(0, disp.get(0) - x);
			disp.set(1, disp.get(1) - y);
      disp.set(2, disp.get(2) - z);
		}
	}
}
