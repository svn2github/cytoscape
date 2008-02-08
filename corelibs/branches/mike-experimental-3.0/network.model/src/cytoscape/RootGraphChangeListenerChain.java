
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

import cytoscape.RootGraphChangeEvent;
import cytoscape.RootGraphChangeListener;


// Package visible.
// Analagous to java.awt.AWTEventMulticaster for chaining together
// cytoscape.RootGraphChangeListener objects.  Example usage:
//
// public class Bar implements RootGraph
// {
//   private RootGraphChangeListener lis = null;
//   void addRootGraphChangeListener(RootGraphChangeListener l) {
//     lis = RootGraphChangeListenerChain.add(lis, l); }
//   void removeRootGraphChangeListener(RootGraphChangeListener l) {
//     lis = RootGraphChangeListenerChain.remove(lis, l); }
//   ...
// }
class RootGraphChangeListenerChain implements RootGraphChangeListener {
	private final RootGraphChangeListener a;
	private final RootGraphChangeListener b;

	private RootGraphChangeListenerChain(RootGraphChangeListener a, RootGraphChangeListener b) {
		this.a = a;
		this.b = b;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param evt DOCUMENT ME!
	 */
	public void rootGraphChanged(RootGraphChangeEvent evt) {
		a.rootGraphChanged(evt);
		b.rootGraphChanged(evt);
	}

	static RootGraphChangeListener add(RootGraphChangeListener a, RootGraphChangeListener b) {
		if (a == null)
			return b;

		if (b == null)
			return a;

		return new RootGraphChangeListenerChain(a, b);
	}

	static RootGraphChangeListener remove(RootGraphChangeListener l, RootGraphChangeListener oldl) {
		if ((l == oldl) || (l == null))
			return null;
		else if (l instanceof RootGraphChangeListenerChain)
			return ((RootGraphChangeListenerChain) l).remove(oldl);
		else

			return l;
	}

	private RootGraphChangeListener remove(RootGraphChangeListener oldl) {
		if (oldl == a)
			return b;

		if (oldl == b)
			return a;

		RootGraphChangeListener a2 = remove(a, oldl);
		RootGraphChangeListener b2 = remove(b, oldl);

		if ((a2 == a) && (b2 == b))
			return this;

		return add(a2, b2);
	}
}
