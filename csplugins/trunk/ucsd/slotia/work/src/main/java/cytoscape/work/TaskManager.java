package cytoscape.work;

/**
 * Runs a <code>Task</code> by creating
 * an interface and its own <code>Thread</code>
 * for executing the <code>Task</code>.
 *
 * <code>TaskManager</code>s detect if a 
 * <code>Task</code> implements <code>Progressable</code>
 * through Java Reflections. If <code>Progressable</code>
 * is implemented, the <code>TaskManager</code> 
 * displays a progress bar in its interface.
 */
public interface TaskManager
{
	public void execute(Task task);
}
