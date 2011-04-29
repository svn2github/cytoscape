package csplugins.layout.algorithms.graphPartition;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTableManager;
import org.cytoscape.view.layout.AbstractGraphPartition;
import org.cytoscape.view.layout.AbstractLayout;
import org.cytoscape.view.layout.LayoutNode;
import org.cytoscape.view.layout.LayoutPartition;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.TunableValidator;
import org.cytoscape.work.undo.UndoSupport;


public class DegreeSortedCircleLayout extends AbstractLayout implements TunableValidator {
	private static final String DEGREE_ATTR_NAME = "degree";
	private CyTableManager tableMgr;

	@Tunable(description="Don't partition graph before layout", groups="Standard settings")
	public boolean singlePartition;

	/**
	 * Creates a new DegreeSortedCircleLayout object.
	 */
	public DegreeSortedCircleLayout(UndoSupport undoSupport, CyTableManager tableMgr) {
		super(undoSupport, "degree-circle", "Degree Sorted Circle Layout");
		this.tableMgr = tableMgr;
	}

	// TODO
	public boolean tunablesAreValid(final Appendable errMsg) {
		return true;
	}

	public TaskIterator getTaskIterator() {
		return new TaskIterator(
			new DegreeSortedCircleLayoutTask(networkView, getName(), selectedOnly,
							 staticNodes, DEGREE_ATTR_NAME,
							 tableMgr, singlePartition));
	}
}
