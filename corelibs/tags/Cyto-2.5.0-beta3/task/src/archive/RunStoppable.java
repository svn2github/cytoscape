
/*
 Copyright (c) 2006, 2007, The Cytoscape Consortium (www.cytoscape.org)

 The Cytoscape Consortium is:
 - Institute for Systems Biology
 - University of California San Diego
 - Memorial Sloan-Kettering Cancer Center
 - Institut Pasteur
 - Agilent Technologies

 This library is free software; you can redistribute it and/or modify it
 under the terms of the GNU Lesser General Public License as published
 by the Free Software Foundation; either version 2.1 of the License, or
 any later version.

 This library is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 documentation provided hereunder is on an "as is" basis, and the
 Institute for Systems Biology and the Whitehead Institute
 have no obligations to provide maintenance, support,
 updates, enhancements or modifications.  In no event shall the
 Institute for Systems Biology and the Whitehead Institute
 be liable to any party for direct, indirect, special,
 incidental or consequential damages, including lost profits, arising
 out of the use of this software and its documentation, even if the
 Institute for Systems Biology and the Whitehead Institute
 have been advised of the possibility of such damage.  See
 the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation,
 Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
*/

package archive;

import cytoscape.task.Task;


/**
 * Creates a <code>Stoppable</code> out of a <code>Task</code>.
 * Code that uses an instance of this class should not
 * use the <code>Task.run()</code> or <code>Task.halt()</code> methods of
 * the underlying <code>Task</code> - <code>RunStoppable.run()</code> and
 * <code>RunStoppable.stop()</code> should be used instead.
 */
public final class RunStoppable implements Runnable, Stoppable {
	private final Task m_task;
	private final Object m_lock = new Object();
	private boolean m_ran = false;
	private boolean m_running = false;
	private boolean m_stop = false;

	/**
	 * Creates a <code>Runnable</code> and <code>Stoppable</code> out of
	 * <code>task</code>.  Code using an instance of
	 * <code>RunStoppable</code> should never call
	 * <code>task.run()</code> or <code>task.halt()</code>.
	 * <font color="#ff0000">This <code>RunStoppable</code> object relies on
	 * being the sole caller of <code>task.run()</code> and
	 * <code>task.halt()</code></font>.
	 */
	public RunStoppable(Task task) {
		if (task == null)
			throw new NullPointerException("task is null");

		m_task = task;
	}

	/**
	 * This method is guaranteed to run at most once - that is, it will call
	 * the underlying <code>Task.run()</code> at most once.
	 * If this method is invoked a second time, it will throw an
	 * <code>IllegalStateException</code>.
	 */
	public void run() {
		synchronized (m_lock) {
			if (m_ran)
				throw new IllegalStateException("already running or ran");

			m_ran = true;
		}

		// Guaranteed to get to this line of code at most once.
		synchronized (m_lock) {
			if (m_stop)
				return;

			m_running = true;
		}

		try {
			m_task.run();
		} finally {
			synchronized (m_lock) {
				m_running = false;
				m_lock.notifyAll();
			}
		}
	}

	/**
	 * Calls the underlying <code>Task.halt()</code> and blocks until
	 * the underlying <code>Task.run()</code> has finished.
	 * This method actually supports multiple threads; all calling threads
	 * will block until the underlying <code>Task.run()</code> returns.
	 */
	public void stop() {
		boolean mustCallHalt = true;

		synchronized (m_lock) {
			if (m_stop) // Someone has called stop() before us; we therefore
			            // have already or will eventually call halt() somewhere
			            // else, maybe in another thread.
				mustCallHalt = false;
			else
				m_stop = true;

			if (!m_running)
				return; // We don't even bother calling halt().
				        // this.run() is guaranteed not to run() the
				        // underlying Runnable.
		}

		// By this line of code, regardless of thread, the run() method
		// will have been called.  It may or may not still be executing by this
		// time.
		if (mustCallHalt)
			m_task.halt(); // This isn't necessary, but we do it
			               // anyways: limit calling halt() to
			               // at most once.

		synchronized (m_lock) {
			while (m_running) {
				try {
					m_lock.wait();
				} catch (InterruptedException exc) {
				}
			}
		}
	}
}
