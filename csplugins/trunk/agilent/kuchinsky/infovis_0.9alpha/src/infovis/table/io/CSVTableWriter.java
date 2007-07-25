/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/

package infovis.table.io;

import infovis.Column;
import infovis.Table;
import infovis.io.AbstractWriter;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;


/**
 * Writer in Excel CVS format for tables.
 * 
 * @version $Revision: 1.8 $
 * @author Jean-Daniel Fekete
 */
public class CSVTableWriter extends AbstractWriter {
    char separator;
    /** True if first line is labels. */
    boolean labelLinePresent;
    /** True if types are declared. */
    boolean typeLinePresent;

    /**
     * Constructor for CSVTableWriter.
     * 
     * @param out
     * @param table
     */
    public CSVTableWriter(Writer out, Table table) {
	super(out, table);
	separator = '\t';
    }


    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean write() {
	int col;

	ArrayList labels = getColumnLabels();
	if (labels == null) {
	    addAllColumnLabels();
	    labels = getColumnLabels();
	}

	try {
	    if (labelLinePresent) {
		for (col = 0; col < labels.size(); col++) {
		    if (col != 0)
			write(separator);
		    write(getColumnLabelAt(col));
					
		}
		write('\n');
	    }
	    if (typeLinePresent) {
		for (col = 0; col < labels.size(); col++) {
		    if (col != 0)
			write(separator);
		    Column c = table.getColumn(getColumnLabelAt(col));
		    write(namedType(c.getValueClass()));
		}
		write('\n');
	    }
			
	    int nrow = table.getRowCount();
	    for (int row = 0; row < nrow; row++) {
		for (col = 0; col < labels.size(); col++) {
		    if (col != 0)
			write(separator);
		    Column c = table.getColumn(getColumnLabelAt(col));
		    write(c.getValueAt(row));
		}
		write('\n');
	    }
	    out.flush();
	}
	catch (IOException e) {
	    return false;
	}
		
	return true;
    }

    /**
     * Returns the separator.
     * 
     * @return char
     */
    public char getSeparator() {
	return separator;
    }

    /**
     * Sets the separator.
     * 
     * @param separator The separator to set
     */
    public void setSeparator(char separator) {
	this.separator = separator;
    }
    /**
     * Returns the labelLinePresent.
     * @return boolean
     */
    public boolean isLabelLinePresent() {
	return labelLinePresent;
    }

    /**
     * Returns the typeLinePresent.
     * @return boolean
     */
    public boolean isTypeLinePresent() {
	return typeLinePresent;
    }

    /**
     * Sets the labelLinePresent.
     * @param labelLinePresent The labelLinePresent to set
     */
    public void setLabelLinePresent(boolean labelLinePresent) {
	this.labelLinePresent = labelLinePresent;
    }

    /**
     * Sets the typeLinePresent.
     * @param typeLinePresent The typeLinePresent to set
     */
    public void setTypeLinePresent(boolean typeLinePresent) {
	this.typeLinePresent = typeLinePresent;
    }

}
