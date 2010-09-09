package org.cytoscape.test.support;

import org.cytoscape.model.AbstractCyTableTest;
import org.cytoscape.model.CyTableFactory;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.CyRow;
import java.util.Random;

/**
 * This will verify that the network created by NetworkTestSupport
 * is a good network.
 */
public class DataTableTestSupportTest extends AbstractCyTableTest {

	DataTableTestSupport support; 
	CyTableFactory factory;
	Random rand;

	public DataTableTestSupportTest() {
		support = new DataTableTestSupport();
		factory = support.getDataTableFactory();
		rand = new Random(15);
	}

	public void setUp() {
		eventHelper = support.getDummyCyEventHelper(); 
		mgr = factory.createTable(Integer.toString( rand.nextInt(10000) ), "SUID", Long.class, false);
		attrs = mgr.getRow(1l);
	}

	public void tearDown() {
		mgr = null;
		attrs = null;
	}
}
