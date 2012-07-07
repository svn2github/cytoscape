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
package bindingDB.commands;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.command.CyCommandResult;
import cytoscape.data.CyAttributes;
import cytoscape.logger.CyLogger;
import cytoscape.util.URLUtil;

public class AnnotateNetworkCommand {
	static String baseURL = "http://bdb2.ucsd.edu/axis2/services/BDBService/getAffinityByUniprot?uniprot="; 
	static String ID_ATTR = "BindingDB_ID";
	public static String SMILES_ATTR = "SMILES";
	public static String AFF_TYPE_ATTR = "Affinity_Type";
	public static String AFFINITY_ATTR = "Affinity";
	public static String AFFINITY_STR_ATTR = "Affinity_String";
	public static String HIT_ATTR = "BindingDB_Hits";

	/**
 	 * This command will add the bindingDB annotations for the specified node.  The bindingDB annotation includes
 	 * 4 fields:
 	 *  BindingDB MonomerID: the bindingDB identifier for the small molecule
 	 *  SMILES: the SMILES string
 	 *  Affinity_Type: the measurement type of the affinity
 	 *  Affinity: the affinity itself
 	 * This command will create the four attributes (if they don't exist) and add the data.
 	 */
	static public CyCommandResult annotateNetwork(CyLogger logger, CyNode node, String uniProtAttr, double cutoff) {
		CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
		CyCommandResult result = new CyCommandResult();

		if (!nodeAttributes.hasAttribute(node.getIdentifier(), uniProtAttr)) {
			result.addMessage("Node "+node.getIdentifier()+" doesn't have attribute "+uniProtAttr);
			return result;
		}

		List<String> idList = null;
		if (nodeAttributes.getType(uniProtAttr) == CyAttributes.TYPE_SIMPLE_LIST) {
			idList = (List<String>)nodeAttributes.getListAttribute(node.getIdentifier(), uniProtAttr);
		} else {
			idList = new ArrayList<String>();
			idList.add(nodeAttributes.getStringAttribute(node.getIdentifier(), uniProtAttr));
		}

		HashSet<String> monomerIDList = new HashSet<String>(); // Use a HashSet to avoid duplicate hits
		List<String> smilesList = new ArrayList<String>();
		List<String> typeList = new ArrayList<String>();
		List<String> affinityStrList = new ArrayList<String>();
		List<Double> affinityList = new ArrayList<Double>();

		int hitCount = 0;

		for (String uniProtID: idList) {
			String url = baseURL+uniProtID+";"+cutoff;
			// logger.debug("...fetching data for "+url);
			DocumentBuilder builder = null;
			Document annotations = null;
			InputStream input = null;

			try {
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				builder = factory.newDocumentBuilder();
			} catch (Exception e) {
				logger.error("Unable to create a new document: "+e.getMessage());
				result.addError("Unable to create a new document: "+e.getMessage());
				return result;
			}

			try {
				input = URLUtil.getBasicInputStream(new URL(url));
				annotations = builder.parse(input);
			} catch (Exception e) {
				logger.error("Unable to get annotations for "+uniProtID+": "+e.getMessage());
				result.addError("Unable to get annotations for "+uniProtID+": "+e.getMessage());
				return result;
			}

			// OK, now we have all of the annotations.  Build the lists
			NodeList affinities = annotations.getElementsByTagName("bdb:affinities");

			// Iterate over all of the affinities
			for (int index = 0; index < affinities.getLength(); index++) {
				Node affinity = affinities.item(index);
				if (affinity.getNodeType() != Node.ELEMENT_NODE)
					continue;

				NodeList children = affinity.getChildNodes();
				// Iterate over all of the children to get our data
				for (int elementIndex = 0; elementIndex < children.getLength(); elementIndex++) {
					Node element = children.item(elementIndex);
					if (element.getNodeType() == Node.ELEMENT_NODE) {
						String data = getContent(element);
						if (element.getNodeName().equals("bdb:monomerid")) {
							if (monomerIDList.contains(data)) 
								continue;

							// logger.debug("Found id "+data);
							monomerIDList.add(data);
							hitCount++;
						} else if (element.getNodeName().equals("bdb:smiles")) {
							// logger.debug("Found smiles "+data);
							if (data.indexOf('|') >= 0 ) {
								String[] d = data.split("\\|");  // Get rid of extra annotation
								smilesList.add(d[0].trim());
							} else {
								smilesList.add(data.trim());
							}
						} else if (element.getNodeName().equals("bdb:affinity_type")) {
							// logger.debug("Found type "+data);
							typeList.add(data);
						} else if (element.getNodeName().equals("bdb:affinity")) {
							affinityStrList.add(data);
							// logger.debug("Found affinity "+data);
							// Special case
							if (data.indexOf('<') >= 0 ) {
								int offset = data.indexOf('<');
								double v = Double.parseDouble(data.substring(offset+1));
								affinityList.add(new Double(v/1.01));
							} else if (data.indexOf('>') >= 0 ) {
								int offset = data.indexOf('>');
								double v = Double.parseDouble(data.substring(offset+1));
								affinityList.add(new Double(v/.99));
							} else {
								affinityList.add(new Double(data));
							}
						}
					}
				}
			}
		}

		result.addResult(ID_ATTR, monomerIDList);
		result.addResult(SMILES_ATTR, smilesList);
		result.addResult(AFF_TYPE_ATTR, typeList);
		result.addResult(AFFINITY_ATTR, affinityList);
		result.addResult(AFFINITY_STR_ATTR, affinityStrList);
		result.addMessage("Node "+node.getIdentifier()+" has "+hitCount+" hits");

		// Now, set the values for this node
		nodeAttributes.setListAttribute(node.getIdentifier(), ID_ATTR, new ArrayList<String>(monomerIDList));
		nodeAttributes.setListAttribute(node.getIdentifier(), SMILES_ATTR, smilesList);
		nodeAttributes.setListAttribute(node.getIdentifier(), AFF_TYPE_ATTR, typeList);
		nodeAttributes.setListAttribute(node.getIdentifier(), AFFINITY_ATTR, affinityList);
		nodeAttributes.setListAttribute(node.getIdentifier(), AFFINITY_STR_ATTR, affinityStrList);
		nodeAttributes.setAttribute(node.getIdentifier(), HIT_ATTR, new Integer(hitCount));
		return result;
	}

	static String getContent(Node node) {
		if (node.getNodeType() == Node.ELEMENT_NODE && node.hasChildNodes()) {
			Node child = node.getFirstChild();
			if (child.getNodeType() == Node.TEXT_NODE)
				return child.getNodeValue();
		}
		return null;
	}
}
