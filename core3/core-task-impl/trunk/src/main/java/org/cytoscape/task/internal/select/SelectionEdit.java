package org.cytoscape.task.internal.select;


import java.util.Collection;

import org.cytoscape.event.CyEventHelper;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.util.swing.CyAbstractEdit;


/** An undoable edit that will undo and redo selection of nodes and edges. */
final class SelectionEdit extends CyAbstractEdit {
	public static enum SelectionFilter {
		NODES_ONLY, EDGES_ONLY, NODES_AND_EDGES;
	}

	private final CyEventHelper eventHelper;
	private final CyNetwork network;
	private final CyNetworkView view;
	private final SelectionFilter filter;
	private Collection<CyRow> nodeRows;
	private Collection<CyRow> edgeRows;

	SelectionEdit(final CyEventHelper eventHelper, final String description,
	              final CyNetwork network, final CyNetworkView view,
	              final SelectionFilter filter)
	{
		super(description);

		this.eventHelper = eventHelper;
		this.network     = network;
		this.view        = view;
		this.filter      = filter;

		saveSelectionState();
	}

	public void redo() {
		super.redo();

		saveSelectionState();
		restoreSelectionState();

		eventHelper.flushPayloadEvents();
		view.updateView();
	}

	public void undo() {
		super.undo();

		restoreSelectionState();
		saveSelectionState();

		eventHelper.flushPayloadEvents();
		view.updateView();
	}

	private void restoreSelectionState() {
		if (filter == SelectionFilter.NODES_ONLY || filter == SelectionFilter.NODES_AND_EDGES) {
			for (final CyRow row : nodeRows)
				row.set(CyNetwork.SELECTED, Boolean.valueOf(true));
		}

		if (filter == SelectionFilter.EDGES_ONLY || filter == SelectionFilter.NODES_AND_EDGES) {
			for (final CyRow row : edgeRows)
				row.set(CyNetwork.SELECTED, Boolean.valueOf(true));
		}
	}

	private void saveSelectionState() {
		if (filter == SelectionFilter.NODES_ONLY || filter == SelectionFilter.NODES_AND_EDGES) {
			final CyTable nodeTable = network.getDefaultNodeTable();
			nodeRows = nodeTable.getMatchingRows(CyNetwork.SELECTED, Boolean.valueOf(true));
		}

		if (filter == SelectionFilter.EDGES_ONLY || filter == SelectionFilter.NODES_AND_EDGES) {
			final CyTable edgeTable = network.getDefaultEdgeTable();
			edgeRows = edgeTable.getMatchingRows(CyNetwork.SELECTED, Boolean.valueOf(true));
		}
	}
}
