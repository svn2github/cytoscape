
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

package cytoscape;

import cytoscape.GraphPerspectiveChangeEvent;
import cytoscape.GraphPerspectiveChangeListener;


// Package visible.
// Analagous to java.awt.AWTEventMulticaster for chaining together
// cytoscape.GraphPerspectiveChangeListener objects.  Example usage:
//
// public class Foo implements GraphPerspective
// {
//   private GraphPerspectiveChangeListener lis = null;
//   public void addGraphPerspectiveChangeListener(
//                                          GraphPerspectiveChangeListener l) {
//     lis = GraphPerspectiveChangeListenerChain.add(lis, l); }
//   public void removeGraphPerspectiveChangeListener(
//                                          GraphPerspectiveChangeListener l) {
//     lis = GraphPerspectiveChangeListenerChain.remove(lis, l); }
//   ...
// }
class GraphPerspectiveChangeListenerChain implements GraphPerspectiveChangeListener {
	private final GraphPerspectiveChangeListener a;
	private final GraphPerspectiveChangeListener b;

	private GraphPerspectiveChangeListenerChain(GraphPerspectiveChangeListener a,
	                                            GraphPerspectiveChangeListener b) {
		this.a = a;
		this.b = b;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param evt DOCUMENT ME!
	 */
	public void graphPerspectiveChanged(GraphPerspectiveChangeEvent evt) {
		a.graphPerspectiveChanged(evt);
		b.graphPerspectiveChanged(evt);
	}

	static GraphPerspectiveChangeListener add(GraphPerspectiveChangeListener a,
	                                          GraphPerspectiveChangeListener b) {
		if (a == null)
			return b;

		if (b == null)
			return a;

		return new GraphPerspectiveChangeListenerChain(a, b);
	}

	static GraphPerspectiveChangeListener remove(GraphPerspectiveChangeListener l,
	                                             GraphPerspectiveChangeListener oldl) {
		if ((l == oldl) || (l == null))
			return null;
		else if (l instanceof GraphPerspectiveChangeListenerChain)
			return ((GraphPerspectiveChangeListenerChain) l).remove(oldl);
		else

			return l;
	}

	private GraphPerspectiveChangeListener remove(GraphPerspectiveChangeListener oldl) {
		if (oldl == a)
			return b;

		if (oldl == b)
			return a;

		GraphPerspectiveChangeListener a2 = remove(a, oldl);
		GraphPerspectiveChangeListener b2 = remove(b, oldl);

		if ((a2 == a) && (b2 == b))
			return this;

		return add(a2, b2);
	}
}
