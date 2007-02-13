
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

package legacy;


/**
 * <b>This class is in a very unfinished state; development effort on this
 * class has been suspended until further notice; don't use this class
 * unless you're the author.
 * </b><p>
 * This interface represents neighboring node information for a given
 * graph.<p>
 * Assuming that a binary relation on the set of nodes in a graph defining
 * &quot;is a neighbor of&quot; is provided, it is possible to compute
 * the information returned by this interface.  Nonetheless, a programmer
 * may choose to access a legacy graph implementation's node adjacency list
 * information instead of computing it inside of this &quot;wrapper&quot;
 * graph framework - that's exactly what this interface is for.
 */
public interface NodeNeighbors {
	/**
	 * Returns a neighboring nodes list.<p>
	 *
	 * @param nodeIndex the index of the node whose neighbors we're trying
	 *                  to find.
	 * @return a non-repeating list of indices of all nodes B such that
	 *         B is a neighbor of node at index <code>nodeIndex</code>; this method
	 *         never returns <code>null</code>.
	 * @throws IndexOutOfBoundsException if <code>nodeIndex</code> does not
	 *                                   fall within a suitable interval.
	 */
	public IndexIterator getNeighboringNodeIndices(int nodeIndex);
}
