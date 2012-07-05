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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import java.util.Arrays;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import cytoscape.command.CyCommandManager;
import cytoscape.command.CyCommandResult;
import cytoscape.logger.CyLogger;
import cytoscape.util.URLUtil;

public class BridgeDBUtils {
	static final String SPECIES_URL = "http://webservice.bridgedb.org/contents";
	static final String BASE_URL = "http://webservice.bridgedb.org/";

	static Map<String,String> resources = new HashMap<String,String>();
	static String selectedResource = null;
	static boolean haveCyThesaurus = false;

	static public String[] getSpeciesList(CyLogger logger) {
		List<String> speciesList = new ArrayList<String>();
		try {
			URL speciesURL = new URL(SPECIES_URL);
			BufferedReader reader = new BufferedReader(new InputStreamReader(URLUtil.getInputStream(speciesURL)));
			String line;
			while ((line = reader.readLine()) != null) {
				String[] tokens = line.split("\t");
				speciesList.add(tokens[0]);
				if (haveCyThesaurus) {
					registerResource(tokens[0]);
				}
			}
		} catch (Exception e) {
			logger.error("Unable to get species list from bridgedb: "+e.getMessage());
			return null;
		}

		String[] speciesArray = speciesList.toArray(new String[0]);
		Arrays.sort(speciesArray);
		// System.out.println("Species: "+Arrays.toString(speciesArray));
		return speciesArray;
	}

	static public String[] getSupportedTypes(CyLogger logger, String species) {
		if (haveCyThesaurus) {
			if (!resources.containsKey(species))
				return null;
			selectResource(resources.get(species));

			try {
				Map<String,Object>args = new HashMap<String, Object>();
				CyCommandResult result = CyCommandManager.execute("idmapping", "get source id types", args);
				if (result != null) {
					Set<String> idTypes = (Set<String>) result.getResult();
					String[] types = idTypes.toArray(new String[0]);
					Arrays.sort(types);
					// System.out.println("Types for "+species+": "+Arrays.toString(types));
					return types;
				}
			} catch (Exception e) {
				logger.error("Unable to get supported types for species "+species+": "+e.getMessage());
			}
		}
		return null;
	}

	static public void mapIDsToUniprot(CyLogger logger, String sourceType, String source, String target) {
		try {
			Map<String,Object>args = new HashMap<String, Object>();
			args.put("sourceattr", source);
			args.put("sourcetype", sourceType);
			args.put("targetattr", target);
			args.put("targettype", "Uniprot/TrEMBL");
			CyCommandManager.execute("idmapping", "attribute based mapping", args);
		} catch (Exception e) {
			logger.error("Unable to map source ID "+source+"("+sourceType+") to "+target);
		}
		return;
	}

	static public boolean haveCyThesaurus() {
		List<String> mappingCommands = CyCommandManager.getCommandList("idmapping");
		if (mappingCommands != null && mappingCommands.size() > 0) {
			haveCyThesaurus = true;
			return true;
		}

		haveCyThesaurus = false;
		return false;
	}

	static void registerResource(String species) {
		Map<String,Object>args = new HashMap<String, Object>();
		String connstring = "idmapper-bridgerest:"+BASE_URL+species;
		args.put("connstring", connstring);
		args.put("classpath", "org.bridgedb.webservice,bridgerest.BridgeRest");
		try {
			CyCommandManager.execute("idmapping", "register resource", args);
		} catch (Exception e) {}

		resources.put(species, connstring);

		// Registering the resource implicitly selects it.  We only want one selected at a
		// time for efficiency reasons
		if (selectedResource != null)
			deselectResource(selectedResource);

		selectedResource = connstring;
	}

	static void deselectResource(String connstring) {
		Map<String,Object>args = new HashMap<String, Object>();
		args.put("connstring", connstring);
		try {
			CyCommandManager.execute("idmapping", "deselect resource", args);
		} catch (Exception e) {}
	}

	static void selectResource(String connstring) {
		if (selectedResource != null)
			deselectResource(selectedResource);

		Map<String,Object>args = new HashMap<String, Object>();
		args.put("connstring", connstring);
		try {
			CyCommandManager.execute("idmapping", "select resource", args);
		} catch (Exception e) {}

		selectedResource = connstring;
	}
}
