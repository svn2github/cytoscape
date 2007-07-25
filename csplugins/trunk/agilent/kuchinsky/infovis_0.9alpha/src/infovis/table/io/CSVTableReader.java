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

import java.io.*;
import java.text.ParseException;
import java.util.ArrayList;

/**
 * Read an Excel CSV format into a table
 * 
 * @version $Revision: 1.23 $
 * @author Jean-Daniel Fekete
 * 
 * @infovis.factory TableReaderFactory csv
 */
public class CSVTableReader extends AbstractTableReader {
    /** Field separator. */
    protected char              separator         = ';';
    /** Number of lines to skip at the beginning of the file. */
    protected int               skipLines;
    /** True if first line is labels. */
    protected boolean           labelLinePresent  = true;
    /** True if types are declared. */
    protected boolean           typeLinePresent   = true;
    /** DefaultTable of column labels */
    protected ArrayList         labels            = new ArrayList();
    /** True is considering quotes */
    protected boolean           consideringQuotes = true;
    
    /**
     * Constructor for CSVTableReader.
     * 
     * @param in
     * @param table
     */
    public CSVTableReader(BufferedReader in, Table table) {
        this(in, "CVS", table);
        setCommentChar('#');
    }

    public CSVTableReader(BufferedReader in, String name, Table table) {
        super(in, name, table);
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
     * Read the next field, storing it in buffer and returns false if it ends
     * the line.
     * 
     * @return true if it ends the line.
     * 
     * @throws IOException
     *             pass exception from BufferedReader
     */
    public boolean nextField(int col) throws IOException {
        int c = read();

        if (consideringQuotes && col == 0 && c == '#') {
            do {
                skipToEol();
                c = read();
            } while (c == '#');
        }
        if (consideringQuotes && c == '"') {
            return readQuoted('"');
        }
        else if (consideringQuotes && c == '\'') {
            return readQuoted('\'');
        }

        buffer.setLength(0);

        while ((c != separator) && (c != '\n') && (c != -1)) {
            buffer.append((char) c);
            c = read();
        }
        int len = buffer.length();
        if (len > 0 && buffer.charAt(len - 1) == '\r') {
            buffer.setLength(len - 1);
        }

        return (c == '\n') || (c == -1);
    }

    /**
     * Returns a default field name for a specified index.
     * 
     * @param index
     *            the index.
     * 
     * @return a default field name for a specified index.
     */
    public String defaultFieldName(int index) {
        buffer.setLength(0);

        for (; index >= 0; index = (index / 26) - 1) {
            buffer.insert(0, (char) ((char) (index % 26) + 'A'));
        }

        return getField();
    }

    /**
     * DOCUMENT ME!
     * 
     * @param index
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public String getLabelAt(int index) {
        while (labels.size() <= index) {
            labels.add(defaultFieldName(labels.size()));
        }

        return (String) labels.get(index);
    }

    public void addLabel(String name) {
        labels.add(name);
    }

    /**
     * Returns the column at the specified index in the label table.
     * 
     * @param index
     *            the index
     * 
     * @return the column at the specified index in the label table or null if
     *         the index is invalid or no column of that name exist.
     */
    public Column getColumnAt(int index) {
        String label = getLabelAt(index);
        if (label == null)
            return null;
        return table.getColumn(label);
    }

    protected void disableNotify() {
        table.disableNotify();
    }

    protected void enableNotify() {
        table.enableNotify();
    }

    /**
     * DOCUMENT ME!
     * 
     * @param column
     *            DOCUMENT ME!
     * @param field
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     * 
     * @throws ParseException
     *             DOCUMENT ME!
     */
    public boolean addField(int column, String field) throws ParseException {
        String label = getLabelAt(column);
        Column col = table.getColumn(label);

        if (col == null) {
            String type = guessFieldType(field);
            col = createColumn(type, label);

            if (col == null) {
                throw new ParseException(
                        "couldn't guess the type of field at column " + column,
                        0);
            }

            table.addColumn(col);
        }

        col.addValueOrNull(field);

        return true;
    }

    /**
     * @see infovis.io.AbstractReader#load()
     */
    public boolean load() {
        try {
            for (int i = 0; i < skipLines; i++)
                skipToEol();

            readLabels();
            readTypes();

            readLines();
        } catch (ParseException e) {
            return false;
        } catch (IOException e) {
            return false;
        } finally {
            try {
                in.close();
            } catch (IOException e) {
            }
        }

        return true;
    }

    protected void readLines() throws IOException, ParseException {
        try {
            disableNotify();
            while (!isEof()) {
                int column = 0;

                for (boolean eol = nextField(column); true; eol = nextField(column)) {
                    if (eol && isEof())
                        break;
                    addField(column, getField());
                    column++;
                    if (eol)
                        break;
                }
            }
        } finally {
            enableNotify();
        }
    }
    
    protected void readTypes() throws IOException, ParseException {
        if (typeLinePresent) {
            int column = 0;
            for (boolean eol = nextField(column); true; eol = nextField(column)) {
                String typeName = getField().trim();
                
                if (typeName.length() != 0) {
                    String label = getLabelAt(column);
                    Column col = createColumn(typeName, label);
                    if (col == null) {
                        throw new ParseException(
                                "couldn't understand the type " + typeName
                                        + " of field at column " + column,
                                0);
                    }
                    table.addColumn(col);
                    column++;
                }
                if (eol)
                    break;
            }
        }
    }

    protected void readLabels() throws IOException {

        if (labelLinePresent) {
            int column = 0;
            for (boolean eol = nextField(column); true; eol = nextField(column)) {
                String label = getField();
                if (label.length() != 0) {
                    addLabel(label);
                    column++;
                }
                if (eol)
                    break;
            }
        }
    }

    public static boolean load(File file, Table t) {
        try {
            CSVTableReader loader = new CSVTableReader(new BufferedReader(
                    new FileReader(file)), t);
            return loader.load();
        } catch (FileNotFoundException e) {
            return false;
        }
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

    /**
     * Returns the consideringQuotes.
     * 
     * @return boolean
     */
    public boolean isConsideringQuotes() {
        return consideringQuotes;
    }

    /**
     * Sets the consideringQuotes.
     * 
     * @param consideringQuotes
     *            The consideringQuotes to set
     */
    public void setConsideringQuotes(boolean consideringQuotes) {
        this.consideringQuotes = consideringQuotes;
    }

}
