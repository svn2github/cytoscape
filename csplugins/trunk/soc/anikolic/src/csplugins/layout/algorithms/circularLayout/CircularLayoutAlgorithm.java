package csplugins.layout.algorithms.circularLayout;

import giny.view.EdgeView;
import giny.view.NodeView;

import java.awt.GridLayout;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JPanel;

import org.cytoscape.coreplugin.cpath.model.MaxHitsOption;

import csplugins.layout.algorithms.hierarchicalLayout.Edge;
import csplugins.layout.algorithms.hierarchicalLayout.Graph;

import cytoscape.layout.AbstractLayout;
import cytoscape.layout.LayoutProperties;
import cytoscape.layout.Tunable;
import cytoscape.task.TaskMonitor;

public class CircularLayoutAlgorithm extends AbstractLayout
{
	private int nodeHorizontalSpacing = 64;
	private int nodeVerticalSpacing = 32;
	private int leftEdge = 32;
	private int topEdge = 32;
	private int rightMargin = 1000;
	private int componentSpacing = 64;
	
	private boolean selected_only = false;
	private LayoutProperties layoutProperties;
	
	private boolean[] posSet;
	private int[] part2NonPart;
	private LinkedList<Integer>[] edgesFrom;
	private NodeView[] nodeView;
	
	public CircularLayoutAlgorithm() 
	{
		super();
		layoutProperties = new LayoutProperties(getName());
		initialize_properties();
	}
	
	public boolean supportsSelectedOnly() 
	{
		return true;
	}
	
	public void construct() 
	{
		taskMonitor.setStatus("Initializing");
		initialize(); // Calls initialize_local
		layout();
		
	}

	public String getName() 
	{
		return "circular";
	}

	public String toString() 
	{
		return "Circular Layout";
	}
	
