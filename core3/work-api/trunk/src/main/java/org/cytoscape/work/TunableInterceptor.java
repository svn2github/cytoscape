package org.cytoscape.work;

import java.util.Map;



/**
 * Provides methods to intercept the Objects annotated as <code>@Tunable</code>, use, and display them.
 * Intended to be used as an OSGi server.
 * @param <T> <code>Handlers</code> that will be detected. They will contain the informations provided by the <code>@Tunable</code> annotations and the Object itself.
 * @author Pasteur
 */
public interface TunableInterceptor<T extends Handler> {

	
	
	/**
	 * Catch the Tunables of the object o
	 * 
	 * This method detects the fields or the methods of the object o, then search for <code>@Tunable</code> annotations, and finally create a <code>Handler</code> for each type of Object by using the <code>HandlerFactory</code> . 
	 * The handlers are stocked in a HashMap and can then be retrieved by their Key(i.e name of the field or method)
	 * 
	 * @param o This has to be a class that contains some <code>@Tunable</code> annotations.
	 */
	void loadTunables(Object o);
	
	
	
	/**
	 * To get the Map that contains all the <code>Handlers</code> for the Object o
	 * 
	 * @param o The Object on which the loadTunable() method has been executed.
	 * @return The Map with all the <code>Handlers</code> that have been previously detected.
	 */
	Map<String,T> getHandlers(Object o);
	
	
	
	/**
	 * Display the Objects caught to the user in order to modify their values.
	 * 
	 * Create the UI with JPanels for each <code>Guihandler</code>, and display it to the user :<br>
	 * 	1) In a <i>parent</i> JPanel if <code>setParent()</code> method has been called before<br>
	 * 		The new values will be applied to the original Objects depending on the action that has been associated to the Buttons provided by this panel.<br>
	 * 
	 * 	2) By default in a JOptionPanel<br>
	 * 		This method will detect if the Object that contains the <code>@Tunable</code> annotations is implementing the <code>TunableValidator</code> interface, and if yes, execute the validation test.
	 *  	The new values will be applied to the original Objects if "OK" is clicked, and if the validation test has succeeded. Either, no modification will happen.
	 * 
	 * @param obs Object[] which contains classes with <code>Tunables</code> that need to be caught.
	 * @return newValuesSet True if the values has been modified, false if not.
	 */
	boolean createUI(Object ... obs );
	
	
	
	/**
	 * Use to add the Tunables' JPanels to an external JPanel that has been defined in another class.
	 * @param o An Object that has to be an instance of a <code>JPanel</code>.
	 * @throws IllegalArgumentException If the Object o is not a JPanel, it can not be set as the parent for the others : they will be displayed to the user in a <code>JOptionPanel</code>.
	 */
	void setParent(Object o);
	
	
	
	/**
	 * Use to apply the new values that have been modified to the original Objects.
	 * This method will set the value for the Object of each <code>Guihandler</code> taken from the <code>Map</code> that is containing the <code>Handlers</code>.<br>
	 * Important : the value of the <code>Guihandler</code> will be set only if its JPanel is valid.
	 */
	void handle();
}
