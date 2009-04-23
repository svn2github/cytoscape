package org.cytoscape.work;

import java.lang.reflect.Field;
import java.lang.reflect.Method;


/**
 * Provides a factory to create <code>Handlers</code> depending on their type.
 * <code>Handlers</code> will be generated for Fields and Methods in the class that contains the <code>Tunables</code>.
 *
 * @param <H> <code>Handlers</code> that will be created by this factory. They will contain the informations provided by the <code>Tunable</code> annotations and the Object itself.
 * @author Pasteur
 */

public interface HandlerFactory<H extends Handler>{
	
	/**
	 * This method returns a <code>Handler</code> for a Field annotated as a <code>Tunable</code>
	 * 
	 * @param f Field that need to have a <code>Handler</code>
	 * @param o Object that takes part of the Field <code>f</code>
	 * @param t	Tunable that contains all the information concerning the Object
	 * @return H The created <code>Handler</code>
	 */
	 H getHandler(Field f, Object o, Tunable t);

	/**
	 * This method returns a <code>Handler</code> for a Method annotated as a <code>Tunable</code>
	 * 
	 * @param m	Method that need to have a <code>Handler</code>
	 * @param o	Object that takes part of the Field <code>f</code>
	 * @param t	Tunable that contains all the information concerning the Object
	 * @return H The created <code>Handler</code>
	 */
	 H getHandler(Method m, Object o, Tunable t);
	 
}