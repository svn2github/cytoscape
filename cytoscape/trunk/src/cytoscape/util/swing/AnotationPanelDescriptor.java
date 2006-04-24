/*
 File: BioDataServerPanel6Descriptor.java 
 
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
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Map;

import com.nexes.wizard.WizardPanelDescriptor;

/*
 * This panel is for new Gene Ontology data formats. Ask user a OBO file and
 * multiple Gene Association Files.
 */
public class AnotationPanelDescriptor extends WizardPanelDescriptor implements
		ActionListener {

	// Name of the panel
	public static final String IDENTIFIER = "ANNOTATION_CHOOSE_PANEL";

	AnotationPanel anotationPanel;
	boolean oboFlag, gaFlag;
	boolean flip;

	String manifestFullPath;
	String speciesName = null;

	File oboFile = null;
	File gaFile = null;

	public AnotationPanelDescriptor() {

		oboFlag = false;
		gaFlag = false;
		flip = false;
		manifestFullPath = null;

		anotationPanel = new AnotationPanel();
		anotationPanel.addButtonActionListener(this);

		setPanelDescriptorIdentifier(IDENTIFIER);
		setPanelComponent(anotationPanel);

	}

	public Object getNextPanelDescriptor() {
		return SpeciesPanelDescriptor.IDENTIFIER;
	}

	public Object getBackPanelDescriptor() {
		return OboPanelDescriptor.IDENTIFIER;
	}

	public void aboutToDisplayPanel() {
		getWizard().setNextFinishButtonEnabled(false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 * 
	 * Handle actions
	 */
	public void actionPerformed(ActionEvent e) {
		System.out.println("Annotation button pushed.");
		anotationPanel.gaButtonMouseClicked();
		if (anotationPanel.isFilesSelected() == true) {
			getWizard().setNextFinishButtonEnabled(true);
		}
	}

	//
	// // "Select OBO" button pressed
	// if (e.getActionCommand().equals("selectObo")) {
	//
	// // Create Filter for obo files
	// CyFileFilter oboFilter = new CyFileFilter();
	// oboFilter.addExtension("obo");
	// oboFilter.setDescription("OBO Ontology files");
	//
	// oboFile = FileUtil.getFile("Please Select OBO File...",
	// FileUtil.LOAD, new CyFileFilter[] { oboFilter });
	// if (oboFile == null) {
	// oboFlag = false;
	// } else {
	// // Display the OBO file Name in the text field
	// //panel6.setOboFileName(oboFile.getPath());
	//
	// if (oboFile.canRead()) {
	// oboFlag = true;
	// } else {
	// oboFlag = false;
	// }
	// setNextButtonAccordingToFileChooser(oboFlag, gaFlag);
	// }
	// } else if (e.getActionCommand().equals("addGA")) {
	// // "Add GA" button pressed
	// gaFile = FileUtil.getFile("Please Select Gene Association File...",
	// FileUtil.LOAD);
	// if (gaFile == null) {
	// gaFlag = false;
	// } else {
	// panel6.addGaFile(gaFile.getPath());
	// if (gaFile.canRead()) {
	// gaFlag = true;
	// } else {
	// gaFlag = false;
	// }
	// setNextButtonAccordingToFileChooser(oboFlag, gaFlag);
	// }
	// } else if (e.getActionCommand().equals("overwrite")) {
	// // "Overwrite" check box state changed.
	//			
	// panel6.setOverwriteState();
	// speciesName = panel6.getOverwiteComboBox();
	// //System.out.println("Combo box is " + panel6.getOverwiteComboBox());
	// // System.out.println("Current def. species name is: "
	// // + CytoscapeInit.getProperties().getProperty("defaultSpeciesName") );
	//
	// String oldVal =
	// CytoscapeInit.getProperties().getProperty("defaultSpeciesName");
	// String newVal = speciesName;
	//			
	// // Update properties
	// CytoscapeInit.getProperties().setProperty("defaultSpeciesName", newVal );
	// Cytoscape.firePropertyChange( Cytoscape.PREFERENCE_MODIFIED, oldVal,
	// newVal );
	// CytoscapeInit.setDefaultSpeciesName();
	//			
	// } else if (e.getActionCommand().equals("comboBoxChanged")) {
	// //System.out.println(panel6.getOverwiteComboBox());
	// speciesName = panel6.getOverwiteComboBox();
	// // System.out.println("Current def. species name is: "
	// // + CytoscapeInit.getProperties().getProperty("defaultSpeciesName") );
	// String oldVal =
	// CytoscapeInit.getProperties().getProperty("defaultSpeciesName");
	// String newVal = speciesName;
	//			
	// // Update properties
	// CytoscapeInit.getProperties().setProperty("defaultSpeciesName", newVal );
	// Cytoscape.firePropertyChange( Cytoscape.PREFERENCE_MODIFIED, oldVal,
	// newVal );
	// CytoscapeInit.setDefaultSpeciesName();
	//			
	// } else if (e.getActionCommand().equals("flip")) {
	// flip = panel6.getFlipCheckBoxStatus();
	// }
	//
	// if (oboFlag == true && gaFlag == true && oboFile != null
	// && gaFile != null) {
	//
	// try {
	// String[] gaList = panel6.getGAFileList();
	// File[] gaFiles = new File[gaList.length];
	//				
	// for(int i = 0; i<gaList.length; i++ ) {
	// gaFiles[i] = new File(gaList[i]);
	// }
	// // Create List of Gene Association files
	// createManifest(oboFile, gaFiles);
	// //System.out.println("Manifest Created.");
	// } catch (IOException e1) {
	// // TODO Auto-generated catch block
	// e1.printStackTrace();
	// }
	// }
	// }

	/*
	 * Create a temp. file called auto_generated_manifest. This manifest file is
	 * different from the old manifest. By writing proper parser in other
	 * classes, it can store arbitrary many arguments.
	 */
	// public void createManifest(File obo, File[] gA) throws IOException {
	//
	// String parentPath = null;
	//
	// if (obo.canRead() == true) {
	// parentPath = obo.getParent() + FS;
	// manifestFullPath = parentPath + AUTO_MANIFEST;
	//
	// PrintWriter wt = new PrintWriter(new BufferedWriter(new FileWriter(
	// manifestFullPath)));
	// wt.println("flip=" + flip);
	// wt.println("obo=" + obo.getName());
	// for (int i = 0; i < gA.length; i++) {
	// wt.println("gene_association=" + gA[i].getName());
	// }
	//
	// wt.close();
	// }
	// }
	public Map getAnotationFiles() {
		return anotationPanel.getGAFiles();
	}

	public boolean isFlip() {
		return anotationPanel.getFlipCheckBoxStatus();
	}

	public String getManifestName() {
		return manifestFullPath;
	}

}