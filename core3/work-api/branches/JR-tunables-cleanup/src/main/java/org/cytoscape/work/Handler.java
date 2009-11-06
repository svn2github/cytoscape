package org.cytoscape.work;

import java.lang.reflect.Field;
import java.lang.reflect.Method;


	/**
	 * This interface specifies all the methods that will be applied to the <code>Handler</code>.
	 * 
	 * <p><pre>
	 * The <code>Handler</code> can be used for the construction of the GUI (by using the <code>Guihandler</code>), 
	 *  or can also be used to get the informations from the CommandLine. (by using the <code>CLHandler</code>), 
	 *  or even to get the properties for each <code>Tunable</code> object through its <code>Handlers</code>.
	 * 
	 * <code>Handler</code> will also be used to set the values that are in the <code>Handler</code> to the Properties object with <code>PropHandler</code>
	 * 
	 * Access to different parts of the<code>Handler</code> is allowed by using the <code>getField()</code>, <code>getMethod()</code>, <code>getObject()</code>, and <code>getTunable</code> methods.
	 * </pre></p>
	 * @author pasteur
	 *
	 */

// The <code>Handler</code> may also be modified. Thus, need to add some listeners to catch the modifications.
// Possibility to change the handler by setting it a new one.

public interface Handler {
	
	/**
	 * To get the Handler's Field
	 * @return Field
	 */
	Field getField();
	
	/**
	 * To get the handler's Method
	 * @return Method
	 */
	Method getMethod();
	
	/**
	 * To get the Handler's Object
	 * @return object
	 */
	Object getObject();
	
	/**
	 * To get the Handler's Tunable
	 * @return Tunable
	 */
	Tunable getTunable();
	
	
	/**
	 * To get the handler's GetMethod
	 * @return Method
	 */
	Method getGetMethod();
	/**
	 * To get the handler's SetMethod
	 * @return Method
	 */
	Method getSetMethod();
	
	/**
	 * To get the Handler's GetTunable
	 * @return Tunable
	 */
	Tunable getGetTunable();

	/**
	 * To get the Handler's SetTunable
	 * @return Tunable
	 */
	Tunable getSetTunable();

	
	
	/**
	 * To add a <code>HandlerListener</code> to the <code>Handler</code>
	 * @param listener	Listener added to the <code>Handler</code>
	 */
	void addHandlerListener(HandlerListener listener);
	/**
	 * DOCUMENT ME!
	 * 
	 * @param listener
	 * @return
	 */
	boolean removeHandlerListener(HandlerListener listener); 
	
	/**
	 * DOCUMENT ME!
	 * 
	 * @param otherHandler
	 */
	void handlerChanged(Handler otherHandler);
	
}
