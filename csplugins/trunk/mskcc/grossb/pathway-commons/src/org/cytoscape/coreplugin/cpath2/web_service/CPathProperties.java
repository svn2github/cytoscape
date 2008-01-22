package org.cytoscape.coreplugin.cpath2.web_service;

import cytoscape.CytoscapeInit;
import cytoscape.plugin.PluginProperties;

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
	public static final String CPATH_URL = new String("cpath2.server_url");

    /**
     * Property:  CPath2 Server Name.
     */
    public static final String CPATH_INSTANCE_BNAME = new String("cpath2.server_name");

    /**
     * Property:  CPath2 Server Blurb
     */
    public static final String CPATH_INSTANCE_BLURB = new String ("cpath2.server_blurb");

    /**
     * Download Networks in Full BioPAX Mode.
     */
    public final static int DOWNLOAD_FULL_BIOPAX = 1;

    /**
     * Download Networks in Binary SIF Reduced Mode
     */
    public final static int DOWNLOAD_REDUCED_BINARY_SIF = 2;


    private static CPathProperties cpathProperties;
    private static String cPathUrl;
    private static String serverName;
    private static String blurb;
    private int downloadMode = DOWNLOAD_FULL_BIOPAX;

    /**
     * Gets singleton instance of cPath Properties.
     * @return CPathProperties class.
     */
    public static CPathProperties getInstance() {
        if (cpathProperties == null) {
               cpathProperties = new CPathProperties();
        }
        return cpathProperties;
    }

    private CPathProperties () {
        //  no-op; private constructor;
    }

    public void initProperties (PluginProperties pluginProperties) {
        cPathUrl = pluginProperties.getProperty(CPATH_URL);

        if (cPathUrl == null) {
            cPathUrl = "http://localhost:8080/cpath/webservice.do";
           //return "http://awabi.cbio.mskcc.org/pc-demo/webservice.do";
        }

        serverName = pluginProperties.getProperty(CPATH_INSTANCE_BNAME);
        if (serverName == null) {
            serverName = "Pathway Commons";
        }

        blurb = pluginProperties.getProperty(CPATH_INSTANCE_BLURB);
        if (blurb == null) {
            blurb = "<span class='bold'>Pathway Commons</span> is a convenient point of access " +
                "to biological pathway " +
                "information collected from public pathway databases, which you can " +
                "browse or search. <BR><BR>Pathways include biochemical reactions, complex " +
                "assembly, transport and catalysis events, and physical interactions " +
                "involving proteins, DNA, RNA, small molecules and complexes.";
        }
    }

    /**
	 * Gets URL for cPath Web Service API.
	 *
	 * @return cPath URL.
	 */
	public String getCPathUrl() {
        return cPathUrl;
	}

    /**
	 * Gets Name of cPath Instance.
	 *
	 * @return cPath URL.
	 */
	public String getCPathServerName() {
		return serverName;
	}

    /**
	 * Gets Text Blurb for cPath Instance
	 *
	 * @return cPath URL.
	 */
	public String getCPathBlurb() {
		return blurb;
	}

    /**
     * Gets Download Mode.
     * @return DOWNLOAD_FULL_BIOPAX or DOWNLOAD_REDUCED_BINARY_SIF.
     */
    public int getDownloadMode() {
        return downloadMode;
    }

    /**
     * Sets Download Mode.
     * @param downloadMode DOWNLOAD_FULL_BIOPAX or DOWNLOAD_REDUCED_BINARY_SIF.
     */
    public void setDownloadMode(int downloadMode) {
        this.downloadMode = downloadMode;
    }    
}
