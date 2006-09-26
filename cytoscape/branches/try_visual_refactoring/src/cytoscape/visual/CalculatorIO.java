
/*
  File: CalculatorIO.java 
  
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

//----------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//----------------------------------------------------------------------------
package cytoscape.visual;
//----------------------------------------------------------------------------
import java.util.*;
import java.io.*;

import cytoscape.visual.calculators.*;
import cytoscape.visual.ui.VizMapUI;
//----------------------------------------------------------------------------
/**
 * This class defines static methods for reading calculator definitions from
 * a properties object and installing them into a CalculatorCatalog, and for
 * constructing a properties object that describes all the calculators in a
 * CalculatorCatalog.
 */
public class CalculatorIO {
    
    public static final String dirHeader = "cytoscape.visual.calculators.";

    public static final String nodeColorBaseKey = "nodeColorCalculator";
    public static final String nodeFillColorBaseKey = "nodeFillColorCalculator";
    public static final String nodeBorderColorBaseKey = "nodeBorderColorCalculator";
    public static final String nodeLineTypeBaseKey = "nodeLineTypeCalculator";
    public static final String nodeShapeBaseKey = "nodeShapeCalculator";
    public static final String nodeSizeBaseKey = "nodeSizeCalculator";
    public static final String nodeWidthBaseKey = "nodeWidthCalculator";
    public static final String nodeHeightBaseKey = "nodeHeightCalculator";
    public static final String nodeUniformSizeBaseKey = "nodeUniformSizeCalculator";
    public static final String nodeLabelBaseKey = "nodeLabelCalculator";
    public static final String nodeToolTipBaseKey = "nodeToolTipCalculator";
    public static final String nodeFontFaceBaseKey = "nodeFontFaceCalculator";
    public static final String nodeFontSizeBaseKey = "nodeFontSizeCalculator";
    public static final String edgeColorBaseKey = "edgeColorCalculator";
    public static final String edgeLineTypeBaseKey = "edgeLineTypeCalculator";
    public static final String edgeArrowBaseKey = "edgeArrowCalculator";
    public static final String edgeSourceArrowBaseKey = "edgeSourceArrowCalculator";
    public static final String edgeTargetArrowBaseKey = "edgeTargetArrowCalculator";
    public static final String edgeLabelBaseKey = "edgeLabelCalculator";
    public static final String edgeToolTipBaseKey = "edgeToolTipCalculator";
    public static final String edgeFontFaceBaseKey = "edgeFontFaceCalculator";
    public static final String edgeFontSizeBaseKey = "edgeFontSizeCalculator";
    
