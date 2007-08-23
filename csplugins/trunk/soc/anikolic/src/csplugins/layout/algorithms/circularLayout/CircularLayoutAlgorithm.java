package csplugins.layout.algorithms.circularLayout;

//import giny.view.EdgeView;
import giny.view.NodeView;

import java.awt.GridLayout;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
//import java.util.List;

import javax.swing.JPanel;

//import org.cytoscape.coreplugin.cpath.model.MaxHitsOption;

import csplugins.layout.algorithms.hierarchicalLayout.Edge;
import csplugins.layout.algorithms.hierarchicalLayout.Graph;
import csplugins.layout.algorithms.graphPartition.*;
import csplugins.layout.*;

//import cytoscape.editor.AddEdgeEdit;
//import cytoscape.layout.AbstractLayout;
import cytoscape.layout.LayoutProperties;
import cytoscape.layout.Tunable;
//import cytoscape.render.stateful.NodeDetails;
import cytoscape.task.TaskMonitor;


/**
 * 
 * @author Aleksandar Nikolic
 * Lays out graph in circular pattern.
 * Algorithm puts all the nodes which belong to same biconnected component 
 * (with more than 3 nodes) on circle. Nodes which doesn't belong to circle
 * are layed out in tree-like pattern.
 */
public class CircularLayoutAlgorithm extends AbstractGraphPartition
{
	private int nodeHorizontalSpacing = 64;
	private int nodeVerticalSpacing = 32;
	private int leftEdge = 32;
	private int topEdge = 32;
	private int rightMargin = 1000;
	private int componentSpacing = 64;
	
	//private boolean selected_only = false;
	private LayoutProperties layoutProperties;
	
	private int[][] bc;
	private boolean[] posSet;
	private boolean[] depthPosSet;
	private HashMap<Integer, Integer> nodeHeights;
	private LinkedList<Integer>[] edgesFrom;
	private NodeView[] nodeView;
	private HashMap<Integer, Integer> node2BiComp;
	private boolean[] drawnBiComps;
	
	
	public CircularLayoutAlgorithm() 
	{
		super();
		layoutProperties = new LayoutProperties(getName());
		initialize_properties();
	}
	
	public boolean supportsSelectedOnly() 
	{
		return false;
	}

	public String getName() 
	{
		return "circular";
	}

	public String toString() 
	{
		return "Circular Layout";
	}
	
	public void layoutPartion(LayoutPartition partition) 
	{
		if (canceled)
			return;
		
		final int numNodes = partition.nodeCount(); 
			
		if (numNodes == 1) {
			// We were asked to do a circular layout of a single node -- done!
			return;
		}
		nodeView = new NodeView[numNodes];
		int nextNode = 0;
		HashMap<Integer, Integer> ginyIndex2Index = new HashMap<Integer, Integer>(numNodes * 2);
	
		
		Iterator iter = partition.getNodeList().iterator();		/* all nodes */

		while (iter.hasNext() && !canceled) 
		{
				NodeView nv = ((LayoutNode) (iter.next())).getNodeView();
				Integer nodeIndexKey = new Integer(nv.getNode().getRootGraphIndex());

				if (!ginyIndex2Index.containsKey(nodeIndexKey)) {
					nodeView[nextNode] = nv;
					ginyIndex2Index.put(nodeIndexKey, new Integer(nextNode));
					nextNode++;
				}
		}
		
		if (canceled)
			return;

		/* create edge list from edges between selected nodes */
		LinkedList<Edge> edges = new LinkedList<Edge>();
		iter = partition.edgeIterator();

		while (iter.hasNext()) {
			LayoutEdge ev = (LayoutEdge) (iter.next());
			Integer edgeFrom = (Integer) ginyIndex2Index.get(new Integer(ev.getSource().getNodeView().getNode().getRootGraphIndex()));					
			Integer edgeTo = (Integer) ginyIndex2Index.get(new Integer(ev.getTarget().getNodeView().getNode().getRootGraphIndex()));

			if ((edgeFrom == null) || (edgeTo == null)) {
				// Must be from an unselected node
				continue;
			}

			if (canceled)
				return;
			
			/* add edge to graph */
			edges.add(new Edge(edgeFrom.intValue(), edgeTo.intValue()));
			edges.add(new Edge(edgeTo.intValue(), edgeFrom.intValue()));			
		}

		/* find horizontal and vertical coordinates of each node */
		Edge[] edge = new Edge[edges.size()];
		edges.toArray(edge);

		Graph graph = new Graph(numNodes, edge);
		
		if (canceled)
			return;

		posSet = new boolean[nodeView.length]; // all false
		depthPosSet = new boolean[nodeView.length]; // all false
	
			System.out.println("plain component:\n" + graph.getNodecount());
		
			Thread.yield();
			
			bc = graph.biconnectedComponents();
					
			int maxSize = -1;
			int maxIndex = -1;
			for (int i = 0; i < bc.length; i++)
				if (bc[i].length > maxSize)
				{
					maxSize = bc[i].length;
					maxIndex = i;
				}
			
			if (maxIndex == -1)
				return;
			
			if (canceled)
				return;
			
			
			drawnBiComps = new boolean[bc.length];
			node2BiComp = new HashMap<Integer, Integer>();
			for (int i = 0; i < bc.length; i++)
				if (bc[i].length > 3)
				{
					for (int j = 0; j < bc[i].length; j++)
					{
						if (!node2BiComp.containsKey(bc[i][j]))
							node2BiComp.put(new Integer(bc[i][j]), new Integer(i));
						else if (bc[i].length > bc[node2BiComp.get(bc[i][j]).intValue()].length)
						{
							node2BiComp.remove(new Integer(bc[i][j])); // check this
							node2BiComp.put(new Integer(bc[i][j]),new Integer(i));
						}
					}
				}
			
			
			double radius = (48 * maxSize) / (2 * Math.PI); 
			
			/*int[] resultingNodes = new int[bc[maxIndex].length];
			for (int i = 0; i < resultingNodes.length; i++)
				resultingNodes[i] = index2GinyIndex.get(bc[maxIndex][i]);*/
			
			double deltaAngle = 2 * Math.PI / maxSize;
			double angle = 0;
			
			int startX = (int)radius;
			int startY = (int)radius;
			
			edgesFrom = graph.GetEdgesFrom();
			
			// sorting nodes on inner circle
			bc[maxIndex] = SortInnerCircle(bc[maxIndex]);
			
			
			// setting nodes on inner circle 
			for (int i = 0; i < bc[maxIndex].length; i++)
			{
				//System.out.println(bc[maxIndex][i] + " " + part2NonPart[bc[maxIndex][i]] + "   ");
				nodeView[bc[maxIndex][i]].setOffset(startX + Math.cos(angle) * radius,  startY - Math.sin(angle) * radius);
				posSet[bc[maxIndex][i]] = true;
												
				angle += deltaAngle;
			}
			drawnBiComps[maxIndex] = true;
			
			nodeHeights = new HashMap<Integer, Integer>();
			
			SetOuterCircle(maxIndex, radius, startX, startY, -1);
			
			if (canceled)
				return;
			
			iter = partition.nodeIterator();
			while (iter.hasNext() && !canceled) 
			{
				LayoutNode ln = (LayoutNode) (iter.next());
				NodeView nv = ln.getNodeView();
				double xPos = nv.getXPosition();
				double yPos = nv.getYPosition();
				ln.setX(xPos);
				ln.setY(yPos);
				partition.moveNodeToLocation(ln);
			}		
	}
	
