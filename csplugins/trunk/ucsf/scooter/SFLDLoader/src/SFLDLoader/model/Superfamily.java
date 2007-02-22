/* vim: set ts=2: */
/**
 * Copyright (c) 2006 The Regents of the University of California.
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
package SFLDLoader.model;

// System imports
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.lang.Comparable;
import java.util.Arrays;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.NamedNodeMap;

/**
 * The Superfamily class represents an SFLD superfamily, which has
 * a list of subgroups, a description, and an id that is used to
 * actually pull the network.
 * 
 */

public class Superfamily implements Comparable {
	private String name;
	private List subgroups;
	private String description;
	private int id;

	public Superfamily(Node superNode) {
		subgroups = new ArrayList();
		// Get our attributes
		NamedNodeMap attributes = superNode.getAttributes();
		this.name = attributes.getNamedItem("name").getNodeValue();
		String str_id = attributes.getNamedItem("sid").getNodeValue();
		this.id = (new Integer(str_id)).intValue();

		// Get our children
		NodeList children = superNode.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			if (!"#text".equals(child.getNodeName()))
				subgroups.add(new Subgroup(child));
		}

		// Sort our children
		Object[] sortable = subgroups.toArray();
		Arrays.sort(sortable);
		subgroups = Arrays.asList(sortable);
	}

	public Superfamily(String name, int id) {
		this.name = name;
		this.id = id;
		this.description = null;
		this.subgroups = new ArrayList();
	}

	public Superfamily(String name, List subgroups,
	                   String description, int id) {
		this.name = name;
		this.id = id;
		this.description = description;
		this.subgroups = subgroups;
	}

	public String getName() { return name; }
	public void setName(String name) { 
		this.name = name; 
	}

	public int getId() { return id; }
	public void setId(int id) { this.id = id; }

	public String getDescription() { return description; }
	public void setDescription(String description) { 
		this.description = description; 
	}

	public List<Subgroup> getSubgroups() { return subgroups; }
	public void setSubgroups(List<Subgroup> subgroups) { 
		this.subgroups = subgroups;
	}

	public int getSubgroupCount() {
		return subgroups.size();
	}

	public Subgroup getSubgroup(int index) {
		return (Subgroup)subgroups.get(index);
	}

	public int getProteinCount() {
		int nProteins = 0;
		Iterator iter = subgroups.iterator();
		while (iter.hasNext())
			nProteins += ((Subgroup)iter.next()).getProteinCount();
		return nProteins;
	}

	public int compareTo(Object o) {
		Superfamily other = (Superfamily) o;
		return name.compareToIgnoreCase(other.getName());
	}
}
