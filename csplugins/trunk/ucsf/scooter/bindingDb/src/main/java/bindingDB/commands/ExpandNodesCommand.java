/* vim: set ts=2: */
/**
 * Copyright (c) 2012 The Regents of the University of California.
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
package bindingDB.commands;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import cytoscape.CyEdge;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.command.CyCommandResult;
import cytoscape.data.CyAttributes;
import cytoscape.data.Semantics;
import cytoscape.groups.CyGroup;
import cytoscape.groups.CyGroupManager;
import cytoscape.logger.CyLogger;


public class ExpandNodesCommand {
	/**
 	 * This command will create metanodes from each protein that has hits and add nodes
 	 * representing each hit.
 	 */
	static public CyCommandResult expandNodes(CyLogger logger, CyNode node) {
		CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
		CyCommandResult result = new CyCommandResult();

		if (!nodeAttributes.hasAttribute(node.getIdentifier(), AnnotateNetworkCommand.HIT_ATTR)) {
			result.addMessage("Node "+node.getIdentifier()+" doesn't have any hits");
			return result;
		}

		Integer hitCount = nodeAttributes.getIntegerAttribute(node.getIdentifier(),
                                                          AnnotateNetworkCommand.HIT_ATTR);

		if (hitCount.intValue() == 0) {
			result.addMessage("Node "+node.getIdentifier()+" doesn't have any hits");
			return result;
		}

		// Get all of our lists
		List<String>monomerIDList = getStringList(nodeAttributes, node, AnnotateNetworkCommand.ID_ATTR);
		List<String>smilesList = getStringList(nodeAttributes, node, AnnotateNetworkCommand.SMILES_ATTR);
		List<String>typeList = getStringList(nodeAttributes, node, AnnotateNetworkCommand.AFF_TYPE_ATTR);
		List<String>affinityStrList = getStringList(nodeAttributes, node, AnnotateNetworkCommand.AFFINITY_STR_ATTR);
		List<Double>affinityList = getDoubleList(nodeAttributes, node, AnnotateNetworkCommand.AFFINITY_ATTR);

		// Create a list of new and edges
		List<CyNode>nodeList = new ArrayList<CyNode>();

		for (int index = 0; index < monomerIDList.size(); index++) {
			String id = monomerIDList.get(index);
			String smiles = smilesList.get(index);
			String type = typeList.get(index);
			String affinityStr = affinityStrList.get(index);
			Double affinity = affinityList.get(index);
			CyNode newNode = createNode(id, smiles);
			CyEdge newEdge = createEdge(node, newNode, type, affinityStr, affinity);
			nodeList.add(newNode);
		}

		// Now, create the settings we want the metanode to use
		String settings = "hideMetanode=false;createMembershipEdges=false;dontExpandEmpty=true";
		nodeAttributes.setAttribute(node.getIdentifier(), "__metanodeSettings", settings);

		// OK, now turn the node into a metanode
		CyGroup group = CyGroupManager.createGroup(node, nodeList, "metaNode", Cytoscape.getCurrentNetwork());

		// Collapse the group
		group.setState(2);
		// ... and expand it
		group.setState(1);

		result.addMessage("Created "+nodeList.size()+" nodes and added them to "+node.getIdentifier());

		return result;
	}

	static CyNode createNode(String id, String smiles) {
		CyNode newNode = Cytoscape.getCyNode(id, true);
		CyAttributes nodeAttribute = Cytoscape.getNodeAttributes();
		List<String>smList = new ArrayList<String>();
		smList.add(smiles);
		nodeAttribute.setListAttribute(id, AnnotateNetworkCommand.SMILES_ATTR, smList);
		Cytoscape.getCurrentNetwork().addNode(newNode);
		return newNode;
	}

	static CyEdge createEdge(CyNode parent, CyNode child, String type, String affStr, Double aff) {
		CyEdge edge = Cytoscape.getCyEdge(parent, child, Semantics.INTERACTION, type, true);
		CyAttributes edgeAttributes = Cytoscape.getEdgeAttributes();
		edgeAttributes.setAttribute(edge.getIdentifier(), AnnotateNetworkCommand.AFFINITY_STR_ATTR, affStr);
		edgeAttributes.setAttribute(edge.getIdentifier(), AnnotateNetworkCommand.AFFINITY_ATTR, aff);
		Cytoscape.getCurrentNetwork().addEdge(edge);
		return edge;
	}

	static List<String> getStringList(CyAttributes attrs, CyNode node, String attrName) {
		if (!attrs.hasAttribute(node.getIdentifier(), attrName))
			return new ArrayList<String>();

		return (List<String>)attrs.getListAttribute(node.getIdentifier(), attrName);
	}

	static List<Double> getDoubleList(CyAttributes attrs, CyNode node, String attrName) {
		if (!attrs.hasAttribute(node.getIdentifier(), attrName))
			return new ArrayList<Double>();

		return (List<Double>)attrs.getListAttribute(node.getIdentifier(), attrName);
	}

}