	/**
	 * Function which sets the first neighbours of nodes from circle (biconnected component)
	 * on the concentric circle (larger then the first circle).   
	 * @param compIndex - index of that biconnected component in array bc
	 * @param innerCircleRadius - radius of the inner cicrle
	 * @param startX - start X position for drawing
	 * @param startY - start Y position for drawing
	 * @param firstTouched - node from that component which is found first 
	 */
	private void SetOuterCircle(int compIndex, double innerCircleRadius, double startX, double startY, int firstTouched)
	{		
		int outerNodesCount = 0, rnc = 0;
		Iterator iter;
		HashMap<Integer, Integer> outerCircle = new HashMap<Integer, Integer>();
		for (int i = 0; i < bc[compIndex].length; i++)
		{
			iter = edgesFrom[bc[compIndex][i]].iterator();
			
			while (iter.hasNext())
			{
				int currNeighbour = ((Integer)iter.next()).intValue();
				if (!posSet[currNeighbour])
				{
					outerNodesCount += NoOfChildren(currNeighbour, outerCircle) + 1;
					outerCircle.put(new Integer(currNeighbour), new Integer(0));
					rnc++;
				}
			}
		}
		
		double outerRadius; //Math.round((nodeHorizontalSpacing * outerNodesCount) / (2 * Math.PI));
		outerRadius = 1.5 * innerCircleRadius; // + 5 * nodeHorizontalSpacing;
		int tryCount = (int) (2 * Math.PI * outerRadius / 32);
		double outerDeltaAngle = (2 * Math.PI) / tryCount;
		if (tryCount < 1.2 * outerNodesCount)
		{
			outerRadius = 1.2 * 32 * outerNodesCount / (2 * Math.PI);
			outerDeltaAngle = (2 * Math.PI) / (1.2  * outerNodesCount);
			outerNodesCount *= 1.2; 
		}
		else outerNodesCount = tryCount;
		if (outerNodesCount > 10 && firstTouched != -1)
			outerNodesCount += 5; // 5 places on outer circle for connection with other biconn. comp.
		//System.out.println("tryCount = " + tryCount);
		
		// setting nodes on outer circle
		int[] outerPositionsTaken = new int[outerNodesCount];
		int[] outerPositionsOwners = new int[outerNodesCount];
		for (int i = 0; i < outerPositionsTaken.length; i++)
		{
			outerPositionsTaken[i] = -1;
			outerPositionsOwners[i] = -1;
		}
		
		double pointX, pointY, theAngle, theAngleHlp, innerDeltaAngle;	
		innerDeltaAngle = 2 * Math.PI / bc[compIndex].length;
		if (firstTouched != -1)
		{
			pointX = nodeView[firstTouched].getOffset().getX();
			pointY = nodeView[firstTouched].getOffset().getY();		
			theAngle = Math.asin ((startY - pointY) / Math.sqrt((pointX - startX)*(pointX - startX) 
					                                          + (pointY - startY)*(pointY - startY)));
			theAngleHlp = Math.acos ((pointX - startX) / Math.sqrt((pointX - startX)*(pointX - startX) 
	                + (pointY - startY)*(pointY - startY)));
			if (theAngleHlp > Math.PI / 2)
				theAngle = Math.PI - theAngle;
			if (theAngle < 0)
				theAngle += 2 * Math.PI;
			int idPos = ((int) (theAngle / outerDeltaAngle)) % outerPositionsTaken.length; 
			outerPositionsTaken[idPos] = (int) (theAngle / innerDeltaAngle);
			outerPositionsOwners[idPos] = -2;   // must not be even moved because that node is coming from another bicomp.
			if (outerPositionsTaken.length > 10)
			{
				outerPositionsTaken[(idPos + 1) % outerPositionsTaken.length] = (int) (theAngle / innerDeltaAngle);
				outerPositionsTaken[(idPos + 2) % outerPositionsTaken.length] = (int) (theAngle / innerDeltaAngle);
				outerPositionsTaken[(idPos - 1 + outerPositionsTaken.length) % outerPositionsTaken.length] = (int) (theAngle / innerDeltaAngle);
				outerPositionsTaken[(idPos - 2 + outerPositionsTaken.length) % outerPositionsTaken.length] = (int) (theAngle / innerDeltaAngle);
				
				outerPositionsOwners[(idPos + 1) % outerPositionsOwners.length] = -2;
				outerPositionsOwners[(idPos + 2) % outerPositionsOwners.length] = -2;
				outerPositionsOwners[(idPos - 1 + outerPositionsOwners.length) % outerPositionsOwners.length] = -2;
				outerPositionsOwners[(idPos - 2 + outerPositionsOwners.length) % outerPositionsOwners.length] = -2;
			}
		}
		
		HashMap<Integer, Integer> addedNeighbours = new HashMap<Integer, Integer>();
		for (int i = 0; i < bc[compIndex].length; i++)
		{
			iter = edgesFrom[bc[compIndex][i]].iterator();
			int currentNeighbour, noOfNeighbours = 0;
			while (iter.hasNext())
			{
				currentNeighbour = ((Integer)iter.next()).intValue();
				if (!posSet[currentNeighbour])
				{
					noOfNeighbours += NoOfChildren(currentNeighbour, addedNeighbours) + 1;
					addedNeighbours.put(new Integer(currentNeighbour), new Integer(0));
				}
			}
			
			if (noOfNeighbours == 0)
				continue;
			
			pointX = nodeView[bc[compIndex][i]].getOffset().getX();
			pointY = nodeView[bc[compIndex][i]].getOffset().getY();
			
			theAngle = Math.asin ((startY - pointY) / Math.sqrt((pointX - startX)*(pointX - startX) 
					                                          + (pointY - startY)*(pointY - startY)));
			theAngleHlp = Math.acos ((pointX - startX) / Math.sqrt((pointX - startX)*(pointX - startX) 
                    + (pointY - startY)*(pointY - startY)));
			if (theAngleHlp > Math.PI / 2)
				theAngle = Math.PI - theAngle;
			if (theAngle < 0)
				theAngle += 2 * Math.PI;
			
			iter = edgesFrom[bc[compIndex][i]].iterator();
			
			int startPos = BestFreePositionsForAll((int) ((theAngle / outerDeltaAngle) - noOfNeighbours / 2.0), outerPositionsTaken, outerPositionsOwners, noOfNeighbours, (int) (theAngle / innerDeltaAngle),
					                     startX, startY, outerDeltaAngle, outerRadius, bc[compIndex].length);
			double startAngle = startPos  * outerDeltaAngle;
						
			if (startAngle < 0)
				continue;
			
			iter = edgesFrom[bc[compIndex][i]].iterator();
			while (iter.hasNext())
			{
				currentNeighbour = ((Integer)iter.next()).intValue();
				if (!posSet[currentNeighbour])
				{
					posSet[currentNeighbour] = true;					
					int holeDepth = NoOfChildren(currentNeighbour, addedNeighbours);
					for (int j = 0; j < (holeDepth / 2); j++)
					{
						outerPositionsOwners[(startPos) % outerPositionsOwners.length] = -3; 
						// free but it must not be used (add. space for tree-like struct.)
						outerPositionsTaken[(startPos) % outerPositionsOwners.length] = (int) (theAngle / innerDeltaAngle);
						startPos++;
						startAngle += outerDeltaAngle;
						if (startAngle > 2 * Math.PI)
							startAngle -= 2 * Math.PI;
					}
					nodeView[currentNeighbour].setOffset(startX + Math.cos(startAngle) * outerRadius, startY - Math.sin(startAngle) * outerRadius);
					outerPositionsOwners[(startPos) % outerPositionsOwners.length] = currentNeighbour;
					outerPositionsTaken[(startPos) % outerPositionsOwners.length] = (int) (theAngle / innerDeltaAngle);
					startPos++;
					startAngle += outerDeltaAngle;
					if (startAngle > 2 * Math.PI)
						startAngle -= 2 * Math.PI;
					for (int j = 0; j < (holeDepth / 2); j++)
					{
						outerPositionsOwners[(startPos) % outerPositionsOwners.length] = -3;
						outerPositionsTaken[(startPos) % outerPositionsOwners.length] = (int) (theAngle / innerDeltaAngle);
						startPos++;
						startAngle += outerDeltaAngle;
						if (startAngle > 2 * Math.PI)
							startAngle -= 2 * Math.PI;
					}
					
				}
			}
			
		}		
		
		// laying out the rest of nodes
		for (int i = 0; i < bc[compIndex].length; i++)
		{
			iter = edgesFrom[bc[compIndex][i]].iterator();
			int currentNeighbour;
			while (iter.hasNext())
			{
				currentNeighbour = ((Integer)iter.next()).intValue();
				if (!addedNeighbours.containsKey(new Integer(currentNeighbour)))
				{
					continue;						
				}
				
				pointX = nodeView[currentNeighbour].getOffset().getX();
				pointY = nodeView[currentNeighbour].getOffset().getY();
				
				theAngle = Math.asin ((startY - pointY) / Math.sqrt((pointX - startX)*(pointX - startX) 
                        + (pointY - startY)*(pointY - startY)));
				theAngleHlp = Math.acos ((pointX - startX) / Math.sqrt((pointX - startX)*(pointX - startX) 
						+ (pointY - startY)*(pointY - startY)));
				if (theAngleHlp > Math.PI / 2)
					theAngle = Math.PI - theAngle;
				if (theAngle < 0)
					theAngle += 2 * Math.PI;
				
				for (int j = 0; j < posSet.length; j++)
					depthPosSet[j] = posSet[j];
				EachNodeHeight(currentNeighbour);				
				
				DFSSetPos (currentNeighbour, theAngle, outerRadius - innerCircleRadius);			
				
			}
		}
		
	}
	/**
	 * Returns number of children of the specified node from outer circle.
	 * If number of children larger than 7 return 7.  
	 * @param nodeID
	 * @param outerCircle
	 * @return
	 */
	private int NoOfChildren (int nodeID, HashMap<Integer, Integer> outerCircle)
	{
		int toReturn = 0;
		Iterator iter = edgesFrom[nodeID].iterator();
		while (iter.hasNext())
		{
			int currNeigh = ((Integer)iter.next()).intValue();
			if (!posSet[currNeigh] && !outerCircle.containsKey(currNeigh))
				toReturn++;
		}
		
		if (toReturn > 7)
			return 7;			
		return toReturn;
	}
	
