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
package bioCycPlugin.commands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import cytoscape.Cytoscape;
import cytoscape.actions.LoadNetworkTask;
import cytoscape.command.AbstractCommandHandler;
import cytoscape.command.CyCommandException;
import cytoscape.command.CyCommandManager;
import cytoscape.command.CyCommandResult;
import cytoscape.layout.Tunable;
import cytoscape.logger.CyLogger;

import bioCycPlugin.BioCycPlugin;
import bioCycPlugin.model.Database;
import bioCycPlugin.model.Gene;
import bioCycPlugin.model.Pathway;
import bioCycPlugin.model.Protein;
import bioCycPlugin.model.Reaction;

enum Command {
  LISTDATABASES("list databases", 
	                "List all of the available databases", null),
  LISTGENES("list genes", 
	            "List all of the genes that meet the criteria",
	            "database=ecoli|name"),
  LISTPATHWAYS("list pathways", 
	            "List all of the pathways that meet the criteria",
	            "database=ecoli|gene"),
  LISTPROTEINS("list proteins", 
	            "List all of the proteins that meet the criteria",
	            "database=ecoli|name"),
  LISTREACTIONS("list reactions", 
	            "List all of the reactions that meet the criteria",
	            "database=ecoli|protein|gene"),
	LOADPATHWAY("load pathway",
	            "Load a pathway in biopax format",
	            "database=ecoli|pathway"),
	OPENRESOURCE("open resource",
	            "Open a Pathway Tools Resource",
	            "url");

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
 * 
 */
public class BioCycCommandHandler extends AbstractCommandHandler {
	CyLogger logger;
	QueryHandler handler;

	public static final String DATABASE = "database";
	public static final String ENZYME = "enzyme";
	public static final String GENE = "gene";
	public static final String NAME = "name";
	public static final String PATHWAY = "pathway";
	public static final String PROTEIN = "protein";
	public static final String REACTION = "reaction";
	public static final String SUBSTRATE = "substrate";
	public static final String URL = "url";

	public BioCycCommandHandler(String namespace, CyLogger logger) {
		super(CyCommandManager.reserveNamespace(namespace));

		this.logger = logger;

		for (Command command: Command.values()) {
			addCommand(command.getCommand(), command.getDescription(), command.getArgString());
		}

		// TODO: this really needs to be externalized!
		handler = new QueryHandler(logger);
	}

  public CyCommandResult execute(String command, Collection<Tunable>args)
                                                      throws CyCommandException, RuntimeException {
		return execute(command, createKVMap(args));
	}

