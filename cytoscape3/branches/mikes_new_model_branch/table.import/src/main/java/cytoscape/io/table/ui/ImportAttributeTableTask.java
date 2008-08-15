
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

package cytoscape.io.table.ui;

import cytoscape.Cytoscape;
import cytoscape.io.table.reader.TextTableReader;
import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;


/**
 *
 */
public class ImportAttributeTableTask implements Task {
	private TextTableReader reader;
	private String source;
	private TaskMonitor taskMonitor;

	/**
	 * Constructor.
	 *
	 * @param file
	 *            File.
	 * @param fileType
	 *            FileType, e.g. Cytoscape.FILE_SIF or Cytoscape.FILE_GML.
	 */
	public ImportAttributeTableTask(TextTableReader reader, String source) {
		this.reader = reader;
		this.source = source;
	}

	/**
	 * Executes Task.
	 */
	public void run() {
		taskMonitor.setStatus("Loading attribute data file...");
		taskMonitor.setPercentCompleted(-1);

		try {
			reader.readTable();
			taskMonitor.setPercentCompleted(100);
			Cytoscape.firePropertyChange(Cytoscape.ATTRIBUTES_CHANGED,null,null);
			informUserOfAnnotationStats();
		} catch (Exception e) {
			e.printStackTrace();
			taskMonitor.setException(e, "Unable to import annotation data.");
		}
	}

	/**
	 * Inform User of Network Stats.
	 */
	private void informUserOfAnnotationStats() {
		StringBuffer sb = new StringBuffer();

		// Give the user some confirmation
		sb.append("Succesfully loaded attribute data from:\n\n");
		sb.append(source + "\n\n");

		sb.append(reader.getReport());

		taskMonitor.setStatus(sb.toString());
	}

	/**
	 * Halts the Task: Not Currently Implemented.
	 */
	public void halt() {
		// Task can not currently be halted.
	}

	/**
	 * Sets the Task Monitor.
	 *
	 * @param taskMonitor
	 *            TaskMonitor Object.
	 */
	public void setTaskMonitor(TaskMonitor taskMonitor) throws IllegalThreadStateException {
		this.taskMonitor = taskMonitor;
	}

	/**
	 * Gets the Task Title.
	 *
	 * @return Task Title.
	 */
	public String getTitle() {
		return new String("Loading Attributes");
	}
}
