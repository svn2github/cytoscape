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

