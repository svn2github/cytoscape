package org.cytoscape.cyprovision.appstub;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
//import org.eclipse.swt.widgets.Display;
//import org.eclipse.ui.IWorkbench;
//import org.eclipse.ui.PlatformUI;
//import org.osgi.framework.Bundle;

/**
 * This class controls all aspects of the application's execution
 */
public class Application implements IApplication {
	/**
	 * This bundle is just a stub application to run Cytoscapee on Equinox OSGi framework. 
	 * Because Cytoscape application is not Equinox specific, it does not implement IApplication. 
	 * So it can not be started from Eclipse launcher directly. This stub application just serves 
	 * as an entry point to start the Equinox OSGI application. After the application is launched, 
	 * Cytoscape application bundle can then be started.
	 */

	/* (non-Javadoc)
	 * @see org.eclipse.equinox.app.IApplication#start(org.eclipse.equinox.app.IApplicationContext)
	 */
	public Object start(IApplicationContext context) {
		System.out.println("myequinoxappstub.start()...");
		while (true) {

			try {
				Thread.sleep(10000); //sleep 10 seconds...
			}
			catch(InterruptedException ie){
				break;
			}
		}

		/*
		Display display = PlatformUI.createDisplay();
		try {
			int returnCode = PlatformUI.createAndRunWorkbench(display, new ApplicationWorkbenchAdvisor());
			if (returnCode == PlatformUI.RETURN_RESTART) {
				return IApplication.EXIT_RESTART;
			}
			return IApplication.EXIT_OK;
		} finally {
			display.dispose();
		}
		*/
		
		return IApplication.EXIT_OK;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.equinox.app.IApplication#stop()
	 */
	public void stop() {
		//System.out.println("myequinoxappstub.stop()...");

		/*
		final IWorkbench workbench = PlatformUI.getWorkbench();
		if (workbench == null)
			return;
		final Display display = workbench.getDisplay();
		display.syncExec(new Runnable() {
			public void run() {
				if (!display.isDisposed())
					workbench.close();
			}
		});
		*/
	}
}
