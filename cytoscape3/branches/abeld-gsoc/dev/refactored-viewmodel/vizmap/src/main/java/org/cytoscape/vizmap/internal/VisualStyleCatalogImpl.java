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

import org.cytoscape.vizmap.VisualStyleCatalog;
import org.cytoscape.vizmap.VisualStyle;
import org.cytoscape.event.CyEventHelper;
import java.util.List;
import java.util.ArrayList;

/**
 * We need a list of currently-used VisualStyles somewhere (?)
 * This is it.
 * It is also a VisualStyle factory
 */
public class VisualStyleCatalogImpl implements VisualStyleCatalog {
    private List<VisualStyle> visualStyles;


	private CyEventHelper eventHelper;

	/**
	 * For setter injection (hmm. whats that?)
	 */
	public VisualStyleCatalogImpl() {
	    visualStyles = new ArrayList<VisualStyle>();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param eventHelper DOCUMENT ME!
	 */
	public void setEventHelper(CyEventHelper eventHelper) {
		this.eventHelper = eventHelper;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public CyEventHelper getEventHelper() {
		return this.eventHelper;
	}

	/**
	 *
	 * @param h  DOCUMENT ME!
	 */
	public VisualStyleCatalogImpl(final CyEventHelper eventHelper) {
		if (eventHelper == null)
			throw new NullPointerException("CyEventHelper is null");
		this.eventHelper = eventHelper;
		visualStyles = new ArrayList<VisualStyle>();
	}

    public VisualStyle createVisualStyle(){
	VisualStyle newVS = new VisualStyleImpl(eventHelper);
	visualStyles.add(newVS);
	return newVS;
    }

    public List<VisualStyle> listOfVisualStyles(){
	return visualStyles; // FIXME: should return copy?
    }
}