
//============================================================================
// 
//  file: FastaProteinParser.java
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



package nct.parsers;

import java.util.*;
import java.util.logging.Logger;
import java.io.*;

import org.biojava.bio.*;
import org.biojava.bio.seq.*;
import org.biojava.bio.seq.db.*;
import org.biojava.bio.seq.io.*;
import org.biojava.bio.symbol.*;


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
 	public static SequenceIterator parseFile(String FASTAFileName) 
		throws BioException, NoSuchElementException, FileNotFoundException { 
		return readerParse(new FileReader(FASTAFileName));
	}

	/**
 	 * A simple method that parses a multiple FASTA file and returns 
	 * a BioJava SequenceIterator.
	 * @param sequenceString A string containing fasta sequence data. 
	 * @return A BioJava SequenceIterator. 
	 */
 	public static SequenceIterator parseString(String sequenceString) 
		throws BioException, NoSuchElementException, FileNotFoundException { 
		return readerParse(new StringReader(sequenceString));
	}

	/**
	 * A helper method to wrap common code.
	 */
	private static SequenceIterator readerParse(Reader r) 
		throws BioException, NoSuchElementException, FileNotFoundException { 
		BufferedReader is = new BufferedReader(r);
		SequenceIterator it = SeqIOTools.readFastaProtein(is); 
		return it;
	}
}
