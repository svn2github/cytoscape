//
// GraphWiseNodeGrouper.java
//
// algorithm to pick out groups from a LayoutGraph
//
// dramage : 2002.1.9
//


package cytoscape.layout;

import java.io.*;
import java.util.*;

import y.base.*;
import y.layout.*;

import y.geom.*;
import y.util.*;

import javax.swing.JOptionPane;
import javax.swing.JFileChooser;


public class GroupWiseNodeGrouper extends GroupingAlgorithm {
    // vectors linking each sub-graph
    // node with its children & vice-versa
    HashMap iCNHash; // child nodes hashed by Node
    HashMap iPNHash; // parent nodes hashed by Node

    // full cluster map (list of nodes
    // to pair to squish the graph)
    int[][] iClusterMap;

    // table with whether or not two given
    // nodes in iGraph are connected.
    int[][] iConnected;


    // version string for Cytoscape Map file
    // TEMP!! insert real version number
    static final String CSM_VERSION = "#CSM:0.6";


    // useGraph(LayoutGraph aGraph)
    //
    // initialize memory structures for graph
    public void useGraph(LayoutGraph aGraph) {
	// do super-processing
	super.useGraph(aGraph);

	// save connectedness
	iConnected = gaConnected(iGraph);
    }





    // getNodeGrouping
    //
    // compress iGraph down to *groups* subgroups
    // and put the results in iGraph in a reversible
    // fashion (returning graph to calling proc)
    public Subgraph getNodeGrouping(int aGroupCount) {
	// run the clustering algo, to populate
	// iClusterMap array
	if (!getClusterMap())
	    return null;

	// create the new subgraph to be returned
	Subgraph subgraph = new Subgraph(iGraph);

	Node[] nodeList = subgraph.getNodeArray();
	int nC = subgraph.nodeCount();


	// initialize vectors for child node lists
	NodeList[] iCN; // child nodes
	iCN = new NodeList[nC];
	for (int i = 0; i < nC; i++)
	    iCN[i] = new NodeList();


	// vector to map the shrinking node list
	// to offsets in the real nodeList
	Vector nodeMap = new Vector();
	for (int i = 0; i < nC; i++)
	    nodeMap.addElement(new Integer(i));

	// copy of iConnected (so we can update it
	// as we go through the edge reconstruction)
	int[][] connected = new int[nC][nC];
	for (int i = 0; i < nC; i++)
	    for (int j = 0; j < nC; j++)
		connected[i][j] = iConnected[i][j];

	System.out.println("Determining clusters...");

	int top = nC - aGroupCount;
	for (int i = 0; i < top; i++) {
	    int pct = Math.round((((float) i) / ((float) top))*100);
	    System.out.print("   \r  "+pct+"%");
	    
	    // offset of consuming node and eaten node into
	    // nodeList (converted from offsets in shrinking list
	    // via nodeMap)
	    int pacman = ((Integer)
			  nodeMap.elementAt(iClusterMap[i][0])).intValue();
	    int food = ((Integer)
			nodeMap.elementAt(iClusterMap[i][1])).intValue();

	    // remove food from node mapping (keep shrinking list
	    // in sync with iClusterMap)
	    nodeMap.removeElementAt(iClusterMap[i][1]);

	    // reconstruct edges of consumed nodes: all nodes
	    // pointing to/from food now point to/from pacman
	    // the fast way: iterate over nodes
	    for (int j = 0; j < nC; j++) {
		// ignore pacman and food
		if ((j != pacman) && (j != food)) {
		    // see if connected to food
		    switch (connected[food][j]) {
		    case CONNECTED_BI:
			switch (connected[pacman][j]) {
			case CONNECTED_TO:
			    subgraph.createEdge(nodeList[j], nodeList[pacman]);
			    break;

			case CONNECTED_FROM:
			    subgraph.createEdge(nodeList[pacman], nodeList[j]);
			    break;

			case NOT_CONNECTED:
			    subgraph.createEdge(nodeList[j], nodeList[pacman]);
			    subgraph.createEdge(nodeList[pacman], nodeList[j]);
			    break;
			}

			connected[pacman][j] = connected[j][pacman]
			    = CONNECTED_BI;
			break;

		    case CONNECTED_TO:
			switch (connected[pacman][j]) {
			case CONNECTED_FROM:
			    subgraph.createEdge(nodeList[pacman], nodeList[j]);
			    connected[pacman][j] = connected[j][pacman]
				= CONNECTED_BI;
			    break;

			case NOT_CONNECTED:
			    subgraph.createEdge(nodeList[pacman], nodeList[j]);
			    connected[pacman][j] = CONNECTED_TO;
			    connected[j][pacman] = CONNECTED_FROM;
			    break;
			} break;

		    case CONNECTED_FROM:
			switch (connected[pacman][j]) {
			case CONNECTED_TO:
			    subgraph.createEdge(nodeList[j], nodeList[pacman]);
			    connected[pacman][j] = connected[j][pacman]
				= CONNECTED_BI;
			    break;

			case NOT_CONNECTED:
			    subgraph.createEdge(nodeList[j], nodeList[pacman]);
			    connected[pacman][j] = CONNECTED_FROM;
			    connected[j][pacman] = CONNECTED_TO;
			    break;
			} break;
		    }

		    // remove food from connectedness table
		    // (so we don't trip up on future runs)
		    connected[food][j] = connected[j][food]
			= NOT_CONNECTED;
		}
	    }

	    
	    // add consumed node and all its child nodes to pacman's
	    // child node array
	    iCN[pacman].addLast(nodeList[food]);
	    for (int j = 0; j < iCN[food].size(); j++)
		iCN[pacman].addLast(iCN[food].elementAt(j));
	}
	System.out.println("\r  Done.");


	// 1) set sizes of nodes in subgraph to be proportional
	//    number of child nodes
	// 2) remove gobbled nodes
	for (int i = 0; i < nodeMap.size(); i++) {
	    int c = ((Integer)nodeMap.elementAt(i)).intValue();
	    
	    double size = subgraph.getWidth(nodeList[c])
		* Math.sqrt(iCN[c].size()+1) * 6.0;

	    subgraph.setSize(nodeList[c], size, size);

	    //int size = iCN[c].size() + 1;
	    //size = (size > 1000 ? 1000 : size);

	    //YDimension nodeSize = subgraph.getSize(nodeList[c]);
	    // System.out.println(nodeSize.getWidth() + ":"+ size);
	    //subgraph.setSize(nodeList[c],
	    //	     nodeSize.getWidth()*size,
	    //	     nodeSize.getHeight()*size);
	    
	    for (int j = 0; j < iCN[c].size(); j++)
		subgraph.removeNode((Node)iCN[c].elementAt(j));
	}



	// create hash mapping of nodes to children nodes.
	// this mapping differs from iCN because:
	//  a) it is indexed by parent Node rather
	//     than by parent index
	//  b) it points to nodes in iGraph (as opposed
	//     to nodes in subgraph)
	iCNHash = new HashMap();
	iPNHash = new HashMap();

	Node[] fullList = iGraph.getNodeArray();

	for (int i = 0; i < nC; i++) {
	    // new list to fill
	    NodeList nList = new NodeList();

	    for (int j = 0; j < iCN[i].size(); j++)
		nList.addLast(subgraph.mapSubFullNode(
						(Node)iCN[i].elementAt(j)));

	    iCNHash.put(fullList[i], nList);

	    // make each node its own parent, initially
	    iPNHash.put(fullList[i], fullList[i]);
	}


	// create a hash mapping of nodes to their parent node

	Iterator parentIter = (iCNHash.entrySet()).iterator();
	while (parentIter.hasNext()) {
	    Map.Entry parent = (Map.Entry)parentIter.next();

	    NodeList childList = (NodeList) parent.getValue();
	    for (int i = 0; i < childList.size(); i++)
		iPNHash.put(childList.elementAt(i), parent.getKey());
	}

	// remove parents that are themselves children
	boolean changed = true;
	while (changed) {
	    changed = false;
	    
	    Iterator childIter = (iPNHash.entrySet()).iterator();
	    while (childIter.hasNext()) {
		Map.Entry bob = (Map.Entry)childIter.next();

		// if parent has parent, make parent's parent our parent
		if (iPNHash.get(bob.getValue()) != bob.getValue()) {
		    iPNHash.put(bob.getKey(), iPNHash.get(bob.getValue()));
		    changed = true;
		}
	    }
	}

	// all done!  return the subgraph
	return subgraph;
    }


