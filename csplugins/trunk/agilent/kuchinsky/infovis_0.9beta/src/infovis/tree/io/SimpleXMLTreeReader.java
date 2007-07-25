/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/

package infovis.tree.io;

import infovis.Tree;
import infovis.column.StringColumn;
import infovis.io.AbstractXMLReader;
import infovis.utils.IntStack;

import java.io.InputStream;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * Simple Reader for an XML tree.  Reads the tag names.
 *
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.12 $
 */
public class SimpleXMLTreeReader extends AbstractXMLReader {
    Tree tree;
    IntStack parent;
    StringColumn qname;
	
    /**
     * Constructor for SimpleXMLTreeReader.
     *
     * @param in the BufferedReader.
     * @param name the file name.
     * @param tree the Tree.
     */
    public SimpleXMLTreeReader(InputStream in, String name, Tree tree) {
	super(in, name);
	this.tree = tree;
	parent = new IntStack();
    }

    /**
     * @see org.xml.sax.ContentHandler#endElement(String, String, String)
     */
    public void endElement(String namespaceURI, String localName, String qName)
	throws SAXException {
	parent.pop();
    }

    /**
     * @see org.xml.sax.ContentHandler#startDocument()
     */
    public void startDocument() throws SAXException {
	parent.clear();
		
    }

    public int createNode() {
        return parent.isEmpty() ? Tree.ROOT : tree.addNode(parent.top());
    }

    /**
     * @see org.xml.sax.ContentHandler#startElement(String, String, String, Attributes)
     */
    public void startElement(
			     String namespaceURI,
			     String localName,
			     String qName,
			     Attributes atts)
	throws SAXException {
	if (this.qname == null)
	    this.qname = StringColumn.findColumn(tree, "qname");
        
	int node = createNode();
	qname.set(node, qName);
	parent.push(node);
    }


}
