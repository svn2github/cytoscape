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

import static org.mockito.Mockito.*;


public class SuperTaskTest {
	@Test
	public final void testConstructor1() {
		final Task t1 = mock(Task.class);
		final Task t2 = mock(Task.class);
		final Task t3 = mock(Task.class);
		new SuperTask(t1, t2, t3);
	}

	@Test
	public final void testConstructor2() {
		final Task[] tasks = new Task[] { mock(Task.class), mock(Task.class), mock(Task.class) };
		final double[] weights = new double[] { 1.0, 2.0, 3.0 };
		new SuperTask(tasks, weights);
	}

	@Test(expected=IllegalArgumentException.class)
	public final void testWrongNumberOfWeights() {
		final Task[] tasks = new Task[] { mock(Task.class), mock(Task.class), mock(Task.class) };
		final double[] weights = new double[] { 1.0, 2.0 };
		new SuperTask(tasks, weights);
	}

	@Test(expected=IllegalArgumentException.class)
	public final void testInvalidNegativeWeight() {
		final Task[] tasks = new Task[] { mock(Task.class), mock(Task.class), mock(Task.class) };
		final double[] weights = new double[] { 1.0, 2.0, -3.0 };
		new SuperTask(tasks, weights);
	}

	@Test
	public final void testCancel() {
		final Task[] tasks = new Task[] { mock(Task.class), mock(Task.class), mock(Task.class) };
		final double[] weights = new double[] { 1.0, 2.0, 3.0 };
		final SuperTask superTask = new SuperTask(tasks, weights);
		superTask.cancel();
		assertTrue("Invalid cancellation state!", superTask.cancelled());
	}

	@Test
	public final void testRunAndSubTaskMonitor() throws Exception {
		final Task[] tasks = new Task[] { mock(Task.class), mock(Task.class), mock(Task.class) };
		final double[] weights = new double[] { 1.0, 2.0, 3.0 };
		final SuperTask superTask = new SuperTask(tasks, weights);
		final TaskMonitor taskMonitor = mock(TaskMonitor.class);
		superTask.run(taskMonitor);
	}
}
