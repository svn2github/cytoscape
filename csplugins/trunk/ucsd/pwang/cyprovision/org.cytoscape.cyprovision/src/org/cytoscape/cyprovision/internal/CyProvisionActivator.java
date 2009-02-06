package org.cytoscape.cyprovision.internal;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.packageadmin.PackageAdmin;

/**
 * The activator class controls the plug-in life cycle
 */
public class CyProvisionActivator extends Plugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.cytoscape.cyprovision";

	// The shared instance
	private static CyProvisionActivator plugin;
	private ServiceRegistration registrationAdapter;
	private String provision_service_name = "org.cytoscape.cyprovision.CyP2Adapter";

	private static PackageAdmin packageAdmin = null;
	private static ServiceReference packageAdminRef = null;

	/**
	 * The constructor
	 */
	public CyProvisionActivator() {
		//System.out.println("CyProvisionActivator constructor ");		
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		
		// this bundle should be started after Cytoscape initialized!!!
		
		//System.out.println("CyProvision.start(): register a service "+ provision_service_name);
		
		CyP2AdapterImpl adapter = new CyP2AdapterImpl();
		registrationAdapter = context.registerService(provision_service_name, adapter, null);
	
		// AutomaticUpdatePlugin bundle (org.eclipse.equinox.p2.ui.sdk.scheduler) should be started
		// after the Cytoscape initialized event is received

		/*
		if (adapter.getCyProperties().getProperty("p2.AUTO_UPDATE_ENABLED")== "true"){
			// for test only, we just wait a few seconds in a thread
			Runnable newThread = new Runnable(){
				public void run() {
					try {
						Thread.sleep(1000);
						getBundle("org.eclipse.equinox.p2.ui.sdk.scheduler").start(Bundle.START_TRANSIENT); //$NON-NLS-1$				
						System.out.println("CyProvisionActivator: sleep 1000ms and start bundle scheduler");
					
					}
					catch(InterruptedException ie) {
						System.out.println("CyProvisionActivator: XXX");

					}
					catch(Exception be) {
						System.out.println("CyProvisionActivator: failed to start org.eclipse.equinox.p2.ui.sdk.scheduler");
						//be.printStackTrace();

						//bundleException
					}
				}
			};
			newThread.run();			
		}
		*/
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
		registrationAdapter.unregister();

	}

	
	public static Bundle getBundle(String symbolicName) {
		if (packageAdmin == null)
			return null;
		Bundle[] bundles = packageAdmin.getBundles(symbolicName, null);
		if (bundles == null)
			return null;
		// Return the first bundle that is not installed or uninstalled
		for (int i = 0; i < bundles.length; i++) {
			if ((bundles[i].getState() & (Bundle.INSTALLED | Bundle.UNINSTALLED)) == 0) {
				return bundles[i];
			}
		}
		return null;
	}


	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static CyProvisionActivator getDefault() {
		return plugin;
	}

}
