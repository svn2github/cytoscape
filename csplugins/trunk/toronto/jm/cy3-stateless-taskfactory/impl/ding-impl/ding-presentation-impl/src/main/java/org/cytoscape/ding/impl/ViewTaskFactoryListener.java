package org.cytoscape.ding.impl;

import java.util.HashMap;
import java.util.Map;

import org.cytoscape.dnd.DropNetworkViewTaskFactory;
import org.cytoscape.dnd.DropNodeViewTaskFactory;
import org.cytoscape.task.EdgeViewTaskFactory;
import org.cytoscape.task.NetworkViewTaskFactory;
import org.cytoscape.task.NodeViewTaskFactory;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.work.TaskContextManager;

public class ViewTaskFactoryListener {

	final Map<CyNetworkView, DGraphView> viewMap;
	final Map<NodeViewTaskFactory, Map> nodeViewTFs;
	final Map<EdgeViewTaskFactory, Map> edgeViewTFs;
	final Map<NetworkViewTaskFactory, Map> emptySpaceTFs;
	final Map<DropNodeViewTaskFactory, Map> dropNodeViewTFs;
	final Map<DropNetworkViewTaskFactory, Map> dropEmptySpaceTFs;
	
	private final TaskContextManager contextManager;


	public ViewTaskFactoryListener(TaskContextManager contextManager){
		viewMap = new HashMap<CyNetworkView, DGraphView>();
		nodeViewTFs = new HashMap<NodeViewTaskFactory, Map>();
		edgeViewTFs = new HashMap<EdgeViewTaskFactory, Map>();
		emptySpaceTFs = new HashMap<NetworkViewTaskFactory, Map>();
		dropNodeViewTFs = new HashMap<DropNodeViewTaskFactory, Map>();
		dropEmptySpaceTFs = new HashMap<DropNetworkViewTaskFactory, Map>();
		this.contextManager = contextManager;
	}

	
	public void addNodeViewTaskFactory(NodeViewTaskFactory nvtf, Map props) {
		if (nvtf == null)
			return;

		nodeViewTFs.put(nvtf, props);
		contextManager.registerTaskFactory(nvtf);
	}

	public void removeNodeViewTaskFactory(NodeViewTaskFactory nvtf, Map props) {
		if (nvtf == null)
			return;

		nodeViewTFs.remove(nvtf);
		contextManager.unregisterTaskFactory(nvtf);
	}

	public void addEdgeViewTaskFactory(EdgeViewTaskFactory evtf, Map props) {
		if (evtf == null)
			return;

		edgeViewTFs.put(evtf, props);
		contextManager.registerTaskFactory(evtf);
	}

	public void removeEdgeViewTaskFactory(EdgeViewTaskFactory evtf, Map props) {
		if (evtf == null)
			return;

		edgeViewTFs.remove(evtf);
		contextManager.unregisterTaskFactory(evtf);
	}

	public void addNetworkViewTaskFactory(NetworkViewTaskFactory evtf, Map props) {
		if (evtf == null)
			return;

		emptySpaceTFs.put(evtf, props);
		contextManager.registerTaskFactory(evtf);
	}

	public void removeNetworkViewTaskFactory(NetworkViewTaskFactory evtf,
			Map props) {
		if (evtf == null)
			return;

		emptySpaceTFs.remove(evtf);
		contextManager.unregisterTaskFactory(evtf);
	}

	public void addDropNetworkViewTaskFactory(DropNetworkViewTaskFactory evtf,
			Map props) {
		if (evtf == null)
			return;

		dropEmptySpaceTFs.put(evtf, props);
		contextManager.registerTaskFactory(evtf);
	}

	public void removeDropNetworkViewTaskFactory(
			DropNetworkViewTaskFactory evtf, Map props) {
		if (evtf == null)
			return;

		dropEmptySpaceTFs.remove(evtf);
		contextManager.unregisterTaskFactory(evtf);
	}

	public void addDropNodeViewTaskFactory(DropNodeViewTaskFactory nvtf, Map props) {
		if (nvtf == null)
			return;

		dropNodeViewTFs.put(nvtf, props);
		contextManager.registerTaskFactory(nvtf);
	}

	public void removeDropNodeViewTaskFactory(DropNodeViewTaskFactory nvtf, Map props) {
		if (nvtf == null)
			return;

		dropNodeViewTFs.remove(nvtf);
		contextManager.unregisterTaskFactory(nvtf);
	}

}
