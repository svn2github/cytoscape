/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/

package infovis.io;

import infovis.Column;
import infovis.column.ColumnFactory;
import infovis.column.StringColumn;
import infovis.column.format.UTCDateFormat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Base class for all the readers.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.23 $
 */
public abstract class AbstractReader {
    private InputStream in;
    private String      name;
    private String      encoding;

    /**
     * Constructor for AbstractReader.
     * 
     * @param in
     *            the <code>InputStream</code> for input.
     * @param name
     *            the resource name.
     */
    public AbstractReader(InputStream in, String name) {
        this.in = in;
        this.name = name;
    }

    /**
     * Returns the input <code>InputStream</code>.
     * 
     * @return the input <code>InputStream</code>.
     */
    public InputStream getIn() {
        return in;
    }

    /**
     * Returns the resource name.
     * 
     * @return the resource name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the <code>InputStream</code>.
     * 
     * @param in
     *            the <code>InputStream</code>
     */
    public void setIn(InputStream in) {
        this.in = in;
    }

    /**
     * Closes the reader.
     * @throws IOException when the underlying stream does so
     */
    public void close() throws IOException {
        in.close();
        in = null;
    }

    /**
     * Sets the name.
     * 
     * @param name
     *            The name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Guess the field class from a value.
     * 
     * @param field
     *            the value.
     * 
     * @return A field class.
     */
    public static String guessFieldType(String field) {
        if ((field == null) || (field.length() == 0)) {
            return "string";
        }

        char c = field.charAt(0);

        if (Character.isDigit(c) || c == '-' || c == '+'
                || (c == '.' && Character.isDigit(field.charAt(1)))) {
            SimpleDateFormat date = new UTCDateFormat();
            try {
                date.parse(field);
                return "date";
            } catch (ParseException e) {
                ; // fall through
            }

            try {
                Float.parseFloat(field);
                return "float";
            } catch (NumberFormatException e) {
                ; // fall through
            }
        }

        return "string";
    }
    
    /**
     * Creates a column of the specified type and name.
     * @param type the type name
     * @param label the column name
     * @return a column of the specified type and name
     */
    public static Column createColumn(String type, String label) {
        Column col = ColumnFactory.createColumn(type, label);
        if (col == null) {
            col = new StringColumn(label);
        }
        return col;
    }

    /**
     * Returns a BufferedReader associated with this stream,
     * creating it if it does not exist yet.
     * @return a BufferedReader associated with this stream.
     */
    public BufferedReader getBufferedReader() {
        if (encoding == null) {
            return new BufferedReader(new InputStreamReader(in));
        }
        try {
            return new BufferedReader(new InputStreamReader(in, encoding));
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    /**
     * Main method for loading the file.
     * 
     * The loading may fail at any point, leaving the table in an indefinite
     * state if the methods returns false.
     * 
     * @return true if the file has been loaded without error, false otherwise.
     * @throws WrongFormatException if the format is not the one expected.
     */
    public abstract boolean load() throws WrongFormatException;

    /**
     * Returns the default encoding of this reader.
     * @return  the default encoding of this reader
     */
    public String getEncoding() {
        return encoding;
    }

    /**
     * Sets the default encoding of this reader.
     * @param encoding the default encoding of this reader.
     */
    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

}
