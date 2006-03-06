
/*
  File: BioDataServerPanel4Descriptor.java 
  
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
