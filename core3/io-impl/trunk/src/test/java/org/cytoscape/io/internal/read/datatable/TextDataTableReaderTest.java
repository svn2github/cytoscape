package org.cytoscape.io.internal.read.datatable;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import java.util.List;

import org.cytoscape.test.support.DataTableTestSupport;

import org.cytoscape.work.TaskMonitor;
import org.cytoscape.model.CyDataTable;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyDataTableFactory;

import org.cytoscape.io.internal.read.AbstractNetworkViewReaderTester;

import static org.mockito.Mockito.*;

public class TextDataTableReaderTest {

	CyDataTableFactory tableFactory;
	TaskMonitor taskMonitor;

	public TextDataTableReaderTest() {
		taskMonitor = mock(TaskMonitor.class);
		tableFactory = new DataTableTestSupport().getDataTableFactory();
	}


	@Test
	public void testDefaultDelimiter() throws Exception {

		CyDataTable[] tables = getTables("table_tab.txt","\t");

		CyDataTable table = checkSingleTable(tables, 3, 4);
	} 

	@Test
	public void testCommaDelimiter() throws Exception {

		CyDataTable[] tables = getTables("table_comma.txt",",");

		CyDataTable table = checkSingleTable(tables, 3, 4);

	} 

	@Test
	public void testSpaceDelimiter() throws Exception {

		CyDataTable[] tables = getTables("table_space.txt"," ");

		CyDataTable table = checkSingleTable(tables, 3, 5);

	} 

	@Test
	public void testBlankDelimiter() throws Exception {

		CyDataTable[] tables = getTables("blank_delimiter.txt", "");

		// 12 is number of chars in the first line and no other rows 
		// have that number of chars, so no rows should be loaded.
		CyDataTable table = checkSingleTable(tables, 12, 0);
	}

	@Test(expected=IOException.class)
	public void testDuplicateHeaders() throws Exception {
		CyDataTable[] tables = getTables("table_space.txt", "");
	} 

	@Test
	public void testBadDelimiter() throws Exception {

		CyDataTable[] tables = getTables("table_tab.txt", ",");

		// Since there are no commas in the first line, we
		// should see only one column created and then each
		// subsequent row should work since they also don't
		// contain commas.
		CyDataTable table = checkSingleTable(tables, 1, 5);
	}

	private CyDataTable checkSingleTable( CyDataTable[] tables, int numCols, int numRows) {
		assertNotNull( tables );
		assertEquals( 1, tables.length );
		CyDataTable table = tables[0];
		assertNotNull( table );
		assertEquals( numRows, table.getAllRows().size() );
		assertEquals( numCols, table.getColumnTypeMap().keySet().size() );
		return table;
	}

	private CyDataTable[] getTables(String file, String delim) throws Exception {
		File f = new File("./src/test/resources/testData/datatable/" + file);
		TextDataTableReader snvp = new TextDataTableReader(new FileInputStream(f), tableFactory);
		snvp.delimiter = delim; // setting the tunable
		snvp.run(taskMonitor);

		return snvp.getCyDataTables();
	}
}
