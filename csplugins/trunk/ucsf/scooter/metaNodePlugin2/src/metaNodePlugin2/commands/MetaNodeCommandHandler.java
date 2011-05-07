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
package metaNodePlugin2.commands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cytoscape.Cytoscape;
import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.command.AbstractCommandHandler;
import cytoscape.command.CyCommandException;
import cytoscape.command.CyCommandManager;
import cytoscape.command.CyCommandResult;
import cytoscape.data.CyAttributes;
import cytoscape.groups.CyGroup;
import cytoscape.groups.CyGroupManager;
import cytoscape.groups.CyGroupViewer;
import cytoscape.layout.Tunable;
import cytoscape.logger.CyLogger;
import cytoscape.view.CyNetworkView;


import metaNodePlugin2.MetaNodeGroupViewer;
import metaNodePlugin2.data.AttributeHandler;
import metaNodePlugin2.data.AttributeHandlingType;
import metaNodePlugin2.data.AttributeManager;
import metaNodePlugin2.model.MetaNode;
import metaNodePlugin2.model.MetaNodeManager;
import metaNodePlugin2.model.MetanodeProperties;
import metaNodePlugin2.ui.MetanodeSettingsDialog;

enum Command {

	ADD("add node",
	       "Add a node to a metanode",
	       "metanode|node|nodelist"),
	CREATE("create",
	       "Create a new metanode",
	       "metanode|network=current|nodelist=selected"),
	COLLAPSE("collapse",
	         "Collapse a metanode",
	         "metanode|networkview=current"),
	COLLAPSEALL("collapse all",
	         "Collapse all metanodes",
	         "networkview=current"),
	EXPAND("expand",
	       "Expand a metanode",
	       "metanode|networkview=current"),
	EXPANDALL("expand all",
	 	      "Expand all metanodes",
	 	      "networkview=current"),	       
	LISTEDGES("list edges",
	          "List the edges in a particular metanode",
	          "metanode"),
	LISTMETA("list metanodes",
	         "List all metanodes",
	         "network=all"),
	LISTNODES("list nodes",
	          "List the nodes in a particular metanode",
	          "metanode"),
	REMOVE("remove node",
	       "Remove a node from a metanode",
	       "metanode|node|nodelist"),
	/* MODIFYAGG("modify aggregation", // FIXME
	          "Modify the aggregation behavior of a metanode",
	          "metanode|enabled=true|string=csv|integer=sum|double=sum|list=none|boolean=or"), */
	MODIFYAPP("modify appearance",
	          "Modify the appearance of a metanode",
	          "metanode|usenestednetworks=false|opacity=100|nodechart=none|chartattribute=none"),
	/* MODIFYAGGOVERRIDE("modify overrides", // FIXME
	                  "Modify aggregation overrides for specific attributes in a metanode",
	                  "metanode|attribute|aggregation"), */
  SETDEFAULTAGG("set default aggregation", 
	              "Set the default aggregation options",
	              "enabled=true|string=csv|integer=sum|double=sum|list=none|boolean=or"),
	SETDEFAULTAPP("set default appearance",
	              "Set the default appearance options",
	              "usenestednetworks=false|opacity=100|nodechart=none|chartattribute=none"),
	SETAGGOVERRIDE("set default overrides",
	               "Override defailt aggregation for specific attributes",
	               "attribute|aggregation");

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
  public boolean equals(String com) { return command.equalsIgnoreCase(com); }
}


/**
 * 
 */
public class MetaNodeCommandHandler extends AbstractCommandHandler {
	CyLogger logger;
	MetaNodeGroupViewer metanodeViewer;

	private static String AGGREGATION = "aggregation";
	private static String ALL = "all";
	private static String ATTRIBUTE = "attribute";
	private static String CHARTATTR = "chartattribute";
	private static String CURRENT = "current";
	private static String DEFAULT = "default";
	private static String ENABLED = "enabled";
	private static String METANODE = "metanode";
	private static String NETWORK = "network";
	private static String NETWORKVIEW = "networkview";
	private static String NODE = "node";
	private static String NODECHART = "nodechart";
	private static String NODELIST = "nodelist";
	private static String NONE = "none";
	private static String OPACITY = "opacity";
	private static String SELECTED = "selected";
	private static String USENESTEDNETWORKS = "usenestednetworks";

