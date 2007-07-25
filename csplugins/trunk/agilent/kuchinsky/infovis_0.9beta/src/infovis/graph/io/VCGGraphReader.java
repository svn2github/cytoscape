/*****************************************************************************
 * Copyright (C) 2003 Jean-Daniel Fekete and INRIA, France                   *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the QPL Software License    *
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
 * Class VCGGraphReader
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.4 $
 * 
 * @infovis.factory GraphReaderFactory vcg
 */
public class VCGGraphReader extends AbstractGraphReader {
    private Logger logger = Logger.getLogger(VCGGraphReader.class);
    protected VCGLexer lexer;
    protected VCGParser parser;
    
    public VCGGraphReader(
        InputStream in,
        String name,
        Graph graph) {
        super(in, name, graph);
        this.graph = graph;
    }
    
    public Rectangle2D.Float getBbox() {
        return parser.getBbox();
    }

    public boolean load() throws WrongFormatException {
        lexer = new VCGLexer(getIn());
        lexer.setFilename(getName());
        parser = new VCGParser(lexer);
        parser.setFilename(getName());
        parser.setGraphReader(this);
        try {
            parser.graph();
        }
        catch (Exception e) {
            logger.error("Parsing error reading VCG Graph", e);
            return false;
        }
        finally {
            try {
                close();
            }
            catch (IOException e) {
                logger.error("Error closing file "+getName(), e);
            }
        }
        return true;
    }

}