    // putNodeGrouping
    //
    // reinsert the node grouping into the mail graph
    //public void putNodeGrouping(Subgraph group) {
    //group.reInsert();
    //}


    // getClusterByNode
    //
    // return a GraphMap corresponding to the
    // nodes from iGraph that have been grouped
    // to aNode in iGraph
    public Subgraph getClusterByNode(Node groupNode, Subgraph group) {
	NodeList nodes = (NodeList)iCNHash.get(group.mapSubFullNode(groupNode));
	nodes.addLast(group.mapSubFullNode(groupNode));
	Subgraph cluster = new Subgraph(iGraph, nodes.nodes());

	// data provider of sluggishness for each node
	NodeMap slug = cluster.createNodeMap();

	Node[] nodeList = iGraph.getNodeArray();
	int nC = iGraph.nodeCount();

	// set initial placement of nodes inside box AND
	// initialize data provider - everyone starts with 1.0
	// factor of slowing for WeightedLayouter
	for (NodeCursor nc = cluster.nodes(); nc.ok(); nc.next()) {
	    // node in full graph, parent in full graph
	    Node node = cluster.mapSubFullNode(nc.node());
	    Node parent = (Node)iPNHash.get(node);
	    //System.out.println(" clustered " + node.toString() + " with " + parent.toString());
	    
	    // find node offset in full graph
	    int offset;
	    for (offset = 0; offset < nC; offset++)
		if (nodeList[offset] == node)
		    break;

	    // vector sum of position relative to other clusters
	    double vx = 0, vy = 0;

	    // iterate over nodes in parent
	    for (int i = 0; i < nC; i++) {
		if (iConnected[offset][i] != NOT_CONNECTED)
		    if ((Node)iPNHash.get(nodeList[i]) != parent) {
			Node bob = (Node)iPNHash.get(nodeList[i]);
			double dx = iGraph.getCenterX(bob)
			    - iGraph.getCenterX(parent);
			double dy = iGraph.getCenterY(bob)
			    - iGraph.getCenterY(parent);

			double h = Math.sqrt(dx*dx + dy*dy);
			vx += dx/h;
			vy += dy/h;
		    }
	    }



	    if (vx != 0.0 || vy != 0.0) {
		double size = iGraph.getWidth(parent) * 3.0
		    * Math.sqrt(((NodeList)iCNHash.get(parent)).size()+1);
		//double size = 1000;

		cluster.setCenter(nc.node(),
				  vx * size,
				  vy * size);
		slug.setDouble(nc.node(), .25);
	    } else {
		cluster.setCenter(nc.node(),
				  10*(Math.random()-.5),
				  10*(Math.random()-.5));
		slug.setDouble(nc.node(), 1.0);
	    }
	}

	// edge nodes - these should move at 1/4 speed
	cluster.addDataProvider("Cytoscape:slug", slug);
	return cluster;
    }






