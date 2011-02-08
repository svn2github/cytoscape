#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.internal;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;

import org.cytoscape.test.support.NetworkTestSupport;

import ${package}.SampleAnalyzer;

import java.util.Collection;

/**
 * Unit test for SampleAnalyzerImpl. 
 * <br/>
 * This unit test tests the implemention of the API directly
 * and we do this for simplicity here.  However, if you have 
 * an API that you expect others to implement, then you should 
 * consider creating an abstract test class for the interface 
 * that others can inherit from and use to test their implementation.
 * To do this, create an abstract test class where the constructor
 * takes an implemenation of the interface being tested as an 
 * argument or in an abstract set method.  Then create unit tests
 * assuming that the implementation has been provided. This 
 * forces you to think critically about your API and whether it
 * provide all necessary functionality.  Anyone implementing
 * your API will be able to inherit your test cases and be able
 * verify that their code meets YOUR expectations.  See the
 * org.cytoscape.model-api bundle for an example of this approach.
 */
public class SampleAnalyzerImplTest {

	CyNetwork network;
	NetworkTestSupport support;
	SampleAnalyzer analyzer;

	public SampleAnalyzerImplTest() {
		support = new NetworkTestSupport();
	}
		

	@Before
	public void setUp() {
		network = support.getNetwork(); 
		analyzer = new SampleAnalyzerImpl();
	}

	@Test
    public void testLowerBound() {
		Collection<CyNode> res = analyzer.analyzeNodes(network);	
		assertNotNull( res );
		assertEquals("list size",0,res.size());
	}

	@Test
    public void testEven() {
		network.addNode();
		network.addNode();
		network.addNode();
		network.addNode();

		Collection<CyNode> res = 	analyzer.analyzeNodes(network);	
		assertNotNull( res );
		assertEquals("list size",2,res.size());
	}

	@Test
    public void testOdd() {
		network.addNode();
		network.addNode();
		network.addNode();
		network.addNode();
		network.addNode();

		Collection<CyNode> res = 	analyzer.analyzeNodes(network);	
		assertNotNull( res );
		assertEquals("list size",2,res.size());
	}

	@Test(expected=NullPointerException.class)
	public void testNull() {
		Collection<CyNode> res = 	analyzer.analyzeNodes(null);	
	}

}
