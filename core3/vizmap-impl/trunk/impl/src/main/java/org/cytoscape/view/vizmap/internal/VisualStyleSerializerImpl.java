/*
 Copyright (c) 2010, The Cytoscape Consortium (www.cytoscape.org)

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
package org.cytoscape.view.vizmap.internal;

import java.awt.Color;
import java.awt.Font;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTableEntry;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.presentation.RenderingEngineManager;
import org.cytoscape.view.presentation.property.TwoDVisualLexicon;
import org.cytoscape.view.vizmap.VisualMappingFunction;
import org.cytoscape.view.vizmap.VisualMappingFunctionFactory;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.view.vizmap.VisualStyleFactory;
import org.cytoscape.view.vizmap.VisualStyleSerializer;
import org.cytoscape.view.vizmap.mappings.BoundaryRangeValues;
import org.cytoscape.view.vizmap.mappings.ContinuousMapping;
import org.cytoscape.view.vizmap.mappings.DiscreteMapping;
import org.cytoscape.view.vizmap.mappings.PassthroughMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VisualStyleSerializerImpl implements VisualStyleSerializer {

    private final VisualStyleFactory visualStyleFactory;
    private final VisualMappingManager visualMappingManager;
    private final VisualMappingFunctionFactory discreteMappingFactory;
    private final VisualMappingFunctionFactory continuousMappingFactory;
    private final VisualMappingFunctionFactory passthroughMappingFactory;
    private final RenderingEngineManager renderingEngineManager;

    private static final String[] OLD_CALC_KEYS;

    private static final Logger logger = LoggerFactory.getLogger(VisualStyleSerializerImpl.class);

    static {
        OLD_CALC_KEYS = new String[] { "EDGELINETYPE", "NODELINETYPE" };
        // TODO
        //		OLD_CALC_KEYS.add(VisualProperty.DEF_EDGE_SRCARROW.toUpperCase() + "=");
        //		OLD_CALC_KEYS.add(VisualProperty.DEF_EDGE_TGTARROW.toUpperCase() + "=");
    }

    public VisualStyleSerializerImpl(VisualStyleFactory visualStyleFactory,
                                     VisualMappingManager visualMappingManager,
                                     VisualMappingFunctionFactory discreteMappingFactory,
                                     VisualMappingFunctionFactory continuousMappingFactory,
                                     VisualMappingFunctionFactory passthroughMappingFactory,
                                     RenderingEngineManager renderingEngineManager) {
        this.visualStyleFactory = visualStyleFactory;
        this.visualMappingManager = visualMappingManager;
        this.discreteMappingFactory = discreteMappingFactory;
        this.continuousMappingFactory = continuousMappingFactory;
        this.passthroughMappingFactory = passthroughMappingFactory;
        this.renderingEngineManager = renderingEngineManager;
    }

    public Properties createProperties(Collection<VisualStyle> styles) {
        // TODO uhhh, implement something here! 
        return new Properties();
    }

    public Collection<VisualStyle> createVisualStyles(Properties props) {
        Set<VisualStyle> styles = new HashSet<VisualStyle>();
        VisualLexicon lexicon = renderingEngineManager.getDefaultVisualLexicon();
        VisualStyle defaultStyle = visualMappingManager.getDefaultVisualStyle();

        if (lexicon == null) {
            // TODO: warning
            return styles;
        }

        if (props != null) {
            // Group properties keys/values by visual style name:
            Map<String, Map<String, String>> styleNamesMap = new Hashtable<String, Map<String, String>>();

            for (String key : props.stringPropertyNames()) {
                String value = props.getProperty(key);
                String styleName = getStyleName(key);

                if (styleName != null) {
                    // Add each style name and its properties to a map
                    Map<String, String> keyValueMap = styleNamesMap.get(styleName);

                    if (keyValueMap == null) {
                        keyValueMap = new Hashtable<String, String>();
                        styleNamesMap.put(styleName, keyValueMap);
                    }

                    keyValueMap.put(key, value);
                }
            }

            // Create a Visual Style for each style name:
            for (Entry<String, Map<String, String>> entry : styleNamesMap.entrySet()) {
                String styleName = entry.getKey();
                // Each new style should be created from the default one:
                VisualStyle vs = null;

                if (styleName.equals(VisualMappingManagerImpl.DEFAULT_STYLE_NAME)) {
                    // If loading the default style, do not create another one,
                    // but just modify the current default object!
                    vs = defaultStyle;
                    // TODO: delete mappings?
                } else {
                    vs = visualStyleFactory.getInstance(defaultStyle);
                    vs.setTitle(styleName);
                }

                // Create and set the visual properties and mappings:
                Map<String, String> vsProps = entry.getValue();

                for (Entry<String, String> p : vsProps.entrySet()) {
                    String key = p.getKey();
                    String value = p.getValue();

                    if (isDefaultProperty(key)) {
                        // e.g. "globalAppearanceCalculator.MyStyle.defaultBackgroundColor"
                        setDefaultProperty(lexicon, vs, key, value);
                    } else if (isMappingFunction(key)) {
                        // e.g. "edgeAppearanceCalculator.MyStyle.edgeColorCalculator"
                        setMappingFunction(lexicon, vs, key, value, props);
                    } else if (isDependency(key)) {
                        setDependency(lexicon, vs, key, value);
                    }
                }

                // Do not add the modified default style to the list!
                if (!vs.equals(defaultStyle)) styles.add(vs);
            }
        }

        //        // The supplied Properties object may contain any kinds of properties.
        //        // We look for keys that start with a name we recognize, identifying a
        //        // particular type of calculator.
        //        // The second field of the key should then be an identifying name. For example:
        //        // nodeFillColorCalculator.mySpecialCalculator.{anything else}
        //        //
        //        // We begin by creating a map of calculator types
        //        // (nodeFillColorCalculator) to a map of names (mySpecialCalculator) to
        //        // properties. Note that this will create maps for _any_ "calculator"
        //        // that appears, even if it isn't a Calculator. This is OK, because the
        //        // CalculatorFactory won't create anything that isn't actually a
        //        // Calculator.
        //        //
        //        // Note that we need to separate constructs for each type of calculator,
        //        // because calculators of different types are allowed to share the same name.
        //        final Map<String, Map<String, Properties>> calcNames = new HashMap<String, Map<String, Properties>>();
        //
        //        // use the propertyNames() method instead of the generic Map iterator,
        //        // because the former method recognizes layered properties objects.
        //        // see the Properties javadoc for details
        //        String key = null;
        //
        //        for (Enumeration<?> eI = props.propertyNames(); eI.hasMoreElements();) {
        //            key = (String) eI.nextElement();
        //
        //            // handle legacy names In these cases the old calculator base key
        //            // was applicable to more than one calculator. In the new system
        //            // it's one key to one calculator, so we simply apply the old
        //            // calculator to all of the new types of calculators that the old
        //            // calculator mapped to.

        return styles;
    }

    /**
     * @param key the Properties key
     * @return The name of the visual style or null if the property key doesn't or shouldn't have it
     */
    protected static String getStyleName(String key) {
        String styleName = null;

        if (key != null) {
            String[] tokens = key.split("\\.");

            if (tokens.length > 2 && tokens[0].matches("(node|edge|global)[a-zA-Z]+Calculator")) {
                // It seems to be a valid entry...
                if (tokens.length == 3) {
                    String t3 = tokens[2];

                    if (t3.matches("nodeSizeLocked|arrowColorMatchesEdge|nodeLabelColorFromNodeColor|"
                                   + "defaultNodeShowNestedNetwork|nodeCustomGraphicsSizeSync|"
                                   + "((node|edge)[a-zA-Z]+Calculator)|"
                                   + "(default(Node|Edge|Background|SloppySelection)[a-zA-Z0-9]+)")) {
                        // It looks like the second token is the style name!
                        styleName = tokens[1];
                    }
                }
            }
        }

        return styleName;
    }

    protected static boolean isDefaultProperty(String key) {
        boolean b = false;

        if (key != null) {
            // Globals
            b |= key.matches("globalAppearanceCalculator\\.[^\\.]+\\.default[a-zA-Z]+Color");
            // Nodes & Edges
            b |= key.matches("nodeAppearanceCalculator\\.[^\\.]+\\.defaultNode\\w+");
            // Edges
            b |= key.matches("edgeAppearanceCalculator\\.[^\\.]+\\.defaultEdge[a-zA-Z]+");
            // exceptions
            b &= !key.contains("defaultNodeShowNestedNetwork");
        }

        return b;
    }

    protected static boolean isMappingFunction(String key) {
        boolean b = false;

        if (key != null) {
            b |= key.matches("(node|edge)AppearanceCalculator\\.[^\\.]+\\."
                             + "\\1((CustomGraphics(Position)?\\d+)|([a-zA-Z]+Calculator))");
        }

        return b;
    }

    protected static boolean isDependency(String key) {
        boolean b = false;

        if (key != null) {
            b |= key
                    .matches("nodeAppearanceCalculator\\.[^\\.]+\\."
                             + "(nodeSizeLocked|nodeLabelColorFromNodeColor|defaultNodeShowNestedNetwork|nodeCustomGraphicsSizeSync)");
            b |= key.matches("edgeAppearanceCalculator\\.[^\\.]+\\.arrowColorMatchesEdge");
        }

        return b;
    }

    private void setDefaultProperty(VisualLexicon lexicon, VisualStyle vs, String key, String sValue) {
        String calcKey = key.split("\\.")[2];
        CalcConverter<?> conv = CalcConverter.getConverter(calcKey);

        if (conv != null) {
            Class<? extends CyTableEntry> dataType = conv.getTargetType();
            String vpKey = conv.getVisualPropertyId();
            VisualProperty vp = lexicon.lookup(dataType, vpKey);

            if (vp != null) {
                Object value = conv.getValue(sValue);
                if (value != null) vs.setDefaultValue(vp, value);
            }
        }
    }

    private void setMappingFunction(VisualLexicon lexicon, VisualStyle vs, String key, String value, Properties props) {
        String calcKey = key.split("\\.")[2];
        CalcConverter<?> conv = CalcConverter.getConverter(calcKey);

        if (conv != null) {
            Class<? extends CyTableEntry> dataType = conv.getTargetType();
            String vpId = conv.getVisualPropertyId();
            VisualProperty vp = lexicon.lookup(dataType, vpId);

            if (vp != null) {
                // TODO: get mapping from converter and add it to the visual style
                VisualMappingFunction mapping = conv.getMappingFunction(props,
                                                                        value,
                                                                        vp,
                                                                        discreteMappingFactory,
                                                                        continuousMappingFactory,
                                                                        passthroughMappingFactory);

                if (mapping != null) vs.addVisualMappingFunction(mapping);
            }
        }
    }

    private void setDependency(VisualLexicon lexicon, VisualStyle vs, String calcKey, String value) {
        if (calcKey.contains(".nodeSizeLocked")) {
            boolean b = Boolean.parseBoolean(value);
            lexicon.getVisualLexiconNode(TwoDVisualLexicon.NODE_X_SIZE).setDependency(b);
            lexicon.getVisualLexiconNode(TwoDVisualLexicon.NODE_Y_SIZE).setDependency(b);
        }
    }

    /**
     * Used for updating calculator names from old style to new style. Only used
     * in a few cases where the old and new don't align.
     */
    private static String updateLegacyKey(String key, Properties props, String oldKey, String newKey, String newClass) {
        String value = props.getProperty(key);

        // Update arrow
        //        if ((key.endsWith("equal") || key.endsWith("greater") || key.endsWith("lesser"))
        //                && (key.startsWith(DEF_EDGE_TGTARROW + ".") || key.startsWith(DEF_EDGE_SRCARROW + "."))) {
        //            value = parseArrowText(value).getShape().toString();
        //        }
        //
        //        if (key.endsWith(DEF_EDGE_TGTARROW) || key.endsWith(DEF_EDGE_SRCARROW)) {
        //            value = parseArrowText(value).getShape().toString();
        //        }

        key = key.replace(oldKey, newKey);

        if (key.endsWith(".class"))
            props.setProperty(key, newClass);
        else
            props.setProperty(key, value);

        return key;
    }

}

