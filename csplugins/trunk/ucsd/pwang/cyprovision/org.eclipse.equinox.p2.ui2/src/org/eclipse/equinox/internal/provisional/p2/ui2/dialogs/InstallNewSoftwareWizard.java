package org.eclipse.equinox.internal.provisional.p2.ui2.dialogs;

import org.eclipse.equinox.internal.provisional.p2.ui2.policy.Policy;
import org.eclipse.equinox.internal.provisional.p2.ui2.QueryableMetadataRepositoryManager;
import org.netbeans.api.wizard.WizardDisplayer;
import org.netbeans.spi.wizard.Wizard;
import org.netbeans.spi.wizard.WizardPage;
import org.netbeans.spi.wizard.WizardPage.WizardResultProducer;
import java.awt.Rectangle;
import java.util.Properties;
import java.net.URL;
import java.util.Map;
import org.netbeans.spi.wizard.WizardException;
import javax.swing.JOptionPane;

public class InstallNewSoftwareWizard {

	public InstallNewSoftwareWizard(Policy policy, String profileId, QueryableMetadataRepositoryManager manager){
        
		
		WizardPage[] pages = new WizardPage[3];
		
		AvailableSoftwarePage page1 = new AvailableSoftwarePage(policy, profileId, manager);
		InstallDetailsPage page2 = new InstallDetailsPage();
		ReviewLicensesPage page3 = new ReviewLicensesPage();
		pages[0] = page1;
		pages[1] = page2;
		pages[2] = page3;

        //Change the background image on the left side of the wizard dialog
		//ClassLoader loader = InstallNewSoftwareWizard.class.getClassLoader();  
		//URL imgURL = loader.getResource("/icons/"+ "obj/iu_obj.gif");
        //System.out.println("imgURL.toString() ="+ imgURL.toString());

		//Properties imageProp = new Properties();
        
        //imageProp.setProperty("wizard.sidebar.image", imgURL.toString());

        //System.setProperties(imageProp);

		
		//Use the utility method to compose a Wizard
        Wizard wizard = WizardPage.createWizard("Install", pages, new MyResultProducer());
        
        //And show it on screen
        Map gatherSettings = (Map) WizardDisplayer.showWizard (wizard, new Rectangle(20,20,800,600));
        
        //System.out.println("property wizard.sidebar.image ="+System.getProperty("wizard.sidebar.image"));
        
	}
	
	class MyResultProducer implements WizardResultProducer {
	    public Object finish(Map wizardData) throws WizardException {
	        //String nameForThing = (String) wizardData.get ("name");

	        return new BackgroundInstall(wizardData);	        
	    }

	    //Called when the user presses the cancel button
	    public boolean cancel(Map settings) {
	        boolean dialogShouldClose = JOptionPane.showConfirmDialog (null, 
	           "Are you sure you want to cancel the installation?!!") == JOptionPane.OK_OPTION;
	        return dialogShouldClose;
	    }
	}

}
