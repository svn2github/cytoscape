/* 
  File: LayoutTask.java

  Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)

  The Cytoscape Consortium is:
  - Institute for Systems Biology
  - University of California San Diego
  - Memorial Sloan-Kettering Cancer Center
  - Pasteur Institute
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
package cytoscape.internal.layout.ui;

import cytoscape.Cytoscape;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.layout.CyLayoutAlgorithm;
import org.cytoscape.view.model.CyNetworkView;
import java.awt.Container; 

/**
 * A wrapper for applying a layout in a task. Use it something like
 * this:
 * <p>taskManager.execute( new LayoutTask(layout, view) );

 */
public class LayoutTask implements Task {

	CyLayoutAlgorithm layout;
	CyNetworkView view;
	TaskMonitor monitor;

	/**
	 * Creates the task.
	 * 
	 * @param layout The CyLayoutAlgorithm to apply.
	 * @param view The view the algorithm should be applied to.
	 */
	public LayoutTask(CyLayoutAlgorithm layout,CyNetworkView view) {
		this.layout = layout; 
		this.view = view; 
	}

	/**
	 * Run the algorithm.  
	 */
	public void run(TaskMonitor monitor) {
		layout.doLayout(view,monitor);
	}

	/**
	 * Halt the algorithm if the CyLayoutAlgorithm supports it.
	 */
	public void cancel() {
		layout.halt();
	}
}
