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
 * @version $Revision: 1.10 $
 */

public interface NewickTokenTypes {
	int EOF = 1;
	int NULL_TREE_LOOKAHEAD = 3;
	int COLON = 4;
	int SEMI = 5;
	int LPAREN = 6;
	int COMMA = 7;
	int RPAREN = 8;
	int LABEL = 9;
	int NUMBER = 10;
	int WS = 11;
	int ESC = 12;
}
