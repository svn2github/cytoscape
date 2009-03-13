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

//import org.eclipse.equinox.internal.provisional.p2.ui.IProvHelpContextIds;
import javax.swing.JDialog;
import org.eclipse.equinox.internal.provisional.p2.ui2.QueryableMetadataRepositoryManager;
import org.eclipse.equinox.internal.provisional.p2.ui2.policy.Policy;
import org.eclipse.equinox.internal.provisional.p2.ui2.dialogs.InstallNewSoftwarePanel;;
/**
 * InstallNewSoftwareHandler invokes the install wizard
 * 
 * @since 3.5
 */
public class InstallNewSoftwareHandler extends PreloadingRepositoryHandler {

	/**
	 * The constructor.
	 */
	public InstallNewSoftwareHandler() {
		super();
	}

	protected void doExecute(String profileId, QueryableMetadataRepositoryManager manager) {
		//InstallWizard wizard = new InstallWizard(Policy.getDefault(), profileId, null, null, manager);
		//WizardDialog dialog = new WizardDialog(getShell(), wizard);
		//dialog.create();
		//PlatformUI.getWorkbench().getHelpSystem().setHelp(dialog.getShell(), IProvHelpContextIds.INSTALL_WIZARD);

		//dialog.open();

		JDialog dlg = new JDialog();
		dlg.setTitle("Install");
		dlg.add(new InstallNewSoftwarePanel(dlg, Policy.getDefault(), profileId, manager));

		dlg.setVisible(true);
		dlg.setSize(600, 500);

	}
}
