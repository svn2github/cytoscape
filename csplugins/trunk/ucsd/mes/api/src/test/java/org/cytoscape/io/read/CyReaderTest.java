package org.cytoscape.io.read;


import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.lang.RuntimeException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.io.InputStream;
import java.io.IOException;

import org.cytoscape.model.*;


public class CyReaderTest extends TestCase {

	public void setUp() {
	}

	public void tearDown() {
	}

	public void testMultipleImplementations() {
		new ReadTest();
		assertTrue( true );
	}

	private class ReadTest implements CyDataTableReader, CyNetworkReader {
		ReadTest() {}
		public List<CyDataTable> getReadDataTables() {return null;}
		public List<CyNetwork> getReadNetworks() {return null;}
		public void read() throws IOException {}
		public void setInput(InputStream is){}
	}
}

