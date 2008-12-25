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
package org.cytoscape.presentation.internal;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import org.cytoscape.presentation.TextNodeRenderer;
import org.cytoscape.viewmodel.Renderer;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;


/**
 *
 */
public class RendererActivator implements BundleActivator {
	private Set<ServiceRegistration> regSet;

	/**
	 *  DOCUMENT ME!
	 *
	 * @param bc DOCUMENT ME!
	 */
	public void start(final BundleContext bc) {
		System.out.println("demo presentation RendererActivator start");
		// initialize the set of registrations
		regSet = new HashSet<ServiceRegistration>();

		Hashtable props = new Hashtable();
		//ServiceRegistration reg = bc.registerService(TextPresentation.class.getName(),
		//					     new AdjMatrixTextRenderer(),props);
		//regSet.add( reg );
		props = new Hashtable();

		// register both as Renderer and as value for DiscreteVisualProperty
		final TextNodeRenderer renderer = new TextNodeRendererImpl();
		final ServiceRegistration reg = bc.registerService(new String[] {
		                                                 TextNodeRenderer.class.getName(),
		                                                 Renderer.class.getName()
		                                             }, renderer, props);
		regSet.add(reg);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param bc DOCUMENT ME!
	 */
	public void stop(final BundleContext bc) {
		for (ServiceRegistration reg : regSet)
			reg.unregister();
	}
}
