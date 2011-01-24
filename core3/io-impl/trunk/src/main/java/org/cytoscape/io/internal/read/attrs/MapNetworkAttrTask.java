package org.cytoscape.io.internal.read.attrs;


import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.CyTableEntry;
import org.cytoscape.session.CyApplicationManager;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;


public final class MapNetworkAttrTask extends AbstractTask {
	@Tunable(description="Map to current network only")
	public boolean currentNetworkOnly = true;

	private final CyTableEntry type; // Must be node or edge!
	private final CyTable newGlobalTable;
	private final CyNetworkManager networkManager;
	final private CyApplicationManager applicationManager;

	public MapNetworkAttrTask(final CyTableEntry type, final CyTable newGlobalTable,
				  final CyNetworkManager networkManager,
				  final CyApplicationManager applicationManager)
	{
		this.type               = type;
		this.newGlobalTable     = newGlobalTable;
		this.networkManager     = networkManager;
		this.applicationManager = applicationManager;

		if (!(type instanceof CyNode) && !(type instanceof CyEdge))
			throw new IllegalArgumentException("\"type\" must be CyNode.class or CyEdge.class!");
	}

	public void run(final TaskMonitor taskMonitor) throws Exception {
		taskMonitor.setTitle("Mapping virtual columns");

		final List<CyTable> targetTables = new ArrayList<CyTable>();
		if (currentNetworkOnly) {
			final CyNetwork currentNetwork = applicationManager.getCurrentNetwork();
			targetTables.add(type instanceof CyNode ? currentNetwork.getDefaultNodeTable()
					                        : currentNetwork.getDefaultEdgeTable());
		} else {
			final Set<CyNetwork> networks = networkManager.getNetworkSet();
			for (final CyNetwork network : networks)
				targetTables.add(type instanceof CyNode ? network.getDefaultNodeTable()
						                        : network.getDefaultEdgeTable());
		}

		mapAll(targetTables);
	}

	private void mapAll(final List<CyTable> targetTables) {
		if (targetTables.isEmpty())
			return;

		if (newGlobalTable.getPrimaryKeyType() != String.class)
			throw new IllegalStateException("The new table's primary key is not of type String!");
		final String sourceTableJoinColumn = newGlobalTable.getPrimaryKey();

		for (final CyTable targetTable : targetTables) {
			if (cancelled)
				return;
			targetTable.addVirtualColumns(newGlobalTable, sourceTableJoinColumn, CyTableEntry.NAME);
		}
	}
}