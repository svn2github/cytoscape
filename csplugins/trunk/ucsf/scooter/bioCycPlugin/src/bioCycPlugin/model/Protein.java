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

/**
 * 
 */
public class Protein extends Reactant {

	List<Protein> parents = null;
	Gene gene = null;
	List<Protein> componentOf = null;
	List<Protein> components = null;
	String commonName = null;
	List<Protein> subclasses = null;
	List<Reaction> catalyzes = null;

	// Future
	// List<GO-Term> goTerms;
	// List<Feature> features;
	// String location = null;

	public Protein(Element protein) {
		super(protein);

		// Get our parents (if any)
		List<Element> parentElements = DomUtils.getChildElements(protein, "parent");
		this.parents = getProteins(parentElements);
		// Get any complexes we're part of
		List<Element> componentOfElements = DomUtils.getChildElements(protein, "component-of");
		this.componentOf = getProteins(componentOfElements);
		// Get any components we have
		List<Element> componentElements = DomUtils.getChildElements(protein, "component");
		this.components = getProteins(componentElements);
		// Get our gene list
		List<Element> geneElements = DomUtils.getChildElements(protein, "gene");
		if (geneElements != null && geneElements.size() > 0) {
			this.gene = new Gene(geneElements.get(0));
		}
		// Get the reactions we catalyze
		List<Element> reactionElements = DomUtils.getChildElements(protein, "catalyzes");
		this.catalyzes = Reaction.getReactions(reactionElements);

		// Get our subclasses
		NodeList subclassElements = protein.getElementsByTagName("subclass");
		if (subclassElements != null && subclassElements.getLength() > 0) {
			this.subclasses = new ArrayList<Protein>();
			for (int index = 0; index < subclassElements.getLength(); index++) {
				NodeList nl = ((Element)subclassElements.item(index)).getElementsByTagName("Compound");
				if (nl != null && nl.getLength() > 0) {
					this.subclasses.add(new Protein((Element)nl.item(0)));
				}
			}
		}
	}

	public List<Protein> getProteinSubClasses() { return subclasses; }
	public List<Reaction> getReactionsCatalyzed() { return catalyzes; }
	public Gene getGene() { return gene; }
	public String getCommonName() { return commonName; }
	public List<Protein> getComponents() { return components; }
	public List<Protein> getComponentMembership() { return componentOf; }

	public static List<Protein> getProteins(List<Element> pElements) {
		if (pElements == null || pElements.size() == 0)
			return null;

		List<Protein> resultList = new ArrayList<Protein>();
		for (Element e: pElements) {
			NodeList childList = e.getElementsByTagName("Protein");
			// childList is either null or the Protein element...
			if (childList == null || childList.getLength() == 0) continue;
			resultList.add(new Protein((Element)(childList.item(0))));
		}
		return resultList;
	}

	public static List<Protein> getProteins(Document response) {
		NodeList pNodes = response.getElementsByTagName("Protein");
		if (pNodes == null || pNodes.getLength() == 0) return null;

		List<Protein> proteins = new ArrayList<Protein>();
		for (int index = 0; index < pNodes.getLength(); index++) {
			Protein p = new Protein((Element)pNodes.item(index));
			if (p.getID() != null)
				proteins.add(p);
		}
		return proteins;
	}

}
