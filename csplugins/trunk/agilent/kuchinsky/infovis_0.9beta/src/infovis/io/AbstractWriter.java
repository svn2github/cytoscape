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
import infovis.column.ColumnFactory;
import infovis.metadata.IO;

import java.io.*;
import java.util.ArrayList;

/**
 * Abstract base class for Table writers.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.8 $
 */
public abstract class AbstractWriter {
    private OutputStream   out;
    private BufferedWriter wout;
    private String         encoding;
    protected Table        table;
    protected String       name;
    protected StringBuffer buffer;
    /** DefaultTable of column labels */
    private ArrayList      columnLabels;

    /**
     * Constructor for AbstractWriter
     * 
     * @param out
     *            the Writer
     * @param table
     *            the Table.
     */
    public AbstractWriter(OutputStream out, String name, Table table) {
        this.out = out;
        this.table = table;
        this.buffer = new StringBuffer();
    }

    public String getName() {
        return name;
    }

    /**
     * Returns a type name from a column.
     * 
     * @param col
     *            the column
     * @return a type name for creating a column of that class.
     */
    public static String namedType(Column col) {
        return ColumnFactory.getInstance().getTypeName(col);
    }

    /**
     * Adds the name of a Column to write to the list of written column.
     * 
     * @param name
     *            the Column name.
     */
    public void addColumnLabel(String name) {
        if (columnLabels == null) {
            columnLabels = new ArrayList();
        }

        columnLabels.add(name);
    }

    /**
     * Adds the name of all the non-internal Columns to the list of written
     * columns.
     */
    public void addAllColumnLabels() {
        int col;

        for (col = 0; col < table.getColumnCount(); col++) {
            Column c = table.getColumnAt(col);

            if (c.isInternal()
                    || c.getMetadata().getAttribute(IO.IO_TRANSIENT) == Boolean.TRUE) {
                continue;
            }

            addColumnLabel(c.getName());
        }
    }

    /**
     * Returns the name of the column to write at a specified index.
     * 
     * @param col
     *            the index.
     * 
     * @return the name of the column to write at a specified index.
     */
    public String getColumnLabelAt(int col) {
        return (String) columnLabels.get(col);
    }

    /**
     * Returns the ArrayList of column names to write.
     * 
     * @return the ArrayList of column names to write.
     */
    public ArrayList getColumnLabels() {
        return columnLabels;
    }

    public OutputStream getOut() {
        if (wout != null) {
            return null;
        }
        return out;
    }
    
    public BufferedWriter getWriter() {
        if (wout == null)
            try {
                if (encoding != null) {
                    wout = new BufferedWriter(new OutputStreamWriter(
                            out,
                            encoding));
                }
                else {
                    wout = new BufferedWriter(new OutputStreamWriter(out));
                }
            } catch (UnsupportedEncodingException e) {
                wout = new BufferedWriter(new OutputStreamWriter(out));
            }
        return wout;
    }

    /**
     * Write one character in the output writer.
     * 
     * @param c
     *            the character.
     */
    public final void write(char c) throws IOException {
        getWriter().write(c);
    }

    /**
     * Write a string in the output writer.
     * 
     * @param s
     *            the string.
     */
    public final void write(String s) throws IOException {
        if (s == null)
            return;
        else if (s.equals("")) {
            s = "\'\'";
        }
        getWriter().write(s);
    }

    /**
     * Write the StringBuffer in the output writer and clears it.
     */
    public void writeBuffer() throws IOException {
        getWriter().write(buffer.toString());
        buffer.setLength(0);
    }

    /**
     * Write a newline in the ouput writer.
     */
    public void writeln() throws IOException {
        writeBuffer();
        write('\n');
    }
    
    public void flush() throws IOException {
        getWriter().flush();
    }

    /**
     * Abstract method that performs the actual writing of data.
     */
    public abstract boolean write();

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }
}
