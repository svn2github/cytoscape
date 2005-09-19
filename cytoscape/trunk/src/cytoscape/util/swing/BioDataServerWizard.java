package cytoscape.util.swing;

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
			if( finalState == 1 ) {
				Cytoscape.loadBioDataServer(manifest);
			} else {
				Cytoscape.loadBioDataServer(oldManifest);
			}
		}
		return ret;
	}
	
	public boolean getFlip() {
		return flip;
	}
	
	public String getManifestFileName() {
		return manifest;
	}
	
}

