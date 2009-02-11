/*
 File: CytoscapeInit.java

 Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)

 The Cytoscape Consortium is:
 - Institute for Systems Biology
 - University of California San Diego
 - Memorial Sloan-Kettering Cancer Center
 - Institut Pasteur
 - Agilent Technologies

 This library is free software; you can redistribute it and/or modify it
 under the terms of the GNU Lesser General Public License as published
 by the Free Software Foundation; either version 2.1 of the License, or
 any later version.

 This library is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 documentation provided hereunder is on an "as is" basis, and the
 Institute for Systems Biology and the Whitehead Institute
 have no obligations to provide maintenance, support,
 updates, enhancements or modifications.  In no event shall the
 Institute for Systems Biology and the Whitehead Institute
 be liable to any party for direct, indirect, special,
 incidental or consequential damages, including lost profits, arising
 out of the use of this software and its documentation, even if the
 Institute for Systems Biology and the Whitehead Institute
 have been advised of the possibility of such damage.  See
 the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation,
 Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 */
package cytoscape;

import cytoscape.init.CyInitParams;
import cytoscape.util.FileUtil;
import cytoscape.view.CySwingApplication;
//import cytoscape.util.shadegrown.WindowUtilities;
import org.cytoscape.model.CyNetwork;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;


/**
 * <p>
 * Cytoscape Init is responsible for starting Cytoscape in a way that makes
 * sense.
 * </p>
 * <p>
 * The comments below are more hopeful than accurate. We currently do not
 * support a "headless" mode (meaning there is no GUI). We do, however, hope to
 * support this in the future.
 * </p>
 *
 * <p>
 * The two main modes of running Cytoscape are either in "headless" mode or in
 * "script" mode. This class will use the command-line options to figure out
 * which mode is desired, and run things accordingly.
 * </p>
 *
 * The order for doing things will be the following:<br>
 * <ol>
 * <li>deterimine script mode, or headless mode</li>
 * <li>get options from properties files</li>
 * <li>get options from command line ( these overwrite properties )</li>
 * <li>Load all Networks</li>
 * <li>Load all data</li>
 * <li>Load all Plugins</li>
 * <li>Initialize all plugins, in order if specified.</li>
 * <li>Start Desktop/Print Output exit.</li>
 * </ol>
 *
 * @since Cytoscape 1.0
 * @author Cytoscape Core Team
 */
public class CytoscapeInit {
	
	private static Properties properties;
	private static Properties visualProperties;


	private static CyInitParams initParams;

	// Error message
	private static String ErrorMsg = "";

	private CySwingApplication desktop;

	/**
	 * Creates a new CytoscapeInit object.
	 */
	public CytoscapeInit(CySwingApplication desktop, CyNetworkManager netmgr) {
		Cytoscape.setDesktop( desktop );
		Cytoscape.setNetworkManager( netmgr );
		this.desktop = desktop;
	}

	/**
	 * Cytoscape Init must be initialized using the command line arguments.
	 *
	 * @param args
	 *            the arguments from the command line
	 * @return false, if we fail to initialize for some reason
	 */
	public boolean init(CyInitParams params) {
		long begintime = System.currentTimeMillis();

		try {
			initParams = params;

		} catch (Throwable t) {
			System.out.println("Caught initialization error:");
			t.printStackTrace();
		} finally {
			// to clean up anything that the plugins have messed up
			desktop.getJFrame().repaint();
		}

		long endtime = System.currentTimeMillis() - begintime;
		System.out.println("\nCytoscape initialized successfully in: " + endtime + " ms");
		Cytoscape.firePropertyChange(Cytoscape.CYTOSCAPE_INITIALIZED, null, null);

		return true;
	}
}
