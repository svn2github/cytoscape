package pingo.test;


import static org.junit.Assert.*;

import java.net.URL;
import java.util.Map;

import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import pingo.ui.IndexedTermModel;

import BiNGO.BiNGOplugin;

public class IndexerTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	
	
	@Test
	public void indexerTest() throws Exception {
		
		final URL targetFile = BiNGOplugin.class.getClassLoader().getResource("GO_Full");
		System.out.println("Resource file = " + targetFile);
		assertNotNull(targetFile);
		
		IndexedTermModel model = new IndexedTermModel(targetFile);
		
		assertNotNull(model);
		
		final Map<String, String> result = model.query("apoptosis");
		assertTrue(result.size() >120);
	}

}
