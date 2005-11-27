package cytoscape.util.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import com.nexes.wizard.WizardPanelDescriptor;

public class BioDataServerPanel4Descriptor 
	extends WizardPanelDescriptor implements ActionListener{

	public static final String IDENTIFIER = "SELECT_OLDFILE_PANEL";

	BioDataServerPanel4 panel4;
	
	String manifestFileName;

	private int finalState;
	

	public BioDataServerPanel4Descriptor() {
		finalState = 0;
		manifestFileName = null;
		panel4 = new BioDataServerPanel4();
		panel4.addSelectButtonActionListener(this);
		
		setPanelDescriptorIdentifier(IDENTIFIER);
		setPanelComponent(panel4);
	}

	public Object getNextPanelDescriptor() {
		return FINISH;
	}

	
	// back leads to the 1st panel.
	public Object getBackPanelDescriptor() {
		return BioDataServerPanel1Descriptor.IDENTIFIER;
	}

	public void aboutToDisplayPanel() {
		getWizard().setNextFinishButtonEnabled(false);
		getWizard().setBackButtonEnabled(true);

	}

	
	private void setNextButtonAccordingToFileChooser( boolean ma ) {
		if ( ma == false )
			getWizard().setNextFinishButtonEnabled(false);
		else
			getWizard().setNextFinishButtonEnabled(true);
	}
	
	public String getManifestFileName() {
		return manifestFileName;
	}
	
	public int getFinalState() {
		// if null, it means new file format are created.
		if( manifestFileName == null ) {
			finalState = 1; // old file loaded
		} 
		return finalState;
	}

	
	public void actionPerformed(ActionEvent e) {
		//System.out.println("+Action! Command is: " + e.getActionCommand());
		//System.out.println("+Action! source is: " + e.getSource().toString() );
		
		panel4.createManifestFileChooser();
		File manifestFile = panel4.getManifestFile( true );
		panel4.setManifestFileName(manifestFile.getPath());
		manifestFileName = panel4.getManifestFileName();
		setNextButtonAccordingToFileChooser( true );
	}
	
	public void aboutToHidePanel() {
		// Can do something here, but we've chosen not not.
	}
}
