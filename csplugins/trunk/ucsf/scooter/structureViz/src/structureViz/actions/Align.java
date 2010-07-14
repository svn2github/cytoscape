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
import java.util.HashMap;
import java.util.Iterator;
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
import structureViz.model.ChimeraChain;
import structureViz.model.ChimeraModel;
import structureViz.model.ChimeraStructuralObject;


/**
 * The Align class provides the interface to Chimera for processing
 * requests to align structures.
 */
public class Align {
	public static final String[] attributeKeys = {"RMSD","AlignmentScore","AlignedResidues"};

	/**
	 * Array offset to the RMSD result
	 */
	public static final int RMSD = 0;

	/**
	 * Array offset to the Alignment Score result
	 */
	public static final int SCORE = 1;

	/**
	 * Array offset to the number of aligned pairs result
	 */
	public static final int PAIRS = 2;

	/**
	 * The name of the Cytoscape interaction type to use if
	 * we're asked to create an edge with the results
	 */
	public static final String structureInteraction = "structuralSimilarity";
	private Chimera chimeraObject = null;
	private HashMap results = null;
	private boolean createEdges = false;
	private boolean showSequence = false;
	private boolean createNewEdges = true;

	/**
	 * Create a new Align object
	 *
	 * @param chimeraObject the Chimera interface object that provides our link
	 * to Chimera
	 */
  public Align(Chimera chimeraObject) { 
		this.chimeraObject = chimeraObject;
	}

	/**
	 * Set the flag that tells us whether to create an edge based on the results
	 * or not.
	 *
	 * @param val the flag to set
	 */
	public void setCreateEdges(boolean val) { this.createEdges = val; };

	/**
	 * Set the flag that tells us whether to create a new edge based on the results
	 * or not.
	 *
	 * @param val the flag to set
	 */
	public void setCreateNewEdges(boolean val) { this.createNewEdges = val; };

	/**
	 * Set the flag that tells us whether to show the sequence results when
	 * an alignment is performed.
	 *
	 * @param val the flag to set
	 */
	public void setShowSequence(boolean val) { this.showSequence = val; };

	/**
	 * Get the results
	 *
	 * @param modelName the name of the model to return the results for
	 * @return the array of 3 float results
	 */
	public float[] getResults(String modelName) {
		if (results.containsKey(modelName))
			return (float [])results.get(modelName);
		return null;
	}

	/**
	 * This method calls Chimera to perform a pairwise alignment
	 * between the <i>reference</i> model and all other currently
	 * open Chimera models.
	 *
	 * @param reference the reference model
	 */
	public void alignAll(ChimeraModel reference) {
		List<ChimeraModel> modelList = chimeraObject.getChimeraModels();
		ArrayList<ChimeraStructuralObject>matchList = new ArrayList();
		for (ChimeraModel match: modelList) {
			if (match != reference)
				matchList.add((ChimeraStructuralObject)match);
		}
		align((ChimeraStructuralObject)reference, matchList);
		chimeraObject.chimeraSend("focus");
		if (createEdges)
			setAllAttributes(reference, matchList);
	}

	/**
	 * This method calls Chimera to perform a pairwise alignment
	 * between the <i>reference</i> model and a List of models.
	 *
	 * @param reference the reference model
	 * @param models a List of ChimeraModels to align to the reference
	 */
	public void align(ChimeraStructuralObject reference, List<ChimeraStructuralObject>models) {
		results = new HashMap();

		for (ChimeraStructuralObject match: models) {
			List<String> matchResult = singleAlign(reference, match);
			results.put(match.toString(), parseResults(matchResult));
		}
		chimeraObject.chimeraSend("focus");
		if (createEdges)
			setAllAttributes(reference, models);
	}

	/**
	 * This method calls Chimera to perform a pairwise alignment
	 * between the <i>reference</i> model expressed as a Structure
	 * and a List of Structures.  This is a special version to be 
	 * called from the command menu.
	 *
	 * @param refStruct the reference model expressed as a Structure
	 * @param structures a List of Structures to align to the reference
	 */
	public void align(Structure refStruct, List<Structure> structures) {
		results = new HashMap();
		ArrayList<ChimeraStructuralObject> modelList = new ArrayList();
		ChimeraStructuralObject reference = chimeraObject.getModel(refStruct.name());
		for (Structure matchStruct: structures) {
			ChimeraStructuralObject match = chimeraObject.getModel(matchStruct.name());
			// System.out.println(match);
			ChimeraModel model = match.getChimeraModel();
			modelList.add(model);
			List<String> matchResult = singleAlign(reference, match);
			results.put(model.getModelName(), parseResults(matchResult));
		}
		chimeraObject.chimeraSend("focus");
		if (createEdges)
			setAllAttributes(reference, modelList);
	}

	/**
	 * Ask Chimera to align a single ChimeraModel to a reference ChimeraModel
	 *
	 * @param reference the ChimeraModel to use as a reference model
	 * @param match the ChimeraModel to align to the reference
	 * @return an Iterator over the results
	 */
	private List<String> singleAlign(ChimeraStructuralObject reference, ChimeraStructuralObject match) {
		String command = "matchmaker "+reference.toSpec()+" "+match.toSpec();
		if (reference instanceof ChimeraChain || match instanceof ChimeraChain)
			command = command + " pair ss";
		if (showSequence) {
			command = command + " show true";
		}
		return chimeraObject.commandReply(command);
	}

	/**
	 * Parse the results returned by <b>singleAlign</b> and return an array of
	 * 3 floats with the results of an alignment.
	 *
	 * @param lineIter the iterator over the lines of responses from Chimera
	 * @return the array of floats containing the results from a single alignment
	 */
	private float[] parseResults(List<String> resultsList) {
		float[] results = new float[3];
		int index = -1;
		for (String line: resultsList) {
			// System.out.println(line);
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
		// System.out.println("RMSD = "+results[RMSD]+", score = "+results[SCORE]);
		return results;
	}

	/**
	 * This method is used to set all of the Cytoscape edge attributes 
	 * resulting from a series of alignments.
	 *
	 * @param source the ChimeraModel representing the source of the edge
	 * (the reference structure)
	 * @param targetList the list of targets (aligned structures)
	 */
	private void setAllAttributes(ChimeraStructuralObject source, List<ChimeraStructuralObject> targetList) {
		ChimeraModel sourceModel = source.getChimeraModel();
		for (ChimeraStructuralObject target: targetList) {
			// If our target is a ChimeraModel, we want the model name, otherwise
			// we want the toString
			String modelKey = null;
			if (target instanceof ChimeraModel)
				modelKey = ((ChimeraModel)target).getModelName();
			else
				modelKey = ((ChimeraStructuralObject)target).toString();
			ChimeraModel targetModel = target.getChimeraModel();
			float[] results = getResults(modelKey);
			setEdgeAttributes(results, sourceModel, targetModel);
		}
	}

	/**
	 *
	 * @param results the results values to assign to the edge as attributes
	 * @param from the ChimeraModel that represents the CyNode to use as the source of the edge
	 * @param to the ChimeraModel that represents the CyNode to use as the destination of the edge
	 */
	private void setEdgeAttributes(float[] results, ChimeraModel from, ChimeraModel to) {
		// System.out.println("From: "+from+" To: "+to+" results: "+results);
		CyNetwork network = chimeraObject.getNetworkView().getNetwork();
		CyNode source = from.getStructure().node();
		CyNode target = to.getStructure().node();
		if (source == null || target == null) {
			return;
		}
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
			d = new Double(results[i]);
			edgeAttr.setAttribute(edge.getIdentifier(), attributeKeys[i], d);
		}
	}
}
