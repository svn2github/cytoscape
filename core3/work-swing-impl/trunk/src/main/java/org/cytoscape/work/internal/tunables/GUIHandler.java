package org.cytoscape.work.internal.tunables;


import javax.swing.JPanel;

import org.cytoscape.work.TunableHandler;


/**
 *	Specific <code>Handler</code> for construction of GUI
 *
 * 	Access to the <code>Handler</code> for any type of <code>Tunable</code> will be provided by using this interface
 * 
 * @author pasteur
 *
 */
public interface GUIHandler extends TunableHandler {
	/**
	 * to get the panel that contains the GUI representation (<code>JTextField, JFileChooser, JLabel, JList ...</code>)
	 * 
	 * @return the panel containing GUI
	 */
	JPanel getJPanel();
	
	/**
	 * To get the current value of a <code>Handler</code> (or path for a <code>FileHandler</code>, or selected item(s)
	 * for <code>ListMultipleSelection ListSingleSelection</code>, ...)
	 *
	 * @return string representing the state
	 */
	String getState();

	
	/**
	 * Use to set the intercepted object (with <code>Tunable</code> annotation) with the new <i>"value"</i> that has been chosen or entered by the user through GUI
	 */
	void handle();
	
	
	/**
	 *  Notify dependents that this object is changing, i.e. an event occured.
	 */
	void notifyDependents();
	
	/**
	 * add a dependency to this <code>GUIHandler</code> on another one
	 * 
	 * @param gh the <code>GUIHandler</code> it will depend on
	 */
	void addDependent(GUIHandler gh);
	
	/**
	 * Check if the dependency matches with the rule provided on the other <code>GUIHandler</code> 
	 * <p>
	 * <pre>
	 * the checking is dynamically done.
	 * 
	 * If it matches : the GUI for this <code>GUIHandler</code> is available, or not if it doesn't match
	 * 
	 * the dependency can me made on String, a boolean value, a specific value for an Integer,Double..., a selected item of a list ...
	 * </pre>
	 * </p>
	 * 
	 * @param name of the <code>GUIHandler</code> on which it depends
	 * @param state of the <code>GUIHandler</code> that is needed to make the GUI available 
	 */
	void checkDependency(String name, String state);
	
	/**
	 * Get the new "values" for the <code>Tunables</code> object that have been modified if their JPanel is enabled : if the dependencies are matching
	 */
	void handleDependents();
	
	/**
	 * To get the name of the dependency of this <code>GUIHandler</code>
	 * @return the name of the dependency
	 */
	String getDependency();
}
