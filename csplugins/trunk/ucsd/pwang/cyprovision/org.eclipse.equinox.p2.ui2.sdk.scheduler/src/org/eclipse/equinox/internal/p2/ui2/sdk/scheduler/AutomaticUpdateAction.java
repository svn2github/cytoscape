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
package org.eclipse.equinox.internal.p2.ui2.sdk.scheduler;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.equinox.internal.provisional.p2.director.ProvisioningPlan;
import org.eclipse.equinox.internal.provisional.p2.metadata.IInstallableUnit;
import org.eclipse.equinox.internal.provisional.p2.ui2.actions.UpdateAction2;

//import org.eclipse.jface.viewers.ISelectionProvider;
//import org.eclipse.jface.window.Window;
//import org.eclipse.swt.widgets.Shell;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import org.eclipse.equinox.internal.provisional.p2.director.ProfileChangeRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

//import org.eclipse.equinox.internal.provisional.p2.ui.actions.u;

//import org.eclipse.equinox.internal.provisional.p2.ui.ElementQueryDescriptor;

/**
 * Overridden so that we can use the profile change request computations,
 * but we hide the resolution from the user and optionally suppress the
 * wizard if we are resolving for reasons other than user request.
 * 
 * @since 3.5
 *
 */
//final class AutomaticUpdateAction extends UpdateAction {
final class AutomaticUpdateAction extends UpdateAction2 {

	/**
	 * 
	 */
	//final AutomaticUpdater automaticUpdater;
	//private boolean suppressWizard = false;

//	AutomaticUpdateAction(AutomaticUpdater automaticUpdater, ISelectionProvider selectionProvider, String profileId) {
//		super(new Policy(), selectionProvider, profileId, false);
//		this.automaticUpdater = automaticUpdater;
//	}

	private IInstallableUnit[] ius;
	private String profileId;
	
	public AutomaticUpdateAction(AutomaticUpdater automaticUpdater, IInstallableUnit[] ius, String profileId) {

		super(ius, profileId);
		this.profileId = profileId;
		this.ius = ius;
		
		//System.out.println("Scheduler.AutomaticUpdateAction constructor");
		
		//ProfileChangeRequest = getProfileChangeRequest(ius,  profileId);
	}
	
	public void doUpdate(IInstallableUnit[] ius, String profileId){
		super.run(ius, profileId);
	}
		
}