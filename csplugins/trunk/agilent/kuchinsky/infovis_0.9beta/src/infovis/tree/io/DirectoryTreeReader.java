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
import infovis.column.LongColumn;
import infovis.column.StringColumn;
import infovis.column.format.UTCDateFormat;
import infovis.metadata.AggregationConstants;

import java.io.BufferedReader;
import java.io.File;

/**
 * Reader of Directory.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.21 $
 * 
 * 
 */
public class DirectoryTreeReader extends AbstractTreeReader {
    public static final String COLUMN_NAME   = "name";
    public static final String COLUMN_LENGTH = "length";
    public static final String COLUMN_DATE   = "date";
    public static final String COLUMN_PATH   = "path";

    protected String           directory;
    protected LongColumn       lengthColumn;
    protected StringColumn     nameColumn;
    protected LongColumn       dateColumn;
    protected StringColumn     pathColumn;

    public DirectoryTreeReader(
            BufferedReader ignore,
            String directory,
            Tree tree) {
        super(null, "dir", tree);
        this.directory = directory;
        nameColumn = StringColumn.getColumn(tree, COLUMN_NAME);
        if (nameColumn == null) {
            nameColumn = new StringColumn(COLUMN_NAME);
        }
        lengthColumn = LongColumn.getColumn(tree, COLUMN_LENGTH);
        if (lengthColumn == null) {
            lengthColumn = new LongColumn(COLUMN_LENGTH);
        }
        lengthColumn.getMetadata().addAttribute(
                AggregationConstants.AGGREGATION_TYPE,
                AggregationConstants.AGGREGATION_TYPE_ADDITIVE);
        dateColumn = LongColumn.getColumn(tree, COLUMN_DATE);
        if (dateColumn == null) {
            dateColumn = new LongColumn(COLUMN_DATE);
        }
        dateColumn.getMetadata().addAttribute(
                AggregationConstants.AGGREGATION_TYPE,
                AggregationConstants.AGGREGATION_TYPE_MAX);
        pathColumn = StringColumn.getColumn(tree, COLUMN_PATH);
    }

    public DirectoryTreeReader(String directory, Tree tree) {
        this(null, directory, tree);
    }

    public DirectoryTreeReader(
            BufferedReader ignore,
            String directory,
            Table table) {
        this(ignore, directory, (Tree) table);
    }

    protected void disableNotify() {
        nameColumn.disableNotify();
        dateColumn.disableNotify();
        lengthColumn.disableNotify();
    }

    protected void enableNotify() {
        nameColumn.enableNotify();
        dateColumn.enableNotify();
        lengthColumn.enableNotify();
    }

    protected int readDirectory(File file, int parent) {
        int node = tree.addNode(parent);

        nameColumn.setExtend(node, file.getName());
        dateColumn.setExtend(node, file.lastModified());
        if (pathColumn != null) {
            pathColumn.setExtend(node, file.getAbsolutePath());
        }

        long len = 0;
        long lastMod = -1;
        if (file.isDirectory()) {
            // lengthColumn.setExtend(node, 0);
            String[] list = file.list();
            for (int i = 0; i < list.length; i++) {
                int child = readDirectory(new File(file, list[i]), node);
                len += lengthColumn.get(child);
                lastMod = Math.max(lastMod, dateColumn.get(child));
            }
        }
        else {
            len = file.length();
        }
        lengthColumn.setExtend(node, len);
        dateColumn.setExtend(node, lastMod);
        return node;
    }

    /**
     * @see infovis.io.AbstractReader#load()
     */
    public boolean load() {
        File file = new File(directory);
        if (!file.isDirectory()) {
            return false;
        }
        int node;
        try {
            disableNotify();
            node = readDirectory(file, Tree.ROOT);
        } finally {
            enableNotify();
        }
        if (node != -1) {
            nameColumn.set(Tree.ROOT, file.getName());
            tree.addColumn(nameColumn);
            lengthColumn.set(Tree.ROOT, lengthColumn.get(node));
            tree.addColumn(lengthColumn);
            dateColumn.set(Tree.ROOT, dateColumn.get(node));
            dateColumn.setFormat(UTCDateFormat.getSharedInstance());
            tree.addColumn(dateColumn);
        }
        return true;
    }
}