	/**
	 * Sort the nodes from biconnected component to get the best ordering in terms 
	 * of tree-like neighbouring patterns
	 * @param icNodes - nodes from biconnected component
	 * @return
	 */
	private int[] SortInnerCircle(int[] icNodes)
	{
		LinkedList<Integer> greedyNodes = new LinkedList<Integer>();
		LinkedList<Integer> modestNodes = new LinkedList<Integer>();
		
		HashMap<Integer, Integer> forFunct = new HashMap<Integer, Integer>();
		for (int i = 0; i < icNodes.length; i++)
			forFunct.put(new Integer(icNodes[i]), new Integer(0));
		
		for (int i = 0; i < icNodes.length; i++)
		{
			int tmp = NoOfChildren(icNodes[i], forFunct);
			if (tmp > 4)
				greedyNodes.add(new Integer(icNodes[i]));
			else
				modestNodes.add(new Integer(icNodes[i]));			
		}
		
		int[] toReturn = new int[icNodes.length];
		int gNo = greedyNodes.size(), mNo = modestNodes.size(), deltaM, deltaG;
		if (gNo == 0)
		{
			deltaM = mNo;
			deltaG = 0;
		} else if (mNo == 0)
		{
			deltaG = gNo;
			deltaM = 0;
		} else if (gNo > mNo)
		{
			deltaM = 1;
			deltaG = gNo / mNo;
		} else 
		{
			deltaG = 1;
			deltaM = mNo / gNo;
		}
		
		int x = 0;
		Iterator iterM = modestNodes.iterator();
		Iterator iterG = greedyNodes.iterator();
		while (iterM.hasNext() && iterG.hasNext())
		{
			for (int i = 0; i < deltaG; i++)
				toReturn[x++] = ((Integer)iterG.next()).intValue();  
			for (int i = 0; i < deltaM; i++)
				toReturn[x++] = ((Integer)iterM.next()).intValue();
		}
		while (iterG.hasNext())
			toReturn[x++] = ((Integer)iterG.next()).intValue();
		while (iterM.hasNext())
			toReturn[x++] = ((Integer)iterM.next()).intValue();
				
		return toReturn;
	}
	
