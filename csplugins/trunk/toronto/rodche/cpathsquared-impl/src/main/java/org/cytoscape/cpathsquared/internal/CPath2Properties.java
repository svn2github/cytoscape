package org.cytoscape.cpathsquared.internal;


/**
 * Contains cPath Specific Properties
 *
 */
public class CPath2Properties {
    /**
     * Download Networks in Full BioPAX Mode.
     */
    public final static int DOWNLOAD_BIOPAX = 1;

    /**
     * Download Networks in Binary SIF Reduced Mode
     */
    public final static int DOWNLOAD_BINARY_SIF = 2;

	public static final String JVM_PROPERTY_CPATH2_URL = "cPath2Url";
	public static final String DEFAULT_CPATH2_URL = "http://www.pathwaycommons.org/pc2/";
    public static String cPathUrl = System.getProperty(JVM_PROPERTY_CPATH2_URL, DEFAULT_CPATH2_URL);
    
    public static String serverName = "Pathway Commons (BioPAX L3)";
    
    public static String blurb = 
    		"<span class='bold'>Pathway Commons</span> is a convenient point of access " +
            "to biological pathway " +
            "information collected from public pathway databases, which you can " +
            "browse or search. <BR><BR>Pathways include biochemical reactions, complex " +
            "assembly, transport and catalysis events, and physical interactions " +
            "involving proteins, DNA, RNA, small molecules and complexes. Now using BioPAX Level3!";
    
    public static String iconToolTip  = "Import Pathway Data from new PathwayCommons.org";
    
    public static String iconFileName = "pc.png";
    
    public static int downloadMode = DOWNLOAD_BINARY_SIF;

    private CPath2Properties () {
        //  no-op; private constructor;
    }

}