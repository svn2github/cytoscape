package org.cytoscape.cpathsquared.internal.webservice;

import java.util.ArrayList;

import org.cytoscape.cpathsquared.internal.view.Organism;

/**
 * Contains cPath Specific Properties
 *
 * @author Ethan Cerami, Igor Rodchenkov
 */
public class CPathProperties {
    /**
     * Download Networks in Full BioPAX Mode.
     */
    public final static int DOWNLOAD_FULL_BIOPAX = 1;

    /**
     * Download Networks in Binary SIF Reduced Mode
     */
    public final static int DOWNLOAD_REDUCED_BINARY_SIF = 2;

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
            "involving proteins, DNA, RNA, small molecules and complexes.";
    
    public static String iconToolTip  = "Retrieve Pathway Data from PathwayCommons.org";
    
    public static String iconFileName = "pc.png";
    
    public static ArrayList<Organism> organismList = new ArrayList<Organism>();
    
    public static int downloadMode = DOWNLOAD_REDUCED_BINARY_SIF;

    private CPathProperties () {
        //  no-op; private constructor;
    }

    static {
        organismList.add(new Organism("Human", 9606));
        organismList.add(new Organism("Mouse", 10090));
        organismList.add(new Organism("Rat", 10116));
        organismList.add(new Organism("S. cerevisiae", 4932));
    }

    /**
     * Gets the Web Services ID.
     * @return Web Service ID.
     */
    public static String getWebServicesId() {
        String temp = serverName.toLowerCase();
        return temp.replaceAll(" ", "_");
    }
}