// $ANTLR : "DOT.g" -> "DOTLexer.java"$

/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/

package infovis.graph.io;
/**
 * Parser and Lexer for the DOT Graph format.
 *
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.22 $
 */

public interface DOTTokenTypes {
	int EOF = 1;
	int NULL_TREE_LOOKAHEAD = 3;
	int LITERAL_strict = 4;
	int ATOM = 5;
	int LCUR = 6;
	int RCUR = 7;
	int LITERAL_graph = 8;
	int LITERAL_digraph = 9;
	int LITERAL_node = 10;
	int LITERAL_edge = 11;
	int SEMI = 12;
	int D_EDGE_OP = 13;
	int ND_EDGE_OP = 14;
	int COMMA = 15;
	int COLON = 16;
	int LPAR = 17;
	int RPAR = 18;
	// "@" = 19
	int LBR = 20;
	int RBR = 21;
	int EQUAL = 22;
	int LITERAL_subgraph = 23;
	int BALANCED = 24;
	int WS = 25;
	int ESC = 26;
	int SL_COMMENT = 27;
	int ML_COMMENT = 28;
}
