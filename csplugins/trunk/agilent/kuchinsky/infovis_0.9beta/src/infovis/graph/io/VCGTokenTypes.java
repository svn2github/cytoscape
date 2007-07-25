// $ANTLR : "VCG.g" -> "VCGParser.java"$

/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/

package infovis.graph.io;
/**
 * Parser and Lexer for the VCG Graph format.
 *
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.7 $
 */

public interface VCGTokenTypes {
	int EOF = 1;
	int NULL_TREE_LOOKAHEAD = 3;
	int LITERAL_graph = 4;
	int COLON = 5;
	int LCUR = 6;
	int RCUR = 7;
	int ID = 8;
	int LITERAL_title = 9;
	int LITERAL_x = 10;
	int INTEGER = 11;
	int LITERAL_y = 12;
	int LITERAL_loc = 13;
	int LITERAL_classname = 14;
	int LITERAL_infoname = 15;
	int LITERAL_colorentry = 16;
	int LITERAL_no_nearedges = 17;
	int FLOAT = 18;
	// "foldnode." = 19
	// "foldedge." = 20
	// "node." = 21
	// "edge." = 22
	int LITERAL_node = 23;
	int LITERAL_edge = 24;
	int LITERAL_nearedge = 25;
	int LITERAL_bentnearedge = 26;
	int LITERAL_backedge = 27;
	int DIGIT = 28;
	int WS = 29;
	int SL_COMMENT = 30;
	int ML_COMMENT = 31;
	int ESC = 32;
}
