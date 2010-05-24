
//============================================================================
// 
//  file: FastaGraph.java
// 
//  Copyright (c) 2006, University of California San Diego 
// 
//  This program is free software; you can redistribute it and/or modify it 
//  under the terms of the GNU General Public License as published by the 
//  Free Software Foundation; either version 2 of the License, or (at your 
//  option) any later version.
//  
//  This program is distributed in the hope that it will be useful, but 
//  WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
//  or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License 
//  for more details.
//  
//  You should have received a copy of the GNU General Public License along 
//  with this program; if not, write to the Free Software Foundation, Inc., 
//  59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
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
public class FastaGraph<NodeType extends Comparable<? super NodeType>,WeightType extends Comparable<? super WeightType>> extends BasicDistanceGraph<NodeType,WeightType>
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
        public void setDBLocation(String location) {
                fileLocation = location;
        }

        /**
         * Sets the type of the database.
         * @param type The new type of the sequence database.
         */
        public void setDBType(int type) {
                System.err.println("you can't change the database type of a FASTA SequenceGraph");
        }


}
