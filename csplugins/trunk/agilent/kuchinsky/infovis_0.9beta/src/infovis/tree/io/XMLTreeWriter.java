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
import infovis.io.AbstractXMLWriter;
import infovis.tree.DepthFirst;

import java.io.OutputStream;
import java.util.ArrayList;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import com.megginson.sax.XMLWriter;

/**
 * Writer according to the treeml DTD.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.17 $
 */
public class XMLTreeWriter extends AbstractXMLWriter {
    protected Tree tree;

    /**
     * Constructor for XMLTreeWriter.
     * 
     * @param out
     * @param tree
     */
    public XMLTreeWriter(OutputStream out, Tree tree) {
        super(out, tree);
        this.tree = tree;
    }

    /**
     * @see infovis.io.AbstractWriter#write()
     */
    public boolean write() {
        int col;

        ArrayList labels = getColumnLabels();
        if (labels == null) {
            addAllColumnLabels();
            labels = getColumnLabels();
        }

        if (labels.size() == 0)
            return false;
        final XMLWriter writer = new XMLWriter(getWriter());
        writer.setStandalone(false);
        depth = 0;

        try {
            writer.startDocument();
            writer.flush();
            write("<!DOCTYPE tree SYSTEM \"treeml.dtd\">\n");
            writer.startElement("tree");
            depth++;
            indent(writer);
            writer.startElement("declarations");
            depth++;
            for (col = 0; col < labels.size(); col++) {
                String label = getColumnLabelAt(col);
                AttributesImpl atts = new AttributesImpl();

                Column c = tree.getColumn(label);
                indent(writer);
                atts.addAttribute("", "name", "", "CDATA", label);
                atts.addAttribute("", "type", "", "CDATA", namedType(c));
                writer.emptyElement("", "attributeDecl", "", atts);
            }
            depth--;
            indent(writer);
            writer.endElement("declarations");

            DepthFirst.visit(tree, new DepthFirst.Visitor() {
                public boolean preorder(int node) {
                    // AttributesImpl atts = createAttributes(node);
                    indent(writer);

                    try {
                        if (tree.isLeaf(node)) {
                            writer.startElement("leaf");
                            writeAttributes(node, writer);
                            writer.endElement("leaf");
                            return false;
                        }
                        else {
                            depth++;
                            writer.startElement("branch");
                            writeAttributes(node, writer);
                            return true;
                        }
                    } catch (SAXException e) {
                        return false;
                    }
                }

                public void postorder(int node) {
                    depth--;
                    indent(writer);
                    if (!tree.isLeaf(node)) {
                        try {
                            writer.endElement("branch");
                        } catch (SAXException e) {
                            ; // ignore
                        }
                    }
                }

            });
            depth--;
            indent(writer);
            writer.endElement("tree");
            writer.endDocument();
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
