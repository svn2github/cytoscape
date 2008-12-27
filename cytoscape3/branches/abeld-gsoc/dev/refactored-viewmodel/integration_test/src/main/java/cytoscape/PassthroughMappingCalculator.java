
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

package cytoscape;

import org.cytoscape.model.*;

import org.cytoscape.viewmodel.*;

import org.cytoscape.vizmap.*;


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
	public PassthroughMappingCalculator(final String attributeName, final VisualProperty<?> vp) {
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
	public <V extends GraphObject> void apply(final View<V> v, Object defaultValue) {
		CyRow row = v.getSource().attrs();
		if (row.contains(attributeName, vp.getType()) ){
			final Object value = v.getSource().attrs().get(attributeName, vp.getType());
			v.setVisualProperty((VisualProperty<Object>)vp, value);
		} else { // apply per-VS or global default where attribute is not found
			v.setVisualProperty((VisualProperty<Object>)vp, defaultValue);
		}
	}
}
