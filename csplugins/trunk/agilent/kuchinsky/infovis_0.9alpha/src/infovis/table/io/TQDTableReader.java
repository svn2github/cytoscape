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
import infovis.column.FloatColumn;
import infovis.column.StringColumn;

import java.io.*;

/**
 * Reader of Time Series TQD format for tables.
 *
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.12 $
 * 
 * @infovis.factory TableReaderFactory tqd
 */
public class TQDTableReader extends CSVTableReader {

    public TQDTableReader(BufferedReader in, String name, Table table) {
	super(in, name, table);
	setSeparator(',');
	setLabelLinePresent(true);
	setTypeLinePresent(false);
    }

    public static boolean load(File file, Table table) {
	try {
	    TQDTableReader loader =
		new TQDTableReader(new BufferedReader(new FileReader(file)), file.toString(), table);
	    return loader.load();
	}
	catch (FileNotFoundException e) {
	    return false;
	}
    }

    public String readNextField() throws IOException {
	StringBuffer buffer = new StringBuffer();

	int c;
	while ((c = in.read()) == '#')
	    skipToEol();
	buffer.append((char)c);
	while (in.ready()) {
	    c = in.read();
	    if (c == ',' || c == '\n') {
		break;
	    }
	    buffer.append((char)c);
	}
	return buffer.toString();
    }

    public FloatColumn readFloatColumn(int values) throws IOException {
	FloatColumn col = new FloatColumn(readNextField());

	for (int i = 0; i < values; i++) {
	    col.addValueOrNull(readNextField());
	}
	return col;
    }

    public boolean load() {
	try {
	    String title = readLine();
	    //String static_attr = 
	    readLine();
	    //String dynamic_attr = 
	    readLine();
	    //int time_points = 
	    readInt();
	    //int records = 
	    readInt();
	    skipToEol(); // skip comment
	    table.getMetadata().put(Metadata.TITLE, title);
            addLabel("Name");
            table.addColumn(new StringColumn("Name"));
	}
	catch (IOException e) {
	    return false;
	}
	return super.load();
    }

}
