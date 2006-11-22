package org.cytoscape.coreplugin.cpath.util;

import cytoscape.CytoscapeInit;

import java.util.Properties;

/**
 * Contains cPath Specific Properties
 *
 * @author Ethan Cerami.
 */
public class CPathProperties {

    /**
     * Property:  CPath Read Location.
     */
    private static final String CPATH_URL = new String("cpath.url");

    /**
     * Gets URL for cPath Web Service API.
     *
     * @return cPath URL.
     */
    public static String getCPathUrl () {
        Properties properties = CytoscapeInit.getProperties();
        String url = properties.getProperty(CPATH_URL);
        if (url != null) {
            return url;
        } else {
            //  hard-coded default
            return "http://cbio.mskcc.org/cpath/webservice.do";
        }
    }
}
