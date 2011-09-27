package org.cytoscape.splash.internal;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.eclipse.gemini.blueprint.context.event.OsgiBundleApplicationContextListener;
import java.util.Properties;

/**
 * Meant to be run at startLevel 1 so that the splash screen pops up before all
 * other bundles start loading.  
 */
public final class SplashActivator implements BundleActivator {

    /**
     * Called whenever the OSGi framework starts our bundle
     */
    public void start( BundleContext bc ) throws Exception {
		SplashManipulator splash = new SplashManipulator();
		bc.addFrameworkListener(splash);
		bc.addBundleListener(splash);
		bc.registerService(OsgiBundleApplicationContextListener.class.getName(),splash,new Properties());
    }

    /**
     * Called whenever the OSGi framework stops our bundle
     */
    public void stop( BundleContext bc ) throws Exception {
    }
}

