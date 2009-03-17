package org.cytoscape.splash.internal;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkListener;
import org.osgi.framework.FrameworkEvent;

import org.cytoscape.splash.CreditScreen;
import java.util.Properties; 
import java.util.List; 
import java.util.ArrayList; 
import java.io.BufferedReader;
import java.io.InputStreamReader;

import javax.swing.ImageIcon;

/**
 * Meant to be run at startLevel 1 so that the splash screen pops up before all
 * other bundles start loading.  Also registers a CreditScreen service.
 */
public final class SplashActivator implements BundleActivator, FrameworkListener {

	private static final String SPLASH_IMAGE = "/images/CytoscapeSplashScreen.png";
	private static final String CREDIT_IMAGE = "/images/CytoscapeCredits.png";
	private static final String CREDITS = "/credits.txt";

	private SplashScreenImpl splash;

    /**
     * Called whenever the OSGi framework starts our bundle
     */
    public void start( BundleContext bc ) throws Exception {
		try {

		// setup and register the credits screen service
		final ImageIcon creditImage = new ImageIcon(this.getClass().getResource(CREDIT_IMAGE));
		BufferedReader br = new BufferedReader(new InputStreamReader(this.getClass().getResource(CREDITS).openStream()));
		List<String> lines = new ArrayList<String>();
		while ( br.ready() )
			lines.add( br.readLine() );
		bc.registerService(CreditScreen.class.getName(), new CreditScreenImpl(creditImage,lines), new Properties() );

		// set up and show the splash screen
		final ImageIcon splashImage = new ImageIcon(this.getClass().getResource(SPLASH_IMAGE));
		splash = new SplashScreenImpl(splashImage);
		bc.addFrameworkListener( this );
		splash.showSplash();

		} catch (Throwable t) {
			t.printStackTrace();
			if ( splash != null )
				splash.hideSplash();
		}
    }

    /**
     * Called whenever the OSGi framework stops our bundle
     */
    public void stop( BundleContext bc ) throws Exception {
    }


	public void frameworkEvent(FrameworkEvent event) {
		System.out.println("SPLASH got event: " + event.toString());
		if ( event.getType() == FrameworkEvent.STARTED ) {
			System.out.println("SPLASH got framework started event");
			if ( splash != null )
				splash.hideSplash();
		}
	}
}

