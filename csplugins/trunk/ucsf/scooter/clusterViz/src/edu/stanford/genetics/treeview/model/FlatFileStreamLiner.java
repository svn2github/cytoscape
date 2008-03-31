/* BEGIN_HEADER                                              Java TreeView
*
* $Author: alokito $
* $RCSfile: FlatFileStreamLiner.java,v $
* $Revision: 1.7 $
* $Date: 2005/03/12 22:32:08 $
* $Name:  $
*
* This file is part of Java TreeView
* Copyright (C) 2001-2003 Alok Saldanha, All Rights Reserved. Modified by Alex Segal 2004/08/13. Modifications Copyright (C) Lawrence Berkeley Lab.
*
* This software is provided under the GNU GPL Version 2. In particular,
*
* 1) If you modify a source file, make a comment in it containing your name and the date.
* 2) If you distribute a modified version, you must do it under the GPL 2.
* 3) Developers are encouraged but not required to notify the Java TreeView maintainers at alok@genome.stanford.edu when they make a useful addition. It would be nice if significant contributions could be merged into the main distribution.
*
* A full copy of the license can be found in gpl.txt or online at
* http://www.gnu.org/licenses/gpl.txt
*
* END_HEADER
*/
package edu.stanford.genetics.treeview.model;

import java.io.*;
import java.util.Vector;


/**
 *  implements the "lexical structure" of flat files basically, calling
 *  nextToken returns a series of words, nulls and newlines, and finally an EOF.
 *  Note that numbers are not parsed by the tokenizer. Also, there is no enforcement
 * of the correct number of tokens per line.
 *
 * it will, however, filter out blank lines.
 * 
 * @author     Alok Saldanha <alok@genome.stanford.edu>
 * @version $Revision: 1.7 $ $Date: 2005/03/12 22:32:08 $
 */
public class FlatFileStreamLiner{

	private char sep;
	/**
	 * parse quoted strings?
	 */
	private boolean pqs = true;
	private StreamTokenizer st;
	private Vector line;

	/**
	 *  Constructor for the FlatFileStreamTokenizer object
	 *
	 * @param  reader  Reader of file to tokenize
	 * @param  ch      Separator character to split cols
	 */
	public FlatFileStreamLiner(Reader reader, char ch, boolean parseQuotedStrings) {
		sep = ch;
		pqs = parseQuotedStrings;
		st = new StreamTokenizer(reader);
		resetSyntax();
		line = new Vector();
	   }
	public void resetSyntax() {
		st.resetSyntax();
		st.wordChars(0, 3000);
		st.whitespaceChars('\r', '\r');
		st.whitespaceChars('\n', '\n');
		st.ordinaryChar(sep);
		if (pqs) {
			//make sure to add these chars to the nextLine() switch statement
			st.quoteChar('"');
		}
		st.eolIsSignificant(true);
		//st.parseNumbers(); do not uncomment.
	}

	public static void main(String astring[])
	{
		System.out.println("analysizing " + astring[0]);
	  	BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(astring[0]));
			FlatFileStreamLiner st = new FlatFileStreamLiner(br, '\t', true);
			while(st.nextLine()) {
				String [] tok = st.getLineTokens();
				for (int i = 0; i < tok.length; i++) {
					System.out.print(tok[i]);
					System.out.print(":");
				}
				System.out.print("\n");
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	 }
	
	public String[] getLineTokens(){
		int len = line.size();
		String[] string = new String[len];
		for (int i = 0; i < len; i++){
			string[i] = (String)line.get(i);
		}
		return string;
	}
	
	public boolean nextLine() throws IOException {
		line.removeAllElements();
		boolean lastsep = true; //in case first token is sep

		while(st.nextToken() != StreamTokenizer.TT_EOF){
			switch (st.ttype){
			case StreamTokenizer.TT_EOL:
				// line ends with tab char (indicating last value null)
				if (lastsep) line.add(null);
				return true;
			case StreamTokenizer.TT_NUMBER:
				System.out.println("parsed number");
				line.add("" + st.nval);
				lastsep = false;
				break;
			case '"':
				if (lastsep == false) {
					// account for stupid excel embedded quotes
					line.setElementAt(line.lastElement() + st.sval, line.size()-1);
					break;
				}
				// otherwise, fall through to new word.
			case StreamTokenizer.TT_WORD:
				line.add(st.sval);
				lastsep = false;
				break;
			default:
				// case statements must be constants, so can't use sep.
				if (st.ttype == sep) {
					if (lastsep){ //already one sep
						line.add(null);
					}else{ //normal sep, after real token
						lastsep = true;
					}
				}
				break;
			}
		}
		
		// indicates that last line lacks EOL token
		if (line.size() == 0) 
			return false;
		else
			return true;
	}
}

