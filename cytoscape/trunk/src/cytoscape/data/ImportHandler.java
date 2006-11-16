package cytoscape.data;

import cytoscape.data.readers.GraphReader;
import cytoscape.util.CyFileFilter;
import cytoscape.util.GMLFileFilter;
import cytoscape.util.SIFFileFilter;
import cytoscape.util.XGMMLFileFilter;

import java.awt.Component;
import java.awt.Cursor;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.swing.JOptionPane;

/**
 * Central registry for all Cytoscape import classes.
 *
 * @author Cytoscape Development Team.
 */
public class ImportHandler {

    /**
     * Filter Type:  NETWORK.
     */
    public static String GRAPH_NATURE = "NETWORK";

    /**
     * Filter Type:  NODE.
     */
    public static String NODE_NATURE = "NODE";

    /**
     * Filter Type:  EDGE.
     */
    public static String EDGE_NATURE = "EDGE";

    /**
     * Filer Type:  PROPERTIES.
     */
    public static String PROPERTIES_NATURE = "PROPERTIES";

    /**
     * Set of all registered CyFileFilter Objects.
     */
    protected static Set cyFileFilters = new HashSet();

    /**
     * Constructor.
     */
    public ImportHandler() {
        //  By default, register SIF, XGMML and GML File Filters
        init();
    }

    /**
     * Initialize ImportHandler.
     */
    private void init() {
        addFilter(new SIFFileFilter());
        addFilter(new XGMMLFileFilter());
        addFilter(new GMLFileFilter());
    }

    /**
     * Registers a new CyFileFilter.
     *
     * @param cff CyFileFilter.
     * @return true indicates filter was added successfully.
     */
    public boolean addFilter(CyFileFilter cff) {
        Set check = cff.getExtensionSet();

        for (Iterator it = check.iterator(); it.hasNext();) {
            String extension = (String) it.next();
            if (getAllExtensions().contains(extension)
                    && ! extension.equals("xml")) {
                return false;
            }
        }
        cyFileFilters.add(cff);
        return true;
    }

    /**
     * Registers an Array of CyFileFilter Objects.
     *
     * @param cff Array of CyFileFilter Objects.
     * @return true indicates all filters were added successfully.
     */
    public boolean addFilter(CyFileFilter[] cff) {
        //first check if suffixes are unique
        boolean flag = true;

        for (int j = 0; (j < cff.length) && (flag == true); j++) {
            flag = addFilter(cff[j]);
        }
        return flag;
    }

    /**
     * Gets the GraphReader that is capable of reading the specified file.
     *
     * @param fileName File name or null if no reader is capable of reading the file.
     * @return GraphReader capable of reading the specified file.
     */
    public GraphReader getReader(String fileName) {
        //  check if fileType is available
        CyFileFilter cff;

        for (Iterator it = cyFileFilters.iterator(); it.hasNext();) {
            cff = (CyFileFilter) it.next();

            if (cff.accept(fileName)) {
                return cff.getReader(fileName);
            }
        }
        return null;
    }

    /**
     * Gets the GraphReader that is capable of reading URL.
     *
     * @param pURLstr -- the URL string, pProxyServer -- the proxy server or null .
     * @return GraphReader capable of reading the specified URL.
     */
    public GraphReader getReader(String pURLstr, Proxy pProxyServer)
    	throws MalformedURLException,IOException
    {    	
    	File tmpFile = getNetworkFromURL(pURLstr, pProxyServer);	
    	return getReader(tmpFile.getAbsolutePath());    	
    }

    /**
     * Gets descriptions for all registered filters, which are of the type:  fileNature.
     *
     * @param fileNature type:  GRAPH_NATURE, NODE_NATURE, EDGE_NATURE, etc.
     * @return Collection of String descriptions, e.g. "XGMML files"
     */
    public Collection getAllTypes(String fileNature) {
        Collection ans = new HashSet();
        CyFileFilter cff;

        for (Iterator it = cyFileFilters.iterator(); it.hasNext();) {
            cff = (CyFileFilter) it.next();

            //if statement to check if nature equals fileNature
            if (cff.getFileNature().equals(fileNature)) {
                cff.setExtensionListInDescription(false);
                ans.add(cff.getDescription());
                cff.setExtensionListInDescription(true);
            }
        }
        return ans;
    }

