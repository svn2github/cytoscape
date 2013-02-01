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

import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.work.*;


/**
 *
 */
public class WaitAction extends AbstractCyAction {
	private final static long serialVersionUID = 1502339870257629L;

	private TaskManager tm;
	public WaitAction(TaskManager tm) {
		super("Wait Normal");
		this.tm = tm;
		setPreferredMenu("Help");
	}

	public void actionPerformed(ActionEvent e) {
		TaskFactory tf = new DummyTaskFactory();
		tm.execute(tf.createTaskIterator());
		System.out.println("finished waiting");
	}

	private class DummyTaskFactory extends AbstractTaskFactory {
		public TaskIterator createTaskIterator() {
			return new TaskIterator( new DummyTask() );
		}
	}

	private class DummyTask implements Task {
		boolean cancelled = false;
		public void run(TaskMonitor taskMonitor) throws Exception {
			taskMonitor.setProgress(0.0);
			taskMonitor.setStatusMessage("Excuting DUMMY ...");
			int i = 0; 
			while(i++ < 10){
				System.out.println("still DUMMY working...");
				Thread.sleep(1000);
				if ( cancelled ) {
					System.out.println("cancelling Infinite DUMMY Task");
					return;
				}
			}
			System.out.println("DUMMY finished...");
		}
		public void cancel() {
			System.out.println("task cancel called");
			cancelled = true;
		}
	}
}


