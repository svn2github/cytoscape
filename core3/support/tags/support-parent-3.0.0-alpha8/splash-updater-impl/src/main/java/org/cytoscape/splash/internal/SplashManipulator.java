package org.cytoscape.splash.internal;

import java.awt.SplashScreen;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.AlphaComposite;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.BundleListener;
import org.osgi.framework.FrameworkListener;
//import org.springframework.osgi.context.event.OsgiBundleContextRefreshedEvent;
//import org.springframework.osgi.context.event.OsgiBundleApplicationContextEvent;
//import org.springframework.osgi.context.event.OsgiBundleApplicationContextListener;
import java.awt.Font;

public class SplashManipulator implements 
	//OsgiBundleApplicationContextListener,
	BundleListener,
	FrameworkListener {

	private final SplashScreen splash;
	private final Graphics2D g;
	private final Font font;

    public SplashManipulator() {
        splash = SplashScreen.getSplashScreen();
		if (splash == null)
			g = null;
		else
        	g = splash.createGraphics();
		renderSplashFrame("Cytoscape Starting...");
		font = new Font(Font.MONOSPACED,Font.PLAIN,12);
	}

    public void bundleChanged(BundleEvent event) {
		if ( event.getType() == BundleEvent.STARTED )
			renderSplashFrame(event.getBundle().getSymbolicName() + " started");
	}

    public void frameworkEvent(FrameworkEvent event) {
		if ( event.getType() == FrameworkEvent.STARTED ) 
			renderSplashFrame("OSGi finished.");
	}

//	public void onOsgiApplicationEvent(OsgiBundleApplicationContextEvent event) {
//		if ( event instanceof OsgiBundleContextRefreshedEvent )
//			renderSplashFrame( event.getBundle().getSymbolicName() + " refreshed");
//	}

    private synchronized void renderSplashFrame(String message) {
		if ( g == null || splash == null || !splash.isVisible() )
			return;
        //g.setComposite(AlphaComposite.Clear);
        g.setColor(Color.WHITE);
        g.fillRect(20,300,800,40);
        g.setPaintMode();
        g.setColor(Color.BLACK);
		g.setFont(font);
        g.drawString(message, 20, 320);
		if ( splash.isVisible() )
			splash.update();
    }
}
