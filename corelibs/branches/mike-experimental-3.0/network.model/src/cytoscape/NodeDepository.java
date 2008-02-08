
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

import cytoscape.Node;
import cytoscape.RootGraph;


final class NodeDepository implements FingNodeDepot {
/*
	private final static int INITIAL_CAPACITY = 11; // Must be non-negative.
	private Node[] m_nodeStack;
	private int m_size;

	NodeDepository() {
		m_nodeStack = new Node[INITIAL_CAPACITY];
		m_size = 0;
	}

	// Gimme a node, darnit!
	public Node getNode(RootGraph root, int index, String id) {
		final FNode returnThis;

		if (m_size == 0) {
			returnThis = new FNode(root,index);
		} else {
			returnThis = (FNode) m_nodeStack[--m_size];
			returnThis.m_rootGraph = root;
			returnThis.m_rootGraphIndex = index;
			returnThis.setIdentifier(id);
		}

		return returnThis;
	}

	// Deinitialize the object's members yourself if you need or want to.
	public void recycleNode(Node node) {
		if (node == null)
			return;

		try {
			m_nodeStack[m_size] = node;
			m_size++;
		} catch (ArrayIndexOutOfBoundsException e) {
			final int newArrSize = (int) Math.min((long) Integer.MAX_VALUE,
			                                      (((long) m_nodeStack.length) * 2L) + 1L);

			if (newArrSize == m_nodeStack.length)
				throw new IllegalStateException("unable to allocate large enough array");

			Node[] newArr = new Node[newArrSize];
			System.arraycopy(m_nodeStack, 0, newArr, 0, m_nodeStack.length);
			m_nodeStack = newArr;
			m_nodeStack[m_size++] = node;
		}
	}
	*/


	/**
	 *  DOCUMENT ME!
	 *
	 * @param root DOCUMENT ME!
	 * @param index DOCUMENT ME!
	 * @param id DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Node getNode(RootGraph root, int index, String id) {
		final FNode returnThis = new FNode(root, index);

		//     returnThis.setIdentifier(id);
		return returnThis;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param node DOCUMENT ME!
	 */
	public void recycleNode(Node node) {
		node.setIdentifier(null);
	}
}
