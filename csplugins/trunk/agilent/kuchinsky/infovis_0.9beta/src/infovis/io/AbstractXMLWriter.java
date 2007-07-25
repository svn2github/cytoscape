/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/

package infovis.io;

import infovis.Column;
import infovis.Table;

import java.io.OutputStream;
import java.util.ArrayList;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import com.megginson.sax.XMLWriter;

/**
 * Abstract writer for XML format.
 *
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.14 $
 */
public abstract class AbstractXMLWriter extends AbstractWriter {
    static char[] LF = {'\n', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '};
    protected int depth;
    protected XMLWriter writer;
    protected boolean identing = true;
	
    /**
     * Constructor for an AbstractXMLWriter
     *
     * @param out the Writer
     * @param table the Table.
     */
    protected AbstractXMLWriter(OutputStream out, String name, Table table) {
        super(out, name, table);
        writer = new XMLWriter(getWriter());
    }
    
    protected AbstractXMLWriter(OutputStream out, Table table) {
        this(out, table.getName(), table);
    }

    /**
     * Writes the attributes associated with a row to a specified XMLWriter.
     *
     * @param row the row.
     * @param writes the XMLWriter.
     *
     * @exception SAXException passed from the XMLWriter
     */
    protected void writeAttributes(int row, XMLWriter writer)
	throws SAXException {
	ArrayList labels = getColumnLabels();
	AttributesImpl atts = new AttributesImpl();
	atts.addAttribute("", "name", "", "CDATA", "");
	atts.addAttribute("", "value", "", "CDATA", "");

	depth++;
	for (int col = 0; col < labels.size(); col++) {
	    String label = getColumnLabelAt(col);
	    Column c = table.getColumn(label);
	    if (! c.isValueUndefined(row)) {
		atts.setValue(0, label);
		atts.setValue(1, c.getValueAt(row));
		indent(writer);
		writer.emptyElement("", "attribute", "", atts);
	    }
	}
	depth--;
    }

    public void indent(XMLWriter writer) {
	if (! isIdenting())
	    return;
	int len = Math.min(depth+1, LF.length);
	try {
	    writer.ignorableWhitespace(LF, 0, len);
	}	
	catch(SAXException e) {
        ; // ignore
	}
    }

    /**
     * Returns the identing.
     * @return boolean
     */
    public boolean isIdenting() {
	return identing;
    }

    /**
     * Sets the identing.
     * @param identing The identing to set
     */
    public void setIdenting(boolean identing) {
	this.identing = identing;
    }

}
