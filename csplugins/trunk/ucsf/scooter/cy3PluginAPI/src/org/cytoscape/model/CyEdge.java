package org.cytoscape.model;

/**
 * CyEdge is the object that represents the connection
 * between two (or more) nodes
 */
public interface CyEdge extends CyModelObject {
	public static enum EdgeFlag {
		DIRECTED(1),
		ISHYPEREDGE(2);
		int flag;
		EdgeFlag(int flag) {
			this.flag = flag;
		}
		public int getFlag() { return flag; }
	}

	/**
	 * Return the source of this edge
	 *
	 * @return edge source
	 */
	public CyNode getSource();

	/**
	 * Return the target of this edge
	 *
	 * @return edge target
	 */
	public CyNode getTarget();

	/**
	 * Return the edge type.  This is usually indicative of the
	 * type of interaction represented by the edge.
	 *
	 * @return edge type
	 */
	public String getEdgeType();

	/**
	 * Set a flag on this edge
	 *
	 * @param flag the flag to set
	 */
	public void setFlag(EdgeFlag flag);

	/**
	 * Clear a flag on this edge
	 *
	 * @param flag the flag to clear
	 */
	public void clearFlag(EdgeFlag flag);

	/**
	 * Test a flag on this edge
	 *
	 * @param flag the flag to test
	 */
	public boolean testFlag(EdgeFlag flag);
}
