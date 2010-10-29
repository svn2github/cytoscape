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

import bioCycPlugin.BioCycPlugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.actions.LoadNetworkTask;
import cytoscape.data.ImportHandler;
import cytoscape.data.readers.GraphReader;
import cytoscape.logger.CyLogger;
import cytoscape.util.CyFileFilter;
import cytoscape.util.ProxyHandler;
import cytoscape.util.URLUtil;

import bioCycPlugin.model.Gene;
import bioCycPlugin.model.Pathway;
import bioCycPlugin.model.Protein;

/**
 * 
 */
public class QueryHandler {
	CyLogger logger;
	DocumentBuilder builder = null;

	public QueryHandler(CyLogger logger) {
		this.logger = logger;
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			builder = factory.newDocumentBuilder();
		} catch (Exception e) {
			logger.error("Unable to create a new document: "+e.getMessage());
		}
	}

	public Document query(String queryString) {
		InputStream input;
		Document result;
		String baseUrl = BioCycPlugin.getBaseUrl();
		logger.info("Executing query: "+baseUrl+"xmlquery?query="+queryString);
		try {
			queryString = baseUrl+"xmlquery?query="+URLEncoder.encode(queryString, "UTF-8");
			// System.out.println("Executing query: "+queryString);
			input = URLUtil.getBasicInputStream(new URL(queryString));
			// result = builder.parse(teeInput(input));
			result = builder.parse(teeInput(input));
		} catch (Exception e) {
			logger.error("Unable to process query "+queryString+": "+e.getMessage(),e);
			return null;
		}
		return result;
	}

	public Document findPathways(String database, String text) {
			String queryString = null;
			queryString = "[x:x<-"+database+"^^pathways";
			if (text != null) {
				queryString = queryString+","+database+"~"+text+" in (pathway-to-genes x)";
			}
			queryString = queryString+"]";
			return query(queryString);
	}

	public void loadNetwork(String queryString) throws MalformedURLException {
		// Unfortunately, the following would be the easiest approach, but
		// biopax isn't automatically recognized, so we need to do this "the hard way"
		// LoadNetworkTask.loadURL(new URL(baseUrl+queryString), false);
		String baseUrl = BioCycPlugin.getBaseUrl();
		logger.info("Executing query: "+baseUrl+queryString);

		ImportHandler inputHandler = Cytoscape.getImportHandler();
		try {
			File rawFile = inputHandler.downloadFromURL(new URL(baseUrl+queryString), null);
			File bioPaxFile = new File(rawFile.getAbsolutePath()+".xml");
			rawFile.renameTo(bioPaxFile);
			// Add a listener so we can fix up some attributes
			Cytoscape.getPropertyChangeSupport()
			                  .addPropertyChangeListener( Cytoscape.NETWORK_LOADED, new ParseCMLAttributes(logger) );

			LoadNetworkTask.loadFile(bioPaxFile, false);
			bioPaxFile.delete();
		} catch (Exception e) {
			throw new MalformedURLException("Unable to load network from "+baseUrl+queryString+" ("+e.getMessage()+")");
		}
	}

	public List<Pathway> andPathways(List<Pathway>a, List<Pathway>b) {
	}

	public List<Pathway> orPathways(List<Pathway>pathways1, List<Pathway>pathways2) {
		for (Pathway p: pathways2) {
			if (!pathways1.contains(p))
				pathways1.add(p);
		}
		return pathways1;

	}

	public List<Pathway> searchForPathways(String database, String text) {
		Map<String,Pathway> pathwayMap = new HashMap<String,Pathway>();
		// We do this iteratively.  First, do a direct match on pathways,
		// then we to find all genes with that text, then
		// all proteins.  Then we'll get all of the pathways that match those genes.
		String queryString = "{y: y<- [x:x<-"+database+"^^pathways,\""+text+"\" instringci x^names]";
		queryString += " ++ [x:x<-"+database+"^^genes,\""+text+"\" instringci x^names]";
		queryString += " ++ {p:x<-"+database+"^^proteins,\""+text+"\" instringci x^names, g<-genes-of-protein(x), p<-"+database+"^^pathways, g in (pathway-to-genes p)}";
		queryString += " ++ {p:x<-"+database+"^^compounds,\""+text+"\" instringci x^names, p<-"+database+"^^pathways, x in (compounds-of-pathway p)} }";
/*
		{ 
			String queryString = "[x:x<-"+database+"^^pathways,\""+text+"\" instringci x^names]";
			List<Pathway>pathways = Pathway.getPathways(query(queryString));
			if (pathways != null) {
				for (Pathway p: pathways) {
					if (!pathwayMap.containsKey(p.getFrameID()))
						pathwayMap.put(p.getFrameID(), p);
				}
			}
		}
		// Now look for all genes that match.  We do this by first finding matching genes,
		// then finding the genes for the proteins that match
		{
			Map<String,Gene> geneList = new HashMap<String,Gene>();
			String queryString = "[x:x<-"+database+"^^genes,\""+text+"\" instringci x^names]";
			List<Gene>genes = Gene.getGenes(query(queryString));
			if (genes != null) {
				for (Gene g: genes) {
					geneList.put(g.getFrameID(), g);
				}
			}
			// OK, now that we have the first set, get all of the proteins
			queryString = "{p:x<-"+database+"^^proteins,\""+text+"\" instringci x^names, g<-genes-of-protein(x), p<-"+database+"^^pathways, g in (pathway-to-genes p)}";

			List<Pathway> pathways = Pathway.getPathways(query(queryString));
			if (pathways != null) {
				for (Pathway p: pathways) {
					if (!pathwayMap.containsKey(p.getFrameID()))
						pathwayMap.put(p.getFrameID(), p);
				}
			}
		}

		{
			String queryString = "{p:x<-"+database+"^^compounds,\""+text+"\" instringci x^names, p<-"+database+"^^pathways, x in (compounds-of-pathway p)}";
			List<Pathway> pathways = Pathway.getPathways(query(queryString));
			if (pathways != null) {
				for (Pathway p: pathways) {
					if (!pathwayMap.containsKey(p.getFrameID()))
						pathwayMap.put(p.getFrameID(), p);
				}
			}
		}
*/
		List<Pathway> pathways = Pathway.getPathways(query(queryString));
		if (pathways != null) {
			for (Pathway p: pathways) {
				if (!pathwayMap.containsKey(p.getFrameID()))
					pathwayMap.put(p.getFrameID(), p);
			}
		}

		return new ArrayList<Pathway>(pathwayMap.values());
	}

	private InputSource teeInput(InputStream input) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(input));
		StringBuilder sBuilder = new StringBuilder();
		String line;
		try {
			while ((line = reader.readLine()) != null) {
				// System.out.println(": "+line);
				sBuilder.append(line);
			}
		} catch (Exception e) {
			logger.error("",e);
		}
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(sBuilder.toString()));
		return is;
	}
}
