package de.mpg.mpi_inf.bioinf.netanalyzer;

/**
 * Exception raised to indicate plugin's internal error, i.e.&nbsp;a bug.
 * 
 * @author Yassen Assenov
 */
public class InnerException extends RuntimeException {

	/**
	 * Initializes a new instance of <code>InnerException</code>.
	 * 
	 * @param cause Exception that was unexpectedly raised.
	 */
	public InnerException(Throwable cause) {
		super(cause);
	}

	/**
	 * Unique ID for this version of this class. It is used in serialization.
	 */
	private static final long serialVersionUID = -2600004852243398033L;
}
