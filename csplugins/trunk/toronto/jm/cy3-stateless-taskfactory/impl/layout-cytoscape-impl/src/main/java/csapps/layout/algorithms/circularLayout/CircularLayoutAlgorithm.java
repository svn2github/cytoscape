package csapps.layout.algorithms.circularLayout;


import org.cytoscape.view.layout.AbstractLayoutAlgorithm;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.undo.UndoSupport;


public class CircularLayoutAlgorithm extends AbstractLayoutAlgorithm<CircularLayoutContext> {
	/**
	 * Creates a new Layout object.
	 */
	public CircularLayoutAlgorithm(UndoSupport un) {
		super(un, "circular", "Circular Layout", false);
	}

	@Override
	public CircularLayoutContext createTaskContext() {
		return new CircularLayoutContext(supportsSelectedOnly(), supportsNodeAttributes(), supportsEdgeAttributes());
	}
	
	@Override
	public TaskIterator createTaskIterator(CircularLayoutContext context) {
		return new TaskIterator(
				new CircularLayoutAlgorithmTask(
						context.getNetworkView(), getName(), context.getSelectedOnly(), context.singlePartition, context.getStaticNodes()));
	}
}
