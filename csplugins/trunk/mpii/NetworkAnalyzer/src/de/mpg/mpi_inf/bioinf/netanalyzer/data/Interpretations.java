package de.mpg.mpi_inf.bioinf.netanalyzer.data;

/**
 * Enumeration on which interpretations to be applied for each network during batch processing.
 * 
 * @author Yassen Assenov
 */
public enum Interpretations {

	/**
	 * Apply all possible interpretations.
	 */
	ALL,

	/**
	 * Apply only interpretations that treat the networks as directed.
	 */
	DIRECTED,

	/**
	 * Apply only interpretations that treat the networks as undirected.
	 */
	UNDIRECTED
}