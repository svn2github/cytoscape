/*
 * This is based on the ISOMLayout from the JUNG project.
 */

package cytoscape.layout;

import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.impl.DenseDoubleMatrix1D;
import cern.colt.map.OpenIntObjectHashMap;
import cern.colt.map.PrimeFinder;

import cytoscape.*;
import cytoscape.view.*;
import cytoscape.util.*;
import giny.view.*;
import javax.swing.JFrame;

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



  public ISOMLayout ( CyNetworkView view ) {
    super( view );
    nodeIndexToDataMap = new OpenIntObjectHashMap( PrimeFinder.nextPrime( view.getNodeViewCount() ) );
    queue = new Vector();
		trace = false;
  }


  public Object construct () {
    
    System.out.println( "ISOM Being Constructed" );

   
    double percent;
     this.currentProgress++;
     percent = (this.currentProgress * 100 )/this.lengthOfTask;
     this.statMessage = "Completed " + percent + "%";

     //System.out.println( statMessage );

    while ( epoch < maxEpoch ) {
      
      advancePositions();
      //System.out.println( getStatus() );
      this.currentProgress++;
      percent = (this.currentProgress * 100 )/this.lengthOfTask;
      this.statMessage = "Completed " + percent + "%";
      //System.out.println( statMessage );

    }

    Iterator nodes = networkView.getNodeViewsIterator();
    while ( nodes.hasNext() ) {
      ( ( NodeView )nodes.next() ).setNodePosition( true );
    }
    done = true;
    return null;
  }
 
   public  void go ( boolean wait ) {
    final SwingWorker worker = new SwingWorker(){
        public Object construct(){
          return ISOMLayout.this.construct();
        }
      };
    worker.start();
    // wait for the task to be done
    //System.out.println("SimilarityCalculator.go() : Thread " + Thread.currentThread() + " about to join");
    //System.out.flush();
    if(wait){
      worker.get();
    }
  }//go()

  public void stop(){
    done = true;
  }//stop()


  public void incrementProgress(){
    this.currentProgress++;
    double percent = (this.currentProgress * 100)/this.lengthOfTask;
    this.statMessage = "Completed " + percent + "%";
  }//incrementProgress

  // implements MonitorableSwingWorker
  public int getCurrent () {
    return currentProgress;
  } // getCurrentProgressValue()

		// implements MonitorableSwingWorker
  public int getLengthOfTask () {
    return lengthOfTask;
  } // getTargetProgressValue()

  public String getMessage(){
    return this.statMessage;
  }//getMessage

  public String getTaskName (){
    return this.taskName;
  }//getTaskName

  // implements MonitorableSwingWorker
  public String getName () {
    return "ISOM Layout";
  } // getName()

		// implements MonitorableSwingWorker
  public boolean done () {
    return done;
  } // isFinished()
  
  /**
   * Gets called when the user clicks on the Cancel button of the progress monitor.
   */
  public void cancel (){
    // Cancel the task, set data structures to null
    this.canceled = true;
  }//cancel

  public boolean wasCanceled () {
    return this.canceled;
  }


  public void doLayout () {
    initialize( );
    
   
    currentProgress = 0;
    done = false;
    canceled = false;
    statMessage = "Completed 0%";

    CytoscapeProgressMonitor monitor = new CytoscapeProgressMonitor( this, Cytoscape.getDesktop() );
    monitor.startMonitor( true );

    System.out.println( "Done with monitor in ISOM" );

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

    lengthOfTask = maxEpoch;

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

    //Get closest vertex to random position
    NodeView winner = getNodeView( globalX, globalY );
    
		for (Iterator iter = networkView.getNodeViewsIterator();
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
      double current_x = networkView.getNodeDoubleProperty( current_index, GraphView.NODE_X_POSITION );
      double current_y = networkView.getNodeDoubleProperty( current_index, GraphView.NODE_Y_POSITION );
      
			double dx = globalX - current_x;
			double dy = globalY - current_y;
			double factor = adaption / Math.pow(2, currData.distance);

      networkView.setNodeDoubleProperty( current_index, GraphView.NODE_X_POSITION, current_x + factor * dx );
      networkView.setNodeDoubleProperty( current_index, GraphView.NODE_Y_POSITION, current_y + factor * dy );

			if (currData.distance < radius) {
				int[] neighbors = networkView.getGraphPerspective().neighborsArray( current_index );
        for ( int neighbor_index = 0; neighbor_index < neighbors.length; ++neighbor_index ) {
          NodeView child = networkView.getNodeView( neighbors[ neighbor_index ] );
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