	private static String BOOLEAN = "boolean";
	private static String DOUBLE = "double";
	private static String INTEGER = "integer";
	private static String LIST = "list";
	private static String STRING = "string";

	private static String AND = "and";
	private static String AVERAGE = "average";
	private static String CONCATENATE = "concatenate";
	private static String CSV = "csv";	// Comma-separated values
	private static String MEDIAN = "median";
	private static String MINIMUM = "minimum";
	private static String MCV = "mcv";	// Most common value
	private static String MAXIMUM = "maximum";
	private static String OR = "or";
	private static String SUM = "sum";
	private static String TSV = "tsv";	// Tab-separated value

	private static String BAR = "bar";
	private static String LINE = "line";
	private static String PIE = "pie";
	private static String STRIPE = "stripe";

	public MetaNodeCommandHandler(String namespace, MetaNodeGroupViewer viewer, CyLogger logger) {
		super(CyCommandManager.reserveNamespace(namespace));

		this.logger = logger;
		this.metanodeViewer = viewer;

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
		MetanodeSettingsDialog settingsDialog = metanodeViewer.getSettingsDialog();

		// Get our properties
		MetanodeProperties props = settingsDialog.getSettings();

		// Check all args to handle any errors
		List<String> legalArgs = getArguments(command);
		for (String arg: args.keySet()) {
			if (!legalArgs.contains(arg))
				throw new RuntimeException("metanode "+command+": unknown argument: "+arg);
		}

		// Many commands take a "metanode" argument. Get it and find the appropriate
		// metanode in advance.
		MetaNode metaNode = null;
		CyGroup metaGroup = null;
		if (args.containsKey(METANODE) && !Command.CREATE.equals(command)) {
			String metanodeName = (String)args.get(METANODE);
			metaGroup = CyGroupManager.findGroup(metanodeName);
			metaNode = MetaNodeManager.getMetaNode(metaGroup);
			if (metaNode == null)
				throw new RuntimeException("metanode: can't find metanode "+metanodeName);
		}

		// Main command cascade

		// 
		// ADD("add node",
		//        "Add a node to a metanode",
		//        "metanode|node|nodelist=selected"),
		// 
		if (Command.ADD.equals(command)) {
			if (metaNode == null) {
				throw new RuntimeException("metanode: add node requires a metanode");
			}
			// This is identical to the coreCommand group add node -- just use that, but we remap the
			// arguments a little and do our own sanity checking
			Map<String, Object> destargs = new HashMap<String, Object>();
			destargs.put("name",metaGroup.getGroupName());
			if (args.containsKey(NODE) && args.containsKey(NODELIST))
				throw new RuntimeException("metanode: only one of 'node' and 'nodelist' may be provided");

			if (!args.containsKey(NODE) && !args.containsKey(NODELIST))
				throw new RuntimeException("metanode: you must provide a 'node' or 'nodelist' to add");

			if (args.containsKey(NODE))
				destargs.put(NODE, args.get(NODE));
			if (args.containsKey(NODELIST))
				destargs.put(NODELIST, args.get(NODELIST));
			return CyCommandManager.execute("group", "add", destargs);

		// 
		//	CREATE("create",
		//	       "Create a new metanode",
		//	       "metanode|network=current|nodelist=selected"),
		// 
		} else if (Command.CREATE.equals(command)) {

		// 
		//	COLLAPSE("collapse",
		//	         "Collapse a metanode",
		//	         "metanode|networkview=current"),
		// 
		} else if (Command.COLLAPSE.equals(command)) {
			if (metaNode == null) {
				throw new RuntimeException("metanode: collapse requires a metanode");
			}
			// Get the network view
			CyNetworkView view = Cytoscape.getCurrentNetworkView();
			if (args.containsKey(NETWORKVIEW) && !CURRENT.equalsIgnoreCase((String)args.get(NETWORKVIEW))) {
				view = Cytoscape.getNetworkView((String)args.get(NETWORKVIEW));
				if (view == null || view == Cytoscape.getNullNetworkView())
					throw new RuntimeException("metanode: can't find a network view for "+args.get(NETWORKVIEW));
			}

			// Make sure this metanode is in this view
			CyNetwork network = metaGroup.getNetwork();
			if (network != null && !network.equals(Cytoscape.getNullNetwork())) {
				if (!view.getNetwork().equals(network))
					throw new RuntimeException("metanode: '"+metaGroup.toString()+"' is not in view: "+view.getIdentifier());
			}
			metanodeViewer.collapse(metaNode, view);
			result.addMessage("Metanode "+metaGroup.toString()+" was collapsed in view "+view.getIdentifier());

		// 
		//	EXPAND("expand",
		//	       "Expand a metanode",
		//	       "metanode|networkview=current"),
		// 
		} else if (Command.COLLAPSEALL.equals(command)){
			// Get the network view
			CyNetworkView view = Cytoscape.getCurrentNetworkView();
			if (args.containsKey(NETWORKVIEW) && !CURRENT.equalsIgnoreCase((String)args.get(NETWORKVIEW))) {
				view = Cytoscape.getNetworkView((String)args.get(NETWORKVIEW));
				if (view == null || view == Cytoscape.getNullNetworkView())
					throw new RuntimeException("metanode: can't find a network view for "+args.get(NETWORKVIEW));
			}
			MetaNodeManager.collapseAll(view);

		} else if (Command.EXPAND.equals(command)) {
			if (metaNode == null) {
				throw new RuntimeException("metanode: expand requires a metanode");
			}
			// Get the network view
			CyNetworkView view = Cytoscape.getCurrentNetworkView();
			if (args.containsKey(NETWORKVIEW) && !CURRENT.equalsIgnoreCase((String)args.get(NETWORKVIEW))) {
				view = Cytoscape.getNetworkView((String)args.get(NETWORKVIEW));
				if (view == null || view == Cytoscape.getNullNetworkView())
					throw new RuntimeException("metanode: can't find a network view for "+args.get(NETWORKVIEW));
			}

			// Make sure this metanode is in this view
			CyNetwork network = metaGroup.getNetwork();
			if (network != null && !network.equals(Cytoscape.getNullNetwork())) {
				if (!view.getNetwork().equals(network))
					throw new RuntimeException("metanode: '"+metaGroup.toString()+"' is not in view: "+view.getIdentifier());
			}
			metaNode.expand(view);
			result.addMessage("Metanode "+metaGroup.toString()+" was expanded in view "+view.getIdentifier());

		//
		//	LISTEDGES("list edges",
		//	          "List the edges in a particular metanode",
		//	          "metanode"),
		//
		} else if (Command.EXPANDALL.equals(command)){
			// Get the network view
			CyNetworkView view = Cytoscape.getCurrentNetworkView();
			if (args.containsKey(NETWORKVIEW) && !CURRENT.equalsIgnoreCase((String)args.get(NETWORKVIEW))) {
				view = Cytoscape.getNetworkView((String)args.get(NETWORKVIEW));
				if (view == null || view == Cytoscape.getNullNetworkView())
					throw new RuntimeException("metanode: can't find a network view for "+args.get(NETWORKVIEW));
			}
			MetaNodeManager.expandAll(view);

		} else if (Command.LISTEDGES.equals(command)) {
			if (metaGroup == null)
				throw new RuntimeException("metanode: list edges requires a metanode name");
			List<CyEdge> internal = metaGroup.getInnerEdges();
			List<CyEdge> external = metaGroup.getOuterEdges();
			List<String> internalNames = new ArrayList<String>();
			if ((internal == null || internal.size() == 0) &&
			    (external == null || external.size() == 0)) {
				result.addMessage("metanode: there are no edges for metanode "+metaGroup.getGroupName());
			}

			if (internal != null && internal.size() > 0) {
				result.addMessage("Internal edges for metanode "+metaGroup.getGroupName());
				for (CyEdge edge: internal) {
					result.addMessage("    "+edge.getIdentifier());
					internalNames.add(edge.getIdentifier());
				}
			}
			result.addResult("internal", internalNames);

			List<String> externalNames = new ArrayList<String>();
			if (external != null && external.size() > 0) {
				result.addMessage("External edges for metanode "+metaGroup.getGroupName());
				for (CyEdge edge: external) {
					result.addMessage("    "+edge.getIdentifier());
					externalNames.add(edge.getIdentifier());
				}
			}
			result.addResult("external", externalNames);

		//
		//	LISTMETA("list metanodes",
		//	         "List all metanodes",
		//	         network=all),
		//
		} else if (Command.LISTMETA.equals(command)) {
			List<CyGroup> metaList = CyGroupManager.getGroupList(metanodeViewer);
			if (metaList == null || metaList.size() == 0) {
				result.addMessage("No current metanodes defined");
				return result;
			}
			CyNetwork network = Cytoscape.getNullNetwork();
			String networkName = ALL;
			if (args.containsKey(NETWORK)) {
				networkName = (String)args.get(NETWORK);
				if (!networkName.equalsIgnoreCase(ALL)) {
					network = Cytoscape.getNetwork(networkName);
					if (network == null || network.equals(Cytoscape.getNullNetwork()))
						throw new RuntimeException("metanode: can't find network "+networkName);
				} else {
					network = null;
				}
			}

			List<String> resultList = new ArrayList<String>();
			result.addMessage("Metanodes for network '"+networkName+"'");
			for (CyGroup group: metaList) {
				CyNetwork groupNetwork = group.getNetwork();
				String groupString = group.toString()+" Network: ";
				if (group.getNetwork() == null || group.getNetwork() == Cytoscape.getNullNetwork())
					groupString += "none";
				else
					groupString += group.getNetwork().getIdentifier();

				groupString +=" "+group.getNodes().size()+" nodes, ";
				groupString += group.getInnerEdges().size()+" inner edges, and "+group.getOuterEdges().size()+" outer edges";
				if (networkName.equalsIgnoreCase(ALL)) {
					resultList.add(group.toString());
					result.addMessage("   "+groupString);
				} else if (network.equals(groupNetwork)) {
					resultList.add(group.toString());
					result.addMessage("   "+groupString);
				}
			}

			if (resultList.size() == 0) {
				result.addMessage("   None");
				return result;
			}

			result.addResult(resultList);

		//
		//	LISTNODES("list nodes",
		//	          "List the nodes in a particular metanode",
		//	          "metanode"),
		//
		} else if (Command.LISTNODES.equals(command)) {
			if (metaGroup == null)
				throw new RuntimeException("metanode: list edges requires a metanode name");
			List<CyNode> nodes = metaGroup.getNodes();
			List<String> nodeNames = new ArrayList<String>();
			if (nodes == null || nodes.size() == 0) {
				result.addMessage("metanode: there are no edges for metanode "+metaGroup.getGroupName());
			} else {
				result.addMessage("Nodes in metanode "+metaGroup.getGroupName());
				for (CyNode node: nodes) {
					result.addMessage("   "+node.toString());
					nodeNames.add(node.toString());
				}
			}
			result.addResult("node",nodeNames);

		// 
		//	REMOVE("remove node",
		//	       "Remove a node from a metanode",
		//	       "metanode|node|nodelist"),
		// 
		} else if (Command.REMOVE.equals(command)) {
			if (metaNode == null) {
				throw new RuntimeException("metanode: remove node requires a metanode");
			}
			// This is identical to the coreCommand group remove node -- just use that, but we remap the
			// arguments a little and do our own sanity checking
			Map<String, Object> destargs = new HashMap<String, Object>();
			destargs.put("name",metaGroup.getGroupName());
			if (args.containsKey(NODE) && args.containsKey(NODELIST))
				throw new RuntimeException("metanode: only one of 'node' and 'nodelist' may be provided");

			if (!args.containsKey(NODE) && !args.containsKey(NODELIST))
				throw new RuntimeException("metanode: you must provide a 'node' or 'nodelist' to remove");

			if (args.containsKey(NODE))
				destargs.put(NODE, args.get(NODE));
			if (args.containsKey(NODELIST))
				destargs.put(NODELIST, args.get(NODELIST));
			return CyCommandManager.execute("group", "remove", destargs);

		// 
		//	MODIFYAGG("modify aggregation",
		//	          "Modify the aggregation behavior of a metanode",
		//	          "metanode|enabled=true|string=csv|integer=sum|double=sum|list=none|boolean=or"),
		// 
		// } else if (Command.MODIFYAGG.equals(command)) {

		// 
		//	MODIFYAPP("modify appearance",
		//	          "Modify the appearance of a metanode",
		//	          "metanode|usenestednetworks=false|opacity=100|nodechart=none|chartattribute=none"),
		// 
		} else if (Command.MODIFYAPP.equals(command)) {
			if (metaNode == null)
				throw new RuntimeException("metanode: must specify a metanode to modify");

			// Handle node chart configuration
			if (args.containsKey(NODECHART) && !NONE.equalsIgnoreCase(args.get(NODECHART).toString())) {
				if (!metanodeViewer.haveNodeCharts())
					throw new RuntimeException("metanode: nodeCharts plugin is not loaded");
				List<String>chartTypes = metanodeViewer.getChartTypes();
				String type = args.get(NODECHART).toString();
				if (!chartTypes.contains(type)) 
					throw new RuntimeException("metanode: nodeCharts type '"+type+"' is not provided by the nodeChart plugin");

				// OK, now check our attribute
				String attribute = getAttribute((String)args.get(CHARTATTR));
				metaNode.setChartType(type);
				metaNode.setNodeChartAttribute(attribute);
			}

			if (args.containsKey(USENESTEDNETWORKS))
				metaNode.setUseNestedNetworks(getBooleanArg(command, USENESTEDNETWORKS, args));

			if (args.containsKey(OPACITY)) 
				metaNode.setMetaNodeOpacity(getDoubleArg(command, OPACITY, args));

		// 
		//	MODIFYAGGOVERRIDE("modify overrides",
		//	                  "Modify aggregation overrides for specific attributes in a metanode",
		//	                  "metanode|attribute|aggregation"),
		// 
		// } else if (Command.MODIFYAGGOVERRIDE.equals(command)) {

		// 
		//  SETDEFAULTAGG("set default aggregation", 
		//	              "Set the default aggregation options",
		//	              "enabled=true|string=csv|integer=sum|double=sum|list=none|boolean=or"),
		// 
		} else if (Command.SETDEFAULTAGG.equals(command)) {
			boolean attrHandling = false;
			if(args.containsKey(ENABLED))
				attrHandling = getBooleanArg(command, ENABLED, args);

			if (!attrHandling) {
				setTunable(props, "enableHandling", "false");
				result.addMessage("metanode: attribute aggregation disabled");
			} else {
				setTunable(props, "enableHandling", "true");
				result.addMessage("metanode: attribute aggregation enabled");

				if (args.containsKey(STRING)) {
					setDefault(props, "stringDefaults", CyAttributes.TYPE_STRING, args.get(STRING).toString());
					result.addMessage("metanode: set default aggregation handling for "+STRING+" to "+args.get(STRING).toString());
				}

				if (args.containsKey(INTEGER)) {
					setDefault(props, "intDefaults", CyAttributes.TYPE_INTEGER, args.get(INTEGER).toString());
					result.addMessage("metanode: set default aggregation handling for "+INTEGER+" to "+args.get(INTEGER).toString());
				}

				if (args.containsKey(DOUBLE)) {
					setDefault(props, "doubleDefaults", CyAttributes.TYPE_FLOATING, args.get(DOUBLE).toString());
					result.addMessage("metanode: set default aggregation handling for "+DOUBLE+" to "+args.get(DOUBLE).toString());
				}

				if (args.containsKey(LIST)) {
					setDefault(props, "listDefaults", CyAttributes.TYPE_SIMPLE_LIST, args.get(LIST).toString());
					result.addMessage("metanode: set default aggregation handling for "+LIST+" to "+args.get(LIST).toString());
				}

				if (args.containsKey(BOOLEAN)) {
					setDefault(props, "booleanDefaults", CyAttributes.TYPE_BOOLEAN, args.get(BOOLEAN).toString());
					result.addMessage("metanode: set default aggregation handling for "+BOOLEAN+" to "+args.get(BOOLEAN).toString());
				}
			}
			

		// 
		//	SETDEFAULTAPP("set default appearance",
		//	              "Set the default appearance options",
		//	              "usenestednetworks=false|opacity=100|nodechart=none|chartattribute=none"),
		// 
		} else if (Command.SETDEFAULTAPP.equals(command)) {
			// Handle node chart configuration
			if (args.containsKey(NODECHART) && !NONE.equalsIgnoreCase(args.get(NODECHART).toString())) {
				if (!metanodeViewer.haveNodeCharts())
					throw new RuntimeException("metanode: nodeCharts plugin is not loaded");
				List<String>chartTypes = metanodeViewer.getChartTypes();
				String type = args.get(NODECHART).toString();
				if (!chartTypes.contains(type)) 
					throw new RuntimeException("metanode: nodeCharts type '"+type+"' is not provided by the nodeChart plugin");

				// OK, now check our attribute
				String attribute = getAttribute((String)args.get(CHARTATTR));
				setTunable(props, "chartType", type);
				setTunable(props, "nodeChartAttribute", attribute);
			}

			if (args.containsKey(USENESTEDNETWORKS))
				setTunable(props, "useNestedNetworks", args.get(USENESTEDNETWORKS).toString());
			if (args.containsKey(OPACITY)) {
				setTunable(props, "metanodeOpacity", args.get(OPACITY).toString());
			}
			settingsDialog.updateSettings(true);

		// 
		//	SETAGGOVERRIDE("set default overrides",
		//	               "Override defailt aggregation for specific attributes",
		//	               "attribute|aggregation");
		// 
		} else if (Command.SETAGGOVERRIDE.equals(command)) {
			if (!args.containsKey(ATTRIBUTE))
				throw new RuntimeException("metanode: "+command+" requires an attribute");
			if (!args.containsKey(AGGREGATION))
				throw new RuntimeException("metanode: "+command+" requires an aggregation type");

			String attr = getAttribute(args.get(ATTRIBUTE).toString());
			byte type = Cytoscape.getNodeAttributes().getType(attr);
			String aggrType = args.get(AGGREGATION).toString();
			AttributeHandlingType aggr = getAggregation(type, aggrType);
			AttributeHandler handler = AttributeManager.getHandler(attr);
			handler.setHandlerType(aggr);
			result.addMessage("metanode: set attribute aggretion for "+attr+" to "+aggr.toString());
		}

		return result;
	}

