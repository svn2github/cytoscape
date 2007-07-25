import infovis.io.AbstractReader;
import infovis.table.DefaultTable;
import infovis.table.io.*;

import java.io.*;

import junit.framework.TestCase;

/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/

public class CSVIOTest extends TestCase {
    public CSVIOTest(String name) {
        super(name);
    }
    
    public void testCSVIO() {
        DefaultTable table = new DefaultTable();
        AbstractReader reader = 
            TableReaderFactory.createTableReader("data/table/homes.csv", table);
        assertTrue("Creating reader", reader != null);
        assertTrue("Loading file", reader.load());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        CSVTableWriter writer = new CSVTableWriter(out, table);
        assertTrue("Saving file", writer.write());
        InputStream sin = new ByteArrayInputStream(out.toByteArray());
        DefaultTable table2 = new DefaultTable();
        reader = new CSVTableReader(sin, table2);
        assertTrue("Loading written file", reader.load());
        assertTrue("Table not equal", table2.equals(table));
    }

}
