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
import org.w3c.dom.NodeList;
import cytoscape.logger.CyLogger;

/**
 * 
 */
public class Compound extends Reactant {

	Element cml = null;
	String inchi = null;
	String componentOf = null;
	List<Compound>subclasses = null;

	public Compound(Element compound) {
		super(compound);
		NodeList cmlList = compound.getElementsByTagName("cml");
		if (cmlList != null && cmlList.getLength() > 0) {
			this.cml = (Element)cmlList.item(0);
		}

		// Get our subclasses
		NodeList subclassElements = compound.getElementsByTagName("subclass");
		if (subclassElements != null && subclassElements.getLength() > 0) {
			subclasses = new ArrayList<Compound>();
			for (int index = 0; index < subclassElements.getLength(); index++) {
				NodeList nl = ((Element)subclassElements.item(index)).getElementsByTagName("Compound");
				if (nl != null && nl.getLength() > 0) {
					subclasses.add(new Compound((Element)nl.item(0)));
				}
			}
		}
	}

	public Element getCML() { return cml; }
	public String getInCHI() { return inchi; }
	public String getComponentOf() { return componentOf; }
	public List<Compound> getSubClasses() { return subclasses; }
}
