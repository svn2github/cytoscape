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
package coreCommands.namespaces.networkView;

import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.CytoscapeInit;

import cytoscape.command.AbstractCommandHandler;
import cytoscape.command.CyCommandException;
import cytoscape.command.CyCommandHandler;
import cytoscape.command.CyCommandManager;
import cytoscape.command.CyCommandNamespace;
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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * XXX FIXME XXX Description 
 */
public class ExportNetworkView extends AbstractCommandHandler {
	private static String NETWORKVIEW = "networkview";

	private static String EXPORT = "export";

	private static String CURRENT = "current";
	private static String NETWORK = "network";
	private static String FILE = "file";
	private static String TYPE = "type";
	private static String ZOOM = "zoom";

	// Export types
	private static String PNG = "png";
	private static String PDF = "pdf";
	private static String SVG = "svg";
	private static String JPG = "jpg";
	private static String JPEG = "jpeg";
	private static String BMP = "bmp";

	public ExportNetworkView(CyCommandNamespace ns) {
		super(ns);

		// Define our subcommands
		addDescription(EXPORT, "Export a network view as a graphic file");
		addArgument(EXPORT, FILE);
		addArgument(EXPORT, NETWORK, CURRENT);
		addArgument(EXPORT, TYPE, PNG);
		addArgument(EXPORT, ZOOM, "1.0");
	}

	public CyCommandResult execute(String command, Collection<Tunable>args) throws CyCommandException {
		return execute(command, createKVMap(args));
	}

	public CyCommandResult execute(String command, Map<String, Object>args) throws CyCommandException { 
		CyCommandResult result = new CyCommandResult();
		Map<String, CyNetworkView> viewMap = Cytoscape.getNetworkViewMap();
		CyNetwork net = Cytoscape.getCurrentNetwork();

		String netName = getArg(command, NETWORK, args);
		if (netName == null) {
			throw new CyCommandException("networkview: "+command+" requires network argument");
		}
		if (!netName.equalsIgnoreCase(CURRENT)) {
			net = Cytoscape.getNetwork(netName);
			if (net == null)
				throw new CyCommandException("networkview: can't find network: "+netName);
			if (!viewMap.containsKey(netName) && !command.equals("create"))
				throw new CyCommandException("networkview: can't find view for network: "+netName);
		}

		CyNetworkView view = viewMap.get(net.getIdentifier());
		float zoom = Float.parseFloat(getArg(command, ZOOM, args));
		String type = getArg(command, TYPE, args);
		String fileName = getArg(command, FILE, args);
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
		if (type.equalsIgnoreCase(PDF) ){
			exporter = new PDFExporter();
			} else if (type.equalsIgnoreCase(SVG)) {
			exporter = new SVGExporter();
		} else if (type.equalsIgnoreCase(PNG)) {
			exporter = new BitmapExporter(PNG, zoom);
			} else if (type.equalsIgnoreCase(JPG)) {
			exporter = new BitmapExporter(JPG, zoom);
			} else if (type.equalsIgnoreCase(JPEG)) {
			exporter = new BitmapExporter(JPG, zoom);
		} else if (type.equalsIgnoreCase(BMP)) {
				exporter = new BitmapExporter(BMP, zoom);
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
		return result;
	}
}
