package cytoscape.util.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.swing.AbstractAction;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.nexes.wizard.WizardPanelDescriptor;

import cytoscape.util.FileUtil;

public class BioDataServerPanel2Descriptor extends WizardPanelDescriptor
		implements ActionListener {

	public static final String IDENTIFIER = "CONNECTOR_CHOOSE_PANEL";

	BioDataServerPanel2 panel2;

	boolean oboFlag, gaFlag;

	boolean flip;

	String manifestFullPath;

	// Define file sepalator, which is system dependent.
	private final String FS = System.getProperty("file.separator");

	public final String OBO_BUTTON = "Obo";

	public final String GA_BUTTON = "Gene Association";

	public final String AUTO_MANIFEST = "auto_generated_manifest";

	public BioDataServerPanel2Descriptor() {

		oboFlag = false;
		gaFlag = false;
		flip = false;
		manifestFullPath = null;

		panel2 = new BioDataServerPanel2();
		panel2.addButtonActionListener(this);
		panel2.addCheckBoxActionListener(this);

		panel2.addOboTextFieldActionListener(new oboFileNameAction());
		panel2.addGaTextFieldActionListener(new oboFileNameAction());

		panel2.addOboTextFieldDocumentListener(new oboFileNameAction());
		panel2.addGaTextFieldDocumentListener(new gaFileNameAction());

		setPanelDescriptorIdentifier(IDENTIFIER);
		setPanelComponent(panel2);

	}

	public Object getNextPanelDescriptor() {
		return BioDataServerPanel3Descriptor.IDENTIFIER;
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

		File oboFile = null;
		File gaFile = null;

		if ( e.getActionCommand().equals(OBO_BUTTON) ) {

			//panel2.setOboFileName(null);
			oboFile = FileUtil.getFile("Select OBO File...", FileUtil.LOAD);
			if (oboFile == null) {
				oboFlag = false;
			} else {
				panel2.setOboFileName(oboFile.getPath());
				if( oboFile.canRead() ) {
					oboFlag = true;
				} else {
					oboFlag = false;
				}
				setNextButtonAccordingToFileChooser(oboFlag, gaFlag);
			}
		} else if (e.getActionCommand().equals(GA_BUTTON) ) {
			//panel2.setGaFileName(null);
			gaFile = FileUtil.getFile("Select Gene Association File...",
					FileUtil.LOAD);
			if (gaFile == null) {
				gaFlag = false;
			} else {
				panel2.setGaFileName(gaFile.getPath());
				if( gaFile.canRead() ) {
					//System.out.println("GA flag is true.");
					gaFlag = true;
				} else {
					gaFlag = false;
				}
				setNextButtonAccordingToFileChooser(oboFlag, gaFlag);
			}
		} else if (flip != panel2.getCheckBoxStatus()) {
			// For "flip" checkbox status check.
			flip = panel2.getCheckBoxStatus();
		}

		if (oboFlag == true && gaFlag == true && oboFile != null && gaFile != null ) {
			
			try {
				if (oboFile.canRead() && gaFile.canRead()) {
					createManifest(oboFile, gaFile);
					System.out.println("Manifest Created.");
				} else {
					System.out.println("Cannot read obo or ga.");
				}
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
	public void createManifest(File obo, File gA) throws IOException {

		String parentPath = null;

		if (obo.canRead() == true && gA.canRead() == true) {
			parentPath = obo.getParent() + FS;
			manifestFullPath = parentPath + AUTO_MANIFEST;

			PrintWriter wt = new PrintWriter(new BufferedWriter(new FileWriter(
					manifestFullPath)));
			wt.println("flip=" + flip);
			wt.println("obo=" + obo.getName());
			wt.println("gene_association=" + gA.getName());
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
			String value = panel2.getOboTextField();
			
			if (value != null) {
				File oboFile = new File(value);
				if (oboFile.canRead()) {
					//System.out.println("Valid Obo file");
					oboFlag = true;
				} else {
					oboFlag = false;
				}
				
				if (oboFlag == true && gaFlag == true) {
					try {
						File gaFile = new File( panel2.getGaTextField() );
						
						if (oboFile.canRead() && gaFile.canRead()) {
							createManifest(oboFile, gaFile);
							System.out.println("Manifest Created.");
						} else {
							System.out.println("Cannot read obo or ga.");
						}
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
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

	private class gaFileNameAction extends AbstractAction implements
			DocumentListener {

		gaFileNameAction() {
			super();
		}

		private void handleChange() {
			String value = panel2.getGaTextField();

			if (value != null) {
				File gaFile = new File(value);
				if (gaFile.canRead()) {
					System.out.println("Valid GA file");
					gaFlag = true;
				} else {
					gaFlag = false;
				}
				
				if (oboFlag == true && gaFlag == true) {
					try {
						File oboFile = new File( panel2.getOboTextField() );
						
						if (oboFile.canRead() && gaFile.canRead()) {
							createManifest(oboFile, gaFile);
							System.out.println("Manifest Created.");
						} else {
							System.out.println("Cannot read obo or ga.");
						}
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
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
} // end of class BioDataServerPanel2Descriptor
