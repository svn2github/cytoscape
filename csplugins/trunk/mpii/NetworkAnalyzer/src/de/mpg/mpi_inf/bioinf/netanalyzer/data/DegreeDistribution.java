package de.mpg.mpi_inf.bioinf.netanalyzer.data;

/**
 * Histogram dataset for degree distribution histograms.
 * 
 * @author Yassen Assenov
 */
public class DegreeDistribution {

	/**
	 * Initializes a new instance of <code>DegreeDistribution</code>.
	 * 
	 * @param aNodeCount Number of nodes in the graph. This number is used to predict the maximum degree of a
	 *        node.
	 */
	public DegreeDistribution(int aNodeCount) {
		int initialSize = Math.max(aNodeCount / 4, MIN_INITIAL_SIZE);
		distribution = new int[initialSize];
		maxDegree = 0;
	}

	/**
	 * Gets the number of nodes of the given degree.
	 * 
	 * @param aDegree Degree to find the nodes of.
	 * @return Number of nodes of <code>aDegree</code> added to this instance; <code>0</code> if the
	 *         specified degree is a negative number or if no nodes of <code>aDegree</code> were added.
	 */
	public int getCount(int aDegree) {
		if (0 <= aDegree && aDegree <= maxDegree) {
			return distribution[aDegree];
		}
		return 0;
	}

	/**
	 * Gets the maximum degree of a node found in the graph.
	 * 
	 * @return Maximum degree of added observation.
	 * @see #addObservation(int)
	 */
	public int getMaxDegree() {
		return maxDegree;
	}

	/**
	 * Creates an <code>IntHistogram</code> complex param instance that contains the data of this
	 * distribution.
	 * 
	 * @return Newly created instance of <code>IntHistogram</code> based on this distribution.
	 */
	public IntHistogram createHistogram() {
		return new IntHistogram(distribution, 0, maxDegree);
	}

	/**
	 * Adds a new observation (node degree) to this distribution.
	 * 
	 * @param aDegree Degree of node observed.
	 * @throws ArrayIndexOutOfBoundsException If <code>aDegree</code> is a negative number.
	 */
	public void addObservation(int aDegree) {
		if (aDegree >= distribution.length) {
			ensureCapacity(aDegree + 1);
		}
		if (maxDegree < aDegree) {
			maxDegree = aDegree;
		}
		distribution[aDegree]++;
	}

	/**
	 * Enlarges the size of {@link #distribution}, if necessary, to be at least the specified capacity.
	 * 
	 * @param aCapacity Desired minimum size of {@link #distribution}.
	 */
	private void ensureCapacity(int aCapacity) {
		int currentCap = distribution.length;
		int newCap = currentCap;
		while (newCap < aCapacity) {
			newCap = (int) (GROWTH_FACTOR * newCap);
		}
		if (newCap != currentCap) {
			int[] newDist = new int[newCap];
			System.arraycopy(distribution, 0, newDist, 0, currentCap);
			distribution = newDist;
		}
	}

	/**
	 * Minimum initial size of {@link #distribution}.
	 */
	private static final int MIN_INITIAL_SIZE = 256;

	/**
	 * Growth factor of enlarging the size of {@link #distribution}.
	 * <p>
	 * Note that this number must be &gt; 1. The initial size ({@link #MIN_INITIAL_SIZE}) and the growth
	 * factor must be chosen such that:<br/> - The method {@link #ensureCapacity(int)} is called as rarely as
	 * possible, since it is a costly operation.<br/> - The size of {@link #distribution} is not excessively
	 * larger than {@link #maxDegree} for this would result in waste of memory.
	 * </p>
	 */
	private static final double GROWTH_FACTOR = 2;

	/**
	 * Node degree distribution. The <code>i</code>-th element of this array contains the number of
	 * observations (nodes) with degree <code>i</code>.
	 */
	private int[] distribution;

	/**
	 * Maximum degree of a node observed.
	 */
	private int maxDegree;
}
