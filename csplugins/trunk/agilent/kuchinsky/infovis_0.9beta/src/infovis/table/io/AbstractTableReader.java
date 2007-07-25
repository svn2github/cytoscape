/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.table.io;

import infovis.Table;
import infovis.io.AbstractReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;

/**
 * Abstract class for loading a table from a textual file.
 * 
 * @version $Revision: 1.6 $
 * @author Jean-Daniel Fekete
 */
public abstract class AbstractTableReader extends AbstractReader {
    protected Table        table;
    protected StringBuffer buffer;
    private BufferedReader bin;
    private boolean        eof;
    private char           commentChar = (char) -1;

    /**
     * Creates a new AbstractTableReader object.
     * 
     * @param in
     *            the BufferedReader
     * @param name
     *            a name.
     * @param table
     *            the table.
     */
    public AbstractTableReader(InputStream in, String name, Table table) {
        super(in, name);
        this.table = table;
        this.buffer = new StringBuffer();
    }

    /**
     * {@inheritDoc}
     */
    public BufferedReader getBufferedReader() {
        if (bin == null) {
            bin = super.getBufferedReader();
        }
        return bin;
    }
    
    /**
     * {@inheritDoc}
     */
    public InputStream getIn() {
        if (bin != null) {
            throw new RuntimeException("Buffered reader already in use");
        }
        return super.getIn();
    }

    /**
     * {@inheritDoc}
     */
    public void close() throws IOException {
        if (bin != null) {
            bin.close();
            bin = null;
        }
        super.close();
    }
    
    /**
     * Read a character.
     * 
     * @return a character.
     * 
     * @throws IOException
     *             pass it.
     */
    public final int read() throws IOException {
        int c = getBufferedReader().read();

        eof = (c == -1);
        return c;
    }

    /**
     * Skips the rest of the line.
     * 
     * @throws IOException
     *             pass the IOException
     */
    public void skipToEol() throws IOException {
        while (!eof && read() != '\n')
            ;
    }

    /**
     * Ignores all comments line plus one.
     * 
     * @throws IOException
     *             pass it.
     */
    public void ignoreLine() throws IOException {
        while (read() == commentChar)
            skipToEol();

        skipToEol();
    }

    /**
     * Returns a line.
     * 
     * @return a line.
     * 
     * @throws IOException pass it.
     */
    public String readLine() throws IOException {
        String ret;
        do {
            ret = getBufferedReader().readLine();
        } 
        while (ret != null 
                && ((ret.length() == 0) || (ret.charAt(0) == '#')));

        return ret;
    }

    /**
     * Read an integer and return it.
     * 
     * @return an integer.
     * 
     * @throws IOException
     *             pass it.
     */
    public int readInt() throws IOException {
        String ret;

        do {
            ret = getBufferedReader().readLine();
        } while ((ret.length() == 0) || (ret.charAt(0) == '#'));

        return Integer.parseInt(ret);
    }

    /**
     * Read a field quoted with a specified quote character.
     * 
     * @param quote
     *            the quote character,
     * 
     * @return true if it ends the line.
     * 
     * @throws IOException
     *             pass exception from BufferedReader.
     */
    public boolean readQuoted(char quote) throws IOException {
        buffer.setLength(0);
        int c;

        while (true) {
            for (c = read(); c != quote; c = read()) {
                buffer.append((char) c);
            }
            c = read();
            if (c == quote) {
                buffer.append(quote);
            }
            else {
                break;
            }
        }
        return c == '\n' || c == -1;
    }

    /**
     * Returns the table.
     * 
     * @return DefaultTable
     */
    public Table getTable() {
        return table;
    }

    /**
     * Sets the table.
     * 
     * @param table
     *            The table to set
     */
    public void setTable(Table table) {
        this.table = table;
    }

    /**
     * Returns the buffer.
     * 
     * @return StringBuffer
     */
    public StringBuffer getBuffer() {
        return buffer;
    }

    /**
     * Sets the buffer.
     * 
     * @param buffer
     *            The buffer to set
     */
    public void setBuffer(StringBuffer buffer) {
        this.buffer = buffer;
    }

    /**
     * Returns a string from the current buffer content.
     * 
     * @return a string from the current buffer content.
     */
    public String getField() {
        return buffer.toString();
    }

    /**
     * Returns the eof.
     * 
     * @return boolean
     */
    public boolean isEof() {
        return eof;
    }

    /**
     * Sets the eof.
     * 
     * @param eof
     *            The eof to set
     */
    public void setEof(boolean eof) {
        this.eof = eof;
    }

    /**
     * Returns the commentChar.
     * 
     * @return char
     */
    public char getCommentChar() {
        return commentChar;
    }

    /**
     * Sets the commentChar.
     * 
     * @param commentChar
     *            The commentChar to set
     */
    public void setCommentChar(char commentChar) {
        this.commentChar = commentChar;
    }
}