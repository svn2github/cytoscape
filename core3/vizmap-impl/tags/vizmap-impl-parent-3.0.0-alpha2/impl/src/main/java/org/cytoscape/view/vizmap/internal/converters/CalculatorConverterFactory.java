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

import java.util.HashSet;
import java.util.Set;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTableEntry;
import org.cytoscape.view.vizmap.VisualMappingFunctionFactory;

/**
 * Simple factory that creates {@link  org.cytoscape.view.vizmap.internal.converters.CalculatorConverter CalculatorConverter}
 * objects.
 * @author Christian
 */
public class CalculatorConverterFactory {

    private final VisualMappingFunctionFactory discreteMappingFactory;
    private final VisualMappingFunctionFactory continuousMappingFactory;
    private final VisualMappingFunctionFactory passthroughMappingFactory;

    public CalculatorConverterFactory(final VisualMappingFunctionFactory discreteMappingFactory,
                                      final VisualMappingFunctionFactory continuousMappingFactory,
                                      final VisualMappingFunctionFactory passthroughMappingFactory) {
        this.discreteMappingFactory = discreteMappingFactory;
        this.continuousMappingFactory = continuousMappingFactory;
        this.passthroughMappingFactory = passthroughMappingFactory;
    }

    /**
     * @param calcKey The calculator identifier (e.g. "edgeColorCalculator" or "defaultEdgeColor").
     * @return
     */
    public CalculatorConverter[] getConverters(String calcKey) {
        Set<CalculatorConverter> convs = new HashSet<CalculatorConverter>();

        Set<String> keys = new HashSet<String>();
        Class<? extends CyTableEntry> targetType = getTargetDataType(calcKey);
        String legacyCalcKey = null;

        // Old/deprecated styles need to be converted to new properties first!
        if (calcKey.matches("(?i)(default)?(node|edge)LineType(Calculator)?")) {
            // Split in two keys; e.g. defaultEdgeLineStyle + defaultEdgeLineWidth
            legacyCalcKey = calcKey;
            keys.add(calcKey.replace("LineType", "LineStyle"));
            keys.add(calcKey.replace("LineType", "LineWidth"));
        } else if (calcKey.matches("(?i)(default)?Edge(Source|Target)Arrow(Calculator)?")) {
            // Split in two; e.g. defaultEdgeSourceArrowShape + defaultEdgeSourceArrowColor
            legacyCalcKey = calcKey;
            keys.add(calcKey.replace("Arrow", "ArrowColor"));
            keys.add(calcKey.replace("Arrow", "ArrowShape"));
        } else {
            // It is NOT an old key
            keys.add(calcKey);
        }

        for (String k : keys) {
            CalculatorConverter c = new CalculatorConverter(k, legacyCalcKey, targetType, discreteMappingFactory,
                                                            continuousMappingFactory, passthroughMappingFactory);
            convs.add(c);
        }

        return convs.toArray(new CalculatorConverter[convs.size()]);
    }

    private static Class<? extends CyTableEntry> getTargetDataType(String calcKey) {
        calcKey = calcKey.toLowerCase();

        if (calcKey.matches("[a-zA-Z]+(node|edge)[reverse]?selectioncolor")) return CyNetwork.class;
        if (calcKey.contains("node")) return CyNode.class;
        if (calcKey.contains("edge")) return CyEdge.class;

        return CyNetwork.class;
    }
}
