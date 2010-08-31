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
package nodeCharts;

// System imports
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

// Cytoscape imports
import cytoscape.Cytoscape;
import cytoscape.command.CyCommandHandler;
import cytoscape.logger.CyLogger;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.view.CytoscapeDesktop;

import nodeCharts.command.NodeChartCommandHandler;

/**
 * The NodeCharts plugin provides a CyCommand interface to place
 * a variety of charts on Cytoscape nodes.
 */
public class NodeCharts extends CytoscapePlugin implements PropertyChangeListener {
	CyLogger logger;
	CyCommandHandler handler;

	/**
	 * The main constructor
	 */
	public NodeCharts() {
		logger = CyLogger.getLogger(NodeCharts.class);

		// Set ourselves up to listen for new networks
		Cytoscape.getDesktop().getSwingPropertyChangeSupport()
		                      .addPropertyChangeListener( CytoscapeDesktop.NETWORK_VIEW_CREATED, this );

		// Register our command handler
		handler = new NodeChartCommandHandler("nodecharts", logger);
	}

	/**
	 * Implements the property change listener support
	 *
	 * @param evt the event that triggered us
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName() == CytoscapeDesktop.NETWORK_VIEW_CREATED) {
			// look for our attributes
			// call the command handler
		}
	}
}