	/**
	 * Sort the nodes from biconnected component to get the best ordering in terms 
	 * of minimizing inner circle crossings
	 * @param icNodes - nodes from biconnected component
	 * @return
	 */
	private int[] ReduceInnerCircleCrossings1(int[] icNodes)
	{
		int[] toReturn = new int[icNodes.length];
		
		HashMap<Integer, Boolean> alreadySet = new HashMap<Integer, Boolean>();
		for (int i = 0; i < icNodes.length; i++)
			alreadySet.put(new Integer(icNodes[i]), new Boolean(false));
		int x = 0, p = 0;
		toReturn[0] = icNodes[0];
		alreadySet.put(new Integer(toReturn[0]), new Boolean(true));
		while (p < icNodes.length - 1)
		{
			if (p == x && p != 0)
				System.out.println("p = " + p + " x = " + x + " count = " + icNodes.length);
			
			Iterator iter = edgesFrom[toReturn[p]].iterator();
			while (iter.hasNext())
			{
				int neigh = ((Integer)iter.next()).intValue();
				if (alreadySet.containsKey(new Integer(neigh)) && !alreadySet.get(new Integer(neigh)).booleanValue())
				{
					toReturn[x++] = neigh;
					alreadySet.put(new Integer(neigh), new Boolean(true));
				}
			}			
			p++;
		}
		
		return toReturn;
	}
		
