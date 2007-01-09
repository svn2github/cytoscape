import cern.colt.map.OpenIntObjectHashMap;
import cern.colt.map.PrimeFinder;

import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.impl.DenseDoubleMatrix1D;

import cytoscape.*;
import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.actions.*;
import cytoscape.actions.GinyUtils;
import cytoscape.data.CyNetworkUtilities;
import cytoscape.data.readers.*;
import cytoscape.layout.*;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.util.*;
import cytoscape.util.CytoscapeAction;
import cytoscape.util.SwingWorker;
import cytoscape.view.*;
import cytoscape.visual.*;

import giny.filter.Filter;

import giny.model.*;
import giny.model.Edge;
import giny.model.GraphPerspective;
import giny.model.GraphPerspective;
import giny.model.GraphPerspectiveChangeEvent;
import giny.model.GraphPerspectiveChangeListener;
import giny.model.Node;

import giny.util.SpringEmbeddedLayouter;

import giny.view.*;

import phoebe.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.event.ActionEvent;

import java.io.*;

import java.util.*;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import javax.swing.*;
import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JOptionPane;


public class HypFocus extends CytoscapePlugin {

    public static int focuscount = 0;

    public static double[] array;

    /**
     * Creates a new HypFocus object.
     */
    public HypFocus() {
        Cytoscape.getDesktop().getCyMenus().getOperationsMenu().add(new MyAction());
    }

    public String describe() {
        return new String("Test plugin");
    }

    class LocalISOMLayout3 extends LocalAbstractLayout {
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
        private double globalX;
        private double globalY;

        public LocalISOMLayout3(CyNetworkView view) {
	    super(view);
            nodeIndexToDataMap = new OpenIntObjectHashMap(PrimeFinder.nextPrime(
                        view.getNodeViewCount()));
            queue = new Vector();
            trace = false;
        }

        public Object construct() {
            System.out.println("ISOM Being Constructed");

            double percent;
            this.currentProgress++;
            percent = (this.currentProgress * 100) / this.lengthOfTask;
            this.statMessage = "Completed " + percent + "%";

            //System.out.println( statMessage );
            while (epoch < maxEpoch) {
                advancePositions();
                //System.out.println( getStatus() );
                this.currentProgress++;
                percent = (this.currentProgress * 100) / this.lengthOfTask;
                this.statMessage = "Completed " + percent + "%";

                //System.out.println( statMessage );
            }

            Iterator nodes = networkView.getNodeViewsIterator();

            while (nodes.hasNext()) {
                ((NodeView) nodes.next()).setNodePosition(true);
            }

            done = true;

            return null;
        }

        public void go(boolean wait) {
            final SwingWorker worker = new SwingWorker() {
                    public Object construct() {
                        return LocalISOMLayout3.this.construct();
                    }
                };

            worker.start();

            // wait for the task to be done
            if (wait) {
                worker.get();
            }
        } //go()

        public void stop() {
            done = true;
        } //stop()

        public void incrementProgress() {
            this.currentProgress++;

            double percent = (this.currentProgress * 100) / this.lengthOfTask;
            this.statMessage = "Completed " + percent + "%";
        } //incrementProgress

        // implements MonitorableSwingWorker
        public int getCurrent() {
            return currentProgress;
        } // getCurrentProgressValue()

        // implements MonitorableSwingWorker
        public int getLengthOfTask() {
            return lengthOfTask;
        } // getTargetProgressValue()

        public String getMessage() {
            return this.statMessage;
        } //getMessage

        public String getTaskName() {
            return this.taskName;
        } //getTaskName

        // implements MonitorableSwingWorker
        public String getName() {
            return "ISOM Layout";
        } // getName()

        // implements MonitorableSwingWorker
        public boolean done() {
            return done;
        } // isFinished()

        /**
         * Gets called when the user clicks on the Cancel button of the progress monitor.
         */
        public void cancel() {
            // Cancel the task, set data structures to null
            this.canceled = true;
        } //cancel

        public boolean wasCanceled() {
            return this.canceled;
        }

        public void doLayout() {
            initialize();

            currentProgress = 0;
            done = false;
            canceled = false;
            statMessage = "Completed 0%";

            CytoscapeProgressMonitor monitor = new CytoscapeProgressMonitor(this,
                    Cytoscape.getDesktop());
            monitor.startMonitor(true);

            System.out.println("Done with monitor in ISOM2");
        }

        /**
         * Returns the current number of epochs and execution status, as a string.
         */
        public String getStatus() {
            return status;
        }

