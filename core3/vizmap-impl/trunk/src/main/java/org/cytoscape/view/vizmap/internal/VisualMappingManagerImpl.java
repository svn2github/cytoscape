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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.cytoscape.event.CyEventHelper;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.RootVisualLexicon;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.vizmap.VisualMappingFunction;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.view.vizmap.events.VisualStyleCreatedListener;
import org.cytoscape.view.vizmap.events.VisualStyleDestroyedListener;
import org.cytoscape.view.vizmap.internal.events.VisualStyleCreatedEventImpl;
import org.cytoscape.view.vizmap.internal.events.VisualStyleDestroyedEventImpl;


/**
 *
 */
public class VisualMappingManagerImpl implements VisualMappingManager {
	private final Map<CyNetworkView, VisualStyle> vsForNetwork;
	private final Set<VisualStyle> visualStyles;
	private CyEventHelper cyEventHelper;
	private RootVisualLexicon rootLexicon;
	
	/**
	 *
	 * @param h
	 *            DOCUMENT ME!
	 */
	public VisualMappingManagerImpl(final CyEventHelper eventHelper,
	                                final RootVisualLexicon rootLexicon) {
		if (eventHelper == null)
			throw new IllegalArgumentException("CyEventHelper cannot be null");

		if (rootLexicon == null)
			throw new IllegalArgumentException("vpCatalog cannot be null");

		this.cyEventHelper = eventHelper;
		this.rootLexicon = rootLexicon;

		visualStyles = new HashSet<VisualStyle>();
		vsForNetwork = new HashMap<CyNetworkView, VisualStyle>();
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param nv
	 *            DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public VisualStyle getVisualStyle(CyNetworkView nv) {
		return vsForNetwork.get(nv);
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param vs
	 *            DOCUMENT ME!
	 * @param nv
	 *            DOCUMENT ME!
	 */
	public void setVisualStyle(VisualStyle vs, CyNetworkView nv) {
		vsForNetwork.put(nv, vs);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param originalVS DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public VisualStyle copyVisualStyle(VisualStyle originalVS) {
		final VisualStyle copyVS = new VisualStyleImpl(rootLexicon, new String(originalVS.getTitle()));

		// TODO: copy everything! This is incomplete
		Collection<VisualMappingFunction<?, ?>> allMapping = originalVS.getAllVisualMappingFunctions();

		String attrName;
		VisualProperty<?> vp;

		for (VisualMappingFunction<?, ?> mapping : allMapping) {
			attrName = mapping.getMappingAttributeName();
			vp = mapping.getVisualProperty();
		}

		visualStyles.add(copyVS);
		cyEventHelper.fireSynchronousEvent(new VisualStyleCreatedEventImpl(copyVS),
		                                   VisualStyleCreatedListener.class);

		return copyVS;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param title DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public VisualStyle createVisualStyle(String title) {
		final VisualStyle newVS = new VisualStyleImpl(rootLexicon, title);
		visualStyles.add(newVS);
		cyEventHelper.fireSynchronousEvent(new VisualStyleCreatedEventImpl(newVS),
		                                   VisualStyleCreatedListener.class);

		return newVS;
	}

	/**
	 * Remove the style from this manager and delete it.
	 *
	 * @param vs
	 *            DOCUMENT ME!
	 */
	public void removeVisualStyle(VisualStyle vs) {
		visualStyles.remove(vs);
		cyEventHelper.fireSynchronousEvent(new VisualStyleDestroyedEventImpl(vs),
		                                   VisualStyleDestroyedListener.class);
		vs = null;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Collection<VisualStyle> getAllVisualStyles() {
		return visualStyles;
	}

	public RootVisualLexicon getRendererCatalog() {
		// TODO Auto-generated method stub
		return null;
	}
}