// Other classes ---------------------------
// TODO: refactor and make it public
class CalcConverter<T> {

    public final byte TYPE_BOOLEAN = 1;
    public final byte TYPE_FLOATING_POINT = 2;
    public final byte TYPE_INTEGER = 3;
    public final byte TYPE_STRING = 4;

    private static final Map<Class<? extends CyTableEntry>, Map<String, CalcConverter<?>>> converters;

    public String key;
    public String visualPropertyId;
    public Class<T> type;
    public Class<? extends CyTableEntry> targetType;

    static {
        converters = new Hashtable<Class<? extends CyTableEntry>, Map<String, CalcConverter<?>>>();
        converters.put(CyNode.class, new Hashtable<String, CalcConverter<?>>());
        converters.put(CyEdge.class, new Hashtable<String, CalcConverter<?>>());
        converters.put(CyNetwork.class, new Hashtable<String, CalcConverter<?>>());
    }

    private CalcConverter(String calcKey, Class<T> type, Class<? extends CyTableEntry> targetType) {
        this.key = calcKey;
        this.visualPropertyId = getVisualPropertyId(calcKey);
        this.type = type;
        this.targetType = targetType;
    }

    /**
     * @param calcKey The calculator identifier (e.g. "edgeColorCalculator" or "defaultEdgeColor").
     * @return
     */
    public static CalcConverter<?> getConverter(String calcKey) {
        // TODO: maybe create a CalcConverterFactory
        CalcConverter<?> conv = null;

        Class<? extends CyTableEntry> targetType = getTargetDataType(calcKey);
        Map<String, CalcConverter<?>> map = converters.get(targetType);

        if (map != null) {
            // Is there a cached converter for this key?
            conv = map.get(calcKey);

            if (conv == null) {
                // Create and cache one...
                Class<?> type = getPropertyType(calcKey);

                if (type == Integer.class)
                    conv = new CalcConverter<Integer>(calcKey, Integer.class, targetType);
                else if (type == Double.class)
                    conv = new CalcConverter<Double>(calcKey, Double.class, targetType);
                else if (type == String.class)
                    conv = new CalcConverter<String>(calcKey, String.class, targetType);
                else if (type == Font.class)
                    conv = new CalcConverter<Font>(calcKey, Font.class, targetType);
                else if (type == Color.class)
                    conv = new CalcConverter<Color>(calcKey, Color.class, targetType);
                else
                    conv = new CalcConverter<Object>(calcKey, Object.class, targetType);

                map.put(calcKey, conv);
            }
        }

        return conv;
    }

