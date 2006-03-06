
/*
  File: BioDataServerPanel5Descriptor.java 
  
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
import java.io.File;
import java.io.IOException;

import com.nexes.wizard.WizardPanelDescriptor;

import cytoscape.util.FileUtil;

public class BioDataServerPanel5Descriptor extends WizardPanelDescriptor { 

	public static final String IDENTIFIER = "INTRODUCTION_PANEL";
	    BioDataServerPanel5 panel5;
	    
	    public BioDataServerPanel5Descriptor() {
	        super(IDENTIFIER, new BioDataServerPanel5());
	        
	    }

	    public Object getNextPanelDescriptor() {
	    		String fileType = null;
	    		panel5 = new BioDataServerPanel5();
	    		
	    		panel5 = (BioDataServerPanel5)super.getPanelComponent();
	    	
	    		//fileType = panel5.getFileFormatRadioButtonSelected();
	    	
	    		
	    			return BioDataServerPanel2Descriptor.IDENTIFIER;
	    		}

	    public Object getBackPanelDescriptor() {
	        return null;
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

			System.out.println("Called!!!");
			
//			if ( e.getActionCommand().equals(OBO_BUTTON) ) {
//
//				//panel2.setOboFileName(null);
//				oboFile = FileUtil.getFile("Select OBO File...", FileUtil.LOAD);
//				if (oboFile == null) {
//					oboFlag = false;
//				} else {
//					panel2.setOboFileName(oboFile.getPath());
//					if( oboFile.canRead() ) {
//						oboFlag = true;
//					} else {
//						oboFlag = false;
//					}
//					setNextButtonAccordingToFileChooser(oboFlag, gaFlag);
//				}
//			} else if (e.getActionCommand().equals(GA_BUTTON) ) {
//				//panel2.setGaFileName(null);
//				gaFile = FileUtil.getFile("Select Gene Association File...",
//						FileUtil.LOAD);
//				if (gaFile == null) {
//					gaFlag = false;
//				} else {
//					panel2.setGaFileName(gaFile.getPath());
//					if( gaFile.canRead() ) {
//						//System.out.println("GA flag is true.");
//						gaFlag = true;
//					} else {
//						gaFlag = false;
//					}
//					setNextButtonAccordingToFileChooser(oboFlag, gaFlag);
//				}
//			} else if (flip != panel2.getCheckBoxStatus()) {
//				// For "flip" checkbox status check.
//				flip = panel2.getCheckBoxStatus();
//			}
//
//			if (oboFlag == true && gaFlag == true && oboFile != null && gaFile != null ) {
//				
//				try {
//					if (oboFile.canRead() && gaFile.canRead()) {
//						createManifest(oboFile, gaFile);
//						System.out.println("Manifest Created.");
//					} else {
//						System.out.println("Cannot read obo or ga.");
//					}
//				} catch (IOException e1) {
//					// TODO Auto-generated catch block
//					e1.printStackTrace();
//				}
//			}
		}

	    
	    
}

