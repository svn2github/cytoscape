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
package structureViz.actions;

// System imports
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Properties;

// giny imports
import giny.view.NodeView;

// Cytoscape imports
import cytoscape.*;
import cytoscape.CytoscapeInit;
import cytoscape.view.CytoscapeDesktop;
import cytoscape.view.CyNetworkView;
import cytoscape.data.CyAttributes;
import cytoscape.util.CytoscapeAction;

// structureViz imports
import structureViz.actions.Chimera;
import structureViz.model.Structure;
import structureViz.model.ChimeraModel;


public class Align {
	private static final String[] attributeKeys = {"RMSD","AlignmentScore","AlignedResidues"};
	public static final int RMSD = 0;
	public static final int SCORE = 1;
	public static final int PAIRS = 2;
	public static final String structureInteraction = "structuralSimilarity";
	private Chimera chimeraObject = null;
	private HashMap results = null;
	private boolean createEdges = false;
	private boolean showSequence = false;
	private boolean createNewEdges = true;

  public Align(Chimera chimeraObject) { 
		this.chimeraObject = chimeraObject;
	}

	public void setCreateEdges(boolean val) { this.createEdges = val; };

	public void setCreateNewEdges(boolean val) { this.createNewEdges = val; };

	public void setShowSequence(boolean val) { this.showSequence = val; };

	public float[] getResults(String modelName) {
		return (float [])results.get(modelName);
	}

	public void alignAll(ChimeraModel reference) {
		List modelList = chimeraObject.getChimeraModels();
		ArrayList matchList = new ArrayList();
		Iterator modelIter = modelList.iterator();
		while (modelIter.hasNext()) {
			ChimeraModel match = (ChimeraModel)modelIter.next();
			if (match != reference)
				matchList.add(match);
		}
		align(reference, matchList);
		chimeraObject.command("focus");
		if (createEdges)
			setAllAttributes(reference, matchList);
	}

	public void align(ChimeraModel reference, List models) {
		results = new HashMap();

		Iterator modelIter = models.iterator();
		while (modelIter.hasNext()) {
			ChimeraModel match = (ChimeraModel)modelIter.next();
			Iterator matchResult = singleAlign(reference, match);
			results.put(match.getModelName(), parseResults(matchResult));
		}
		chimeraObject.command("focus");
		if (createEdges)
			setAllAttributes(reference, models);
	}

	// Special version for calling from the command menu
	public void align(Structure refStruct, List structures) {
		results = new HashMap();
		ArrayList modelList = new ArrayList();
		ChimeraModel reference = chimeraObject.getModel(refStruct.name());
		Iterator structIter = structures.iterator();
		while (structIter.hasNext()) {
			Structure matchStruct = (Structure)structIter.next();
			ChimeraModel match = chimeraObject.getModel(matchStruct.name());
			modelList.add(match);
			Iterator matchResult = singleAlign(reference, match);
			results.put(match.getModelName(), parseResults(matchResult));
		}
		chimeraObject.command("focus");
		if (createEdges)
			setAllAttributes(reference, modelList);
	}

	private Iterator singleAlign(ChimeraModel reference, ChimeraModel match) {
		String command = "matchmaker "+reference.toSpec()+" "+match.toSpec();
		if (showSequence) {
			command = command + " show true";
		}
		return chimeraObject.commandReply(command);
	}

	private float[] parseResults(Iterator lineIter) {
		float[] results = new float[3];
		int index = -1;
		while (lineIter.hasNext()) {
			String line = (String)lineIter.next();
			if ((index = line.indexOf("score = ")) > 0) {
				Float score = new Float(line.substring(index+8));
				results[SCORE] = score.floatValue();
			} else if ((index = line.indexOf("RMSD between")) == 0) {
				String[] tokens = line.split(" ");
				Float pairs = new Float(tokens[2]);
				results[PAIRS] = pairs.floatValue();
				Float rmsd = new Float(tokens[6]);
				results[RMSD] = rmsd.floatValue();
			}
		}
		return results;
	}

	private void setAllAttributes(ChimeraModel source, List targetList) {
		Iterator targetIter = targetList.iterator();
		while (targetIter.hasNext()) {
			ChimeraModel target = (ChimeraModel)targetIter.next();
			float[] results = getResults(target.getModelName());
			setEdgeAttributes(results, source, target);
		}
	}

	private void setEdgeAttributes(float[] results, ChimeraModel from, ChimeraModel to) {
		CyNetwork network = chimeraObject.getNetworkView().getNetwork();
		CyNode source = from.getStructure().node();
		CyNode target = to.getStructure().node();
		CyEdge edge = null;
		ArrayList nodeList = new ArrayList(2);
		nodeList.add(source);
		nodeList.add(target);
		List edgeList = network.getConnectingEdges(nodeList);
		CyAttributes edgeAttr = Cytoscape.getEdgeAttributes();
		if (edgeList == null || edgeList.size() == 0 || createNewEdges ) {
			edgeList = new ArrayList();
			// Use Cytoscape.getCyEdge()?
			edge = (CyEdge) Cytoscape.getRootGraph().getEdge(Cytoscape.getRootGraph().createEdge(source, target));
			String edge_name = source.getIdentifier() + " ("+structureInteraction+") "+target.getIdentifier();
			edge.setIdentifier(edge_name);
			edgeAttr.setAttribute(edge.getIdentifier(), "interaction", structureInteraction);
			network.addEdge(edge);
		} else {
			edge = (CyEdge) edgeList.get(0);
		}
		// Now add the attributes
		Double d;
		for (int i = 0; i < 3; i++) {
			d = new Double(1/results[i]);
			edgeAttr.setAttribute(edge.getIdentifier(), attributeKeys[i], d);
		}
	}
}
