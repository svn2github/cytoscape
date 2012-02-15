package org.cytoscape.cpathsquared.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.biopax.paxtools.model.level3.BioSource;
import org.cytoscape.cpathsquared.internal.util.EmptySetException;
import org.cytoscape.work.TaskMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cpath.client.CPath2Client;
import cpath.client.util.CPathException;
import cpath.service.OutputFormat;
import cpath.service.jaxb.SearchResponse;

/**
 * Class for accessing the cPath Web API.
 *
 * @author Ethan Cerami, Igor Rodchenkov
 */
public class CPath2WebServiceImpl implements CPath2WebService {
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
     * @param ncbiTaxonomyId Organism filter (-1 to to search all organisms)
     * @return
     */
    public SearchResponse searchPhysicalEntities(String keyword, int ncbiTaxonomyId,
            TaskMonitor taskMonitor) throws CPath2Exception, EmptySetException {

    	if(LOGGER.isDebugEnabled())
    		LOGGER.debug("searchPhysicalEntities: query=" + keyword);
    	
    	// Notify all listeners of start
        for (int i = listeners.size() - 1; i >= 0; i--) {
            CPath2WebServiceListener listener = listeners.get(i);
            listener.searchInitiatedForPhysicalEntities(keyword, ncbiTaxonomyId);
        }

        CPath2Client client = CPath2Client.newInstance();
    	if(LOGGER.isDebugEnabled())
    		LOGGER.debug("cPath2Url=" + client.getEndPointURL());
    	
        if(ncbiTaxonomyId > 0)
        	client.setOrganisms(Collections.singleton(String.valueOf(ncbiTaxonomyId)));
        client.setType("PhysicalEntity");

        try {
        	//was: =protocol.connect(taskMonitor);
        	SearchResponse res = (SearchResponse) client.search(keyword); 
			// Notify all listeners of end
			for (int i = listeners.size() - 1; i >= 0; i--) {
				CPath2WebServiceListener listener = listeners.get(i);
				listener.searchCompletedForPhysicalEntities(res);
			}
			return res;
        } catch (CPathException e) {
			throw new CPath2Exception(e.getError().getErrorCode(),
					e.toString());
		}
    }

    /**
     * Gets parent summaries for specified record.
     *
     * @param id     Primary ID of Record.
     * @param taskMonitor   Task Monitor Object.
     * @return SummaryResponse Object.
     * @throws CPath2Exception       CPath Error.
     * @throws EmptySetException    Empty Set Error.
     */
    public SearchResponse getParentSummaries (String id, TaskMonitor taskMonitor)
            throws CPath2Exception, EmptySetException 
    {
    	SearchResponse response = new SearchResponse();
    	
        // Notify all listeners of start
        for (int i = listeners.size() - 1; i >= 0; i--) {
            CPath2WebServiceListener listener = listeners.get(i);
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
            CPath2WebServiceListener listener = listeners.get(i);
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
     * @throws CPath2Exception       CPath Error.
     * @throws EmptySetException    Empty Set Error.
     */
    public String getRecordsByIds(String[] ids, OutputFormat format,
            TaskMonitor taskMonitor) throws CPath2Exception, EmptySetException 
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
