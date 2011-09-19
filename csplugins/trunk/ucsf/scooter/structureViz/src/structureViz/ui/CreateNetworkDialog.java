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

// System imports
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import javax.swing.border.EtchedBorder;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// Cytoscape imports
import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.data.Semantics;
import cytoscape.layout.LayoutProperties;
import cytoscape.layout.Tunable;
import cytoscape.view.CyNetworkView;

import giny.view.NodeView;

// StructureViz imports
import structureViz.actions.Chimera;
import structureViz.model.ChimeraModel;

/**
 */
public class CreateNetworkDialog extends JDialog implements ActionListener {
	// Instance variables
	Chimera chimeraObject;
	LayoutProperties properties;
	boolean includeContacts = true;
	boolean includeClashes = false;
	boolean includeHBonds = false;
	int interactionBetween = 2; // Between selection & other models
	static final int BETWEENMODELS = 0;
	static final int BETWEENSELMODELS = 1;
	static final int BETWEENALL = 2;
	static final String[] interactionArray = {"Between models", "Between selection & other models", "Between selection and all atoms"};
	static final String CLASHCOMMAND = "findclash sel makePseudobonds false log true namingStyle command";

	/**
	 * Create a CreateNetworkDialog
	 *
	 * @param parent the Frame acting as the parent of this Dialog
	 * @param object the Chimera interface object
	 */
	public CreateNetworkDialog (Dialog parent, Chimera object) {
		super(parent, false);
		chimeraObject = object;
		properties = new LayoutProperties("CreateNetworkDialog");
		initTunables();
		initComponents();
	}

	/**
	 * Initialize all of the graphical components of the dialog
	 */
	private void initComponents() {
		this.setTitle("Create Network Dialog");

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		// Create a panel for the main content
		JPanel dataPanel = new JPanel();
		BoxLayout layout = new BoxLayout(dataPanel, BoxLayout.PAGE_AXIS);
		dataPanel.setLayout(layout);

		// Use Tunables for the checkboxes, etc.
		dataPanel.add(properties.getTunablePanel());

		// Add the button box
		JPanel buttonBox = new JPanel();
		JButton createButton = new JButton("Create network");
		createButton.setActionCommand("create");
		createButton.addActionListener(this);

		JButton doneButton = new JButton("Cancel");
		doneButton.setActionCommand("done");
		doneButton.addActionListener(this);
		buttonBox.add(createButton);
		buttonBox.add(doneButton);
		buttonBox.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		buttonBox.setMaximumSize(new Dimension(1000,35));
		dataPanel.add(buttonBox);
		
		setContentPane(dataPanel);
	}

	private void initTunables() {
		properties.add(new Tunable("includeContacts", "Include contacts", 
		                           Tunable.BOOLEAN, includeContacts));
		properties.add(new Tunable("includeClashes", "Include clashes", 
		                           Tunable.BOOLEAN, includeClashes));
		properties.add(new Tunable("includeHBonds", "Include hydrogen bonds (overlaps with contacts)", 
		                           Tunable.BOOLEAN, includeHBonds));
		properties.add(new Tunable("interaction", "Include interactions",
		                           Tunable.LIST, new Integer(interactionBetween),
		                           (Object) interactionArray, (Object) null, 0));
	}

	private void updateTunables(boolean force) {
		Tunable t = properties.get("includeContacts");
		if ((t != null) && (t.valueChanged() || force))
			includeContacts = ((Boolean) t.getValue()).booleanValue();

		t = properties.get("includeClashes");
		if ((t != null) && (t.valueChanged() || force))
			includeClashes = ((Boolean) t.getValue()).booleanValue();

		t = properties.get("includeHBonds");
		if ((t != null) && (t.valueChanged() || force))
			includeHBonds = ((Boolean) t.getValue()).booleanValue();

		t = properties.get("interaction");
		if ((t != null) && (t.valueChanged() || force))
			interactionBetween = ((Integer) t.getValue()).intValue();

	}
	