	public CyCommandResult execute(String command, Map<String, Object>args)
                                                      throws CyCommandException, RuntimeException {
		CyCommandResult result = new CyCommandResult();

  	// LISTDATABASES("list databases", 
	 	//               "List all of the available databases", ""),
	 	if (Command.LISTDATABASES.equals(command)) {
			result.addMessage("Available databases: ");
			List<Database>databases = Database.getDatabases(handler.query("dbs"));
			for (Database d: databases) {
				result.addMessage("   "+d.getOrgID());
				result.addResult(d.getOrgID(), d);
			}
			return result;
		}

		// OPENRESOURCE("open resource",
	 	//              "Open a Pathway Tools Resource",
	 	//              "url");
		if (Command.OPENRESOURCE.equals(command)) {
			String url = getArg(command, URL, args);
			if (url == null || url.length() == 0)
				throw new CyCommandException("Must specify a url for the resource");
			
			String oldUrl = BioCycPlugin.getBaseUrl();
			BioCycPlugin.setProp(BioCycPlugin.WEBSERVICE_URL, url);
			result.addMessage("Switched resource from "+oldUrl+" to "+url);
			return result;
		}

		// All of the rest of these require a database
		String database = getArg(command, DATABASE, args);
		if (database == null)
			throw new CyCommandException("Must specify a database");

		// Databases are upper-case
		database = database.toUpperCase();

		// LOADPATHWAY("load pathway",
	 	//            "Load a pathway in biopax format",
	 	//            "database=ecoli|pathway");
	 	if (Command.LOADPATHWAY.equals(command)) {
			// Get the pathway
			String pathway = getArg(command, PATHWAY, args);
			if (pathway == null)
				throw new CyCommandException("Must specify a pathway to load");
			
			try {
				handler.loadNetwork(database+"/pathway-biopax?type=2&object="+pathway);
				result.addMessage("Loaded pathway "+pathway);
			} catch (Exception ex) {
				result.addError("Unable to load network from "+database+"/pathway-biopax?type=2&object="+pathway+": "+ex.getMessage());
			}
			return result;
		}

  	// LISTGENES("list genes", 
	 	//            "List all of the genes that meet the criteria",
	 	//            "database=ecoli|name"),
	 	if (Command.LISTGENES.equals(command)) {
			String name = getArg(command, NAME, args);
			String query = "[x:x<-"+database+"^^genes";
			if (name != null) {
				query = query+",\""+name+"\" instringci x^names";
				result.addMessage("Available genes with name "+name+": ");
			} else {
				result.addMessage("Available genes: ");
			}
			query = query+"]";

			List<Gene>genes = Gene.getGenes(handler.query(query));
			for (Gene g: genes) {
				result.addMessage("   "+g.getID());
				result.addResult(g.getID(), g);
			}
			return result;
		}

		// Both of the next commands can take protein or gene restrictions
		// String protein = getArg(command, PROTEIN, args);
		String gene = getArg(command, GENE, args);

  	// LISTPATHWAYS("list pathways", 
	 	//            "List all of the pathways that meet the criteria",
	 	//            "database=ecoli|gene"),
	 	if (Command.LISTPATHWAYS.equals(command)) {
			String queryString = "[x:x<-"+database+"^^pathways";
			if (gene != null)
				queryString = queryString+","+database+"~"+gene+" in (pathway-to-genes x)";
			queryString = queryString+"]";

			List<Pathway>pathways = Pathway.getPathways(handler.query(queryString));
			for (Pathway p: pathways) {
				result.addMessage("   "+p.getID());
				result.addResult(p.getID(), p);
			}
			return result;
		}

  	// LISTPROTEINS("list proteins", 
	 	//            "List all of the proteins that meet the criteria",
	 	//            "database=ecoli|name"),
	 	if (Command.LISTPROTEINS.equals(command)) {
			String name = getArg(command, NAME, args);
			String query = "[x:x<-"+database+"^^proteins";
			if (name != null) {
				query = query+",\""+name+"\" instringci x^names";
				result.addMessage("Available proteins with name "+name+": ");
			} else {
				result.addMessage("Available proteins: ");
			}
			query = query+"]";

			try {
				List<Protein>proteins = Protein.getProteins(handler.query(query));
				for (Protein p: proteins) {
					result.addMessage("   "+p.getID());
					result.addResult(p.getID(), p);
				}
			} catch (Exception e) {
				logger.error("Unable to get proteins: "+e.getMessage(),e);
				result.addError("Unable to get proteins: "+e.getMessage());
			}
			return result;
		}

  	// LISTREACTIONS("list reactions", 
	 	//            "List all of the reactions that meet the criteria",
	 	//            "database=ecoli|gene"),
	 	if (Command.LISTREACTIONS.equals(command)) {
			String query = "[x:x<-"+database+"^^reactions";
			if (gene != null)
				query = query+","+database+"~"+gene+" in (reaction-to-genes x)";
			query = query+"]";
			result.addMessage("Available reactions with gene "+gene+": ");
			List<Reaction>reactions = Reaction.getReactions(handler.query(query));
			for (Reaction r: reactions) {
				result.addMessage("   "+r.getID());
				result.addResult(r.getID(), r);
			}
			return result;
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
