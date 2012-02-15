package org.cytoscape.cpathsquared.internal;

import java.util.ArrayList;
import java.util.List;

import org.biopax.paxtools.model.level3.BioSource;

import cpath.service.OutputFormat;
import cpath.service.jaxb.SearchResponse;

import org.cytoscape.cpathsquared.internal.util.EmptySetException;
import org.cytoscape.work.TaskMonitor;

/**
 * Interface for accessing the cPath Web API.
 *
 * @author Ethan Cerami, Igor Rodchenkov
 */
public interface CPath2WebService {

    /**
     * Searches for Physical Entities in cPath Instance.
     * Given a keyword, such as "BRCA1", this method returns the first 10 physical entities
     * which contain this keyword.  For each matching physical entity, you will receive
     * entity details, such as name, synonyms, external links, and list of all pathways in
     * which this entity participates. 
     *
     * @param keyword        Keyword to search for.
     * @param ncbiTaxonomyId Organism filter (-1 to to search all organisms).
     * @param taskMonitor    TaskMonitor Object (can be null);
     * @return SearchResponseType Object.
     * @throws CPath2Exception   CPath Connect Error.
     * @throws EmptySetException    No matches found to specified query.
     */
    public SearchResponse searchPhysicalEntities(String keyword, int ncbiTaxonomyId,
            TaskMonitor taskMonitor) throws CPath2Exception, EmptySetException;

    /**
     * Gets parent summaries for specified record.
     * For example, if primaryId refers to protein A, the "parent" records are all
     * interactions in which protein A participates.  If primaryId refers to interaction X,
     * the "parent" records are all parent interactions which control or modulate X.
     * To retrieve the full record (instead of just the summary), you must extract the primary
     * ID, and follow-up with a call to getRecordsByIds(). 
     *
     * @param id     Primary ID of Record.
     * @param taskMonitor   Task Monitor Object.
     * @return parents (e.g., upstream nearest neighborhood...)
     * @throws CPath2Exception       CPath Error.
     * @throws EmptySetException    Empty Set Error.
     */
    public SearchResponse getParentSummaries (String id, TaskMonitor taskMonitor)
            throws CPath2Exception, EmptySetException;

    /**
     * Gets One or more records by primary ID.
     * You can obtain primary IDs for physical entities and/or pathways via the
     * searchPhysicalEntities() method.
     * 
     * @param ids               Array of Primary IDs.
     * @param format            CPathResponseFormat.BIOPAX or CPathResponseFormat.BINARY_SIF.
     * @param taskMonitor       Task Monitor Object.
     * @return  BioPAX XML String or SIF String.
     * @throws CPath2Exception       CPath Error.
     * @throws EmptySetException    Empty Set Error.
     */
    public String getRecordsByIds(String[] ids, OutputFormat format, TaskMonitor taskMonitor)
            throws CPath2Exception, EmptySetException;

    /**
     * Gets a list of all Organisms currently available within the cPath instance.
     *
     * @return List of Organism (BioSource) Objects.
     */
    public List<BioSource> getOrganismList();

    /**
     * Abort the Request.
     */
    public void abort();

    /**
     * Registers a new listener.
     *
     * @param listener CPath2WebService Listener.
     */
    public void addApiListener(CPath2WebServiceListener listener);

    /**
     * Removes the specified listener.
     *
     * @param listener CPath2WebService Listener.
     */
    public void removeApiListener(CPath2WebServiceListener listener);

    /**
     * Gets the list of all registered listeners.
     *
     * @return ArrayList of CPath2WebServiceListener Objects.
     */
    public ArrayList<CPath2WebServiceListener> getListeners();
}