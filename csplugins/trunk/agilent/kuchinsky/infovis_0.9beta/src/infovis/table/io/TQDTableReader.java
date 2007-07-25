/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/

package infovis.table.io;

import infovis.Metadata;
import infovis.Table;
import infovis.column.StringColumn;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Reader of Time Series TQD format for tables.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.15 $
 * 
 * @infovis.factory TableReaderFactory tqd
 */
public class TQDTableReader extends CSVTableReader {
    /**
     * Constructor.
     * @param in the input stream
     * @param name the resource name
     * @param table the table
     */
    public TQDTableReader(InputStream in, String name, Table table) {
        super(in, name, table);
        setSeparator(',');
        setLabelLinePresent(true);
        setTypeLinePresent(false);
    }

    /**
     * Loads a file into a table.
     * 
     * @param file the file
     * @param table the table
     * @return true if the file has been succesfuly read
     */
    public static boolean load(File file, Table table) {
        try {
            TQDTableReader loader = new TQDTableReader(
                    new FileInputStream(file),
                    file.toString(),
                    table);
            return loader.load();
        } catch (FileNotFoundException e) {
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean load() {
        try {
            String title = readLine();
            // String static_attr =
            readLine();
            // String dynamic_attr =
            readLine();
            // int time_points =
            readInt();
            // int records =
            readInt();
            skipToEol(); // skip comment
            table.getMetadata().addAttribute(Metadata.TITLE, title);
            addLabel("Name");
            table.addColumn(new StringColumn("Name"));
        } catch (IOException e) {
            return false;
        }
        return super.load();
    }

}
