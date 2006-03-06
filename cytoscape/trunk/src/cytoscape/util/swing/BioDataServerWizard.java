
/*
  File: BioDataServerWizard.java 
  
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

package cytoscape.util.swing;

import java.awt.event.ActionEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

import com.nexes.wizard.Wizard;
import com.nexes.wizard.WizardPanelDescriptor;

import cytoscape.Cytoscape;
import cytoscape.CytoscapeInit;
import cytoscape.data.annotation.AnnotationGui;
import cytoscape.data.servers.BioDataServer;
import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.util.TaskManager;

/*
 * Bio Data Server Wizard utility.
 */
public class BioDataServerWizard {

	// The wizard object
	Wizard wizard;

	// 1st panel to select BDS source type.
	WizardPanelDescriptor descriptor1;

	// WizardPanelDescriptor descriptor2;

	// WizardPanelDescriptor descriptor3;

	// Panel to select old-style manifest file
	WizardPanelDescriptor descriptor4;

	// WizardPanelDescriptor descriptor5;

	// Panel to select OBO and GA file
	WizardPanelDescriptor descriptor6;

	// Parameters obtained from the wizard session
	private boolean flip;

	private String manifest;

	private String species;

	private int finalState;

	private String oldManifest;

	private final String FS = System.getProperty("file.separator");

	public BioDataServerWizard() {
		flip = false;
		manifest = "not specified";
		oldManifest = "not specified";
		species = null;
		finalState = -1;

		// Constructor for the wizard
		wizard = new Wizard(Cytoscape.getDesktop());
		// wizard.getDialog().getParent().setSize(1000, 1000);

		wizard.getDialog().setTitle("Gene Ontology Wizard");

		descriptor1 = new BioDataServerPanel1Descriptor();
		wizard.registerWizardPanel(BioDataServerPanel1Descriptor.IDENTIFIER,
				descriptor1);

		// descriptor2 = new BioDataServerPanel2Descriptor();
		// wizard.registerWizardPanel(BioDataServerPanel2Descriptor.IDENTIFIER,
		// descriptor2);

		// descriptor3 = new BioDataServerPanel3Descriptor();
		// wizard.registerWizardPanel(BioDataServerPanel3Descriptor.IDENTIFIER,
		// descriptor3);

		descriptor4 = new BioDataServerPanel4Descriptor();
		wizard.registerWizardPanel(BioDataServerPanel4Descriptor.IDENTIFIER,
				descriptor4);

		// descriptor5 = new BioDataServerPanel5Descriptor();
		// wizard.registerWizardPanel(BioDataServerPanel5Descriptor.IDENTIFIER,
		// descriptor5);

		descriptor6 = new BioDataServerPanel6Descriptor();
		wizard.registerWizardPanel(BioDataServerPanel6Descriptor.IDENTIFIER,
				descriptor6);

		// Set the start panel
		wizard.setCurrentPanel(BioDataServerPanel1Descriptor.IDENTIFIER);

	}

	// Show the wizard.
	public int show() {
		int ret = wizard.showModalDialog();

		// Getting the species name

		if (ret == Wizard.FINISH_RETURN_CODE) {
			flip = ((BioDataServerPanel6Descriptor) descriptor6).isFlip();

			manifest = ((BioDataServerPanel6Descriptor) descriptor6)
					.getManifestName();
			oldManifest = ((BioDataServerPanel4Descriptor) descriptor4)
					.getManifestFileName();
			// species = ((BioDataServerPanel3Descriptor) descriptor3)
			// .getSpeciesName();
			finalState = ((BioDataServerPanel4Descriptor) descriptor4)
					.getFinalState();

			LoadGeneOntologyTask task;

			if (finalState == 1) {
				File mfTest = new File(manifest);
				String mParent = null;
				if (mfTest.canRead()) {
					mParent = mfTest.getParent();
					// appendSpecies(mParent);
				}
				// Cytoscape.loadBioDataServer(manifest);

				task = new LoadGeneOntologyTask(manifest);

			} else {
				// Cytoscape.loadBioDataServer( oldManifest );
				task = new LoadGeneOntologyTask(oldManifest);
			}

			// Configure JTask Dialog Pop-Up Box
			JTaskConfig jTaskConfig = new JTaskConfig();
			jTaskConfig.setOwner(Cytoscape.getDesktop());
			jTaskConfig.displayCancelButton(true);
			jTaskConfig.displayCloseButton(true);
			jTaskConfig.displayStatus(true);
			jTaskConfig.setAutoDispose(false);

			// Execute Task in New Thread; pops open JTask Dialog Box.
			TaskManager.executeTask(task, jTaskConfig);

			// Show annotation window
			AnnotationGui antGui = new AnnotationGui();
			antGui.actionPerformed(null);
			
		}
		return ret;
	}

