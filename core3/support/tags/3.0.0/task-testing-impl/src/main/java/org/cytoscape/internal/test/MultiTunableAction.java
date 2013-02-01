package org.cytoscape.internal.test;

/*
 * #%L
 * Tasks for Testing
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2006 - 2013 The Cytoscape Consortium
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as 
 * published by the Free Software Foundation, either version 2.1 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */

import java.awt.event.ActionEvent;
import javax.swing.JPanel;
import javax.swing.JLabel;

import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.work.*;


/**
 *
 */
public class MultiTunableAction extends AbstractCyAction {
	private final static long serialVersionUID = 1502339870257629L;

	private TaskManager tm;
	public MultiTunableAction(TaskManager tm) {
		super("Multi Tunable Normal");
		this.tm = tm;
		setPreferredMenu("Help");
	}

	public void actionPerformed(ActionEvent e) {
		TaskFactory tf = new DummyTaskFactory();
		tm.execute(tf.createTaskIterator());
	}

	public class DummyTaskFactory extends AbstractTaskFactory {
		public TaskIterator createTaskIterator() {
			return new TaskIterator( new DummyTask(), new DummyTask2() );
		}
	}

	public class DummyTask extends AbstractTask {
		@Tunable(description="int value1")
		public int value;
		public void run(TaskMonitor taskMonitor) throws Exception {
			Thread.sleep(1000);
			System.out.println("dummy 1 got value: " + value);
			Thread.sleep(1000);
		}
	}

	public class DummyTask2 extends AbstractTask {

		@Tunable(description="int value2")
		public int value;
		public void run(TaskMonitor taskMonitor) throws Exception {
			Thread.sleep(1000);
			System.out.println("dummy 2 got value: " + value);
			Thread.sleep(1000);
		}
	}
}


