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
package coreCommands.namespaces;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.command.CyCommandResult;

/**
 * This is a utility class that contains some methods that help with the handling of
 * node and node list parameters.  A node list is denoted by the key <b>nodelist</b>
 * and the a string value with a simple comma-separated list of node identifiers.  A 
 * node is denoted by the key <b>node</b> and a single node identifier.
 */
public class NodeListUtils {

	/**
 	 * This method is used to handle both <b>nodelist</b> and <b>node</b> parameters.
 	 *
 	 * @param net the network we are currently dealing with
 	 * @param result the CyCommandResult to store our values in
 	 * @param args the argument list we're use to look for <b>nodelist</b> and
 	 * <b>node</b> arguments.
 	 * @return the list of CyNode objects we found that matched the arguments
 	 */
	public static List<CyNode> getNodeList(CyNetwork net, CyCommandResult result, 
	                                          Map<String, Object> args) {
		if (args == null || args.size() == 0)
			return null;

		List<CyNode> retList = new ArrayList();
		if (args.containsKey("nodelist")) {
			String[] nodes = args.get("nodelist").toString().split(",");
			for (int nodeIndex = 0; nodeIndex < nodes.length; nodeIndex++) {
				addNode(net, nodes[nodeIndex], retList, result);
			}
		} else if (args.containsKey("node")) {
			String nodeName = args.get("node").toString();
			addNode(net, nodeName, retList, result);
		} else {
			return null;
		}
		return retList;
	}

	private static void addNode(CyNetwork net, String nodeName, List<CyNode> list, CyCommandResult result) {
		CyNode node = Cytoscape.getCyNode(nodeName, false);
		if (node == null) 
			result.addError("node: can't find node "+nodeName);
		else
			list.add(node);
		return;
	}

}
