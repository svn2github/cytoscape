package org.cytoscape.work.swing;


import java.util.Map;
import javax.swing.JPanel;

import org.cytoscape.work.TunableInterceptor;
import org.cytoscape.work.TunableHandler;


/**
 * Provides methods to intercept the Objects annotated as <code>@Tunable</code>, use, and display them.
 * Intended to be used as an OSGi server.
 * @param <TH> <code>TunableHandler</code>s that will be detected. It will contain the information provided
 * by the <code>@Tunable</code> annotations and the Object itself.
 * @author Pasteur
 */
public interface GUITunableInterceptor<TH extends TunableHandler> extends TunableInterceptor<TH> {
	/**
	 * Identify Tunables of an Object o and assign a <code>Handler</code> to each <code>Tunable</code>.
	 * 
	 * This method detects the fields and the methods of the object o, then searches for <code>@Tunable</code> annotations, and finally creates a
	 * <code>Handler</code> for each type of Object by using the <code>HandlerFactory</code>. 
	 * The handlers are stored in a HashMap and can then be retrieved by their key (i.e name of the field or method).
	 * 
	 * @param o This has to be an instance of a class that contains at least one <code>@Tunable</code> annotation.
	 */
	void loadTunables(Object o);
	
	/**
	 * Returns the Map that contains all the <code>Handler</code>s for the Object <code>o</code>.
	 * 
	 * @param o An Object on which the loadTunable() method has previously been executed.
	 * @return The Map with all the <code>Handlers</code> that have been previously assigned to <code>o</code>.
	 */
	Map<String, TH> getHandlers(Object o);

	/**
	 *  @param obs  objects which are instances classes with <code>Tunables</code> that need to be displayed to a user.
	 *  @return a panel that contains a visual representation of the tunables and is appropriate for embedding in a dialog or another panel
	 */
	JPanel getUI(Object... obs);

	/**
	 * Display the identified tunables to a user in order to allow her/him to modify their values.
	 * <p><pre>
	 * Create the UI with JPanels for each <code>GUITunableHandler</code>, and display it to the user :
	 * 	1) In a <i>parent</i> JPanel if <code>setParent()</code> method has been called before
	 * 		The new values will be applied to the original Objects depending on the action that has been associated to the Buttons provided by this panel.
	 * 
	 * 	2) By default in a JOptionPanel<
	 * 		This method will detect if the Object that contains the <code>@Tunable</code> annotations is implementing the <code>TunableValidator</code> interface, and if yes, execute the validation test.
	 *  	The new values will be applied to the original Objects if "OK" is clicked, and if the validation test has succeeded. Either, no modification will happen.
	 * </pre></p>
	 * @param obs Object[] which contains classes with <code>Tunables</code> that need to be displayed to a user.
	 * @return newValuesSet True if at least one value has been modified, false if not.
	 */
	boolean execUI(Object... obs);
	
	/**
	 * Use to add the Tunables' JPanels to an external JPanel that has been defined in another class.
	 * @param o An Object that has to be an instance of a <code>JPanel</code>.
	 * @throws IllegalArgumentException If the Object o is not a JPanel, it can not be set as the parent for the others : they will be displayed to the user in a <code>JOptionPanel</code>.
	 */
	void setParent(final JPanel parent);
	
	/**
	 * Used to update tunable's values w/ the values as provided by a user.
	 * This method will set the value for the Object of each <code>GUITunableHandler</code> taken from the <code>Map</code> that is containing the
	 * <code>Handlers</code>.<br>
	 * Important : the value of the <code>GUITunableHandler</code> will be set only if its JPanel is valid.
	 * @return boolean The success or failure of the validation of <code>Tunables</code>' values depending on <code>validate</code> method from <code>TunableValidator</code> interface.
	 * <p><pre>
	 * True if the validation of <code>Tunables</code> values is a success : the following tasks can then be executed
	 * 
	 * False if an exception is thrown (from <code>TunableValidator</code>) and so the tasks won't be performed
	 */
	boolean validateAndWriteBackTunables();

	/** Tests an object for having tunable annotations.
	 *
	 *  @return true if "o" has tunable annotations and else false.
	 */
	boolean hasTunables(final Object o);
}
