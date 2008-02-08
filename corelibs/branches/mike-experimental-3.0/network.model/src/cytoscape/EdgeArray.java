
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


// Valid indices: [0, Integer.MAX_VALUE - 1].
class EdgeArray {
	private final static int INITIAL_CAPACITY = 0; // Must be non-negative.
	private Edge[] m_edgeArr;

	EdgeArray() {
		m_edgeArr = new Edge[INITIAL_CAPACITY];
	}

	// Understand that this method will not increase the size of the underlying
	// array, no matter what.
	// Throws ArrayIndexOutOfBoundsException if index is negative or
	// Integer.MAX_VALUE.
	Edge getEdgeAtIndex(int index) {
		// Do pre-checking because try/catch with thrown exception causes huge
		// performance hit.
		if ((index >= m_edgeArr.length) && (index != Integer.MAX_VALUE))
			return null;

		return m_edgeArr[index]; // Exception if Integer.MAX_VALUE or negative.
	}

	// Understand that this method will potentially increase the size of the
	// underlying array, but only if two conditions hold:
	//   1. edge is not null and
	//   2. index is greater than or equal to the length of the array.
	// Throws ArrayIndexOutOfBoundsException if index is negative or
	// Integer.MAX_VALUE.
	void setEdgeAtIndex(Edge edge, int index) {
		// Do pre-checking because try/catch with thrown exception causes huge
		// performance hit.
		if ((index >= m_edgeArr.length) && (edge == null) && (index != Integer.MAX_VALUE))
			return;

		try {
			m_edgeArr[index] = edge;
		} catch (ArrayIndexOutOfBoundsException e) {
			if ((index < 0) || (index == Integer.MAX_VALUE)) {
				throw e;
			}

			final int newArrSize = (int) Math.min((long) Integer.MAX_VALUE,
			                                      Math.max((((long) m_edgeArr.length) * 2L) + 1L,
			                                               ((long) index) + 1L
			                                               + (long) INITIAL_CAPACITY));
			Edge[] newArr = new Edge[newArrSize];
			System.arraycopy(m_edgeArr, 0, newArr, 0, m_edgeArr.length);
			m_edgeArr = newArr;
			m_edgeArr[index] = edge;
		}
	}
}
