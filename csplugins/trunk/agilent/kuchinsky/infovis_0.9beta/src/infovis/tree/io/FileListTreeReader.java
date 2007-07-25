/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.tree.io;

import infovis.Tree;
import infovis.column.IntColumn;
import infovis.column.StringColumn;
import infovis.io.WrongFormatException;

import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;

/**
 * Class FileListTreeReader
 * 
 * Creates a tree from a list of file names, one per line.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.15 $
 */
public class FileListTreeReader extends AbstractTreeReader {
    private static Logger logger = Logger.getLogger(FileListTreeReader.class);
    protected StringColumn nameColumn;
    protected IntColumn indexColumn;
    protected String prefix;
    protected String separator = "/";

    public FileListTreeReader(
        InputStream in,
        String name,
        Tree tree) {
        super(in, name, tree);
        nameColumn = StringColumn.findColumn(tree, "name");
        prefix = "";
    }
    
    public boolean load() throws WrongFormatException {
        int lineNum = 0;
        try {
            while (! isEof()) {
                String line = readLine();
                if (line == null)
                    break;
                if (! line.startsWith(prefix)) {
                    continue;
                }
                String[] path =
                    line.substring(prefix.length()).split(separator);
                int parent = Tree.ROOT;
                for (int j = 0; j < path.length; j++) {
                    String name = path[j];
                    if (name.length() == 0)
                        continue;
                    parent = findNode(name, parent, tree, nameColumn);
                }
                if (indexColumn != null) {
                    indexColumn.setExtend(parent, lineNum);
                }
                lineNum++;
            }
        }
        catch(IOException e) {
            logger.error("Error reading "+getName(), e);
            return false;
        }
        return true;
    }


    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String string) {
        prefix = string;
    }

    public String getSeparator() {
        return separator;
    }

    public void setSeparator(String string) {
        separator = string;
    }

    public IntColumn getIndexColumn() {
        return indexColumn;
    }

    public void setIndexColumn(IntColumn column) {
        indexColumn = column;
    }

}
