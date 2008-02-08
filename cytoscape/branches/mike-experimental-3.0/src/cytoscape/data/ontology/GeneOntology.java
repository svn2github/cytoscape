/*
 Copyright (c) 2006, 2007, The Cytoscape Consortium (www.cytoscape.org)

 The Cytoscape Consortium is:
 - Institute for Systems Biology
 - University of California San Diego
 - Memorial Sloan-Kettering Cancer Center
 - Institut Pasteur
 - Agilent Technologies

 This library is free software; you can redistribute it and/or modify it
 under the terms of the GNU Lesser General Public License as published
 by the Free Software Foundation; either version 2.1 of the License, or
 any later version.

 This library is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 documentation provided hereunder is on an "as is" basis, and the
 Institute for Systems Biology and the Whitehead Institute
 have no obligations to provide maintenance, support,
 updates, enhancements or modifications.  In no event shall the
 Institute for Systems Biology and the Whitehead Institute
 be liable to any party for direct, indirect, special,
 incidental or consequential damages, including lost profits, arising
 out of the use of this software and its documentation, even if the
 Institute for Systems Biology and the Whitehead Institute
 have been advised of the possibility of such damage.  See
 the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation,
 Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
*/
package cytoscape.data.ontology;

import cytoscape.GraphPerspective;
import cytoscape.Cytoscape;

import cytoscape.data.CyAttributes;

import cytoscape.data.ontology.readers.OBOTags;
import static cytoscape.data.ontology.readers.OBOTags.DEF;
import static cytoscape.data.ontology.readers.OBOTags.NAME;
import static cytoscape.data.ontology.readers.OBOTags.NAMESPACE;
import static cytoscape.data.readers.MetadataEntries.SOURCE;

import org.biojava.ontology.Term;

import java.net.MalformedURLException;
import java.net.URISyntaxException;

import java.util.List;


/**
 * Gene Ontology object based on general Ontology.<br>
 *
 *
 * @author kono
 *
 */
public class GeneOntology extends Ontology {
	private CyAttributes goTermAttributes = Cytoscape.getNodeAttributes();
	public enum GOAspect {
		BIOLOGICAL_PROCESS("P"),
		CELLULAR_COMPONENT("C"),
		MOLECULAR_FUNCTION("F");

		private String aspect;

		private GOAspect(String aspect) {
			this.aspect = aspect;
		}

		public String toString() {
			return aspect;
		}
	}
	public GeneOntology(String name, String curator, String description, GraphPerspective dag)
	    throws URISyntaxException, MalformedURLException {
		super(name, curator, description, dag);

		final DBReference reference = Cytoscape.getOntologyServer().getCrossReferences()
		                                       .getDBReference("GOC");
		metaParser.setMetadata(SOURCE, reference.getGenericURL().toString());
	}

	/**
	 *
	 * @return Curator as string
	 */
	public String getCurator() {
		return null;
	}

	public List<Term> getTermsInNamespace(String namespace) {
		return null;
	}

	public GOTerm getGOTerm(String goID) {
		return new GOTerm(goID,
		                  Cytoscape.getNodeAttributes()
		                           .getStringAttribute(goID,
		                                               OBOTags.getPrefix() + "." + NAME.toString()),
		                  name,
		                  goTermAttributes.getStringAttribute(goID,
		                                                      OBOTags.getPrefix() + "."
		                                                      + DEF.toString()));
	}

	/**
	 * Returns Aspect/name space of the GO term.
	 *
	 * @param goID
	 *            ID of the GO term (for example GO:000011)
	 *
	 * @return GOAspect of the given ID
	 */
	public GOAspect getAspect(String goID) {
		final String nameSpace = goTermAttributes.getStringAttribute(goID,
		                                                             OBOTags.getPrefix() + "."
		                                                             + NAMESPACE.toString());

		if (nameSpace == null) {
			return null;
		}

		if (nameSpace.equalsIgnoreCase(GOAspect.BIOLOGICAL_PROCESS.name())) {
			return GOAspect.BIOLOGICAL_PROCESS;
		} else if (nameSpace.equalsIgnoreCase(GOAspect.CELLULAR_COMPONENT.name())) {
			return GOAspect.CELLULAR_COMPONENT;
		} else if (nameSpace.equalsIgnoreCase(GOAspect.MOLECULAR_FUNCTION.name())) {
			return GOAspect.MOLECULAR_FUNCTION;
		}

		return null;
	}
}
