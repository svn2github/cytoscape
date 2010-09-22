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
	
	//@Tunable(description="Integration algorithm to use", groups="Algorithm settings")
	public Integrators integrator = Integrators.RUNGEKUTTA;
	//public ListSingleSelection<String> integratorChoice = "RUNGEKUTTA";

	//TODO refactor
	public enum Integrators {
		RUNGEKUTTA ("RUNGEKUTTA"),
		EULER ("EULER");

		private String name;
		private Integrators(String str) { name=str; }
		public String toString() { return name; }
		public Integrator getNewIntegrator() {
			// FIXME: could we use a switch on 'this' instead? (can't use one on
			// name, because that is string, but) 'this' would be an enum, right?
			// but eclipse complains if I have Integrators.EULER as a switch
			// label...
		
			if (name.equals("EULER")){
				return new EulerIntegrator();
			}
			else if (name.equals("RUNGEKUTTA")){
				return new RungeKuttaIntegrator();
			} else {// use Euler as default
				return new EulerIntegrator();
			}
		}
	}

	
	/**
	 * Creates a new GridNodeLayout object.
	 */
	public ForceDirectedLayout(UndoSupport un) {
		super(un);
	}

	//TODO how to validate these values?
	public boolean tunablesAreValid(final Appendable errMsg) {
		// Do something here to validate the parameter values
		// ??????????????
		
		return true;
	}

	public TaskIterator getTaskIterator() {
		
		return new TaskIterator(new ForceDirectedLayoutTask(networkView, getName(), selectedOnly, staticNodes,
				numIterations, defaultSpringCoefficient,defaultSpringLength,defaultNodeMass,integrator));
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