    /**
     * @param sValue The string value to be parsed
     * @return The visual property value
     */
    public T getValue(String sValue) {
        Object value = null;

        if (type == Integer.class) {
            try {
                value = sValue == null ? 0 : (new Double(sValue)).intValue();
            } catch (NumberFormatException nfe) {
                // TODO: log/warning
            }
        } else if (type == Double.class) {
            try {
                value = sValue == null ? 0.0 : (new Double(sValue)).doubleValue();
            } catch (NumberFormatException nfe) {
                // TODO: log/warning
            }
        } else if (type == Color.class) {
            // Color (e.g. "255,255,255")
            String[] rgb = sValue.split(",");

            if (rgb.length == 3) {
                try {
                    value = new Color(Integer.parseInt(rgb[0]), Integer.parseInt(rgb[1]), Integer.parseInt(rgb[2]));
                } catch (NumberFormatException nfe) {
                    // TODO: log/warning
                }
            }
        } else if (type == Font.class) {
            // e.g. nodeLabelFont="Dialog.bold,plain,12"
            if (sValue != null) {
                String name = sValue.replaceAll("(\\.[bB]old)?,[a-zA-Z]+,\\d+(\\.\\d+)?", "");

                boolean bold = sValue.matches("(?i).*\\.bold,[a-zA-Z]+,.*");
                int style = bold ? Font.BOLD : Font.PLAIN;
                int size = 12;

                String sSize = sValue.replaceAll(".+,[^,]+,", "");
                try {
                    size = Integer.parseInt(sSize);
                } catch (NumberFormatException nfe) {
                    // TODO: log/warning
                }

                value = new Font(name, style, size);
            }
        } else if (type == String.class) {
            value = sValue;
        }

        return (T) value;
    }

