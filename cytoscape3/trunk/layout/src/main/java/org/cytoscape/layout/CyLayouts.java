/*
  File: CyLayouts.java

  Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)

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
package org.cytoscape.layout;

import org.cytoscape.layout.algorithms.GridNodeLayout;

import java.util.Collection;
import java.util.LinkedList;

import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

import java.util.Hashtable;

public class CyLayouts implements BundleActivator {

	static BundleContext bc;

	public void start(BundleContext bc) {
		this.bc = bc;

		// add the default layout
		CyLayoutAlgorithm def = new GridNodeLayout();
        Hashtable props = new Hashtable();
        props.put("preferredMenu", "Cytoscape Layouts");
        props.put("name", def.getName());
        ServiceRegistration reg = bc.registerService(CyLayoutAlgorithm.class.getName(), def, props);
	}

	public void stop(BundleContext bc) {
	}


	/**
	 * Get the layout named "name".  If "name" does
	 * not exist, this will return null
	 *
	 * @param name String representing the name of the layout
	 * @return the layout of that name or null if it is not reigstered
	 */
	public static CyLayoutAlgorithm getLayout(String name) {
		if ( bc == null )
			return null;

		String query = "(&(name="+ name +"))";

		try {
		ServiceReference[] sr = bc.getServiceReferences(CyLayoutAlgorithm.class.getName(), query);
		if ( sr != null )
			for (ServiceReference r : sr ) {
				CyLayoutAlgorithm cla = (CyLayoutAlgorithm)bc.getService(r);
				if ( cla != null )
					return cla;
			}
		} catch (Exception e) { e.printStackTrace(); }
	
		return null;
	}

	/**
	 * Get all of the available layouts.
	 *
	 * @return a Collection of all the available layouts
	 */
	public static Collection<CyLayoutAlgorithm> getAllLayouts() {

		Collection<CyLayoutAlgorithm> allLayouts = new LinkedList<CyLayoutAlgorithm>();

		if ( bc == null )
			return allLayouts;

		try {
		ServiceReference[] sr = bc.getServiceReferences(CyLayoutAlgorithm.class.getName(), null);
		if ( sr != null )
			for (ServiceReference r : sr ) {
				CyLayoutAlgorithm cla = (CyLayoutAlgorithm)bc.getService(r);
				if ( cla != null )
					allLayouts.add( cla );
			}
		} catch (Exception e) { e.printStackTrace(); }

		return allLayouts;
	}

	/**
	 * Get the default layout.  This is either the grid layout or a layout
	 * chosen by the user via the setting of the "layout.default" property.
	 *
	 * @return CyLayoutAlgorithm to use as the default layout algorithm
	 */
	public static CyLayoutAlgorithm getDefaultLayout() {
		return getLayout("grid");
	}

	// Ack. 
	// TODO this really shouldn't be here
	public static String getMenuName(CyLayoutAlgorithm layout) {
		if ( bc == null )
			return null;

		try {
		ServiceReference[] sr = bc.getServiceReferences(CyLayoutAlgorithm.class.getName(), layout.getName());
		if ( sr != null )
			for (ServiceReference r : sr ) {
				return (String)r.getProperty("preferredMenu");
			}
		} catch (Exception e) { e.printStackTrace(); }
	
		return null;
	}
}
