//----------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//----------------------------------------------------------------------------
package cytoscape.visual;
//----------------------------------------------------------------------------
import java.util.Properties;
import java.util.Enumeration;

import cytoscape.util.Misc;
import cytoscape.visual.calculators.*;
//----------------------------------------------------------------------------
/**
 * This class provides methods to read property keys in the old vizmap format,
 * construct suitable calculator objects, and install them in a supplied
 * CalculatorCatalog.
 *
 * All of the calculators will have the name of the static member variable
 * 'calcName', except that the borderColor calculator, the node height
 * calculator, and the targetDecoration calculator will have a '2' appended
 * to the name. (This is because in the old system, node.fillColor and
 * node.borderColor were separate types, but are both NodeColorCalculators
 * in the new system, and similarly with the node size and edge arrow calculators).
 *
 * In addition to the attribute specific calculators, this class will create
 * a NodeAppearance and EdgeAppearanceCalculator, also named 'calcName', and
 * set them to use the attribute calculators defined here.
 */
public class OldStyleCalculatorIO {
    
    public static String packageHeader = "cytoscape.visual.calculators.";
    public static String calcName = "oldFormat";

    
    public static void loadCalculators(Properties props, CalculatorCatalog catalog) {
        String colorInterpolator = "LinearNumberToColorInterpolator";
        String numberInterpolator = "LinearNumberToNumberInterpolator";
        String flatInterpolator = "FlatInterpolator";
        
        String nodeColorName = "nodeColorCalculator." + calcName;
        String nodeColorClass = packageHeader + "GenericNodeColorCalculator";
        String nodeColorIntClass = NodeColorCalculator.class.getName();
        loadCalculator(props, catalog, "node.fillColor", nodeColorName, nodeColorClass,
        nodeColorIntClass, colorInterpolator);
        String nodeColorName2 = nodeColorName + "2";
        loadCalculator(props, catalog, "node.borderColor", nodeColorName2,
        nodeColorClass, nodeColorIntClass, colorInterpolator);
        String nodeLineTypeName = "nodeLineTypeCalculator." + calcName;
        String nodeLineTypeClass = packageHeader + "GenericNodeLineTypeCalculator";
        String nodeLineTypeIntClass = NodeLineTypeCalculator.class.getName();
        loadCalculator(props, catalog, "node.borderLinetype", nodeLineTypeName,
        nodeLineTypeClass, nodeLineTypeIntClass, flatInterpolator);
        String nodeSizeName = "nodeSizeCalculator." + calcName;
        String nodeSizeClass = packageHeader + "GenericNodeSizeCalculator";
        String nodeSizeIntClass = NodeSizeCalculator.class.getName();
        loadCalculator(props, catalog, "node.width", nodeSizeName, nodeSizeClass,
        nodeSizeIntClass, numberInterpolator);
        String nodeSizeName2 = nodeSizeName + "2";
        loadCalculator(props, catalog, "node.height", nodeSizeName2, nodeSizeClass,
        nodeSizeIntClass, numberInterpolator);
        String nodeShapeName = "nodeShapeCalculator." + calcName;
        String nodeShapeClass = packageHeader + "GenericNodeShapeCalculator";
        String nodeShapeIntClass = NodeShapeCalculator.class.getName();
        loadCalculator(props, catalog, "node.shape", nodeShapeName, nodeShapeClass,
        nodeShapeIntClass, flatInterpolator);
        
        String edgeColorName = "edgeColorCalculator." + calcName;
        String edgeColorClass = packageHeader + "GenericEdgeColorCalculator";
        String edgeColorIntClass = EdgeColorCalculator.class.getName();
        loadCalculator(props, catalog, "edge.color", edgeColorName, edgeColorClass,
        edgeColorIntClass, colorInterpolator);
        String edgeLineTypeName = "edgeLineTypeCalculator." + calcName;
        String edgeLineTypeClass = packageHeader + "GenericEdgeLineTypeCalculator";
        String edgeLineTypeIntClass = EdgeLineTypeCalculator.class.getName();
        loadCalculator(props, catalog, "edge.linetype", edgeLineTypeName,
        edgeLineTypeClass, edgeLineTypeIntClass, flatInterpolator);
        String edgeSourceName = "edgeArrowCalculator." + calcName;
        String edgeArrowClass = packageHeader + "GenericEdgeArrowCalculator";
        String edgeArrowIntClass = EdgeArrowCalculator.class.getName();
        loadCalculator(props, catalog, "edge.sourceDecoration", edgeSourceName,
        edgeArrowClass, edgeArrowIntClass, flatInterpolator);
        String edgeTargetName = edgeSourceName + "2";
        loadCalculator(props, catalog, "edge.targetDecoration", edgeTargetName,
        edgeArrowClass, edgeArrowIntClass, flatInterpolator);
        
        loadNodeAppearanceCalculator(props, catalog);
        loadEdgeAppearanceCalculator(props, catalog);
    }
    
