/*
 Copyright (c) 2006, 2010, The Cytoscape Consortium (www.cytoscape.org)

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
package org.cytoscape.io.internal.read.vizmap;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;

import org.cytoscape.io.internal.read.AbstractVizmapReader;
import org.cytoscape.io.internal.read.vizmap.converters.CalculatorConverter;
import org.cytoscape.io.internal.read.vizmap.converters.CalculatorConverterFactory;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTableEntry;
import org.cytoscape.view.vizmap.model.ContinuousMapping;
import org.cytoscape.view.vizmap.model.DiscreteMapping;
import org.cytoscape.view.vizmap.model.Edges;
import org.cytoscape.view.vizmap.model.Network;
import org.cytoscape.view.vizmap.model.Nodes;
import org.cytoscape.view.vizmap.model.PassthroughMapping;
import org.cytoscape.view.vizmap.model.VisualProperty;
import org.cytoscape.view.vizmap.model.VisualStyle;
import org.cytoscape.view.vizmap.model.Vizmap;
import org.cytoscape.work.TaskMonitor;

public class VizmapPropertiesReader extends AbstractVizmapReader {

    private final CalculatorConverterFactory calculatorConverterFactory;

    public VizmapPropertiesReader(InputStream inputStream, CalculatorConverterFactory calculatorConverterFactory) {
        super(inputStream);
        this.calculatorConverterFactory = calculatorConverterFactory;
    }

    public void run(TaskMonitor tm) throws Exception {
        Properties props = new Properties();
        props.load(inputStream);

        // Convert properties to Vizmap:
        Vizmap vizmap = new Vizmap();
        List<VisualStyle> styles = vizmap.getVisualStyle();

        // Group properties keys/values by visual style name:
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

            VisualStyle vs = new VisualStyle();
            vs.setName(styleName);
            vs.setNetwork(new Network());
            vs.setNodes(new Nodes());
            vs.setEdges(new Edges());

            // Create and set the visual properties and mappings:
            Map<String, String> vsProps = entry.getValue();

            for (Entry<String, String> p : vsProps.entrySet()) {
                String key = p.getKey();
                String value = p.getValue();

                if (isDefaultProperty(key)) {
                    // e.g. "globalAppearanceCalculator.MyStyle.defaultBackgroundColor"
                    setDefaultProperty(vs, key, value);
                } else if (isMappingFunction(key)) {
                    // e.g. "edgeAppearanceCalculator.MyStyle.edgeColorCalculator"
                    setMappingFunction(vs, key, value, props);
                }
            }

            styles.add(vs);
        }

        this.vizmap = vizmap;
    }

    /**
     * @param key the Properties key
     * @return The name of the visual style or null if the property key doesn't or shouldn't have it
     */
    private static String getStyleName(String key) {
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

    private void setDefaultProperty(VisualStyle vs, String key, String sValue) {
        String calcKey = key.split("\\.")[2];
        CalculatorConverter[] convs = calculatorConverterFactory.getConverters(calcKey);

        for (org.cytoscape.io.internal.read.vizmap.converters.CalculatorConverter c : convs) {
            Class<? extends CyTableEntry> dataType = c.getTargetType();
            String vpId = c.getVisualPropertyId();
            VisualProperty vp = getVisualProperty(vs, dataType, vpId);

            String value = c.getValue(sValue);
            vp.setDefault(value);
        }
    }

    /**
     * Get or create a new Visual Property for the passed Visual Style.
     * It checks if the property already exists in Network, Nodes or Edges, according to the dataType. If it does not
     * exist, the property is created and added to the style.
     * @param vs
     * @param dataType
     * @param vpKey
     * @return
     */
    private VisualProperty getVisualProperty(VisualStyle vs, Class<? extends CyTableEntry> dataType, String vpId) {
        VisualProperty vp = null;
        List<VisualProperty> vpList = null;

        if (dataType == CyNetwork.class) {
            vpList = vs.getNetwork().getVisualProperty();
        } else if (dataType == CyNode.class) {
            vpList = vs.getNodes().getVisualProperty();
        } else if (dataType == CyEdge.class) {
            vpList = vs.getEdges().getVisualProperty();
        }
        
        for (VisualProperty v : vpList) {
            if (v.getId().equalsIgnoreCase(vpId)) {
                // The Visual Property has already been created...
                vp = v;
                break;
            }
        }
        
        if (vp == null) {
            // The Visual Property has not been created yet...
            vp = new VisualProperty();
            vp.setId(vpId);
            vpList.add(vp);
        }

        return vp;
    }

    private void setMappingFunction(VisualStyle vs, String key, String value, Properties props) {
        String calcKey = key.split("\\.")[2];
        CalculatorConverter[] convs = calculatorConverterFactory.getConverters(calcKey);

        for (CalculatorConverter c : convs) {
            Class<? extends CyTableEntry> dataType = c.getTargetType();
            String vpId = c.getVisualPropertyId();
            VisualProperty vp = getVisualProperty(vs, dataType, vpId);

            Object mapping = c.getMappingFunction(props, value, vp);
            
            if (mapping instanceof PassthroughMapping)
                vp.setPassthroughMapping((PassthroughMapping) mapping);
            else if (mapping instanceof ContinuousMapping)
                vp.setContinuousMapping((ContinuousMapping) mapping);
            else if (mapping instanceof DiscreteMapping)
                vp.setDiscreteMapping((DiscreteMapping) mapping);
        }
    }

    private static boolean isDefaultProperty(String key) {
        boolean b = false;

        if (key != null) {
            // Globals
            b |= key.matches("globalAppearanceCalculator\\.[^\\.]+\\.default[a-zA-Z]+Color");
            // Nodes & Edges
            b |= key.matches("nodeAppearanceCalculator\\.[^\\.]+\\.defaultNode\\w+");
            // Edges
            b |= key.matches("edgeAppearanceCalculator\\.[^\\.]+\\.defaultEdge[a-zA-Z]+");
            // dependencies
            b |= key
                    .matches("nodeAppearanceCalculator\\.[^\\.]+\\."
                             + "(nodeSizeLocked|nodeLabelColorFromNodeColor|defaultNodeShowNestedNetwork|nodeCustomGraphicsSizeSync)");
            b |= key.matches("edgeAppearanceCalculator\\.[^\\.]+\\.arrowColorMatchesEdge");
            // exceptions
            b &= !key.contains("defaultNodeShowNestedNetwork");
        }

        return b;
    }

    private static boolean isMappingFunction(String key) {
        boolean b = false;

        if (key != null) {
            b |= key.matches("(node|edge)AppearanceCalculator\\.[^\\.]+\\."
                             + "\\1((CustomGraphics(Position)?\\d+)|LabelColor|([a-zA-Z]+Calculator))");
        }

        return b;
    }
}
