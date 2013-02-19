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
package chemViz.commands;

import java.lang.RuntimeException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


// Cytoscape imports
import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.command.AbstractCommandHandler;
import cytoscape.command.CyCommandException;
import cytoscape.command.CyCommandHandler;
import cytoscape.command.CyCommandNamespace;
import cytoscape.command.CyCommandManager;
import cytoscape.command.CyCommandResult;
import cytoscape.data.CyAttributes;
import cytoscape.layout.Tunable;
import cytoscape.task.util.TaskManager;
import cytoscape.view.CyNetworkView;

import giny.model.GraphObject;

// chemViz imports
import chemViz.model.ChemInfoProperties;
import chemViz.model.Compound;
import chemViz.model.Compound.AttriType;
import chemViz.model.Compound.DescriptorType;
import chemViz.tasks.CreatePopupTask;
import chemViz.tasks.CreateCompoundTableTask;
import chemViz.tasks.CreateMCSSTask;
import chemViz.tasks.CreateNodeGraphicsTask;
import chemViz.tasks.SMARTSSearchTask;
import chemViz.ui.ChemInfoSettingsDialog;

enum Command {
	ATTACH("attach",
	       "Attach 2D structures to nodes",
				 "network|nodelist|node|inchiattribute|smilesattribute|inchi|smiles"),
	CALCULATE("calculate similarity",
	          "Create a similarity network for the current nodes",
	          "nodelist"),
	CLOSESTRUCTURES("close structures",
	                "Close the 2D structure grid",
	                "dialog"),
	CLOSETABLE("close table",
	           "Close the structure table",
	           "dialog"),
	GETDESC("get descriptors",
	        "Return chemical descriptors for nodes or edges",
	        "inchiattribute|smilesattribute|descriptors|edge|edgelist|network=current|node|nodelist|inchi|smiles"),
	LISTDESC("list descriptors",
	         "Return the list of available chemical descriptors",
	         ""),
	MCSS("mcss",
	    "Calculate the maximum common substructure of a node or group of nodes",
	    "edge|edgelist|node|nodelist|showresult=false|creategroup=false"),
	REMOVE("remove",
	       "Remove 2D structures from nodes",
				 "network|nodelist|node"),
	SEARCHSTRUCTURES("search structures",
	               "Search the designated structures using SMARTS",
	               "node|nodelist|edge|edgelist|smarts|smiles|inchi|columnlist"),
	SHOWSTRUCTURES("show structures",
	               "Popup the 2D structures for a node/edge or group of nodes/edges",
	               "node|nodelist|edge|edgelist|labelattribute|smiles|inchi"),
	SHOWTABLE("show table",
	          "Show the structure table for a node/edge or group of nodes/edges",
	          "edge|edgelist|node|nodelist|columnlist"),
	SETPARAM("set parameter",
	         "Set chemViz parameters",
	         "fingerprinter=CDK|smilesAttributes|inchiAttributes|nodeSize=100|position=Centered|imageLabel");

	private String command = null;
	private String argList = null;
	private String desc = null;

	Command(String command, String description, String argList) {
		this.command = command;
		this.argList = argList;
		this.desc = description;
	}

	public String getCommand() { return command; }
	public String getArgString() { return argList; }
	public String getDescription() { return desc; }
	public boolean equals(String com) { return command.equals(com); }
}
	

/**
 * Inner class to handle CyCommands
 */
public class ChemVizCommandHandler extends AbstractCommandHandler {
	static final String ALL = "all";
	static final String COLUMNLIST = "columnlist";
	static final String CREATEGROUP = "creategroup";
	static final String CURRENT = "current";
	static final String DIALOG = "dialog";
	static final String DESCRIPTORS = "descriptors";
	static final String EDGE = "edge";
	static final String EDGELIST = "edgelist";
	static final String INCHI = "inchi";
	static final String INCHIATTRIBUTE = "inchiattribute";
	static final String LABELATTRIBUTE = "labelattribute";
	static final String NETWORK = "network";
	static final String NODE = "node";
	static final String NODELIST = "nodelist";
	static final String SELECTED = "selected";
	static final String SHOWRESULT = "showresult";
	static final String SMARTS = "smarts";
	static final String SMILES = "smiles";
	static final String SMILESATTRIBUTE = "smilesattribute";

