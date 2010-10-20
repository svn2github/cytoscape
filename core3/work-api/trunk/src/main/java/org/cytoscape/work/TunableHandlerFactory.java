package org.cytoscape.work;


import java.lang.reflect.Field;
import java.lang.reflect.Method;


/**
 * Provides a factory to create <code>Handlers</code> depending on their type.
 * <code>Handlers</code> will be generated for Fields and Methods in the class that contains the <code>Tunables</code>.
 *
 * @param <T> <code>TunableHandler</code> that will be created by this factory. They will contain the informations
 * provided by the <code>Tunable</code> annotations and the Object itself.
 * @author Pasteur
 */

public interface TunableHandlerFactory<T extends TunableHandler> {
	/**
	 * This method returns a <code>TunableHandler</code> for a Field annotated as a <code>Tunable</code>
	 * 
	 * @param field     Field that need to have a <code>Handler</code>
	 * @param instance  the object on  which we want to read/write the Field <code>field</code>
	 * @param tunable   Tunable that contains all the information concerning the user interface
	 * @return T       the newly constructed <code>TunableHandler</code>
	 */
	 T getHandler(final Field field, final Object instance, final Tunable tunable);

	/**
	 * This method returns a <code>TunableHandler</code> for a Method annotated as a <code>Tunable</code>
	 * 
	 * @param setter    a Method that need to be annotated with  <code>@Tunable</code>
	 * @param getter    a Method that need to be annotated with  <code>@Tunable</code>
	 * @param instance  the object on which we want to invoke the <code>setter</code> and <code>getter</code> methods
	 * @param tunable   Tunable that contains all the information concerning the user interface
	 * @return T       the newly constructed <code>TunableHandler</code>
	 */
	 T getHandler(final Method setter, final Method getter, final Object instance, final Tunable tunable);
}