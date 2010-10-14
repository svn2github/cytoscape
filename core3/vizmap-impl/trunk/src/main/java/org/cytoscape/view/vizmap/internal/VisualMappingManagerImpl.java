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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.cytoscape.event.CyEventHelper;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.view.vizmap.events.VisualStyleDestroyedEvent;


/**
 *
 */
public class VisualMappingManagerImpl implements VisualMappingManager {
	
	private final Map<CyNetworkView, VisualStyle> network2VisualStyleMap;
	private final Set<VisualStyle> visualStyles;
	
	private final CyEventHelper cyEventHelper;
	
	
	public VisualMappingManagerImpl(final CyEventHelper eventHelper) {
		if (eventHelper == null)
			throw new IllegalArgumentException("CyEventHelper cannot be null");

		this.cyEventHelper = eventHelper;

		visualStyles = new HashSet<VisualStyle>();
		network2VisualStyleMap = new HashMap<CyNetworkView, VisualStyle>();
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param nv
	 *            DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	@Override public VisualStyle getVisualStyle(CyNetworkView nv) {
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
	@Override public void setVisualStyle(VisualStyle vs, CyNetworkView nv) {
		network2VisualStyleMap.put(nv, vs);
	}


	/**
	 * Remove the style from this manager and delete it.
	 *
	 * @param vs
	 *            DOCUMENT ME!
	 */
	@Override public void removeVisualStyle(VisualStyle vs) {
		visualStyles.remove(vs);
		cyEventHelper.fireSynchronousEvent(new VisualStyleDestroyedEvent(this,vs));
		vs = null;
	}
	

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	@Override public Set<VisualStyle> getAllVisualStyles() {
		return visualStyles;
	}

	@Override public void addVisualStyle(VisualStyle vs) {
		this.visualStyles.add(vs);
	}
}
