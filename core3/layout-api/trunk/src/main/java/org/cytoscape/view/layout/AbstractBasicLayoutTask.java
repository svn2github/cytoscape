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

/**
 *  This is a basic implementation of a LayoutAlgorithm Task that does
 *  some bookkeeping, but primarily delegates to the doLayout() method.
 *  Extensions of this class are meant to operate on the CyNetworkView 
 *  provided to the constructor (and is available as a protected member 
 *  variable).
 */
public abstract class AbstractBasicLayoutTask extends AbstractTask {
	
	private static final Logger logger = LoggerFactory.getLogger(AbstractBasicLayoutTask.class);
	protected static final String LAYOUT_ALGORITHM = "layoutAlgorithm";

	protected final CyNetworkView networkView;
	protected final boolean selectedOnly;
	protected final Set<View<CyNode>> staticNodes;

	private final String name;

	public AbstractBasicLayoutTask(final CyNetworkView networkView, final String name, boolean selectedOnly,
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
		doLayout(taskMonitor);

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


	protected boolean isLocked(View<CyNode> v) {
		return ((staticNodes != null) && (staticNodes.contains(v)));
	}

	protected abstract void doLayout(final TaskMonitor taskMonitor);
}