	/**
	 * Sort the nodes from biconnected component to get the best ordering in terms 
	 * of minimizing inner circle crossings
	 * @param icNodes - nodes from biconnected component
	 * @return
	 */
	private int[] ReduceInnerCircleCrossings(int[] icNodes)
	{
		int[] toReturn = new int[icNodes.length];
		
		HashMap<Integer, Boolean> alreadySet = new HashMap<Integer, Boolean>();
		HashMap<Integer, Integer> nodeDegree = new HashMap<Integer, Integer>();
		for (int i = 0; i < icNodes.length; i++)
		{
			alreadySet.put(new Integer(icNodes[i]), new Boolean(false));
			toReturn[i] = icNodes[i];
		}
		for (int i = 0; i < icNodes.length; i++)
		{
			int degree = 0;
			Iterator iter = edgesFrom[icNodes[i]].iterator();
			while (iter.hasNext())
			{
				int neigh = ((Integer)iter.next()).intValue();
				if (alreadySet.containsKey(new Integer(neigh)))
					degree++;
			}			
			nodeDegree.put(new Integer(icNodes[i]), new Integer(degree));
		}
		
		for (int i = 0; i < toReturn.length - 1; i++)
		{
			for (int j = i + 1; j < toReturn.length; j++)
			{
				if (nodeDegree.get(new Integer(toReturn[i])) > nodeDegree.get(new Integer(toReturn[j])))
				{
					int tmp = toReturn[i];
					toReturn[i] = toReturn[j];
					toReturn[j] = tmp;
				}
			}			
		}
		
		return toReturn;
	}

