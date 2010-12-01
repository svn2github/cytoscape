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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTableEntry;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.presentation.property.TwoDVisualLexicon;
import org.cytoscape.view.vizmap.VisualMappingFunction;
import org.cytoscape.view.vizmap.VisualStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 */
public class VisualStyleImpl implements VisualStyle {

	private static final Logger logger = LoggerFactory
			.getLogger(VisualStyleImpl.class);

	private static final String DEFAULT_TITLE = "?";

	private final Map<VisualProperty<?>, VisualMappingFunction<?, ?>> mappings;
	private final Map<VisualProperty<?>, Object> perVSDefaults;

	private final VisualLexicon lexicon;
	
	final Collection<VisualProperty<?>> nodeVPs;
	final Collection<VisualProperty<?>> edgeVPs;
	final Collection<VisualProperty<?>> networkVPs;

	private String title;

	/**
	 * Creates a new VisualStyleImpl object.
	 * 
	 * @param rootLexicon
	 *            DOCUMENT ME!
	 */
	public VisualStyleImpl(final VisualLexicon lexicon) {
		this(null, lexicon);
	}

	/**
	 * Creates a new VisualStyleImpl object.
	 * 
	 * @param eventHelper
	 *            DOCUMENT ME!
	 * @param rootLexicon
	 *            DOCUMENT ME!
	 */
	public VisualStyleImpl(final String title, final VisualLexicon lexicon) {
		if (lexicon == null)
			throw new NullPointerException("Lexicon is null");

		if (title == null)
			this.title = DEFAULT_TITLE;
		else
			this.title = title;

		this.lexicon = lexicon;
		mappings = new HashMap<VisualProperty<?>, VisualMappingFunction<?, ?>>();
		perVSDefaults = new HashMap<VisualProperty<?>, Object>();
		
		for(VisualProperty<?> vp: lexicon.getAllVisualProperties())
			perVSDefaults.put(vp, vp.getDefault());
		
		// Node-related Visual Properties are linked as a children of NODE VP.
		nodeVPs = lexicon.getAllDescendants(TwoDVisualLexicon.NODE);
		
		// Node-related Visual Properties are linked as a children of NODE VP.
		edgeVPs = lexicon.getAllDescendants(TwoDVisualLexicon.EDGE);
		
		networkVPs = new HashSet<VisualProperty<?>>();
		for(VisualProperty<?> vp: lexicon.getAllVisualProperties()) {
			if(!nodeVPs.contains(vp) && !edgeVPs.contains(vp))
				networkVPs.add(vp);
		}

		logger.info("New Visual Style Created: Style Name = " + this.title);
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param c
	 *            DOCUMENT ME!
	 */
	public void addVisualMappingFunction(
			final VisualMappingFunction<?, ?> mapping) {
		mappings.put(mapping.getVisualProperty(), mapping);
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param <V>
	 *            DOCUMENT ME!
	 * @param t
	 *            DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	@SuppressWarnings("unchecked")
	public <V> VisualMappingFunction<?, V> getVisualMappingFunction(
			VisualProperty<V> t) {
		return (VisualMappingFunction<?, V>) mappings.get(t);
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param <V>
	 *            DOCUMENT ME!
	 * @param t
	 *            DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void removeVisualMappingFunction(VisualProperty<?> t) {
		mappings.remove(t);
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param <T>
	 *            DOCUMENT ME!
	 * @param vp
	 *            DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <V> V getDefaultValue(final VisualProperty<V> vp) {
		return (V) perVSDefaults.get(vp);
	}

	/**
	 * Set the default value for a Visual Property
	 * 
	 * @param <T>
	 *            Default value data type.
	 * @param vp
	 *            DOCUMENT ME!
	 * @param value
	 *            DOCUMENT ME!
	 */
	@Override
	public <V, S extends V> void setDefaultValue(final VisualProperty<V> vp,
			final S value) {
		perVSDefaults.put(vp, value);
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param networkView
	 *            DOCUMENT ME!
	 */
	@Override
	public void apply(final CyNetworkView networkView) {
		if (networkView == null) {
			logger.warn("Tried to apply Visual Style to null view");
			return;
		}

		logger.debug("Visual Style Apply method called: " + this.title);
		
		final Collection<View<CyNode>> nodeViews = networkView.getNodeViews();
		final Collection<View<CyEdge>> edgeViews = networkView.getEdgeViews();
		final Collection<View<CyNetwork>> networkViewSet = new HashSet<View<CyNetwork>>();
		networkViewSet.add(networkView);
		
		// Current visual prop tree.
		applyImpl(nodeViews, nodeVPs);
				
		applyImpl(edgeViews, edgeVPs);
		applyImpl(networkViewSet, networkVPs);
		
		logger.debug("Visual Style applied: " + this.title + "\n");
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param <T>
	 *            DOCUMENT ME!
	 * @param views
	 *            DOCUMENT ME!
	 * @param visualProperties
	 *            DOCUMENT ME!
	 */
	private void applyImpl(
			final Collection<? extends View<?>> views,
			final Collection<VisualProperty<?>> visualProperties) {	

		for (VisualProperty<?> vp : visualProperties)
			applyToView(views, vp);
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param <T>
	 *            DOCUMENT ME!
	 * @param views
	 *            DOCUMENT ME!
	 * @param visualProperties
	 *            DOCUMENT ME!
	 */
	private void applyToView(
			final Collection<? extends View<?>> views,
			final VisualProperty<?> vp) {

		final VisualMappingFunction<?, ?> mapping = getVisualMappingFunction(vp);

		if (mapping != null) {
			// Mapping is available for this VP. Apply it.
			logger.debug("###### Mapping found for " + vp.getDisplayName() + ": " + mapping.toString());
			final Object styleDefaultValue = getDefaultValue(vp);
			for (View<?> view : views) {
				mapping.apply((View<? extends CyTableEntry>) view);
				
				if(view.getVisualProperty(vp) == vp.getDefault())
					view.setVisualProperty(vp, styleDefaultValue);
			}
		} else if (!vp.shouldIgnoreDefault()) {
			// Ignore defaults flag is OFF. Apply defaults.
			applyStyleDefaults((Collection<View<?>>) views, vp);
		} else if(lexicon.getVisualLexiconNode(vp).getChildren().size() == 0){
			Object defVal = getDefaultValue(vp);
			for (View<?> view : views) {
				Object val = view.getVisualProperty(vp);
				//logger.debug(vp.getDisplayName() + ": Ignore flag.  Val = " + val);
				//logger.debug(vp.getDisplayName() + ": DEF Val = " + defVal);
				if(defVal.equals(val) == false)
					view.setVisualProperty(vp, val);
			}
		}
	}

	private void applyStyleDefaults(
			final Collection<View<?>> views,
			final VisualProperty<?> vp) {

		Object defaultValue = getDefaultValue(vp);
		
		// reset all rows to allow usage of default value:
		for (final View<?> viewModel : views) {
			
			// Not a leaf VP. We can ignore those.
			if (lexicon.getVisualLexiconNode(vp).getChildren().size() != 0)
				continue;
			
			final Object currentValue = viewModel.getVisualProperty(vp);
			
//			// Some of the VP has null defaults.
//			if (currentValue == null)
//				continue;

//			// If equals, it is not necessary to set new value.
//			if (currentValue.equals(defaultValue))
//				continue;
//
//			

			// This is a leaf, and need to be updated.
			viewModel.setVisualProperty(vp, defaultValue);
			
			//logger.debug(vp.getDisplayName() + " updated from: " + currentValue + " to " + defaultValue);
		}
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param title
	 *            DOCUMENT ME!
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * toString method returns title of this Visual Style.
	 * 
	 * @return DOCUMENT ME!
	 */
	@Override
	public String toString() {
		return this.title;
	}

	@Override
	public Collection<VisualMappingFunction<?, ?>> getAllVisualMappingFunctions() {
		return mappings.values();
	}

	// TODO Is this the right set of lexicon?
	@Override
	public VisualLexicon getVisualLexicon() {
		return lexicon;
	}

}
