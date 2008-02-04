
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

package legacy.layout;


/**
 * This class extends <code>GraphLayout</code> to offer the possibility
 * of defining poly-line edges (as opposed to just straight-line edges).<p>
 * For a given edge E in this graph, the definition of a poly-line
 * for E is as follows.
 * If we define a sequence of points {P[0], P[1], ... P[N+1]} where<ul>
 * <li>P[0] is the source node of edge E,</li>
 * <li>P[N+1] is the target node of edge E, and</li>
 * <li>for i not equal to 0 or N+1, P[i] is edge E's anchor
 * point at index i-1,</li></ul>
 * then the poly-line for edge E is defined to be the union of line segments
 * {S[0], S[1], ... S[N]} where
 * each S[i] is the straight-line segment starting
 * at P[i] and ending at P[i+1].
 */
public interface PolyEdgeGraphLayout extends GraphLayout {
	/**
	 * Returns the number of anchor points belonging to edge at index
	 * <code>edgeIndex</code>.  In other methods of this
	 * interface an anchor point is referenced by the index of the edge to which
	 * the anchor point belongs along with the anchor point's index within that
	 * edge.  Indices of anchor points within an edge at index
	 * <code>edgeIndex</code> start at <code>0</code> and end at
	 * <nobr><code>getNumAnchors(edgeIndex) - 1</code></nobr>, inclusive.
	 *
	 * @return the number of edge anchor points belonging to edge at index
	 *         <code>edgeIndex</code>.
	 * @throws IndexOutOfBoundsException if <code>edgeIndex</code> is not
	 *                                   in the interval <nobr><code>[0, getNumEdges() - 1]</code></nobr>.
	 */
	public int getNumAnchors(int edgeIndex);

	/**
	 * Returns the X or Y position of an edge anchor point.
	 *
	 * @param edgeIndex   the index of the edge to which the anchor point whose
	 *                    position we're seeking belongs.
	 * @param anchorIndex if edge E has index <code>edgeIndex</code>,
	 *                    the index of anchor point, within E, whose position we're seeking.
	 * @param xPosition   if <code>true</code>, return X position of anchor point;
	 *                    if <code>false</code>, return Y position of anchor point.
	 * @return the X or Y position of anchor point with index
	 *         <code>anchorIndex</code> within edge at index <code>edgeIndex</code>;
	 *         X return values are within the interval
	 *         <nobr><code>[0.0, getMaxWidth()]</code></nobr> and Y return values
	 *         are within the interval <nobr><code>[0.0, getMaxHeight()]</code></nobr>.
	 * @throws IndexOutOfBoundsException if <code>edgeIndex</code> is not in
	 *                                   the interval <nobr><code>[0, getNumEdges() - 1]</code></nobr>.
	 * @throws IndexOutOfBoundsException if <code>anchorIndex</code> is not
	 *                                   in the interval
	 *                                   <nobr><code>[0, getNumAnchors(edgeIndex) - 1]</code></nobr>.
	 */
	public double getAnchorPosition(int edgeIndex, int anchorIndex, boolean xPosition);
}
