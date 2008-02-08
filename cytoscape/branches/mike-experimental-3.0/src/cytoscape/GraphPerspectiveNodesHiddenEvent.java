
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

import cytoscape.GraphPerspective;
import cytoscape.Node;


final class GraphPerspectiveNodesHiddenEvent extends GraphPerspectiveChangeEventAdapter {
	private final static long serialVersionUID = 1202347362681230L;
	private final Node[] m_hiddenNodes;
	private final int[] m_hiddenNodeInx;

	// Note that no copy of the array hiddenNodes is made - the exact
	// array reference is kept.  However, copies are made in the return values
	// of methods of this class.  Note that the Node objects in the input array
	// must contain valid RootGraph indices at the time this constructor is
	// called; further behavior of the Node objects is not too important
	// because the getHiddenNodes() method has been deprecated.
	GraphPerspectiveNodesHiddenEvent(Object source, Node[] hiddenNodes) {
		super(source);
		m_hiddenNodes = hiddenNodes;
		m_hiddenNodeInx = new int[m_hiddenNodes.length];

		for (int i = 0; i < m_hiddenNodeInx.length; i++)
			m_hiddenNodeInx[i] = m_hiddenNodes[i].getRootGraphIndex();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public final int getType() {
		return NODES_HIDDEN_TYPE;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public final int[] getHiddenNodeIndices() {
		final int[] returnThis = new int[m_hiddenNodeInx.length];
		System.arraycopy(m_hiddenNodeInx, 0, returnThis, 0, m_hiddenNodeInx.length);

		return returnThis;
	}
}