	/**
	 * Function traverses graph starting from the node from outer circle until 
	 * it traverse all the nodes. When it comes along another biconnected component 
	 * it sets it out on circle and calls SetOuterCircle() again. The main purpose of 
	 * the function is setting the node positions of tree-like parts of graph. 
	 * @param nodeID - ID of the node from which we start DFS
	 * @param theAngle - the angle at which we "enter" the node, using it we can calculate
	 * 					at which position to set the node
	 * @param theRadius - this will represent the distance between the parent of the node and 
	 * 					the child in tree-like parts 
	 */
	private void DFSSetPos (int nodeID, double theAngle, double theRadius)
	{
		if (node2BiComp.containsKey(new Integer (nodeID)) && !drawnBiComps[node2BiComp.get(new Integer(nodeID)).intValue()])
		{
			int comp = node2BiComp.get(new Integer(nodeID)).intValue();
			double centerX = nodeView[nodeID].getOffset().getX();
			double centerY = nodeView[nodeID].getOffset().getY();
			double radius = 48 *  bc[comp].length / (2 * Math.PI);
			double deltaAngle = 2 * Math.PI / bc[comp].length;
			double currAngle = theAngle - Math.PI - deltaAngle;
			if (currAngle < 0)
				currAngle += 2 * Math.PI;
			
			centerX += Math.cos(theAngle) * radius * 4.0;
			centerY -= Math.sin(theAngle) * radius * 4.0;
			
			drawnBiComps[comp] = true;
			
			// sorting nodes on inner circle
			bc[comp] = SortInnerCircle(bc[comp]);
			/*if (bc[comp].length > 20)
				bc[comp] = ReduceInnerCircleCrossings(bc[comp]);*/
			
			boolean oneAtLeast = false;
			for (int i = 0; i < bc[comp].length; i++)
			{
				if (posSet[bc[comp][i]])
					continue;
				
				nodeView[bc[comp][i]].setOffset(centerX + Math.cos(currAngle) * radius, centerY - Math.sin(currAngle) * radius);
				posSet[bc[comp][i]] = true;
								
				oneAtLeast = true;
				currAngle -= deltaAngle;
				if (currAngle < 0)
					currAngle += 2 * Math.PI;				
			}
			
			if (oneAtLeast)
			{
				nodeView[nodeID].setOffset(nodeView[nodeID].getOffset().getX() + Math.cos(theAngle) * 3 * radius, 
						nodeView[nodeID].getOffset().getY() - Math.sin(theAngle) * 3 * radius);
				
				SetOuterCircle(comp, radius, centerX, centerY, nodeID);
			}			
		}
		else
		{			
			Iterator iter = edgesFrom[nodeID].iterator();
			int currentNeighbour;
			double startAngle = theAngle + (Math.PI / 2);
			if (startAngle > 2 * Math.PI)
				startAngle -= 2 * Math.PI;
						
			int neighboursCount = 0, min1 = 1000, min2 = 1000, max = -1, min1Id = -1, min2Id = -2, maxId = -3;
			HashMap<Integer, Integer> tmp = new HashMap<Integer, Integer>(); 
			while (iter.hasNext())
			{
				currentNeighbour = ((Integer)iter.next()).intValue();
				if (!posSet[currentNeighbour] && !tmp.containsKey(new Integer(currentNeighbour)))
				{
					neighboursCount++;
					tmp.put(new Integer(currentNeighbour), new Integer(0));
					if (nodeHeights.get(new Integer(currentNeighbour)).intValue() < min1)
					{
						min2 = min1; min2Id = min1Id;
						min1 = nodeHeights.get(new Integer(currentNeighbour)).intValue();
						min1Id =  currentNeighbour;
					}
					else if (nodeHeights.get(new Integer(currentNeighbour)).intValue() < min2)
					{
						min2 = nodeHeights.get(new Integer(currentNeighbour)).intValue();
						min2Id = currentNeighbour;
					}
					if (nodeHeights.get(new Integer(currentNeighbour)).intValue() >= max)
							//&& currentNeighbour != min2Id && currentNeighbour != min1Id)
					{
						max = nodeHeights.get(new Integer(currentNeighbour)).intValue();
						maxId = currentNeighbour;
					}					
				}
			}
					
			if (neighboursCount == 0)
				return;
			
			double deltaAngle = Math.PI / (neighboursCount + 1);
			
			startAngle -= deltaAngle;
			if (startAngle < 0)
				startAngle += 2 * Math.PI;
			
			double remStartAngle = startAngle;
			if (neighboursCount > 2)
			{
				deltaAngle = 2 * Math.PI / neighboursCount;
				startAngle = theAngle + Math.PI - 3 * deltaAngle / 2;
				if (startAngle > 2 * Math.PI)
					startAngle -= 2 * Math.PI;
				remStartAngle = theAngle + Math.PI - deltaAngle / 2;
				if (remStartAngle > 2 * Math.PI)
					remStartAngle -= 2 * Math.PI;
			}		
			
			iter = edgesFrom[nodeID].iterator();
			
			double r = 72, rTry;
			if (48 * neighboursCount / (2 * Math.PI) > r)
				r = 48 * neighboursCount / (2 * Math.PI);
			rTry = r;
				
			double hlp = 100.0;
			double startX = nodeView[nodeID].getOffset().getX();
			double startY = nodeView[nodeID].getOffset().getY();
			if (neighboursCount > 2)
			{
				nodeView[nodeID].setOffset(startX + Math.cos(theAngle) * r * ((min2 + 1) % 100) , startY - Math.sin(theAngle) * r * ((min2 + 1) % 100));
				startX = nodeView[nodeID].getOffset().getX();
				startY = nodeView[nodeID].getOffset().getY();
				
				//System.out.println("theAngle = " + theAngle + ", startAngle = " + startAngle + ", remStartAngle = " + remStartAngle + ", deltaAngle = " + deltaAngle);
				System.out.println("min1Id = " + min1Id + ", min2Id" + min2Id + ", maxId" + maxId);
				nodeView[min1Id].setOffset(startX + Math.cos(remStartAngle) * r, startY - Math.sin(remStartAngle) * r);
				nodeView[min2Id].setOffset(startX + Math.cos(remStartAngle + deltaAngle) * r, startY - Math.sin(remStartAngle + deltaAngle) * r);
				if (nodeHeights.get(new Integer(maxId)).intValue() > 8)
					r = 256;
				nodeView[maxId].setOffset(startX + Math.cos(remStartAngle - (neighboursCount / 2) * deltaAngle) * r, startY - Math.sin(remStartAngle - (neighboursCount / 2) * deltaAngle) * r);
				System.out.println("Ugao za maxID " + (remStartAngle - (neighboursCount / 2) * deltaAngle));
			}
			
			tmp = new HashMap<Integer, Integer>();
			while (iter.hasNext())
			{
				currentNeighbour = ((Integer)iter.next()).intValue();
				if (!posSet[currentNeighbour]  && !tmp.containsKey(new Integer(currentNeighbour)))
				{
					if (nodeHeights.get(new Integer(currentNeighbour)).intValue() > 8)
						r = 256;
					else r = rTry;
					
					posSet[currentNeighbour] = true;				
					tmp.put(new Integer(currentNeighbour), new Integer(0));
					
					if ((currentNeighbour != min1Id && currentNeighbour != min2Id && currentNeighbour != maxId)
							|| neighboursCount <= 2)
					{			
						nodeView[currentNeighbour].setOffset(startX + Math.cos(startAngle) * r, startY - Math.sin(startAngle) * r);
						
						startAngle -= deltaAngle;
						if (startAngle < 0)
							startAngle += 2 * Math.PI;
						if ((Math.abs(startAngle - (remStartAngle - (neighboursCount / 2) * deltaAngle)) < 0.0001 || 
								Math.abs(startAngle - (remStartAngle - (neighboursCount / 2) * deltaAngle + 2 * Math.PI)) < 0.0001)
								&& neighboursCount > 2)
						{
							startAngle -= deltaAngle;
							if (startAngle < 0)
								startAngle += 2 * Math.PI;
						}
						
					}
				}			
			}			
			
			iter = edgesFrom[nodeID].iterator();		
			if (neighboursCount > 2)
			{
				DFSSetPos (min1Id, remStartAngle, theRadius * Math.sin(deltaAngle / 2));
				DFSSetPos (min2Id, remStartAngle + deltaAngle, theRadius * Math.sin(deltaAngle / 2));
				DFSSetPos (maxId, remStartAngle - (neighboursCount / 2) * deltaAngle, theRadius * Math.sin(deltaAngle / 2));
				hlp = remStartAngle;
				remStartAngle -= deltaAngle;
			}
			while (iter.hasNext())
			{
				currentNeighbour = ((Integer)iter.next()).intValue();
				if (tmp.containsKey(new Integer(currentNeighbour)))
				{
					if ((currentNeighbour != min1Id && currentNeighbour != min2Id && currentNeighbour != maxId)
							|| neighboursCount <= 2)
					{		
						DFSSetPos (currentNeighbour, remStartAngle, theRadius * Math.sin(deltaAngle / 2));
						
						remStartAngle -= deltaAngle;
						if ((remStartAngle == hlp - (neighboursCount / 2) * deltaAngle || 
								remStartAngle == hlp - (neighboursCount / 2) * deltaAngle + 2 * Math.PI)
								&& neighboursCount > 2)
							startAngle -= deltaAngle;
						if (remStartAngle < 0)
							remStartAngle += 2 * Math.PI;
					}
				}
			}
		}		
	}
	
