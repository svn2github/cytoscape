/** Copyright (c) 2002 Institute for Systems Biology and the Whitehead Institute
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** any later version.
 **
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and the
 ** Institute for Systems Biology and the Whitehead Institute
 ** have no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall the
 ** Institute for Systems Biology and the Whitehead Institute
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if the
 ** Institute for Systems Biology and the Whitehead Institute
 ** have been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 **
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/
//----------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//----------------------------------------------------------------------------
package cytoscape.visual;

import cytoscape.Cytoscape;
import cytoscape.CytoscapeInit;
import cytoscape.visual.calculators.GenericNodeLabelCalculator;
import cytoscape.visual.calculators.NodeLabelCalculator;
import cytoscape.visual.mappings.ContinuousMapping;
import cytoscape.visual.mappings.DiscreteMapping;
import cytoscape.visual.mappings.PassThroughMapping;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.StringBufferInputStream;
import java.util.Properties;
import java.io.IOException; 
import java.net.*;

/**
 * This class provides a static method for reading a CalculatorCatalog object
 * from file, using parameters specified in a supplied CytoscapeConfig.
 * What's provided here is the set of files from which to read the calculator
 * information, as well as the construction of a suitable default visual style
 * if one does not already exist.
 */
public abstract class CalculatorCatalogFactory {

    static File propertiesFile;
    static Properties vizmapProps;

    /**
     * Loads a CalculatorCatalog object from the various properties files
     * specified by the options in the supplied CytoscapeConfig object.
     * The catalog will be properly initialized with known mapping types
     * and a default visual style (named "default").
     */
    public static CalculatorCatalog loadCalculatorCatalog() {

        final CalculatorCatalog calculatorCatalog = new CalculatorCatalog();
        
        // register mappings
        calculatorCatalog.addMapping("Discrete Mapper", DiscreteMapping.class);
        calculatorCatalog.addMapping("Continuous Mapper", ContinuousMapping.class);
        calculatorCatalog.addMapping("Passthrough Mapper", PassThroughMapping.class);

        boolean propsFound = false;
        vizmapProps = new Properties();

        String vizmapslocation = CytoscapeInit.getVizmapPropertiesLocation();
        
        if(vizmapslocation == null || vizmapslocation.length() == 0){
        		// we should have a vizmap.props, since CytoscapeInit creates one
        		// if there is none
        		throw new IllegalStateException("CytoscapeInit.getVizmapPropertiesLocation() returns " + vizmapslocation);
        }
        
        // See if the location is an URL
        boolean isURL = false;
        try{
        		URL url = new URL(vizmapslocation);
        		isURL = true;
        }catch(MalformedURLException e){
        		isURL = false;
        }
        
        if(isURL){	
        		// do not write back to the file pointed to by the URL
        		propertiesFile = null;
	        try {
	        		System.out.println("Location for vizmap.props is a URL: " + vizmapslocation);
	        		String url = vizmapslocation;
	            //if (url != null) {
	        		vizmapProps.load(new BufferedInputStream(new StringBufferInputStream(new cytoscape.data.readers.TextHttpReader(url).getText())));
	        		propsFound = true;
	        		//}
	        } catch (IOException e) {
	        		// error while reading file into vizmapProps
	        		e.printStackTrace();
	            propsFound = false;
	            
	        } catch (Exception e){
	        		// something went wrong in cytoscape.data.readers.TextHttpReader.getText()
	        		e.printStackTrace();
	        		propsFound = false;
	        }
        }else{
        		// not a URL
        		File vizmaps = new File(vizmapslocation);
        		try{
        			vizmapProps.load(new FileInputStream(vizmaps));
        			propsFound = true;
        			propertiesFile = vizmaps;
        		}catch (FileNotFoundException e) {
        	        	// "file" does not exist. This should not happen!
        	        	e.printStackTrace();
        	        	propsFound = false;
        	        	propertiesFile = null;
        	     } catch (IOException e){
        	    	 	// IO error while reading "file"
        	    	 	e.printStackTrace();
        	    	 	propsFound = false;
        	    	 	propertiesFile = null;
        	     }
        	
        }
        
        System.out.println("The save to location of vizmap.props is " + propertiesFile);
        
        // OLD COMPLICATED CODE
        // 2. Try the current working directory
		// try {
		// File file = new File(System.getProperty("user.dir"), "vizmap.props");
		// //creates a new File
		// if (!propsFound) vizmapProps.load(new FileInputStream(file));// new
		// FileInputStream throws an exception if file does not exist
		// propertiesFile = file;
		// System.out.println("USER.DIR vizmaps found at: " + propertiesFile);
		// propsFound = true;
		// } catch (FileNotFoundException e) {
		// "file" does not exist. This should not happen, since we just created
		// it!
		// e.printStackTrace();
		// propsFound = false;
		// } catch (IOException e){
		// IO error while reading "file"
		// propsFound = false;
		// }

		// 3. Try CYTOSCAPE_HOME
		// try {
		// File file = new File(System.getProperty("CYTOSCAPE_HOME"),
		// "vizmap.props"); //creates a new file
		// if (!propsFound) vizmapProps.load(new FileInputStream(file));
		// propertiesFile = file;
		// System.out.println("CYTOSCAPE_HOME vizmaps found at: " +
		// propertiesFile);
		// propsFound = true;
		// } catch (FileNotFoundException e) {
		// "file" does not exist
		// e.printStackTrace();
		// propsFound = false;
		// } catch (IOException e){
		// e.printStackTrace();
		// IO error while reading "file"
		// propsFound = false;
		// }

		// 4. Try ~/.cytoscape
		// try {
		// File file = CytoscapeInit.getConfigFile("vizmap.props");
		// if (!propsFound) vizmapProps.load(new FileInputStream(file));
		// propertiesFile = file;
		// System.out.println(".CYTOSCAPE vizmaps found at: " + propertiesFile);
		// propsFound = true;
		// } catch (FileNotFoundException e) {
		// "file" does not exist. This should not happen, since we just created
		// it!
		// e.printStackTrace();
		// propsFound = false;
		// } catch (IOException e){
		// IO error while reading "file"
		// e.printStackTrace();
		// propsFound = false;
		// }
        // if (vizmapProps == null) {
	    // System.out.println("vizmaps not found");
		// vizmapProps = new Properties();
		// }

        // now load using the constructed Properties object (ok if it is empty)
        CalculatorIO.loadCalculators(vizmapProps, calculatorCatalog);

        //make sure a default visual style exists, creating as needed
        VisualStyle defaultVS = calculatorCatalog.getVisualStyle("default");
        if (defaultVS == null) {
            defaultVS = new VisualStyle("default");
            //setup the default to at least put canonical names on the nodes
            String cName = "Common Names";
            NodeLabelCalculator nlc = calculatorCatalog.getNodeLabelCalculator(cName);
            if (nlc == null) {
                PassThroughMapping m =
                        new PassThroughMapping(new String(), cytoscape.data.Semantics.COMMON_NAME);
                nlc = new GenericNodeLabelCalculator(cName, m);
            }
            defaultVS.getNodeAppearanceCalculator().setNodeLabelCalculator(nlc);
            calculatorCatalog.addVisualStyle(defaultVS);
        }

        Cytoscape.getSwingPropertyChangeSupport().addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent e) {
                if (e.getPropertyName() == Cytoscape.CYTOSCAPE_EXIT) {
                    if(propertiesFile != null){
                    		CalculatorIO.storeCatalog(calculatorCatalog, propertiesFile);
                    		System.out.println("Saved Vizmaps to: " + propertiesFile);
                    }
                }
            }
        });

        return calculatorCatalog;
    }


}

