/*
 Copyright (c) 2006, 2007, The Cytoscape Consortium (www.cytoscape.org)

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

package org.cytoscape.presentation.internal;

import org.cytoscape.view.model.DependentVisualPropertyCallback;
import org.cytoscape.view.model.VisualProperty;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import java.util.*;


/**
 * A VisualProperty whose values are elements of a discrete set, all
 * implementing the same interface T
 *
 * This VisualProperty is extensible by providing an OSGi service of
 * interface T. (See demo code at....)
 *
 * Note that defaultValue instance also has to be registered as an
 * OSGi service.
 *
 * TODO: will need some events so that UI can handle services being
 * added/removed. (Maybe UI will listen directly to OSGi events, maybe
 * DiscreteVisualProperty will wrap OSGi events, so that UI can be
 * OSGi-agnostic.)
 * @param <T> DOCUMENT ME!
 */
public class DiscreteVisualProperty<T> implements VisualProperty<T> {
	private String id;
	private String name;
	private T defaultValue;
	private Class<T> dataType;
	private String objectType;
	private DependentVisualPropertyCallback callback;
	private BundleContext bc;

	/**
	 * Creates a new DiscreteVisualProperty object.
	 *
	 * @param id  DOCUMENT ME!
	 * @param name  DOCUMENT ME!
	 * @param dataType  DOCUMENT ME!
	 * @param defaultValue  DOCUMENT ME!
	 * @param objectType  DOCUMENT ME!
	 * @param bc  DOCUMENT ME!
	 */
	public DiscreteVisualProperty(final String id, final String name, final Class<T> dataType, final T defaultValue,
	                              final String objectType, final BundleContext bc) {
		this(id, name, dataType, defaultValue, objectType, null, bc);
	}

	/**
	 * Creates a new DiscreteVisualProperty object.
	 *
	 * @param id  DOCUMENT ME!
	 * @param name  DOCUMENT ME!
	 * @param dataType  DOCUMENT ME!
	 * @param defaultValue  DOCUMENT ME!
	 * @param objectType  DOCUMENT ME!
	 * @param callback  DOCUMENT ME!
	 * @param bc  DOCUMENT ME!
	 */
	public DiscreteVisualProperty(final String id, final String name, final Class<T> dataType, final T defaultValue,
	                              final String objectType,
	                              final DependentVisualPropertyCallback callback, final BundleContext bc) {
		this.id = id;
		this.name = name;
		this.defaultValue = defaultValue;
		this.dataType = dataType;
		this.objectType = objectType;
		this.callback = callback;
		this.bc = bc;
	}

	/**
	 * Return all known values
	 *
	 * This method is to allow UI to show a list of values so that user can pick one.
	 *
	 * This implementation simply queries the OSGi framework for all
	 * services implementing dataType interface.
	 * @return DOCUMENT ME!
	 */
	public Set<T> getValues() { // copy-paste-modified from CyEventHelperImpl in core3/model

		final Set<T> ret = new HashSet<T>();

		if (bc == null)
			return ret;

		try {
			final ServiceReference[] sr = bc.getServiceReferences(dataType.getName(), null);

			if (sr != null) {
				for (ServiceReference r : sr) {
					final T value = (T) bc.getService(r);

					if (value != null)
						ret.add(value);
				}
			} else {
				System.out.println("sr is null");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return ret;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String getObjectType() {
		return objectType;
	}

	/**
	 * The type of object represented by this property.
	 * FIXME: should return DiscreteValue instead!!
	 * @return  DOCUMENT ME!
	 */
	public Class<T> getType() {
		// neither of these work, see
		// http://saloon.javaranch.com/cgi-bin/ubb/ultimatebb.cgi?ubb=get_topic&f=1&t=021798
		//return defaultValue.getClass();
		//return T.class;
		// this is needed, and thus the need for 'dataType' in the constructor:
		return dataType;
	}

	/**
	 * The default value of this property.
	 *
	 * @return  DOCUMENT ME!
	 */
	public T getDefault() {
		return defaultValue; //FIXME: defensive copy needed? how to do that?
	}

	/**
	 * Used for hashes identifying this property.
	 *
	 * @return  DOCUMENT ME!
	 */
	public String getID() {
		return id;
	}

	/**
	 * For presentation to humans.
	 *
	 * @return  DOCUMENT ME!
	 */
	public String getName() {
		return name;
	}

	/**
	 *
	 *
	 * @return callback, or null if there isn't one
	 */
	public DependentVisualPropertyCallback dependentVisualPropertyCallback() {
		return callback;
	}
}
