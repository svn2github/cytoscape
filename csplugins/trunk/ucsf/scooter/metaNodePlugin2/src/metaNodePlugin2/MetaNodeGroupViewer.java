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
package metaNodePlugin2;

import java.util.HashMap;
import java.util.Map;

import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.view.CyNetworkView;

import cytoscape.command.CyCommandManager;
import cytoscape.command.CyCommandResult;

import cytoscape.groups.CyGroup;
import cytoscape.groups.CyGroupChangeEvent;
import cytoscape.groups.CyGroupChangeListener;
import cytoscape.groups.CyGroupManager;
import cytoscape.groups.CyGroupViewer;
import cytoscape.groups.CyGroupViewer.ChangeType;
import cytoscape.logger.CyLogger;

import metaNodePlugin2.model.MetaNode;
import metaNodePlugin2.model.MetaNodeManager;
import metaNodePlugin2.model.MetanodeProperties;
import metaNodePlugin2.ui.MetanodeMenuListener;
import metaNodePlugin2.ui.MetanodeSettingsDialog;

/**
 * 
 * This class implements the CyGroupViewer for the metaNode viewer.
 */

public class MetaNodeGroupViewer implements CyGroupViewer { 
	CyLogger logger = null;
	String viewerName = null;
	CyGroupViewer namedSelectionViewer = null;
	MetanodeSettingsDialog settingsDialog = null;
	boolean registeredWithGroupPanel = false;

	private static String NAMEDSELECTION = "namedselection";

	public MetaNodeGroupViewer (String viewerName, CyLogger logger) {
		this.viewerName = viewerName;
		this.logger = logger;

		try {
			// Initialize the settings dialog -- we do this here so that our properties
			// get read in.
			settingsDialog = new MetanodeSettingsDialog(this);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}

		// Tell the group panel we want to use it
		registerWithGroupPanel();
	}

	/**
	 * Provides a handle to get all of our settings
	 *
	 * @return our current settings
	 */
	public MetanodeProperties getSettings() {
		if (settingsDialog != null) 
			return settingsDialog.getSettings();
		else
			return null;
	}

	public MetanodeSettingsDialog getSettingsDialog() {
		return settingsDialog;
	}

	// These are required by the CyGroupViewer interface

	/**
	 * Return the name of our viewer
	 *
	 * @return viewer name
	 */
	public String getViewerName() { return viewerName; }

	/**
	 * This is called when a new group has been created that
	 * we care about.  If we weren't building our menu each
	 * time, this would be used to update the list of groups
	 * we present to the user.
	 *
	 * @param group the CyGroup that was just created
	 */
	public void groupCreated(CyGroup group) { 
		// logger.debug("groupCreated("+group+")");
		if (MetaNodeManager.getMetaNode(group) == null) {
			MetaNode newNode = MetaNodeManager.createMetaNode(group);
		}
		// Update the attributes of the group node
		logger.info("updating group panel for new group: "+group);
		updateGroupPanel();
		// logger.debug("done");
	}

	/**
	 * This is called when a new group has been created that
	 * we care about.  This version of the groupCreated
	 * method is called by XGMML and provides the CyNetworkView
	 * that is in the process of being created.
	 *
	 * @param group the CyGroup that was just created
	 * @param view the CyNetworkView that is being created
	 */
	public void groupCreated(CyGroup group, CyNetworkView myview) { 
		// logger.debug("groupCreated("+group+", view)");
		if (MetaNodeManager.getMetaNode(group) == null) {
			MetaNode newNode = MetaNodeManager.createMetaNode(group);

			// We need to be a little tricky if we are restoring a collapsed
			// metaNode from XGMML.  We essentially need to "recollapse" it,
			// but we need to save the old hints
			if (group.getState() == MetaNodePlugin2.COLLAPSED) {
				// We are, we need to "fix up" the network
				newNode.recollapse(false, false, myview);
			} else {
				if (settingsDialog.getSizeToBoundingBox()) {
					newNode.expand(true, null, true);
				} else {
					CyNetwork network = myview.getNetwork();
					network.hideNode(group.getGroupNode());
				}
			}
		}
		// logger.debug("registering");
		logger.info("updating group panel for new group: "+group);
		updateGroupPanel();
		// logger.debug("done");
	}

	/**
	 * This is called when a group we care about is about to 
	 * be deleted.  If we weren't building our menu each
	 * time, this would be used to update the list of groups
	 * we present to the user.
	 *
	 * @param group the CyGroup that will be deleted
	 */
	public void groupWillBeRemoved(CyGroup group) { 
		MetaNode mn = MetaNodeManager.getMetaNode(group);
		if (mn == null) return;
		// Expand the group in any views that it's collapsed
		mn.expand(true, null, true);

		// Get rid of the MetaNode
		logger.info("updating group panel for removed group: "+group);
		MetaNodeManager.removeMetaNode(mn);
	}

	/**
	 * This is called when a group we care about has been
	 * changed (usually node added or deleted).
	 *
	 * @param group the CyGroup that has changed
	 * @param node the CyNode that caused the change
	 * @param change the change that occured
	 */
	public void groupChanged(CyGroup group, CyNode node, ChangeType change) { 

		MetaNode mn = MetaNodeManager.getMetaNode(group);
		if (mn == null) return;

		if (change == ChangeType.NODE_ADDED) {
			mn.nodeAdded(node);
		} else if (change == ChangeType.NODE_REMOVED) {
			mn.nodeRemoved(node);
		} else if (change == ChangeType.STATE_CHANGED) {
			if (group.getState() == MetaNodePlugin2.COLLAPSED) {
				mn.collapse(false, false, true, Cytoscape.getCurrentNetworkView());
			} else {
				mn.expand(false, Cytoscape.getCurrentNetworkView(), true);
			}
		}
	}

	private void updateGroupPanel() {
		if (!registeredWithGroupPanel) {
			registerWithGroupPanel();
		} else {
			try {
				Map<String,Object> args = new HashMap<String,Object>();
				CyCommandResult result = CyCommandManager.execute(NAMEDSELECTION, "update", args);
			} catch (Exception e) {
				logger.info(e.getMessage());
				return;
			}
		}
		return;
	}

	public void registerWithGroupPanel() {
		try {
			Map<String,Object> args = new HashMap<String,Object>();
			args.put("viewer",viewerName);
			CyCommandResult result = CyCommandManager.execute(NAMEDSELECTION, "add viewer", args);
			if (result.getErrors() != null && result.getErrors().size() > 0) {
				for (String error: result.getErrors())
					logger.warning(error);
				return;
			} else if (result.getMessages() != null && result.getMessages().size() > 0) {
				for (String message: result.getMessages())
					logger.info(message);
			}
		} catch (Exception e) {
			logger.warning(e.getMessage());
			return;
		}
		registeredWithGroupPanel = true;
	}
}