    ////
    //
    // support methods
    //
    ////


    // getClusterMap
    //
    // generates the cluster map from the graph.
    // returns true if successful
    private boolean getClusterMap() {
	// step 0: pop up dialog asking to load
	// file or calculate from scratch

	boolean askMode = true;

	while (askMode) {

	    Object[] options = { "Load From File", "Generate", "Cancel" };
	    switch ((int)JOptionPane.showOptionDialog(null,
				       "Generation can take several minutes",
						  "Cluster map required",
						  JOptionPane.DEFAULT_OPTION,
						  JOptionPane.WARNING_MESSAGE,
						  null, options, options[0])) {

	    case 0:
		// clustermap: Load From File
		// keep going if file not loaded
		askMode = (!cmLoad());
		break;

	    case 1:
		// clustermap: Generate
		return cmGenerate();
		
	    case 2:
		// clustermap: Cancel
	    default:
		return false;
	    }
	}

	// default: nothing loaded
	return true;
    }



    // cmLoad
    //
    // load cluster map from file
    private boolean cmLoad() {
	JFileChooser fChooser = new JFileChooser();	
	fChooser.setDialogTitle("Load cluster map");

	switch (fChooser.showOpenDialog(null)) {
		
	case JFileChooser.APPROVE_OPTION:
	    File file = fChooser.getSelectedFile();

	    try {
		FileReader fin = new FileReader(file);
		BufferedReader bin = new BufferedReader(fin);

		// file firstline should contain version info
		// as well as node count.  verify these
		StringTokenizer st = new StringTokenizer(bin.readLine());
		if (!st.nextToken().equals(CSM_VERSION)) {
		    JOptionPane.showMessageDialog(null,
			  "File incompatible with this version of Cytoscape",
			         "Error Reading \"" + file.getName()+"\"",
					       JOptionPane.ERROR_MESSAGE);
		    return false;
		}

		if (Integer.parseInt(st.nextToken()) != iGraph.nodeCount()) {
		    JOptionPane.showMessageDialog(null, "Wrong number of " +
			  "nodes found, expected " + iGraph.nodeCount(),
			         "Error Reading \"" + file.getName()+"\"",
					       JOptionPane.ERROR_MESSAGE);
		    return false;
		}


		// all good, so read the file
		iClusterMap = new int[iGraph.nodeCount()-1][2];
		
		String s;
		int i = 0;
		while ((s = bin.readLine()) != null) {
		    st = new StringTokenizer(s);
		    iClusterMap[i][0] = Integer.parseInt(st.nextToken());
		    iClusterMap[i][1] = Integer.parseInt(st.nextToken());
		    i++;
		}

		fin.close();
		
	    } catch (Exception e) {
		JOptionPane.showMessageDialog(null, e.toString(),
			         "Error Reading \"" + file.getName()+"\"",
					       JOptionPane.ERROR_MESSAGE);
		return false;
	    }

	    return true;

	default:
	    // cancel or error
	    return false;
	}
    }