	/**
	 * Heuristic function which estimates the number of nodes "after" the given node.
	 * Using it we can estimate the distance from this node to his children. 
	 * @param nodeID - ID of given node
	 * @return
	 */
	private int EachNodeHeight (int nodeID)
	{
		Iterator iter = edgesFrom[nodeID].iterator();
		int currentNeighbour, noOfChildren = 0;
		HashMap<Integer, Integer> tmp = new HashMap<Integer, Integer>();
		
		while (iter.hasNext())
		{
			currentNeighbour = ((Integer)iter.next()).intValue();
			if (!depthPosSet[currentNeighbour] && !tmp.containsKey(new Integer(currentNeighbour)))
			{
				depthPosSet[currentNeighbour] = true;
				tmp.put(new Integer(currentNeighbour), new Integer(0));				
			}
		}
		
		iter = edgesFrom[nodeID].iterator();
		while (iter.hasNext())
		{
			currentNeighbour = ((Integer)iter.next()).intValue();
			if (tmp.containsKey(new Integer(currentNeighbour)))
			{
				noOfChildren += EachNodeHeight (currentNeighbour);				
			}
		}
		if (nodeHeights.containsKey(new Integer(nodeID)))
			nodeHeights.remove(new Integer(nodeID));
		nodeHeights.put(new Integer(nodeID), new Integer (noOfChildren));
		return (noOfChildren + 1);
	}
		
	/**
	 * Founds best positions for nodes from outer cicrle, according to inner circle.
	 * We avoid crossings of edges between inner and outer circle, and we want to minimize
	 * the length of that edges. 
	 * @param idealPosition - according to position of neighbour node from inner circle
	 * @param outerPositionsTaken - array of availability of positions on second circle
	 * @param outerPositionsOwners - array of owners (from inner cicrle) of positions on second circle
	 * @param noOfPos - number of positions that we need
	 * @param innerCirclePos - owner (parent, neighbour from inner cicrle) of given node
	 * @param startX
	 * @param startY
	 * @param outerDeltaAngle
	 * @param outerRadius
	 * @param innerCSize
	 * @return
	 */	
	private int BestFreePositionsForAll(int idealPosition, int[] outerPositionsTaken, int[] outerPositionsOwners, int noOfPos, int innerCirclePos,
			                            double startX, double startY, double outerDeltaAngle, double outerRadius, int innerCSize)
	{
		for (int j = 0; j < outerPositionsTaken.length; j++)
			System.out.print(outerPositionsTaken[j] + " ");
		System.out.println("innerCircPos: " + innerCirclePos + ", noOfPos: " + noOfPos + ", idealPos: " + idealPosition);
		
		
		int startPos = idealPosition;
		if (idealPosition < 0)
			startPos += outerPositionsTaken.length;
			
		int i = 0, alreadyFound = 0, startOfAlFound = -1;
		boolean found = false, goDown = false, goUp = false;
		while (!found && !(goUp && goDown))
		{
			//System.out.print(startPos + " ");
			for (i = startPos; (i < startPos + noOfPos) && (outerPositionsTaken[i % outerPositionsTaken.length] == -1); i++)
			{			
			}			
			if (i < startPos + noOfPos)
			{
				if ((outerPositionsTaken[i % outerPositionsTaken.length] > innerCirclePos 
						&& outerPositionsTaken[i % outerPositionsTaken.length] - innerCirclePos < 0.7 * innerCSize)
						|| (innerCirclePos - outerPositionsTaken[i % outerPositionsTaken.length] > 0.7 * innerCSize))
				{
					alreadyFound = (i - startPos + outerPositionsTaken.length) % outerPositionsTaken.length;
					startOfAlFound = startPos;
					startPos -= noOfPos - alreadyFound;
					if (startPos < 0)
						startPos += outerPositionsTaken.length;
					goDown = true;					
				}
				else 
				{
					startPos = (i + 1) % outerPositionsTaken.length;
					goUp = true;
				}
			}
			else found = true;
		}
		
		if (goUp && goDown)
		{		
			i = startOfAlFound - 1;
			int j = i - 1, count = 0;
			System.out.print(j + " ");
			if ((outerPositionsTaken[i % outerPositionsTaken.length] > innerCirclePos
					&& outerPositionsTaken[i % outerPositionsTaken.length] - innerCirclePos < 0.7 * innerCSize)
				|| (innerCirclePos - outerPositionsTaken[i % outerPositionsTaken.length] > 0.7 * innerCSize))
			{
				j--;
				i--;
			}
			while (count < (noOfPos - alreadyFound))
			{
				System.out.print(j + " ");
				if (outerPositionsTaken[(j + outerPositionsTaken.length) % outerPositionsTaken.length] == -1)
				{
					// move all for one place left
				//	System.out.print(" moving ");
					if (outerPositionsOwners[(j + outerPositionsTaken.length) % outerPositionsTaken.length] == -2)
					{
						System.out.println("BUUUUUUUUUUUUUUUUUUU");
						return -1;
					}
					for (int k = j; k < i - count; k++)
					{
						if (outerPositionsOwners[(k + 1 + outerPositionsTaken.length) % outerPositionsTaken.length] > 0)
							nodeView[outerPositionsOwners[(k + 1 + outerPositionsTaken.length) % outerPositionsTaken.length]].setOffset(startX + Math.cos(outerDeltaAngle * k) * outerRadius, 
								                           startY - Math.sin(outerDeltaAngle * k) * outerRadius);
						outerPositionsOwners[(k + outerPositionsTaken.length) % outerPositionsTaken.length] = outerPositionsOwners[(k + 1 + outerPositionsTaken.length) % outerPositionsTaken.length];
						outerPositionsTaken[(k + outerPositionsTaken.length) % outerPositionsTaken.length] = outerPositionsTaken[(k + 1 + outerPositionsTaken.length) % outerPositionsTaken.length];
					}					 
					count++;						
				}
				j--;				
			}			
			
			startPos = (i - count + 1 + outerPositionsOwners.length) % outerPositionsOwners.length;
		}
		
	/*	for (i = startPos; i < startPos + noOfPos; i++)
		{
			outerPositionsTaken[i % outerPositionsTaken.length] = innerCirclePos;		
		}*/
		
		return startPos;
	}
	
