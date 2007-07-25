// $ANTLR : "Newick.g" -> "NewickParser.java"$

/*****************************************************************************
 * Copyright (C) 2003 Jean-Daniel Fekete and INRIA, France                   *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/

package infovis.tree.io;
/**
 * Parser and Lexer for the Newick Tree format.
 *
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.12 $
 */

import antlr.TokenBuffer;
import antlr.TokenStreamException;
import antlr.TokenStreamIOException;
import antlr.ANTLRException;
import antlr.LLkParser;
import antlr.Token;
import antlr.TokenStream;
import antlr.RecognitionException;
import antlr.NoViableAltException;
import antlr.MismatchedTokenException;
import antlr.SemanticException;
import antlr.ParserSharedInputState;
import antlr.collections.impl.BitSet;

import cern.colt.list.IntArrayList;

import infovis.Tree;
import infovis.column.StringColumn;
import infovis.column.FloatColumn;

/**
 * Parser for the Newick Tree format.
 *
 * @author Jean-Daniel Fekete
 */

public class NewickParser extends antlr.LLkParser       implements NewickTokenTypes
 {

	Tree tree;
	IntArrayList stack = new IntArrayList();
	StringColumn nameColumn;
	FloatColumn lengthColumn;
	String name;
	int serial;
	
	int current() { return stack.get(stack.size()-1); }
	void push() { 
		stack.add(tree.addNode(current()));
	}
	
	void pop() {
		if (nameColumn.isValueUndefined(current())) {
			addName("?"+getName()+(serial++)+"?");
		}
		stack.remove(stack.size()-1);
	}
	
	void addName(String name) {
		if (nameColumn == null)
			nameColumn = StringColumn.findColumn(tree, "name");
		nameColumn.setExtend(current(), name);
	}
	
	void addLength(String length) {
		if (lengthColumn == null)
			lengthColumn = FloatColumn.findColumn(tree, "length");
		lengthColumn.setValueOrNullAt(current(), length);
	}
	
	String getName() { return name; }
	void setName(String name) { this.name = name; }
	
	Tree getTree() { return tree; }
	void setTree(Tree t) { tree = t; }

protected NewickParser(TokenBuffer tokenBuf, int k) {
  super(tokenBuf,k);
  tokenNames = _tokenNames;
}

public NewickParser(TokenBuffer tokenBuf) {
  this(tokenBuf,2);
}

protected NewickParser(TokenStream lexer, int k) {
  super(lexer,k);
  tokenNames = _tokenNames;
}

public NewickParser(TokenStream lexer) {
  this(lexer,2);
}

public NewickParser(ParserSharedInputState state) {
  super(state,2);
  tokenNames = _tokenNames;
}

	public final void tree() throws RecognitionException, TokenStreamException {
		
		String name, length; stack.add(Tree.ROOT);
		
		try {      // for error handling
			descendant_list();
			{
			switch ( LA(1)) {
			case LABEL:
			{
				name=label();
				addName(name);
				break;
			}
			case COLON:
			case SEMI:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			{
			switch ( LA(1)) {
			case COLON:
			{
				match(COLON);
				length=branch_length();
				addLength(length);
				break;
			}
			case SEMI:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			match(SEMI);
			pop();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_0);
		}
	}
	
	public final void descendant_list() throws RecognitionException, TokenStreamException {
		
		
		try {      // for error handling
			match(LPAREN);
			subtree();
			{
			_loop680:
			do {
				if ((LA(1)==COMMA)) {
					match(COMMA);
					subtree();
				}
				else {
					break _loop680;
				}
				
			} while (true);
			}
			match(RPAREN);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_1);
		}
	}
	
	public final String  label() throws RecognitionException, TokenStreamException {
		 String ret = null;
		
		Token  l = null;
		
		try {      // for error handling
			l = LT(1);
			match(LABEL);
			ret = l.getText();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_2);
		}
		return ret;
	}
	
	public final String  branch_length() throws RecognitionException, TokenStreamException {
		 String ret = null;
		
		Token  n = null;
		
		try {      // for error handling
			n = LT(1);
			match(NUMBER);
			ret = n.getText();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_3);
		}
		return ret;
	}
	
	public final void subtree() throws RecognitionException, TokenStreamException {
		
		String name, length; push();
		
		try {      // for error handling
			switch ( LA(1)) {
			case LPAREN:
			{
				descendant_list();
				{
				switch ( LA(1)) {
				case LABEL:
				{
					name=label();
					addName(name);
					break;
				}
				case COLON:
				case COMMA:
				case RPAREN:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				{
				switch ( LA(1)) {
				case COLON:
				{
					match(COLON);
					length=branch_length();
					addLength(length);
					break;
				}
				case COMMA:
				case RPAREN:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				pop();
				break;
			}
			case LABEL:
			{
				name=label();
				addName(name);
				{
				switch ( LA(1)) {
				case COLON:
				{
					match(COLON);
					length=branch_length();
					addLength(length);
					break;
				}
				case COMMA:
				case RPAREN:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				pop();
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_4);
		}
	}
	
	
	public static final String[] _tokenNames = {
		"<0>",
		"EOF",
		"<2>",
		"NULL_TREE_LOOKAHEAD",
		"COLON",
		"SEMI",
		"LPAREN",
		"COMMA",
		"RPAREN",
		"LABEL",
		"NUMBER",
		"WS",
		"ESC"
	};
	
	private static final long[] mk_tokenSet_0() {
		long[] data = { 2L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_0 = new BitSet(mk_tokenSet_0());
	private static final long[] mk_tokenSet_1() {
		long[] data = { 944L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_1 = new BitSet(mk_tokenSet_1());
	private static final long[] mk_tokenSet_2() {
		long[] data = { 432L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_2 = new BitSet(mk_tokenSet_2());
	private static final long[] mk_tokenSet_3() {
		long[] data = { 416L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_3 = new BitSet(mk_tokenSet_3());
	private static final long[] mk_tokenSet_4() {
		long[] data = { 384L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_4 = new BitSet(mk_tokenSet_4());
	
	}
