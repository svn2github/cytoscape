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
package csplugins.layout.algorithms.bioLayout;

// Cytoscape imports
import cytoscape.*;
import cytoscape.layout.*;
import cytoscape.plugin.*;
import cytoscape.view.*;
import cytoscape.data.CyAttributes;
import cytoscape.util.CytoscapeAction;

import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.ui.JTask;
import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;
import cytoscape.task.util.TaskManager;

import csplugins.layout.AbstractLayout;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;

import csplugins.layout.algorithms.bioLayout.BioLayoutAlgorithm;
import csplugins.layout.algorithms.bioLayout.BioLayoutKKAlgorithm;
import csplugins.layout.algorithms.bioLayout.BioLayoutFRAlgorithm;


public class BioLayoutActionListener extends AbstractAction implements Task 
{
	private ActionEvent event;
	private String attribute;
	private TaskMonitor taskMonitor;
	private BioLayoutAlgorithm algObj;
	private int algorithm;
	private boolean selectedOnly = false;

	// String for doing an "unweighted" layout
	public static final String UNWEIGHTEDATTRIBUTE = "(unweighted)";

	public static final int KK_ALGORITHM = 0;
	public static final int FR_ALGORITHM = 1;
	public static final int SE_ALGORITHM = 2;

	public BioLayoutActionListener(boolean selectedOnly)
	{
		this.algorithm = SE_ALGORITHM;
		this.selectedOnly = selectedOnly;
	}

	public BioLayoutActionListener(int algorithm, boolean selectedOnly)
	{
		this.algorithm = algorithm;
		this.selectedOnly = selectedOnly;
	}

	public void actionPerformed (ActionEvent e)
	{
		this.event = e;
		this.attribute = e.getActionCommand();
		if (algorithm == SE_ALGORITHM || attribute.equals(UNWEIGHTEDATTRIBUTE))
			this.attribute = null;

		JTaskConfig taskConfig = getNewDefaultTaskConfig();
		TaskManager.executeTask(this, taskConfig);
	}

	public void run()
	{
		if (algorithm == KK_ALGORITHM) {
			algObj = new BioLayoutKKAlgorithm(Cytoscape.getCurrentNetworkView());
		} else if (algorithm == SE_ALGORITHM) {
			algObj = new BioLayoutKKAlgorithm(Cytoscape.getCurrentNetworkView());
			// This makes it consistent with the older layout
			((BioLayoutKKAlgorithm)algObj).setNumberOfIterationsPerNode(20);
		} else {
			algObj = new BioLayoutFRAlgorithm(Cytoscape.getCurrentNetworkView());
		}
		algObj.setEvalueAttribute(attribute);
		algObj.setTaskMonitor(taskMonitor);
		// If we're doing selected only, we really don't want
		// to spread these nodes around everywhere
		if (selectedOnly) {
			algObj.setRandomize(false);
		}
		algObj.setSelectedOnly(selectedOnly);
		algObj.construct();
	}

	public String getTitle() 
	{ 
		if (attribute == null)
			return "Performing Spring Embedded Layout using no edges weights";
		else if (algorithm == KK_ALGORITHM)
			return "Performing Spring Embedded layout using "+attribute+" for edge weight"; 
		else
			return "Performing BioLayout using "+attribute+" for edge weight"; 
	}

	public void halt() { 
		algObj.setCancel();
	}

	public void setTaskMonitor(TaskMonitor _monitor) { taskMonitor = _monitor; }

	private JTaskConfig getNewDefaultTaskConfig()
	{
		JTaskConfig result = new JTaskConfig();

		result.displayCancelButton(true);
		result.displayCloseButton(false);
		result.displayStatus(true);
		result.displayTimeElapsed(false);
		result.setAutoDispose(true);
		result.setModal(true);
		result.setOwner(Cytoscape.getDesktop());

		return result;
	}
}

