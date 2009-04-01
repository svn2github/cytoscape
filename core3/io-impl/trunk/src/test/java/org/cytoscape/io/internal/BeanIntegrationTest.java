package org.cytoscape.io.internal;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;

import org.cytoscape.io.CyFileFilter;
import org.cytoscape.io.DataCategory;
import org.cytoscape.io.read.CyReader;
import org.cytoscape.io.read.CyReaderFactory;
import org.cytoscape.io.read.CyReaderManager;
import org.cytoscape.io.internal.read.AbstractNetworkReader;
import org.cytoscape.io.internal.read.sif.InteractionsReader;
import org.cytoscape.io.internal.read.xgmml.XGMMLReader;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;


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
	private CyNetworkViewFactory viewFactoryMock;

	private CyFileFilter sifFilter;
	
	private CyReaderFactory sifFactory;
	
	private URI sifFileLocation;
	private URL xgmmlFile;
	private URL xgmmlURL;
	
	private CyReaderFactory xgmmlFactory;
	

	@Before public void initializeTest1() throws Exception {
		sifFileLocation = new URI("http://chianti.ucsd.edu/kono/data/galFiltered.sif");
		final File xFile = new File("src/test/resources/testData/galFiltered.xgmml");
		xgmmlURL = new URL("http://chianti.ucsd.edu/kono/data/galFiltered.xgmml");
		
		xgmmlFile = xFile.toURL();
		
		sifReader = (CyReader) applicationContext.getBean("sifReader");
		xgmmlReader = (CyReader) applicationContext.getBean("xgmmlReader");
		sifFilter = (CyFileFilter) applicationContext.getBean("sifFilter");
		sifFactory = (CyReaderFactory) applicationContext.getBean("sifReaderFactory");
		xgmmlFactory = (CyReaderFactory) applicationContext.getBean("xgmmlReaderFactory");
		
		factoryMock = createMock(CyNetworkFactory.class);
		viewFactoryMock = createMock(CyNetworkViewFactory.class);
		
		expect(factoryMock.getInstance()).andReturn(null).times(2);
		
		
		((AbstractNetworkReader)sifReader).setCyNetworkFactory(factoryMock);
		((AbstractNetworkReader)xgmmlReader).setCyNetworkFactory(factoryMock);
		
		System.out.println("--------------------------------------- SIF Description = " + sifFilter.getDescription());
	}

	
	/**
	 * Test using mock service objects.
	 * @throws Exception 
	 * @throws IllegalArgumentException 
	 */
	@Test public void readerManagerTest() throws Exception {
		System.out.println("--------------------------------------- Reader Manager Test Begins");
		
		// Register factories
		manager.addReaderFactory(sifFactory, null);
		manager.addReaderFactory(xgmmlFactory, null);
		
		CyReader reader1 = manager.getReader(sifFileLocation, DataCategory.NETWORK);
		
		assertEquals(InteractionsReader.class, reader1.getClass());
		// TODO these keep returning sif readers - probably because CyFileFilters is
		// broken for input streams.
/*		
		replay(factoryMock);
		CyReader reader2 = manager.getReader(xgmmlFile.openStream(), DataCategory.NETWORK);
		assertEquals(XGMMLReader.class, reader2.getClass());
		
		CyReader reader3 = manager.getReader(xgmmlURL.openStream(), DataCategory.NETWORK);
		assertEquals(XGMMLReader.class, reader3.getClass());
		verify(factoryMock);
*/		
		
		System.out.println("--------------------------------------- End of reader manager test");
	}
}
