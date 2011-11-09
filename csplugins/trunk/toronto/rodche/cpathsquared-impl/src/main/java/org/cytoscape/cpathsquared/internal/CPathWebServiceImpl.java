package org.cytoscape.cpathsquared.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.biopax.paxtools.io.pathwayCommons.PathwayCommons2Client;
import org.biopax.paxtools.io.pathwayCommons.util.PathwayCommonsException;
import org.biopax.paxtools.model.level3.BioSource;
import org.cytoscape.cpathsquared.internal.util.EmptySetException;
import org.cytoscape.work.TaskMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cpath.service.OutputFormat;
import cpath.service.jaxb.SearchResponse;

/**
 * Class for accessing the cPath Web API.
 *
 * @author Ethan Cerami, Igor Rodchenkov
 */
public class CPathWebServiceImpl implements CPathWebService {
    private static ArrayList<CPathWebServiceListener> listeners = new ArrayList<CPathWebServiceListener>();
    private static CPathWebService webApi;
    private static final Logger LOGGER = LoggerFactory.getLogger(CPathWebServiceImpl.class);
    
    /**
     * Gets a singleton instance of the cPath2 web service handler.
     * @return CPathWebService Object.
     */
    public static CPathWebService getInstance() {
        if (webApi == null) {
            webApi = new CPathWebServiceImpl();
        }
        return webApi;
    }

    /**
     * Private Constructor.
     */
    private CPathWebServiceImpl() {
    }

    /**
     * Searches Physical Entities in cPath Instance.
     *
     * @param keyword        Keyword to search for.
     * @param ncbiTaxonomyId Organism filter (-1 to to search all organisms)
     * @return
     */
    public SearchResponse searchPhysicalEntities(String keyword, int ncbiTaxonomyId,
            TaskMonitor taskMonitor) throws CPathException, EmptySetException {

    	if(LOGGER.isDebugEnabled())
    		LOGGER.debug("searchPhysicalEntities: query=" + keyword);
    	
    	// Notify all listeners of start
        for (int i = listeners.size() - 1; i >= 0; i--) {
            CPathWebServiceListener listener = listeners.get(i);
            listener.searchInitiatedForPhysicalEntities(keyword, ncbiTaxonomyId);
        }

        PathwayCommons2Client client = new PathwayCommons2Client();
    	if(LOGGER.isDebugEnabled())
    		LOGGER.debug("cPath2Url=" + client.getEndPointURL());
    	
        client.setOrganisms(Collections.singleton(String.valueOf(ncbiTaxonomyId)));
        //protocol.setCommand(CPathProtocol.COMMAND_SEARCH);
        client.setType("PhysicalEntity");

        try {
        	//was: =protocol.connect(taskMonitor);
        	SearchResponse res = (SearchResponse) client.findEntity(keyword); 
			// Notify all listeners of end
			for (int i = listeners.size() - 1; i >= 0; i--) {
				CPathWebServiceListener listener = listeners.get(i);
				listener.searchCompletedForPhysicalEntities(res);
			}
			return res;
        } catch (PathwayCommonsException e) {
			throw new CPathException(e.getError().getErrorCode(),
					e.toString());
		}
    }

    /**
     * Gets parent summaries for specified record.
     *
     * @param id     Primary ID of Record.
     * @param taskMonitor   Task Monitor Object.
     * @return SummaryResponse Object.
     * @throws CPathException       CPath Error.
     * @throws EmptySetException    Empty Set Error.
     */
    public SearchResponse getParentSummaries (String id, TaskMonitor taskMonitor)
            throws CPathException, EmptySetException 
    {
    	SearchResponse response = new SearchResponse();
    	
        // Notify all listeners of start
        for (int i = listeners.size() - 1; i >= 0; i--) {
            CPathWebServiceListener listener = listeners.get(i);
            listener.requestInitiatedForParentSummaries(id);
        }

        //TODO get neighborhood (upstream?), i.e, parents in SIFNX!
        //TODO create and add hits (with uri, name, ds, organism) to the response

//        protocol = new CPathProtocol();
//        protocol.setCommand(CPathProtocol.COMMAND_GET_PARENTS);
//        protocol.setFormat(CPathResponseFormat.GENERIC_XML);
//        protocol.setQuery(Long.toString(id));
//        SearchResponse response = protocol.connect(taskMonitor);


        // Notify all listeners of end
        for (int i = listeners.size() - 1; i >= 0; i--) {
            CPathWebServiceListener listener = listeners.get(i);
            listener.requestCompletedForParentSummaries(id, response);
        }
        
        return response;
    }

    /**
     * Gets One or more records by Primary ID.
     * @param ids               Array of Primary IDs.
     * @param format            CPathResponseFormat Object.
     * @param taskMonitor       Task Monitor Object.
     * @return BioPAX XML String.
     * @throws CPathException       CPath Error.
     * @throws EmptySetException    Empty Set Error.
     */
    public String getRecordsByIds(String[] ids, OutputFormat format,
            TaskMonitor taskMonitor) throws CPathException, EmptySetException 
    {
//        protocol = new CPathProtocol();
//        protocol.setCommand(CPathProtocol.COMMAND_GET);
//        protocol.setFormat(format);
//        StringBuffer q = new StringBuffer();
//        for (int i=0; i<ids.length; i++) {
//            q.append (Long.toString(ids[i])+",");
//        }
//        protocol.setQuery(q.toString());
//        String xml = protocol.connect(taskMonitor);
//        return xml;
    	return ""; //TODO
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
     * @return ArrayList of Organism Type Objects.
     */
    public List<BioSource> getOrganismList() {
        throw new UnsupportedOperationException("getOrganismList() is not yet implemented.");
    }

    /**
     * Registers a new listener.
     *
     * @param listener CPathWebService Listener.
     */
    public void addApiListener(CPathWebServiceListener listener) {
        listeners.add(listener);
    }

    /**
     * Removes the specified listener.
     *
     * @param listener CPathWebService Listener.
     */
    public void removeApiListener(CPathWebServiceListener listener) {
        listeners.remove(listener);
    }

    /**
     * Gets the list of all registered listeners.
     *
     * @return ArrayList of CPathWebServiceListener Objects.
     */
    public ArrayList<CPathWebServiceListener> getListeners() {
        return listeners;
    }
}