	public void layout() 
	{
		taskMonitor.setPercentCompleted(0);
		taskMonitor.setStatus("Capturing snapshot of network and selected nodes");

		if (canceled)
			return;

		/* construct node list with selected nodes first */
		List selectedNodes = networkView.getSelectedNodes();
		int numSelectedNodes = selectedNodes.size();

		if (!selectedOnly)
			numSelectedNodes = 0;

		if (numSelectedNodes == 1) {
			// We were asked to do a hierchical layout of a single node -- done!
			return;
		}

		final int numNodes = networkView.getNodeViewCount();
		final int numLayoutNodes = (numSelectedNodes < 1) ? numNodes : numSelectedNodes;
		nodeView = new NodeView[numNodes];
		int nextNode = 0;
		HashMap<Integer, Integer> ginyIndex2Index = new HashMap(numNodes * 2);
	/*	HashMap<Integer, Integer> index2GinyIndex = new HashMap(numNodes * 2);*/

		if (numSelectedNodes > 1) {
			Iterator iter = selectedNodes.iterator();

			while (iter.hasNext() && !canceled) {
				nodeView[nextNode] = (NodeView) (iter.next());
				ginyIndex2Index.put(new Integer(nodeView[nextNode].getNode().getRootGraphIndex()),
				                    new Integer(nextNode));
		/*		index2GinyIndex.put(new Integer(nextNode), new Integer(nodeView[nextNode].getNode().getRootGraphIndex()));*/
				nextNode++;
			}
		} else {
			Iterator iter = networkView.getNodeViewsIterator(); /* all nodes */

			while (iter.hasNext() && !canceled) {
				NodeView nv = (NodeView) (iter.next());
				Integer nodeIndexKey = new Integer(nv.getNode().getRootGraphIndex());

				if (!ginyIndex2Index.containsKey(nodeIndexKey)) {
					nodeView[nextNode] = nv;
					ginyIndex2Index.put(nodeIndexKey, new Integer(nextNode));
				/*	index2GinyIndex.put(new Integer(nextNode), nodeIndexKey);*/
					nextNode++;
				}
			}
		}

		if (canceled)
			return;

		/* create edge list from edges between selected nodes */
		LinkedList<Edge> edges = new LinkedList();
		Iterator iter = networkView.getEdgeViewsIterator();

		while (iter.hasNext()) {
			EdgeView ev = (EdgeView) (iter.next());
			Integer edgeFrom = (Integer) ginyIndex2Index.get(new Integer(ev.getEdge().getSource()
			                                                               .getRootGraphIndex()));
			Integer edgeTo = (Integer) ginyIndex2Index.get(new Integer(ev.getEdge().getTarget()
			                                                             .getRootGraphIndex()));

			if ((edgeFrom == null) || (edgeTo == null)) {
				// Must be from an unselected node
				continue;
			}

			if (canceled)
				return;

			if ((numSelectedNodes <= 1)
			    || ((edgeFrom.intValue() < numSelectedNodes)
			       && (edgeTo.intValue() < numSelectedNodes))) {
				/* add edge to graph */
				edges.add(new Edge(edgeFrom.intValue(), edgeTo.intValue()));
				edges.add(new Edge(edgeTo.intValue(), edgeFrom.intValue()));
			}
		}

		/* find horizontal and vertical coordinates of each node */
		Edge[] edge = new Edge[edges.size()];
		edges.toArray(edge);

		Graph graph = new Graph(numLayoutNodes, edge);

		/*
		int edgeIndex;
		for (edgeIndex = 0; edgeIndex<edge.length; edgeIndex++) {
		     System.out.println("Edge: " + edge[edgeIndex].getFrom() + " - " + edge[edgeIndex].getTo());
		}
		*/
		int[] cI = graph.componentIndex();
		int x;
		/*
		System.out.println("Node index:\n");
		for (x=0; x<graph.getNodecount(); x++) {
		    System.out.println(cI[x]);
		}
		System.out.println("Partitioning into components:\n");
		*/
		taskMonitor.setPercentCompleted(10);
		taskMonitor.setStatus("Finding connected components");

		if (canceled)
			return;

		int[] renumber = new int[cI.length];
		Graph[] component = graph.partition(cI, renumber);
		final int numComponents = component.length;
		int[][] layer = new int[numComponents][];
		int[][] horizontalPosition = new int[numComponents][];
		
		int maxX = leftEdge;
		int maxY = topEdge;
		int curY = topEdge;

		posSet = new boolean[nodeView.length]; // all false
		for (x = 0; x < component.length; x++) 
		{			
			System.out.println("plain component:\n" + component[x].getNodecount());
		//	System.out.println(component[x]);
			/*System.out.println("filtered component:\n");
			System.out.println(component[x].getGraphWithoutOneOrTwoCycles());
			System.out.println("nonmulti component:\n");
			System.out.println(component[x].getGraphWithoutMultipleEdges());
			int cycleEliminationPriority[] = component[x].getCycleEliminationVertexPriority();
			System.out.println("acyclic component:\n");
			System.out.println(component[x].getGraphWithoutCycles(cycleEliminationPriority));
			System.out.println("reduced component:\n");
			System.out.println(component[x].getReducedGraph());
			System.out.println("layer assignment:\n");
			*/
			
			part2NonPart = new int[component[x].getNodecount()];
			int t = 0;
			for (int i = 0; i < cI.length; i++)
				if (cI[i] == x)
					part2NonPart[t++] = i;
			
			
			taskMonitor.setPercentCompleted(20 + ((60 * (x * 3)) / numComponents / 3));
			
			taskMonitor.setStatus("Finding biconnected components");
			Thread.yield();
			
			int[][] bc = component[x].biconnectedComponents();
		/*	for (int i = 0; i < bc.length; i++)
			{
				for (int j = 0; j < bc[i].length; j++)
					System.out.print(bc[i][j] + " ");
				System.out.println();
			}*/
			
			int maxSize = -1;
			int maxIndex = -1;
			for (int i = 0; i < bc.length; i++)
				if (bc[i].length > maxSize)
				{
					maxSize = bc[i].length;
					maxIndex = i;
				}
			
			if (maxIndex == -1)
				continue;
			
			if (canceled)
				return;
			
			double radius = Math.round((nodeHorizontalSpacing * maxSize) / (2 * Math.PI)); 
			
			/*int[] resultingNodes = new int[bc[maxIndex].length];
			for (int i = 0; i < resultingNodes.length; i++)
				resultingNodes[i] = index2GinyIndex.get(bc[maxIndex][i]);*/
			
			double deltaAngle = 2 * Math.PI / maxSize;
			double angle = 0;
			if (maxX + 2 * radius > rightMargin)
			{
				maxX = leftEdge;
				curY = maxY;
				maxY += 2 * radius;				
			}
			int startX = maxX + (int)radius;
			int startY = curY + (int)radius;
			
						
			edgesFrom = component[x].GetEdgesFrom();

			// setting nodes on inner circle 
			int outerNodesCount = 0;
						
			for (int i = 0; i < bc[maxIndex].length; i++)
			{
				//System.out.println(bc[maxIndex][i] + " " + part2NonPart[bc[maxIndex][i]] + "   ");
				nodeView[part2NonPart[bc[maxIndex][i]]].setOffset(startX + Math.cos(angle) * radius,  startY - Math.sin(angle) * radius);
				posSet[part2NonPart[bc[maxIndex][i]]] = true;
								
				angle += deltaAngle;
			}
			
			// counting nodes on second circle (first neighbours of nodes from the first circle)
			for (int i = 0; i < bc[maxIndex].length; i++)
			{
				iter = edgesFrom[bc[maxIndex][i]].iterator();
				while (iter.hasNext())
				{
					if (!posSet[part2NonPart[((Integer)iter.next()).intValue()]])
						outerNodesCount++;
				}
			}
			
			double outerRadius; //Math.round((nodeHorizontalSpacing * outerNodesCount) / (2 * Math.PI));
			outerRadius = 1.5 * radius; // + 5 * nodeHorizontalSpacing;
			int tryCount = (int) (2 * Math.PI * outerRadius / nodeHorizontalSpacing);
			double outerDeltaAngle = (2 * Math.PI) / tryCount;
			if (tryCount < outerNodesCount)
			{
				outerRadius = nodeHorizontalSpacing * outerNodesCount / (2 * Math.PI);
				outerDeltaAngle = (2 * Math.PI) / outerNodesCount;
			}
			else outerNodesCount = tryCount;
			
			System.out.println(outerNodesCount);
			// setting nodes on outer circle
			boolean[] outerPositionsTaken = new boolean[outerNodesCount];
			double pointX, pointY, theAngle, theAngleHlp, newAngle;
			
			int[] nodesSortedByGreed = new int[bc[maxIndex].length];
			int lb = 0, ub = bc[maxIndex].length - 1;
			for (int i = 0; i < bc[maxIndex].length; i++)
			{
				iter = edgesFrom[bc[maxIndex][i]].iterator();
				int currentNeighbour, noOfNeighbours = 0;
				while (iter.hasNext())
				{
					currentNeighbour = part2NonPart[((Integer)iter.next()).intValue()];
					if (!posSet[currentNeighbour])
					{
						noOfNeighbours++;						
					}
				}
				if (noOfNeighbours > 4)
					nodesSortedByGreed[lb++] = bc[maxIndex][i];
				else 
					nodesSortedByGreed[ub--] = bc[maxIndex][i];
			}
			for (int i = 0; i < nodesSortedByGreed.length; i++)
			{
				pointX = nodeView[part2NonPart[nodesSortedByGreed[i]]].getOffset().getX();
				pointY = nodeView[part2NonPart[nodesSortedByGreed[i]]].getOffset().getY();
				
				theAngle = Math.asin ((startY - pointY) / Math.sqrt((pointX - startX)*(pointX - startX) 
						                                          + (pointY - startY)*(pointY - startY)));
				theAngleHlp = Math.acos ((pointX - startX) / Math.sqrt((pointX - startX)*(pointX - startX) 
                        + (pointY - startY)*(pointY - startY)));
				//if (theAngle < 0)
					//theAngle += 2 * Math.PI;
				if (theAngleHlp > Math.PI / 2)
					theAngle = Math.PI - theAngle;
				if (theAngle < 0)
					theAngle += 2 * Math.PI;
				
				iter = edgesFrom[nodesSortedByGreed[i]].iterator();
				int currentNeighbour, noOfNeighbours = 0;
				while (iter.hasNext())
				{
					currentNeighbour = part2NonPart[((Integer)iter.next()).intValue()];
					if (!posSet[currentNeighbour])
					{
						noOfNeighbours++;						
					}
				}
				//if (i < 5)
					//System.out.println("Greedy cvor " + i + " ima suseda " + noOfNeighbours);
				//if (noOfNeighbours > 9)
					//System.out.println("ID greedy node-a " + nodesSortedByGreed[i] + " i njegovo mesto u nizu " + i);
				double startAngle = BestFreePositionsForAll((int) (theAngle / outerDeltaAngle), outerPositionsTaken, noOfNeighbours) * outerDeltaAngle;
				
				iter = edgesFrom[nodesSortedByGreed[i]].iterator();
				while (iter.hasNext())
				{
					currentNeighbour = part2NonPart[((Integer)iter.next()).intValue()];
					if (!posSet[currentNeighbour])
					{
						posSet[currentNeighbour] = true;
						//System.out.println(theAngle + " " + outerDeltaAngle + " " + outerPositionsTaken.length);
						nodeView[currentNeighbour].setOffset(startX + Math.cos(startAngle) * outerRadius, startY - Math.sin(startAngle) * outerRadius);
						startAngle += outerDeltaAngle;
						if (startAngle > 2 * Math.PI)
							startAngle -= 2 * Math.PI;
					}
				}
			}
			
			HashMap<Integer, Integer> mainCircle = new HashMap<Integer, Integer>();
			for (int i = 0; i < bc[maxIndex].length; i++)
			{
				mainCircle.put(new Integer(bc[maxIndex][i]), new Integer(0));
			}
			
						
			// laying out the rest of the biconnected components
			for (int i = 0; i < bc[maxIndex].length; i++)
			{
				double neighX = nodeView[part2NonPart[bc[maxIndex][i]]].getOffset().getX();
				double neighY = nodeView[part2NonPart[bc[maxIndex][i]]].getOffset().getY();
				
				iter = edgesFrom[bc[maxIndex][i]].iterator();
				int currentNeighbour;
				while (iter.hasNext())
				{
					currentNeighbour = ((Integer)iter.next()).intValue();
					if (mainCircle.containsKey(new Integer(currentNeighbour)))
					{
						//System.out.print("SGK ");
						continue;						
					}
					
					pointX = nodeView[part2NonPart[currentNeighbour]].getOffset().getX();
					pointY = nodeView[part2NonPart[currentNeighbour]].getOffset().getY();
					
					theAngle = Math.asin ((startY - pointY) / Math.sqrt((pointX - startX)*(pointX - startX) 
                            + (pointY - startY)*(pointY - startY)));
					theAngleHlp = Math.acos ((pointX - startX) / Math.sqrt((pointX - startX)*(pointX - startX) 
							+ (pointY - startY)*(pointY - startY)));
					//if (theAngle < 0)
						//theAngle += 2 * Math.PI;
					if (theAngleHlp > Math.PI / 2)
						theAngle = Math.PI - theAngle;
					if (theAngle < 0)
						theAngle += 2 * Math.PI;
					
					if ((theAngle > 0 && theAngle < Math.PI/2) || (theAngle > Math.PI && theAngle < 3 * Math.PI/2)) 
						System.out.println("prosledjen ugao " + theAngle);
					
					DFSSetPos (currentNeighbour, theAngle);			
					
				}
			}
			
		/*	for (int k = 0; k < posSet.length; k++)
				System.out.print(posSet[k] + " ");*/
								
			
			maxX = startX + (int)outerRadius + componentSpacing;
			if (startY + outerRadius + componentSpacing > maxY)
				maxY = startY + (int)outerRadius + componentSpacing + 500;
			
		}		
		
		taskMonitor.setPercentCompleted(100);
		taskMonitor.setStatus("circular layout complete");

	}
	
