package org.cytoscape.work;


public abstract class AbstractTask extends IteratorAwareTask {
	private boolean cancelled;


	public AbstractTask() {
		cancelled = false;
	}

	public synchronized void cancel() {
		cancelled = true;
	}

	final public synchronized boolean cancelled() {
		return this.cancelled;
	}
}
