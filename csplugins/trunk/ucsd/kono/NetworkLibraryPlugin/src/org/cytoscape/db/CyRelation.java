package org.cytoscape.db;

import org.neo4j.graphdb.RelationshipType;

public enum CyRelation implements RelationshipType {
	INTERACTION, GRAPHS, GRAPH, MEMBER_OF;
}
