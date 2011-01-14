/* vim: set ts=2: */
/**
 * Copyright (c) 2006 The Regents of the University of California.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *   1. Redistributions of source code must retain the above copyright
 *      notice, this list of conditions, and the following disclaimer.
 *   2. Redistributions in binary form must reproduce the above
 *      copyright notice, this list of conditions, and the following
 *      disclaimer in the documentation and/or other materials provided
 *      with the distribution.
 *   3. Redistributions must acknowledge that this software was
 *      originally developed by the UCSF Computer Graphics Laboratory
 *      under support by the NIH National Center for Research Resources,
 *      grant P41-RR01081.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDER "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE REGENTS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT
 * OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 * BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */
package structureViz.ui;

// System imports
import java.awt.event.ActionEvent;

import java.awt.Component;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

// Cytoscape imports
import cytoscape.Cytoscape;
import cytoscape.CyNode;
import cytoscape.logger.CyLogger;
import cytoscape.view.CytoscapeDesktop;
import cytoscape.view.CyNetworkView;

// structureViz imports
import structureViz.StructureViz;
import structureViz.actions.Chimera;
import structureViz.actions.OpenTask;
import structureViz.model.Structure;
import structureViz.ui.ModelNavigatorDialog;
import structureViz.ui.AlignStructuresDialog;

/**
 * The StructureViz class provides the primary interface to the
 * Cytoscape plugin mechanism
 */
