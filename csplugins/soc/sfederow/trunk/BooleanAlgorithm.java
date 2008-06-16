

import javax.swing.JPanel;




public interface BooleanAlgorithm {
//	 Property change
	//public static String CLUSTER_COMPUTED = "CLUSTER_COMPUTED";

	/**
 	 * Get the short name of this algorithm
 	 *
 	 * @return short-hand name for algorithm
 	 */
	public String getShortName();

	/**
 	 * Get the name of this algorithm
 	 *
 	 * @return name for algorithm
 	 */
	public String getName();

	/**
 	 * Get the settings panel for this algorithm
 	 *
 	 * @return settings panel
 	 */
	public JPanel getSettingsPanel();

	/**
	 * This method is used to ask the algorithm to revert its settings
	 * to some previous state.  It is called from the settings dialog
	 * when the user presses the "Cancel" button.
	 *
	 * NOTE: ClusterAlgorithmBase implements this on behalf of all its subclasses
	 * by using Java Preferences.
	 */
	public void revertSettings();

  /**
	 * This method is used to ask the algorithm to get its settings
	 * from the settings dialog.  It is called from the settings dialog
	 * when the user presses the "Done" or the "Execute" buttons.
	 *
	 * NOTE: ClusterAlgorithmBase implements this on behalf of all its subclasses
	 * by using Java Preferences.
	 */
	public void updateSettings();

  /**
	 * This method is used to ask the algorithm to get all of its tunables
	 * and return them to the caller.
	 *
	 * @return the cluster properties for this algorithm
	 *
	 */
	public BooleanProperties getSettings();

	/**
	 * This method is used to signal a running cluster algorithm to stop
	 *
	 */
	public void halt();

	/**
	 * This is the main interface to trigger a cluster to compute
	 *
	 * @param monitor a TaskMonitor
	 */
	//public void doCluster(TaskMonitor monitor);

	/**
 	 * Hooks for the visualizer
 	 *
 	 * @return the visualizer or null if one doesn't exist
 	 */
	//public ClusterViz getVisualizer();

	/**
 	 * This is a hook to notify interested parties that we have finished
 	 * computing a cluster.  The major use is for clusters with visualizers
 	 * to inform UI components that the visualizer can now be launched.
 	 */
	//public PropertyChangeSupport getPropertyChangeSupport();

}


