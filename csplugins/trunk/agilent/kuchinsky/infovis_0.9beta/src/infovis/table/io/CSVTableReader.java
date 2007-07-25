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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;

import org.apache.log4j.Logger;

/**
 * Read an Excel CSV format into a table.
 * 
 * @version $Revision: 1.27 $
 * @author Jean-Daniel Fekete
 * 
 * @infovis.factory TableReaderFactory csv
 */
public class CSVTableReader extends AbstractTableReader {
    /** Field separator. */
    private char                separator         = ';';
    /** Number of lines to skip at the beginning of the file. */
    private int                 skipLines;
    /** True if first line is labels. */
    private boolean             labelLinePresent  = true;
    /** True if types are declared. */
    private boolean             typeLinePresent   = true;
    /** DefaultTable of column labels */
    protected ArrayList         labels            = new ArrayList();
    /** True is considering quotes */
    private boolean             consideringQuotes = true;
    protected transient boolean emptyField        = false;

    private static final Logger log               = Logger
                                                          .getLogger(CSVTableReader.class);

    /**
     * Constructor.
     * 
     * @param in
     *            the input stream
     * @param table
     *            the table
     */
    public CSVTableReader(InputStream in, Table table) {
        this(in, "CVS", table);
        setCommentChar('#');
    }

    /**
     * Constructor.
     * 
     * @param in
     *            the input stream
     * @param name
     *            the name
     * @param table
     *            the table
     */
    public CSVTableReader(InputStream in, String name, Table table) {
        super(in, name, table);
    }

    /**
     * Returns the column separator character.
     * 
     * @return char the column separator character.
     */
    public char getSeparator() {
        return separator;
    }

    /**
     * Sets the column separator character.
     * 
     * @param separator
     *            the column separator character. The separator to set
     */
    public void setSeparator(char separator) {
        this.separator = separator;
    }

    /**
     * Returns <code>true</code> if the first line contains labels.
     * 
     * @return <code>true</code> if the first line contains labels.
     */
    public boolean isLabelLinePresent() {
        return labelLinePresent;
    }

    /**
     * Sets whether the first line contains labels.
     * 
     * @param labelLinePresent
     *            <code>true</code> the first line contains labels.
     */
    public void setLabelLinePresent(boolean labelLinePresent) {
        this.labelLinePresent = labelLinePresent;
    }

    /**
     * Returns <code>true</code> if a line containing the types should be
     * read.
     * 
     * @return <code>true</code> if a line containing the types should be
     *         read.
     */
    public boolean isTypeLinePresent() {
        return typeLinePresent;
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

    /**
     * Returns the number of lines to skip at the begining of the file.
     * 
     * @return the number of lines to skip at the begining of the file.
     */
    public int getSkipLines() {
        return skipLines;
    }

    /**
     * Sets the number of lines to skip at the begining of the file.
     * 
     * @param skipLines
     *            the number of lines to skip at the begining of the file.
     */
    public void setSkipLines(int skipLines) {
        this.skipLines = skipLines;
    }

    /**
     * Read the next field, storing it in buffer and returns false if it ends
     * the line.
     * 
     * @param col
     *            the column
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
            emptyField = false;
            return readQuoted('"');
        }
        else if (consideringQuotes && c == '\'') {
            emptyField = false;
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
            len--;
        }
        emptyField = len==0;
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
     * Returns the label at the specified index.
     * 
     * @param index
     *            the index
     * 
     * @return the label at the specified index.
     */
    public String getLabelAt(int index) {
        while (labels.size() <= index) {
            labels.add(defaultFieldName(labels.size()));
        }

        return (String) labels.get(index);
    }

    /**
     * Adds a label.
     * 
     * @param name
     *            the label
     */
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
     * Adds the value of specified column.
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
            log.error("While loading a CSV file", e);
            return false;
        } catch (IOException e) {
            log.error("While loading a CSV file", e);
            return false;
        } finally {
            try {
                close();
            } catch (IOException e) {
                log.error("Closing a CSV file", e);
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
                String typeName = getField();
                if (typeName == null) {
                    typeName = "";
                }
                else {
                    typeName.trim();
                }

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
    
    /**
     * {@inheritDoc}
     */
    public String getField() {
        if (emptyField) return null;
        return super.getField();
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

    /**
     * Loads the specified file in the specified table.
     * 
     * @param file
     *            the file
     * @param t
     *            the table
     * @return true if the loading was successful.
     */
    public static boolean load(File file, Table t) {
        try {
            CSVTableReader loader = new CSVTableReader(
                    new FileInputStream(file),
                    t);
            return loader.load();
        } catch (FileNotFoundException e) {
            return false;
        }
    }
}
