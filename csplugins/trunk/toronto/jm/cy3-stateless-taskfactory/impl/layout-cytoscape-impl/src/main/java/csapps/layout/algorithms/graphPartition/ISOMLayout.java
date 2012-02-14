/*
 * This is based on the ISOMLayout from the JUNG project.
 */
package csapps.layout.algorithms.graphPartition;


import org.cytoscape.view.layout.AbstractLayoutAlgorithm;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.undo.UndoSupport;


public class ISOMLayout extends AbstractLayoutAlgorithm<ISOMLayoutContext> {
	/**
	 * Creates a new ISOMLayout object.
	 */
	public ISOMLayout(UndoSupport undoSupport) {
		super(undoSupport,"isom", "Inverted Self-Organizing Map Layout", true);
	}

	@Override
	public ISOMLayoutContext createTaskContext() {
		return new ISOMLayoutContext(supportsSelectedOnly(), supportsNodeAttributes(), supportsEdgeAttributes());
	}
	
	public TaskIterator createTaskIterator(ISOMLayoutContext context) {
		if (context.getSelectedOnly())
			context.initStaticNodes();

		return new TaskIterator(
			new ISOMLayoutTask(context.getNetworkView(), getName(), context.getSelectedOnly(), context.getStaticNodes(),
					   context.maxEpoch, context.radiusConstantTime, context.radius, context.minRadius,
					   context.initialAdaptation, context.minAdaptation, context.sizeFactor,
					   context.coolingFactor, context.singlePartition));
	}
}
