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
package org.cytoscape.view.model.internal;

import org.cytoscape.event.CyEventHelper;

import org.cytoscape.model.CyNetwork;

import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewFactory;

import org.cytoscape.service.util.CyServiceRegistrar;

import java.util.Properties;

/**
 *
 */
public class ColumnOrientedNetworkViewFactoryImpl implements
		CyNetworkViewFactory {
	private final CyEventHelper eventHelper;
	private final CyServiceRegistrar registrar;

	/**
	 * For injection, use this constructor.
	 * 
	 * @param eventHelper
	 */
	public ColumnOrientedNetworkViewFactoryImpl(CyEventHelper eventHelper, CyServiceRegistrar registrar) {
		if (eventHelper == null)
			throw new NullPointerException("CyEventHelper is null");
		this.eventHelper = eventHelper;

		if (registrar == null)
			throw new NullPointerException("CyServiceRegistrar is null");
		this.registrar = registrar;
	}


	/**
	 * DOCUMENT ME!
	 * 
	 * @param network
	 *            for which the CyNetworkView is to be created
	 * @return DOCUMENT ME!
	 */
	public CyNetworkView getNetworkViewFor(final CyNetwork network) {
		CyNetworkView view = new ColumnOrientedNetworkViewImpl(eventHelper, network);
		registrar.registerAllServices(view,new Properties());

		return view; 
	}
}
