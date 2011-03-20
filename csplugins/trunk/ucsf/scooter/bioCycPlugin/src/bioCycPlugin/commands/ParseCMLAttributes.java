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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.io.StringReader;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.xml.sax.InputSource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

// Cytoscape imports
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.logger.CyLogger;

import bioCycPlugin.model.DomUtils;

public class ParseCMLAttributes implements PropertyChangeListener {
	final static String CML_ATTRIBUTE = "biopax.chemicalStructure.STRUCTURE-DATA";
	final static String SMILES_ATTRIBUTE = "smiles";
	CyLogger logger;

	public ParseCMLAttributes(CyLogger logger) {
		this.logger = logger;
	}


	public void propertyChange(PropertyChangeEvent evt) {
		if ( evt.getPropertyName() == Cytoscape.NETWORK_LOADED && evt.getNewValue() != null) {
			// Get the network that was loaded
			Object[] ret_val = (Object[]) evt.getNewValue();
			CyNetwork network = (CyNetwork) ret_val[0];
			CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
			// Iterate through all of the nodes and find the CML (if there is any)
			for (CyNode node: (List<CyNode>)network.nodesList()) {
				if (nodeAttributes.hasAttribute(node.getIdentifier(), CML_ATTRIBUTE)) {
					addSMILES(node, nodeAttributes.getStringAttribute(node.getIdentifier(), CML_ATTRIBUTE));
				}
			}
			Cytoscape.getPropertyChangeSupport()
			                  .removePropertyChangeListener( Cytoscape.NETWORK_LOADED, this );
		}
	}

	private void addSMILES(CyNode node, String CML) {
		Document cml = null;
		CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
		// Alternative approach

		// Find the SMILES string
		int offset = CML.indexOf("smiles");
		int end = CML.indexOf("<", offset+8);

		String smilesString = CML.substring(offset+8, end);

		/*
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(CML));
			cml = builder.parse(is);
		} catch (Exception e) {
			logger.error("Unable to parse CML: "+e.getMessage());
		}
		NodeList pNodes = cml.getElementsByTagName("string");
		Node smilesNode = pNodes.item(0); // We assume that we only have one...
		if (smilesNode != null && smilesNode.getNodeType() == Node.ELEMENT_NODE) {
			Element smilesElement = (Element)smilesNode;
			if (smilesElement.getAttribute("title").equals("smiles")) {
				String smiles = DomUtils.getChildData(smilesElement);
				nodeAttributes.setAttribute(node.getIdentifier(), SMILES_ATTRIBUTE, smiles);
			}
		}
		*/
		nodeAttributes.setAttribute(node.getIdentifier(), SMILES_ATTRIBUTE, smilesString);
	}
}

