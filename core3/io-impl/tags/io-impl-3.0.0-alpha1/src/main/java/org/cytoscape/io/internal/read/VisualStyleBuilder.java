/*
 Copyright (c) 2007, The Cytoscape Consortium (www.cytoscape.org)

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

package org.cytoscape.io.internal.read;

import java.util.Hashtable;
import java.util.Map;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.vizmap.VisualMappingFunctionFactory;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.view.vizmap.VisualStyleFactory;
import org.cytoscape.view.vizmap.mappings.DiscreteMapping;

/**
 * Based on the graph/node/edge view information, build new Visual Style.
 * 
 * This class accepts style properties and adds hidden Cytoscape attributes that
 * will be used to actually create the style.
 */
public class VisualStyleBuilder {

    Map<VisualProperty<?>, Map<String, Object>>  valueMaps;
    Map<VisualProperty<?>, Map<String, Integer>> counts;
    String                                       name;
    private boolean                              nodeSizeLocked = true;

    private int                                  nodeMax;
    private int                                  edgeMax;

    private final VisualStyleFactory             styleFactory;
    private final VisualMappingManager           visMappingManager;
    private final VisualMappingFunctionFactory   discreteMappingFactory;

    private final CyTable                        nodesTable;
    private final CyTable                        edgesTable;

    /**
     * Build a new VisualStyleBuilder object whose output style will be called
     * "name".
     * 
     * @param name
     *            the name of the visual style that will be created.
     */
    public VisualStyleBuilder(String name,
                              final VisualStyleFactory styleFactory,
                              final VisualMappingManager visMappingManager,
                              final VisualMappingFunctionFactory discreteMappingFactory,
                              final CyTable nodesTable,
                              final CyTable edgesTable) {
        // because visual style parsing breaks with '.' in the names
        this.name = name.replaceAll("\\.", "_");
        this.styleFactory = styleFactory;
        this.visMappingManager = visMappingManager;
        this.discreteMappingFactory = discreteMappingFactory;
        this.nodesTable = nodesTable;
        this.edgesTable = edgesTable;

        this.valueMaps = new Hashtable<VisualProperty<? extends Object>, Map<String, Object>>();
        this.counts = new Hashtable<VisualProperty<? extends Object>, Map<String, Integer>>();
    }

    /**
     * Actually build the style using the provided properties
     * 
     * @param <K>
     * 
     * @param defStyle
     *            the default syle
     */
    public <V> VisualStyle buildStyle() {
        // Create the new style
        VisualStyle style = styleFactory.getInstance(visMappingManager.getDefaultVisualStyle());
        String styleName = name + " style";
        style.setTitle(styleName);

        // TODO: Lock node size?
        // style.getDependency().set(NODE_SIZE_LOCKED, nodeSizeLocked);

        processCounts();

        for (VisualProperty<?> _vp : valueMaps.keySet()) {
            final VisualProperty<V> vp = (VisualProperty<V>) _vp;
            final Class<?> type = vp.getTargetDataType();
            Map<String, V> valMap = (Map<String, V>) valueMaps.get(vp);

            if (createMapping(vp)) {
                // If there is more than one value specified for a given visual
                // property, or if only a subset of nodes/edges have a property
                // then create a mapping and calculator.
                final String attrName = getAttrName(vp);

                DiscreteMapping<String, V> dm = (DiscreteMapping<String, V>) discreteMappingFactory
                        .createVisualMappingFunction(attrName, String.class, vp);

                dm.putAll(valMap);
                style.addVisualMappingFunction(dm);
            } else {
                // Otherwise, set the default appearance value for the visual
                // style and then remove the attribute that was created.
                for (String key : valMap.keySet()) {
                    V val = (V) valMap.get(key);
                    style.setDefaultValue(vp, val);
                }

                // delete the column we created
                if (type == CyNode.class) {
                    nodesTable.deleteColumn(getAttrName(vp));
                } else if (type == CyEdge.class) {
                    edgesTable.deleteColumn(getAttrName(vp));
                }
            }
        }

        // TODO: should we do this?
        // Remove styles that have the same name in case we've already loaded this network once
        //        Set<VisualStyle> allStyles = visMappingManager.getAllVisualStyles();
        //
        //        for (VisualStyle oldStyle : allStyles) {
        //            if (styleName.equals(oldStyle.getTitle())) visMappingManager.removeVisualStyle(oldStyle);
        //        }

        return style;
    }

