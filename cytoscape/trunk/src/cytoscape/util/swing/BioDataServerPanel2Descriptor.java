package cytoscape.util.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import com.nexes.wizard.WizardPanelDescriptor;

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
	 *  (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 * Handle acctions.
	 * 
	 * Commands are defined in the Panel2 class.
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals(OBO_BUTTON)
				&& !(oboFlag == true && gaFlag == true)) {
			panel2.setOboFileName(null);
			panel2.createOboFileChooser();
			File oboFile = panel2.getOboFile(true);
			
			if(oboFile == null ){
				System.out.println("Obo is null.");
			} else {
				panel2.setOboFileName(oboFile.getPath());
				oboFlag = true;
				setNextButtonAccordingToFileChooser(oboFlag, gaFlag);
			}
		} else if (e.getActionCommand().equals(GA_BUTTON)
				&& !(oboFlag == true && gaFlag == true)) {
			panel2.setGaFileName(null);
			panel2.createGaFileChooser();
			File gaFile = panel2.getGaFile(true);
			panel2.setGaFileName(gaFile.getPath());
			gaFlag = true;
			setNextButtonAccordingToFileChooser(oboFlag, gaFlag);
		} else if (flip != panel2.getCheckBoxStatus()) {
			// For "flip" checkbox status check.
			//System.out.println("*******checked");
			flip = panel2.getCheckBoxStatus();
		}

		if (oboFlag == true && gaFlag == true) {
			File obo = panel2.getOboFile(false);
			File gA = panel2.getGaFile(false);

			try {
				createManifest(obo, gA);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

	/*
	 * Create a temp. file called auto_generated_manifest.
	 * This manifest file is different from the old manifest.
	 * By writing proper parser in other classes, it can store arbitrary many
	 * arguments.
	 */
	public void createManifest(File obo, File gA) throws IOException {
		
		String parentPath = null;

		if (obo.canRead() == true && gA.canRead() == true) {
			parentPath = obo.getParent() + FS;
			manifestFullPath = parentPath + AUTO_MANIFEST;

			PrintWriter wt = new PrintWriter(new BufferedWriter(new FileWriter(
					manifestFullPath)));
			wt.println("flip=" + flip );
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

	//

}
