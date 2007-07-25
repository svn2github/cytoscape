/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.tree.io;

import infovis.Table;
import infovis.Tree;
import infovis.io.AbstractReader;
import infovis.io.AbstractReaderFactory;

import java.io.InputStream;

/**
 * Creator for JarTreeReader
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.2 $
 * 
 */
public class JarReaderCreator implements AbstractReaderFactory.Creator {
    public JarReaderCreator() {
    }
    
    public String getName() {
        return "jar";
    }
    public AbstractReader create(InputStream in, String name,
            Table table) {
        return create(name, table);
    }
    
    public AbstractReader create(String name, Table table) {
        if (name.endsWith(".jar")) {
            return new JarTreeReader(null, name, (Tree)table);
        }
        return null;
    }

}
