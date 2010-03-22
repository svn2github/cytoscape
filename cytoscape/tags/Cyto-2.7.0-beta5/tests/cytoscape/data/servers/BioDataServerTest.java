/*
  File: BioDataServerTest.java

  Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)

  The Cytoscape Consortium is:
  - Institute for Systems Biology
  - University of California San Diego
  - Memorial Sloan-Kettering Cancer Center
  - Institut Pasteur
  - Agilent Technologies

  This library is free software; you can redistribute it and/or modify it
  under the terms of the GNU Lesser General Public License as published
  by the Free Software Foundation; either version 2.1 of the License, or
  any later version.

  This library is distributed in the hope that it will be useful, but
  WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
  MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
  documentation provided hereunder is on an "as is" basis, and the
  Institute for Systems Biology and the Whitehead Institute
  have no obligations to provide maintenance, support,
  updates, enhancements or modifications.  In no event shall the
  Institute for Systems Biology and the Whitehead Institute
  be liable to any party for direct, indirect, special,
  incidental or consequential damages, including lost profits, arising
  out of the use of this software and its documentation, even if the
  Institute for Systems Biology and the Whitehead Institute
  have been advised of the possibility of such damage.  See
  the GNU Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License
  along with this library; if not, write to the Free Software Foundation,
  Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
*/

// BioDataServerTest.java
package cytoscape.data.servers;

import cytoscape.data.annotation.AnnotationDescription;

import cytoscape.data.servers.BioDataServer;

import junit.framework.TestCase;
import junit.framework.TestSuite;


/**
 * test the DataServer class, running it in process (not via RMI)
 */