	/*
	 * This file append species name to the end of new manifest files
	 */
	public void appendSpecies(String parentPath) {
		boolean append = true;
		String autoManifest = parentPath + FS + "auto_generated_manifest";
		try {
			FileWriter fw = new FileWriter(autoManifest, append);
			BufferedWriter br = new BufferedWriter(fw);
			PrintWriter pw = new PrintWriter(br);
			pw.println("species=" + species);
			pw.close();
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
	}

	public boolean getFlip() {
		return flip;
	}

	public String getManifestFileName() {
		return manifest;
	}

}

/**
 * Task to Load New Network Data.
 */
class LoadGeneOntologyTask implements Task {

	private TaskMonitor taskMonitor;

	private String manifest;

	public LoadGeneOntologyTask(String target) {
		this.manifest = target;
	}

	/**
	 * Executes Task.
	 */
	public void run() {
		taskMonitor.setStatus("Reading Gene Ontology Database files...");

		taskMonitor.setPercentCompleted(-1);
		Cytoscape.loadBioDataServer(manifest);
		//taskMonitor.setPercentCompleted(80);

		BioDataServer bds = Cytoscape.getBioDataServer();

		if (bds.getAnnotationCount() != 0) {
			informUserOfServerStats(bds);
			// taskMonitor.setStatus("Gene Ontology Server loaded
			// successfully.");
		} else {
			StringBuffer sb = new StringBuffer();
			sb.append("Could not load Gene Ontology Server.");
			sb
					.append("\nSome of the data file may not be a valid ontology or annotation file.");
			taskMonitor.setException(new IOException(sb.toString()), sb
					.toString());
		}
		taskMonitor.setPercentCompleted(100);
	}

	private void informUserOfServerStats(BioDataServer server) {

		//
		// Display the summary of the Gene Ontology Server
		//
		String message = server.describe();
		String newMessage = "";

		String status = "";

		String[] oneEntry = message.split("\n");
		Arrays.sort(oneEntry);

		for (int i = 0; i < oneEntry.length; i++) {
			String[] element = oneEntry[i].split(",");

			if (element.length > 2) {
				for (int j = 0; j < element.length; j++) {

					if (element[j].startsWith("annotation") == false) {
						newMessage = newMessage + element[j] + "\n     ";
					}
				}
				newMessage = newMessage + "\n";
			}

		}

		status = "Summary of the Gene Ontology Server:\n\n" + newMessage
				+ "Default Species Name is set to "
				+ CytoscapeInit.getDefaultSpeciesName() + "\n\n"
				+ "Gene Ontology Server loaded successfully.";

		taskMonitor.setStatus(status);
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
	public void setTaskMonitor(TaskMonitor taskMonitor)
			throws IllegalThreadStateException {
		this.taskMonitor = taskMonitor;
	}

	/**
	 * Gets the Task Title.
	 * 
	 * @return Task Title.
	 */
	public String getTitle() {
		return new String("Loading Gene Ontology Database");
	}
}
