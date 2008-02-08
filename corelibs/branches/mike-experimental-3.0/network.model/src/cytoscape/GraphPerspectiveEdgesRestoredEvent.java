
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


final class GraphPerspectiveEdgesRestoredEvent extends GraphPerspectiveChangeEventAdapter {
	private final static long serialVersionUID = 1202347362661870L;
	private final GraphPerspective m_persp;
	private final int[] m_restoredEdgeInx;

	// Note that no copy of the array restoredEdgeInx is made - the exact
	// array reference is kept.  However, copies are made in the return values
	// of methods of this class.
	GraphPerspectiveEdgesRestoredEvent(GraphPerspective persp, int[] restoredEdgeInx) {
		super(persp);
		m_persp = persp;
		m_restoredEdgeInx = restoredEdgeInx;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public final int getType() {
		return EDGES_RESTORED_TYPE;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public final Edge[] getRestoredEdges() {
		final Edge[] returnThis = new Edge[m_restoredEdgeInx.length];

		for (int i = 0; i < returnThis.length; i++)
			returnThis[i] = m_persp.getRootGraph().getEdge(m_restoredEdgeInx[i]);

		return returnThis;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public final int[] getRestoredEdgeIndices() {
		final int[] returnThis = new int[m_restoredEdgeInx.length];
		System.arraycopy(m_restoredEdgeInx, 0, returnThis, 0, m_restoredEdgeInx.length);

		return returnThis;
	}
}
