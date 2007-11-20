package org.cytoscape.coreplugin.cpath2.web_service;

import cytoscape.CytoscapeInit;
import java.util.Properties;

/**
 * Contains cPath Specific Properties
 *
 * @author Ethan Cerami.
 */
public class CPathProperties {
	/**
	 * Property:  CPath2 Read Location.
	 */
	public static final String CPATH_URL = new String("cpath2.url");

    /**
     * Property:  CPath2 Server Name.
     */
    public static final String CPATH_INSTANCE_BNAME = new String("cpath2.server_name");

    /**
     * Property:  CPath2 Server Blurb
     */
    public static final String CPATH_INSTANCE_BLURB = new String ("cpath2.server_blurb");

    /**
	 * Gets URL for cPath Web Service API.
	 *
	 * @return cPath URL.
	 */
	public static String getCPathUrl() {
		Properties properties = CytoscapeInit.getProperties();
		String url = properties.getProperty(CPATH_URL);

		if (url != null) {
			return url;
		} else {
			//  hard-coded default
			return "http://localhost:8080/cpath/webservice.do";
           //return "http://awabi.cbio.mskcc.org/pc-demo/webservice.do";
        }
	}

    /**
	 * Gets Name of cPath Instance.
	 *
	 * @return cPath URL.
	 */
	public static String getCPathServerName() {
		Properties properties = CytoscapeInit.getProperties();
		String serverName = properties.getProperty(CPATH_INSTANCE_BNAME);

		if (serverName != null) {
			return serverName;
		} else {
			//  hard-coded default
			return "Pathway Commons";
        }
	}

    /**
	 * Gets Text Blurb for cPath Instance
	 *
	 * @return cPath URL.
	 */
	public static String getCPathBlurb() {
		Properties properties = CytoscapeInit.getProperties();
		String blurb = properties.getProperty(CPATH_INSTANCE_BLURB);

		if (blurb != null) {
			return blurb;
		} else {
			//  hard-coded default
			return "<span class='bold'>Pathway Commons</span> is a convenient point of access " +
                "to biological pathway " +
                "information collected from public pathway databases, which you can " +
                "browse or search. <BR><BR>Pathways include biochemical reactions, complex " +
                "assembly, transport and catalysis events, and physical interactions " +
                "involving proteins, DNA, RNA, small molecules and complexes.";
        }
	}
}
