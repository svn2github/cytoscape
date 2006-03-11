/*
 File: CalculatorCatalogFactory.java 
 
 Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)
 
 The Cytoscape Consortium is: 
 - Institute for Systems Biology
 - University of California San Diego
 - Memorial Sloan-Kettering Cancer Center
 - Pasteur Institute
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

//----------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//----------------------------------------------------------------------------
package cytoscape.visual;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringBufferInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import cytoscape.Cytoscape;
import cytoscape.CytoscapeInit;
import cytoscape.util.ZipMultipleFiles;
import cytoscape.visual.calculators.GenericNodeLabelCalculator;
import cytoscape.visual.calculators.NodeLabelCalculator;
import cytoscape.visual.mappings.ContinuousMapping;
import cytoscape.visual.mappings.DiscreteMapping;
import cytoscape.visual.mappings.PassThroughMapping;

/**
 * This class provides a static method for reading a CalculatorCatalog object
 * from file, using parameters specified in a supplied CytoscapeConfig. What's
 * provided here is the set of files from which to read the calculator
 * information, as well as the construction of a suitable default visual style
 * if one does not already exist.
 */
public abstract class CalculatorCatalogFactory {

	static File propertiesFile;
	static Properties vizmapProps;

	public static CalculatorCatalog loadCalculatorCatalog() {
		return loadCalculatorCatalog(null);
	}

	/**
	 * Loads a CalculatorCatalog object from the various properties files
	 * specified by the options in the supplied CytoscapeConfig object. The
	 * catalog will be properly initialized with known mapping types and a
	 * default visual style (named "default").
	 */
	public static CalculatorCatalog loadCalculatorCatalog(String vizmapName) {

		final CalculatorCatalog calculatorCatalog = new CalculatorCatalog();

		// register mappings
		calculatorCatalog.addMapping("Discrete Mapper", DiscreteMapping.class);
		calculatorCatalog.addMapping("Continuous Mapper",
				ContinuousMapping.class);
		calculatorCatalog.addMapping("Passthrough Mapper",
				PassThroughMapping.class);

		boolean propsFound = false;
		vizmapProps = new Properties();

		String vizmapslocation = CytoscapeInit.getVizmapPropertiesLocation();

		if (vizmapslocation == null || vizmapslocation.length() == 0) {
			// we should have a vizmap.props, since CytoscapeInit creates one
			// if there is none
			throw new IllegalStateException(
					"CytoscapeInit.getVizmapPropertiesLocation() returns "
							+ vizmapslocation);
		}

		// See if the location is an URL
		boolean isURL = false;
		try {
			URL url = new URL(vizmapslocation);
			isURL = true;
		} catch (MalformedURLException e) {
			isURL = false;
		}

		if (isURL) {
			// do not write back to the file pointed to by the URL
			propertiesFile = null;
			try {
				System.out.println("Location for vizmap.props is a URL: "
						+ vizmapslocation);
				String url = vizmapslocation;
				// if (url != null) {
				vizmapProps.load(new BufferedInputStream(
						new StringBufferInputStream(
								new cytoscape.data.readers.TextHttpReader(url)
										.getText())));

				propsFound = true;
				// }
			} catch (IOException e) {
				// error while reading file into vizmapProps
				e.printStackTrace();
				propsFound = false;

			} catch (Exception e) {
				// something went wrong in
				// cytoscape.data.readers.TextHttpReader.getText()
				e.printStackTrace();
				propsFound = false;
			}
		} else {
			// not a URL
			File vizmaps = null;
			if (vizmapName == null) {
				vizmaps = new File(vizmapslocation);
			} else {
				System.out.println("!!!Name Found: " + vizmapName);
				vizmaps = new File(vizmapName);
			}

			try {
				vizmapProps.load(new FileInputStream(vizmaps));
				propsFound = true;
				propertiesFile = vizmaps;
			} catch (FileNotFoundException e) {
				// "file" does not exist. This should not happen!
				e.printStackTrace();
				propsFound = false;
				propertiesFile = null;
			} catch (IOException e) {
				// IO error while reading "file"
				e.printStackTrace();
				propsFound = false;
				propertiesFile = null;
			}

		}

		System.out.println("The save to location of vizmap.props is "
				+ propertiesFile);

		// now load using the constructed Properties object (ok if it is empty)
		CalculatorIO.loadCalculators(vizmapProps, calculatorCatalog);

		// make sure a default visual style exists, creating as needed
		VisualStyle defaultVS = calculatorCatalog.getVisualStyle("default");
		if (defaultVS == null) {

			System.out.println("No default visual style found. Creating...");

			defaultVS = new VisualStyle("default");
			// setup the default to at least put canonical names on the nodes
			String cName = "Common Names";
			NodeLabelCalculator nlc = calculatorCatalog
					.getNodeLabelCalculator(cName);
			if (nlc == null) {
				PassThroughMapping m = new PassThroughMapping(new String(),
						cytoscape.data.Semantics.COMMON_NAME);
				nlc = new GenericNodeLabelCalculator(cName, m);
			}
			defaultVS.getNodeAppearanceCalculator().setNodeLabelCalculator(nlc);
			calculatorCatalog.addVisualStyle(defaultVS);
		}

		Cytoscape.getSwingPropertyChangeSupport().addPropertyChangeListener(
				new PropertyChangeListener() {
					public void propertyChange(PropertyChangeEvent e) {
						if (e.getPropertyName() == Cytoscape.CYTOSCAPE_EXIT) {
							if (propertiesFile != null) {

								// Testing
								Set test = calculatorCatalog
										.getVisualStyleNames();
								Iterator it = test.iterator();
								while (it.hasNext()) {
									System.out.println("Saving VS: "
											+ it.next().toString());

								}

								CalculatorIO.storeCatalog(calculatorCatalog,
										propertiesFile);
								System.out.println("Quit: Saved Vizmaps to: "
										+ propertiesFile);
							}
						} else if (e.getPropertyName() == Cytoscape.SESSION_SAVED) {
							if (propertiesFile != null) {

								// Testing
								Set test = calculatorCatalog
										.getVisualStyleNames();
								Iterator it = test.iterator();
								while (it.hasNext()) {
									System.out
											.println("Session Writer is saving VS: "
													+ it.next().toString());

								}

								CalculatorIO.storeCatalog(calculatorCatalog,
										propertiesFile);
							}
						} else if (e.getPropertyName() == Cytoscape.SESSION_LOADED) {

							// Session Loaded. Clear current VS and load the new
							// one.
							if (propertiesFile != null) {

								vizmapProps.clear();
								calculatorCatalog.clear();

								String sessionName = (String) e.getNewValue();
								System.out
										.println("Restoring Saved Vizmapper from session file: "
												+ sessionName);

								ZipMultipleFiles zipUtil = new ZipMultipleFiles(sessionName);

								try {
									zipUtil.readVizmap();
									vizmapProps.load(new FileInputStream(
											propertiesFile));
								} catch (FileNotFoundException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								} catch (IOException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
								CalculatorIO.loadCalculators(vizmapProps,
										calculatorCatalog);
								Cytoscape.getDesktop().getVizMapUI()
										.getStyleSelector().resetStyles();
								Cytoscape.getDesktop().getVizMapUI()
										.getStyleSelector().repaint();
							}
						}
					}
				});

		return calculatorCatalog;
	}

}
