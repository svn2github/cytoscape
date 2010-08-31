/*
 Copyright (c) 2010, The Cytoscape Consortium (www.cytoscape.org)

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
package org.cytoscape.work;


import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;


public class IteratorAwareTaskTest {
	private TaskIterator iter;
	private SimpleIteratorAwareTask initialTask;

	@Before
	public void init() {
		initialTask = new SimpleIteratorAwareTask(1);
		iter = new TaskIterator(initialTask);
	}

	@Test
	public final void testTaskInsertion() throws Exception {
		initialTask.addTaskAtEnd(new SimpleIteratorAwareTask(2));
		initialTask.addTaskAtEnd(new SimpleIteratorAwareTask(3));
		initialTask.insertTaskAfterCurrentTask(new SimpleIteratorAwareTask(4));

		final int expectedSequence[] = { 1, 4, 2, 3 };
		for (int taskId : expectedSequence) {
			assertTrue("Invalid task count in iterator!", iter.hasNext());
			final Task task = iter.next();
			assertEquals("Task ID does not match expected ID!", taskId, ((SimpleIteratorAwareTask)task).getId());
		}
		assertFalse("Invalid task count in iterator!", iter.hasNext());
	}
}


class SimpleIteratorAwareTask extends IteratorAwareTask {
	private int id;

	SimpleIteratorAwareTask(final int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void run(TaskMonitor taskMonitor) throws Exception {
		// Intentionally do nothing!
	}

	public void cancel() {
		// Intentionally do nothing!
	}

	public boolean cancelled() {
		return false;
	}
}