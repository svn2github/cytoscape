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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;

import org.cytoscape.model.CyTableEntry;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.presentation.RenderingEngineManager;
import org.cytoscape.view.presentation.property.TwoDVisualLexicon;
import org.cytoscape.view.vizmap.VisualMappingFunction;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.view.vizmap.VisualStyleFactory;
import org.cytoscape.view.vizmap.VisualStyleSerializer;
import org.cytoscape.view.vizmap.internal.converters.CalculatorConverter;
import org.cytoscape.view.vizmap.internal.converters.CalculatorConverterFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * So far this implementation handles only 2.x vizmap properties.
 * @author Christian
 */
public class VisualStyleSerializerImpl implements VisualStyleSerializer {

    private final VisualStyleFactory visualStyleFactory;
    private final VisualMappingManager visualMappingManager;
    private final RenderingEngineManager renderingEngineManager;
    private final CalculatorConverterFactory calculatorConverterFactory;

    private static final Logger logger = LoggerFactory.getLogger(VisualStyleSerializerImpl.class);

    public VisualStyleSerializerImpl(final VisualStyleFactory visualStyleFactory,
                                     final VisualMappingManager visualMappingManager,
                                     final RenderingEngineManager renderingEngineManager,
                                     final CalculatorConverterFactory calculatorConverterFactory) {
        this.visualStyleFactory = visualStyleFactory;
        this.visualMappingManager = visualMappingManager;
        this.renderingEngineManager = renderingEngineManager;
        this.calculatorConverterFactory = calculatorConverterFactory;
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
            // Handle convert old styles and group properties keys/values by visual style name:
            Map<String, Map<String, String>> styleNamesMap = new HashMap<String, Map<String, String>>();
            Set<String> propNames = props.stringPropertyNames();

            for (String key : propNames) {
                String value = props.getProperty(key);
                String styleName = getStyleName(key);

                if (styleName != null) {
                    // Add each style name and its properties to a map
                    Map<String, String> keyValueMap = styleNamesMap.get(styleName);

                    if (keyValueMap == null) {
                        keyValueMap = new HashMap<String, String>();
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
                                   + "((node|edge)LabelColor)|" + "((node|edge)[a-zA-Z]+Calculator)|"
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
    protected static boolean isDeprecated(String key) {
        // TODO: delete this method
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
        CalculatorConverter[] convs = calculatorConverterFactory.getConverters(calcKey);

        for (CalculatorConverter c : convs) {
            Class<? extends CyTableEntry> dataType = c.getTargetType();
            String vpKey = c.getVisualPropertyId();
            VisualProperty vp = lexicon.lookup(dataType, vpKey);

            if (vp != null) {
                Object value = c.getValue(sValue, vp);
                if (value != null) vs.setDefaultValue(vp, value);
            }
        }
    }

    private void setMappingFunction(VisualLexicon lexicon, VisualStyle vs, String key, String value, Properties props) {
        String calcKey = key.split("\\.")[2];
        CalculatorConverter[] convs = calculatorConverterFactory.getConverters(calcKey);

        for (CalculatorConverter c : convs) {
            Class<? extends CyTableEntry> dataType = c.getTargetType();
            String vpId = c.getVisualPropertyId();
            VisualProperty vp = lexicon.lookup(dataType, vpId);

            if (vp != null) {
                VisualMappingFunction mapping = c.getMappingFunction(props, value, vp);
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
}
