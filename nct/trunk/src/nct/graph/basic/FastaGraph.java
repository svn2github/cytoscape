
//============================================================================
// 
//  file: FastaGraph.java 
// 
//  Copyright (c) 2006, University of California, San Diego
//  All rights reverved.
// 
//============================================================================

package nct.graph.basic;

import java.util.*;
import java.lang.*;
import java.io.*;

import nct.graph.SequenceGraph;
import nct.graph.DistanceGraph;

import org.biojava.bio.seq.io.SeqIOTools;
import org.biojava.bio.seq.db.SequenceDB;

/**
 * An extension of BasicDistanceGraph that implements the SequenceGraph 
 * interface and contains a (pointer to a) fasta database. 
 */
public class FastaGraph<NodeType extends Comparable<NodeType>,WeightType extends Comparable<WeightType>> extends BasicDistanceGraph<NodeType,WeightType>
	implements SequenceGraph<NodeType,WeightType> {

	/**
	 * The name of the multiple fasta file
	 */
	protected String fileName;

	/**
	 * The location of the multiple fasta file
	 */
	protected String fileLocation;

	/**
	 * @param fileName The name of the mulitple Fasta file.
	 * @param fileLocation The location of the mulitple Fasta file.
	 */
	public FastaGraph(String fileName,String fileLocation) {
		super();
		this.fileName = fileName;
		this.fileLocation = fileLocation;
	}

	/**
	 * @param sdb A BioJava SequenceDB object that contains the sequences in question.
	 * @param destFileName The name of the mulitple Fasta file where the sequences 
	 * should be written.
	 * @param destFileLocation The location of the mulitple Fasta file.
	 */
	public FastaGraph(SequenceDB sdb, String destFileName, String destFileLocation) {
		super();

		try {
		FileOutputStream fos = new FileOutputStream(destFileLocation + "/" + destFileName);
		SeqIOTools.writeFasta(fos, sdb);
		fos.close();
		} catch (Exception e) { e.printStackTrace(); }

		fileName = destFileName;
		fileLocation = destFileLocation;
	}

        /**
         * Returns the name of the database used.
         * @return The name of the database used.
         */
	public String getDBName() {
		return fileName;
	}

        /**
         * Returns the location of the database used.
         * @return The location of the database used.
         */
	public String getDBLocation() {
		return fileLocation;
	}

        /**
         * Returns an integer identifying the type of database as FASTA.
         * @return An integer identifying the type of databas as FASTA.
         */
	public int getDBType() {
		return SequenceGraph.FASTA;
	}


        /**
         * Sets the name of the database.
         * @param name The new name of the sequence database.
         */
        public void setDBName(String name) {
                fileName = name;
        }

        /**
         * Sets the location of the database.
         * @param location The new location of the sequence database.
         */
        public void setDBLocation(String loc) {
                fileLocation = loc;
        }

        /**
         * Sets the type of the database.
         * @param type The new type of the sequence database.
         */
        public void setDBType(int type) {
                System.err.println("you can't change the database type of a FASTA SequenceGraph");
        }


}