	private boolean getBooleanArg(String command, String arg, Map<String, Object>args) {
		String com = getArg(command, arg, args);
		if (com == null || com.length() == 0) return false;
		boolean b = false;
		b = Boolean.parseBoolean(com);
		// throw new CyCommandException(arg+" must be 'true' or 'false'");
		return b;
	}

	private double getDoubleArg(String command, String arg, Map<String, Object>args) throws RuntimeException {
		String com = getArg(command, arg, args);
		if (com == null || com.length() == 0) 
			throw new RuntimeException("metanode: can't find argument '"+arg+"'");

		double v = 0.0;
		try {
			v = Double.parseDouble(com);
		} catch (NumberFormatException e) {
			throw new RuntimeException("metanode: argument '"+arg+"' requires a number");
		}

		return v;
	}

	private void setTunable(MetanodeProperties props, String tunable, String value) throws RuntimeException {
		Tunable t = props.get(tunable);
		if (t == null)
			throw new RuntimeException("metanode: can't find tunable for "+tunable);
		t.setValue(value);
	}

	private String getAttribute(String attr) throws RuntimeException {
		CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
		if (nodeAttributes.getType(attr) == CyAttributes.TYPE_UNDEFINED)
			throw new RuntimeException("metanode: unknown attribute '"+attr+"'");
		return attr;
	}

