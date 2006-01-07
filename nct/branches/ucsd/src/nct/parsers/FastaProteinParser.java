package nct.parsers;

import java.util.*;
import java.util.logging.Logger;
import java.io.*;

import org.biojava.bio.*;
import org.biojava.bio.seq.*;
import org.biojava.bio.seq.db.*;
import org.biojava.bio.seq.io.*;
import org.biojava.bio.symbol.*;

import nct.graph.Graph;

/**
 * A simple class that provides a method to parse a multiple FASTA file. 
 */
public class FastaProteinParser { 

	/**
 	 * A simple method that parses a multiple FASTA file and returns 
	 * a BioJava SequenceIterator.
	 * @param FASTAFileName The name of the multiple Fasta file to parse.
	 * @return A BioJava SequenceIterator. 
	 */
 	public static SequenceIterator parse(String FASTAFileName) 
		throws BioException, NoSuchElementException, FileNotFoundException { 
		
		BufferedReader is = new BufferedReader(new FileReader(FASTAFileName));
		
		//get a SequenceDB of all sequences in the file
		SequenceIterator it = SeqIOTools.readFastaProtein(is); 

		return it;

	}
}
