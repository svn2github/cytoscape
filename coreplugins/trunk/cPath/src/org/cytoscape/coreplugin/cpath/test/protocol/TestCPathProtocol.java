package org.cytoscape.coreplugin.cpath.test.protocol;

import junit.framework.TestCase;
import org.cytoscape.coreplugin.cpath.protocol.CPathProtocol;
import org.cytoscape.coreplugin.cpath.model.EmptySetException;

public class TestCPathProtocol extends TestCase {

    public void testGetCounter() throws Exception {
        CPathProtocol cpath = new CPathProtocol();

        //  This query should get some hits
        cpath.setCommand(CPathProtocol.COMMAND_GET_BY_KEYWORD);
        cpath.setFormat(CPathProtocol.FORMAT_COUNT_ONLY);
        cpath.setQuery("p53");

        String url = cpath.getURI();
        assertEquals ("http://cbio.mskcc.org/cpath/webservice.do?cmd=get_by_keyword&q=p53"
                + "&format=count_only&version=1.0&maxHits=10&startIndex=0&", url);

        String response = cpath.connect();
        int count = Integer.parseInt(response);
        assertTrue (count > 1);

        //  This query should trigger an empty set exception
        cpath.setQuery("blahblahboogy");
        try {
            cpath.connect();
            fail ("Empty Set Exception should have been thrown");
        } catch (EmptySetException e) {
        }
    }
}
