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
 * Parser and Lexer for the VCG Graph format.
 *
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.7 $
 */
}

{
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
}	
class VCGParser extends Parser;
options {
	exportVocab=VCG;
	k = 2;
}

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
}

graph
	:	"graph" COLON LCUR (graph_entry)+ RCUR
	;
	
graph_entry
	:	graph_attribute
	|	node_defaults
	|	edge_defaults
	|	foldnode_defaults
	|	foldedge_defaults
	|	graph
	|	node
	|	edge
	|	nearedge
	|	bentnearedge
	|	backedge
	;

graph_attribute
	:	ID COLON value
	|	"title" COLON i:ID { graph.setName(getText(i)); }
	|	"x" COLON INTEGER
	|	"y" COLON INTEGER
	|	"loc" COLON LCUR "x" COLON INTEGER "y" COLON INTEGER RCUR
	|	"classname" INTEGER COLON ID
	|	"infoname" INTEGER  COLON ID
	|	"colorentry" INTEGER COLON INTEGER INTEGER INTEGER 
	|	"no_nearedges"
	;

value returns [ String ret=null]
	:	id:ID		{ ret = getText(id); }
	|	i:INTEGER	{ ret = getText(i); }
	|	f:FLOAT		{ ret = getText(f); }
	;

foldnode_defaults
	:	"foldnode." node_attribute
	;

foldedge_defaults
	:	"foldedge." edge_attribute
	;

node_defaults
	:	"node." node_attribute
	;

edge_defaults
	:	"edge." edge_attribute
	;

node
	:	"node" COLON LCUR
	{ currentNode = Graph.NIL; }
		(node_attribute)+
	{ currentNode = Graph.NIL; }
		RCUR
	;

edge
	:	"edge" COLON LCUR
	{ currentEdge = sourceNode = targetNode = Graph.NIL; }
		(edge_attribute)+
	{ currentEdge = sourceNode = targetNode = Graph.NIL; }
		RCUR
	;

nearedge
	:   "nearedge" COLON LCUR
	{ currentEdge = sourceNode = targetNode = Graph.NIL; }
			(edge_attribute)+
	{ currentEdge = sourceNode = targetNode = Graph.NIL; }
			RCUR
    	;

bentnearedge
	:   "bentnearedge" COLON LCUR
	{ currentEdge = sourceNode = targetNode = Graph.NIL; }
		(edge_attribute)+
	{ currentEdge = sourceNode = targetNode = Graph.NIL; }
		RCUR
	;

backedge
	:   "backedge" COLON LCUR
	{ currentEdge = sourceNode = targetNode = Graph.NIL; }
		(edge_attribute)+
	{ currentEdge = sourceNode = targetNode = Graph.NIL; }
		RCUR
	;

node_attribute
{ String v; }
	:	a:ID COLON v=value	{ addNodeAttribute(getText(a), v); }
	|	"title" COLON i:ID 	{ addNodeTitle(getText(i)); }
	|	"x" COLON x:INTEGER	{ addNodeX(getInteger(x)); }
	|	"y" COLON y:INTEGER 	{ addNodeY(getInteger(y)); }
	|	"loc" COLON LCUR "x" COLON xl:INTEGER "y" COLON yl:INTEGER RCUR
		{ addNodeX(getInteger(xl)); addNodeY(getInteger(yl)); }
	;

edge_attribute
{ String v; }
	:	a:ID COLON v=value	{ addEdgeAttribute(getText(a), v); }
	;

/**
 * Lexer for the VCG Graph format.
 *
 * @author Jean-Daniel Fekete
 */
class VCGLexer extends Lexer;
options {
	exportVocab=VCG;
	k=2;
	charVocabulary = '\3'..'\377';
	testLiterals = false;
}

ID
options {
	testLiterals = true;
	paraphrase = "a string value";
}
    :   ( 'a'..'z' | 'A'..'Z' | '_' ) ( 'a'..'z' | 'A'..'Z' | '_' | '.' | '0'..'9' )*
	|   '"'! (ESC|~'"')* '"'!
	|   '\''! (ESC|~'\'')* '\''!
    ;

protected
DIGIT
options {
  paraphrase = "a digit";
}
	:	'0'..'9'
	;

INTEGER
options {
  paraphrase = "an integer value";
}
	:    ('+' | '-')? (DIGIT)+                  // base-10 
             (  '.' (DIGIT)*                      	{$setType(FLOAT);}
	         (('e' | 'E') ('+' | '-')? (DIGIT)+)? 
	     |   ('e' | 'E') ('+' | '-')? (DIGIT)+   	{$setType(FLOAT);}
             )?
	;

FLOAT
options {
  paraphrase = "an floating point value";
}

	:    '.' (DIGIT)+ (('e' | 'E') ('+' | '-')? (DIGIT)+)?
     	;


WS	:	(' '
	|	'\t'
	|	'\n'	{newline();}
	|	'\r')
		{ _ttype = Token.SKIP; }
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



protected
ESC
    :   '\\' .
//		(	'n'
//		|	'N'
//		|	'r'
//		|	't'
//		|	'b'
//		|	'f'
//		|	'"'
//		|	'\n'
//		|	'\r'
//		|	'\''
//		|	'\\'
//		)
    ;

LCUR
    :   '{'
    ;

RCUR
    :   '}'
    ;

COLON
    :   ':'
    ;
	
