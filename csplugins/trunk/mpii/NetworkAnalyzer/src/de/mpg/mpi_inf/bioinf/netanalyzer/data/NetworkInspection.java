package de.mpg.mpi_inf.bioinf.netanalyzer.data;

/**
 * Storage class for results of inspection on the edges in a network.
 * 
 * @author Yassen Assenov
 */
public class NetworkInspection {

	/**
	 * Initializes a new instance of <code>NetworkInspection</code>.
	 */
	public NetworkInspection() {
		time = System.currentTimeMillis();
		dir = false;
		uniqueDir = false;
		undir = false;
		dirLoops = false;
		undirLoops = false;
		dupEdges = false;
		dupDirEdges = false;
	}

	/**
	 * Stops the timer, indicating the inspection has ended.
	 * <p>
	 * This method must be called exactly once in the lifetime of this instance.
	 * </p>
	 */
	public void stopTimer() {
		time = System.currentTimeMillis() - time;
	}

	/**
	 * Time, in milliseconds, taken for the inspection.
	 */
	public long time;

	/**
	 * Flag indicating if the network contains directed edge(s).
	 */
	public boolean dir;

	/**
	 * Flag indicating if the network contains unpaired directed edge(s).
	 */
	public boolean uniqueDir;

	/**
	 * Flag indicating if the network contains undirected edge(s).
	 */
	public boolean undir;

	/**
	 * Flag indicating if the network contains directed self-loop(s).
	 */
	public boolean dirLoops;

	/**
	 * Flag indicating if the network contains undirected self-loop(s).
	 */
	public boolean undirLoops;

	/**
	 * Flag indicating if the network contains a pair of nodes connected by more than one edge.
	 */
	public boolean dupEdges;

	/**
	 * Flag indicating if the network contains two or more identical directed edges.
	 */
	public boolean dupDirEdges;
}
