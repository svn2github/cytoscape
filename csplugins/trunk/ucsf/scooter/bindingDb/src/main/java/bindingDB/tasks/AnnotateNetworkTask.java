/* vim: set ts=2: */
/**
 * Copyright (c) 2012 The Regents of the University of California.
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
package bindingDB.tasks;

import java.util.List;

import cytoscape.Cytoscape;
import cytoscape.CyNode;
import cytoscape.command.CyCommandResult;
import cytoscape.logger.CyLogger;
import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;

import bindingDB.commands.AnnotateNetworkCommand;

public class AnnotateNetworkTask extends AbstractTask {
	String identifier = null;
	double cutoff = 10.0;
	double offset = 0.0;

	public AnnotateNetworkTask(String id, double cutoff, CyLogger logger) {
		this.logger = logger;
		this.identifier = id;
		this.cutoff = cutoff;
	}

	public String getTitle() {
		return "Annotating Network";
	}

	public void setOffset(double offset) {
		this.offset = offset;
	}

	public void run() {
		setStatus("Adding BindingDB annotations to the network");
		double complete = offset; // The starting point for our % complete
		double stepSize = (100.0-offset)/Cytoscape.getCurrentNetwork().getNodeCount();
		for (CyNode node: (List<CyNode>)Cytoscape.getCurrentNetwork().nodesList()) {
			// logger.debug("Annotating "+node.getIdentifier());
			CyCommandResult result = AnnotateNetworkCommand.annotateNetwork(logger, node, identifier, cutoff);
			complete += stepSize;
			setPercentCompleted((int)complete);
			if (canceled) break;
		}
	}
}
