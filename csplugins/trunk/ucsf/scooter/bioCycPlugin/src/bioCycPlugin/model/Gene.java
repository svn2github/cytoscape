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
public class Gene extends Reactant {

	List<Gene> parents = null;
	String commonName = null;
	List<Protein> product = null;
	List<Gene> instances = null;

	public Gene(Element gene) {
		super(gene);

		List<Element> parentElements = DomUtils.getChildElements(gene, "parent");
		this.parents = getGenes(parentElements);

		List<Element> productElements = DomUtils.getChildElements(gene, "product");
		this.product = Protein.getProteins(productElements);

		this.commonName = DomUtils.getChildData(gene, "common-name");

		NodeList subclassElements = gene.getElementsByTagName("instance");
		if (subclassElements != null && subclassElements.getLength() > 0) {
			this.instances = new ArrayList<Gene>();
			for (int index = 0; index < subclassElements.getLength(); index++) {
				NodeList nl = ((Element)subclassElements.item(index)).getElementsByTagName("Gene");
				if (nl != null && nl.getLength() > 0) {
					this.instances.add(new Gene((Element)nl.item(0)));
				}
			}
		}
	}

	public static List<Gene> getGenes(List<Element> gElements) {
		if (gElements == null || gElements.size() == 0)
			return null;

		List<Gene> resultList = new ArrayList<Gene>();
		for (Element e: gElements) {
			NodeList childList = e.getElementsByTagName("Gene");
			// childList is either null or the Gene element...
			if (childList == null || childList.getLength() == 0) continue;
			resultList.add(new Gene((Element)(childList.item(0))));
		}
		return resultList;
	}

	public static List<Gene> getGenes(Document response) {
		NodeList gNodes = response.getElementsByTagName("Gene");
		if (gNodes == null || gNodes.getLength() == 0) return null;

		List<Gene> genes = new ArrayList<Gene>();
		for (int index = 0; index < gNodes.getLength(); index++) {
			Gene g = new Gene((Element)gNodes.item(index));
			if (g.getID() != null)
				genes.add(g);
		}
		return genes;
	}

}