	private void DFSSetPos (int nodeID, double theAngle)
	{
		//posSet[part2NonPart[nodeID]] = true;
		Iterator iter = edgesFrom[nodeID].iterator();
		int currentNeighbour;
		double startAngle = theAngle + (Math.PI / 2);
		if (startAngle > 2 * Math.PI)
			startAngle -= 2 * Math.PI;
		//System.out.println("Kad udje u f-ju" + startAngle);
		
		
		int neighboursCount = 0;
		HashMap<Integer, Integer> tmp = new HashMap<Integer, Integer>(); // ovo postavljanje tmp-a nema nikakve poente
		while (iter.hasNext())
		{
			currentNeighbour = part2NonPart[((Integer)iter.next()).intValue()];
			if (!posSet[currentNeighbour] && !tmp.containsKey(new Integer(currentNeighbour)))
			{
				neighboursCount++;
				tmp.put(new Integer(currentNeighbour), new Integer(0));
			}
		}
		
		if (neighboursCount == 0)
			return;
		
		double startX = nodeView[part2NonPart[nodeID]].getOffset().getX();
		double startY = nodeView[part2NonPart[nodeID]].getOffset().getY();
		double deltaAngle = Math.PI / (neighboursCount + 1);
		startAngle -= deltaAngle;
		if (startAngle < 0)
			startAngle += 2 * Math.PI;
		iter = edgesFrom[nodeID].iterator();
		double remStartAngle = startAngle;
				
		tmp = new HashMap<Integer, Integer>();
		while (iter.hasNext())
		{
			currentNeighbour = ((Integer)iter.next()).intValue();
			if (!posSet[part2NonPart[currentNeighbour]]  && !tmp.containsKey(new Integer(part2NonPart[currentNeighbour])))
			{
				/*	if (startAngle > 0 && startAngle < Math.PI / 2)
						nodeView[part2NonPart[currentNeighbour]].setOffset(startX + Math.cos(startAngle) * 128, startY + Math.sin(startAngle) * 128);
					if (startAngle > Math.PI && startAngle < 3 * Math.PI / 2)
						nodeView[part2NonPart[currentNeighbour]].setOffset(startX + Math.cos(startAngle) * 128, startY + Math.sin(startAngle) * 128);
					else
					{*/
					//System.out.println("startAngle " + startAngle + ", deltaY " + (- Math.sin(startAngle) * 128));
					nodeView[part2NonPart[currentNeighbour]].setOffset(startX + Math.cos(startAngle) * 128, startY - Math.sin(startAngle) * 128);
					//}
					posSet[part2NonPart[currentNeighbour]] = true;
					
				
				tmp.put(new Integer(part2NonPart[currentNeighbour]), new Integer(0));
				startAngle -= deltaAngle;
				if (startAngle < 0)
					startAngle += 2 * Math.PI;
			}
		}
		iter = edgesFrom[nodeID].iterator();
		while (iter.hasNext())
		{
			currentNeighbour = ((Integer)iter.next()).intValue();
			if (tmp.containsKey(new Integer(part2NonPart[currentNeighbour])))
			{
				DFSSetPos (currentNeighbour, remStartAngle);
				
				remStartAngle -= deltaAngle;
				if (remStartAngle < 0)
					remStartAngle += 2 * Math.PI;
			}
		}
		
	}
	
