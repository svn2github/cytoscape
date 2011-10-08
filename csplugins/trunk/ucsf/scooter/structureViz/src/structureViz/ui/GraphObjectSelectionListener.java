/* vim: set ts=2: */
/**
 * Copyright (c) 2006 The Regents of the University of California.
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
package structureViz.ui;

import structureViz.actions.Chimera;
import structureViz.actions.CyChimera;
import structureViz.model.Structure;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cytoscape.logger.CyLogger;
import cytoscape.view.CyNetworkView;

import giny.model.GraphObject;
import giny.view.GraphView;
import giny.view.GraphViewChangeEvent;
import giny.view.GraphViewChangeListener;

/**
 * This class is used to implement the listener for the
 * context menu on the ModelNavigatorDialog
 */
public class GraphObjectSelectionListener implements GraphViewChangeListener {
	private CyLogger logger;
	private Map<GraphObject, String>selectionMap = null;

	public GraphObjectSelectionListener(CyLogger logger) {
		this.logger = logger;
		selectionMap = new HashMap<GraphObject, String>();
	}
	
	public void graphViewChanged(GraphViewChangeEvent event) {
		if (!CyChimera.selectionEnabled())
			return;
		CyChimera.setSelectionEnabled(false);
		GraphView view = (GraphView)event.getSource();
		if (event.isEdgesSelectedType()) {
			setSelection(view, event.getSelectedEdges(), true);
		} else if (event.isEdgesUnselectedType()) {
			setSelection(view, event.getUnselectedEdges(), false);
		} else if (event.isNodesSelectedType()) {
			setSelection(view, event.getSelectedNodes(), true);
		} else if (event.isNodesUnselectedType()) {
			setSelection(view, event.getUnselectedNodes(), false);
		}
		CyChimera.setSelectionEnabled(true);
	}

	private void setSelection(GraphView view, GraphObject[] goArray, boolean selected) {
		if (goArray == null || goArray.length == 0) return;

		// Get the chimera instance for this view
		Chimera chimera = Chimera.GetChimeraInstance((CyNetworkView)view, logger);

		// Get all of the open structures
		List<Structure> structureList = chimera.getOpenStructs();

		String command = null;

		// For each graph object, see if the selection has changed
		for (Structure structure: structureList) {
			List<String> residueList = new ArrayList<String>();
			List<GraphObject> objList = structure.getGraphObjectList();
			for (int index = 0; index < goArray.length; index++ ) {
				GraphObject object = goArray[index];
				if (objList.contains(object) && structure.getResidueList(object) != null) {
					if (selected) {
						String selString = getSelectionString(chimera, structure, object);
						if (selString != null) {
							selectionMap.put(object, selString);
							// System.out.println("Adding "+object+" to the selection: "+selString);
						} else
							selectionMap.remove(object);
					} else {
						// System.out.println("Removing "+object+" from the selection");
						selectionMap.remove(object);
					}
				} 
			}
		}
		// System.out.println("SelectionMap has "+selectionMap.size()+ " values");
		setResidueSelection(chimera, selectionMap.values());
		// chimera.modelChanged();
	}

	private String getSelectionString(Chimera chimera, Structure structure, 
	                                  GraphObject object) {

		List<String> residueList = structure.getResidueList(object);
		if (residueList == null || residueList.size() == 0) return null;

		int model = structure.modelNumber();
		if (chimera.getChimeraModel(model) == null) return null;

		String residues = "";
		for (String residue: residueList) {
			residues = residues.concat(residue+",");
		}
		residues = residues.substring(0,residues.length()-1);
		return " #"+structure.modelNumber()+":"+residues;
	}

	private void setResidueSelection(Chimera chimera, Collection<String> selStrs) {
		String command = null;
		if (selStrs == null || selStrs.size() == 0) {
			command = "~select";
		} else {
			for (String selStr: selStrs) {
				if (command == null) 
					command = "select "+selStr;
				else
					command = command.concat(" | "+selStr);
			}
		}
		// System.out.println("Selection command: "+command);
		chimera.chimeraSendNoReply(command);
	}
}
