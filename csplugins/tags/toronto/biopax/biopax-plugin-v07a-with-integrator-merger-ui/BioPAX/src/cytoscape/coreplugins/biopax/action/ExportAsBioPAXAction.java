/* Copyright 2008 - The Cytoscape Consortium (www.cytoscape.org)
 *
 * The Cytoscape Consortium is:
 * - Institute for Systems Biology
 * - University of California San Diego
 * - Memorial Sloan-Kettering Cancer Center
 * - Institut Pasteur
 * - Agilent Technologies
 *
 * Authors: B. Arman Aksoy, Thomas Kelder, Emek Demir
 * 
 * This file is part of PaxtoolsPlugin.
 *
 *  PaxtoolsPlugin is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  PaxtoolsPlugin is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this project.  If not, see <http://www.gnu.org/licenses/>.
 */

package cytoscape.coreplugins.biopax.action;

import cytoscape.util.CytoscapeAction;
import cytoscape.util.FileUtil;
import cytoscape.util.CyFileFilter;
import cytoscape.Cytoscape;
import cytoscape.CyNetwork;
import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;
import cytoscape.task.util.TaskManager;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.coreplugins.biopax.util.BioPaxUtil;
import cytoscape.data.CyAttributes;

import javax.swing.event.MenuEvent;
import java.awt.event.ActionEvent;
import java.io.*;

import org.biopax.paxtools.model.Model;
import org.biopax.paxtools.io.simpleIO.SimpleExporter;

public class ExportAsBioPAXAction extends CytoscapeAction {
    public ExportAsBioPAXAction() {
		super("Network as BioPAX...");
		setPreferredMenu("File.Export");
	}

    /**
	 * User-initiated action to save the current network in SIF format
	 * to a user-specified file.  If successfully saved, fires a
	 * PropertyChange event with property=Cytoscape.NETWORK_SAVED,
	 * old_value=null, and new_value=a three element Object array containing:
	 * <OL>
	 * <LI>first element = CyNetwork saved
	 * <LI>second element = URI of the location where saved
	 * <LI>third element = an Integer representing the format in which the
	 * Network was saved (e.g., Cytoscape.FILE_SIF).
	 * </OL>
	 * @param e ActionEvent Object.
	 */
    public void actionPerformed(ActionEvent e) {
		File file = FileUtil.getFile("Save Network as BioPAX", FileUtil.SAVE,
		                             new CyFileFilter[] {  });
		if (file != null) {
			String fileName = file.getAbsolutePath();

			if (!fileName.endsWith(".xml"))
				fileName = fileName + ".xml";

			ExportAsBioPAXTask task = new ExportAsBioPAXTask(fileName);

			JTaskConfig jTaskConfig = new JTaskConfig();
			jTaskConfig.setOwner(Cytoscape.getDesktop());
			jTaskConfig.displayCloseButton(true);
			jTaskConfig.displayStatus(true);
			jTaskConfig.setAutoDispose(false);

			TaskManager.executeTask(task, jTaskConfig);
		}
    }

    public void menuSelected(MenuEvent e) {
        CyNetwork cyNetwork = Cytoscape.getCurrentNetwork();

        if( BioPaxUtil.isBioPAXNetwork(cyNetwork) )
            enableForNetwork();
        else
            setEnabled(false);
    }
}

class ExportAsBioPAXTask implements Task {
	private String fileName;
	private TaskMonitor taskMonitor;

	ExportAsBioPAXTask(String fileName) {
		this.fileName = fileName;
	}

    public void run() {
		taskMonitor.setStatus("Saving BioPAX...");
        Model bpModel = BioPaxUtil.getNetworkModel(Cytoscape.getCurrentNetwork());
        CyAttributes networkAttributes = Cytoscape.getNetworkAttributes();
        String bpModelStr = (String) networkAttributes
        	.getAttribute(Cytoscape.getCurrentNetwork().getIdentifier(),
        		BioPaxUtil.BIOPAX_MODEL_STRING);
        try {
            FileOutputStream fOutput = new FileOutputStream(fileName);

            if(bpModel == null || bpModelStr == null )
                throw new IllegalArgumentException("Invalid/empty BioPAX model.");

            SimpleExporter simpleExporter = new SimpleExporter(bpModel.getLevel());
            simpleExporter.convertToOWL(bpModel, fOutput);

            fOutput.close();

			Object[] ret_val = new Object[3];
			ret_val[0] = Cytoscape.getCurrentNetwork();
			ret_val[1] = new File(fileName).toURI();
			ret_val[2] = Cytoscape.FILE_BIOPAX;
			Cytoscape.firePropertyChange(Cytoscape.NETWORK_SAVED, null, ret_val);

			taskMonitor.setPercentCompleted(100);
			taskMonitor.setStatus("Network successfully saved to:  " + fileName + ".");
		} catch (IllegalArgumentException e) {
			taskMonitor.setException(e, "Network is invalid. Cannot be saved.");
        } catch (IOException e) {
			taskMonitor.setException(e, "Unable to save network.");
        }
    }

	public void halt() {
        // No halt support
    }

	public void setTaskMonitor(TaskMonitor taskMonitor) throws IllegalThreadStateException {
		this.taskMonitor = taskMonitor;
	}

	public String getTitle() {
		return "Saving Network as BioPAX";
	}
}
