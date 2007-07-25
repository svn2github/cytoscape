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
import java.io.OutputStream;
import java.util.ArrayList;

import org.apache.log4j.Logger;

/**
 * Write an Excel CVS format a a table.
 * 
 * @version $Revision: 1.13 $
 * @author Jean-Daniel Fekete
 * 
 * @infovis.factory TableWriterFactory csv
 */
public class CSVTableWriter extends AbstractWriter {
    /** Field separator. */
    private char                separator        = ';';
    /** True if first line is labels. */
    private boolean             labelLinePresent = true;
    /** True if types are declared. */
    private boolean             typeLinePresent  = true;

    private static final Logger log              = Logger
                                                         .getLogger(CSVTableWriter.class);

    /**
     * Constructor.
     * 
     * @param out the output stream
     * @param name the name
     * @param table the table
     */
    public CSVTableWriter(OutputStream out, String name, Table table) {
        super(out, name, table);
    }

    /**
     * Constructor.
     * 
     * @param out the output stream
     * @param table the table
     */
    public CSVTableWriter(OutputStream out, Table table) {
        this(out, table.getName(), table);
    }

    /**
     * Returns a quoted string from a specified string.
     * @param s the string
     * @return a quoted string from a specified string.
     */
    public String quoteString(String s) {
        if (s != null && (s.indexOf(separator) != -1 || s.indexOf('"') != -1)) {
            StringBuffer sb = new StringBuffer(s.length() + 10);
            sb.append('"');
            int old = 0;
            int i;
            for (i = s.indexOf('"'); i != -1; i = s.indexOf('"', old + 1)) {
                sb.append(s.substring(old, i));
                sb.append('"');
                old = i;
            }
            sb.append(s.substring(old, s.length()));
            sb.append('"');
            s = sb.toString();
        }
        return s;
    }

    /**
     * {@inheritDoc}
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
                    write(quoteString(getColumnLabelAt(col)));

                }
                write('\n');
            }
            if (typeLinePresent) {
                for (col = 0; col < labels.size(); col++) {
                    if (col != 0)
                        write(separator);
                    Column c = table.getColumn(getColumnLabelAt(col));
                    write(quoteString(namedType(c)));
                }
                write('\n');
            }

            int nrow = table.getRowCount();
            for (int row = 0; row < nrow; row++) {
                for (col = 0; col < labels.size(); col++) {
                    if (col != 0)
                        write(separator);
                    Column c = table.getColumn(getColumnLabelAt(col));
                    write(quoteString(c.getValueAt(row)));
                }
                write('\n');
            }
            flush();
        } catch (IOException e) {
            log.error("Error writing CSV table", e);
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
     * @param separator
     *            The separator to set
     */
    public void setSeparator(char separator) {
        this.separator = separator;
    }

    /**
     * Returns the labelLinePresent.
     * 
     * @return boolean
     */
    public boolean isLabelLinePresent() {
        return labelLinePresent;
    }

    /**
     * Returns the typeLinePresent.
     * 
     * @return boolean
     */
    public boolean isTypeLinePresent() {
        return typeLinePresent;
    }

    /**
     * Sets the labelLinePresent.
     * 
     * @param labelLinePresent
     *            The labelLinePresent to set
     */
    public void setLabelLinePresent(boolean labelLinePresent) {
        this.labelLinePresent = labelLinePresent;
    }

    /**
     * Sets the typeLinePresent.
     * 
     * @param typeLinePresent
     *            The typeLinePresent to set
     */
    public void setTypeLinePresent(boolean typeLinePresent) {
        this.typeLinePresent = typeLinePresent;
    }

}
