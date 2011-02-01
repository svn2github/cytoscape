package org.cytoscape.view.layout;


import java.util.Set;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;

import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;

import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class LayoutTask extends AbstractTask {
	
	protected static final Logger logger = LoggerFactory.getLogger(LayoutTask.class);
	
	protected static final String LAYOUT_ALGORITHM = "layoutAlgorithm";

	protected final CyNetworkView networkView;
	private final String name;
	protected final boolean selectedOnly;
	protected final Set<View<CyNode>> staticNodes;

	protected boolean cancelled = false;

	public LayoutTask(final CyNetworkView networkView, final String name, final boolean selectedOnly,
			  final Set<View<CyNode>> staticNodes)
	{
		super();

		this.networkView = networkView;
		this.name = name;
		this.selectedOnly = selectedOnly;
		this.staticNodes = staticNodes;
	}

	@Override
	public final void run(final TaskMonitor taskMonitor)  {
		final long start = System.currentTimeMillis();
		logger.debug("Layout Start: " + name);

		// do some sanity checking
		if (networkView == null)
			return;

		final CyNetwork network = networkView.getModel();
		if (network.getNodeCount() <= 0)
			return;

		// this is overridden by children and does the actual layout
		doLayout(taskMonitor, network);

		// Fit Content method always redraw the presentation.
		networkView.fitContent();

		// update the __layoutAlgorithm attribute
		final CyRow networkAttributes = network.getCyRow(CyNetwork.HIDDEN_ATTRS);
		final CyTable netAttrsTable = networkAttributes.getTable();
		if (netAttrsTable.getColumn(LAYOUT_ALGORITHM) == null)
			netAttrsTable.createColumn(LAYOUT_ALGORITHM, String.class, true);
		networkAttributes.set(LAYOUT_ALGORITHM, name);

		logger.debug("Layout finished: " + (System.currentTimeMillis()-start) + " msec.");
	}

	/**
	 * Lock these nodes (i.e. prevent them from moving).
	 *
	 * @param nodes An array of View<CyNode>'s to lock
	 */
	public void lockNodes(View<CyNode>[] nodes) {
		for (int i = 0; i < nodes.length; ++i) {
			staticNodes.add(nodes[i]);
		}
	}

	/**
	 * Lock this node (i.e. prevent it from moving).
	 *
	 * @param v A View<CyNode> to lock
	 */
	public void lockNode(View<CyNode> v) {
		staticNodes.add(v);
	}

	/**
	 * Unlock this node
	 *
	 * @param v A View<CyNode> to unlock
	 */
	public void unlockNode(View<CyNode> v) {
		staticNodes.remove(v);
	}

	protected boolean isLocked(View<CyNode> v) {
		return (staticNodes.contains(v));
	}

	/**
	 * Unlock all nodes
	 */
	public void unlockAllNodes() {
		staticNodes.clear();
	}

	protected abstract void doLayout(final TaskMonitor taskMonitor, final CyNetwork network);
}