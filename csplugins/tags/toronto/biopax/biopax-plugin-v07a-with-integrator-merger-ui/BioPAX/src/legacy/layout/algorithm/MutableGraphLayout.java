
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

import legacy.layout.GraphLayout;


/**
 * This class offers a hook for layout algorithms to operate on.
 */
public interface MutableGraphLayout extends GraphLayout {
	/**
	 * Tells us whether or not the node at index <code>nodeIndex</code>
	 * can be moved by <code>setNodePosition()</code>.
	 *
	 * @param nodeIndex index of node whose mobility we are querying.
	 * @throws IndexOutOfBoundsException if <code>nodeIndex</code> is not
	 *                                   in the interval <nobr><code>[0, getNumNodes() - 1]</code></nobr>.
	 * @see #setNodePosition(int, double, double)
	 */
	public boolean isMovableNode(int nodeIndex);

	/*
	* Returns <code>true</code> if and only if
	* <code>isMovableNode(nodeIx)</code> returns <code>true</code> for every
	* <code>nodeIx</code> in the interval
	* <nobr><code>[0, getNumNodes() - 1]</code></nobr>.
	public boolean areAllNodesMovable();
	*/

	/**
	 * Sets the X,Y position of a node at index <code>nodeIndex</code>.
	 * This is a hook for layout algorithms to actually set locations of
	 * nodes.  Layout algorithms should call this method.<p>
	 * X, Y values set by this method shall be reflected in the return values
	 * of <code>getNodePosition()</code> - that is, if we call
	 * <blockquote><code>setNodePosition(nodeIndex, x, y)</code></blockquote>
	 * then the subsequent expressions
	 * <blockquote>
	 * <nobr><code>x == getNodePosition(nodeIndex, true)</code></nobr><br />
	 * <nobr><code>y == getNodePosition(nodeIndex, false)</code></nobr>
	 * </blockquote>
	 * both evaluate to <code>true</code> (assuming that <code>nodeIndex</code>,
	 * <code>x</code>, and <code>y</code> are allowable parameters).<p>
	 * Layout algorithms are encouraged to set node positions such that
	 * their X and Y values use the full range of allowable values, including
	 * the boundary values <code>0</code>, <code>getMaxWidth()</code>, and
	 * <code>getMaxHeight()</code>.  Any notion of node thickness, graph
	 * border on perimeter, etc. should be predetermined by the application
	 * using a layout algorithm; <code>getMaxWidth()</code> and
	 * <code>getMaxHeight()</code> should be defined accordingly by the
	 * application using a layout algorithm.
	 *
	 * @throws IllegalArgumentException      if
	 *                                       <nobr><code>xPos < 0.0</code></nobr>, if
	 *                                       <nobr><code>xPos > getMaxWidth()</code></nobr>, if
	 *                                       <nobr><code>yPos < 0.0</code></nobr>, or if
	 *                                       <nobr><code>yPos > getMaxHeight()</code></nobr>.
	 * @throws IndexOutOfBoundsException     if <code>nodeIndex</code> is not
	 *                                       in the interval <nobr><code>[0, getNumNodes() - 1]</code></nobr>.
	 * @throws UnsupportedOperationException if
	 *                                       <nobr><code>isMovableNode(nodeIndex)</code></nobr> returns
	 *                                       <code>false</code>.
	 * @see #getMaxWidth()
	 * @see #getMaxHeight()
	 * @see #getNodePosition(int, boolean)
	 * @see #isMovableNode(int)
	 */
	public void setNodePosition(int nodeIndex, double xPos, double yPos);
}