    // cmGenerate
    //
    // generate cluster map from the graph
    private boolean cmGenerate() {
	// step 1: calculate distance matrix
	float[][] dm = getFastDistanceMatrix();

	// step 2: condense distance matrix
	iClusterMap = new int[iGraph.nodeCount()-1][2];
	condenseDistanceMatrix(dm);

	// DEBUG: print out array
	// System.out.println(CSM_VERSION + " " + iGraph.nodeCount());
	// for (int i = 0; i < iGraph.nodeCount()-1; i++)
	//    System.out.println(iClusterMap[i][0] + " " + iClusterMap[i][1]);

	// step 3: save cluster map to file
	boolean trySave = true;
	
	while (trySave) {
	    
	    // choose file
	    JFileChooser fChooser = new JFileChooser();
	    fChooser.setDialogTitle("Save cluster map to file");

	    switch (fChooser.showSaveDialog(null)) {
		
	    case JFileChooser.APPROVE_OPTION:
		File file = fChooser.getSelectedFile();
		    
		try {
		    boolean writeFile = true;
		    
		    if (file.exists()) {
			int write = JOptionPane.showConfirmDialog(null,
					  "Overwrite " + file.getName() + "?",
					  "File already exists",
					  JOptionPane.YES_NO_OPTION);
			writeFile = (write == 0);
		    }
		    
		    if (writeFile) {
			FileWriter fout = new FileWriter(file);
			
			fout.write(CSM_VERSION+" "+iGraph.nodeCount()+"\n");
			
			for (int i = 0; i < iGraph.nodeCount()-1; i++)
			    fout.write(iClusterMap[i][0] + " "
				       + iClusterMap[i][1]+"\n");
			
			fout.close();
			
			// done!
			trySave = false;
		    } else
			// keep trying to save
			trySave = true;
		    
		} catch (IOException e) {
		    JOptionPane.showMessageDialog(null, e.toString(),
			         "Error Writing to \"" + file.getName()+"\"",
					       JOptionPane.ERROR_MESSAGE);
		    trySave = true;
		}
		break;
		
		
	    case JFileChooser.CANCEL_OPTION:
	    case JFileChooser.ERROR_OPTION:
		int r = JOptionPane.showConfirmDialog(null,
				  "Cluster map may need to be regenerated",
							  "Confirm Cancel",
					         JOptionPane.YES_NO_OPTION);
		
		trySave = (r == 1);
		break;
	    }
	}


	return true;
    }

    
    // getDistanceMatrix
    //
    // fill up dm
    private float[][] getDistanceMatrix() {
	// get node list and allocate space for
	// the distance matrix
        Node[] nodeList = iGraph.getNodeArray();
	int nC = iGraph.nodeCount();
	float[][] dm = new float[nC][nC];

	System.out.println("Distance matrix: ("+nC+" nodes)");

	// initialize 1st order defaults (from iConnected)
	for (int i = 0; i < nC; i++)
	    for (int j = 0; j < nC; j++)
		dm[i][j] = (float)(iConnected[i][j] != NOT_CONNECTED ? 1:0);

	System.out.println("  filling distance matrix...");

	boolean matrixChanged = true;

	for (int c = 0; (c < nC) && (matrixChanged); c++) {
	    // mark the matrix unchanged
	    matrixChanged = false;

	    // iterate over starting nodes
	    for (int sn = 0; sn < nC; sn++) {
		int pct = Math.round((((float) sn) / ((float) nC))*100);
		System.out.print("\r    pass "+c+"/2(?): "+pct+"%   ");

		// iterate over mid-point nodes
		for (int mn = 0; mn < nC; mn++) {

		    // iterate over target nodes
		    if (dm[sn][mn] != 0.0) {
			for (int tn = 0; tn < nC; tn++) {

			    // take the new distance from source
			    // to target, if conditions holding
			    if (dm[mn][tn] != 0.0) {
				float newDist = dm[sn][mn] + dm[mn][tn];

				if ((dm[sn][tn] == 0) ||
				    (newDist < dm[sn][tn])) {
				    dm[sn][tn] = newDist;
				    dm[tn][sn] = newDist;
				    matrixChanged = true;
				}
			    }
			}
		    }
		}
	    }

	    System.out.println();
	}


	// DEBUG: print out matrix
	// for (int i = 0; i < nC; i++) {
	//     System.out.println(nodeList[i].toString());
	//     System.out.print(dm[i][0]);
	//     for (int j = 1; j < nC; j++)
	// 	System.out.print("\t"+dm[i][j]);
	//     System.out.print("\n");

	// return distance map
	return dm;
    }







