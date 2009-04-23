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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.cytoscape.event.CyEventHelper;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.model.VisualPropertyCatalog;
import org.cytoscape.view.vizmap.VisualMappingFunction;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.view.vizmap.VisualStyleCatalog;
import org.cytoscape.view.vizmap.events.VisualStyleCreatedListener;
import org.cytoscape.view.vizmap.events.VisualStyleDestroyedListener;
import org.cytoscape.view.vizmap.internal.events.VisualStyleCreatedEventImpl;
import org.cytoscape.view.vizmap.internal.events.VisualStyleDestroyedEventImpl;


/**
 * We need a list of currently-used VisualStyles somewhere (?)
 * This is it.
 * It is also a VisualStyle factory
 */
public class VisualStyleCatalogImpl implements VisualStyleCatalog {
	private final Set<VisualStyle> visualStyles;
	private CyEventHelper cyEventHelper;
	private VisualPropertyCatalog vpCatalog;
	
	/**
	 * For setter injection (hmm. whats that?)
	 */
	public VisualStyleCatalogImpl() {
		visualStyles = new HashSet<VisualStyle>();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param eventHelper DOCUMENT ME!
	 */
	public void setEventHelper(final CyEventHelper eventHelper) {
		this.cyEventHelper = eventHelper;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public CyEventHelper getEventHelper() {
		return this.cyEventHelper;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param vpCatalog DOCUMENT ME!
	 */
	public void setVisualPropertyCatalog(final VisualPropertyCatalog vpCatalog) {
		this.vpCatalog = vpCatalog;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public VisualPropertyCatalog getVisualPropertyCatalog() {
		return this.vpCatalog;
	}

	/**
	 * 
	 * @param h  DOCUMENT ME!
	 */
	public VisualStyleCatalogImpl(final CyEventHelper eventHelper,
	                              final VisualPropertyCatalog vpCatalog) {
		if (eventHelper == null)
			throw new NullPointerException("CyEventHelper is null");

		if (vpCatalog == null)
			throw new NullPointerException("vpCatalog is null");

		this.cyEventHelper = eventHelper;
		this.vpCatalog = vpCatalog;
		visualStyles = new HashSet<VisualStyle>();
	}
	
	public VisualStyle createVisualStyle(String title) {
		final VisualStyle newVS = new VisualStyleImpl(vpCatalog, title);
		visualStyles.add(newVS);
		cyEventHelper.fireSynchronousEvent(new VisualStyleCreatedEventImpl(newVS),
		                                 VisualStyleCreatedListener.class);

		return newVS;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public List<VisualStyle> listOfVisualStyles() {
		return new ArrayList<VisualStyle>(visualStyles);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param vs DOCUMENT ME!
	 */
	public void removeVisualStyle(final VisualStyle vs) {
		visualStyles.remove(vs);
		cyEventHelper.fireSynchronousEvent(new VisualStyleDestroyedEventImpl(vs),
		                                 VisualStyleDestroyedListener.class);
	}

	public VisualStyle copyVisualStyle(VisualStyle originalVS) {
		
		final VisualStyle copyVS = new VisualStyleImpl(vpCatalog, new String(originalVS.getTitle()));
		//TODO: copy everything!
		Collection<VisualMappingFunction<?, ?>> allMapping = originalVS.getAllVisualMappingFunctions();
		
		String attrName;
		VisualProperty<?> vp;
		for(VisualMappingFunction<?,?> mapping:allMapping) {
			attrName = mapping.getMappingAttributeName();
			vp = mapping.getVisualProperty();
			
		}
		
		visualStyles.add(copyVS);
		cyEventHelper.fireSynchronousEvent(new VisualStyleCreatedEventImpl(copyVS),
		                                 VisualStyleCreatedListener.class);
		return copyVS;
	}
}
