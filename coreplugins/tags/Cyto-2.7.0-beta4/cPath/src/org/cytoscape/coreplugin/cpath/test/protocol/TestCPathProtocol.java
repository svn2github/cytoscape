/*
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
package org.cytoscape.coreplugin.cpath.test.protocol;

import junit.framework.TestCase;
import org.cytoscape.coreplugin.cpath.model.EmptySetException;
import org.cytoscape.coreplugin.cpath.model.CPathException;
import org.cytoscape.coreplugin.cpath.protocol.CPathProtocol;

/**
 * Tests the CPath Protocol.
 * <p/>
 * Requires network access to cPath Web Service API.
 *
 * @author Ethan Cerami
 */
public class TestCPathProtocol extends TestCase {

    /**
     * Tests the Get Counter Query.
     *
     * @throws Exception All Errors.
     */
    public void testGetCounter () throws Exception {
        CPathProtocol cpath = new CPathProtocol();

        //  This query should get some hits
        cpath.setCommand(CPathProtocol.COMMAND_GET_BY_KEYWORD);
        cpath.setFormat(CPathProtocol.FORMAT_COUNT_ONLY);
        cpath.setQuery("p53");

        String url = cpath.getURI();
        assertEquals("http://cbio.mskcc.org/cpath/webservice.do?cmd=get_by_keyword&q=p53"
                + "&format=count_only&version=1.0&maxHits=10&startIndex=0&", url);

        String response = cpath.connect();
        int count = Integer.parseInt(response);
        assertTrue(count > 1);

        //  This query should trigger an empty set exception
        cpath.setQuery("blahblahboogy");
        try {
            cpath.connect();
            fail("Empty Set Exception should have been thrown");
        } catch (EmptySetException e) {
        }

        //  Test that the query is URL Encoded.
        //  When not encoded, users cannot enter more than one search term.
        //  Bug was discovered by Melissa, during creation of the Nature Cytoscape
        //  Protocol paper
        cpath.setQuery("p53 rad51");
        String uri = cpath.getURI();
        int index = uri.indexOf("p53+rad51");
        assertTrue ("cPath URL is not URL Encoded", index > 0);

        //  Try sending an invalid format, and verify that we trigger a cPath Exception
        cpath.setFormat("SMBL");
        try {
            String content = cpath.connect();
            System.out.println(content);
        } catch (CPathException e) {
            String msg = e.getMessage();
            assertEquals ("Error Connecting to cPath Web Service "
                + "(Error Code:  451, Error Message:  Bad Data Format "
                + "(data format not recognized))", msg);
        }
    }
}
