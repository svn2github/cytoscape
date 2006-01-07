package nct.graph.basic;

import java.util.*;
import java.lang.*;

import nct.graph.SequenceGraph;
import nct.graph.DistanceGraph;

/**
 * An extension of BasicDistanceGraph that implements SequenceGraph and
 * contains a pointer to a Blast database. 
 */
public class BlastGraph<NodeType extends Comparable<NodeType>,WeightType extends Comparable<WeightType>> extends BasicDistanceGraph<NodeType,WeightType>
	implements SequenceGraph<NodeType,WeightType> {

	/**
	 * This string is the name of the database files. In the Blast
	 * world, a single name identifies a "database" which consists of
	 * three files with the same prefix (the name), but with different
	 * suffixes.
	 */
	protected String dbName;

	/**
	 * This string is the location of the database files. 
	 */
	protected String dbLocation;

	/**
	 * @param dbName The name of the database.
	 * @param dbLocation The location of the database.
	 */
	public BlastGraph(String dbName,String dbLocation) {
		super(dbName);
		this.dbName = dbName;
		this.dbLocation = dbLocation;
	}

        /**
         * Returns the name of the Blast database used. In the Blast
	 * world, this name identifies a "database" which consists of
	 * three files with the same prefix (the name), but with different
	 * suffixes.
         * @return The name of the database used.
         */
	public String getDBName() {
		return dbName;
	}

        /**
         * Returns the location of the Blast database used.
         * @return The location of the database used.
         */
	public String getDBLocation() {
		return dbLocation;
	}


        /**
         * Returns an integer identifying the type of database as Blast.
         * @return An integer identifying the type of database as Blast.
         */
	public int getDBType() {
		return SequenceGraph.BLAST;
	}
}
