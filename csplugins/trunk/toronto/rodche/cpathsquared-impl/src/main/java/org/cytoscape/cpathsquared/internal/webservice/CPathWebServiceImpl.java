package org.cytoscape.cpathsquared.internal.webservice;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.biopax.paxtools.io.pathwayCommons.PathwayCommons2Client;
import org.biopax.paxtools.model.level3.BioSource;
import org.biopax.paxtools.model.level3.Entity;
import org.cytoscape.work.TaskMonitor;

import cpath.service.jaxb.SearchResponse;

/**
 * Class for accessing the cPath Web API.
 *
 * @author Ethan Cerami, Igor Rodchenkov
 */
public class CPathWebServiceImpl implements CPathWebService {
    private static ArrayList<CPathWebServiceListener> listeners = new ArrayList<CPathWebServiceListener>();
    private volatile PathwayCommons2Client client;
    private static CPathWebService webApi;

    /**
     * Gets Singelton instance of CPath Web API.
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
     * @return SearchResponseType Object.
     */
    public SearchResponse searchPhysicalEntities(String keyword, int ncbiTaxonomyId,
            TaskMonitor taskMonitor) throws CPathException, EmptySetException {

        // Notify all listeners of start
        for (int i = listeners.size() - 1; i >= 0; i--) {
            CPathWebServiceListener listener = listeners.get(i);
            listener.searchInitiatedForPhysicalEntities(keyword, ncbiTaxonomyId);
        }

        protocol = new CPathProtocol();
        protocol.setCommand(CPathProtocol.COMMAND_SEARCH);
        protocol.setOrganism(ncbiTaxonomyId);
        protocol.setFormat(CPathResponseFormat.GENERIC_XML);
        protocol.setQuery(keyword);

		SearchResponse searchResponse = protocol.connect(taskMonitor);

        //SearchResponseType searchResponse = createDummySearchResults();
        // Notify all listeners of end
        for (int i = listeners.size() - 1; i >= 0; i--) {
            CPathWebServiceListener listener = listeners.get(i);
            listener.searchCompletedForPhysicalEntities(searchResponse);
        }
        return searchResponse;
    }

    /**
     * Gets parent summaries for specified record.
     *
     * @param primaryId     Primary ID of Record.
     * @param taskMonitor   Task Monitor Object.
     * @return SummaryResponse Object.
     * @throws CPathException       CPath Error.
     * @throws EmptySetException    Empty Set Error.
     */
    public List<Entity> getParentSummaries (String primaryId, TaskMonitor taskMonitor)
            throws CPathException, EmptySetException {
        // Notify all listeners of start
        for (int i = listeners.size() - 1; i >= 0; i--) {
            CPathWebServiceListener listener = listeners.get(i);
            listener.requestInitiatedForParentSummaries(primaryId);
        }

        //TODO
        protocol = new CPathProtocol();
        protocol.setCommand(CPathProtocol.COMMAND_GET_PARENTS);
        protocol.setFormat(CPathResponseFormat.GENERIC_XML);
        protocol.setQuery(Long.toString(primaryId));
        SearchResponse response = protocol.connect(taskMonitor);


        // Notify all listeners of end
        for (int i = listeners.size() - 1; i >= 0; i--) {
            CPathWebServiceListener listener = listeners.get(i);
            listener.requestCompletedForParentSummaries(primaryId, summaryResponse);
        }
        return summaryResponse;
    }

    /**
     * Gets One or more records by Primary ID.
     * @param ids               Array of Primary IDs.
     * @param format            CPathResponseFormat Object.
     * @param taskMonitor       Task Monitor Object.
     * @return  BioPAX XML String.
     * @throws CPathException       CPath Error.
     * @throws EmptySetException    Empty Set Error.
     */
    public String getRecordsByIds(String[] ids, CPathResponseFormat format,
            TaskMonitor taskMonitor) throws CPathException, EmptySetException {
        protocol = new CPathProtocol();
        protocol.setCommand(CPathProtocol.COMMAND_GET);
        protocol.setFormat(format);
        StringBuffer q = new StringBuffer();
        for (int i=0; i<ids.length; i++) {
            q.append (Long.toString(ids[i])+",");
        }
        protocol.setQuery(q.toString());
        String xml = protocol.connect(taskMonitor);
        return xml;
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
