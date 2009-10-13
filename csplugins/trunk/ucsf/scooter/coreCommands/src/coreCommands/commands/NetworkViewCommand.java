/* vim: set ts=2: */
/**
 * Copyright (c) 2009 The Regents of the University of California.
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
package coreCommands.commands;

import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.CytoscapeInit;
import cytoscape.command.CyCommandException;
import cytoscape.command.CyCommandManager;
import cytoscape.command.CyCommandResult;
import cytoscape.ding.DingNetworkView;
import cytoscape.layout.Tunable;
import cytoscape.logger.CyLogger;
import cytoscape.view.CyNetworkView;
import cytoscape.view.InternalFrameComponent;

// Cytoscape exporter imports
import cytoscape.util.export.BitmapExporter;
import cytoscape.util.export.PDFExporter;
import cytoscape.util.export.SVGExporter;
import cytoscape.util.export.Exporter;

import ding.view.DGraphView;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * XXX FIXME XXX Description 
 */
public class NetworkViewCommand extends AbstractCommand {

	public NetworkViewCommand() {
		// Define our subcommands
		settingsMap = new HashMap();
		addSetting("create", "network", "current");
		addSetting("export", "file");
		addSetting("export", "type", "png");
		addSetting("export", "zoom", "1.0");
		addSetting("get current");
		// addSetting("get size", "network", "current");
		addSetting("fit", "network", "current");
		addSetting("focus", "nodelist");
		addSetting("focus", "network", "current");
		addSetting("make current", "network");
		// addSetting("set window", "network", "current");
		// addSetting("set window", "x");
		// addSetting("set window", "y");
		// addSetting("set window", "height");
		// addSetting("set window", "width");
		addSetting("update", "network", "current");
		addSetting("zoom", "factor", "2.0");
		addSetting("zoom", "scale");
		addSetting("zoom", "network", "current");
	}


	/**
	 * commandName returns the command name.  This is used to build the
	 * hash table of commands to hand to the command parser
	 *
	 * @return name of the command
	 */
	public String getHandlerName() { return "networkview"; }

