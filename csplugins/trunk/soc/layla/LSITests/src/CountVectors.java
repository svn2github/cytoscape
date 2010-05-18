import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;

///////////////////////////////////////////////////////////////////////////////
//                   
// Main Class File:  CountVectors
// File:             
// Semester:         Summer 2010 GSoC
//
// Author:           Layla Oesper layla.oesper@gmail.com
//
// Credits:          None
//////////////////////////// 80 columns wide //////////////////////////////////

/**
 * This class reads in a file and creates count vectors for each new document.
 */
public class CountVectors {
	
	//Variables
	ArrayList vocab;
	ArrayList docCounts;
	
	//Constructor
	/**
	 * Creates an empty CountVector
	 */
	public CountVectors()
	{
		vocab = new ArrayList();
		docCounts = new ArrayList();
	}

	/**
	 * @param name of file to be opened
	 * @param delimeter used to delineate a new document
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		//RETRIEVE PARAMETERS NAME DELIM

		//Must have 2 command line arguments
		if (args.length != 2)
		{
			System.out.println("Invalid command-line arguments.");
			System.exit(1);
		}
		
		String name = args[0];
		String delim = args[1];
		
		CountVectors myDoc = new CountVectors();
		File aFile = openFile(name);
		myDoc.readFile(aFile, delim);
		myDoc.printData();
		//myDoc.printOrigData();
		myDoc.printVocab();

	}
	
	/**
	 * Opens the specified file
	 * @param name of file to open
	 * @return the File object
	 */
	public static File openFile(String name)
	{
		File in = new File(name);
		
		//Ensure that the file exists and can be read
		if (!in.exists() || !in.canRead())
		{
			System.out.println("Problem with input file: " + name);
			System.exit(1);
		}
		return in;
	}
	
	/**
	 * Reads data from the specified file and creates a count matrix.
	 * Each entry in the returned ArrayList is a document.  Each document
	 * is represented by a list of numbers where each number lists the 
	 * number of the word in the vocabulary that appears in the document.
	 * @param aFile the file to read from
	 * @param delim the delimiter to use for new documents.
	 */
	public void readFile(File aFile, String delim)
	{
		//ArrayList vocab = new ArrayList();
		//ArrayList documents = new ArrayList();
		
		try
		{
			Scanner docScanner = new Scanner(aFile);
			docScanner.useDelimiter(delim);
			while (docScanner.hasNext())
			{
				ArrayList wordIndices = new ArrayList();
				String nextLine = docScanner.next();
		
				//Search through words
				StringTokenizer token = new StringTokenizer(nextLine," :\n\r\f\t", false);
				while (token.hasMoreTokens())
				{
					String curWord = token.nextToken();
					curWord = curWord.toLowerCase();
					
					//System.out.println(curWord);
					
					if (!vocab.contains(curWord))
					{
						vocab.add(curWord);
					}
					
					//Retrieve and store vocabulary index
					Integer index = vocab.indexOf(curWord);
					wordIndices.add(index);
				}	
				
				docCounts.add(wordIndices);
			}// end Scanner while
			docScanner.close();
		}//end try
		
		catch (FileNotFoundException e)
		{
			System.out.println("An error occurred while scanning the file.");
			System.out.println(e.toString());
			System.exit(1);
		}
	}
	
	/**
	 * Prints out the the document word info in unexpaneded form;
	 */
	public void printOrigData()
	{
		int numDocs = docCounts.size();
		
		if (numDocs == 0)
			return;
		
		for (int i=0; i < numDocs; i++)
		{
			ArrayList document = (ArrayList)docCounts.get(i);
			
			//Word indexes
			for (int j=0; j < document.size(); j++)
			{
				int val = (Integer)document.get(j);
				
				System.out.print(val + " ");
			}
			System.out.println();
		}
		
	}

	/**
	 * Prints out a Matlab usable matrix from the data from the CountVectors.
	 * @throws IOException 
	 */
	public void printData() throws IOException
	{
		PrintWriter out = new PrintWriter(new FileWriter("C:\\Users\\Layla\\Desktop\\test_output.txt"));
		
		int numDocs = docCounts.size();
		int vocabSize = vocab.size();
		
		if (numDocs == 0 || vocabSize == 0)
			return;
		
		int[][] matrix = new int[vocabSize][numDocs];
		
		//Documents
		for (int j=0; j < numDocs; j++)
		{
			ArrayList document = (ArrayList)docCounts.get(j);
			
			//Word indexes
			for (int i=0; i < document.size(); i++)
			{
				int val = (Integer)document.get(i);
				
				matrix[val][j] = matrix[val][j] + 1;
			}
		}
		
		//Print Matrix
		
		for (int i=0; i < vocabSize; i++)
		{
			for (int j=0; j < numDocs; j++)
			{
				out.print(matrix[i][j] + " ");
			}
			out.println();
		}
		
		out.close();
	}
	
	/**
	 * Prints out the Vocab List to file
	 * @throws IOException 
	 */
	public void printVocab() throws IOException
	{
		PrintWriter out = new PrintWriter(new FileWriter("C:\\Users\\Layla\\Desktop\\vocab_output.txt"));
		
		for (int i = 0; i< vocab.size(); i++)
			out.println((String)vocab.get(i));
		
		out.close();
	}
	
	
	/**
	 * Retrieve the vocabulary list
	 * @return ArrayList of vocabulary.
	 */
	public ArrayList getVocab()
	{
		return vocab;
	}
	
	/**
	 * Retrieve the document counts.
	 * @return ArrayList of ArrayLists of counts.
	 */
	public ArrayList getDocCounts()
	{
		return docCounts;
	}
	
	/**
	 * Returns the word specified by the given index
	 * @param index
	 * @return
	 */
	public String getWord(int index)
	{
		return (String)vocab.get(index);
	}

}
