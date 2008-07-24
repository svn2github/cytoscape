
package org.cytoscape.work;

/** 
 * An interface capturing the execution of an arbitrary
 * task.  Implementations of the Command interface can
 * be parameterized using {@link org.cytoscape.tunable.Tunable}s and used within
 * that framework or using normal getter/setter methods.  
 * Command status can be monitored using the 
 * {@link org.cytoscape.monitor.Monitorable} interface.
 * <p>
 * Should commands have context or namespace, allowing  
 * something like GUI specific commands?  Or just allow
 * this by extending this interface.
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

	/**
	 * Cancels the execution of the command once the {@link Command#execute()} 
	 * method has been called.  
	 * <p>
	 * I tend to think this belongs here, but I wonder if a separate Cancelable 
	 * interface might be more appropriate?
	 * <p>
	 * How closely, if at all, should this interface mimic the {@link java.lang.Thread} interface?
	 * <p>
	 * Do we want the ability to "pause".  I tend to think not, but perhaps in another interface? 
	 * <p>
	 * In general the ability to cancel execution is managed by
	 * an internal boolean state variable in the implementation of this
	 * interface.  This method should set the state variable to true, 
	 * which should in turn be checked by in the body of the {@link Command#execute()}
	 * method.
	 */
	public void cancel();
}