public class BioDataServerTest extends TestCase {
	/**
	 * Creates a new BioDataServerTest object.
	 *
	 * @param name  DOCUMENT ME!
	 */
	public BioDataServerTest(String name) {
		super(name);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @throws Exception DOCUMENT ME!
	 */
	public void setUp() throws Exception {
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @throws Exception DOCUMENT ME!
	 */
	public void tearDown() throws Exception {
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @throws Exception DOCUMENT ME!
	 */
	public void notestThesaurusFromFlatFile() throws Exception {
		System.out.println("testThesaurusFromFlatFile");

		BioDataServer server = new BioDataServer();
		String[] thesaurusFiles = { "yeastThesaurusSmall.txt" };
		server.loadThesaurusFiles(thesaurusFiles);

		String species = "Saccharomyces cerevisiae";
		String canonicalName = "YOL165C";
		String commonName = "AAD15";
		assertTrue(server.getCommonName(species, canonicalName).equals(commonName));

		canonicalName = "YPR060C";
		commonName = "ARO7";
		assertTrue(server.getCommonName(species, canonicalName).equals(commonName));
		assertTrue(server.getCommonName(species, commonName).equals(commonName));
		assertTrue(server.getCanonicalName(species, commonName).equals(canonicalName));
		assertTrue(server.getCanonicalName(species, canonicalName).equals(canonicalName));

		String[] allCommonNames = server.getAllCommonNames(species, canonicalName);
		assertTrue(allCommonNames.length == 4);
	} // testThesaurusFromFlatFile

	/**
	 *  DOCUMENT ME!
	 *
	 * @throws Exception DOCUMENT ME!
	 */
	public void notestThesaurusWithAbsentEntries() throws Exception {
		System.out.println("testThesaurusWithAbsentEntries");

		BioDataServer server = new BioDataServer();
		String[] thesaurusFiles = { "yeastThesaurusSmall.txt" };
		server.loadThesaurusFiles(thesaurusFiles);

		String species = "duck";
		String canonicalName = "duck37";
		String commonName = "duck37";

		assertTrue(server.getCommonName("duck", "mallardase").equals("mallardase"));
		assertTrue(server.getCanonicalName("duck", "mallardase").equals("mallardase"));

		String[] allCommonNames = server.getAllCommonNames("duck", "grebase");
		assertTrue(allCommonNames.length == 1);
	} //  testThesaurusWithAbsentEntries

	/**
	 *  DOCUMENT ME!
	 *
	 * @throws Exception DOCUMENT ME!
	 */
	public void notestReadGoYeastAnnotation() throws Exception {
		System.out.println("testReadGoYeastAnnotation");

		// read manifest, a local file, and the files it names:
		//
		//   ontology=go.onto
		//   annotation=bioproc.anno
		//   annotation=molfunc.anno
		//   annotation=cellcomp.anno
		String manifest = "annotations/goYeast/manifest";
		BioDataServer server = new BioDataServer(manifest);

		AnnotationDescription[] aDescs = server.getAnnotationDescriptions();
		assertTrue(aDescs.length == 3);

		String species = "Saccharomyces cerevisiae";
		String curator = "GO";
		String orf = "YER033C";

		int[] ids = server.getClassifications(species, curator, "Molecular Function", orf);
		assertTrue(ids.length == 1);
		assertTrue(ids[0] == 5554);

		ids = server.getClassifications(species, curator, "Cellular Component", orf);
		assertTrue(ids.length == 1);
		assertTrue(ids[0] == 8372);

		ids = server.getClassifications(species, curator, "Biological Process", orf);
		assertTrue(ids.length == 1);
		assertTrue(ids[0] == 4);

		orf = "YNR016C";
		ids = server.getClassifications(species, curator, "Molecular Function", orf);
		assertTrue(ids.length == 2);

		int[] expected0 = { 3989, 4075 };
		assertTrue(containedIn(ids, expected0));

		ids = server.getClassifications(species, curator, "Cellular Component", orf);
		assertTrue(ids.length == 2);

		int[] expected1 = { 5829, 5789 };
		assertTrue(containedIn(ids, expected1));

		ids = server.getClassifications(species, curator, "Biological Process", orf);
		assertTrue(ids.length == 2);

		int[] expected2 = { 6633, 6998 };
		assertTrue(containedIn(ids, expected2));
	} // testReadGoYeastAnnotation

	/**
	 *  DOCUMENT ME!
	 *
	 * @throws Exception DOCUMENT ME!
	 */
	public void notestReadViaHTTP() throws Exception {
		System.out.println("testReadViaHTTP");

		// read manifest, via http, and the files it names (which are
		// implicitly at the same url:
		//
		//   ontology=go.onto
		//   annotation=bioproc.anno
		//   annotation=molfunc.anno
		//   annotation=cellcomp.anno
		String manifest = "http://db.systemsbiology.net:8080/cytoscape/annotation/testDoNotDelete/manifest";

		BioDataServer server = new BioDataServer(manifest);

		AnnotationDescription[] aDescs = server.getAnnotationDescriptions();
		assertTrue(aDescs.length == 4);

		String species = "Saccharomyces cerevisiae";
		String curator = "GO";
		String orf = "YER033C";

		int[] ids = server.getClassifications(species, curator, "Molecular Function", orf);
		assertTrue(ids.length == 1);
		assertTrue(ids[0] == 5554);

		ids = server.getClassifications(species, curator, "Cellular Component", orf);
		assertTrue(ids.length == 1);
		assertTrue(ids[0] == 8372);

		ids = server.getClassifications(species, curator, "Biological Process", orf);
		assertTrue(ids.length == 1);
		assertTrue(ids[0] == 4);

		orf = "YNR016C";
		ids = server.getClassifications(species, curator, "Molecular Function", orf);
		assertTrue(ids.length == 2);

		int[] expected0 = { 3989, 4075 };
		assertTrue(containedIn(ids, expected0));

		ids = server.getClassifications(species, curator, "Cellular Component", orf);
		assertTrue(ids.length == 2);

		int[] expected1 = { 5829, 5789 };
		assertTrue(containedIn(ids, expected1));

		ids = server.getClassifications(species, curator, "Biological Process", orf);
		assertTrue(ids.length == 2);

		int[] expected2 = { 6633, 6998 };
		assertTrue(containedIn(ids, expected2));

		curator = "KEGG";
		ids = server.getClassifications(species, curator, "Metabolic Pathways", orf);
		assertTrue(ids.length == 4);

		int[] expected3 = { 61, 620, 640, 253 };
		assertTrue(containedIn(ids, expected3));

		String canonicalName = "YAL001C";
		String commonName = "TFC3";
		String synonym2 = "TSV115";
		String synonym3 = "FUN24";

		assertTrue(server.getCommonName(species, canonicalName).equals(commonName));
		assertTrue(server.getCanonicalName(species, commonName).equals(canonicalName));
		assertTrue(server.getCanonicalName(species, synonym2).equals(canonicalName));
		assertTrue(server.getCanonicalName(species, synonym3).equals(canonicalName));
		assertTrue(server.getCanonicalName(species, canonicalName).equals(canonicalName));
	} // testReadViaHttp

	/**
	 *  DOCUMENT ME!
	 *
	 * @throws Exception DOCUMENT ME!
	 */
	public void notestReadKeggHaloAnnotation() throws Exception {
		System.out.println("testReadKeggHaloAnnotation");

		// read manifest, a local file, and the files it names:
		//
		// annotation=pathways.anno
		// ontology=ontology
		String manifest = "annotations/keggHalo/manifest";
		BioDataServer server = new BioDataServer(manifest);

		AnnotationDescription[] aDescs = server.getAnnotationDescriptions();
		assertTrue(aDescs.length == 1);

		String species = "Halobacterium sp.";
		String curator = "KEGG";
		String orf = "VNG1873G";
		String annotationType = "Metabolic Pathways";

		int[] ids = server.getClassifications(species, curator, annotationType, orf);
		assertTrue(ids.length == 3);

		int[] expected = { 20, 720, 480 };
		assertTrue(containedIn(ids, expected));
	} // testReadKeggHaloAnnotation

	/**
	 *  DOCUMENT ME!
	 *
	 * @throws Exception DOCUMENT ME!
	 */
	public void testReadAnnotationsWithTwoOntologies() throws Exception {
		System.out.println("testReadAnnotationsWithTwoOntologies");

		//String manifest = "annotations/humanKeggAndGO/manifest.both";
		String manifest = "testData/humanKeggAndGO/manifest.both";
		BioDataServer server = new BioDataServer(manifest);

		String species = "Homo sapiens";
		String curator = "KEGG";

		String canonicalName = "NP_647593";
		String commonName = "qqq";
		String synonym2 = "QQQ";

		assertTrue(server.getCommonName(species, canonicalName).equals(commonName));
		assertTrue(server.getCanonicalName(species, commonName).equals(canonicalName));
		assertTrue(server.getCanonicalName(species, synonym2).equals(canonicalName));
		assertTrue(server.getCanonicalName(species, canonicalName).equals(canonicalName));

		String annotationType = "Metabolic Pathways";

		int[] ids = server.getClassifications(species, curator, annotationType, canonicalName);
		assertTrue(ids.length == 6);

		int[] expected0 = { 120, 350, 561, 260, 300, 40 };
		assertTrue(containedIn(ids, expected0));

		AnnotationDescription[] aDescs = server.getAnnotationDescriptions();

		assertTrue(aDescs.length == 4);

		curator = "GO";

		String annotationType1 = "Molecular Function";
		String annotationType2 = "Cellular Component";
		String annotationType3 = "Biological Process";

		ids = server.getClassifications(species, curator, annotationType1, canonicalName);
		assertTrue(ids.length == 1);
		assertTrue(ids[0] == 8181);

		ids = server.getClassifications(species, curator, annotationType2, canonicalName);
		assertTrue(ids.length == 2);

		int[] expected2 = { 5871, 15629 };
		assertTrue(containedIn(ids, expected2));

		ids = server.getClassifications(species, curator, annotationType3, canonicalName);
		assertTrue(ids.length == 6);

		int[] expected3 = { 6899, 7268, 8099, 8283, 30154, 45786 };
		assertTrue(containedIn(ids, expected3));
	} // testReadAnnotationsWithTwoOntologies

	private boolean containedIn(int[] a, int[] b) {
		for (int i = 0; i < a.length; i++) {
			boolean foundA = false;

			for (int j = 0; j < b.length; j++)
				if (b[j] == a[i]) {
					foundA = true;

					break;
				}

			if (!foundA)
				return false;
		} // for i

		return true;
	} // containedIn

	/**
	 *  DOCUMENT ME!
	 *
	 * @param args DOCUMENT ME!
	 */
	public static void main(String[] args) {
		junit.textui.TestRunner.run(new TestSuite(BioDataServerTest.class));
		System.exit(0); // needed because otherwise UnicastRemoteObject runs forever
	}
} // BioDataServerTest
