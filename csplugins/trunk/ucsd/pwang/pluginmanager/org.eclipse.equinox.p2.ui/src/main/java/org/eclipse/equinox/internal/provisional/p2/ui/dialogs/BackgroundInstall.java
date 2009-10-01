package org.eclipse.equinox.internal.provisional.p2.ui.dialogs;

import org.netbeans.spi.wizard.DeferredWizardResult;
import org.netbeans.spi.wizard.ResultProgressHandle;
import org.netbeans.spi.wizard.Summary;

import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.equinox.internal.provisional.p2.director.ProvisioningPlan;
import org.eclipse.equinox.internal.provisional.p2.ui.ProvisioningOperationRunner;
import org.eclipse.equinox.internal.provisional.p2.ui.operations.ProfileModificationOperation;
import org.eclipse.equinox.internal.provisional.p2.ui.ResolutionResult;
//import org.eclipse.ui.statushandlers.StatusManager;
import org.eclipse.equinox.internal.provisional.p2.ui.operations.PlannerResolutionOperation;

public class BackgroundInstall extends DeferredWizardResult {

	Map wizardData; 
	String profileId;
	public BackgroundInstall(Map wizardData){
		super(true);
		this.wizardData = wizardData;
	}
	
	public void start(Map wizardData, ResultProgressHandle progress) {

		//System.out.println("wizardData.size() = "+wizardData.size());
	
		profileId = (String) wizardData.get("profileId");
		
		ResolutionResult resolutionResult = (ResolutionResult) wizardData.get("resolutionResult");
		PlannerResolutionOperation resolvedOperation = (PlannerResolutionOperation) wizardData.get("resolvedOperation");

		System.out.println("profileId = "+ profileId);
		System.out.println("resolutionResult.getSummaryStatus().getSeverity() = "+ resolutionResult.getSummaryStatus().getSeverity());
		//System.out.println("IStatus.ERROR = "+IStatus.ERROR); // 4
		System.out.println("resolvedOperation.getProvisioningPlan().toString() = "+ resolvedOperation.getProvisioningPlan().toString());
		
		if (resolutionResult != null && resolutionResult.getSummaryStatus().getSeverity() != IStatus.ERROR) {
			ProfileModificationOperation op = new ProfileModificationOperation("Install", profileId, resolvedOperation.getProvisioningPlan());
			//=createProfileModificationOperation(resolvedOperation.getProvisioningPlan());
			//ProvisioningOperationRunner.schedule(op, StatusManager.SHOW | StatusManager.LOG);
		}

		
		/*
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
		*/
	}
	
	//private ProfileModificationOperation createProfileModificationOperation(ProvisioningPlan plan) {
	//	return new ProfileModificationOperation("Install", profileId, plan);
	//}

}
