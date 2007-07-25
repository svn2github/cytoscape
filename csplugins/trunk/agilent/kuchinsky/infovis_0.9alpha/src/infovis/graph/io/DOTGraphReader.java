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
import java.io.BufferedReader;
import java.io.IOException;

import org.apache.log4j.Logger;

/**
 * Class DOTGraphReader
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.9 $
 * 
 * @infovis.factory GraphReaderFactory dot
 */
public class DOTGraphReader extends AbstractGraphReader {
    private Logger logger = Logger.getLogger(DOTGraphReader.class);
    protected DOTLexer lexer;
    protected DOTParser parser;
    protected String attributePrefix;

    public DOTGraphReader(
        BufferedReader in,
        String name,
        Graph graph) {
        super(in, name, graph);
    }
    
    public Rectangle2D.Float getBbox() {
        return parser.getBbox();
    }
    
    public String getAttributePrefix() {
        return attributePrefix;
    }
    
    public void setAttributePrefix(String prefix) {
        attributePrefix = prefix;
    }

    public boolean load() throws WrongFormatException {
        lexer = new DOTLexer(in);
        parser = new DOTParser(lexer);
        parser.setGraphReader(this);
        if (attributePrefix != null) {
            parser.attributePrefix = attributePrefix;
        }
        try {
            parser.graph();
        }
        catch (Exception e) {
            logger.error("Cannot parse file "+getName(), e);
            return false;
        }
        finally {
            try {
                in.close();
            }
            catch (IOException e) {
                logger.error("Error closing file "+getName(), e);
            }
        }
        return true;
    }

}
