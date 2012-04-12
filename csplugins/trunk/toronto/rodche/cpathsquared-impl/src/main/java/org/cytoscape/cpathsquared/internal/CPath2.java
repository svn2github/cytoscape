package org.cytoscape.cpathsquared.internal;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.biopax.paxtools.model.Model;
import org.cytoscape.cpathsquared.internal.util.BioPaxUtil;
import org.cytoscape.cpathsquared.internal.util.EmptySetException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cpath.client.CPath2Client;
import cpath.client.util.CPathException;
import cpath.service.OutputFormat;
import cpath.service.jaxb.SearchResponse;

/**
 * Class for accessing the cPath Web API.
 *
 */
public final class CPath2 {
    private static ArrayList<CPath2Listener> listeners = new ArrayList<CPath2Listener>();
    private static final Logger LOGGER = LoggerFactory.getLogger(CPath2.class);
    

    /**
     * Private Constructor.
     */
    private CPath2() {
    	throw new AssertionError(); // non-instantiable
    }

    /**
     * Searches Physical Entities in cPath Instance.
     *
     * @param keyword        Keyword to search for.
     * @param organism TODO
     * @param datasource TODO
     * @return
     * @throws CPathException 
     */
    public static SearchResponse search(String keyword, Set<String> organism,
            Set<String> datasource) throws CPathException {

    	if(LOGGER.isDebugEnabled())
    		LOGGER.debug("search: query=" + keyword);
    	
    	// Notify all listeners of start
        for (int i = listeners.size() - 1; i >= 0; i--) {
            CPath2Listener listener = listeners.get(i);
            listener.searchInitiated(keyword, organism, datasource);
        }

        CPath2Client client = CPath2Client.newInstance();
        client.setEndPointURL("http://awabi.cbio.mskcc.org/cpath2/");
    	if(LOGGER.isDebugEnabled())
    		LOGGER.debug("cPath2Url=" + client.getEndPointURL());
    	
        if(organism != null)
        	client.setOrganisms(organism);
        
        client.setType("Entity"); //TODO make sure: we want Entity type hits only, because UtilityClass elements can be retrieved via sub-queries 
        
        if(datasource != null)
        	client.setDataSources(datasource);

        	SearchResponse res = (SearchResponse) client.search(keyword); 
			// Notify all listeners of end
			for (int i = listeners.size() - 1; i >= 0; i--) {
				CPath2Listener listener = listeners.get(i);
				listener.searchCompleted(res);
			}
			return res;
    }


    /**
     * Gets One or more records by Primary ID.
     * @param ids               Array of URIs.
     * @param format            Output format. TODO
     * @return data string.
     * @throws CPath2Exception       CPath Error.
     * @throws EmptySetException    Empty Set Error.
     */
    public static String getRecordsByIds(String[] ids, OutputFormat format) 
    {
    	CPath2Client client = CPath2Client.newInstance();
        
    	//TODO client must return other formats, if requested
    	Model res = client.get(Arrays.asList(ids));
    	        
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BioPaxUtil.getBiopaxIO().convertToOWL(res, baos);
        
        return baos.toString();
    }


    //TODO
    public static List<String> getOrganisms() {
        throw new UnsupportedOperationException("Not implemented.");
    }

    
    //TODO
    public static List<String> getDataSources() {
        throw new UnsupportedOperationException("Not implemented.");
    }
    
    
    /**
     * Registers a new listener.
     *
     * @param listener CPath2 Listener.
     */
    public static void addApiListener(CPath2Listener listener) {
        listeners.add(listener);
    }

    /**
     * Removes the specified listener.
     *
     * @param listener CPath2 Listener.
     */
    public static void removeApiListener(CPath2Listener listener) {
        listeners.remove(listener);
    }

    /**
     * Gets the list of all registered listeners.
     *
     * @return ArrayList of CPath2Listener Objects.
     */
    public static ArrayList<CPath2Listener> getListeners() {
        return listeners;
    }


	public static SearchResponse topPathways(String keyword, Set<String> organism,
			Set<String> datasource) {
		// TODO Auto-generated method stub
		return null;
	}
}
