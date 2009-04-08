package org.eclipse.equinox.internal.provisional.p2.ui2.dialogs;

import org.netbeans.spi.wizard.DeferredWizardResult;
import org.netbeans.spi.wizard.ResultProgressHandle;
import org.netbeans.spi.wizard.Summary;

import java.util.Map;

public class BackgroundInstall extends DeferredWizardResult {

	Map wizardData; 
	public BackgroundInstall(Map wizardData){
		super(true);
		this.wizardData = wizardData;
	}
	
	public void start(Map wizardData, ResultProgressHandle progress) {

		int totalSteps = 3;
		int currentStep = 0;
		try {
			Thread.sleep(6000);
			progress.setProgress ("Install plugin 1", currentStep, totalSteps);

			//doSomethingExpensive (wizardData);
			
			Thread.sleep(6000);
			currentStep = 1;
			progress.setProgress ("Install plugin 2", currentStep, totalSteps);
			//doSomethingElseExpensive (wizardData);

			Thread.sleep(6000);
			currentStep =2;
			progress.setProgress ("Install plugin 3", currentStep, totalSteps);
			
			String [] items = new String[4];
			items[0] = "The following plugins have been installed:";
			items[1] = "plugin 1";			
			items[2] = "plugin 2";
			items[3] = "plugin 3";
			
			// Replace null with an object reference to have this object returned
			// from the showWizard() method
			progress.finished (Summary.create(items, null));
		}
		catch (InterruptedException ie){
			progress.failed(ie.getMessage(), false);
		}
	}
}
