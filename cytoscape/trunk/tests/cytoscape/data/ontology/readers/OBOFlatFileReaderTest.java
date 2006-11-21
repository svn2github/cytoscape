package cytoscape.data.ontology.readers;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;
import cytoscape.CyNetwork;
import cytoscape.data.CyAttributes;
import cytoscape.data.ontology.readers.OBOFlatFileReader;
import cytoscape.data.ontology.readers.OBOTags;

public class OBOFlatFileReaderTest extends TestCase {

	private static final String REMOTE_OBO = "http://www.geneontology.org/ontology/gene_ontology.obo";
	private static final String LOCAL_OBO = "testData/annotation/goslim_generic.obo";
	
	private static final int DAG_SIZE = 10738;

	private InputStream is;

	protected void setUp() throws Exception {
		super.setUp();
		File sampleOBO = new File(LOCAL_OBO);
		
		URL oboUrl = sampleOBO.toURL();
		is = oboUrl.openStream();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		try {
			if (is != null) {
				is.close();
			}
		} catch (IOException ioe) {
		} finally {
			is = null;
		}
	}

	public void testReadOBO() {

		long start = System.currentTimeMillis();
		OBOFlatFileReader obor = new OBOFlatFileReader(is, null);
		try {
			obor.readOntology();
			assertNotNull(obor.getHeader());
			Map header = obor.getHeader();
			Set<String> keys = obor.getHeader().keySet();
			for (String key : keys) {
				System.out.println("Key = " + key + ", Value = "
						+ header.get(key));
			}
			assertTrue(keys.contains("format-version"));
			assertTrue(keys.contains("date"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("## OBO Loading time = " + (System.currentTimeMillis() - start) + " msec.");
		
		start = System.currentTimeMillis();
		
		CyAttributes goTermAttr = obor.getTermsAttributes();
		String[] names = goTermAttr.getAttributeNames();
		for(int i = 0; i<names.length; i++) {
			System.out.println("Name = " + names[i]);
		}
		
		String curString = goTermAttr.getStringAttribute("GO:0000004", "name");
		System.out.println("GO:0000004 = " + curString);
		assertEquals(curString, "biological process unknown");
		
		curString = goTermAttr.getStringAttribute("GO:0016049", "name");
		System.out.println("GO:0016049 = " + curString);
		assertEquals(curString, "cell growth");
		
		Map synoMap = goTermAttr.getMapAttribute("GO:0016049", OBOTags.SYNONYM.toString());
		assertNotNull(synoMap);
		assertEquals(4, synoMap.size());
		for(Object val: synoMap.keySet()) {
			System.out.print("GO:0016049 Synonyms = " + val.toString());
			System.out.println(" ( " + synoMap.get(val) + " )");
		}
		
		CyNetwork dag = obor.getDag();
		assertNotNull(dag);
		
		System.out.println("## OBO Test time = " + (System.currentTimeMillis() - start) + " msec.");
		
	}

}
