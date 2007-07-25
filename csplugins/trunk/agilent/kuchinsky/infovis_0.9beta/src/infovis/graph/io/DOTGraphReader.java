/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.graph.io;

import infovis.Graph;
import infovis.io.WrongFormatException;

import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;

/**
 * Reader class for the DOT file format.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.11 $
 * 
 * @infovis.factory GraphReaderFactory dot
 */
public class DOTGraphReader extends AbstractGraphReader {
    private Logger      logger = Logger.getLogger(DOTGraphReader.class);
    protected DOTLexer  lexer;
    protected DOTParser parser;
    protected String    attributePrefix;

    /**
     * Constructor.
     * @param in the input stream
     * @param name the name
     * @param graph the graph
     */
    public DOTGraphReader(InputStream in, String name, Graph graph) {
        super(in, name, graph);
    }

    /**
     * {@inheritDoc}
     */
    public Rectangle2D.Float getBbox() {
        return parser.getBbox();
    }

    /**
     * Returns the attribute prefix.
     * @return the attribute prefix.
     */
    public String getAttributePrefix() {
        return attributePrefix;
    }

    /**
     * Sets the attribute prefix.
     * @param prefix the prefix.
     */
    public void setAttributePrefix(String prefix) {
        attributePrefix = prefix;
    }

    /**
     * {@inheritDoc}
     */
    public boolean load() throws WrongFormatException {
        lexer = new DOTLexer(getIn());
        parser = new DOTParser(lexer);
        parser.setGraphReader(this);
        if (attributePrefix != null) {
            parser.attributePrefix = attributePrefix;
        }
        try {
            parser.graph();
        } catch (Exception e) {
            logger.error("Cannot parse file " + getName(), e);
            return false;
        } finally {
            try {
                getIn().close();
            } catch (IOException e) {
                logger.error("Error closing file " + getName(), e);
            }
        }
        return true;
    }

}