	private AttributeHandlingType getAggregation(byte type, String aggr) throws RuntimeException {
		if (aggr.equalsIgnoreCase(NONE))
			return AttributeHandlingType.NONE;
		if (aggr.equalsIgnoreCase(DEFAULT))
			return AttributeHandlingType.DEFAULT;

		switch(type) {
		case CyAttributes.TYPE_BOOLEAN:
			if (aggr.equalsIgnoreCase(OR))
				return AttributeHandlingType.OR;
 			if (aggr.equalsIgnoreCase(AND))
				return AttributeHandlingType.AND;
			throw new RuntimeException("metanode: can't apply attribute handling type '"+aggr+"' to boolean attributes");

		case CyAttributes.TYPE_FLOATING:
		case CyAttributes.TYPE_INTEGER:
			if (aggr.equalsIgnoreCase(AVERAGE))
				return AttributeHandlingType.AVG;
			if (aggr.equalsIgnoreCase(MINIMUM))
				return AttributeHandlingType.MIN;
			if (aggr.equalsIgnoreCase(MAXIMUM))
				return AttributeHandlingType.MAX;
			if (aggr.equalsIgnoreCase(SUM))
				return AttributeHandlingType.SUM;
			if (aggr.equalsIgnoreCase(MEDIAN))
				return AttributeHandlingType.MEDIAN;
			throw new RuntimeException("metanode: can't apply attribute handling type '"+aggr+"' to numeric attributes");

		case CyAttributes.TYPE_STRING:
			if (aggr.equalsIgnoreCase(CSV))
				return AttributeHandlingType.CSV;
			if (aggr.equalsIgnoreCase(TSV))
				return AttributeHandlingType.TSV;
			if (aggr.equalsIgnoreCase(MCV))
				return AttributeHandlingType.MCV;
			throw new RuntimeException("metanode: can't apply attribute handling type '"+aggr+"' to string attributes");

		case CyAttributes.TYPE_SIMPLE_LIST:
			if (aggr.equalsIgnoreCase(CONCATENATE))
				return AttributeHandlingType.CONCAT;
			throw new RuntimeException("metanode: can't apply attribute handling type '"+aggr+"' to string attributes");

		case CyAttributes.TYPE_COMPLEX:
			throw new RuntimeException("metanode: can't aggregate complex attributes");
		case CyAttributes.TYPE_SIMPLE_MAP:
			throw new RuntimeException("metanode: can't aggregate map attributes");
		}
		throw new RuntimeException("metanode: unknown attribute type!?!");
	}

	private void setDefault(MetanodeProperties props, String tunable, byte type, String value) {
		// First, check to see if our value is good
		AttributeHandlingType aggrType = getAggregation(type, value);

		// Apparently it is, so set the default
		AttributeManager.setDefault(type, aggrType);

		// Now reflect the change in our tunable
		Tunable t = props.get(tunable);
		setListTunable(t, aggrType.toString());
	}

	private void setListTunable(Tunable t, String value) {
		if (t == null) return;

		// Get the list of options
		Object[] array = (Object [])t.getLowerBound();
		for (int index = 0; index < array.length; index++) {
			if (value.equalsIgnoreCase(array[index].toString())) {
				t.setValue(index);
				return;
			}
		}

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
}
