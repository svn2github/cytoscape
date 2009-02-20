/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.equinox.internal.p2.ui2.sdk;

import javax.swing.JOptionPane;
import org.eclipse.core.runtime.*;
import org.eclipse.core.runtime.jobs.*;
import org.eclipse.equinox.internal.provisional.p2.core.ProvisionException;
import org.eclipse.equinox.internal.provisional.p2.ui2.QueryableMetadataRepositoryManager;
import org.eclipse.equinox.internal.provisional.p2.ui2.policy.Policy;

/**
 * PreloadingRepositoryHandler provides background loading of
 * repositories before executing the provisioning handler.
 * 
 * @since 3.5
 */
abstract class PreloadingRepositoryHandler { //extends AbstractHandler {

	Job loadJob = null;

	/**
	 * The constructor.
	 */
	public PreloadingRepositoryHandler() {
		// constructor
	}

	/**
	 * Execute the command.
	 */
	//public Object execute(ExecutionEvent event) {
	public Object execute() {
		try {
			final String profileId = ProvSDKUIActivator.getSelfProfileId();

			Runnable newThread = new Runnable() {
				public void run() {
					doExecuteAndLoad(profileId, preloadRepositories());
				}
			};
			newThread.run();
			//BusyIndicator.showWhile(getShell().getDisplay(), new Runnable() {
			//	public void run() {
			//		doExecuteAndLoad(profileId, preloadRepositories());
			//	}
			//});

		} catch (ProvisionException e) {
			JOptionPane.showInternalMessageDialog(null, "Error", "Can not launch UI", JOptionPane.ERROR_MESSAGE);
			//MessageDialog.openInformation(null, ProvSDKMessages.Handler_SDKUpdateUIMessageTitle, ProvSDKMessages.Handler_CannotLaunchUI);
		}
		return null;
	}

	void doExecuteAndLoad(final String profileId, boolean preloadRepositories) {

		final QueryableMetadataRepositoryManager queryableManager = new QueryableMetadataRepositoryManager(Policy.getDefault(), false);
		if (preloadRepositories) {
			if (loadJob == null) {

				loadJob = new Job(ProvSDKMessages.InstallNewSoftwareHandler_LoadRepositoryJobLabel) {

					protected IStatus run(IProgressMonitor monitor) {
						queryableManager.loadAll(monitor);
						return Status.OK_STATUS;
					}

				};
				loadJob.addJobChangeListener(new JobChangeAdapter() {
					public void done(IJobChangeEvent event) {
						loadJob = null;

						Runnable newThread = new Runnable() {
							public void run() {
								doExecute(profileId, queryableManager);
							}
						};
						newThread.run();

						//if (PlatformUI.isWorkbenchRunning())
						//	if (event.getResult().isOK()) {
						//		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
						//			public void run() {
						//				doExecute(profileId, queryableManager);
						//			}
						//		});
						//	}
					}
				});
				loadJob.setUser(true);
				loadJob.schedule();
			}
			return;
		}
		doExecute(profileId, queryableManager);
	}

	protected abstract void doExecute(String profileId, QueryableMetadataRepositoryManager manager);

	protected boolean preloadRepositories() {
		return true;
	}

	/**
	 * Return a shell appropriate for parenting dialogs of this handler.
	 * @return a Shell
	 */
	//protected Shell getShell() {
	//	return null;//ProvUI.getDefaultParentShell();
	//}
}
