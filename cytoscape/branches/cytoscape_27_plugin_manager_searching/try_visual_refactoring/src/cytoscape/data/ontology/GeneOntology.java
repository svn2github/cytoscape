package cytoscape.data.ontology;

import static cytoscape.data.readers.MetadataEntries.SOURCE;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.List;

import org.biojava.ontology.Term;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.data.ontology.readers.OBOTags;

/**
 * Gene Ontology object based on general Ontology.<br>
 * 
 * 
 * @author kono
 * 
 */
public class GeneOntology extends Ontology {

	public GeneOntology(String name, String curator, String description,
			CyNetwork dag) throws URISyntaxException, MalformedURLException {
		super(name, curator, description, dag);
		final DBReference reference = Cytoscape.getOntologyServer()
				.getCrossReferences().getDBReference("GOC");
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
		return new GOTerm(goID, Cytoscape.getNodeAttributes()
				.getStringAttribute(goID, OBOTags.NAME.toString()), name,
				Cytoscape.getNodeAttributes().getStringAttribute(goID,
						OBOTags.DEF.toString()));
	}
}
