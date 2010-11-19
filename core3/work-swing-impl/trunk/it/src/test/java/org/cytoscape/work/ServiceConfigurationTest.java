package org.cytoscape.work; 

import static org.ops4j.pax.exam.CoreOptions.*;
import static org.ops4j.pax.exam.container.def.PaxRunnerOptions.*;
import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.Inject;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.MavenConfiguredJUnit4TestRunner;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;


import org.cytoscape.work.swing.GUITaskManager;
import org.cytoscape.work.swing.GUITunableInterceptor;
import org.cytoscape.work.undo.UndoSupport;
import org.cytoscape.integration.ServiceTestSupport;

//@RunWith(MavenConfiguredJUnit4TestRunner.class)
@RunWith(JUnit4TestRunner.class)
public class ServiceConfigurationTest extends ServiceTestSupport {

	@Configuration
	public static Option[] configuration() {      
		return options(felix(), 
		               profile("spring.dm"), 
		               provision( 
		                         mavenBundle().groupId("org.cytoscape").artifactId("model-api").versionAsInProject(),
		                         mavenBundle().groupId("org.cytoscape").artifactId("viewmodel-api").versionAsInProject(),
		                         mavenBundle().groupId("org.cytoscape").artifactId("presentation-api").versionAsInProject(),
		                         mavenBundle().groupId("org.cytoscape").artifactId("vizmap-api").versionAsInProject(),
		                         mavenBundle().groupId("org.cytoscape").artifactId("event-api").versionAsInProject(),
		                         mavenBundle().groupId("org.cytoscape").artifactId("session-api").versionAsInProject(),
		                         mavenBundle().groupId("org.cytoscape").artifactId("work-api").versionAsInProject(),
		                         mavenBundle().groupId("org.cytoscape").artifactId("work-swing-api").versionAsInProject(),
		                         mavenBundle().groupId("org.cytoscape").artifactId("work-swing-impl").versionAsInProject(),
		                         mavenBundle().groupId("org.cytoscape").artifactId("property-api").versionAsInProject(),
		                         mavenBundle().groupId("org.cytoscape").artifactId("property-impl").versionAsInProject(),
		                         mavenBundle().groupId("org.cytoscape").artifactId("io-api").versionAsInProject(),
		                         mavenBundle().groupId("org.cytoscape").artifactId("work-spring-hack").versionAsInProject(),
		                         mavenBundle().groupId("org.cytoscape").artifactId("integration-test-support").versionAsInProject()
		));
	}  

	@Test
	public void testExpectedServices() {
		checkService(GUITaskManager.class);
		checkService(TaskManager.class);
		checkService(TunableHandlerFactory.class);
		checkService(TunableInterceptor.class);
		checkService(GUITunableInterceptor.class);
		checkService(UndoSupport.class);
	}
}
