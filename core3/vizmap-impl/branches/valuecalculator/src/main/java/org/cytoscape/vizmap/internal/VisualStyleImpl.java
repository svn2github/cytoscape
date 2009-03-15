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
package org.cytoscape.vizmap.internal;

import org.cytoscape.event.CyEventHelper;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.GraphObject;

import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.ViewColumn;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.model.VisualPropertyCatalog;

import org.cytoscape.vizmap.MappingCalculator;
import org.cytoscape.vizmap.VisualStyle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 */
public class VisualStyleImpl implements VisualStyle {
	private Map<VisualProperty<?>, MappingCalculator> calculators;
	private Map<VisualProperty<?>, Object> perVSDefaults;
	private CyEventHelper eventHelper;
	private VisualPropertyCatalog vpCatalog;

	/**
	 * Creates a new VisualStyleImpl object.
	 *
	 * @param eventHelper  DOCUMENT ME!
	 * @param vpCatalog  DOCUMENT ME!
	 */
	public VisualStyleImpl(final CyEventHelper eventHelper, final VisualPropertyCatalog vpCatalog) {
		if (eventHelper == null)
			throw new NullPointerException("CyEventHelper is null");

		if (vpCatalog == null)
			throw new NullPointerException("vpCatalog is null");

		this.eventHelper = eventHelper;
		this.vpCatalog = vpCatalog;
		calculators = new HashMap<VisualProperty<?>, MappingCalculator>();
		perVSDefaults = new HashMap<VisualProperty<?>, Object>();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param c DOCUMENT ME!
	 */
	public void setMappingCalculator(final MappingCalculator c) {
		calculators.put(c.getVisualProperty(), c);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param t DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public MappingCalculator getMappingCalculator(final VisualProperty<?> t) {
		return calculators.get(t);
	}

	public MappingCalculator removeMappingCalculator(final VisualProperty<?> t) {
		return calculators.remove(t);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param <T> DOCUMENT ME!
	 * @param vp DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public <T> T getDefaultValue(final VisualProperty<T> vp) {
		return (T) perVSDefaults.get(vp);
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

	/**
	 *  {@inheritDoc}
	 */
	public void apply(final CyNetworkView view) {
		final List<View<CyNode>> nodeviews = view.getCyNodeViews();
		final List<View<CyEdge>> edgeviews = view.getCyEdgeViews();

		applyImpl(view, nodeviews,
		          vpCatalog.collectionOfVisualProperties(nodeviews, VisualProperty.NODE));
		applyImpl(view, edgeviews,
		          vpCatalog.collectionOfVisualProperties(edgeviews, VisualProperty.EDGE));
		applyImpl(view, Arrays.asList((View<CyNetwork>)view),
		          vpCatalog.collectionOfVisualProperties(VisualProperty.NETWORK));
	}

	// note: can't use applyImpl(List<View<?>>views ... ) because that does not compile
	/**
	 *
	 */
	public <T extends GraphObject> void applyImpl(final CyNetworkView view, final List<View<T>> views,
	                                              final Collection<? extends VisualProperty<?>> visualProperties) {
		for (VisualProperty<?> vp: visualProperties){
			applyImpl(view, views, vp);
		}
	}
	/**
	 *  I think this needs to be in a separate method to allow making it generic -- abeld
	 *
	 * @param <T> DOCUMENT ME!
	 * @param views DOCUMENT ME!
	 * @param visualProperties DOCUMENT ME!
	 */
	public <T, V extends GraphObject> void applyImpl(final CyNetworkView view, final List<View<V>> views, final VisualProperty<T> vp) {
		ViewColumn<T> column = view.getColumn(vp);
		final MappingCalculator c = getMappingCalculator(vp);
		final T perVSDefault = getDefaultValue(vp);
		if (perVSDefault != null){
			column.setDefaultValue(perVSDefault);
		}
		if (c != null) {
			String attrName = c.getMappingAttributeName();
			if (views.size() < 1)
				return; // empty list, nothing to do
			CyRow row = views.get(0).getSource().attrs(); // to check types, have to peek at first view instance
			// check types:
			Class<?> attrType = row.getDataTable().getColumnTypeMap().get(attrName);
			Class<T> vpType = vp.getType();

			if (true) {
				doMap(views, c, attrName, attrType, column, vpType);
			} else {
				throw new IllegalArgumentException("Mapping "+toString()+" can't map from attribute type "+attrType+" to VisualProperty "+vp+" of type "+vp.getType());
			}
		} else {
			column.setValues(new HashMap<View<V>, T>(), views);
		}
	}
	/**
	 * I think this needs to be in a separate method to allow making it generic -- abeld
	 *
	 * @param T the VisualProperty type being mapped to
	 * @param A the attribute type being mapped from
	 * @param V the view type for which the mapping is made
	 */
	private <T, A, V extends GraphObject> void doMap(final List<? extends View<V>> views,  MappingCalculator calc,
			String attrName, Class<A> attrType, ViewColumn<T> column, Class<T> visualType){
		// aggregate changes to be made in these:
		Map<View<V>, T> valuesToSet = new HashMap<View<V>, T>();
		List<View<V>> valuesToClear = new ArrayList<View<V>>();

		for (View<V> v: views){
			CyRow row = v.getSource().attrs();
			if (row.contains(attrName, attrType) ){ // skip Views where source attribute is not defined;
					 								// ViewColumn will automatically substitute the per-VS or global default, as appropriate
				T value = calc.valueFor((A) v.getSource().attrs().get(attrName, attrType), visualType);
				if (value != null){ // 'null' value mean 'revert to default'
					valuesToSet.put(v, value);
				} else { // remove value so that default value will be used:
					valuesToClear.add(v);
				}					
			} else { // remove value so that default value will be used:
				valuesToClear.add(v);
			}
		}
		column.setValues(valuesToSet, valuesToClear);
	}
}
