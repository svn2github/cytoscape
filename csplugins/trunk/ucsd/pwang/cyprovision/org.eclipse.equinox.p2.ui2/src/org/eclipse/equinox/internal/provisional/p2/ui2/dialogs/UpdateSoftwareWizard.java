package org.eclipse.equinox.internal.provisional.p2.ui2.dialogs;

import java.awt.Rectangle;
import java.util.Map;
import java.util.Vector;

import javax.swing.JOptionPane;

//import org.eclipse.equinox.internal.provisional.p2.ui2.dialogs.InstallNewSoftwareWizard.MyResultProducer;
import org.eclipse.equinox.internal.provisional.p2.ui2.model.InstalledIUElement;
import org.netbeans.api.wizard.WizardDisplayer;
import org.netbeans.spi.wizard.Wizard;
import org.netbeans.spi.wizard.WizardException;
import org.netbeans.spi.wizard.WizardPage;
import org.netbeans.spi.wizard.WizardPage.WizardResultProducer;

public class UpdateSoftwareWizard {

	public UpdateSoftwareWizard(InstallationInformationDialog parent, String profileId, Vector<InstalledIUElement> ius){
		parent.dispose();
		WizardPage[] pages = new WizardPage[2];

		AvailableUpdatesPage page1 = new AvailableUpdatesPage(profileId, ius);
		UpdateDetailsPage page2 = new UpdateDetailsPage(profileId, ius);
		pages[0] = page1;
		pages[1] = page2;
		
		//Use the utility method to compose a Wizard
		Wizard wizard = WizardPage.createWizard("Available Updates", pages, new MyResultProducer());

		//And show it on screen
		Map gatherSettings = (Map) WizardDisplayer.showWizard (wizard, new Rectangle(20,20,800,600));

	}

	class MyResultProducer implements WizardResultProducer {
		public Object finish(Map wizardData) throws WizardException {
			//String nameForThing = (String) wizardData.get ("name");

			return new BackgroundUpdate(wizardData);	        
		}

		//Called when the user presses the cancel button
		public boolean cancel(Map settings) {
			//boolean dialogShouldClose = JOptionPane.showConfirmDialog (null, 
			//   "Are you sure you want to cancel the installation?!!") == JOptionPane.OK_OPTION;
			return true; //dialogShouldClose;
		}
	}

}
