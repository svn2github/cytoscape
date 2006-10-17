package legacy.layout;

import legacy.GraphTopology;

/**
 * This class represents not just a graph's topology but also a layout of its
 * nodes in 2D space (a &quot;straight-line graph drawing&quot;).
 */
public interface GraphLayout extends GraphTopology {

    /**
     * Returns the maximum allowable value of X positions of nodes.
     * All X positions of nodes in this graph will lie in the interval
     * <nobr><code>[0.0, getMaxWidth()]</code></nobr>.
     *
     * @see #getNodePosition(int, boolean)
     */
    public double getMaxWidth();

    /**
     * Returns the maximum allowable value of Y positions of nodes.
     * All Y positions of nodes in this graph will lie in the interval
     * <nobr><code>[0.0, getMaxHeight()]</code></nobr>.
     *
     * @see #getNodePosition(int, boolean)
     */
    public double getMaxHeight();

    /**
     * Returns the X or Y position of a node.
     *
     * @param nodeIndex the index of node whose position we're seeking.
     * @param xPosition if <code>true</code>, return X position; if
     *                  <code>false</code>, return Y position.
     * @return the X or Y position of node at index <code>nodeIndex</code>.
     * @throws IndexOutOfBoundsException if <code>nodeIndex</code> is not
     *                                   in the interval <nobr><code>[0, getNumNodes() - 1]</code></nobr>.
     */
    public double getNodePosition(int nodeIndex, boolean xPosition);

}
