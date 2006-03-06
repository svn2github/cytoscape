
/*
  File: BioDataServerPanel1Descriptor.java 
  
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

import com.nexes.wizard.WizardPanelDescriptor;

public class BioDataServerPanel1Descriptor extends WizardPanelDescriptor { 

	public static final String IDENTIFIER = "INTRODUCTION_PANEL";
	    BioDataServerPanel1 panel1;
	    
	    public BioDataServerPanel1Descriptor() {
	        super(IDENTIFIER, new BioDataServerPanel1());
	    }

	    public Object getNextPanelDescriptor() {
	    		String fileType = null;
	    		panel1 = new BioDataServerPanel1();
	    		
	    		panel1.reshape(10,10,10,10);
	    		
	    		panel1 = (BioDataServerPanel1)super.getPanelComponent();
	    	
	    		fileType = panel1.getFileFormatRadioButtonSelected();
	    	
	    		if( fileType.equals("oboAndGa") ) {
	    			return BioDataServerPanel6Descriptor.IDENTIFIER;
	    		} else {
	    			return BioDataServerPanel4Descriptor.IDENTIFIER;
	    		}
	    	}

	    public Object getBackPanelDescriptor() {
	        return null;
	    }

}

