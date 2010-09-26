package de.mpg.mpi_inf.bioinf.netanalyzer.data;

/**
 * Mutable integer.
 * 
 * @author Yassen Assenov
 */
public class MutInteger {

	/**
	 * Initializes a new instance of <code>MutInteger</code> and sets the value to <code>0</code>.
	 */
	public MutInteger() {
		value = 0;
	}

	/**
	 * Initializes a new instance of <code>MutInteger</code>.
	 * 
	 * @param aValue Initial value of this integer.
	 */
	public MutInteger(int aValue) {
		value = aValue;
	}

	/**
	 * Value of this mutable integer.
	 */
	public int value;
}
