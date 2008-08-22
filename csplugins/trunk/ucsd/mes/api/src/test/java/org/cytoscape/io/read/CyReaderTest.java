package org.cytoscape.io.read;


import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.lang.RuntimeException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.net.URI;

import org.cytoscape.model.attrs.*;
import org.cytoscape.model.network.*;


public class CyReaderTest extends TestCase {


	public static Test suite() {
		return new TestSuite(CyReaderTest.class);
	}

	public void setUp() {
	}

	public void tearDown() {
	}

	public void testMultipleImplementations() {
		new ReadTest();
		assertTrue( true );
	}

	private class ReadTest implements CyAttributesReader, CyNetworkReader {
		ReadTest() {}
		public List<CyAttributes> getReadAttributes() {return null;}
		public List<CyNetwork> getReadNetworks() {return null;}
		public void read(){}
		public void setInput(URI u){}
	}
}