        protected void initialize_local() {
            done = false;

            maxEpoch = 20;
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

        protected void initialize_local_node_view(NodeView v) {
            ISOMVertexData vd = getISOMVertexData(v);

            if (vd == null) {
                vd = new ISOMVertexData();
                nodeIndexToDataMap.put(-v.getGraphPerspectiveIndex(), vd);
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
                status += ("adaption: " + adaption + "; ");
                status += "status: done";
                done = true;
            }
        }

        private synchronized void adjust() {
            //Generate random position in graph space
            ISOMVertexData tempISOM = new ISOMVertexData();

            // creates a new XY data location
            globalX = 10 + (Math.random() * getCurrentSize().getWidth());
            globalY = 10 + (Math.random() * getCurrentSize().getHeight());

            //Get closest vertex to random position
            NodeView winner = getNodeView(globalX, globalY);

            for (Iterator iter = networkView.getNodeViewsIterator(); iter.hasNext();) {
                NodeView v = (NodeView) iter.next();
                ISOMVertexData ivd = getISOMVertexData(v);
                ivd.distance = 0;
                ivd.visited = false;
            }

            adjustVertex(winner);
        }

        private synchronized void updateParameters() {
            epoch++;

            double factor = Math.exp(-1 * coolingFactor * ((1.0 * epoch) / maxEpoch));
            adaption = Math.max(minAdaption, factor * initialAdaption);

            if ((radius > minRadius) && ((epoch % radiusConstantTime) == 0)) {
                radius--;
            }
        }

        private synchronized void adjustVertex(NodeView v) {
            queue.removeAllElements();
        }

        public ISOMVertexData getISOMVertexData(NodeView v) {
            return (ISOMVertexData) nodeIndexToDataMap.get(-v.getGraphPerspectiveIndex());
        }

        public ISOMVertexData getISOMVertexData(int v) {
            return (ISOMVertexData) nodeIndexToDataMap.get(v);
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

        public class ISOMVertexData {
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
	abstract private class LocalAbstractLayout 
	  implements LayoutAlgorithm {

	  protected Set staticNodes;
	  protected CyNetworkView networkView;
	  protected GraphPerspective network;
	  protected Dimension currentSize = new Dimension( 20, 20 );

	  // monitoring
	  protected int lengthOfTask;
	  protected int currentProgress;
	  protected boolean done, canceled;
	  protected String statMessage; 
	  protected String taskName;

	  /**
	   * The Constructor is null 
	   */
	  public LocalAbstractLayout ( CyNetworkView networkView ) {
	    this.networkView = networkView;
	    this.staticNodes = new HashSet();
	    this.network = networkView.getNetwork();
	  }

	 
	  public abstract Object construct ();


	  //------------------------------//
	  // Implements MonitoredTask
	  //------------------------------//

	 
	    
	    
	  public String getMessage(){
	    return this.statMessage;
	  }//getMessage

	  public String getTaskName (){
	    return this.taskName;
	  }//getTaskName


	  public  String getName () {
	    return "replace";
	  }

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

	  /**
	   * Initializes currentProgress (generally to zero) and then spawns a SwingWorker
	   * to start doing the work.
	   * @param wait whether or not the method should wait for the task to be done before returning
	   *             if true, should call SwingWorker.get() before returning
	   */
	  public void go ( boolean wait ) {
	    final SwingWorker worker = new SwingWorker(){
		public Object construct(){
		  return LocalAbstractLayout.this.construct();
		}
	      };
	    worker.start();
	    // wait for the task to be done
	    if(wait){
	      worker.get();
	    }
	  }
		
	  public void doLayout () {
	   
	    
	   
	    currentProgress = 0;
	    done = false;
	    canceled = false;
	    statMessage = "Completed 0%";
	    //System.out.println( "In Abstract Layout -start" );
	    CytoscapeProgressMonitor monitor = new CytoscapeProgressMonitor( this, Cytoscape.getDesktop() );
	    monitor.startMonitor( true );

	    networkView.fitContent();
	    done = true;

	    //System.out.println( "In Abstract Layout -ddone" );

	  
	  }




	  //------------------------------//
	  // Implements Layout
	  //------------------------------//


	  /**
	   * @return if the given NodeView is static
	   */
	  public boolean dontMove ( NodeView nv ) {
	    return staticNodes.contains( nv );
	  }

	  /**
		 * Initializer, calls <tt>intialize_local</tt> and
		 * <tt>initializeLocations</tt> to start construction
		 * process.
		 */
	  public void initialize () {


	   
	    double node_count = ( double )network.getNodeCount();
	    node_count = Math.sqrt( node_count );
	    // now we know how many nodes on a side
	    // give each node 100 room
	    node_count *= 100;
	    currentSize = new Dimension( (int)node_count, (int)node_count );

	    initialize_local();
	    initializeLocations();
	    
	  }

	  /**
		 * Initializes all local information, and is called immediately
		 * within the <tt>initialize()</tt> process.
		 * The user is responsible for overriding this method
		 * to do any construction that may be necessary:
		 * for example, to initialize local per-edge or
		 * graph-wide data.
		 */
	  protected  void initialize_local() {}

		/**
		 * Initializes the local information on a single vertex.
		 * The user is responsible for overriding this method
		 * to do any vertex-level construction that may be
		 * necessary: for example, to attach vertex-level
		 * information to each vertex.
		 */
	  protected  void initialize_local_node_view ( NodeView nv) {}

	  /**
		 * This method calls <tt>initialize_local_vertex</tt> for
		 * each vertex, and also adds initial coordinate information
		 * for each vertex. (The vertex's initial location is
		 * set by calling <tt>initializeLocation</tt>.
		 */
	  protected void initializeLocations() {

	    int count = 0;
	    for (Iterator iter = network.nodesIterator(); iter.hasNext();) {
	      NodeView v = networkView.getNodeView( ( Node ) iter.next() );
	      if ( !staticNodes.contains( v ) )
		initializeLocation( v, currentSize);
	      initialize_local_node_view(v);
	      //System.out.println( (count++)+"init: "+v.getNode().getIdentifier() );
	    }
	  }
	  
	  /**
	   * Sets random locations for a vertex within the dimensions of the space.
	   * If you want to initialize in some different way, override this method.
	   *
	   * @param	v
	   * @param d
	   */
	  protected void initializeLocation ( NodeView v, Dimension d ) {
	    }
	  
	  /**
		 * {@inheritDoc}
		 * By default, an <tt>LocalAbstractLayout</tt> returns
		 * null for its status.
		 */
	  public String getStatus() {
	    return null;
	  }

	  /**
		 * Implementors must override this method in order
		 * to create a Layout. If the Layout is
		 * the sort that only calculates locations once,
		 * this method may be overridden with an empty
		 * method.<p>
		 * Note that "locked" vertices are not to be moved;
		 * however, it is the policy of the visualization
		 * to decide how to handle them, and what to do with
		 * the vertices around them. Prototypical code might
		 * include a clipping like
		 * <pre>
		 * 		for (Iterator i = getVertices().iterator(); i.hasNext() ) {
		 * 			Vertex v = (Vertex) i.next();
		 * 			if (! dontmove.contains( v ) ) {
		 * 				... // handle the node
		 * 			} else { // ignore the node
		 * 			}
		 * 		}
		 * </pre>
		 * @see Layout#advancePositions()
		 */
	  public  void advancePositions() {}
	  
	  /**
		 * Returns the current size of the visualization
		 * space, accoring to the last call to resize().
		 * @return the current size of the screen
		 */
	  public Dimension getCurrentSize() {
	    return currentSize;
	  }


	  public void move ( double x, double y ) {

	    Iterator nodeIter = network.nodesIterator();
	    while ( nodeIter.hasNext() ) {
	      int nodeIndex = ((CyNode)nodeIter.next()).getRootGraphIndex();
	      networkView.setNodeDoubleProperty( nodeIndex, GraphView.NODE_X_POSITION, x +  networkView.getNodeDoubleProperty( nodeIndex, GraphView.NODE_X_POSITION ) );
	      networkView.setNodeDoubleProperty( nodeIndex, GraphView.NODE_Y_POSITION, y +  networkView.getNodeDoubleProperty( nodeIndex, GraphView.NODE_Y_POSITION ) );
										    
	    }

	  }

	  public void setSingle ( double x, double y ) {
	    Iterator nodeIter = network.nodesIterator();
	    while ( nodeIter.hasNext() ) {
	      int nodeIndex = ((CyNode)nodeIter.next()).getRootGraphIndex();
	      networkView.setNodeDoubleProperty( nodeIndex, GraphView.NODE_X_POSITION, x );
	      networkView.setNodeDoubleProperty( nodeIndex, GraphView.NODE_Y_POSITION, y );
	    }
	  }

	  /**
		 * When a visualizetion is resized, it presumably
		 * wants to fix the locations of the vertices
		 * and possibly to reinitialize its data. The
		 * current method calls <tt>initializeLocations</tt>
		 * followed by <tt>initialize_local</tt>.
		 * TODO: A better
		 * implementation wouldn't destroy the current
		 * information, but would either scale the
		 * current visualization, or move the nodes toward
		 * the new center.
		 */
	  public void resize ( Dimension size ) {
	    // are we initialized yet?
	    
	    Dimension oldSize;
	    synchronized (currentSize) {
	      if (currentSize.equals( size )) return;
	      oldSize = currentSize;
	      this.currentSize = size;
	    }
	    
	    int xOffset = (size.width - oldSize.width ) / 2;
			int yOffset = (size.height - oldSize.height) / 2;
	    
	    // now, move each vertex to be at the new screen center
	    for (Iterator iter = networkView.getNodeViewsIterator(); iter.hasNext();) {
				NodeView e = ( NodeView ) iter.next();
				e.setOffset( e.getXPosition() + xOffset, e.getYPosition() + yOffset );
	    }
	    // optionally, we may want to restart
	  }

	  /**
		 * Restarts the visualization entirely, as if the the
		 * user had pressed the "scramble" button. Calls
		 * <tt>initializeLocation</tt> for each vertex.
		 * TODO: Is this enough? Should it call the whole
		 * initialization process? Why does resize do more?
		 */
	  public void restart() {
			initializeLocations();
			initialize_local();
	  }

	  /**
	   * @return the closest NodeView to these coords.
	   */
	  public NodeView getNodeView ( double x, double y ) {
	    double minDistance = Double.MAX_VALUE;
	    NodeView closest = null;
	    for ( Iterator iter = network.nodesIterator();
		  iter.hasNext(); ) {
	      NodeView v = networkView.getNodeView( (Node)iter.next() );
	      double dx = networkView.getNodeDoubleProperty( v.getRootGraphIndex(), GraphView.NODE_X_POSITION ) - x;
	      double dy = networkView.getNodeDoubleProperty( v.getRootGraphIndex(), GraphView.NODE_Y_POSITION ) - y;
	      double dist = dx * dx + dy * dy;
	      if ( dist < minDistance ) {
		minDistance = dist;
		closest = v;
	      }
	    }
	    return closest;
	  }



	  /**
		 * Forcibly moves a vertex to the (x,y) location by
		 * setting its x and y locations to the inputted
		 * location. Does not add the vertex to the "dontmove"
		 * list, and (in the default implementation) does not
		 * make any adjustments to the rest of the graph.
		 */
	  public void forceMove ( NodeView picked, double x, double y) {
	    picked.setOffset( x, y );
	  }

	  public void lockNodes ( NodeView[] nodes ) {
	    for ( int i = 0; i < nodes.length; ++i ) {
	      staticNodes.add( nodes[i] );
	    }

	  }

	  public void lockNode ( NodeView v ) {
	    staticNodes.add(v);
	  }
	  
	  public void unlockNode( NodeView v ) {
	    staticNodes.remove(v);
	  }


	}

    class MyAction extends AbstractAction {
        public MyAction() {
            super("HypFocus");
        }

        public void actionPerformed(ActionEvent ae) {
            System.out.println("Action Performed");

            //get the network object; this contains the graph
            CyNetwork network = Cytoscape.getCurrentNetwork();

            //get the network view object
            final CyNetworkView view = Cytoscape.getCurrentNetworkView();

            //can't continue if either of these is null
            if ((network == null) || (view == null)) {
                return;
            }

            double mid2x = 0;
            double mid2y = 0;

            array = new double[ 2 * network.getNodeCount() ];

            // init array
            for (Iterator i = view.getNodeViewsIterator(); i.hasNext();) {
                NodeView nview = (NodeView) i.next();
                int current_index = -nview.getGraphPerspectiveIndex();

                array[(2 * current_index) - 2] = nview.getXPosition();
                array[(2 * current_index) - 1] = nview.getYPosition();
            } 

            if (view.getSelectedNodes().size() >0) {
                for (Iterator i = view.getSelectedNodes().iterator(); i.hasNext();) {
                    NodeView nView = (NodeView) i.next();
                    int current_index = -nView.getGraphPerspectiveIndex();
                    mid2x = array[(2 * current_index) - 2];
                    mid2y = array[(2 * current_index) - 1];
                } 
            } 

            final SwingWorker worker = new SwingWorker() {
                    public Object construct() {
                        LayoutAlgorithm layout = new LocalISOMLayout3(view);
                        view.applyLayout(layout);

                        return null;
                    }
                }; // SwingWorker

            worker.start();

            for (Iterator i = view.getNodeViewsIterator(); i.hasNext();) {
                NodeView nview = (NodeView) i.next();
                int current_index = -nview.getGraphPerspectiveIndex();

                double squeeze = 1;
                double radius = 1000;

                double relx = array[(2 * current_index) - 2] - mid2x;
                double rely = array[(2 * current_index) - 1] - mid2y;

                double euc_dist = Math.sqrt((relx * relx) + (rely * rely));

                double ratio = radius / Math.sqrt((euc_dist * euc_dist) + (radius * radius));

                relx *= ratio;
                rely *= ratio;

                double current_x = (relx * squeeze);
                double current_y = (rely * squeeze);
		
		nview.setXPosition(current_x);
		nview.setYPosition(current_y);
            } //iterator

            view.fitContent();

            HypFocus.focuscount++;
            System.out.println("focuscount: " + HypFocus.focuscount);
        }
    }
}



