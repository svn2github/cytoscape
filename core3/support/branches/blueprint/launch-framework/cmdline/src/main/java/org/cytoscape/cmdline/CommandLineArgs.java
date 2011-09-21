package org.cytoscape.cmdline;

/**
 * A service interface that provides access to the command line arguments
 * used to start an application.
 */
public interface CommandLineArgs {

	/**
	 * Returns a copy of the array of Strings passed in as command line arguments
	 * to the application.
	 * @return A copy of the array of Strings passed in as command line arguments
	 * to the application.
	 */
	String[] getArgs();
}
