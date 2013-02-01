
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


import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;


public class SingleTask extends AbstractTask {
	private boolean showProgress;

	public SingleTask(boolean s) {
		showProgress = s;
	}

	public void run(final TaskMonitor taskMonitor) throws Exception {
		if ( !showProgress )
			taskMonitor.setProgress(-1.0);

		double progress = 0.0;
		if ( showProgress ) taskMonitor.setProgress(progress);
		taskMonitor.setStatusMessage("Excuting task...");
		while(progress < 1.0){ 
			if ( showProgress ) taskMonitor.setStatusMessage("executing step: " + progress);
			if ( showProgress ) taskMonitor.setProgress(progress);
			Thread.sleep(200);
			progress += 0.1;
		}
	}
}
