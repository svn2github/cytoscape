/**
 * 
 */
package org.systemsbiology.cytoscape.task;

import giny.model.Edge;
import giny.model.Node;

import java.util.ArrayList;
import java.util.Collection;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.logger.CyLogger;
import cytoscape.data.Semantics;

import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;
import cytoscape.task.ui.JTask;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.util.TaskManager;

import org.systemsbiology.cytoscape.NetworkObject;
import org.systemsbiology.cytoscape.Attributes;

import org.systemsbiology.gaggle.core.datatypes.Interaction;
import org.systemsbiology.gaggle.core.datatypes.Network;

/**
 * @author skillcoy
 * 
 */
public class HandleNetworkTask implements Task
	{
  private static CyLogger logger = CyLogger.getLogger(HandleNetworkTask.class);

  private TaskMonitor taskMonitor;
	private Network gaggleNetwork;
	private CyNetwork cyNetwork;
	private String bSource;
	private boolean create;
	
	public HandleNetworkTask(String broadcastSource, Network gNetwork,
			CyNetwork cyNet, boolean createNetwork)
		{
		this.bSource = broadcastSource;
		this.gaggleNetwork = gNetwork;
		this.cyNetwork = cyNet;
		this.create = createNetwork;
		}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cytoscape.task.Task#getTitle()
	 */
	public String getTitle()
		{
		return "Creating Network...";
		}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cytoscape.task.Task#halt()
	 */
	public void halt()
		{
		logger.info("Halt " + this.getClass().getName() + " not yet implemented");
		}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cytoscape.task.Task#run()
	 */
	public void run()
		{
		logger.debug("======> Creating network in " + this.getClass().getName());
		if (taskMonitor == null)  
			throw new IllegalStateException("Task Monitor is not set.");
		
		taskMonitor.setStatus("Creating new network from " + this.bSource+ " broadcast...");
		taskMonitor.setPercentCompleted(-1);

		Collection<Node> srcCollection = new ArrayList<Node>();
		Collection<Node> targetCollection = new ArrayList<Node>();
		Collection<Edge> edgeCollection = new ArrayList<Edge>();

		double percent = 0.0d;
		int maxCount = this.gaggleNetwork.getNodes().length + this.gaggleNetwork.getInteractions().length;
		int progressCount = 0;

		int i = 0;
		while (maxCount > i)
			{
			percent = ((double) progressCount / maxCount) * 100.0;

			for (String NodeName : this.gaggleNetwork.getNodes())
				{
				Node NewNode = (Node) Cytoscape.getCyNode(NodeName, create);
				this.cyNetwork.addNode(NewNode);
				this.cyNetwork.setSelectedNodeState(NewNode, true);
				i++;
				}

			Attributes.addAttributes(this.gaggleNetwork, NetworkObject.NODE);
			for (Interaction CurrentInteraction : this.gaggleNetwork.getInteractions())
				{
				String srcNodeName = CurrentInteraction.getSource();
				String targetNodeName = CurrentInteraction.getTarget();
				String interactionType = CurrentInteraction.getType();
				// flag source node (create new node if it doesn't exist)
				Node srcNode = (Node) Cytoscape.getCyNode(srcNodeName, true);
				this.cyNetwork.addNode(srcNode);
				srcCollection.add(srcNode);
				// flag target node (create new node if it doesn't exist)
				Node targetNode = (Node) Cytoscape.getCyNode(targetNodeName, true);
				this.cyNetwork.addNode(targetNode);
				targetCollection.add(targetNode);
				// flag edge (create a new edge if it's not found)
				Edge selectEdge = (Edge) Cytoscape.getCyEdge(srcNode, targetNode, Semantics.INTERACTION, interactionType, create);
				// add newly created edge to current network
				if (!this.cyNetwork.containsEdge(selectEdge))
					this.cyNetwork.addEdge(selectEdge);
				edgeCollection.add(selectEdge);
				i++;
				}
			Attributes.addAttributes(this.gaggleNetwork, NetworkObject.EDGE);

			JTask jTask = (JTask) taskMonitor;
			if (jTask.haltRequested())
				{ // abort
				// TODO WHAT DO I DO TO ABORT THIS??
				taskMonitor.setStatus("Canceling the download ...");
				taskMonitor.setPercentCompleted(100);
				break;
				}

			taskMonitor.setPercentCompleted((int) percent);
			}
		// flag all selected nodes & edges
		this.cyNetwork.setSelectedNodeState(srcCollection, true);
		this.cyNetwork.setSelectedNodeState(targetCollection, true);
		this.cyNetwork.setSelectedEdgeState(edgeCollection, true);
		}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see cytoscape.task.Task#setTaskMonitor(cytoscape.task.TaskMonitor)
	 */
	public void setTaskMonitor(TaskMonitor arg0)
			throws IllegalThreadStateException
		{
		this.taskMonitor = arg0;
		}

	/*
	 * --- create the tasks and task monitors to show the user what's going on
	 * during download/install ---
	 */

	public static void createHandleNetworkTask(String broadcastSource,
			Network gNet, CyNetwork cNet, boolean createNet)
		{
		// Create Task
		HandleNetworkTask task = new HandleNetworkTask(broadcastSource, gNet, cNet, createNet);

		// Configure JTask Dialog Pop-Up Box
		JTaskConfig jTaskConfig = new JTaskConfig();
		jTaskConfig.setOwner(Cytoscape.getDesktop());
		jTaskConfig.displayCloseButton(false);
		jTaskConfig.displayStatus(true);
		jTaskConfig.setAutoDispose(true);
		jTaskConfig.displayCancelButton(true);
		// Execute Task in New Thread; pop open JTask Dialog Box.
		TaskManager.executeTask(task, jTaskConfig);
		}

	}
