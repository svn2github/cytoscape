/** Copyright (c) 2004 Institute for Systems Biology, University of
 ** California at San Diego, and Memorial Sloan-Kettering Cancer Center.
 **
 ** Code written by: Robert Sheridan
 ** Authors: Gary Bader, Ethan Cerami, Chris Sander
 ** Date: January 19.2004
 ** Description: Hierarcical layout plugin, based on techniques by Sugiyama
 ** et al. described in chapter 9 of "graph drawing", Di Battista et al,1999
 **
 ** Based on the csplugins.tutorial written by Ethan Cerami and GINY plugin
 ** written by Andrew Markiel
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
 ** Institute for Systems Biology, the University of California at San Diego
 ** and/or Memorial Sloan-Kettering Cancer Center
 ** have no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall the
 ** Institute for Systems Biology, the University of California at San Diego
 ** and/or Memorial Sloan-Kettering Cancer Center
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if the
 ** Institute for Systems Biology, the University of California at San
 ** Diego and/or Memorial Sloan-Kettering Cancer Center
 ** have been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 **
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/

package cytoscape.bubbleRouter;

import giny.view.EdgeView;
import giny.view.NodeView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.util.TaskManager;
import cytoscape.view.CyNetworkView;

class HierarchyFlowLayoutOrderNode implements Comparable {
	public NodeView nodeView;
	public int componentNumber;
	public int componentSize;
	public int layer;
	public int horizontalPosition;
	public int xPos;
	public int yPos;

	public int getXPos() { return xPos; }
	public int getYPos() { return yPos; }
	public void setXPos(int a_xPos) { xPos = a_xPos; }
	public void setYPos(int a_yPos) { yPos = a_yPos; }

	public HierarchyFlowLayoutOrderNode(NodeView a_nodeView, int a_componentNumber,
			int a_componentSize, int a_layer, int a_horizontalPosition) {
		nodeView = a_nodeView;
		componentNumber = a_componentNumber;
		componentSize = a_componentSize;
		layer = a_layer;
		horizontalPosition = a_horizontalPosition;
	}

	public int compareTo(Object o) {
		HierarchyFlowLayoutOrderNode y = (HierarchyFlowLayoutOrderNode)o;
		int diff = y.componentSize - componentSize;
		if (diff != 0) return diff;
		diff = componentNumber - y.componentNumber;
		if (diff != 0) return diff;
		diff = y.layer - layer;
		if (diff != 0) return diff;
		return horizontalPosition - y.horizontalPosition;
	}
};

/**
 * Lays out graph in tree-like pattern.
 * The layout will approximate the optimal orientation
 * for nodes which have a tree-like relationship. <strong> This
 * assumed relationship is based on directed edges. This class does
 * not currently distinguish or gracefully treat undirected edges.
 * Also, duplicate edges are ignored for the purpose of positioning
 * nodes in the layout.
 * </strong>
 * <br>The major steps in this algorithm are:
 * <ol>
 * <li>Choose the set of nodes to be layed out based on which are selected</li>
 * <li>Partition this set into connected components</li>
 * <li>Detect and eliminate (temporarily) graph cycles</li>
 * <li>Eliminate (temporarily) transitive edges</li>
 * <li>Assign nodes to layers (parents always in layer above any child's layer)</li>
 * <li>Choose a within-layer ordering which reduces edge crossings between layers</li>
 * <li>Select horizontal positions for nodes within a layer to minimize edge length</li>
 * <li>Assemble layed out compoents and any unselected nodes into a composite layout</li>
 * </ol>
 * Steps 2 through 6 are performed by calls to methods in the class
 * {@link csplugins.hierarchicallayout.Graph}
*/
public class HierarchicalLayoutListener implements ActionListener, Task {
	private TaskMonitor taskMonitor = null;
	private boolean interrupted = false;

