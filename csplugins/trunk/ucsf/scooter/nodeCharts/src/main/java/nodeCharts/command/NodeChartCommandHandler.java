/* vim: set ts=2: */
/**
 * Copyright (c) 2010 The Regents of the University of California.
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
package nodeCharts.command;

// System imports
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Cytoscape imports
import cytoscape.CyNode;
import cytoscape.Cytoscape;

import cytoscape.command.AbstractCommandHandler;
import cytoscape.command.CyCommandException;
import cytoscape.command.CyCommandManager;
import cytoscape.command.CyCommandResult;
import cytoscape.layout.Tunable;
import cytoscape.logger.CyLogger;
import cytoscape.view.CyNetworkView;

// nodeChart imports
import nodeCharts.view.NodeChartViewer;
import nodeCharts.view.PieChart;
import nodeCharts.view.ViewUtils;

/**
 * 
 */
public class NodeChartCommandHandler extends AbstractCommandHandler {
	CyLogger logger;
	Map<String, NodeChartViewer> viewerMap;

	// Global commands
	private static String ATTRIBUTELIST = "attributelist";
	private static String CLEAR = "clear";
	private static String NODE = "node";
	private static String NODELIST = "nodelist";
	private static String POSITION = "position";

	public NodeChartCommandHandler(String namespace, CyLogger logger) {
		super(CyCommandManager.reserveNamespace(namespace));

		this.logger = logger;
		viewerMap = new HashMap<String, NodeChartViewer>();

		// Register each command
		register(new PieChart());
	}

  public CyCommandResult execute(String command, Collection<Tunable>args)
                                                      throws CyCommandException, RuntimeException {
		return execute(command, createKVMap(args));
	}

	public CyCommandResult execute(String command, Map<String, Object>args)
                                                      throws CyCommandException, RuntimeException {
		CyCommandResult result = new CyCommandResult();
		if (!viewerMap.containsKey(command))
			throw new CyCommandException("unknown chart type: '"+command+"'");

		if (!args.containsKey(NODE)) {
			throw new CyCommandException("node to map chart to must be specified");
		}
		String nodeName = (String)args.get(NODE);
		CyNode node = Cytoscape.getCyNode(nodeName, false);
		if (node == null)
			throw new CyCommandException("can't find node: '"+nodeName+"'");

		Object pos = null;
		if (args.containsKey(POSITION)) {
			String position = (String) args.get(POSITION);
			pos = ViewUtils.getPosition(position);
			if (pos == null)
				throw new CyCommandException("unknown position keyword or illegal position expression: "+position);
		}

		NodeChartViewer viewer = viewerMap.get(command);

		CyNetworkView view = Cytoscape.getCurrentNetworkView();

		ViewUtils.addCustomGraphics(viewer.getCustomGraphics(args, node, view, pos), node, view);

		return result;
	}

	private void register(NodeChartViewer viewer) {
		addDescription(viewer.getName(), viewer.getDescription());
		addArgument(viewer.getName(), NODE);
		Map<String,String> args = viewer.getOptions();
		for (String arg: args.keySet()) {
			if (args.get(arg) == "") {
				addArgument(viewer.getName(), arg);
			} else {
				addArgument(viewer.getName(), arg, args.get(arg));
			}
		}
		viewerMap.put(viewer.getName(), viewer);
	}
}
