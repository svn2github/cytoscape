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
import cytoscape.util.shadegrown.WindowUtilities;
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
	private static final String SPLASH_SCREEN_LOCATION = "/images/CytoscapeSplashScreen.png";
	
	private static Properties properties;
	private static Properties visualProperties;

	static {
		System.out.println("CytoscapeInit static initialization");
		initProperties();
	}

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

			// setup properties
			initProperties();
			properties.putAll(initParams.getProps());
			visualProperties.putAll(initParams.getVizProps());

			
			// see if we are in headless mode
			// show splash screen, if appropriate
			System.out.println("init mode: " + initParams.getMode());

		
			// Initialize as GUI mode
			final ImageIcon image = new ImageIcon(this.getClass()
				                                          .getResource(SPLASH_SCREEN_LOCATION));
			WindowUtilities.showSplash(image, 8000);

			// set the wait cursor
			desktop.getJFrame().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

			setUpAttributesChangedListener();

//			System.out.println("loading session...");
//			loadSessionFile();

			System.out.println("loading networks...");
			loadNetworks();

//			System.out.println("loading attributes...");
//			loadAttributes();

			System.out.println("loading expression files...");
			loadExpressionFiles();

		} catch (Throwable t) {
			System.out.println("Caught initialization error:");
			t.printStackTrace();
		} finally {
			// Always restore the cursor and hide the splash, even there is
			// exception
			WindowUtilities.hideSplash();
			desktop.getJFrame().setCursor(Cursor.getDefaultCursor());

			// to clean up anything that the plugins have messed up
			desktop.getJFrame().repaint();
		}

		long endtime = System.currentTimeMillis() - begintime;
		System.out.println("\nCytoscape initialized successfully in: " + endtime + " ms");
		Cytoscape.firePropertyChange(Cytoscape.CYTOSCAPE_INITIALIZED, null, null);

		return true;
	}

	/**
	 * Returns the CyInitParams object used to initialize Cytoscape.
	 */
	public static CyInitParams getCyInitParams() {
		return initParams;
	}

	/**
	 * Returns the properties used by Cytoscape, the result of cytoscape.props
	 * and command line options.
	 */
	public static Properties getProperties() {
		return properties;
	}

	/**
	 * If .cytoscape directory does not exist, it creates it and returns it
	 *
	 * @return the directory ".cytoscape" in the users home directory.
	 */
	public static File getConfigDirectory() {
		File dir = null;

		try {
			String dirName = properties.getProperty("alternative.config.dir",
			                                        System.getProperty("user.home"));
			File parent_dir = new File(dirName, ".cytoscape");

			if (parent_dir.mkdir())
				System.err.println("Parent_Dir: " + parent_dir + " created.");

			return parent_dir;
		} catch (Exception e) {
			System.err.println("error getting config directory");
		}

		return null;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param file_name DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public static File getConfigFile(String file_name) {
		try {
			File parent_dir = getConfigDirectory();
			File file = new File(parent_dir, file_name);

			if (file.createNewFile())
				System.err.println("Config file: " + file + " created.");

			return file;
		} catch (Exception e) {
			System.err.println("error getting config file:" + file_name);
		}

		return null;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public static Properties getVisualProperties() {
		return visualProperties;
	}

	private static void loadStaticProperties(String defaultName, Properties props) {
		if (props == null) {
			System.out.println("input props is null");
			props = new Properties();
		}

		String tryName = "";

		try {
			// load the props from the jar file
			tryName = "application.jar";

			// This somewhat unusual way of getting the ClassLoader is because
			// other methods don't work from WebStart.
			//ClassLoader cl = Thread.currentThread().getContextClassLoader();
			ClassLoader cl = Cytoscape.class.getClassLoader();

			URL vmu = null;

			if (cl != null)
				vmu = cl.getResource(defaultName);
			else
				System.out.println("ClassLoader for reading cytoscape.jar is null");

			if (vmu != null)
			    // We'd like to use URLUtil.getBasicInputStream() to get
			    // InputStream, but it is too early in the initialization of
				// Cytoscape and vmu is most likely out of a local resource, so
				// get
			    // it directly:
				props.load(vmu.openStream());
			else
				System.out.println("couldn't read " + defaultName + " from " + tryName);

			// load the props file from $HOME/.cytoscape
			tryName = "$HOME/.cytoscape";

			File vmp = CytoscapeInit.getConfigFile(defaultName);

			if (vmp != null)
				props.load(new FileInputStream(vmp));
			else
				System.out.println("couldn't read " + defaultName + " from " + tryName);
		} catch (IOException ioe) {
			System.err.println("couldn't open " + tryName + " " + defaultName
			                   + " file - creating a hardcoded default");
			ioe.printStackTrace();
		}
	}

	private static void loadExpressionFiles() {
		// load expression data if specified
		List ef = initParams.getExpressionFiles();

		if ((ef != null) && (ef.size() > 0)) {
			for (Iterator iter = ef.iterator(); iter.hasNext();) {
				String expDataFilename = (String) iter.next();

				if (expDataFilename != null) {
					try {
						Cytoscape.loadExpressionData(expDataFilename, true);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
/*
	private boolean loadSessionFile() {
		String sessionFile = initParams.getSessionFile();

		try {
			String sessionName = "";

			if (sessionFile != null) {

				CytoscapeSessionReader reader = null;

				if (sessionFile.matches(FileUtil.urlPattern)) {
					URL u = new URL(sessionFile);
					reader = new CytoscapeSessionReader(u);
					sessionName = u.getFile();
				} else {
					Cytoscape.setCurrentSessionFileName(sessionFile);

					File shortName = new File(sessionFile);
					URL sessionURL = shortName.toURL();
					reader = new CytoscapeSessionReader(sessionURL);
					sessionName = shortName.getName();
				}

				if (reader != null) {
					reader.read();
					desktop.setTitle("Cytoscape Desktop (Session Name: " + sessionName + ")");
					return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("couldn't create session from file: '" + sessionFile + "'");
		} finally {
			desktop.getVizMapperUI().initVizmapperGUI();
			System.gc();
		}

		return false;
	}
	*/

	private static void initProperties() {
		if (properties == null) {
			properties = new Properties();
			loadStaticProperties("cytoscape.props", properties);
		}

		if (visualProperties == null) {
			visualProperties = new Properties();
			loadStaticProperties("vizmap.props", visualProperties);
		}
	}

	private void setUpAttributesChangedListener() {
		/*
		 * TODO - WTF?
		 *
		 * This cannot be done in CytoscapeDesktop construction (like the other
		 * menus) because we need CytoscapeDesktop created first. This is
		 * because CytoPanel menu item listeners need to register for CytoPanel
		 * events via a CytoPanel reference, and the only way to get a CytoPanel
		 * reference is via CytoscapeDeskop:
		 * desktop.getCytoPanel(...)
		 * desktop.getCyMenus().initCytoPanelMenus(); Add a
		 * listener that will apply vizmaps every time attributes change
		PropertyChangeListener attsChangeListener = new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				if (e.getPropertyName().equals(Cytoscape.ATTRIBUTES_CHANGED)) {
					// apply vizmaps
					Cytoscape.redrawGraph(Cytoscape.getCurrentNetworkView());
				}
			}
		};

		Cytoscape.getSwingPropertyChangeSupport().addPropertyChangeListener(attsChangeListener);
		 */
	}

	// Load all requested networks
	private void loadNetworks() {
		for (Iterator i = initParams.getGraphFiles().iterator(); i.hasNext();) {
			String net = (String) i.next();
			System.out.println("Load: " + net);

			CyNetwork network = null;

			boolean createView = false;

			if ((initParams.getMode() == CyInitParams.GUI)
			    || (initParams.getMode() == CyInitParams.EMBEDDED_WINDOW))
				createView = true;

			if (net.matches(FileUtil.urlPattern)) {
//				try {
//					// TODO 
//					network = Cytoscape.createNetworkFromURL(new URL(net), createView);
//				} catch (MalformedURLException mue) {
//					mue.printStackTrace();
//					System.out.println("Couldn't load network.  Bad URL!");
//				}
			} else {
				// TODO 
				//network = Cytoscape.createNetworkFromFile(net, createView);
			}

			// This is for browser and other plugins.
//			Object[] ret_val = new Object[3];
//			ret_val[0] = network;
//			ret_val[1] = net;
//			ret_val[2] = Integer.valueOf(0);
//
//			Cytoscape.firePropertyChange(Cytoscape.NETWORK_LOADED, null, ret_val);
		}
	}

	// load any specified data attribute files
	/*
	private void loadAttributes() {
		try {
			Cytoscape.loadAttributes((String[]) initParams.getNodeAttributeFiles()
			                                              .toArray(new String[] {  }),
			                         (String[]) initParams.getEdgeAttributeFiles()
			                                              .toArray(new String[] {  }));
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("failure loading specified attributes");
		}
	}
	*/

}
