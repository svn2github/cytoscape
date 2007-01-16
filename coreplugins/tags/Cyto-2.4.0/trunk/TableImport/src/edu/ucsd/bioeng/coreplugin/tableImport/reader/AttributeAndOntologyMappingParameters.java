package edu.ucsd.bioeng.coreplugin.tableImport.reader;

import java.util.List;

import org.biojava.ontology.Ontology;

import cytoscape.Cytoscape;

import edu.ucsd.bioeng.coreplugin.tableImport.reader.TextTableReader.ObjectType;

public class AttributeAndOntologyMappingParameters extends
		AttributeMappingParameters {

	private final int ontologyIndex;
	private final String ontologyName;
	
	public AttributeAndOntologyMappingParameters(ObjectType objectType,
			List<String> delimiters, String listDelimiter, int keyIndex,
			String mappingAttribute, List<Integer> aliasIndex,
			String[] attributeNames, Byte[] attributeTypes,
			Byte[] listAttributeTypes, boolean[] importFlag, int ontologyIndex, final String ontologyName) throws Exception {
		super(objectType, delimiters, listDelimiter, keyIndex,
				mappingAttribute, aliasIndex, attributeNames, attributeTypes,
				listAttributeTypes, importFlag);
		// TODO Auto-generated constructor stub
		this.ontologyName = ontologyName;
		this.ontologyIndex = ontologyIndex;
	}
	
	public int getOntologyIndex() {
		return ontologyIndex;
	}
	
	public Ontology getOntology() {
		final Ontology testOntology;
		if(Cytoscape.getOntologyServer() != null) {
			testOntology = Cytoscape.getOntologyServer()
			.getOntologies().get(ontologyName);
		} else {
			testOntology = null;
		}
		
		return testOntology;
	}

}
