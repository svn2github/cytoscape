package org.cytoscape.task.internal.edit;

import java.util.List;

import org.cytoscape.event.CyEventHelper;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyEdge.Type;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.CyTableEntry;
import org.cytoscape.model.CyTableUtil;
import org.cytoscape.model.events.RowsAboutToChangeEvent;
import org.cytoscape.model.events.RowsFinishedChangingEvent;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;

public class ConnectSelectedNodesTask extends AbstractTask {

	// TODO: is it sufficient to create undirected edge only?
	private static final String INTERACTION = "undirected";

	private final CyNetwork network;
	private final CyEventHelper eventHelper;
	
	public ConnectSelectedNodesTask(final CyNetwork network, final CyEventHelper eventHelper) {
		if (network == null)
			throw new NullPointerException("Network is null.");
		this.network = network;
		this.eventHelper = eventHelper;
	}

	@Override
	public void run(TaskMonitor taskMonitor) throws Exception {

		final List<CyNode> selectedNodes = CyTableUtil.getNodesInState(network, CyNetwork.SELECTED, true);

		final CyTable nodeTable = network.getDefaultNodeTable();
		final CyTable edgeTable = network.getDefaultEdgeTable();
		try {
			// Generate bundled event to avoid too many events problem.
			eventHelper.fireSynchronousEvent(new RowsAboutToChangeEvent(this, nodeTable));
			eventHelper.fireSynchronousEvent(new RowsAboutToChangeEvent(this, edgeTable));
			
			for (CyNode source : selectedNodes) {
				for (CyNode target : selectedNodes) {
					if (source != target) {
						final List<CyNode> sourceNeighborList = network.getNeighborList(source, Type.ANY);
						if (sourceNeighborList.contains(target) == false) {
							// connect it
							final CyEdge newEdge = network.addEdge(source, target, false);
							newEdge.getCyRow().set(
									CyTableEntry.NAME,
									source.getCyRow().get(CyTableEntry.NAME, String.class) + " (" + INTERACTION + ") "
											+ target.getCyRow().get(CyTableEntry.NAME, String.class));
							newEdge.getCyRow().set(CyEdge.INTERACTION, INTERACTION);

						}
					}
				}
			}
		} finally {
			eventHelper.fireSynchronousEvent(new RowsFinishedChangingEvent(this, nodeTable));
			eventHelper.fireSynchronousEvent(new RowsFinishedChangingEvent(this, edgeTable));
		}
	}

}
