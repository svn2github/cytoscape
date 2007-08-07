
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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Properties;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import javax.swing.AbstractAction;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.nexes.wizard.WizardPanelDescriptor;

import cytoscape.Cytoscape;
import cytoscape.CytoscapeInit;
import cytoscape.util.CyFileFilter;
import cytoscape.util.FileUtil;

/*
 * This panel is for new Gene Ontology data formats.
 * Ask user a OBO file and multiple Gene Association Files.
 */
public class BioDataServerPanel6Descriptor extends WizardPanelDescriptor
		implements ActionListener {

	public static final String IDENTIFIER = "CONNECTOR_CHOOSE_PANEL";

	BioDataServerPanel6 panel6;

	boolean oboFlag, gaFlag;

	boolean flip;

	String manifestFullPath;

	String speciesName = null;

	File oboFile = null;

	File gaFile = null;

	// Define file sepalator, which is system dependent.
	private final String FS = System.getProperty("file.separator");

	public final String OBO_BUTTON = "Obo";

	public final String GA_BUTTON = "Gene Association";

	public final String AUTO_MANIFEST = "auto_generated_manifest";
	
	public BioDataServerPanel6Descriptor() {

		oboFlag = false;
		gaFlag = false;
		flip = false;
		manifestFullPath = null;		

		panel6 = new BioDataServerPanel6();
		panel6.addButtonActionListener(this);
		panel6.addOverwriteCheckBoxActionListener(this);
		panel6.addOverwriteComboBoxActionListener(this);

		panel6.addFlipCheckBoxActionListener(this);

		panel6.addOboTextFieldActionListener(new oboFileNameAction());

		panel6.addOboTextFieldDocumentListener(new oboFileNameAction());

		setPanelDescriptorIdentifier(IDENTIFIER);
		setPanelComponent(panel6);

	}

	public Object getNextPanelDescriptor() {
		return FINISH;
		//return BioDataServerPanel3Descriptor.IDENTIFIER;
	}

	public Object getBackPanelDescriptor() {
		return BioDataServerPanel1Descriptor.IDENTIFIER;
	}

	public void aboutToDisplayPanel() {
		setNextButtonAccordingToFileChooser(false, false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 * 
	 * Handle actions
	 */
	public void actionPerformed(ActionEvent e) {

		// "Select OBO" button pressed
		if (e.getActionCommand().equals("selectObo")) {

			// Create Filter for obo files
			CyFileFilter oboFilter = new CyFileFilter();
			oboFilter.addExtension("obo");
			oboFilter.setDescription("OBO Ontology files");

			oboFile = FileUtil.getFile("Please Select OBO File...",
					FileUtil.LOAD, new CyFileFilter[] { oboFilter });
			if (oboFile == null) {
				oboFlag = false;
			} else {
				// Display the OBO file Name in the text field
				panel6.setOboFileName(oboFile.getPath());

				if (oboFile.canRead()) {
					oboFlag = true;
				} else {
					oboFlag = false;
				}
				setNextButtonAccordingToFileChooser(oboFlag, gaFlag);
			}
		} else if (e.getActionCommand().equals("addGA")) {
			// "Add GA" button pressed
			gaFile = FileUtil.getFile("Please Select Gene Association File...",
					FileUtil.LOAD);
			if (gaFile == null) {
				gaFlag = false;
			} else {
				panel6.addGaFile(gaFile.getPath());
				if (gaFile.canRead()) {
					gaFlag = true;
				} else {
					gaFlag = false;
				}
				setNextButtonAccordingToFileChooser(oboFlag, gaFlag);
			}
		} else if (e.getActionCommand().equals("overwrite")) {
			// "Overwrite" check box state changed.
			
			panel6.setOverwriteState();
			speciesName = panel6.getOverwiteComboBox();
			//System.out.println("Combo box is " + panel6.getOverwiteComboBox());
//			System.out.println("Current def. species name is: " 
//					+ CytoscapeInit.getProperties().getProperty("defaultSpeciesName") );

			String oldVal = CytoscapeInit.getProperties().getProperty("defaultSpeciesName");
			String newVal = speciesName;
			
			// Update properties
			CytoscapeInit.getProperties().setProperty("defaultSpeciesName", newVal );
			Cytoscape.firePropertyChange( Cytoscape.PREFERENCE_MODIFIED, oldVal, newVal );
			CytoscapeInit.setDefaultSpeciesName();		
			
		} else if (e.getActionCommand().equals("comboBoxChanged")) {
			//System.out.println(panel6.getOverwiteComboBox());
			speciesName = panel6.getOverwiteComboBox();
//			System.out.println("Current def. species name is: " 
//					+ CytoscapeInit.getProperties().getProperty("defaultSpeciesName") );
			String oldVal = CytoscapeInit.getProperties().getProperty("defaultSpeciesName");
			String newVal = speciesName;
			
			// Update properties
			CytoscapeInit.getProperties().setProperty("defaultSpeciesName", newVal );
			Cytoscape.firePropertyChange( Cytoscape.PREFERENCE_MODIFIED, oldVal, newVal );
			CytoscapeInit.setDefaultSpeciesName();
			
		} else if (e.getActionCommand().equals("flip")) {
			flip = panel6.getFlipCheckBoxStatus();
		}

		if (oboFlag == true && gaFlag == true && oboFile != null
				&& gaFile != null) {

			try {
				String[] gaList = panel6.getGAFileList();
				File[] gaFiles = new File[gaList.length];
				
				for(int i = 0; i<gaList.length; i++ ) {
					gaFiles[i] = new File(gaList[i]);
				}
				// Create List of Gene Association files
				createManifest(oboFile, gaFiles);
				//System.out.println("Manifest Created.");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

	/*
	 * Create a temp. file called auto_generated_manifest. This manifest file is
	 * different from the old manifest. By writing proper parser in other
	 * classes, it can store arbitrary many arguments.
	 */
	public void createManifest(File obo, File[] gA) throws IOException {

		String parentPath = null;

		if (obo.canRead() == true) {
			parentPath = obo.getParent() + FS;
			manifestFullPath = parentPath + AUTO_MANIFEST;

			PrintWriter wt = new PrintWriter(new BufferedWriter(new FileWriter(
					manifestFullPath)));
			wt.println("flip=" + flip);
			wt.println("obo=" + obo.getName());
			for( int i = 0; i<gA.length; i++ ) {
				wt.println( "gene_association=" + gA[i].getName() );
			}
			
			wt.close();
		}
	}

	public boolean isFlip() {
		return flip;
	}

	public String getManifestName() {
		return manifestFullPath;
	}

	private void setNextButtonAccordingToFileChooser(boolean obo, boolean ga) {
		if (obo == false || ga == false)
			getWizard().setNextFinishButtonEnabled(false);
		else
			getWizard().setNextFinishButtonEnabled(true);
	}

	private class oboFileNameAction extends AbstractAction implements
			DocumentListener {

		oboFileNameAction() {
			super();
		}

		// any change that is made is handled the same - parameter is updated
		private void handleChange() {
			String value = panel6.getOboTextField();

			if (value != null) {
				File oboFile = new File(value);
				if (oboFile.canRead()) {
					//System.out.println("Valid Obo file");
					oboFlag = true;
				} else {
					oboFlag = false;
				}

				// if (oboFlag == true && gaFlag == true) {
				// try {
				// File gaFile = new File( panel6.getGaTextField() );
				//						
				// if (oboFile.canRead() && gaFile.canRead()) {
				// createManifest(oboFile, gaFile);
				// System.out.println("Manifest Created.");
				// } else {
				// System.out.println("Cannot read obo or ga.");
				// }
				// } catch (IOException e1) {
				// // TODO Auto-generated catch block
				// e1.printStackTrace();
				// }
				// }
				setNextButtonAccordingToFileChooser(oboFlag, gaFlag);
			}
		}

		public void actionPerformed(ActionEvent e) {
			handleChange();
		}

		public void changedUpdate(DocumentEvent e) {
			handleChange();
		}

		public void insertUpdate(DocumentEvent e) {
			handleChange();
		}

		public void removeUpdate(DocumentEvent e) {
			handleChange();
		}
	}

//	private class gaFileNameAction extends AbstractAction implements
//			DocumentListener {
//
//		gaFileNameAction() {
//			super();
//		}
//
//		private void handleChange() {
//			String value = panel6.getOboTextField();
//
//			if (value != null) {
//				File gaFile = new File(value);
//				if (gaFile.canRead()) {
//					System.out.println("Valid GA file");
//					gaFlag = true;
//				} else {
//					gaFlag = false;
//				}
//
//				if (oboFlag == true && gaFlag == true) {
//					try {
//						File oboFile = new File(panel6.getOboTextField());
//
//						if (oboFile.canRead() && gaFile.canRead()) {
//							//createManifest(oboFile, gaFile);
//							System.out.println("Manifest Created.");
//						} else {
//							System.out.println("Cannot read obo or ga.");
//						}
//					} catch (IOException e1) {
//						// TODO Auto-generated catch block
//						e1.printStackTrace();
//					}
//				}
//				setNextButtonAccordingToFileChooser(oboFlag, gaFlag);
//			}
//		}
//
//		public void actionPerformed(ActionEvent e) {
//			handleChange();
//		}
//
//		public void changedUpdate(DocumentEvent e) {
//			handleChange();
//		}
//
//		public void insertUpdate(DocumentEvent e) {
//			handleChange();
//		}
//
//		public void removeUpdate(DocumentEvent e) {
//			handleChange();
//		}
//	}
}
