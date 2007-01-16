/*
 * This is based on the ISOMLayout from the JUNG project.
 */

package csplugins.layout.algorithms.graphPartition;

import cern.colt.list.IntArrayList;
import cern.colt.matrix.impl.DenseDoubleMatrix1D;
import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.map.OpenIntObjectHashMap;
import cern.colt.map.PrimeFinder;

import cytoscape.CyNetwork;
import giny.model.*;

import java.util.Iterator;


public class ISOMLayout extends AbstractLayout {

  
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

  //Queue, First In First Out, use add() and get(0)/remove(0)
  private IntArrayList q;
	private String status = null;

  OpenIntObjectHashMap nodeIndexToDataMap;
  double globalX, globalY;
  GraphPerspective net;
  double squared_size;
  
  public ISOMLayout ( CyNetwork network ) {
    super( network );
  
    q = new IntArrayList();
		trace = false;
 
  }

  public void layoutPartion ( GraphPerspective net) {
    
    this.net = net;

		int nodeCount = net.getNodeCount();
    nodeIndexToDataMap = new OpenIntObjectHashMap( PrimeFinder.nextPrime( nodeCount ) );
    squared_size = nodeCount*50;
   
		epoch = 1;
    maxEpoch = 5000;
   
		radiusConstantTime = 100;
		radius = 5;
		minRadius = 1;

		initialAdaption = 90.0D / 100.0D;
		adaption = initialAdaption;
		minAdaption = 0;

    coolingFactor = 2;

    System.out.println ( "Epoch: "+epoch+" maxEpoch: "+maxEpoch );
    while ( epoch < maxEpoch ) {
      adjust();
			updateParameters();
    }
   
  }

  /**
   * @return the closest NodeView to these coords.
   */
  public int getClosestPosition ( double x, double y ) {
    double minDistance = Double.MAX_VALUE;
    int closest = 0;
		Iterator nodeIter = net.nodesIterator();
		while (nodeIter.hasNext()) {
			int nodeIndex = ((Node)nodeIter.next()).getRootGraphIndex();

      double dx = layout.getX(  nodeIndex );
      double dy = layout.getY(  nodeIndex );
      double dist = dx * dx + dy * dy;
      if ( dist < minDistance ) {
        minDistance = dist;
        closest = nodeIndex;
      }
    }
    return closest;
  }



  public  void adjust() {
		//Generate random position in graph space
		ISOMVertexData tempISOM = new ISOMVertexData();
		

		// creates a new XY data location
		globalX = 10 + Math.random() * squared_size;
    globalY = 10 + Math.random() * squared_size;

    //Get closest vertex to random position
    int winner = getClosestPosition( globalX, globalY );
    
		Iterator nodeIter = net.nodesIterator();
		while (nodeIter.hasNext()) {
			int nodeIndex = ((Node)nodeIter.next()).getRootGraphIndex();
      ISOMVertexData ivd = getISOMVertexData(nodeIndex);
			ivd.distance = 0;
			ivd.visited = false;
		}
		adjustVertex(winner);
	}
  
  public  void updateParameters() {
		epoch++;
		double factor = Math.exp(-1 * coolingFactor * (1.0 * epoch / maxEpoch));
		adaption = Math.max(minAdaption, factor * initialAdaption);
    if ((radius > minRadius) && (epoch % radiusConstantTime == 0)) {
			radius--;
		}
	}

  public void adjustVertex( int v ) {
		q.clear();
		ISOMVertexData ivd = getISOMVertexData(v);
		ivd.distance = 0;
		ivd.visited = true;
		q.add(v);
		int current;

    while ( !q.isEmpty() ) {
			current =  q.get(0);
      q.remove(0);

			ISOMVertexData currData = getISOMVertexData(current);
			
      double current_x = layout.getX( current );
      double current_y = layout.getY( current );
      
			double dx = globalX - current_x;
			double dy = globalY - current_y;
      
      // possible mod
			double factor = adaption / Math.pow(2, currData.distance) ;
      

      layout.setX( current, current_x + factor * dx );
      layout.setY( current, current_y + factor * dy );

      
			if (currData.distance < radius) {
				int[] neighbors = neighborsArray( net, current );

        for ( int neighbor_index = 0; neighbor_index < neighbors.length; ++neighbor_index ) {
          
          ISOMVertexData childData = getISOMVertexData(neighbors[neighbor_index]);
					if (!childData.visited) {
						childData.visited = true;
						childData.distance = currData.distance + 1;
            q.add(neighbors[neighbor_index]);
					}
				}
			}
		}
	}

  public ISOMVertexData getISOMVertexData (  int v ) {
    ISOMVertexData vd = ( ISOMVertexData )nodeIndexToDataMap.get( v );
    if ( vd == null ) {
      vd = new ISOMVertexData();
      nodeIndexToDataMap.put( v, vd );
    }
    return vd;
  }                                                


  public static class ISOMVertexData {
		public DoubleMatrix1D disp;

		int distance;
		boolean visited;

		public ISOMVertexData() {
			initialize();
		}

		public void initialize() {
			disp = new DenseDoubleMatrix1D(2);

			distance = 0;
			visited = false;
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

	// This is here to replace the deprecated neighborsArray function
	public int[] neighborsArray ( GraphPerspective network, int nodeIndex ) {
		// Get a list of edges
		int[] edges = network.getAdjacentEdgeIndicesArray(nodeIndex, true, true, true);
		int[] neighbors = new int[edges.length];
		int offset = 0;
		for (int edge = 0; edge < edges.length; edge++) {
			int source = network.getEdgeSourceIndex(edge);
			int target = network.getEdgeTargetIndex(edge);
			if (source != nodeIndex)
				neighbors[offset++] = source;
			else
				neighbors[offset++] = target;
		}
		return neighbors;
	}
}