    /**
     * @return a symmetrical matrix of distances the given nodes.  The
     * diagonal of the returned matrix will be 0s, and all cells i,j such that
     * nodes with "index"es i and j are adjacent will have value 1.
     */
    private float[][] getFastDistanceMatrix() {

	// this algorithm is a modification of the Dijkstra algorithm.
	// The speedups over Dijkstra based on integer distances, and
	// the initial code, were both created by Paul Edlefsen.
	// The conversion to code that would work in this framework was
	// done by Owen Ozier.  The framework was build by Dan Ramage,
	// and the ideas were generated by all of the above, as well as
	// Trey Ideker and Rowan Christmas.

	// get node list and allocate space for
	// the distance matrix
	int nC = iGraph.nodeCount();
	float[][] dm = new float[nC][nC];
	LinkedList[] lla = new LinkedList[nC];
	for(int i=0; i<nC; i++)
	    lla[i] = new LinkedList();
	//System.err.println( "Fast Alg" );

	// We don't have to make new Integers all the time, so we store the index
	// Objects in this array for reuse.
	Integer[] integers = new Integer[ nC ];
	for(int i=0; i<nC; i++)
	    integers[i] = new Integer(i);
	    
	for(int from_node_int=0; from_node_int< nC; from_node_int++) {
	    //System.err.println( from_node_int );
	    lla[from_node_int].clear();
	    for(int to_node_int=0; to_node_int< nC; to_node_int++) {
		if (iConnected[from_node_int][to_node_int] != NOT_CONNECTED) {
		    lla[from_node_int].add(integers[to_node_int]);
		    //System.out.println("found edge " + from_node_int + " " + to_node_int);
		}
	    }
	}

	// TODO: REMOVE
	System.err.println( "Calculating all node distances.." );
	
	
	// Fill the nodes array with the nodes in their proper index locations.
	int index;
	LinkedList from_node_ll;
	
	LinkedList queue = new LinkedList();
	boolean[] completed_nodes = new boolean[ nC ];
	Iterator neighbors;
	LinkedList to_node_ll;
	int neighbor_index;
	int to_node_distance;
	int neighbor_distance;
	for( int from_node_index = 0;
	     from_node_index < nC;
	     from_node_index++ ) {
	    from_node_ll = lla[from_node_index];
	    if( from_node_ll.getFirst() == null ) {
		// Make the distances in this row all Integer.MAX_VALUE.
		Arrays.fill( dm[ from_node_index ], nC+2 );
		continue;
	    }
	    
	    /*
	      // TODO: REMOVE
	      System.err.print( "Calculating node distances from graph node " +
	      from_node );
	      System.err.flush();
	    */
	    
	    // Make the distances row and initialize it.
	    Arrays.fill( dm[ from_node_index ], nC+2 );
	    dm[ from_node_index ][ from_node_index ] = 0;
	    
	    // Reset the completed nodes array.
	    Arrays.fill( completed_nodes, false );
	    
	    // Add the start node to the queue.
	    queue.add( integers[from_node_index] );
	    
	    while( !( queue.isEmpty() ) ) {
		
		index = ( (Integer)queue.removeFirst()).intValue();
		if( completed_nodes[ index ] ) {
		    continue;
		}
		completed_nodes[ index ] = true;
		
		to_node_ll = lla[index];
		to_node_distance = (int)dm[ from_node_index ][ index ];
		
		if( index < from_node_index ) {
		    // Oh boy.  We've already got every distance from/to this node.
		    int distance_through_to_node;
		    for( int i = 0; i < nC; i++ ) {
			if( (int)dm[ index ][ i ] == nC+2 ) {
			    continue;
			}
			distance_through_to_node =
			    to_node_distance + (int)dm[ index ][ i ];
			if( distance_through_to_node <=
			    (int)dm[ from_node_index ][ i ] ) {
			    // Any immediate neighbor of a node that's already been
			    // calculated for that does not already have a shorter path
			    // calculated from from_node never will, and is thus complete.
			    if( (int)dm[ index ][ i ] == 1 ) { 
				completed_nodes[ i ] = true;
			    }
			    dm[ from_node_index ][ i ] =
				(float)distance_through_to_node;
			}
		    } // End for every node, update the distance using the distance from
		    // to_node.
		    // So now we don't need to put any neighbors on the queue or
		    // anything, since they've already been taken care of by the previous
		    // calculation.
		    continue;
		} // End if to_node has already had all of its distances calculated.
		
		neighbors = to_node_ll.listIterator(0);
		
		while( neighbors.hasNext() ) {
		    neighbor_index = ((Integer)neighbors.next()).intValue();
		    
		    if( completed_nodes[ neighbor_index ] ) {
			// We've already done everything we can here.
			continue;
		    }
		    
		    neighbor_distance = (int)dm[ from_node_index ][ neighbor_index ];
		    
		    if( ( to_node_distance != nC+2 ) &&
			( neighbor_distance > ( to_node_distance + 1 ) ) ) {
			dm[ from_node_index ][ neighbor_index ] =
			    (float)( to_node_distance + 1 );
			queue.addLast( integers[neighbor_index] );
		    }
		    
		    // TODO: REMOVE
		    /*
		      System.err.print( "." );
		      System.err.flush();
		    */
		    
		} // For each of the next nodes' neighbors
		// TODO: REMOVE
		/*
		  System.err.print( "|" );
		  System.err.flush();
		*/
	    } // For each to_node, in order of their (present) distances
	    
	    // TODO: REMOVE
	    /*
	      System.err.println( "done." );
	    */
	    
	} // For each from_node
	
	// TODO: REMOVE
	System.err.println( "..Done calculating all node distances." );
	
	return dm; // owoUP
    } // calculateAllNodeDistances(..)