    private static void loadCalculator(Properties props, CalculatorCatalog catalog,
                                       String oldBaseKey, String newBaseKey,
                                       String className, String intClassName,
                                       String interpolator) {
        Properties newProps =
            getNewProperties(props, oldBaseKey, newBaseKey, className, interpolator);
        if (newProps == null) {return;}
        //hack to get the right name for calculators with '2' after their name
        String name = (newBaseKey.endsWith("2")) ? calcName + "2" : calcName;
        Calculator c = CalculatorFactory.newCalculator(name, newProps, newBaseKey,
                                                       intClassName);
        if (c != null) {
            //CalculatorIO.removeDuplicate(c, catalog);
            catalog.addCalculator(c);
        }
    }
    
    private static Properties getNewProperties(Properties props, String oldBaseKey,
    String newBaseKey, String className, String interpolator) {
        Properties newProps = null;
        String controller = props.getProperty(oldBaseKey + ".controller");
        if (controller != null) {
            String header = oldBaseKey + "." + controller;
            String type = props.getProperty(header + ".type");
            if (type == null) {
                //handle missing type error
                return null;
            } else if (type.equals("discrete")) {
                newProps = translateDiscrete(props, header, newBaseKey);
            } else if (type.equals("continuous")) {
                newProps = translateContinuous(props, header, newBaseKey);
                newProps.setProperty(newBaseKey + ".mapping.interpolator",interpolator);
            } else {
                //handle unknown type error
                return null;
            }
            newProps.setProperty(newBaseKey + ".class", className);
            newProps.setProperty(newBaseKey + ".mapping.controller", controller);
        }
        return newProps;
    }
    
    private static Properties translateDiscrete(Properties props, String header,
    String newBaseKey) {
        Properties newProps = new Properties();
        newProps.setProperty(newBaseKey + ".mapping.type", "DiscreteMapping");
        String mapKey = header + ".map.";
        Enumeration eProps = props.propertyNames();
        while(eProps.hasMoreElements()) {
            String key = (String)eProps.nextElement();
            if (key.startsWith(mapKey)) {
                String subKey = key.substring(mapKey.length());
                String newKey = newBaseKey + ".mapping.map." + subKey;
                String value = props.getProperty(key);
                newProps.setProperty(newKey, value);
            }
        }
        return newProps;
    }
    
    private static Properties translateContinuous(Properties props, String header,
    String newBaseKey) {
        Properties newProps = new Properties();
        newProps.setProperty(newBaseKey + ".mapping.type", "ContinuousMapping");
        String bvNumKey = header + ".boundaryvalues";
        String bvNumString = props.getProperty(bvNumKey);
        newProps.setProperty(newBaseKey + ".mapping.boundaryvalues", bvNumString);
        int numBV;
        try {
            numBV = Integer.parseInt(bvNumString);
        } catch (NumberFormatException e) {
            //handle bad number error
            return null;
        }
        for (int i=0; i<numBV; i++) {
            String bvBase = header + ".bv" + Integer.toString(i);
            String newBvBase = newBaseKey + ".mapping.bv" + Integer.toString(i);
            
            String dvKey = bvBase + ".domainvalue";
            String dvString = props.getProperty(dvKey);
            newProps.setProperty(newBvBase + ".domainvalue", dvString);
            
            String lString = props.getProperty(bvBase + ".lesser");
            newProps.setProperty(newBvBase + ".lesser", lString);
            String eString = props.getProperty(bvBase + ".equal");
            newProps.setProperty(newBvBase + ".equal", eString);
            String gString = props.getProperty(bvBase + ".greater");
            newProps.setProperty(newBvBase + ".greater", gString);
        }
        
        return newProps;
    }
        