    public static final String nodeAppearanceBaseKey = "nodeAppearanceCalculator";
    public static final String edgeAppearanceBaseKey = "edgeAppearanceCalculator";
    public static final String globalAppearanceBaseKey = "globalAppearanceCalculator";
    
    
    /**
     * Writes the contents of a CalculatorCatalog to the specified file as a
     * properties file.
     * This method sorts the lines of text produced by the store method of
     * Properties, so that the properties descriptions of the calculators are
     * reasonably human-readable.
     */
    public static void storeCatalog(CalculatorCatalog catalog, File outFile) {
        try {
            //construct the header comment for the file
            String lineSep = System.getProperty("line.separator");
            StringBuffer header = new StringBuffer();
            header.append("This file specifies visual mappings for Cytoscape");
            header.append(" and has been automatically generated.").append(lineSep);
            header.append("# WARNING: any changes you make to this file while");
            header.append(" Cytoscape is running may be overwritten.").append(lineSep);
            header.append("# Any changes may make these visual mappings unreadable.");
            header.append(lineSep);
            header.append("# Please make sure you know what you are doing before");
            header.append(" modifying this file by hand.").append(lineSep);
            
            //writer that writes final version to file;
            //created now so that we crash early if the file is unwritable
            BufferedWriter writer = new BufferedWriter(new FileWriter(outFile));
            
            //get a Properties description of the catalog
            Properties props = getProperties(catalog);
            //and dump it to a buffer of bytes
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            props.store( buffer, header.toString() );
            
            //convert the bytes to a String we can read from
            String theData = buffer.toString();
            BufferedReader reader = new BufferedReader(new StringReader(theData));
            //read all the lines and store them in a container object
            //store the header lines separately so they don't get sorted
            List headerLines = new ArrayList();
            List lines = new ArrayList();
            String oneLine = reader.readLine();
            while (oneLine != null) {
                if (oneLine.startsWith("#")) {
                    headerLines.add(oneLine);
                } else {
                    lines.add(oneLine);
                }
                oneLine = reader.readLine();
            }
            
            //now sort all the non-header lines
            Collections.sort(lines);
            //and write to file
            for (Iterator li = headerLines.iterator(); li.hasNext(); ) {
                String theLine = (String)li.next();
                writer.write(theLine, 0, theLine.length());
                writer.newLine();
            }
            for (Iterator li = lines.iterator(); li.hasNext(); ) {
                String theLine = (String)li.next();
                writer.write(theLine, 0, theLine.length());
                writer.newLine();
            }
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
            
    /**
     * Given a CalculatorCatalog, assembles a Properties object representing all of the
     * calculators contained in the catalog. The resulting Properties object, if passed
     * to the loadCalculators method, would reconstruct all the calculators. This method
     * works by getting each set of calculators from the catalog and calling the
     * getProperties method on each calculator with the proper header for the property key.
     */
    public static Properties getProperties(CalculatorCatalog catalog) {
        Properties newProps = new Properties();
        
        //gather properties for node calculators
        //addProperties(newProps, catalog.getCalculators(VizMapUI.NODE_COLOR), nodeColorBaseKey);
        addProperties(newProps, catalog.getCalculators(VizMapUI.NODE_COLOR), nodeFillColorBaseKey);
        addProperties(newProps, catalog.getCalculators(VizMapUI.NODE_BORDER_COLOR), nodeBorderColorBaseKey);
        addProperties(newProps, catalog.getCalculators(VizMapUI.NODE_LINETYPE), nodeLineTypeBaseKey);
        addProperties(newProps, catalog.getCalculators(VizMapUI.NODE_SHAPE), nodeShapeBaseKey);
        //addProperties(newProps, catalog.getCalculators(VizMapUI.NODE_SIZE), nodeSizeBaseKey);
        addProperties(newProps, catalog.getCalculators(VizMapUI.NODE_SIZE), nodeUniformSizeBaseKey);
        addProperties(newProps, catalog.getCalculators(VizMapUI.NODE_HEIGHT), nodeHeightBaseKey);
        addProperties(newProps, catalog.getCalculators(VizMapUI.NODE_WIDTH), nodeWidthBaseKey);
        addProperties(newProps, catalog.getCalculators(VizMapUI.NODE_LABEL), nodeLabelBaseKey);
        addProperties(newProps, catalog.getCalculators(VizMapUI.NODE_TOOLTIP), nodeToolTipBaseKey);
        addProperties(newProps, catalog.getCalculators(VizMapUI.NODE_FONT_FACE), nodeFontFaceBaseKey);
        addProperties(newProps, catalog.getCalculators(VizMapUI.NODE_FONT_SIZE), nodeFontSizeBaseKey);
        //gather properties for edge calculators
        addProperties(newProps, catalog.getCalculators(VizMapUI.EDGE_COLOR), edgeColorBaseKey);
        addProperties(newProps, catalog.getCalculators(VizMapUI.EDGE_LINETYPE), edgeLineTypeBaseKey);
        addProperties(newProps, catalog.getCalculators(VizMapUI.EDGE_SRCARROW), edgeSourceArrowBaseKey);
        addProperties(newProps, catalog.getCalculators(VizMapUI.EDGE_TGTARROW), edgeTargetArrowBaseKey);
        addProperties(newProps, catalog.getCalculators(VizMapUI.EDGE_LABEL), edgeLabelBaseKey);
        addProperties(newProps, catalog.getCalculators(VizMapUI.EDGE_TOOLTIP), edgeToolTipBaseKey);
        addProperties(newProps, catalog.getCalculators(VizMapUI.EDGE_FONT_FACE), edgeFontFaceBaseKey);
        addProperties(newProps, catalog.getCalculators(VizMapUI.EDGE_FONT_SIZE), edgeFontSizeBaseKey);
        
        //visual styles
        Set visualStyleNames = catalog.getVisualStyleNames();
        for (Iterator i = visualStyleNames.iterator(); i.hasNext(); ) {
            String name = (String)i.next();
            VisualStyle vs = catalog.getVisualStyle(name);
            try {
                Properties styleProps = new Properties();
                NodeAppearanceCalculator nac = vs.getNodeAppearanceCalculator();
                String nacBaseKey = nodeAppearanceBaseKey + "." + name;
                Properties nacProps = nac.getProperties(nacBaseKey);
                styleProps.putAll(nacProps);
                EdgeAppearanceCalculator eac = vs.getEdgeAppearanceCalculator();
                String eacBaseKey = edgeAppearanceBaseKey + "." + name;
                Properties eacProps = eac.getProperties(eacBaseKey);
                styleProps.putAll(eacProps);
                GlobalAppearanceCalculator gac = vs.getGlobalAppearanceCalculator();
                String gacBaseKey = globalAppearanceBaseKey + "." + name;
                Properties gacProps = gac.getProperties(gacBaseKey);
                styleProps.putAll(gacProps);
                //now that we've constructed all the properties for this visual
                //style without Exceptions, store in the global properties object
                newProps.putAll(styleProps);
            } catch (Exception e) {
                String s = "Exception while saving visual style " + name;
                System.err.println(s);
                System.err.println(e.getMessage());
            }
        }
        
        return newProps;
    }
    
    /**
     * Given a collection of calculators and a base key, gets the properties description from
     * each calculator and adds all the properties to the supplied Properties argument.
     */
    private static void addProperties(Properties newProps, Collection calcs, String baseKey) {
        for (Iterator i = calcs.iterator(); i.hasNext(); ) {
            Calculator c = (Calculator)i.next();
            if (c == null) {continue;}
            String calcBaseKey = baseKey + "." + c.toString();
            //wrap each calculator addition in a try/catch block, so that
            //failure to convert one calculator doesn't affect all the others
            try {
                Properties props = CalculatorFactory.getProperties(c, calcBaseKey);
                newProps.putAll(props);
            } catch (Exception e) {
                System.err.println("Exception while saving " + calcBaseKey);
                System.err.println(e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    
    /**
     * Equivalent to loadCalculators(props, catalog, true);
     */
    public static void loadCalculators(Properties props, CalculatorCatalog catalog) {
        loadCalculators(props, catalog, true);
    }
    
    /**
     * Loads calculators from their description in a Properties object into a
     * supplied CalculatorCatalog object. This method searches the Properties
     * object for known keys identifying calculators, then delegates to other
     * methods that use the preprocessed properties to construct valid
     * calculator objects.
     * For any calculator defined by the Properties, it is possible for the
     * catalog to already hold a calculator with the same name and interface
     * type (especially if this method has already been run with the same
     * Properties object and catalog). If the overWrite argument is true,
     * this method will remove any such duplicate calculator before adding
     * the new one to prevent duplicate name exceptions. If overwrite is
     * false, this method will get a unique name from the catalog and change
     * the name of the installed calculator as needed.
     */
    public static void loadCalculators(Properties props, CalculatorCatalog catalog,
                                       boolean overWrite) {
        /* The supplied Properties object may contain any kinds of properties.
         * We look for keys that start with a name we recognize, identifying a
         * particular type of calculator. The second field of the key should
         * then be an identifying name. For example,
         *     nodeColorCalculator.mySpecialCalculator.{anything else}
         *
         * The first thing we want to do is group every key with the same name
         * and type together for further processing. To do this, we construct
         * a Map object for each type of calculator where the the map keys are
         * the names, and the map value is a new Properties object that only 
         * contains the properties with that specific type and name. Copying
         * the Properties to a new object ensures that calculators can't trample
         * the original while customizing themselves.
         *
         * Note that we need separate constructs for each type of calculator,
         * because calculators of different types are allowed to share the same name.
         */

        Map nodeFillColorNames = new HashMap();
        Map nodeBorderColorNames = new HashMap();
        Map nodeLineTypeNames = new HashMap();
        Map nodeShapeNames = new HashMap();
        Map nodeUniformSizeNames = new HashMap();
        Map nodeWidthNames = new HashMap();
        Map nodeHeightNames = new HashMap();
        Map nodeLabelNames = new HashMap();
        Map nodeToolTipNames = new HashMap();
        Map nodeFontFaceNames = new HashMap();
        Map nodeFontSizeNames = new HashMap();
        Map edgeColorNames = new HashMap();
        Map edgeLineTypeNames = new HashMap();
        Map edgeSourceArrowNames = new HashMap();
        Map edgeTargetArrowNames = new HashMap();
        Map edgeLabelNames = new HashMap();
        Map edgeToolTipNames = new HashMap();
        Map edgeFontFaceNames = new HashMap();
        Map edgeFontSizeNames = new HashMap();
        Map nacNames = new HashMap();
        Map eacNames = new HashMap();
        Map gacNames = new HashMap();

        //use the propertyNames() method instead of the generic Map iterator,
        //because the former method recognizes layered properties objects.
        //see the Properties javadoc for details
        for (Enumeration eI = props.propertyNames(); eI.hasMoreElements(); ) {
            String key = (String)eI.nextElement();

            if (key.startsWith(nodeColorBaseKey + ".")) {
	    	key = updateLegacyKey(key,props,nodeColorBaseKey,nodeFillColorBaseKey,
			"cytoscape.visual.calculators.GenericNodeFillColorCalculator");
      		storeKey(key, props, nodeFillColorNames);
	    	key = updateLegacyKey(key,props,nodeFillColorBaseKey,nodeBorderColorBaseKey,
			"cytoscape.visual.calculators.GenericNodeBorderColorCalculator");
		storeKey(key, props, nodeBorderColorNames);
            } else if (key.startsWith(nodeFillColorBaseKey + ".")) {
                storeKey(key, props, nodeFillColorNames);
            } else if (key.startsWith(nodeBorderColorBaseKey + ".")) {
                storeKey(key, props, nodeBorderColorNames);
            } else if (key.startsWith(nodeLineTypeBaseKey + ".")) {
                storeKey(key, props, nodeLineTypeNames);
            } else if (key.startsWith(nodeShapeBaseKey + ".")) {
                storeKey(key, props, nodeShapeNames);
            } else if (key.startsWith(nodeSizeBaseKey + ".")) {
	    	key = updateLegacyKey(key,props,nodeSizeBaseKey,nodeUniformSizeBaseKey,
			"cytoscape.visual.calculators.GenericNodeUniformSizeCalculator");
                storeKey(key, props, nodeUniformSizeNames);
	    	key = updateLegacyKey(key,props,nodeUniformSizeBaseKey,nodeWidthBaseKey,
			"cytoscape.visual.calculators.GenericNodeWidthCalculator");
		storeKey(key, props, nodeWidthNames);
	    	key = updateLegacyKey(key,props,nodeWidthBaseKey,nodeHeightBaseKey,
			"cytoscape.visual.calculators.GenericNodeHeightCalculator");
		storeKey(key, props, nodeHeightNames);
            } else if (key.startsWith(nodeUniformSizeBaseKey + ".")) {
                storeKey(key, props, nodeUniformSizeNames);
            } else if (key.startsWith(nodeHeightBaseKey + ".")) {
                storeKey(key, props, nodeHeightNames);
            } else if (key.startsWith(nodeWidthBaseKey + ".")) {
                storeKey(key, props, nodeWidthNames);
            } else if (key.startsWith(nodeLabelBaseKey + ".")) {
                storeKey(key, props, nodeLabelNames);
            } else if (key.startsWith(nodeToolTipBaseKey + ".")) {
                storeKey(key, props, nodeToolTipNames);
            } else if (key.startsWith(nodeFontFaceBaseKey + ".")) {
                storeKey(key, props, nodeFontFaceNames);
            } else if (key.startsWith(nodeFontSizeBaseKey + ".")) {
                storeKey(key, props, nodeFontSizeNames);
            } else if (key.startsWith(edgeColorBaseKey + ".")) {
                storeKey(key, props, edgeColorNames);
            } else if (key.startsWith(edgeLineTypeBaseKey + ".")) {
                storeKey(key, props, edgeLineTypeNames);
            } else if (key.startsWith(edgeArrowBaseKey + ".")) {
	    	key = updateLegacyKey(key,props,edgeArrowBaseKey,edgeSourceArrowBaseKey,
			"cytoscape.visual.calculators.GenericEdgeSourceArrowCalculator");
		storeKey(key, props, edgeSourceArrowNames);
	    	key = updateLegacyKey(key,props,edgeSourceArrowBaseKey,edgeTargetArrowBaseKey,
			"cytoscape.visual.calculators.GenericEdgeTargetArrowCalculator");
		storeKey(key, props, edgeTargetArrowNames);
            } else if (key.startsWith(edgeSourceArrowBaseKey + ".")) {
                storeKey(key, props, edgeSourceArrowNames);
            } else if (key.startsWith(edgeTargetArrowBaseKey + ".")) {
                storeKey(key, props, edgeTargetArrowNames);
            } else if (key.startsWith(edgeLabelBaseKey + ".")) {
                storeKey(key, props, edgeLabelNames);
            } else if (key.startsWith(edgeToolTipBaseKey + ".")) {
                storeKey(key, props, edgeToolTipNames);
            } else if (key.startsWith(edgeFontFaceBaseKey + ".")) {
                storeKey(key, props, edgeFontFaceNames);
            } else if (key.startsWith(edgeFontSizeBaseKey + ".")) {
                storeKey(key, props, edgeFontSizeNames);
            } else if (key.startsWith(nodeAppearanceBaseKey + ".")) {
                storeKey(key, props, nacNames);
            } else if (key.startsWith(edgeAppearanceBaseKey + ".")) {
                storeKey(key, props, eacNames);
            } else if (key.startsWith(globalAppearanceBaseKey + ".")) {
                storeKey(key, props, gacNames);
            }
        }
        
        /* Now that we have all the properties in groups, we pass each Map of
         * names and Properties objects to a helper function that creates a
         * calculator for each entry and stores the calculators in the catalog.
         * Before storing the calculator, we either remove any existing calculator
         * with the same name, or get a unique name from the calculator, depending
         * on the value of the overWrite argument.
         */
        handleCalculators(nodeFillColorNames, catalog, overWrite, nodeFillColorBaseKey);
        handleCalculators(nodeBorderColorNames, catalog, overWrite, nodeBorderColorBaseKey);
        handleCalculators(nodeLineTypeNames, catalog, overWrite, nodeLineTypeBaseKey);
        handleCalculators(nodeShapeNames, catalog, overWrite, nodeShapeBaseKey);
        handleCalculators(nodeUniformSizeNames, catalog, overWrite, nodeUniformSizeBaseKey);
        handleCalculators(nodeWidthNames, catalog, overWrite, nodeWidthBaseKey);
        handleCalculators(nodeHeightNames, catalog, overWrite, nodeHeightBaseKey);
        handleCalculators(nodeLabelNames, catalog, overWrite, nodeLabelBaseKey);
        handleCalculators(nodeToolTipNames, catalog, overWrite, nodeToolTipBaseKey);
        handleCalculators(nodeFontFaceNames, catalog, overWrite, nodeFontFaceBaseKey);
        handleCalculators(nodeFontSizeNames, catalog, overWrite, nodeFontSizeBaseKey);
        handleCalculators(edgeColorNames, catalog, overWrite, edgeColorBaseKey);
        handleCalculators(edgeLineTypeNames, catalog, overWrite, edgeLineTypeBaseKey);
        handleCalculators(edgeSourceArrowNames, catalog, overWrite, edgeSourceArrowBaseKey);
        handleCalculators(edgeTargetArrowNames, catalog, overWrite, edgeTargetArrowBaseKey);
        handleCalculators(edgeLabelNames, catalog, overWrite, edgeLabelBaseKey);
        handleCalculators(edgeToolTipNames, catalog, overWrite, edgeToolTipBaseKey);
        handleCalculators(edgeFontFaceNames, catalog, overWrite, edgeFontFaceBaseKey);
        handleCalculators(edgeFontSizeNames, catalog, overWrite, edgeFontSizeBaseKey);

        //Map structure to hold visual styles that we build here
        Map visualStyles = new HashMap();
        //now that all the individual calculators are loaded, load the
        //Node/Edge/Global appearance calculators
        for (Iterator si = nacNames.keySet().iterator(); si.hasNext(); ) {
            String name = (String)si.next();
            Properties nacProps = (Properties)nacNames.get(name);
            String baseKey = nodeAppearanceBaseKey + "." + name;
            NodeAppearanceCalculator nac =
                new NodeAppearanceCalculator(name, nacProps, baseKey, catalog);
            //store in the matching visual style, creating as needed
            VisualStyle vs = (VisualStyle)visualStyles.get(name);
            if (vs == null) {
                vs = new VisualStyle(name);
                visualStyles.put(name, vs);
            }
            vs.setNodeAppearanceCalculator(nac);
        }
        for (Iterator si = eacNames.keySet().iterator(); si.hasNext(); ) {
            String name = (String)si.next();
            Properties eacProps = (Properties)eacNames.get(name);
            String baseKey = edgeAppearanceBaseKey + "." + name;
            EdgeAppearanceCalculator eac =
                new EdgeAppearanceCalculator(name, eacProps, baseKey, catalog);
            //store in the matching visual style, creating as needed
            VisualStyle vs = (VisualStyle)visualStyles.get(name);
            if (vs == null) {
                vs = new VisualStyle(name);
                visualStyles.put(name, vs);
            }
            vs.setEdgeAppearanceCalculator(eac);
        }
        for (Iterator si = gacNames.keySet().iterator(); si.hasNext(); ) {
            String name = (String)si.next();
            Properties gacProps = (Properties)gacNames.get(name);
            String baseKey = globalAppearanceBaseKey + "." + name;
            GlobalAppearanceCalculator gac =
                new GlobalAppearanceCalculator(name, gacProps, baseKey, catalog);
            //store in the matching visual style, creating as needed
            VisualStyle vs = (VisualStyle)visualStyles.get(name);
            if (vs == null) {
                vs = new VisualStyle(name);
                visualStyles.put(name, vs);
            }
            vs.setGlobalAppearanceCalculator(gac);
        }
        
        //now store the visual styles in the catalog
        for (Iterator si = visualStyles.values().iterator(); si.hasNext(); ) {
            VisualStyle vs = (VisualStyle)si.next();
            catalog.addVisualStyle(vs);
        }
    }

    
    /**
     * The supplied Map m maps names to Properties objects that hold all the
     * properties entries associated with that name. Given a new key, this
     * method extracts the name field from the key, gets the matching
     * Properties object from the Map (creating a new map entry if needed)
     * and stores the (key, value) property pair in that Properties object).
     */
    private static void storeKey(String key, Properties props, Map m) {
        String name = extractName(key);
        if (name != null) {
            //get the entry for this name in the Map

	    // calcProps contains all of the properties for this calculator,
	    // e.g. the mappings, the controller, etc.
            Properties calcProps = (Properties)m.get(name);
            if (calcProps == null) {//create a new entry for this name
                calcProps = new Properties();
                m.put(name, calcProps);
            }
            calcProps.setProperty( key, props.getProperty(key) );
        }//should report parse errors if we can't get a name
    }

    
    /**
     * Given the key of a property entry, extract the second field (i.e., between the
     * first and second period) and return it.
     */
    private static String extractName(String key) {
        if (key == null) {return null;}
        //find index of first period character
        int dot1 = key.indexOf(".");
        //return null if not found, or found at end of string
        if (dot1 == -1 || dot1 >= key.length()-1) {return null;}
        //find the second period character
        int dot2 = key.indexOf(".", dot1+1);
        if (dot2 == -1) {return null;}//return null if not found
        //return substring between the periods
        return key.substring(dot1+1, dot2);
    }
    
    /**
     * Construct and store Calculators. Ensures that there will be no name
     * collision by either removing an existing duplicate or renaming the
     * new calculator as needed.
     */
    private static void handleCalculators(Map nameMap, CalculatorCatalog catalog,
                    boolean overWrite, String baseKey) {
	// for each calculator name
        for (Iterator si = nameMap.keySet().iterator(); si.hasNext(); ) {
            String name = (String)si.next();
	    // get the properties object that contains all info for
	    // that particular calculator
            Properties calcProps = (Properties)nameMap.get(name);
            String keyString = baseKey + "." + name;
	    // create a calculator based on the calculator name and type
            Calculator c = CalculatorFactory.newCalculator(name, calcProps, keyString);
            if (c!= null) {
                if (overWrite) {//remove any existing calculator of same name and type
                    removeDuplicate(c, catalog);
                } else {//ensure a unique name
                    renameAsNeeded(c, catalog);
                }
                catalog.addCalculator(c);
            }
        }
    }
    
    /**
     * Given a Calculator of a given type and a CalculatorCatalog, removes any
     * existing calculator of the same type and name.
     */
    public static void removeDuplicate(Calculator c, CalculatorCatalog catalog) {
 	catalog.removeCalculator(c);	
    }

    /**
     * Given a Calculator of a given type and a CalculatorCatalog, checks
     * for an existing catalog with the same name and type. If one exists,
     * gets a new unique name from the catalog and applied it to the
     * calculator argument.
     */
    public static void renameAsNeeded(Calculator c, CalculatorCatalog catalog) {
        String name = c.toString();
	String newName = catalog.checkCalculatorName(c.getType(),name);
        if (!newName.equals(name)) {c.setName(newName);}
    }

    /**
     * Used for updating calculator names from old style to new style. 
     * Only used in a few cases where the old and new don't align.
     */
    private static String updateLegacyKey(String key, Properties props, String oldKey, String newKey, String newClass ) {
    	String value = props.getProperty(key);
	key = key.replace(oldKey,newKey);
	if ( key.endsWith(".class") )
		props.setProperty(key,newClass);
	else
		props.setProperty(key,value);

	return key;
    }
}

