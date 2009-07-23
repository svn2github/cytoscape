
/*
Copyright (c) 2008, The Cytoscape Consortium (www.cytoscape.org)

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

package org.cytoscape.cmdline.launcher.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.felix.fileinstall.FileInstall;
import org.apache.felix.framework.Felix;
import org.apache.felix.framework.util.FelixConstants;
import org.apache.felix.framework.util.StringMap;
import org.apache.felix.main.AutoActivator;
import org.apache.felix.main.Main;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;
import org.osgi.framework.ServiceReference;


/**
 * A class that launches Felix and automatically starts the FileInstall
 * bundle as well as starting a CommandLineProvider service. 
 */
public class Launcher {

	
	static ServiceReference serv;
	static BundleContext context;
	
	/**
	 * The main method. 
	 *
	 * @param args The command line arguments. 
	 */
	public static void main(String[] args) {

		// Tell felix where the config file is.
		System.setProperty(Main.CONFIG_PROPERTIES_PROP, 
		                   Launcher.class.getResource("/conf/config.properties").toString());

		// Load system properties.
		Main.loadSystemProperties();

		// Read configuration properties.
		Properties configProps = Main.loadConfigProperties();

		// Copy framework properties from the system properties.
		Main.copySystemProperties(configProps);

		//install.
		try {
			// Create a list of bundles to start automatically.
	        List<BundleActivator> list = new ArrayList<BundleActivator>();
	        list.add(new AutoActivator(configProps)); // from config auto.start 
	        list.add(new FileInstall());
	        list.add(new CommandLineProviderImpl(args));
	        configProps.put(FelixConstants.SYSTEMBUNDLE_ACTIVATORS_PROP, list);
			// Create a case-insensitive property map.
			Map configMap = new StringMap(configProps, false);

			// Create an instance of the framework.
			final Felix m_felix = new Felix(configMap);
			m_felix.start();


			m_felix.getBundleContext().addBundleListener(new BundleListener(){

				long id;
				boolean firstBreak = false;
				boolean secondBreak = false;
				
				public void bundleChanged(BundleEvent event) {
					if(firstBreak && secondBreak){
						if(event.getBundle().getSymbolicName().equals(m_felix.getBundleContext().getBundle(m_felix.getBundleContext().getBundles().length-1).getSymbolicName())){
							long time0 = System.currentTimeMillis();
							long time1 = System.currentTimeMillis()+100;
							try{
								long timediff = time1-time0;
								System.out.println("\n\n\n\n Loading TaskFactories ... \n\n\n\n");
								while(timediff<5000){
									time1 = System.currentTimeMillis();
									timediff = time1-time0;
								}
								m_felix.getBundleContext().getBundle(id).start();
							}catch(Exception e){e.printStackTrace();}
						}
					}
					
					if(event.getBundle().getSymbolicName().equals("org.cytoscape.cmdline-parser-impl")){
						id=event.getBundle().getBundleId();
						try{
							if(firstBreak && secondBreak){
								m_felix.getBundleContext().getBundle(id).start();
							}
							else{
								m_felix.getBundleContext().getBundle(id).stop();
							}
						}catch(Exception e){e.printStackTrace();}
					}


					if(event.getBundle().getSymbolicName().equals("org.cytoscape.core-task-impl") && event.getType()==BundleEvent.STARTED){
						System.out.println("\n\n\n\n 2nd BREAKPOINT \n\n\n\n");
						secondBreak=true;
					}
					
					if(event.getBundle().getSymbolicName().equals("org.cytoscape.core-task-api") && event.getType()==BundleEvent.STARTED){
						System.out.println("\n\n\n\n 1st BREAKPOINT \n\n\n\n");
						firstBreak=true;
					}
				
				}
				
			});
			
			
            // Wait for framework to stop to exit the VM.
            m_felix.waitForStop(0);
            System.exit(0);

		} catch (Exception ex) {
			System.err.println("Could not create framework: " + ex);
			ex.printStackTrace();
			System.exit(-1);
		}
	}
}