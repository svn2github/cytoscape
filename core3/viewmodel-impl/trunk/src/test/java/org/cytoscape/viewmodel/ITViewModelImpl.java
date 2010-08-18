package org.cytoscape.viewmodel;

import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.RootVisualLexicon;
import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.springframework.osgi.test.AbstractConfigurableBundleCreatorTests;
import org.springframework.osgi.util.OsgiStringUtils;

/**
 * Integration test for viewmodel-impl bundle.
 * 
 * @author kono
 * 
 */
public class ITViewModelImpl extends AbstractConfigurableBundleCreatorTests {

	// Exported services (FROM viewmodel-impl bundle)
	private RootVisualLexicon rootVisualLexicon;
	private CyNetworkViewFactory cyNetworkViewFactory;

	// Inject those services to this test (by setter injection)
	public void setRootVisualLexicon(RootVisualLexicon rootVisualLexicon) {
		this.rootVisualLexicon = rootVisualLexicon;
	}

	public void setCyNetworkViewFactory(
			CyNetworkViewFactory cyNetworkViewFactory) {
		this.cyNetworkViewFactory = cyNetworkViewFactory;
	}

	public void testOsgiPlatformStarts() throws Exception {
		System.out.println("###### Starting Integration Test ######");
		// Make sure bundle context exists.
		assertNotNull(bundleContext);

		System.out.println(bundleContext
				.getProperty(Constants.FRAMEWORK_VENDOR));
		System.out.println(bundleContext
				.getProperty(Constants.FRAMEWORK_VERSION));
		System.out.println(bundleContext
				.getProperty(Constants.FRAMEWORK_EXECUTIONENVIRONMENT));

		final Bundle[] bundles = bundleContext.getBundles();

		Bundle viewModelImplBundle = null;
		for (int i = 0; i < bundles.length; i++) {
			final Bundle bundle = bundles[i];
			final String bundleName = OsgiStringUtils.nullSafeName(bundle);
			System.out.println(bundleName);
			if (bundleName.equals("org.cytoscape.viewmodel-impl"))
				viewModelImplBundle = bundle;
			ServiceReference[] services = bundle.getRegisteredServices();
			if (services != null)
				for (ServiceReference ref : services)
					System.out.println("\tService = " + ref);
			System.out.println("\n");
		}

		// Make sure viewmodel-impl bundle is running
		assertNotNull(viewModelImplBundle);

		System.out.println("###### ViewModel bundle registered. ######");
	}

	public void testServiceReferencesExist() {
		System.out.println("###### Starting Service Reference Tests ######");
		final ServiceReference rootVisualLexiconServiceReference = bundleContext
				.getServiceReference(RootVisualLexicon.class.getName());
		assertNotNull(rootVisualLexiconServiceReference);
		Object beanName = rootVisualLexiconServiceReference
				.getProperty("org.springframework.osgi.bean.name");
		assertEquals("rootVisualLexicon", beanName);

		final ServiceReference cyNetworkViewFactoryServiceReference = bundleContext
				.getServiceReference(CyNetworkViewFactory.class.getName());
		assertNotNull(cyNetworkViewFactoryServiceReference);
		beanName = cyNetworkViewFactoryServiceReference
				.getProperty("org.springframework.osgi.bean.name");
		assertEquals("cyNetworkViewFactory", beanName);

		System.out.println("###### SR test done! ######");
	}

	/**
	 * Do very basic tests for injected services. Complete tests for all methods
	 * will be done in the Unit tests.
	 * 
	 */
	public void testInjectedServices() {
		System.out.println("###### Simple tests for injected services ######");
		assertNotNull(rootVisualLexicon);
		assertNotNull(cyNetworkViewFactory);
		System.out.println("###### Injected services pass the tests. ######");
	}

	/**
	 * Specify Spring DM config file to import registered services by
	 * viewmodel-impl bundle.
	 * 
	 */
	@Override
	protected String[] getConfigLocations() {
		return new String[] { "file:./target/test-classes/META-INF/spring/bundle-context-test.xml" };
	}

	/**
	 * Import bundles required to run viewmodel-impl bundle.
	 */
	@Override
	protected String[] getTestBundlesNames() {
		return new String[] { 
				"org.cytoscape, event-api, 1.0-SNAPSHOT",
				"org.cytoscape, event-impl, 1.0-SNAPSHOT",
				"org.cytoscape, model-api, 1.0-SNAPSHOT",
				"org.cytoscape, service-util, 1.0-SNAPSHOT",
				"org.cytoscape, viewmodel-api, 1.0-SNAPSHOT",
				"org.cytoscape, viewmodel-impl, 1.0-SNAPSHOT"
				};
	}
}