    // condenseCondenseMatrix
    //
    // grouping phase 2: matrix condensation
    private void condenseDistanceMatrix(float[][] dm) {
	// fill out defaults for weighted avg
	int[] weight = new int[iGraph.nodeCount()];
	for (int i = 0; i < iGraph.nodeCount(); i++)
	    weight[i] = 1;

	//int[] label = new int[iGraph.nodeCount()];
	//for (int i = 0; i < iGraph.nodeCount(); i++)
	//    label[i] = i;

	
	for (int nC = iGraph.nodeCount(); nC > 1; nC--) {
	    System.out.println("Condensed Matrix: ("+(nC-1)+" nodes)");

	    // step 1: find the min value of dm[i][j]
	    int iMin = 0, jMin = 1;

	    // iterate over nodes skipping diagonal
	    for (int i = 0; i < nC; i++) {
		for (int j = i+1; j < nC; j++) {
		    if (dm[i][j] < dm[iMin][jMin]) {
			iMin = i;
			jMin = j;
		    }
		}
	    }


	    // step 2: combine the nodes
	    System.out.println("  combining nodes "+iMin+" and "+jMin
			       +" (score: "+dm[iMin][jMin]+")");
	    iClusterMap[iGraph.nodeCount()-nC][0] = iMin;
	    iClusterMap[iGraph.nodeCount()-nC][1] = jMin;

	    float[][] dmNew = new float[nC-1][nC-1];
	    int[] weightNew = new int[nC-1];
	    
	    for (int i = 0; i < nC; i++) {
		int pct = Math.round((((float) i) / ((float) nC))*100);
		System.out.print("   \r  "+pct+"%");
		
		if((i>=jMin)&&(i<nC-1)) {
		    weightNew[i] = weight[i+1];
		    //label[i] = label[i+1];
		    //System.out.println(" update "+i+" " + label[i]);
		}

		// ignore row j
		if (i != jMin) {
		    // reindex if past jth row
		    int iNew = (i > jMin ? i-1 : i);
		    
		    // update weightNew table
		    weightNew[iNew] = weight[i];
		    if (i == iMin)
			weightNew[iNew] += weight[jMin];

		    for (int j = i+1; j < nC; j++) {
			// ignore column j
			if (j != jMin) {
			    // reindex if past jth column
			    int jNew = (j > jMin ? j-1 : j);
			    
			    // recalc column/row i
			    if ((i == iMin) && (j != iMin)) {
				if (false) {
				// unweighted average:
				    dmNew[iNew][jNew] = (dm[j][iMin]
				                    + dm[j][jMin])/2;
				} else if (true) {
			        // weighted average:
				    dmNew[iNew][jNew]
					= (weight[iMin]*dm[j][iMin] +
					   weight[jMin]*dm[j][jMin]) /
					  (weight[iMin]+weight[jMin]);
				}
			    } else
				dmNew[iNew][jNew] = dm[i][j];

			    // remember symmetry
			    dmNew[jNew][iNew] = dmNew[iNew][jNew];
			}
		    }
		}
	    }

	    System.out.print("\r");

	    dm = dmNew;
	    weight = weightNew;

	    // explicitly run garbage collection
	    // NOTE!! slows the function down to a crawl
	    // Runtime.getRuntime().gc();
	}
    }
}