	private ChemInfoProperties props;
	private ChemInfoSettingsDialog dialog;
	
	// Dialogs that we may want to dispose of...
	static Map<Integer, CreatePopupTask> popupTasks = null;
	static int popupTaskCount = 0;
	static Map<Integer, CreateCompoundTableTask> tableTasks = null; 
	static int tableTaskCount = 0;

	public ChemVizCommandHandler (ChemInfoSettingsDialog settingsDialog) {
		super(CyCommandManager.reserveNamespace("chemViz"));

		props = settingsDialog.getProperties();
		dialog = settingsDialog;

		for (Command command: Command.values()) {
			addCommand(command.getCommand(), command.getDescription(), command.getArgString());
		}
	}

	public CyCommandResult execute(String command, Collection<Tunable>args)
                                                      throws CyCommandException, RuntimeException {
		return execute(command, createKVMap(args));
	}

	public CyCommandResult execute(String command, Map<String, Object>args) 
                                                      throws CyCommandException, RuntimeException {
		CyCommandResult result = new CyCommandResult();

		List<String> legalArgs = getArguments(command);
		for (String arg: args.keySet()) {
			if (!legalArgs.contains(arg))
				throw new RuntimeException("chemviz "+command+": unknown argument: "+arg);
		}

		// Pull out common args
		List<GraphObject> gObjList = ValueUtils.getGraphObjectList(command, args);
		String objectType = "node";	
		if (gObjList != null && (gObjList.get(0) instanceof CyEdge))
			objectType = "edge";	

		String mstring = null;
		AttriType mtype = AttriType.smiles;
		if (args.containsKey(SMILES)) {
			mstring = args.get(SMILES).toString();
		} else if (args.containsKey(INCHI)) {
			mstring = args.get(INCHI).toString();
			mtype = AttriType.inchi;
		}

		List<String> smilesAttrList = null;
		List<String> inchiAttrList = null;

		if (args.containsKey(INCHIATTRIBUTE)) {
			inchiAttrList.add(args.get(INCHIATTRIBUTE).toString());
		} else {
			inchiAttrList = dialog.getCompoundAttributes(objectType,AttriType.inchi);
		}

		if (args.containsKey(SMILESATTRIBUTE)) {
			smilesAttrList.add(args.get(SMILESATTRIBUTE).toString());
		} else {
			smilesAttrList = dialog.getCompoundAttributes(objectType,AttriType.smiles);
		}

		// Main command cascade

		//	ATTACH("attach",
		//	       "Attach 2D structures to nodes",
		//				 "network|nodelist|node|inchiattribute|smilesattribute|inchi|smiles"),
		if (Command.ATTACH.equals(command)) {
			if (gObjList == null) 
				throw new RuntimeException("chemviz "+command+": must provide node or nodelist");
			if (gObjList == null && mstring == null) 
				throw new RuntimeException("chemviz "+command+": must have one of smiles/inchi string or nodes");

			List<Compound> compoundList = ValueUtils.getCompounds(gObjList, mstring, mtype, smilesAttrList, inchiAttrList);

			// Get the network view
			CyNetworkView view = Cytoscape.getNetworkView(ValueUtils.getNetwork(args).getIdentifier());

			// Do it!
			CreateNodeGraphicsTask cngTask = CreateNodeGraphicsTask.getCustomGraphicsTask(view);
			if (cngTask != null) {
				cngTask.setCompoundList(compoundList);
				cngTask.setRemove(false);
			} else
				cngTask = new CreateNodeGraphicsTask(compoundList, view, dialog, false);

			TaskManager.executeTask(cngTask, cngTask.getDefaultTaskConfig());

			Map<GraphObject, Compound> map = new HashMap<GraphObject, Compound>();
			for (Compound c: compoundList) {
				if (!map.containsKey(c.getSource())) {
					map.put(c.getSource(), c);
					result.addMessage("Added compound '"+c+"' to node '"+c.getSource()+"'");
				}
			}


		//	CALCULATE("calculate similarity",
		//	          "Create a similarity network for the current nodes",
		//	          "nodelist"),
		} else if (Command.CALCULATE.equals(command)) {

		//	CLOSESTRUCTURES("close structures",
		//	                "Close the 2D structure grid(s)",
		//	                "dialog"),
		} else if (Command.CLOSESTRUCTURES.equals(command)) {
			if (popupTasks != null && popupTasks.size() > 0) {
				Set<Integer> dialogNumbers = getDialogNumbers(popupTasks.keySet(), args);

				for (Integer index: dialogNumbers) {
					if (popupTasks.containsKey(index)) {
						CreatePopupTask popup = popupTasks.get(index);
						popup.closePopup();
						popupTasks.remove(index);
					}
				}
			}

		//	CLOSETABLE("close table",
		//	           "Close the structure table",
		//	           ""),
		} else if (Command.CLOSETABLE.equals(command)) {
			if (tableTasks != null && tableTasks.size() > 0) {
				Set<Integer> dialogNumbers = getDialogNumbers(tableTasks.keySet(), args);

				for (Integer index: dialogNumbers) {
					if (tableTasks.containsKey(index)) {
						CreateCompoundTableTask table = tableTasks.get(index);
						table.closePopup();
						tableTasks.remove(index);
					}
				}
			}

		//	LISTDESC("list descriptors",
		//	         "Return the list of available chemical descriptors",
		//	         ""),
		} else if (Command.LISTDESC.equals(command)) {
			List<DescriptorType> descriptors = ValueUtils.getDescriptors(command, "all");
			List<String> descStrings = new ArrayList<String>();
			result.addMessage("Available descriptors: ");
			for (DescriptorType type: descriptors) {
				String shortName = type.getShortName();
				if (shortName.equals("image")) continue;

				String name = type.toString();
				result.addMessage(shortName+": "+name);
				descStrings.add(shortName);
			}
			result.addResult("descriptors", descStrings);

		//	GETDESC("get descriptors",
		//	        "Return chemical descriptors for a node",
		//	        "edge|edgelist|descriptors|inchi|node|nodelist|inchi|smiles"),
		} else if (Command.GETDESC.equals(command)) {
			if (gObjList != null && mstring != null) 
				throw new RuntimeException("chemviz "+command+": can't have both smiles/inchi string and nodes/edges");
			if (gObjList == null && mstring == null) 
				throw new RuntimeException("chemviz "+command+": must have one of smiles/inchi string or nodes/edges");

			if (!args.containsKey(DESCRIPTORS))
				throw new RuntimeException("chemviz "+command+": descriptor list must be specified");
			List<DescriptorType> descriptors = ValueUtils.getDescriptors(command, args.get(DESCRIPTORS).toString());

			List<Compound> compoundList = ValueUtils.getCompounds(gObjList, mstring, mtype, smilesAttrList, inchiAttrList);
			for (Compound compound: compoundList) {
				for (DescriptorType type: descriptors) {
					if (type.getShortName().equals("image")) continue;
					result.addResult(compound.toString()+":"+type.getShortName(), compound.getDescriptor(type));
					result.addMessage("Compound "+compound.toString()+" "+type.toString()+" = "+compound.getDescriptor(type).toString());
				}
			}
		
		//	MCSS("mcss",
		//	    "Calculate the maximum common substructure of a node or group of nodes",
		//	    "edge|edgelist|node|nodelist|showresult=false"),
		} else if (Command.MCSS.equals(command)) {
			if (gObjList == null) 
				throw new RuntimeException("chemviz "+command+": must specify node/edge or nodelist/edgelist");

			List<Compound> compoundList = ValueUtils.getCompounds(gObjList, mstring, mtype, smilesAttrList, inchiAttrList);
			CyAttributes attributes = Cytoscape.getNodeAttributes();
			if (gObjList != null && (gObjList.get(0) instanceof CyEdge))
				attributes = Cytoscape.getEdgeAttributes();

			boolean showresult = false;
			if (args.containsKey(SHOWRESULT)) {
				showresult = Boolean.parseBoolean(args.get(SHOWRESULT).toString());
			}

			boolean createGroup = false;
			if (args.containsKey(CREATEGROUP)) {
				createGroup = Boolean.parseBoolean(args.get(CREATEGROUP).toString());
			}

			CreateMCSSTask mcssTask = new CreateMCSSTask(gObjList, attributes, dialog, false, createGroup);
			TaskManager.executeTask(mcssTask, null);

			try {
				while(!mcssTask.isDone()) {
					Thread.currentThread().sleep(100);
				}
			} catch (Exception e) {}

			String mcss = mcssTask.getMCSSSmiles();
			result.addMessage("MCSS = "+mcss);
			result.addResult("MCSS",mcss);
			if (showresult) {
				String label = "MCSS = "+mcss;
				List<Compound> mcssList = ValueUtils.getCompounds(null, mcss, AttriType.smiles, null, null);
				CreatePopupTask loader = new CreatePopupTask(mcssList, null, dialog, label, 1);
				TaskManager.executeTask(loader, loader.getDefaultTaskConfig());

				if (popupTasks == null) popupTasks = new HashMap<Integer, CreatePopupTask>();
				result.addMessage("Showing structures: dialog #"+popupTaskCount);
				popupTasks.put(popupTaskCount++, loader);
			}

		//	REMOVE("remove",
		//	       "Remove 2D structures from nodes",
		//				 "network|nodelist|node"),
		} else if (Command.REMOVE.equals(command)) {
			if (gObjList == null) 
				throw new RuntimeException("chemviz "+command+": must provide node or nodelist");

			// Get the network view
			CyNetworkView view = Cytoscape.getNetworkView(ValueUtils.getNetwork(args).getIdentifier());

			// Do it!
			CreateNodeGraphicsTask cngTask = CreateNodeGraphicsTask.getCustomGraphicsTask(view);
			if (cngTask != null) {
				cngTask.setSelection(gObjList);
				cngTask.setRemove(true);
			} else
				cngTask = new CreateNodeGraphicsTask(gObjList, dialog, true);

			TaskManager.executeTask(cngTask, cngTask.getDefaultTaskConfig());
			for (GraphObject obj: gObjList)
				result.addMessage("Removed graphics from node '"+obj+"'");

		// SEARCHSTRUCTURES("search structures",
		//                "Search the designated structures using SMARTS",
		//                "node|nodelist|edge|edgelist|smarts|smiles|inchi|columnlist"),
		} else if (Command.SEARCHSTRUCTURES.equals(command)) {
			if (gObjList == null) 
				throw new RuntimeException("chemviz "+command+": must specify node/edge or nodelist/edgelist");

			if (!args.containsKey(SMARTS))
				throw new RuntimeException("chemviz "+command+": must specify SMARTS string");

			String smarts = (String)args.get(SMARTS);

			List<Compound> compoundList = ValueUtils.getCompounds(gObjList, mstring, mtype, smilesAttrList, inchiAttrList);
			CyAttributes attributes = Cytoscape.getNodeAttributes();
			if (gObjList != null && (gObjList.get(0) instanceof CyEdge))
				attributes = Cytoscape.getEdgeAttributes();

			List<String> columnList = null;
			if (args.containsKey(COLUMNLIST)) {
				String[] columnSpecs = ((String)args.get(COLUMNLIST)).split(",");
				columnList = Arrays.asList(columnSpecs);
			}

			SMARTSSearchTask searchTask = 
				new SMARTSSearchTask(smarts, gObjList, attributes, dialog, 0, columnList);
			TaskManager.executeTask(searchTask, searchTask.getDefaultTaskConfig());
			result.addMessage("Showing structures matching: "+smarts);
		
		//	SHOWSTRUCTURES("show structures",
		//	               "Popup the 2D structures for a node or group of nodes",
		//	               "node|nodelist|edge|edgelist|labelattribute|smiles|inchi"),
		} else if (Command.SHOWSTRUCTURES.equals(command)) {
			if (gObjList != null && mstring != null) 
				throw new RuntimeException("chemviz "+command+": can't have both smiles/inchi string and nodes/edges");
			if (gObjList == null && mstring == null) 
				throw new RuntimeException("chemviz "+command+": must have one of smiles/inchi string or nodes/edges");

			String labelAttribute = null;
			if (args.containsKey(LABELATTRIBUTE))
				labelAttribute = args.get(LABELATTRIBUTE).toString();

			CreatePopupTask loader = null;
			if (mstring != null) {
				List<Compound> compoundList = ValueUtils.getCompounds(gObjList, mstring, mtype, smilesAttrList, inchiAttrList);
  			loader = new CreatePopupTask(compoundList, null, dialog, labelAttribute, dialog.getMaxCompounds());
			} else {
				List<Compound> compoundList = ValueUtils.getCompounds(gObjList, mstring, mtype, smilesAttrList, inchiAttrList);
  			loader = new CreatePopupTask(null, gObjList, dialog, labelAttribute, dialog.getMaxCompounds());
			}
			TaskManager.executeTask(loader, loader.getDefaultTaskConfig());

			if (popupTasks == null) popupTasks = new HashMap<Integer, CreatePopupTask>();
			result.addMessage("Showing structures: dialog #"+popupTaskCount);
			popupTasks.put(popupTaskCount++, loader);
			
		//	SHOWTABLE("show table",
		//	          "Show the structure table for a node/edge or group of nodes/edges",
		//	          "edge|edgelist|node|nodelist|columnlist"),
		} else if (Command.SHOWTABLE.equals(command)) {
			if (gObjList == null)
				throw new RuntimeException("chemviz "+command+": node/edge or nodelist/edgelist required");

			List<String> columnList = null;
			if (args.containsKey(COLUMNLIST)) {
				String[] columnSpecs = ((String)args.get(COLUMNLIST)).split(",");
				columnList = Arrays.asList(columnSpecs);
			}
			CreateCompoundTableTask loader = new CreateCompoundTableTask(gObjList, dialog, dialog.getMaxCompounds(), columnList);
			TaskManager.executeTask(loader, loader.getDefaultTaskConfig());

			if (tableTasks == null) tableTasks = new HashMap<Integer, CreateCompoundTableTask>();
			result.addMessage("Showing structure table: dialog #"+tableTaskCount);
			tableTasks.put(tableTaskCount++, loader);
		}
		
		return result;
	}

