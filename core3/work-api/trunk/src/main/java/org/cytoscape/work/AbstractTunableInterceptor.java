
/*
 Copyright (c) 2008, The Cytoscape Consortium (www.cytoscape.org)

 The Cytoscape Consortium is:
 - Institute for Systems Biology
 - University of California San Diego
 - Memorial Sloan-Kettering Cancer Center
 - Institut Pasteur
 - Agilent Technologies

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

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;



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
 * @param <H>  <code>Handlers</code> created in the factory
 */
public abstract class AbstractTunableInterceptor<H extends Handler> implements TunableInterceptor<H> {
	/**
	 * Factory for Handlers
	 */
	protected HandlerFactory<H> factory;

	/**
	 * Store the Handlers
	 */
	protected Map<Object, LinkedHashMap<String, H>> handlerMap;

	/**
	 * Creates a new AbstractTunableInterceptor object.
	 *
	 * @param tunablehandlerfactory  Factory of <code>Handlers</code> = can be <code>GuiHandlerFactory</code> to make the GUI with the <code>Handlers</code>,
	 * 	<code>CLHandlerFactory</code> to get the <code>Handlers</code> that will create the <i>Options</i> for the <code>Tasks</code> runnable through the CommandLine Interface,
	 *  or <code>PropHandlerFactory</code> to get the <code>Handlers</code> for Properties.
	 */
	public AbstractTunableInterceptor(HandlerFactory<H> tunablehandlerfactory) {
		this.factory = tunablehandlerfactory;
		handlerMap = new HashMap<Object, LinkedHashMap<String, H>>();
	}

	/**
	 *	To detect the Field and Methods annotated as <code>Tunable</code>, create a <code>Handler</code> for each from the factory, and store it.
	 * @param obj A class that contains <code>Tunable</code> that need to be caught to interact with the users
	 */
	public void loadTunables(Object obj) {
		//System.out.println("looking at obj: " + obj.getClass().toString());
		if (!handlerMap.containsKey(obj)) { //Deleted to get new Panels if we do it many times
			LinkedHashMap<String, H> handlerList = new LinkedHashMap<String, H>();

			// Find each public field in the class.
			for (Field field : obj.getClass().getFields()) {
				// See if the field is annotated as a Tunable.
				if (field.isAnnotationPresent(Tunable.class)) {
					try {
						
						Tunable tunable = field.getAnnotation(Tunable.class);

						H handler = factory.getHandler(field, obj, tunable);

						if (handler != null) {
							handlerList.put(field.getName(), handler);
						} else
							System.out.println("No handler for type: " + field.getType().getName());
					} catch (Throwable ex) {
						System.out.println("tunable field intercept failed: " + field.toString());
						ex.printStackTrace();
					}
				}
			}

			Map<String, Method> setMethodsMap = new HashMap<String,Method>();
			Map<String, Method> getMethodsMap = new HashMap<String,Method>();
			
			Map<String, Tunable> getTunableMap = new HashMap<String,Tunable>();
			Map<String, Tunable> setTunableMap = new HashMap<String,Tunable>();

			
			// Find each public method in the class.
			for (Method method : obj.getClass().getMethods()) {
	
				// See if the method is annotated as a Tunable.
   				if (method.isAnnotationPresent(Tunable.class)) {
					try {
						Tunable tunable = method.getAnnotation(Tunable.class);
						if(method.getName().startsWith("get")){
							getMethodsMap.put(method.getName().substring(3),method);
							getTunableMap.put(method.getName().substring(3),tunable);
							if(setMethodsMap.containsKey(method.getName().substring(3))){
								//get a handler with the getMethod and setMethod
								H handler = factory.getHandler(getMethodsMap.get(method.getName().substring(3)),setMethodsMap.get(method.getName().substring(3)), obj, getTunableMap.get(method.getName().substring(3)),setTunableMap.get(method.getName().substring(3)));
								if ( handler != null ) {
								 	handlerList.put( "getset" + method.getName().substring(3), handler ); 
								}
							}
						}
						else if(method.getName().startsWith("set")){
							setMethodsMap.put(method.getName().substring(3),method);
							setTunableMap.put(method.getName().substring(3),tunable);
							if(getMethodsMap.containsKey(method.getName().substring(3))){
								//get a handler with the getMethod and setMethod
								H handler = factory.getHandler(getMethodsMap.get(method.getName().substring(3)),setMethodsMap.get(method.getName().substring(3)), obj, getTunableMap.get(method.getName().substring(3)),setTunableMap.get(method.getName().substring(3)));
								//add it to the list
								if ( handler != null ) {
								 	handlerList.put( "getset" + method.getName().substring(3), handler ); 
								}
							}
						}
						else throw new Exception("the name of the method has to start with \"set\" or \"get\"");

					} catch (Throwable ex) {
						System.out.println("tunable method intercept failed: " + method.toString() );
						ex.printStackTrace();
					}
				}
			}

			handlerMap.put(obj, handlerList);
		}
	}

	/**
	 *  To get the Map of the <code>Handlers</code> that are contained in this <code>TunableInterceptor</code> Object applied to an external Object(class).
	 *
	 * @param o The Object the TunableInterceptor has been applied to.
	 *
	 * @return  The map that contains all the <code>Handlers</code> that have been created for the Object o
	 */
	public Map<String, H> getHandlers(Object o) {
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
	public abstract void handle();
}
