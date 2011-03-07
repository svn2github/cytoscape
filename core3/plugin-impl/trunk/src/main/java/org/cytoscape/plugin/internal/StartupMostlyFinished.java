package org.cytoscape.plugin.internal;

import org.cytoscape.application.swing.CySwingApplication; 

/**
 * A simple class whose instantiation indicates that startup is
 * largely (but not necessarily 100 percent) complete for Cytoscape.
 * This class should only exist once in the application and the reason it
 * exists here is that the plugin-impl has dependencies on nearly every
 * aspect of the system, so once those dependencies are fulfilled, we 
 * we can assume that the application is ready for use, and thus the
 * splash screen can be closed.
 */
class StartupMostlyFinished {
	public StartupMostlyFinished(CySwingApplication app) {
		// Make the main desktop visible, which
		// will also close the splash screen.
		app.getJFrame().setVisible(true);
		app.getJFrame().toFront();
	}
}
