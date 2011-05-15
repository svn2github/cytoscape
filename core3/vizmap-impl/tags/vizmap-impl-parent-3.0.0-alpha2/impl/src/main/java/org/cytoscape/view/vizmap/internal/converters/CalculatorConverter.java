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
package org.cytoscape.view.vizmap.internal.converters;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTableEntry;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.vizmap.VisualMappingFunction;
import org.cytoscape.view.vizmap.VisualMappingFunctionFactory;
import org.cytoscape.view.vizmap.mappings.BoundaryRangeValues;
import org.cytoscape.view.vizmap.mappings.ContinuousMapping;
import org.cytoscape.view.vizmap.mappings.DiscreteMapping;
import org.cytoscape.view.vizmap.mappings.PassthroughMapping;

/**
 * Converter for Cytoscape 2.x calculators, which used to serialized as a properties file
 * (session_vizmap.props file in the .cys file).
 * @author Christian
 * @param <T>
 */
public class CalculatorConverter {

    /** This type corresponds to java.lang.Boolean. */
    public static final byte TYPE_BOOLEAN = 1;
    /** This type corresponds to java.lang.Double. */
    public static final byte TYPE_FLOATING_POINT = 2;
    /** This type corresponds to java.lang.Integer. */
    public static final byte TYPE_INTEGER = 3;
    /** This type corresponds to java.lang.String. */
    public static final byte TYPE_STRING = 4;
    /** This type corresponds to an attribute which has not been defined. */
    public static final byte TYPE_UNDEFINED = -1;
    /**
     * This type corresponds to a 'simple' list.
     * <P>
     * A 'simple' list is defined as follows:
     * <UL>
     * <LI>All items within the list are of the same type, and are chosen from
     * one of the following: <CODE>Boolean</CODE>, <CODE>Integer</CODE>,
     * <CODE>Double</CODE> or <CODE>String</CODE>.
     * </UL>
     */
    public static final byte TYPE_SIMPLE_LIST = -2;

    private String key;
    private String legacyKey;
    private String visualPropertyId;
    private Class<? extends CyTableEntry> targetType;

    private final VisualMappingFunctionFactory discreteMappingFactory;
    private final VisualMappingFunctionFactory continuousMappingFactory;
    private final VisualMappingFunctionFactory passthroughMappingFactory;

    private static final Map<Class<? extends CyTableEntry>, Map<String, CalculatorConverter>> converters;

    /** old_style -> new_style */
    private static final Map<String, String> oldLineStyles;
    /** old_color -> new_color */
    private static final Map<String, String> oldArrowColors;

    static {
        converters = new HashMap<Class<? extends CyTableEntry>, Map<String, CalculatorConverter>>();
        converters.put(CyNode.class, new HashMap<String, CalculatorConverter>());
        converters.put(CyEdge.class, new HashMap<String, CalculatorConverter>());
        converters.put(CyNetwork.class, new HashMap<String, CalculatorConverter>());

        oldLineStyles = new HashMap<String, String>();
        oldLineStyles.put("", "SOLID");
        oldLineStyles.put("LINE", "SOLID");
        oldLineStyles.put("DASHED", "EQUAL_DASH");

        oldArrowColors = new HashMap<String, String>();
        oldArrowColors.put("WHITE", "255,255,255");
        oldArrowColors.put("BLACK", "0,0,0");
    }

    CalculatorConverter(String calcKey,
                        String legacyCalcKey,
                        Class<? extends CyTableEntry> targetType,
                        VisualMappingFunctionFactory discreteMappingFactory,
                        VisualMappingFunctionFactory continuousMappingFactory,
                        VisualMappingFunctionFactory passthroughMappingFactory) {
        this.key = calcKey;
        this.legacyKey = legacyCalcKey;
        this.visualPropertyId = getVisualPropertyId(calcKey);
        this.targetType = targetType;

        this.discreteMappingFactory = discreteMappingFactory;
        this.continuousMappingFactory = continuousMappingFactory;
        this.passthroughMappingFactory = passthroughMappingFactory;
    }

    public <T> T getValue(String sValue, VisualProperty<T> vp) {
        T value = null;

        if (sValue != null && vp != null) {
            sValue = updateLegacyValue(sValue);
            if (sValue != null) value = vp.parseSerializableString(sValue);
        }

        return value;
    }

    /**
     * @param props
     *            All the visual properties
     * @param mapperName
     *            Example: "MyStyle-Edge Color-Discrete Mapper"
     * @return
     */
    public <K, T> VisualMappingFunction<K, T> getMappingFunction(Properties props,
                                                                 String mapperName,
                                                                 VisualProperty<T> vp) {
        // e.g. "edgeColorCalculator.MyStyle-Edge Color-Discrete Mapper.mapping."
        String baseKey = (legacyKey != null ? legacyKey : key) + "." + mapperName + ".mapping.";

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
                            // Note: Always handle List type as String!
                            break;
                    }

                    T vpValue = getValue(sv, vp);
                    if (vpValue != null) dm.putMapValue((K) dataValue, vpValue);
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
                    T lesser = getValue(props.getProperty(baseKey + "bv" + i + ".lesser"), vp);
                    T equal = getValue(props.getProperty(baseKey + "bv" + i + ".equal"), vp);
                    T greater = getValue(props.getProperty(baseKey + "bv" + i + ".greater"), vp);

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

    private String updateLegacyValue(String value) {
        if (value != null) {
            if (legacyKey != null) {
                // The value is from older calculators and need to be updated!
                if (key.matches("(?i)(default)?(node|edge)LineStyle(Calculator)?")) {
                    // Convert the former line type (e.g. LINE_1, LINE_2, DASHED_1) to
                    // current line style
                    String[] vv = value.split("_");
                    if (vv.length == 2) return oldLineStyles.get(vv[0]);
                } else if (key.matches("(?i)(default)?(node|edge)LineWidth(Calculator)?")) {
                    // Convert the former line type to current line width
                    String[] vv = value.split("_");
                    if (vv.length == 2) return vv[1];
                } else if (key.matches("(?i)(default)?Edge(Source|Target)ArrowColor(Calculator)?")) {
                    // Convert the former arrow property value (e.g. NONE,
                    // WHITE_DIAMOND, BLACK_DIAMOND) to color
                    if (!value.equalsIgnoreCase("NONE")) {
                        String[] vv = value.split("_");
                        if (vv.length == 2) return oldArrowColors.get(vv[0]);
                    }
                } else if (key.matches("(?i)(default)?Edge(Source|Target)ArrowShape(Calculator)?")) {
                    // Convert the former arrow property value to shape
                    if (value.equalsIgnoreCase("NONE")) {
                        return "NONE";
                    } else {
                        String[] vv = value.split("_");
                        if (vv.length == 2) return vv[1];
                    }
                }
            } else {
                // No need to update the value
                return value;
            }
        }

        return null;
    }

    // Util --------------

    private static String getVisualPropertyId(String calcKey) {
        if (calcKey != null) {
            // TODO: updateLegacyKey
            return calcKey.replaceAll("(?i)default|calculator|uniform", "").toLowerCase().trim();
        }

        return calcKey;
    }
}