	private Set<Integer> getDialogNumbers(Set<Integer> dialogNumbers, Map<String, Object> args) {
		if (args.containsKey(DIALOG)) {
			Set<Integer> dn = new HashSet<Integer>();
			
			String dNumber = ((String)args.get(DIALOG)).trim();
			if (!dNumber.equals(ALL)) {
				String[] dList = dNumber.split(",");
				for (String d: dList) {
					dn.add(new Integer(d.trim()));
				}
				return dn;
			}
		}
		return dialogNumbers;
	}

	private void addCommand(String command, String description, String argString) {
		// Add the description first
		addDescription(command, description);

		if (argString == null) {
			addArgument(command);
			return;
		}

		// Split up the options
		String[] options = argString.split("\\|");
		for (int opt = 0; opt < options.length; opt++) {
			String[] args = options[opt].split("=");
			if (args.length == 1)
				addArgument(command, args[0]);
			else
				addArgument(command, args[0], args[1]);
		}
	}

	private void addArguments(String command) {
		if (props == null) {
			addArgument(command);
			return;
		}

		for (Tunable t: props.getTunables()) {
			if (t.getType() == Tunable.BUTTON || t.getType() == Tunable.GROUP)
				continue;

			// Is there a default value for this prop?
			if (t.getValue() != null)
				addArgument(command, t.getName(), t.getValue().toString());
			else
				addArgument(command, t.getName());
		}
	}
}
