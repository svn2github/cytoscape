
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


import java.lang.reflect.Field;
import java.lang.reflect.Method;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;



/**
 * Interceptor for Tunables : detect them, create an appropriate <code>Handler</code> from the <code>HandlerFactory</code> for each of them, and store them in a HashMap for further use.
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
	 * @param tunablehandlerfactory  Factory of Handler = can be <code>GuiHandlerFactory</code> to make the GUI with the <code>Handlers</code>
	 *  or <code>PropHandlerFactory</code> to get the <code>Handlers</code> for Properties.
	 */
	public AbstractTunableInterceptor(HandlerFactory<H> tunablehandlerfactory) {
		this.factory = tunablehandlerfactory;
		handlerMap = new HashMap<Object, LinkedHashMap<String, H>>();
	}

	/**
	 *	To detect the Field and Method annotated as <code>Tunable</code>, create a <code>Handler</code> for each from the factory, and store it.
	 * @param obj A class that contains <code>Tunable</code> that need to be caught to interact with the users
	 */
	public void loadTunables(Object obj) {
		//System.out.println("looking at obj: " + obj.getClass().toString());
		if (!handlerMap.containsKey(obj)) { //Deleted to get new Panels if we do it many times
			LinkedHashMap<String, H> handlerList = new LinkedHashMap<String, H>();

			// Find each public field in the class.
			for (Field field : obj.getClass().getFields()) {
				//System.out.println("evaluating: " + field.toString());
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
			
			// Find each public method in the class.
			for (Method method : obj.getClass().getMethods()) {
	
				// See if the method is annotated as a Tunable.
   				if (method.isAnnotationPresent(Tunable.class)) {
					try {
						Tunable tunable = method.getAnnotation(Tunable.class);
						
						// Get a handler for this particular field type and
						// add it to the list.
						H handler = factory.getHandler(method,obj,tunable);
	
						if ( handler != null ) {
						 	handlerList.put( method.getName(), handler ); 
						}
	
					} catch (Throwable ex) {
						System.out.println("tunable method intercept failed: " + method.toString() );
						ex.printStackTrace();
					}
				}
			}

			handlerMap.put(obj, handlerList);
		} //End of the deleted Loop
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
