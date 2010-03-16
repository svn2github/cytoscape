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

