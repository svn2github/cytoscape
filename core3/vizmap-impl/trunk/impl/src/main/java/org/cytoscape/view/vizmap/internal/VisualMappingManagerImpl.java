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
package org.cytoscape.view.vizmap.internal;

import java.awt.Color;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.cytoscape.event.CyEventHelper;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.presentation.property.TwoDVisualLexicon;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.view.vizmap.VisualStyleFactory;
import org.cytoscape.view.vizmap.events.VisualStyleAboutToBeRemovedEvent;
import org.cytoscape.view.vizmap.events.VisualStyleAddedEvent;

/**
 *
 */
public class VisualMappingManagerImpl implements VisualMappingManager {
	public static final String DEFAULT_STYLE_NAME = "Default";
	
	// Default Style
	private static final Color NODE_COLOR = new Color(0x4F, 0x94, 0xCD);
	private static final Color EDGE_COLOR = new Color(50, 50, 50);
	private static final Double EDGE_WIDTH = 3d;
	private static final Double NODE_WIDTH = 35d;
	private static final Double NODE_HEIGHT = 35d;

	private final VisualStyle defaultStyle;

	private final Map<CyNetworkView, VisualStyle> network2VisualStyleMap;
	private final Set<VisualStyle> visualStyles;

	private final CyEventHelper cyEventHelper;
	private final VisualLexiconManager lexManager;

	public VisualMappingManagerImpl(final CyEventHelper eventHelper,
			final VisualStyleFactory factory, final VisualLexiconManager lexManager) {
		if (eventHelper == null)
			throw new NullPointerException("CyEventHelper cannot be null");

		this.cyEventHelper = eventHelper;
		this.lexManager = lexManager;

		visualStyles = new HashSet<VisualStyle>();
		network2VisualStyleMap = new HashMap<CyNetworkView, VisualStyle>();

		this.defaultStyle = buildGlobalDefaultStyle(factory);
		this.visualStyles.add(defaultStyle);
	}
	
	private VisualStyle buildGlobalDefaultStyle(final VisualStyleFactory factory) {
		final VisualStyle defStyle = factory.getInstance(DEFAULT_STYLE_NAME);
		
		defStyle.setDefaultValue(TwoDVisualLexicon.NODE_COLOR, NODE_COLOR);
		defStyle.setDefaultValue(TwoDVisualLexicon.NODE_X_SIZE, NODE_WIDTH);
		defStyle.setDefaultValue(TwoDVisualLexicon.NODE_Y_SIZE, NODE_HEIGHT);
		defStyle.setDefaultValue(TwoDVisualLexicon.EDGE_WIDTH, EDGE_WIDTH);
		defStyle.setDefaultValue(TwoDVisualLexicon.EDGE_PAINT, EDGE_COLOR);
		
		return defStyle;
	}

	/**
	 * Get VisualStyle for the given view model.
	 * 
	 * @param nv
	 *            DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	@Override
	public VisualStyle getVisualStyle(CyNetworkView nv) {
		if (nv == null)
			throw new NullPointerException("network view is null.");

		if (network2VisualStyleMap.containsKey(nv) == false) {
			if (this.defaultStyle == null)
				throw new IllegalStateException(
						"No rendering engine is available, and cannot create default style!");

			// Not registered yet. Provide default style.
			network2VisualStyleMap.put(nv, defaultStyle);
			return defaultStyle;
		}
		return network2VisualStyleMap.get(nv);
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param vs
	 *            DOCUMENT ME!
	 * @param nv
	 *            DOCUMENT ME!
	 */
	@Override
	public void setVisualStyle(final VisualStyle vs, final CyNetworkView nv) {
		if (nv == null)
			throw new NullPointerException("Network view is null.");

		if (vs == null)
			network2VisualStyleMap.remove(nv);
		else
			network2VisualStyleMap.put(nv, vs);

		if (this.visualStyles.contains(vs) == false)
			this.visualStyles.add(vs);
	}

	/**
	 * Remove a {@linkplain VisualStyle} from this manager. This will be called
	 * through OSGi service mechanism.
	 * 
	 * @param vs
	 *            DOCUMENT ME!
	 */
	@Override
	public void removeVisualStyle(VisualStyle vs) {
		if (vs == null)
			throw new NullPointerException("Visual Style is null.");
		if (vs == defaultStyle)
			throw new IllegalArgumentException(
					"Cannot remove default visual style.");

		
		// Use default for all views using this vs.
		if (this.network2VisualStyleMap.values().contains(vs)) {
			for(final CyNetworkView view: network2VisualStyleMap.keySet()) {
				if(network2VisualStyleMap.get(view).equals(vs))
					network2VisualStyleMap.put(view, defaultStyle);
			}
		}
		
		cyEventHelper
				.fireSynchronousEvent(new VisualStyleAboutToBeRemovedEvent(
						this, vs));
		visualStyles.remove(vs);
		vs = null;
	}

	/**
	 * Add a new VisualStyle ot this manager. This will be called through OSGi
	 * service mechanism.
	 * 
	 * @param vs
	 */
	@Override
	public void addVisualStyle(final VisualStyle vs) {
		this.visualStyles.add(vs);
		cyEventHelper.fireSynchronousEvent(new VisualStyleAddedEvent(this, vs));
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	@Override
	public Set<VisualStyle> getAllVisualStyles() {
		return visualStyles;
	}

	@Override
	public VisualStyle getDefaultVisualStyle() {
		return defaultStyle;
	}

	@Override
	public Set<VisualLexicon> getAllVisualLexicon() {
		return lexManager.getAllVisualLexicon();
	}
}
