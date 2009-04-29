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
package org.cytoscape.view.vizmap.internal;

import static org.cytoscape.model.GraphObject.EDGE;
import static org.cytoscape.model.GraphObject.NETWORK;
import static org.cytoscape.model.GraphObject.NODE;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.GraphObject;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.RootVisualLexicon;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.ViewColumn;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.vizmap.VisualMappingFunction;
import org.cytoscape.view.vizmap.VisualStyle;


/**
 */
public class VisualStyleImpl implements VisualStyle {
	private Map<VisualProperty<?>, VisualMappingFunction<?, ?>> mappings;
	private Map<VisualProperty<?>, Object> perVSDefaults;
	private RootVisualLexicon rootLexicon;
	private static final String DEFAULT_TITLE = "?";
	private String title;

	/**
	 * Creates a new VisualStyleImpl object.
	 *
	 * @param rootLexicon  DOCUMENT ME!
	 */
	public VisualStyleImpl(final RootVisualLexicon rootLexicon) {
		this(rootLexicon, null);
	}

	/**
	 * Creates a new VisualStyleImpl object.
	 *
	 * @param eventHelper  DOCUMENT ME!
	 * @param rootLexicon  DOCUMENT ME!
	 */
	public VisualStyleImpl(final RootVisualLexicon rootLexicon, final String title) {
		if (rootLexicon == null)
			throw new NullPointerException("rootLexicon is null");

		if (title == null)
			this.title = DEFAULT_TITLE;
		else
			this.title = title;

		this.rootLexicon = rootLexicon;
		mappings = new HashMap<VisualProperty<?>, VisualMappingFunction<?, ?>>();
		perVSDefaults = new HashMap<VisualProperty<?>, Object>();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param c DOCUMENT ME!
	 */
	public void addVisualMappingFunction(final VisualMappingFunction<?, ?> mapping) {
		mappings.put(mapping.getVisualProperty(), mapping);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param <V> DOCUMENT ME!
	 * @param t DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	@SuppressWarnings("unchecked")
	public <V> VisualMappingFunction<?, V> getVisualMappingFunction(VisualProperty<V> t) {
		return (VisualMappingFunction<?, V>) mappings.get(t);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param <V> DOCUMENT ME!
	 * @param t DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	@SuppressWarnings("unchecked")
	public <V> VisualMappingFunction<?, V> removeVisualMappingFunction(VisualProperty<V> t) {
		return (VisualMappingFunction<?, V>) mappings.remove(t);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param <T> DOCUMENT ME!
	 * @param vp DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	@SuppressWarnings("unchecked")
	public <V> V getDefaultValue(final VisualProperty<V> vp) {
		// Since setter checks type, this cast is always legal.
		return (V) perVSDefaults.get(vp);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param <T> DOCUMENT ME!
	 * @param vp DOCUMENT ME!
	 * @param value DOCUMENT ME!
	 */
	public <T> void setDefaultValue(final VisualProperty<T> vp, final T value) {
		perVSDefaults.put(vp, value);
	}

	// ??
	/**
	 *  DOCUMENT ME!
	 *
	 * @param view DOCUMENT ME!
	 */
	public void apply(final CyNetworkView view) {
		final List<View<CyNode>> nodeviews = view.getNodeViews();
		final List<View<CyEdge>> edgeviews = view.getEdgeViews();

		applyImpl(view, nodeviews,
		          rootLexicon.getVisualProperties(nodeviews, NODE));
		applyImpl(view, edgeviews,
		          rootLexicon.getVisualProperties(edgeviews, EDGE));
		applyImpl(view, Arrays.asList((View<CyNetwork>) view),
		          rootLexicon.getVisualProperties(NETWORK));
	}

	// note: can't use applyImpl(List<View<?>>views ... ) because that does not compile
	/**
	 *  DOCUMENT ME!
	 *
	 * @param <T> DOCUMENT ME!
	 * @param views DOCUMENT ME!
	 * @param visualProperties DOCUMENT ME!
	 */
	public <G extends GraphObject> void applyImpl(final CyNetworkView view,
	                                              final List<View<G>> views,
	                                              final Collection<?extends VisualProperty<?>> visualProperties) {
		for (VisualProperty<?> vp : visualProperties)
			applyImpl(view, views, vp);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param <T> DOCUMENT ME!
	 * @param views DOCUMENT ME!
	 * @param visualProperties DOCUMENT ME!
	 */
	public <V, G extends GraphObject> void applyImpl(final CyNetworkView view,
	                                                 final List<View<G>> views,
	                                                 final VisualProperty<V> vp) {
		ViewColumn<V> column = view.getColumn(vp);
		final VisualMappingFunction<?, V> c = getVisualMappingFunction(vp);
		final V perVSDefault = getDefaultValue(vp);

		if (perVSDefault != null) {
			column.setDefaultValue(perVSDefault);
		}

		if (c != null) {
			c.apply(column, views);
		} else {
			// reset all rows to allow usage of default value:
			column.setValues(new HashMap<View<G>, V>(), views);
		}
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String getTitle() {
		return title;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param title DOCUMENT ME!
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 *  toString method returns title of this Visual Style.
	 *
	 * @return  DOCUMENT ME!
	 */
	@Override
	public String toString() {
		return this.title;
	}

	public Collection<VisualMappingFunction<?,?>> getAllVisualMappingFunctions() {
		return mappings.values();
	}

	public VisualLexicon getRenderer() {
		// TODO Auto-generated method stub
		return null;
	}

}