	private int BestFreePosition(int idealPosition, boolean[] outerPositionsTaken)
	{
		//for (int i = 0; i < outerPositionsTaken.length; i++)
			//System.out.print(outerPositionsTaken[i] + " ");
		//System.out.println();
		//System.out.println(idealPosition);
		
		int startPosition = idealPosition;
		if (idealPosition < 0)
			startPosition = idealPosition % outerPositionsTaken.length;
		
		int move = 0;
		while (move < outerPositionsTaken.length / 2 + 1)
		{
			if (!outerPositionsTaken[(startPosition + move) % outerPositionsTaken.length])
			{
				outerPositionsTaken[(startPosition + move) % outerPositionsTaken.length] = true;
				//System.out.println((startPosition + move) % outerPositionsTaken.length);
				return (startPosition + move) % outerPositionsTaken.length;
			}
			if (!outerPositionsTaken[(startPosition - move) % outerPositionsTaken.length])
			{
				outerPositionsTaken[(startPosition - move) % outerPositionsTaken.length] = true;
			//	System.out.println((startPosition - move) % outerPositionsTaken.length);
				return (startPosition - move) % outerPositionsTaken.length;
			}
			move++;
		}
		
		return -1;	
	}
	
	public void halt() {
		canceled = true;
	}
	
	protected void initialize_properties() {
		layoutProperties.add(new Tunable("nodeHorizontalSpacing",
		                                 "Horizontal spacing between nodes", Tunable.INTEGER,
		                                 new Integer(64)));
		layoutProperties.add(new Tunable("nodeVerticalSpacing", "Vertical spacing between nodes",
		                                 Tunable.INTEGER, new Integer(32)));
		
		layoutProperties.add(new Tunable("leftEdge", "Left edge margin", Tunable.INTEGER,
		                                 new Integer(32)));
		layoutProperties.add(new Tunable("topEdge", "Top edge margin", Tunable.INTEGER,
		                                 new Integer(32)));
		layoutProperties.add(new Tunable("rightMargin", "Right edge margin", Tunable.INTEGER,
		                                 new Integer(1000)));
		// We've now set all of our tunables, so we can read the property 
		// file now and adjust as appropriate
		layoutProperties.initializeProperties();

		// Finally, update everything.  We need to do this to update
		// any of our values based on what we read from the property file
		updateSettings(true);
	}

	public void updateSettings() {
		updateSettings(false);
	}

	public void setTaskMonitor(TaskMonitor tm) {
		taskMonitor = tm;
	}
	/**
	 *  DOCUMENT ME!
	 *
	 * @param force DOCUMENT ME!
	 */
	public void updateSettings(boolean force) {
		layoutProperties.updateValues();

		Tunable t = layoutProperties.get("nodeHorizontalSpacing");

		if ((t != null) && (t.valueChanged() || force))
			nodeHorizontalSpacing = ((Integer) t.getValue()).intValue();

		t = layoutProperties.get("nodeVerticalSpacing");

		if ((t != null) && (t.valueChanged() || force))
			nodeVerticalSpacing = ((Integer) t.getValue()).intValue();

		t = layoutProperties.get("leftEdge");

		if ((t != null) && (t.valueChanged() || force))
			leftEdge = ((Integer) t.getValue()).intValue();

		t = layoutProperties.get("topEdge");

		if ((t != null) && (t.valueChanged() || force))
			topEdge = ((Integer) t.getValue()).intValue();

		t = layoutProperties.get("rightMargin");

		if ((t != null) && (t.valueChanged() || force))
			rightMargin = ((Integer) t.getValue()).intValue();
	
	}

	public JPanel getSettingsPanel() {
		JPanel panel = new JPanel(new GridLayout(0, 1));
		panel.add(layoutProperties.getTunablePanel());

		return panel;
	}

	
	public String getTitle() {
		return new String("Circular Layout");
	}

}