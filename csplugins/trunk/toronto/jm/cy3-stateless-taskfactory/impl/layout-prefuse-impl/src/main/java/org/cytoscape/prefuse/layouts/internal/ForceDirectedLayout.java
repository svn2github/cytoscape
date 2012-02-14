package org.cytoscape.prefuse.layouts.internal;


import org.cytoscape.view.layout.AbstractLayoutAlgorithm;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.undo.UndoSupport;

import prefuse.util.force.EulerIntegrator;
import prefuse.util.force.Integrator;
import prefuse.util.force.RungeKuttaIntegrator;


public class ForceDirectedLayout extends AbstractLayoutAlgorithm<ForceDirectedLayoutContext> {
	private Integrators integrator = Integrators.RUNGEKUTTA;
	
	public enum Integrators {
		RUNGEKUTTA ("Runge-Kutta"),
		EULER ("Euler");

		private String name;
		private Integrators(String str) { name=str; }
		public String toString() { return name; }
		public Integrator getNewIntegrator() {
			if (this == EULER)
				return new EulerIntegrator();
			else
				return new RungeKuttaIntegrator();
		}
	}

	
	/**
	 * Creates a new GridNodeLayout object.
	 */
	public ForceDirectedLayout(UndoSupport un) {
		super(un, "force-directed", "Force Directed Layout", true);
	}

	@Override
	public ForceDirectedLayoutContext createTaskContext() {
		return new ForceDirectedLayoutContext(supportsSelectedOnly(), supportsNodeAttributes(), supportsEdgeAttributes());
	}
	
	public TaskIterator createTaskIterator(ForceDirectedLayoutContext context) {
		return new TaskIterator(
			new ForceDirectedLayoutTask(context.getNetworkView(), getName(), context.getSelectedOnly(), context.getStaticNodes(),
						    context.numIterations, context.defaultSpringCoefficient,
						    context.defaultSpringLength, context.defaultNodeMass, integrator,
						    context.singlePartition));
	}
}
