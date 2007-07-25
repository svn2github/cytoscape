// $ANTLR : "DOT.g" -> "DOTParser.java"$

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
import infovis.Column;
import infovis.column.FloatColumn;
import infovis.io.AbstractReader;
import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.awt.geom.GeneralPath;
import java.util.HashMap;
import java.util.ArrayList;
import infovis.utils.RectPool;
import infovis.column.StringColumn;
import infovis.utils.RowIterator;

/**
 * Parser for the DOT Graph format.
 *
 * @author Jean-Daniel Fekete
 */

public class DOTParser extends antlr.LLkParser       implements DOTTokenTypes
 {

	/** Name of the column containing dot positions for the layout. */
	public static final String DOT_POS_COLUMN = "pos";
	/** Name of the column containing dot widths for the layout. */
	public static final String DOT_WIDTH_COLUMN = "width";
	/** Name of the column containing dot heights for the layout. */
	public static final String DOT_HEIGHT_COLUMN = "height";
    	
	Graph graph;
	HashMap nodeMap = new HashMap();
	StringColumn id;
	Rectangle2D.Float bbox;
	AbstractGraphReader graphReader;
	String attributePrefix = "#dot_";

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
	
	void append(IntArrayList into, IntArrayList from) {
		if (from == null)
			return;
		for (int i = 0; i < from.size(); i++) {
			into.add(from.get(i));
		}
	}
	
	IntArrayList nodeList(int node) {
		IntArrayList ret = new IntArrayList();
		ret.add(node);
		return ret;
	}
	
	protected Column createColumn(String name, String field) {
		return AbstractReader.createColumn(AbstractReader.guessFieldType(field), name);
	}

	Rectangle2D.Float parseBbox(String value) {
		String[] values = value.split(",");
		int xmin = Integer.parseInt(values[0]);
		int ymin = Integer.parseInt(values[1]);
		int xmax = Integer.parseInt(values[2]);
		int ymax = Integer.parseInt(values[3]);
		
		Rectangle2D.Float rect = RectPool.allocateRect();
		rect.setRect(xmin, ymin, xmax - xmin, ymax-ymin);
		return rect; 
	}
	
	void addGraphAttributes(ArrayList attrs) {
		for (int j = 0; j < attrs.size(); j += 2) {
			String name = (String)attrs.get(j);
			String metaName = attributePrefix+name;
			String value = (String)attrs.get(j+1);
			graph.getEdgeTable().getMetadata().addAttribute(metaName, value);
			if (name.equals("bb")) {
				bbox = parseBbox(value);
			}
		}
	}
	
	protected int parsePoint(String pos, int index, Point p) {
		while(pos.charAt(index)==' ') {
	            index++;   
                }
        	int i = pos.indexOf(',', index+1);
                if (i == -1)
                    return -1;
                p.x = Integer.parseInt(pos.substring(index, i));
                index = pos.indexOf(' ', i+1);
                if (index == -1)
                	p.y = Integer.parseInt(pos.substring(i+1));
                else 
                	p.y = Integer.parseInt(pos.substring(i+1, index)); 
                return index;
	}
	
	void parseEdgeValue(int e, String pos) {
		GeneralPath p = (GeneralPath)graphReader.getLinkShape(e);
                if (p == null) {
                	p = new GeneralPath();
                	graphReader.setLinkShape(e, p);
                }
                else {
                	p.reset();
                }
        
                int index = 0;
                Point pt = new Point();
                float x1 = 0, y1 = 0;
                float x2, y2, x3, y3, x4 = 0, y4 = 0;
                float arrow_x = 0, arrow_y = 0;
                
                if (pos.charAt(index)=='e' ||
                    pos.charAt(index)=='s') {
                    index = parsePoint(pos, 2, pt);
                    arrow_x = pt.x;
                    arrow_y = pt.y;
                }
                
                if (index == -1)
                	return;
                index = parsePoint(pos, index, pt);
                x1 = pt.x;
                y1 = pt.y;
                p.moveTo(x1, y1);
                
                while (index != -1) {
			index = parsePoint(pos, index, pt);
			x2 = pt.x;
			y2 = pt.y;
			if (index != -1) {
				index = parsePoint(pos, index, pt);
			        x3 = pt.x;
			        y3 = pt.y;
			}
			else {
				x3 = x2;
				y3 = y2;
			}
			if (index != -1) {
				index = parsePoint(pos, index, pt);
				x4 = pt.x;
				y4 = pt.y;
			}
			else {
				x4 = x3;
				y4 = y3;
			}
			p.curveTo(x2, y2, x3, y3, x4, y4);
                }
                if (pos.charAt(0)=='e') {
                	addArrow(x4, y4, arrow_x, arrow_y, p);
                }
                else if (pos.charAt(0)=='s') {
                	addArrow(x1, y1, arrow_x, arrow_y, p);
                }
	}
                    
        public void addArrow(float from_x, float from_y, float to_x, float to_y, GeneralPath p) {
                float dx = to_x - from_x;
                float dy = to_y - from_y;
        
                p.moveTo(to_x, to_y);
                p.lineTo(from_x + dy/2, from_y - dx/2);
                p.lineTo(from_x - dy/2, from_y + dx/2);
                p.closePath();        
            }

	
	void addEdgeAttributes(IntArrayList el, ArrayList attrs) {
		for (int i = 0; i < el.size(); i++) {
			int e = el.get(i);
			for (int j = 0; j < attrs.size(); j += 2) {
				String name = (String)attrs.get(j);
				String colName = attributePrefix+name;
				String value = (String)attrs.get(j+1);
				Column c = graph.getEdgeTable().getColumn(colName);
				if (c == null) {
					c = createColumn(colName, value);
					graph.getEdgeTable().addColumn(c);
				}
				c.setValueOrNullAt(e, value);
				if (colName.equals(attributePrefix+DOT_POS_COLUMN)) {
					parseEdgeValue(e, value);
				}
			}
		}
	}
	
	void addNodeAttributes(IntArrayList nl, ArrayList attrs) {
		for (int i = 0; i < nl.size(); i++) {
			int v = nl.get(i);
			for (int j = 0; j < attrs.size(); j += 2) {
				String name = (String)attrs.get(j);
				String colName = attributePrefix+name;
				String value = (String)attrs.get(j+1);
				Column c = graph.getVertexTable().getColumn(colName);
				if (c == null) {
					c = createColumn(colName, value);
					graph.getVertexTable().addColumn(c);
				}
				c.setValueOrNullAt(v, value);
				if (colName.equals(attributePrefix+DOT_POS_COLUMN)) {
					Rectangle2D.Float s = (Rectangle2D.Float)graphReader.findNodeShape(v);
					int index = value.indexOf(',');
					
					s.x += Integer.parseInt(value.substring(0, index));
					s.y += Integer.parseInt(value.substring(index+1));
				}
				else if (colName.equals(attributePrefix+DOT_WIDTH_COLUMN)) {
					Rectangle2D.Float s = (Rectangle2D.Float)graphReader.findNodeShape(v);
					s.width = 72*((FloatColumn)c).get(v);
					s.x -= s.width/2;
				}
				else if (colName.equals(attributePrefix+DOT_HEIGHT_COLUMN)) {
					Rectangle2D.Float s = (Rectangle2D.Float)graphReader.findNodeShape(v);
					s.height = 72*((FloatColumn)c).get(v);
					s.y -= s.height/2;
				}
			}
		}
	}
	
	String getText(Token t) {
		String s = t.getText();
		int index = s.indexOf('\\');
		
		if (index == -1) {
			return s;
		}
		StringBuffer buffer = new StringBuffer();
		int prev = 0;
		while (index != -1) {
			buffer.append(s.substring(prev, index));
			prev = index+2;
			if ((index+1) == s.length()) {
				return buffer.toString();
			}
		    char c = s.charAt(index+1);
            if (c == '\r') {
                if (prev < s.length() && s.charAt(prev) == '\n')
                    prev++;
            }
            else if (c != 'N' && c != '\n') { // ignore \N for now
            	switch(c) {
            		case 't': c = '\t'; break;
            	}
				buffer.append(c);
			}
			index = s.indexOf('\\', prev);
		}
		buffer.append(s.substring(prev));
		return buffer.toString();
	}

protected DOTParser(TokenBuffer tokenBuf, int k) {
  super(tokenBuf,k);
  tokenNames = _tokenNames;
}

public DOTParser(TokenBuffer tokenBuf) {
  this(tokenBuf,2);
}

protected DOTParser(TokenStream lexer, int k) {
  super(lexer,k);
  tokenNames = _tokenNames;
}

public DOTParser(TokenStream lexer) {
  this(lexer,2);
}

public DOTParser(ParserSharedInputState state) {
  super(state,2);
  tokenNames = _tokenNames;
}

	public final void graph() throws RecognitionException, TokenStreamException {
		
		IntArrayList ignore;
		
		try {      // for error handling
			hdr();
			ignore=body();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_0);
		}
	}
	
	public final void hdr() throws RecognitionException, TokenStreamException {
		
		Token  a = null;
		
		try {      // for error handling
			{
			{
			switch ( LA(1)) {
			case LITERAL_strict:
			{
				match(LITERAL_strict);
				break;
			}
			case LITERAL_graph:
			case LITERAL_digraph:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			graph_type();
			}
			{
			a = LT(1);
			match(ATOM);
			graph.setName(getText(a));
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_1);
		}
	}
	
	public final IntArrayList  body() throws RecognitionException, TokenStreamException {
		IntArrayList ret=null;
		
		
		try {      // for error handling
			match(LCUR);
			ret=opt_stmt_list();
			match(RCUR);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_2);
		}
		return ret;
	}
	
	public final void graph_type() throws RecognitionException, TokenStreamException {
		
		
		try {      // for error handling
			switch ( LA(1)) {
			case LITERAL_graph:
			{
				match(LITERAL_graph);
				graph.setDirected(false);
				break;
			}
			case LITERAL_digraph:
			{
				match(LITERAL_digraph);
				graph.setDirected(true);
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
			recover(ex,_tokenSet_3);
		}
	}
	
	public final IntArrayList  opt_stmt_list() throws RecognitionException, TokenStreamException {
		IntArrayList ret=new IntArrayList();
		
		IntArrayList iv;
		
		try {      // for error handling
			{
			_loop91:
			do {
				if ((_tokenSet_4.member(LA(1)))) {
					iv=stmt();
					append(ret, iv);
				}
				else {
					break _loop91;
				}
				
			} while (true);
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_5);
		}
		return ret;
	}
	
	public final IntArrayList  stmt() throws RecognitionException, TokenStreamException {
		IntArrayList ret=null;
		
		ret = null; ArrayList la;
		
		try {      // for error handling
			switch ( LA(1)) {
			case LITERAL_graph:
			{
				match(LITERAL_graph);
				la=attr_list();
				addGraphAttributes(la);
				break;
			}
			case LITERAL_node:
			{
				match(LITERAL_node);
				la=attr_list();
				break;
			}
			case LITERAL_edge:
			{
				match(LITERAL_edge);
				la=attr_list();
				break;
			}
			case SEMI:
			{
				match(SEMI);
				break;
			}
			default:
				if ((LA(1)==ATOM) && (LA(2)==EQUAL)) {
					graph_attr_defs();
				}
				else if ((LA(1)==ATOM||LA(1)==LCUR||LA(1)==LITERAL_subgraph) && (_tokenSet_6.member(LA(2)))) {
					ret=compound();
				}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_7);
		}
		return ret;
	}
	
	public final ArrayList  attr_list() throws RecognitionException, TokenStreamException {
		 ArrayList ret=null;
		
		ret = new ArrayList();
		
		try {      // for error handling
			{
			_loop114:
			do {
				if ((LA(1)==LBR)) {
					match(LBR);
					attr_defs(ret);
					match(RBR);
				}
				else {
					break _loop114;
				}
				
			} while (true);
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_7);
		}
		return ret;
	}
	
	public final void graph_attr_defs() throws RecognitionException, TokenStreamException {
		
		
		try {      // for error handling
			attr_list_simple();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_7);
		}
	}
	
	public final IntArrayList  compound() throws RecognitionException, TokenStreamException {
		IntArrayList ret=null;
		
		int n; IntArrayList nl, el; ArrayList attrs; ret=new IntArrayList();
		
		try {      // for error handling
			switch ( LA(1)) {
			case ATOM:
			{
				n=node_id();
				{
				switch ( LA(1)) {
				case ATOM:
				case LCUR:
				case RCUR:
				case LITERAL_graph:
				case LITERAL_node:
				case LITERAL_edge:
				case SEMI:
				case LBR:
				case LITERAL_subgraph:
				{
					attrs=attr_list();
					
						    		ret.add(n);
						    		addNodeAttributes(ret,attrs);
						    	
					break;
				}
				case D_EDGE_OP:
				case ND_EDGE_OP:
				{
					el=edge_cont(nodeList(n));
					attrs=attr_list();
					
						    		addEdgeAttributes(el, attrs);
						    		ret.add(n);
						    		for (int i = 0; i < el.size(); i++) {
						    			ret.add(graph.getSecondVertex(el.get(i)));
						    		}
						    	
						    	
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				break;
			}
			case LCUR:
			case LITERAL_subgraph:
			{
				ret=subgraph();
				{
				switch ( LA(1)) {
				case ATOM:
				case LCUR:
				case RCUR:
				case LITERAL_graph:
				case LITERAL_node:
				case LITERAL_edge:
				case SEMI:
				case LBR:
				case LITERAL_subgraph:
				{
					attrs=attr_list();
					
						    		addNodeAttributes(ret,attrs);
						    	
					break;
				}
				case D_EDGE_OP:
				case ND_EDGE_OP:
				{
					el=edge_cont(ret);
					attrs=attr_list();
					
						    		addEdgeAttributes(el, attrs);
						    		ret.clear();
						    		for (int i = 0; i < el.size(); i++) {
						    			ret.add(graph.getSecondVertex(el.get(i)));
						    		}
						    	
						    	
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
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
			recover(ex,_tokenSet_7);
		}
		return ret;
	}
	
	public final int  node_id() throws RecognitionException, TokenStreamException {
		int n = Graph.NIL;
		
		
		try {      // for error handling
			n=node();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_8);
		}
		return n;
	}
	
	public final IntArrayList  edge_cont(
		IntArrayList vertices
	) throws RecognitionException, TokenStreamException {
		IntArrayList ret=null;
		
		int n; IntArrayList nl, el; ret = new IntArrayList();
		
		try {      // for error handling
			edge_op();
			{
			switch ( LA(1)) {
			case ATOM:
			{
				n=node_id();
				nl=nodeList(n);
				break;
			}
			case LCUR:
			case LITERAL_subgraph:
			{
				nl=subgraph();
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			
					for (int i = 0; i < vertices.size(); i++) {
						for (int j = 0; j < nl.size(); j++) {
							int e = findEdge(vertices.get(i), nl.get(j));
							ret.add(e);
						}
					}
				
			{
			switch ( LA(1)) {
			case D_EDGE_OP:
			case ND_EDGE_OP:
			{
				el=edge_cont(nl);
				append(ret, el);
				break;
			}
			case ATOM:
			case LCUR:
			case RCUR:
			case LITERAL_graph:
			case LITERAL_node:
			case LITERAL_edge:
			case SEMI:
			case LBR:
			case LITERAL_subgraph:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_9);
		}
		return ret;
	}
	
	public final IntArrayList  subgraph() throws RecognitionException, TokenStreamException {
		IntArrayList ret=null;
		
		
		try {      // for error handling
			switch ( LA(1)) {
			case LCUR:
			{
				ret=body();
				break;
			}
			case LITERAL_subgraph:
			{
				match(LITERAL_subgraph);
				match(ATOM);
				ret=body();
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
			recover(ex,_tokenSet_2);
		}
		return ret;
	}
	
	public final void edge_op() throws RecognitionException, TokenStreamException {
		
		
		try {      // for error handling
			switch ( LA(1)) {
			case D_EDGE_OP:
			{
				match(D_EDGE_OP);
				break;
			}
			case ND_EDGE_OP:
			{
				match(ND_EDGE_OP);
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
			recover(ex,_tokenSet_10);
		}
	}
	
	public final int  node() throws RecognitionException, TokenStreamException {
		int node=Graph.NIL;
		
		Token  n = null;
		
		try {      // for error handling
			n = LT(1);
			match(ATOM);
			{
			switch ( LA(1)) {
			case COLON:
			case 19:
			{
				port();
				break;
			}
			case EOF:
			case ATOM:
			case LCUR:
			case RCUR:
			case LITERAL_graph:
			case LITERAL_node:
			case LITERAL_edge:
			case SEMI:
			case D_EDGE_OP:
			case ND_EDGE_OP:
			case COMMA:
			case LBR:
			case LITERAL_subgraph:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			node = findVertex(getText(n));
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_11);
		}
		return node;
	}
	
	public final IntArrayList  simple() throws RecognitionException, TokenStreamException {
		IntArrayList ret = null;
		
		
		try {      // for error handling
			switch ( LA(1)) {
			case ATOM:
			{
				ret=node_list();
				break;
			}
			case LCUR:
			case LITERAL_subgraph:
			{
				ret=subgraph();
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
			recover(ex,_tokenSet_0);
		}
		return ret;
	}
	
	public final IntArrayList  node_list() throws RecognitionException, TokenStreamException {
		IntArrayList ret=null;
		
		int n; ret = new IntArrayList();
		
		try {      // for error handling
			n=node();
			ret.add(n);
			{
			_loop104:
			do {
				if ((LA(1)==COMMA)) {
					match(COMMA);
					n=node();
					ret.add(n);
				}
				else {
					break _loop104;
				}
				
			} while (true);
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_0);
		}
		return ret;
	}
	
	public final void port() throws RecognitionException, TokenStreamException {
		
		
		try {      // for error handling
			switch ( LA(1)) {
			case COLON:
			{
				port_location();
				break;
			}
			case 19:
			{
				port_angle();
				{
				switch ( LA(1)) {
				case COLON:
				{
					port_location();
					break;
				}
				case EOF:
				case ATOM:
				case LCUR:
				case RCUR:
				case LITERAL_graph:
				case LITERAL_node:
				case LITERAL_edge:
				case SEMI:
				case D_EDGE_OP:
				case ND_EDGE_OP:
				case COMMA:
				case LBR:
				case LITERAL_subgraph:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
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
			recover(ex,_tokenSet_11);
		}
	}
	
	public final void port_location() throws RecognitionException, TokenStreamException {
		
		
		try {      // for error handling
			match(COLON);
			{
			switch ( LA(1)) {
			case ATOM:
			{
				match(ATOM);
				break;
			}
			case LPAR:
			{
				match(LPAR);
				match(ATOM);
				match(COMMA);
				match(ATOM);
				match(RPAR);
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_11);
		}
	}
	
	public final void port_angle() throws RecognitionException, TokenStreamException {
		
		
		try {      // for error handling
			match(19);
			match(ATOM);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_12);
		}
	}
	
	public final void attr_defs(
		ArrayList arr
	) throws RecognitionException, TokenStreamException {
		
		
		try {      // for error handling
			{
			_loop118:
			do {
				if ((LA(1)==ATOM||LA(1)==COMMA)) {
					{
					switch ( LA(1)) {
					case COMMA:
					{
						match(COMMA);
						break;
					}
					case ATOM:
					{
						break;
					}
					default:
					{
						throw new NoViableAltException(LT(1), getFilename());
					}
					}
					}
					attr_item(arr);
				}
				else {
					break _loop118;
				}
				
			} while (true);
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_13);
		}
	}
	
	public final void attr_item(
		ArrayList arr
	) throws RecognitionException, TokenStreamException {
		
		Token  a = null;
		Token  v = null;
		
		try {      // for error handling
			a = LT(1);
			match(ATOM);
			match(EQUAL);
			v = LT(1);
			match(ATOM);
			
				arr.add(getText(a));
				arr.add(getText(v));
			
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_14);
		}
	}
	
	public final void attr_list_simple() throws RecognitionException, TokenStreamException {
		
		Token  a = null;
		Token  v = null;
		
		try {      // for error handling
			a = LT(1);
			match(ATOM);
			match(EQUAL);
			v = LT(1);
			match(ATOM);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_7);
		}
	}
	
	
	public static final String[] _tokenNames = {
		"<0>",
		"EOF",
		"<2>",
		"NULL_TREE_LOOKAHEAD",
		"\"strict\"",
		"an identifier",
		"LCUR",
		"RCUR",
		"\"graph\"",
		"\"digraph\"",
		"\"node\"",
		"\"edge\"",
		"SEMI",
		"D_EDGE_OP",
		"ND_EDGE_OP",
		"COMMA",
		"COLON",
		"LPAR",
		"RPAR",
		"\"@\"",
		"LBR",
		"RBR",
		"EQUAL",
		"\"subgraph\"",
		"BALANCED",
		"WS",
		"ESC",
		"SL_COMMENT",
		"ML_COMMENT"
	};
	
	private static final long[] mk_tokenSet_0() {
		long[] data = { 2L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_0 = new BitSet(mk_tokenSet_0());
	private static final long[] mk_tokenSet_1() {
		long[] data = { 64L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_1 = new BitSet(mk_tokenSet_1());
	private static final long[] mk_tokenSet_2() {
		long[] data = { 9469410L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_2 = new BitSet(mk_tokenSet_2());
	private static final long[] mk_tokenSet_3() {
		long[] data = { 32L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_3 = new BitSet(mk_tokenSet_3());
	private static final long[] mk_tokenSet_4() {
		long[] data = { 8396128L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_4 = new BitSet(mk_tokenSet_4());
	private static final long[] mk_tokenSet_5() {
		long[] data = { 128L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_5 = new BitSet(mk_tokenSet_5());
	private static final long[] mk_tokenSet_6() {
		long[] data = { 10059232L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_6 = new BitSet(mk_tokenSet_6());
	private static final long[] mk_tokenSet_7() {
		long[] data = { 8396256L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_7 = new BitSet(mk_tokenSet_7());
	private static final long[] mk_tokenSet_8() {
		long[] data = { 9469408L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_8 = new BitSet(mk_tokenSet_8());
	private static final long[] mk_tokenSet_9() {
		long[] data = { 9444832L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_9 = new BitSet(mk_tokenSet_9());
	private static final long[] mk_tokenSet_10() {
		long[] data = { 8388704L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_10 = new BitSet(mk_tokenSet_10());
	private static final long[] mk_tokenSet_11() {
		long[] data = { 9502178L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_11 = new BitSet(mk_tokenSet_11());
	private static final long[] mk_tokenSet_12() {
		long[] data = { 9567714L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_12 = new BitSet(mk_tokenSet_12());
	private static final long[] mk_tokenSet_13() {
		long[] data = { 2097152L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_13 = new BitSet(mk_tokenSet_13());
	private static final long[] mk_tokenSet_14() {
		long[] data = { 2129952L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_14 = new BitSet(mk_tokenSet_14());
	
	}
