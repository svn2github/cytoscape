package de.mpg.mpi_inf.bioinf.netanalyzer.data;

/**
 * Data type storing information about the shortest path lengths from one node to other nodes in the
 * networks.
 * 
 * @author Yassen Assenov
 */
public class PathLengthData {

	/**
	 * Number of shortest path lengths accumulated in this instance.
	 */
	private int count;

	/**
	 * Sum of all shortest path lengths accumulated in this instance.
	 */
	private long totalLength;

	/**
	 * Maximum length of a shortest path accumulated in this instance.
	 */
	private int maxLength;

	/**
	 * Initializes a new instance of <code>PathLengthData</code>.
	 */
	public PathLengthData() {
		count = 0;
		totalLength = 0;
		maxLength = 0;
	}

	/**
	 * Accumulates a new shortest path length to this data instance.
	 * 
	 * @param aLength Length of shortest path to be accumulated.
	 */
	public void addSPL(int aLength) {
		count++;
		totalLength += aLength;
		if (maxLength < aLength) {
			maxLength = aLength;
		}
	}

	/**
	 * Gets the number of shortest path lengths.
	 * 
	 * @return Number of shortest path lengths accumulated in this instance.
	 */
	public int getCount() {
		return count;
	}

	/**
	 * Gets the total length of shortest paths.
	 * 
	 * @return Sum of all shortest path lengths accumulated in this instance.
	 */
	public long getTotalLength() {
		return totalLength;
	}

	/**
	 * Longest among the shortest path lengths added to this data instance.
	 * 
	 * @return Maximum length of a shortest path accumulated in this instance.
	 */
	public int getMaxLength() {
		return maxLength;
	}

	/**
	 * Average shortest path length.
	 * 
	 * @return Average length of a shortest path accumulated in this instance.
	 * 
	 * @throws IllegalStateException If no SPLs were accumulated in this instance ({@link #getCount()}<code> == 0</code>).
	 */
	public double getAverageLength() {
		if (count == 0) {
			throw new IllegalStateException();
		}
		return ((double) totalLength) / count;
	}
}
