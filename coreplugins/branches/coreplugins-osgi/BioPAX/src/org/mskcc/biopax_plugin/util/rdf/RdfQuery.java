// $Id: RdfQuery.java,v 1.2 2006/06/15 22:06:02 grossb Exp $
//------------------------------------------------------------------------------
/** Copyright (c) 2006 Memorial Sloan-Kettering Cancer Center.
 **
 ** Code written by: Ethan Cerami
 ** Authors: Ethan Cerami, Gary Bader, Chris Sander
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** any later version.
 **
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and
 ** Memorial Sloan-Kettering Cancer Center
 ** has no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall
 ** Memorial Sloan-Kettering Cancer Center
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if
 ** Memorial Sloan-Kettering Cancer Center
 ** has been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 **
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/
package org.mskcc.biopax_plugin.util.rdf;

import org.jdom.Attribute;
import org.jdom.Element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;


/**
 * Enables XPath-"lite" Queries on RDF Documents.
 * <p/>
 * To make things really simple, this implementation ignore case, and
 * ignores all Namespaces.
 *
 * @author Ethan Cerami.
 */
public class RdfQuery {
	private HashMap rdfMap;

	/**
	 * A Hashmap of all XML Elements, keyed by RDF ID.
	 *
	 * @param rdfMap HashMap of RDF ID to XML Element.
	 */
	public RdfQuery(HashMap rdfMap) {
		this.rdfMap = rdfMap;
	}

	/**
	 * Gets all Nodes that match the XPath-"lite" Query.
	 *
	 * @param e     Target Element.
	 * @param query XPath-"lite" Query.
	 * @return ArrayList of JDOM Elements, which match the query.
	 */
	public ArrayList getNodes(Element e, String query) {
		StringTokenizer tokenizer = new StringTokenizer(query, "/");
		ArrayList queryList = new ArrayList();

		while (tokenizer.hasMoreElements()) {
			String target = tokenizer.nextToken();
			queryList.add(target);
		}

		return traverse(e, queryList, 0);
	}

	/**
	 * Gets First Node that match the XPath-"lite" Query.
	 *
	 * @param e     Target Element.
	 * @param query XPath-"lite" Query.
	 * @return ArrayList of JDOM Elements, which match the query.
	 */
	public Element getNode(Element e, String query) {
		StringTokenizer tokenizer = new StringTokenizer(query, "/");
		ArrayList queryList = new ArrayList();

		while (tokenizer.hasMoreElements()) {
			String target = tokenizer.nextToken();
			queryList.add(target);
		}

		ArrayList list = traverse(e, queryList, 0);

		if (list.size() > 0) {
			return (Element) list.get(0);
		} else {
			return null;
		}
	}

	/**
	 * Traverse the Document Tree, matching the query one level at a time.
	 */
	private ArrayList traverse(Element e, ArrayList queryList, int index) {
		String target = (String) queryList.get(index);
		List children = new ArrayList();

		//  If this element points to something via RDF, go get the reference,
		//  and keep walking from there.
		Attribute rdfResourceAttribute = e.getAttribute(RdfConstants.RESOURCE_ATTRIBUTE,
		                                                RdfConstants.RDF_NAMESPACE);

		if (rdfResourceAttribute != null) {
			String rdfKey = RdfUtil.removeHashMark(rdfResourceAttribute.getValue());
			Element resource = (Element) rdfMap.get(rdfKey);
			children.add(resource);
		} else {
			children = e.getChildren();
		}

		ArrayList targetNodes = new ArrayList();

		for (int i = 0; i < children.size(); i++) {
			Element child = (Element) children.get(i);

			if (target.equals("*") || child.getName().equalsIgnoreCase(target)) {
				if (index < (queryList.size() - 1)) {
					targetNodes.addAll(traverse(child, queryList, index + 1));
				} else {
					targetNodes.add(child);
				}
			}
		}

		return targetNodes;
	}
}
