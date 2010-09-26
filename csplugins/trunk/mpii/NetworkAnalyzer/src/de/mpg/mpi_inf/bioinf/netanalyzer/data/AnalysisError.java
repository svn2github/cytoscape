package de.mpg.mpi_inf.bioinf.netanalyzer.data;

/**
 * Enumeration on possible errors which can occur on a single network analysis during batch processing.
 * 
 * @author Nadezhda Doncheva
 */
public enum AnalysisError {

	/**
	 * Output (.netstats) file could not be created.
	 */
	OUTPUT_NOT_CREATED,

	/**
	 * I/O error has occurred while writing to the netstats file.
	 */
	OUTPUT_IO_ERROR,

	/**
	 * Exception has occurred during computation of topological parameters.
	 */
	INTERNAL_ERROR,

	/**
	 * Network with no nodes loaded.
	 */
	NETWORK_EMPTY,
	
	/**
	 * Network file is invalid.
	 */
	NETWORK_FILE_INVALID,

	/**
	 * Network file could not be opened.
	 */
	NETWORK_NOT_OPENED;

	/**
	 * Gets the message explaining the occurred <code>aError</code> to the user. 
	 * 
	 * @param aError Error occurred during batch analysis.
	 * @return Message for the user explaining the occurred error.
	 */
	public static String getMessage(AnalysisError aError) {
		switch (aError) {
		case OUTPUT_NOT_CREATED:
			return Messages.SM_OUTPUTNOTCREATED;
		case OUTPUT_IO_ERROR:
			return Messages.SM_OUTPUTIOERROR;
		case INTERNAL_ERROR:
			return Messages.SM_INTERNALERROR;
		case NETWORK_EMPTY:
			return Messages.SM_NETWORKEMPTY;
		case NETWORK_FILE_INVALID:
			return Messages.SM_NETWORKFILEINVALID;
		case NETWORK_NOT_OPENED:
			return Messages.SM_NETWORKNOTOPENED;
		default:
			return Messages.SM_UNKNOWNERROR;
		}
	}
}