public class StructureVizMenuHandler 
  implements ActionListener, ItemListener {

 	private static final long serialVersionUID = 1;
	private static Chimera chimera = null;
	private static AlignStructuresDialog alDialog = null;
	private int command;
	private static boolean showModelWarning = true;
	private Object userData = null; // Either a Structure or an ArrayList
	private CyLogger logger;
	private	OpenTask openTask = null;

	public StructureVizMenuHandler(int command, Object userData, CyLogger logger) {
		this.command = command;
		this.userData = userData;
		this.logger = logger;
	}

   /**
    * This method is called when the user selects the menu item.
    */
   public void actionPerformed(ActionEvent ae) {
		String label = ae.getActionCommand();
		if (command == StructureViz.OPEN) {
			openAction(label, null, false);
		} else if (command == StructureViz.EXIT) {
			exitAction();
		} else if (command == StructureViz.ALIGN) {
			alignAction(label);
		} else if (command == StructureViz.SELECTRES) {
			selectResiduesAction();
		} else if (command == StructureViz.FINDMODELS) {
			findModelsAction();
		} else if (command == StructureViz.CLOSE) {
			closeAction(label);
		} else if (command == StructureViz.SALIGN) {
			seqAlignAction(label);
		} else if (command == StructureViz.COMPARE) {
			seqCompareAction(label);
		}
	}

	/**
	 * Perform the action associated with an align menu selection
	 *
	 * @param label the Label associated with this command
	 */
	private void alignAction(String label) {
		// Launch Chimera (if necessary)
		boolean isLaunched = (chimera != null && chimera.isLaunched());
		if (!isLaunched) {
			chimera = launchChimera();
		} 

		if (chimera.getDialog() == null) {
			ModelNavigatorDialog.LaunchModelNavigator(Cytoscape.getDesktop(), chimera);
		}

		chimera.getDialog().setVisible(true);

		List<Structure> structures = (List<Structure>)userData;

		// If the structures aren't opened -- open them
		for (Structure structure: structures) {
			int modelNumber = structure.modelNumber();
			if (!chimera.containsModel(structure.modelNumber()) || 
			    !chimera.getModel(modelNumber).getModelName().equals(structure.name()));
				openAction(structure.name(), structure, true);
		}

		// Bring up the dialog
		alDialog = 
							new AlignStructuresDialog(chimera.getDialog(), chimera, structures);
		alDialog.pack();
		alDialog.setLocationRelativeTo(Cytoscape.getDesktop());
		alDialog.setVisible(true);
		chimera.setAlignDialog(alDialog);
	}

	/**
 	 * Search modbase for modeled structures that correspond to this identifier.
 	 * Since modeled structures can be somewhat misleading, popup a warning dialog
 	 * while we do the fetch.
 	 */
	private void findModelsAction() {
		CyNode node = (CyNode)userData;
		final StructureVizMenuHandler listener = this;

		// Bring up the warning dialog, but do it in a thread to allow the load to continue
		if (showModelWarning) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					String message = "<html><b>Warning</b>: Modeled structures are predictions, not experimental data.<br>";
					message += "These structures are from the ModBase web service at <a href=\"http://modbase.salilab.org\">http://modbase.salilab.org/</a><br>";
					message += "Measures of model reliability, or likelihood of correctness, are provided in the<br>";
					message += "Chimera ModBase Model List.</html>";
					Object[] options = new Object[3];
					JCheckBox enough = new JCheckBox("Don't show this message again");
					enough.addItemListener(listener);
					enough.setSelected(false);
					options[0] = enough;
					options[1] = new JLabel("      ");
					options[2] = "OK";
					JOptionPane dialog = new JOptionPane(message, 
						                                   JOptionPane.WARNING_MESSAGE,
					                                     JOptionPane.DEFAULT_OPTION,
					                                     null, 
					                                     options);
					JDialog jd = dialog.createDialog(chimera.getDialog(), "Modeled Structure Warning");
					jd.pack();
					jd.setVisible(true);
				}
			});
		}
		String ident = node.getIdentifier();
		if (ident.startsWith("gi"))
			ident = ident.substring(2);
		Structure struct = new Structure(ident, node, Structure.StructureType.MODBASE_MODEL);
		userData = struct;
		openAction(ident, null, false);
	}

	public void itemStateChanged(ItemEvent e) {
		if (e.getStateChange() == ItemEvent.DESELECTED) {
			showModelWarning = true;
		} else if (e.getStateChange() == ItemEvent.SELECTED) {
			showModelWarning = false;
		}
	}

	/**
 	 * Open the structures (if necessary) and select the residues.  Residues
 	 * can be formatted as pdb1#res1,pdb1#res2,pdb2#res1 or just res1,res2,res3.
 	 * In the latter case, the residues are assumed to be on all of the associated
 	 * structures.
 	 */
	private void selectResiduesAction() {
		List<Structure> structuresList = (List<Structure>)userData;

		String command = "select ";
		for (Structure structure: structuresList) {
			// Select the residues
			List<String> residueL = structure.getResidueList();
			if (residueL == null || residueL.size() == 0) continue;

			// OK, we have a residue list, make sure the structure is open
			int model = structure.modelNumber();
			if (chimera == null || chimera.getChimeraModel(model) == null) {
				openAction(structure.name(), structure, true);
			}

			// The residue list is of the form RRRnnn,RRRnnn.  We want
			// to reformat this to nnn,nnn
			String residues = new String();
			for (String residue: residueL) {
				residues = residues.concat(residue+",");
			}
			residues = residues.substring(0,residues.length()-1);
			logger.debug("selectResiduesAction: structure: "+structure+" residues: "+residues);

			command = command.concat(" #"+structure.modelNumber()+":"+residues);
		}
		chimera.select(command);
		chimera.modelChanged();
	}

	/**
	 * Exit Chimera and the plugin
	 */
	private void exitAction() {
		if (chimera.getDialog() != null) {
			// get rid of the dialog
			chimera.getDialog().setVisible(false);
			chimera.getDialog().dispose();
			chimera.setDialog(null);
		}
		if (alDialog != null) {
			alDialog.setVisible(false);
			alDialog.dispose();
			alDialog = null;
			chimera.setAlignDialog(alDialog);
		}
		if (chimera != null) {
			chimera.exit();
			chimera = null;
		}
	}

	/**
	 * Close a Chimera molecule
	 */
	private void closeAction(String commandLabel) {
		List<Structure>structList;
		if (chimera != null) {
			if (commandLabel.compareTo("all") != 0) {
				structList = new ArrayList<Structure>();
				structList.add((Structure)userData);
			} else {
				structList = (ArrayList)userData;
			}
			ListIterator iter = structList.listIterator();
			while (iter.hasNext()) {
				Structure structure = (Structure)iter.next();
				chimera.close(structure);
				// Not open any more -- remove it
				iter.remove();
			}
		}
		if (chimera.getDialog() != null) chimera.getDialog().modelChanged();
	}

	/**
	 * Open a pdb model in Chimera
	 */
	private void openAction(String commandLabel, Object dataOverride, boolean wait) {
		// Make sure chimera is launched
		if (chimera == null || !chimera.isLaunched())
			chimera = launchChimera();

		if (chimera == null) 
			return;

		if (dataOverride == null) {
			if (openTask == null) {
				logger.debug("Opening structure: "+commandLabel);
				openTask = new OpenTask(commandLabel, chimera, userData);
			}
		} else {
			logger.debug("Opening structure: "+commandLabel);
			openTask = new OpenTask(commandLabel, chimera, dataOverride);
		}

		if (chimera.getDialog() == null) {
			ModelNavigatorDialog.LaunchModelNavigator(Cytoscape.getDesktop(), chimera);
		}

		chimera.getDialog().setVisible(true);

		if (!wait)
			new Thread(openTask).start();
		else
			openTask.run();

	}

	/**
	 * Align two sequences and open the resulting alignment in Chimera
	 */
	private void seqAlignAction(String commandLabel) {
		List sequenceList = (List)userData;
		// Start a new thread
		// Call backend to calculate alignment
		// Open resulting alignment in Chimera
	}

	/**
	 * Compare two sequences and use the results to add or update
	 * Cytoscape attributes on edges connecting the :w
	 *
	 */
	private void seqCompareAction(String commandLabel) {
		List sequenceList = (List)userData;
		// Start a new thread
		// Iterate through all pairs
		// Call backend to calculate comparison
		// Store results back onto connecting edge
	}

	private Chimera launchChimera() {
		Chimera chimera = Chimera.GetChimeraInstance(Cytoscape.getCurrentNetworkView(), logger);
		if (chimera.isLaunched())
			return chimera;

		boolean launched = false;
		String message = "Unable to launch UCSF Chimera";
		try {
			launched = chimera.launch();
		} catch (IOException e) {
			message += ": "+e.getMessage();
		}
		if (!launched) {
			JOptionPane.showMessageDialog(Cytoscape.getDesktop(), message,
					"Failure to Launch", JOptionPane.ERROR_MESSAGE);
			chimera = null;
		}
		return chimera;
	}

}
