/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/

package infovis.tree.io;

import infovis.Tree;
import infovis.io.AbstractWriter;

import java.io.Writer;

/**
 * Abstract Writer for Trees.
 *
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.7 $
 */
public abstract class AbstractTreeWriter extends AbstractWriter {
    protected Tree tree;
	
    protected AbstractTreeWriter(Writer out, Tree tree) {
	super(out, tree);
	this.tree = tree;
    }
}
