package org.cytoscape.work.swing;


import javax.swing.JPanel;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskManager;
import org.cytoscape.work.TaskFactory;


/**
 * A Swing specific extension of {@link TaskManager} that allows a
 * JPanel to be used to present the {@link org.cytoscape.work.Tunable}s.
 */
public interface GUITaskManager extends TaskManager {
	/**
	 *  Sets the parent panel on the TunableInterceptor that it manages.
	 *  @param parent the new parent panel for the tunables panel
	 */
	void setParent(final JPanel parent);

	/**
	 * Returns the configuration panel for the specified task factory.
	 * @param taskFactory a non-null task factory
	 * @return the panel generated from the tunables annotating "taskFactory" 
	 * or null if "taskFactory" has no tunables
	 */
	JPanel getConfigurationPanel(final TaskFactory taskFactory);
}
