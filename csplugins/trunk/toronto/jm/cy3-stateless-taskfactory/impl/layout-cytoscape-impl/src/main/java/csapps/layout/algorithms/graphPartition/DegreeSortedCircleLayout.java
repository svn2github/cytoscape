package csapps.layout.algorithms.graphPartition;


import org.cytoscape.view.layout.AbstractLayoutAlgorithm;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.undo.UndoSupport;


public class DegreeSortedCircleLayout extends AbstractLayoutAlgorithm<DegreeSortedCircleLayoutContext> {
	private static final String DEGREE_ATTR_NAME = "degree";

	/**
	 * Creates a new DegreeSortedCircleLayout object.
	 */
	public DegreeSortedCircleLayout(UndoSupport undoSupport) {
		super(undoSupport, "degree-circle", "Degree Sorted Circle Layout", true);
	}

	@Override
	public DegreeSortedCircleLayoutContext createTaskContext() {
		return new DegreeSortedCircleLayoutContext(supportsSelectedOnly(), supportsNodeAttributes(), supportsEdgeAttributes());
	}
	
	public TaskIterator createTaskIterator(DegreeSortedCircleLayoutContext context) {
		return new TaskIterator(
			new DegreeSortedCircleLayoutTask(context.getNetworkView(), getName(), context.getSelectedOnly(),
							 context.getStaticNodes(), DEGREE_ATTR_NAME, context.singlePartition));
	}
}
