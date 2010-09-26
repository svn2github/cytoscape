package de.mpg.mpi_inf.bioinf.netanalyzer.data;

/**
 * Utility class for storing the sum and length of a non-empty sequence of numbers.
 * 
 * @author Yassen Assenov
 */
public class SumCountPair {

	/**
	 * Initializes a new instance of <code>SumCountPair</code>.
	 * 
	 * @param aValue The first number of the sequence.
	 */
	public SumCountPair(double aValue) {
		mSum = aValue;
		mCount = 1;
	}

	/**
	 * Adds a new number to the sequence.
	 * 
	 * @param aValue New number to be added.
	 */
	public void add(double aValue) {
		mSum += aValue;
		mCount++;
	}

	/**
	 * Gets the sum of the numbers in the sequence.
	 * 
	 * @return Sum of the numbers added so far to the sequence.
	 */
	public double getSum() {
		return mSum;
	}

	/**
	 * Gets the average of the numbers in the sequence.
	 * <p>
	 * This method facilitates the calls <code>{@link #getSum()} / {@link #getCount()}</code>.
	 * </p>
	 * 
	 * @return Average of the numbers added so far to the sequence.
	 */
	public double getAverage() {
		return mSum / mCount;
	}

	/**
	 * Gets the size of the sequence.
	 * 
	 * @return Number of numbers added to the sequence so far.
	 */
	public int getCount() {
		return mCount;
	}

	/**
	 * Sum of numbers added to the sequence.
	 */
	private double mSum;

	/**
	 * Number of numbers added to the sequence.
	 */
	private int mCount;
}
