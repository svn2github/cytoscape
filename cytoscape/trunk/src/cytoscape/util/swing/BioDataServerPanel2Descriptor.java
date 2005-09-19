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

	private final String FS = System.getProperty("file.separator");

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

	public void actionPerformed(ActionEvent e) {

		if (e.getActionCommand().equals("Select Obo File") && oboFlag == false
				&& !(oboFlag == true && gaFlag == true)) {
			panel2.setOboFileName(null);
			panel2.createOboFileChooser();
			File oboFile = panel2.getOboFile(true);
			panel2.setOboFileName(oboFile.getPath());
			oboFlag = true;
			setNextButtonAccordingToFileChooser(oboFlag, gaFlag);
		} else if (e.getActionCommand().equals("Select Gene Association File")
				&& gaFlag == false && !(oboFlag == true && gaFlag == true)) {
			panel2.setGaFileName(null);
			panel2.createGaFileChooser();
			File gaFile = panel2.getGaFile(true);
			panel2.setGaFileName(gaFile.getPath());
			gaFlag = true;
			setNextButtonAccordingToFileChooser(oboFlag, gaFlag);
		} else if (flip != panel2.getCheckBoxStatus()) {
			// checkbox
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

	public void createManifest(File obo, File gA) throws IOException {
		File manifest;
		String parentPath = null;

		if (obo.canRead() == true && gA.canRead() == true) {
			parentPath = obo.getParent() + FS;
			manifestFullPath = parentPath + "auto_generated_manifest";

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
