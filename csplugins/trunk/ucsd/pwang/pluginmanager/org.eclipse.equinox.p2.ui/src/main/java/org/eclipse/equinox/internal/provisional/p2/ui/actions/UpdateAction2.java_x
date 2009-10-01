/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.equinox.internal.provisional.p2.ui2.actions;

import java.util.*;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.jobs.*;
import org.eclipse.equinox.internal.p2.ui2.PlanAnalyzer;
import org.eclipse.equinox.internal.p2.ui2.ProvUIMessages;
import org.eclipse.equinox.internal.p2.ui2.model.AvailableUpdateElement;
import org.eclipse.equinox.internal.p2.ui2.model.IUElementListRoot;
import org.eclipse.equinox.internal.provisional.p2.director.ProfileChangeRequest;
import org.eclipse.equinox.internal.provisional.p2.director.ProvisioningPlan;
import org.eclipse.equinox.internal.provisional.p2.metadata.IInstallableUnit;
import org.eclipse.equinox.internal.provisional.p2.ui2.*;
import org.eclipse.equinox.internal.provisional.p2.ui2.model.Updates;
import org.eclipse.equinox.internal.provisional.p2.ui2.operations.PlannerResolutionOperation;
import org.eclipse.equinox.internal.provisional.p2.ui2.operations.ProfileModificationOperation;
import org.eclipse.equinox.internal.provisional.p2.ui2.policy.Policy;
import org.eclipse.equinox.internal.provisional.p2.ui2.policy.QueryProvider;
import org.eclipse.ui.statushandlers.StatusManager;

public class UpdateAction2 { //extends ExistingIUInProfileAction {
	private HashMap latestReplacements;
	private Policy policy = new Policy();
	IUElementListRoot root; // root that will be used to seed the wizard

	private IInstallableUnit[] ius;
	String profileId;

	public UpdateAction2(IInstallableUnit[] ius, String targetProfileId) {
		this.ius = ius;
		this.profileId = targetProfileId;
	}

	public IInstallableUnit[] getLatestIUs() {
		ProfileChangeRequest req = getProfileChangeRequest(ius, profileId);
		IInstallableUnit[] latestIUs = req.getAddedInstallableUnits();
		return latestIUs;
	}

	//protected ProfileChangeRequest getProfileChangeRequest(IInstallableUnit[] ius, String targetProfileId, MultiStatus status, IProgressMonitor monitor) {
	protected ProfileChangeRequest getProfileChangeRequest(IInstallableUnit[] ius, String targetProfileId) {

		// Here we create a profile change request by finding the latest version available for any replacement.
		ArrayList toBeUpdated = new ArrayList();
		latestReplacements = new HashMap();
		ArrayList allReplacements = new ArrayList();
		//SubMonitor sub = SubMonitor.convert(monitor, ProvUIMessages.ProfileChangeRequestBuildingRequest, ius.length);
		for (int i = 0; i < ius.length; i++) {
			ElementQueryDescriptor descriptor = getQueryProvider().getQueryDescriptor(new Updates(targetProfileId, new IInstallableUnit[] {ius[i]}));
			Iterator iter = descriptor.queryable.query(descriptor.query, descriptor.collector, null).iterator();
			if (iter.hasNext())
				toBeUpdated.add(ius[i]);
			ArrayList currentReplacements = new ArrayList();
			root = new IUElementListRoot();
			while (iter.hasNext()) {
				IInstallableUnit iu = (IInstallableUnit) ProvUI.getAdapter(iter.next(), IInstallableUnit.class);
				if (iu != null) {
					AvailableUpdateElement element = new AvailableUpdateElement(root, iu, ius[i], targetProfileId, true);
					currentReplacements.add(element);
					allReplacements.add(element);
				}
			}
			root.setChildren(allReplacements.toArray());
			for (int j = 0; j < currentReplacements.size(); j++) {
				AvailableUpdateElement replacementElement = (AvailableUpdateElement) currentReplacements.get(j);
				AvailableUpdateElement latestElement = (AvailableUpdateElement) latestReplacements.get(replacementElement.getIU().getId());
				IInstallableUnit latestIU = latestElement == null ? null : latestElement.getIU();
				if (latestIU == null || replacementElement.getIU().getVersion().compareTo(latestIU.getVersion()) > 0)
					latestReplacements.put(replacementElement.getIU().getId(), replacementElement);
			}
			//sub.worked(1);
		}
		if (toBeUpdated.size() <= 0) {
			//status.add(PlanAnalyzer.getStatus(IStatusCodes.NOTHING_TO_UPDATE, null));
			//sub.done();
			return null;
		}

		ProfileChangeRequest request = ProfileChangeRequest.createByProfileId(targetProfileId);
		Iterator iter = toBeUpdated.iterator();
		while (iter.hasNext())
			request.removeInstallableUnits(new IInstallableUnit[] {(IInstallableUnit) iter.next()});
		iter = latestReplacements.values().iterator();
		while (iter.hasNext())
			request.addInstallableUnits(new IInstallableUnit[] {((AvailableUpdateElement) iter.next()).getIU()});
		//sub.done();
		return request;
	}

