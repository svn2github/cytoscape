//

/** Copyright (c) 2002 Institute for Systems Biology and the Whitehead Institute
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** any later version.
 ** 
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and the
 ** Institute of Systems Biology and the Whitehead Institute 
 ** have no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall the
 ** Institute of Systems Biology and the Whitehead Institute 
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if the
 ** Institute of Systems Biology and the Whitehead Institute 
 ** have been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 ** 
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/

// DegreeGrouper.java
//
// algorithm to pick out groups from a LayoutGraph
//
// dramage : 2002.2.19
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


public class DegreeGrouper extends GroupingAlgorithm {
    // connectedness, number of connections, node distances
    int[][] iConnected;
    int[] iDegree;
    float[][] iDm;


    // useGraph(LayoutGraph aGraph)
    //
    // initialize memory structures for graph
    public void useGraph(LayoutGraph aGraph) {
	// allow over-ridden function to run
	super.useGraph(aGraph);
	
	// save connectedness table
	iConnected = gaConnected(iGraph);

	// save degree table
	iDegree = gaDegree(iGraph, iConnected);

	// save distance matrix
	iDm = gaDistanceMatrix(iGraph, iConnected);
    }



    // getNodeGrouping
    //
    // compress iGraph down to *groups* subgroups
    // and put the results in iGraph in a reversible
    // fashion (returning graph to calling proc)
    public Subgraph getNodeGrouping(int aGroupCount) {
	Node[] nodeList = iGraph.getNodeArray();
	int nC = iGraph.nodeCount();

	// return the full jobby if all requested
	if (aGroupCount >= nC)
	    return new Subgraph(iGraph);

        // array of indeces to sort by degree
	IndexedDegree[] sortedO = new IndexedDegree[nC];
	for (int i = 0; i < nC; i++)
	    sortedO[i] = new IndexedDegree(i, iDegree[i]);

	// sort em
	Arrays.sort(sortedO, (Comparator)new ByDegree());

	// un-object them
	int[] sorted = new int[nC];
	for (int i = 0; i < nC; i++)
	    sorted[i] = sortedO[i].index();

	Subgraph subgraph = new Subgraph(iGraph);

	// join all nodes to their closest group
	for (int i = aGroupCount; i < nC; i++) {

	    // find closest group
	    int min = 0;
	    for (int j = 1; j < aGroupCount; j++)
		if (iDm[sorted[i]][sorted[j]] < iDm[sorted[i]][sorted[min]])
		    min = j;

	    // join dem nodes!  tell both us and tell subgraph
	    joinNodes(nodeList[sorted[min]], nodeList[sorted[i]]);
	    subgraph.mergeNodes(subgraph.mapFullSubNode(nodeList[sorted[min]]),
			      subgraph.mapFullSubNode(nodeList[sorted[i]]));
	}

	return subgraph;

	/*
	// create the new subgraph to be returned
	Subgraph subgraph = new Subgraph(iGraph);


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
	*/
    }





    // getClusterByNode
    //
    // return a GraphMap corresponding to the
    // nodes from iGraph that have been grouped
    // to aNode in iGraph
    public Subgraph getClusterByNode(Node groupNode, Subgraph group) {
	NodeList nodes = (NodeList)iChildMap.get(group.mapSubFullNode(groupNode));
	if (nodes == null) {
	    System.out.println("\n\n\nNULL!\n\n");
	    nodes = new NodeList();
	}

	Subgraph cluster = new Subgraph(iGraph, nodes.nodes());

	return cluster;


	/*

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
	    Node parent = (Node)iParentMap.get(node);

	    if (parent == null)
		parent = node;
	    
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
		    if ((Node)iParentMap.get(nodeList[i]) != parent) {
			Node bob = (Node)iParentMap.get(nodeList[i]);
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
		    * Math.sqrt(((NodeList)iChildMap.get(parent)).size()+1);
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

	*/
    }










    

    //////
    //
    // private classes needed for sorting by degree
    //
    //////

    // class IndexedDegree
    //
    // a class to hold both index and degree, for ease
    // of sorting
    //
    private class IndexedDegree {
	int iIndex, iDegree;

	public IndexedDegree(int index, int degree) {
	    iIndex = index;
	    iDegree = degree;
	}

	public int index() {
	    return iIndex;
	}

	public int degree() {
	    return iDegree;
	}
    }

    // class ByDegree
    //
    // a sorting Comparator to compare IndexedDegree objects
    //
    private class ByDegree implements Comparator {
	public int compare(Object a, Object b) {
	    int degreeA = ((IndexedDegree)a).degree();
	    int degreeB = ((IndexedDegree)b).degree();

	    if (degreeA > degreeB) return -1;
	    else if (degreeA == degreeB) return 0;
	    else return 1;
	}
    }
}


