/*
 * This	is based on the	ISOMLayout from	the JUNG project.
 */

package	csplugins.layout.Jung;

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
import cytoscape.layout.*;
import giny.view.*;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class JungISOMLayout extends AbstractLayout {  
	private	int maxEpoch;
	private	int epoch;

	private	int radiusConstantTime;
	private	int radius;
	private	int minRadius;

	private	double adaption;
	private	double initialAdaption;
	private	double minAdaption;

	private	double factor;
	private	double coolingFactor;
	private double distanceFactor;

	private	boolean	trace;
	private	boolean	done;

	private	Vector queue;
	private	String status =	null;

	private	OpenIntObjectHashMap nodeIndexToDataMap;

	private	double globalX,	globalY;
	private	double currentNetworkViewSizeX,	currentNetworkViewSizeY;

	public JungISOMLayout (	CyNetworkView view ) {
		super( view );
		nodeIndexToDataMap = new OpenIntObjectHashMap( PrimeFinder.nextPrime( view.getNodeViewCount() )	);
		queue =	new Vector();
		trace =	false;
	}

	public void doLayout ()	{
		initialize( );
		
		currentProgress	= 0;
		done = false;
		canceled = false;
		statMessage = "Completed 0%";
		CytoscapeProgressMonitor monitor = new CytoscapeProgressMonitor( this, Cytoscape.getDesktop() );
		monitor.startMonitor( true );
		
		System.out.println( "Done with monitor in ISOM"	);
	}

	/**
	 * Initializer,	calls <tt>intialize_local</tt> and
	 * <tt>initializeLocations</tt>	to start construction
	 * process.
	 */
	public void initialize () {
		double node_count = ( double )network.getNodeCount();
		node_count = Math.sqrt(	node_count );
		// now we know how many	nodes on a side
		// give	each node 100 room
		node_count *= 100;
		currentSize = new Dimension( (int)node_count, (int)node_count );

		initialize_local();
		initializeLocations();
	}

	/**
	 * initialize all the local variables
	 */
	protected void initialize_local() {
		String dialog;
		done = false;

		radiusConstantTime = 100;
		radius = floor( Math.sqrt( ( double )network.getNodeCount() ) );
		minRadius = 1;
		maxEpoch = (radius - minRadius + 10) * radiusConstantTime;
		epoch =	1;
		lengthOfTask = maxEpoch;

		initialAdaption	= 90.0D	/ 100.0D;
		adaption = initialAdaption;
		minAdaption = 0;

		coolingFactor =	20;
		distanceFactor = 1;
	}

	/**
	 * This	method calls <tt>initialize_local_vertex</tt> for
	 * each	vertex,	and also adds initial coordinate information
	 * for each vertex. (The vertex's initial location is
	 * set by calling <tt>initializeLocation</tt>.
	 */
	protected void initializeLocations() {
		int count = 0;
		for (Iterator iter = network.nodesIterator(); iter.hasNext();) {
			NodeView v = networkView.getNodeView( (	Node ) iter.next() );
			if ( !staticNodes.contains( v )	)
			initializeLocation( v, currentSize);
			initialize_local_node_view(v);
			//System.out.println( (count++)+"init: "+v.getNode().getIdentifier() );
		}
	}

	/**
	 * Sets	random locations for a vertex within the dimensions of the space.
	 * If you want to initialize in some different way, override this method.
	 *
	 * @param v
	 * @param d
	 */
	protected void initializeLocation ( NodeView v, Dimension d )	{
		double x = Math.random() * d.getWidth();
		double y = Math.random() * d.getHeight();
		v.setXPosition( x, false );
		v.setYPosition( y, false );
	}

	/**
	 * Initializes the local information on a single vertex.
	 * The user is responsible for overriding this method
	 * to do any vertex-level construction that may be
	 * necessary: for example, to attach vertex-level
	 * information to each vertex.
	 */
	protected void initialize_local_node_view( NodeView v) {
		ISOMVertexData vd = getISOMVertexData(v);
		if (vd == null)	{
			vd = new ISOMVertexData();
			nodeIndexToDataMap.put(	v.getGraphPerspectiveIndex(), vd );
		}
		vd.visited = false;
	}

	public ISOMVertexData getISOMVertexData	(  NodeView v )	{
		return ( ISOMVertexData	)nodeIndexToDataMap.get( v.getGraphPerspectiveIndex() );
	}

///////////////////////////////////////////////////////////////////////////////

	public Object construct	() {
		System.out.println( "ISOM Being	Constructed" );
		
		int[] nodeIndicesArray = network.getNodeIndicesArray();
		int[] BelongedGroupArray = new int[nodeIndicesArray.length];
		int[] ElementsNumberInGroupsArray = new int[nodeIndicesArray.length];
		
		for (int loop = 0; loop<nodeIndicesArray.length; loop++) {
			BelongedGroupArray[loop] = -1;
			ElementsNumberInGroupsArray[loop] = 0;
		}
		
		int groupID = 0;
		queue.removeAllElements();
		for (int nodeIndex = 0;nodeIndex<nodeIndicesArray.length;nodeIndex++) {
			if (BelongedGroupArray[nodeIndex] == -1) {
				BelongedGroupArray[nodeIndex] = groupID;
				ElementsNumberInGroupsArray[groupID]++;
				queue.add(nodeIndicesArray[nodeIndex]);
				int current_index;
				
				while ( !queue.isEmpty() ) {
					current_index = ( int ) queue.remove(0);
					
					int[] neighbors = networkView.getGraphPerspective().neighborsArray( current_index );
					for ( int neighbor_index = 0; neighbor_index < neighbors.length; ++neighbor_index ) {
						int child = neighbors[ neighbor_index ];
						if (BelongedGroupArray[child] == -1)	{
							BelongedGroupArray[child] = groupID;
							ElementsNumberInGroupsArray[groupID]++;
							queue.addElement(child);
						}
					}
				}
				groupID++;
			}
		}
		
		int largeVSSmallCriterion = 4; // if NodeNumber exceeds this number, it's large group.
		
		double largeGPX = 0, largeGPY = 0,
		       smallGPX = 0, smallGPY = 0;
		double largeGPXInterval = 10, largeGPSpacePerNode = 100,
		       smallGPXInterval = 10, smallGPSpacePerNode = 10,
		       largeSmallInterval = 30;
		       
		for (int groupIndex = 0; groupIndex < groupID; groupIndex++) {
			if (ElementsNumberInGroupsArray[groupIndex] > largeVSSmallCriterion) {
				for (int loop1 = 0; loop1 < nodeIndicesArray.length; loop1++) {
					if (BelongedGroupArray[loop1] == groupIndex) {
						NodeView movingNode = networkView.getNodeView(nodeIndices[loop1]);
						movingNode.setNodeDoubleProperty(nodeIndicdes[loop1], NODE_X_POSITION, largeGPX + Math.sqrt(ElementsNumberInGroupsArray[groupIndex])*largeGPSpacePerNode*Math.random();
						movingNode.setNodeDoubleProperty(nodeIndicdes[loop1], NODE_Y_POSITION, largeGPY + Math.sqrt(ElementsNumberInGroupsArray[groupIndex])*largeGPSpacePerNode*Math.random();
					}
				}
				largeGPX += largeGPSpacePerNode*Math.sqrt(ElementsNumberInGroupsArray[groupIndex]);
			} else {
				for (int loop1 = 0; loop1 < nodeIndicesArray.length; loop1++) {
					if (BelongedGroupArray[loop1] == groupIndex) {
						networkView.setNodeDoubleProperty(nodeIndicdes[loop1], NODE_X_POSITION, smallGPX + Math.sqrt(ElementsNumberInGroupsArray[groupIndex])*smallGPSpacePerNode*Math.random();
						networkView.setNodeDoubleProperty(nodeIndicdes[loop1], NODE_Y_POSITION, smallGPY + Math.sqrt(ElementsNumberInGroupsArray[groupIndex])*smallGPSpacePerNode*Math.random();
					}
				}
				smallGPX += smallGPSpacePerNode*Math.sqrt(ElementsNumberInGroupsArray[groupIndex]);
			}
		}
		
		double percent;
		this.currentProgress++;
		percent	= (this.currentProgress	* 100 )/this.lengthOfTask;
		this.statMessage = "Completed "	+ percent + "%";

		currentNetworkViewSizeX	= getCurrentSize().getWidth();
		currentNetworkViewSizeY	= getCurrentSize().getHeight();
		randomizeVerticesCoordinates();
		
/*		while (	epoch <= maxEpoch ) {
			advancePositions();
			this.currentProgress++;
			percent	= (this.currentProgress	* 100 )/this.lengthOfTask;
			this.statMessage = "Completed "	+ percent + "%";
		}*/
		
		Iterator nodes = networkView.getNodeViewsIterator();
		
		while (	nodes.hasNext()	) {
			( ( NodeView )nodes.next() ).setNodePosition( true );
		}
		done = true;
		return null;
	}

	/*
	* Advances the current positions of the	graph elements.
	*/
	public void advancePositions() {
		status = "epoch: " + epoch + ";	";
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

	public Dimension getCurrentSize() {
		return currentSize;
	}

	private	synchronized void adjust() {
		//Generate random position in graph space
		ISOMVertexData tempISOM	= new ISOMVertexData();
		
		// creates a new XY data location
		globalX	= Math.random()	* currentNetworkViewSizeX;
		globalY	= Math.random()	* currentNetworkViewSizeY;

		//Get closest vertex to	random position
		NodeView winner	= getNodeView( globalX,	globalY	);
		
		for (Iterator iter = networkView.getNodeViewsIterator();iter.hasNext();) {
			NodeView v = ( NodeView	) iter.next();
			ISOMVertexData ivd = getISOMVertexData(v);
			ivd.distance = 0;
			ivd.visited = false;
		}
		adjustVertex(winner);
	}

	public static class ISOMVertexData {
		public DoubleMatrix1D disp;

		int distance;
		boolean	visited;

		public ISOMVertexData()	{
			initialize();
		}

		public void initialize() {
			disp = new DenseDoubleMatrix1D(2);

			distance = 0;
			visited	= false;
		}

		public double getXDisp() {
			return disp.get(0);
		}

		public double getYDisp() {
			return disp.get(1);
		}

		public void setDisp(double x, double y)	{
			disp.set(0, x);
			disp.set(1, y);
		}

		public void incrementDisp(double x, double y) {
			disp.set(0, disp.get(0)	+ x);
			disp.set(1, disp.get(1)	+ y);
		}

		public void decrementDisp(double x, double y) {
			disp.set(0, disp.get(0)	- x);
			disp.set(1, disp.get(1)	- y);
		}
	}

	private	synchronized void adjustVertex(	NodeView v ) {
		queue.removeAllElements();
		ISOMVertexData ivd = getISOMVertexData(v);
		ivd.distance = 0;
		ivd.visited = true;
		queue.add(v);
		NodeView current;

		while (	!queue.isEmpty() ) {
			current	= ( NodeView ) queue.remove(0);
			ISOMVertexData currData	= getISOMVertexData(current);
			
			int current_index = current.getGraphPerspectiveIndex();
			double current_x = networkView.getNodeDoubleProperty( current_index, GraphView.NODE_X_POSITION );
			double current_y = networkView.getNodeDoubleProperty( current_index, GraphView.NODE_Y_POSITION );
			
			double dx = globalX - current_x;
			double dy = globalY - current_y;
			double factor =	adaption / Math.pow(2, distanceFactor * currData.distance);

			networkView.setNodeDoubleProperty( current_index, GraphView.NODE_X_POSITION, current_x + factor	* dx );
			networkView.setNodeDoubleProperty( current_index, GraphView.NODE_Y_POSITION, current_y + factor	* dy );

			if (currData.distance <	radius)	{
				int[] neighbors	= networkView.getGraphPerspective().neighborsArray( current_index );
				for ( int neighbor_index = 0; neighbor_index < neighbors.length; ++neighbor_index ) {
					NodeView child = networkView.getNodeView( neighbors[ neighbor_index ] );
					ISOMVertexData childData = getISOMVertexData(child);
					if (!childData.visited)	{
						childData.visited = true;
						
						childData.distance = currData.distance + 1;
						queue.addElement(child);
					}
				}
			}
		}
	}
	
	private	synchronized void updateParameters() {
		epoch++;
		double factor =	Math.exp(-1 * coolingFactor * (1.0 * epoch / maxEpoch));
		adaption = Math.max(minAdaption, factor	* initialAdaption);
		if ((radius > minRadius) && (epoch % radiusConstantTime	== 0)) {
			radius--;
		}
	}
	
	public ISOMVertexData getISOMVertexData	(  int v ) {
		return ( ISOMVertexData	)nodeIndexToDataMap.get( v );
	}						 




	/*
	 * This	one is an incremental visualization.
	 * @return <code>true</code> is	the layout algorithm is	incremental, <code>false</code>	otherwise
	 */
	public boolean isIncremental() {
		return true;
	}

	/*
	 * For now, we pretend it never	finishes.
	 * @return <code>true</code> is	the increments are done, <code>false</code> otherwise
	 */
	public boolean incrementsAreDone() {
		return false;
	}

/*******************************************************************************
	Not Important methods
*******************************************************************************/

 /**
	 * Gets called when the user clicks on the Cancel button of the progress monitor.
	 */
	public void cancel (){
	// Cancel the task, set	data structures	to null
		this.canceled =	true;
	}//cancel

	
/*******************************************************************************
	Necessary methods
*******************************************************************************/
	public	void go	( boolean wait ) {
		final SwingWorker worker = new SwingWorker(){
			public Object construct(){
				return JungISOMLayout.this.construct();
			}
		};
				worker.start();
		// wait	for the	task to	be done
		//System.out.println("SimilarityCalculator.go()	: Thread " + Thread.currentThread() + "	about to join");
		//System.out.flush();
		if(wait){
			worker.get();
				}
		}//go()

	public void stop(){
		done = true;
	}//stop()
	
/*******************************************************************************
	Unnecessary methods
*******************************************************************************/
	
	/*
	 * JungISOMLayout.incrementProgress(): Increase	the progress
	 */
	public void incrementProgress(){
		this.currentProgress++;
		double percent = (this.currentProgress * 100)/this.lengthOfTask;
		this.statMessage = "Completed "	+ percent + "%";
	}//JungISOMLayout.incrementProgress()

	/*
	 * JungISOMLayout.getMessage():	Returns	the status message.
	 */
	public String getMessage(){
		return this.statMessage;
	}//JungISOMLayout.getMessage

	/*
	 * JungISOMLayout.getTaskName(): Returns the current taskName.
	 */
	public String getTaskName (){
		return this.taskName;
	}//JungISOMLayout.getTaskName()


	/*
	 * JungISOMLayout.getStatus(): Returns the current number 
	 * of epochs and execution status, as a	string.
	 */
	public String getStatus() {
		return status;
	}//JungISOMLayout.getStatus()

	/*
	 * JungISOMLayout.wasCanceled(): Returns whether the cancel button
	 * was clicked or not.
	 */
	public boolean wasCanceled () {
		return this.canceled;
	}

	/**********************************
		Colt Implements
	**********************************/
	
	// implements MonitorableSwingWorker
	public int getCurrent () {
		return currentProgress;
	} // getCurrentProgressValue()

	// implements MonitorableSwingWorker
	public int getLengthOfTask () {
		return lengthOfTask;
	} // getTargetProgressValue()

	// implements MonitorableSwingWorker
	public String getName () {
		return "ISOM Layout";
	} // getName()

	// implements MonitorableSwingWorker
	public boolean done () {
		return done;
	} // isFinished()

}
