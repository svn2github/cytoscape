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
package batchTool.commands;

import java.util.List;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Vector;
import java.io.IOException;
import java.io.File;
import java.io.FileWriter;

import batchTool.commands.ParseException;

import cytoscape.Cytoscape;
import cytoscape.CyNetwork;
import cytoscape.CytoscapeInit;
import cytoscape.view.CyNetworkView;
import cytoscape.view.InternalFrameComponent;
import cytoscape.ding.DingNetworkView;

import ding.view.DGraphView;

// FreeHEP imports
import org.freehep.util.export.ExportFileTypeRegistry;
import org.freehep.util.export.ExportFileType;
import org.freehep.graphicsio.ps.PSExportFileType;
import org.freehep.graphicsio.ps.EPSExportFileType;
import org.freehep.graphicsio.pdf.PDFExportFileType;
import org.freehep.graphicsio.gif.GIFExportFileType;
import org.freehep.graphicsio.svg.SVGExportFileType;
import org.freehep.graphicsio.emf.EMFExportFileType;
import org.freehep.graphicsio.swf.SWFExportFileType;
import org.freehep.graphicsio.exportchooser.ImageExportFileType;

//
import cytoscape.data.readers.GMLWriter;
import cytoscape.data.readers.GMLParser;
import cytoscape.data.readers.GMLReader;
import cytoscape.data.writers.XGMMLWriter;
import cytoscape.data.writers.InteractionWriter;

enum ExportObject {NONE, NETWORK, EDGEATTR, NODEATTR, VIZMAP};
enum ExportType {NONE, XGMML, GML, SIF, PSI_MI, PSI_MI_1, 
                 EPS, SWF, PDF, PS, SVG, EMF, GIF, PNG, JPG};

/**
 * The open command opens a Cytoscape session file
 */
public class ExportCommand extends AbstractCommand {
	private String fileName = null;
	private ExportObject object = ExportObject.NONE;
	private ExportType type = ExportType.NONE;
	private HashMap<String,String> optionMap = null;

	/**
	 * commandName returns the command name.  This is used to build the
	 * hash table of commands to hand to the command parser
	 *
	 * @return name of the command
	 */
	public String commandName() { return "export"; }

	/**
	 * parse is the main parse routine.  It is handed the entire command
	 * along with all of its arguments.  If the command is successfully
	 * parsed, the number of arguments actually read is returned.
	 *
	 * @param args the arguments to the command.  The "export" command
	 * takes an initial, mandatory argument, which must be the object
	 * that we are exporting, followed by either an additional object
	 * name, then a "to filename" or, in the case of export network, an
	 * "as type" clause to indicate the type.
	 *
	 * export vizmap to filename
	 * export node attributes to filename
	 * export edge attributes to filename
	 * export network as {XGMML,PSI-MI,GML,SIF,EPS,SWF,PDF,PS,SVG,EMF,GIF,PNG,JPG} to filename
	 */
	public int parse(String[] args) throws ParseException {
		// Second argument must be what we want to export: network, vizmap, node attributes,
		// edge attributes, graphics
		String obj = args[1];
		System.out.println("Export "+obj);

		// Get the rest of the information.
		if (obj.equals("network")) {
			object = ExportObject.NETWORK;

			// Get our "as" clause
			type = getAsClause(args);

			// Get our "from" clause
			fileName = getToClause(args);
			if (fileName == null)
				throw new ParseException("export network requires a file name", -1);

			// Get our options, if we're exporting graphics
			if (isGraphic(type)) {
				optionMap = getOptionMap(args);
			}

		} else if (obj.equals("node")) {
			object = ExportObject.NODEATTR;
			if (!("attributes".startsWith(args[2]))) {
				throw new ParseException("Don't know how to export node "+args[2],0);
			}
		} else if (obj.equals("edge")) {
			object = ExportObject.EDGEATTR;
			if (! "attributes".startsWith(args[2])) {
				throw new ParseException("Don't know how to export edge "+args[2],0);
			}
		} else if (obj.equals("vizmap")) {
			object = ExportObject.VIZMAP;
		}

		return args.length;
	}

	/**
	 * read the session
	 *
	 * @param substitutions reserved for future use
	 */
	public int execute(String[] substitutions) throws Exception {
		// Do the appropriate substitutions (if any)
		System.out.println("ExportCommand: executing");
		if (object == ExportObject.NETWORK) {
			if (isNetwork(type)) {
				exportNetwork();
			} else if (isGraphic(type)) {
				exportGraphic();
			}
		}
		return -1;
	}

