package org.cytoscape.product;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

/**
 * This class controls all aspects of the application's execution
 */
public class Application implements IApplication {
	/**
	 * This bundle is just a stub application to run Cytoscapee on Equinox OSGi framework. 
	 * Because Cytoscape application is not Equinox specific, it does not implement IApplication. 
	 * So it can not be started from Eclipse launcher directly. This stub application just serves 
	 * as an entry point to start the Equinox OSGi application and run Cytoscape application.
	 */

	/* (non-Javadoc)
	 * @see org.eclipse.equinox.app.IApplication#start(org.eclipse.equinox.app.IApplicationContext)
	 */
	public Object start(IApplicationContext context) {
		System.out.println("org.cytoscape.product.start()...");
		java.text.DateFormat dateFormat = new java.text.SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        java.util.Date date = new java.util.Date();
        System.out.println(" at : " + dateFormat.format(date));

		// Start Cytoscape.application bundle here ??? 
		//????
		//????
        String[] args = (String[]) context.getArguments().get("application.argvs");
		
		while (true) {
			try {
				Thread.sleep(10000); //sleep 10 seconds...
			}
			catch(InterruptedException ie){
				break;
			}
		}

		return IApplication.EXIT_OK;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.equinox.app.IApplication#stop()
	 */
	public void stop() {
		System.out.println("org.cytoscape.product.stop()...");
	}
}