    /**
     * Gets a collection of all registered file extensions.
     *
     * @return Collection of Strings, e.g. "xgmml", "sif", etc.
     */
    public Collection getAllExtensions() {
        Collection ans = new HashSet();
        for (Iterator it = cyFileFilters.iterator(); it.hasNext();) {
            ans.addAll(((CyFileFilter) (it.next())).getExtensionSet());
        }
        return ans;
    }

    /**
     * Gets a collection of all registered filter descriptions.
     * Descriptions are of the form:  "{File Description} ({file extensions})".
     * For example: "GML files (*.gml)"
     *
     * @return Collection of Strings, e.g. "GML files (*.gml)", etc.
     */
    public Collection getAllDescriptions() {
        Collection ans = new HashSet();
        for (Iterator it = cyFileFilters.iterator(); it.hasNext();) {
            ans.add(((CyFileFilter) it.next()).getDescription());
        }
        return ans;
    }

    /**
     * Gets the name of the filter which is capable of reading the specified file.
     *
     * @param fileName File Name.
     * @return name of filter capable of reading the specified file.
     */
    public String getFileType(String fileName) {
        CyFileFilter cff;
        String ans = null;
        for (Iterator it = cyFileFilters.iterator(); it.hasNext();) {
            cff = (CyFileFilter) it.next();

            if (cff.accept(fileName)) {
                cff.setExtensionListInDescription(false);
                ans = (cff.getDescription());
                cff.setExtensionListInDescription(true);
            }
        }
        return ans;
    }

    /**
     * Gets a list of all registered filters plus a catch-all super set filter.
     *
     * @return List of CyFileFilter Objects.
     */
    public List getAllFilters() {
        List ans = new ArrayList();

        for (Iterator it = cyFileFilters.iterator(); it.hasNext();) {
            ans.add((CyFileFilter) it.next());
        }
        if (ans.size() > 1) {
            String[] allTypes = concatAllExtensions(ans);
            ans.add(new CyFileFilter(allTypes, "All Natures"));
        }
        return ans;
    }

    /**
     * Gets a list of all registered filters, which are of type:  fileNature,
     * plus a catch-all super set filter.
     *
     * @param fileNature type:  GRAPH_NATURE, NODE_NATURE, EDGE_NATURE, etc.
     * @return List of CyFileFilter Objects.
     */
    public List getAllFilters(String fileNature) {
        List ans = new ArrayList();
        CyFileFilter cff;

        for (Iterator it = cyFileFilters.iterator(); it.hasNext();) {
            cff = (CyFileFilter) it.next();

            //if statement to check if nature equals fileNature
            if (cff.getFileNature().equals(fileNature)) {
                ans.add(cff);
            }
        }
        if (ans.size() > 1) {
            String[] allTypes = concatAllExtensions(ans);
            ans.add(new CyFileFilter(allTypes, "All "
                    + fileNature.toLowerCase() + " files", fileNature));
        }
        return ans;
    }

    /**
     * Unregisters all registered File Filters (except default file filters.)
     * Resets everything with a clean slate.
     */
    public void resetImportHandler() {
        cyFileFilters = new HashSet();
        init();
    }

