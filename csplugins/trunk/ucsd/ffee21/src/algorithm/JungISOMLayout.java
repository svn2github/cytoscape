/*
 * This	is based on the	ISOMLayout from	the JUNG project.
 */

package	csplugins.layout.Jung;

import java.util.*;
import java.lang.Integer;

import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.impl.DenseDoubleMatrix1D;
import cern.colt.map.OpenIntObjectHashMap;
import cern.colt.map.PrimeFinder;

import cytoscape.*;
import cytoscape.view.*;
import cytoscape.util.*;
import cytoscape.layout.*;

import giny.view.*;
import giny.view.GraphView;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import java.awt.Dimension;

public class JungISOMLayout extends AbstractLayout {  
	private	int maxEpoch;
	private	int epoch;
	private int timeFactor;

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

	
	private int largeVSSmallCriterion; // if NodeNumber exceeds this number, it's large group.
	
	private double scaleFactor;
	private double largeGPX, largeGPY,
	       	       smallGPX, smallGPY;
	private double largeGPXInterval, largeGPSpacePerNode,
	               smallGPXInterval, smallGPSpace,
	               largeSmallInterval;
	
        private int[] nodeID;		// the array of the nodeID
	private int[] nodeGP;		// the array of the group_Number which the node belonged
	private int[] num_inGP;		// the array of the number_of_elements_in_the_group along the group_number

	private int groupID;

	private double[][] boundaries;
	
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
		
	}

	/**
	 * initialize all the local variables
	 */
	protected void initialize_local() {
		String dialog;
		done = false;

		radiusConstantTime = 140;
		radius = (int) Math.floor( Math.sqrt( ( double )network.getNodeCount() ) );
		minRadius = 1;
		epoch =	1;
		lengthOfTask = maxEpoch;

		initialAdaption	= 90.0D	/ 100.0D;
		adaption = initialAdaption;
		minAdaption = 0;

		coolingFactor =	1;
		distanceFactor = 1;

		/*
		String timeFactorS = JOptionPane.showInputDialog("timeFactor:");
		timeFactor = Integer.parseInt(timeFactorS);
		String scaleFactorS = JOptionPane.showInputDialog("scaleFactor:");
		scaleFactor = Double.parseDouble(scaleFactorS);
		*/

		timeFactor = 100;
		scaleFactor = 10;
		
		largeVSSmallCriterion = 4; // if NodeNumber exceeds this number, it's large group.
		
		largeGPXInterval = 1000;
		largeGPSpacePerNode = 40 	* scaleFactor;
	        smallGPXInterval = 1000;
		smallGPSpace = 50		* scaleFactor;
	        largeSmallInterval = 150;

		largeGPX = 0;
		largeGPY = (smallGPSpace+largeSmallInterval);
	       	smallGPX = 0;
		smallGPY = 0;

		nodeID = network.getNodeIndicesArray();
		nodeGP = new int[nodeID.length];
		num_inGP = new int[nodeID.length];

		for (int loop = 0; loop<nodeID.length; loop++) {
			int[] tempnei = networkView.getGraphPerspective().neighborsArray( nodeID[loop]);
			NodeView temp = networkView.getNodeView(nodeID[loop]);
			for (int j=0;j<tempnei.length;j++) {
				NodeView temp2 = networkView.getNodeView(tempnei[j]);
			}
			
			nodeGP[loop] = -1;
			num_inGP[loop] = 0;
		}


		int [] stack = new int [nodeID.length+1];
		int top = 0;
		
		groupID = 0;

		for (int index = 0;index<nodeID.length;index++) {		// for all nodes: 'index' will contain the array index.( 0 ~ (nodeID.length-1) )
			if (nodeGP[index] == -1) {					// if the node is not belonged to any group
				NodeView current_nodeview = networkView.getNodeView(nodeID[index]);			// get the name of this 'free' node
				
				nodeGP[index] = groupID;				// assign the group to the 'free' node
				num_inGP[groupID]++;
				stack[top++] = nodeID[index];				// store this node to stack
				int currentID;
				
				while ( top!=0 ) {
					currentID = stack[--top];
					
					int[] neighbors = networkView.getGraphPerspective().neighborsArray( currentID );

					for ( int neighbor_index = 0; neighbor_index < neighbors.length; neighbor_index++ ) {
						int neiID = neighbors[ neighbor_index ];
						int neiIndex=-1;
						for ( int i=0;i<nodeID.length;i++) {
							if (Math.abs(network.getRootGraphNodeIndex(neiID)) == Math.abs(nodeID[i])) {
								neiIndex = i;
							}
						}
						NodeView currentNV = networkView.getNodeView(neiID);
						if (neiIndex < nodeGP.length) {
							if (nodeGP[neiIndex] == -1)	{
								nodeGP[neiIndex] = groupID;
								num_inGP[groupID]++;
								stack[top++] = neiID;
							}
						}
					}
					neighbors = null;
				}
				groupID++;
			}
			
		}
	}

	public ISOMVertexData getISOMVertexData	(  NodeView v )	{
		return ( ISOMVertexData	)nodeIndexToDataMap.get( v.getGraphPerspectiveIndex() );
	}