	/**
	 * Lays out the graph. See this class' description for an outline
	 * of the method used. <br>
	 * For the last step, assembly of the layed out components, the method
	 * implemented is similar to the FlowLayout layout manager from the AWT.
	 * Space is allocated in horizontal bands, with a new band begun beneath
	 * the higher bands. This happens on two scales: on the component scale,
	 * and on the intra-component scale. Within each component, the layers
	 * are placed horizontally, each within its own band. Globally, each
	 * component appears in a band which is filled until a right margin is
	 * hit. After that a new band is started beneath the higher band of
	 * layed out components. Components are never split between these global
	 * bands. Each component is finished, regardless of its horizontal
	 * extent. <br>
	 * Also, a post placement pass is done on each component to move each
	 * layer horizontally in order to line up the centers of the layers with
	 * the center of the component.
	 * @param event Menu Selection Event.
	 */
	public void actionPerformed(ActionEvent event) {
		JTaskConfig config = new JTaskConfig();
		config.setOwner(Cytoscape.getDesktop());
		config.displayCancelButton(true);
		config.displayStatus(true);
		config.displayTimeElapsed(true);
		TaskManager.executeTask(new HierarchicalLayoutListener(), config);
	}

	public void run() {
		if (taskMonitor == null) {
			throw new IllegalStateException("Task Monitor is not set.");
		}
	try {
		taskMonitor.setPercentCompleted(0);
		taskMonitor.setStatus("capturing snapshot of network and selected nodes");
		Thread.yield();
		if (interrupted) return;
		//get the graph view object from the window.
		CyNetworkView networkView = Cytoscape.getCurrentNetworkView();
		//get the network object; this contains the graph
		CyNetwork network = Cytoscape.getCurrentNetwork();
		//can't continue if either of these is null
		if (networkView == null || network == null) {
			return;
		}
		//and the view should be a view on this structure
		if (networkView.getNetwork() != network) {
			System.err.println("In HierarchicalLayoutListenr.run: ");
			System.err.println("Current CyNetworkView is not a view on the current CyNetwork.");
			return;
		}
		//Select all nodes as the default action if none are selected
		if(network.getNodeCount() <= 0) {
			return;
		}
		/* construct node list with selected nodes first */
		List selectedNodes = networkView.getSelectedNodes();
		final int numSelectedNodes = selectedNodes.size();
		final int numNodes = networkView.getNodeViewCount();
		// correct for edge case where there is only one node
		if (numSelectedNodes == 1)
		{
			return;
		}
				
		final int numLayoutNodes = (numSelectedNodes <= 1) ? numNodes : numSelectedNodes;

		NodeView nodeView[] = new NodeView[numNodes];
		int nextNode = 0;
		HashMap<Object, Object> ginyIndex2Index = new HashMap<Object, Object>(numNodes*2);
		if (numSelectedNodes > 1) {
			Iterator iter = selectedNodes.iterator();
			while (iter.hasNext()) {
				nodeView[nextNode] = (NodeView)(iter.next());
				ginyIndex2Index.put(new Integer(nodeView[nextNode].getNode().getRootGraphIndex()), new Integer(nextNode));
				nextNode++;
			}
		}
		Iterator iter = networkView.getNodeViewsIterator() ; /* all nodes */
		while (iter.hasNext()) {
			NodeView nv = (NodeView)(iter.next());
			Integer nodeIndexKey = new Integer(nv.getNode().getRootGraphIndex());
			if (!ginyIndex2Index.containsKey(nodeIndexKey)) {
				nodeView[nextNode] = nv;
				ginyIndex2Index.put(nodeIndexKey, new Integer(nextNode));
				nextNode++;
			}
		}
		/* create edge list from edges between selected nodes */
		LinkedList<Object> edges = new LinkedList<Object>();
		iter = networkView.getEdgeViewsIterator();
		while (iter.hasNext()) {
			EdgeView ev = (EdgeView)(iter.next());
			Integer edgeFrom = (Integer)ginyIndex2Index.get(new Integer(ev.getEdge().getSource().getRootGraphIndex()));
			Integer edgeTo = (Integer)ginyIndex2Index.get(new Integer(ev.getEdge().getTarget().getRootGraphIndex()));
			if (edgeFrom == null || edgeTo == null) {
			System.err.println("In HierarchicalLayoutListener.run: ");
				System.err.println("Error in internal edge representation.");
				return;
			}
			if (numSelectedNodes <= 1
					|| (edgeFrom.intValue() < numSelectedNodes
					&& edgeTo.intValue() < numSelectedNodes)) {
				/* add edge to graph */
				edges.add(new Edge(edgeFrom.intValue(),edgeTo.intValue()));
			}
		}
		/* find horizontal and vertical coordinates of each node */
		Edge edge[] = new Edge[edges.size()];
		edges.toArray(edge);
		Graph graph = new Graph(numLayoutNodes,edge);

		int cI[] = graph.componentIndex();
		int x;

		taskMonitor.setPercentCompleted(10);
		taskMonitor.setStatus("finding connected components");
		Thread.yield();
		if (interrupted) return;
		int renumber[] = new int[cI.length];
		Graph component[] = graph.partition(cI,renumber);
		final int numComponents = component.length;
		int layer[][] = new int[numComponents][];
		int horizontalPosition[][] = new int[numComponents][];
		for (x=0; x<component.length; x++) {
			taskMonitor.setPercentCompleted(20 + 60 * (x * 3) / numComponents / 3);
			taskMonitor.setStatus("making acyclic transitive reduction");
			Thread.yield();
			if (interrupted) return;
			Graph reduced = component[x].getReducedGraph();
			taskMonitor.setPercentCompleted(20 + 60 * (x * 3 + 1) / numComponents / 3);
			taskMonitor.setStatus("layering nodes vertically");
			Thread.yield();
			if (interrupted) return;
			layer[x] = reduced.getVertexLayers();

			taskMonitor.setPercentCompleted(20 + 60 * (x * 3 + 2) / numComponents / 3);
			taskMonitor.setStatus("positioning nodes within layer");
			Thread.yield();
			if (interrupted) return;
			horizontalPosition[x] = reduced.getHorizontalPosition(layer[x]);
		}
		taskMonitor.setPercentCompleted(80);
		taskMonitor.setStatus("repositioning nodes in view");
		Thread.yield();
		if (interrupted) return;
		/* order nodeviews by layout order */
		HierarchyFlowLayoutOrderNode flowLayoutOrder[] = new HierarchyFlowLayoutOrderNode[numLayoutNodes];
		for (x=0; x<numLayoutNodes; x++) {
			flowLayoutOrder[x] = new HierarchyFlowLayoutOrderNode(
				nodeView[x], cI[x], component[cI[x]].getNodecount(),
				layer[cI[x]][renumber[x]], horizontalPosition[cI[x]][renumber[x]]);
		}
		Arrays.sort(flowLayoutOrder);
		final int nodeHorizontalSpacing = 64;
		final int nodeVerticalSpacing = 32;
		final int componentSpacing = 64;
		final int bandGap = 64;
		final int leftEdge = 32;
		final int topEdge = 32;
		final int rightMargin = 1000;
		int lastComponent = -1;
		int lastLayer = -1;
		int startBandY = topEdge;
		int cleanBandY = topEdge;
		int startComponentX = leftEdge;
		int cleanComponentX = leftEdge;
		int startLayerY = topEdge;
		int cleanLayerY = topEdge;
		int cleanLayerX = leftEdge;
		int layerCenter[] = new int[numLayoutNodes+1];
		int maxLayerCenter = -1;
		/* layout nodes which are selected */
		int nodeIndex;
		for (nodeIndex=0; nodeIndex<numLayoutNodes; nodeIndex++) {
			HierarchyFlowLayoutOrderNode node = flowLayoutOrder[nodeIndex];
			int currentComponent = node.componentNumber;
			int currentLayer = node.layer;
			NodeView currentView = node.nodeView;
			if (lastComponent == -1) {
				/* this is the first component */
				lastComponent = currentComponent;
				lastLayer = currentLayer;
				layerCenter[currentLayer] = -1;
				maxLayerCenter = -1;
			}
			if (lastComponent != currentComponent) {
				/* new component */
				layerCenter[lastLayer] = (startComponentX + cleanLayerX - nodeHorizontalSpacing) / 2;
				if (layerCenter[lastLayer] > maxLayerCenter) maxLayerCenter = layerCenter[lastLayer];
				/* adjust centers of last component */
				int backIndex;
				for (backIndex=nodeIndex - 1;  backIndex >= 0; backIndex--) {
					HierarchyFlowLayoutOrderNode backNode = flowLayoutOrder[backIndex];
					int backComponent = backNode.componentNumber;
					if (backComponent != lastComponent) {
						break;
					}
					int backLayer = backNode.layer;
					backNode.setXPos(backNode.getXPos() + maxLayerCenter - layerCenter[backLayer]);
				}
				/* initialize for new component */
				startComponentX = cleanComponentX + componentSpacing;
				if (startComponentX > rightMargin) {
					/* new band */
					startBandY = cleanBandY + bandGap;
					cleanBandY = startBandY;
					startComponentX = leftEdge;
					cleanComponentX = leftEdge;
				}
				startLayerY = startBandY;
				cleanLayerY = startLayerY;
				cleanLayerX = startComponentX;
				layerCenter[currentLayer] = -1;
				maxLayerCenter = -1;
			} else if (lastLayer != currentLayer) {
				/* new layer */
				layerCenter[lastLayer] = (startComponentX + cleanLayerX - nodeHorizontalSpacing) / 2;
				if (layerCenter[lastLayer] > maxLayerCenter) maxLayerCenter = layerCenter[lastLayer];
				startLayerY = cleanLayerY + nodeVerticalSpacing;
				cleanLayerY = startLayerY;
				cleanLayerX = startComponentX;
				layerCenter[currentLayer] = -1;
			}
			node.setXPos(cleanLayerX);
			node.setYPos(startLayerY);
			cleanLayerX += nodeHorizontalSpacing;
			int currentBottom = startLayerY + (int)(currentView.getHeight());
			int currentRight = cleanLayerX + (int)(currentView.getWidth());
			if (currentBottom > cleanBandY) cleanBandY = currentBottom;
			if (currentRight > cleanComponentX) cleanComponentX = currentRight;
			if (currentBottom > cleanLayerY) cleanLayerY = currentBottom;
			if (currentRight > cleanLayerX) cleanLayerX = currentRight;
			lastComponent = currentComponent;
			lastLayer = currentLayer;
			if (nodeIndex == numLayoutNodes - 1) {
				/* this is the last node of the last component */
				layerCenter[currentLayer] = (startComponentX + cleanLayerX - nodeHorizontalSpacing) / 2;
				if (layerCenter[currentLayer] > maxLayerCenter) maxLayerCenter = layerCenter[currentLayer];
				/* adjust centers of this component */
				int backIndex;
				for (backIndex=nodeIndex;  backIndex >= 0; backIndex--) {
					HierarchyFlowLayoutOrderNode backNode = flowLayoutOrder[backIndex];
					int backComponent = backNode.componentNumber;
					if (backComponent != currentComponent) {
						break;
					}
					int backLayer = backNode.layer;
					backNode.setXPos(backNode.getXPos() + maxLayerCenter - layerCenter[backLayer]);
				}
			}
		}

                /* Delete edge anchors */
                iter = networkView.getEdgeViewsIterator();
                while (iter.hasNext()) {
                  ((EdgeView) iter.next()).getBend().removeAllHandles();
                } /* Done removing edge anchors */

		for (nodeIndex=0; nodeIndex<numLayoutNodes; nodeIndex++) {
			HierarchyFlowLayoutOrderNode node = flowLayoutOrder[nodeIndex];
			NodeView currentView = node.nodeView;
			currentView.setOffset(node.getXPos(),node.getYPos());
		}
		/* layout any other nodes */
		if (numNodes > numLayoutNodes) {
			int highestY = Integer.MAX_VALUE;
			for (x=numLayoutNodes; x<numNodes; x++) {
				int nodeY = (int)(nodeView[x].getYPosition());
				if (nodeY < highestY) highestY = nodeY;
			}
			int shiftY = cleanBandY + bandGap - highestY; 
			
			// try not shifting the Y-positions
			shiftY = 0;
			
			for (x=numLayoutNodes; x<numNodes; x++) {
				nodeView[x].setYPosition(nodeView[x].getYPosition() + shiftY,true);
			}
		}
		
		taskMonitor.setPercentCompleted(100);
		taskMonitor.setStatus("hierarchical layout complete");
	} catch (Throwable e) {
		taskMonitor.setException(e, "Layout aborted due to unexpected condition");
	}
	}

	/**
	* Non-blocking call to interrupt the task.
	*/
	public void halt() {
		interrupted = true;
	}

	/**
	* Sets the Task Monitor.
	*
	* @param taskMonitor TaskMonitor Object.
	*/
	public void setTaskMonitor(TaskMonitor tm) {
		if (taskMonitor != null) {
			throw new IllegalStateException("Task Monitor is already set.");
		}
		taskMonitor = tm;
	}

	/**
	* Gets the Task Title.
	*
	* @return human readable task title.
	*/
	public String getTitle() {
		return new String("Hierarchical Layout");
	}
}
