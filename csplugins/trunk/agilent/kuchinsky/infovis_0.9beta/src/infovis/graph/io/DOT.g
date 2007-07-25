header {
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
}

{
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
}	
class DOTParser extends Parser;
options {
	exportVocab=DOT;
	k = 2;
}

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
}

graph
	{ IntArrayList ignore; }
	:	hdr ignore=body
	;

hdr
    :   (	(	"strict"	)? graph_type
    	)
        ( a:ATOM { graph.setName(getText(a)); } )
    ;

body returns [IntArrayList ret=null]
	:	LCUR ret=opt_stmt_list RCUR
	;

graph_type
	:	"graph"		{ graph.setDirected(false); }
    |   "digraph"	{ graph.setDirected(true); }
    ;

opt_stmt_list returns [IntArrayList ret=new IntArrayList()]
	{ IntArrayList iv; }
    :   ( iv=stmt 	{ append(ret, iv); } )*
    ;

stmt returns [IntArrayList ret=null]
	{ ret = null; ArrayList la; }
    :   "graph" la=attr_list { addGraphAttributes(la); }
    |	"node"	la=attr_list
    |	"edge"	la=attr_list
    |   graph_attr_defs 
    | 	ret=compound
    |	SEMI
    ;

compound returns [IntArrayList ret=null]
	{ int n; IntArrayList nl, el; ArrayList attrs; ret=new IntArrayList(); }
    :   n=node_id	(
	    attrs=attr_list
	    	{
	    		ret.add(n);
	    		addNodeAttributes(ret,attrs);
	    	}
	    |	el=edge_cont[nodeList(n)] attrs=attr_list
	    	{ 
	    		addEdgeAttributes(el, attrs);
	    		ret.add(n);
	    		for (int i = 0; i < el.size(); i++) {
	    			ret.add(graph.getSecondVertex(el.get(i)));
	    		}
	    	
	    	}
    )
    |	ret=subgraph 
    (
	    attrs=attr_list
	    	{
	    		addNodeAttributes(ret,attrs);
	    	}
	    |	el=edge_cont[ret] attrs=attr_list
	    	{ 
	    		addEdgeAttributes(el, attrs);
	    		ret.clear();
	    		for (int i = 0; i < el.size(); i++) {
	    			ret.add(graph.getSecondVertex(el.get(i)));
	    		}
	    	
	    	}
    )
    ;

edge_cont [IntArrayList vertices] returns [IntArrayList ret=null]
	{ int n; IntArrayList nl, el; ret = new IntArrayList(); }
	:	edge_op
		(	n=node_id	{ nl=nodeList(n); }
		|	nl=subgraph	//{ append(ret, nl); }
		)
	{
		for (int i = 0; i < vertices.size(); i++) {
			for (int j = 0; j < nl.size(); j++) {
				int e = findEdge(vertices.get(i), nl.get(j));
				ret.add(e);
			}
		}
	}
	( el=edge_cont[nl]	{ append(ret, el); } )?
	;

node_id returns [int n = Graph.NIL]
	:	n=node
	;

simple returns[IntArrayList ret = null]
    :   ret=node_list
    |   ret=subgraph
    ;

edge_op
    :   D_EDGE_OP
    |   ND_EDGE_OP
    ;

node_list returns[IntArrayList ret=null]
	{ int n; ret = new IntArrayList(); }
    :   n=node  { ret.add(n); }
        ( COMMA n=node	{ ret.add(n); } )*
    ;

node returns[int node=Graph.NIL]
    :   n:ATOM	( port )?
    { node = findVertex(getText(n)); }
    ;
    
port
    :	port_location
    |	port_angle ( port_location )?
    ;

port_location
    :	COLON ( ATOM | LPAR ATOM COMMA ATOM RPAR )
    ;

port_angle
    :	"@" ATOM
    ;

attr_list returns [ ArrayList ret=null]
	{ ret = new ArrayList(); }
    :   ( LBR attr_defs[ret] RBR )*
    ;

attr_defs [ArrayList arr]
    :   ( ( COMMA )? attr_item[arr] )*
    ;

attr_item [ArrayList arr]
    :   a:ATOM EQUAL v:ATOM	
    {
    	arr.add(getText(a));
    	arr.add(getText(v));
    }
    ;

graph_attr_defs
	:	attr_list_simple
	;

attr_list_simple
    :   a:ATOM EQUAL v:ATOM
    ;

subgraph returns[IntArrayList ret=null]
    :   ret=body
    |	"subgraph" ATOM ret=body
    ;

/**
 * Lexer for the DOT Graph format.
 *
 * @author Jean-Daniel Fekete
 */
class DOTLexer extends Lexer;
options {
	exportVocab=DOT;
	k=4;
	charVocabulary = '\3'..'\377';
	testLiterals = false;
}

ATOM
options {
	testLiterals = true;
	paraphrase = "an identifier";
}
    :   ( 'a'..'z' | 'A'..'Z' | '_' | '.' | '0'..'9' )+
    |	( '-' | '+' ) ('.' | '0'..'9' )+
    |   '"'! (ESC|~'"')* '"'!
    |   '\''! (ESC|~'\'')* '\''!
    |    '<'! ( BALANCED )* '>'!
    ;
    
protected BALANCED
    :	'<' BALANCED '>'
    |	( ~('<' | '>') )+
    ;    
    
WS	:	(' '
	|	'\t'
	|	'\n'	{newline();}
	|	'\r')
		{ _ttype = Token.SKIP; }
	;

protected
ESC
    :   '\\'
		(	'n'
		|	'N'
		|	'r'
		|	't'
		|	'b'
		|	'f'
		|	'l'
		|	'"'
		|	'\n'	{newline();}
		|	'\r'
		|	'\''
		|	'\\'
		)
    ;
SL_COMMENT
	: 	"//"	(~'\n')* '\n'
		{ _ttype = Token.SKIP; newline(); }
	;
	
ML_COMMENT
	:	"/*"
		(	{ LA(2)!='/' }? '*'
		|	'\n' { newline(); }
		|	~('*'|'\n')
		)*
		"*/"
			{ $setType(Token.SKIP); }
	;
D_EDGE_OP
    :   "->"
    ;

ND_EDGE_OP
    :   "--"
    ;

SEMI
    :   ';'
    ;

COMMA
    :   ','
    ;

LCUR
    :   '{'
    ;

RCUR
    :   '}'
    ;

LBR
    :   '['
    ;

RBR
    :   ']'
    ;

LPAR
    :   '('
    ;

RPAR
    :   ')'
    ;

EQUAL
    :   '='
    ;

COLON
    :   ':'
    ;