    /**
     * @param props All the visual properties
     * @param mapperName Example: "MyStyle-Edge Color-Discrete Mapper"
     * @return
     */
    public <K> VisualMappingFunction<K, T> getMappingFunction(Properties props,
                                                              String mapperName,
                                                              VisualProperty<T> vp,
                                                              VisualMappingFunctionFactory discreteMappingFactory,
                                                              VisualMappingFunctionFactory continuousMappingFactory,
                                                              VisualMappingFunctionFactory passthroughMappingFactory) {
        // e.g. "edgeColorCalculator.MyStyle-Edge Color-Discrete Mapper"
        String baseKey = key + "." + mapperName + ".mapping.";

        String functionType = props.getProperty(baseKey + "type");
        String attributeName = props.getProperty(baseKey + "controller");

        // "ID" is actually the "name" column!!!
        if ("ID".equalsIgnoreCase(attributeName)) attributeName = "name";

        if ("DiscreteMapping".equalsIgnoreCase(functionType)) {
            byte controllerType = Byte.parseByte(props.getProperty(baseKey + "controllerType"));
            Class<?> attrValueType = String.class;

            switch (controllerType) {
                case TYPE_BOOLEAN:
                    attrValueType = Boolean.class;
                    break;
                case TYPE_FLOATING_POINT:
                    attrValueType = Double.class;
                    break;
                case TYPE_INTEGER:
                    attrValueType = Integer.class;
                    break;
                default:
                    attrValueType = String.class;
                    break;
            }

            DiscreteMapping<K, T> dm = (DiscreteMapping<K, T>) discreteMappingFactory
                    .createVisualMappingFunction(attributeName, attrValueType, vp);

            String entryKey = baseKey + "map.";

            for (String sk : props.stringPropertyNames()) {
                if (sk.contains(entryKey)) {
                    String sv = props.getProperty(sk);
                    sk = sk.replaceAll(entryKey, "");

                    T vpValue = getValue(sv);
                    Object dataValue = sk;

                    switch (controllerType) {
                        case TYPE_BOOLEAN:
                            dataValue = Boolean.parseBoolean(sk);
                            break;
                        case TYPE_FLOATING_POINT:
                            dataValue = Double.parseDouble(sk);
                            break;
                        case TYPE_INTEGER:
                            dataValue = Integer.parseInt(sk);
                            break;
                        default:
                            break;
                    }

                    dm.putMapValue((K) dataValue, vpValue);
                }
            }

            return dm;
        } else if ("ContinuousMapping".equalsIgnoreCase(functionType)) {
            String interpolator = props.getProperty(baseKey + "interpolator");
            boolean isColor = "LinearNumberToColorInterpolator".equalsIgnoreCase(interpolator);

            ContinuousMapping<K, T> cm = (ContinuousMapping<K, T>) continuousMappingFactory
                    .createVisualMappingFunction(attributeName, Number.class, vp);

            int boundaryValues = 0;

            try {
                String s = props.getProperty(baseKey + "boundaryvalues");
                boundaryValues = Integer.parseInt(s);
            } catch (NumberFormatException nfe) {
                // TODO: warning
            }

            for (int i = 0; i < boundaryValues; i++) {
                try {
                    T lesser = getValue(props.getProperty(baseKey + "bv" + i + ".lesser"));
                    T equal = getValue(props.getProperty(baseKey + "bv" + i + ".equal"));
                    T greater = getValue(props.getProperty(baseKey + "bv" + i + ".greater"));

                    BoundaryRangeValues<T> brv = new BoundaryRangeValues<T>(lesser, equal, greater);
                    Object value = Double.parseDouble(props.getProperty(baseKey + "bv" + i + ".domainvalue"));

                    cm.addPoint((K) value, brv);
                } catch (Exception e) {
                    // TODO: warning
                }
            }

            return cm;
        } else if ("PassThroughMapping".equalsIgnoreCase(functionType)) {
            PassthroughMapping<K, T> pm = (PassthroughMapping<K, T>) passthroughMappingFactory
                    .createVisualMappingFunction(attributeName, String.class, vp);

            return pm;
        }

        return null;
    }

