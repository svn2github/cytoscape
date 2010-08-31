package org.cytoscape.work;


import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;


public final class TaskIterator {
	private final List<Task> tasks;
	private int currentIndex;

	public TaskIterator(final Task... initialTasks) {
		this.tasks = new ArrayList<Task>(initialTasks.length);
		this.currentIndex = 0;

		for (final Task initialTask : initialTasks)
			tasks.add(initialTask);
		
	}

	/** Pushes "newTask" to the current end of the iterator.
	 */
	public void addTaskAtEnd(final Task newTask) {
		tryToAddSelfReferenceToTask(newTask);
		tasks.add(newTask);
	}

	/** Inserts "newTask" immediately after "referenceTask".
	 *  @throws IllegalStateException if "referenceTask" is not known to the iterator.
	 */
	public void insertTaskAfter(final Task referenceTask, final Task newTask) throws IllegalStateException {
		tryToAddSelfReferenceToTask(newTask);
		final int referenceIndex = tasks.indexOf(referenceTask);
		if (referenceIndex == -1)
			throw new IllegalStateException("invalid reference task in call to insertTaskAfter()!");
		tasks.add(referenceIndex + 1, newTask);
	}

	public boolean hasNext() {
		return currentIndex < tasks.size();
	}

	public Task next() {
		if (currentIndex < tasks.size()) {
			++currentIndex;
			return tasks.get(currentIndex - 1);
		}

		throw new NoSuchElementException("call to next() even though hasNext() is false!");
	}

	/**
	 *  Let's one look at the current Task without advancing, unlike next().
	 *  @return the current Task or null if the iterator is at the end
	 */
	public Task peek() {
		return (currentIndex == tasks.size()) ? null : tasks.get(currentIndex);
	}

	/**
	 *  Unsupported -&gt; always throws an exception!
	 */
	public void remove() {
		throw new UnsupportedOperationException("TaskIteratorImpl.remove() has not been implemented!");
	}

	private void tryToAddSelfReferenceToTask(final Task newTask) {
		if (newTask instanceof IteratorAwareTask) {
			try {
				((IteratorAwareTask)newTask).setTaskIterator(this);
			} catch (final Exception e) {
				// The above cast must always succeed and therefore we should never get here!
			}
		}
	}
}


