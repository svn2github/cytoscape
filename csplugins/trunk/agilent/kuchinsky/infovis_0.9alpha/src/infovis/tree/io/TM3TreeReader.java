/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.tree.io;

import infovis.Column;
import infovis.Tree;
import infovis.column.StringColumn;
import infovis.table.io.CSVTableReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.text.ParseException;

/**
 * Reader for the UMD Treemap TM3 Format.
 *
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.11 $
 * 
 * @infovis.factory TreeReaderFactory tm3
 */
public class TM3TreeReader extends CSVTableReader {
    protected Tree tree;
    protected StringColumn nameColumn;
	
    /**
     * Constructor for TM3TreeReader.
     * @param in
     * @param tree
     */
    public TM3TreeReader(BufferedReader in, Tree tree) {
	this(in, null, tree);
    }

    /**
     * Constructor for TM3TreeReader.
     * @param in
     * @param name
     * @param tree
     */
    public TM3TreeReader(BufferedReader in, String name, Tree tree) {
	super(in, name, tree);
	this.tree = tree;
	nameColumn = StringColumn.findColumn(tree, "name");
	setSeparator('\t');
	setLabelLinePresent(true);
	setTypeLinePresent(true);
    }
    
    /**
     * @see infovis.table.io.CSVTableReader#readLines()
     */
    protected void readLines() throws IOException, ParseException {
	try {
	    disableNotify();
	    while (!isEof()) {
		int column;
                int node = Tree.ROOT;
		boolean eol;
			
		for (column = 0; column < labels.size(); column++) {
		    eol = nextField(column);
					
		    Column col = tree.getColumn(getLabelAt(column));
		    if (col == null) {
			throw new ParseException("cannot get column "+column, 0);
		    }

		    if (eol) {
			if (column == 0)
			    return; // ++++ RETURN
			throw new ParseException("unexpected end of line at column "
                                                 + column, 0);
		    }
		    else if (column == 0) {
                        // assume first node is root (makes sense)
			node = tree.addNode(Tree.ROOT);
                    }
		    col.setValueOrNullAt(node, getField());
		}
                
		int parent = Tree.NIL;
		while (!nextField(column++)) {
		    String field = getField();
		    if (field.length() != 0) {
			parent = AbstractTreeReader.findNode(
                            field,
                            parent,
                            tree,
                            nameColumn);
		    }
		}
		// The last one is where the node should go.
		nameColumn.setExtend(node, getField());
                if (node != Tree.ROOT)
		  tree.reparent(node, parent);
	    }
	}
	finally {
	    enableNotify();
	}
    }


}