///////////////////////////////////////////////////////////////////////////////

	public Object construct	() {
		System.out.println( "ISOM Being	Constructed" );	
		
		boundaries = new double [groupID][4];

		for (int groupIndex = 0; groupIndex < groupID; groupIndex++) {
			if (num_inGP[groupIndex] > largeVSSmallCriterion) {
				for (int loop1 = 0; loop1 < nodeID.length; loop1++) {
					if (nodeGP[loop1] == groupIndex) {
						NodeView temp = networkView.getNodeView(nodeID[loop1]);
						
						networkView.setNodeDoubleProperty(nodeID[loop1], GraphView.NODE_X_POSITION, largeGPX +
						Math.sqrt(num_inGP[groupIndex])*largeGPSpacePerNode*(0.4+0.2*Math.random()));
						networkView.setNodeDoubleProperty(nodeID[loop1], GraphView.NODE_Y_POSITION, largeGPY +
						Math.sqrt(num_inGP[groupIndex])*largeGPSpacePerNode*(0.4+0.2*Math.random()));
						boundaries[groupIndex][0] = largeGPX;
						boundaries[groupIndex][1] = largeGPY;
						boundaries[groupIndex][2] = Math.sqrt(num_inGP[groupIndex])*largeGPSpacePerNode;
						boundaries[groupIndex][3] = Math.sqrt(num_inGP[groupIndex])*largeGPSpacePerNode;
					}
				}
				largeGPX += largeGPSpacePerNode*Math.sqrt(num_inGP[groupIndex]);
			} else {
				for (int loop1 = 0; loop1 < nodeID.length; loop1++) {
					if (nodeGP[loop1] == groupIndex) {	
						NodeView temp = networkView.getNodeView(nodeID[loop1]);
						
						networkView.setNodeDoubleProperty(nodeID[loop1], GraphView.NODE_X_POSITION, smallGPX +
						smallGPSpace*Math.random());
						networkView.setNodeDoubleProperty(nodeID[loop1], GraphView.NODE_Y_POSITION, smallGPY +
						smallGPSpace*Math.random());
						
						boundaries[groupIndex][0] = smallGPX;
						boundaries[groupIndex][1] = smallGPY;
						boundaries[groupIndex][2] = smallGPSpace;
						boundaries[groupIndex][3] = smallGPSpace;
					}
				}
				smallGPX += smallGPSpace;
			}
		}
		
		System.out.println("NodeMoving Completed");

		advancePositions();
/*
		while (	epoch <= maxEpoch ) {
			advancePositions();
			this.currentProgress++;
			percent	= (this.currentProgress	* 100 )/this.lengthOfTask;
			this.statMessage = "Completed "	+ percent + "%";
		}
*/	

		Iterator nodes = networkView.getNodeViewsIterator();
		
		while (	nodes.hasNext()	) {
			( ( NodeView )nodes.next() ).setNodePosition( true );
		}

		nodeID = null;
		nodeGP = null;
		num_inGP = null;
		done = true;
		return null;
	}

	/*
	* Advances the current positions of the	graph elements.
	*/
	public void advancePositions() {
		for (int loop1=0;loop1<groupID;loop1++) {
			System.out.println("Processing of the group "+Integer.toString(loop1) + "/" + Integer.toString(groupID));
			maxEpoch = timeFactor*(int)Math.floor(Math.pow(num_inGP[loop1],0.8));
			if (maxEpoch<400) {
				maxEpoch = 400;
				if (num_inGP[loop1]<=largeVSSmallCriterion) {
					maxEpoch = 50;
				}
			}
			radius = (int) Math.floor( Math.sqrt( ( double )network.getNodeCount() ) );
			ISOMVertexData IVDs = new ISOMVertexData();
			epoch = 1;
			radiusConstantTime = (int)Math.floor(0.7*(double)maxEpoch/(double)(radius-1));
			System.out.println("Processing of the group "+Integer.toString(loop1+1) + "/" + Integer.toString(groupID) + "   MaxEpoch: " + Integer.toString(maxEpoch));
			while (epoch < maxEpoch ) {
				if (epoch % 100 == 0) {
					System.out.print("Progress: " + Integer.toString((int)Math.floor(epoch))+"/"+Integer.toString(maxEpoch) + "   radius: " +
					Integer.toString(radius)+"  ");
					for (int i=0;i<radius;i++) {
						System.out.print(Double.toString(Math.floor(1000 * distanceFactor * adaption / Math.pow(2*(1+epoch/maxEpoch), i/1.5))/1000)+" ");
					}
					System.out.println(" ");
				}
				if (epoch < maxEpoch) {
					NodeView winner;
					do {
						// creates a new XY data location
						globalX	= boundaries[loop1][0] + Math.random()	* boundaries[loop1][2];
						globalY	= boundaries[loop1][1] + Math.random()	* boundaries[loop1][3];
				
						//Get closest vertex to	random position
						winner	= getNodeView( globalX,	globalY	);
					} while (nodeGP[searchIndex(winner.getGraphPerspectiveIndex())]!=loop1);
					
					adjustVertex(winner);
					
					updateParameters();
					status += " status: running";
				}
			}
		}
		done = true;
	}
	
	public Dimension getCurrentSize() {
		return currentSize;
	}

	public static class ISOMVertexData {
		public DoubleMatrix1D disp;

		public int distance;
		public boolean	visited;

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

	private synchronized int searchIndex(int node) {
		for (int i=0;i<nodeID.length;i++) {
			if (Math.abs(network.getRootGraphNodeIndex(node)) == Math.abs(nodeID[i])) {
				return i;
			}
		}
		return -1;
	}
	
	private	synchronized void adjustVertex(	NodeView v ) {
		int []IDq = new int[nodeID.length+1];
		int []distance = new int[nodeID.length];
		int []visited = new int[nodeID.length];
		int top = 0;
		
		for (int i=0;i<nodeID.length;i++) {
			distance[i] = 0;
			visited[i] = 0;
		}
		
		IDq[top++] = v.getGraphPerspectiveIndex();
		
		while ( top!=0 ) {
			int current_nodeID = IDq[--top];
			int cur_i = searchIndex(current_nodeID);
			
			double current_x = networkView.getNodeDoubleProperty( current_nodeID, GraphView.NODE_X_POSITION );
			double current_y = networkView.getNodeDoubleProperty( current_nodeID, GraphView.NODE_Y_POSITION );
			
			double dx, dy;
			if (distance[cur_i]>0) {
				dx = (globalX - current_x)*(1-(distance[cur_i]/radius)+((2*distance[cur_i]/radius)*Math.random()));
				dy = (globalY - current_y)*(1-(distance[cur_i]/radius)+((2*distance[cur_i]/radius)*Math.random()));
			} else {
				dx = globalX - current_x;
				dy = globalY - current_y;
			}
			double factor = adaption / Math.pow(2*(1+epoch/maxEpoch), distanceFactor * distance[cur_i]/1.5);

			
			networkView.setNodeDoubleProperty( current_nodeID, GraphView.NODE_X_POSITION, current_x + factor * dx );
			networkView.setNodeDoubleProperty( current_nodeID, GraphView.NODE_Y_POSITION, current_y + factor * dy );
			
			if (distance[cur_i] < radius) {
				int[] neighbors	= networkView.getGraphPerspective().neighborsArray( current_nodeID );
				for ( int neighbor_index = 0; neighbor_index < neighbors.length; ++neighbor_index ) {
					int nei_i = searchIndex(neighbors[ neighbor_index ] );
					if (visited[nei_i]==0) {
						visited[nei_i] = 1;

						distance[nei_i] = distance[cur_i]+1;
						IDq[top++] = neighbors[ neighbor_index ];
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