    /**
     * This method actually adds a property to be considered for inclusion into
     * the resulting style.
     * 
     * @param row
     *            the node or edge row
     * @param vp
     *            the visual property to be added
     * @param value
     *            the property value
     */
    public <T> void addProperty(CyRow row, VisualProperty<T> vp, T value) {
        if (vp == null || value == null) return;

        // TODO: create value parsers (see version 2.8.1)?
        String vString = vp.toSerializableString(value);
        Class<?> type = vp.getTargetDataType();

        if (type != CyNetwork.class) { // only edges and nodes...
            String colName = getAttrName(vp);

            // add a column to the table if it does not exist yet
            CyTable table = row.getDataTable();
            Map<String, Class<?>> columns = table.getColumnTypeMap();

            if (!columns.containsKey(colName)) table.createColumn(colName, String.class);

            // set the visual property value as a row attribute
            row.set(colName, vString);
        }

        // store the value
        if (!valueMaps.containsKey(vp)) valueMaps.put(vp, new Hashtable<String, Object>());

        valueMaps.get(vp).put(vString, value);

        // store the count
        if (!counts.containsKey(vp)) counts.put(vp, new Hashtable<String, Integer>());
        if (!counts.get(vp).containsKey(vString)) counts.get(vp).put(vString, 0);

        counts.get(vp).put(vString, counts.get(vp).get(vString) + 1);
    }

    /**
     * This method lock/unlock the size object (Node Width, Node Height) in
     * NodeAppearanceCalculator If unlocked, we can modify both width and height
     * of node
     * 
     * @param pLock
     */

    public void setNodeSizeLocked(boolean pLock) {
        nodeSizeLocked = pLock;
    }

    private String getAttrName(VisualProperty<?> vp) {
        return "vizmap:" + name + " " + vp.getIdString();
    }

    /**
     * Processes the counts for the various visual properties and establishes
     * how many nodes and edges there are.
     */
    private void processCounts() {
        Map<VisualProperty<?>, Integer> cm = new Hashtable<VisualProperty<?>, Integer>();

        for (VisualProperty<?> vp : counts.keySet()) {
            int total = 0;

            for (Object o : counts.get(vp).keySet()) {
                total += counts.get(vp).get(o);
            }

            cm.put(vp, total);
        }

        nodeMax = 0;
        edgeMax = 0;

        for (VisualProperty<?> vp : counts.keySet()) {
            if (counts.get(vp).size() == 1) {
                Class<?> type = vp.getTargetDataType();

                for (Object o : counts.get(vp).keySet()) {
                    if (type == CyNode.class)
                        nodeMax = Math.max(counts.get(vp).get(o), nodeMax);
                    else if (type == CyEdge.class) edgeMax = Math.max(counts.get(vp).get(o), edgeMax);
                }
            }
        }
    }

    /**
     * This method determines whether or not to create a mapping for this visual
     * property type. There are two times when you want to create a mapping: 1)
     * when there is more than one key mapped to a value for type and 2) when
     * only one key is mapped to a value, but only a subset of nodes or edges
     * have that mapping (which is to say the property doesn't hold for all
     * nodes or all edges).
     */
    private boolean createMapping(VisualProperty<?> vp) {
        // if there is more than one mapping
        if (counts.get(vp).size() > 1) return true;

        Class<?> type = vp.getTargetDataType();

        // check the number of times the value is mapped
        // relative to the number of nodes or edges
        for (Object o : counts.get(vp).keySet()) {
            int ct = counts.get(vp).get(o).intValue();

            if (type == CyNode.class) {
                return (ct < nodeMax);
            } else if (type == CyEdge.class) {
                return (ct < edgeMax);
            } else {
                return false;
            }
        }

        return false;
    }
}
