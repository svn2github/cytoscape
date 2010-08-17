package org.cytoscape.integration;

import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.springframework.osgi.test.AbstractConfigurableBundleCreatorTests;
import org.springframework.osgi.util.OsgiStringUtils;

/**
 * This is a simple wrapper class that when constructed properly verifies that
 * the expected services are exported given the Spring bundle-config-osgi.xml
 * configuration. This class should be used by every module that uses Spring-DM
 * to verify that the configuration is functional. To use this class simply
 * extend the class and call the constructor with the proper configuration
 * information for your bundle.
 * <br>
 * Here is a simple example
 * <pre>
package org.cytoscape.viewmodel;

import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.RootVisualLexicon;
import org.cytoscape.integration.AbstractIntegrationTester;

public class ITViewModelImpl extends AbstractIntegrationTester {

    public ITViewModelImpl() {
        super( // bundle name
               "org.cytoscape.viewmodel-impl",  

               // necessary bundles to run in the form: groupId, artifactId, version
               new String[] { "org.cytoscape, event-api, 1.0",
                              "org.cytoscape, event-impl, 1.0",
                              "org.cytoscape, model-api, 1.0",
                              "org.cytoscape, integration-test-support, 1.0",
                              "org.cytoscape, service-util, 1.0",
                              "org.cytoscape, viewmodel-api, 1.0",
                              "org.cytoscape, viewmodel-impl, 1.0", },

               // the beans you want to test
               new String[] { "rootVisualLexicon", "cyNetworkViewFactory" },

               // the classes of the beans
               new Class[] { RootVisualLexicon.class, CyNetworkViewFactory.class }
               );
    }
}
 * </pre>
 */
public class AbstractIntegrationTester extends AbstractConfigurableBundleCreatorTests {

	private String expectedBundleName;
	private String[] dependencyBundleNames;
	private String[] expectedBeanNames;
	private Class[] expectedClasses;

	/**
	 * The constructor that must be used to properly configure this class so that
	 * the integration tests run as expected.  The configuration being tested is
	 * found in src/main/resources/META-INF/spring/bundle-context-osgi.xml.
	 *
	 * @param expectedBundleName The name of the bundle that you're testing.
	 * @param dependencyBundleNames The names and versions of the bundles needed to
	 * provide the necessary OSGi services to start this bundle.
	 * @param expectedBeanNames The names of the beans that are exported as services
	 * by this bundle and whose configuration an existance you want to verify. This
	 * should include all services exported by this bundle.
	 * @param expectedClasses The Class object associated with each bean being tested. This
	 * array should be the same length as teh expectedBeanNames array.
	 */
	public AbstractIntegrationTester(String expectedBundleName,
	                               String[] dependencyBundleNames,
	                               String[] expectedBeanNames,
	                               Class[] expectedClasses) {
		this.expectedBundleName = expectedBundleName;
		this.dependencyBundleNames = dependencyBundleNames;
		this.expectedBeanNames = expectedBeanNames;
		this.expectedClasses = expectedClasses;
	}

	public void testOsgiPlatformStarts() throws Exception {

		// check bundleContext
		assertNotNull(bundleContext);

		// check that the expected bundle exists
		for (Bundle bundle : bundleContext.getBundles()) {
			final String bundleName = OsgiStringUtils.nullSafeName(bundle);
			if (bundleName.equals(expectedBundleName)) 
				return;
		}

		fail("expected bundle: " + expectedBundleName + " NOT found!");
	}

	public void testServiceReferencesExist() {
		for ( int i = 0; i < expectedBeanNames.length; i++ )
			checkServiceReference(expectedBeanNames[i], expectedClasses[i]);
	}

	private void checkServiceReference(String expectedBeanName, Class<?> serviceClass) {
		final ServiceReference ref = bundleContext.getServiceReference(serviceClass.getName());
		assertNotNull(ref);
		Object beanName = ref.getProperty("org.springframework.osgi.bean.name");
		assertEquals(expectedBeanName, beanName);
	}

	/**
	 * Specify Spring DM config file to import registered services by
	 * viewmodel-impl bundle.
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
		return dependencyBundleNames;
	}
}
