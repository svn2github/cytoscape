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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import cytoscape.layout.TunableListener;
import cytoscape.view.CyNetworkView;

import giny.view.NodeView;

// StructureViz imports
import structureViz.actions.Chimera;
import structureViz.actions.CyChimera;
import structureViz.model.ChimeraChain;
import structureViz.model.ChimeraModel;
import structureViz.model.ChimeraResidue;
import structureViz.model.ChimeraStructuralObject;
import structureViz.model.Structure;
import structureViz.model.StructureUtils;
import structureViz.model.Structure.StructureType;

/**
 */
public class CreateNetworkDialog extends JDialog implements ActionListener,TunableListener {
	// Instance variables
	Chimera chimeraObject;
	LayoutProperties properties;
	boolean includeContacts = true;
	boolean includeClashes = false;
	boolean includeHBonds = false;
	boolean includeConnectivity = false;
	boolean includeConnectivityDistance = false;
	int interactionBetween = 2; // Between selection & other models
	static final int BETWEENMODELS = 0;
	static final int BETWEENSELMODELS = 1;
	static final int BETWEENALL = 2;
	static final String[] interactionArray = {"Between models", "Between selection & other models", "Between selection and all atoms"};
	static final String CLASHCOMMAND = "findclash sel makePseudobonds false log true namingStyle command";
	static final String DISTANCE_ATTR = "MinimumDistance";
	static final String OVERLAP_ATTR = "MaximumOverlap";
	static final String RESIDUE_ATTR = "FunctionalResidues";
	static final String SEED_ATTR = "SeedResidue";
	static final String BACKBONE_ATTR = "BackboneInteraction";
	static final String SIDECHAIN_ATTR = "SideChainInteraction";

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
		Tunable t = new Tunable("includeConnectivity", "Include connectivity",
		                        Tunable.BOOLEAN, includeConnectivity);
		t.addTunableValueListener(this);
		properties.add(t);
		t = new Tunable("includeConnectivityDistance", "Calculate connectivity distances (more time consuming)",
		                Tunable.BOOLEAN, includeConnectivityDistance);
		if (!includeConnectivity)
			t.setImmutable(true);
		properties.add(t);
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

		t = properties.get("includeConnectivity");
		if ((t != null) && (t.valueChanged() || force))
			includeConnectivity = ((Boolean) t.getValue()).booleanValue();

