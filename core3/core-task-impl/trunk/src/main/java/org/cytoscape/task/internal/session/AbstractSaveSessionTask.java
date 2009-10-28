/*
 File: AbstractSaveSessionTask.java

 Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)

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
package org.cytoscape.task.internal.session;


import org.cytoscape.io.write.CyWriterManager;
import org.cytoscape.io.write.CyWriter;
import org.cytoscape.io.DataCategory;

import org.cytoscape.work.Task;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;
import org.cytoscape.task.AbstractTask;

import org.cytoscape.session.CySessionManager;

import java.io.File;


abstract class AbstractSaveSessionTask extends AbstractTask {

	private String SESSION_EXT = ".cys";
	
	private CySessionManager mgr;
	private CyWriterManager factory;

	@Tunable(description="Save session as")
	public File file;

	/**
	 * setAcceleratorCombo(KeyEvent.VK_S, ActionEvent.CTRL_MASK);
	 */
	public AbstractSaveSessionTask(CySessionManager mgr, CyWriterManager factory) {
		this.mgr = mgr;
		this.factory = factory;
	}

	public abstract void run(TaskMonitor taskMonitor) throws Exception;

	/**
	 * If no current session file exists, open dialog box to ask user a new
	 * session file name, otherwise, overwrite the file.
	 */
	protected void saveSession(TaskMonitor taskMonitor) throws Exception {

		String name = file.getAbsolutePath();

		CyWriter sw = factory.getWriter(DataCategory.SESSION);

		taskMonitor.setStatusMessage("Saving Cytoscape Session.\n\nIt may take a while.  Please wait...");
		taskMonitor.setProgress(-1.0);

		try {
			sw.write(file);
		} catch (Exception e) {
			throw new Exception("Could not write session to the file: " + name, e);
		}

		taskMonitor.setProgress(1.0);
		taskMonitor.setStatusMessage("Session successfully saved to:  " + name);

		// TODO  This should fire an event that updates appropriate windows and such-like.
		mgr.setCurrentSessionFileName(file.getName());
	}

} 
