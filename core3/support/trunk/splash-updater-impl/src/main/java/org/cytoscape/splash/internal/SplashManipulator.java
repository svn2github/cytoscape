package org.cytoscape.splash.internal;

/*
 * #%L
 * Splash Updater
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2006 - 2013 The Cytoscape Consortium
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as 
 * published by the Free Software Foundation, either version 2.1 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.SplashScreen;
import java.util.HashSet;
import java.util.Set;

import javax.swing.SwingUtilities;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.FrameworkListener;

public class SplashManipulator implements
	BundleListener,
	FrameworkListener {

	private final SplashScreen splash;
	private final Graphics2D g;
	private final Font font;
	private Set<Long> resolved;
	private Set<Long> started;
	private BundleContext context;

    public SplashManipulator(BundleContext context) {
    	this.context = context;
    	resolved = new HashSet<Long>();
    	started = new HashSet<Long>();
    	
    	for (Bundle bundle : context.getBundles()) {
    		long id = bundle.getBundleId();
    		resolved.add(id);
    		if (bundle.getState() == Bundle.ACTIVE)
    			started.add(id);
    	}
    	
        splash = SplashScreen.getSplashScreen();
		if (splash == null)
			g = null;
		else
        	g = splash.createGraphics();
		renderSplashFrame("Cytoscape Starting...");
		font = new Font(Font.MONOSPACED,Font.PLAIN,12);
	}

	public void bundleChanged(BundleEvent event) {
		if ( event.getType() == BundleEvent.RESOLVED )
			resolved.add(event.getBundle().getBundleId());
		
		if ( event.getType() == BundleEvent.STARTED ) {
			started.add(event.getBundle().getBundleId());
			renderSplashFrame(event.getBundle().getSymbolicName() + " started");
		}
	}

    public void frameworkEvent(FrameworkEvent event) {
		if ( event.getType() == FrameworkEvent.STARTED ) { 
			renderSplashFrame("OSGi finished.");
			context.removeBundleListener(this);
			context.removeFrameworkListener(this);
			resolved.clear();
			started.clear();
		}
	}

    private synchronized void renderSplashFrame(final String message) {
    	if (!SwingUtilities.isEventDispatchThread()) {
    		SwingUtilities.invokeLater(new Runnable() {
    			@Override
    			public void run() {
    				renderSplashFrame(message);
    			}
    		});
    		return;
    	}
    	
		if ( g == null || splash == null || !splash.isVisible())
			return;
        g.setColor(Color.WHITE);
        g.fillRect(20,300,800,40);
        g.setPaintMode();
        g.setColor(Color.BLACK);
		g.setFont(font);
        g.drawString(message, 20, 320);
        
        int totalResolved = resolved.size();
        int totalStarted = started.size();
        double progress = totalResolved == 0 ? 0 : (double) totalStarted / totalResolved;
        g.setColor(new Color(computeColor(progress)));
        int progressWidth = (int) (800.0 * progress);
        g.fillRect(20, 304, progressWidth, 4);
		if ( splash.isVisible() )
			splash.update();
    }

	private int computeColor(double progress) {
		int red = (int) (progress * 247);
		int green = (int) (progress * 148);
		int blue = (int) (progress * 30);
		return (red << 16) | (green << 8) | blue;
	}
}
