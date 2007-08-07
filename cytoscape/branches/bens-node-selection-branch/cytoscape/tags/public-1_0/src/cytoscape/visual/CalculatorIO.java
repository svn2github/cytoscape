//----------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//----------------------------------------------------------------------------
package cytoscape.visual;
//----------------------------------------------------------------------------
import java.util.*;

import cytoscape.visual.calculators.*;
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
    public static final String nodeColorClassName = "NodeColorCalculator";
    public static final String nodeLineTypeBaseKey = "nodeLineTypeCalculator";
    public static final String nodeLineTypeClassName = "NodeLineTypeCalculator";
    public static final String nodeShapeBaseKey = "nodeShapeCalculator";
    public static final String nodeShapeClassName = "NodeShapeCalculator";
    public static final String nodeSizeBaseKey = "nodeSizeCalculator";
    public static final String nodeSizeClassName = "NodeSizeCalculator";
    public static final String nodeLabelBaseKey = "nodeLabelCalculator";
    public static final String nodeLabelClassName = "NodeLabelCalculator";
    public static final String nodeToolTipBaseKey = "nodeToolTipCalculator";
    public static final String nodeToolTipClassName = "NodeToolTipCalculator";
    public static final String nodeFontFaceBaseKey = "nodeFontFaceCalculator";
    public static final String nodeFontFaceClassName = "NodeFontFaceCalculator";
    public static final String nodeFontSizeBaseKey = "nodeFontSizeCalculator";
    public static final String nodeFontSizeClassName = "NodeFontSizeCalculator";
    public static final String edgeColorBaseKey = "edgeColorCalculator";
    public static final String edgeColorClassName = "EdgeColorCalculator";
    public static final String edgeLineTypeBaseKey = "edgeLineTypeCalculator";
    public static final String edgeLineTypeClassName = "EdgeLineTypeCalculator";
    public static final String edgeArrowBaseKey = "edgeArrowCalculator";
    public static final String edgeArrowClassName = "EdgeArrowCalculator";
    public static final String edgeLabelBaseKey = "edgeLabelCalculator";
    public static final String edgeLabelClassName = "EdgeLabelCalculator";
    public static final String edgeToolTipBaseKey = "edgeToolTipCalculator";
    public static final String edgeToolTipClassName = "EdgeToolTipCalculator";
    public static final String edgeFontFaceBaseKey = "edgeFontFaceCalculator";
    public static final String edgeFontFaceClassName = "EdgeFontFaceCalculator";
    public static final String edgeFontSizeBaseKey = "edgeFontSizeCalculator";
    public static final String edgeFontSizeClassName = "EdgeFontSizeCalculator";
    
    public static final String nodeAppearanceBaseKey = "nodeAppearanceCalculator";
    public static final String edgeAppearanceBaseKey = "edgeAppearanceCalculator";
    

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
        Map nodeColorNames = new HashMap();
        Map nodeLineTypeNames = new HashMap();
        Map nodeShapeNames = new HashMap();
        Map nodeSizeNames = new HashMap();
        Map nodeLabelNames = new HashMap();
        Map nodeToolTipNames = new HashMap();
        Map nodeFontFaceNames = new HashMap();
        Map nodeFontSizeNames = new HashMap();
        Map edgeColorNames = new HashMap();
        Map edgeLineTypeNames = new HashMap();
        Map edgeArrowNames = new HashMap();
        Map edgeLabelNames = new HashMap();
        Map edgeToolTipNames = new HashMap();
        Map edgeFontFaceNames = new HashMap();
        Map edgeFontSizeNames = new HashMap();
        Map nacNames = new HashMap();
        Map eacNames = new HashMap();
        //use the propertyNames() method instead of the generic Map iterator,
        //because the former method recognizes layered properties objects.
        //see the Properties javadoc for details
        for (Enumeration eI = props.propertyNames(); eI.hasMoreElements(); ) {
            String key = (String)eI.nextElement();
            if (key.startsWith(nodeColorBaseKey + ".")) {
                storeKey(key, props, nodeColorNames);
            } else if (key.startsWith(nodeLineTypeBaseKey + ".")) {
                storeKey(key, props, nodeLineTypeNames);
            } else if (key.startsWith(nodeShapeBaseKey + ".")) {
                storeKey(key, props, nodeShapeNames);
            } else if (key.startsWith(nodeSizeBaseKey + ".")) {
                storeKey(key, props, nodeSizeNames);
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
                storeKey(key, props, edgeArrowNames);
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
            }
        }
        
        /* Now that we have all the properties in groups, we pass each Map of
         * names and Properties objects to a helper function that creates a
         * calculator for each entry and stores the calculators in the catalog.
         * Before storing the calculator, we either remove any existing calculator
         * with the same name, or get a unique name from the calculator, depending
         * on the value of the overWrite argument.
         */
        handleCalculators(nodeColorNames, catalog, overWrite, nodeColorBaseKey,
                          nodeColorClassName);
        handleCalculators(nodeLineTypeNames, catalog, overWrite, nodeLineTypeBaseKey,
                          nodeLineTypeClassName);
        handleCalculators(nodeShapeNames, catalog, overWrite, nodeShapeBaseKey,
                          nodeShapeClassName);
        handleCalculators(nodeSizeNames, catalog, overWrite, nodeSizeBaseKey,
                          nodeSizeClassName);
        handleCalculators(nodeLabelNames, catalog, overWrite, nodeLabelBaseKey,
                          nodeLabelClassName);
        handleCalculators(nodeToolTipNames, catalog, overWrite, nodeToolTipBaseKey,
                          nodeToolTipClassName);
        handleCalculators(nodeFontFaceNames, catalog, overWrite, nodeFontFaceBaseKey,
                          nodeFontFaceClassName);
        handleCalculators(nodeFontSizeNames, catalog, overWrite, nodeFontSizeBaseKey,
                          nodeFontSizeClassName);
        handleCalculators(edgeColorNames, catalog, overWrite, edgeColorBaseKey,
                          edgeColorClassName);
        handleCalculators(edgeLineTypeNames, catalog, overWrite, edgeLineTypeBaseKey,
                          edgeLineTypeClassName);
        handleCalculators(edgeArrowNames, catalog, overWrite, edgeArrowBaseKey,
                          edgeArrowClassName);
        handleCalculators(edgeLabelNames, catalog, overWrite, edgeLabelBaseKey,
                          edgeLabelClassName);
        handleCalculators(edgeToolTipNames, catalog, overWrite, edgeToolTipBaseKey,
                          edgeToolTipClassName);
        handleCalculators(edgeFontFaceNames, catalog, overWrite, edgeFontFaceBaseKey,
                          edgeFontFaceClassName);
        handleCalculators(edgeFontSizeNames, catalog, overWrite, edgeFontSizeBaseKey,
                          edgeFontSizeClassName);

        //now that all the individual calculators are loaded, load the
        //Node and Edge appearance calculators
        for (Iterator si = nacNames.keySet().iterator(); si.hasNext(); ) {
            String name = (String)si.next();
            Properties nacProps = (Properties)nacNames.get(name);
            String baseKey = nodeAppearanceBaseKey + "." + name;
            NodeAppearanceCalculator nac =
                new NodeAppearanceCalculator(name, nacProps, baseKey, catalog);
            catalog.removeNodeAppearanceCalculator(name);
            catalog.addNodeAppearanceCalculator(name, nac);
        }
        for (Iterator si = eacNames.keySet().iterator(); si.hasNext(); ) {
            String name = (String)si.next();
            Properties eacProps = (Properties)eacNames.get(name);
            String baseKey = edgeAppearanceBaseKey + "." + name;
            EdgeAppearanceCalculator eac =
                new EdgeAppearanceCalculator(name, eacProps, baseKey, catalog);
            catalog.removeEdgeAppearanceCalculator(name);
            catalog.addEdgeAppearanceCalculator(name, eac);
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
        addProperties(newProps, catalog.getNodeColorCalculators(), nodeColorBaseKey);
        addProperties(newProps, catalog.getNodeLineTypeCalculators(), nodeLineTypeBaseKey);
        addProperties(newProps, catalog.getNodeShapeCalculators(), nodeShapeBaseKey);
        addProperties(newProps, catalog.getNodeSizeCalculators(), nodeSizeBaseKey);
        addProperties(newProps, catalog.getNodeLabelCalculators(), nodeLabelBaseKey);
        addProperties(newProps, catalog.getNodeToolTipCalculators(), nodeToolTipBaseKey);
        addProperties(newProps, catalog.getNodeFontFaceCalculators(), nodeFontFaceBaseKey);
        addProperties(newProps, catalog.getNodeFontSizeCalculators(), nodeFontSizeBaseKey);
        //gather properties for edge calculators
        addProperties(newProps, catalog.getEdgeColorCalculators(), edgeColorBaseKey);
        addProperties(newProps, catalog.getEdgeLineTypeCalculators(), edgeLineTypeBaseKey);
        addProperties(newProps, catalog.getEdgeArrowCalculators(), edgeArrowBaseKey);
        addProperties(newProps, catalog.getEdgeLabelCalculators(), edgeLabelBaseKey);
        addProperties(newProps, catalog.getEdgeToolTipCalculators(), edgeToolTipBaseKey);
        addProperties(newProps, catalog.getEdgeFontFaceCalculators(), edgeFontFaceBaseKey);
        addProperties(newProps, catalog.getEdgeFontSizeCalculators(), edgeFontSizeBaseKey);
        
        //node appearance calculators
        Collection nacNames = catalog.getNodeAppearanceCalculatorNames();
        for (Iterator i = nacNames.iterator(); i.hasNext(); ) {
            String name = (String)i.next();
            NodeAppearanceCalculator nac = catalog.getNodeAppearanceCalculator(name);
            String baseKey = nodeAppearanceBaseKey + "." + name;
            Properties props = nac.getProperties(baseKey);
            newProps.putAll(props);
        }
        Collection eacNames = catalog.getEdgeAppearanceCalculatorNames();
        for (Iterator i = eacNames.iterator(); i.hasNext(); ) {
            String name = (String)i.next();
            EdgeAppearanceCalculator eac = catalog.getEdgeAppearanceCalculator(name);
            String baseKey = edgeAppearanceBaseKey + "." + name;
            Properties props = eac.getProperties(baseKey);
            newProps.putAll(props);
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
            String calcBaseKey = baseKey + "." + c.toString();
            Properties props = CalculatorFactory.getProperties(c, calcBaseKey);
            newProps.putAll(props);
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
                    boolean overWrite, String baseKey, String className) {
        for (Iterator si = nameMap.keySet().iterator(); si.hasNext(); ) {
            String name = (String)si.next();
            Properties calcProps = (Properties)nameMap.get(name);
            String keyString = baseKey + "." + name;
            String intClassName = dirHeader + className;
            Calculator c = CalculatorFactory.newCalculator(name, calcProps, keyString,
                                                           intClassName);
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
    private static void removeDuplicate(Calculator c, CalculatorCatalog catalog) {
        String name = c.toString();
        if (c instanceof NodeColorCalculator) {
            catalog.removeNodeColorCalculator(name);
        } else if (c instanceof NodeLineTypeCalculator) {
            catalog.removeNodeLineTypeCalculator(name);
        } else if (c instanceof NodeShapeCalculator) {
            catalog.removeNodeShapeCalculator(name);
        } else if (c instanceof NodeSizeCalculator) {
            catalog.removeNodeSizeCalculator(name);
        } else if (c instanceof NodeLabelCalculator) {
            catalog.removeNodeLabelCalculator(name);
        } else if (c instanceof NodeToolTipCalculator) {
            catalog.removeNodeToolTipCalculator(name);
        } else if (c instanceof NodeFontFaceCalculator) {
            catalog.removeNodeFontFaceCalculator(name);
        } else if (c instanceof NodeFontSizeCalculator) {
            catalog.removeNodeFontSizeCalculator(name);
        } else if (c instanceof EdgeColorCalculator) {
            catalog.removeEdgeColorCalculator(name);
        } else if (c instanceof EdgeLineTypeCalculator) {
            catalog.removeEdgeLineTypeCalculator(name);
        } else if (c instanceof EdgeArrowCalculator) {
            catalog.removeEdgeArrowCalculator(name);
        } else if (c instanceof EdgeLabelCalculator) {
            catalog.removeEdgeLabelCalculator(name);
        } else if (c instanceof EdgeToolTipCalculator) {
            catalog.removeEdgeToolTipCalculator(name);
        } else if (c instanceof EdgeFontFaceCalculator) {
            catalog.removeEdgeFontFaceCalculator(name);
        } else if (c instanceof EdgeFontSizeCalculator) {
            catalog.removeEdgeFontSizeCalculator(name);
        }
    }
    /**
     * Given a Calculator of a given type and a CalculatorCatalog, checks
     * for an existing catalog with the same name and type. If one exists,
     * gets a new unique name from the catalog and applied it to the
     * calculator argument.
     */
    private static void renameAsNeeded(Calculator c, CalculatorCatalog catalog) {
        String name = c.toString();
        String newName;
        if (c instanceof NodeColorCalculator) {
            newName = catalog.checkNodeColorCalculatorName(name);
            if (!newName.equals(name)) {c.setName(newName);}
        } else if (c instanceof NodeLineTypeCalculator) {
            newName = catalog.checkNodeLineTypeCalculatorName(name);
            if (!newName.equals(name)) {c.setName(newName);}
        } else if (c instanceof NodeShapeCalculator) {
            newName = catalog.checkNodeShapeCalculatorName(name);
            if (!newName.equals(name)) {c.setName(newName);}
        } else if (c instanceof NodeSizeCalculator) {
            newName = catalog.checkNodeSizeCalculatorName(name);
            if (!newName.equals(name)) {c.setName(newName);}
        } else if (c instanceof NodeLabelCalculator) {
            newName = catalog.checkNodeLabelCalculatorName(name);
            if (!newName.equals(name)) {c.setName(newName);}
        } else if (c instanceof NodeToolTipCalculator) {
            newName = catalog.checkNodeToolTipCalculatorName(name);
            if (!newName.equals(name)) {c.setName(newName);}
        } else if (c instanceof NodeFontFaceCalculator) {
            newName = catalog.checkNodeFontFaceCalculatorName(name);
            if (!newName.equals(name)) {c.setName(newName);}
        } else if (c instanceof NodeFontSizeCalculator) {
            newName = catalog.checkNodeFontSizeCalculatorName(name);
            if (!newName.equals(name)) {c.setName(newName);}
        } else if (c instanceof EdgeColorCalculator) {
            newName = catalog.checkEdgeColorCalculatorName(name);
            if (!newName.equals(name)) {c.setName(newName);}
        } else if (c instanceof EdgeLineTypeCalculator) {
            newName = catalog.checkEdgeLineTypeCalculatorName(name);
            if (!newName.equals(name)) {c.setName(newName);}
        } else if (c instanceof EdgeArrowCalculator) {
            newName = catalog.checkEdgeArrowCalculatorName(name);
            if (!newName.equals(name)) {c.setName(newName);}
        } else if (c instanceof EdgeLabelCalculator) {
            newName = catalog.checkEdgeLabelCalculatorName(name);
            if (!newName.equals(name)) {c.setName(newName);}
        } else if (c instanceof EdgeToolTipCalculator) {
            newName = catalog.checkEdgeToolTipCalculatorName(name);
            if (!newName.equals(name)) {c.setName(newName);}
        } else if (c instanceof EdgeFontFaceCalculator) {
            newName = catalog.checkEdgeFontFaceCalculatorName(name);
            if (!newName.equals(name)) {c.setName(newName);}
        } else if (c instanceof EdgeFontSizeCalculator) {
            newName = catalog.checkEdgeFontSizeCalculatorName(name);
            if (!newName.equals(name)) {c.setName(newName);}
        }
    }
}

