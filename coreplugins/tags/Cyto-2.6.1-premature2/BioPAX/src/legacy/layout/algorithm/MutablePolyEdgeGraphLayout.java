
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

package legacy.layout.algorithm;

import legacy.layout.PolyEdgeGraphLayout;


/**
 * This class extends <code>MutableGraphLayout</code> to offer the possibility
 * of defining poly-line edges (as opposed to just straight-line edges).
 */
public interface MutablePolyEdgeGraphLayout extends PolyEdgeGraphLayout, MutableGraphLayout {
	/**
	 * Deletes an edge anchor point.<p>
	 * The deletion of an anchor point is accomplished such that the ordering of
	 * remaining anchor points stays the same.  An anchor point [belonging
	 * to edge with index <code>edgeIndex</code>] with index greater than
	 * <code>anchorIndex</code> will be assigned a new index equal to its
	 * previous index minus one; an anchor point with index less than
	 * <code>anchorIndex</code> will keep its index.
	 *
	 * @param edgeIndex   the index of the edge to which the anchor point to be
	 *                    deleted belongs.
	 * @param anchorIndex if edge E has index <code>edgeIndex</code>,
	 *                    the index of anchor point, within E, which we're trying to delete.
	 * @throws IndexOutOfBoundsException     if <code>edgeIndex</code> is not in
	 *                                       the interval <nobr><code>[0, getNumEdges() - 1]</code></nobr>.
	 * @throws IndexOutOfBoundsException     if <code>anchorIndex</code> is not
	 *                                       in the interval
	 *                                       <nobr><code>[0, getNumAnchors(edgeIndex) - 1]</code></nobr>.
	 * @throws UnsupportedOperationException if edge at index
	 *                                       <code>edgeIndex</code> has source and target nodes that are both
	 *                                       non-movable.
	 */
	public void deleteAnchor(int edgeIndex, int anchorIndex);

	/**
	 * Creates a new edge anchor point.<p>
	 * The creation of an anchor point is accomplished such that the ordering
	 * of existing anchor points stays the same.  An existing anchor point
	 * [belonging to edge with index <code>edgeIndex</code>] with index greater
	 * than or equal to <code>anchorIndex</code> will be assigned a new index
	 * equal to its previous index plus one; an existing anchor point with index
	 * less than <code>anchorIndex</code> will keep its index.<p>
	 * A new anchor point P's X,Y position is the midpoint along the segment
	 * whose end points are P's neighbors in the edge poly-line definition;
	 * X,Y positions of existing anchor points and nodes are unchanged.
	 *
	 * @param edgeIndex   new anchor point will be created on edge with
	 *                    index <code>edgeIndex</code>.
	 * @param anchorIndex new anchor point will have index
	 *                    <code>anchorIndex</code> within edge at index <code>edgeIndex</code>.
	 * @throws IndexOutOfBoundsException     if <code>edgeIndex</code> is not
	 *                                       in the interval <nobr><code>[0, getNumEdges() - 1]</code></nobr>.
	 * @throws IndexOutOfBoundsException     if <code>anchorIndex</code> is not
	 *                                       in the interval <nobr><code>[0, getNumAnchors(edgeIndex)]</code></nobr>.
	 * @throws UnsupportedOperationException if edge at index
	 *                                       <code>edgeIndex</code> has source and target
	 *                                       nodes that are both non-movable.
	 */
	public void createAnchor(int edgeIndex, int anchorIndex);

	/**
	 * Sets the X,Y position of an edge anchor point.<p>
	 * X, Y values set by this method shall be reflected in the return values
	 * of <code>getAnchorPosition()</code> - that is, if we call
	 * <blockquote><code>setAnchorPosition(eInx, aInx, x, y)</code></blockquote>
	 * then the subsequent expressions
	 * <blockquote>
	 * <nobr><code>x == getAnchorPosition(eInx, aInx, true)</code></nobr><br />
	 * <nobr><code>y == getAnchorPosition(eInx, aInx, false)</code></nobr>
	 * </blockquote>
	 * both evaluate to <code>true</code>.
	 *
	 * @param edgeIndex   the index of the edge to which the anchor point to be
	 *                    positioned belongs.
	 * @param anchorIndex if edge E has index <code>edgeIndex</code>,
	 *                    the index of anchor point, within E, which we're trying to position.
	 * @param xPosition   the desired X position of specified edge anchor point.
	 * @param yPosition   the desired Y position of specified edge anchor point.
	 * @throws IndexOutOfBoundsException     if <code>edgeIndex</code> is not in
	 *                                       the interval <nobr><code>[0, getNumEdges() - 1]</code></nobr>.
	 * @throws IndexOutOfBoundsException     if <code>anchorIndex</code> is not
	 *                                       in the interval
	 *                                       <nobr><code>[0, getNumAnchors(edgeIndex) - 1]</code></nobr>.
	 * @throws IllegalArgumentException      if
	 *                                       <nobr><code>xPosition < 0.0</code></nobr>, if
	 *                                       <nobr><code>xPosition > getMaxWidth()</code></nobr>, if
	 *                                       <nobr><code>yPosition < 0.0</code></nobr>, or if
	 *                                       <nobr><code>yPosition > getMaxHeight()</code></nobr>.
	 * @throws UnsupportedOperationException if edge at index
	 *                                       <code>edgeIndex</code> has source and target
	 *                                       nodes that are both non-movable.
	 */
	public void setAnchorPosition(int edgeIndex, int anchorIndex, double xPosition, double yPosition);
}
