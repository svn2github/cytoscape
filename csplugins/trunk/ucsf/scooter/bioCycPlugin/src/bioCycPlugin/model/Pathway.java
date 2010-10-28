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
package bioCycPlugin.model;

import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import cytoscape.logger.CyLogger;

/**
 * 
 */
public class Pathway {
	CyLogger logger;

	List<Reaction> reactionList = null;
	List<Pathway> parents = null;
	List<Pathway> superPathways = null;
	String ID = null;
	String orgid = null;
	String frameid = null;
	String commonName = null;
	String comment = null;
	String resource = null;


	public Pathway(Element pathway) {
		this.ID = DomUtils.getAttribute(pathway,"ID");
		this.orgid = DomUtils.getAttribute(pathway,"orgid");
		this.frameid = DomUtils.getAttribute(pathway,"frameid");
		this.resource = DomUtils.getAttribute(pathway,"resource");
		this.commonName = DomUtils.getChildData(pathway, "common-name");
		this.comment = DomUtils.getChildData(pathway, "comment");
		List<Element> parentElements = DomUtils.getChildElements(pathway, "parent");
		this.parents = getPathwayElements(parentElements);
		List<Element> superElements = DomUtils.getChildElements(pathway, "super-pathways");
		this.superPathways = getPathwayElements(superElements);

		List<Element> reactionElements = DomUtils.getChildElements(pathway, "reaction-list");
		this.reactionList = getReactionElements(reactionElements);

	}

	public String getID() { return ID; }
	public String getOrgID() { return orgid; }
	public String getFrameID() { return frameid; }
	public String getCommonName() { return commonName; }
	public String getComment() { return comment; }
	public List<Pathway> getParents() { return parents; }
	public List<Pathway> getSuperPathways() { return superPathways; }
	public List<Reaction> getReactions() { return reactionList; }
	public String toString() {
		String result = "ID:"+ID+"|OrgID:"+orgid+"|common-name:"+commonName;
		return result;
	}

	public static List<Pathway> getPathways(Document response) {
		NodeList pNodes = response.getElementsByTagName("Pathway");
		if (pNodes == null || pNodes.getLength() == 0) return null;

		List<Pathway> pathways = new ArrayList<Pathway>();
		for (int index = 0; index < pNodes.getLength(); index++) {
			Pathway p = new Pathway((Element)pNodes.item(index));
			if (p.getID() != null)
				pathways.add(p);
		}
		return pathways;
	}

	private List<Pathway> getPathwayElements(List<Element> pElements) {
		if (pElements == null || pElements.size() == 0)
			return null;

		List<Pathway> resultList = new ArrayList<Pathway>();
		for (Element e: pElements) {
			NodeList childList = e.getElementsByTagName("Pathway");
			// childList is either null or the Pathway element...
			if (childList == null || childList.getLength() == 0) continue;
			resultList.add(new Pathway((Element)(childList.item(0))));
		}
		return resultList;
	}

	private List<Reaction> getReactionElements(List<Element> rElements) {
		if (rElements == null || rElements.size() == 0)
			return null;

		List<Reaction> resultList = new ArrayList<Reaction>();
		for (Element e: rElements) {
			NodeList childList = e.getElementsByTagName("Reaction");
			// childList is either null or the Protein element...
			if (childList != null && childList.getLength() > 0) {
				resultList.add(new Reaction((Element)(childList.item(0))));
			} 
		}
		return resultList;
	}
}
