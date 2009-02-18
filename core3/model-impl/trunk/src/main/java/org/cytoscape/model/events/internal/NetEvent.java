
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

package org.cytoscape.model.events.internal;

import org.cytoscape.model.CyNetwork;


/**
 * A basic network event that can extended to support nodes and edges.
 *
 * @param <T>  DOCUMENT ME!
 */
public class NetEvent<T> {
	private final T t;
	private final CyNetwork n;

	/**
	 * Creates a new NetEvent object.
	 *
	 * @param t  DOCUMENT ME!
	 * @param n  DOCUMENT ME!
	 */
	public NetEvent(T t, CyNetwork n) {
		this.t = t;
		this.n = n;
	}

	/**
	 * Creates a new NetEvent object.
	 *
	 * @param n  DOCUMENT ME!
	 */
	public NetEvent(CyNetwork n) {
		this(null, n);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	protected T get() {
		return t;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public CyNetwork getSource() {
		return n;
	}
}
