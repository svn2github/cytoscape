
/*
 File: WordFilter.java

 Copyright (c) 2010, The Cytoscape Consortium (www.cytoscape.org)

 This library is free software; you can redistribute it and/or modify it
 under the terms of the GNU Lesser General Public License as published
 by the Free Software Foundation; either version 2.1 of the License, or
 any later version.

 This library is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 documentation provided hereunder is on an "as is" basis, and the
 Institute for Systems Biology and the Whitehead Institute
 have no obligations to provide maintenance, support,
 updates, enhancements or modifications.  In no event shall the
 Institute for Systems Biology and the Whitehead Institute
 be liable to any party for direct, indirect, special,
 incidental or consequential damages, including lost profits, arising
 out of the use of this software and its documentation, even if the
 Institute for Systems Biology and the Whitehead Institute
 have been advised of the possibility of such damage.  See
 the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation,
 Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 */

package cytoscape.csplugins.semanticsummary;
import cytoscape.data.readers.TextFileReader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Iterator;


/**
 * This class defines the WordFilter class.  This class is used to determine
 * if a word in question should be filtered out.  The list of words that will
 * be filtered is built from included .txt files.
 * 
 * @author Layla Oesper
 * @version 1.0
 */


public class WordFilter 
{
	
	//VARIABLES
	
	private HashSet<String> words = new HashSet<String>(); //Filtered words
	final static public String stopWordFile = "StopWords.txt";
	final static public String flaggedWordFile = "FlaggedWords.txt";
	final static public String separator = "/";
	final static public String resources = "resources";
	final static public String newline = "\n";
	
	
	//CONSTRUCTORS
	
	/**
	 * WordFilter constructor
	 * @throws FileNotFoundException 
	 */
	public WordFilter()
	{
		//Initialize from files
		String stopPath = resources + separator + stopWordFile;
		String flaggedPath = resources + separator + flaggedWordFile;
		
		this.initialize(stopPath);
		this.initialize(flaggedPath);
	}
	
	
	//METHODS
	
	/**
	 * Checks to see if the word should be filtered
	 * 
	 * @param aWord - word to be checked
	 * @return boolean - true if word should be filtered out
	 */
	public boolean contains(String aWord)
	{
		if (words.contains(aWord))
			return true;
		else
			return false;
	}
	
	/**
	 * Adds a word to the list of words to filter.
	 * 
	 * @param aWord - word to be added to filter
	 */
	public void add(String aWord)
	{
		words.add(aWord);
	}
	
	/**
	 * Removes a word from the list of words to filter.
	 * 
	 * @param aWord - word to be removed from the filter.
	 */
	public void remove(String aWord)
	{
		if (this.contains(aWord))
			words.remove(aWord);
	}
	
	/**
	 * Initializes the WordFilter to contain words from the specified
	 * resource file.
	 * 
	 * @param resourcePath - location, relative to this class of the .txt file
	 * containing list of words to add to this filter.
	 */
	private void initialize(String resourcePath)
	{
		URL myURL = SemanticSummaryPlugin.class.getResource(resourcePath);
		String path = myURL.getPath();
		path = path.replaceAll("%20", " "); //fix spaces
		
		//Read file and retrieve all lines
		TextFileReader reader = new TextFileReader(path);
		reader.read();
		String fullText = reader.getText();
		
		String[] lines = fullText.split("\n");
		
		//Each line should be a word, add to filter
		for (int i = 0; i < lines.length; i++)
		{
			String curWord = lines[i];
			this.add(curWord);
		}//end for loop
	}
	
	/**
	 * Creates a String representation of all words currently in this 
	 * WordFilter.
	 * 
	 * @return String - list of words in this WordFilter.
	 */
	public String toString()
	{
		String listOfWords = "";
		
		//Iterate through to retrieve words
		Iterator<String> iter = words.iterator();
		while (iter.hasNext())
		{
			String curWord = iter.next();
			listOfWords = listOfWords + curWord + newline;
		}
		
		return listOfWords;
	}
}