	protected QueryProvider getQueryProvider() {
		return policy.getQueryProvider();
	}

	PlannerResolutionOperation operation;

	public void run(final IInstallableUnit[] ius, final String id) {
		// Get a profile change request.  Supply a multi-status so that information
		// about the request can be provided along the way.
		//final MultiStatus additionalStatus = getProfileChangeAlteredStatus();
		final ProfileChangeRequest[] request = new ProfileChangeRequest[1];
		// TODO even getting a profile change request can be expensive
		// when updating, because we are looking for updates.  For now, most
		// clients work around this by preloading repositories in a job.
		// Consider something different here.  We'll pass a fake progress monitor
		// into the profile change request method so that later we can do
		// something better here.
		//BusyIndicator.showWhile(getShell().getDisplay(), new Runnable() {
		//	public void run() {
		//request[0] = getProfileChangeRequest(ius, id, additionalStatus, new NullProgressMonitor());
		//}
		//});
		final MultiStatus additionalStatus = getProfileChangeAlteredStatus();
		request[0] = getProfileChangeRequest(ius, id);

		//System.out.println("Banana:" + ius[0].getVersion());

		// If we couldn't build a request, then report an error and bail.
		// Hopefully the provider of the request gave an explanation in the status.
		if (request[0] == null) {
			//IStatus failureStatus;
			//if (additionalStatus.getChildren().length > 0) {
			//if (additionalStatus.getChildren().length == 1)
			//failureStatus = additionalStatus.getChildren()[0];
			//else {
			//MultiStatus nullRequestStatus = new MultiStatus(ProvUIActivator.PLUGIN_ID, IStatusCodes.UNEXPECTED_NOTHING_TO_DO, additionalStatus.getChildren(), ProvUIMessages.ProfileModificationAction_NoChangeRequestProvided, null);
			//nullRequestStatus.addAll(additionalStatus);
			//failureStatus = nullRequestStatus;
			//}
			//} else {
			// No explanation for failure was provided.  It shouldn't happen, but...
			//failureStatus = new Status(IStatus.ERROR, ProvUIActivator.PLUGIN_ID, ProvUIMessages.ProfileModificationAction_NoExplanationProvided);
			//}
			//ProvUI.reportStatus(failureStatus, StatusManager.SHOW);
			//runCanceled();
			return;
		}
		// We have a profile change request, let's get a plan for it.  This could take awhile.
		//final PlannerResolutionOperation operation = new PlannerResolutionOperation(ProvUIMessages.ProfileModificationAction_ResolutionOperationLabel, ius, id, request[0], additionalStatus, isResolveUserVisible());

		//final PlannerResolutionOperation operation = new PlannerResolutionOperation(ProvUIMessages.ProfileModificationAction_ResolutionOperationLabel, getLatestIUs(), id, request[0], additionalStatus, true);

		operation = new PlannerResolutionOperation(ProvUIMessages.ProfileModificationAction_ResolutionOperationLabel, ius, id, request[0], additionalStatus, true);

		// Since we are resolving asynchronously, our job is done.  Setting this allows
		// callers to decide to close the launching window.
		// See https://bugs.eclipse.org/bugs/show_bug.cgi?id=236495
		//result = Window.OK;
		Job job = ProvisioningOperationRunner.schedule(operation, StatusManager.SHOW);
		job.addJobChangeListener(new JobChangeAdapter() {
			public void done(IJobChangeEvent event) {
				// Do we have a plan??
				final ProvisioningPlan plan = operation.getProvisioningPlan();
				if (plan != null) {
					//if (PlatformUI.isWorkbenchRunning()) {

					System.out.println("UpdateAction2: ProvisioningPlan is ready");
					//PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
					//Runnable theJob = new Runnable() {
					//	public void run() {
					//		System.out.println("Orange");

					//if (validatePlan(plan))
					performAction(ius, getProfileId(true), operation);
					//	userChosenProfileId = null;
					//	}
					//};

					//theJob.run();
				}
			}
		});

		// Run job in a different thread
		/*
		Runnable theJob = new Runnable() {
		public void run() {
			System.out.println("UpdateAction2: run update job now!");
			final ProvisioningPlan plan = operation.getProvisioningPlan();

			System.out.println("\tplan = " + plan);

			//if (plan != null) {

			// validatePlan(plan)
			//performAction(ius, getProfileId(true), operation);
			try {
				System.out.println("\tin operation.execute() thread -- step A");

				operation.execute(null);

				System.out.println("operation.getProvisioningPlan().toString() =" + operation.getProvisioningPlan().toString());

				ProfileModificationOperation op = new ProfileModificationOperation("software update", profileId, operation.getProvisioningPlan());

				op.execute(null);
				org.eclipse.core.runtime.jobs.Job theJob = ProvisioningOperationRunner.schedule(op, StatusManager.SHOW | StatusManager.LOG);

				theJob.schedule(0);

				System.out.println("\tin operation.execute() thread -- step B");

			} catch (ProvisionException pe) {
				System.out.println("UpdateAction2: caught ProvisionException");
			}
			//}
		}
		};
		theJob.run();
		*/
	}

