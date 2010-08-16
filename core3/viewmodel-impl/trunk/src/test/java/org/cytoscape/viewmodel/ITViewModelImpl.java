package org.cytoscape.viewmodel;

import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.springframework.osgi.test.AbstractConfigurableBundleCreatorTests;
import org.springframework.osgi.util.OsgiStringUtils;

public class ITViewModelImpl extends AbstractConfigurableBundleCreatorTests {

	public void testOsgiPlatformStarts() throws Exception {

		// Make sure bundle context exists.
		assertNotNull(bundleContext);

		System.out.println(bundleContext
				.getProperty(Constants.FRAMEWORK_VENDOR));
		System.out.println(bundleContext
				.getProperty(Constants.FRAMEWORK_VERSION));
		System.out.println(bundleContext
				.getProperty(Constants.FRAMEWORK_EXECUTIONENVIRONMENT));

		final Bundle[] bundles = bundleContext.getBundles();
		System.out
				.println("###### Integration Test OSGi System Bundles ######");

		for (int i = 0; i < bundles.length; i++) {
			Bundle bundle = bundles[i];
			System.out.println(OsgiStringUtils.nullSafeName(bundle));
			ServiceReference[] services = bundle.getRegisteredServices();
			if (services != null)
				for (ServiceReference ref : services)
					System.out.println("\tService = " + ref);
		}

	}

	public String getRootPath() {
		return "file:./target/test-classes";
	}

	// protected String[] getConfigLocations() {
	// return new String[] {
	// //"file:./target/classes/META-INF/spring/bundle-context.xml",
	// // "file:./target/classes/META-INF/spring/bundle-context-osgi.xml",
	// "file:./target/test-classes/META-INF/spring/bundle-context-test.xml"
	//
	// };
	// }

	protected String[] getTestBundlesNames() {
		return new String[] { 
				"org.cytoscape, event-api, 1.0-SNAPSHOT",
				"org.cytoscape, model-api, 1.0-SNAPSHOT",
				"org.cytoscape, service-util, 1.0-SNAPSHOT",
				"org.cytoscape, viewmodel-api, 1.0-SNAPSHOT"
				};
	}

}
