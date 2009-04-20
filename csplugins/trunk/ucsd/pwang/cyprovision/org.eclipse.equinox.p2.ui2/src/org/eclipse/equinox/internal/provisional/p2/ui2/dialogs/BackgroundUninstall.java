package org.eclipse.equinox.internal.provisional.p2.ui2.dialogs;

import org.netbeans.spi.wizard.DeferredWizardResult;
import org.netbeans.spi.wizard.ResultProgressHandle;
import org.netbeans.spi.wizard.Summary;

import java.util.Map;
import java.util.Vector;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.equinox.internal.provisional.p2.director.ProvisioningPlan;
import org.eclipse.equinox.internal.provisional.p2.ui2.ProvisioningOperationRunner;
import org.eclipse.equinox.internal.provisional.p2.ui2.operations.ProfileModificationOperation;
import org.eclipse.equinox.internal.provisional.p2.ui2.ResolutionResult;
import org.eclipse.equinox.internal.provisional.p2.ui2.operations.PlannerResolutionOperation;

public class BackgroundUninstall extends DeferredWizardResult {

	Map wizardData; 
	String profileId;
	public BackgroundUninstall(Map wizardData){
		super(true);
		this.wizardData = wizardData;
	}
	
	public void start(Map wizardData, ResultProgressHandle progress) {

		//System.out.println("wizardData.size() = "+wizardData.size());
	
		Vector ius = (Vector) wizardData.get("ius");

		
		System.out.println("BackgroundUninstall: ius.size() = " + ius.size());
		
	}
	
	//private ProfileModificationOperation createProfileModificationOperation(ProvisioningPlan plan) {
	//	return new ProfileModificationOperation("Install", profileId, plan);
	//}

}
