
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

import cytoscape.Edge;
import cytoscape.GraphPerspective;


final class GraphPerspectiveEdgesHiddenEvent extends GraphPerspectiveChangeEventAdapter {
	private final static long serialVersionUID = 1202347362644474L;
	private final Edge[] m_hiddenEdges;
	private final int[] m_hiddenEdgeInx;

	// Note that no copy of the array hiddenEdges is made - the exact
	// array reference is kept.  However, copies are made in the return values
	// of methods of this class.  Note that the Edge objects in the input array
	// must contain valid RootGraph indices at the time this constructor is
	// called; further behavior of the Edge objects is not too important
	// because the getHiddenEdges() method has been deprecated.
	GraphPerspectiveEdgesHiddenEvent(Object source, Edge[] hiddenEdges) {
		super(source);
		m_hiddenEdges = hiddenEdges;
		m_hiddenEdgeInx = new int[m_hiddenEdges.length];

		for (int i = 0; i < m_hiddenEdgeInx.length; i++)
			m_hiddenEdgeInx[i] = m_hiddenEdges[i].getRootGraphIndex();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public final int getType() {
		return EDGES_HIDDEN_TYPE;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public final int[] getHiddenEdgeIndices() {
		final int[] returnThis = new int[m_hiddenEdgeInx.length];
		System.arraycopy(m_hiddenEdgeInx, 0, returnThis, 0, m_hiddenEdgeInx.length);

		return returnThis;
	}
}
