
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

import org.cytoscape.presentation.NetworkPresentationFactory;
import org.cytoscape.presentation.SwingPresentation;
import org.cytoscape.presentation.TextPresentation;
import org.cytoscape.view.model.CyNetworkView;
import org.osgi.framework.BundleContext;


/**
 *
 */
public class NetworkPresentationFactoryImpl implements NetworkPresentationFactory {
	private BundleContext bundleContext;

	/**
	 * For setter injection (hmm. whats that?)
	 */
	public NetworkPresentationFactoryImpl() {
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param bundleContext DOCUMENT ME!
	 */
	public void setBundleContext(final BundleContext bundleContext) {
		this.bundleContext = bundleContext;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public BundleContext getBundleContext() {
		return bundleContext;
	}

	/**
	 * Creates a new CyNetworkFactoryImpl object.
	 *
	 * @param h  DOCUMENT ME!
	 */
	public NetworkPresentationFactoryImpl(final BundleContext bundleContext) {
		if (bundleContext == null)
			throw new NullPointerException("bundleContext is null");

		this.bundleContext = bundleContext;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param view DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public TextPresentation getTextPresentationFor(final CyNetworkView view) {
		return new AdjMatrixTextRenderer(view, bundleContext);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param view DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public SwingPresentation getSwingPresentationFor(final CyNetworkView view) {
		throw new RuntimeException("not implemented");
	}
}