    /**
     * Creates a String array of all extensions
     */
    private String[] concatAllExtensions(List cffs) {
        Set ans = new HashSet();

        for (Iterator it = cffs.iterator(); it.hasNext();) {
            ans.addAll(((CyFileFilter) (it.next())).getExtensionSet());
        }

        String[] stringAns = (String[]) ans.toArray(new String[0]);
        return stringAns;
    }
    
	
    /**
     * Download a temporary file from the given URL. The file will be saved in the 
     * temporary directory and will be deleted after Cytoscape exits.
     */
	public File downloadFromURL(String pURLstr, Proxy pProxyServer) throws MalformedURLException,
	IOException
	{				
		URL theUrl;
		theUrl = new URL(pURLstr);			
		
		URLConnection conn = null;;
		
		if (pProxyServer == null) {
			conn = theUrl.openConnection();							
		}
		else {
			conn = theUrl.openConnection(pProxyServer);
		}			
		
		//Create a unique filename based on the current time
		String baseFilename = "";
		if (baseFilename.equals("")) {
			Date date = new Date();
			DateFormat df = new SimpleDateFormat("yyyy_MM_dd-HH_mm");
			baseFilename = df.format(date);
		}
		
		//create a tmp file, which will be deleted upon exit
		java.util.Properties theProperties = System.getProperties();
		File tmpFile = new File(theProperties.getProperty("java.io.tmpdir"), baseFilename);
		if (tmpFile.exists()) {
			baseFilename = baseFilename +"a";
			tmpFile = new File(theProperties.getProperty("java.io.tmpdir"), baseFilename);
		}
	    tmpFile.deleteOnExit();

	    BufferedWriter out = null;
	    BufferedReader in = null;

	    out = new BufferedWriter(new FileWriter(tmpFile));
		in = new BufferedReader(new InputStreamReader(conn.getInputStream()));	    	
	    	    
	    // Write to tmp file
		String inputLine;
		while ((inputLine = in.readLine()) != null) 
		{
		    out.write(inputLine+"\n");
		}

		in.close();
	    out.close();				
		
		return tmpFile;
	}//downloadFromURL()
	
	
    /**
     * Download a network from URL
     */
	public File getNetworkFromURL(String pURLstr, Proxy pProxyServer)
	throws MalformedURLException,IOException
	{
		//System.out.println("ImportHandler.getNetworkFromURL() ...");
		File tmpFile = downloadFromURL(pURLstr, pProxyServer);
		
		if (tmpFile == null) return null;
		
		String baseFilename = "";

		//Try if we can determine the network type from URLstr
		//test the URL against the various file extensions,if one matches, then extract the basename
		Collection theExts = getAllExtensions();
		for (Iterator it = theExts.iterator(); it.hasNext();) {
			String theExt = (String) it.next();
			if (pURLstr.endsWith(theExt)) {
				baseFilename = pURLstr.substring(pURLstr.lastIndexOf("/")+1);
				break;
			}
		}
		//If not, determine network type by reading first few lines of header
		if (baseFilename.equals("")) {
			String ext = getNetworkTypeByHeader(tmpFile);
			baseFilename = tmpFile.getName()+ "." + ext;
		}
		//Rename network file with ext
		File newFile = new File(tmpFile.getParent(), baseFilename);
		newFile.deleteOnExit();
		tmpFile.renameTo(newFile);

		return newFile;
	}
	
	
    /**
     * For a file without extension, check if its content is "xml" or "gml" or "unknown"
     */	
	public String getNetworkTypeByHeader(File pFile) throws IOException {
		String theHeader = getHeader(pFile).trim().toUpperCase();
		
		if (theHeader.startsWith("<?XML")) {
			return "xml";
		}
	
		if (theHeader.contains("GRAPH")) {
			int index = theHeader.indexOf("GRAPH");
			String theSuffix = theHeader.substring(index+5).trim();

			if (theSuffix.startsWith("[")) {
				return "gml";
			}
		}
		
		return "unknown";
	}
	
	
    /**
     * Gets header of specified file. 
     * Copy from cytoscape.util.CyFileFilter.getHeader()
     */
    protected String getHeader(File file) throws IOException {
        FileReader reader = null;
        BufferedReader bufferedReader = null;
        try {
            reader = new FileReader (file);
            bufferedReader = new BufferedReader (reader);
            String line = bufferedReader.readLine();
            StringBuffer header = new StringBuffer();
            int numLines = 0;
            while (line != null && numLines < 5) {
                header.append(line + "\n");
                line = bufferedReader.readLine();
                numLines++;
            }
            return header.toString();
        } finally {
            bufferedReader.close();
            reader.close();
        }
    }
}