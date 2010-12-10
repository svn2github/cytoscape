/* vim: set ts=2: */
/**
 * Copyright (c) 2007 The Regents of the University of California.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *   1. Redistributions of source code must retain the above copyright
 *      notice, this list of conditions, and the following disclaimer.
 *   2. Redistributions in binary form must reproduce the above
 *      copyright notice, this list of conditions, and the following
 *      disclaimer in the documentation and/or other materials provided
 *      with the distribution.
 *   3. Redistributions must acknowledge that this software was
 *      originally developed by the UCSF Computer Graphics Laboratory
 *      under support by the NIH National Center for Research Resources,
 *      grant P41-RR01081.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDER "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE REGENTS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT
 * OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 * BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */
package metaNodePlugin2.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// giny imports
import giny.view.EdgeView;
import giny.view.NodeView;

// Cytoscape imports
import cytoscape.Cytoscape;
import cytoscape.CyNode;
import cytoscape.CyNetwork;
import cytoscape.command.CyCommandException;
import cytoscape.command.CyCommandManager;
import cytoscape.command.CyCommandResult;
import cytoscape.logger.CyLogger;
import cytoscape.view.CyNetworkView;
import cytoscape.data.CyAttributes;

import cytoscape.groups.CyGroup;

// Metanode imports
import metaNodePlugin2.MetaNodeGroupViewer;
import metaNodePlugin2.model.MetaNode;


/**
 * The NodeCharts class provides several static methods that
 * manage the interface to the nodechart plugin
 *
 * It would be nice to reuse the AttributeHandler implementations, but
 * we interpret things differently for nodeCharts than we do for aggregated
 * attributes...
 *
 * At this point, this handles the following types:
 *	TYPE_INTEGER: labels are the names of the nodes and the values are the values
 *	TYPE_FLOATING: labels are the names of the nodes and the values are the values
 *	TYPE_STRING: labels are the names of the strings and the values are the number of times that string occurs
 *	TYPE_BOOLEAN: labels are either "True" or "False" and the values are the number of times that condition occurs
 *	TYPE_SIMPLE_LIST: results depend on type:
 *		String: labels are the names of the strings and the values are the number of times that string occurs
 *		Integer: ??
 *		Double: ??
 */
public class NodeCharts {

	private static String VALUELIST = "valuelist";
	private static String LABELLIST = "labellist";
	
	/**
	 * This method calculates the values and labels and draws the 
	 * node chart on a metanode.
	 *
	 * @param mn the metanode we're drawing on
	 * @param logger the CyLogger we're using
	 */
	public static void updateNodeCharts(MetaNode mn, CyLogger logger) {
		// Get the attribute we're using
		String nodeChartAttribute = mn.getNodeChartAttribute();

		// Get the chart type
		String chartType = mn.getChartType();

		if (nodeChartAttribute == null || chartType == null || chartType.equals(MetaNodeGroupViewer.NONODECHART)) return;

		// Get the values from all of our children
		CyGroup group = mn.getCyGroup();
		List<CyNode> nodeList = group.getNodes();
		if (nodeList == null) return;

		CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();

		// Make sure this is a type that makes sense for us
		byte attributeType = nodeAttributes.getType(nodeChartAttribute);
		if (attributeType == CyAttributes.TYPE_UNDEFINED || attributeType == CyAttributes.TYPE_SIMPLE_MAP ||
		    attributeType == CyAttributes.TYPE_COMPLEX || attributeType == CyAttributes.TYPE_BOOLEAN)
			return;

		// Create our argument map
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("node", group.getGroupNode().getIdentifier());
		args.put("colorlist", "contrasting");

		// Depending on the attribute type -- do the right thing
		switch (attributeType) {
			case CyAttributes.TYPE_SIMPLE_LIST:
				getHistogramLabelsAndValues(args, group, nodeAttributes, nodeChartAttribute, chartType);
				break;
			case CyAttributes.TYPE_INTEGER:
			case CyAttributes.TYPE_FLOATING:
				getNumericLabelsAndValues(args, group, nodeAttributes, nodeChartAttribute, chartType);
				break;
			case CyAttributes.TYPE_BOOLEAN:
			case CyAttributes.TYPE_STRING:
				getStringLabelsAndValues(args, group, nodeAttributes, nodeChartAttribute, chartType);
				break;
			default:
		}

		try {
			// First, make sure we don't have any nodechart on the node
			CyCommandResult result = CyCommandManager.execute("nodecharts", "clear", args);
			// Now, add the node chart
			result = CyCommandManager.execute("nodecharts", chartType, args);
		} catch (CyCommandException cce) {
			logger.warning("node chart command failed: "+cce.getMessage());
		}
	}

	/**
	 * This method is used to handle numeric attribute types
	 */
	private static void getNumericLabelsAndValues(Map<String,Object>args, CyGroup group, CyAttributes nodeAttributes,
	                                              String nodeChartAttribute, String chartType) {
		List<String> labels = new ArrayList<String>();
		List<String> values = new ArrayList<String>();
		for (CyNode node: group.getNodes()) {
			if (nodeAttributes.hasAttribute(node.getIdentifier(), nodeChartAttribute)) {
				labels.add(node.getIdentifier());
				values.add(nodeAttributes.getAttribute(node.getIdentifier(), nodeChartAttribute).toString());
			}
		}
		args.put(VALUELIST, values);
		args.put(LABELLIST, labels);
	}

	/**
	 * This method is used to handle string attribute types
	 */
	private static void getStringLabelsAndValues(Map<String,Object>args, CyGroup group, CyAttributes nodeAttributes,
	                                              String nodeChartAttribute, String chartType) {
		Map<String, Integer> valueMap = new HashMap<String, Integer>();
		for (CyNode node: group.getNodes()) {
			if (nodeAttributes.hasAttribute(node.getIdentifier(), nodeChartAttribute)) {
				String v = nodeAttributes.getAttribute(node.getIdentifier(), nodeChartAttribute).toString();
				incrementHistogram(valueMap, v);
			}
		}

		addHistoArgs(valueMap, args);
	}

	/**
	 * This method is used to handle list attribute types
	 */
	private static void getHistogramLabelsAndValues(Map<String,Object>args, CyGroup group, CyAttributes nodeAttributes,
	                                              String nodeChartAttribute, String chartType) {
		Map<String, Integer> valueMap = new HashMap<String, Integer>();
		for (CyNode node: group.getNodes()) {
			if (nodeAttributes.hasAttribute(node.getIdentifier(), nodeChartAttribute)) {
				List l = nodeAttributes.getListAttribute(node.getIdentifier(), nodeChartAttribute);
				for (Object v: l) {
					String label = v.toString();
					incrementHistogram(valueMap, label);
				}
			}
		}
		addHistoArgs(valueMap, args);
	}

	private static void incrementHistogram(Map<String, Integer> map, String value) {
		if (!map.containsKey(value))
			map.put(value, new Integer(1));
		else {
			Integer count = map.get(value);
			map.put(value, count+1);
		}
	}

	private static void addHistoArgs(Map<String, Integer> map, Map<String, Object> args) {
		// Now, we've got the histogram, create the appropriate arrays
		List<String>labels = new ArrayList<String>(map.keySet());
		List<Integer>values = new ArrayList<Integer>();
		for (String label: labels) {
			values.add(map.get(label));
		}
		args.put(VALUELIST, values);
		args.put(LABELLIST, labels);
	}

}
