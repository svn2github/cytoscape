package org.cytoscape.db;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;

public class StatusChecker {

	private GraphDatabaseService graphDb;

	public StatusChecker() {

		this.graphDb = NetworkLibraryPlugin.getDB();
		System.out.println("##################### Checker start");

		final Transaction tx = graphDb.beginTx();
		final Iterable<Relationship> rel = graphDb.getReferenceNode()
				.getRelationships(CyRelation.GRAPHS);

		Iterable<Node> nodes = graphDb.getAllNodes();

		for (Node n : nodes) {
			try {
				if (n.getProperty("id") != null) {
					System.out.println("N ==== " + n.getId() + " ===> " + n.getProperty("id"));
				} else {
					System.out.println("N ==== " + n.getId() + n.hasRelationship(Direction.OUTGOING));
				}
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
		}

		for (Relationship r : rel) {
			System.out.println("Current ==== " + r.getProperty("id"));
		}
		tx.success();
		tx.finish();

	}

}
