package org.cytoscape.coreplugin.cpath2.web_service;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import java.util.ArrayList;
import java.util.List;
import java.io.StringReader;
import java.io.File;

import cytoscape.task.TaskMonitor;
import org.cytoscape.coreplugin.cpath2.schemas.search_response.*;
import org.cytoscape.coreplugin.cpath2.schemas.summary_response.SummaryResponseType;

/**
 * Interface for accessing the cPath Web API.
 *
 * @author Ethan Cerami
 */
public interface CPathWebService {

    /**
     * Searches Physical Entities in cPath Instance.
     *
     * @param keyword        Keyword to search for.
     * @param ncbiTaxonomyId Organism filter (-1 to to search all organisms)
     * @param startIndex     Start index into search results; used to perform pagination.
     * @return SearchResponseType Object.
     */
    public SearchResponseType searchPhysicalEntities(String keyword, int ncbiTaxonomyId,
            int startIndex, TaskMonitor taskMonitor) throws CPathException, EmptySetException;

    /**
     * Gets parent summaries for specified record.
     *
     * @param primaryId     Primary ID of Record.
     * @param taskMonitor   Task Monitor Object.
     * @return SummaryResponse Object.
     * @throws CPathException       CPath Error.
     * @throws EmptySetException    Empty Set Error.
     */
    public SummaryResponseType getParentSummaries (long primaryId, TaskMonitor taskMonitor)
            throws CPathException, EmptySetException;

    /**
     * Gets One or more records by Primary ID.
     * @param ids               Array of Primary IDs.
     * @param format            FORMAT_BIOPAX or FORMAT_BINARY_SIF.
     * @param taskMonitor       Task Monitor Object.
     * @return  BioPAX XML String.
     * @throws CPathException       CPath Error.
     * @throws EmptySetException    Empty Set Error.
     */
    public String getRecordsByIds(long[] ids, String format, TaskMonitor taskMonitor) throws
            CPathException, EmptySetException;

    /**
     * Gets a list of all Organisms currently available within cPath instance.
     *
     * @return ArrayList of Organism Type Objects.
     */
    public ArrayList<OrganismType> getOrganismList();

    /**
     * Abort the Request.
     */
    public void abort();

    /**
     * Registers a new listener.
     *
     * @param listener CPathWebService Listener.
     */
    public void addApiListener(CPathWebServiceListener listener);

    /**
     * Removes the specified listener.
     *
     * @param listener CPathWebService Listener.
     */
    public void removeApiListener(CPathWebServiceListener listener);

    /**
     * Gets the list of all registered listeners.
     *
     * @return ArrayList of CPathWebServiceListener Objects.
     */
    public ArrayList<CPathWebServiceListener> getListeners();
}