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
package org.eclipse.equinox.internal.p2.ui.sdk;

//import org.eclipse.equinox.internal.provisional.p2.ui.IProvHelpContextIds;
import javax.swing.JDialog;

import org.cytoscape.session.CyNetworkManager;
import org.eclipse.equinox.internal.provisional.p2.ui.QueryableMetadataRepositoryManager;
import org.eclipse.equinox.internal.provisional.p2.ui.policy.Policy;
import org.eclipse.equinox.internal.provisional.p2.ui.dialogs.InstallNewSoftwarePanel;
import org.eclipse.equinox.internal.provisional.p2.ui.dialogs.InstallNewSoftwareWizard;

import cytoscape.view.CySwingApplication;
/**
 * InstallNewSoftwareHandler invokes the install wizard
 * 
 * @since 3.5
 */
public class InstallNewSoftwareHandler extends PreloadingRepositoryHandler {

	private CySwingApplication desktop;
	private CyNetworkManager netmgr;
	
	/**
	 * The constructor.
	 */
	public InstallNewSoftwareHandler(CySwingApplication desktop,CyNetworkManager netmgr) {
		super();
		this.desktop = desktop;
		this.netmgr = netmgr;
	}

	protected void doExecute(String profileId, QueryableMetadataRepositoryManager manager) {
		//InstallWizard wizard = new InstallWizard(Policy.getDefault(), profileId, null, null, manager);
		//WizardDialog dialog = new WizardDialog(getShell(), wizard);
		//dialog.create();
		//PlatformUI.getWorkbench().getHelpSystem().setHelp(dialog.getShell(), IProvHelpContextIds.INSTALL_WIZARD);

		//dialog.open();

		//JDialog dlg = new JDialog();
		//dlg.setTitle("Install");
		//dlg.add(new InstallNewSoftwarePanel(dlg, Policy.getDefault(), profileId, manager));

		//dlg.setVisible(true);
		//dlg.setSize(700, 600);

		InstallNewSoftwareWizard wizard = new InstallNewSoftwareWizard(Policy.getDefault(), profileId, manager);

		
	}
}
