
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
package org.cytoscape.view.model;

/**
 * An abstract implementation of VisualProperty that omits the methods
 * dealing with serializing data.  
 */
public abstract class AbstractVisualProperty<T> implements VisualProperty<T> {

	final protected String ot;
	final protected T def;
	final protected String id;
	final protected String name;

	public AbstractVisualProperty(final String ot, final T def, final String id, final String name) {
		this.ot = ot;
		this.def = def;
		this.id = id;
		this.name = name;
	}

	public String getObjectType() { return  ot; }

	@SuppressWarnings("unchecked")
	public Class<T> getType() { 
		if(def != null)
			return (Class<T>) def.getClass();
		else return null;
	}

	public T getDefault() { return def; }

	public String getIdString() { return id; }

	public String getDisplayName() { return name; }

	public DependentVisualPropertyCallback dependentVisualPropertyCallback() { return null; }
}
