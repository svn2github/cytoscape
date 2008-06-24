
package org.cytoscape.command;

/** 
 * An interface capturing the execution of an arbitrary
 * task.  Implementations of the Command interface can
 * be parameterized using {@link Tunable}s and used within
 * that framework or using normal getter/setter methods.  
 * Command status can be monitored using the 
 * {@link Monitorable} interface.
 * <p>
 * Should commands have context or namespace, allowing  
 * something like GUI specific commands? 
 */
public interface Command {

	/**
	 * The name of the Command. This should be
	 * unique among Commands and is immutable.
	 */
	public String getName();

	/**
	 * A brief description of what the command accomplishes.
	 */
	public String getDescription();

	/**
	 * Triggers execution of the command.
	 */
	public void execute();
}
