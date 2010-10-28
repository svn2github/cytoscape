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

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import cytoscape.logger.CyLogger;

/**
 * 
 */
public class DomUtils {

	public static String getChildData(Element e, String tag) {
		NodeList children = e.getElementsByTagName(tag);
		if (children == null || children.getLength() == 0)
			return null;

		return getChildData((Element)children.item(0));
	}

	public static String getAttribute(Element e, String attribute) {
		if (e.hasAttribute(attribute))
			return e.getAttribute(attribute);
		return null;
	}

	public static String getChildData(Element e) {
		NodeList eChildren = e.getChildNodes();
		if (eChildren == null || eChildren.getLength() == 0)
			return null;

		// OK, now, find the text node and return it's value
		for (int index = 0; index < eChildren.getLength(); index++) {
			Node n = eChildren.item(index);
			if (n.getNodeType() == Node.TEXT_NODE) {
				return n.getNodeValue();
			}
		}
		return null;
	}

	public static List<Element> getChildElements(Element e, String tag) {
		NodeList children = e.getElementsByTagName(tag);
		if (children == null || children.getLength() == 0)
			return null;

		List<Element> result = new ArrayList<Element>();
		for (int index = 0; index < children.getLength(); index++) {
			Node n = children.item(index);
			if (n.getNodeType() == Node.ELEMENT_NODE) {
				result.add((Element)n);
			}
		}
		return result;
	}

	public static List<DbLink> getDbLinks(Element parent) {
		List<Element> dbLinkElements = getChildElements(parent, "dblink");
		if (dbLinkElements == null || dbLinkElements.size() == 0)
			return null;
		List<DbLink> result = new ArrayList<DbLink>();
		for (Element e: dbLinkElements) {
			result.add(new DbLink(e));
		}
		return result;
	}
}
