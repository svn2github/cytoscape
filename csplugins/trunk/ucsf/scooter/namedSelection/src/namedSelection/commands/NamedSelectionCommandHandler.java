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

package namedSelection.commands;

import namedSelection.NamedSelection;
import namedSelection.ui.GroupPanel;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import cytoscape.command.AbstractCommandHandler;
import cytoscape.command.CyCommandException;
import cytoscape.command.CyCommandManager;
import cytoscape.command.CyCommandResult;
import cytoscape.groups.CyGroup;
import cytoscape.groups.CyGroupManager;
import cytoscape.groups.CyGroupViewer;
import cytoscape.layout.Tunable;
import cytoscape.logger.CyLogger;

public class NamedSelectionCommandHandler extends AbstractCommandHandler {
	CyLogger logger;
	GroupPanel groupPanel;

	private static String ADDVIEWER = "add viewer";
	private static String DESELECT = "deselect";
	private static String GROUPNAME = "group";
	private static String UPDATE = "update";
	private static String SELECT = "select";
	private static String VIEWERNAME = "viewer";

	public NamedSelectionCommandHandler(String namespace, CyLogger logger, GroupPanel groupPanel) {
		super(CyCommandManager.reserveNamespace(namespace));

		this.logger = logger;
		this.groupPanel = groupPanel;

		addDescription(ADDVIEWER, "Add an additional viewer to the group panel");
		addArgument(ADDVIEWER, VIEWERNAME);

		addDescription(DESELECT, "Deselect a group");
		addArgument(DESELECT, GROUPNAME);
		
		addDescription(UPDATE, "Update the group panel");
		addArgument(UPDATE);

		addDescription(SELECT, "Select a group");
		addArgument(SELECT, GROUPNAME);
	}

	public CyCommandResult execute(String command, Collection<Tunable>args) throws CyCommandException {
		return execute(command, createKVMap(args));
	}

  public CyCommandResult execute(String command, Map<String, Object>args) throws CyCommandException {
		CyCommandResult result = new CyCommandResult();

		// System.out.println("namedselection command: "+command);
		if (ADDVIEWER.equals(command)) {
			String viewerName = getArg(command, VIEWERNAME, args);
			if (viewerName == null)
				throw new CyCommandException("The name of a group viewer must be specified");
			CyGroupViewer groupViewer = CyGroupManager.getGroupViewer(viewerName);
			if (groupViewer == null)
				throw new CyCommandException("Group viewer "+viewerName+" isn't registered");
			groupPanel.addViewer(groupViewer);
			result.addMessage("Added viewer "+viewerName+" to the group panel");

		} else if (UPDATE.equals(command)) {
			groupPanel.reload();
			result.addMessage("Updated the group panel");

		} else if (SELECT.equals(command) || DESELECT.equals(command)) {
			String groupName = getArg(command, GROUPNAME, args);
			if (groupName == null)
				throw new CyCommandException("The name of a group must be specified");
			CyGroup group = CyGroupManager.findGroup(groupName);
			if (group == null)
				throw new CyCommandException("Can't find group: "+groupName);
			// Make sure we're the viewer for this group
			if (!group.getViewer().equals(NamedSelection.viewerName))
				throw new CyCommandException("Group "+groupName+" is not a named selection");

			// OK, now do what we're told
			if (SELECT.equals(command)) {
				group.setState(NamedSelection.SELECTED);
				result.addMessage("Selected "+group.getNodes().size()+" nodes in group "+groupName);
			} else {
				group.setState(NamedSelection.UNSELECTED);
				result.addMessage("Deselected group "+groupName);
			}
		} 
		return result;
	}
}

