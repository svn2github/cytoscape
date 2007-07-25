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

import infovis.Graph;
import infovis.Table;
import infovis.Column;
import infovis.io.AbstractReader;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.ArrayList;
import infovis.column.StringColumn;
import infovis.utils.RowIterator;

/**
 * Parser for the VCG Graph format.
 *
 * @author Jean-Daniel Fekete
 */

public class VCGParser extends antlr.LLkParser       implements VCGTokenTypes
 {

	Graph graph;
	AbstractGraphReader graphReader;
	HashMap nodeMap = new HashMap();
	StringColumn id;
	boolean updating;
	Rectangle2D.Float bbox = new Rectangle2D.Float();
	int currentNode;
	int currentEdge;
	int sourceNode;
	int targetNode;

	public Graph getGraph() {
		return graph;
	}
	
	protected void setGraph(Graph graph) {
		this.graph = graph;
		id= StringColumn.findColumn(graph.getVertexTable(), "id");
		if (! id.isEmpty()) {
			for (RowIterator iter = id.iterator(); iter.hasNext(); ) {
				int vertex = iter.nextRow();
				nodeMap.put(id.get(vertex), new Integer(vertex));
			}
		}
	}
	
	public AbstractGraphReader getGraphReader() {
		return graphReader;
	}
	
	public void setGraphReader(AbstractGraphReader reader) {
		graphReader = reader;
		setGraph(reader.getGraph());
	}	
	
	public boolean isUpdating() {
		return updating;
	}
	
	public void setUpdating(boolean set) {
		updating = set;
	}
	
	public Rectangle2D.Float getBbox() {
		return bbox;
	}
	
	public int findVertex(String name) {
		Integer i = (Integer)nodeMap.get(name);
		if (i == null) {
			int v = graph.addVertex();
			i = new Integer(v);
			nodeMap.put(name, i);
			id.setExtend(v, name);
		}
		return i.intValue();
	}
	
	public int findEdge(int in, int out) {
		return graph.findEdge(in, out);
	}
	
	String getText(Token t) {
		String s = t.getText();
		return s;
	}
	
	int getInteger(Token t) {
		String s = t.getText();
		return Integer.parseInt(s);		
	}
	
	protected Column createColumn(String name, String field) {
		if (name.equals("label")) {
			return new StringColumn(name);
		}
		return AbstractReader.createColumn(AbstractReader.guessFieldType(field), name);
	}
	
	
	void addNodeAttribute(String name, String value) {
		Column c = graph.getVertexTable().getColumn(name);
		if (c == null) {
			c = createColumn(name, value);
			graph.getVertexTable().addColumn(c);
		}
		c.setValueOrNullAt(currentNode, value);
	}
	
	void addNodeTitle(String title) {
		currentNode = findVertex(title);
	}
	
	void addNodeX(int x) {
		Rectangle2D.Float s =
			(Rectangle2D.Float)graphReader.findNodeShape(currentNode);
		s.x = x;
	}
	
	void addNodeY(int y) {
		Rectangle2D.Float s =
			(Rectangle2D.Float)graphReader.findNodeShape(currentNode);
		s.y = y;
	}
	
	void addNodeWidth(int w) {
		Rectangle2D.Float s =
			(Rectangle2D.Float)graphReader.findNodeShape(currentNode);
		s.width = w;
	}
	
	void addNodeHeight(int h) {
		Rectangle2D.Float s =
			(Rectangle2D.Float)graphReader.findNodeShape(currentNode);
		s.height = h;
	}
	
	void addEdgeAttribute(String name, String value) {
		if (currentEdge != -1) {
			Column c = graph.getEdgeTable().getColumn(name);
			if (c == null) {
				c = createColumn(name, value);
				graph.getEdgeTable().addColumn(c);
			}
			c.setValueOrNullAt(currentEdge, value);
		}
		if (name.equals("sourcename")) {
			sourceNode = findVertex(value);
		}
		else if (name.equals("targetname")) {
			targetNode = findVertex(value);
		}
		if (targetNode != -1 && sourceNode != -1) {
			currentEdge = findEdge(sourceNode, targetNode);
			sourceNode = -1;
			targetNode = -1;
		}
	}

protected VCGParser(TokenBuffer tokenBuf, int k) {
  super(tokenBuf,k);
  tokenNames = _tokenNames;
}

public VCGParser(TokenBuffer tokenBuf) {
  this(tokenBuf,2);
}

protected VCGParser(TokenStream lexer, int k) {
  super(lexer,k);
  tokenNames = _tokenNames;
}

public VCGParser(TokenStream lexer) {
  this(lexer,2);
}

public VCGParser(ParserSharedInputState state) {
  super(state,2);
  tokenNames = _tokenNames;
}

	public final void graph() throws RecognitionException, TokenStreamException {
		
		
		try {      // for error handling
			match(LITERAL_graph);
			match(COLON);
			match(LCUR);
			{
			int _cnt236=0;
			_loop236:
			do {
				if ((_tokenSet_0.member(LA(1)))) {
					graph_entry();
				}
				else {
					if ( _cnt236>=1 ) { break _loop236; } else {throw new NoViableAltException(LT(1), getFilename());}
				}
				
				_cnt236++;
			} while (true);
			}
			match(RCUR);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_1);
		}
	}
	
	public final void graph_entry() throws RecognitionException, TokenStreamException {
		
		
		try {      // for error handling
			switch ( LA(1)) {
			case ID:
			case LITERAL_title:
			case LITERAL_x:
			case LITERAL_y:
			case LITERAL_loc:
			case LITERAL_classname:
			case LITERAL_infoname:
			case LITERAL_colorentry:
			case LITERAL_no_nearedges:
			{
				graph_attribute();
				break;
			}
			case 21:
			{
				node_defaults();
				break;
			}
			case 22:
			{
				edge_defaults();
				break;
			}
			case 19:
			{
				foldnode_defaults();
				break;
			}
			case 20:
			{
				foldedge_defaults();
				break;
			}
			case LITERAL_graph:
			{
				graph();
				break;
			}
			case LITERAL_node:
			{
				node();
				break;
			}
			case LITERAL_edge:
			{
				edge();
				break;
			}
			case LITERAL_nearedge:
			{
				nearedge();
				break;
			}
			case LITERAL_bentnearedge:
			{
				bentnearedge();
				break;
			}
			case LITERAL_backedge:
			{
				backedge();
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
			recover(ex,_tokenSet_1);
		}
	}
	
	public final void graph_attribute() throws RecognitionException, TokenStreamException {
		
		Token  i = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case ID:
			{
				match(ID);
				match(COLON);
				value();
				break;
			}
			case LITERAL_title:
			{
				match(LITERAL_title);
				match(COLON);
				i = LT(1);
				match(ID);
				graph.setName(getText(i));
				break;
			}
			case LITERAL_x:
			{
				match(LITERAL_x);
				match(COLON);
				match(INTEGER);
				break;
			}
			case LITERAL_y:
			{
				match(LITERAL_y);
				match(COLON);
				match(INTEGER);
				break;
			}
			case LITERAL_loc:
			{
				match(LITERAL_loc);
				match(COLON);
				match(LCUR);
				match(LITERAL_x);
				match(COLON);
				match(INTEGER);
				match(LITERAL_y);
				match(COLON);
				match(INTEGER);
				match(RCUR);
				break;
			}
			case LITERAL_classname:
			{
				match(LITERAL_classname);
				match(INTEGER);
				match(COLON);
				match(ID);
				break;
			}
			case LITERAL_infoname:
			{
				match(LITERAL_infoname);
				match(INTEGER);
				match(COLON);
				match(ID);
				break;
			}
			case LITERAL_colorentry:
			{
				match(LITERAL_colorentry);
				match(INTEGER);
				match(COLON);
				match(INTEGER);
				match(INTEGER);
				match(INTEGER);
				break;
			}
			case LITERAL_no_nearedges:
			{
				match(LITERAL_no_nearedges);
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
			recover(ex,_tokenSet_1);
		}
	}
	
	public final void node_defaults() throws RecognitionException, TokenStreamException {
		
		
		try {      // for error handling
			match(21);
			node_attribute();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_1);
		}
	}
	
	public final void edge_defaults() throws RecognitionException, TokenStreamException {
		
		
		try {      // for error handling
			match(22);
			edge_attribute();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_1);
		}
	}
	
	public final void foldnode_defaults() throws RecognitionException, TokenStreamException {
		
		
		try {      // for error handling
			match(19);
			node_attribute();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_1);
		}
	}
	
	public final void foldedge_defaults() throws RecognitionException, TokenStreamException {
		
		
		try {      // for error handling
			match(20);
			edge_attribute();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_1);
		}
	}
	
	public final void node() throws RecognitionException, TokenStreamException {
		
		
		try {      // for error handling
			match(LITERAL_node);
			match(COLON);
			match(LCUR);
			currentNode = Graph.NIL;
			{
			int _cnt246=0;
			_loop246:
			do {
				if ((_tokenSet_2.member(LA(1)))) {
					node_attribute();
				}
				else {
					if ( _cnt246>=1 ) { break _loop246; } else {throw new NoViableAltException(LT(1), getFilename());}
				}
				
				_cnt246++;
			} while (true);
			}
			currentNode = Graph.NIL;
			match(RCUR);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_1);
		}
	}
	
	public final void edge() throws RecognitionException, TokenStreamException {
		
		
		try {      // for error handling
			match(LITERAL_edge);
			match(COLON);
			match(LCUR);
			currentEdge = sourceNode = targetNode = Graph.NIL;
			{
			int _cnt249=0;
			_loop249:
			do {
				if ((LA(1)==ID)) {
					edge_attribute();
				}
				else {
					if ( _cnt249>=1 ) { break _loop249; } else {throw new NoViableAltException(LT(1), getFilename());}
				}
				
				_cnt249++;
			} while (true);
			}
			currentEdge = sourceNode = targetNode = Graph.NIL;
			match(RCUR);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_1);
		}
	}
	
	public final void nearedge() throws RecognitionException, TokenStreamException {
		
		
		try {      // for error handling
			match(LITERAL_nearedge);
			match(COLON);
			match(LCUR);
			currentEdge = sourceNode = targetNode = Graph.NIL;
			{
			int _cnt252=0;
			_loop252:
			do {
				if ((LA(1)==ID)) {
					edge_attribute();
				}
				else {
					if ( _cnt252>=1 ) { break _loop252; } else {throw new NoViableAltException(LT(1), getFilename());}
				}
				
				_cnt252++;
			} while (true);
			}
			currentEdge = sourceNode = targetNode = Graph.NIL;
			match(RCUR);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_1);
		}
	}
	
	public final void bentnearedge() throws RecognitionException, TokenStreamException {
		
		
		try {      // for error handling
			match(LITERAL_bentnearedge);
			match(COLON);
			match(LCUR);
			currentEdge = sourceNode = targetNode = Graph.NIL;
			{
			int _cnt255=0;
			_loop255:
			do {
				if ((LA(1)==ID)) {
					edge_attribute();
				}
				else {
					if ( _cnt255>=1 ) { break _loop255; } else {throw new NoViableAltException(LT(1), getFilename());}
				}
				
				_cnt255++;
			} while (true);
			}
			currentEdge = sourceNode = targetNode = Graph.NIL;
			match(RCUR);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_1);
		}
	}
	
	public final void backedge() throws RecognitionException, TokenStreamException {
		
		
		try {      // for error handling
			match(LITERAL_backedge);
			match(COLON);
			match(LCUR);
			currentEdge = sourceNode = targetNode = Graph.NIL;
			{
			int _cnt258=0;
			_loop258:
			do {
				if ((LA(1)==ID)) {
					edge_attribute();
				}
				else {
					if ( _cnt258>=1 ) { break _loop258; } else {throw new NoViableAltException(LT(1), getFilename());}
				}
				
				_cnt258++;
			} while (true);
			}
			currentEdge = sourceNode = targetNode = Graph.NIL;
			match(RCUR);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_1);
		}
	}
	
	public final String  value() throws RecognitionException, TokenStreamException {
		 String ret=null;
		
		Token  id = null;
		Token  i = null;
		Token  f = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case ID:
			{
				id = LT(1);
				match(ID);
				ret = getText(id);
				break;
			}
			case INTEGER:
			{
				i = LT(1);
				match(INTEGER);
				ret = getText(i);
				break;
			}
			case FLOAT:
			{
				f = LT(1);
				match(FLOAT);
				ret = getText(f);
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
			recover(ex,_tokenSet_1);
		}
		return ret;
	}
	
	public final void node_attribute() throws RecognitionException, TokenStreamException {
		
		Token  a = null;
		Token  i = null;
		Token  x = null;
		Token  y = null;
		Token  xl = null;
		Token  yl = null;
		String v;
		
		try {      // for error handling
			switch ( LA(1)) {
			case ID:
			{
				a = LT(1);
				match(ID);
				match(COLON);
				v=value();
				addNodeAttribute(getText(a), v);
				break;
			}
			case LITERAL_title:
			{
				match(LITERAL_title);
				match(COLON);
				i = LT(1);
				match(ID);
				addNodeTitle(getText(i));
				break;
			}
			case LITERAL_x:
			{
				match(LITERAL_x);
				match(COLON);
				x = LT(1);
				match(INTEGER);
				addNodeX(getInteger(x));
				break;
			}
			case LITERAL_y:
			{
				match(LITERAL_y);
				match(COLON);
				y = LT(1);
				match(INTEGER);
				addNodeY(getInteger(y));
				break;
			}
			case LITERAL_loc:
			{
				match(LITERAL_loc);
				match(COLON);
				match(LCUR);
				match(LITERAL_x);
				match(COLON);
				xl = LT(1);
				match(INTEGER);
				match(LITERAL_y);
				match(COLON);
				yl = LT(1);
				match(INTEGER);
				match(RCUR);
				addNodeX(getInteger(xl)); addNodeY(getInteger(yl));
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
			recover(ex,_tokenSet_1);
		}
	}
	
	public final void edge_attribute() throws RecognitionException, TokenStreamException {
		
		Token  a = null;
		String v;
		
		try {      // for error handling
			a = LT(1);
			match(ID);
			match(COLON);
			v=value();
			addEdgeAttribute(getText(a), v);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_1);
		}
	}
	
	
	public static final String[] _tokenNames = {
		"<0>",
		"EOF",
		"<2>",
		"NULL_TREE_LOOKAHEAD",
		"\"graph\"",
		"COLON",
		"LCUR",
		"RCUR",
		"a string value",
		"\"title\"",
		"\"x\"",
		"an integer value",
		"\"y\"",
		"\"loc\"",
		"\"classname\"",
		"\"infoname\"",
		"\"colorentry\"",
		"\"no_nearedges\"",
		"an floating point value",
		"\"foldnode.\"",
		"\"foldedge.\"",
		"\"node.\"",
		"\"edge.\"",
		"\"node\"",
		"\"edge\"",
		"\"nearedge\"",
		"\"bentnearedge\"",
		"\"backedge\"",
		"a digit",
		"WS",
		"SL_COMMENT",
		"ML_COMMENT",
		"ESC"
	};
	
	private static final long[] mk_tokenSet_0() {
		long[] data = { 268171024L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_0 = new BitSet(mk_tokenSet_0());
	private static final long[] mk_tokenSet_1() {
		long[] data = { 268171152L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_1 = new BitSet(mk_tokenSet_1());
	private static final long[] mk_tokenSet_2() {
		long[] data = { 14080L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_2 = new BitSet(mk_tokenSet_2());
	
	}
