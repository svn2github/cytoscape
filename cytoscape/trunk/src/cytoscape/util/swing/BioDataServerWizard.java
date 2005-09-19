package cytoscape.util.swing;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

import com.nexes.wizard.Wizard;
import com.nexes.wizard.WizardPanelDescriptor;

import cytoscape.Cytoscape;


public class BioDataServerWizard {
	
	// The wizard object
	Wizard wizard;
	WizardPanelDescriptor descriptor1;
	WizardPanelDescriptor descriptor2;
	WizardPanelDescriptor descriptor3;
	WizardPanelDescriptor descriptor4;
	
	// Parameters obtained from the wizard session
	private boolean flip;
	private String manifest;
	private String inputType;
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
		wizard.getDialog().setTitle("Bio Data Server Wizard");
		
		descriptor1 = new BioDataServerPanel1Descriptor();
        wizard.registerWizardPanel(BioDataServerPanel1Descriptor.IDENTIFIER, descriptor1);
        
        descriptor2 = new BioDataServerPanel2Descriptor();
        wizard.registerWizardPanel(BioDataServerPanel2Descriptor.IDENTIFIER, descriptor2);

        descriptor3 = new BioDataServerPanel3Descriptor();
        wizard.registerWizardPanel(BioDataServerPanel3Descriptor.IDENTIFIER, descriptor3);
	
        descriptor4 = new BioDataServerPanel4Descriptor();
        wizard.registerWizardPanel(BioDataServerPanel4Descriptor.IDENTIFIER, descriptor4);

        wizard.setCurrentPanel(BioDataServerPanel1Descriptor.IDENTIFIER);
	
	}
	
	
	// Show the 
	public int show() {
		int ret = wizard.showModalDialog();

		// Getting the species name
		
		if( ret == Wizard.FINISH_RETURN_CODE ) {
			flip = ((BioDataServerPanel2Descriptor) descriptor2).isFlip();
			
			manifest = ((BioDataServerPanel2Descriptor) descriptor2).getManifestName();
			oldManifest = ((BioDataServerPanel4Descriptor) descriptor4).getManifestFileName();
			species = ((BioDataServerPanel3Descriptor)descriptor3).getSpeciesName();
			finalState = ((BioDataServerPanel4Descriptor) descriptor4).getFinalState();
			
			System.out.println( "Species set to: " + species );
			File mfTest = new File(manifest);
			String mParent = null;
			if(mfTest.canRead()) {
				mParent = mfTest.getParent();
				//ystem.out.println( "Parent is " + mParent );
				appendSpecies( mParent );
			}
			if( finalState == 1 ) {
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
	public void appendSpecies( String parentPath ) {
		boolean append = true;
		String autoManifest = parentPath + FS + "auto_generated_manifest";
		try {
			FileWriter fw = new FileWriter(autoManifest, append );
			BufferedWriter br = new BufferedWriter( fw );
			PrintWriter pw = new PrintWriter( br );
			pw.println("species=" + species );
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