	public CyCommandResult execute(String command, Map<String, String>args) throws CyCommandException { 
		CyCommandResult result = new CyCommandResult();
		Map<String, CyNetworkView> viewMap = Cytoscape.getNetworkViewMap();
		CyNetwork net = Cytoscape.getCurrentNetwork();

		if (command.equals("get current")) {
			CyNetworkView current = Cytoscape.getCurrentNetworkView();
			result.addMessage("networkview: current network view is "+current.getIdentifier());
			result.addResult("currentview", current);
			return result;
		} else if (command.equals("list")) {
			result.addResult("views",viewMap.keySet());

			result.addMessage("networkview: current network views:");
			for (String key: viewMap.keySet()) {
				result.addMessage("  "+key);
			}
			return result;
		}

		String netName = getArg(command, "network", args);
		if (netName == null) {
			throw new CyCommandException("networkview: "+command+" requires network argument");
		}
		if (!netName.equalsIgnoreCase("current")) {
			net = Cytoscape.getNetwork(netName);
			if (net == null)
				throw new CyCommandException("networkview: can't find network: "+netName);
			if (!viewMap.containsKey(netName) && !command.equals("create"))
				throw new CyCommandException("networkview: can't find view for network: "+netName);
		}

		if (command.equals("export")) {
			CyNetworkView view = viewMap.get(net.getIdentifier());
			float zoom = Float.parseFloat(getArg(command, "zoom", args));
			String type = getArg(command, "type", args);
			String fileName = getArg(command, "file", args);
			if (fileName == null)
				throw new CyCommandException("networkview: no file name specified for export");

			// Get the component to export
			InternalFrameComponent ifc =
				Cytoscape.getDesktop().getNetworkViewManager().getInternalFrameComponent(view);

			// Handle the exportTextAsShape property
			DGraphView theViewToPrint = (DingNetworkView) view;
			boolean exportTextAsShape =
			    new Boolean(CytoscapeInit.getProperties().getProperty("exportTextAsShape")).booleanValue();
			theViewToPrint.setPrintingTextAsShape(exportTextAsShape);

			// Now get the right export handler & handle any options
			Exporter exporter = null;
			if (type.equalsIgnoreCase("pdf") ){
				exporter = new PDFExporter();
			} else if (type.equalsIgnoreCase("svg")) {
				exporter = new SVGExporter();
			} else if (type.equalsIgnoreCase("png")) {
				exporter = new BitmapExporter("png", zoom);
			} else if (type.equalsIgnoreCase("jpg")) {
				exporter = new BitmapExporter("jpg", zoom);
			} else if (type.equalsIgnoreCase("jpeg")) {
				exporter = new BitmapExporter("jpg", zoom);
			} else if (type.equalsIgnoreCase("bmp")) {
				exporter = new BitmapExporter("bmp", zoom);
			} else
				throw new CyCommandException("networkview: don't know how to export to type "+type);

			// Handle filename changes
			try {
				FileOutputStream outputFile = new FileOutputStream(fileName);
				exporter.export(view, outputFile);
			} catch (Exception e) {
				throw new CyCommandException("networkview: unable to export view: "+e.getMessage());
			}
			result.addMessage("networkview: exported view for "+net.getIdentifier()+" to "+fileName);

		} else if (command.equals("create")) {
			CyNetworkView view = Cytoscape.createNetworkView(net);
			result.addResult("newview",view);
			result.addMessage("networkview: created view for :"+view.getIdentifier());

		} else if (command.equals("make current")) {
			Cytoscape.setCurrentNetworkView(net.getIdentifier());
			result.addMessage("networkview: set network view for "+net.getIdentifier()+" as current");
		} else if (command.equals("fit")) {
			CyNetworkView view = viewMap.get(net.getIdentifier());
			view.fitContent();
			result.addMessage("networkview: fit view to content for "+net.getIdentifier());

		} else if (command.equals("update")) {
			CyNetworkView view = viewMap.get(net.getIdentifier());
			view.updateView();
			result.addMessage("networkview: view '"+net.getIdentifier()+"' updated");

		} else if (command.equals("focus")) {
			CyNetworkView view = viewMap.get(net.getIdentifier());
			String nodes = getArg(command, "nodelist", args);
			if (nodes == null || nodes.length() == 0) {
				((DingNetworkView) view).fitSelected();
				view.updateView();
				result.addMessage("networkview: focused '"+net.getIdentifier()+"' on selected nodes/edges");
			} else {
				// get the list of nodes
				List<CyNode> nodeList = getNodeList(net, result, args);

				// Remember our currently selected nodes and edges
				List<CyNode>selNodes = new ArrayList(net.getSelectedNodes());
				List<CyEdge>selEdges = new ArrayList(net.getSelectedEdges());

				// Select the desire nodes
				net.unselectAllEdges();
				net.unselectAllNodes();
				net.setSelectedNodeState(nodeList, true);

				// focus
				((DingNetworkView) view).fitSelected();

				// Reselect the previously selected nodes and edges
				net.setSelectedNodeState(nodeList, false);
				net.setSelectedNodeState(selNodes, true);
				net.setSelectedEdgeState(selEdges, true);

				view.updateView();
				result.addMessage("networkview: focused '"+net.getIdentifier()+"' on node(s)");
			}
		} else if (command.equals("zoom")) {
			CyNetworkView view = viewMap.get(net.getIdentifier());
			double factor = Double.parseDouble(getArg(command, "factor", args));
			String scale = getArg(command, "scale", args);
			double zoom = view.getZoom();
			// If we have a scale, use that -- otherwise, use the factor
			if (scale != null) {
				view.setZoom(Double.parseDouble(scale));
			} else {
				view.setZoom(zoom*factor);
			}
			result.addMessage("networkview: network '"+net.getIdentifier()+"' zoom set to "+view.getZoom());
			result.addResult("scale", new Double(view.getZoom()));

		} else if (command.equals("get size")) {

		} else if (command.equals("set window")) {

		} else {
			throw new CyCommandException("networkview: unknown command "+command);
		}

		return result;
	}

}
