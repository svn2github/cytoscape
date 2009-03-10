package org.cytoscape.io.internal;

import java.net.URI;
import java.net.URISyntaxException;

import org.cytoscape.io.CyFileFilter;
import org.cytoscape.io.DataCategory;
import org.cytoscape.io.read.CyReader;
import org.cytoscape.io.read.CyReaderFactory;
import org.cytoscape.io.read.CyReaderManager;
import org.cytoscape.io.read.internal.AbstractNetworkReader;
import org.cytoscape.io.read.internal.sif.InteractionsReader;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.view.GraphViewFactory;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;


/**
 * Integration test the bundle locally (outside of OSGi). Use AbstractOsgiTests
 * and a separate integration test project for testing inside of OSGi.
 */
@ContextConfiguration(locations = "classpath:META-INF/spring/bundle-context.xml")
public class BeanIntegrationTest extends AbstractJUnit4SpringContextTests {

	@Autowired
	private CyReaderManager manager;
	
	private CyReader sifReader;
	private CyReader xgmmlReader;
	
	private CyNetworkFactory factoryMock;
	private GraphViewFactory viewFactoryMock;

	private CyFileFilter sifFilter;
	
	private CyReaderFactory sifFactory;
	
	private URI sampleFileLocation1;
	

	@Before
	public void initializeTest1() throws Exception {
		sampleFileLocation1 = new URI("http://chianti.ucsd.edu/kono/data/galFiltered.sif");
		
		sifReader = (CyReader) applicationContext.getBean("sifReader");
		xgmmlReader = (CyReader) applicationContext.getBean("xgmmlReader");
		sifFilter = (CyFileFilter) applicationContext.getBean("sifFilter");
		sifFactory = (CyReaderFactory) applicationContext.getBean("sifReaderFactory");
		
		factoryMock = createMock(CyNetworkFactory.class);
		viewFactoryMock = createMock(GraphViewFactory.class);
		
		((AbstractNetworkReader)sifReader).setCyNetworkFactory(factoryMock);
		
		System.out.println("--------------------------------------- SIF Description = " + sifFilter.getDescription());
	}

	
	/**
	 * Test using mock service objects.
	 */
	@Test public void readerManagerTest() {
		System.out.println("--------------------------------------- Reader Manager Test Begins");
		manager.addReaderFactory(sifFactory, null);
		CyReader reader = manager.getReader(sampleFileLocation1, DataCategory.NETWORK);
		
		assertEquals(reader.getClass(), InteractionsReader.class);
		
		System.out.println("--------------------------------------- Reader Obj = " + reader.toString());
	}
}
