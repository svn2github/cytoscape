package org.cytoscape.cpathsquared.internal;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.biopax.paxtools.model.Model;
import org.biopax.paxtools.model.level3.BioSource;
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
public final class CPath2WebServiceImpl implements CPath2WebService {
    private static ArrayList<CPath2WebServiceListener> listeners = new ArrayList<CPath2WebServiceListener>();
    private static CPath2WebService webApi;
    private static final Logger LOGGER = LoggerFactory.getLogger(CPath2WebServiceImpl.class);
    
    /**
     * Gets a singleton instance of the cPath2 web service handler.
     * @return CPath2WebService Object.
     */
    public static CPath2WebService getInstance() {
        if (webApi == null) {
            webApi = new CPath2WebServiceImpl();
        }
        return webApi;
    }

    /**
     * Private Constructor.
     */
    private CPath2WebServiceImpl() {
    }

    /**
     * Searches Physical Entities in cPath Instance.
     *
     * @param keyword        Keyword to search for.
     * @param organism TODO
     * @param datasource TODO
     * @return
     */
    public SearchResponse search(String keyword, Set<String> organism,
            Set<String> datasource) throws CPath2Exception, EmptySetException {

    	if(LOGGER.isDebugEnabled())
    		LOGGER.debug("search: query=" + keyword);
    	
    	// Notify all listeners of start
        for (int i = listeners.size() - 1; i >= 0; i--) {
            CPath2WebServiceListener listener = listeners.get(i);
            listener.searchInitiated(keyword, organism, datasource);
        }

        CPath2Client client = CPath2Client.newInstance();
    	if(LOGGER.isDebugEnabled())
    		LOGGER.debug("cPath2Url=" + client.getEndPointURL());
    	
        if(organism != null)
        	client.setOrganisms(organism);
        
        client.setType("Entity"); //TODO remove (this is temporary, for prototyping/tests only..)
        
        if(datasource != null)
        	client.setDataSources(datasource);

        try {
        	//was: =protocol.connect(taskMonitor);
        	SearchResponse res = (SearchResponse) client.search(keyword); 
			// Notify all listeners of end
			for (int i = listeners.size() - 1; i >= 0; i--) {
				CPath2WebServiceListener listener = listeners.get(i);
				listener.searchCompleted(res);
			}
			return res;
        } catch (CPathException e) {
			throw new CPath2Exception(e.getError().getErrorCode(),
					e.toString());
		}
    }


    /**
     * Gets One or more records by Primary ID.
     * @param ids               Array of URIs.
     * @param format            Output format. TODO
     * @return data string.
     * @throws CPath2Exception       CPath Error.
     * @throws EmptySetException    Empty Set Error.
     */
    public String getRecordsByIds(String[] ids, OutputFormat format) 
    		throws CPath2Exception, EmptySetException 
    {
    	CPath2Client client = CPath2Client.newInstance();
        
    	//TODO client must return other formats, if requested
    	Model res = client.get(Arrays.asList(ids));
    	        
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BioPaxUtil.getBiopaxIO().convertToOWL(res, baos);
        
        return baos.toString();
    }

    
    /**
     * Abort the Request.
     */
    public void abort() {
        //TODO
    }

    /**
     * Gets a list of all Organisms currently available within cPath instance.
     *
     * @return ArrayList of FilterBoxItem Type Objects.
     */
    public List<BioSource> getOrganismList() {
        throw new UnsupportedOperationException("getOrganismList() is not yet implemented.");
    }

    /**
     * Registers a new listener.
     *
     * @param listener CPath2WebService Listener.
     */
    public void addApiListener(CPath2WebServiceListener listener) {
        listeners.add(listener);
    }

    /**
     * Removes the specified listener.
     *
     * @param listener CPath2WebService Listener.
     */
    public void removeApiListener(CPath2WebServiceListener listener) {
        listeners.remove(listener);
    }

    /**
     * Gets the list of all registered listeners.
     *
     * @return ArrayList of CPath2WebServiceListener Objects.
     */
    public ArrayList<CPath2WebServiceListener> getListeners() {
        return listeners;
    }
}
