package cytoscape.data.ontology;

import java.util.Map;

import cytoscape.Cytoscape;
import cytoscape.data.ontology.readers.OBOTags;

/**
 * A Gene Ontology term. This class is an extended version of normal ontology.
 * 
 * @author kono
 * 
 */
public class GOTerm extends OntologyTerm {

	public GOTerm(String id, String termName, String ontologyName,
			String description) {
		super(id, ontologyName, description);
		Cytoscape.getNodeAttributes().setAttribute(id, OBOTags.NAME.toString(), termName);
	}

	public String getNameSpace() {
		return null;
	}

	public Map getCrossReferences() {
		return null;
	}

	public String getFullName(String goID) {
		return Cytoscape.getNodeAttributes().getStringAttribute(goID,
				OBOTags.NAME.toString());
	}
	
	public String getDescription(String goID) {
		return Cytoscape.getNodeAttributes().getStringAttribute(goID,
				OBOTags.DEF.toString());
	}
	
	public String getType(String goID) {
		return Cytoscape.getNodeAttributes().getStringAttribute(goID,
				OBOTags.NAMESPACE.toString());
	}

}
