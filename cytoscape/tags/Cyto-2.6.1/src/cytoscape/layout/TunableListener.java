// vim: set ts=2: */
package cytoscape.layout;

/**
 * The TunableListener interface provides a way to encapsulate
 * CyLayoutAlgorithm property and settings values.  Each Tunable
 * has a name, which corresponds to the property name, a description,
 * which is used as the label in the settings dialog, a type, a
 * value, and information about the value, such as a list of options
 * or the lower and upper bounds for the value.  These are meant
 * to be used as part of the LayoutSettingsDialog (see getPanel).
 */
public interface TunableListener {
	/**
	 * The tunableChanged method is called whenever a tunable value
	 * has been changed by the user (as opposed to programmatically).
	 * It can be used by systems to react to user input to improve
	 * interactivity or user feedback.
	 *
	 * @param tunable the Tunable that was changed
	 */
	public void tunableChanged(Tunable tunable);
}
