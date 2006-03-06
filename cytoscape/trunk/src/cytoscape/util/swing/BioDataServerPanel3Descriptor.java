
/*
  File: BioDataServerPanel3Descriptor.java 
  
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
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.nexes.wizard.WizardPanelDescriptor;

public class BioDataServerPanel3Descriptor extends WizardPanelDescriptor
		implements ActionListener {

	public static final String IDENTIFIER = "SERVER_CONNECT_PANEL";

	BioDataServerPanel3 panel3;

	String spName;

	public BioDataServerPanel3Descriptor() {

		panel3 = new BioDataServerPanel3();
		panel3.addSpComboBoxActionListener(this);
		panel3.addRadioButtonActionListener(this);
		panel3.addSetButtonActionListener(this);

		setPanelDescriptorIdentifier(IDENTIFIER);
		setPanelComponent(panel3);

		// Set default name
		spName = panel3.getSpNameFromComboBox();
	}

	public Object getNextPanelDescriptor() {
		return FINISH;
	}

	public Object getBackPanelDescriptor() {
		return BioDataServerPanel2Descriptor.IDENTIFIER;
	}

	public void aboutToDisplayPanel() {
		// System.out.println("Creating Manifest...");
		getWizard().setNextFinishButtonEnabled(true);
		getWizard().setBackButtonEnabled(true);
	}

	private void setNextButtonAccordingToSpecies() {
		String selection = (String) panel3.getRadioButtonSelected();
		if (selection.equals("Or, enter species:")) {
			spName = panel3.getSpNameFromTextBox();
		} else if (selection
				.equals("Please select the species for the data source:")) {
			spName = panel3.getSpNameFromComboBox();
		}
		// System.out.println("Finally, Sp name is : " + spName );

		if (spName.equals("")) {
			getWizard().setNextFinishButtonEnabled(false);
		} else {
			getWizard().setNextFinishButtonEnabled(true);
		}
	}

	public String getSpeciesName() {
		// System.out.println("Finally, Sp name is : " + spName );
		return spName;
	}

	public void actionPerformed(ActionEvent e) {
		// System.out.println("+Action! Command is: " + e.getActionCommand());
		// System.out.println("+Action! source is: " + e.getSource().toString()
		// );
		if (e.getActionCommand().equals("comboBoxChanged")) {
			spName = panel3.getSpNameFromComboBox();
			panel3.setCurrentSpBox(spName);

		} else if (e.getActionCommand().equals("Or, enter species:")) {
			panel3.setTextBoxState(true);
			panel3.setComboBoxState(false);
			spName = panel3.getSpNameFromTextBox();
			panel3.setCurrentSpBox(spName);
		} else if (e.getActionCommand().equals(
				"Please select the species for the data source:")) {
			panel3.setTextBoxState(false);
			panel3.setComboBoxState(true);
			spName = panel3.getSpNameFromComboBox();
			panel3.setCurrentSpBox(spName);
		} else {
			spName = panel3.getSpNameFromTextBox();
			panel3.setCurrentSpBox(spName);
		}
		// System.out.println("Sp name is : " + spName );
	}

	public void aboutToHidePanel() {
		// Can do something here, but we've chosen not not.
	}

	private class SpeciesNameAction extends AbstractAction implements
			DocumentListener {

		SpeciesNameAction() {
			super();
		}

		private void handleChange() {

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

}
