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
        Wizard wizard = WizardPage.createWizard("Install", pages);
        
        //And show it on screen
        WizardDisplayer.showWizard (wizard, new Rectangle(20,20,800,600));

	}
}
