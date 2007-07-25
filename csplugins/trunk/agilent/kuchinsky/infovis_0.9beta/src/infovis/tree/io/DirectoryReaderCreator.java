/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.tree.io;

import infovis.Table;
import infovis.io.AbstractReader;
import infovis.io.AbstractReaderFactory;

import java.io.File;
import java.io.InputStream;

/**
 * Class DirectoryReaderCreator
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.2 $
 *
 * @infovis.factory TableReaderFactory directory noopen
 * @infovis.factory TreeReaderFactory directory noopen
 */
public class DirectoryReaderCreator implements AbstractReaderFactory.Creator {
    public DirectoryReaderCreator() {
    }
    
    public String getName() {
        return "Directory";
    }
    public AbstractReader create(InputStream ignore, String name,
            Table table) {
        return create(name, table);
    }
    
    public AbstractReader create(String name, Table table) {
        File f = new File(name);
        if (f.isDirectory()) {
            return new DirectoryTreeReader(null, name, table);
        }
        return null;
    }
};
