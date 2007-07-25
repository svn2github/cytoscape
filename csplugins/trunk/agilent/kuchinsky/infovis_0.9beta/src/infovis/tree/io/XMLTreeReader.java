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

import java.io.InputStream;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * Tree Reader for the treemal DTD.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.15 $
 * 
 * @infovis.factory TreeReaderFactory xml
 */
public class XMLTreeReader extends SimpleXMLTreeReader {
    protected boolean inTree;
    protected boolean inDeclarations;

    /**
     * Constructor for XMLTreeReader.
     * 
     * @param in
     *            the <code>BufferedReader</in>
     * @param name a name,
     * @param tree the Tree.
     */
    public XMLTreeReader(InputStream in, String name, Tree tree) {
        super(in, name, tree);
    }

    /**
     * @see org.xml.sax.ContentHandler#startElement(String, String, String,
     *      Attributes)
     */
    public void startElement(
            String namespaceURI,
            String localName,
            String qName,
            Attributes atts) throws SAXException {
        if (firstTag) {
            if (qName.equals("tree"))
                inTree = true;
            else
                throw new RuntimeException("Expected a tree toplevel element");
            firstTag = false;
        }
        else if (qName.equals("declarations") && inTree)
            inDeclarations = true;
        else if (qName.equals("attributeDecl") && inDeclarations)
            declareAttribute(atts.getValue("name"), atts.getValue("type"), atts
                    .getValue("control"));
        else if (qName.equals("branch") && inTree && !inDeclarations)
            addBranch(atts);
        else if (qName.equals("leaf") && inTree && !inDeclarations)
            addLeaf(atts);
        else if (qName.equals("attribute") && inTree && !inDeclarations)
            addAttribute(atts);
    }

    /**
     * @see org.xml.sax.ContentHandler#endElement(String, String, String)
     */
    public void endElement(String namespaceURI, String localName, String qName)
            throws SAXException {
        if (qName.equals("tree") && inTree)
            inTree = false;
        else if (qName.equals("declarations") && inTree)
            inDeclarations = false;
        else if ((qName.equals("branch") || qName.equals("leaf")) && inTree
                && !inDeclarations) {
            // The super class just pops the current node out of the stack.
            super.endElement(namespaceURI, localName, qName);
        }
    }

    protected void declareAttribute(String name, String type, String control) {
        Column c = tree.getColumn(name);
        if (c == null) {
            c = createColumn(type, name);
            tree.addColumn(c);
        }
//        else {
//            if (c.getValueClass() != typeNamed(type))
//                throw new ClassCastException("column " + name
//                        + " already exists with an incompatible type");
//        }

    }

    protected void addBranch(Attributes atts) {
        int node = createNode();
        parent.push(node);
    }

    protected void addLeaf(Attributes atts) {
        addBranch(atts);
    }

    protected void addAttribute(Attributes atts) {
        Column c = tree.getColumn(atts.getValue("name"));
        if (c == null)
            throw new RuntimeException("invalid column named "
                    + atts.getValue("name"));
        c.setValueOrNullAt(parent.top(), atts.getValue("value"));
    }
}