    private static void loadNodeAppearanceCalculator(Properties props,
                                                     CalculatorCatalog catalog) {
        NodeAppearanceCalculator nac = new NodeAppearanceCalculator();
        
        String defaultNodeFillString = props.getProperty("node.fillColor.default");
        if (defaultNodeFillString != null) {
            nac.setDefaultNodeFillColor( Misc.parseRGBText(defaultNodeFillString) );
        }
        String defaultBorderColorString = props.getProperty("node.borderColor.default");
        if (defaultBorderColorString != null) {
            nac.setDefaultNodeBorderColor( Misc.parseRGBText(defaultBorderColorString) );
        }
        String defaultLineTypeString = props.getProperty("node.borderLinetype.default");
        if (defaultLineTypeString != null) {
            nac.setDefaultNodeLineType( Misc.parseLineTypeText(defaultLineTypeString) );
        }
        String defaultWidthString = props.getProperty("node.width.default");
        if (defaultWidthString != null) {
            try {
                double d = Double.parseDouble(defaultWidthString);
                nac.setDefaultNodeWidth(d);
            } catch (NumberFormatException e) {
            }
        }
        String defaultHeightString = props.getProperty("node.height.default");
        if (defaultHeightString != null) {
            try {
                double d = Double.parseDouble(defaultHeightString);
                nac.setDefaultNodeHeight(d);
            } catch (NumberFormatException e) {
            }
        }
        String defaultShapeString = props.getProperty("node.shape.default");
        if (defaultShapeString != null) {
            nac.setDefaultNodeShape( Misc.parseNodeShapeText(defaultShapeString) );
        }
        
        //note that null values from the catalog are acceptable
        nac.setNodeFillColorCalculator( catalog.getNodeColorCalculator(calcName) );
        nac.setNodeBorderColorCalculator( catalog.getNodeColorCalculator(calcName + "2"));
        nac.setNodeLineTypeCalculator( catalog.getNodeLineTypeCalculator(calcName) );
        nac.setNodeWidthCalculator( catalog.getNodeSizeCalculator(calcName) );
        nac.setNodeHeightCalculator( catalog.getNodeSizeCalculator(calcName + "2") );
        nac.setNodeShapeCalculator( catalog.getNodeShapeCalculator(calcName) );
        
        catalog.addNodeAppearanceCalculator(calcName, nac);
    }
    
    private static void loadEdgeAppearanceCalculator(Properties props,
                                                     CalculatorCatalog catalog) {
        EdgeAppearanceCalculator eac = new EdgeAppearanceCalculator();
        
        String defaultColorString = props.getProperty("edge.color.default");
        if (defaultColorString != null) {
            eac.setDefaultEdgeColor( Misc.parseRGBText(defaultColorString) );
        }
        String defaultLineTypeString = props.getProperty("edge.linetype.default");
        if (defaultLineTypeString != null) {
            eac.setDefaultEdgeLineType( Misc.parseLineTypeText(defaultLineTypeString) );
        }
        String defaultSourceString = props.getProperty("edge.sourceDecoration.default");
        if (defaultSourceString != null) {
            eac.setDefaultEdgeSourceArrow( Misc.parseArrowText(defaultSourceString) );
        }
        String defaultTargetString = props.getProperty("edge.targetDecoration.default");
        if (defaultTargetString != null) {
            eac.setDefaultEdgeTargetArrow( Misc.parseArrowText(defaultTargetString) );
        }
        
        eac.setEdgeColorCalculator( catalog.getEdgeColorCalculator(calcName) );
        eac.setEdgeLineTypeCalculator( catalog.getEdgeLineTypeCalculator(calcName) );
        eac.setEdgeSourceArrowCalculator( catalog.getEdgeArrowCalculator(calcName) );
        eac.setEdgeTargetArrowCalculator(catalog.getEdgeArrowCalculator(calcName + "2"));
        
        catalog.addEdgeAppearanceCalculator(calcName, eac);
    }
}

    