	private void exportGraphic() throws ParseException,IOException {
		CyNetworkView curr = Cytoscape.getCurrentNetworkView();
		if (curr == Cytoscape.getNullNetworkView()) {
			return;
		}

		// Get the component to export
		InternalFrameComponent ifc = 
			Cytoscape.getDesktop().getNetworkViewManager().getInternalFrameComponent(curr);

		// Handle the exportTextAsShape property
		DGraphView theViewToPrint = (DingNetworkView) curr;
    boolean exportTextAsShape =
        new Boolean(CytoscapeInit.getProperties().getProperty("exportTextAsShape")).booleanValue();
    theViewToPrint.setPrintingTextAsShape(exportTextAsShape);

/*
		ExportFileTypeRegistry r = ExportFileTypeRegistry.getDefaultInstance(null);
		List<ExportFileType> l = r.get();
		Iterator<ExportFileType>ftIter = l.iterator();
		while (ftIter.hasNext()) {
			ExportFileType ft = ftIter.next();
			System.out.println(ft.getDescription());
			System.out.println(ft);
		}
*/

		// Now get the right export handler & handle any options
		ExportFileType exportFileType = null;
		if (type == ExportType.EPS) {
			exportFileType = new EPSExportFileType();
		} else if (type == ExportType.SWF) {
			exportFileType = new SWFExportFileType();
		} else if (type == ExportType.PDF) {
			exportFileType = new PDFExportFileType();
		} else if (type == ExportType.PS) {
			exportFileType = new PSExportFileType();
		} else if (type == ExportType.SVG) {
			exportFileType = new SVGExportFileType();
		} else if (type == ExportType.EMF) {
			exportFileType = new EMFExportFileType();
		} else if (type == ExportType.GIF) {
			exportFileType = new GIFExportFileType();
		} else if (type == ExportType.PNG) {
			exportFileType = ImageExportFileType.getInstance("png");
		} else if (type == ExportType.JPG) {
			exportFileType = ImageExportFileType.getInstance("jpg");
		}

		// Handle filename changes
		String[] acceptableExtensions = exportFileType.getExtensions();
		File outputFile = new File(fileName);
		if (!exportFileType.checkExtension(outputFile, acceptableExtensions)) {
			throw new ParseException("Illegal extension for file type", -1);
		}

		try {
			exportFileType.exportToFile(outputFile, ifc, ifc, null, "Cytoscape");
		} catch (IOException e) {
			System.out.println("Unable to export "+fileName+": "+e.getMessage());
			return;
		}
	}

	private void exportNetwork() throws Exception {
		Object[] ret_val = new Object[3];
		List list = null;
		CyNetwork network = Cytoscape.getCurrentNetwork();
		CyNetworkView view = Cytoscape.getCurrentNetworkView();
		ret_val[0] = network;

		if (type == ExportType.XGMML) {
			if (!fileName.endsWith(".xgmml"))
				fileName = fileName + ".xgmml";

			FileWriter fileWriter = new FileWriter(fileName);
			XGMMLWriter xgmmlWriter = new XGMMLWriter(network, view);

			try {
				xgmmlWriter.write(fileWriter);
			} finally {
				fileWriter.close();
			}
			ret_val[2] = new Integer(Cytoscape.FILE_XGMML);

		} else if (type == ExportType.GML) {
			if (!fileName.endsWith(".gml"))
				fileName = fileName + ".gml";

			// Get the GML data
			GMLReader reader = (GMLReader) network.getClientData("GML");
			if (reader != null) {
				list = reader.getList();
			} else {
				list = new Vector();
			}

			FileWriter fileWriter = new FileWriter(fileName);
			GMLWriter gmlWriter = new GMLWriter();
			gmlWriter.writeGML(network, view, list);
			GMLParser.printList(list, fileWriter);
			fileWriter.close();

			ret_val[2] = new Integer(Cytoscape.FILE_GML);
		} else if (type == ExportType.SIF) {
			if (!fileName.endsWith(".sif"))
				fileName = fileName + ".sif";

			FileWriter fileWriter = new FileWriter(fileName);
			InteractionWriter.writeInteractions(network, fileWriter, null);
			fileWriter.close();

			ret_val[2] = new Integer(Cytoscape.FILE_SIF);
		}

		ret_val[1] = new File(fileName).toURI();
		Cytoscape.firePropertyChange(Cytoscape.NETWORK_SAVED, null, ret_val);
	}

	private ExportType getAsClause(String[] args) throws ParseException {
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("as")) {
				// Figure out what we're exporting
				String type = args[i+1];
				if (type.equals("xgmml"))
					return ExportType.XGMML;
				else if (type.equals("gml"))
					return ExportType.GML;
				else if (type.equals("sif"))
					return ExportType.SIF;
				else if (type.equals("psi-mi") || type.equals("psi-mi-2"))
					return ExportType.PSI_MI;
				else if (type.equals("psi-mi-1"))
					return ExportType.PSI_MI_1;
				else if (type.equals("eps"))
					return ExportType.EPS;
				else if (type.equals("swf"))
					return ExportType.SWF;
				else if (type.equals("pdf"))
					return ExportType.PDF;
				else if (type.equals("ps"))
					return ExportType.PS;
				else if (type.equals("svg"))
					return ExportType.SVG;
				else if (type.equals("emf"))
					return ExportType.EMF;
				else if (type.equals("gif"))
					return ExportType.GIF;
				else if (type.equals("png"))
					return ExportType.PNG;
				else if (type.equals("JPG"))
					return ExportType.JPG;

				throw new ParseException("Unknown export type: "+type, -1);
			}
		}
		throw new ParseException("No 'as export_type' clause found", -1);
	}

	private String getToClause(String[] args) {
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("to")) {
				return args[i+1];
			}
		}
		return null;
	}

	private HashMap getOptionMap(String[] args) {
		HashMap<String,String>optionMap = null;
		// step through all of the args
		for (int i = 0; i < args.length; i++) {
			// See if we have an "="
			String[] pair = args[i].split("=");
			if (pair.length == 2) {
				// Yes, add it to the map
				if (optionMap == null) optionMap = new HashMap();
				optionMap.put(pair[0],pair[1]);
			}
		}
		return optionMap;
	}

	private boolean isGraphic(ExportType type) {
		if (type == ExportType.EPS ||
		    type == ExportType.SWF ||
		    type == ExportType.PDF ||
		    type == ExportType.PS ||
		    type == ExportType.SVG ||
		    type == ExportType.EMF ||
		    type == ExportType.GIF ||
		    type == ExportType.PNG ||
		    type == ExportType.JPG) {
			return true;
		}

		return false;
	}

	private boolean isNetwork(ExportType type) {
		if (type == ExportType.XGMML ||
		    type == ExportType.GML ||
		    type == ExportType.SIF ||
		    type == ExportType.PSI_MI) {
			return true;
		}

		return false;
	}

}


