/*
 Copyright (c) 2008, 2010, The Cytoscape Consortium (www.cytoscape.org)

 This library is free software; you can redistribute it and/or modify it
 under the terms of the GNU Lesser General Public License as published
 by the Free Software Foundation; either version 2.1 of the License, or
 any later version.

 This library is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 documentation provided hereunder is on an "as is" basis, and the
 Institute for Systems Biology and the Whitehead Institute
 have no obligations to provide maintenance, support,
 updates, enhancements or modifications.  In no event shall the
 Institute for Systems Biology and the Whitehead Institute
 be liable to any party for direct, indirect, special,
 incidental or consequential damages, including lost profits, arising
 out of the use of this software and its documentation, even if the
 Institute for Systems Biology and the Whitehead Institute
 have been advised of the possibility of such damage.  See
 the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation,
 Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
*/
package org.cytoscape.work;


import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;


/**
 * Interceptor for Tunables : detect them, create an appropriate <code>Handler</code> from the <code>HandlerFactory</code> for each of them, and store them in a HashMap for further use.
 *
 * <p><pre>
 * <b>example :</b>
 * <code>
 * public class Test{
 * 	<code>@Tunable(description="Number of Modules", group={"General Parameters"})</code>
 * 	public BoundedInteger numMod = new BoundedInteger(0,5,1000,false,false);
 * 	<code>@Tunable(description="Overlap Threshold", group={"General Parameters"})</code>
 * 	public BoundedDouble overlap = new BoundedDouble(0.0,0.8,1.0,false,false);
 * 	<code>@Tunable(description="Adjust for size?", group={"General Parameters"})</code>
 * 	public boolean adjustForSize = true;
 * }
 * </code></pre></p>
 *
 * <p><pre>
 * Here are the steps to get a list of handlers for each object annotated as a<code> @Tunable </code>, in order to provide :
 * <ul>
 * 	<li>a Graphic User Interface for <code>Tunables</code> in the Cytoscape Desktop(use of a <code>GuiTunableInterceptor</code>)</li>
 * 	<li>a CommandLine Interface in a terminal to execute the Tasks by just typing the name of the class implementing the <code>TaskFactory</code> interface (use of <code>CLTunableInterceptor</code>)</li>
 * 	<li>access to the properties of the <code>Tunables</code>(use of <code>LoadPropsInterceptor</code> or <code>StorePropsInterceptor</code> NEED TO BE DEVELOPPED !!!!!!!</li>
 * </ul>
 *
 * <ol>
 * 	<li> First, detection of the Field annonated as <code> @Tunable </code> in the class the <code>TunableInterceptor</code> is applied to</li>
 * 	<li>	Then, the <code>Handlers</code> are created for each kind of <code>Tunable</code> Object (In this example : creation of a <code>AbstractBounded<Integer></code>, <code>AbstractBounded<Double></code> and <code>Boolean</code>  <code>Handlers<code>)</li>
 * 	<li>	The <code>Handlers</code> are stored in a <i>handlerList</i>, and are used by different <code>TunableInterceptor</code> types</li>
 * 	<li> Create a GUI, provide CommandLine Options, Store or Load properties for those <code>Tunables</code> by using those <code>Handlers</code></li>
 * </ol>
 * </pre></p>
 *
 * @param <TH>  <code>TunableHandler</code>s created in the factory
 */
public abstract class AbstractTunableInterceptor<TH extends TunableHandler> implements TunableInterceptor<TH> {
	/**
	 *  Factory for Handlers
	 */
	protected HandlerFactory<TH> factory;

	/**
	 *  Store the Handlers
	 */
	protected Map<Object, LinkedHashMap<String, TH>> handlerMap;

	/**
	 *  Store the JPanel-returning methods
	 */
	protected Map<Object, Method> guiProviderMap;

	protected Logger logger;

	/**
	 * Creates a new AbstractTunableInterceptor object.
	 *
	 * @param tunableHandlerFactory  Factory of <code>Handlers</code> = can be <code>GUIHandlerFactory</code> to make the GUI with the <code>Handlers</code>,
	 * 	<code>CLHandlerFactory</code> to get the <code>Handlers</code> that will create the <i>Options</i> for the <code>Tasks</code> runnable through the CommandLine Interface,
	 *  or <code>PropHandlerFactory</code> to get the <code>Handlers</code> for Properties.
	 */
	public AbstractTunableInterceptor(HandlerFactory<TH> tunableHandlerFactory) {
		this.factory = tunableHandlerFactory;
		handlerMap = new HashMap<Object, LinkedHashMap<String, TH>>();
		guiProviderMap = new HashMap<Object, Method>();
		logger = LoggerFactory.getLogger(getClass());
	}

