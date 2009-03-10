package org.cytoscape.io.internal;

import org.cytoscape.io.CyFileFilter;
import org.cytoscape.io.read.CyReader;
import org.cytoscape.io.read.CyReaderManager;
import org.cytoscape.io.read.internal.AbstractNetworkReader;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.view.GraphViewFactory;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import static org.easymock.EasyMock.*;


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
	

	@Before
	public void initializeTest1() {
		sifReader = (CyReader) applicationContext.getBean("sifReader");
		xgmmlReader = (CyReader) applicationContext.getBean("xgmmlReader");
		sifFilter = (CyFileFilter) applicationContext.getBean("sifFilter");
		
		
		factoryMock = createMock(CyNetworkFactory.class);
		viewFactoryMock = createMock(GraphViewFactory.class);
		
		((AbstractNetworkReader)sifReader).setCyNetworkFactory(factoryMock);
		
		System.out.println("--------------------------------------- SIF Description = " + sifFilter.getDescription());
	}

	
	/**
	 * Test using mock service objects.
	 */
	@Test public void fooManager() {
		System.out.println("--------------------------------------- Test1 ");
		
		System.out.println("--------------------------------------- Test12 " + this.applicationContext);
	}
}
