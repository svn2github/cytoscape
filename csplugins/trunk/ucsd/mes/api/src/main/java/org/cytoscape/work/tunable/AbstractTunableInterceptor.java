
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

package org.cytoscape.work.tunable;

import org.cytoscape.work.Tunable;

import java.lang.annotation.*;
import java.lang.reflect.*;

import java.util.LinkedList;
import java.util.List;


/**
 * An abstract implementation of {@link TunableInterceptor} that should serve as the super
 * class for almost all implementations of {@link TunableInterceptor}.
 *
 * @param <T>  DOCUMENT ME!
 */
public abstract class AbstractTunableInterceptor<T extends Handler> implements TunableInterceptor {
	/**
	 * DOCUMENT ME!
	 */
	protected HandlerFactory<T> factory;

	/**
	 * Creates a new AbstractTunableInterceptor object.
	 *
	 * @param factory  DOCUMENT ME!
	 */
	public AbstractTunableInterceptor(HandlerFactory<T> factory) {
		this.factory = factory;
	}

	/**
	 * Calls the <code>process(List&lt;T&gt; handlers)</code> method once it has identified
	 * all {@link Tunable} fields in the object and created appropriate handlers for each field.
	 *
	 * @param command  DOCUMENT ME!
	 */
	public final void intercept(Object command) {
		List<T> handlerList = new LinkedList<T>();

		// Find each public field in the class.
		for (Field field : command.getClass().getFields()) {
			// See if the field is annotated as a Tunable.
			if (field.isAnnotationPresent(Tunable.class)) {
				try {
					Tunable tunable = field.getAnnotation(Tunable.class);

					// Get a handler for this particular field type and
					// add it to the list.
					T handler = factory.getHandler(field, command, tunable);

					if (handler != null)
						handlerList.add(handler);
					else
						System.out.println("No handler for type: " + field.getType().getName());
				} catch (Throwable ex) {
					System.out.println("tunable intercept failed: " + field.toString());
					ex.printStackTrace();
				}
			}
		}

		process(handlerList);
	}

	/**
	 * This method gets executed by the <code>intercept(Object c)</code> method after all
	 * {@link Tunable}s have been extracted. This should NOT be called otherwise!
	 *
	 * @param handlers  DOCUMENT ME!
	 */
	protected abstract void process(List<T> handlers);
}
