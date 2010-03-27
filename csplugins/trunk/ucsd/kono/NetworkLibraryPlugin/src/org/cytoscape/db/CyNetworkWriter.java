package org.cytoscape.db;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.List;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;

import cytoscape.CyEdge;
import cytoscape.CyNetwork;

public class CyNetworkWriter {

	private final Node rootGraphNode;
	private final GraphDatabaseService graphDb;

	public CyNetworkWriter() {
		graphDb = NetworkLibraryPlugin.getDB();

		
		final Transaction tx = graphDb.beginTx();
		final Node ref = graphDb.getReferenceNode();
		Relationship rel = graphDb.getReferenceNode().getSingleRelationship(
				CyRelation.GRAPHS, Direction.OUTGOING);

		if (rel == null) {
			rootGraphNode = graphDb.createNode();
			rootGraphNode.setProperty("id", "Root Graph");
			graphDb.getReferenceNode().createRelationshipTo(rootGraphNode,
					CyRelation.GRAPHS);

		} else {
			rootGraphNode = rel.getEndNode();
		}

		tx.success();
		tx.finish();
	}

	public void write(final CyNetwork network) {

		MemoryMXBean mbean = ManagementFactory.getMemoryMXBean();
		MemoryUsage heapUsage = mbean.getHeapMemoryUsage();

		long init = heapUsage.getInit();
		long used = heapUsage.getUsed();
		long committed = heapUsage.getCommitted();
		long max = heapUsage.getMax();

		System.out.println("Neo 4J Test: init,used,comitted, max = " + init
				+ ", " + used + ", " + committed + ", " + max);
		
		Transaction tx = graphDb.beginTx();
		try {
			
			final Node networkNode = graphDb.createNode();
			networkNode.setProperty("id", network.getTitle());
			
			rootGraphNode.createRelationshipTo( networkNode,
	            CyRelation.GRAPH );
			
			final List<CyEdge> list = network.edgesList();
			for (CyEdge edge : list) {
				final Node source = graphDb.createNode();
				final Node target = graphDb.createNode();
				final Relationship relationship = source.createRelationshipTo(target,
						CyRelation.INTERACTION);
				final Relationship relationship1 = source.createRelationshipTo(networkNode,
						CyRelation.MEMBER_OF);
				final Relationship relationship2 = target.createRelationshipTo(networkNode,
						CyRelation.MEMBER_OF);

				source.setProperty("id", edge.getSource().getIdentifier());
				target.setProperty("id", edge.getTarget().getIdentifier());
				relationship.setProperty("id", edge.getIdentifier());
				

				System.out.println(relationship.getProperty("id"));
			}
			
			tx.success();
		} finally {
			init = heapUsage.getInit();
			used = heapUsage.getUsed();
			committed = heapUsage.getCommitted();
			max = heapUsage.getMax();
			System.out.println("Neo 4J Test END: init,used,comitted, max = "
					+ init + ", " + used + ", " + committed + ", " + max);
			tx.finish();
			graphDb.shutdown();
		}

	}

	private void map() {

	}

}
