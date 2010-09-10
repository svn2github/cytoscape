package org.cytoscape.work;


/**
 *  A base class for tasks that need to be able to access the TaskIterator that contains them.
 */
public abstract class AbstractTask implements Task {
	protected boolean cancelled = false;

	private TaskIterator taskIterator;

	/** This method is typically used by a TaskIterator to set itself on the newly added Task.
	 */
	final public void setTaskIterator(final TaskIterator taskIterator) {
		this.taskIterator = taskIterator;
	}

	/** Inserts "newTasks" after the current Task, in the TaskIterator that is being managed by this class.
	 */
	final protected void insertTasksAfterCurrentTask(final Task... newTasks) {
		taskIterator.insertTasksAfter(this, newTasks);
	}

	/** Inserts "newTasks" after the current Task, in the TaskIterator that is being managed by this class.
	 */
	final protected void insertTasksAfterCurrentTask(final TaskIterator newTasks) {
		taskIterator.insertTasksAfter(this, newTasks);
	}

	/** Calling this attempts to abort the current task.  How well this works depends on the granularity of
	 *  a Task's checking whether "cancelled" is true or not and then taking appropriate action.
	 */
	public void cancel() {
		cancelled = true;
	}
}