	/**
	 * The method called to actually execute the command.
	 */
	public void actionPerformed(ActionEvent e) {
		if ("done".equals(e.getActionCommand())) {
			setVisible(false);
			return;
		}
		if ("create".equals(e.getActionCommand())) {
			updateTunables(true);
			List<String> edgeList = null;
			String cutoff = "";
			String type = "Clashes";
			// Send the commands to Chimera and get the results
			if (includeContacts) {
				type = "Contacts";
				cutoff = "overlapCutoff -0.4 hbondAllowance 0.0";
			}
			if (includeClashes || includeContacts) {
	 			String command = "findclash sel makePseudobonds false log true namingStyle command "+cutoff ;
				if (interactionBetween == BETWEENMODELS) {
				} else if (interactionBetween == BETWEENSELMODELS)
					command = command.concat(" test other");
				else if (interactionBetween == BETWEENALL)
					command = command.concat(" test model");
				
				List<String>replyList = chimeraObject.commandReply(command);
				printReply(replyList);
				edgeList = parseClashReplies(replyList, type);
			}
			if (includeHBonds) {
				String command = null;
				if (interactionBetween == BETWEENMODELS) {
				} else if (interactionBetween == BETWEENSELMODELS)
					command = "findhbond selRestrict any intermodel true intramodel false makePseudobonds false log true namingStyle command";
				else if (interactionBetween == BETWEENALL)
					command = "findhbond selRestrict any intermodel true intramodel true makePseudobonds false log true namingStyle command";
				List<String>replyList = chimeraObject.commandReply(command);
				if (edgeList == null)
					edgeList = parseHBondReplies(replyList);
				else
					edgeList.addAll(parseHBondReplies(replyList));
				printReply(replyList);
			}
			// We've got a list of edges, now we need to create the nodes and edges
			// and assign the attributes we want, then we can create the network as a child of the current network
			System.out.println("edgeList has "+edgeList.size()+" entries");
			int[] edges = new int[edgeList.size()];
			int[] nodes = new int[edgeList.size()*2];
			int edgeCount = 0;
			for (String edge: edgeList) {
				System.out.println("Edge "+(edgeCount+1)+": "+edge);
				createNodesAndEdge(edge, nodes, edges, edgeCount);
				edgeCount++;
			}

			CyNetwork network = Cytoscape.getCurrentNetwork();
			String name = network.getTitle();

			// Create the network
			Cytoscape.createNetwork(nodes, edges, "Interaction from "+name, network, true);

			setVisible(false);
			return;
		}
	}

	private void printReply(List<String> replyLog) {
		for (String str: replyLog) System.out.println(str);
	}

	/**
 	 * Clash replies look like:
 	 * 	*preamble*
 	 * 	*header line*
 	 * 	*clash lines*
 	 * where preamble is:
 	 * 	Allowed overlap: -0.4
 	 * 	H-bond overlap reduction: 0
 	 * 	Ignore contacts between atoms separated by 4 bonds or less
 	 * 	Ignore intra-residue contacts
 	 * 	44 contacts
 	 * and the header line is:
 	 * 	atom1  atom2  overlap  distance
 	 * and the clash lines look like:
 	 *	:2470.A@N    :323.A@OD2  -0.394  3.454
 	 */
	private List<String> parseClashReplies(List<String> replyLog, String type) {
		// Scan for our header line
		boolean foundHeader = false;
		int index = 0;
		for (index = 0; index < replyLog.size(); index++) {
			String str = replyLog.get(index);
			System.out.println("Line "+index+": "+str);
			if (str.trim().startsWith("atom1")) {
				foundHeader = true;
				break;
			}
		}
		if (!foundHeader) return null;

		List<String> edgeList = new ArrayList<String>();

		for (++index; index < replyLog.size(); index++) {
			System.out.println("Line "+index+": "+replyLog.get(index));
			String[] line = replyLog.get(index).trim().split("\\s+");
			if (line.length != 4) continue;
			
			System.out.println("atom1 = "+line[0]+" atom2 = "+line[1]+" overlap = "+line[2]+" distance = "+line[3]);
			edgeList.add(fixResidue(line[0])+"\t"+type+"\t"+fixResidue(line[1])+"\t"+line[2]+"\t"+line[3]);
		}

		return edgeList;
		
	}


