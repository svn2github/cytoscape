package org.eclipse.equinox.internal.provisional.p2.ui.dialogs;

import java.awt.Rectangle;
import java.util.Map;
import java.util.Vector;

import javax.swing.JOptionPane;

//import org.eclipse.equinox.internal.provisional.p2.ui.dialogs.InstallNewSoftwareWizard.MyResultProducer;
import org.eclipse.equinox.internal.provisional.p2.ui.model.InstalledIUElement;
import org.netbeans.api.wizard.WizardDisplayer;
import org.netbeans.spi.wizard.Wizard;
import org.netbeans.spi.wizard.WizardException;
import org.netbeans.spi.wizard.WizardPage;
import org.netbeans.spi.wizard.WizardPage.WizardResultProducer;

public class UninstallSoftwareWizard {

	public UninstallSoftwareWizard(InstallationInformationDialog parent, String profileId, Vector ius){
		parent.dispose();
		WizardPage[] pages = new WizardPage[1];

		UninstallDetailsPage page1 = new UninstallDetailsPage(profileId, ius);
		pages[0] = page1;

		//Use the utility method to compose a Wizard
		Wizard wizard = WizardPage.createWizard("Uninstall", pages, new MyResultProducer());

		//And show it on screen
		Map gatherSettings = (Map) WizardDisplayer.showWizard (wizard, new Rectangle(20,20,800,600));

	}

	class MyResultProducer implements WizardResultProducer {
		public Object finish(Map wizardData) throws WizardException {
			//String nameForThing = (String) wizardData.get ("name");

			return new BackgroundUninstall(wizardData);	        
		}

		//Called when the user presses the cancel button
		public boolean cancel(Map settings) {
			//boolean dialogShouldClose = JOptionPane.showConfirmDialog (null, 
			//   "Are you sure you want to cancel the installation?!!") == JOptionPane.OK_OPTION;
			return true; //dialogShouldClose;
		}
	}

}