		t = properties.get("includeConnectivityDistance");
		if ((t != null) && (t.valueChanged() || force))
			includeConnectivityDistance = ((Boolean) t.getValue()).booleanValue();

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
			List<CyEdge> edgeList = null;
			Map<CyNode, CyNode> nodeMap = new HashMap<CyNode, CyNode>();
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
					// Get the first model
					ChimeraModel model = chimeraObject.getChimeraModels().get(0);
					int modelNumber = model.getModelNumber();
					// Create the command
					command = "findclash #"+modelNumber+" makePseudobonds false log true namingStyle command test other "+cutoff;
				} else if (interactionBetween == BETWEENSELMODELS)
					command = command.concat(" test other");
				else if (interactionBetween == BETWEENALL)
					command = command.concat(" test model");
				
				List<String>replyList = chimeraObject.commandReply(command);
				// printReply(replyList);
				edgeList = parseClashReplies(replyList, nodeMap, type);
			}
			if (includeHBonds) {
				// Get the first model
				ChimeraModel model = chimeraObject.getChimeraModels().get(0);
				int modelNumber = model.getModelNumber();
				String command = 
					"findhbond spec #"+modelNumber+" intramodel false intermodel true makePseudobonds false log true namingStyle command";
				if (interactionBetween == BETWEENMODELS) {
					command = "findhbond selRestrict any intermodel true makePseudobonds false log true namingStyle command";
				} else if (interactionBetween == BETWEENSELMODELS)
					command = command.concat(" intramodel false");
				else if (interactionBetween == BETWEENALL)
					command = command.concat(" intramodel true");
				List<String>replyList = chimeraObject.commandReply(command);
				if (edgeList == null)
					edgeList = parseHBondReplies(replyList, nodeMap);
				else
					edgeList.addAll(parseHBondReplies(replyList, nodeMap));
				// printReply(replyList);
			}
			if (includeConnectivity) {
				String command = "listphysicalchains";
				List<String>replyList = chimeraObject.commandReply(command);
				edgeList.addAll(parseConnectivityReplies(replyList, new ArrayList<CyNode>(nodeMap.keySet())));
			}

			int[] edges = new int[edgeList.size()];
			int[] nodes = new int[edgeList.size()*2];
			int edgeCount = 0;
			for (CyEdge edge: edgeList) {
				edges[edgeCount] = edge.getRootGraphIndex();
				nodes[edgeCount*2] = edge.getSource().getRootGraphIndex();
				nodes[edgeCount*2+1] = edge.getTarget().getRootGraphIndex();
				edgeCount++;
			}

			// Create the network
			CyNetwork network = Cytoscape.getCurrentNetwork();
			String name = network.getTitle();
			CyNetwork newNetwork = Cytoscape.createNetwork(nodes, edges, "Interaction from "+name, network, true);
			
			// Set vizmap

			// Do a layout

			// Make it current
			Cytoscape.setCurrentNetwork(newNetwork.getIdentifier());
			Cytoscape.setCurrentNetworkView(newNetwork.getIdentifier());

			// Activate structureViz for all of our nodes
			CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
			for (CyNode node: nodeMap.keySet()) {
				String residueSpec = nodeAttributes.getStringAttribute(node.getIdentifier(), RESIDUE_ATTR);
				String structure = CyChimera.findStructures(residueSpec);
				Structure s = Structure.getStructure(structure, node, StructureType.PDB_MODEL);
				s.setResidueList(node, residueSpec);
			}

			setVisible(false);
			return;
		}
	}

	public void tunableChanged(Tunable t) {
		if (t.getName().equals("includeConnectivity")) {
			Tunable icd = properties.get("includeConnectivityDistance");
			if (((Boolean)t.getValue()).booleanValue()) {
				icd.setImmutable(false);
			} else {
				icd.setImmutable(true);
			}
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
	private List<CyEdge> parseClashReplies(List<String> replyLog, Map<CyNode, CyNode>nodes, String type) {
		// Scan for our header line
		boolean foundHeader = false;
		int index = 0;
		for (index = 0; index < replyLog.size(); index++) {
			String str = replyLog.get(index);
			if (str.trim().startsWith("atom1")) {
				foundHeader = true;
				break;
			}
		}
		if (!foundHeader) return null;

		Map<CyEdge, Double> distanceMap = new HashMap<CyEdge, Double>();
		Map<CyEdge, Double> overlapMap = new HashMap<CyEdge, Double>();
		for (++index; index < replyLog.size(); index++) {
			String[] line = replyLog.get(index).trim().split("\\s+");
			if (line.length != 4) continue;

			CyEdge edge = createEdge(nodes, line[0], line[1], type);

			updateMap(distanceMap, edge, line[3], -1); // We want the smallest distance
			updateMap(overlapMap, edge, line[2], 1); // We want the largest overlap
		}

		CyAttributes edgeAttributes = Cytoscape.getEdgeAttributes();
		// OK, now update the edge attributes we want
		for (CyEdge edge: distanceMap.keySet()) {
			edgeAttributes.setAttribute(edge.getIdentifier(), DISTANCE_ATTR, distanceMap.get(edge));
			edgeAttributes.setAttribute(edge.getIdentifier(), OVERLAP_ATTR, overlapMap.get(edge));
		}

		return new ArrayList<CyEdge>(distanceMap.keySet());
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
	private List<CyEdge> parseHBondReplies(List<String> replyLog, Map<CyNode, CyNode>nodes) {
		// Scan for our header line
		boolean foundHeader = false;
		int index = 0;
		for (index = 0; index < replyLog.size(); index++) {
			String str = replyLog.get(index);
			if (str.trim().startsWith("H-bonds")) {
				foundHeader = true;
				break;
			}
		}
		if (!foundHeader) return null;

		Map<CyEdge, Double> distanceMap = new HashMap<CyEdge, Double>();
		for (++index; index < replyLog.size(); index++) {
			String[] line = replyLog.get(index).trim().split("\\s+");
			if (line.length != 6 && line.length != 7) continue;
			
			CyEdge edge = createEdge(nodes, line[0], line[1], "HBond");

			String distance = line[3];
			if (line[2].equals("no") && line[3].equals("hydrogen"))
				distance = line[4];
			updateMap(distanceMap, edge, distance, -1); // We want the smallest distance
		}

		CyAttributes edgeAttributes = Cytoscape.getEdgeAttributes();
		// OK, now update the edge attributes we want
		for (CyEdge edge: distanceMap.keySet()) {
			edgeAttributes.setAttribute(edge.getIdentifier(), DISTANCE_ATTR, distanceMap.get(edge));
		}

		return new ArrayList<CyEdge>(distanceMap.keySet());
	}

	/**
 	 * Parse the connectivity information from Chimera.  The data is of the form:
 	 * physical chain #0:283.A #0:710.A
 	 * physical chain #0:283.B #0:710.B
 	 * physical chain #0:283.C #0:710.C
 	 *
 	 * We don't use this data to create new nodes -- only new edges.  If two nodes are within the
 	 * same physical chain, we connect them with a "Connected" edge
 	 */
	private List<CyEdge> parseConnectivityReplies(List<String> replyLog, List<CyNode>nodes) {
		List<CyEdge> edgeList = new ArrayList<CyEdge>();
		List<ChimeraResidue[]> rangeList = new ArrayList<ChimeraResidue[]>();
		for (String line: replyLog) {
			String[] tokens = line.split(" ");
			if (tokens.length != 4) continue;
			String start = tokens[2];
			String end = tokens[3];

			ChimeraResidue[] range = new ChimeraResidue[2];

			// Get the residues from the reside spec
			range[0] = StructureUtils.getResidue(start, chimeraObject);
			range[1] = StructureUtils.getResidue(end, chimeraObject);
			rangeList.add(range);
		}

		// If we don't have any nodes, get all of the residues in the connectivity
		// list and create them as nodes

		// For each node pair, figure out if the pair is connected
		for (int i = 0; i < nodes.size(); i++) {
			CyNode node1 = nodes.get(i);
			// System.out.println("Getting the range for the first node..."+node1);
			ChimeraResidue[] range = getRange(rangeList, node1);
			if (range == null) continue;
			for (int j = i+1; j < nodes.size(); j++) {
				CyNode node2 = nodes.get(j);
				// System.out.println("Seeing if node2 "+node2+" is in the range...");
				if (inRange(range, node2)) {
					// System.out.println("....it is");
					// These two nodes are connected
					CyEdge edge = Cytoscape.getCyEdge(node1, node2, Semantics.INTERACTION, "connected", true);
					edgeList.add(createConnectivityEdge(node1, node2));
				}
			}
		}

		// Now, make the edges based on whether any pair of nodes are in the same range
		return edgeList;
	}

	private CyEdge createEdge(Map<CyNode, CyNode>nodes, String sourceAlias, String targetAlias, String type) {
		// Create our two nodes.  Note that makeResidueNode also adds three attributes:
		//  1) FunctionalResidues
		//  2) Seed
		//  3) SideChainOnly
		CyNode source = makeResidueNode(sourceAlias);
		CyNode target = makeResidueNode(targetAlias);
		nodes.put(source, source);
		nodes.put(target, target);

		// Create our edge
		return Cytoscape.getCyEdge(source, target, Semantics.INTERACTION, type, true);
	}

	private CyEdge createConnectivityEdge(CyNode node1, CyNode node2) {
		CyEdge edge = Cytoscape.getCyEdge(node1, node2, Semantics.INTERACTION, "connected", true);

		// Get the residue for node1 and node2 and ask Chimera to calculate the distance
		CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
		String residueAttr = nodeAttributes.getStringAttribute(node1.getIdentifier(), RESIDUE_ATTR); 
		ChimeraStructuralObject cso1 = StructureUtils.fromAttribute(residueAttr, chimeraObject);
		residueAttr = nodeAttributes.getStringAttribute(node2.getIdentifier(), RESIDUE_ATTR); 
		ChimeraStructuralObject cso2 = StructureUtils.fromAttribute(residueAttr, chimeraObject);
		if (cso1 instanceof ChimeraResidue && cso2 instanceof ChimeraResidue) {
			String spec1 = cso1.toSpec()+"@CA";
			String spec2 = cso2.toSpec()+"@CA";
			System.out.println("Getting distance between "+spec1+" and "+spec2);
		
			List<String>replyList = chimeraObject.commandReply("distance "+spec1+" "+spec2);
			int offset = replyList.get(0).indexOf(':');
			Double distance = Double.valueOf(replyList.get(0).substring(offset+1));
			CyAttributes edgeAttributes = Cytoscape.getEdgeAttributes();
			edgeAttributes.setAttribute(edge.getIdentifier(), DISTANCE_ATTR, distance);
			chimeraObject.chimeraSend("~distance "+spec1+" "+spec2);
		}
		return edge;
	}

	private CyNode makeResidueNode(String alias) {
		// alias is a atomSpec of the form [#model]:residueNumber@atom
		// We want to convert that to a node identifier of [pdbid#]ABC nnn
		// and add FunctionalResidues and BackboneOnly attributes
		boolean singleModel = false;
		ChimeraModel model = StructureUtils.getModel(alias, chimeraObject);
		if (model == null) {
			model = chimeraObject.getChimeraModels().get(0);
			singleModel = true;
		}
		ChimeraResidue residue = StructureUtils.getResidue(alias, model, chimeraObject);
		boolean backbone = StructureUtils.isBackbone(alias, chimeraObject);

		int displayType = ChimeraResidue.getDisplayType();
		ChimeraResidue.setDisplayType(ChimeraResidue.THREE_LETTER);
		// OK, now we have everything we need, create the node
		String nodeName = residue.toString().trim()+"."+residue.getChainId();
		ChimeraResidue.setDisplayType(displayType);

		if (!singleModel)
			nodeName = model.getModelName()+"#"+nodeName;

		// Create the node
		CyNode node = Cytoscape.getCyNode(nodeName, true);

		// Add our attributes
		CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
		nodeAttributes.setAttribute(nodeName, RESIDUE_ATTR, 
		   model.getModelName()+"#"+residue.getIndex()+"."+residue.getChainId());
		nodeAttributes.setAttribute(nodeName, SEED_ATTR, Boolean.valueOf(residue.isSelected()));
		if (backbone)
			nodeAttributes.setAttribute(nodeName, BACKBONE_ATTR, Boolean.TRUE);
		else
			nodeAttributes.setAttribute(nodeName, SIDECHAIN_ATTR, Boolean.TRUE);

		return node;
	}

	private void updateMap(Map<CyEdge, Double>map, CyEdge edge, String value, int comparison) {
		// Save the minimum distance between atoms
		Double v = Double.valueOf(value);
		if (map.containsKey(edge)) {
			if (comparison < 0 && map.get(edge).compareTo(v) > 0)
				map.put(edge, v);
			else if (comparison > 0 && map.get(edge).compareTo(v) < 0)
				map.put(edge, v);
		} else {
			map.put(edge, v);
		}
	}

	private ChimeraResidue[] getRange(List<ChimeraResidue[]> rangeList, CyNode node) {
		for (ChimeraResidue[] range: rangeList) {
			if (inRange(range, node)) return range;
		}
		return null;
	}

	private boolean inRange(ChimeraResidue[] range, CyNode node) {
		CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
		String residueAttr = nodeAttributes.getStringAttribute(node.getIdentifier(), RESIDUE_ATTR); 
		ChimeraStructuralObject cso = StructureUtils.fromAttribute(residueAttr, chimeraObject);
		// Models can't be in a range...
		if (cso == null || cso instanceof ChimeraModel) return false;

		// A chain might be in a range -- check this
		if (cso instanceof ChimeraChain) {
			String chainID = ((ChimeraChain)cso).getChainId();
			return inChainRange(range, chainID);
		}

		// OK, we have a residue, but we need to be careful to make
		// sure that the chains match
		ChimeraResidue residue = (ChimeraResidue)cso;
		if (inChainRange(range, residue.getChainId())) {
			return true;
		}

		int startIndex = Integer.parseInt(range[0].getIndex());
		int endIndex = Integer.parseInt(range[1].getIndex());
		int residueIndex = Integer.parseInt(residue.getIndex());

		if (endIndex < startIndex) {
			if (endIndex <= residueIndex && residueIndex <= startIndex) return true;
		} else {
			if (startIndex <= residueIndex && residueIndex <= endIndex) return true;
		}

		return false;
		
	}

	private boolean inChainRange(ChimeraResidue[] range, String chainID) {
		String start = range[0].getChainId();
		String end = range[1].getChainId();
	
		if (start == null || end == null) return false;

		if (start.equals(end)) return false;

		if (start.compareTo(end) > 0) {
			end = range[0].getChainId();
			start = range[1].getChainId();
		}

		if (start.compareTo(chainID) <= 0 && chainID.compareTo(end) <= 0) return true;

		return false;
	}
}