	protected int performAction(IInstallableUnit[] iusX, String targetProfileId, PlannerResolutionOperation resolution) {

		for (int i = 0; i < iusX.length; i++) {
			System.out.println("Pear: " + iusX[i].getVersion());
		}

		ProfileModificationOperation op = new ProfileModificationOperation("getOperationLabel()", profileId, operation.getProvisioningPlan());
		try {

			System.out.println("UpdateAction2: Peach A");

			op.execute(null);
			System.out.println("UpdateAction2: Peach B");

		} catch (Exception e) {
			System.out.println("UpdateAction2: Provision error");
		}
		//ProvisioningOperationRunner.schedule(op, StatusManager.SHOW | StatusManager.LOG);
		//ProvisioningOperationRunner.run(op, StatusManager.SHOW);
		return 0;
	}

	protected String getProfileId(boolean chooseProfile) {
		if (profileId != null)
			return profileId;
		//if (userChosenProfileId != null)
		//	return userChosenProfileId;
		//if (chooseProfile && getProfileChooser() != null) {
		//	userChosenProfileId = getProfileChooser().getProfileId(getShell());
		//	return userChosenProfileId;
		//}
		return null;
	}

	protected MultiStatus getProfileChangeAlteredStatus() {
		return PlanAnalyzer.getProfileChangeAlteredStatus();
	}

}
