/*
 * This is based on the ISOMLayout from the JUNG project.
 */

package cytoscape.layout;

import java.util.*;

import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.impl.DenseDoubleMatrix1D;
import cern.colt.map.OpenIntObjectHashMap;
import cern.colt.map.PrimeFinder;

import cytoscape.*;
import cytoscape.view.*;
import cytoscape.util.*;
import giny.view.*;
import giny.model.*;
import giny.util.*;
import javax.swing.JFrame;
import java.awt.Dimension;

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

  private Vector queue;
	private String status = null;

  private OpenIntObjectHashMap nodeIndexToDataMap;

  private double globalX, globalY;

  CyNetwork parent;
  List partions;
  Iterator i;
  double percent;

  public ISOMLayout ( CyNetworkView view ) {
    super( view );
    maxEpoch = 2000;
    System.out.println( "Initialize_Local" );
    parent = ( CyNetwork )network;
    partions = GraphPartition.partition( parent );
    i = partions.iterator();
    lengthOfTask = partions.size() * maxEpoch;
  }

   public  Object construct () {

    double last_x = 0;
    double last_y = 0;
    double sum_x = 0;
    double sum_y = 0;

    double _x = 0;
    double _y = 0;
    double incr = 100;
    boolean ones = false;
    
    double small_x = Double.MAX_VALUE;

    double node_count = ( double )parent.getNodeCount();
    node_count = Math.sqrt( node_count );
    // now we know how many nodes on a side
    // give each node 100 room
    node_count *= 100;
    
   
    
    this.currentProgress++;
    percent = (this.currentProgress * 100 )/this.lengthOfTask;
    this.statMessage = "Completed " + percent + "%";
    

    while ( i.hasNext() ) {
      



      int[] nodes = ( int[] )i.next();
      if ( nodes.length == 0 ) {
        continue;
      }
      network = parent.createGraphPerspective( nodes, parent.getConnectingEdgeIndicesArray( nodes ) );
      
      if ( nodes.length != 1 ) {
        prepare();
        initialize();
        do_it();
        move( sum_x, sum_y );
        sum_x += currentSize.getWidth();

      } else {
        if ( !ones ) {
          System.out.println( "Got to singletons" );
          ones = true;
          incr = 20;
          _x = 0;
          _y = sum_y + last_y;
          if ( small_x == Double.MAX_VALUE )
            small_x = 0;
        }
        if ( _x > node_count ) {
          _y += incr;
          _x = 0;;
        } else {
          _x += incr;
        } 
        
        System.out.println( "Move to x: "+_x+" y: "+_y );
        

        setSingle( _x, _y );

      }

      
    
     

      if ( currentSize.getHeight() > last_y ) {
        //System.out.println( "new y is: "+currentSize.getHeight() );
        last_y = currentSize.getHeight();

      }
     

      if ( sum_x > node_count ) {
        sum_x = 0;
        sum_y+= last_y;
        last_y = 0;
      }

      
    }


    Iterator nodes = networkView.getNodeViewsIterator();
    while ( nodes.hasNext() ) {
      ( ( NodeView )nodes.next() ).setNodePosition( true );
    }
    done = true;

    return null;



  }
 



  public void prepare () {
    nodeIndexToDataMap = new OpenIntObjectHashMap( PrimeFinder.nextPrime( network.getNodeCount() ) );
    queue = new Vector();
		trace = false;
  }

  public void do_it () {
    
    
    

     //System.out.println( statMessage );

    while ( epoch < maxEpoch ) {
      
      advancePositions();
      //System.out.println( getStatus() );
    
      //System.out.println( statMessage );

    }

   
  }
 
 //   public  void go ( boolean wait ) {
//      final SwingWorker worker = new SwingWorker(){
//         public Object construct(){
//           return ISOMLayout.this.construct();
//         }
//       };
//     worker.start();
//     // wait for the task to be done
//     //System.out.println("SimilarityCalculator.go() : Thread " + Thread.currentThread() + " about to join");
//     //System.out.flush();
//     if(wait){
//       worker.get();
//     }
//   }//go()

 

 

  //implements MonitorableSwingWorker
  public String getName () {
    return "ISOM Layout";
  } //getName()

	
  
 

 
  	/**
	 * Returns the current number of epochs and execution status, as a string.
	 */
	public String getStatus() {
		return status;
	}

  protected void initialize_local() {
		done = false;

	
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
      nodeIndexToDataMap.put( v.getRootGraphIndex(), vd );
    }
		vd.visited = false;
	}

  /**
	* Advances the current positions of the graph elements.
	*/
	public void advancePositions() {
		status = "epoch: " + epoch + "; ";
    this.currentProgress++;
    percent = (this.currentProgress * 100 )/this.lengthOfTask;
    this.statMessage = "Completed " + percent + "%";
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

    //Get closest vertex to random position
    NodeView winner = getNodeView( globalX, globalY );
    
		for (Iterator iter = network.nodesIterator();
			iter.hasNext();
			) {
			NodeView v = networkView.getNodeView( ( Node ) iter.next() );
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
			
      int current_index = current.getRootGraphIndex();
      
      double current_x = networkView.getNodeDoubleProperty( current_index, GraphView.NODE_X_POSITION );
      double current_y = networkView.getNodeDoubleProperty( current_index, GraphView.NODE_Y_POSITION );
      
			double dx = globalX - current_x;
			double dy = globalY - current_y;
      
      // possible mod
			double factor = adaption / Math.pow(2, currData.distance);

      networkView.setNodeDoubleProperty( current_index, GraphView.NODE_X_POSITION, current_x + factor * dx );
      networkView.setNodeDoubleProperty( current_index, GraphView.NODE_Y_POSITION, current_y + factor * dy );

			if (currData.distance < radius) {
				int[] neighbors = network.neighborsArray( current_index );
       //  for ( int neighbor_index = 0; neighbor_index < neighbors.length; ++neighbor_index )
//                 System.out.print( " "+neighbors[ neighbor_index ] );
              

        for ( int neighbor_index = 0; neighbor_index < neighbors.length; ++neighbor_index ) {
          
          NodeView child = networkView.getNodeView( network.getRootGraphNodeIndex( neighbors[ neighbor_index ] ) );
          
          // System.out.println(   network.getRootGraphNodeIndex( neighbors[ neighbor_index ]) +"getting for: "+child.getRootGraphIndex() );
          // System.out.println( "Network contains: "+network.getNode(  network.getRootGraphNodeIndex( neighbors[ neighbor_index ] ) ) );
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
    return ( ISOMVertexData )nodeIndexToDataMap.get( v.getRootGraphIndex() );
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
}
