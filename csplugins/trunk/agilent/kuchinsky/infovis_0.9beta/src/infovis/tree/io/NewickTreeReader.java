/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.tree.io;

import infovis.Tree;

import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;


/**
 * Read Trees in Newick format, usually for phylogenetic trees.
 *
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.16 $
 * 
 * @infovis.factory TreeReaderFactory nh
 */
public class NewickTreeReader extends AbstractTreeReader {
    private static Logger logger = Logger.getLogger(NewickTreeReader.class);
    /**
     * Constructor for NewickTreeReader.
     * @param in
     * @param name
     * @param tree
     */
    public NewickTreeReader(InputStream in, String name, Tree tree) {
        super(in, name, tree);
    }

    /**
     * @see infovis.io.AbstractReader#load()
     */
    public boolean load() {
        NewickLexer  lexer = new NewickLexer(getIn());
        NewickParser parser = new NewickParser(lexer);
        parser.setTree(tree);
        parser.setName(getName());
        try {
            parser.tree();
        } catch (Exception e) {
            logger.error("Syntax error reading "+getName(), e);
            return false;
        } finally {
            try {
                close();
            } catch (IOException e) {
                logger.error("Error closing file "+getName(), e);
            }
        }
        return true;
    }

}
