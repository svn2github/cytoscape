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

/**
 * 
 */
public class Reactant {
	CyLogger logger;
	String ID;
	String orgid;
	String frameid;
	List<String> dblinks = null;
	List<String> synonyms = null;

	public Reactant (Element reactant) {
		this.ID = reactant.getAttribute("ID");
		this.orgid = reactant.getAttribute("orgid");
		this.frameid = reactant.getAttribute("frameid");
		this.dblinks = getDbLinks(reactant);
		this.synonyms = new ArrayList<String>();
		List<Element> synonymElements = getChildElements(reactant, "synonym");
		for (Element e: synonymElements) {
			synonyms.add(getChildData(e));
		}
	}

	public String getID() { return ID; }
	public String getOrgID() { return orgid; }
	public String getFrameID() { return frameid; }
	public List<String> getDbLinks() { return dblinks; }
	public List<String> getSynonyms() { return synonyms; }
}
