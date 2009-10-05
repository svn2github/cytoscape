/*
 Copyright (c) 2006, 2007, The Cytoscape Consortium (www.cytoscape.org)

 The Cytoscape Consortium is:
 - Institute for Systems Biology
 - University of California San Diego
 - Memorial Sloan-Kettering Cancer Center
 - Institut Pasteur
 - Agilent Technologies

 This library is free software; you can redistribute it and/or modify it
 under the terms of the GNU Lesser General Public License as published
 by the Free Software Foundation; either version 2.1 of the License, or
 any later version.

 This library is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 documentation provided hereunder is on an "as is" basis, and the
 Institute for Systems Biology and the Whitehead Institute
 have no obligations to provide maintenance, support,
 updates, enhancements or modifications.  In no event shall the
 Institute for Systems Biology and the Whitehead Institute
 be liable to any party for direct, indirect, special,
 incidental or consequential damages, including lost profits, arising
 out of the use of this software and its documentation, even if the
 Institute for Systems Biology and the Whitehead Institute
 have been advised of the possibility of such damage.  See
 the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation,
 Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
*/
package org.cytoscape.ide.eclipse.setup.perspectives;

import org.cytoscape.ide.eclipse.setup.MavenSetup;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.console.IConsoleConstants;


/**
 * Cytoscape IDE default perspective.
 */
public class CyPerspective implements IPerspectiveFactory {
	private IPageLayout factory;

	/**
	 * Creates a new CyPerspective object.
	 */
	public CyPerspective() {
		super();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param factory DOCUMENT ME!
	 */
	public void createInitialLayout(IPageLayout factory) {
		this.factory = factory;
		
		MavenSetup.setRepository();
		
		addViews();
//		addActionSets();
//		addNewWizardShortcuts();
//		addPerspectiveShortcuts();
//		addViewShortcuts();
	}

	private void addViews() {
		// Creates the overall folder layout.
		// Note that each new Folder uses a percentage of the remaining
		// EditorArea.
		
		
		// Bottom Area
		IFolderLayout bottom = factory.createFolder("bottomRight", IPageLayout.BOTTOM, 0.75f,
		                                            factory.getEditorArea());
		bottom.addView(IPageLayout.ID_PROBLEM_VIEW);
		bottom.addView("org.eclipse.team.ui.GenericHistoryView");
		bottom.addView(IConsoleConstants.ID_CONSOLE_VIEW);

		
		// Left Panels
		IFolderLayout topLeft = factory.createFolder("topLeft", IPageLayout.LEFT, 0.25f,
		                                             factory.getEditorArea());
		topLeft.addView("org.eclipse.jdt.ui.PackageExplorer");
		topLeft.addView("org.tigris.subversion.subclipse.ui.repository.RepositoriesView");
		topLeft.addView("org.maven.ide.eclipse.views.MavenIndexesView");
		topLeft.addView("org.eclipse.jdt.junit.ResultView");

		IFolderLayout topRight = factory.createFolder("topRight", IPageLayout.RIGHT, 0.25f,
		                                              factory.getEditorArea());
		topRight.addView("org.eclipse.ui.views.ContentOutline");

		factory.addFastView("org.eclipse.team.ccvs.ui.RepositoriesView", 0.50f);
		factory.addFastView("org.eclipse.team.sync.views.SynchronizeView", 0.50f);
	}

	private void addActionSets() {
		factory.addActionSet("org.eclipse.debug.ui.launchActionSet"); //NON-NLS-
		                                                              // 1

		factory.addActionSet("org.eclipse.debug.ui.debugActionSet"); //NON-NLS-1
		factory.addActionSet("org.eclipse.debug.ui.profileActionSet"); //NON-NLS
		                                                               // -1

		factory.addActionSet("org.eclipse.jdt.debug.ui.JDTDebugActionSet"); // NON
		                                                                    // -
		                                                                    // NLS
		                                                                    // -
		                                                                    // 1

		factory.addActionSet("org.eclipse.jdt.junit.JUnitActionSet"); //NON-NLS-
		                                                              // 1

		factory.addActionSet("org.eclipse.team.ui.actionSet"); // NON-NLS-1
		factory.addActionSet("org.eclipse.team.cvs.ui.CVSActionSet"); //NON-NLS-
		                                                              // 1

		factory.addActionSet("org.eclipse.ant.ui.actionSet.presentation"); //NON-
		                                                                   // NLS
		                                                                   // -
		                                                                   // 1

		factory.addActionSet(JavaUI.ID_ACTION_SET);
		factory.addActionSet(JavaUI.ID_ELEMENT_CREATION_ACTION_SET);
		factory.addActionSet(IPageLayout.ID_NAVIGATE_ACTION_SET); // NON-NLS-1
	}

	private void addPerspectiveShortcuts() {
		factory.addPerspectiveShortcut("org.eclipse.team.ui.TeamSynchronizingPerspective"); // NON
		                                                                                    // -
		                                                                                    // NLS
		                                                                                    // -
		                                                                                    // 1

		factory.addPerspectiveShortcut("org.eclipse.team.cvs.ui.cvsPerspective"); // NON
		                                                                          // -
		                                                                          // NLS
		                                                                          // -
		                                                                          // 1

		factory.addPerspectiveShortcut("org.eclipse.ui.resourcePerspective"); // NON
		                                                                      // -
		                                                                      // NLS
		                                                                      // -
		                                                                      // 1
	}

	private void addNewWizardShortcuts() {
		factory.addNewWizardShortcut("org.eclipse.team.cvs.ui.newProjectCheckout"); // NON
		                                                                            // -
		                                                                            // NLS
		                                                                            // -
		                                                                            // 1

		factory.addNewWizardShortcut("org.eclipse.ui.wizards.new.folder"); //NON-
		                                                                   // NLS
		                                                                   // -
		                                                                   // 1

		factory.addNewWizardShortcut("org.eclipse.ui.wizards.new.file"); //NON-NLS
		                                                                 // -1
	}

	private void addViewShortcuts() {
		factory.addShowViewShortcut("org.eclipse.ant.ui.views.AntView"); // NON-
		                                                                 // NLS
		                                                                 // -
		                                                                 // 1

		factory.addShowViewShortcut("org.eclipse.team.ccvs.ui.AnnotateView"); // NON
		                                                                      // -
		                                                                      // NLS
		                                                                      // -
		                                                                      // 1

		factory.addShowViewShortcut("org.eclipse.pde.ui.DependenciesView"); // NON
		                                                                    // -
		                                                                    // NLS
		                                                                    // -
		                                                                    // 1

		factory.addShowViewShortcut("org.eclipse.jdt.junit.ResultView"); // NON-
		                                                                 // NLS
		                                                                 // -
		                                                                 // 1

		factory.addShowViewShortcut("org.eclipse.team.ui.GenericHistoryView"); // NON
		                                                                       // -
		                                                                       // NLS
		                                                                       // -
		                                                                       // 1

		factory.addShowViewShortcut(IConsoleConstants.ID_CONSOLE_VIEW);
		factory.addShowViewShortcut(JavaUI.ID_PACKAGES);
		factory.addShowViewShortcut(IPageLayout.ID_RES_NAV);
		factory.addShowViewShortcut(IPageLayout.ID_PROBLEM_VIEW);
		factory.addShowViewShortcut(IPageLayout.ID_OUTLINE);
	}
}
