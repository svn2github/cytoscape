package cytoscape.data.ontology.readers.unitTests;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

import junit.framework.TestCase;
import cytoscape.data.ontology.DBReference;
import cytoscape.data.ontology.readers.DBCrossReferenceReader;

/**
 * This class actually tests the following:
 * 
 * DBReference DBCrossReferences DBCrossReferenceReader
 * 
 * @author kono
 * 
 */
public class DBCrossReferenceReaderTest extends TestCase {

	DBCrossReferenceReader rd;

	protected void setUp() throws Exception {
		super.setUp();

		rd = new DBCrossReferenceReader();
	}

	protected void tearDown() throws Exception {
		super.tearDown();

		rd = null;
	}

	public void testGetXrefMap() {
		try {
			rd.readResourceFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Map<String, DBReference> sampleMap = rd.getXrefMap();

		assertTrue(sampleMap.size() > 0);
		assertTrue(sampleMap.containsKey("KEGG_PATHWAY"));
		assertTrue(sampleMap.containsKey("AgBase"));
		assertTrue(sampleMap.containsKey("ZFIN"));

		DBReference ref = sampleMap.get("SGD");
		assertEquals("Saccharomyces Genome Database", ref.getFullName());
		try {
			URL url = ref.getGenericURL();
			assertEquals("http://www.yeastgenome.org/", url.toString());
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertEquals("Identifier for SGD Loci", ref.getObject());

		try {
			String result = connectToURL(ref.getQueryURL("S000006169"));
			assertNotNull(result);

			/*
			 * Check this is a correct web page or not.
			 */
			assertTrue(result.contains("YPL248C"));
			assertTrue(result.contains("GAL4"));
			assertTrue(result.contains("DNA-binding transcription"));

		} catch (SocketTimeoutException e) {
			System.out
					.print("======= Connection to SGD timeout.  Check connection or try again later ======");
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block

		}
	}

	private String connectToURL(URL url) throws IOException {
		URLConnection uc = url.openConnection();

		/*
		 * Set timeout.
		 */
		uc.setReadTimeout(5000);

		InputStream is = uc.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		String s;
		StringBuffer sb = new StringBuffer();

		while ((s = reader.readLine()) != null) {
			sb.append(s);
		}
		reader.close();

		return sb.toString();
	}
}
