package org.cytoscape.work;


/**
 *  A base class for tasks that need to be able to access the iterator that contains them.
 */
public abstract class AbstractTask implements Task {
	private TaskIterator taskIterator;

	/** This method is typically used by a TaskIterator to set itself on the newly added Task.
	 */
	final public void setTaskIterator(final TaskIterator taskIterator) {
		this.taskIterator = taskIterator;
	}

	/** Adds a Task to the end of the iterator that is managed by this class.
	 */
	final protected void addTaskAtEnd(final Task newTask) {
		taskIterator.addTaskAtEnd(newTask);
	}

	/** Inserts "newTask" after the current Task, in the iterator that is being managed by this class.
	 */
	final protected void insertTaskAfterCurrentTask(final Task newTask) {
		taskIterator.insertTaskAfter(this, newTask);
	}
}