	private int BestFreePositionsForAll(int idealPosition, boolean[] outerPositionsTaken, int noOfPos)
	{
		/*if (noOfPos > 9)
			System.out.println("Id poz" + idealPosition);*/
		//System.out.println("idealPos: " + idealPosition);
		int startPosition = idealPosition % outerPositionsTaken.length;
		//System.out.println("startPos: " + startPosition);
		startPosition = startPosition - noOfPos / 2;
		//System.out.println("moved startPos: " + startPosition);
		if (startPosition < 0)
			startPosition = startPosition + outerPositionsTaken.length;
		//System.out.println("after mod startPos: " + startPosition);
		
		for (int offset = 0; offset < outerPositionsTaken.length / 2; offset++)
		{
			int currentPos = startPosition + offset;
			//System.out.println("currentPos: " + currentPos);
			while (!outerPositionsTaken[currentPos % outerPositionsTaken.length] 
			                            && (currentPos - startPosition - offset < noOfPos))
			{
				currentPos++;
			}
			if (currentPos - startPosition - offset == noOfPos)
			{
				for (int i = startPosition + offset; i < currentPos; i++)
					outerPositionsTaken[i % outerPositionsTaken.length] = true;
				return (startPosition + offset) % outerPositionsTaken.length;
			}
			
			currentPos = startPosition + offset;
			while (!outerPositionsTaken[(currentPos + outerPositionsTaken.length) % outerPositionsTaken.length] 
			                            && (startPosition + offset - currentPos < noOfPos))
			{
				currentPos--;
			}
			if (startPosition + offset - currentPos == noOfPos)
			{
				for (int i = currentPos + 1; i <= startPosition + offset; i++)
					outerPositionsTaken[(i + outerPositionsTaken.length) % outerPositionsTaken.length] = true;
				return (currentPos + 1 + outerPositionsTaken.length) % outerPositionsTaken.length;
			}
			
			
			currentPos = startPosition - offset;
			while (!outerPositionsTaken[(currentPos + outerPositionsTaken.length) % outerPositionsTaken.length] 
			                            && (currentPos - startPosition + offset < noOfPos))
			{
				currentPos++;
			}
			if (currentPos - startPosition + offset == noOfPos)
			{
				for (int i = startPosition - offset; i < currentPos; i++)
					outerPositionsTaken[(i + outerPositionsTaken.length) % outerPositionsTaken.length] = true;
				return (startPosition - offset + outerPositionsTaken.length) % outerPositionsTaken.length;
			}
			
			currentPos = startPosition - offset;
			while (!outerPositionsTaken[(currentPos + outerPositionsTaken.length) % outerPositionsTaken.length] 
			                            && (startPosition - offset - currentPos < noOfPos))
			{
				currentPos--;
			}
			if (startPosition - offset - currentPos == noOfPos)
			{
				for (int i = currentPos + 1; i <= startPosition - offset; i++)
					outerPositionsTaken[(i + outerPositionsTaken.length) % outerPositionsTaken.length] = true;
				return (currentPos + 1 + outerPositionsTaken.length) % outerPositionsTaken.length;
			}
		}
		
		return -1;
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
		layoutProperties.add(new Tunable("selected_only", "Only layout selected nodes",
		                                 Tunable.BOOLEAN, new Boolean(false)));
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

		t = layoutProperties.get("selected_only");

		if ((t != null) && (t.valueChanged() || force))
			selected_only = ((Boolean) t.getValue()).booleanValue();
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