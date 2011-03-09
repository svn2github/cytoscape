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

    private static final Logger logger = LoggerFactory.getLogger(VisualStyleSerializerImpl.class);

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
        // TODO implement something here, after the new format for 3.0 is specified! 
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

                    if (!isDeprecated(key)) {
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
                }

                // Do not add the modified default style to the list!
                if (!vs.equals(defaultStyle)) styles.add(vs);
            }
        }

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
                                   + "((node|edge)LabelColor)|"
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

    /**
     * @param key
     * @return true if it is a deprecated key, used only in old versions of Cytoscape.
     */
    private static boolean isDeprecated(String key) {
        return key.matches("(?i).+(EdgeLineType|NodeLineType|EdgeSourceArrow|EdgeTargetArrow)");
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
                             + "\\1((CustomGraphics(Position)?\\d+)|LabelColor|([a-zA-Z]+Calculator))");
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

    private <T> void setDefaultProperty(VisualLexicon lexicon, VisualStyle vs, String key, String sValue) {
        String calcKey = key.split("\\.")[2];
        CalcConverter conv = CalcConverter.getConverter(calcKey);

        if (conv != null) {
            Class<? extends CyTableEntry> dataType = conv.getTargetType();
            String vpKey = conv.getVisualPropertyId();
            VisualProperty vp = lexicon.lookup(dataType, vpKey);

            if (vp != null) {
                Object value = vp.parseSerializableString(sValue);
                if (value != null) vs.setDefaultValue(vp, value);
            }
        }
    }

    private void setMappingFunction(VisualLexicon lexicon, VisualStyle vs, String key, String value, Properties props) {
        String calcKey = key.split("\\.")[2];
        CalcConverter conv = CalcConverter.getConverter(calcKey);

        if (conv != null) {
            Class<? extends CyTableEntry> dataType = conv.getTargetType();
            String vpId = conv.getVisualPropertyId();
            VisualProperty vp = lexicon.lookup(dataType, vpId);

            if (vp != null) {
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
        // TODO ?
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
class CalcConverter {

    /** This type corresponds to java.lang.Boolean. */
    public final byte TYPE_BOOLEAN = 1;
    /** This type corresponds to java.lang.Double. */
    public final byte TYPE_FLOATING_POINT = 2;
    /** This type corresponds to java.lang.Integer. */
    public final byte TYPE_INTEGER = 3;
    /** This type corresponds to java.lang.String. */
    public final byte TYPE_STRING = 4;
    /** This type corresponds to an attribute which has not been defined. */
    public final byte TYPE_UNDEFINED = -1;
    /**
     * This type corresponds to a 'simple' list.
     * <P>
     * A 'simple' list is defined as follows:
     * <UL>
     * <LI>All items within the list are of the same type, and are chosen
     * from one of the following: <CODE>Boolean</CODE>, <CODE>Integer</CODE>,
     * <CODE>Double</CODE> or <CODE>String</CODE>.
     * </UL>
     */
    public final byte TYPE_SIMPLE_LIST = -2;
    /**
     * This type corresponds to a 'simple' hash map.
     * <P>
     * A 'simple' map is defined as follows:
     * <UL>
     * <LI>All keys within the map are of type:  <CODE>String</CODE>.
     * <LI>All values within the map are of the same type, and are chosen
     * from one of the following: <CODE>Boolean</CODE>, <CODE>Integer</CODE>,
     * <CODE>Double</CODE> or <CODE>String</CODE>.
     * </UL>
     */
    public final byte TYPE_SIMPLE_MAP = -3;

    private static final Map<Class<? extends CyTableEntry>, Map<String, CalcConverter>> converters;

    public String key;
    public String visualPropertyId;
    public Class<? extends CyTableEntry> targetType;

    static {
        converters = new Hashtable<Class<? extends CyTableEntry>, Map<String, CalcConverter>>();
        converters.put(CyNode.class, new Hashtable<String, CalcConverter>());
        converters.put(CyEdge.class, new Hashtable<String, CalcConverter>());
        converters.put(CyNetwork.class, new Hashtable<String, CalcConverter>());
    }

    private CalcConverter(String calcKey, Class<? extends CyTableEntry> targetType) {
        this.key = calcKey;
        this.visualPropertyId = getVisualPropertyId(calcKey);
        this.targetType = targetType;
    }

    /**
     * @param calcKey The calculator identifier (e.g. "edgeColorCalculator" or "defaultEdgeColor").
     * @return
     */
    public static CalcConverter getConverter(String calcKey) {
        CalcConverter conv = null;

        Class<? extends CyTableEntry> targetType = getTargetDataType(calcKey);
        Map<String, CalcConverter> map = converters.get(targetType);

        if (map != null) {
            // Is there a cached converter for this key?
            conv = map.get(calcKey);

            if (conv == null) {
                // Create and cache one...
                conv = new CalcConverter(calcKey, targetType);
                map.put(calcKey, conv);
            }
        }

        return conv;
    }

    /**
     * @param props All the visual properties
     * @param mapperName Example: "MyStyle-Edge Color-Discrete Mapper"
     * @return
     */
    public <K, T> VisualMappingFunction<K, T> getMappingFunction(Properties props,
                                                                 String mapperName,
                                                                 VisualProperty<T> vp,
                                                                 VisualMappingFunctionFactory discreteMappingFactory,
                                                                 VisualMappingFunctionFactory continuousMappingFactory,
                                                                 VisualMappingFunctionFactory passthroughMappingFactory) {
        // e.g. "edgeColorCalculator.MyStyle-Edge Color-Discrete Mapper.mapping."
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
                            // TODO: Always handle List type as String?
                            break;
                    }

                    T vpValue = vp.parseSerializableString(sv);

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
                    T lesser = vp.parseSerializableString(props.getProperty(baseKey + "bv" + i + ".lesser"));
                    T equal = vp.parseSerializableString(props.getProperty(baseKey + "bv" + i + ".equal"));
                    T greater = vp.parseSerializableString(props.getProperty(baseKey + "bv" + i + ".greater"));

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
