/*
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
package org.cytoscape.coreplugin.psi_mi.plugin;

import cytoscape.Cytoscape;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.util.TaskManager;
import cytoscape.util.CyFileFilter;
import cytoscape.util.CytoscapeAction;
import cytoscape.util.FileUtil;

import java.awt.event.ActionEvent;
import java.io.File;


/**
 * Action to Export Network as PSI-MI Level 2.5
 *
 * @author Ethan Cerami
 */
public class ExportNetworkAsPsiMiTwoFive extends CytoscapeAction {
	/**
	 * Constructor.
	 */
	public ExportNetworkAsPsiMiTwoFive() {
		super("Network as PSI-MI Level 2.5 File...");
		setPreferredMenu("File.Export");
	}

	/**
	 * User-initiated action to save the current network in PSI-MI format
	 * to a user-specified file.  If successfully saved, fires a
	 * PropertyChange event with property=Cytoscape.NETWORK_SAVED,
	 * old_value=null, and new_value=a three element Object array containing:
	 * <OL>
	 * <LI>first element = GraphPerspective saved
	 * <LI>second element = URI of the location where saved
	 * <LI>third element = an Integer representing the format in which the
	 * Network was saved (e.g., Cytoscape.FILE_PSI_MI).
	 * </OL>
	 *
	 * @param e ActionEvent Object.
	 */
	public void actionPerformed(ActionEvent e) {
		// get the file name
		File file = FileUtil.getFile("Save Network as PSI-MI Level 2.5", FileUtil.SAVE,
		                             new CyFileFilter[] {  });

		if (file != null) {
			String fileName = file.getAbsolutePath();

			if (!fileName.endsWith(".xml")) {
				fileName = fileName + ".xml";
			}

			//  Create LoadNetwork Task
			SaveAsPsiTask task = new SaveAsPsiTask(fileName, SaveAsPsiTask.EXPORT_PSI_MI_LEVEL_2_5);

			//  Configure JTask Dialog Pop-Up Box
			JTaskConfig jTaskConfig = new JTaskConfig();
			jTaskConfig.setOwner(Cytoscape.getDesktop());
			jTaskConfig.displayCloseButton(true);
			jTaskConfig.displayStatus(true);
			jTaskConfig.setAutoDispose(false);

			//  Execute Task in New Thread;  pop open JTask Dialog Box.
			TaskManager.executeTask(task, jTaskConfig);
		}
	}
}
