package de.mpg.mpi_inf.bioinf.netanalyzer;

/**
 * Listener interface for analysis results listeners.
 * 
 * @author Yassen Assenov
 */
public interface AnalysisListener {

	/**
	 * Invoked when analysis is cancelled by the user.
	 */
	public void analysisCancelled();

	/**
	 * Invoked when analysis
	 * 
	 * @param aAnalyzer
	 *            Analyzer instance which has successfully completed the analysis of a network.
	 */
	public void analysisCompleted(NetworkAnalyzer aAnalyzer);
}
