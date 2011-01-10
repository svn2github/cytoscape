package org.cytoscape.prefuse.layouts.internal;

import java.io.IOException;

import org.cytoscape.view.layout.AbstractLayout;
import org.cytoscape.view.layout.CyLayouts;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.TunableValidator;
import org.cytoscape.work.undo.UndoSupport;
import org.cytoscape.work.util.ListSingleSelection;

import prefuse.util.force.EulerIntegrator;
import prefuse.util.force.RungeKuttaIntegrator;
import prefuse.util.force.Integrator;
import org.cytoscape.work.util.ListSingleSelection;

public class ForceDirectedLayout extends AbstractLayout implements TunableValidator {
	@Tunable(description="Number of Iterations", groups="Algorithm settings")
	public int numIterations = 100;
	@Tunable(description="Default Spring Coefficient", groups="Algorithm settings")
	public double defaultSpringCoefficient = 1e-4;
	@Tunable(description="Default Spring Length", groups="Algorithm settings")
	public double defaultSpringLength = 50.0;
	@Tunable(description="Default Node Mass", groups="Algorithm settings")
	public double defaultNodeMass = 3.0;
	@Tunable(description="Don't partition graph before layout", groups="Standard settings")
	public boolean singlePartition;

	//@Tunable(description="Integration algorithm to use", groups="Algorithm settings")
	public Integrators integrator = Integrators.RUNGEKUTTA;
	//public ListSingleSelection<String> integratorChoice = "Runge-Kutta";
	
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
		super(un);
	}

	public boolean tunablesAreValid(final Appendable errMsg) {
		return isPositive(numIterations) && isPositive(defaultSpringCoefficient)
		       && isPositive(defaultSpringLength) && isPositive(defaultNodeMass);
	}

	private static boolean isPositive(final int n) {
		return n > 0;
	}

	private static boolean isPositive(final double n) {
		return n > 0.0;
	}

	public TaskIterator getTaskIterator() {
		return new TaskIterator(
			new ForceDirectedLayoutTask(networkView, getName(), selectedOnly, staticNodes,
						    numIterations, defaultSpringCoefficient,
						    defaultSpringLength, defaultNodeMass, integrator,
						    singlePartition));
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String getName() {
		return "Force-Directed";
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String toString() {
		return "Force Directed Layout";
	}

	/**
	 * We do support selected only
	 *
	 * @return true
	 */
	public boolean supportsSelectedOnly() {
		return true;
	}
}