	/**
	 *  To detect fields and methods annotated with <code>Tunable</code>, create a <code>Handler</code> for
	 *  each from the factory, and store it in <code>handlerMap</code>.
	 *  In addition we also detect methods annotated with <code>ProvidesGUI</code> and store those in
	 *  <code>guiProviderMap</code>.
	 *
	 *  @param obj A class that contains <code>Tunable</code> that need to be caught to interact with the users
	 */
	public void loadTunables(final Object obj) {
		if (!handlerMap.containsKey(obj)) {
			LinkedHashMap<String, TH> handlerList = new LinkedHashMap<String, TH>();

			// Find each public field in the class.
			for (final Field field : obj.getClass().getFields()) {
				// See if the field is annotated as a Tunable.
				if (field.isAnnotationPresent(Tunable.class)) {
					try {
						// Get the tunable's annotations
						final Tunable tunable = field.getAnnotation(Tunable.class);

						// Get a Handler for this type of Tunable and...
						TH handler = factory.getHandler(field, obj, tunable);

						// ...add it to the list of Handlers
						if (handler != null)
							handlerList.put(field.getName(), handler);
						else
							System.out.println("No handler for type: " + field.getType().getName());
					} catch (final Throwable ex) {
						System.out.println("tunable field intercept failed: " + field.toString());
						ex.printStackTrace();
					}
				}
			}

			Map<String, Method> setMethodsMap = new HashMap<String,Method>();
			Map<String, Method> getMethodsMap = new HashMap<String,Method>();
			Map<String, Tunable> tunableMap = new HashMap<String,Tunable>();

			// Find each public method in the class.
			for (final Method method : obj.getClass().getMethods()) {
				// See if the method is annotated as a Tunable.
   				if (method.isAnnotationPresent(Tunable.class)) {
					try {
						final Tunable tunable = method.getAnnotation(Tunable.class);
						if (method.getName().startsWith("get")) {
							if (!isValidGetter(method))
								throw new Exception("Invalid getter method specified \"" + method.getName()
										    + "\", maybe this method takes arguments or returns void?");

							final String rootName = method.getName().substring(3);
							getMethodsMap.put(rootName, method);
							tunableMap.put(rootName, tunable);
							if (setMethodsMap.containsKey(rootName)){
								final Method setter = setMethodsMap.get(rootName);
								if (!setterAndGetterTypesAreCompatible(method, setter))
									throw new Exception("Return type of " + method.getName() + "() and the argument type of "
											    + setter.getName() + "() are not the same!");

								//get a handler with the getMethod and setMethod
								final TH handler = factory.getHandler(method, setter, obj,
											              tunableMap.get(rootName));
								if (handler == null)
									System.err.println("*** Warning: Failed to create a handler for " + setter + "!");
								else
								 	handlerList.put("getset" + rootName, handler);
							}
						}
						else if (method.getName().startsWith("set")) {
							if (!isValidSetter(method))
								throw new Exception("Invalid setter method specified \"" + method.getName()
										    + "\", maybe this method does not take a single "
										    + "argument or does not return void?");

							final String rootName = method.getName().substring(3);
							setMethodsMap.put(rootName, method);
							tunableMap.put(rootName, tunable);
							if (getMethodsMap.containsKey(rootName)) {
								final Method getter = getMethodsMap.get(rootName);
								if (!setterAndGetterTypesAreCompatible(getter, method))
									throw new Exception("Return type of " + getter.getName() + "() and the argument type of "
											    + method.getName() + "() are not the same!");

								//get a handler with the getMethod and setMethod
								final TH handler = factory.getHandler(getter, method, obj,
								                                      tunableMap.get(rootName));
								//add it to the list
								if (handler == null)
									System.err.println("*** Warning: Failed to create a handler for " + getter + "!");
								else
								 	handlerList.put("getset" + rootName, handler);
							}
						}
						else
							throw new Exception("the name of the method has to start with \"set\" or \"get\"");

					} catch (Throwable ex) {
						System.out.println("tunable method intercept failed: " + method);
						ex.printStackTrace();
					}
				} else if (method.isAnnotationPresent(ProvidesGUI.class)) {
					if (method.getReturnType() != void.class)
						logger.error(method.getName() + " annotated with @ProvidesGUI must return void!");
					else if (method.getParameterTypes().length != 0)
						logger.error(method.getName() + " annotated with @ProvidesGUI must take 0 arguments!");
					else {
						if (!guiProviderMap.isEmpty())
							logger.error("Classes must have at most a single @ProvidesGUI annotated method but + "
							             + method.getDeclaringClass().getName() + " has more than one!");
						guiProviderMap.put(obj, method);
					}
				}
			}

			handlerMap.put(obj, handlerList);
		}
	}

	private boolean isValidGetter(final Method getterCandidate) {
		// Make sure we're not returning "void":
		try {
			final Type returnType = getterCandidate.getGenericReturnType();
			if (returnType == Void.class)
				return false;
		} catch(final Exception e) {
			return false;
		}

		// Make sure we're not taking any arguments:
		return getterCandidate.getParameterTypes().length == 0;
	}

	private boolean isValidSetter(final Method setterCandidate) {
		// Make sure we are returning "void":
		try {
			final Type returnType = setterCandidate.getGenericReturnType();
			if (returnType != void.class)
				return false;
		} catch(final Exception e) {
			return false;
		}

		// Make sure we're taking a single arguments:
		return setterCandidate.getParameterTypes().length == 1;
	}

	/**
	 *  @return returns true if the return type of the getter method is the same as the single argument type of the setter method, otherwise returns false
	 */
	private boolean setterAndGetterTypesAreCompatible(final Method getter, final Method setter) {
		return getter.getGenericReturnType() == setter.getParameterTypes()[0];
	}

	/**
	 *  To get the Map of the <code>Handlers</code> that are contained in this <code>TunableInterceptor</code> Object applied to an external Object(class).
	 *
	 * @param o The Object the TunableInterceptor has been applied to.
	 *
	 * @return  The map that contains all the <code>Handlers</code> that have been created for the Object o
	 */
	public Map<String, TH> getHandlers(final Object o) {
		if (o == null)
			return null;

		return handlerMap.get(o);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public abstract boolean createUI(Object... objs);
	public abstract void setParent(Object o);
	public abstract boolean handle();
}
