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
package bioCycPlugin;

import cytoscape.Cytoscape;
import cytoscape.CytoscapeInit;
import cytoscape.command.CyCommandManager;
import cytoscape.data.webservice.WebServiceClientManager;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.logger.CyLogger;

import java.util.Properties;

import bioCycPlugin.commands.BioCycCommandHandler;
import bioCycPlugin.webservices.BioCycClient;

public class BioCycPlugin extends CytoscapePlugin {
	private CyLogger logger = null;
	private BioCycClient wpclient;

	protected static final String WEBSERVICE_URL = "biocyc.webservice.uri";
	// protected static final String DEFAULT_URL = "http://brg-preview.ai.sri.com/";
	protected static final String DEFAULT_URL = "http://websvc.biocyc.org/";

	/**
	 * We don't do much at initialization time
	 */
	public BioCycPlugin() {
		logger = CyLogger.getLogger(BioCycPlugin.class);

		// Register our commands
		try {
			new BioCycCommandHandler("biocyc", logger);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		// Setup any global properties
		Properties p = CytoscapeInit.getProperties();
		if (p.get(WEBSERVICE_URL) == null) {
			p.put(WEBSERVICE_URL, DEFAULT_URL);
		}
		// Register ourselves with the web services infrastructure
		wpclient = new BioCycClient(logger);
		WebServiceClientManager.registerClient(wpclient);

	}

	public static String getBaseUrl() {
		return CytoscapeInit.getProperties().getProperty(WEBSERVICE_URL);
	}
}
