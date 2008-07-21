
package org.cytoscape.command;

/** 
 * An interface that allows the result of a {@link Command} execution
 * to be returned. This is meant to allow more interactive behavior
 * of user interfaces.
 * <p>
 * <b>I think this could use a new name!</b>
 */
public interface InteractiveCommand<T> extends Command {

	/**
	 * This method returns a result of type T after
	 * the {@link Command#execute()} method has been
	 * called.
	 * <p>
	 * This is a lot like {@link java.util.concurrent.Callable}.  Should
	 * we also throw an exception if no result is returned?
	 * @return An object of type T that represents the result of the 
	 * {@link Command} execution.
	 */
	public T result();
}
