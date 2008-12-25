
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

import org.cytoscape.viewmodel.DependentVisualPropertyCallback;
import org.cytoscape.viewmodel.VisualProperty;


/**
 * FIXME
 * Think of it as a column in the viewmodel table.
 * @param <T> DOCUMENT ME!
 */
public class VisualPropertyImpl<T> implements VisualProperty<T> {
	private String id;
	private String name;
	private T defaultValue;
	private Class<T> dataType;
	private VisualProperty.GraphObjectType objectType;
	private DependentVisualPropertyCallback callback;

	/**
	 * Creates a new VisualPropertyImpl object.
	 *
	 * @param id  DOCUMENT ME!
	 * @param name  DOCUMENT ME!
	 * @param defaultValue  DOCUMENT ME!
	 * @param dataType  DOCUMENT ME!
	 * @param objectType  DOCUMENT ME!
	 */
	public VisualPropertyImpl(final String id, final String name, final T defaultValue, final Class<T> dataType,
	                          final VisualProperty.GraphObjectType objectType) {
		this(id, name, defaultValue, dataType, objectType, null);
	}

	/**
	 * Creates a new VisualPropertyImpl object.
	 *
	 * @param id  DOCUMENT ME!
	 * @param name  DOCUMENT ME!
	 * @param defaultValue  DOCUMENT ME!
	 * @param dataType  DOCUMENT ME!
	 * @param objectType  DOCUMENT ME!
	 * @param callback  DOCUMENT ME!
	 */
	public VisualPropertyImpl(final String id, final String name, final T defaultValue, final Class<T> dataType,
	                          final VisualProperty.GraphObjectType objectType,
	                          final DependentVisualPropertyCallback callback) {
		this.id = id;
		this.name = name;
		this.defaultValue = defaultValue;
		this.dataType = dataType;
		this.objectType = objectType;
		this.callback = callback;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public VisualProperty.GraphObjectType getObjectType() {
		return objectType;
	}

	/**
	 * The type of object represented by this property.
	 *
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