    // Accessors --------------

    /**
     * @return The calculator identifier (e.g. "edgeColorCalculator").
     */
    public String getKey() {
        return key;
    }

    /**
     * @return The key used in the Visual Lexicon lookup.
     */
    public String getVisualPropertyId() {
        return visualPropertyId;
    }

    /**
     * @return The visual property type.
     */
    public Class<T> getType() {
        return type;
    }

    public Class<? extends CyTableEntry> getTargetType() {
        return targetType;
    }

    // Util --------------

    private static Class<? extends CyTableEntry> getTargetDataType(String calcKey) {
        calcKey = calcKey.toLowerCase();
        if (calcKey.matches("[a-zA-Z]+(node|edge)[reverse]?selectioncolor")) return CyNetwork.class;
        if (calcKey.contains("node")) return CyNode.class;
        if (calcKey.contains("edge")) return CyEdge.class;
        return CyNetwork.class;
    }

    private static Class<?> getPropertyType(String calcKey) {
        calcKey = calcKey.toLowerCase();
        if (calcKey.equals("nodesizelocked")) return Boolean.class;
        if (calcKey.matches("[a-zA-Z]*color[a-zA-Z]*")) return Color.class;
        if (calcKey.matches("[a-zA-Z]*(opacity|fontsize)[a-zA-Z]*")) return Integer.class;
        if (calcKey.matches("[a-zA-Z]*(h(e)?ight|width|size)[a-zA-Z]*")) return Double.class;
        if (calcKey.matches("[a-zA-Z]*font[a-zA-Z]*")) return Font.class;
        return String.class;
    }

    private static String getVisualPropertyId(String calcKey) {
        if (calcKey != null) {
            // TODO: updateLegacyKey
            return calcKey.replaceAll("(?i)default|calculator|uniform", "").toLowerCase().trim();
        }

        return calcKey;
    }

    private static String convert(String calcKey) {
        if (calcKey != null) {
            // TODO: updateLegacyKey
            return calcKey.replaceAll("(?i)default|calculator", "").toLowerCase().trim();
        }

        return calcKey;
    }
}
