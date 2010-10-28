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

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Element;
import cytoscape.logger.CyLogger;

/**
 * 
 */
public class Reaction {
	CyLogger logger;

	List<Reaction> parents = null;
	List<Reactant> left = null;
	List<Reactant> right = null;
	String resource;
	String ID;
	String orgid;
	String frameid;


	public Reaction(Element reaction) {
		this.ID = reaction.getAttribute("ID");
		this.orgid = reaction.getAttribute("orgid");
		this.frameid = reaction.getAttribute("frameid");
		this.resource = reaction.getAttribute("resource");
		List<Element> parentElements = getChildElements(reaction, "parent");
		this.parents = getParentReactions(parentElements);
		List<Element> leftElements = getChildElements(reaction, "left");
		this.left = getReactants(leftElements);
		List<Element> rightElements = getChildElements(reaction, "right");
		this.right = getReactants(rightElements);
		
	}

	public String getID() { return ID; }
	public String getOrgID() { return orgid; }
	public String getFrameID() { return frameid; }
	public String getResource() { return resource; }
	public List<Reaction> getParents() { return parents; }
	public List<Reactant> getLeft() { return left; }
	public List<Reactant> getRight() { return right; }

	private List<Reaction> getParentReactions(List<Element> pElements) {
		if (pElements == null || pElements.length() == 0)
			return null;

		List<Reaction> resultList = new ArrayList<Reaction>();
		for (Element e: pElements) {
			NodeList childList = e.getElementsByTagName("Reaction");
			// childList is either null or the Reaction element...
			if (childList == null || childList.getLength() == 0) continue;
			resultList.add(new Reaction((Element)(childList.item(0))));
		}
		return resultList;
	}

	private List<Reactant> getReactants(List<Element> rElements) {
		if (rElements == null || rElements.length() == 0)
			return null;

		List<Reactant> resultList = new ArrayList<Reactant>();
		for (Element e: rElements) {
			NodeList childList = e.getElementsByTagName("Protein");
			// childList is either null or the Protein element...
			if (childList != null && childList.getLength() > 0) {
				resultList.add(new Protein((Element)(childList.item(0))));
			} else {
				// might be a compound
				childList = e.getElementsByTagName("Compound");
				if (childList == null || childList.getLength() == 0) {
					continue;
				}
				resultList.add(new Compound((Element)(childList.item(0))));
			}
		}
		return resultList;
}
