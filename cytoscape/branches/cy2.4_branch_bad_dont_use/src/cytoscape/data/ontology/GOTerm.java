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
		if (termName != null) {
			Cytoscape.getNodeAttributes().setAttribute(id,
					OBOTags.NAME.toString(), termName);
		}
	}

	public String getNameSpace() {
		return null;
	}

	public Map getCrossReferences() {
		return null;
	}

	public String getFullName() {
		return Cytoscape.getNodeAttributes().getStringAttribute(
				super.getName(), OBOTags.NAME.toString());
	}

	public String getDescription() {
		return Cytoscape.getNodeAttributes().getStringAttribute(
				super.getName(), OBOTags.DEF.toString());
	}

	public String getType() {
		return Cytoscape.getNodeAttributes().getStringAttribute(
				super.getName(), OBOTags.NAMESPACE.toString());
	}

}
