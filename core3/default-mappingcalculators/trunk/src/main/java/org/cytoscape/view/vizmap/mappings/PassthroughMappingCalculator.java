
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

package org.cytoscape.view.vizmap.mappings;

import org.cytoscape.model.*;

import org.cytoscape.view.model.*;

import org.cytoscape.view.vizmap.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 */
public class PassthroughMappingCalculator implements MappingCalculator {
	private String attributeName;
	private VisualProperty<?> vp;

	/**
	 * dataType is the type of the _attribute_ !!
	 * currently we force that to be the same as the VisualProperty;
	 * FIXME: allow different once? but how to coerce?
	 */
	public <T> PassthroughMappingCalculator(final String attributeName, final VisualProperty<T> vp) {
		this.attributeName = attributeName;
		this.vp = vp;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param attributeName DOCUMENT ME!
	 */
	public void setMappingAttributeName(final String attributeName) {
		this.attributeName = attributeName;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String getMappingAttributeName() {
		return attributeName;
	}

	/**
	 * The visual property the attribute gets mapped to.
	 *
	 * @param vp  DOCUMENT ME!
	 */
	public void setVisualProperty(final VisualProperty<?> vp) {
		this.vp = vp;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public VisualProperty<?> getVisualProperty() {
		return vp;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param v DOCUMENT ME!
	 */
	public <T, V extends GraphObject> void apply(ViewColumn<T> column, List<? extends View<V>> views){
		if (views.size() < 1)
			return; // empty list, nothing to do
		CyRow row = views.get(0).getSource().attrs();
		Class<?> attrType = row.getDataTable().getColumnTypeMap().get(attributeName);
		// since attributes can only store certain types of Objects, it is enough to test for these:
		Class<?> vpType = vp.getType();
		// FIXME: also check that column's vp is internally-stored vp!
		if (vpType.isAssignableFrom(attrType)){ // can simply copy object without any conversion
			// aggregate changes to be made in these:
			Map<View<V>, T> valuesToSet = new HashMap<View<V>, T>();
			List<View<V>> valuesToClear = new ArrayList<View<V>>();
			for (View<V> v: views){
				row = v.getSource().attrs();
				if (row.contains(attributeName, attrType) ){
					// skip Views where source attribute is not defined; ViewColumn will automatically substitute the per-VS or global default, as appropriate 
					final T value = (T) row.get(attributeName, attrType);
					valuesToSet.put(v, value);
				} else { // remove value so that default value will be used:
					valuesToClear.add(v);
				}
			}
			column.setValues(valuesToSet, valuesToClear);
		} else if (String.class.isAssignableFrom(vpType)){
			// can convert any object to string, so no need to check attribute type
			// also, since we have to convert the Object here, can't use checkAndDoCopy()
			ViewColumn<String> c = (ViewColumn<String>) column; // have  to cast here, even though previous check ensures that T is java.util.String

			// aggregate changes to be made in these:
			Map<View<V>, String> valuesToSet = new HashMap<View<V>, String>();
			List<View<V>> valuesToClear = new ArrayList<View<V>>();

			for (View<V> v: views){
				row = v.getSource().attrs();
				if (row.contains(attributeName, attrType) ){
					// skip Views where source attribute is not defined; ViewColumn will automatically substitute the per-VS or global default, as appropriate 
					final Object value = (Object) row.get(attributeName, attrType);
					valuesToSet.put(v, value.toString());
				} else { // remove value so that default value will be used:
					valuesToClear.add(v);
				}
			}
			c.setValues(valuesToSet, valuesToClear);
		} else {	
			throw new IllegalArgumentException("Mapping "+toString()+" can't map from attribute type "+attrType+" to VisualProperty "+vp+" of type "+vp.getType()); 
		}
		// FIXME: handle List & Map
	}
}
