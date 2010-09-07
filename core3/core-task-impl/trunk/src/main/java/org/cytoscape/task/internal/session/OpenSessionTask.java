/*
 File: OpenSessionTask.java

 Copyright (c) 2006, 2010, The Cytoscape Consortium (www.cytoscape.org)

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
package org.cytoscape.task.internal.session; 


import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;
import org.cytoscape.session.CySessionManager;
import org.cytoscape.io.read.CyNetworkViewReaderManager;
import org.cytoscape.io.read.CyDataTableReader;
import org.cytoscape.io.DataCategory;

import java.io.File;


/**
 * Call the session reader and read everything in the zip archive.<br>
 * setAcceleratorCombo(java.awt.event.KeyEvent.VK_O, ActionEvent.CTRL_MASK);
 */
public class OpenSessionTask extends AbstractTask {
	private CySessionManager mgr;
	private CyNetworkViewReaderManager factory;

	@Tunable(description="Session file to load")
	public File file;

	/**
	 * Constructor.<br>
	 * Add a menu item under "File" and set shortcut.
	 */
	public OpenSessionTask(CySessionManager mgr, CyNetworkViewReaderManager factory) {
		this.mgr = mgr;
		this.factory = factory;
	}

	/**
	 * Clear current session and open the cys file.
	 */
	public void run(TaskMonitor taskMonitor) throws Exception {
		String name = file.toString();

		// Close all networks in the workspace.
		//mgr.setSessionState(Cytoscape.SESSION_OPENED);
		mgr.createNewSession();
		//mgr.setSessionState(Cytoscape.SESSION_NEW);


		taskMonitor.setStatusMessage("Opening Session File.\n\nIt may take a while.\nPlease wait...");
		taskMonitor.setProgress(0.0);

		CyDataTableReader sr;

		try {
			//sr = factory.getReader(file.toURI(), DataCategory.SESSION);
			//sr.read();
		} catch (Exception e) {
			throw new Exception("Cannot open the session file: " + name, e);
		} finally {
			sr = null;
		}

		mgr.setCurrentSessionFileName(name);

		taskMonitor.setProgress(1.0);
		taskMonitor.setStatusMessage("Session file " + name + " successfully loaded.");
	}

	@Override
	public void cancel() {
	}
}