	// H-bonds (donor, acceptor, hydrogen, D..A dist, D-H..A dist):
	/**
 	 * Finding acceptors in model '1tkk'
 	 * Building search tree of acceptor atoms
 	 * Finding donors in model '1tkk'
 	 * Matching donors in model '1tkk' to acceptors
 	 * Finding intermodel H-bonds
 	 * Finding intramodel H-bonds
 	 * Constraints relaxed by 0.4 angstroms and 20 degrees
 	 * Models used:
 	 * 	#0 1tkk
 	 * 	H-bonds (donor, acceptor, hydrogen, D..A dist, D-H..A dist):
 	 * 	ARG 24.A NH1  GLU 2471.A OE1  no hydrogen  3.536  N/A
 	 * 	LYS 160.A NZ  GLU 2471.A O    no hydrogen  2.680  N/A
 	 * 	LYS 162.A NZ  ALA 2470.A O    no hydrogen  3.022  N/A
 	 * 	LYS 268.A NZ  GLU 2471.A O    no hydrogen  3.550  N/A
 	 * 	ILE 298.A N   GLU 2471.A OE2  no hydrogen  3.141  N/A
 	 * 	ALA 2470.A N  THR 135.A OG1   no hydrogen  2.814  N/A
 	 * 	ALA 2470.A N  ASP 321.A OD1   no hydrogen  2.860  N/A
 	 * 	ALA 2470.A N  ASP 321.A OD2   no hydrogen  3.091  N/A
 	 * 	ALA 2470.A N  ASP 323.A OD1   no hydrogen  2.596  N/A
 	 * 	ALA 2470.A N  ASP 323.A OD2   no hydrogen  3.454  N/A
 	 * 	GLU 2471.A N  SER 296.A O     no hydrogen  2.698  N/A
 	 * 	HOH 2541.A O  GLU 2471.A OE1  no hydrogen  2.746  N/A
 	 * 	HOH 2577.A O  GLU 2471.A O    no hydrogen  2.989  N/A
 	 */
	private List<String> parseHBondReplies(List<String> replyLog) {
		// Scan for our header line
		boolean foundHeader = false;
		int index = 0;
		for (index = 0; index < replyLog.size(); index++) {
			String str = replyLog.get(index);
			System.out.println("Line "+index+": "+str);
			if (str.trim().startsWith("H-bonds")) {
				foundHeader = true;
				break;
			}
		}
		if (!foundHeader) return null;

		List<String> edgeList = new ArrayList<String>();

		for (++index; index < replyLog.size(); index++) {
			System.out.println("Line "+index+": "+replyLog.get(index));
			String[] line = replyLog.get(index).trim().split("\\s+");
			if (line.length != 6 && line.length != 7) continue;
			
			String atom1 = line[0];
			String atom2 = line[1];
			String distance = line[3];
			if (line[2].equals("no") && line[3].equals("hydrogen"))
				distance = line[4];
			edgeList.add(fixResidue(atom1)+"\tHBond\t"+fixResidue(atom2)+"\t\t"+distance);
		}
		return edgeList;
	}

	private String fixResidue(String residue) {
		int atIndex = residue.indexOf('@');
		if (atIndex == -1) return residue;
		return residue.substring(0, atIndex);
	}

	private void createNodesAndEdge(String edgeSpec, int[] nodes, int[] edges, int edgeCount) {
		CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
		CyAttributes edgeAttributes = Cytoscape.getEdgeAttributes();

		String[] edgeParts = edgeSpec.split("\t");
		CyNode node1 = Cytoscape.getCyNode(makeFunctionalResidue(edgeParts[0]), true);
		CyNode node2 = Cytoscape.getCyNode(makeFunctionalResidue(edgeParts[2]), true);
		nodeAttributes.setAttribute(node1.getIdentifier(), "FunctionalResidues", node1.getIdentifier());
		nodeAttributes.setAttribute(node2.getIdentifier(), "FunctionalResidues", node2.getIdentifier());
		CyEdge edge = Cytoscape.getCyEdge(node1, node2, Semantics.INTERACTION, edgeParts[1], true);
		if (edgeParts[3] != null && edgeParts[3].length() > 0)
			edgeAttributes.setAttribute(edge.getIdentifier(), "Overlap", Double.valueOf(edgeParts[3]));
		edgeAttributes.setAttribute(edge.getIdentifier(), "Distance", Double.valueOf(edgeParts[4]));
		edges[edgeCount] = edge.getRootGraphIndex();
		nodes[edgeCount*2] = node1.getRootGraphIndex();
		nodes[edgeCount*2+1] = node2.getRootGraphIndex();
	}

	private String makeFunctionalResidue(String alias) {
		int model = 0;
		int submodel = 0;
		String[] modelSplit = alias.split(":");
		if (modelSplit[0].length() > 0) {
			String[] subSplit = modelSplit[0].substring(1).split(".");
			model = Integer.parseInt(subSplit[0]);
			if (subSplit.length > 1)
				submodel = Integer.parseInt(subSplit[1]);
		}
		// Get the model
		ChimeraModel cModel = chimeraObject.getChimeraModel(model, submodel);
		return "#"+cModel.getModelName()+":"+modelSplit[1];
	}
}

