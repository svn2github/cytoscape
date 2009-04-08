
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
 * DOCUMENT ME!
  *
 * @param <T>  DOCUMENT ME!
 */
public abstract class AbstractTunableInterceptor<T extends Handler> implements TunableInterceptor<T> {
	/**
	 * DOCUMENT ME!
	 */
	protected HandlerFactory<T> factory;

	/**
	 * DOCUMENT ME!
	 */
	protected Map<Object, LinkedHashMap<String, T>> handlerMap;

	/**
	 * Creates a new AbstractTunableInterceptor object.
	 *
	 * @param tunablehandlerfactory  DOCUMENT ME!
	 */
	public AbstractTunableInterceptor(HandlerFactory<T> tunablehandlerfactory) {
		this.factory = tunablehandlerfactory;
		handlerMap = new HashMap<Object, LinkedHashMap<String, T>>();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param obj DOCUMENT ME!
	 */
	public void loadTunables(Object obj) {
		//System.out.println("looking at obj: " + obj.getClass().toString());
		//if (!handlerMap.containsKey(obj)) { //Deleted to get new Panels if we do it many times
			LinkedHashMap<String, T> handlerList = new LinkedHashMap<String, T>();

			// Find each public field in the class.
			for (Field field : obj.getClass().getFields()) {
				//System.out.println("evaluating: " + field.toString());
				// See if the field is annotated as a Tunable.
				if (field.isAnnotationPresent(Tunable.class)) {
					try {
						Tunable tunable = field.getAnnotation(Tunable.class);

						T handler = factory.getHandler(field, obj, tunable);

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
						T handler = factory.getHandler(method,obj,tunable);
	
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
		//} //End of the deleted Loop
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param o DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Map<String, T> getHandlers(Object o) {
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
