/* BEGIN_HEADER                                              Java TreeView
 *
 * $Author: alokito $
 * $RCSfile: FlatFileStreamTokenizer.java,v $
 * $Revision: 1.3 $
 * $Date: 2004/12/21 03:28:12 $
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
/**
 *  implements the "lexical structure" of flat files basically, calling
 *  nextToken returns a series of words, nulls and newlines, and finally an EOF.
 *  Note that numbers are not parsed by the tokenizer. Also, there is no enforcement
 * of the correct number of tokens per line.
 *
 * it will, however, filter out blank lines.
 * 
 * @author     Alok Saldanha <alok@genome.stanford.edu>
 * @version $Revision: 1.3 $ $Date: 2004/12/21 03:28:12 $
 */
public class FlatFileStreamTokenizer extends StreamTokenizer {

	private int lines;
	private char sep;
	private boolean lastSep            = false;
	private boolean pushedEOL          = false;
	/**
	 *  Constant signifying a null token
	 */
	public final static int TT_NULL    = -5;
	/**
	 *  Constant used for debugging
	 */
	public static boolean printTokens  = false;
	private int saved;
	
	public static void main(String astring[])
	{
	  try {
		System.out.println("analysizing " + astring[0]);
	  	BufferedReader br = new BufferedReader(new FileReader(astring[0]));
	  	FlatFileStreamTokenizer st = new FlatFileStreamTokenizer(br);
		st.printToken();
		while (st.nextToken() != FlatFileStreamTokenizer.TT_EOF) {
		  st.printToken();
		}
		st.printToken();
	  } catch (Exception e) {
		System.out.println("Got exception: "+e);
	  }
	 }

	/**
	 *  Constructor for the FlatFileStreamTokenizer object
	 *
	 * @param  reader  Reader of file to tokenize
	 * @param  ch      Separator character to split cols
	 */
	public FlatFileStreamTokenizer(Reader reader, char ch) {
		super(reader);
		resetSyntax();
		setSeparator(ch);
		lines = 1;
		// start at line 1.
	}


	/**
	 *  Constructor for the FlatFileStreamTokenizer object. Defaults to tab-delimitted.
	 *
	 * @param  reader  Reader of file to tokenize
	 */
	public FlatFileStreamTokenizer(Reader reader) {
		this(reader, '\t');
	}


	/**
	 *  Sets the separator attribute of the FlatFileStreamTokenizer object
	 *
	 * @param  ch  The new separator value
	 */
	public void setSeparator(char ch) {
		sep = ch;
		wordChars(0, 3000);
		// I really want all chars to be words here...
		/*
		 *  ordinaryChar('\n');         // required, to recognize eols.
		 *  ordinaryChar('\r');         // eol on mac, will this work?
		 */
		whitespaceChars('\r', '\r');
		//but, really should be word..
		whitespaceChars('\n', '\n');

		ordinaryChar(ch);
		// \t separates tokens, but also special null token..
		eolIsSignificant(true);
		// eols matter...
	}


	/**
	 * @return    String representation of current token
	 */
	public String toString() {
	String msg;
		switch (ttype) {
						case FlatFileStreamTokenizer.TT_WORD:
							msg = "Word: " + sval;
							break;
						case FlatFileStreamTokenizer.TT_NUMBER:
							msg = "Number: " + nval;
							break;
						case FlatFileStreamTokenizer.TT_EOL:
							msg = "EOL:";
							break;
						case FlatFileStreamTokenizer.TT_NULL:
							msg = "NULL:";
							break;
						default:
							msg = "INVALID TOKEN, ttype=" + ttype;
							break;
		}
		return msg;
	}


	/**
	 *  prints current token to System.out
	 */
	public void printToken() {
		System.out.println(toString());
	}


	/**
	 *  Returns next token. 
	 *  Multiple separators generate null tokens. So do ones at ends of lines.
	 *
	 * @return                  token type of next token
	 * @exception  IOException  Thrown by the reader, of course
	 */
	public int nextToken() throws IOException {
		if (printTokens) {
			printToken();
		}

		int lastType = ttype;
		super.nextToken();

		if (lastType == TT_EOL) { // skip consecutive blanks
			while (ttype == TT_EOL) {
				super.nextToken();
			}
		}

		// special handling of separator character
		if (ttype == sep) {
			if (lastSep) { // construct null on consecuitive seps
				ttype = TT_NULL;
				return ttype;
			} else { // skip over initial sep, but set flag
				lastSep = true;
				return nextToken();
			}
		}

		if (lastSep) { // we're after a sep...
			if ((ttype == TT_EOL) || (ttype == TT_EOF)) { // need to construct a null.
				super.pushBack();
				// hack, we need to create a null even though
				//pushBack() doesn't actually push us back a char...
				saved = ttype;
				pushedEOL = true;
				ttype = TT_NULL;
				return ttype;
			}
		}

		lastSep = false;

		if (pushedEOL) {
			// restore pushedback state
			pushedEOL = false;
			ttype = saved;
		}

		// okay because only nulls are returned early...
		if (ttype == TT_EOL) {
			lines++;
		}

		// maybe add special processing for words...
		return ttype;
	}


	/**
	 *  Pushes back current token to be read again.
	 */
	public void pushBack() {
		if (ttype == TT_EOL) {
			//	    System.out.println("pushback TT_EOL");
			lines--;
		}
		super.pushBack();
	}


	/**
	 * @return    lines read so far
	 */
	public int lineno() {
		return lines;
	}


	/**
	 * @return    lines read so far
	 */
	public int lines() {
		return lines;
	}
}

