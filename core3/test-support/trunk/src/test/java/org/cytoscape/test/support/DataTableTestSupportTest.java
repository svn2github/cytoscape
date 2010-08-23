package org.cytoscape.test.support;

import org.cytoscape.model.AbstractCyDataTableTest;
import org.cytoscape.model.CyDataTableFactory;
import org.cytoscape.model.CyDataTable;
import org.cytoscape.model.CyRow;
import java.util.Random;

/**
 * This will verify that the network created by NetworkTestSupport
 * is a good network.
 */
public class DataTableTestSupportTest extends AbstractCyDataTableTest {

	DataTableTestSupport support; 
	CyDataTableFactory factory;
	Random rand;

	public DataTableTestSupportTest() {
		support = new DataTableTestSupport();
		factory = support.getDataTableFactory();
		rand = new Random(15);
	}

	public void setUp() {
		eventHelper = support.getDummyCyEventHelper(); 
		mgr = factory.createTable(Integer.toString( rand.nextInt(10000) ), false);
		attrs = mgr.getRow(1);
	}

	public void tearDown() {
		mgr = null;
		attrs = null;
	}
}
