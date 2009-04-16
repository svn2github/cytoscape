package org.cytoscape.work;


/**
 * If implemented, it is used to apply a test to the modified values of the class.
 * 
 * @author Mathieu
 *
 */
public interface TunableValidator{
	
	/**
	 * Executes the validation test on the annotated <code>Tunables</code> present in the class whose Objects have been modified.
	 * 
	 * @return The message that will be displayed if the test failed, or <i>null</i> if the test succeeded.
	 */
	public String validate();
}