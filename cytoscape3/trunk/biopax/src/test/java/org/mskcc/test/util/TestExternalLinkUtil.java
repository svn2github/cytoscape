// $Id: TestExternalLinkUtil.java,v 1.11 2006/06/15 22:07:49 grossb Exp $
//------------------------------------------------------------------------------
/** Copyright (c) 2006 Memorial Sloan-Kettering Cancer Center.
 **
 ** Code written by: Ethan Cerami, Benjamin Gross.
 ** Authors: Ethan Cerami, Gary Bader, Chris Sander
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** any later version.
 **
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and
 ** Memorial Sloan-Kettering Cancer Center
 ** has no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall
 ** Memorial Sloan-Kettering Cancer Center
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if
 ** Memorial Sloan-Kettering Cancer Center
 ** has been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 **
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/
package org.mskcc.test.util;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.mskcc.biopax_plugin.util.biopax.BioPaxConstants;
import org.mskcc.biopax_plugin.util.links.ExternalLink;
import org.mskcc.biopax_plugin.util.links.ExternalLinkUtil;

import java.util.ArrayList;


/**
 * Tests the ExternalLink Utility Class.
 *
 * @author Ethan Cerami.
 */
public class TestExternalLinkUtil extends TestCase {
	/**
	 * Test bootstrap
	 *
	 * @return Test
	 */
	public static Test suite() {
		// Will dynamically add all methods as tests that begin with 'test'
		// and have no arguments:
		return new TestSuite(TestExternalLinkUtil.class);
	}

	/**
	 * Test main
	 *
	 * @param args String[]
	 */
	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	/**
	 * Tests External Database Links.
	 */
	public void testExternalLinks() {
		String url = ExternalLinkUtil.createLink("SWP", "ABC123");
		assertEquals("<A class=\"link\" HREF=\"http://www.pir.uniprot.org/cgi-bin/upEntry?"
		             + "id=ABC123\">SWP:  ABC123</A>", url);
	}

	/**
	 * Tests Links to IHOP.
	 */
	public void testIHOPLinks() {
		ArrayList dbList = new ArrayList();
		dbList.add(new ExternalLink("UniProt", "P10275"));
		dbList.add(new ExternalLink("GO", "4321"));
		dbList.add(new ExternalLink("ENTREZ_GENE", "367"));
		dbList.add(new ExternalLink("Reactome", "XYZ"));

		ArrayList synList = new ArrayList();
		synList.add("KD");
		synList.add("AIS");
		synList.add("TFM");

		//  Temporarily disable URL Encoding so we can more easily
		//  see the actual URL for connecting to IHOP
		ExternalLinkUtil.useUrlEncoding(false);

		//  Verify a Sample URL to IHOP
		String url = ExternalLinkUtil.getIHOPUrl(BioPaxConstants.PROTEIN, synList, dbList, 9606);
		assertEquals("http://www.ihop-net.org/UniPub/iHOP/in?syns_1=KD|"
		             + "AIS|TFM&dbrefs_1=UNIPROT__AC|P10275,NCBI_GENE__ID|367",
		             url);

		//  Verify that no link is generated for small molecules
		url = ExternalLinkUtil.getIHOPUrl(BioPaxConstants.SMALL_MOLECULE, synList, dbList, 9606);
		assertEquals(null, url);

		//  Very a Sample HREF Link to IHOP
		String html = ExternalLinkUtil.createIHOPLink(BioPaxConstants.PROTEIN, synList, dbList, 9606);
		assertTrue(html.startsWith("<A class=\"link\" HREF=\"http://www.ihop-net.org/UniPub/iHOP/in?syns_1"));

		//  Try using Synonyms + XRefs not supported by IHOP
		//  Should result in link with synonyms only.
		dbList = new ArrayList();
		dbList.add(new ExternalLink("GO", "4321"));
		dbList.add(new ExternalLink("Reactome", "XYZ"));
		url = ExternalLinkUtil.getIHOPUrl(BioPaxConstants.PROTEIN, synList, dbList, 9606);
        assertEquals("http://www.ihop-net.org/UniPub/iHOP/in?syns_1=KD|AIS|TFM",
		             url);

		//  Try using no Synonyms + XRefs not supported by IHOP
		//  Should result in null.
		url = ExternalLinkUtil.getIHOPUrl(BioPaxConstants.PROTEIN, null, dbList, -1);
		assertEquals(null, url);

		//  Try using null arguments.
		//  Should result in null.
		url = ExternalLinkUtil.getIHOPUrl(BioPaxConstants.PROTEIN, null, null, -1);
		assertEquals(null, url);

		//  Now, turn URL Encoding back on, and verify a link
		ExternalLinkUtil.useUrlEncoding(true);
		url = ExternalLinkUtil.getIHOPUrl(BioPaxConstants.PROTEIN, synList, dbList, 9606);
		assertEquals("http://www.ihop-net.org/UniPub/iHOP/in?syns_1=KD%7"
		             + "CAIS%7CTFM", url);

		//  Test Special Case where we have exactly dbRef of type UniProt,
		//  and no synonyms
		dbList = new ArrayList();
		dbList.add(new ExternalLink("UniProt", "P10275"));
		synList = new ArrayList();
		url = ExternalLinkUtil.getIHOPUrl(BioPaxConstants.PROTEIN, synList, dbList, 9606);
		assertEquals(null, url);

		//  Now, add one synonym, and verify new link
		synList.add("TNF");
		url = ExternalLinkUtil.getIHOPUrl(BioPaxConstants.PROTEIN, synList, dbList, 9606);
		assertEquals("http://www.ihop-net.org/UniPub/iHOP/in?syns_1=TNF&dbrefs_1="
		             + "UNIPROT__AC%7CP10275", url);
	}
}
