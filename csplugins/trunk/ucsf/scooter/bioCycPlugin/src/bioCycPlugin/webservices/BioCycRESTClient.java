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
package bioCycPlugin.webservices;

import cytoscape.logger.CyLogger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bioCycPlugin.commands.QueryHandler;
import bioCycPlugin.model.Database;
import bioCycPlugin.model.Gene;
import bioCycPlugin.model.Pathway;
import bioCycPlugin.model.Protein;
import bioCycPlugin.model.Reaction;


/**
 * 
 */
public class BioCycRESTClient {
	CyLogger logger;
	QueryHandler handler;
	List<Database> databaseCache = null;

	enum Operator {AND, OR};

	public BioCycRESTClient(String urlString, CyLogger logger) {
		this.logger = logger;
		handler = new QueryHandler(logger);
	}

	public void loadNetwork(String identifier, String database) {
		try {
			handler.loadNetwork(database+"/pathway-biopax?type=2&object="+identifier);
		} catch (Exception e) {
			logger.error("Unable to load network: "+e.getMessage(), e);
		}
	}

	public List<Database> listDatabases() {
		if (databaseCache == null) {
			try {
				databaseCache = Database.getDatabases(handler.query("dbs"));
			} catch (Exception e) {
				// We get an unknown host exception if we're not connected to the network
				return new ArrayList<Database>();
			}
		}
		return databaseCache;
	}

	public List<Pathway> findPathwaysByText(String query, String db) {

		// Split query into words
		String words[] = query.split(" ");

		
		Operator op = Operator.OR;
		boolean foundOp = false;
		Map<String, Pathway> opResults = new HashMap<String, Pathway>();

		for (String term: words) {
			if (term.equals("AND")) {
				op = Operator.AND;
				foundOp = true;
				continue;
			}

			if (term.equals("OR")) {
				op = Operator.OR;
				foundOp = true;
				continue;
			}

			if (!foundOp) {
				op = Operator.OR;
			}
			foundOp = false;

			List<Pathway> resultMap = handler.searchForPathways(db, term);
			switch (op) {
			case OR:
				combineResultsOR(opResults, resultMap);
				break;
			case AND:
				combineResultsAND(opResults, resultMap);
				break;
			}
		}

		List<Pathway> list = new ArrayList<Pathway>();
		list.addAll(opResults.values());
		return list;
	}

	private void combineResultsOR(Map<String, Pathway> dest, List<Pathway> source) {
		for (Pathway p: source) {
			if (!dest.containsKey(p.getFrameID())) {
				dest.put(p.getFrameID(), p);
			}
		}
	}

	private void combineResultsAND(Map<String, Pathway> dest, List<Pathway> source) {
		for (Pathway p: source) {
			if (dest.containsKey(p.getFrameID())) {
				dest.remove(p.getFrameID());
			}
		}
	}

}
