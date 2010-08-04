package org.cytoscape.work;

import java.lang.reflect.Field;
import java.lang.reflect.Method;


/**
 * This interface specifies all the methods that will be applied to a <code>Handler</code>.
 * 
 * <p><pre>
 * The <code>Handler</code> can be used for the construction of the GUI (by using the <code>GUIHandler</code>), 
 * or can also be used to get parameters from the command line (by using the <code>CLHandler</code>), 
 * and also to get the properties for each <code>Tunable</code> object through its <code>Handlers</code>.
 * 
 * <code>Handler</code> will also be used to set the values that are in the <code>Handler</code> to the Properties object with <code>PropHandler</code>
 * 
 * Access to different parts of the <code>Handler</code> is allowed by using the <code>getField()</code>,
 * <code>getObject()</code>, and <code>getTunable</code> methods.
 * </pre></p>
 * @author pasteur
 */

/** Tunables can be accessed through either {getField(),getTunable()} or {getGetMethod(),getSetMethod(),getGetTunable(),getSetTunable()}.
 *  Those two sets of methods are mutually exclusive, either one set will return all nulls or the other set will.  The direct field access (corresponding
 *  to the first set is easier to use but, both, less flexible and violates information-hiding best practices.
 */
public interface Handler {
	
	/**
	 * @return an object describing a field annotated with @Tunable or null if no field has been associated with this handler
	 */
	Field getField();
	
	/**
	 * In order to actually set a field, both an instance of Field as well as an object instance are required.  Similarly, in order to call setter and getter
	 * methods an object instance is required.
	 *
	 * @return if getField() returns non-null, this will return an instance whose annotated field can be accessed with the Field instance returned by getField
	 *         otherwise the returned object will be the one with annoated getter send setter methods
	 */
	Object getObject();
	
	/**
	 * @return the tunable that is annotating the field represented by the Field instance returned by getField()
	 */
	Tunable getTunable();
	
	/**
	 * @return an instance of Method representating a getter method annotated with @Tunable or null
	 */
	Method getGetMethod();

	/**
	 * @return an instance of Method representating a setter method annotated with @Tunable or null
	 */
	Method getSetMethod();
	
	/**
	 * @return the Tunable associated with a getter method
	 */
	Tunable getGetTunable();

	/**
	 * @return the Tunable associated with a setter method
	 */
	Tunable getSetTunable();
}
