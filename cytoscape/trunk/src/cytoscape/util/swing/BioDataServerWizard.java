package cytoscape.util.swing;

import java.awt.Color;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

import com.nexes.wizard.Wizard;
import com.nexes.wizard.WizardPanelDescriptor;

import cytoscape.Cytoscape;

/*
 * Bio Data Server Wizard utility.
 */
public class BioDataServerWizard {

	// The wizard object
	Wizard wizard;

	// 1st panel to select BDS source type.
	WizardPanelDescriptor descriptor1;

	//WizardPanelDescriptor descriptor2;

	//WizardPanelDescriptor descriptor3;

	// Panel to select old-style manifest file
	WizardPanelDescriptor descriptor4;

	//WizardPanelDescriptor descriptor5;
	
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
		wizard = new Wizard();
		//wizard.getDialog().getParent().setSize(1000, 1000);
		
		wizard.getDialog().setTitle("Gene Ontology Wizard");

		descriptor1 = new BioDataServerPanel1Descriptor();
		wizard.registerWizardPanel(BioDataServerPanel1Descriptor.IDENTIFIER,
				descriptor1);
		
//		descriptor2 = new BioDataServerPanel2Descriptor();
//		wizard.registerWizardPanel(BioDataServerPanel2Descriptor.IDENTIFIER,
//				descriptor2);

//		descriptor3 = new BioDataServerPanel3Descriptor();
//		wizard.registerWizardPanel(BioDataServerPanel3Descriptor.IDENTIFIER,
//				descriptor3);

		descriptor4 = new BioDataServerPanel4Descriptor();
		wizard.registerWizardPanel(BioDataServerPanel4Descriptor.IDENTIFIER,
				descriptor4);
		
//		descriptor5 = new BioDataServerPanel5Descriptor();
//		wizard.registerWizardPanel(BioDataServerPanel5Descriptor.IDENTIFIER,
//				descriptor5);
		
		descriptor6 = new BioDataServerPanel6Descriptor();
		wizard.registerWizardPanel(BioDataServerPanel6Descriptor.IDENTIFIER,
				descriptor6);
		
		
		// Set the start panel
		wizard.setCurrentPanel(BioDataServerPanel1Descriptor.IDENTIFIER);

	}

	// Show the
	public int show() {
		int ret = wizard.showModalDialog();

		// Getting the species name

		if (ret == Wizard.FINISH_RETURN_CODE) {
			flip = ((BioDataServerPanel6Descriptor) descriptor6).isFlip();

			manifest = ((BioDataServerPanel6Descriptor) descriptor6)
					.getManifestName();
			oldManifest = ((BioDataServerPanel4Descriptor) descriptor4)
					.getManifestFileName();
//			species = ((BioDataServerPanel3Descriptor) descriptor3)
//					.getSpeciesName();
			finalState = ((BioDataServerPanel4Descriptor) descriptor4)
					.getFinalState();
			
			if (finalState == 1) {
				File mfTest = new File(manifest);
				String mParent = null;
				if (mfTest.canRead()) {
					mParent = mfTest.getParent();
					//appendSpecies(mParent);
				}
				Cytoscape.loadBioDataServer(manifest);
			} else {
				Cytoscape.loadBioDataServer(oldManifest);
			}
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
