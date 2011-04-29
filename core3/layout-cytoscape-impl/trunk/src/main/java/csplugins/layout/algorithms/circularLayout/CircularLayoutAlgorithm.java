package csplugins.layout.algorithms.circularLayout;


import java.util.HashMap;
import java.util.LinkedList;

import org.cytoscape.model.CyNode;
import org.cytoscape.view.layout.AbstractLayout;
import org.cytoscape.view.model.View;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.TunableValidator;
import org.cytoscape.work.undo.UndoSupport;


public class CircularLayoutAlgorithm extends AbstractLayout implements TunableValidator{
	@Tunable(description="Horizontal spacing between nodes")
	public int nodeHorizontalSpacing = 64;
	@Tunable(description="Vertical spacing between nodes")
	public int nodeVerticalSpacing = 32;
	@Tunable(description="Left edge margin")
	public int leftEdge = 32;
	@Tunable(description="Top edge margin")
	public int topEdge = 32;
	@Tunable(description="Right edge margin")
	public int rightMargin = 1000;
        @Tunable(description="Don't partition graph before layout", groups="Standard settings")
	public boolean singlePartition;

	/**
	 * Creates a new Layout object.
	 */
	public CircularLayoutAlgorithm(UndoSupport un) {
		super(un, "circular", "Circular Layout");
	}

	//TODO how to validate these values?
	public boolean tunablesAreValid(final Appendable errMsg) {
		return true;
	}

	public TaskIterator getTaskIterator() {
		return new TaskIterator(
			new CircularLayoutAlgorithmTask(networkView, getName(), selectedOnly,
							staticNodes, nodeHorizontalSpacing,
							nodeVerticalSpacing, leftEdge, topEdge,
							rightMargin, singlePartition));
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public boolean supportsSelectedOnly() {
		return false;
	}
}
