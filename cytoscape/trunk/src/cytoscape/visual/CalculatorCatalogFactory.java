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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.net.JarURLConnection;
import java.net.URL;

import cytoscape.Cytoscape;
import cytoscape.CytoscapeInit;
import cytoscape.util.ZipMultipleFiles;
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

	// static File propertiesFile;
	static String vizmapName;
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
	public static CalculatorCatalog loadCalculatorCatalog(String vmName) {
		vizmapName = vmName;

		final CalculatorCatalog calculatorCatalog = new CalculatorCatalog();

		// register mappings
		calculatorCatalog.addMapping("Discrete Mapper", DiscreteMapping.class);
		calculatorCatalog.addMapping("Continuous Mapper",
				ContinuousMapping.class);
		calculatorCatalog.addMapping("Passthrough Mapper",
				PassThroughMapping.class);

		vizmapProps = CytoscapeInit.getVisualProperties();

		// now load using the constructed Properties object (ok if it is empty)
		CalculatorIO.loadCalculators(vizmapProps, calculatorCatalog);

		// make sure a default visual style exists, creating as needed
		VisualStyle defaultVS = calculatorCatalog.getVisualStyle("default");

		Cytoscape.getSwingPropertyChangeSupport().addPropertyChangeListener(
				new PropertyChangeListener() {
					public void propertyChange(PropertyChangeEvent e) {
						if (e.getPropertyName() == Cytoscape.SAVE_VIZMAP_PROPS) {
							File propertiesFile = CytoscapeInit
									.getConfigFile("vizmap.props");
							if (propertiesFile != null) {

								// Testing
								Set test = calculatorCatalog
										.getVisualStyleNames();
								Iterator it = test.iterator();
								while (it.hasNext()) {
									System.out.println("Saving Visual Style: "
											+ it.next().toString());

								}

								CalculatorIO.storeCatalog(calculatorCatalog,
										propertiesFile);
								System.out.println("Vizmap saved to: "
										+ propertiesFile);
							}
						} else if (e.getPropertyName() == Cytoscape.VIZMAP_RESTORED) {

							// 
							vizmapProps.clear();
							calculatorCatalog.clear();

							// Rebuild mappings
							calculatorCatalog.addMapping("Discrete Mapper",
									DiscreteMapping.class);
							calculatorCatalog.addMapping("Continuous Mapper",
									ContinuousMapping.class);
							calculatorCatalog.addMapping("Passthrough Mapper",
									PassThroughMapping.class);

							Object vizmapSource = e.getNewValue();
							if(vizmapSource.getClass() == URL.class) {
								JarURLConnection jarConnection;
								try {
									jarConnection = (JarURLConnection)((URL) vizmapSource).openConnection();
									vizmapProps.load((InputStream) jarConnection.getContent());
								} catch (IOException e2) {
									// TODO Auto-generated catch block
									e2.printStackTrace();
								}
								
							} else if(vizmapSource.getClass() == String.class) {
								try {
									InputStream is = ZipMultipleFiles.readFile((String)vizmapSource,".*vizmap.props");
									if ( is != null )
										vizmapProps.load(is);

								} catch (FileNotFoundException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								} catch (IOException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
							}
							
							System.out
									.println("Restoring Saved Vizmapper from: " + vizmapSource.toString());

							
							CalculatorIO.loadCalculators(vizmapProps,
									calculatorCatalog);
							Cytoscape.getDesktop().resetVizMapUI();
							Cytoscape.getDesktop().getVizMapUI()
									.getStyleSelector().resetStyles();
							Cytoscape.getDesktop().getVizMapUI()
									.getStyleSelector().repaint();
							Cytoscape.getDesktop().getVizMapUI().refreshUI();

						} else if (e.getPropertyName() == Cytoscape.VIZMAP_LOADED) {
							vizmapProps.clear();
							calculatorCatalog.clear();

							// Rebuild mappings
							calculatorCatalog.addMapping("Discrete Mapper",
									DiscreteMapping.class);
							calculatorCatalog.addMapping("Continuous Mapper",
									ContinuousMapping.class);
							calculatorCatalog.addMapping("Passthrough Mapper",
									PassThroughMapping.class);

							Object vizmapSource = e.getNewValue();
							if(vizmapSource.getClass() == URL.class) {
								JarURLConnection jarConnection;
								try {
									jarConnection = (JarURLConnection)((URL) vizmapSource).openConnection();
									vizmapProps.load((InputStream) jarConnection.getContent());
								} catch (IOException e2) {
									// TODO Auto-generated catch block
									e2.printStackTrace();
								}
								
							} else if(vizmapSource.getClass() == String.class) {
								try {
									File userVizmapFile = new File((String)vizmapSource);
									vizmapProps.load(new FileInputStream(
											userVizmapFile));
								} catch (FileNotFoundException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								} catch (IOException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
							}
							
							System.out
									.println("Restoring Saved Vizmapper from: " + vizmapSource.toString());

							
							CalculatorIO.loadCalculators(vizmapProps,
									calculatorCatalog);
							Cytoscape.getDesktop().getVizMapUI()
									.getStyleSelector().resetStyles();
							Cytoscape.getDesktop().getVizMapUI()
									.getStyleSelector().repaint();
							Cytoscape.getDesktop().getVizMapUI().refreshUI();
						}
					}
				});

		return calculatorCatalog;
	}
}
